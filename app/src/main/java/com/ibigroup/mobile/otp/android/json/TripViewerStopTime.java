package com.ibigroup.mobile.otp.android.json;

public class TripViewerStopTime {
    private String stopId;
    private int stopIndex;
    private long realtimeArrival;
    private long scheduledArrival;
    private long serviceDay;
    private boolean realtime;

    public String getStopId() {
        return stopId;
    }

    public void setStopId(String stopId) {
        this.stopId = stopId;
    }

    public int getStopIndex() {
        return stopIndex;
    }

    public void setStopIndex(int stopIndex) {
        this.stopIndex = stopIndex;
    }

    public long getRealtimeArrival() {
        return realtimeArrival;
    }

    public void setRealtimeArrival(long realtimeArrival) {
        this.realtimeArrival = realtimeArrival;
    }

    public long getScheduledArrival() {
        return scheduledArrival;
    }

    public void setScheduledArrival(long scheduledArrival) {
        this.scheduledArrival = scheduledArrival;
    }

    public long getServiceDay() {
        return serviceDay;
    }

    public void setServiceDay(long serviceDay) {
        this.serviceDay = serviceDay;
    }

    public boolean isRealtime() {
        return realtime;
    }

    public void setRealtime(boolean realtime) {
        this.realtime = realtime;
    }
}
