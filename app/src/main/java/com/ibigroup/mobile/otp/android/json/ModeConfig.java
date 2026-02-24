package com.ibigroup.mobile.otp.android.json;

public class ModeConfig {
    private String filter;
    private String mode;
    private int numItineraries;

    private double bikeReluctance;

    private double carReluctance;
    private double walkReluctance;

    private boolean arriveBy;

    private boolean wheelchair;


    public double getBikeReluctance() {
        return bikeReluctance;
    }

    public void setBikeReluctance(int bikeReluctance) {
        this.bikeReluctance = bikeReluctance;
    }

    public double getCarReluctance() {
        return carReluctance;
    }

    public void setCarReluctance(int carReluctance) {
        this.carReluctance = carReluctance;
    }

    public double getWalkReluctance() {
        return walkReluctance;
    }

    public void setWalkReluctance(int walkReluctance) {
        this.walkReluctance = walkReluctance;
    }

    public boolean isArriveBy() {
        return arriveBy;
    }

    public void setArriveBy(boolean arriveBy) {
        this.arriveBy = arriveBy;
    }

    public boolean isWheelchair() {
        return wheelchair;
    }

    public void setWheelchair(boolean wheelchair) {
        this.wheelchair = wheelchair;
    }

    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public int getNumItineraries() {
        return numItineraries;
    }

    public void setNumItineraries(int numItineraries) {
        this.numItineraries = numItineraries;
    }
}
