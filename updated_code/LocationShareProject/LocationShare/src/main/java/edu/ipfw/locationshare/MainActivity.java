package edu.ipfw.locationshare;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;

import edu.ipfw.locationshare.serverinterface.AppClient;
import edu.ipfw.locationshare.serverinterface.SimpleMessageListener;

public class MainActivity extends ActionBarActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }

        //Register a simple logging message listener with the AppClient
        SimpleMessageListener messageListener = new SimpleMessageListener();
        AppClient.getInstance().AddMessageListener(messageListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.action_settings:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            final View rootView = inflater.inflate(R.layout.fragment_main, container, false);

            /*
             * Register button listeners
             */
            Button connectButton = (Button) rootView.findViewById(R.id.connectButton);
            connectButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Handle click event for connectButton
                    //Get user's name
                    EditText myNameTB = (EditText) rootView.findViewById(R.id.myNameTB);
                    String uid = myNameTB.getText().toString();

                    AppClient.getInstance().Login(uid);
                }
            });

            Button sendButton = (Button) rootView.findViewById(R.id.sendButton);
            sendButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Handle click event for sendButton
                    //Get user's name
                    EditText myNameTB = (EditText) rootView.findViewById(R.id.myNameTB);
                    String uid = myNameTB.getText().toString();

                    //Get message content
                    EditText messageContentTB = (EditText) rootView.findViewById(R.id.messageContentTB);
                    String messageContent = messageContentTB.getText().toString();

                    //Get recipient #1 name
                    EditText recipient1TB = (EditText) rootView.findViewById(R.id.recipient1TB);
                    String recipient1 = recipient1TB.getText().toString();

                    //Get recipient #2 name
                    EditText recipient2TB = (EditText) rootView.findViewById(R.id.recipient2TB);
                    String recipient2 = recipient2TB.getText().toString();

                    //Create list of recipients
                    ArrayList<String> recipients = new ArrayList<String>();
                    recipients.add(recipient1);
                    recipients.add(recipient2);

                    //Calculate random longitude and latitude
                    float longitude = (float) Math.random() * 180;
                    float latitude = (float) Math.random() * 90;

                    AppClient.getInstance().SendMessage(recipients, messageContent, longitude, latitude);
                }
            });

            Button deleteAllMessagesButton = (Button) rootView.findViewById(R.id.deleteButton);
            deleteAllMessagesButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Delete all messages for this user
                    AppClient.getInstance().DeleteAllMessages();
                }
            });

            Button getUnreadButton = (Button) rootView.findViewById(R.id.getUnreadButton);
            getUnreadButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Get unread messages
                    AppClient.getInstance().GetUnreadMessages();
                }
            });

            return rootView;
        }
    }

}
