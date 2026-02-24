package com.ibigroup.mobile.otp.android.json.itinerary;

import android.text.SpannableString;

import java.io.Serializable;

public class MatchedSchedules implements Serializable {

    private SpannableString schedule;


    public MatchedSchedules(SpannableString schedule) {

        this.schedule = schedule;

    }


    public SpannableString getSchedule() {
        return schedule;
    }

    public void setSchedule(SpannableString schedule) {
        this.schedule = schedule;
    }


}
