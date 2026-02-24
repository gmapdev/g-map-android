package com.ibigroup.mobile.otp.android.json;

import java.util.ArrayList;

public class StopViewerSchedule {
    private StopViewerPattern pattern;
    private ArrayList<StopViewerTime> times;

    public StopViewerPattern getPattern() {
        return pattern;
    }

    public void setPattern(StopViewerPattern pattern) {
        this.pattern = pattern;
    }

    public ArrayList<StopViewerTime> getTimes() {
        return times;
    }

    public void setTimes(ArrayList<StopViewerTime> times) {
        this.times = times;
    }
}
