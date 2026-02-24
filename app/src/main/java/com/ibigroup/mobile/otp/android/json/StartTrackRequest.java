package com.ibigroup.mobile.otp.android.json;

public class StartTrackRequest {
    private TrackingLocation location;
    private String tripId;

    public TrackingLocation getLocation() {
        return location;
    }

    public void setLocation(TrackingLocation location) {
        this.location = location;
    }

    public String getTripId() {
        return tripId;
    }

    public void setTripId(String tripId) {
        this.tripId = tripId;
    }
}
