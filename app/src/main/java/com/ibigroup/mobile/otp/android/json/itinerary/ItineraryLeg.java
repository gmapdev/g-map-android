package com.ibigroup.mobile.otp.android.json.itinerary;

import com.google.gson.annotations.JsonAdapter;
import com.ibigroup.mobile.otp.android.json.ServiceAlert;
import com.ibigroup.mobile.otp.android.json.trip.Trip;

import java.io.Serializable;
import java.util.ArrayList;

public class ItineraryLeg implements Serializable {
    private long startTime;
    private long endTime;
    private boolean realTime;
    private String mode;

    private long agencyTimeZoneOffset;
    private String routeColor;
    private int routeType;
    private String routeId;
    private String routeTextColor;
    //    private String route;
    @JsonAdapter(ItineraryLegRouteJsonAdapter.class)
    private ItineraryLegRoute route;
    private String routeLongName;//Showing for subway and bus
    private String routeShortName;
    private String agencyName;
    private String agencyUrl;
    private String headsign; //Showing for subway and bus
    private String agencyId;
    private String tripId;
    private String tripBlockId;
    private String serviceDate;
    private boolean interlineWithPreviousLeg;
    private double duration;
    private double distance;
    private ItineraryLegPoint from;
    private ItineraryLegPoint to;
    private ItineraryLegGeometry legGeometry;
    private ItineraryLegAgency agency;
    private boolean transitLeg;
    private boolean pathway;
    private long departureDelay;
    private long arrivalDelay;
    private ItineraryLegTNCData tncData;
    private Double accessibilityScore;

    private ArrayList<ItineraryLegStep> steps;
    private ArrayList<IntermediateStop> intermediateStops;//Showing for bus

    private boolean rentedBike;
    private boolean rentedCar;
    private boolean rentedVehicle;
    private boolean hailedCar;

    private String dropoffType;

    private String pickupType;

    private ArrayList<FareProducts> fareProducts;
    private ArrayList<ServiceAlert> alerts;

    private Trip trip;

    public void setRoute(ItineraryLegRoute route) {
        this.route = route;
    }

    public ArrayList<FareProducts> getFareProducts() {
        return fareProducts;
    }

    public Trip getTrip() {
        return trip;
    }

    public void setTrip(Trip trip) {
        this.trip = trip;
    }

    public String getDropoffType() {
        return dropoffType;
    }

    public void setDropoffType(String dropoffType) {
        this.dropoffType = dropoffType;
    }

    public String getPickupType() {
        return pickupType;
    }

    public void setPickupType(String pickupType) {
        this.pickupType = pickupType;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public boolean isRealTime() {
        return realTime;
    }

    public void setRealTime(boolean realTime) {
        this.realTime = realTime;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public boolean isInterlineWithPreviousLeg() {
        return interlineWithPreviousLeg;
    }

    public void setInterlineWithPreviousLeg(boolean interlineWithPreviousLeg) {
        this.interlineWithPreviousLeg = interlineWithPreviousLeg;
    }

    public double getDuration() {
        return duration;
    }

    public void setDuration(double duration) {
        this.duration = duration;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public ItineraryLegPoint getFrom() {
        return from;
    }

    public void setFrom(ItineraryLegPoint from) {
        this.from = from;
    }

    public ItineraryLegPoint getTo() {
        return to;
    }

    public void setTo(ItineraryLegPoint to) {
        this.to = to;
    }

    public ItineraryLegGeometry getLegGeometry() {
        return legGeometry;
    }

    public void setLegGeometry(ItineraryLegGeometry legGeometry) {
        this.legGeometry = legGeometry;
    }

    public ArrayList<ItineraryLegStep> getSteps() {
        return steps;
    }

    public void setSteps(ArrayList<ItineraryLegStep> steps) {
        this.steps = steps;
    }

    public long getAgencyTimeZoneOffset() {
        return agencyTimeZoneOffset;
    }

    public void setAgencyTimeZoneOffset(long agencyTimeZoneOffset) {
        this.agencyTimeZoneOffset = agencyTimeZoneOffset;
    }

    public String getRouteColor() {
        return routeColor;
    }

    public void setRouteColor(String routeColor) {
        this.routeColor = routeColor;
    }

    public int getRouteType() {
        return routeType;
    }

    public void setRouteType(int routeType) {
        this.routeType = routeType;
    }

    public String getRouteId() {
        if (routeId == null) {
            if (route != null && route.getId() != null) {
                return route.getId();
            }
        }
        return routeId;
    }

    public void setRouteId(String routeId) {
        this.routeId = routeId;
    }

    public String getRouteTextColor() {
        return routeTextColor;
    }

    public void setRouteTextColor(String routeTextColor) {
        this.routeTextColor = routeTextColor;
    }

    public String getRoute() {
        if (route != null) {
            return route.toString();
        } else {
            return null;
        }
    }

    public ItineraryLegRoute getOriginalRoute() {
        return route;
    }

    public String getAgencyName() {
        return agencyName;
    }

    public void setAgencyName(String agencyName) {
        this.agencyName = agencyName;
    }

    public String getAgencyUrl() {
        return agencyUrl;
    }

    public void setAgencyUrl(String agencyUrl) {
        this.agencyUrl = agencyUrl;
    }

    public String getHeadsign() {
        return headsign;
    }

    public void setHeadsign(String headsign) {
        this.headsign = headsign;
    }

    public String getAgencyId() {
        return agencyId;
    }

    public void setAgencyId(String agencyId) {
        this.agencyId = agencyId;
    }

    public String getTripId() {
        return tripId;
    }

    public void setTripId(String tripId) {
        this.tripId = tripId;
    }

    public String getTripBlockId() {
        return tripBlockId;
    }

    public void setTripBlockId(String tripBlockId) {
        this.tripBlockId = tripBlockId;
    }

    public String getServiceDate() {
        return serviceDate;
    }

    public void setServiceDate(String serviceDate) {
        this.serviceDate = serviceDate;
    }

    public String getRouteLongName() {
        if (routeLongName == null) {
            if (route != null && route.getLongName() != null) {
                return route.getLongName();
            }
        }
        return routeLongName;
    }

    public String getLongName() {
        if (route != null && route.getLongName() != null) {
            return route.getLongName();
        }
        return null;
    }

    public void setRouteLongName(String routeLongName) {
        this.routeLongName = routeLongName;
    }

    public ArrayList<IntermediateStop> getIntermediateStops() {
        return intermediateStops;
    }

    public void setIntermediateStops(ArrayList<IntermediateStop> intermediateStops) {
        this.intermediateStops = intermediateStops;
    }

    public boolean isRentedBike() {
        return rentedBike;
    }

    public void setRentedBike(boolean rentedBike) {
        this.rentedBike = rentedBike;
    }

    public boolean isTransitLeg() {
        return transitLeg;
    }

    public void setTransitLeg(boolean transitLeg) {
        this.transitLeg = transitLeg;
    }

    public boolean isPathway() {
        return pathway;
    }

    public void setPathway(boolean pathway) {
        this.pathway = pathway;
    }

    public long getDepartureDelay() {
        return departureDelay;
    }

    public void setDepartureDelay(long departureDelay) {
        this.departureDelay = departureDelay;
    }

    public long getArrivalDelay() {
        return arrivalDelay;
    }

    public void setArrivalDelay(long arrivalDelay) {
        this.arrivalDelay = arrivalDelay;
    }

    public boolean isRentedCar() {
        return rentedCar;
    }

    public void setRentedCar(boolean rentedCar) {
        this.rentedCar = rentedCar;
    }

    public boolean isRentedVehicle() {
        return rentedVehicle;
    }

    public void setRentedVehicle(boolean rentedVehicle) {
        this.rentedVehicle = rentedVehicle;
    }

    public boolean isHailedCar() {
        return hailedCar;
    }

    public void setHailedCar(boolean hailedCar) {
        this.hailedCar = hailedCar;
    }

    public ItineraryLegTNCData getTncData() {
        return tncData;
    }

    public void setTncData(ItineraryLegTNCData tncData) {
        this.tncData = tncData;
    }

    public String getRouteShortName() {
        if (routeShortName == null) {
            if (route != null && route.getShortName() != null) {
                return route.getShortName();
            }
        }
        return routeShortName;
    }

    public void setRouteShortName(String routeShortName) {
        this.routeShortName = routeShortName;
    }

    public ItineraryLegAgency getAgency() {
        return agency;
    }

    public void setAgency(ItineraryLegAgency agency) {
        this.agency = agency;
    }

    public Double getAccessibilityScore() {
        return accessibilityScore;
    }

    public void setAccessibilityScore(Double accessibilityScore) {
        this.accessibilityScore = accessibilityScore;
    }

    public ArrayList<ServiceAlert> getAlerts() {
        return alerts;
    }

    public void setAlerts(ArrayList<ServiceAlert> alerts) {
        this.alerts = alerts;
    }
}
