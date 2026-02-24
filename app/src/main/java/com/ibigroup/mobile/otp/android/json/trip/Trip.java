package com.ibigroup.mobile.otp.android.json.trip;

import java.io.Serializable;

public class Trip implements Serializable {

    private String gtfsId;

    public String getGtfsId() {
        return gtfsId;
    }

    public void setGtfsId(String gtfsId) {
        this.gtfsId = gtfsId;
    }
}
