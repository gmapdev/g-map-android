/*
 * Copyright (C) 2013 Maciej Górski
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ibigroup.mobile.otp.android;


import static com.ibigroup.mobile.otp.android.utility.ServerDelegate.volleyAccountStringRequest;

import android.Manifest;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PointF;
import android.location.LocationManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.ibigroup.mobile.otp.android.api.Resource;
import com.ibigroup.mobile.otp.android.customize.IndoorNavigationCustomizePopupDialog;
import com.ibigroup.mobile.otp.android.json.AudioAlertElement;

import com.ibigroup.mobile.otp.android.json.IndoorEntranceExit;
import com.ibigroup.mobile.otp.android.json.IndoorMainEntrance;
import com.ibigroup.mobile.otp.android.json.LocalDestination;
import com.ibigroup.mobile.otp.android.json.MobilityProfile;
import com.ibigroup.mobile.otp.android.json.UserPreferences;
import com.ibigroup.mobile.otp.android.setup.OTPContext;
import com.ibigroup.mobile.otp.android.utility.Configurations;
import com.ibigroup.mobile.otp.android.utility.Constants;
import com.ibigroup.mobile.otp.android.utility.Convert;
import com.ibigroup.mobile.otp.android.utility.Logger;
import com.ibigroup.mobile.otp.android.utility.Utilities;
import com.inpixon.odp.Extend;
import com.inpixon.odp.ExtendNotification;
import com.inpixon.odp.TDNavOutput;
import com.inpixon.odp.aiding.FloorPlan;
import com.inpixon.odp.aiding.Region;
import com.jibestream.jmapandroidsdk.astar.ASNode;
import com.jibestream.jmapandroidsdk.astar.PathPerFloor;
import com.jibestream.jmapandroidsdk.collections.FloorCollection;
import com.jibestream.jmapandroidsdk.components.ActiveVenue;
import com.jibestream.jmapandroidsdk.components.Amenity;
import com.jibestream.jmapandroidsdk.components.BaseModel;
import com.jibestream.jmapandroidsdk.components.Destination;
import com.jibestream.jmapandroidsdk.components.Floor;
import com.jibestream.jmapandroidsdk.components.PathType;
import com.jibestream.jmapandroidsdk.components.Waypoint;
import com.jibestream.jmapandroidsdk.jcontroller.JController;
import com.jibestream.jmapandroidsdk.jcore.JCore;
import com.jibestream.jmapandroidsdk.jmap.JMap;
import com.jibestream.jmapandroidsdk.jmap.JMapOptions;
import com.jibestream.jmapandroidsdk.main.ErrorMessage;
import com.jibestream.jmapandroidsdk.main.PointD;
import com.jibestream.jmapandroidsdk.oauth.JAuth;
import com.jibestream.jmapandroidsdk.rendering_engine.MapLayer;
import com.jibestream.jmapandroidsdk.rendering_engine.MapView;
import com.jibestream.jmapandroidsdk.rendering_engine.Stage;
import com.jibestream.jmapandroidsdk.rendering_engine.drawables.MapDrawable;
import com.jibestream.jmapandroidsdk.rendering_engine.moving_objects.UserLocation;
import com.jibestream.jmapandroidsdk.styles.JStyle;
import com.jibestream.mapuikit.Compass;
import com.jibestream.mapuikit.CompassOptions;
import com.jibestream.mapuikit.FloorSelectorOptions;
import com.jibestream.mapuikit.MapUiKit;
import com.jibestream.mapuikit.PopupDrawable;
import com.jibestream.mapuikit.PopupDrawableOptions;
import com.jibestream.mapuikit.Search;
import com.jibestream.mapuikit.SearchOptions;
import com.jibestream.navigationkit.NavigationKit;
import com.jibestream.navigationkit.instructionfactory.Instruction;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;


public class IndoorNavigationActivity extends AppCompatActivity implements MapView.OnTapListener, TextToSpeech.OnInitListener {

    private ProgressDialog progressDialog;
    private JCore core;
    private JController controller;
    private NavigationKit navigationKit = null;
    private MapUiKit mapUiKit;
    private Waypoint userLocationWaypoint;
    private Stage stage;
    private EditText editText;
    private static final String TAG = IndoorNavigationActivity.class.getSimpleName();
    private JMap map;

    private PointF currentLocationPointF = null;
    private com.jibestream.jmapandroidsdk.components.Map currentLocationMap = null;

    private TextView tv_destination, tv_origin;
    private boolean isOriginSelected = true;
    private RelativeLayout rlControllers;

    private int deviationDistanceMM = 1001;
    private int deviationPopupCountMax = 3;
    private int deviationPopupWaitTimeSeconds = 30;
    private boolean deviationPopupWaitTimeAccumulate = true;

    private int deviationPopupCount = 0;

    private boolean onPaused = false;
    /**
     * Runtime permission request Ends
     */
    private boolean locationUpdated = false;
    private double _latitude = 0;
    private double _longitude = 0;
    private double _bearing = 0;
    private int _floor = 0xffffff;
    private ArrayList<Waypoint> _waypoints;
    private ArrayList<Region> _regions;
    private String _region = "";
    private String _point = "";

    private String KEY = "Aw8AAAAAAAAAAAAAB-7PgAAAAAAH6ICAAAABKIUAHNL2WiCeQXqLDJm1G1Ms0szdQTiLOa9AFGl9MprcIy8SjyYDHvwe2sHJBeh9MzlZryyvVk62zqKy7QmI5uPWNrJGHk6s8-aFeipj0zq1ErnekHFmpG4GcOMAGUShf2487n1Xj51usnpGFoxqPPQfncYQEfs0auFB0d4Vv-o";
    private Extend _extend = null;
    /**
     * Runtime permission request starts
     */
    private static final int REQUEST_CODE_PERMISSIONS = 1;
    private static final int REQUEST_CODE_LOCATION = 2;
    private static final int REQUEST_ENABLE_BT = 3;

    private TextToSpeech tts;
    private AudioManager am;
    private UtteranceProgressListener utteranceProgressListener;
    private AudioManager.OnAudioFocusChangeListener afChangeListener;

    private LinearLayout llMapLabels;
    private SwitchCompat scMapLabels;

    private LinearLayout llArriveExitPopup;
    private ImageView ivArriveExitClose;
    private Button btContinueIndoorNavigation, btStartSavedTrip, btNavigateToAnotherRoom;

    private boolean locationMeEnabled = true;
    private ImageView ivLocateMe;

    private ListView lvFloors, lvRouteInstructions;
    private RouteInstructionListAdapter routeInstructionListAdapter;
    private RelativeLayout rlSearchRoute, rlRouteInstructions, rlRouteInstructionsDetail, rlCurrentInstructions;
    private LinearLayout llRouteInstructions, llAudioNotificationPopup;
    private EditText etOrigin, etDestination;
    private TextView tvCancelSearchRoute, tvSearchRoute, tvAudioNotification, tvTotalDistance, tvInstructionFloor;
    private ImageView ivList, ivMute, ivFirstInstructionClose, ivRouteInstructionClose,
            ivAudioNotification;
    private TextView tvTitle;
    private ImageView ivSearch;
    private FloorListAdapter floorListAdapter;
    private final ArrayList<Floor> floors = new ArrayList<>();
    private int selectedFloorIndex = 0;
    private boolean isMultiFloors = false;

    private boolean isRouteInstructionsVisible = false;
    private boolean isCurrentInstructionsVisible = false;
    private final ArrayList<Instruction> instructions = new ArrayList<>();
    private int currentInstructionIndex = 0;
    private float totalDistance = 0.0f;
    private int currentPlayedInstructionIndex = 0xffffff;

    private ListView lvDestinations;
    private DestinationListAdapter destinationListAdapter;
    private final ArrayList<LocalDestination> destinationList = new ArrayList<>();
    private final ArrayList<LocalDestination> destinationFilterList = new ArrayList<>();
    private int selectedDestinationIndex = -1;

    private LocalDestination originDestination = null, destinationDestination = null;

    private ImageView ivUpload, ivDelete, ivEnableLog;
    private IndoorNavigationCustomizePopupDialog enableLogPopupDialog, uploadLogPopupDialog, deleteLogPopupDialog,
            uploadedLogPopupDialog, deletedLogPopupDialog;
    private PopupDrawable thePopup = null;

    private IndoorNavigationCustomizePopupDialog mainEntrancePopupDialog = null;
    private boolean ttsForMainEntrancePopup = false;
    private boolean mainEntrancePopupShown = false;

    private final boolean stopPreviousAndClearQueue = true;
    private MediaPlayer mediaPlayer;
    private ArrayList<IndoorEntranceExit> indoorEntranceExits;
    private int indoorEntranceExitPopupDistanceMM = 3000;
    private float snapThresholdInMM;
    private boolean pauseCheckingExit = false;
    private boolean isCurrentDestinationExit = false;

    private final int refreshInterval = 2000;
    private Runnable gRunnable = null;
    private final Handler gHandler = new Handler();

    private int refreshUserLocationInterval = 300;
    private Runnable gUserLocationRunnable = null;
    private Handler gUserLocationHandler = new Handler();
    private boolean gStartedUserLocationTimer = false;

    private Runnable gDeviationCheckRunnable = null;
    private Handler gDeviationCheckHandler = new Handler();

    private PathPerFloor[] pathPerFloors = null;
    private int currentPathPerFloorIndex = 0;
    private int currentPointOnFloorIndex = 0;
    private boolean simulateUserMoving = true;
    private boolean pauseDeviateRouteChecking = false;
    private ASNode currentASNode = null;

    private LinearLayout llOffRoutePopup;
    private ImageView ivCancelReroute, ivConfirmReroute;

    private IndoorNavigationCustomizePopupDialog arrivalPopupDialog = null;

    private void initDeviationConfigs() {
        deviationDistanceMM = Configurations.getInt("indoor_nav_deviation_distance_mm", 1001);
        deviationPopupCountMax = Configurations.getInt("indoor_nav_deviation_popup_count_max_number", 3);
        deviationPopupWaitTimeSeconds = Configurations.getInt("indoor_nav_deviation_popup_wait_time_seconds", 30);
        deviationPopupWaitTimeAccumulate = Configurations.getBoolean("indoor_nav_deviation_popup_wait_time_accumulate", true);
    }

    /**
     * Depends on OS version, check whether run time permission should be requested.
     * Since API level 23 (Android M/6.0), we need to request permissions for so called
     * dangerous permissions from the user during runtime.
     */
    private void requestPermissionsFromUser() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int permissionCheckForLocation = ContextCompat.checkSelfPermission(
                    IndoorNavigationActivity.this, Manifest.permission.ACCESS_FINE_LOCATION);
            int permissionCheckForBluetooth, permissionCheckForBluetoothScan;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
                permissionCheckForBluetooth = ContextCompat.checkSelfPermission(
                        IndoorNavigationActivity.this, Manifest.permission.BLUETOOTH_CONNECT);
                permissionCheckForBluetoothScan = ContextCompat.checkSelfPermission(
                        IndoorNavigationActivity.this, Manifest.permission.BLUETOOTH_SCAN);
            } else {
                permissionCheckForBluetooth = PackageManager.PERMISSION_GRANTED;
                permissionCheckForBluetoothScan = PackageManager.PERMISSION_GRANTED;
            }

            if (permissionCheckForLocation != PackageManager.PERMISSION_GRANTED ||
                    permissionCheckForBluetooth != PackageManager.PERMISSION_GRANTED ||
                    permissionCheckForBluetoothScan != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(
                        new String[]{
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.BLUETOOTH_SCAN,
                                Manifest.permission.BLUETOOTH_CONNECT},
                        REQUEST_CODE_PERMISSIONS);
            } else {
                //Permissions were already granted, continue check if Location Services are enabled.
                checkLocationIsEnabled();
            }


        } else {
            // For OS older than M/6.0, the permissions should already be granted during installation.
            continueLoading();
        }
    }

    /**
     * The Android system callback after the user has granted or denied permission requests
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (requestCode == REQUEST_CODE_PERMISSIONS) {
                // Since we have requested multiple permissions,
                // we need to check if any were denied

                for (int grant : grantResults) {

                    if (grant == PackageManager.PERMISSION_DENIED) {
                        if (shouldShowRequestPermissionRationale(
                                Manifest.permission.ACCESS_FINE_LOCATION)) {
                            // User has *NOT* allowed us to use ACCESS_FINE_LOCATION
                            // permission on first try. This is the last chance we get
                            // to ask the user, so we explain why we want this permission
                            Toast.makeText(this,
                                    "Location is used for Bluetooth location",
                                    Toast.LENGTH_SHORT).show();
                            // Re-ask for permission
                            requestPermissionsFromUser();
                            return;
                        }

                        // The user has finally denied us permissions.
                        Toast.makeText(this,
                                "Cannot continue without permissions.",
                                Toast.LENGTH_SHORT).show();
                        this.finishAffinity();
                        return;
                    }
                }
                checkLocationIsEnabled();
            }
        }

    }

    /**
     * Check if user has enabled location or not. If not, direct user to Settings to enable it.
     */
    private void checkLocationIsEnabled() {
        // On android Marshmallow we also need to have active Location Services (GPS or Network based)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            LocationManager locationManager =
                    (LocationManager) getSystemService(LOCATION_SERVICE);
            boolean isNetworkLocationProviderEnabled =
                    locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            boolean isGPSLocationProviderEnabled =
                    locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

            if (!isGPSLocationProviderEnabled &&
                    !isNetworkLocationProviderEnabled) {
                // If both providers are disabled, we need to ask the user to enable it
                Toast.makeText(this,
                        "Location is required but it is off, enable it in settings.",
                        Toast.LENGTH_LONG).show();
                Intent locationInSettingsIntent =
                        new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                this.startActivityForResult(
                        locationInSettingsIntent, REQUEST_CODE_LOCATION);
            } else {
                continueLoading();
            }
        } else {
            continueLoading();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_LOCATION) {
            // Check if the user has really enabled Location services.
            checkLocationIsEnabled();
        } else if (requestCode == REQUEST_ENABLE_BT) {

        }
    }

    // At this point we have got all permission granted and location enabled, ready to launch SDK.
    private void continueLoading() {
        if (_extend != null) {
            _extend.bind();
        }

        BluetoothAdapter mBtAdapter = BluetoothAdapter.getDefaultAdapter();
        if (!mBtAdapter.isEnabled()) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            } else {
                mBtAdapter.enable();
            }
        }
    }

    public void setRegion(String name) {
        _region = name;

    }

    public void setPoint(String name) {
        _point = name;

    }

    public void setLatitude(double latitude) {
        locationUpdated = true;
        _latitude = latitude;

    }

    public void setLongitude(double longitude) {
        _longitude = longitude;

    }

    private void refreshUserLocationByInterval() {
        if (_floor != 0xffffff) {
            updateCurrentLocation(_longitude, _latitude, _floor);
        }
        checkMainEntranceAndPopup();
    }

    public void setBearing(double bearing) {
        _bearing = bearing;

    }

    public void setFloor(int floor) {
        _floor = floor;

    }

    private void refreshCurrentLocationAndUpdateInstruction(PointF pointF,
                                                            com.jibestream.jmapandroidsdk.components.Map currentLocationMap) {
        if (currentLocationMap != null) {
            checkInstructionAndUpdate(currentLocationMap.getId(), pointF.x, pointF.y);

            if (currentLocationMap != controller.getCurrentMap()) {
                controller.showMap(currentLocationMap, new JController.OnMapShownCallback() {
                    @Override
                    public void onBeforeMapShown(MapDrawable mapDrawable) {

                    }

                    @Override
                    public void onMapShown() {
                        IndoorNavigationActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                updateCurrentSelectedFloor();
                                if (tvTitle != null) {
                                    tvTitle.setText(String.format("%s - %s", getString(R.string.indoor_map),
                                            getFloorShortName(currentLocationMap.getId())));
                                }

                                if (tvInstructionFloor != null && tvInstructionFloor.getVisibility() == View.VISIBLE) {
                                    tvInstructionFloor.setText(getFloorName(currentLocationMap.getId()));
                                }

                                if (deviationPopupCount == 0) {
                                    checkDeviateAndPopup();
                                }
                            }
                        });

                    }

                    @Override
                    public void onError(String s) {

                    }
                });
            } else {
                if (deviationPopupCount == 0) {
                    checkDeviateAndPopup();
                }
            }
        }

    }

    //return true if it's deviated.
    private boolean checkDeviateAndPopup() {
        if (Configurations.getConfigurations().getBoolean("enable_indoor_reroute", false)) {
            if (!pauseDeviateRouteChecking && llOffRoutePopup.getVisibility() != View.VISIBLE) {
                if (pathPerFloors != null &&
                        navigationKit.hasUserVeeredOffRoute(pathPerFloors, deviationDistanceMM)) {

                    llOffRoutePopup.setVisibility(View.VISIBLE);
                    speakOutWithMessage(getString(R.string.off_route_warning_message));

                    deviationPopupCount++;
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isCloseEnough(float x, float y, Double[] coordinates, int thresholdInFeet) {
        if (coordinates != null
                && coordinates.length >= 2) {
            double currentDistance = Math.sqrt(Math.pow(coordinates[0] - x, 2)
                    + Math.pow(coordinates[1] - y, 2));

            return controller.getCurrentMap().getMmPerPixel() *
                    currentDistance * Constants.METER_TO_FOOT / 1000 <= thresholdInFeet;
        }
        return false;
    }

    private void checkInstructionAndUpdate(int mapID, float x, float y) {
        int thresholdInFeet = Configurations.getInt("currentLocation_upcoming_point_threshold_feet", 5);
        if (currentInstructionIndex < instructions.size()) {
            for (int i = currentInstructionIndex; (i < instructions.size() && i < currentInstructionIndex + 3); i++) {
                Instruction instruction = instructions.get(i);

                if (currentPlayedInstructionIndex != i) {
                    if (instruction.getDecisionPoint() != null) {
                        Waypoint waypoint = instruction.getDecisionPoint();
                        if (isCloseEnough(x, y, waypoint.getCoordinates(), thresholdInFeet)) {
                            //Announce TTS
                            playCurrentInstruction();

                            currentInstructionIndex = i;
                            currentPlayedInstructionIndex = i;
                            break;
                        }
                    }
                }
                if (instruction.getCompletionPoint() != null) {
                    Waypoint waypoint = instruction.getCompletionPoint();
                    if (isCloseEnough(x, y, waypoint.getCoordinates(), thresholdInFeet)) {
                        currentInstructionIndex = i + 1;

                        refreshCurrentInstructions();
                        break;
                    }
                }
            }

        }

    }

    private void updateCurrentLocation(double lon, double lat, int floor) {
        if (controller != null) {
            //If converting from EPSG:4326, point must be in (long, lat) format.
            PointD convertPoint = new PointD(lon, lat);
// Pass those values into the utility class
            controller.convertCoordinate(convertPoint, "EPSG:4326", "jmap:local");
//jmap local point to use in "updateUserLocation" function
            if (!Double.isNaN(convertPoint.x) && !Double.isNaN(convertPoint.y)) {
                currentLocationPointF = new PointF((float) (convertPoint.x), (float) (convertPoint.y));

                if (isMultiFloors) {
                    currentLocationMap = getCurrentFloorMap(floor);
                }

                if (currentLocationMap == null) {
                    currentLocationMap = controller.getCurrentMap();
                }

                if (!onPaused) {
                    userLocationWaypoint = controller.getActiveVenue().getClosestWaypointToCoordinatesOnMap(currentLocationPointF,
                            currentLocationMap);
                    controller.updateUserLocationPosition(currentLocationPointF,
                            (int) _bearing, 100, currentLocationMap);

                    //if start the indoor navigation
                    if (pathPerFloors != null && navigationKit != null) {
                        refreshCurrentLocationAndUpdateInstruction(currentLocationPointF, currentLocationMap);
                    } else {
                        if (currentLocationMap != controller.getCurrentMap() && locationMeEnabled) {
                            controller.showMap(currentLocationMap, new JController.OnMapShownCallback() {
                                @Override
                                public void onBeforeMapShown(MapDrawable mapDrawable) {

                                }

                                @Override
                                public void onMapShown() {
                                    IndoorNavigationActivity.this.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            updateCurrentSelectedFloor();
                                            if (tvTitle != null) {
                                                tvTitle.setText(String.format("%s - %s", getString(R.string.indoor_map),
                                                        getFloorShortName(currentLocationMap.getId())));
                                            }
                                        }
                                    });

                                }

                                @Override
                                public void onError(String s) {

                                }
                            });
                        }
                    }
                }
                checkArriveExitAction(currentLocationPointF, currentLocationMap.getId());
            }
        }
    }

    class ExtendListener implements Extend.OnExtend {
        @Override
        public void onKeyValidation(boolean validated) {
            if (validated) {
                _extend.setVicinityEnabled(true);
                _extend.start();

                gStartedUserLocationTimer = true;
                gUserLocationHandler.postDelayed(gUserLocationRunnable, refreshUserLocationInterval);
            }
        }

        @Override
        public void onReady(boolean ready) { //True means client has been successfully bound to ExtendService.
            //If false, something wrong has happened during binding to ExtendService and no more Extend operation is supported.
        }

        @Override
        public void onFacilityUpdate(String name, com.inpixon.odp.aiding.Location location, Region region, List<FloorPlan> plans) {
        }

        @Override
        public void onNavigationUpdate(TDNavOutput navOutput) {
            double latitude = navOutput.latitude;
            double longitude = navOutput.longitude;
            double bearing = navOutput.bearing;
            int floor = navOutput.floor;

            setLatitude(latitude);
            setLongitude(longitude);
            setBearing(bearing);
            setFloor(floor);
        }

        @Override
        public void onDebugEvent(Bundle bundle) {
        }

        @Override
        public void onUploadProgressUpdate(int i, int i1, int i2, int i3) {

        }

    }

    @Override
    protected void onDestroy() {
        // Don't forget to shutdown tts!
        stopTTS();
        if (tts != null) {
            tts.shutdown();
        }

        if (_extend != null) {
            _extend.unbind();
        }

        if (controller != null) {
            controller.removeComponent(UserLocation.getInstance(), controller.getCurrentMap());
            controller.clearMapDrawables();

            controller = null;
        }

        if (gUserLocationHandler != null) {
            gUserLocationHandler.removeCallbacks(gUserLocationRunnable);
            gUserLocationHandler = null;
        }

        if (gDeviationCheckHandler != null) {
            gDeviationCheckHandler.removeCallbacks(gDeviationCheckRunnable);
            gDeviationCheckHandler = null;
        }

        super.onDestroy();
    }

    public void stopTTS() {
        MyApplication.getInstance().audioAlertElementArray.clear();
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
        }
        MyApplication.getInstance().alertPlaybackStatus = Constants.AUDIO_PLAYBACK_IDLE;
        if (tts != null) {
            tts.stop();
        }
    }

    private void initMediaPlayerIfNeeded() {
        if (mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {

                @Override
                public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
                    if (MyApplication.getInstance().alertPlaybackTunnel == Constants.AUDIO_PLAYBACK_TUNNEL_IVR) {
                        if (MyApplication.getInstance().audioAlertElementArray.size() > 0) {
                            MyApplication.getInstance().alertPlaybackTunnel = Constants.AUDIO_PLAYBACK_TUNNEL_TTS;
                            speakTTS(MyApplication.getInstance().audioAlertElementArray.get(0).getDescription());
                        }
                    } else {
                        MyApplication.getInstance().alertPlaybackStatus = Constants.AUDIO_PLAYBACK_IDLE;
                        //playing TTS error, play next one
                        if (MyApplication.getInstance().audioAlertElementArray.size() > 0) {
                            MyApplication.getInstance().audioAlertElementArray.remove(0);
                            playAlert();
                        }
                    }

                    return false;
                }
            });

            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    MyApplication.getInstance().alertPlaybackStatus = Constants.AUDIO_PLAYBACK_IDLE;
                    if (MyApplication.getInstance().audioAlertElementArray.size() > 0) {
                        MyApplication.getInstance().audioAlertElementArray.remove(0);
                    }
                    playAlert();
                }
            });
        }
    }

    public void speakOut(AudioAlertElement audioAlertElement) {
        if (audioAlertElement == null || (!Configurations.getBoolean("indoor_navigation_audio_announcement_enabled", true)
                && !ttsForMainEntrancePopup)) {
            return;
        }
        ttsForMainEntrancePopup = false;
        audioAlertElement.setPlayedTimestamp(System.currentTimeMillis() / 1000);
        MyApplication.getInstance().lastAudioAlertElement = audioAlertElement;

        initMediaPlayerIfNeeded();

        if (stopPreviousAndClearQueue) {
            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            MyApplication.getInstance().alertPlaybackStatus = Constants.AUDIO_PLAYBACK_IDLE;
            MyApplication.getInstance().audioAlertElementArray.clear();
        }

        MyApplication.getInstance().audioAlertElementArray.add(audioAlertElement);
        if (MyApplication.getInstance().audioAlertElementArray.size() == 1 &&
                MyApplication.getInstance().alertPlaybackStatus == Constants.AUDIO_PLAYBACK_IDLE) {
            playAlert();
        }

    }

    @Override
    public void onInit(int status) {
        try {
            if (status == TextToSpeech.SUCCESS) {
                boolean initSuccessful = false;
                String supportLanguage = Configurations.getString("support_language", "en");
                String defaultLanguage = Configurations.getString("default_language", "en");
                String defaultCountryCode = Configurations.getString("DefaultCountry", "us");

                Locale locale = Locale.getDefault();

                if (supportLanguage != null && supportLanguage.contains(locale.getLanguage()) &&
                        (TextToSpeech.LANG_AVAILABLE == tts.isLanguageAvailable(locale)
                                || TextToSpeech.LANG_COUNTRY_AVAILABLE == tts.isLanguageAvailable(locale))) {
                    int result = tts.setLanguage(locale);

                    if (result == TextToSpeech.LANG_MISSING_DATA
                            || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Logger.e("TTS", "This Language is not supported");
                    } else {
                        initSuccessful = true;
                    }
                } else {
                    Logger.e("TTS", "This Language is not supported");
                }

                if (!initSuccessful) {
                    locale = new Locale(defaultLanguage, defaultCountryCode);// Locale.US;
                    if (TextToSpeech.LANG_AVAILABLE == tts.isLanguageAvailable(locale)) {
                        tts.setLanguage(locale);
                    }
                }

            } else {
                Logger.e("TTS", "Initilization Failed!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean requestAudioFocus() {
        // Request audio focus for playback
        int result = am.requestAudioFocus(afChangeListener,
                // Use the music stream.
                AudioManager.STREAM_MUSIC,
                // Request permanent focus.
                AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);//AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK
        int currentVolume = am.getStreamVolume(AudioManager.STREAM_MUSIC);

        Logger.d("audio_focus", "Request audio focus result:" + result);

        if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            // Start playback, continue...
            Logger.d("audiotag", "AudioManager.AUDIOFOCUS_REQUEST_GRANTED");

            am.setStreamVolume(AudioManager.STREAM_MUSIC, currentVolume, 0);
            return true;
        } else {
            Logger.d("audiotag", "denied audio focus gain...");

            result = am.requestAudioFocus(afChangeListener,
                    // Use the music stream.
                    AudioManager.STREAM_MUSIC,
                    // Request permanent focus.
                    AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK);//AUDIOFOCUS_GAIN_TRANSIENT
            Logger.d("audio_focus", "Second Request audio focus result:" + result);
            if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                // Start playback, continue...
                am.setStreamVolume(AudioManager.STREAM_MUSIC, currentVolume, 0);
                Logger.d("audiotag", "second try: AudioManager.AUDIOFOCUS_REQUEST_GRANTED");
                return true;
            } else {
                return false;
            }

        }
    }

    private void abandonAudioFocus() {
        am.abandonAudioFocus(afChangeListener);
    }

    private void speakTTS(String text) {
        if (text != null && tts != null) {
            if (utteranceProgressListener == null) {
                utteranceProgressListener = new UtteranceProgressListener() {
                    @Override
                    public void onStart(String utteranceId) {
                        Logger.d("audio_focus", "onStart! utteranceId:" + utteranceId);
                    }

                    @Override
                    public void onDone(String utteranceId) {
                        Logger.d("audio_focus", "onDone! utteranceId:" + utteranceId);
                        abandonAudioFocus();

                        playTTSAudio();
                    }

                    @Override
                    public void onError(String utteranceId) {
                        Logger.d("audio_focus", "onError! utteranceId:" + utteranceId);
                        MyApplication.getInstance().alertPlaybackStatus = Constants.AUDIO_PLAYBACK_IDLE;
                        abandonAudioFocus();
                    }
                };
            }
            requestAudioFocus();

            tts.setOnUtteranceProgressListener(utteranceProgressListener);
            HashMap<String, String> params = new HashMap<String, String>();
            params.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, text);
            //tts.speak(text, TextToSpeech.QUEUE_ADD, params);

            File file = this.getFileStreamPath(Constants.TTS_AUDIO_FILE_NAME);

            if (file.exists()) {
                file.delete();
            }

            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, "uniqueUtteranceId");
        }
    }

    private void playTTSAudio() {
        try {
            File file = this.getFileStreamPath(Constants.TTS_AUDIO_FILE_NAME);

            Logger.d("TTS", "play tts audio file name:" + file.getAbsolutePath());
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.reset();
            mediaPlayer.setDataSource(file.getAbsolutePath());
            mediaPlayer.prepare(); // might take long! (for buffering, etc)

            mediaPlayer.start();

        } catch (Exception e) {
            e.printStackTrace();
            MyApplication.getInstance().alertPlaybackStatus = Constants.AUDIO_PLAYBACK_IDLE;
            //playing TTS error, play next one
            if (MyApplication.getInstance().audioAlertElementArray.size() > 0) {
                MyApplication.getInstance().audioAlertElementArray.remove(0);
            }
            playAlert();

        }
    }

    private void playAlert() {
        if (MyApplication.getInstance().audioAlertElementArray.size() > 0) {
            MyApplication.getInstance().alertPlaybackStatus = Constants.AUDIO_PLAYBACK_PLAYING;
            MyApplication.getInstance().alertPlaybackTunnel = Constants.AUDIO_PLAYBACK_TUNNEL_TTS;
            speakTTS(MyApplication.getInstance().audioAlertElementArray.get(0).getDescription());
        }
    }

    private void speakOutWithMessage(String message) {
        if (Utilities.doesCurrentUserNeedHaptic()) {
            Utilities.vibrateOneShot();
        }

        AudioAlertElement audioAlertElement = new AudioAlertElement();

        audioAlertElement.setDescription(message);
        audioAlertElement.setAlertID(null);
        audioAlertElement.setClustergroup(0);

        speakOut(audioAlertElement);
    }

    private void checkCurrentDestinationIsExit() {
        isCurrentDestinationExit = false;
        if (destinationDestination != null && destinationDestination.getName() != null
                && indoorEntranceExits != null && indoorEntranceExits.size() > 0) {
            String destinationName = destinationDestination.getName().toLowerCase();
            for (IndoorEntranceExit indoorEntranceExit : indoorEntranceExits) {
                if (indoorEntranceExit != null && indoorEntranceExit.getName() != null
                        && destinationName.contains(indoorEntranceExit.getName().toLowerCase())) {
                    isCurrentDestinationExit = true;
                    return;
                }
            }
        }
    }

    private void checkArriveExitAction(PointF pointF, int mapID) {
        //_longitude, _latitude
        if (!pauseCheckingExit && isCurrentDestinationExit && destinationDestination != null
                && llArriveExitPopup.getVisibility() != View.VISIBLE) {
            Waypoint destinationWaypoint = destinationDestination.getWaypoints()[0];

            //need to be the same floor
            if (!isMultiFloors || destinationWaypoint.getMapId() == mapID) {
                double currentDistance = Math.sqrt(Math.pow(destinationWaypoint.getCoordinates()[0] - pointF.x, 2)
                        + Math.pow(destinationWaypoint.getCoordinates()[1] - pointF.y, 2));

                if (controller.getCurrentMap().getMmPerPixel() *
                        currentDistance < indoorEntranceExitPopupDistanceMM) {
                    llArriveExitPopup.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_indoor_navigation);

        indoorEntranceExits = MyApplication.getInstance().getIndoorEntranceExits();
        indoorEntranceExitPopupDistanceMM = Configurations.getInt("indoor_entrance_exit_popup_distance_mm", 3000);
        KEY = Configurations.getString("extends_sdk_key_android", "");

        _extend = new Extend(getApplicationContext(), KEY, Configurations.getString("extends_sdk_url", ""));

        if (_extend != null) {
            _extend.setExtendNotification(new ExtendNotification("", getString(R.string.gmap_indoor_notification), R.drawable.ic_login_logo));
            _extend.setOnExtend(new ExtendListener());
            Logger.d("EXTEND", "EXTEND CREATED");
        }
        requestPermissionsFromUser();
        refreshUserLocationInterval = Configurations.getInt("current_location_should_update_in_seconds", 1) * 1000;
        Convert.mDensity = getResources().getDisplayMetrics().density;
        stage = findViewById(R.id.stage);
        rlControllers = findViewById(R.id.rl_controllers);
        int venueID = 0;
        if (MyApplication.getInstance().currentJmapTriggerableLocation != null) {
            venueID = MyApplication.getInstance().currentJmapTriggerableLocation.getVenueId();
        }
        String clientID = Configurations.getString("jibestream_client_id", "");
        String clientSecret = Configurations.getString("jibestream_client_secret", "");
        int customerID = Configurations.getInt("jibesream_customer_id", 0);
        String host = Configurations.getString("jibestream_endpoint_url", "");
        JAuth authentication = new JAuth(clientID, clientSecret);

        JMapOptions mapOptions = new JMapOptions(authentication,
                customerID, venueID, host, stage);
        mapOptions.shouldParseAllMaps(true);
        mapOptions.shouldApplyDisplayMode(true);

        map = new JMap(this, mapOptions);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading map...");
        progressDialog.setCancelable(false);
        rlControllers.setVisibility(View.GONE);
        progressDialog.show();

        snapThresholdInMM = Configurations.getInt("snap_to_wayfind_path_threshold_in_meter", 5) * 1000;
        //CHECK - setUpToggleButtons method
        map.init(new JMap.OnMapInitCallback() {
            @Override
            public void onReady() {
                core = map.getCore();
                controller = map.getController();

                Logger.i(TAG, controller.getCurrentMap().id + "");

                final PointF centerOfMap = new PointF(
                        controller.getCurrentMap().getWidth() / 2, controller.getCurrentMap().getHeight() / 2);

                UserLocation.getInstance().setPosition(centerOfMap);

                userLocationWaypoint = controller.getActiveVenue().getClosestWaypointToCoordinatesOnMap(centerOfMap,
                        controller.getCurrentMap());//map

                controller.addComponent(UserLocation.getInstance(), controller.getCurrentMap(),
                        new PointF(userLocationWaypoint.getCoordinates()[0].floatValue(), userLocationWaypoint.getCoordinates()[1].floatValue()));

                UserLocation.getInstance().setSnapToGrid(true);

                UserLocation.getInstance().snapToWayfindingPathWithThreshold(snapThresholdInMM / controller.getCurrentMap().getMmPerPixel());

                controller.setOnTapListener(IndoorNavigationActivity.this);
                controller.setOnTapListener(new MapView.OnTapListener() {
                    @Override
                    public void onTap(MotionEvent motionEvent, Object o) {
                        if (lvFloors.getVisibility() == View.VISIBLE) {
                            lvFloors.setVisibility(View.GONE);
                        }

                        llMapLabels.setVisibility(View.VISIBLE);
                    }
                });
                mapUiKit = new MapUiKit(core, controller);

                RelativeLayout container = mapUiKit.getUiContainer();

                Logger.i(TAG, container.toString());

                mapUiKit.setAllowConcurrentPopups(false);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        renderMapUiComponents();
                        initFloors();

                        rlControllers.setVisibility(View.VISIBLE);
                        if (progressDialog != null) {
                            progressDialog.dismiss();
                            progressDialog = null;
                        }

                        //Enabled the location Me function by default
                        ivSearch.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                locationMeEnabled = true;
                            }
                        }, 100);

                        setupMapLabels();
                    }
                });
                controller.enableLayerInteractivity("Popups");
                controller.enableLayerInteractivity(MapLayer.Layer_Units);
            }

            @Override
            public void onError(String s) {
                Log.e(ErrorMessage.ERROR_TAG, s);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (progressDialog != null) {
                            progressDialog.dismiss();
                            progressDialog = null;
                        }
                    }
                });
            }
        });

        LinearLayout llDestination = findViewById(R.id.ll_destination);
        lvDestinations = findViewById(R.id.lv_destinations);
        destinationListAdapter = getDestinationListAdapter();
        lvDestinations.setAdapter(destinationListAdapter);
        llDestination.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isOriginSelected = false;
                lvDestinations.setVisibility(View.VISIBLE);
            }
        });
        tv_destination = findViewById(R.id.tv_destination);

        LinearLayout llOrigin = findViewById(R.id.ll_origin);
        tv_origin = findViewById(R.id.tv_origin);
        llOrigin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isOriginSelected = true;
                lvDestinations.setVisibility(View.VISIBLE);
            }
        });

        setupHeader();
        setupSearchRoute();
        setupRouteInstructions();
        setupDeviateRoutePopup();
        setupArriveExitPopup();

        gRunnable = new Runnable() {
            @Override
            public void run() {
                refreshCurrentLocationAndUpdateInstructionSimulation();
            }
        };
        gUserLocationRunnable = new Runnable() {
            @Override
            public void run() {
                refreshUserLocationByInterval();
                gUserLocationHandler.postDelayed(gUserLocationRunnable, refreshUserLocationInterval);
            }
        };

        gDeviationCheckRunnable = new Runnable() {
            @Override
            public void run() {
                if (!checkDeviateAndPopup()) {
                    deviationPopupChecking();
                }
            }
        };
        initTTS();

        setUpLocateMe();
        initDeviationConfigs();

        setupExtendSDKLogFeature();
    }

    private void setupMapLabels() {
        boolean enableMapLabels = Configurations.getBoolean("enable_indoor_map_labels", true);
        llMapLabels = findViewById(R.id.ll_map_labels);
        scMapLabels = findViewById(R.id.sc_map_labels);

        scMapLabels.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(@NonNull CompoundButton buttonView, boolean isChecked) {
                Configurations.setBoolean("enable_indoor_map_labels", isChecked);
                if (isChecked) {
                    showMapLabels();
                } else {
                    hideMapLabels();
                }
            }
        });

        scMapLabels.setChecked(enableMapLabels);

        setMapLabelsLayoutLocation(false);
    }

    private void setMapLabelsLayoutLocation(boolean startedRoute) {
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) llMapLabels.getLayoutParams();
        if (startedRoute) {
            layoutParams.topMargin = Convert.dpToPx(88);
        } else {
            layoutParams.topMargin = Convert.dpToPx(78);
        }

        llMapLabels.setVisibility(View.VISIBLE);
        llMapLabels.setLayoutParams(layoutParams);
    }

    private void showMapLabels() {
        if (controller != null) {
            controller.showAllTextMapLabels();
        }
    }

    private void hideMapLabels() {
        if (controller != null) {
            controller.hideAllTextMapLabels();
        }
    }

    private void setupArriveExitPopup() {
        llArriveExitPopup = findViewById(R.id.ll_arrive_exit_popup);
        ivArriveExitClose = findViewById(R.id.iv_arrive_exit_close);
        btContinueIndoorNavigation = findViewById(R.id.bt_continue_indoor_navigation);
        btStartSavedTrip = findViewById(R.id.bt_start_saved_trip);
        btNavigateToAnotherRoom = findViewById(R.id.bt_navigate_to_another_room);

        ivArriveExitClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                llArriveExitPopup.setVisibility(View.GONE);
                pauseCheckingExit = true;
            }
        });

        btContinueIndoorNavigation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                llArriveExitPopup.setVisibility(View.GONE);
                pauseCheckingExit = true;
            }
        });

        btStartSavedTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyApplication.getInstance().needGotoSavedTripPage = true;
                finish();
            }
        });

        btNavigateToAnotherRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                llArriveExitPopup.setVisibility(View.GONE);
                pauseCheckingExit = true;

                rlSearchRoute.setVisibility(View.VISIBLE);
                llMapLabels.setVisibility(View.GONE);
                etDestination.requestFocus();
                isOriginSelected = false;
                filterDestination();
            }
        });
    }

    private void initTTS() {
        am = (AudioManager) OTPContext.getAppContext().getSystemService(Context.AUDIO_SERVICE);
        afChangeListener = new AudioManager.OnAudioFocusChangeListener() {
            public void onAudioFocusChange(int focusChange) {
                Logger.d("audio_focus", "Audio focus status:" + focusChange);
                if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT) {
                    Logger.d("audio_focus", "Audio focus loss transient, stop playing alert.");

                } else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
                    Logger.d("audio_focus", "Audio focus gain, try to play the next alert.");

                } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
                    Logger.d("audio_focus", "Audio focus loss, stop playing alert.");
                    am.abandonAudioFocus(afChangeListener);
                }
            }
        };
        tts = new TextToSpeech(this, this, "com.google.android.tts");
    }

    private void setUpLocateMe() {
        ivLocateMe = findViewById(R.id.iv_locate_me);
        ivLocateMe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (controller != null) {
                    //UserLocation.getInstance().moveTo(currentLocationPointF);

                    if (currentLocationMap != controller.getCurrentMap()) {
                        controller.showMap(currentLocationMap, new JController.OnMapShownCallback() {
                            @Override
                            public void onBeforeMapShown(MapDrawable mapDrawable) {

                            }

                            @Override
                            public void onMapShown() {
                                IndoorNavigationActivity.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        updateCurrentSelectedFloor();
                                        if (tvTitle != null) {
                                            tvTitle.setText(String.format("%s - %s", getString(R.string.indoor_map),
                                                    getFloorShortName(currentLocationMap.getId())));
                                        }

                                        if (tvInstructionFloor != null && tvInstructionFloor.getVisibility() == View.VISIBLE) {
                                            tvInstructionFloor.setText(getFloorName(currentLocationMap.getId()));
                                        }

                                        controller.fitBoundsInView(UserLocation.getInstance().getBounds());
                                        ivSearch.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                locationMeEnabled = true;
                                            }
                                        }, 100);
                                    }
                                });

                            }

                            @Override
                            public void onError(String s) {

                            }
                        });
                    } else {
                        controller.fitBoundsInView(UserLocation.getInstance().getBounds());
                    }

                    if (!locationMeEnabled) {
                        Toast.makeText(IndoorNavigationActivity.this,
                                getString(R.string.follow_me_function_enabled), Toast.LENGTH_LONG).show();

                        ivLocateMe.setImageResource(R.drawable.ic_locateme_enable);
                    }

                    ivSearch.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            locationMeEnabled = true;
                        }
                    }, 100);

                }
            }
        });
    }

    //update the current selected floor
    private void updateCurrentSelectedFloor() {
        for (int i = 0; i < floors.size(); i++) {
            Floor floor = floors.get(i);
            if (floor != null && floor.getMap() != null
                    && floor.getMap().getId() == currentLocationMap.getId()) {
                selectedFloorIndex = i;

                UserLocation.getInstance().snapToWayfindingPathWithThreshold(
                        snapThresholdInMM / currentLocationMap.getMmPerPixel());

                Logger.i("indoor", "snapThresholdInMM:" + snapThresholdInMM + "," +
                        "current Map MmPerPixel:" + currentLocationMap.getMmPerPixel());
                break;
            }
        }
    }

    private void initFloors() {
        ActiveVenue activeVenue = controller.getActiveVenue();
        floors.addAll(Arrays.asList(activeVenue.getBuildings().getAllFloors()));
        ImageView ivTitleDropdown = findViewById(R.id.iv_title_dropdown);
        LinearLayout llTitle = findViewById(R.id.ll_title);

        if (floors.size() > 1) {
            isMultiFloors = true;
            ivTitleDropdown.setVisibility(View.VISIBLE);
            llTitle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (lvFloors.getVisibility() != View.VISIBLE) {
                        lvFloors.setVisibility(View.VISIBLE);
                        llMapLabels.setVisibility(View.GONE);
                    } else {
                        lvFloors.setVisibility(View.GONE);
                        llMapLabels.setVisibility(View.VISIBLE);
                    }
                }
            });
            tvTitle.setText(String.format("%s - %s", getString(R.string.indoor_map), floors.get(0).getShortName()));
        } else {
            isMultiFloors = false;
            ivTitleDropdown.setVisibility(View.GONE);
            tvTitle.setText(getString(R.string.indoor_map));
        }
        if (floorListAdapter != null) {
            floorListAdapter.notifyDataSetChanged();
        }
    }

    private void setupHeader() {
        ImageView ivClose = findViewById(R.id.iv_close);
        ivSearch = findViewById(R.id.iv_search);

        tvTitle = findViewById(R.id.tv_title);
        lvFloors = findViewById(R.id.lv_floors);
        floorListAdapter = getFloorListAdapter();
        lvFloors.setAdapter(floorListAdapter);
        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        ivSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rlSearchRoute.setVisibility(View.VISIBLE);
                llMapLabels.setVisibility(View.GONE);
                etDestination.requestFocus();
                isOriginSelected = false;
                filterDestination();
            }
        });
    }

    private void filterDestination() {
        String searchString;
        if (isOriginSelected) {
            searchString = etOrigin.getText().toString().toLowerCase(Locale.ROOT);
        } else {
            searchString = etDestination.getText().toString().toLowerCase(Locale.ROOT);
        }

        destinationFilterList.clear();
        if (searchString.isEmpty()) {
            destinationFilterList.addAll(destinationList);
        } else {
            for (LocalDestination destination : destinationList) {
                if (destination.getName() != null && destination.getName().toLowerCase(Locale.ROOT).contains(searchString)) {
                    destinationFilterList.add(destination);
                }
            }
        }

        if (destinationListAdapter != null) {
            destinationListAdapter.notifyDataSetChanged();
        }
    }

    private void setupSearchRoute() {
        rlSearchRoute = findViewById(R.id.rl_search_route);
        tvCancelSearchRoute = findViewById(R.id.tv_cancel_search_route);
        tvSearchRoute = findViewById(R.id.tv_search_route);
        etOrigin = findViewById(R.id.et_origin);
        etDestination = findViewById(R.id.et_destination);

        etOrigin.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    isOriginSelected = true;
                    filterDestination();
                }
            }
        });
        etDestination.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    isOriginSelected = false;
                    filterDestination();
                }
            }
        });
        etOrigin.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                filterDestination();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        etDestination.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                filterDestination();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        TextView.OnEditorActionListener searchActionListener = new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                int result = actionId & EditorInfo.IME_MASK_ACTION;
                switch (result) {
                    case EditorInfo.IME_ACTION_SEARCH:
                        searchAction();
                        return true;
                }
                return false;
            }
        };
        etOrigin.setOnEditorActionListener(searchActionListener);
        etDestination.setOnEditorActionListener(searchActionListener);

        tvCancelSearchRoute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rlSearchRoute.setVisibility(View.GONE);
                llMapLabels.setVisibility(View.VISIBLE);
            }
        });

        tvSearchRoute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchAction();
            }
        });
    }

    private void hideKeyboard() {
        if (etOrigin != null && etDestination != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(etOrigin.getWindowToken(), 0);
            imm.hideSoftInputFromWindow(etDestination.getWindowToken(), 0);
        }
    }

    private void searchAction() {
        if (destinationDestination != null) {
            if (lvFloors.getVisibility() == View.VISIBLE) {
                lvFloors.setVisibility(View.GONE);
                llMapLabels.setVisibility(View.VISIBLE);
            }
            routeFromOriginToDestination();
            rlSearchRoute.setVisibility(View.GONE);
            llMapLabels.setVisibility(View.VISIBLE);
            hideKeyboard();
        } else {
            Toast.makeText(IndoorNavigationActivity.this,
                    getString(R.string.please_select_destination), Toast.LENGTH_LONG).show();
        }
    }

    private void setupRouteInstructions() {
        rlRouteInstructions = findViewById(R.id.rl_route_instructions);
        rlCurrentInstructions = findViewById(R.id.rl_current_instructions);
        llRouteInstructions = findViewById(R.id.ll_route_instructions);
        rlRouteInstructionsDetail = findViewById(R.id.rl_route_instructions_detail);
        llAudioNotificationPopup = findViewById(R.id.ll_audio_notification_popup);
        ivRouteInstructionClose = findViewById(R.id.iv_route_instruction_close);
        tvAudioNotification = findViewById(R.id.tv_audio_notification);
        ivAudioNotification = findViewById(R.id.iv_audio_notification);
        ivList = findViewById(R.id.iv_list);
        ivMute = findViewById(R.id.iv_mute);
        ivFirstInstructionClose = findViewById(R.id.iv_first_instruction_close);
        tvTotalDistance = findViewById(R.id.tv_total_distance);

        tvInstructionFloor = findViewById(R.id.tv_instruction_floor);

        lvRouteInstructions = findViewById(R.id.lv_route_instructions);
        routeInstructionListAdapter = getRouteInstructionListAdapter();
        lvRouteInstructions.setAdapter(routeInstructionListAdapter);
        ivRouteInstructionClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rlRouteInstructionsDetail.setVisibility(View.GONE);
                rlCurrentInstructions.setVisibility(View.VISIBLE);

                llMapLabels.setVisibility(View.VISIBLE);
            }
        });
        ivList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rlRouteInstructionsDetail.setVisibility(View.VISIBLE);
                rlCurrentInstructions.setVisibility(View.GONE);

                llMapLabels.setVisibility(View.GONE);
            }
        });

        ivFirstInstructionClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finishCurrentNavigation(true);
            }
        });

        if (Configurations.getBoolean("indoor_navigation_audio_announcement_enabled", true)) {
            ivMute.setImageResource(R.drawable.ic_unmute);
        } else {
            ivMute.setImageResource(R.drawable.ic_mute);
        }
        ivMute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                llAudioNotificationPopup.setVisibility(View.VISIBLE);

                boolean indoorNavigationAudioAnnouncementEnabled = Configurations.getBoolean("indoor_navigation_audio_announcement_enabled", true);
                indoorNavigationAudioAnnouncementEnabled = !indoorNavigationAudioAnnouncementEnabled;
                Configurations.setBoolean("indoor_navigation_audio_announcement_enabled", indoorNavigationAudioAnnouncementEnabled);

                if (indoorNavigationAudioAnnouncementEnabled) {
                    tvAudioNotification.setText(getString(R.string.audio_notification_on));// current state is on
                    ivMute.setImageResource(R.drawable.ic_unmute);
                    ivAudioNotification.setImageResource(R.drawable.ic_audio_notification);
                } else {
                    tvAudioNotification.setText(getString(R.string.audio_notification_off));
                    ivMute.setImageResource(R.drawable.ic_mute);
                    ivAudioNotification.setImageResource(R.drawable.ic_mutebell);

                    if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                        mediaPlayer.stop();
                    }
                    MyApplication.getInstance().alertPlaybackStatus = Constants.AUDIO_PLAYBACK_IDLE;
                    MyApplication.getInstance().audioAlertElementArray.clear();
                }
                isRouteInstructionsVisible = rlRouteInstructionsDetail.getVisibility() == View.VISIBLE;
                isCurrentInstructionsVisible = rlCurrentInstructions.getVisibility() == View.VISIBLE;
                rlRouteInstructionsDetail.setVisibility(View.GONE);
                rlCurrentInstructions.setVisibility(View.GONE);
                ivMute.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (isRouteInstructionsVisible) {
                            rlRouteInstructionsDetail.setVisibility(View.VISIBLE);
                        }
                        if (isCurrentInstructionsVisible) {
                            rlCurrentInstructions.setVisibility(View.VISIBLE);
                        }
                        llAudioNotificationPopup.setVisibility(View.GONE);
                    }
                }, 3000);
            }
        });
    }

    private FloorListAdapter getFloorListAdapter() {
        ArrayList<HashMap<String, Object>> data = new ArrayList<HashMap<String, Object>>();

        for (int i = 0; i < floors.size(); i++) {
            String destinationName = floors.get(i).getName();

            HashMap<String, Object> map = new HashMap<String, Object>();

            map.put("tv_filter_name", destinationName);

            data.add(map);
        }

        FloorListAdapter mFloorListAdapter = new FloorListAdapter(
                this, data,
                R.layout.item_route_filter_list,
                new String[]{"tv_filter_name"}, new int[]{R.id.tv_filter_name});

        return mFloorListAdapter;
    }

    class FloorListAdapter extends SimpleAdapter {
        Context mContext;
        private final LayoutInflater l_Inflater;

        public FloorListAdapter(Context context, List<? extends Map<String, ?>> data,
                                int resource, String[] from, int[] to) {
            super(context, data, resource, from, to);

            mContext = context;
            l_Inflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return floors.size();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            String destinationName = floors.get(position).getName();

            if (convertView == null) {
                convertView = l_Inflater.inflate(
                        R.layout.item_route_filter_list, null);
            }

            TextView tvFilterName = convertView.findViewById(R.id.tv_filter_name);
            ImageView ivFilterSelected = convertView.findViewById(R.id.iv_filter_selected);

            tvFilterName.setText(destinationName);

            if (position == selectedFloorIndex) {
                ivFilterSelected.setVisibility(View.VISIBLE);
            } else {
                ivFilterSelected.setVisibility(View.INVISIBLE);
            }

            convertView.setTag(position);
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (locationMeEnabled) {
                        Toast.makeText(IndoorNavigationActivity.this,
                                getString(R.string.follow_me_function_disabled), Toast.LENGTH_LONG).show();
                    }
                    ivLocateMe.setImageResource(R.drawable.ic_locateme_disable);
                    locationMeEnabled = false;
                    int position = (Integer) view.getTag();
                    if (position < floors.size()) {
                        selectedFloorIndex = position;
                        Floor floor = floors.get(position);
                        if (floor != null) {
                            controller.showMap(floor.getMap(), new JController.OnMapShownCallback() {
                                @Override
                                public void onBeforeMapShown(MapDrawable mapDrawable) {

                                }

                                @Override
                                public void onMapShown() {
                                    currentLocationMap = controller.getCurrentMap();
                                    UserLocation.getInstance().snapToWayfindingPathWithThreshold(
                                            snapThresholdInMM / currentLocationMap.getMmPerPixel());

                                    Logger.i("indoor", "snapThresholdInMM:" + snapThresholdInMM +
                                            ",current Map MmPerPixel:" + currentLocationMap.getMmPerPixel());
                                }

                                @Override
                                public void onError(String s) {

                                }
                            });

                            if (tvTitle != null) {
                                tvTitle.setText(String.format("%s - %s", getString(R.string.indoor_map), floor.getShortName()));
                            }
                        }
                    }

                    if (floorListAdapter != null) {
                        floorListAdapter.notifyDataSetChanged();
                    }

                    lvFloors.setVisibility(View.GONE);

                    llMapLabels.setVisibility(View.VISIBLE);
                }
            });
            return convertView;
        }
    }

    private RouteInstructionListAdapter getRouteInstructionListAdapter() {
        ArrayList<HashMap<String, Object>> data = new ArrayList<HashMap<String, Object>>();

        for (int i = 0; i < instructions.size(); i++) {
            String routeInstructionName = instructions.get(i).getDirection();

            HashMap<String, Object> map = new HashMap<String, Object>();

            map.put("tv_instruction_direction", routeInstructionName);

            data.add(map);
        }

        RouteInstructionListAdapter mRouteInstructionListAdapter = new RouteInstructionListAdapter(
                this, data,
                R.layout.item_indoor_route_instructions,
                new String[]{"tv_instruction_direction"}, new int[]{R.id.tv_instruction_direction});

        return mRouteInstructionListAdapter;
    }

    class RouteInstructionListAdapter extends SimpleAdapter {
        Context mContext;
        private final LayoutInflater l_Inflater;

        public RouteInstructionListAdapter(Context context, List<? extends Map<String, ?>> data,
                                           int resource, String[] from, int[] to) {
            super(context, data, resource, from, to);

            mContext = context;
            l_Inflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return instructions.size();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Instruction routeInstruction = instructions.get(position);
            String routeInstructionDirection = routeInstruction.getDirection();

            if (convertView == null) {
                convertView = l_Inflater.inflate(
                        R.layout.item_indoor_route_instructions, null);
            }

            TextView tvInstructionDirection = convertView.findViewById(R.id.tv_instruction_direction);
            ImageView ivInstructionDirection = convertView.findViewById(R.id.iv_instruction_direction);
            TextView tvInstructionLevel = convertView.findViewById(R.id.tv_instruction_level);
            TextView tvInstructionDistance = convertView.findViewById(R.id.tv_instruction_distance);

            if (position < (instructions.size() - 1)) {
                tvInstructionDirection.setText(routeInstruction.getText());

                if (Utilities.indoorDirectionIconMapping.containsKey(routeInstructionDirection.toLowerCase(Locale.ROOT))) {
                    ivInstructionDirection.setImageResource(Utilities.indoorDirectionIconMapping.get(routeInstructionDirection.toLowerCase(Locale.ROOT)));
                } else {
                    ivInstructionDirection.setImageResource(R.drawable.ic_indoor_straight);
                }
                if (isMultiFloors) {
                    tvInstructionLevel.setVisibility(View.VISIBLE);
                    String floorShortName = null;
                    if (routeInstruction.getCompletionPoint() != null) {
                        floorShortName = getFloorShortName(routeInstruction.getCompletionPoint().getMapId());
                    }
                    if (floorShortName == null && routeInstruction.getDecisionPoint() != null) {
                        floorShortName = getFloorShortName(routeInstruction.getDecisionPoint().getMapId());
                    }
                    if (floorShortName != null) {
                        tvInstructionLevel.setText(floorShortName);
                    }
                } else {
                    tvInstructionLevel.setVisibility(View.GONE);
                }

            } else {
                tvInstructionDirection.setText(getString(R.string.arrive_at_destination));
                ivInstructionDirection.setImageResource(R.drawable.ic_indoor_location);
                tvInstructionLevel.setVisibility(View.GONE);
            }

            tvInstructionDistance.setText(getString(R.string.distance_with_value,
                    controller.getCurrentMap().getMmPerPixel() * routeInstruction.getPixelDistance()
                            * Constants.METER_TO_FOOT / 1000));
            return convertView;
        }
    }

    private DestinationListAdapter getDestinationListAdapter() {
        ArrayList<HashMap<String, Object>> data = new ArrayList<HashMap<String, Object>>();

        for (int i = 0; i < destinationFilterList.size(); i++) {
            String destinationName = destinationFilterList.get(i).getName();

            HashMap<String, Object> map = new HashMap<String, Object>();

            map.put("tv_destination_name", destinationName);

            data.add(map);
        }

        DestinationListAdapter mDestinationListAdapter = new DestinationListAdapter(
                this, data,
                R.layout.item_indoor_route_search,
                new String[]{"tv_destination_name"}, new int[]{R.id.tv_destination_name});

        return mDestinationListAdapter;
    }

    class DestinationListAdapter extends SimpleAdapter {
        Context mContext;
        private final LayoutInflater l_Inflater;

        public DestinationListAdapter(Context context, List<? extends Map<String, ?>> data,
                                      int resource, String[] from, int[] to) {
            super(context, data, resource, from, to);

            mContext = context;
            l_Inflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return destinationFilterList.size();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LocalDestination destination = destinationFilterList.get(position);
            String destinationName = destination.getName();

            if (convertView == null) {
                convertView = l_Inflater.inflate(
                        R.layout.item_indoor_route_search, null);
            }

            TextView tvDestinationName = convertView.findViewById(R.id.tv_destination_name);

            String floorShortName = null;
            if (isMultiFloors) {
                if (destination.getWaypoints() != null && destination.getWaypoints().length > 0) {
                    Waypoint waypoint = destination.getWaypoints()[0];
                    if (waypoint != null) {
                        floorShortName = getFloorShortName(waypoint.getMapId());
                    }
                }
            }

            if (floorShortName != null) {
                tvDestinationName.setText(String.format("%s - %s", destinationName, floorShortName));
            } else {
                tvDestinationName.setText(destinationName);
            }

            convertView.setTag(position);
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = (Integer) view.getTag();
                    if (position < destinationFilterList.size()) {
                        selectedDestinationIndex = position;
                    }

                    if (destinationListAdapter != null) {
                        destinationListAdapter.notifyDataSetChanged();
                    }

                    if (isOriginSelected) {
                        originDestination = destinationFilterList.get(position);
                        etOrigin.setText(originDestination.getName());

                        etOrigin.post(new Runnable() {
                            @Override
                            public void run() {
                                etOrigin.setSelection(etOrigin.getText().length());
                            }
                        });
                    } else {
                        destinationDestination = destinationFilterList.get(position);
                        //editText.setText(destination.getName());
                        etDestination.setText(destinationDestination.getName());
                        //selectADestination(destinationFilterList.get(position));
                        //routeFromOriginToDestination();
                        etDestination.post(new Runnable() {
                            @Override
                            public void run() {
                                etDestination.setSelection(etDestination.getText().length());
                            }
                        });
                    }

                }
            });
            return convertView;
        }
    }

    private com.jibestream.jmapandroidsdk.components.Map getCurrentFloorMap(int floorID) {
        ActiveVenue activeVenue = controller.getActiveVenue();
        Floor[] floors = activeVenue.getBuildings().getAllFloors();
        FloorCollection floorCollection = new FloorCollection(floors);
        Floor floorSelected = floorCollection.getById(floorID);//7908
        if (floorSelected != null) {
            Log.d("Floor", floorSelected.toString());
            return floorSelected.getMap();
        } else {
            return null;
        }
    }

    private String getFloorShortName(int mapID) {
        for (Floor floor : floors) {
            if (floor != null && floor.getMap() != null && floor.getMap().getId() == mapID) {
                return floor.getShortName();
            }
        }
        return null;
    }

    private String getFloorName(int mapID) {
        for (Floor floor : floors) {
            if (floor != null && floor.getMap() != null && floor.getMap().getId() == mapID) {
                return floor.getName();
            }
        }
        return null;
    }

    private void renderMapUiComponents() {
        //setup floor selector
        FloorSelectorOptions floorSelectorOptions = new FloorSelectorOptions();
        floorSelectorOptions.setVertical(true);

        //setup compass
        CompassOptions compassOptions = new CompassOptions();
        compassOptions.setResetRotationOnTap(true);

        //Certain properties that can be applied on Compass Options:
        compassOptions.setIcon(R.drawable.ic_compass);
        compassOptions.setRotatedIcon(R.drawable.ic_rotated_compass);
        //setup search bar
        Search.OnItemSelectListener onItemClickListener = new Search.OnItemSelectListener() {
            @Override
            public void onItemClick(BaseModel o) {
                //get destination by id
                selectADestination((Destination) o);
            }
        };

        final SearchOptions searchOptions = new SearchOptions(null);
        searchOptions.setSearchArray(controller.getActiveVenue().getDestinations().getAll());
        searchOptions.setMaxResults(5);
        searchOptions.setHint("Hint");

        Destination[] itemDestinations = controller.getActiveVenue().getDestinations().getAll();
        for (Destination destination : itemDestinations) {
            if (destination != null) {
                LocalDestination localDestination = new LocalDestination();
                localDestination.setName(destination.getName());
                localDestination.setWaypoints(destination.getWaypoints());

                destinationList.add(localDestination);
            }
        }
        destinationListAdapter.notifyDataSetChanged();

        Search search = mapUiKit.renderSearch(searchOptions, onItemClickListener);
        editText = search.findViewById(R.id.search_src_text);

        search.setVisibility(View.GONE);
        Compass compass = mapUiKit.renderCompass(compassOptions);

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);

        compass.setLayoutParams(params);
    }

    private void checkInstructionAndUpdateSimulation(int mapID, float x, float y) {

        if (currentInstructionIndex < instructions.size()) {
            Instruction instruction = instructions.get(currentInstructionIndex);
            if (instruction.getCompletionPoint() != null) {
                Waypoint waypoint = instruction.getCompletionPoint();
                if (waypoint.getCoordinates() != null
                        && waypoint.getCoordinates().length == 2
                        && Math.abs(x - waypoint.getCoordinates()[0]) < 1.0f
                        && Math.abs(y - waypoint.getCoordinates()[1]) < 1.0f) {
                    currentInstructionIndex++;
                    if (currentInstructionIndex < instructions.size()) {
                        Instruction newInstruction = instructions.get(currentInstructionIndex);
                        if (newInstruction.getCompletionPoint() != null) {
                            Waypoint newWaypoint = newInstruction.getCompletionPoint();
                            if (newWaypoint.getCoordinates() != null
                                    && newWaypoint.getCoordinates().length == 2
                                    && Math.abs(x - newWaypoint.getCoordinates()[0]) < 1.0f
                                    && Math.abs(y - newWaypoint.getCoordinates()[1]) < 1.0f) {
                                currentInstructionIndex++;
                            }
                        }
                    }
                    refreshCurrentInstructions();
                }
            }
        }

    }

    private void refreshCurrentLocationAndUpdateInstructionSimulation() {

        if (pathPerFloors != null) {
            if (simulateUserMoving) {
                if (currentPathPerFloorIndex < pathPerFloors.length) {
                    int mapID = pathPerFloors[currentPathPerFloorIndex].mapId;
                    com.jibestream.jmapandroidsdk.components.Map map = controller.getCurrentMap();
                    if (isMultiFloors) {
                        for (Floor floor : floors) {
                            if (floor != null && floor.getMap() != null && floor.getMap().getId() == mapID) {
                                map = floor.getMap();
                                break;
                            }
                        }
                    }

                    if (currentPointOnFloorIndex < pathPerFloors[currentPathPerFloorIndex].points.length) {
                        currentASNode = pathPerFloors[currentPathPerFloorIndex].points[currentPointOnFloorIndex];
                        {
                            if (map != null) {
                                PointF currentLocation = new PointF((float) currentASNode.x, (float) currentASNode.y);
                                userLocationWaypoint = controller.getActiveVenue().getClosestWaypointToCoordinatesOnMap(
                                        currentLocation, map);
                                controller.updateUserLocationPosition(currentLocation,
                                        0, 100, map);
                            }
                        }
                        checkInstructionAndUpdateSimulation(mapID, (float) currentASNode.x, (float) currentASNode.y);

                        if (map != null && map != controller.getCurrentMap()) {
                            controller.showMap(map, new JController.OnMapShownCallback() {
                                @Override
                                public void onBeforeMapShown(MapDrawable mapDrawable) {

                                }

                                @Override
                                public void onMapShown() {
                                    IndoorNavigationActivity.this.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (Configurations.getConfigurations().getBoolean("enable_indoor_reroute", false)) {
                                                if (!pauseDeviateRouteChecking) {
                                                    if (navigationKit.hasUserVeeredOffRoute(pathPerFloors, 100f)) {
                                                        //UserLocation.getInstance().shouldAutoRerouteWayfindingPath();
                                                        pauseDeviateRouteChecking = true;
                                                        simulateUserMoving = false;
                                                        llOffRoutePopup.setVisibility(View.VISIBLE);
                                                    }
                                                }
                                            }
                                        }
                                    });
                                }

                                @Override
                                public void onError(String s) {

                                }
                            });
                        } else {
                            if (Configurations.getConfigurations().getBoolean("enable_indoor_reroute", false)) {
                                if (!pauseDeviateRouteChecking) {
                                    if (navigationKit.hasUserVeeredOffRoute(pathPerFloors, 100f)) {
                                        //UserLocation.getInstance().shouldAutoRerouteWayfindingPath();
                                        pauseDeviateRouteChecking = true;
                                        simulateUserMoving = false;
                                        llOffRoutePopup.setVisibility(View.VISIBLE);
                                    }
                                }
                            }
                        }
                    }

                    currentPointOnFloorIndex++;
                    if (currentPointOnFloorIndex >= pathPerFloors[currentPathPerFloorIndex].points.length) {
                        currentPointOnFloorIndex = 0;
                        currentPathPerFloorIndex++;
                    }

                    if (currentPathPerFloorIndex >= pathPerFloors.length) {
                        //Arrived
                        speakOutWithMessage(getString(R.string.you_have_arrived));
                        Toast.makeText(IndoorNavigationActivity.this, getString(R.string.you_have_arrived), Toast.LENGTH_LONG).show();
                        gHandler.removeCallbacks(gRunnable);
                    } else {
                        gHandler.postDelayed(gRunnable, refreshInterval);
                    }
                }

            }
        }

    }

    private void rerouteAction() {
        Waypoint originWaypoint = userLocationWaypoint;
        Waypoint destinationWaypoint = destinationDestination.getWaypoints()[0];

        //wayfind between user location and destination
        if (isAccessible()) {
            pathPerFloors = controller.wayfindBetweenWaypoints(originWaypoint, destinationWaypoint);
        } else {
            pathPerFloors = controller.wayfindBetweenWaypoints(originWaypoint, destinationWaypoint, 0);
        }

        commonRouteAction();
    }

    private void commonRouteAction() {
        currentPathPerFloorIndex = 0;
        currentPointOnFloorIndex = 0;

        //clear any previous maps
        controller.clearWayfindingPath();
        //draw wayfind path
        controller.drawWayfindingPath(pathPerFloors);
        JStyle jStyle1 = new JStyle();
        jStyle1.setColor(getColor(R.color.colorIndoorLine));
        jStyle1.setStrokeColor(getColor(R.color.colorIndoorLine));
        jStyle1.setStrokeWidth(3);

        JStyle jStyle2 = new JStyle();
        jStyle2.setColor(getColor(R.color.colorPrimary));
        jStyle2.setStrokeWidth(3);

        JStyle jStyle3 = new JStyle();
        jStyle3.setColor(Color.BLACK);
        jStyle3.setStrokeWidth(3);
        jStyle3.setTextSize(3f);

        if (navigationKit == null) {
            navigationKit = new NavigationKit(controller);
        }

        navigationKit.drawPathWithInstructions(pathPerFloors, jStyle1, jStyle2, jStyle3);

        instructions.clear();
        instructions.addAll(Arrays.asList(navigationKit.createInstructionsFromPaths(pathPerFloors)));
        currentInstructionIndex = 0;

        totalDistance = 0.0f;
        for (Instruction instruction : instructions) {
            totalDistance = totalDistance + instruction.getPixelDistance();
            Logger.i("Nav", "instruction:" + instruction.getDirection());
        }
        totalDistance = totalDistance * controller.getCurrentMap().getMmPerPixel()
                * Constants.METER_TO_FOOT / 1000;
        tvTotalDistance.setText(getString(R.string.distance_with_value, totalDistance));
        refreshCurrentInstructions();
        if (routeInstructionListAdapter != null) {
            routeInstructionListAdapter.notifyDataSetChanged();
        }

        controller.zoomToPathOnMap(controller.getCurrentMap(), controller.getCurrentMap().getWidth() / 10, 500, null);

        setMapLabelsLayoutLocation(true);
        rlRouteInstructions.setVisibility(View.VISIBLE);
        simulateUserMoving = true;
        pauseDeviateRouteChecking = false;
        deviationPopupCount = 0;
        pauseCheckingExit = false;
        checkCurrentDestinationIsExit();
    }

    private void deviationPopupChecking() {
        if (deviationPopupCount < deviationPopupCountMax) {
            if (deviationPopupWaitTimeAccumulate) {
                gDeviationCheckHandler.postDelayed(gDeviationCheckRunnable,
                        (long) deviationPopupWaitTimeSeconds * deviationPopupCount * 1000);
            } else {
                gDeviationCheckHandler.postDelayed(gDeviationCheckRunnable, deviationPopupWaitTimeSeconds * 1000L);
            }
        }
    }

    private void setupDeviateRoutePopup() {
        llOffRoutePopup = findViewById(R.id.ll_off_route_popup);
        ivCancelReroute = findViewById(R.id.iv_cancel_reroute);
        ivConfirmReroute = findViewById(R.id.iv_confirm_reroute);

        ivCancelReroute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                llOffRoutePopup.setVisibility(View.GONE);
                simulateUserMoving = true;

                deviationPopupChecking();
            }
        });
        ivConfirmReroute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Configurations.getConfigurations().getBoolean("enable_indoor_reroute", false)) {
                    llOffRoutePopup.setVisibility(View.GONE);
                    gDeviationCheckHandler.removeCallbacks(gDeviationCheckRunnable);
                    if (pathPerFloors != null) {
                        navigationKit.clearPathWithInstructions(pathPerFloors);
                        pathPerFloors = null;
                    }
                    rerouteAction();
                    pauseDeviateRouteChecking = false;
                    simulateUserMoving = true;
                }
            }
        });
    }

    private boolean isAccessible() {
        if (Utilities.isLoggedIn()) {
            UserPreferences userPreferences = MyApplication.getInstance().userPreferences;
            if (userPreferences != null) {
                MobilityProfile mobilityProfile = userPreferences.getMobilityProfile();
                if (mobilityProfile != null) {
                    ArrayList<String> mobilityDevices = mobilityProfile.getMobilityDevices();
                    for (String mobilityDevice : mobilityDevices) {
                        if (mobilityDevice != null && !mobilityDevice.equalsIgnoreCase("No Assistive Device")) {
                            return false;
                        }
                    }

                    String visionLimitation = mobilityProfile.getVisionLimitation();

                    return visionLimitation == null || visionLimitation.equalsIgnoreCase("none");
                }
            }
        }

        return true;
    }

    private void routeFromOriginToDestination() {
        if (destinationDestination == null) {
            return;
        }
        //extract destination's first waypoint
        Waypoint originWaypoint = userLocationWaypoint;
        Waypoint destinationWaypoint = destinationDestination.getWaypoints()[0];

        if (originDestination != null) {
            originWaypoint = originDestination.getWaypoints()[0];
        }

        //wayfind between user location and destination
        if (isAccessible()) {
            pathPerFloors = controller.wayfindBetweenWaypoints(originWaypoint, destinationWaypoint);
        } else {
            pathPerFloors = controller.wayfindBetweenWaypoints(originWaypoint, destinationWaypoint, 0);
        }

        commonRouteAction();
    }

    private void playCurrentInstruction() {
        if (currentInstructionIndex < instructions.size() - 1) {
            Instruction instruction = instructions.get(currentInstructionIndex);
            if (instruction != null) {
                speakOutWithMessage(instruction.getText());
            }
        }

    }

    private void refreshCurrentInstructions() {
        tvInstructionFloor.setVisibility(View.VISIBLE);
        if ((instructions.size() - currentInstructionIndex) >= 1) {
            Instruction firstInstruction = instructions.get(currentInstructionIndex);

            if (isMultiFloors) {
                int mapID = 0;
                if (firstInstruction.getOriginPoint() != null) {
                    mapID = firstInstruction.getOriginPoint().getMapId();
                } else if (firstInstruction.getDecisionPoint() != null) {
                    mapID = firstInstruction.getDecisionPoint().getMapId();
                } else if (firstInstruction.getCompletionPoint() != null) {
                    mapID = firstInstruction.getCompletionPoint().getMapId();
                }

                for (Floor floor : floors) {
                    if (floor != null && floor.getMap() != null && floor.getMap().getId() == mapID) {
                        tvInstructionFloor.setText(floor.getName());
                        break;
                    }
                }
            } else {
                tvInstructionFloor.setText(getString(R.string.indoor_map));
            }
        } else {
            finishCurrentNavigation(false);
        }
    }

    private void showArrivalPopupDialog(String message) {
        if (arrivalPopupDialog == null) {
            arrivalPopupDialog = new IndoorNavigationCustomizePopupDialog(IndoorNavigationActivity.this,
                    getString(R.string.attention), message,
                    getString(R.string.ok), null, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    arrivalPopupDialog.dismiss();
                    arrivalPopupDialog = null;
                }
            }, null, false);
        }
        if (!arrivalPopupDialog.isShowing()) {
            arrivalPopupDialog.show();
        }
    }

    private void finishCurrentNavigation(boolean cancelled) {
        if (!cancelled) {
            String message = getString(R.string.you_have_arrived_your_destination);
            speakOutWithMessage(message);

            if (isCurrentDestinationExit && !pauseCheckingExit) {
                llArriveExitPopup.setVisibility(View.VISIBLE);
            }
        } else {
            isCurrentDestinationExit = false;
            rlRouteInstructions.setVisibility(View.GONE);
            setMapLabelsLayoutLocation(false);

            if (pathPerFloors != null) {
                navigationKit.clearPathWithInstructions(pathPerFloors);
            }
            if (gHandler != null) {
                gHandler.removeCallbacks(gRunnable);
            }
            if (gDeviationCheckHandler != null) {
                gDeviationCheckHandler.removeCallbacks(gDeviationCheckRunnable);
            }
            stopTTS();
            pathPerFloors = null;
        }
    }

    private void selectADestination(Destination destination) {

        //extract destination's first waypoint
        Waypoint destinationWaypoint = destination.getWaypoints()[0];

        //wayfind between user location and destination
        PathPerFloor[] pathPerFloors = controller.wayfindBetweenWaypoints(userLocationWaypoint, destinationWaypoint);

        //clear any previous maps
        controller.clearWayfindingPath();

        //controller.clearMapDrawables();
        controller.removeComponent(null, controller.getCurrentMap());

        //draw wayfind path
        controller.drawWayfindingPath(pathPerFloors);
        JStyle jStyle1 = new JStyle();
        jStyle1.setColor(Color.RED);
        jStyle1.setStrokeWidth(6);

        JStyle jStyle2 = new JStyle();
        jStyle2.setColor(Color.GREEN);
        jStyle2.setStrokeWidth(6);

        JStyle jStyle3 = new JStyle();
        jStyle3.setColor(Color.BLUE);
        jStyle3.setStrokeWidth(6);

        NavigationKit navigationKit = new NavigationKit(controller);

        navigationKit.drawPathWithInstructions(pathPerFloors, jStyle1, jStyle2, jStyle3);

        Instruction[] instructions = navigationKit.createInstructionsFromPaths(pathPerFloors);
        for (Instruction instruction : instructions) {
            Log.i("Nav", "instruction:" + instruction.getDirection());
        }

        controller.zoomToPathOnMap(controller.getCurrentMap(), controller.getCurrentMap().getWidth() / 10, 500, null);
    }

    @Override
    protected void onResume() {
        super.onResume();
        onPaused = false;
        stage.getMapView().resumeMap();
        if (gStartedUserLocationTimer) {
            refreshUserLocationByInterval();
            gUserLocationHandler.postDelayed(gUserLocationRunnable, refreshUserLocationInterval);
        }

        if (Utilities.isLoggedIn()) {
            getFromToken();
        }

    }

    public void getFromToken() {
        Response.Listener listener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response != null && !response.isEmpty()) {
                    Gson gson = new Gson();
                    try {
                        MyApplication.getInstance().userPreferences = gson.fromJson(response, UserPreferences.class);

                        Configurations.setString("account_user_preferences", response);
                    } catch (Exception e) {

                    }
                } else {
                    Logger.d("account", "data is not correct:" + response);
                }

            }
        };
        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Logger.e("account", error.toString());
            }
        };

        String url = Resource.API_ACCOUNT_FROM_TOKEN;

        volleyAccountStringRequest(Request.Method.GET, url, "", listener, errorListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        onPaused = true;

        if (gUserLocationHandler != null) {
            gUserLocationHandler.removeCallbacks(gUserLocationRunnable);
        }

        try {
            stage.getMapView().pauseMap();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onTap(MotionEvent motionEvent, Object o) {
    }

    public void drawPopup(Destination destination) {
        if (thePopup != null && controller != null) {
            controller.removeComponent(thePopup, controller.getCurrentMap());
            thePopup = null;
        }
        Waypoint destinationWaypoint = destination.getWaypoints()[0];

        PopupDrawableOptions popupOptions = new PopupDrawableOptions(destination.getName());
        popupOptions.setCoordinates(new PointF(destinationWaypoint.getCoordinates()[0].floatValue(), destinationWaypoint.getCoordinates()[1].floatValue()));
        popupOptions.setSubText("Id = " + destination.id.toString());
        popupOptions.setActionButtonText("GO");

        popupOptions.setShowActionButton(false);

        popupOptions.setPopupStyle(new JStyle(Color.WHITE));

        JStyle subTextStyle = new JStyle(Color.BLACK);
        popupOptions.setSubTextStyle(subTextStyle);
        popupOptions.setTitleTextStyle(new JStyle(Color.BLACK));

        popupOptions.setActionButtonStyle(new JStyle(Color.parseColor("#32AABA")));
        popupOptions.setActionButtonTextStyle(new JStyle(Color.WHITE));

        popupOptions.setMetaData(destination);
        thePopup = mapUiKit.renderPopup(popupOptions, new PointF(destinationWaypoint.getCoordinates()[0].floatValue(), destinationWaypoint.getCoordinates()[1].floatValue()));
        thePopup.setCounterScale(true);

    }

    private void setupExtendSDKLogFeature() {
        LinearLayout llLog = findViewById(R.id.ll_extend_log);
        if (Configurations.getBoolean("enable_extend_sdk_log_feature", false)) {
            llLog.setVisibility(View.VISIBLE);
            ivUpload = findViewById(R.id.iv_upload);
            ivDelete = findViewById(R.id.iv_delete);
            ivEnableLog = findViewById(R.id.iv_enable_disable_log);

            if (Configurations.getBoolean("extend_sdk_log_enabled", false)) {
                ivEnableLog.setImageResource(R.drawable.ic_disable);
            } else {
                ivEnableLog.setImageResource(R.drawable.ic_enable);
            }

            ivEnableLog.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String message;
                    boolean log_enabled = Configurations.getBoolean("extend_sdk_log_enabled", false);
                    log_enabled = !log_enabled;
                    if (log_enabled) {
                        ivEnableLog.setImageResource(R.drawable.ic_disable);
                        message = getString(R.string.enable_log);
                    } else {
                        message = getString(R.string.disable_log);
                        ivEnableLog.setImageResource(R.drawable.ic_enable);
                    }

                    Configurations.setBoolean("extend_sdk_log_enabled", log_enabled);
                    if (_extend != null) {
                        _extend.setLogWriting(log_enabled);
                    }
                    enableLogPopupDialog = new IndoorNavigationCustomizePopupDialog(IndoorNavigationActivity.this,
                            getString(R.string.attention), message,
                            getString(R.string.ok), null, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            enableLogPopupDialog.dismiss();
                            enableLogPopupDialog = null;
                        }
                    }, null, true);
                    enableLogPopupDialog.show();
                }
            });

            ivUpload.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    uploadLogPopupDialog = new IndoorNavigationCustomizePopupDialog(IndoorNavigationActivity.this,
                            getString(R.string.attention), getString(R.string.upload_log_confirmation_message),
                            getString(R.string.yes), getString(R.string.cancel), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            uploadLogPopupDialog.dismiss();
                            uploadLogPopupDialog = null;
                            if (_extend != null) {
                                _extend.uploadLogs();
                            }

                            uploadedLogPopupDialog = new IndoorNavigationCustomizePopupDialog(IndoorNavigationActivity.this,
                                    getString(R.string.attention), getString(R.string.successfully_uploaded_log),
                                    getString(R.string.ok), null, new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    uploadedLogPopupDialog.dismiss();
                                    uploadedLogPopupDialog = null;
                                }
                            }, null, true);
                            uploadedLogPopupDialog.show();
                        }
                    }, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            uploadLogPopupDialog.dismiss();
                            uploadLogPopupDialog = null;
                        }
                    }, true);
                    uploadLogPopupDialog.show();
                }
            });

            ivDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteLogPopupDialog = new IndoorNavigationCustomizePopupDialog(IndoorNavigationActivity.this,
                            getString(R.string.attention), getString(R.string.delete_log_confirmation_message),
                            getString(R.string.yes), getString(R.string.cancel), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            deleteLogPopupDialog.dismiss();
                            deleteLogPopupDialog = null;
                            if (_extend != null) {
                                _extend.deleteLogs();
                            }

                            deletedLogPopupDialog = new IndoorNavigationCustomizePopupDialog(IndoorNavigationActivity.this,
                                    getString(R.string.attention), getString(R.string.successfully_deleted_log),
                                    getString(R.string.ok), null, new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    deletedLogPopupDialog.dismiss();
                                    deletedLogPopupDialog = null;
                                }
                            }, null, true);
                            deletedLogPopupDialog.show();
                        }
                    }, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            deleteLogPopupDialog.dismiss();
                            deleteLogPopupDialog = null;
                        }
                    }, true);
                    deleteLogPopupDialog.show();
                }
            });
        } else {
            llLog.setVisibility(View.GONE);
        }
    }

    private void showMainEntrancePopupDialog(String title, String message) {
        if (mainEntrancePopupDialog == null) {
            int margin = Convert.dpToPx(10);
            DisplayMetrics gDm = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(gDm);
            mainEntrancePopupDialog = new IndoorNavigationCustomizePopupDialog(IndoorNavigationActivity.this,
                    title, message,
                    getString(R.string.ok), null, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mainEntrancePopupDialog.dismiss();
                    mainEntrancePopupDialog = null;

                    stopTTS();
                }
            }, null, gDm.widthPixels - 2 * margin, -1, false);
        }
        if (!mainEntrancePopupDialog.isShowing()) {
            mainEntrancePopupDialog.show();
        }
    }

    private void checkMainEntranceAndPopup() {
        if (!mainEntrancePopupShown) {
            int venueID = 0;
            double latitude = 0;
            double longitude = 0;
            String outdoorLocationDescription = "unknown";
            if (MyApplication.getInstance().currentLocation != null) {
                latitude = MyApplication.getInstance().currentLocation.getLatitude();
                longitude = MyApplication.getInstance().currentLocation.getLongitude();
                outdoorLocationDescription = latitude + ", " + longitude;
            } else if (locationUpdated) {
                latitude = _latitude;
                longitude = _longitude;
            }

            StringBuilder description = new StringBuilder("\noutdoor location: " + outdoorLocationDescription +
                    "\nindoor location: " + _latitude + ", " + _longitude);

            int radiusInMeters = Configurations.getInt("main_entrance_detection_radius_meters", 10);
            if (MyApplication.getInstance().currentJmapTriggerableLocation != null) {
                venueID = MyApplication.getInstance().currentJmapTriggerableLocation.getVenueId();
            }

            ArrayList<IndoorMainEntrance> indoorMainEntrances = MyApplication.getInstance().
                    getIndoorMainEntrancesInfo();
            if (indoorMainEntrances != null && !indoorMainEntrances.isEmpty()) {
                for (IndoorMainEntrance indoorMainEntrance : indoorMainEntrances) {
                    if (indoorMainEntrance != null && (venueID == 0 || indoorMainEntrance.getVenueId() == venueID)) {
                        double distance = Utilities.getDistance(
                                latitude,
                                longitude,
                                indoorMainEntrance.getMainLatitude(), indoorMainEntrance.getMainLongitude()
                        );

                        description.append("\ndistance:");
                        description.append(distance);
                        int radiusInMeter;
                        if (indoorMainEntrance.getRadiusInMeter() != null) {
                            radiusInMeter = indoorMainEntrance.getRadiusInMeter();
                        } else {
                            radiusInMeter = radiusInMeters;
                        }
                        if (distance <= radiusInMeter) {
                            String message = indoorMainEntrance.getPopupMessage();
                            String title = indoorMainEntrance.getPopupTitle();
                            showMainEntrancePopupDialog(title, message);
                            ttsForMainEntrancePopup = true;
                            mainEntrancePopupShown = true;
                            speakOutWithMessage(title + ". " + message);
                            break;
                        }
                    }
                }
            }
        }
    }

}
