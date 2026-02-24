package com.ibigroup.mobile.otp.android.manager;

import static com.ibigroup.mobile.otp.android.utility.ServerDelegate.volleyStringRequest;

import android.os.Handler;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ibigroup.mobile.otp.android.MyApplication;
import com.ibigroup.mobile.otp.android.api.Resource;
import com.ibigroup.mobile.otp.android.json.ParkRideLocation;
import com.ibigroup.mobile.otp.android.utility.Configurations;
import com.ibigroup.mobile.otp.android.utility.Constants;
import com.ibigroup.mobile.otp.android.utility.Logger;

import java.util.ArrayList;


public class ParkRideManager {

    private static ParkRideManager instance;
    private final ArrayList<ParkRideListener> parkRideListeners;

    private final Handler handler;
    private final Runnable runnable;

    public ParkRideManager() {
        handler = new Handler();

        runnable = new Runnable() {
            @Override
            public void run() {
                getParkRides();

                handler.postDelayed(this, Configurations.getInt("park_ride_location_refresh_rate", 300) * 1000L);
            }
        };

        parkRideListeners = new ArrayList<ParkRideListener>();
    }

    public static ParkRideManager getInstance() {
        if (instance == null) {
            synchronized (ParkRideManager.class) {
                // May be another thread has created an instance, check again within the synched code
                if (instance == null) {
                    instance = new ParkRideManager();
                }
            }
        }
        return instance;
    }

    public void getParkRides() {
        Response.Listener getParkRideListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response != null && !response.isEmpty() && response.startsWith("[")) {
                    Gson gson = new Gson();

                    MyApplication.getInstance().parkRideLocations = gson.fromJson(response, new TypeToken<ArrayList<ParkRideLocation>>() {
                    }.getType());

                    fireParkRideChanged();
                } else {
                    Log.d("parkRide", "data is not correct:" + response);
                }

            }
        };
        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Logger.e("get park ride feed error:", error.toString());
            }
        };

        try {
            getParkRideResponse(getParkRideListener, errorListener);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void getParkRideResponse(Response.Listener listener,
                                    Response.ErrorListener errListener) throws Exception {
        String url = Resource.API_PARK_AND_RIDE;
        String params = "?maxTransitDistance=" + Constants.MAX_TRANSIT_DISTANCE;

        volleyStringRequest(Request.Method.GET, url + params, "", listener, errListener);
    }

    public void start() {
        if (handler != null) {
            handler.removeCallbacks(runnable);
            handler.postDelayed(runnable, 1000);
        }
    }

    public void stop() {
        if (handler != null) {
            //cancel handler
            handler.removeCallbacks(runnable);
        }
    }

    public interface ParkRideListener {
        void parkRideChanged();
    }

    public void addListener(ParkRideManager.ParkRideListener listener) {
        if (!parkRideListeners.contains(listener)) {
            parkRideListeners.add(listener);
        }
    }

    public void removeListener(ParkRideManager.ParkRideListener listener) {
        parkRideListeners.remove(listener);
    }

    private void fireParkRideChanged() {
        for (ParkRideManager.ParkRideListener listener : parkRideListeners) {
            listener.parkRideChanged();
        }
    }

}
