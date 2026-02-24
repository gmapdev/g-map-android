package com.ibigroup.mobile.otp.android.json;

public class TransitStop {
    private String id;
    private String code;
    private String name;
    private double lat;
    private double lon;
    private Integer routeColor;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public Integer getRouteColor() {
        return routeColor;
    }

    public void setRouteColor(Integer routeColor) {
        this.routeColor = routeColor;
    }
}
