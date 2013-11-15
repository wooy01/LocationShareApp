package com.zoom.util;

import com.google.android.maps.GeoPoint;

public class LocationData {
	private GeoPoint location;
	private String time;
	private String accuracy;
	private String altitude;
	private String bearing;
	private String speed;
	private String distance;
	
	public LocationData(GeoPoint location) {
		this.location = location;
	}

	public GeoPoint get_location() {
		return location;
	}

	public String get_time() {
		return time;
	}

	public String get_accuracy() {
		return accuracy;
	}

	public String get_altitude() {
		return altitude;
	}

	public String get_bearing() {
		return bearing;
	}

	public String get_speed() {
		return speed;
	}

	public String get_distance() {
		return distance;
	}

	public void set_location(GeoPoint location) {
		this.location = location;
	}
	
	public void set_time(String time) {
		this.time = time;
	}

	public void set_accuracy(String accuracy) {
		this.accuracy = accuracy;
	}

	public void set_altitude(String altitude) {
		this.altitude = altitude;
	}

	public void set_bearing(String bearing) {
		this.bearing = bearing;
	}

	public void set_speed(String speed) {
		this.speed = speed;
	}

	public void set_distance(String distance) {
		this.distance = distance;
	}
}
