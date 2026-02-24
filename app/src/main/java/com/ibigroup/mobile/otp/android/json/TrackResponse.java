package com.ibigroup.mobile.otp.android.json;

public class TrackResponse {
    private int frequencySeconds = 5;
    private String instruction;
    private String journeyId;
    private String tripStatus;
    private String message;

    public int getFrequencySeconds() {
        return frequencySeconds;
    }

    public void setFrequencySeconds(int frequencySeconds) {
        this.frequencySeconds = frequencySeconds;
    }

    public String getInstruction() {
        return instruction;
    }

    public void setInstruction(String instruction) {
        this.instruction = instruction;
    }

    public String getJourneyId() {
        return journeyId;
    }

    public void setJourneyId(String journeyId) {
        this.journeyId = journeyId;
    }

    public String getTripStatus() {
        return tripStatus;
    }

    public void setTripStatus(String tripStatus) {
        this.tripStatus = tripStatus;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
