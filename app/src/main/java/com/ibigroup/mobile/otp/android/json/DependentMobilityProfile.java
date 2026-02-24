package com.ibigroup.mobile.otp.android.json;

import java.io.Serializable;

public class DependentMobilityProfile implements Serializable {
    private String userId;
    private String mobilityMode;
    private String email;
    private String name;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getMobilityMode() {
        return mobilityMode;
    }

    public void setMobilityMode(String mobilityMode) {
        this.mobilityMode = mobilityMode;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
