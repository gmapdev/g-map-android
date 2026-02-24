package com.ibigroup.mobile.otp.android.json.itinerary;

import java.io.Serializable;

public class ItineraryLegGeometry implements Serializable {
    private String points;
    private int length;

    public String getPoints() {
        return points;
    }

    public void setPoints(String points) {
        this.points = points;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }
}
