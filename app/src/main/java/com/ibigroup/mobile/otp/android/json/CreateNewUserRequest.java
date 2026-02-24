package com.ibigroup.mobile.otp.android.json;

import java.util.ArrayList;

public class CreateNewUserRequest {
    private String email;
    private String auth0UserId;
    private String notificationChannel = "email"; //email, sms or empty
    private String phoneNumber = "";
    private boolean hasConsentedToTerms = false;
    private boolean storeTripHistory = false;
    private ArrayList<AddedLocation> savedLocations = new ArrayList<AddedLocation>();

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAuth0UserId() {
        return auth0UserId;
    }

    public void setAuth0UserId(String auth0UserId) {
        this.auth0UserId = auth0UserId;
    }

    public String getNotificationChannel() {
        return notificationChannel;
    }

    public void setNotificationChannel(String notificationChannel) {
        this.notificationChannel = notificationChannel;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public boolean isHasConsentedToTerms() {
        return hasConsentedToTerms;
    }

    public void setHasConsentedToTerms(boolean hasConsentedToTerms) {
        this.hasConsentedToTerms = hasConsentedToTerms;
    }

    public boolean isStoreTripHistory() {
        return storeTripHistory;
    }

    public void setStoreTripHistory(boolean storeTripHistory) {
        this.storeTripHistory = storeTripHistory;
    }

    public ArrayList<AddedLocation> getSavedLocations() {
        return savedLocations;
    }

    public void setSavedLocations(ArrayList<AddedLocation> savedLocations) {
        this.savedLocations = savedLocations;
    }
}
