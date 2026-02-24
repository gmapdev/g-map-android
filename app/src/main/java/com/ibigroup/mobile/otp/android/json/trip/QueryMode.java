package com.ibigroup.mobile.otp.android.json.trip;

import java.io.Serializable;

public class QueryMode implements Serializable {
    private String mode;

    private String qualifier;

    public String getQualifier() {
        return qualifier;
    }

    public void setQualifier(String qualifier) {
        this.qualifier = qualifier;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }
}
