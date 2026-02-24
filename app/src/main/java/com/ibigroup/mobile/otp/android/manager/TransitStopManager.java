package com.ibigroup.mobile.otp.android.manager;

import static com.ibigroup.mobile.otp.android.utility.ServerDelegate.volleyStringRequest;

import android.os.Handler;
import android.util.Log;

import androidx.annotation.NonNull;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.ApolloClient;
import com.apollographql.apollo.exception.ApolloException;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ibigroup.mobile.otp.android.MapStopsQuery;
import com.ibigroup.mobile.otp.android.MyApplication;
import com.ibigroup.mobile.otp.android.api.Resource;
import com.ibigroup.mobile.otp.android.json.TransitStop;
import com.ibigroup.mobile.otp.android.utility.Configurations;
import com.ibigroup.mobile.otp.android.utility.Logger;

import java.util.ArrayList;
import java.util.List;


public class TransitStopManager {

    private static TransitStopManager instance;
    private final ArrayList<TransitStopListener> transitStopListeners;

    private final Handler handler;
    private final Runnable runnable;

    public TransitStopManager() {
        handler = new Handler();

        runnable = new Runnable() {
            @Override
            public void run() {
                if (!Configurations.getString("graphql_base_url", Configurations.GRAPHQL_SERVER_URL).isEmpty()) {
                    getTransitStopsFromGraphQL();
                } else {
                    getTransitStops();
                }
                handler.postDelayed(this, Configurations.getInt("transit_stop_refresh_rate", 300) * 1000L);
            }
        };

        transitStopListeners = new ArrayList<TransitStopListener>();
    }

    public static TransitStopManager getInstance() {
        if (instance == null) {
            synchronized (TransitStopManager.class) {
                // May be another thread has created an instance, check again within the synched code
                if (instance == null) {
                    instance = new TransitStopManager();
                }
            }
        }
        return instance;
    }

    public void getTransitStopsFromGraphQL() {
        ApolloClient apolloClient = ApolloClient.builder()
                .serverUrl(Configurations.getString("graphql_base_url", Configurations.GRAPHQL_SERVER_URL))
                .build();
        apolloClient.query(new MapStopsQuery()).enqueue(new ApolloCall.Callback<MapStopsQuery.Data>() {
            @Override
            public void onResponse(@NonNull com.apollographql.apollo.api.Response<MapStopsQuery.Data> response) {
                Gson gson = new Gson();

                if (response != null && response.getData() != null) {
                    List<MapStopsQuery.Stop> stops = new ArrayList<>();
                    //filter stop that not the primary stop.
                    for (MapStopsQuery.Stop stop : response.getData().stops()) {
                        if (stop.stoptimesWithoutPatterns() != null && !stop.stoptimesWithoutPatterns().isEmpty()) {
                            stops.add(stop);
                        }
                    }
                    String jsonResponse = gson.toJson(stops);
                    MyApplication.getInstance().transitStops = gson.fromJson(jsonResponse, new TypeToken<ArrayList<TransitStop>>() {
                    }.getType());
                    Configurations.setString("transit_stops", jsonResponse);
                    fireTransitStopChanged();
                }

            }

            @Override
            public void onFailure(@NonNull ApolloException e) {
                Logger.e("get Map Stops feed error:", e.toString());
            }
        });


    }

    public void getTransitStops() {
        Response.Listener getTransitStopListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response != null && !response.isEmpty() && response.startsWith("[")) {
                    Gson gson = new Gson();

                    MyApplication.getInstance().transitStops = gson.fromJson(response, new TypeToken<ArrayList<TransitStop>>() {
                    }.getType());

                    Configurations.setString("transit_stops", response);
                    fireTransitStopChanged();
                } else {
                    Log.d("transitStop", "data is not correct:" + response);
                }

            }
        };
        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Logger.e("get transit stop feed error:", error.toString());
            }
        };

        try {
            getTransitStopResponse(getTransitStopListener, errorListener);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void getTransitStopResponse(Response.Listener listener,
                                       Response.ErrorListener errListener) throws Exception {
        String url = Resource.API_TRANSIT_STOP;
        String params = "?minLat=" + Configurations.MIN_LATITUDE + "&maxLat=" + Configurations.MAX_LATITUDE
                + "&minLon=" + Configurations.MIN_LONGITUDE + "&maxLon=" + Configurations.MAX_LONGITUDE;

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

    public interface TransitStopListener {
        void transitStopChanged();
    }

    public void addListener(TransitStopManager.TransitStopListener listener) {
        if (!transitStopListeners.contains(listener)) {
            transitStopListeners.add(listener);
        }
    }

    public void removeListener(TransitStopManager.TransitStopListener listener) {
        transitStopListeners.remove(listener);
    }

    private void fireTransitStopChanged() {
        for (TransitStopManager.TransitStopListener listener : transitStopListeners) {
            listener.transitStopChanged();
        }
    }

}
