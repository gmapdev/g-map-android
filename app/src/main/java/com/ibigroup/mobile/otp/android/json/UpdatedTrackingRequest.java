package com.ibigroup.mobile.otp.android.json;

import java.util.ArrayList;

public class UpdatedTrackingRequest {
    private String journeyId;

    private ArrayList<TrackingLocation> locations = new ArrayList<>();

    public String getJourneyId() {
        return journeyId;
    }

    public void setJourneyId(String journeyId) {
        this.journeyId = journeyId;
    }

    public ArrayList<TrackingLocation> getLocations() {
        return locations;
    }

    public void setLocations(ArrayList<TrackingLocation> locations) {
        this.locations = locations;
    }
}
