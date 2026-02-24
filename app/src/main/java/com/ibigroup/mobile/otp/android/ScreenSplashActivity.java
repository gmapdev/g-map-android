package com.ibigroup.mobile.otp.android;

import static com.ibigroup.mobile.otp.android.manager.ConfigManager.downloadJsonAndSave;
import static com.ibigroup.mobile.otp.android.utility.Constants.MODE_COMBINATION_FILE_NAME;
import static com.ibigroup.mobile.otp.android.utility.ServerDelegate.volleyAccountStringRequestExt;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.ibigroup.mobile.otp.android.api.Resource;
import com.ibigroup.mobile.otp.android.manager.ConfigManager;
import com.ibigroup.mobile.otp.android.utility.Configurations;
import com.ibigroup.mobile.otp.android.utility.Constants;
import com.ibigroup.mobile.otp.android.utility.Logger;
import com.ibigroup.mobile.otp.android.utility.ServerDelegate;
import com.ibigroup.mobile.otp.android.utility.Utilities;

public class ScreenSplashActivity extends BaseActivity {

    private final String LOG_TAG = this.getClass().getSimpleName();
    private final int nWelcomeScreenDisplay = 1500;
    private Runnable gRunnable = null;
    private final Handler gHandler = new Handler();

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    protected void onBrandInfoChanged() {

    }

    @Override
    protected void onFeatureChanged() {

    }

    @Override
    protected void onThemeChanged() {

    }

    @Override
    public void onPause() {
        Log.i(LOG_TAG, "onPause");
        super.onPause();
    }

    @Override
    protected void onResume() {
        Log.i(LOG_TAG, "onresume");
        super.onResume();
    }

    private void registerGCMAndLoadDataFromServer() {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w("FCM", "Fetching FCM registration token failed", task.getException());
                            return;
                        }

                        // Get new FCM registration token
                        String token = task.getResult();

                        // send token to backend server
                        try {
                            if (Utilities.isLoggedIn()) {
                                ServerDelegate.postRegistration(token);
                            } else {
                                Configurations.setString("device_token", token);
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        Logger.d("FCM", "token:" + token);
                    }
                });
        gHandler.postDelayed(gRunnable, nWelcomeScreenDisplay);

    }

    /*
     * Define a request code to send to Google Play services
     * This code is returned in Activity.onActivityResult
     */
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    public boolean checkGoogleServiceAvailable() {
        GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();

        int status = googleAPI.isGooglePlayServicesAvailable(this); //GooglePlayServicesUtil.isGooglePlayServicesAvailable(activity);

        boolean available = status == ConnectionResult.SUCCESS;

        if (!available) {
            if (googleAPI.isUserResolvableError(status)) {
                googleAPI.getErrorDialog(this, status,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();

            } else {
                Toast.makeText(this, googleAPI.getErrorString(status), Toast.LENGTH_LONG).show();
            }
        }
        return available;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PLAY_SERVICES_RESOLUTION_REQUEST && resultCode == RESULT_OK) {
            registerGCMAndLoadDataFromServer();
        }
    }

    private void loadDataFromLocal() {
        //load Config from local
        if (Configurations.getString("brand_info", "").isEmpty() ||
                Configurations.getString("theme", "").isEmpty()) {
            ConfigManager.loadConfigFileFromAsset(this);
        } else {
            ConfigManager.initGlobalVariablesWithConfig();
        }

        if (Configurations.getString("jmap_triggerable_locations", "").isEmpty()) {
            loadJmapTriggerableLocationsFromAsset(this);
        }

        if (Configurations.getString("indoor_entrance_exit_list", "").isEmpty()) {
            loadIndoorEntranceExitListFromAsset(this);
        }
    }

    private void loadJmapTriggerableLocationsFromAsset(Context context) {
        try {
            String jmapTriggerableLocations = Utilities.loadDataFromAsset(context, "json/jmap_triggerable_locations.json");

            if (jmapTriggerableLocations != null) {
                Configurations.setString("jmap_triggerable_locations", jmapTriggerableLocations);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadIndoorEntranceExitListFromAsset(Context context) {
        try {
            String indoorEntranceExitList = Utilities.loadDataFromAsset(context, "json/indoor_entrance_exit_list.json");

            if (indoorEntranceExitList != null) {
                Configurations.setString("indoor_entrance_exit_list", indoorEntranceExitList);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screen_splash);
        Log.i("ScreenSplash", "onCreate");

        gRunnable = new Runnable() {
            @Override
            public void run() {
                launchMainPage();
            }
        };
        loadDataFromLocal();

        Configurations.setInt(Constants.ROUTE_FILTER_MODE_KEY, 0);
        Configurations.setInt(Constants.ROUTE_FILTER_AGENCY_KEY, 0);

        if (checkGoogleServiceAvailable()) {
            registerGCMAndLoadDataFromServer();
        } else {
            gHandler.postDelayed(gRunnable, nWelcomeScreenDisplay);
        }
        //download the route_mode_combinations file every time launch the app if it's not empty
        String route_mode_combinations_url = Configurations.getString("route_mode_combinations_url", "");
        if (route_mode_combinations_url != null && !route_mode_combinations_url.isEmpty()) {
            downloadJsonAndSave(MyApplication.getInstance().getApplicationContext(), route_mode_combinations_url, MODE_COMBINATION_FILE_NAME);
        }
    }

    private void launchMainPage() {
        Intent mainIntent = new Intent(ScreenSplashActivity.this, TabActivity.class);//IndoorNavigationActivity.class
        if (getIntent() != null) {
            //deviation survey push
            if (getIntent().hasExtra(Constants.NOTIFICATION_KEY_TYPE)
                    && getIntent().getStringExtra(Constants.NOTIFICATION_KEY_TYPE).equals("2")) {
                Log.e("ScreenSplash", "sendDeviationSurveyTrackToServer");
                String surveySubdomain = getIntent().getStringExtra(Constants.NOTIFICATION_KEY_SURVEY_SUBDOMAIN);
                String surveyID = getIntent().getStringExtra(Constants.NOTIFICATION_KEY_SURVEY_ID);
                String userID = getIntent().getStringExtra(Constants.NOTIFICATION_KEY_USER_ID);
                String tripID = getIntent().getStringExtra(Constants.NOTIFICATION_KEY_TRIP_ID);
                String notificationID = getIntent().getStringExtra(Constants.NOTIFICATION_KEY_NOTIFICATION_ID);
                String surveyUrl = String.format("https://%s.typeform.com/to/%s#user_id=%s&trip_id=%s&notification_id=%s",
                        surveySubdomain,
                        surveyID,
                        userID,
                        tripID,
                        notificationID);

                Utilities.GotoTheUrl(ScreenSplashActivity.this, surveyUrl);

                sendDeviationSurveyTrackToServer(String.format("?user_id=%s&trip_id=%s&notification_id=%s",
                        userID,
                        tripID,
                        notificationID));
                return;
            }
            mainIntent.putExtra("from_notification", getIntent().getBooleanExtra("from_notification", false));
            if (getIntent().hasExtra(Constants.NOTIFICATION_KEY_TRIP_ID)) {
                mainIntent.putExtra(Constants.NOTIFICATION_KEY_TRIP_ID,
                        getIntent().getStringExtra(Constants.NOTIFICATION_KEY_TRIP_ID));
                Logger.d("fcm", "splash trip_id:" + getIntent().getStringExtra(Constants.NOTIFICATION_KEY_TRIP_ID));
            }
        }
        startActivity(mainIntent);
        finish();
    }

    private void sendDeviationSurveyTrackToServer(String params) {
        if (params != null) {
            Response.Listener listener = new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    finish();
                }
            };
            Response.ErrorListener errorListener = new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    finish();
                }
            };

            String url = Resource.API_TRIP_SURVEY_OPEN + params;

            volleyAccountStringRequestExt(Request.Method.GET, url, "", listener, errorListener);
        }
    }
}
