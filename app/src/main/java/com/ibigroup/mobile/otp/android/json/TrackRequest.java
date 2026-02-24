package com.ibigroup.mobile.otp.android.json;

import java.util.ArrayList;

public class TrackRequest {
    private String tripId;

    private ArrayList<TrackingLocation> locations = new ArrayList<>();

    public String getTripId() {
        return tripId;
    }

    public void setTripId(String tripId) {
        this.tripId = tripId;
    }

    public ArrayList<TrackingLocation> getLocations() {
        return locations;
    }

    public void setLocations(ArrayList<TrackingLocation> locations) {
        this.locations = locations;
    }
}
