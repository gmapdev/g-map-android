/**
 * Copyright 2017 Google Inc. All Rights Reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ibigroup.mobile.otp.android.manager;

import static android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_LOCATION;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.location.Location;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.ibigroup.mobile.otp.android.MyApplication;
import com.ibigroup.mobile.otp.android.ScreenSplashActivity;
import com.ibigroup.mobile.otp.android.TabActivity;
import com.ibigroup.mobile.otp.android.fcm.NotificationHelper;
import com.ibigroup.mobile.otp.android.setup.OTPContext;
import com.ibigroup.mobile.otp.android.utility.Logger;


/**
 * A bound and started service that is promoted to a foreground service when location updates have
 * been requested and all clients unbind.
 * <p>
 * For apps running in the background on "O" devices, location is computed only once every 10
 * minutes and delivered batched every 30 minutes. This restriction applies even to apps
 * targeting "N" or lower which are run on "O" devices.
 * <p>
 * This sample show how to use a long-running service for location updates. When an activity is
 * bound to this service, frequent location updates are permitted. When the activity is removed
 * from the foreground, the service promotes itself to a foreground service, and location updates
 * continue. When the activity comes back to the foreground, the foreground service stops, and the
 * notification assocaited with that service is removed.
 */
public class LocationUpdatesService extends Service implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = LocationUpdatesService.class.getSimpleName();

    /**
     * The name of the channel for notifications.
     */
    private static final String CHANNEL_ID = "channel_01";

    private final IBinder mBinder = new LocalBinder();

    public static String getActionBroadcast() {
        if (MyApplication.getInstance() != null && MyApplication.getInstance().getPackageName() != null) {
            return MyApplication.getInstance().getPackageName() + ".broadcast";
        } else {
            return "com.ibigroup.mobile.ta.android.broadcast";
        }
    }

    public static String getExtraLocation() {
        if (MyApplication.getInstance() != null && MyApplication.getInstance().getPackageName() != null) {
            return MyApplication.getInstance().getPackageName() + ".location";
        } else {
            return "com.ibigroup.mobile.ta.android.location";
        }
    }

    /**
     * The desired interval for location updates. Inexact. Updates may be more or less frequent.
     */
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 2000;

    /**
     * The fastest rate for active location updates. Updates will never be more frequent
     * than this value.
     */
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;

    /**
     * The identifier for the notification displayed for the foreground service.
     */
    private static final int NOTIFICATION_ID = 12345678;

    /**
     * Used to check whether the bound activity has really gone away and not unbound as part of an
     * orientation change. We create a foreground service notification only if the former takes
     * place.
     */
    private boolean mChangingConfiguration = false;

    private NotificationManager mNotificationManager;

    /**
     * Contains parameters used by {@link FusedLocationProviderApi}.
     */
    private LocationRequest mLocationRequest;

    /**
     * Provides access to the Fused Location Provider API.
     */
    private FusedLocationProviderApi mFusedLocationClient;

    /**
     * Callback for changes in location.
     */
    private LocationCallback mLocationCallback;

    private Handler mServiceHandler;
    GoogleApiClient mGoogleApiClient;

    private static LocationUpdatesService instance;
    /**
     * The current location.
     */
    private Location mLocation;

    public LocationUpdatesService() {
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            Logger.d("location_service", "cancel notification on onTaskRemoved");
            notificationManager.cancel(2);
            notificationManager.cancel(9096);
        }

        //Logger.e("LocationUpdatesService", "END");
        stopForeground(true);
        stopSelf();
    }

    private void startServiceWithChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Intent intent;

            if (OTPContext.getCurrentActivity() == null) {
                intent = new Intent(this, ScreenSplashActivity.class);
            } else {
                intent = new Intent(this, TabActivity.class);
            }
            PendingIntent pendingIntent = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                pendingIntent = PendingIntent.getActivity(this, 2017, intent, PendingIntent.FLAG_MUTABLE);
            } else {
                pendingIntent = PendingIntent.getActivity(this, 2017, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            }
            Notification.Builder nb = NotificationHelper.getNotification2(
                    this.getApplicationInfo().loadLabel(this.getPackageManager()).toString(),
                    "", pendingIntent);

            if (nb != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    startForeground(2, nb.build(), FOREGROUND_SERVICE_TYPE_LOCATION);
                } else {
                    startForeground(2, nb.build());
                }
            }
        }
    }

    @Override
    public void onCreate() {
        try {
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.O) {
                startForeground(2, new Notification());
            } else {
                startServiceWithChannel();
            }

            mFusedLocationClient = LocationServices.FusedLocationApi;//LocationServices.getFusedLocationProviderClient(this);
            /*
             * Create a new location client, using the enclosing class to
             * handle callbacks.
             */
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();

            mLocationCallback = new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    super.onLocationResult(locationResult);
                    onNewLocation(locationResult.getLastLocation());
                }
            };

            createLocationRequest();
            //getLastLocation();

            HandlerThread handlerThread = new HandlerThread(TAG);
            handlerThread.start();
            mServiceHandler = new Handler(handlerThread.getLooper());
            mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

            instance = this;
            start();
        } catch (Exception e) {
            //startServiceWithChannel();
        }
    }

    private String getStartedFromNotification() {
        if (MyApplication.getInstance() != null && MyApplication.getInstance().getPackageName() != null) {
            return MyApplication.getInstance().getPackageName() + ".started_from_notification";
        } else {
            return "com.ibigroup.mobile.ta.android.started_from_notification";
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Logger.i(TAG, "Service started");
        if (intent != null) {
            boolean startedFromNotification = intent.getBooleanExtra(getStartedFromNotification(),
                    false);

            // We got here because the user decided to remove location updates from the notification.
            if (startedFromNotification) {
                removeLocationUpdates();
                stopSelf();
            }
        }
        // Tells the system to not try to recreate the service after it has been killed.
        return START_NOT_STICKY;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mChangingConfiguration = true;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // Called when a client (MainActivity in case of this sample) comes to the foreground
        // and binds with this service. The service should cease to be a foreground service
        // when that happens.
        Logger.i(TAG, "in onBind()");
        stopForeground(true);
        mChangingConfiguration = false;
        return mBinder;
    }

    @Override
    public void onRebind(Intent intent) {
        // Called when a client (MainActivity in case of this sample) returns to the foreground
        // and binds once again with this service. The service should cease to be a foreground
        // service when that happens.
        Logger.i(TAG, "in onRebind()");
        stopForeground(true);
        mChangingConfiguration = false;
        super.onRebind(intent);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Logger.i(TAG, "Last client unbound from service");

        return true; // Ensures onRebind() is called when a client re-binds.
    }

    @Override
    public void onDestroy() {
        mServiceHandler.removeCallbacksAndMessages(null);
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            Logger.d("location_service", "cancel notification on destroy");
            notificationManager.cancel(2);
            notificationManager.cancel(9096);
        }
        stopForeground(true);
    }

    /**
     * Makes a request for location updates. Note that in this sample we merely log the
     * {@link SecurityException}.
     */
    public void requestLocationUpdates() {
        Logger.i(TAG, "Requesting location updates");
        Utils.setRequestingLocationUpdates(this, true);

        try {
            startService(new Intent(this, LocationUpdatesService.class));
            mFusedLocationClient.requestLocationUpdates(mGoogleApiClient, mLocationRequest,
                    mLocationCallback, Looper.myLooper());
        } catch (SecurityException unlikely) {
            Utils.setRequestingLocationUpdates(this, false);
            Logger.e(TAG, "Lost location permission. Could not request updates. " + unlikely);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Removes location updates. Note that in this sample we merely log the
     * {@link SecurityException}.
     */
    public void removeLocationUpdates() {
        Logger.i(TAG, "Removing location updates");
        try {
            mFusedLocationClient.removeLocationUpdates(mGoogleApiClient, mLocationCallback);
            Utils.setRequestingLocationUpdates(this, false);
            stopSelf();
        } catch (SecurityException unlikely) {
            Utils.setRequestingLocationUpdates(this, true);
            Logger.e(TAG, "Lost location permission. Could not remove updates. " + unlikely);
        }
    }

    private void onNewLocation(Location location) {
        Logger.i(TAG, "New location: " + location);

        mLocation = location;

        // Notify anyone listening for broadcasts about the new location.
        Intent intent = new Intent(getActionBroadcast());
        intent.putExtra(getExtraLocation(), location);
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);

    }

    /**
     * Sets the location request parameters.
     */
    @SuppressLint("RestrictedApi")
    private void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    public void start() {
        if (mGoogleApiClient != null) {
            if (mGoogleApiClient.isConnected() || mGoogleApiClient.isConnecting()) {
                mGoogleApiClient.reconnect();
            } else {
                mGoogleApiClient.connect();
            }
        }
    }

    public void stop() {
        if (mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        requestLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    /**
     * Class used for the client Binder.  Since this service runs in the same process as its
     * clients, we don't need to deal with IPC.
     */
    public class LocalBinder extends Binder {
        public LocationUpdatesService getService() {
            return LocationUpdatesService.this;
        }
    }

    /**
     * Returns true if this is a foreground service.
     *
     * @param context The {@link Context}.
     */
    public boolean serviceIsRunningInForeground(Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(
                Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(
                Integer.MAX_VALUE)) {
            if (getClass().getName().equals(service.service.getClassName())) {
                if (service.foreground) {
                    return true;
                }
            }
        }
        return false;
    }
}
