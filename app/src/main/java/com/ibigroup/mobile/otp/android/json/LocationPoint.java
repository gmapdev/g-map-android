package com.ibigroup.mobile.otp.android.json;

import java.io.Serializable;
import java.util.Objects;

public class LocationPoint implements Serializable {
    private String address;
    private double lat;
    private double lon;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LocationPoint)) return false;
        LocationPoint that = (LocationPoint) o;
        return Double.compare(that.getLat(), getLat()) == 0 &&
                Double.compare(that.getLon(), getLon()) == 0 &&
                Objects.equals(getAddress(), that.getAddress());
    }

    public LocationPoint(LocationPoint old) {
        if (old != null) {
            this.address = old.address;
            this.lat = old.lat;
            this.lon = old.lon;
        }
    }

    public LocationPoint() {
    }
}
