package com.zoom;

import java.text.DecimalFormat;
import java.util.ArrayList;

import com.zoom.util.LocationData;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class Activity_Routes extends Activity{
	TextView heading;
	ListView directionList;
	Button doneButton;
	ArrayAdapter<String> routeArrayAdapter;
	boolean isNormal;
	
			
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.routes);
		setUpUI();  
	} 	
	
	
	private void setUpUI() {
		heading = (TextView)findViewById(R.id.heading);
		directionList = (ListView)findViewById(R.id.listView1);
		doneButton = (Button)findViewById(R.id.done);

		routeArrayAdapter = new ArrayAdapter<String>(this, R.layout.message);
		directionList.setAdapter(new myCustomAdapter()); 
				
		// Determine device screen size
		if ((getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == 
				Configuration.SCREENLAYOUT_SIZE_NORMAL) 
		{
			isNormal = true;
			setupNormal();
		} 
		else {
			isNormal = false;
			setupLarge();
		}
			
		doneButton.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {	
				finish();					  	
			}			
		});
	}
	
		
	//
	// Display heading for normal screen sizes
	//
	private void setupNormal() {
		heading.setText("#" + "\t\t" +
				"Color" + "\t" + 
				"Time" + "\t\t\t\t"  +
				"Speed (mph)" + "\t\t" + 
				"Distance (miles)");
	}
	
	
	//
	// Display heading for large screen sizes
	//
	private void setupLarge() {
		heading.setText("#" + "\t\t\t\t" +
				"Color" + "\t\t\t\t" + 
				"Time" + "\t\t\t\t\t\t"  +
				"Speed (mph)" + "\t\t\t\t" + 
				"Distance (miles)");
	}
		
	
	//
	// Display data for normal screen sizes
	//
	public String getDataNormal(int index) {
		ArrayList<LocationData> route = Activity_GoogleMap.routeDataArray.get(index);
		String insert = "";
		
		long epoch = Long.parseLong(route.get(route.size() - 1).get_time());
		String time = new java.text.SimpleDateFormat("HH:mm:ss").format(new java.util.Date (epoch*1000));
		
		insert += (index + 1) + "\t\t";
		insert += "\t\t\t";
		insert += time + "\t\t\t";
		insert += setTabNormal("" + Math.round(convertToMPH(Double.parseDouble(route.get(route.size() - 1).get_speed()))));
		insert += "\t\t\t\t\t\t";
		insert += setTabNormal("" + convertToMiles(Double.parseDouble(route.get(route.size() - 1).get_distance())));
		
		return insert;
	}
	
	
	//
	// Display data for large screen sizes
	//
	public String getDataLarge(int index) {
		ArrayList<LocationData> route = Activity_GoogleMap.routeDataArray.get(index);
		String insert = "";
		
		long epoch = Long.parseLong(route.get(route.size() - 1).get_time());
		String time = new java.text.SimpleDateFormat("HH:mm:ss").format(new java.util.Date (epoch*1000));
		
		insert += (index + 1) + "\t\t\t\t\t\t\t\t\t\t";
		insert += time + "\t\t\t\t\t";
		insert += setTabLarge("" + Math.round(convertToMPH(Double.parseDouble(route.get(route.size() - 1).get_speed()))));
		insert += "\t\t\t";
		insert += setTabLarge("" + convertToMiles(Double.parseDouble(route.get(route.size() - 1).get_distance())));
		
		return insert;
	}
	
		
	//
	// Add tabs to the input
	//
	private String setTabNormal(String input) {		
		DecimalFormat format = new DecimalFormat("######0.00");
		
		if(input.length() > 5)
			input = format.format(Double.parseDouble(input));
		
		if(input.length() <= 3)
			return input + "\t";
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
			return input + "\t\t\t\t\t\t";
		else if(input.length() <= 4)
			return input + "\t\t\t\t\t";
		else if(input.length() <= 8)
			return input + "\t\t\t\t";
		else
			return input + "\t\t\t";
	}
	
	
	private double convertToMiles(double meters) {
		return meters * 0.000621371;
	}
	
	
	private double convertToMPH(double metersPerSecond) {
		return metersPerSecond * 3600 * 0.000621371;
	}	
	
	
	//
	// Custom ArrayAdapter
	//
	class myCustomAdapter extends BaseAdapter {
		Button colorButton;
		TextView userName;
		int colorCount = Activity_GoogleMap.colors.length;	

		/**
		 * returns the count of elements in the Array that is used to draw the
		 * text in rows
		 * 
		 * @see android.widget.Adapter#getCount()
		 */
		@Override
		public int getCount() {
			// return the length of the data array, so that the List View knows
			// how much rows it has to draw
			return Activity_GoogleMap.routeDataArray.size();
		}

		/**
		 * @param position
		 *            The position of the row that was clicked (0-n)
		 * @see android.widget.Adapter#getItem(int)
		 */
		@Override
		public String getItem(int position) {
			return null;
		}

		/**
		 * @param position
		 *            The position of the row that was clicked (0-n)
		 * @see android.widget.Adapter#getItemId(int)
		 */
		@Override
		public long getItemId(int position) {
			return position;
		}

		/**
		 * Returns the complete row that the System draws. It is called every
		 * time the System needs to draw a new row; You can control the
		 * appearance of each row inside this function.
		 * 
		 * @param position
		 *            The position of the row that was clicked (0-n)
		 * @param convertView
		 *            The View object of the row that was last created. null if
		 *            its the first row
		 * @param parent
		 *            The ViewGroup object of the parent view
		 * @see android.widget.Adapter#getView(int, android.view.View,
		 *      android.view.ViewGroup)
		 */
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View row = convertView;
			final int pos = position;
			
			if (row == null) {
				// Getting custom layout to the row
				LayoutInflater inflater = getLayoutInflater();
				row = inflater.inflate(R.layout.messageroutes, parent, false);
			}
						
			int color = Activity_GoogleMap.colors[(position + colorCount) % colorCount];
			
			colorButton = (Button) row.findViewById(R.id.colorbutton);
			colorButton.setBackgroundColor(color);
			
			// Get a reference to the row's text view; find with row.findViewById()
			userName = (TextView) row.findViewById(R.id.textview);
			
			if(isNormal)
				userName.setText(getDataNormal(position));
			else
				userName.setText(getDataLarge(position));
			
			row.setOnClickListener(new OnClickListener() {    
	        	public void onClick(View v) {           		
	        		Activity_GoogleMap.index = pos;
	        		
	        		// Launch activity to view route data
	                startActivityForResult(new Intent(getApplicationContext(), Activity_RouteData.class), 11);       			
	        	}
	        });
			
			return row; // the row that ListView draws
		}
	}
}
