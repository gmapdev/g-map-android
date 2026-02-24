package com.ibigroup.mobile.otp.android.config;

import com.google.gson.annotations.SerializedName;

public class LayerFeatureDetail {
    private DetailLayer layer;

    @SerializedName("is_enabled")
    private boolean isEnabled;

    public DetailLayer getLayer() {
        return layer;
    }

    public void setLayer(DetailLayer layer) {
        this.layer = layer;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }
}
