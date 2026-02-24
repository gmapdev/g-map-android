package com.ibigroup.mobile.otp.android.json.itinerary;

import java.io.Serializable;

public class ItineraryLegRoute implements Serializable {
    private String color;
    private String id;
    private String longName;
    private String shortName;
    private String textColor;
    private int type;
    private String alerts;

    private String gtfsid;

    public String getGtfsid() {
        return gtfsid;
    }

    public void setGtfsid(String gtfsid) {
        this.gtfsid = gtfsid;
    }

    @Override
    public String toString() {
        return shortName;
    }

    public String getColor() {
        return color;
    }

    public String getId() {
        return id;
    }

    public String getLongName() {
        return longName;
    }

    public String getShortName() {
        return shortName;
    }

    public String getTextColor() {
        return textColor;
    }

    public int getType() {
        return type;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setLongName(String longName) {
        this.longName = longName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public void setTextColor(String textColor) {
        this.textColor = textColor;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getAlerts() {
        return alerts;
    }

    public void setAlerts(String alerts) {
        this.alerts = alerts;
    }
}
