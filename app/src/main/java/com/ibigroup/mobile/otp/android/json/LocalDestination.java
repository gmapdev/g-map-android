package com.ibigroup.mobile.otp.android.json;

import com.jibestream.jmapandroidsdk.components.Waypoint;

public class LocalDestination {
    private String name;
    private Waypoint[] waypoints;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Waypoint[] getWaypoints() {
        return waypoints;
    }

    public void setWaypoints(Waypoint[] waypoints) {
        this.waypoints = waypoints;
    }
}
