package com.ibigroup.mobile.otp.android.json;

import com.ibigroup.mobile.otp.android.json.itinerary.ItineraryLegAgency;

import java.io.Serializable;

public class RouteViewerRoute implements Serializable {
    private String id;
    private String shortName;
    private String longName;
    private String mode;
    private String color;
    private String agencyId;
    private String agencyName;
    private String routeUrl;
    private ItineraryLegAgency agency;
    private int sortOrder;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getLongName() {
        return longName;
    }

    public void setLongName(String longName) {
        this.longName = longName;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getAgencyId() {
        return agencyId;
    }

    public void setAgencyId(String agencyId) {
        this.agencyId = agencyId;
    }

    public String getAgencyName() {
        if (agencyName == null) {
            if (agency != null && agency.getName() != null) {
                return agency.getName();
            }
        }
        return agencyName;
    }

    public void setAgencyName(String agencyName) {
        this.agencyName = agencyName;
    }

    public int getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(int sortOrder) {
        this.sortOrder = sortOrder;
    }

    public String getRouteUrl() {
        return routeUrl;
    }

    public void setRouteUrl(String routeUrl) {
        this.routeUrl = routeUrl;
    }

    public ItineraryLegAgency getAgency() {
        return agency;
    }

    public void setAgency(ItineraryLegAgency agency) {
        this.agency = agency;
    }
}
