package com.ibigroup.mobile.otp.android.json.trip;

import java.util.ArrayList;

public class MonitoredTripResponse {
    private String clazz = "MonitoredTrip";
    private ArrayList<MonitoredTrip> data;
    private int offset;
    private int limit;
    private int total;
    private long timestamp;

    public String getClazz() {
        return clazz;
    }

    public void setClazz(String clazz) {
        this.clazz = clazz;
    }

    public ArrayList<MonitoredTrip> getData() {
        return data;
    }

    public void setData(ArrayList<MonitoredTrip> data) {
        this.data = data;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
