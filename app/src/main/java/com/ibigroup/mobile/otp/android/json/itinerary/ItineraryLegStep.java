package com.ibigroup.mobile.otp.android.json.itinerary;

import java.io.Serializable;

public class ItineraryLegStep implements Serializable {
    private double distance;
    private String relativeDirection;
    private String streetName;
    private String absoluteDirection;
    private boolean stayOn;
    private boolean area;
    private boolean bogusName;
    private double lon;
    private double lat;

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public String getRelativeDirection() {
        return relativeDirection;
    }

    public void setRelativeDirection(String relativeDirection) {
        this.relativeDirection = relativeDirection;
    }

    public String getStreetName() {
        return streetName;
    }

    public void setStreetName(String streetName) {
        this.streetName = streetName;
    }

    public String getAbsoluteDirection() {
        return absoluteDirection;
    }

    public void setAbsoluteDirection(String absoluteDirection) {
        this.absoluteDirection = absoluteDirection;
    }

    public boolean isStayOn() {
        return stayOn;
    }

    public void setStayOn(boolean stayOn) {
        this.stayOn = stayOn;
    }

    public boolean isArea() {
        return area;
    }

    public void setArea(boolean area) {
        this.area = area;
    }

    public boolean isBogusName() {
        return bogusName;
    }

    public void setBogusName(boolean bogusName) {
        this.bogusName = bogusName;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }
}
