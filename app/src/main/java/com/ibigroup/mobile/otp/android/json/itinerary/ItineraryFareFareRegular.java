package com.ibigroup.mobile.otp.android.json.itinerary;

import java.io.Serializable;

public class ItineraryFareFareRegular implements Serializable {

    private long cents;

    public long getCents() {
        return cents;
    }

    public void setCents(long cents) {
        this.cents = cents;
    }
}
