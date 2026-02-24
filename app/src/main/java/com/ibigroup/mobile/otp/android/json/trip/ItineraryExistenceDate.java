package com.ibigroup.mobile.otp.android.json.trip;

import java.io.Serializable;
import java.util.ArrayList;

public class ItineraryExistenceDate implements Serializable {
    private ArrayList<String> invalidDates;
    private ArrayList<String> validDates;
    private boolean valid;

    public ArrayList<String> getInvalidDates() {
        return invalidDates;
    }

    public void setInvalidDates(ArrayList<String> invalidDates) {
        this.invalidDates = invalidDates;
    }

    public ArrayList<String> getValidDates() {
        return validDates;
    }

    public void setValidDates(ArrayList<String> validDates) {
        this.validDates = validDates;
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }
}
