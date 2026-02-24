package com.ibigroup.mobile.otp.android.config;

import com.google.gson.annotations.SerializedName;

public class DetailLayerParameters {
    @SerializedName("tile_server")
    private String tileServer;

    @SerializedName("legend_html_detail")
    private String legendHtmlDetail;

    @SerializedName("marker_tts_field")
    private String markerttsField;

    @SerializedName("marker_ivr_field")
    private String markerivrField;

    @SerializedName("marker_popover_title")
    private String markerPopoverTitle;

    @SerializedName("marker_refresh_interval_secs")
    private Integer markerRefreshIntervalSecs;

    public String getTileServer() {
        return tileServer;
    }

    public void setTileServer(String tileServer) {
        this.tileServer = tileServer;
    }

    public String getLegendHtmlDetail() {
        return legendHtmlDetail;
    }

    public void setLegendHtmlDetail(String legendHtmlDetail) {
        this.legendHtmlDetail = legendHtmlDetail;
    }

    public String getMarkerttsField() {
        return markerttsField;
    }

    public void setMarkerttsField(String markerttsField) {
        this.markerttsField = markerttsField;
    }

    public String getMarkerivrField() {
        return markerivrField;
    }

    public void setMarkerivrField(String markerivrField) {
        this.markerivrField = markerivrField;
    }

    public String getMarkerPopoverTitle() {
        return markerPopoverTitle;
    }

    public void setMarkerPopoverTitle(String markerPopoverTitle) {
        this.markerPopoverTitle = markerPopoverTitle;
    }

    public Integer getMarkerRefreshIntervalSecs() {
        return markerRefreshIntervalSecs;
    }

    public void setMarkerRefreshIntervalSecs(Integer markerRefreshIntervalSecs) {
        this.markerRefreshIntervalSecs = markerRefreshIntervalSecs;
    }
}
