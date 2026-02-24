package com.ibigroup.mobile.otp.android.json;

import java.io.Serializable;

public class RouteViewerVehicleRequest implements Serializable {
    private String query = "query($routeId: String!) {route(id: $routeId) {shortName longName gtfsId patterns { name vehiclePositions { lat lon lastUpdated speed } } }}";
    private RouteViewerVehicleVariables variables;

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public RouteViewerVehicleVariables getVariables() {
        return variables;
    }

    public void setVariables(RouteViewerVehicleVariables variables) {
        this.variables = variables;
    }
}
