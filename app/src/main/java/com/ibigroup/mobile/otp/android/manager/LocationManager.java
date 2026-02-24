package com.ibigroup.mobile.otp.android.manager;

import android.Manifest;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.ibigroup.mobile.otp.android.cache.CurrentLocation;
import com.ibigroup.mobile.otp.android.utility.Logger;

import java.util.ArrayList;

public class LocationManager implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        LocationListener {
    private static LocationManager instance;
    private final ArrayList<LocationListener> locationListeners;

    // Global constants
    // Milliseconds per second
    private final int MILLISECONDS_PER_SECOND = 1000;
    // Update frequency in seconds
    private final int UPDATE_INTERVAL_IN_SECONDS = 5;
    // Update frequency in milliseconds
    private final long UPDATE_INTERVAL =
            MILLISECONDS_PER_SECOND * UPDATE_INTERVAL_IN_SECONDS;
    // The fastest update frequency, in seconds
    private final int FASTEST_INTERVAL_IN_SECONDS = 1;
    // A fast frequency ceiling in milliseconds
    private final long FASTEST_INTERVAL =
            MILLISECONDS_PER_SECOND * FASTEST_INTERVAL_IN_SECONDS;

    // Define an object that holds accuracy and frequency parameters
    LocationRequest mLocationRequest;

    // Global variables
    GoogleApiClient mGoogleApiClient;
    boolean mUpdatesRequested;
    private final FragmentActivity activity;

    // this is a number calculated by (_totalPointCount - 1) mod 30, and it's used as a counter to trigger upload point event


    // A collection of received GPS locations which is in WgsPoint format

    private Location previousLocation;

    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

    public LocationManager(FragmentActivity activity) {
        this.activity = activity;

        instance = this;

        locationListeners = new ArrayList<LocationListener>();

        // Create the LocationRequest object
        mLocationRequest = LocationRequest.create();
        // Use high accuracy
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        // Set the update interval to 5 seconds
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        // Set the fastest update interval to 1 second
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);

        /*
         * Create a new location client, using the enclosing class to
         * handle callbacks.
         */
        mGoogleApiClient = new GoogleApiClient.Builder(activity)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        // Start with updates turned off
        mUpdatesRequested = true;
    }

    public static LocationManager getInstance(FragmentActivity activity) {
        if (null == instance) {
            instance = new LocationManager(activity);
        }
        return instance;
    }

    public static LocationManager getInstance() {
        return instance;
    }

    public void start() {
        if (mGoogleApiClient.isConnected() || mGoogleApiClient.isConnecting()) {
            mGoogleApiClient.reconnect();
        } else {
            mGoogleApiClient.connect();
        }
    }

    public void setLocationRequestPriority(int priority) {
        if (priority == LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY) {
            Logger.d("priority", "PRIORITY_BALANCED_POWER_ACCURACY");
        } else if (priority == LocationRequest.PRIORITY_HIGH_ACCURACY) {
            Logger.d("priority", "PRIORITY_HIGH_ACCURACY");
        }
        mLocationRequest.setPriority(priority);

        mUpdatesRequested = true;

        if (ContextCompat.checkSelfPermission(activity,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            start();
        }
    }

    public void stop() {
        mGoogleApiClient.disconnect();
    }

    /*
     * Called by Location Services if the attempt to
     * Location Services fails.
     */
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // Display the connection status
        /*
         * Google Play services can resolve some errors it detects.
         * If the error has a resolution, try sending an Intent to
         * start a Google Play services activity that can resolve
         * error.
         */
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(
                        activity,
                        CONNECTION_FAILURE_RESOLUTION_REQUEST);
                /*
                 * Thrown if Google Play services canceled the original
                 * PendingIntent
                 */
            } catch (IntentSender.SendIntentException e) {
                // Log the error
                e.printStackTrace();
            }
        }
    }


    /*
     * Called by Location Services when the request to connect the
     * client finishes successfully. At this point, you can
     * request the current location or start periodic updates
     */
    @Override
    public void onConnected(Bundle dataBundle) {
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        // Display the connection status
        CurrentLocation.getInstance().setCurrentLocation(LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient));

        // If already requested, start periodic updates
        if (mUpdatesRequested) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {
        //not moving
        if (location.getBearing() == 0) {
            if (previousLocation != null) {
                //use previous bearing if available
                location.setBearing(previousLocation.getBearing());
            }
        } else if (previousLocation != null && location.getBearing() == 90) {
            //used for gps simulator
            location.setBearing(previousLocation.bearingTo(location));
        }

        previousLocation = location;

        CurrentLocation.getInstance().setCurrentLocation(location);

        fireLocationChanged(location);
    }

    public Location getLastKnownLocation() {
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return null;
        }
        return LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
    }

    /// //// L O C A T I O N   L I S T E N E R ///////
    public interface LocationListener {
        void locationChanged(Location location);
    }

    public void addListener(LocationListener listener) {
        if (!locationListeners.contains(listener)) {
            locationListeners.add(listener);
        }
    }

    public void removeListener(LocationListener listener) {
        locationListeners.remove(listener);
    }

    private void fireLocationChanged(Location location) {
        for (LocationListener listener : locationListeners) {
            listener.locationChanged(location);
        }
    }
}