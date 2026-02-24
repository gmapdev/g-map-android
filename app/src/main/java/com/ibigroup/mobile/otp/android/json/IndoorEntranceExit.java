package com.ibigroup.mobile.otp.android.json;

import java.io.Serializable;

public class IndoorEntranceExit implements Serializable {
    private String type;
    private String name;
    private String message;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
