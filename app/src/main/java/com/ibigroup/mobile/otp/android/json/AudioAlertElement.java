package com.ibigroup.mobile.otp.android.json;

public class AudioAlertElement {
    private String alertID;
    private String description;
    private String ttsFileName;
    private String audioUrl;

    //in secs
    private long playedTimestamp = 0;
    private int clustergroup = 0;

    public String getAlertID() {
        return alertID;
    }

    public void setAlertID(String alertID) {
        this.alertID = alertID;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTtsFileName() {
        return ttsFileName;
    }

    public void setTtsFileName(String ttsFileName) {
        this.ttsFileName = ttsFileName;
    }

    public int getClustergroup() {
        return clustergroup;
    }

    public void setClustergroup(int clustergroup) {
        this.clustergroup = clustergroup;
    }

    public String getAudioUrl() {
        return audioUrl;
    }

    public void setAudioUrl(String audioUrl) {
        this.audioUrl = audioUrl;
    }

    public long getPlayedTimestamp() {
        return playedTimestamp;
    }

    public void setPlayedTimestamp(long playedTimestamp) {
        this.playedTimestamp = playedTimestamp;
    }
}
