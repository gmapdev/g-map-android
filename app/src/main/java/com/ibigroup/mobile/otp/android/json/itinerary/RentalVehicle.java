package com.ibigroup.mobile.otp.android.json.itinerary;

import java.io.Serializable;

public class RentalVehicle implements Serializable {
    private String id;
    private String network;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNetwork() {
        return network;
    }

    public void setNetwork(String network) {
        this.network = network;
    }
}
