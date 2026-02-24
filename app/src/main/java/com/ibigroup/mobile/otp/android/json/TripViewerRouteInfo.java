package com.ibigroup.mobile.otp.android.json;

public class TripViewerRouteInfo {
    private String id;
    private int wheelchairAccessible;
    private int tripBikesAllowed;
    private int bikesAllowed;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getWheelchairAccessible() {
        return wheelchairAccessible;
    }

    public void setWheelchairAccessible(int wheelchairAccessible) {
        this.wheelchairAccessible = wheelchairAccessible;
    }

    public int getTripBikesAllowed() {
        return tripBikesAllowed;
    }

    public void setTripBikesAllowed(int tripBikesAllowed) {
        this.tripBikesAllowed = tripBikesAllowed;
    }

    public int getBikesAllowed() {
        return bikesAllowed;
    }

    public void setBikesAllowed(int bikesAllowed) {
        this.bikesAllowed = bikesAllowed;
    }
}
