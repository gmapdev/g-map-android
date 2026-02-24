package com.ibigroup.mobile.otp.android.config;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;


public class ThemeColor implements Serializable {
    @SerializedName("primary_color")
    private String primaryColor;

    @SerializedName("secondary_color")
    private String secondaryColor;

    @SerializedName("tertiary_color")
    private String tertiaryColor;

    @SerializedName("foreground_color")
    private String foregroundColor;

    @SerializedName("primary_background_color")
    private String primaryBackgroundColor;

    @SerializedName("secondary_background_color")
    private String secondaryBackgroundColor;

    @SerializedName("mask_background_color")
    private String maskBackgroundColor;

    @SerializedName("menu_logo_color")
    private String menuLogoColor;

    @SerializedName("cameradetail_title_brandinfo_color")
    private String cameraDetailTitleBrandInfoColor;

    @SerializedName("toggle_on_color")
    private String toggleOnColor;

    @SerializedName("toggle_off_color")
    private String toggleOffColor;

    @SerializedName("map_logo_foreground_color")
    private String mapLogoForegroundColor; //don't tint if it is “#000001”

    @SerializedName("moving_progress_color")
    private String movingProgressColor;

    @SerializedName("map_overlap_stroke_color")
    private String mapOverlapStrokeColor;

    @SerializedName("pushchannel_text_color")
    private String pushChannelTextColor;

    @SerializedName("pushchannel_item_text_color")
    private String pushChannelItemTextColor;

    @SerializedName("map_marker_cluster_foreground_color")
    private String mapMarkerClusterForegroundColor;

    @SerializedName("map_marker_cluster_text_color")
    private String mapMarkerClusterTextColor;

    @SerializedName("map_marker_cluster_background_color")
    private String mapMarkerClusterBackgroundColor;

    @SerializedName("layerpanel_background_color")
    private String layerPanelBackgroundColor;

    @SerializedName("drivemode_button_voicemail_foreground_color")
    private String driveModeButtonVoicemailForegroundColor;

    @SerializedName("drivemode_button_phonenumber_foreground_color")
    private String driveModeButtonPhonenumberForegroundColor;

    public String getPrimaryColor() {
        return primaryColor;
    }

    public void setPrimaryColor(String primaryColor) {
        this.primaryColor = primaryColor;
    }

    public String getSecondaryColor() {
        return secondaryColor;
    }

    public void setSecondaryColor(String secondaryColor) {
        this.secondaryColor = secondaryColor;
    }

    public String getTertiaryColor() {
        return tertiaryColor;
    }

    public void setTertiaryColor(String tertiaryColor) {
        this.tertiaryColor = tertiaryColor;
    }

    public String getForegroundColor() {
        return foregroundColor;
    }

    public void setForegroundColor(String foregroundColor) {
        this.foregroundColor = foregroundColor;
    }

    public String getPrimaryBackgroundColor() {
        return primaryBackgroundColor;
    }

    public void setPrimaryBackgroundColor(String primaryBackgroundColor) {
        this.primaryBackgroundColor = primaryBackgroundColor;
    }

    public String getSecondaryBackgroundColor() {
        return secondaryBackgroundColor;
    }

    public void setSecondaryBackgroundColor(String secondaryBackgroundColor) {
        this.secondaryBackgroundColor = secondaryBackgroundColor;
    }

    public String getMaskBackgroundColor() {
        return maskBackgroundColor;
    }

    public void setMaskBackgroundColor(String maskBackgroundColor) {
        this.maskBackgroundColor = maskBackgroundColor;
    }

    public String getMenuLogoColor() {
        return menuLogoColor;
    }

    public void setMenuLogoColor(String menuLogoColor) {
        this.menuLogoColor = menuLogoColor;
    }

    public String getCameraDetailTitleBrandInfoColor() {
        return cameraDetailTitleBrandInfoColor;
    }

    public void setCameraDetailTitleBrandInfoColor(String cameraDetailTitleBrandInfoColor) {
        this.cameraDetailTitleBrandInfoColor = cameraDetailTitleBrandInfoColor;
    }

    public String getToggleOnColor() {
        return toggleOnColor;
    }

    public void setToggleOnColor(String toggleOnColor) {
        this.toggleOnColor = toggleOnColor;
    }

    public String getToggleOffColor() {
        return toggleOffColor;
    }

    public void setToggleOffColor(String toggleOffColor) {
        this.toggleOffColor = toggleOffColor;
    }

    public String getMapLogoForegroundColor() {
        return mapLogoForegroundColor;
    }

    public void setMapLogoForegroundColor(String mapLogoForegroundColor) {
        this.mapLogoForegroundColor = mapLogoForegroundColor;
    }

    public String getMovingProgressColor() {
        return movingProgressColor;
    }

    public void setMovingProgressColor(String movingProgressColor) {
        this.movingProgressColor = movingProgressColor;
    }

    public String getMapOverlapStrokeColor() {
        return mapOverlapStrokeColor;
    }

    public void setMapOverlapStrokeColor(String mapOverlapStrokeColor) {
        this.mapOverlapStrokeColor = mapOverlapStrokeColor;
    }

    public String getPushChannelTextColor() {
        return pushChannelTextColor;
    }

    public void setPushChannelTextColor(String pushChannelTextColor) {
        this.pushChannelTextColor = pushChannelTextColor;
    }

    public String getPushChannelItemTextColor() {
        return pushChannelItemTextColor;
    }

    public void setPushChannelItemTextColor(String pushChannelItemTextColor) {
        this.pushChannelItemTextColor = pushChannelItemTextColor;
    }

    public String getMapMarkerClusterForegroundColor() {
        return mapMarkerClusterForegroundColor;
    }

    public void setMapMarkerClusterForegroundColor(String mapMarkerClusterForegroundColor) {
        this.mapMarkerClusterForegroundColor = mapMarkerClusterForegroundColor;
    }

    public String getMapMarkerClusterTextColor() {
        return mapMarkerClusterTextColor;
    }

    public void setMapMarkerClusterTextColor(String mapMarkerClusterTextColor) {
        this.mapMarkerClusterTextColor = mapMarkerClusterTextColor;
    }

    public String getMapMarkerClusterBackgroundColor() {
        return mapMarkerClusterBackgroundColor;
    }

    public void setMapMarkerClusterBackgroundColor(String mapMarkerClusterBackgroundColor) {
        this.mapMarkerClusterBackgroundColor = mapMarkerClusterBackgroundColor;
    }

    public String getLayerPanelBackgroundColor() {
        return layerPanelBackgroundColor;
    }

    public void setLayerPanelBackgroundColor(String layerPanelBackgroundColor) {
        this.layerPanelBackgroundColor = layerPanelBackgroundColor;
    }

    public String getDriveModeButtonVoicemailForegroundColor() {
        return driveModeButtonVoicemailForegroundColor;
    }

    public void setDriveModeButtonVoicemailForegroundColor(String driveModeButtonVoicemailForegroundColor) {
        this.driveModeButtonVoicemailForegroundColor = driveModeButtonVoicemailForegroundColor;
    }

    public String getDriveModeButtonPhonenumberForegroundColor() {
        return driveModeButtonPhonenumberForegroundColor;
    }

    public void setDriveModeButtonPhonenumberForegroundColor(String driveModeButtonPhonenumberForegroundColor) {
        this.driveModeButtonPhonenumberForegroundColor = driveModeButtonPhonenumberForegroundColor;
    }
}
