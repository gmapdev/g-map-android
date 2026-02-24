package com.ibigroup.mobile.otp.android.json.trip;

import java.io.Serializable;
import java.util.List;

public class Otp2QueryParams implements Serializable {
    private boolean arriveBy;
    private Banned banned;
    private String date;
    private String fromPlace;
    private List<QueryMode> modes;
    private int numItineraries;
    private String time;
    private String toPlace;
    private double walkReluctance;
    private double walkSpeed;
    private boolean wheelchair;
    private String mobilityProfile;

    // Getters and Setters

    public boolean isArriveBy() {
        return arriveBy;
    }

    public void setArriveBy(boolean arriveBy) {
        this.arriveBy = arriveBy;
    }

    public Banned getBanned() {
        return banned;
    }

    public void setBanned(Banned banned) {
        this.banned = banned;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getFromPlace() {
        return fromPlace;
    }

    public void setFromPlace(String fromPlace) {
        this.fromPlace = fromPlace;
    }

    public List<QueryMode> getModes() {
        return modes;
    }

    public void setModes(List<QueryMode> queryModes) {
        this.modes = queryModes;
    }

    public int getNumItineraries() {
        return numItineraries;
    }

    public void setNumItineraries(int numItineraries) {
        this.numItineraries = numItineraries;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getToPlace() {
        return toPlace;
    }

    public void setToPlace(String toPlace) {
        this.toPlace = toPlace;
    }

    public double getWalkReluctance() {
        return walkReluctance;
    }

    public void setWalkReluctance(double walkReluctance) {
        this.walkReluctance = walkReluctance;
    }

    public double getWalkSpeed() {
        return walkSpeed;
    }

    public void setWalkSpeed(double walkSpeed) {
        this.walkSpeed = walkSpeed;
    }

    public boolean isWheelchair() {
        return wheelchair;
    }

    public void setWheelchair(boolean wheelchair) {
        this.wheelchair = wheelchair;
    }

    public String getMobilityProfile() {
        return mobilityProfile;
    }

    public void setMobilityProfile(String mobilityProfile) {
        this.mobilityProfile = mobilityProfile;
    }
}




