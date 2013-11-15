package com.zoom;

import java.util.List;

import com.facebook.model.GraphUser;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class Activity_CreateMessage extends Activity {
	
	Button friend_button;
	Button send_button;
	final int REQUEST_FRIEND = 100;	
	public static List<GraphUser> selectedUsers;
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_createmessage); 
		
		
		friend_button = (Button) findViewById(R.id.friend_button);
		friend_button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				
				selectedUsers = null;
				
				
				// launch friend picker
				startPickerActivity(Activity_FriendPicker.FRIEND_PICKER, REQUEST_FRIEND);
				
				
				
				
			}			
		});
		
		
		
		send_button = (Button) findViewById(R.id.send_button);
		send_button.setEnabled(false);
		send_button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// send to server
				
				
				
				
				selectedUsers = null;
				
				// move to AsyncTask onComplete
				finish();
			}			
		});
	}
	
	
	//
	// Launch the friend picker activity
	//
	private void startPickerActivity(Uri data, int requestCode) {
		Intent intent = new Intent();
		intent.setData(data);
		intent.setClass(this, Activity_FriendPicker.class);
		startActivityForResult(intent, requestCode);
	}
	
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
	            
		if(requestCode == REQUEST_FRIEND) {
			
			//Toast.makeText(getApplicationContext(), "Friends: " + selectedUsers.size(), Toast.LENGTH_SHORT).show();
			
			
			// make sure a friend has been selected
			if(selectedUsers != null && selectedUsers.size() > 0) {
				send_button.setEnabled(true);				
			}
			
	    	  
		}
	}
}
