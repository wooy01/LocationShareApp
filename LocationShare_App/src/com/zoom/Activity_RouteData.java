package com.zoom;

import java.text.DecimalFormat;
import java.util.ArrayList;

import com.zoom.util.LocationData;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class Activity_RouteData extends Activity{
	TextView title;
	TextView heading;
	ListView directionList;
	Button doneButton;
	ArrayAdapter<String> routeArrayAdapter;
	
			
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.routedata);
		setUpUI();  
	} 	
	
	
	private void setUpUI() {
		title = (TextView)findViewById(R.id.textView1);
		heading = (TextView)findViewById(R.id.heading);
		directionList = (ListView)findViewById(R.id.listView1);
		doneButton = (Button)findViewById(R.id.done);
		
		title.setText("Route " + (Activity_GoogleMap.index + 1));
				
		routeArrayAdapter = new ArrayAdapter<String>(this, R.layout.message);
		directionList.setAdapter(routeArrayAdapter);
		
		// Determine device screen size
		if ((getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == 
				Configuration.SCREENLAYOUT_SIZE_NORMAL) 
		{
			setupNormal();
		} else
			setupLarge();		
				
		doneButton.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {	
				finish();					  	
			}			
		});
	}
	
	
	private void setupNormal() {
		heading.setText("Time" + "\t\t\t"  +
				"Altitude" + "\t\t" + 
				"Bearing" + "\t\t" + 
				"Speed (mph)" + "\t\t" + 
				"Distance (miles)");
		
		ArrayList<LocationData> route = Activity_GoogleMap.routeDataArray.get(Activity_GoogleMap.index);
		
		for(int i = 0; i < route.size(); i++) {			
			String insert = "";
			
			long epoch = Long.parseLong(route.get(i).get_time());//dateTime.get(i));
			String time = new java.text.SimpleDateFormat("HH:mm:ss").format(new java.util.Date (epoch*1000));
			
			insert += time + "\t\t";
			insert += setTabNormal(route.get(i).get_altitude());// + "\t\t\t";			
			insert += setTabNormal(route.get(i).get_bearing());			
			insert += setTabNormal("" + Math.round(convertToMPH(Double.parseDouble(route.get(i).get_speed()))));
			insert += "\t\t\t";
			insert += setTabNormal("" + convertToMiles(Double.parseDouble(route.get(i).get_distance())));
			
			routeArrayAdapter.add(insert);
		}
	}
	
	
	private void setupLarge() {
		heading.setText("Time" + "\t\t\t\t"  +
				"Altitude" + "\t\t\t" + 
				"Bearing" + "\t\t\t\t" + 
				"Speed (mph)" + "\t\t\t\t" + 
				"Distance (miles)");
		
		ArrayList<LocationData> route = Activity_GoogleMap.routeDataArray.get(Activity_GoogleMap.index);
		
		for(int i = 0; i < route.size(); i++) {			
			String insert = "";
			
			long epoch = Long.parseLong(route.get(i).get_time());//dateTime.get(i));
			String time = new java.text.SimpleDateFormat("HH:mm:ss").format(new java.util.Date (epoch*1000));
			
			insert += time + "\t\t\t";
			insert += route.get(i).get_altitude() + "\t\t\t\t";			
			insert += setTabLarge(route.get(i).get_bearing());			
			insert += setTabLarge("" + Math.round(convertToMPH(Double.parseDouble(route.get(i).get_speed()))));
			insert += "\t\t";
			insert += setTabLarge("" + convertToMiles(Double.parseDouble(route.get(i).get_distance())));
			
			routeArrayAdapter.add(insert);
		}
	}
	
	
	//
	// Add tabs to the input
	//
	private String setTabNormal(String input) {		
		DecimalFormat format = new DecimalFormat("######0.00");
		
		if(input.length() > 5)
			input = format.format(Double.parseDouble(input));
				
		if(input.length() <= 4)
			return input + "\t\t\t\t";
		else if(input.length() <= 6)
			return input + "\t\t\t";
		else
			return input + "\t\t";
	}
	
	
	//
	// Add tabs to the input
	//
	private String setTabLarge(String input) {		
		DecimalFormat format = new DecimalFormat("######0.00");
		
		if(input.length() > 5)
			input = format.format(Double.parseDouble(input));
		
		if(input.length() == 1)
			return input + "\t\t\t\t\t\t\t";
		else if(input.length() <= 4)
			return input + "\t\t\t\t\t\t";
		else if(input.length() <= 8)
			return input + "\t\t\t\t\t";
		else
			return input + "\t\t\t\t";
	}
	
	
	private double convertToMiles(double meters) {
		return meters * 0.000621371;
	}
	
	
	private double convertToMPH(double metersPerSecond) {
		return metersPerSecond * 3600 * 0.000621371;
	}	
}
