package com.zoom.overlay;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.widget.Toast;

import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.OverlayItem;
import com.zoom.Activity_GoogleMap;
import com.zoom.Activity_RouteData;
import com.zoom.R;
import com.zoom.util.LocationData;

public class MapOverlay_Vehicle extends ItemizedOverlay<OverlayItem> {
	private ArrayList<OverlayItem> mOverlays = new ArrayList<OverlayItem>();
	private Activity_GoogleMap activity;

	
	public MapOverlay_Vehicle(Drawable defaultMarker, Activity_GoogleMap context) {
		super(boundCenter(defaultMarker));
		activity = context;
	}

	
	public void addOverlay(OverlayItem overlay) {
		mOverlays.add(overlay);
		populate();
	}

	
	@Override
	protected OverlayItem createItem(int i) {
		return mOverlays.get(i);
	}

	
	@Override
	public int size() {
		return mOverlays.size();
	}

	
	@Override
	protected boolean onTap(final int index) {
		// Set the current index
		Activity_GoogleMap.index = index;
		
		OverlayItem item = mOverlays.get(index);
		
		AlertDialog.Builder dialog = new AlertDialog.Builder(activity);
		dialog.setIcon(activity.getResources().getDrawable(R.drawable.icon_zoom));
		dialog.setTitle(item.getTitle());
		dialog.setMessage(item.getSnippet());
		
		dialog.setPositiveButton("DETAILS", new DialogInterface.OnClickListener() { 
			@Override
			public void onClick(DialogInterface dialog, int which) { 
				dialog.dismiss();
				
				activity.startActivityForResult(new Intent(activity, Activity_RouteData.class), 2); 
			} 
		}); 
		dialog.setNeutralButton("DIRECTIONS", new DialogInterface.OnClickListener() { 
			@Override
			public void onClick(DialogInterface dialog, int which) { 
				dialog.dismiss();
				
				Toast.makeText(activity, "GETTING DRIVING DIRECTIONS", Toast.LENGTH_SHORT).show(); 
				
				activity.removePath();
				activity.removeDirections();	
				
				ArrayList<LocationData> route = Activity_GoogleMap.routeDataArray.get(index);
				Activity_GoogleMap.navigator.showPath(Activity_GoogleMap.whereAmI.getMyLocation(), route.get(route.size() - 1).get_location());  	// start a thread for network connection
			} 
		}); 
		
		dialog.show();
		return true;
	}
}
