package com.ibigroup.mobile.otp.android.json;


import com.ibigroup.mobile.otp.android.json.itinerary.ItineraryLegGeometry;

import java.util.ArrayList;

public class RouteViewerPattern {
    private String id;
    private String desc;
    private String headsign;

    private String title;
    private String name;
    //private ItineraryLegGeometry geometry;
    private ItineraryLegGeometry patternGeometry;
    private ArrayList<TransitStop> stops;
    private ArrayList<VehicleOnRoute> vehicleOnRoutes = null;

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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public ItineraryLegGeometry getPatternGeometry() {
        return patternGeometry;
    }

    public void setPatternGeometry(ItineraryLegGeometry patternGeometry) {
        this.patternGeometry = patternGeometry;
    }

    public String getHeadsign() {
        return headsign;
    }

    public void setHeadsign(String headsign) {
        this.headsign = headsign;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<TransitStop> getStops() {
        return stops;
    }

    public void setStops(ArrayList<TransitStop> stops) {
        this.stops = stops;
    }

    public ArrayList<VehicleOnRoute> getVehicleOnRoutes() {
        return vehicleOnRoutes;
    }

    public void setVehicleOnRoutes(ArrayList<VehicleOnRoute> vehicleOnRoutes) {
        this.vehicleOnRoutes = vehicleOnRoutes;
    }
}
