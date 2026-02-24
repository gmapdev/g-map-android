package com.ibigroup.mobile.otp.android.config;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;


public class ThemeFont implements Serializable {
    @SerializedName("primary_font_family")
    private String primaryFontFamily;

    @SerializedName("secondary_font_family")
    private String secondaryFontFamily;

    @SerializedName("body_font_size")
    private int bodyFontSize;

    @SerializedName("footnote_font_size")
    private int footnoteFontSize;

    @SerializedName("title_font_size")
    private int titleFontSize;

    public String getPrimaryFontFamily() {
        return primaryFontFamily;
    }

    public void setPrimaryFontFamily(String primaryFontFamily) {
        this.primaryFontFamily = primaryFontFamily;
    }

    public String getSecondaryFontFamily() {
        return secondaryFontFamily;
    }

    public void setSecondaryFontFamily(String secondaryFontFamily) {
        this.secondaryFontFamily = secondaryFontFamily;
    }

    public int getBodyFontSize() {
        return bodyFontSize;
    }

    public void setBodyFontSize(int bodyFontSize) {
        this.bodyFontSize = bodyFontSize;
    }

    public int getFootnoteFontSize() {
        return footnoteFontSize;
    }

    public void setFootnoteFontSize(int footnoteFontSize) {
        this.footnoteFontSize = footnoteFontSize;
    }

    public int getTitleFontSize() {
        return titleFontSize;
    }

    public void setTitleFontSize(int titleFontSize) {
        this.titleFontSize = titleFontSize;
    }
}
