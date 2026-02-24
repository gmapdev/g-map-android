package com.ibigroup.mobile.otp.android.config;

import com.google.gson.annotations.SerializedName;

public class DetailLayer {
    @SerializedName("layer_on")
    private boolean layerOn;

    @SerializedName("associated_layer_ids")
    private String associatedLayerIds;

    private Boolean visibility;

    @SerializedName("layer_id")
    private String layerId;

    @SerializedName("layer_icon")
    private String layerIcon;

    @SerializedName("layer_order")
    private int layerOrder;

    @SerializedName("layer_title")
    private String layerTitle;

    @SerializedName("layer_parameters")
    private DetailLayerParameters layerParameters;

    @SerializedName("layer_z_index")
    private Integer layerZIndex;

    public boolean isLayerOn() {
        return layerOn;
    }

    public void setLayerOn(boolean layerOn) {
        this.layerOn = layerOn;
    }

    public DetailLayerParameters getLayerParameters() {
        return layerParameters;
    }

    public void setLayerParameters(DetailLayerParameters layerParameters) {
        this.layerParameters = layerParameters;
    }

    public int getLayerOrder() {
        return layerOrder;
    }

    public void setLayerOrder(int layerOrder) {
        this.layerOrder = layerOrder;
    }

    public String getLayerTitle() {
        return layerTitle;
    }

    public void setLayerTitle(String layerTitle) {
        this.layerTitle = layerTitle;
    }

    public String getLayerId() {
        return layerId;
    }

    public void setLayerId(String layerId) {
        this.layerId = layerId;
    }

    public String getLayerIcon() {
        return layerIcon;
    }

    public void setLayerIcon(String layerIcon) {
        this.layerIcon = layerIcon;
    }

    public String getAssociatedLayerIds() {
        return associatedLayerIds;
    }

    public void setAssociatedLayerIds(String associatedLayerIds) {
        this.associatedLayerIds = associatedLayerIds;
    }

    public Boolean getVisibility() {
        return visibility;
    }

    public void setVisibility(Boolean visibility) {
        this.visibility = visibility;
    }

    public Integer getLayerZIndex() {
        return layerZIndex;
    }

    public void setLayerZIndex(Integer layerZIndex) {
        this.layerZIndex = layerZIndex;
    }
}
