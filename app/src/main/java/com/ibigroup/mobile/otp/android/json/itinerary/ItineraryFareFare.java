package com.ibigroup.mobile.otp.android.json.itinerary;

import java.io.Serializable;

public class ItineraryFareFare implements Serializable {
    private ItineraryFareFareRegular regular;

    public ItineraryFareFareRegular getRegular() {
        return regular;
    }

    public void setRegular(ItineraryFareFareRegular regular) {
        this.regular = regular;
    }
}
