package com.ibigroup.mobile.otp.android.json.itinerary;

import com.ibigroup.mobile.otp.android.json.trip.Otp2QueryParams;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;

public class Itinerary implements Serializable {
    private int duration;
    private int walkTime;
    private int transitTime;
    private int waitingTime;
    private long startTime;
    private long endTime;
    private double walkDistance;
    private boolean walkLimitExceeded;
    private int transfers;
    private double cost = 0.0f;
    private ArrayList<ItineraryLeg> legs;
    private double elevationGained;
    private double elevationLost;
    private String queryParams;
    private ItineraryFare fare;
    private boolean matchedToOtheritinerary = false;
    private ArrayList<Itinerary> matchedItineraries = new ArrayList<>();
    private Double accessibilityScore;

    private String listViewType;

    private Otp2QueryParams otp2QueryParams;

    public Otp2QueryParams getOtp2QueryParams() {
        return otp2QueryParams;
    }

    public void setOtp2QueryParams(Otp2QueryParams otp2QueryParams) {
        this.otp2QueryParams = otp2QueryParams;
    }


    private LinkedHashMap<String, LinkedHashMap<String, LinkedHashMap<String, Double>>> fareTableSum = new LinkedHashMap<>();

    public String getListViewType() {
        return listViewType;
    }

    public Itinerary setListViewType(String listViewType) {
        this.listViewType = listViewType;
        return this;
    }

    public ArrayList<Itinerary> getMatchedItineraries() {
        return matchedItineraries;
    }

    public void setMatchedItineraries(ArrayList<Itinerary> matchedItineraries) {
        this.matchedItineraries = matchedItineraries;
    }

    public boolean isMatchedToOtheritinerary() {
        return matchedToOtheritinerary;
    }

    public void setMatchedToOtheritinerary(boolean matchedToOtheritinerary) {
        this.matchedToOtheritinerary = matchedToOtheritinerary;
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

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
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

    public String getQueryParams() {
        return queryParams;
    }

    public void setQueryParams(String queryParams) {
        this.queryParams = queryParams;
    }

    public ItineraryFare getFare() {
        return fare;
    }

    public void setFare(ItineraryFare fare) {
        this.fare = fare;
    }

    public Double getAccessibilityScore() {
        return accessibilityScore;
    }

    public void setAccessibilityScore(Double accessibilityScore) {
        this.accessibilityScore = accessibilityScore;
    }

    public LinkedHashMap<String, LinkedHashMap<String, LinkedHashMap<String, Double>>> getFareTableSum() {
        return fareTableSum;
    }

    public void setFareTableSum(LinkedHashMap<String, LinkedHashMap<String, LinkedHashMap<String, Double>>> fareTableSum) {
        this.fareTableSum = fareTableSum;
    }
}
