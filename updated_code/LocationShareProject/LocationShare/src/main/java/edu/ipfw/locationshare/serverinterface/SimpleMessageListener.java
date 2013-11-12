package edu.ipfw.locationshare.serverinterface;

import android.util.Log;

import edu.ipfw.locationshare.datamodel.Message;

public class SimpleMessageListener extends MessageListener {

    @Override
    public void MessageReceived(Message message) {
        //Log the message
        Log.d("SimpleMessageListener:MessageReceived", "Message received from " + message.getSenderUID() + ", content: " + message.getContent());

        //Mark message as read
        AppClient.getInstance().MarkAsRead(message.getID());
    }
}
