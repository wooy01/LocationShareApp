package com.zoom;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.model.GraphUser;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

public class Activity_Start extends Activity{

	final int REQUEST_LOGIN = 64206;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_start); 
		
		// start Facebook Login
	    Session.openActiveSession(this, true, new Session.StatusCallback() {
	    	// callback when session changes state
	    	@SuppressWarnings("deprecation")
			@Override
	    	public void call(Session session, SessionState state, Exception exception) {
	    		if (session.isOpened()) {
	    			// make request to the /me API
	    			Request.executeMeRequestAsync(session, new Request.GraphUserCallback() {
	    				// callback after Graph API response with user object
	    				@Override
	    				public void onCompleted(GraphUser user, Response response) {
	    					if (user != null) {
	    						//TextView welcome = (TextView) findViewById(R.id.welcome);
	    						//welcome.setText("Hello " + user.getName() + "!");
	    						
	    						Toast.makeText(getApplicationContext(), "Hello " + user.getName() + "!", Toast.LENGTH_SHORT).show();
	    						
	    						startActivityForResult(new Intent(getApplicationContext(), Activity_GoogleMap.class), 0); 
	    					}
	    				}
	    			});
	    		}
	    	}
	    });
	}
	
	//
	// Called after a launched activity returns
	//
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		if(requestCode == REQUEST_LOGIN){
			if(resultCode == RESULT_OK) {
				Session.getActiveSession().onActivityResult(this, requestCode, resultCode, intent);
			}    		  
			else {
				finish();
			}    		  
		}
		else {
			finish();
		}
	} 
}
