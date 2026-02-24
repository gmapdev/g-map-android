package com.ibigroup.mobile.otp.android.json.trip;

import com.ibigroup.mobile.otp.android.json.DependentMobilityProfile;
import com.ibigroup.mobile.otp.android.json.RelatedUser;
import com.ibigroup.mobile.otp.android.json.itinerary.ItineraryForSaveTrip;

import java.util.ArrayList;

public class CreateNewTripRequest {
    private String userId;
    private String tripName;
    private int leadTimeInMinutes = 30;
    private boolean monday;
    private boolean tuesday;
    private boolean wednesday;
    private boolean thursday;
    private boolean friday;
    private boolean saturday;
    private boolean sunday;
    private boolean excludeFederalHolidays = true;
    private boolean isActive = true;
    private String queryParams;
    private ItineraryForSaveTrip itinerary;
    private boolean notifyOnAlert = true;
    private int departureVarianceMinutesThreshold = 5;
    private int arrivalVarianceMinutesThreshold = 5;
    private boolean notifyOnItineraryChange = true;
    private Otp2QueryParams otp2QueryParams;
    private String primaryTravelerId;
    private DependentMobilityProfile primary;
    private RelatedUser companion;
    private ArrayList<RelatedUser> observers;

    public void copyFromMonitoredTripUpdateRequest(MonitoredTripUpdateRequest monitoredTrip) {
        if (monitoredTrip != null) {
            this.setUserId(monitoredTrip.getUserId());
            this.setTripName(monitoredTrip.getTripName());
            this.setLeadTimeInMinutes(monitoredTrip.getLeadTimeInMinutes());
            this.setMonday(monitoredTrip.isMonday());
            this.setTuesday(monitoredTrip.isTuesday());
            this.setWednesday(monitoredTrip.isWednesday());
            this.setThursday(monitoredTrip.isThursday());
            this.setFriday(monitoredTrip.isFriday());
            this.setSaturday(monitoredTrip.isSaturday());
            this.setSunday(monitoredTrip.isSunday());
            this.setExcludeFederalHolidays(monitoredTrip.isExcludeFederalHolidays());
            this.setActive(monitoredTrip.isActive());

            this.setQueryParams(monitoredTrip.getQueryParams());
            this.setItinerary(monitoredTrip.getItinerary());
            this.setNotifyOnAlert(monitoredTrip.isNotifyOnAlert());
            this.setDepartureVarianceMinutesThreshold(monitoredTrip.getDepartureVarianceMinutesThreshold());
            this.setArrivalVarianceMinutesThreshold(monitoredTrip.getArrivalVarianceMinutesThreshold());
            this.setNotifyOnItineraryChange(monitoredTrip.isNotifyOnItineraryChange());
            this.setOtp2QueryParams(itinerary.getOtp2QueryParams());

            this.primaryTravelerId = monitoredTrip.getPrimaryTravelerId();
            this.setPrimary(monitoredTrip.getPrimary());
            this.setCompanion(monitoredTrip.getCompanion());
            this.setObservers(monitoredTrip.getObservers());
        }
    }

    public Otp2QueryParams getOtp2QueryParams() {
        return otp2QueryParams;
    }

    public void setOtp2QueryParams(Otp2QueryParams otp2QueryParams) {
        this.otp2QueryParams = otp2QueryParams;
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

    public String getQueryParams() {
        return queryParams;
    }

    public void setQueryParams(String queryParams) {
        this.queryParams = queryParams;
    }

    public ItineraryForSaveTrip getItinerary() {
        return itinerary;
    }

    public void setItinerary(ItineraryForSaveTrip itinerary) {
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
