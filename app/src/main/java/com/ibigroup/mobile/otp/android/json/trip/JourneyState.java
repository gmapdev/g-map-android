package com.ibigroup.mobile.otp.android.json.trip;

import com.ibigroup.mobile.otp.android.json.LastNotification;
import com.ibigroup.mobile.otp.android.json.itinerary.Itinerary;

import java.io.Serializable;
import java.util.ArrayList;

public class JourneyState implements Serializable {
    private long baselineArrivalTimeEpochMillis;
    private long baselineDepartureTimeEpochMillis;
    private long scheduledArrivalTimeEpochMillis;
    private long scheduledDepartureTimeEpochMillis;
    private long lastCheckedEpochMillis;
    private ArrayList<LastNotification> lastNotifications = new ArrayList<>();
    private long lastNotificationTimeMillis;
    private Itinerary matchingItinerary;
    private String targetDate;
    private String tripStatus;
    private boolean hasRealtimeData;

    public long getBaselineArrivalTimeEpochMillis() {
        return baselineArrivalTimeEpochMillis;
    }

    public void setBaselineArrivalTimeEpochMillis(long baselineArrivalTimeEpochMillis) {
        this.baselineArrivalTimeEpochMillis = baselineArrivalTimeEpochMillis;
    }

    public long getBaselineDepartureTimeEpochMillis() {
        return baselineDepartureTimeEpochMillis;
    }

    public void setBaselineDepartureTimeEpochMillis(long baselineDepartureTimeEpochMillis) {
        this.baselineDepartureTimeEpochMillis = baselineDepartureTimeEpochMillis;
    }

    public long getScheduledArrivalTimeEpochMillis() {
        return scheduledArrivalTimeEpochMillis;
    }

    public void setScheduledArrivalTimeEpochMillis(long scheduledArrivalTimeEpochMillis) {
        this.scheduledArrivalTimeEpochMillis = scheduledArrivalTimeEpochMillis;
    }

    public long getScheduledDepartureTimeEpochMillis() {
        return scheduledDepartureTimeEpochMillis;
    }

    public void setScheduledDepartureTimeEpochMillis(long scheduledDepartureTimeEpochMillis) {
        this.scheduledDepartureTimeEpochMillis = scheduledDepartureTimeEpochMillis;
    }

    public long getLastCheckedEpochMillis() {
        return lastCheckedEpochMillis;
    }

    public void setLastCheckedEpochMillis(long lastCheckedEpochMillis) {
        this.lastCheckedEpochMillis = lastCheckedEpochMillis;
    }

    public long getLastNotificationTimeMillis() {
        return lastNotificationTimeMillis;
    }

    public void setLastNotificationTimeMillis(long lastNotificationTimeMillis) {
        this.lastNotificationTimeMillis = lastNotificationTimeMillis;
    }

    public String getTargetDate() {
        return targetDate;
    }

    public void setTargetDate(String targetDate) {
        this.targetDate = targetDate;
    }

    public String getTripStatus() {
        return tripStatus;
    }

    public void setTripStatus(String tripStatus) {
        this.tripStatus = tripStatus;
    }

    public boolean isHasRealtimeData() {
        return hasRealtimeData;
    }

    public void setHasRealtimeData(boolean hasRealtimeData) {
        this.hasRealtimeData = hasRealtimeData;
    }

    public ArrayList<LastNotification> getLastNotifications() {
        return lastNotifications;
    }

    public void setLastNotifications(ArrayList<LastNotification> lastNotifications) {
        this.lastNotifications = lastNotifications;
    }

    public Itinerary getMatchingItinerary() {
        return matchingItinerary;
    }

    public void setMatchingItinerary(Itinerary matchingItinerary) {
        this.matchingItinerary = matchingItinerary;
    }
}
