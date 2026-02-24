package com.ibigroup.mobile.otp.android.json;

public class IndoorMainEntrance {
    private double mainLatitude;
    private double mainLongitude;
    private String popupTitle;
    private String popupMessage;
    private String place;
    private int venueId;
    private Integer radiusInMeter;

    public double getMainLatitude() {
        return mainLatitude;
    }

    public void setMainLatitude(double mainLatitude) {
        this.mainLatitude = mainLatitude;
    }

    public double getMainLongitude() {
        return mainLongitude;
    }

    public void setMainLongitude(double mainLongitude) {
        this.mainLongitude = mainLongitude;
    }

    public String getPopupTitle() {
        return popupTitle;
    }

    public void setPopupTitle(String popupTitle) {
        this.popupTitle = popupTitle;
    }

    public String getPopupMessage() {
        return popupMessage;
    }

    public void setPopupMessage(String popupMessage) {
        this.popupMessage = popupMessage;
    }

    public int getVenueId() {
        return venueId;
    }

    public void setVenueId(int venueId) {
        this.venueId = venueId;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public Integer getRadiusInMeter() {
        return radiusInMeter;
    }

    public void setRadiusInMeter(Integer radiusInMeter) {
        this.radiusInMeter = radiusInMeter;
    }
}
