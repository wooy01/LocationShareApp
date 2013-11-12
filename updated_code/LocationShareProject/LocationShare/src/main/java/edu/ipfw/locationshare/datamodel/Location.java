package edu.ipfw.locationshare.datamodel;

public class Location {
    public float Longitude;
    public float Latitude;

    public Location() {

    }

    public Location(float longitude, float latitude) {
        this.Longitude = longitude;
        this.Latitude = latitude;
    }

    public float getLongitude() {
        return Longitude;
    }

    public void setLongitude(float longitude) {
        Longitude = longitude;
    }

    public float getLatitude() {
        return Latitude;
    }

    public void setLatitude(float latitude) {
        Latitude = latitude;
    }
}
