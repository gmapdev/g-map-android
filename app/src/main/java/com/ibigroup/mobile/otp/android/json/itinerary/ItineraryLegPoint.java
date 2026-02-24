package com.ibigroup.mobile.otp.android.json.itinerary;

import java.io.Serializable;

public class ItineraryLegPoint implements Serializable {
    private String name;
    private String stopId;
    private String stopCode;
    private int stopIndex;
    private int stopSequence;
    private double lon;
    private double lat;
    private long arrival;
    private long departure;
    private String vertexType;
    private String orig;
    private ItineraryLegPointStop stop;

    private RentalVehicle rentalVehicle;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public long getArrival() {
        return arrival;
    }

    public void setArrival(long arrival) {
        this.arrival = arrival;
    }

    public long getDeparture() {
        return departure;
    }

    public void setDeparture(long departure) {
        this.departure = departure;
    }

    public String getStopId() {
        if (stopId == null) {
            if (stop != null) {
                if (stop.getGtfsId() != null) {
                    stopId = stop.getGtfsId();
                } else if (stop.getId() != null) {
                    stopId = stop.getId();
                }
            }
        }
        return stopId;
    }

    public void setStopId(String stopId) {
        this.stopId = stopId;
    }

    public String getStopCode() {
        if (stopCode == null) {
            if (stop != null && stop.getCode() != null) {
                stopCode = stop.getCode();
            }
        }
        return stopCode;
    }

    public void setStopCode(String stopCode) {
        this.stopCode = stopCode;
    }

    public int getStopIndex() {
        return stopIndex;
    }

    public void setStopIndex(int stopIndex) {
        this.stopIndex = stopIndex;
    }

    public int getStopSequence() {
        return stopSequence;
    }

    public void setStopSequence(int stopSequence) {
        this.stopSequence = stopSequence;
    }

    public String getVertexType() {
        return vertexType;
    }

    public void setVertexType(String vertexType) {
        this.vertexType = vertexType;
    }

    public String getOrig() {
        return orig;
    }

    public void setOrig(String orig) {
        this.orig = orig;
    }

    public ItineraryLegPointStop getStop() {
        return stop;
    }

    public void setStop(ItineraryLegPointStop stop) {
        this.stop = stop;
    }

    public RentalVehicle getRentalVehicle() {
        return rentalVehicle;
    }

    public void setRentalVehicle(RentalVehicle rentalVehicle) {
        this.rentalVehicle = rentalVehicle;
    }
}
