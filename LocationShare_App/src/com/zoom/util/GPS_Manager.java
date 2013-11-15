package com.zoom.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.widget.Toast;

public class GPS_Manager {
	Activity activity;

	
	public GPS_Manager(Activity activity) {
		this.activity = activity;
	}
	
	
	//
	// Determine whether GPS is enabled on the device, if not then open GPS settings
	//
	public void enableGPS() {	
		AlertDialog.Builder builder = new AlertDialog.Builder(activity); 
		builder.setCancelable(false);  
		builder.setTitle("GPS IS TURNED OFF");
		builder.setMessage("PLEASE ENABLE GPS");
		builder.setInverseBackgroundForced(true); 
		builder.setPositiveButton("ENABLE", new DialogInterface.OnClickListener() { 
			@Override
			public void onClick(DialogInterface dialog, int which) { 
				dialog.dismiss();
				
				activity.startActivityForResult(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS), 2); 				
			} 
		}); 
		builder.setNegativeButton("IGNORE", new DialogInterface.OnClickListener() { 
			@Override
			public void onClick(DialogInterface dialog, int which) { 	
				dialog.dismiss(); 
				
				Toast.makeText(activity, "GPS WAS NOT ENABLED", Toast.LENGTH_SHORT).show();  			
			} 
		}); 
		AlertDialog alert = builder.create(); 
		alert.show();      	
	}	
}
