package com.ibigroup.mobile.otp.android.json;

import java.io.Serializable;

public class ServiceAlert implements Serializable {

    private String alertDescriptionText;
    private String alertHeaderText;
    private String alertUrl;
    private Long effectiveStartDate;
    private String id;

    public String getAlertDescriptionText() {
        return alertDescriptionText;
    }

    public void setAlertDescriptionText(String alertDescriptionText) {
        this.alertDescriptionText = alertDescriptionText;
    }

    public String getAlertHeaderText() {
        return alertHeaderText;
    }

    public void setAlertHeaderText(String alertHeaderText) {
        this.alertHeaderText = alertHeaderText;
    }

    public String getAlertUrl() {
        return alertUrl;
    }

    public void setAlertUrl(String alertUrl) {
        this.alertUrl = alertUrl;
    }

    public Long getEffectiveStartDate() {
        return effectiveStartDate;
    }

    public void setEffectiveStartDate(Long effectiveStartDate) {
        this.effectiveStartDate = effectiveStartDate;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
