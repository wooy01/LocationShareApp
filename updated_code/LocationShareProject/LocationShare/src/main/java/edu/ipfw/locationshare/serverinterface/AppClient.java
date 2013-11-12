package edu.ipfw.locationshare.serverinterface;

import android.util.Log;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.util.ArrayList;
import java.util.Date;

import edu.ipfw.locationshare.datamodel.Location;
import edu.ipfw.locationshare.datamodel.Message;
import edu.ipfw.locationshare.datamodel.ProcedureCall;

public class AppClient {
    //Singleton reference to an instance of this class
    private static AppClient Instance;

    private JSONParser Parser;
    private ArrayList<MessageListener> Listeners;
    private String SenderUID;
    private TCPClient Client;

    /*
     * Constructor
     */
    public AppClient() {
        this.Parser = new JSONParser();
        this.Listeners = new ArrayList<MessageListener>();
        this.Client = new TCPClient();
    }

    static {
        Instance = new AppClient();
    }

    public static AppClient getInstance() {
        return Instance;
    }

    /*
     * Login the user and initiate communications with the server
     */
    public void Login(String uid) {
        this.SenderUID = uid;

        //Construct the procedure call
        ProcedureCall procedureCall = new ProcedureCall();
        procedureCall.setCommand("LOGIN");
        procedureCall.getParameters().put("UID", uid);

        //Convert the call to JSON
        String json = procedureCall.toString();
        Log.d("ConnectButton", json);

        //Connect to the server
        this.Client.Connect();

        //Send the procedure call to the server
        this.Client.Write(json);
    }

    /*
     * Sends a message to the recipients
     */
    public void SendMessage(ArrayList<String> recipients, String content, float longitude, float latitude) {
        //Convert the recipients list to a comma delimited string
        String recipientList = "";
        for (String recipient : recipients) {
            if (recipientList.length() > 0) {
                recipientList += ",";
            }

            recipientList += recipient;
        }

        //Construct the procedure call
        ProcedureCall procedureCall = new ProcedureCall();
        procedureCall.setCommand("SEND_MESSAGE");
        procedureCall.getParameters().put("UID", this.SenderUID);
        procedureCall.getParameters().put("Recipients", recipientList);
        procedureCall.getParameters().put("Content", content);
        procedureCall.getParameters().put("Longitude", "" + longitude);
        procedureCall.getParameters().put("Latitude", "" + latitude);

        //Convert the call to JSON
        String json = procedureCall.toString();
        Log.d("AppClient:SendMessage", json);

        //Send the procedure call to the server
        this.Client.Write(json);
    }

    /*
     * Marks the specified message as read in the server's database
     */
    public void MarkAsRead(int id) {
        //Construct the procedure call
        ProcedureCall procedureCall = new ProcedureCall();
        procedureCall.setCommand("MARK_AS_READ");
        procedureCall.getParameters().put("ID", "" + id);

        //Convert the call to JSON
        String json = procedureCall.toString();
        Log.d("AppClient:MarkAsRead", json);

        //Send the procedure call to the server
        this.Client.Write(json);
    }

    /*
     * Fetches all unread messages for the user from the server
     */
    public void GetUnreadMessages() {
        //Construct the procedure call
        ProcedureCall procedureCall = new ProcedureCall();
        procedureCall.setCommand("GET_UNREAD_MESSAGES");
        procedureCall.getParameters().put("UID", this.SenderUID);

        //Convert the call to JSON
        String json = procedureCall.toString();
        Log.d("AppClient:GetUnreadMessages", json);

        //Send the procedure call to the server
        this.Client.Write(json);
    }

    /*
     * Deletes the specified message from the server's database
     */
    public void DeleteMessage(int id) {
        //Construct the procedure call
        ProcedureCall procedureCall = new ProcedureCall();
        procedureCall.setCommand("DELETE_MESSAGE");
        procedureCall.getParameters().put("ID", "" + id);

        //Convert the call to JSON
        String json = procedureCall.toString();
        Log.d("AppClient:DeleteMessage", json);

        //Send the procedure call to the server
        this.Client.Write(json);
    }

    /*
     * Deletes all messages for the user from the server's database
     */
    public void DeleteAllMessages() {
        //Construct the procedure call
        ProcedureCall procedureCall = new ProcedureCall();
        procedureCall.setCommand("DELETE_ALL_MESSAGES");
        procedureCall.getParameters().put("UID", this.SenderUID);

        //Convert the call to JSON
        String json = procedureCall.toString();
        Log.d("AppClient:DeleteAllMessages", json);

        //Send the procedure call to the server
        this.Client.Write(json);
    }

    /*
     * Processes incoming data from the server
     */
    public void ProcessData(String text) {
        Log.d("AppClient:ProcessData", text);

        try {
            //Try to parse the message text
            Object obj = Instance.Parser.parse(text);
            JSONObject jsonObject = (JSONObject) obj;

            //Check if the JSON is a message notification
            if (jsonObject.containsKey("ID")) {
                ParseMessage(jsonObject);
            }
            //Check if the JSON is a procedure call results
            else if (jsonObject.containsKey("Results")) {
                //Get the procedure call results
                JSONObject results = (JSONObject) jsonObject.get("Results");

                //Check if the results contains a list of messages
                if (!results.containsKey("Messages")) {
                    return;
                }

                //Process the messages
                JSONArray jsonArray = (JSONArray) results.get("Messages");
                for (int i = 0; i < jsonArray.size(); i++) {
                    JSONObject messageObj = (JSONObject) jsonArray.get(i);

                    ParseMessage(messageObj);
                }
            }

        } catch (Exception err) {
            Log.e("AppClient:ProcessData", err.toString());
        }
    }

    /*
     * Parses a message from the given JSONObject
     */
    private void ParseMessage(JSONObject jsonObject){
        //Parse the message's information
        int id = Integer.parseInt(jsonObject.get("ID").toString());
        String senderUID = jsonObject.get("SenderUID").toString();
        String recipientUID = jsonObject.get("RecipientUID").toString();
        String content = jsonObject.get("Content").toString();
        JSONObject location = (JSONObject) jsonObject.get("Location");
        float longitude = (float) Double.parseDouble(location.get("Longitude").toString());
        float latitude = (float) Double.parseDouble(location.get("Latitude").toString());
        Date sendDT = new Date();
        Date receiveDT = new Date();

        //Reconstruct the message as a Java object
        Message message = new Message();
        message.setID(id);
        message.setSenderUID(senderUID);
        message.setRecipientUID(recipientUID);
        message.setContent(content);
        message.setLocation(new Location(longitude, latitude));
        message.setSendDT(sendDT);
        message.setReceiveDT(receiveDT);

        //Notify the listeners about the new message received
        NotifyListeners(message);
    }

    /*
     * Registers the message listener
     */
    public void AddMessageListener(MessageListener listener) {
        this.Listeners.add(listener);
    }

    /*
     * Unregisters the message listener
     */
    public void RemoveMessageListener(MessageListener listener) {
        this.Listeners.remove(listener);
    }

    /*
     * Notifies all message listeners about the new message received
     */
    private void NotifyListeners(Message message) {
        for (MessageListener listener : this.Listeners) {
            listener.MessageReceived(message);
        }
    }
}
