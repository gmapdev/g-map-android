package com.ibigroup.mobile.otp.android.json;

import com.google.gson.annotations.SerializedName;


public class MenuItem {

    @SerializedName(value = "isOn", alternate = {"isVisible"})
    private boolean isOn;
    private int order;
    private String title;
    private String icon;
    @SerializedName(value = "action", alternate = {"type"})
    private String action;
    @SerializedName(value = "value", alternate = {"url"})
    private String value;

    public boolean isOn() {
        return isOn;
    }

    public void setOn(boolean on) {
        isOn = on;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
