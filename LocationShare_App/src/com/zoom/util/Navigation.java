package com.zoom.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

//import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Handler;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.zoom.Activity_GoogleMap;
import com.zoom.overlay.MapOverlay_Navigation;

public class Navigation {	
	private MapView mapView;
	private Activity_GoogleMap googleMap;
	
	private ArrayList<String> tripInfo;
	private ArrayList<String> distances;
	private ArrayList<String> durations;
	private ArrayList<String> instructions;
	List<GeoPoint> pointToDraw;
	
	
	public Navigation(Activity_GoogleMap googleMap, MapView mapView) {
		this.googleMap = googleMap;
		this.mapView = mapView;
	}

	
	public void showPath(final GeoPoint source, final GeoPoint destination) {        	
        if(source != null) {		
			new Thread() {
				@Override
				public void run() {
					double fromLat = (double) source.getLatitudeE6() / 1e6;
					double fromLon = (double) source.getLongitudeE6() / 1e6;
					double toLat = (double) destination.getLatitudeE6() / 1e6;
					double toLon = (double) destination.getLongitudeE6() / 1e6;
										
					// Google maps driving directions
					String url = "http://maps.googleapis.com/maps/api/directions/json?origin=" + 
							fromLat + "," +
							fromLon + "&destination=" + 
							toLat + "," +
							toLon + "&sensor=false";
															
					try {
						HttpClient httpclient = new DefaultHttpClient();				 
						HttpPost httppost = new HttpPost(url); 						
						HttpResponse response = httpclient.execute(httppost); 
						HttpEntity entity = response.getEntity(); 
						
						InputStream is = null; 
						is = entity.getContent(); 
						BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8); 
						StringBuilder sb = new StringBuilder(); 
						sb.append(reader.readLine() + "\n"); 
						String line = "0"; 
						
						while ((line = reader.readLine()) != null) { 
						    sb.append(line + "\n"); 
						} 
						
						is.close(); 
						reader.close(); 
						String result = sb.toString(); 
						
						/*** Parse JSON ***/
						JSONObject jsonObject = new JSONObject(result); 
						JSONArray routeArray = jsonObject.getJSONArray("routes"); 
						JSONObject routes = routeArray.getJSONObject(0); 
						JSONObject overviewPolylines = routes.getJSONObject("overview_polyline"); 
						String encodedString = overviewPolylines.getString("points"); 		
												
						tripInfo = new ArrayList<String>();
						distances = new ArrayList<String>();
						durations = new ArrayList<String>();
						instructions = new ArrayList<String>();
						
						JSONArray legs = routes.getJSONArray("legs"); 
						JSONObject legs_objects = legs.getJSONObject(0); 
						JSONObject total_distance = legs_objects.getJSONObject("distance"); 
						JSONObject total_duration = legs_objects.getJSONObject("duration"); 
						
						tripInfo.add(total_distance.getString("text"));
						tripInfo.add(total_duration.getString("text"));						
						tripInfo.add(legs_objects.getString("start_address"));
						tripInfo.add(legs_objects.getString("end_address"));
												
						JSONObject steps1 = legs.getJSONObject(0);
						JSONArray steps = steps1.getJSONArray("steps"); 
											
						for(int i = 0; i < steps.length(); i++) {
							JSONObject temp = steps.getJSONObject(i);
							JSONObject distance_step = temp.getJSONObject("distance"); 
							JSONObject duration_step = temp.getJSONObject("duration");  
													
							distances.add(distance_step.getString("text"));
							durations.add(duration_step.getString("text"));
							instructions.add(temp.getString("html_instructions"));			
						}			
											 
						// Draw path
						pointToDraw = decodePoly(encodedString); 
												
						// Display the results
						mHandler.sendEmptyMessage(0);							
					} catch (Exception e) {
						e.printStackTrace();
					}					
				}
			}.start();
        }
        else
        	Toast.makeText(googleMap.getApplicationContext(), "CANNOT OBTAIN YOUR LOCATION, TRY AGAIN", 
					Toast.LENGTH_SHORT).show(); 
	}
	
	
	//@SuppressLint("HandlerLeak")
	//
	// Display the results
	//
	Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {	
			if(tripInfo != null && !tripInfo.isEmpty())	{		
				Activity_GoogleMap.isNavigationShown = true;
				
				mapView.getOverlays().add(new MapOverlay_Navigation(pointToDraw, Color.BLUE)); 
				mapView.invalidate();
								
				googleMap.showResults("Directions", "Distance:  " + tripInfo.get(0) + "\nDuration:  " + tripInfo.get(1));				
			}
			else 
				Toast.makeText(googleMap.getApplicationContext(), "CANNOT OBTAIN DIRECTIONS", 
						Toast.LENGTH_SHORT).show();  
		};
	};
	
		
	private List<GeoPoint> decodePoly(String encoded) { 		 
	    List<GeoPoint> poly = new ArrayList<GeoPoint>(); 
	    int index = 0, len = encoded.length(); 
	    int lat = 0, lng = 0; 
	 
	    while (index < len) { 
	        int b, shift = 0, result = 0; 
	        do { 
	            b = encoded.charAt(index++) - 63; 
	            result |= (b & 0x1f) << shift; 
	            shift += 5; 
	        } while (b >= 0x20); 
	        int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1)); 
	        lat += dlat; 
	 
	        shift = 0; 
	        result = 0; 
	        do { 
	            b = encoded.charAt(index++) - 63; 
	            result |= (b & 0x1f) << shift; 
	            shift += 5; 
	        } while (b >= 0x20); 
	        int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1)); 
	        lng += dlng; 
	 
	        GeoPoint p = new GeoPoint((int) (((double) lat / 1E5) * 1E6), (int) (((double) lng / 1E5) * 1E6)); 
	        poly.add(p); 
	    } 
	 
	    return poly; 
	} 	
		
	public ArrayList<String> get_tripInfo() {
		return tripInfo;
	}
	
	public ArrayList<String> get_distances() {
		return distances;
	}
	
	public ArrayList<String> get_durations() {
		return durations;
	}
	
	public ArrayList<String> get_instructions() {		
		return instructions;
	}
	
	public void set_instructions(ArrayList<String> instructions) {
		this.instructions = instructions;
	}
}
