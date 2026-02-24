package com.ibigroup.mobile.otp.android.json.trip;

import java.io.Serializable;

public class ItineraryExistence implements Serializable {
    private String id;
    private long lastUpdated;
    private long dateCreated;
    private ItineraryExistenceDate monday = null;
    private ItineraryExistenceDate tuesday = null;
    private ItineraryExistenceDate wednesday = null;
    private ItineraryExistenceDate thursday = null;
    private ItineraryExistenceDate friday = null;
    private ItineraryExistenceDate saturday = null;
    private ItineraryExistenceDate sunday = null;
    private String message;
    private boolean error;
    private long timestamp;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(long lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public long getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(long dateCreated) {
        this.dateCreated = dateCreated;
    }

    public ItineraryExistenceDate getMonday() {
        return monday;
    }

    public void setMonday(ItineraryExistenceDate monday) {
        this.monday = monday;
    }

    public ItineraryExistenceDate getTuesday() {
        return tuesday;
    }

    public void setTuesday(ItineraryExistenceDate tuesday) {
        this.tuesday = tuesday;
    }

    public ItineraryExistenceDate getWednesday() {
        return wednesday;
    }

    public void setWednesday(ItineraryExistenceDate wednesday) {
        this.wednesday = wednesday;
    }

    public ItineraryExistenceDate getThursday() {
        return thursday;
    }

    public void setThursday(ItineraryExistenceDate thursday) {
        this.thursday = thursday;
    }

    public ItineraryExistenceDate getFriday() {
        return friday;
    }

    public void setFriday(ItineraryExistenceDate friday) {
        this.friday = friday;
    }

    public ItineraryExistenceDate getSaturday() {
        return saturday;
    }

    public void setSaturday(ItineraryExistenceDate saturday) {
        this.saturday = saturday;
    }

    public ItineraryExistenceDate getSunday() {
        return sunday;
    }

    public void setSunday(ItineraryExistenceDate sunday) {
        this.sunday = sunday;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
