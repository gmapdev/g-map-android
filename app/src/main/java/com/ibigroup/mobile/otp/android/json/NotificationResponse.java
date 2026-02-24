package com.ibigroup.mobile.otp.android.json;

import com.google.gson.annotations.SerializedName;

public class NotificationResponse {
    @SerializedName(value = "message", alternate = {"msg"})
    private String msg;
    @SerializedName(value = "trip_id", alternate = {"tripId"})
    private String tripId;
    private String title;
    @SerializedName(value = "survey_id", alternate = {"surveyId"})
    private String surveyId;
    @SerializedName(value = "user_id", alternate = {"userId"})
    private String userId;
    @SerializedName(value = "notification_type", alternate = {"notificationType"})
    private String notificationType;

    @SerializedName(value = "notification_id", alternate = {"notificationId"})
    private String notificationId;

    @SerializedName(value = "survey_subdomain", alternate = {"surveySubdomain"})
    private String surveySubdomain;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getTripId() {
        return tripId;
    }

    public void setTripId(String tripId) {
        this.tripId = tripId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSurveyId() {
        return surveyId;
    }

    public void setSurveyId(String surveyId) {
        this.surveyId = surveyId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(String notificationType) {
        this.notificationType = notificationType;
    }

    public String getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(String notificationId) {
        this.notificationId = notificationId;
    }

    public String getSurveySubdomain() {
        return surveySubdomain;
    }

    public void setSurveySubdomain(String surveySubdomain) {
        this.surveySubdomain = surveySubdomain;
    }
}
