package com.ibigroup.mobile.otp.android.manager;


import static com.ibigroup.mobile.otp.android.utility.Constants.FEATURE_DATA;
import static com.ibigroup.mobile.otp.android.utility.Constants.MODE_COMBINATION_FILE_NAME;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ibigroup.mobile.ibilib.ConfigOption;
import com.ibigroup.mobile.ibilib.IBIConfig;
import com.ibigroup.mobile.ibilib.IBIEnvironment;
import com.ibigroup.mobile.ibilib.IBISecurity;
import com.ibigroup.mobile.otp.android.MyApplication;
import com.ibigroup.mobile.otp.android.config.ConfigTheme;
import com.ibigroup.mobile.otp.android.config.LayerFeature;
import com.ibigroup.mobile.otp.android.setup.OTPContext;
import com.ibigroup.mobile.otp.android.utility.Configurations;
import com.ibigroup.mobile.otp.android.utility.Constants;
import com.ibigroup.mobile.otp.android.utility.Logger;
import com.ibigroup.mobile.otp.android.utility.Utilities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;


public class ConfigManager {

    private static ConfigManager instance;

    private final boolean isDownloadingSplash = false;

    public ConfigManager() {

    }

    public static ConfigManager getInstance() {
        if (instance == null) {
            synchronized (ConfigManager.class) {
                // May be another thread has created an instance, check again within the synched code
                if (instance == null) {
                    instance = new ConfigManager();
                }
            }
        }
        return instance;
    }

    public static void parsingBrandInfo(String brandInfo) throws Exception {
        if (brandInfo != null) {
            JSONObject brandInfoObject = new JSONObject(brandInfo);


            Configurations.setBoolean("enable_follow_me_by_default", brandInfoObject.optBoolean("enable_follow_me_by_default", true));
            Configurations.setString("app_identifier", brandInfoObject.optString("app_identifier", "sound-transit"));
            Configurations.setString("app_api_key", brandInfoObject.optString("app_api_key", ""));
            Configurations.setString("request_api_key", brandInfoObject.optString("request_api_key", ""));
            Configurations.setString("account_api_key", brandInfoObject.optString("account_api_key", ""));

            //Auth0
            Configurations.setString("auth0_domain", brandInfoObject.optString("auth0_domain", ""));
            Configurations.setString("auth0_client_id", brandInfoObject.optString("auth0_client_id", ""));
            Configurations.setString("auth0_client_secret", brandInfoObject.optString("auth0_client_secret", ""));

            Configurations.setFloat("default_lat", (float) brandInfoObject.optDouble("latitude_of_map_center", 53.54611));
            Configurations.setFloat("default_lon", (float) brandInfoObject.optDouble("longitude_of_map_center", -113.4938));

            String environment = brandInfoObject.optString("environment", "staging");

            String baseUrl = brandInfoObject.optString("base_url", "");
            String loggingUrl = brandInfoObject.optString("logging_url", "");

            setConfigValueToLocal(baseUrl, "app_domain", environment);
            String appDomain = Configurations.getString("app_domain", "");
            if (appDomain != null && !appDomain.endsWith("/")) {
                Configurations.setString("app_domain", appDomain + "/");
            }

            setConfigValueToLocal(loggingUrl, "logging_url", environment);

            String baseURL = Configurations.getString("base_url", "");
            if (baseURL != null && !baseURL.endsWith("/")) {
                Configurations.setString("base_url", baseURL + "/");
            }

            Configurations.setInt("live_tracking_deviation_waittime_seconds",
                    brandInfoObject.optInt("live_tracking_deviation_waittime_seconds", 30));

            //in seconds
            Configurations.setInt("realtime_businfo_refresh_interval",
                    brandInfoObject.optInt("realtime_businfo_refresh_interval", 30));

            Configurations.setLong("android_vibration_duration_ms",
                    brandInfoObject.optLong("android_vibration_duration_ms", 250));
            Configurations.setInt("android_vibration_intensity",
                    brandInfoObject.optInt("android_vibration_intensity", 70));

        }
    }

    private static void setConfigValueToLocal(String config, String key, String environment) throws JSONException {
        if (config != null && !config.isEmpty()) {
            JSONObject configObject = new JSONObject(config);
            String Value = configObject.optString(environment, "");
            if (!Value.isEmpty()) {
                Configurations.setString(key, Value);
            } else //default is staging url
            {
                Configurations.setString(key, configObject.optString("staging", ""));
            }
        }
    }

    public static void parsingTheme(String theme) throws Exception {
        if (theme != null) {
            JSONObject themeObject = new JSONObject(theme);

            String templateString = themeObject.optString("Template", "");
            JSONObject templateObject = null;

            if (!templateString.isEmpty()) {
                templateObject = new JSONObject(templateString);
            }
            String themeValue;
            if (Utilities.isDarkMode()) {
                themeValue = themeObject.optString("Dark_Mode_Skin", "");
            } else {
                themeValue = themeObject.optString("Light_Mode_Skin", "");
            }

            if (templateObject != null && !themeValue.isEmpty()) {
                String currentThemeString = templateObject.optString(themeValue, "");

                Configurations.setString("current_theme", currentThemeString);
                if (!currentThemeString.isEmpty()) {
                    Gson gson = new GsonBuilder().serializeNulls().create();
                    MyApplication.getInstance().configTheme = gson.fromJson(currentThemeString, ConfigTheme.class);
                }
            }

        }
    }

    private static void enableFeatureByConfig(String config, String layerID) throws JSONException {
        if (config != null && layerID != null) {
            String key = "enable_feature_" + layerID;
            String dataKey = layerID + FEATURE_DATA;

            if (!config.isEmpty()) {
                JSONObject configObject = new JSONObject(config);
                String detail = configObject.optString("detail", "");
                if (!detail.isEmpty()) {
                    JSONObject detailObject = new JSONObject(detail);

                    boolean isEnabled = detailObject.optBoolean("is_enabled", false);
                    Configurations.setBoolean(key, isEnabled);

                    if (isEnabled) {
                        Configurations.setString(dataKey, detail);
                    } else {
                        Configurations.setString(dataKey, "");
                    }
                    return;
                }
            }

            Configurations.setBoolean(key, false);
            Configurations.setString(dataKey, "");
        }
    }

    public static void parsingFeature(String feature) throws Exception {
        if (feature != null) {

            try {
                JSONObject featureObject = new JSONObject(feature);

                JSONObject modeObject = featureObject.optJSONObject("Modes");
                if (modeObject != null) {
                    JSONObject detailObject = modeObject.optJSONObject("detail");
                    if (detailObject != null) {
                        String route_mode_combinations_url = detailObject.optString("route_mode_combinations_url");
                        if (route_mode_combinations_url != null && !route_mode_combinations_url.isEmpty()) {
                            Configurations.setString("route_mode_combinations_url", route_mode_combinations_url);
                            downloadJsonAndSave(MyApplication.getInstance().getApplicationContext(), route_mode_combinations_url, MODE_COMBINATION_FILE_NAME);
                        }

                        Configurations.setString("route_mode_overrides", detailObject.optString("route_mode_overrides"));
                    }
                }

                JSONObject searchObject = featureObject.optJSONObject("Search");
                if (searchObject != null) {
                    JSONObject detailObject = searchObject.optJSONObject("detail");
                    if (detailObject != null) {
                        String selectable_modes = detailObject.optString("selectable_modes");
                        if (selectable_modes != null && !selectable_modes.isEmpty()) {
                            Configurations.setString("selectable_modes", selectable_modes);
                        }
                        Boolean enable_report_tracking_gps = detailObject.optBoolean("enable_report_tracking_gps");
                        if (enable_report_tracking_gps != null) {
                            Configurations.setBoolean("enable_report_tracking_gps", enable_report_tracking_gps);
                        }

                        Boolean enable_start_route = detailObject.optBoolean("enable_start_route");
                        if (enable_start_route != null) {
                            Configurations.setBoolean("enable_start_route", enable_start_route);
                        }
                    }
                }

                JSONObject loginObject = featureObject.optJSONObject("Login");
                if (loginObject != null) {
                    JSONObject detailObject = loginObject.optJSONObject("detail");
                    if (detailObject != null) {
                        Boolean enable_mobile_questionairs = detailObject.optBoolean("enable_mobile_questionairs");
                        if (enable_mobile_questionairs != null) {
                            Configurations.setBoolean("enable_mobile_questionairs", enable_mobile_questionairs);
                        }
                        //"Email,SMS,PushNotification,HapticFeedback"
                        String available_notification_methods = detailObject.optString("available_notification_methods");
                        if (available_notification_methods != null && !available_notification_methods.isEmpty()) {
                            Configurations.setString("available_notification_methods", available_notification_methods);
                        }

                        String helpUrl = detailObject.optString("help_url");
                        if (helpUrl != null && !helpUrl.isEmpty()) {
                            Configurations.setString("help_url", helpUrl);
                        }
                    }
                }

                JSONObject menuObject = featureObject.optJSONObject("Menu");
                if (menuObject != null) {
                    JSONObject detailObject = menuObject.optJSONObject("detail");
                    if (detailObject != null) {
                        Boolean enable_main_menu = detailObject.optBoolean("is_enabled");
                        if (enable_main_menu != null) {
                            Configurations.setBoolean("enable_main_menu", enable_main_menu);
                            if (enable_main_menu) {
                                Configurations.setString("menu_items", detailObject.optString("items", ""));
                            } else {
                                Configurations.setString("menu_items", "");
                            }
                        }
                    }
                }

                JSONObject indoorNavigationObject = featureObject.optJSONObject("Indoor Navigation");
                if (indoorNavigationObject != null) {
                    JSONObject detailObject = indoorNavigationObject.optJSONObject("detail");
                    if (detailObject != null) {
                        Configurations.setBoolean("enable_indoor_navigation", detailObject.optBoolean("is_enabled", false));

                        Configurations.setString("indoor_entrance_exit_list_url",
                                detailObject.optString("indoor_entrance_exit_list_url", Configurations.INDOOR_ENTRANCE_EXIT_URL));

                        Configurations.setInt("indoor_entrance_exit_popup_distance_mm",
                                detailObject.optInt("indoor_entrance_exit_popup_distance_mm", 3000));

                        Configurations.setInt("current_instruction_should_update_in_seconds",
                                detailObject.optInt("current_instruction_should_update_in_seconds", 2));
                        Configurations.setInt("current_location_should_update_in_seconds",
                                detailObject.optInt("current_location_should_update_in_seconds", 1));
                        Configurations.setInt("snap_to_wayfind_path_threshold_in_meter",
                                detailObject.optInt("snap_to_wayfind_path_threshold_in_meter", 5));

                        Configurations.setBoolean("indoor_ui_should_display_distance", detailObject.optBoolean("indoor_ui_should_display_distance", true));

                        Configurations.setInt("indoor_nav_deviation_distance_mm",
                                detailObject.optInt("indoor_nav_deviation_distance_mm", 1001));
                        Configurations.setInt("indoor_nav_deviation_popup_count_max_number",
                                detailObject.optInt("indoor_nav_deviation_popup_count_max_number", 3));
                        Configurations.setInt("indoor_nav_deviation_popup_wait_time_seconds",
                                detailObject.optInt("indoor_nav_deviation_popup_wait_time_seconds", 30));
                        Configurations.setBoolean("indoor_nav_deviation_popup_wait_time_accumulate",
                                detailObject.optBoolean("indoor_nav_deviation_popup_wait_time_accumulate", true));

                        Configurations.setBoolean("indoor_checking_active_route_entrance",
                                detailObject.optBoolean("indoor_checking_active_route_entrance", true));
                        Configurations.setInt("indoor_entrance_exit_checking_interval_secs",
                                detailObject.optInt("indoor_entrance_exit_checking_interval_secs", 5));

                        Configurations.setInt("currentLocation_upcoming_point_threshold_feet",
                                detailObject.optInt("currentLocation_upcoming_point_threshold_feet", 5));

                        Configurations.setInt("main_entrance_detection_radius_meters",
                                detailObject.optInt("main_entrance_detection_radius_meters", 10));

                        Configurations.setString("jibestream_endpoint_url",
                                detailObject.optString("jibestream_endpoint_url", ""));
                        Configurations.setString("jibestream_client_id",
                                detailObject.optString("jibestream_client_id", ""));
                        Configurations.setString("jibestream_client_secret",
                                detailObject.optString("jibestream_client_secret", ""));
                        Configurations.setInt("jibesream_customer_id",
                                detailObject.optInt("jibesream_customer_id", 0));
                        Configurations.setString("extends_sdk_key_android",
                                detailObject.optString("extends_sdk_key_android", ""));
                        Configurations.setString("extends_sdk_url",
                                detailObject.optString("extends_sdk_url", ""));
                    }
                }

                JSONObject liveTrackingObject = featureObject.optJSONObject("Live Tracking");
                if (liveTrackingObject != null) {
                    JSONObject detailObject = liveTrackingObject.optJSONObject("detail");
                    if (detailObject != null) {
                        Configurations.setBoolean("enable_feature_live_tracking", liveTrackingObject.optBoolean("is_enabled", false));

                        Configurations.setInt("live_tracking_deviation_waittime_seconds",
                                liveTrackingObject.optInt("live_tracking_deviation_waittime_seconds", 30));

                        Configurations.setInt("live_tracking_repeat_instruction_waittime_seconds",
                                liveTrackingObject.optInt("live_tracking_repeat_instruction_waittime_seconds", 30));
                    }
                }

                enableFeatureByConfig(featureObject.optString("Indoor Navigation", ""), Constants.INDOOR_NAVIGATION_ID);
                enableFeatureByConfig(featureObject.optString("Live Tracking", ""), Constants.LIVE_TRACKING_ID);
                enableFeatureByConfig(featureObject.optString("Preview Trip", ""), Constants.PREVIEW_TRIP_ID);
                enableFeatureByConfig(featureObject.optString("Travel Companion", ""), Constants.TRAVEL_COMPANION_ID);
                enableFeatureByConfig(featureObject.optString("Mobility Profile", ""), Constants.MOBILITY_PROFILE_ID);

                enableFeatureByConfig(featureObject.optString("Fare Info", ""), Constants.FARE_INFO_ID);
                enableFeatureByConfig(featureObject.optString("Trip Detail", ""), Constants.TRIP_DETAIL_ID);
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("parsingFeature", e.toString());
            }
        }
    }

    public static void downloadJsonAndSave(Context context, String jsonUrl, String fileName) {
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(jsonUrl, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // Save the JSON to internal storage
                        saveJsonToInternalStorage(context, response.toString(), fileName);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        //if cannot convert to json object, then try json array
                        RequestQueue requestQueue = Volley.newRequestQueue(context);
                        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(jsonUrl,
                                new Response.Listener<JSONArray>() {
                                    @Override
                                    public void onResponse(JSONArray response) {
                                        // Save the JSON array to internal storage
                                        saveJsonToInternalStorage(context, response.toString(), fileName);
                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        error.printStackTrace();
                                    }
                                });

                        requestQueue.add(jsonArrayRequest);
                    }
                });

        requestQueue.add(jsonObjectRequest);
    }

    private static void saveJsonToInternalStorage(Context context, String json, String fileName) {
        try {
            File internalStorageDir = context.getFilesDir();
            File jsonFile = new File(internalStorageDir, fileName);

            FileOutputStream outputStream = new FileOutputStream(jsonFile);
            outputStream.write(json.getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String loadJsonFromInternalStorage(Context context, String fileName) {
        try {
            File jsonFile = new File(context.getFilesDir(), fileName);

            if (jsonFile.exists()) {
                FileInputStream fileInputStream = new FileInputStream(jsonFile);
                InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                StringBuilder stringBuilder = new StringBuilder();
                String line;

                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line);
                }

                bufferedReader.close();
                inputStreamReader.close();
                fileInputStream.close();

                String json = stringBuilder.toString();
                return json;
            } else {
                return null; // File doesn't exist
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static void digestConfigDate(String response)
    {
        Log.e("ConfigManager", "response not empty");
        try {
            JSONObject configObject = new JSONObject(response);
            String status = configObject.optString("status", "");
            if (status.equalsIgnoreCase("SUCCESS")) {
                String data = configObject.optString("data", "");

                if (!data.isEmpty()) {
                    JSONObject dataObject = new JSONObject(data);
                    String brandInfo = dataObject.optString("brandInfo", "");
                    String feature = dataObject.optString("feature", "");
                    String theme = dataObject.optString("theme", "");
                    String revision = dataObject.optString("revision", "0");

                    Configurations.setString("brand_info", brandInfo);
                    parsingBrandInfo(brandInfo);

                    filterOutUnusedFeatures(feature);
                    parsingFeature(feature);

                    Configurations.setString("theme", theme);
                    parsingTheme(theme);

                    Configurations.setString("revision", revision);
                }
            }
        } catch (Exception e) {
            Log.e("ConfigManager", "Exception");
            e.printStackTrace();
        }
    }

    public static void filterOutUnusedFeatures(String featureData) {
        Gson gson = new Gson();

        try {
            JSONObject featureObject = new JSONObject(featureData);
            JSONObject newObject = new JSONObject();
            Iterator<String> iterator = featureObject.keys();

            while (iterator.hasNext()) {
                String key = iterator.next();
                String value = featureObject.getString(key);

                LayerFeature layerFeature = gson.fromJson(value, LayerFeature.class);

                if (layerFeature != null && layerFeature.getDetail() != null && layerFeature.getDetail().getLayer() != null
                        && layerFeature.getDetail().isEnabled()) {
                    newObject.put(key, value);
                }
            }

            Configurations.setString("feature", newObject.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void initGlobalVariablesWithConfig() {
        String theme = Configurations.getString("theme", "");
        if (!theme.isEmpty()) {
            try {
                parsingTheme(theme);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void loadConfigFileFromAsset(Context context) {
        try {
            String configsData = Utilities.loadDataFromAssetExt(context, "json/config.json");

            if( configsData != null) {
                String response = SecurityManager.decrypt(configsData);

                digestConfigDate(response);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

