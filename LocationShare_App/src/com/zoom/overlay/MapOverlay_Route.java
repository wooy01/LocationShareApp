package com.zoom.overlay;

import java.util.ArrayList;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;

import com.google.android.maps.MapView;
import com.zoom.util.LocationData;

public class MapOverlay_Route extends com.google.android.maps.Overlay {
	ArrayList<LocationData> mPoints;
	int routeColor;
	

	public MapOverlay_Route(ArrayList<LocationData> coords, MapView mv, int routeColor) {
		mPoints = coords;
		this.routeColor = routeColor;
	}
	

	@Override
	public boolean draw(Canvas canvas, MapView mv, boolean shadow, long when) {
		super.draw(canvas, mv, shadow);
		drawPath(mv, canvas);
		return true;
	}

	
	public void drawPath(MapView mv, Canvas canvas) {
		int x1 = -1, y1 = -1, x2 = -1, y2 = -1;
		Paint paint = new Paint();
		paint.setAntiAlias(true); 
		paint.setColor(routeColor);
        paint.setStyle(Paint.Style.STROKE); 
        paint.setStrokeWidth(7); 
        paint.setAlpha(90); 
		
		for(int i = 0; i < mPoints.size(); i++) {
			android.graphics.Point point = new android.graphics.Point();
			mv.getProjection().toPixels(mPoints.get(i).get_location(), point);
			x2 = point.x;
			y2 = point.y;

			if (i > 0) {
				canvas.drawLine(x1, y1, x2, y2, paint);
			}

			x1 = x2;
			y1 = y2;
		}		
		
		android.graphics.Point endPoint = new android.graphics.Point();
		mv.getProjection().toPixels(mPoints.get(mPoints.size() - 1).get_location(), endPoint);
		drawOval(canvas, paint, endPoint);
		drawOvalOutline(canvas, paint, endPoint);
	}
	
	
	private void drawOval(Canvas canvas, Paint paint, Point point) { 
        Paint ovalPaint = new Paint(paint); 
        ovalPaint.setStyle(Paint.Style.FILL_AND_STROKE); 
        ovalPaint.setStrokeWidth(2); 
        ovalPaint.setAlpha(255); 
        
        int _radius = 9; 
        RectF oval = new RectF(point.x - _radius, point.y - _radius, point.x + _radius, point.y + _radius); 
        canvas.drawOval(oval, ovalPaint);                
	} 
	
	
	private void drawOvalOutline(Canvas canvas, Paint paint, Point point) { 
        Paint ovalPaint = new Paint(paint); 
        ovalPaint.setStyle(Paint.Style.STROKE); 
        ovalPaint.setStrokeWidth(4); 
        ovalPaint.setAlpha(255); 
        ovalPaint.setColor(Color.WHITE);
        
        int _radius = 9; 
        RectF oval = new RectF(point.x - _radius, point.y - _radius, point.x + _radius, point.y + _radius); 
        canvas.drawOval(oval, ovalPaint);                
	} 
}
