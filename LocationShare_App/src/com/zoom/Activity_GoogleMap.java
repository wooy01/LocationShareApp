package com.zoom;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.android.Facebook;
import com.facebook.model.GraphUser;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;
import com.zoom.overlay.MapOverlay_Route;
import com.zoom.overlay.MapOverlay_Vehicle;
import com.zoom.util.GPS_Manager;
import com.zoom.util.LocationData;
import com.zoom.util.Navigation;
import com.zoom.util.ServerFlags;
import com.zoom.util.ServerInfo;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class Activity_GoogleMap extends MapActivity {    
    private static MapView mapView = null;
    private MapController mapController = null;
    public static MyLocationOverlay whereAmI = null;
	private int zoomLevel = 13;
	private static boolean isRouteShown = false;
	public static boolean isNavigationShown = false;
		
	private TextView distance_textview;
	private Button list;
	private Button remove;
	
	static int[] colors = {Color.RED, Color.CYAN, Color.GREEN, Color.YELLOW, Color.MAGENTA};
	
	public static ArrayList<ArrayList<LocationData>> routeDataArray;	
	static ArrayList<LocationData> routeData;
	public static int index;
	
	public static Navigation navigator;
	
	private final int VIEW_MESSAGES = 0;
	
	
	/** Called when the activity is first created. */ 
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.googlemap); 
				
		if(isNavigationShown) {
			isNavigationShown = false;
			removePath();
		}
		
		//Toast.makeText(getApplicationContext(), "GETTING DATA FROM SERVER", Toast.LENGTH_SHORT).show();
		
		// Get routes from server
		//new GetRoutesFromServerTCP().execute();  	// start a thread for network connection
		
		// Get images from server
		//new GetImagesFromServer().execute();  	// start a thread for network connection
		
		setupActivity();
	}   

	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		
		if(isNavigationShown) 
			setElementSizeNavigation();
		else if(isRouteShown)
			setElementSizeRoutes();		
	}

	
	private void setupActivity() {
		distance_textview = (TextView) findViewById(R.id.distance);
		distance_textview.setVisibility(TextView.GONE);
		
		list = (Button) findViewById(R.id.list);
		list.setVisibility(Button.GONE);     
        list.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				showDirections();
			}
		}); 
        
        remove = (Button) findViewById(R.id.remove);
        remove.setVisibility(Button.GONE);     
        remove.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				removePath();
				removeDirections();
				showResults("Routes", "Number of Routes:\t" + routeDataArray.size());
			}
		});
		
        
        /***** Map *****/
        // Set up the map
		mapView = (MapView) findViewById(R.id.geoMap);
		mapView.setBuiltInZoomControls(true);
		mapView.setSatellite(true);

		mapController = mapView.getController();
		mapController.setZoom(zoomLevel);        // integer from 1-22 (max zoom-in) 

		whereAmI = new MyLocationOverlay(getApplicationContext(), mapView);		

		mapView.getOverlays().add(whereAmI);
		mapView.invalidate();	
		
		navigator = new Navigation(this, mapView); 
	}
	
	
	//
	// Remove the route overlay
	//
	public void removePath() {
		if(isNavigationShown) {
			isNavigationShown = false;		
			
			// Remove driving route from map
			List<Overlay> listOfOverlays = mapView.getOverlays();
			listOfOverlays.remove(listOfOverlays.size() - 1);
			mapView.invalidate();
		}
	}
	
	
	//
	// Remove the route information
	//
	public void removeDirections() {	
		list.setVisibility(Button.GONE);
		remove.setVisibility(Button.GONE);
		distance_textview.setVisibility(TextView.GONE);
	}
		
		
	@Override
	protected boolean isLocationDisplayed() {
		return whereAmI.isMyLocationEnabled();
	}

	
	@Override
	protected boolean isRouteDisplayed() {
		return false;
	} 

	
	@Override
	public void onResume() {
		super.onResume();	
				
		if(whereAmI.enableMyLocation()) 			
			whereAmI.runOnFirstFix(new Runnable() {
				public void run() {
					mapController.setCenter(whereAmI.getMyLocation());	// enable auto-center
				}	            
			});		
		else	
			new GPS_Manager(this).enableGPS();   			
	}

	
	@Override
	protected void onPause() {
		super.onPause();

whereAmI.disableMyLocation();

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		
		MenuInflater inflater = getMenuInflater();    
		inflater.inflate(R.menu.menu, menu); 		
		return true;
	}

	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection    
		switch (item.getItemId()) {		
		case R.id.satellite:				// set map to satellite view
			mapView.setSatellite(true);			
			if (item.isChecked()) 
				item.setChecked(false);    
			else 
				item.setChecked(true);
			break;
		case R.id.normal:					// set map to normal view
			mapView.setSatellite(false);  	
			if (item.isChecked()) 
				item.setChecked(false);    
			else 
				item.setChecked(true);
			break;			
		case R.id.center:					// center map on users location
			try {
        		mapController.animateTo(whereAmI.getMyLocation());
        	}
        	catch(Exception e) {	            		
        	}	
			break;
		case R.id.view_messages:
			//Toast.makeText(getApplicationContext(), "View Messages", Toast.LENGTH_SHORT).show();
			startActivityForResult(new Intent(this, Activity_ViewMessages.class), VIEW_MESSAGES);
			break;
		case R.id.create_message:
			//Toast.makeText(getApplicationContext(), "Create Message", Toast.LENGTH_SHORT).show();	
			startActivity(new Intent(this, Activity_CreateMessage.class));
			break;
		case R.id.logout:
			//Toast.makeText(getApplicationContext(), "Logout", Toast.LENGTH_SHORT).show();
			//this.getSharedPreferences("YOUR_PREFS", 0).edit().clear().commit();
			Session.getActiveSession().closeAndClearTokenInformation();
			Session.setActiveSession(null);
			
			//trimCache(this);

			finish();
			break;
			
		default:        
			return super.onOptionsItemSelected(item);
		}		
		return true;
	}    
	
	
	
	
	
	 public static void trimCache(Context context) {
	      try {
	         File dir = context.getCacheDir();
	         if (dir != null && dir.isDirectory()) {
	            deleteDir(dir);
	         }
	      } catch (Exception e) {
	         // TODO: handle exception
	      }
	   }

	   public static boolean deleteDir(File dir) {
	      if (dir != null && dir.isDirectory()) {
	         String[] children = dir.list();
	         for (int i = 0; i < children.length; i++) {
	            boolean success = deleteDir(new File(dir, children[i]));
	            if (!success) {
	               return false;
	            }
	         }
	      }

	      // The directory is now empty so delete it
	      return dir.delete();
	   }
	   
	   
	   
	   
	
	
	//
	// Thread for network connection to get routes
	//
	private class GetRoutesFromServerTCP extends AsyncTask<Void, Void, Boolean> {
		protected Boolean doInBackground(Void... params) {				
			routeDataArray = null;
			getRoutesFromServerTCP();
			return routeDataArray != null && !routeDataArray.isEmpty();
		}

		// Called when doInBackground returns; can interact with the UI thread
		protected void onPostExecute(Boolean isSuccess) {				
			if(!isSuccess) {
				Toast.makeText(getApplicationContext(), "ERROR IN NETWORK CALL", Toast.LENGTH_SHORT).show();
			}
			else {
				List<Overlay> listOfOverlays = mapView.getOverlays();				
				int colorCount = colors.length;				
								
				for(int i = 0; i < routeDataArray.size(); i++) {
					int color = colors[(i + colorCount) % colorCount];
					
					listOfOverlays.add(new MapOverlay_Route(routeDataArray.get(i), mapView, color));
					mapView.invalidate();
				}
				
				if(routeDataArray.size() > 0)
					isRouteShown = true;
												
				showResults("Routes", "Number of Routes:\t" + routeDataArray.size());
				
				setupOverlayRoutes();
			}
		}
	} 
	
	
	//
	// Get the routes from the server and display them on the map
	//
	private void getRoutesFromServerTCP() {		
		try {
			// Establish a TCP connection with the server
			Socket clientSocket = new Socket(ServerInfo.HOST_NAME, ServerInfo.PORT_NUMBER);
			
			if(clientSocket.isConnected()) {
				// Create a stream to send input to the server
				DataOutputStream outToServer = new DataOutputStream(
						clientSocket.getOutputStream());
	
				// Create a stream to receive output from the server
				BufferedReader inFromServer = new BufferedReader(
						new InputStreamReader(clientSocket.getInputStream()));
	
				// Request tracking data from server;
				// NOTE: terminate user input with the newline character
				outToServer.writeBytes(ServerFlags.REQUEST_GPS + "\n");
		
				int delimiter_1;
				int delimiter_2;
				double lat;
				double lon;	
				String temp = inFromServer.readLine();					
				
				routeDataArray = new ArrayList<ArrayList<LocationData>>();				
				routeData = new ArrayList<LocationData>();				
				
				while (temp != null) {									
					if(temp.equals("end")) {
						routeDataArray.add(routeData);
						routeData = new ArrayList<LocationData>();
					}
					else {
						// Parse the line returned from the server
						delimiter_1 = temp.indexOf(',');
						delimiter_2 = temp.indexOf(';');
											
						// Get the coordinates and create a LocationData object
						lat = Double.parseDouble(temp.substring(0, delimiter_1).trim());
						lon = Double.parseDouble(temp.substring(++delimiter_1, delimiter_2).trim());					
						LocationData location = new LocationData(new GeoPoint((int) (lat * 1000000), (int) (lon * 1000000)));
											
						// Get the time (epoch)
						delimiter_1 = delimiter_2;
						delimiter_2 = temp.indexOf(';', ++delimiter_1);
						location.set_time(temp.substring(++delimiter_1, delimiter_2));
						
						// Get the accuracy
						delimiter_1 = delimiter_2;
						delimiter_2 = temp.indexOf(';', ++delimiter_1);
						location.set_accuracy(temp.substring(++delimiter_1, delimiter_2));
						
						// Get the altitude
						delimiter_1 = delimiter_2;
						delimiter_2 = temp.indexOf(';', ++delimiter_1);					
						location.set_altitude(temp.substring(++delimiter_1, delimiter_2));
						
						// Get the bearing
						delimiter_1 = delimiter_2;
						delimiter_2 = temp.indexOf(';', ++delimiter_1);
						location.set_bearing(temp.substring(++delimiter_1, delimiter_2));
						
						// Get the speed
						delimiter_1 = delimiter_2;
						delimiter_2 = temp.indexOf(';', ++delimiter_1);
						location.set_speed(temp.substring(++delimiter_1, delimiter_2));
						
						// Get the distance
						delimiter_1 = delimiter_2;
						location.set_distance(temp.substring(++delimiter_1));
						
						routeData.add(location);	
					}
						
					temp = inFromServer.readLine();			
				}
											
				// Close the streams and TCP connection to the server
				inFromServer.close();
				outToServer.close();
				clientSocket.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	//
	// Overlay route icons on the map that can be tapped
	//
	private void setupOverlayRoutes() {			
		/*
		List<Overlay> mapOverlays = mapView.getOverlays();
		Drawable drawable = getResources().getDrawable(R.drawable.trans);
		MapOverlay_Vehicle itemizedoverlay = new MapOverlay_Vehicle(drawable, this);
		
		for (int i = 0; i < routeDataArray.size(); i++) {
			ArrayList<LocationData> route = routeDataArray.get(i);

			// Get and format the date/time
			long epoch = Long.parseLong(route.get(0).get_time());
			String date = new java.text.SimpleDateFormat("MM/dd/yyyy HH:mm:ss")
					.format(new java.util.Date(epoch * 1000));

			// Get and format the distance in miles
			DecimalFormat format_distance = new DecimalFormat("######0.00");			
			String distance_miles = format_distance
					.format(convertToMiles(Double.parseDouble(
							route.get(route.size() - 1).get_distance())));

			long speed = Math.round(convertToMPH(Double.parseDouble(
					route.get(route.size() - 1).get_speed())));			
			
			String title = "Route: " + (i + 1);
			String message = "Start Time:\t\t" + date + "\n"
					+ "Distance:\t\t\t" + distance_miles + " miles" + "\n"
					+ "Speed:\t\t\t\t\t" + speed + " mph";

			GeoPoint point = route.get(route.size() - 1).get_location();
			OverlayItem overlayitem = new OverlayItem(point, title, message);

			itemizedoverlay.addOverlay(overlayitem);			
		}

		// Add overlays to the map
		mapOverlays.add(itemizedoverlay);
		*/
	}
			
	
	public void showResults(String label, String message) {	
		if(isNavigationShown) {
			setElementSizeNavigation();
			remove.setVisibility(Button.VISIBLE);
		}
		else
			setElementSizeRoutes();
		
		list.setVisibility(Button.VISIBLE);
		distance_textview.setVisibility(TextView.VISIBLE);
		
		list.setText(label);			
		distance_textview.setText(message);
	}
		
	
	private void setElementSizeRoutes() {		
		RelativeLayout.LayoutParams layoutParams2 =(RelativeLayout.LayoutParams)list.getLayoutParams(); 
		layoutParams2.addRule(RelativeLayout.CENTER_HORIZONTAL, 0); //ALIGN_PARENT_RIGHT / LEFT etc. 
		list.setLayoutParams(layoutParams2);
		
		RelativeLayout.LayoutParams layoutParams1 =(RelativeLayout.LayoutParams)list.getLayoutParams(); 
		layoutParams1.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 1); //ALIGN_PARENT_RIGHT / LEFT etc. 
		list.setLayoutParams(layoutParams1);
		
		// Get the screen size; height and width
		DisplayMetrics metrics = new DisplayMetrics(); 
		getWindowManager().getDefaultDisplay().getMetrics(metrics); 
		 
		//int height = metrics.heightPixels; 
		int width = metrics.widthPixels; 
					
		// Set the width of the button and text view
		list.setWidth(width / 2);
		distance_textview.setWidth(width / 2);
	}
	
	
	private void setElementSizeNavigation() {
		RelativeLayout.LayoutParams layoutParams1 =(RelativeLayout.LayoutParams)list.getLayoutParams(); 
		layoutParams1.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 0); //ALIGN_PARENT_RIGHT / LEFT etc. 
		list.setLayoutParams(layoutParams1);
		
		RelativeLayout.LayoutParams layoutParams2 =(RelativeLayout.LayoutParams)list.getLayoutParams(); 
		layoutParams2.addRule(RelativeLayout.CENTER_HORIZONTAL, 1); //ALIGN_PARENT_RIGHT / LEFT etc. 
		list.setLayoutParams(layoutParams2);
		
		// Get the screen size; height and width
		DisplayMetrics metrics = new DisplayMetrics(); 
		getWindowManager().getDefaultDisplay().getMetrics(metrics); 
		 
		//int height = metrics.heightPixels; 
		int width = metrics.widthPixels; 
					
		// Set the width of the button and text view
		list.setWidth(width / 3);
		remove.setWidth(width / 3);
		distance_textview.setWidth(width / 3);
	}
	
	
	// Launch activity to display driving directions
	private void showDirections() {
		if(isNavigationShown)
			startActivityForResult(new Intent(this, Activity_DirectionList.class), 0); 
		else
			startActivityForResult(new Intent(this, Activity_Routes.class), 0); 
	}
		
	
	//
	// Called after a launched activity returns
	//
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {		
		if(requestCode == VIEW_MESSAGES){
			if(resultCode == RESULT_OK) {
				//Session.getActiveSession().onActivityResult(this, requestCode, resultCode, intent);
			}    		  
			else {
				//finish();
			}    		  
		}
	} 
} 
