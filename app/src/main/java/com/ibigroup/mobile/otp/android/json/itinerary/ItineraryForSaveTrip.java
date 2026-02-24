package com.ibigroup.mobile.otp.android.json.itinerary;

import com.ibigroup.mobile.otp.android.json.trip.Otp2QueryParams;

import java.io.Serializable;
import java.util.ArrayList;

public class ItineraryForSaveTrip implements Serializable {
    private int duration;
    private int walkTime;
    private int transitTime;
    private int waitingTime;
    private long startTime;
    private long endTime;
    private double walkDistance;
    private boolean walkLimitExceeded;
    private int transfers;
    private ArrayList<ItineraryLeg> legs;
    private double elevationGained;
    private double elevationLost;

    private Otp2QueryParams otp2QueryParams;

    public Otp2QueryParams getOtp2QueryParams() {
        return otp2QueryParams;
    }

    public void setOtp2QueryParams(Otp2QueryParams otp2QueryParams) {
        this.otp2QueryParams = otp2QueryParams;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getWalkTime() {
        return walkTime;
    }

    public void setWalkTime(int walkTime) {
        this.walkTime = walkTime;
    }

    public int getTransitTime() {
        return transitTime;
    }

    public void setTransitTime(int transitTime) {
        this.transitTime = transitTime;
    }

    public int getWaitingTime() {
        return waitingTime;
    }

    public void setWaitingTime(int waitingTime) {
        this.waitingTime = waitingTime;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public double getWalkDistance() {
        return walkDistance;
    }

    public void setWalkDistance(double walkDistance) {
        this.walkDistance = walkDistance;
    }

    public boolean isWalkLimitExceeded() {
        return walkLimitExceeded;
    }

    public void setWalkLimitExceeded(boolean walkLimitExceeded) {
        this.walkLimitExceeded = walkLimitExceeded;
    }

    public int getTransfers() {
        return transfers;
    }

    public void setTransfers(int transfers) {
        this.transfers = transfers;
    }

    public ArrayList<ItineraryLeg> getLegs() {
        return legs;
    }

    public void setLegs(ArrayList<ItineraryLeg> legs) {
        this.legs = legs;
    }

    public double getElevationGained() {
        return elevationGained;
    }

    public void setElevationGained(double elevationGained) {
        this.elevationGained = elevationGained;
    }

    public double getElevationLost() {
        return elevationLost;
    }

    public void setElevationLost(double elevationLost) {
        this.elevationLost = elevationLost;
    }
}
