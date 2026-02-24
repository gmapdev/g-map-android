package com.ibigroup.mobile.otp.android.json;

import com.ibigroup.mobile.otp.android.json.trip.Stop;

import java.util.ArrayList;

public class StopViewerPattern {
    private String id;
    private String headsign;
    private String desc;
    private String routeId;

    private Route route;

    private ArrayList<Stop> stops;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getRouteId() {
        return routeId;
    }

    public void setRouteId(String routeId) {
        this.routeId = routeId;
    }

    public String getHeadsign() {
        return headsign;
    }

    public void setHeadsign(String headsign) {
        this.headsign = headsign;
    }

    public Route getRoute() {
        return route;
    }

    public void setRoute(Route route) {
        this.route = route;
    }

    public ArrayList<Stop> getStops() {
        return stops;
    }

    public void setStops(ArrayList<Stop> stops) {
        this.stops = stops;
    }

    private RouteViewerRoute routeViewerRoute;

    public RouteViewerRoute getRouteViewerRoute() {
        return routeViewerRoute;
    }

    public void setRouteViewerRoute(RouteViewerRoute routeViewerRoute) {
        this.routeViewerRoute = routeViewerRoute;
    }
}
