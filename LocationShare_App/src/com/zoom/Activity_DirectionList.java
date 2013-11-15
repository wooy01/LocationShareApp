package com.zoom;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class Activity_DirectionList extends Activity{
	TextView directionList;
	Button doneButton;
	
			
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.directions);
		setUpUI();  
	} 	
	
	
	private void setUpUI() {
		directionList = (TextView)findViewById(R.id.directions);
		doneButton = (Button)findViewById(R.id.done);
		
		ArrayList<String> tripInfo = Activity_GoogleMap.navigator.get_tripInfo();
		ArrayList<String> distances = Activity_GoogleMap.navigator.get_distances();	
		ArrayList<String> durations = Activity_GoogleMap.navigator.get_durations();	
		ArrayList<String> instructions = removeHTML(Activity_GoogleMap.navigator.get_instructions());	
				
		directionList.append("Start:  " + tripInfo.get(2) + '\n' + '\n');
		
		for(int i = 0; i < instructions.size(); i++) {
			directionList.append((i+1) + ".  " + instructions.get(i) + "  (" + distances.get(i) + ", " + durations.get(i) + ")\n");
		}
		
		directionList.append('\n' + "End:  " + tripInfo.get(3) + '\n');
		
		doneButton.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {	
				finish();					  	
			}			
		});
	}
	
	
	//
	// Remove HTML tags from string
	//
	private ArrayList<String> removeHTML(ArrayList<String> data) {
		ArrayList<String> clean = new ArrayList<String>();
				
		for(int i = 0; i < data.size(); i++) {
			String s = "";
			char[] temp = data.get(i).toCharArray();			
			boolean isDelete = false;
			boolean isLast;
			
			for(int j = 0; j < temp.length; j++) {
				isLast = false;				
				char c = temp[j];
				
				if(c == '<') {					
					isDelete = true;
				}
				else if(c == '>') {
					isDelete = false;
					isLast = true;
				}
								
				if(!isDelete && !isLast) {
					s += c;
				}
			}
			
			clean.add(s);
		}
				
		return clean;
	}
}
