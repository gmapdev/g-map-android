package com.ibigroup.mobile.otp.android.json;

import java.util.ArrayList;

public class RouteViewerRouteDetail {
    private String id;
    private String shortName;
    private String longName;
    private String url;
    private String color;
    private String textColor;
    private String desc;
    private int type;
    private int sortOrder;
    private RouteViewerRouteAgency agency;

    private ArrayList<RouteViewerPattern> patterns;

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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getTextColor() {
        return textColor;
    }

    public void setTextColor(String textColor) {
        this.textColor = textColor;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(int sortOrder) {
        this.sortOrder = sortOrder;
    }

    public RouteViewerRouteAgency getAgency() {
        return agency;
    }

    public void setAgency(RouteViewerRouteAgency agency) {
        this.agency = agency;
    }

    public ArrayList<RouteViewerPattern> getPatterns() {
        return patterns;
    }

    public void setPatterns(ArrayList<RouteViewerPattern> patterns) {
        this.patterns = patterns;
    }
}
