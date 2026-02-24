package com.ibigroup.mobile.otp.android.manager;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Looper;

import androidx.core.app.ActivityCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.ibigroup.mobile.otp.android.MyApplication;


public class NewLocationManager {
    private static NewLocationManager instance = null;
    private Context context;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;

    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 1000;

    /**
     * The fastest rate for active location updates. Updates will never be more frequent
     * than this value.
     */
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;
    private static final String TAG = NewLocationManager.class.getSimpleName();

    public String getActionBroadcast() {
        if (MyApplication.getInstance() != null && MyApplication.getInstance().getPackageName() != null) {
            return MyApplication.getInstance().getPackageName() + ".broadcast";
        } else {
            return "com.ibigroup.mobile.ta.android.broadcast";
        }
    }

    public String getExtraLocation() {
        if (MyApplication.getInstance() != null && MyApplication.getInstance().getPackageName() != null) {
            return MyApplication.getInstance().getPackageName() + ".location";
        } else {
            return "com.ibigroup.mobile.ta.android.location";
        }
    }

    private NewLocationManager() {
    }

    public static NewLocationManager getInstance(Context context) {
        if (instance == null) {
            instance = new NewLocationManager();
            instance.init(context);
        }

        return instance;
    }

    private void init(Context context) {
        this.context = context;
        this.fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context);
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                onNewLocation(locationResult.getLastLocation());
            }
        };
        createLocationRequest();
    }

    private void onNewLocation(Location location) {
        // Notify anyone listening for broadcasts about the new location.
        Intent intent = new Intent(getActionBroadcast());
        intent.putExtra(getExtraLocation(), location);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }

    public void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
    }

    protected void createLocationRequest() {
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        locationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }
}
