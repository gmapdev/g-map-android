package com.ibigroup.mobile.otp.android.json.trip;

import com.ibigroup.mobile.otp.android.json.DependentMobilityProfile;
import com.ibigroup.mobile.otp.android.json.RelatedUser;
import com.ibigroup.mobile.otp.android.json.itinerary.Itinerary;
import com.ibigroup.mobile.otp.android.json.itinerary.ItineraryLegPoint;

import java.io.Serializable;
import java.util.ArrayList;

public class MonitoredTrip implements Serializable {
    private String id;
    private long lastUpdated;
    private long dateCreated;
    private String userId;

    private String tripName;
    private String tripTime;
    private ItineraryLegPoint from;
    private ItineraryLegPoint to;
    private boolean arriveBy;
    private int leadTimeInMinutes;
    private boolean monday;
    private boolean tuesday;
    private boolean wednesday;
    private boolean thursday;
    private boolean friday;
    private boolean saturday;
    private boolean sunday;
    private boolean excludeFederalHolidays;
    private boolean isActive;
    private boolean snoozed;
    private String queryParams;
    private Itinerary itinerary;
    private boolean notifyOnAlert;
    private int departureVarianceMinutesThreshold;
    private int arrivalVarianceMinutesThreshold;
    private boolean notifyOnItineraryChange;
    private ItineraryExistence itineraryExistence;
    private JourneyState journeyState;
    private boolean inactive;

    private String primaryTravelerId;
    private DependentMobilityProfile primary;
    private RelatedUser companion;
    private ArrayList<RelatedUser> observers;

    private Otp2QueryParams otp2QueryParams;

    public Otp2QueryParams getOtp2QueryParams() {
        return otp2QueryParams;
    }

    public void setOtp2QueryParams(Otp2QueryParams qtp2QueryParams) {
        this.otp2QueryParams = qtp2QueryParams;
    }

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

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTripName() {
        return tripName;
    }

    public void setTripName(String tripName) {
        this.tripName = tripName;
    }

    public String getTripTime() {
        return tripTime;
    }

    public void setTripTime(String tripTime) {
        this.tripTime = tripTime;
    }

    public ItineraryLegPoint getFrom() {
        return from;
    }

    public void setFrom(ItineraryLegPoint from) {
        this.from = from;
    }

    public ItineraryLegPoint getTo() {
        return to;
    }

    public void setTo(ItineraryLegPoint to) {
        this.to = to;
    }

    public boolean isArriveBy() {
        return arriveBy;
    }

    public void setArriveBy(boolean arriveBy) {
        this.arriveBy = arriveBy;
    }

    public int getLeadTimeInMinutes() {
        return leadTimeInMinutes;
    }

    public void setLeadTimeInMinutes(int leadTimeInMinutes) {
        this.leadTimeInMinutes = leadTimeInMinutes;
    }

    public boolean isMonday() {
        return monday;
    }

    public void setMonday(boolean monday) {
        this.monday = monday;
    }

    public boolean isTuesday() {
        return tuesday;
    }

    public void setTuesday(boolean tuesday) {
        this.tuesday = tuesday;
    }

    public boolean isWednesday() {
        return wednesday;
    }

    public void setWednesday(boolean wednesday) {
        this.wednesday = wednesday;
    }

    public boolean isThursday() {
        return thursday;
    }

    public void setThursday(boolean thursday) {
        this.thursday = thursday;
    }

    public boolean isFriday() {
        return friday;
    }

    public void setFriday(boolean friday) {
        this.friday = friday;
    }

    public boolean isSaturday() {
        return saturday;
    }

    public void setSaturday(boolean saturday) {
        this.saturday = saturday;
    }

    public boolean isSunday() {
        return sunday;
    }

    public void setSunday(boolean sunday) {
        this.sunday = sunday;
    }

    public boolean isExcludeFederalHolidays() {
        return excludeFederalHolidays;
    }

    public void setExcludeFederalHolidays(boolean excludeFederalHolidays) {
        this.excludeFederalHolidays = excludeFederalHolidays;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public boolean isSnoozed() {
        return snoozed;
    }

    public void setSnoozed(boolean snoozed) {
        this.snoozed = snoozed;
    }

    public String getQueryParams() {
        return queryParams;
    }

    public void setQueryParams(String queryParams) {
        this.queryParams = queryParams;
    }

    public Itinerary getItinerary() {
        return itinerary;
    }

    public void setItinerary(Itinerary itinerary) {
        this.itinerary = itinerary;
    }

    public boolean isNotifyOnAlert() {
        return notifyOnAlert;
    }

    public void setNotifyOnAlert(boolean notifyOnAlert) {
        this.notifyOnAlert = notifyOnAlert;
    }

    public int getDepartureVarianceMinutesThreshold() {
        return departureVarianceMinutesThreshold;
    }

    public void setDepartureVarianceMinutesThreshold(int departureVarianceMinutesThreshold) {
        this.departureVarianceMinutesThreshold = departureVarianceMinutesThreshold;
    }

    public int getArrivalVarianceMinutesThreshold() {
        return arrivalVarianceMinutesThreshold;
    }

    public void setArrivalVarianceMinutesThreshold(int arrivalVarianceMinutesThreshold) {
        this.arrivalVarianceMinutesThreshold = arrivalVarianceMinutesThreshold;
    }

    public boolean isNotifyOnItineraryChange() {
        return notifyOnItineraryChange;
    }

    public void setNotifyOnItineraryChange(boolean notifyOnItineraryChange) {
        this.notifyOnItineraryChange = notifyOnItineraryChange;
    }

    public ItineraryExistence getItineraryExistence() {
        return itineraryExistence;
    }

    public void setItineraryExistence(ItineraryExistence itineraryExistence) {
        this.itineraryExistence = itineraryExistence;
    }

    public JourneyState getJourneyState() {
        return journeyState;
    }

    public void setJourneyState(JourneyState journeyState) {
        this.journeyState = journeyState;
    }

    public boolean isInactive() {
        return inactive;
    }

    public void setInactive(boolean inactive) {
        this.inactive = inactive;
    }

    public String getPrimaryTravelerId() {
        return primaryTravelerId;
    }

    public void setPrimaryTravelerId(String primaryTravelerId) {
        this.primaryTravelerId = primaryTravelerId;
    }

    public DependentMobilityProfile getPrimary() {
        return primary;
    }

    public void setPrimary(DependentMobilityProfile primary) {
        this.primary = primary;
    }

    public RelatedUser getCompanion() {
        return companion;
    }

    public void setCompanion(RelatedUser companion) {
        this.companion = companion;
    }

    public ArrayList<RelatedUser> getObservers() {
        return observers;
    }

    public void setObservers(ArrayList<RelatedUser> observers) {
        this.observers = observers;
    }
}
