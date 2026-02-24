package com.ibigroup.mobile.otp.android.json;

import java.util.ArrayList;

public class MobilityProfile {
    private Boolean isMobilityLimited;
    private ArrayList<String> mobilityDevices;
    private String mobilityMode;
    private String visionLimitation;

    public Boolean getMobilityLimited() {
        return isMobilityLimited;
    }

    public void setMobilityLimited(Boolean mobilityLimited) {
        isMobilityLimited = mobilityLimited;
    }

    public ArrayList<String> getMobilityDevices() {
        if (mobilityDevices == null) {
            mobilityDevices = new ArrayList<>();
        }
        return mobilityDevices;
    }

    public void setMobilityDevices(ArrayList<String> mobilityDevices) {
        this.mobilityDevices = mobilityDevices;
    }

    public String getMobilityMode() {
        return mobilityMode;
    }

    public void setMobilityMode(String mobilityMode) {
        this.mobilityMode = mobilityMode;
    }

    public String getVisionLimitation() {
        return visionLimitation;
    }

    public void setVisionLimitation(String visionLimitation) {
        this.visionLimitation = visionLimitation;
    }
}
