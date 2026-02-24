package com.ibigroup.mobile.otp.android.config;

public class LayerFeature {

    private String layerName;
    private LayerFeatureDetail detail;

    public LayerFeatureDetail getDetail() {
        return detail;
    }

    public void setDetail(LayerFeatureDetail detail) {
        this.detail = detail;
    }

    public String getLayerName() {
        return layerName;
    }

    public void setLayerName(String layerName) {
        this.layerName = layerName;
    }
}
