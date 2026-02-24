package com.ibigroup.mobile.otp.android.cache;

import android.location.Location;
import android.location.LocationManager;

import com.ibigroup.mobile.otp.android.utility.Configurations;

public class CurrentLocation {
    private Location currentLocation;
    private Location originLocation;
    private boolean isDriving;

    private static CurrentLocation instance;

    private CurrentLocation() {
        isDriving = false;
    }

    public static CurrentLocation getInstance() {
        if (instance == null) {
            instance = new CurrentLocation();
        }
        return instance;
    }

    public boolean isCurrentLocationAccurate() {
        if (currentLocation != null) {
            return currentLocation.getAccuracy() <= Configurations.getInt("location_accuracy_radius", 30) &&
                    currentLocation.getAccuracy() != 0;
        }
        return false;
    }

    public Location getCurrentLocation() {
        if (currentLocation == null) {
            Location lastLocation = new Location(LocationManager.GPS_PROVIDER);
            lastLocation.setLatitude(Configurations.getFloat("current_lat", Configurations.getFloat("default_lat", 0)));
            lastLocation.setLongitude(Configurations.getFloat("current_lon", Configurations.getFloat("default_lon", 0)));
            currentLocation = lastLocation;
        }
        return currentLocation;
    }

    public void setCurrentLocation(Location currentLocation) {
        if (currentLocation != null) {
            Configurations.setFloat("current_lat", (float) currentLocation.getLatitude());
            Configurations.setFloat("current_lon", (float) currentLocation.getLongitude());

            if (originLocation == null) {
                originLocation = currentLocation;
            }

            //if speed is greater than 10 km/h or has driven 2km from the origin location the user is driving :)
            if (!isDriving && (currentLocation.getSpeed() >= 2.77778 || currentLocation.distanceTo(originLocation) >= 2000)) {
                isDriving = true;
            }

            this.currentLocation = currentLocation;
        }
    }

    public boolean isDriving() {
        return isDriving;
    }
}