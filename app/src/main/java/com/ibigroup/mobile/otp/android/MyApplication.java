package com.ibigroup.mobile.otp.android;

import android.app.Application;
import android.location.Location;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.security.ProviderInstaller;
import com.google.firebase.FirebaseApp;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ibigroup.mobile.otp.android.config.ConfigTheme;
import com.ibigroup.mobile.otp.android.customize.LruBitmapCache;
import com.ibigroup.mobile.otp.android.customize.TLSSocketFactory;
import com.ibigroup.mobile.otp.android.json.AudioAlertElement;
import com.ibigroup.mobile.otp.android.json.DependentMobilityProfile;
import com.ibigroup.mobile.otp.android.json.GPSSubmissionStatus;
import com.ibigroup.mobile.otp.android.json.IndoorEntranceExit;
import com.ibigroup.mobile.otp.android.json.IndoorMainEntrance;
import com.ibigroup.mobile.otp.android.json.JmapTriggerableLocation;
import com.ibigroup.mobile.otp.android.json.ParkRideLocation;
import com.ibigroup.mobile.otp.android.json.RentalVehicle;
import com.ibigroup.mobile.otp.android.json.RouteViewerRoute;
import com.ibigroup.mobile.otp.android.json.StopViewerSchedule;
import com.ibigroup.mobile.otp.android.json.TransitStop;
import com.ibigroup.mobile.otp.android.json.UserPreferences;
import com.ibigroup.mobile.otp.android.json.trip.MonitoredTripResponse;
import com.ibigroup.mobile.otp.android.setup.OTPContext;
import com.ibigroup.mobile.otp.android.utility.Configurations;
import com.ibigroup.mobile.otp.android.utility.Constants;
import com.ibigroup.mobile.otp.android.utility.Logger;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.geometry.LatLng;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSocketFactory;

public class MyApplication extends Application {
    public static final String TAG = MyApplication.class.getSimpleName();
    private static MyApplication mInstance;
    private RequestQueue mRequestQueue;
    private RequestQueue mHttpRequestQueue;
    private ImageLoader mImageLoader;
    private LruBitmapCache gLruBitmapCache;

    public Location currentLocation = null;
    public ArrayList<ParkRideLocation> parkRideLocations;
    public ArrayList<TransitStop> transitStops;
    public ArrayList<RentalVehicle> rentalVehicles;
    public String stopViewerStopID;
    public ArrayList<StopViewerSchedule> stopViewerNextArrivals;
    public UserPreferences userPreferences;
    public int currentSelectedTab = Constants.BOTTOM_TAB_SELECTED_PLAN_TRIP;
    public LatLng mapTargetOnStopViewerPage = null;
    public double mapZoomOnStopViewerPage = 0.0;
    public MonitoredTripResponse gMonitoredTripResponse = null;

    public static synchronized MyApplication getInstance() {
        return mInstance;
    }

    public ConfigTheme configTheme;

    public ArrayList<RouteViewerRoute> routeViewerRoutes = new ArrayList<>();

    public ArrayList<JmapTriggerableLocation> jmapTriggerableLocations = new ArrayList<>();
    public ArrayList<IndoorMainEntrance> indoorMainEntrances = new ArrayList<>();
    public JmapTriggerableLocation currentJmapTriggerableLocation = null;

    public ArrayList<IndoorEntranceExit> indoorEntranceExits = new ArrayList<>();

    //tts
    public AudioAlertElement lastAudioAlertElement = null;
    public ArrayList<AudioAlertElement> audioAlertElementArray = new ArrayList<AudioAlertElement>();
    public int alertPlaybackStatus = Constants.AUDIO_PLAYBACK_IDLE; //0: stopped/IDLE; 1: playing; 2: paused
    public int alertPlaybackTunnel = Constants.AUDIO_PLAYBACK_TUNNEL_IVR; //0: IVR; 1: TTS;

    public boolean needGotoSavedTripPage = false;
    public ArrayList<DependentMobilityProfile> dependentMobilityProfiles;
    public ArrayList<GPSSubmissionStatus> gpsSubmissionStatusList = new ArrayList<GPSSubmissionStatus>();
    public boolean gbStartOver = false;

    @Override
    public void onCreate() {
        try {
            OTPContext.init(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onCreate();

        mInstance = this;
        Configurations.getConfigurations();
        Configurations.searchModeSettingsResetStatus();
        Mapbox.getInstance(this, getString(R.string.mapbox_api_key));

        FirebaseApp.initializeApp(this);
// Explicit initialization of Crashlytics is no longer required.
// OPTIONAL: If crash reporting has been explicitly disabled previously, add:
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true);

        try {
            ProviderInstaller.installIfNeeded(getApplicationContext());
        } catch (GooglePlayServicesRepairableException |
                 GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }

    }

    public RequestQueue getRequestQueue() {

        HurlStack hurlStack = new HurlStack() {
            @Override
            protected HttpURLConnection createConnection(URL url) throws IOException {
                HttpsURLConnection httpsURLConnection = (HttpsURLConnection) super.createConnection(url);
                try {
                    SSLSocketFactory tlsSocketFactory = new TLSSocketFactory();

                    httpsURLConnection.setSSLSocketFactory(tlsSocketFactory);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return httpsURLConnection;

            }
        };

        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext(), hurlStack);
        }

        return mRequestQueue;
    }

    public RequestQueue getRequestQueueWithURL(String url) {
        HurlStack hurlStack = new HurlStack() {
            @Override
            protected HttpURLConnection createConnection(URL url) throws IOException {
                HttpsURLConnection httpsURLConnection = (HttpsURLConnection) super.createConnection(url);
                try {
                    SSLSocketFactory tlsSocketFactory = new TLSSocketFactory();

                    httpsURLConnection.setSSLSocketFactory(tlsSocketFactory);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return httpsURLConnection;

            }
        };
        if (url != null && url.startsWith("https://")) {
            if (mRequestQueue == null) {
                mRequestQueue = Volley.newRequestQueue(getApplicationContext(), hurlStack);
            }

            return mRequestQueue;
        } else {
            if (mHttpRequestQueue == null) {
                mHttpRequestQueue = Volley.newRequestQueue(getApplicationContext());
            }

            return mHttpRequestQueue;
        }
    }

    public LruBitmapCache getLruBitmapCache() {
        if (gLruBitmapCache == null) {
            gLruBitmapCache = new LruBitmapCache();
        }

        return gLruBitmapCache;
    }

    public ImageLoader getImageLoader() {
        getRequestQueue();
        getLruBitmapCache();

        if (mImageLoader == null) {
            mImageLoader = new ImageLoader(this.mRequestQueue,
                    gLruBitmapCache);
        }

        return this.mImageLoader;
    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        // set the default tag if tag is empty
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(String url, Request<T> req) {
        req.setTag(TAG);
        getRequestQueueWithURL(url).add(req);
    }

    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }

    private void formatJmapLocationsIfNeeded() {
        if (jmapTriggerableLocations != null) {
            for (JmapTriggerableLocation jmapTriggerableLocation : jmapTriggerableLocations) {
                if (jmapTriggerableLocation != null && jmapTriggerableLocation.getType() != null &&
                        jmapTriggerableLocation.getType().equalsIgnoreCase("polygon")) {
                    if (jmapTriggerableLocation.getPolygon() != null && !jmapTriggerableLocation.getPolygon().isEmpty()) {
                        continue;
                    }
                    ArrayList<ArrayList<Double>> coordinates = jmapTriggerableLocation.getCoordinates();
                    List<com.google.android.gms.maps.model.LatLng> polygon = new ArrayList<>();
                    for (int i = 0; i < coordinates.size(); i++) {
                        ArrayList<Double> latLng = coordinates.get(i);

                        if (latLng.size() == 2) {
                            polygon.add(new com.google.android.gms.maps.model.LatLng(latLng.get(1),
                                    latLng.get(0)));
                        }
                    }

                    jmapTriggerableLocation.setPolygon(polygon);
                }
            }
        }
    }

    public ArrayList<JmapTriggerableLocation> getJmapTriggerableLocations() {
        String jmapTriggerableLocationString = Configurations.getString("jmap_triggerable_locations", "");
        if (jmapTriggerableLocations.isEmpty() && !jmapTriggerableLocationString.isEmpty()) {
            Gson gson = new Gson();
            try {
                jmapTriggerableLocations = gson.fromJson(jmapTriggerableLocationString,
                        new TypeToken<ArrayList<JmapTriggerableLocation>>() {
                        }.getType());
            } catch (Exception e) {
                Logger.e("indoor", "get JmapTriggerableLocations from local exception:" + e);
            }
            formatJmapLocationsIfNeeded();
        }

        return jmapTriggerableLocations;
    }

    public void setJmapTriggerableLocations(String jmapTriggerableLocationString) {
        if (jmapTriggerableLocationString != null) {
            Gson gson = new Gson();
            try {
                jmapTriggerableLocations = gson.fromJson(jmapTriggerableLocationString,
                        new TypeToken<ArrayList<JmapTriggerableLocation>>() {
                        }.getType());
            } catch (Exception e) {
                Logger.e("indoor", "set JmapTriggerableLocations to local exception:" + e);
            }
            formatJmapLocationsIfNeeded();

            Configurations.setString("jmap_triggerable_locations", gson.toJson(jmapTriggerableLocations));
        }
    }

    public ArrayList<IndoorEntranceExit> getIndoorEntranceExits() {
        String indoorEntranceExitsString = Configurations.getString("indoor_entrance_exit_list", "");
        if (indoorEntranceExits.size() == 0 && !indoorEntranceExitsString.isEmpty()) {
            Gson gson = new Gson();
            try {
                indoorEntranceExits = gson.fromJson(indoorEntranceExitsString,
                        new TypeToken<ArrayList<IndoorEntranceExit>>() {
                        }.getType());
            } catch (Exception e) {
                Logger.e("indoor", "get EntranceExits from local exception:" + e);
            }
        }

        return indoorEntranceExits;
    }

    public void setIndoorEntranceExits(String indoorEntranceExitsString) {
        if (indoorEntranceExitsString != null) {
            Configurations.setString("indoor_entrance_exit_list", indoorEntranceExitsString);

            Gson gson = new Gson();
            try {
                indoorEntranceExits = gson.fromJson(indoorEntranceExitsString,
                        new TypeToken<ArrayList<IndoorEntranceExit>>() {
                        }.getType());
            } catch (Exception e) {
                Logger.e("indoor", "Save EntranceExits to local exception:" + e);
            }
        }
    }

    public ArrayList<IndoorMainEntrance> getIndoorMainEntrancesInfo() {
        String indoorMainEntranceString = Configurations.getString("indoor_main_entrance_info", "");
        if (indoorMainEntrances.size() == 0 && !indoorMainEntranceString.isEmpty()) {
            Gson gson = new Gson();
            try {
                indoorMainEntrances = gson.fromJson(indoorMainEntranceString,
                        new TypeToken<ArrayList<IndoorMainEntrance>>() {
                        }.getType());
            } catch (Exception e) {
                Logger.e("indoor", "get JmapTriggerableLocations from local exception:" + e);
            }
        }

        return indoorMainEntrances;
    }

    public void setIndoorMainEntrancesInfo(String indoorMainEntranceString) {
        if (indoorMainEntranceString != null) {
            Configurations.setString("indoor_main_entrance_info", indoorMainEntranceString);

            Gson gson = new Gson();
            try {
                indoorMainEntrances = gson.fromJson(indoorMainEntranceString,
                        new TypeToken<ArrayList<IndoorMainEntrance>>() {
                        }.getType());
            } catch (Exception e) {
                Logger.e("indoor", "set IndoorMainEntrancesInfo to local exception:" + e);
            }
        }
    }
}
