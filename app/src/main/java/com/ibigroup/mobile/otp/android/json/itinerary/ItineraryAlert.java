package com.ibigroup.mobile.otp.android.json.itinerary;

public class ItineraryAlert {
    private String alertHeaderText;
    private String alertDescriptionText;
    private String alertUrl;
    private String effectiveStartDate;

    public String getAlertHeaderText() {
        return alertHeaderText;
    }

    public void setAlertHeaderText(String alertHeaderText) {
        this.alertHeaderText = alertHeaderText;
    }

    public String getAlertDescriptionText() {
        return alertDescriptionText;
    }

    public void setAlertDescriptionText(String alertDescriptionText) {
        this.alertDescriptionText = alertDescriptionText;
    }

    public String getAlertUrl() {
        return alertUrl;
    }

    public void setAlertUrl(String alertUrl) {
        this.alertUrl = alertUrl;
    }

    public String getEffectiveStartDate() {
        return effectiveStartDate;
    }

    public void setEffectiveStartDate(String effectiveStartDate) {
        this.effectiveStartDate = effectiveStartDate;
    }
}
