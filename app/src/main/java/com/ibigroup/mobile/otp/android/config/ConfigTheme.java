package com.ibigroup.mobile.otp.android.config;

import java.io.Serializable;

public class ConfigTheme implements Serializable {
    private ThemeColor color;
    private ThemeFont font;

    public ThemeColor getColor() {
        return color;
    }

    public void setColor(ThemeColor color) {
        this.color = color;
    }

    public ThemeFont getFont() {
        return font;
    }

    public void setFont(ThemeFont font) {
        this.font = font;
    }
}
