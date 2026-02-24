package com.ibigroup.mobile.otp.android.json.itinerary;

import java.io.Serializable;

public class ItineraryLegAgency implements Serializable {
    private String id;
    private String name;
    private String timezone;
    private String url;

    private String gtfsId;

    public void setGtfsId(String gtfsId) {
        this.gtfsId = gtfsId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getGtfsId() {
        return gtfsId;
    }
}
