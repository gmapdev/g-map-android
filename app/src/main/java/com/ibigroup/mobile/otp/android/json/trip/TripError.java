package com.ibigroup.mobile.otp.android.json.trip;

public class TripError {
    String errorCode;
    String displayText;
    String displaySubText;

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getDisplayText() {
        return displayText;
    }

    public void setDisplayText(String displayText) {
        this.displayText = displayText;
    }

    public String getDisplaySubText() {
        return displaySubText;
    }

    public void setDisplaySubText(String displaySubText) {
        this.displaySubText = displaySubText;
    }
}
