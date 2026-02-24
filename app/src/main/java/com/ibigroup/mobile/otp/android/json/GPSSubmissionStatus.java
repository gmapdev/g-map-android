package com.ibigroup.mobile.otp.android.json;

public class GPSSubmissionStatus {
    private long intervalTimestamp;
    private boolean submittedSuccessfully;
    private String description;

    public long getIntervalTimestamp() {
        return intervalTimestamp;
    }

    public void setIntervalTimestamp(long intervalTimestamp) {
        this.intervalTimestamp = intervalTimestamp;
    }

    public boolean isSubmittedSuccessfully() {
        return submittedSuccessfully;
    }

    public void setSubmittedSuccessfully(boolean submittedSuccessfully) {
        this.submittedSuccessfully = submittedSuccessfully;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
