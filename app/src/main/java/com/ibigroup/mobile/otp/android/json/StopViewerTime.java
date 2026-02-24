package com.ibigroup.mobile.otp.android.json;

import com.google.gson.annotations.SerializedName;
import com.ibigroup.mobile.otp.android.json.trip.Stop;

public class StopViewerTime {
    private String stopId;
    @SerializedName(value = "realtimeState", alternate = {"realTimeState"})
    private String realtimeState;
    @SerializedName(value = "realtimeArrival", alternate = {"realTimeArrival"})
    private long realtimeArrival;
    private long scheduledArrival;
    private long serviceDay;
    @SerializedName(value = "realtime", alternate = {"realTime"})
    private boolean realtime;
    private String headsign;
    //show for Stop viewer schedule
    private String route;
    private String to;
    private RouteAgency routeAgency;

    private Stop stop;
    private long realtimeDeparture;
    private long scheduledDeparture;

    public String getRealtimeState() {
        return realtimeState;
    }

    public void setRealtimeState(String realtimeState) {
        this.realtimeState = realtimeState;
    }

    public long getRealtimeArrival() {
        return realtimeArrival;
    }

    public void setRealtimeArrival(long realtimeArrival) {
        this.realtimeArrival = realtimeArrival;
    }

    public long getScheduledArrival() {
        return scheduledArrival;
    }

    public void setScheduledArrival(long scheduledArrival) {
        this.scheduledArrival = scheduledArrival;
    }

    public long getServiceDay() {
        return serviceDay;
    }

    public void setServiceDay(long serviceDay) {
        this.serviceDay = serviceDay;
    }

    public boolean isRealtime() {
        return realtime;
    }

    public void setRealtime(boolean realtime) {
        this.realtime = realtime;
    }

    public String getHeadsign() {
        return headsign;
    }

    public void setHeadsign(String headsign) {
        this.headsign = headsign;
    }

    public String getRoute() {
        return route;
    }

    public void setRoute(String route) {
        this.route = route;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getStopId() {
        return stopId;
    }

    public void setStopId(String stopId) {
        this.stopId = stopId;
    }

    public RouteAgency getRouteAgency() {
        return routeAgency;
    }

    public void setRouteAgency(RouteAgency routeAgency) {
        this.routeAgency = routeAgency;
    }

    public Stop getStop() {
        return stop;
    }

    public void setStop(Stop stop) {
        this.stop = stop;
    }

    public long getRealtimeDeparture() {
        return realtimeDeparture;
    }

    public void setRealtimeDeparture(long realtimeDeparture) {
        this.realtimeDeparture = realtimeDeparture;
    }

    public long getScheduledDeparture() {
        return scheduledDeparture;
    }

    public void setScheduledDeparture(long scheduledDeparture) {
        this.scheduledDeparture = scheduledDeparture;
    }
}
