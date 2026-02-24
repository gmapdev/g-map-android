package com.ibigroup.mobile.otp.android.json;

import java.util.ArrayList;

public class UserPreferences {
    private String id;
    private String email;
    private String auth0UserId;
    private String notificationChannel; //email, sms or empty
    private String phoneNumber;
    private boolean isDataToolsUser;
    private boolean hasConsentedToTerms;
    private boolean isPhoneNumberVerified;
    private boolean storeTripHistory;

    private int pushDevices;
    private ArrayList<AddedLocation> savedLocations;

    private MobilityProfile mobilityProfile;
    private ArrayList<RelatedUser> relatedUsers;
    private ArrayList<String> dependents;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

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

    public boolean isDataToolsUser() {
        return isDataToolsUser;
    }

    public void setDataToolsUser(boolean dataToolsUser) {
        isDataToolsUser = dataToolsUser;
    }

    public boolean isHasConsentedToTerms() {
        return hasConsentedToTerms;
    }

    public void setHasConsentedToTerms(boolean hasConsentedToTerms) {
        this.hasConsentedToTerms = hasConsentedToTerms;
    }

    public boolean isPhoneNumberVerified() {
        return isPhoneNumberVerified;
    }

    public void setPhoneNumberVerified(boolean phoneNumberVerified) {
        isPhoneNumberVerified = phoneNumberVerified;
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

    public int getPushDevices() {
        return pushDevices;
    }

    public void setPushDevices(int pushDevices) {
        this.pushDevices = pushDevices;
    }

    public MobilityProfile getMobilityProfile() {
        return mobilityProfile;
    }

    public void setMobilityProfile(MobilityProfile mobilityProfile) {
        this.mobilityProfile = mobilityProfile;
    }

    public ArrayList<RelatedUser> getRelatedUsers() {
        return relatedUsers;
    }

    public void setRelatedUsers(ArrayList<RelatedUser> relatedUsers) {
        this.relatedUsers = relatedUsers;
    }

    public ArrayList<String> getDependents() {
        return dependents;
    }

    public void setDependents(ArrayList<String> dependents) {
        this.dependents = dependents;
    }
}
