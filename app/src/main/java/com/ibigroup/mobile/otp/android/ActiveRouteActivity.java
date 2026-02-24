package com.ibigroup.mobile.otp.android;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static com.ibigroup.mobile.otp.android.utility.Convert.dpToPx;
import static com.ibigroup.mobile.otp.android.utility.ServerDelegate.volleyAccountStringRequestExt;
import static com.ibigroup.mobile.otp.android.utility.ServerDelegate.volleyTrackingStringRequest;
import static com.mapbox.mapboxsdk.style.expressions.Expression.get;
import static com.mapbox.mapboxsdk.style.expressions.Expression.has;
import static com.mapbox.mapboxsdk.style.expressions.Expression.not;
import static com.mapbox.mapboxsdk.style.layers.Property.NONE;
import static com.mapbox.mapboxsdk.style.layers.Property.SYMBOL_Z_ORDER_AUTO;
import static com.mapbox.mapboxsdk.style.layers.Property.VISIBLE;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconAllowOverlap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconIgnorePlacement;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconImage;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.symbolZOrder;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.visibility;

import android.Manifest;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.provider.Settings;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ImageSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.work.BackoffPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.maps.android.SphericalUtil;
import com.ibigroup.mobile.otp.android.api.Resource;
import com.ibigroup.mobile.otp.android.customize.CustomizeMarkerView;
import com.ibigroup.mobile.otp.android.customize.IndoorNavigationCustomizePopupDialog;
import com.ibigroup.mobile.otp.android.json.AudioAlertElement;
import com.ibigroup.mobile.otp.android.json.EndTrackingRequest;
import com.ibigroup.mobile.otp.android.json.JmapTriggerableLocation;
import com.ibigroup.mobile.otp.android.json.LocationPoint;
import com.ibigroup.mobile.otp.android.json.RerouteResponse;
import com.ibigroup.mobile.otp.android.json.ServiceAlert;
import com.ibigroup.mobile.otp.android.json.StartTrackingResponse;
import com.ibigroup.mobile.otp.android.json.TrackRequest;
import com.ibigroup.mobile.otp.android.json.TrackResponse;
import com.ibigroup.mobile.otp.android.json.TrackingLocation;
import com.ibigroup.mobile.otp.android.json.TransitStop;
import com.ibigroup.mobile.otp.android.json.itinerary.IntermediateStop;
import com.ibigroup.mobile.otp.android.json.itinerary.Itinerary;
import com.ibigroup.mobile.otp.android.json.itinerary.ItineraryLeg;
import com.ibigroup.mobile.otp.android.json.itinerary.ItineraryLegGeometry;
import com.ibigroup.mobile.otp.android.json.itinerary.ItineraryLegPoint;
import com.ibigroup.mobile.otp.android.json.itinerary.ItineraryLegStep;
import com.ibigroup.mobile.otp.android.json.trip.MonitoredTrip;
import com.ibigroup.mobile.otp.android.manager.LocationUpdatesService;
import com.ibigroup.mobile.otp.android.manager.LocationWork;
import com.ibigroup.mobile.otp.android.manager.NewLocationManager;
import com.ibigroup.mobile.otp.android.manager.TransitStopManager;
import com.ibigroup.mobile.otp.android.setup.OTPContext;
import com.ibigroup.mobile.otp.android.utility.Configurations;
import com.ibigroup.mobile.otp.android.utility.Constants;
import com.ibigroup.mobile.otp.android.utility.Convert;
import com.ibigroup.mobile.otp.android.utility.Logger;
import com.ibigroup.mobile.otp.android.utility.Utilities;
import com.mapbox.android.core.location.LocationEngine;
import com.mapbox.android.core.location.LocationEngineCallback;
import com.mapbox.android.core.location.LocationEngineResult;
import com.mapbox.android.gestures.MoveGestureDetector;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.LineString;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.geometry.LatLngBounds;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.location.modes.RenderMode;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.plugins.markerview.MarkerView;
import com.mapbox.mapboxsdk.plugins.markerview.MarkerViewManager;
import com.mapbox.mapboxsdk.style.expressions.Expression;
import com.mapbox.mapboxsdk.style.layers.Layer;
import com.mapbox.mapboxsdk.style.layers.LineLayer;
import com.mapbox.mapboxsdk.style.layers.Property;
import com.mapbox.mapboxsdk.style.layers.PropertyFactory;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonOptions;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.mapbox.mapboxsdk.style.sources.Source;
import com.mapbox.mapboxsdk.utils.BitmapUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.lang.ref.WeakReference;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class ActiveRouteActivity extends BaseActivity implements TextToSpeech.OnInitListener {
    private final DisplayMetrics gDm = new DisplayMetrics();
    private ImageView ivBack;
    private TextView tvActiveRouteTitle, tvActiveRouteDuration, tvActiveRouteDistance;
    private com.mapbox.mapboxsdk.maps.MapView mapView;
    private MapboxMap gMapboxMap;
    private Style gMapStyle;
    private String mapCurrentStyle;
    private LocationListener locationListener;
    private Itinerary selectedItinerary = null;
    private MonitoredTrip currentStartedTrip;
    private double zoomLevel = Constants.MARKER_POPUP_ZOOM_LEVEL;
    private LocationPoint originPoint = null;
    private LocationPoint destinationPoint = null;
    private CustomizeMarkerView originMarkerView = null, destinationMarkerView = null;
    private double totalDistance = 0;
    private String tripID = null;
    private String journeyId = null;

    private int updateTrackingTimeout = 1500;
    private final Handler gHandler = new Handler();
    private Runnable gRunnable = null;

    private final Handler gRerouteHandler = new Handler();
    private Runnable gRerouteRunnable = null;
    private IndoorNavigationCustomizePopupDialog deviationPopupDialog = null;

    private final ArrayList<TrackingLocation> updateTrackingLocations = new ArrayList<>();

    private final ArrayList<String> sourceIDsForRoutePolyline = new ArrayList<String>();
    private final ArrayList<String> layerIDsForRoutePolyline = new ArrayList<String>();
    private final ArrayList<MarkerView> routeMarkerViews = new ArrayList<>();
    private MarkerViewManager markerViewManager = null;
    private ImageView ivLocateMe, ivLocateMeBg, ivItineraryDetail, ivCloseButton;
    private TextView tvInstruction, tvMessage;
    private boolean locateMeEnabled = true;
    private LinearLayout llActiveRouteBottomBar, llTripOverviewLayout;

    // Start here...
    private ItineraryLegListAdapter itineraryLegListAdapter;
    private ImageView ivSaveTrip;
    private ListView lvItineraryLegList;
    private ArrayList<ItineraryLeg> itineraryLegArrayList = new ArrayList<>();
    private final ArrayList<Boolean> itineraryLegStepArray = new ArrayList<>();
    private String currentSelectedTripId = null;
    private ListView lvServiceAlerts;
    private View viewRouteHeaderAlerts;
    private TextView tvRouteNumberAlerts, tvNumberOfAlerts, tvNumberOfAlertsAccessbility;

    public static final int MY_PERMISSIONS_REQUEST_BACKGROUND_LOCATION = 9001;

    private final ArrayList<Source> activeRouteSources = new ArrayList<>();
    private final ArrayList<LineLayer> activeRouteLayers = new ArrayList<>();

    private StringRequest trackingStringRequest = null;
    private ImageView ivIndoorNavigation;
    private RelativeLayout rlIndoorNavigationPopup;
    private TextView tvIndoorNavigationPopupAddress, tvIndoorNavigationPopupCancel, tvIndoorNavigationPopupOpen;
    private JmapTriggerableLocation currentJmapTriggerableLocation = null;

    private TransitStopManager.TransitStopListener transitStopListener;
    private TransitStop selectedTransitStop;
    private int currentSelectedLocationMode = Constants.SELECTED_LOCATION_MODE_POINT;
    private TextView tvMapAddress, tvMarkerStopID;
    private ImageView ivMarkerViewClose;
    private TextView atSearchOrigin, atSearchDestination, tvOriginUnderline;
    private LinearLayout llMarkerStopViewer, llMarkerBtnStopViewer;
    private SymbolLayer unclusteredTransitStop;
    private GeoJsonSource transitStopSource;
    private LatLng markerViewLatlng = null;
    private View customView;
    private MarkerView markerView = null;

    private String previousInstruction = "";
    private long lastPlayedTimestamp = 0;

    private Location newCurrentLocation = null;
    private boolean isDeviated = false;

    private LocationEngine locationEngine;

    private final ActiveRouteActivityLocationCallback callback = new ActiveRouteActivityLocationCallback(this);
    private Context systemContext;
    private final Itinerary currentSelectedItinerary = null;
    private ItineraryLegGeometry currentLegGeometry = null;
    private ItineraryLegPoint stopViewerItineraryLegPoint = null;
    private ItineraryLeg selectedItineraryLeg = null;
    private int currentHeight;
    private int currentY;

    private ImageView ivAlertsClose;
    private ServiceAlertAdapter serviceAlertAdapter;
    private LinearLayout rlServiceAlerts;

    // Monitors the state of the connection to the service.
    private ServiceConnection mServiceConnection = null;
    // The BroadcastReceiver used to listen from broadcasts from the service.
    private MyReceiver myReceiver;
    // A reference to the service used to get location updates.
    private LocationUpdatesService mService = null;

    // Tracks the bound state of the service.
    private boolean mBound = false;

    private final boolean stopPreviousAndClearQueue = true;
    private TextToSpeech tts;
    private AudioManager am;
    private UtteranceProgressListener utteranceProgressListener;
    private AudioManager.OnAudioFocusChangeListener afChangeListener;
    private MediaPlayer mediaPlayer;

    private void drawRoutePolylineOnMap() {
        ArrayList<ItineraryLeg> itineraryLegs = null;

        if (selectedItinerary != null) {
            itineraryLegs = selectedItinerary.getLegs();
        }
        for (LineLayer lineLayer : activeRouteLayers) {
            if (lineLayer != null) {
                gMapStyle.removeLayer(lineLayer);
            }
        }
        activeRouteLayers.clear();

        for (Source source : activeRouteSources) {
            if (source != null) {
                gMapStyle.removeSource(source);
            }
        }
        activeRouteSources.clear();
        removeRouteMarkerViewsFromMap();

        if (itineraryLegs != null) {
            for (int i = 0; i < itineraryLegs.size(); i++) {
                ItineraryLeg itineraryLeg = itineraryLegs.get(i);
                if (itineraryLeg.getLegGeometry() != null && itineraryLeg.getLegGeometry().getPoints() != null) {
                    String routeColorString = itineraryLeg.getRouteColor();
                    if (routeColorString == null && itineraryLeg.getOriginalRoute() != null) {
                        routeColorString = itineraryLeg.getOriginalRoute().getColor();
                    }

                    int routeColor;
                    String mode = itineraryLeg.getMode().toLowerCase();

                    if (routeColorString == null) {
                        routeColor = Utilities.getRouteColorByMode(this, mode);
                    } else {
                        if (!routeColorString.startsWith("#")) {
                            routeColorString = "#" + routeColorString;
                        }

                        routeColor = Color.parseColor(routeColorString);
                    }
                    List<Point> routeCoordinates = Utilities.decodeGPSPointsForRoute(itineraryLeg.getLegGeometry().getPoints());

                    String sourceID = "line-source" + i;
                    String layerID = "linelayer" + i;
                    sourceIDsForRoutePolyline.add(sourceID);
                    layerIDsForRoutePolyline.add(layerID);

                    Feature feature = Feature.fromGeometry(
                            LineString.fromLngLats(routeCoordinates)
                    );
                    feature.addNumberProperty("zIndex", 1.0);
                    Source source = new GeoJsonSource(sourceID,
                            FeatureCollection.fromFeatures(new Feature[]{feature}));
                    gMapStyle.addSource(source);
                    activeRouteSources.add(source);
                    LineLayer lineLayer = null;
                    if (mode.equals("walk") || mode.equals("bike") || mode.equals("bicycle") || mode.equals("car") || mode.equals("micromobility")) {
                        if (mode.equals("car")) {
                            lineLayer = new LineLayer(layerID, sourceID).withProperties(
                                    PropertyFactory.lineDasharray(new Float[]{2.0f, 0.5f}),
                                    PropertyFactory.lineCap(Property.LINE_CAP_SQUARE),
                                    PropertyFactory.lineJoin(Property.LINE_JOIN_BEVEL),
                                    PropertyFactory.lineWidth(5.0f),
                                    PropertyFactory.symbolZOrder(SYMBOL_Z_ORDER_AUTO),
                                    PropertyFactory.lineColor(routeColor)
                            );

                        } else {
                            lineLayer = new LineLayer(layerID, sourceID).withProperties(
                                    PropertyFactory.lineDasharray(new Float[]{0.01f, 2.0f}),
                                    PropertyFactory.lineCap(Property.LINE_CAP_ROUND),
                                    PropertyFactory.lineJoin(Property.LINE_JOIN_ROUND),
                                    PropertyFactory.lineWidth(5.0f),
                                    PropertyFactory.symbolZOrder(SYMBOL_Z_ORDER_AUTO),
                                    PropertyFactory.lineColor(routeColor)
                            );
                        }

                    } else {
                        lineLayer = new LineLayer(layerID, sourceID).withProperties(
                                PropertyFactory.lineCap(Property.LINE_CAP_ROUND),
                                PropertyFactory.lineJoin(Property.LINE_JOIN_ROUND),
                                PropertyFactory.lineWidth(5.0f),
                                PropertyFactory.symbolZOrder(SYMBOL_Z_ORDER_AUTO),
                                PropertyFactory.lineColor(routeColor)
                        );

                        if (routeCoordinates.size() > 0) {
                            double latitude;
                            double longitude;

                            List<com.google.android.gms.maps.model.LatLng> googleLatLngs = new ArrayList<>();

                            for (Point point : routeCoordinates) {
                                com.google.android.gms.maps.model.LatLng latLng = new com.google.android.gms.maps.model.LatLng(point.latitude(), point.longitude());
                                googleLatLngs.add(latLng);
                            }
                            double middleDistance = SphericalUtil.computeLength(googleLatLngs) / 2.0;

                            com.google.android.gms.maps.model.LatLng middleMarkerPosition = Utilities.extrapolate(googleLatLngs, googleLatLngs.get(0), middleDistance);

                            Point point = routeCoordinates.get(routeCoordinates.size() / 2);

                            if (middleMarkerPosition == null) {
                                latitude = point.latitude();
                                longitude = point.longitude();
                            } else {
                                latitude = middleMarkerPosition.latitude;
                                longitude = middleMarkerPosition.longitude;
                            }

                            String routeNumber = itineraryLeg.getRouteShortName();
                            if (routeNumber == null || routeNumber.isEmpty()) {
                                routeNumber = itineraryLeg.getRoute();
                            }


                            if (routeColorString == null && itineraryLeg.getOriginalRoute() != null) {
                                routeColorString = itineraryLeg.getOriginalRoute().getColor();
                            }

                            String routeTextColor = itineraryLeg.getRouteTextColor();
                            if (routeTextColor == null && itineraryLeg.getOriginalRoute() != null) {
                                routeTextColor = itineraryLeg.getOriginalRoute().getTextColor();
                            }

                            if (routeNumber != null && !routeNumber.isEmpty()) {
                                MarkerView markerViewRoute = addMarkerViewRoute(new LatLng(latitude, longitude),
                                        routeNumber,
                                        routeColorString, routeTextColor);

                                routeMarkerViews.add(markerViewRoute);
                            }

                        }
                    }

                    gMapStyle.addLayer(lineLayer);
                    activeRouteLayers.add(lineLayer);
                }
            }
        }
    }

    private void removeRouteMarkerViewsFromMap() {
        for (int i = (routeMarkerViews.size() - 1); i >= 0; i--) {
            MarkerView markerViewRoute = routeMarkerViews.get(i);
            if (markerViewRoute != null) {
                markerViewManager.removeMarker(markerViewRoute);
            }

            routeMarkerViews.remove(i);
        }
    }

    private MarkerView addMarkerViewRoute(LatLng latlng, String route, String routeColor, String textColor) {
        MarkerView markerViewRoute = null;
        View markerViewRouteView;

        RelativeLayout rlMarkerViewRoute;
        TextView tvMarkerViewRoute;

        markerViewRouteView = getLayoutInflater().inflate(R.layout.layout_marker_view_route, null);
        markerViewRouteView.setLayoutParams(new FrameLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT));

        rlMarkerViewRoute = markerViewRouteView.findViewById(R.id.rl_maker_view_route);
        tvMarkerViewRoute = markerViewRouteView.findViewById(R.id.tv_maker_view_route);

        if (route != null) {
            tvMarkerViewRoute.setText(route);
        }

        try {
            if (routeColor != null) {
                if (!routeColor.startsWith("#")) {
                    routeColor = "#" + routeColor;
                }
                rlMarkerViewRoute.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(routeColor)));
            } else {
                rlMarkerViewRoute.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FF000000")));
            }
            if (textColor != null) {
                if (!textColor.startsWith("#")) {
                    textColor = "#" + textColor;
                }
                tvMarkerViewRoute.setTextColor(Color.parseColor(textColor));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        markerViewRouteView.measure(
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        final float offsetX = markerViewRouteView.getMeasuredWidth() / 2.0f;
        final float offsetY = markerViewRouteView.getMeasuredHeight() / 2.0f;
        markerViewRoute = new MarkerView(latlng, markerViewRouteView);
        markerViewRoute.setOnPositionUpdateListener(new MarkerView.OnPositionUpdateListener() {
            @Override
            public PointF onUpdate(PointF pointF) {
                pointF.x = pointF.x - offsetX;
                pointF.y = pointF.y - offsetY;

                return pointF;
            }
        });

        markerViewManager.addMarker(markerViewRoute);
        return markerViewRoute;
    }

    private void setupIndoorNavigation() {
        ivIndoorNavigation = findViewById(R.id.iv_indoor_navigation);
        rlIndoorNavigationPopup = findViewById(R.id.rl_indoor_navigation_popup_live);
        tvIndoorNavigationPopupAddress = findViewById(R.id.tv_indoor_navigation_address_live);
        tvIndoorNavigationPopupCancel = findViewById(R.id.tv_indoor_navigation_popup_cancel_live);
        tvIndoorNavigationPopupOpen = findViewById(R.id.tv_indoor_navigation_popup_open_live);
        ivIndoorNavigation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentJmapTriggerableLocation != null) {
                    //endTracking();
                    MyApplication.getInstance().currentJmapTriggerableLocation = currentJmapTriggerableLocation;
                    Intent intent = new Intent(ActiveRouteActivity.this, IndoorNavigationActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });

        tvIndoorNavigationPopupCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rlIndoorNavigationPopup.setVisibility(View.GONE);
            }
        });

        tvIndoorNavigationPopupOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rlIndoorNavigationPopup.setVisibility(View.GONE);
                if (currentJmapTriggerableLocation != null) {
                    //endTracking();
                    MyApplication.getInstance().currentJmapTriggerableLocation = currentJmapTriggerableLocation;
                    Intent intent = new Intent(ActiveRouteActivity.this, IndoorNavigationActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });

        TextView tvBuildingHint = findViewById(R.id.tv_indoor_navigation_popup_building_live);

        ImageSpan imageSpan = new ImageSpan(BitmapFactory.decodeResource(getResources(), R.drawable.ic_popup_building),
                ImageSpan.ALIGN_BOTTOM) {
            public void draw(Canvas canvas, CharSequence text, int start,
                             int end, float x, int top, int y, int bottom,
                             Paint paint) {
                Drawable b = getDrawable();
                canvas.save();

                int transY = bottom - b.getBounds().bottom;
                // this is the key
                transY -= paint.getFontMetricsInt().descent / 2;

                canvas.translate(x, transY);
                b.draw(canvas);
                canvas.restore();
            }
        };

        CharSequence text = Html.fromHtml("A " + getString(R.string.indoor_navigation_button_hint));
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(text);
        spannableStringBuilder.setSpan(imageSpan, 0,
                1,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tvBuildingHint.setText(spannableStringBuilder);
    }

    private void detectJmapLocation() {
        Location currentLocation = MyApplication.getInstance().currentLocation;
        ArrayList<JmapTriggerableLocation> jmapTriggerableLocations = MyApplication.getInstance().
                getJmapTriggerableLocations();
        LocationPoint locationPoint = new LocationPoint();
        if (currentJmapTriggerableLocation != null) {
            if (currentJmapTriggerableLocation.getType() != null && currentJmapTriggerableLocation.getType().equalsIgnoreCase("polygon") &&
                    currentJmapTriggerableLocation.getPolygon() != null) {
                if (!Utilities.containsLocation(currentLocation.getLatitude(), currentLocation.getLongitude(),
                        currentJmapTriggerableLocation.getPolygon(), false)) {
                    currentJmapTriggerableLocation = null;
                    ivIndoorNavigation.setVisibility(View.GONE);
                }
            } else {
                locationPoint.setLat(currentJmapTriggerableLocation.getLatitude());
                locationPoint.setLon(currentJmapTriggerableLocation.getLongitude());
                double distance = Utilities.getDistance(currentLocation.getLatitude(), currentLocation.getLongitude(),
                        locationPoint);
                if (distance > currentJmapTriggerableLocation.getRadiusInMeters()) {
                    currentJmapTriggerableLocation = null;
                    ivIndoorNavigation.setVisibility(View.GONE);
                }
            }
        }

        if (currentJmapTriggerableLocation == null) {
            for (JmapTriggerableLocation jmapTriggerableLocation : jmapTriggerableLocations) {
                if (jmapTriggerableLocation.getType() != null && jmapTriggerableLocation.getType().equalsIgnoreCase("polygon") &&
                        jmapTriggerableLocation.getPolygon() != null) {
                    if (Utilities.containsLocation(currentLocation.getLatitude(), currentLocation.getLongitude(),
                            jmapTriggerableLocation.getPolygon(), false)) {
                        setCurrentJmapTriggerableLocation(jmapTriggerableLocation);
                        break;
                    }
                } else {
                    locationPoint.setLat(jmapTriggerableLocation.getLatitude());
                    locationPoint.setLon(jmapTriggerableLocation.getLongitude());
                    double distance = Utilities.getDistance(currentLocation.getLatitude(), currentLocation.getLongitude(),
                            locationPoint);
                    if (distance <= jmapTriggerableLocation.getRadiusInMeters()) {
                        setCurrentJmapTriggerableLocation(jmapTriggerableLocation);
                        break;
                    }
                }
            }
        }
    }

    private boolean isIndoorNavigationPopupShowed = false;

    private void setCurrentJmapTriggerableLocation(JmapTriggerableLocation jmapTriggerableLocation) {
        if (!isIndoorNavigationPopupShowed) {
            isIndoorNavigationPopupShowed = true;
            rlIndoorNavigationPopup.setVisibility(View.VISIBLE);
        }
        currentJmapTriggerableLocation = jmapTriggerableLocation;
        tvIndoorNavigationPopupAddress.setText(jmapTriggerableLocation.getAddress());
        ivIndoorNavigation.setVisibility(View.VISIBLE);
    }

    private RelativeLayout rlArriveDestinationPopup;
    private TextView tvArriveDestinationAddress, tvArriveDestinationConfirm;

    private void setupArriveDestinationPopup() {
        rlArriveDestinationPopup = findViewById(R.id.rl_arrive_destination_popup);
        tvArriveDestinationAddress = findViewById(R.id.tv_arrive_destination_address);
        tvArriveDestinationConfirm = findViewById(R.id.tv_arrive_destination_confirm);

        tvArriveDestinationConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gHandler.removeCallbacks(gRunnable);
                //endTracking();
                rlArriveDestinationPopup.setVisibility(View.GONE);
                finish();
            }
        });

        if (currentStartedTrip != null && currentStartedTrip.getTo() != null) {
            tvArriveDestinationAddress.setText(currentStartedTrip.getTo().getName());
        }
    }

    private boolean checkDestinationArrival(String tripStatus) {
        if (tripStatus != null && tripStatus.equalsIgnoreCase("COMPLETED")) {
            if (rlArriveDestinationPopup.getVisibility() != View.VISIBLE) {
                rlArriveDestinationPopup.setVisibility(View.VISIBLE);
            }
            return true;
        }

        return false;
    }

    private void initData() {
        if (originPoint != null && destinationPoint != null) {
            totalDistance = Utilities.getDistance(originPoint,
                    destinationPoint) / 1000.0;
        }

        if (currentStartedTrip != null) {
            selectedItinerary = currentStartedTrip.getItinerary();
            if (currentStartedTrip.getFrom() != null) {
                originPoint = new LocationPoint();
                originPoint.setLat(currentStartedTrip.getFrom().getLat());
                originPoint.setLon(currentStartedTrip.getFrom().getLon());
                originPoint.setAddress(currentStartedTrip.getFrom().getName());
            }

            if (currentStartedTrip.getTo() != null) {
                destinationPoint = new LocationPoint();
                destinationPoint.setLat(currentStartedTrip.getTo().getLat());
                destinationPoint.setLon(currentStartedTrip.getTo().getLon());
                destinationPoint.setAddress(currentStartedTrip.getTo().getName());
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_avtive_route);
        systemContext = MyApplication.getInstance().getApplicationContext();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        Intent intent = getIntent();
        if (intent != null) {
            selectedItinerary = (Itinerary) intent.getSerializableExtra("current_route");
            zoomLevel = intent.getFloatExtra("current_map_zoom_level", (float) Constants.MARKER_POPUP_ZOOM_LEVEL);
            originPoint = (LocationPoint) intent.getSerializableExtra("origin");
            destinationPoint = (LocationPoint) intent.getSerializableExtra("destination");
            tripID = intent.getStringExtra("trip_id");
            currentStartedTrip = (MonitoredTrip) intent.getSerializableExtra("monitored_trip");
        }
        initData();

        gRunnable = new Runnable() {
            @Override
            public void run() {
                tracking();
            }
        };
        {
            if (tripID != null) {
                startTracking();
            }
        }

        gRerouteRunnable = new Runnable() {
            @Override
            public void run() {
                if (!isFinishing()) {
                    deviationPopupDialog = new IndoorNavigationCustomizePopupDialog(ActiveRouteActivity.this,
                            getString(R.string.attention), getString(R.string.off_route_warning_message),
                            getString(R.string.yes), getString(R.string.no), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            reroute();
                        }
                    }, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            isDeviated = false;
                        }
                    }, true);

                    deviationPopupDialog.show();
                }
            }
        };
        ivBack = findViewById(R.id.iv_back);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gHandler.removeCallbacks(gRunnable);
                gRerouteHandler.removeCallbacks(gRerouteRunnable);
                finish();
            }
        });

        initTTS();
        setupMuteButton();
        setupMapView(savedInstanceState);
        setupActiveRouteLayout();
        setupLocateMeButton();
        setupIndoorNavigation();
        setupArriveDestinationPopup();

        tvInstruction = findViewById(R.id.tv_instruction);
        tvMessage = findViewById(R.id.tv_message);
        ivItineraryDetail = findViewById(R.id.iv_itinerary_detail);
        llActiveRouteBottomBar = findViewById(R.id.ll_active_route_bottom_bar);
        llTripOverviewLayout = findViewById(R.id.ll_trip_overview);
        ivCloseButton = findViewById(R.id.iv_close_button);

        if (selectedItinerary != null) {
            itineraryLegArrayList = selectedItinerary.getLegs();
            itineraryLegStepArray.clear();
            for (ItineraryLeg itineraryLeg : itineraryLegArrayList) {
                itineraryLegStepArray.add(false);
            }
        }
        lvItineraryLegList = findViewById(R.id.lv_itinerary_leg_list);
        itineraryLegListAdapter = getItineraryLegListAdapter();
        lvItineraryLegList.setAdapter(itineraryLegListAdapter);

        ivItineraryDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                llTripOverviewLayout.setVisibility(View.GONE);
                llActiveRouteBottomBar.setVisibility(View.VISIBLE);

            }
        });

        ivCloseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                llTripOverviewLayout.setVisibility(View.VISIBLE);
                llActiveRouteBottomBar.setVisibility(View.GONE);
            }
        });

        transitStopListener = new TransitStopManager.TransitStopListener() {
            @Override
            public void transitStopChanged() {
                loadTransitStopsOnMap();
            }
        };
        TransitStopManager.getInstance().addListener(transitStopListener);

    }

    private void removeTransitStopsFromMap() {
        if (transitStopSource != null) {
            gMapStyle.removeLayer("unclustered_transit_stop_points");
            gMapStyle.removeSource("transit_stop_list");
            transitStopSource = null;
            unclusteredTransitStop = null;
        }
    }

    private void loadTransitStopsOnMap() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (gMapStyle != null) {
                    gMapStyle.addImage("transit_stop_icon_id", BitmapUtils.getBitmapFromDrawable(
                            ActiveRouteActivity.this.getResources().getDrawable(R.drawable.ic_place)));

                    FeatureCollection features = generateTransitStopFeatureData();

                    removeTransitStopsFromMap();

                    transitStopSource = new GeoJsonSource("transit_stop_list",
                            features,
                            new GeoJsonOptions()
                                    .withCluster(false)
                    );

                    gMapStyle.addSource(transitStopSource);

                    //Creating a marker layer for single data point
                    unclusteredTransitStop = new SymbolLayer("unclustered_transit_stop_points", "transit_stop_list");

                    if (gMapboxMap.getCameraPosition().zoom >= Constants.TRANSIT_STOP_MARKER_MIN_ZOOM_LEVEL) {
                        unclusteredTransitStop.setProperties(
                                iconImage("transit_stop_icon_id"),
                                symbolZOrder(Property.SYMBOL_Z_ORDER_SOURCE),
                                iconAllowOverlap(true),
                                iconIgnorePlacement(true),
                                visibility(VISIBLE)
                        );
                    } else {
                        unclusteredTransitStop.setProperties(
                                iconImage("transit_stop_icon_id"),
                                symbolZOrder(Property.SYMBOL_Z_ORDER_SOURCE),
                                iconAllowOverlap(true),
                                iconIgnorePlacement(true),
                                visibility(NONE)
                        );
                    }

                    unclusteredTransitStop.setFilter(Expression.all(Expression.eq(get("typeId"), Resource.ID_TRANSIT_STOP), not(has("cluster"))));
                    gMapStyle.addLayer(unclusteredTransitStop);
                }
            }
        });
    }

    private void addMarkerViewIfNeeded(LatLng latlng) {
        if (markerView != null) {
            markerViewManager.removeMarker(markerView);
            markerView = null;
        }
        customView = getLayoutInflater().inflate(R.layout.layout_map_trip_plan, null);
        customView.setLayoutParams(new FrameLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT));

        setupMarkerView();

        refreshMarkerView(latlng);

        customView.setVisibility(View.VISIBLE);

        markerView = new MarkerView(latlng, customView);

        markerView.setOnPositionUpdateListener(new MarkerView.OnPositionUpdateListener() {
            @Override
            public PointF onUpdate(PointF pointF) {
                customView.measure(
                        View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                        View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
                pointF.x = pointF.x - customView.getMeasuredWidth() / 2.0f;
                pointF.y = pointF.y - customView.getMeasuredHeight();

                Log.e("markerview", "width:" + customView.getMeasuredWidth() + ", height:" + customView.getMeasuredHeight());
                Log.e("markerview", "pointF.x:" + pointF.x + ", pointF.y:" + pointF.y);
                return pointF;
            }
        });

        markerViewManager.addMarker(markerView);

        markerViewLatlng = latlng;

        moveMapToShowMarkerView();
    }

    private void refreshMarkerView(LatLng latlng) {
        switch (currentSelectedLocationMode) {

            case Constants.SELECTED_LOCATION_MODE_TRANSIT_STOP:
                llMarkerStopViewer.setVisibility(View.VISIBLE);
                llMarkerBtnStopViewer.setVisibility(View.VISIBLE);
                if (selectedTransitStop != null) {
                    tvMapAddress.setText(selectedTransitStop.getName());
                    String id = selectedTransitStop.getCode();
                    if (id == null) {
                        id = Utilities.getStopId(selectedTransitStop);
                    }
                    tvMarkerStopID.setText(id);
                    tvMapAddress.setTypeface(null, Typeface.BOLD);
                }
                break;
            default:
                break;
        }
    }

    private void moveMapToShowMarkerView() {
        if (markerViewLatlng != null) {
            double topPadding = dpToPx(200);

            double zoomLevel = gMapboxMap.getCameraPosition().zoom;
            if (zoomLevel < Constants.MARKER_POPUP_ZOOM_LEVEL) {
                zoomLevel = Constants.MARKER_POPUP_ZOOM_LEVEL;
            }
            CameraPosition position = new CameraPosition.Builder()
                    .target(markerViewLatlng)
                    .padding(0, topPadding, 0, 0)
                    .zoom(zoomLevel)
                    .build();
            gMapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(position));
        }
    }

    private void setupMarkerView() {
        tvMapAddress = customView.findViewById(R.id.tv_map_address);
        LinearLayout llActionButtons = customView.findViewById(R.id.ll_action_buttons);

        ivMarkerViewClose = customView.findViewById(R.id.iv_marker_view_close);
        llMarkerStopViewer = customView.findViewById(R.id.ll_marker_stop_viewer);
        tvMarkerStopID = customView.findViewById(R.id.tv_marker_stop_id);
        llMarkerBtnStopViewer = customView.findViewById(R.id.ll_marker_btn_stop_viewer);

        ivMarkerViewClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                customView.setVisibility(View.GONE);
            }
        });

        llActionButtons.setVisibility(View.GONE);
    }

    private FeatureCollection generateTransitStopFeatureData() {
        Gson gson = new Gson();
        ArrayList<Feature> featureList = new ArrayList<Feature>();
        if (MyApplication.getInstance().transitStops != null) {
            ArrayList<TransitStop> transitStops = MyApplication.getInstance().transitStops;
            for (TransitStop transitStop : transitStops) {

                Feature feature = Feature.fromGeometry(Point.fromLngLat(
                        transitStop.getLon(),
                        transitStop.getLat()
                ));

                feature.addNumberProperty("typeId", Resource.ID_TRANSIT_STOP);
                feature.addStringProperty("jsonString", gson.toJson(transitStop));

                featureList.add(feature);
            }
        }
        return FeatureCollection.fromFeatures(featureList);
    }

    private void setupLocateMeButton() {
        RelativeLayout rlLocateMe = findViewById(R.id.rl_locate_me);
        ivLocateMe = findViewById(R.id.iv_locate_me);
        ivLocateMeBg = findViewById(R.id.iv_locate_me_bg);
        rlLocateMe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                locateMeEnabled = true;
                if (MyApplication.getInstance().currentLocation != null && gMapboxMap != null) {
                    double currentZoom = gMapboxMap.getCameraPosition().zoom;
                    if (currentZoom < Constants.TRANSIT_STOP_MARKER_MIN_ZOOM_LEVEL) {
                        currentZoom = Constants.TRANSIT_STOP_MARKER_MIN_ZOOM_LEVEL;
                    }
                    gMapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder()
                            .target(new LatLng(MyApplication.getInstance().currentLocation.getLatitude(),
                                    MyApplication.getInstance().currentLocation.getLongitude()))
                            .zoom(currentZoom)
                            .build()));
                    ivLocateMe.setColorFilter(getResources().getColor(android.R.color.white));
                    ivLocateMeBg.setImageResource(R.drawable.ic_layer_bkg_on);
                    ivLocateMeBg.setColorFilter(getResources().getColor(R.color.colorPrimary));
                } else {
                    Toast.makeText(ActiveRouteActivity.this, getString(R.string.current_location_not_available),
                            Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        TransitStopManager.getInstance().removeListener(transitStopListener);
        gHandler.removeCallbacks(gRunnable);
        gRerouteHandler.removeCallbacks(gRerouteRunnable);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        endTracking();

        // Don't forget to shutdown tts!
        stopTTS();
        if (tts != null) {
            tts.shutdown();
        }

        stopAllWork();

        if (trackingStringRequest != null) {
            trackingStringRequest.cancel();
            trackingStringRequest = null;
        }
        super.onDestroy();
    }

    private final AlertDialog previousAlertDialog = null;

    private void startTracking() {
        TrackRequest startTrackRequest = new TrackRequest();
        TrackingLocation trackingLocation = new TrackingLocation();

        Location location = MyApplication.getInstance().currentLocation;
        if (location == null) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
                if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                } else if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                    location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                }
            }
        }

        startTrackRequest.setTripId(tripID);

        if (location != null) {
            trackingLocation.setBearing(location.getBearing());
            trackingLocation.setLat(location.getLatitude());
            trackingLocation.setLon(location.getLongitude());
            trackingLocation.setSpeed(location.getSpeed());
            trackingLocation.setTimestamp(location.getTime() / 1000);
            trackingLocation.setLocationAccuracy(location.getAccuracy());
        }
        updateTrackingLocations.add(trackingLocation);
        startTrackRequest.setLocations(updateTrackingLocations);
        Response.Listener listener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Logger.i("tracking", "startTracking response:" + response);
                Configurations.setString("last_track_trip_id", tripID);
                Gson gson = new Gson();

                try {
                    StartTrackingResponse startTrackingResponse = gson.fromJson(response, StartTrackingResponse.class);
                    StringBuilder sb = new StringBuilder();
                    String message = startTrackingResponse.getMessage();
                    String instruction = startTrackingResponse.getInstruction();
                    if (message != null && !message.isEmpty()) {
                        sb.append("Message: ");
                        sb.append(message);
                    }
                    if (instruction != null && !instruction.isEmpty()) {
                        if (!sb.toString().isEmpty()) {
                            sb.append("\n");
                        }
                        sb.append("Instruction: ");
                        sb.append(instruction);
                    }

                    hapticAndTTSHandler(instruction);

                    tvInstruction.setText(instruction);
                    tvMessage.setText(message);

                    journeyId = startTrackingResponse.getJourneyId();
                    updateTrackingTimeout = startTrackingResponse.getFrequencySeconds();
                    updateTrackingLocations.clear();

                    gHandler.postDelayed(gRunnable, updateTrackingTimeout * 1000L);

                    checkDestinationArrival(startTrackingResponse.getTripStatus());
                } catch (Exception e) {
                    e.printStackTrace();
                }


            }
        };
        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Logger.e("tracking", "Failed to start tracking:" + error.toString());

                errorHandler(error, "Failed to start tracking, do you want to try again", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startTracking();
                    }
                });

                String description = "";
                if (error != null) {
                    if (error instanceof TimeoutError) {
                        description = "Weak network, timeout";
                    } else if (error instanceof NoConnectionError) {
                        description = "No network";
                    } else {
                        if (error.networkResponse != null) {
                            if (error.networkResponse.statusCode >= 500) {
                                description = error.networkResponse.statusCode + "server error";
                            } else {
                                description = new String(error.networkResponse.data, StandardCharsets.UTF_8);
                            }
                        }
                    }
                }
            }
        };

        String url = Resource.TRACK;
        Gson gson = new GsonBuilder().serializeNulls().create();
        String params = gson.toJson(startTrackRequest);

        trackingStringRequest = volleyTrackingStringRequest(Request.Method.POST, url, params, listener, errorListener);
    }

    private void hapticAndTTSHandler(String instruction) {
        boolean needPlayNewInstruction = false;
        long currentTimestamp = System.currentTimeMillis() / 1000;
        int waitTime = Configurations.getInt("live_tracking_repeat_instruction_waittime_seconds", 30);

        if (instruction != null) {
            if (!previousInstruction.equalsIgnoreCase(instruction)) {
                needPlayNewInstruction = true;
            } else {
                if (currentTimestamp - lastPlayedTimestamp >= waitTime) {
                    needPlayNewInstruction = true;
                }
            }
        }

        if (needPlayNewInstruction) {
            if (Utilities.doesCurrentUserNeedHaptic()) {
                Utilities.vibrateOneShot();
            }
            speakOutWithMessage(instruction);

            lastPlayedTimestamp = currentTimestamp;
            previousInstruction = instruction;
        }
    }

    private void tracking() {
        TrackRequest trackRequest = new TrackRequest();
        //skip it if there is no updated location.
        if (updateTrackingLocations.size() == 0) {
            gHandler.postDelayed(gRunnable, updateTrackingTimeout * 1000L);
            return;
        }
        trackRequest.setLocations(updateTrackingLocations);
        trackRequest.setTripId(tripID);
        Response.Listener listener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Logger.i("tracking", "tracking response:" + response);
                boolean completed = false;
                Gson gson = new Gson();
                try {
                    TrackResponse trackResponse = gson.fromJson(response, TrackResponse.class);

                    if (trackResponse != null) {
                        StringBuilder sb = new StringBuilder();
                        String message = trackResponse.getMessage();
                        String instruction = trackResponse.getInstruction();
                        if (message != null && !message.isEmpty()) {
                            sb.append("Message: ");
                            sb.append(message);
                        }
                        if (instruction != null && !instruction.isEmpty()) {
                            if (!sb.toString().isEmpty()) {
                                sb.append("\n");
                            }
                            sb.append("Instruction: ");
                            sb.append(instruction);
                        }

                        tvInstruction.setText(instruction);
                        tvMessage.setText(message);

                        journeyId = trackResponse.getJourneyId();
                        updateTrackingTimeout = trackResponse.getFrequencySeconds();

                        completed = checkDestinationArrival(trackResponse.getTripStatus());

                        if (completed) {
                            StringBuilder stringBuilder = new StringBuilder();
                            stringBuilder.append(instruction);
                            stringBuilder.append(" ");
                            stringBuilder.append(getString(R.string.you_have_arrived_at));
                            if (currentStartedTrip != null && currentStartedTrip.getTo() != null) {
                                stringBuilder.append(" ");
                                stringBuilder.append(currentStartedTrip.getTo().getName());
                            }
                            hapticAndTTSHandler(stringBuilder.toString());
                        } else {
                            hapticAndTTSHandler(instruction);
                        }

                        checkDeviationAndReroute(trackResponse.getTripStatus());
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (!completed) {
                    updateTrackingLocations.clear();
                    gHandler.postDelayed(gRunnable, updateTrackingTimeout * 1000L);
                }
            }
        };
        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Logger.e("tracking", error.toString());
                errorHandler(error, "Failed to update tracking", null);

                String description = "";
                if (error != null) {
                    if (error instanceof TimeoutError) {
                        description = "Weak network, timeout";
                    } else if (error instanceof NoConnectionError) {
                        description = "No network";
                    } else {
                        if (error.networkResponse != null) {
                            if (error.networkResponse.statusCode >= 500) {
                                description = error.networkResponse.statusCode + "server error";
                            } else {
                                description = new String(error.networkResponse.data, StandardCharsets.UTF_8);
                            }
                        }
                    }
                }

                updateTrackingLocations.clear();
                gHandler.postDelayed(gRunnable, updateTrackingTimeout * 1000L);

            }
        };

        String url = Resource.TRACK;
        Gson gson = new GsonBuilder().serializeNulls().create();
        String params = gson.toJson(trackRequest);

        trackingStringRequest = volleyTrackingStringRequest(Request.Method.POST, url, params, listener, errorListener);
    }

    private void checkDeviationAndReroute(String tripStatus) {
        if (tripStatus != null) {
            if (isDeviated) {
                if (!tripStatus.equalsIgnoreCase("DEVIATED")) {
                    isDeviated = false;
                    //stop timer for the reroute

                    gRerouteHandler.removeCallbacks(gRerouteRunnable);
                }
            } else {
                if (tripStatus.equalsIgnoreCase("DEVIATED")) {
                    isDeviated = true;
                    //start timer for the reroute
                    gRerouteHandler.postDelayed(gRerouteRunnable,
                            Configurations.getInt("live_tracking_deviation_waittime_seconds", 30) * 1000L);
                }
            }
        }
    }

    private void reroute() {
        gHandler.removeCallbacks(gRunnable);
        isDeviated = false;
        TrackRequest trackRequest = new TrackRequest();

        trackRequest.setLocations(updateTrackingLocations);
        trackRequest.setTripId(tripID);
        newCurrentLocation = MyApplication.getInstance().currentLocation;
        Response.Listener listener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Logger.i("tracking", "reroute response:" + response);
                Gson gson = new Gson();

                try {
                    RerouteResponse rerouteResponse = gson.fromJson(response, RerouteResponse.class);

                    if (rerouteResponse != null) {
                        StringBuilder sb = new StringBuilder();
                        String message = rerouteResponse.getMessage();
                        String instruction = rerouteResponse.getInstruction();
                        if (message != null && !message.isEmpty()) {
                            sb.append("Message: ");
                            sb.append(message);
                        }
                        if (instruction != null && !instruction.isEmpty()) {
                            if (!sb.toString().isEmpty()) {
                                sb.append("\n");
                            }
                            sb.append("Instruction: ");
                            sb.append(instruction);
                        }

                        tvInstruction.setText(instruction);
                        tvMessage.setText(message);

                        journeyId = rerouteResponse.getJourneyId();
                        updateTrackingTimeout = rerouteResponse.getFrequencySeconds();

                        if (rerouteResponse.getItinerary() != null) {

                            selectedItinerary = rerouteResponse.getItinerary();
                            itineraryLegArrayList = selectedItinerary.getLegs();
                            itineraryLegStepArray.clear();
                            for (ItineraryLeg itineraryLeg : itineraryLegArrayList) {
                                itineraryLegStepArray.add(false);
                            }

                            if (itineraryLegListAdapter != null) {
                                itineraryLegListAdapter.notifyDataSetInvalidated();
                            }

                            Double latitude = null;
                            Double longitude = null;
                            if (newCurrentLocation == null && updateTrackingLocations.size() > 0) {
                                latitude = updateTrackingLocations.get(updateTrackingLocations.size() - 1).getLat();
                                longitude = updateTrackingLocations.get(updateTrackingLocations.size() - 1).getLon();
                            }

                            if (newCurrentLocation != null) {
                                latitude = newCurrentLocation.getLatitude();
                                longitude = newCurrentLocation.getLongitude();
                            }

                            if (latitude != null) {
                                originPoint = new LocationPoint();
                                originPoint.setLat(latitude);
                                originPoint.setLon(longitude);

                                if (originMarkerView != null) {
                                    originMarkerView.setLatLng(new LatLng(originPoint.getLat(), originPoint.getLon()));
                                }

                                totalDistance = Utilities.getDistance(originPoint,
                                        destinationPoint) / 1000.0;
                            }

                            drawRoutePolylineOnMap();
                        } else {
                            IndoorNavigationCustomizePopupDialog rerouteNoItineraryPopupDialog = new IndoorNavigationCustomizePopupDialog(ActiveRouteActivity.this,
                                    getString(R.string.attention), getString(R.string.reroute_no_itineray_found),
                                    getString(R.string.ok), null, null, null, true);
                            rerouteNoItineraryPopupDialog.show();
                        }

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

                updateTrackingLocations.clear();
                gHandler.postDelayed(gRunnable, updateTrackingTimeout * 1000L);
            }
        };
        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Logger.e("reroute", error.toString());
                IndoorNavigationCustomizePopupDialog rerouteFailedPopupDialog = new IndoorNavigationCustomizePopupDialog(ActiveRouteActivity.this,
                        getString(R.string.attention), getString(R.string.reroute_failed),
                        getString(R.string.yes), getString(R.string.no), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        reroute();
                    }
                }, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        updateTrackingLocations.clear();
                        gHandler.postDelayed(gRunnable, updateTrackingTimeout * 1000L);
                    }
                }, true);
                rerouteFailedPopupDialog.show();
            }
        };

        String url = Resource.REROUTE;
        Gson gson = new GsonBuilder().serializeNulls().create();
        String params = gson.toJson(trackRequest);

        volleyAccountStringRequestExt(Request.Method.POST, url, params, listener, errorListener);
    }

    private void endTracking() {
        EndTrackingRequest endTrackingRequest = new EndTrackingRequest();
        endTrackingRequest.setJourneyId(journeyId);
        Response.Listener listener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Configurations.setString("last_track_trip_id", "");
            }
        };
        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Logger.e("tracking", "Failed to End Tracking" + error.toString());

                errorHandler(error, "Failed to End Tracking", null);
            }
        };

        String url = Resource.END_TRACKING;
        Gson gson = new GsonBuilder().serializeNulls().create();
        String params = gson.toJson(endTrackingRequest);

        volleyAccountStringRequestExt(Request.Method.POST, url, params, listener, errorListener);
    }

    private void errorHandler(VolleyError error, @NonNull String defaultMessage, DialogInterface.OnClickListener clickListener) {
        if (error != null && error instanceof NoConnectionError) {
            Utilities.showNormalPopupDialog(this, "",
                    getString(R.string.connection_failed_msg), clickListener);
        } else if (error != null && error.networkResponse != null && error.networkResponse.statusCode == 400) {
            String msg = null;

            String responseBody = new String(error.networkResponse.data, StandardCharsets.UTF_8);

            try {
                JSONObject responseObject = new JSONObject(responseBody);

                if (!responseObject.optString("message", "").isEmpty()) {
                    msg = responseObject.optString("message", "");
                } else {
                    msg = responseBody;
                }
            } catch (JSONException e) {
                msg = responseBody;
                e.printStackTrace();
            }

            if (msg != null) {
                Utilities.showNormalPopupDialog(this, "", msg, clickListener);
            }
        } else {
            if (clickListener != null) {
                Utilities.showNormalPopupDialog(this, "", defaultMessage, clickListener,
                        null);
            } else {
                Utilities.showNormalPopupDialog(this, "", defaultMessage);
            }
        }
    }

    private void setupActiveRouteLayout() {
        tvActiveRouteTitle = findViewById(R.id.tv_active_route_title);
        tvActiveRouteDuration = findViewById(R.id.tv_active_route_duration);
        tvActiveRouteDistance = findViewById(R.id.tv_active_route_distance);
    }

    private void refreshActiveRoute() {
        if (selectedItinerary != null && totalDistance > 0) {
            Location currentLocation = MyApplication.getInstance().currentLocation;

            double currentDistance = Utilities.getDistance(currentLocation.getLatitude(), currentLocation.getLongitude(),
                    destinationPoint) / 1000.0;


            if (!Configurations.getBoolean("metric_system_enabled", true)) {
                currentDistance = currentDistance * 0.621371;
            }

            String distanceString;
            if (Configurations.getBoolean("metric_system_enabled", true)) {
                distanceString = String.format(Locale.CANADA, "%.2f", currentDistance) + " km";
            } else {
                distanceString = String.format(Locale.CANADA, "%.2f", currentDistance) + " mi";
            }
            tvActiveRouteDistance.setText(getString(R.string.around_distance, distanceString));

            int travelTimeMins = selectedItinerary.getDuration() / 60;
            int remainTimeMins = (int) ((currentDistance * travelTimeMins) / totalDistance);

            String remainingTime = getRemainingTime(remainTimeMins);
            tvActiveRouteDuration.setText(getString(R.string.duration_left, remainingTime));

            String arriveTime = getArrivalTime(remainTimeMins);
            tvActiveRouteTitle.setText(getString(R.string.arrive_at_time, arriveTime));
        } else if (currentStartedTrip != null) {
            if (currentStartedTrip.getJourneyState() != null && currentStartedTrip.getJourneyState().getMatchingItinerary() != null) {
                long endTime = currentStartedTrip.getJourneyState().getMatchingItinerary().getEndTime();

                int remainTimeMins;
                if (endTime > 10000000000L) {
                    remainTimeMins = (int) ((endTime - System.currentTimeMillis()) / 60000);
                } else {
                    remainTimeMins = (int) ((endTime - System.currentTimeMillis() / 1000L) / 60);
                }
                if (remainTimeMins < 0) {
                    remainTimeMins = 0;
                }
                String remainingTime = getRemainingTime(remainTimeMins);
                tvActiveRouteDuration.setText(getString(R.string.duration_left, remainingTime));

                String arriveTime = getArrivalTime(remainTimeMins);
                tvActiveRouteTitle.setText(getString(R.string.arrive_at_time, arriveTime));

                Location currentLocation = MyApplication.getInstance().currentLocation;
                if (currentLocation != null) {
                    double currentDistance = Utilities.getDistance(currentLocation.getLatitude(), currentLocation.getLongitude(),
                            destinationPoint) / 1000.0;

                    if (!Configurations.getBoolean("metric_system_enabled", true)) {
                        currentDistance = currentDistance * 0.621371;
                    }

                    String distanceString;
                    if (Configurations.getBoolean("metric_system_enabled", true)) {
                        distanceString = String.format(Locale.CANADA, "%.2f", currentDistance) + " km";
                    } else {
                        distanceString = String.format(Locale.CANADA, "%.2f", currentDistance) + " mi";
                    }
                    tvActiveRouteDistance.setText(getString(R.string.around_distance, distanceString));
                }
            }
        }
    }

    private String getRemainingTime(int minutes) {
        String remainingTime;
        int hrs = minutes / 60; //since both are ints, you get an int
        int mins = minutes % 60;

        if (hrs < 1) {
            remainingTime = (minutes > 1 ? minutes + " " + getString(R.string.mins) : minutes + " " + getString(R.string.min));
        } else {
            if (mins > 1) {
                remainingTime = hrs + " " + getString(R.string.h) + " " + mins + " " + getString(R.string.mins);
            } else {
                remainingTime = hrs + " " + getString(R.string.h) + " " + mins + " " + getString(R.string.min);
            }

        }

        return remainingTime;
    }

    public static String getArrivalTime(int minutes) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MINUTE, minutes);
        Date date = cal.getTime();

        return new SimpleDateFormat("hh:mm a").format(date);
    }

    private void setupMapView(Bundle savedInstanceState) {
        mapView = findViewById(R.id.map_view);
        mapView.onCreate(savedInstanceState);

        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(MapboxMap mapboxMap) {
                gMapboxMap = mapboxMap;
                markerViewManager = new MarkerViewManager(mapView, mapboxMap);
                gMapboxMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder()
                        .zoom(zoomLevel)
                        .build()));
                refreshMapStyle();

                gMapboxMap.addOnMoveListener(new MapboxMap.OnMoveListener() {
                    @Override
                    public void onMoveBegin(@NonNull MoveGestureDetector detector) {

                    }

                    @Override
                    public void onMove(@NonNull MoveGestureDetector detector) {
                        locateMeEnabled = false;
                        ivLocateMe.setColorFilter(null);
                        ivLocateMeBg.setImageResource(R.drawable.ic_layer_bkg_default);
                        ivLocateMeBg.setColorFilter(null);
                    }

                    @Override
                    public void onMoveEnd(@NonNull MoveGestureDetector detector) {

                    }
                });

                if (locateMeEnabled) {
                    ivLocateMe.setColorFilter(getResources().getColor(android.R.color.white));
                    ivLocateMeBg.setImageResource(R.drawable.ic_layer_bkg_on);
                    ivLocateMeBg.setColorFilter(getResources().getColor(R.color.colorPrimary));
                }

                gMapboxMap.addOnMapClickListener(new MapboxMap.OnMapClickListener() {
                    @Override
                    public boolean onMapClick(@NonNull LatLng point) {
//                        hideLayerPopup();

                        if (customView != null && customView.getVisibility() == View.VISIBLE) {
                            customView.setVisibility(View.GONE);
                            return true;
                        }
                        final PointF pixel = gMapboxMap.getProjection().toScreenLocation(point);

                        List<Feature> features = gMapboxMap.queryRenderedFeatures(pixel);

// Get the first feature within the list if one exist
                        if (features.size() > 0) {
                            for (Feature feature : features) {
// Ensure the feature has properties defined
                                if (feature.properties() != null && feature.properties().entrySet() != null) {
                                    for (Map.Entry<String, JsonElement> entry : feature.properties().entrySet()) {
                                        if (entry.getKey().equals("typeId")) {

                                            if (Math.abs((double) (feature.getNumberProperty(entry.getKey())) - Resource.ID_TRANSIT_STOP) < 0.01) {
                                                String transitStopString = feature.getStringProperty("jsonString");
                                                if (transitStopString != null) {
                                                    Gson gson = new Gson();
                                                    try {
                                                        selectedTransitStop = gson.fromJson(transitStopString, TransitStop.class);
                                                        if (selectedTransitStop != null) {
                                                            currentSelectedLocationMode = Constants.SELECTED_LOCATION_MODE_TRANSIT_STOP;
                                                            addMarkerViewIfNeeded(new LatLng(selectedTransitStop.getLat(), selectedTransitStop.getLon()));
                                                            //addMarkerViewIfNeeded(point);
                                                            return true;
                                                        }
                                                    } catch (Exception e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                            }

                                        }
                                    }
                                }
                            }
                        }


                        return true;
                    }
                });

                gMapboxMap.addOnCameraIdleListener(new MapboxMap.OnCameraIdleListener() {
                    @Override
                    public void onCameraIdle() {
                        if (gMapStyle != null && gMapStyle.isFullyLoaded()) {
                            Layer layer = gMapStyle.getLayer("unclustered_transit_stop_points");
                            if (layer != null) {
                                if (gMapboxMap.getCameraPosition().zoom >= Constants.TRANSIT_STOP_MARKER_MIN_ZOOM_LEVEL) {
                                    layer.setProperties(visibility(VISIBLE));
                                } else {
                                    layer.setProperties(visibility(NONE));
                                }
                            }
                        }
                    }
                });
                loadTransitStopsOnMap();
            }
        });
    }

    private static class ActiveRouteActivityLocationCallback
            implements LocationEngineCallback<LocationEngineResult> {

        private final WeakReference<ActiveRouteActivity> activityWeakReference;
        private final boolean movedMapToCurrentLocation = false;

        ActiveRouteActivityLocationCallback(ActiveRouteActivity activity) {
            this.activityWeakReference = new WeakReference<>(activity);
        }

        /**
         * The LocationEngineCallback interface's method which fires when the device's location has changed.
         *
         * @param result the LocationEngineResult object which has the last known location within it.
         */
        @Override
        public void onSuccess(LocationEngineResult result) {
            ActiveRouteActivity activity = activityWeakReference.get();

            if (activity != null) {
                Location location = result.getLastLocation();

                if (location == null) {
                    return;
                }

                MyApplication.getInstance().currentLocation = location;
                if (activity.locateMeEnabled) {
                    activity.moveMapToCurrentLocation();
                }
                activity.refreshActiveRoute();

                TrackingLocation trackingLocation = new TrackingLocation();
                trackingLocation.setBearing(location.getBearing());
                trackingLocation.setLat(location.getLatitude());
                trackingLocation.setLon(location.getLongitude());
                trackingLocation.setSpeed(location.getSpeed());
                trackingLocation.setTimestamp(location.getTime() / 1000);
                trackingLocation.setLocationAccuracy(location.getAccuracy());

                activity.updateTrackingLocations.add(trackingLocation);

                activity.detectJmapLocation();
            }
        }

        /**
         * The LocationEngineCallback interface's method which fires when the device's location can't be captured
         *
         * @param exception the exception message
         */
        @Override
        public void onFailure(@NonNull Exception exception) {
            ActiveRouteActivity activity = activityWeakReference.get();
            if (activity != null) {
                Toast.makeText(activity, exception.getLocalizedMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void startLocationListener() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            LocationManager locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);

            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                MyApplication.getInstance().currentLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            } else if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                MyApplication.getInstance().currentLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            }

            enableLocationComponentOnMap();
            startLocationManager();
        }
    }

    private void enableLocationComponentOnMap() {
        if (gMapboxMap != null && gMapStyle != null) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                // Get an instance of the component
                LocationComponent locationComponent = gMapboxMap.getLocationComponent();

                checkBackgroundLocationPermission();
// Activate with options
                locationComponent.activateLocationComponent(
                        LocationComponentActivationOptions.builder(this, gMapStyle).build());

// Enable to make component visible
                locationComponent.setLocationComponentEnabled(true);

// Set the component's camera mode
                locationComponent.setCameraMode(CameraMode.NONE);

// Set the component's render mode
                locationComponent.setRenderMode(RenderMode.GPS);
            }
        }
    }

    private void checkBackgroundLocationPermission() {
        if (Build.VERSION.SDK_INT >= 30) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Logger.d("background explanation", ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_BACKGROUND_LOCATION) + "");
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_BACKGROUND_LOCATION)) {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                    alertDialogBuilder
                            .setMessage(getString(R.string.request_background_location_explanation))
                            .setCancelable(false)
                            .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    ActivityCompat.requestPermissions(ActiveRouteActivity.this,
                                            new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION},
                                            MY_PERMISSIONS_REQUEST_BACKGROUND_LOCATION);
                                }
                            });

                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                } else {
                    if (!Configurations.getBoolean("showed_background_location_confirmation", false)) {
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                        alertDialogBuilder
                                .setMessage(getString(R.string.request_background_location_confirmation))
                                .setCancelable(false)
                                .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        gotoAppPermissionSetting();
                                        Configurations.setBoolean("showed_background_location_confirmation", true);
                                    }
                                }).setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Configurations.setBoolean("showed_background_location_confirmation", true);
                                    }
                                });

                        AlertDialog alertDialog = alertDialogBuilder.create();
                        alertDialog.show();
                    }
                }
            }
        }
    }

    private void gotoAppPermissionSetting() {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivity(intent);
    }

    private void refreshMapStyle() {
        if (gMapboxMap != null) {
            if (Configurations.getBoolean("map_style_streets", true)) {
                mapCurrentStyle = Style.LIGHT;
            } else {
                mapCurrentStyle = Style.SATELLITE_STREETS;
            }

            gMapboxMap.setStyle(mapCurrentStyle, new Style.OnStyleLoaded() {
                @Override
                public void onStyleLoaded(@NonNull Style style) {
                    gMapStyle = style;
                    if (ContextCompat.checkSelfPermission(ActiveRouteActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                            || ContextCompat.checkSelfPermission(ActiveRouteActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        startLocationListener();
                    }
                    moveMapToCurrentLocation();
                    drawRoutePolylineOnMap();
                    showOriginMarker();
                    showDestinationMarker();
                }
            });
        }
    }

    private CustomizeMarkerView addMarkerViewOriginDestination(LatLng latlng, boolean isOrigin) {
        CustomizeMarkerView markerViewRouteOriginDestination = null;
        View view = getLayoutInflater().inflate(R.layout.layout_marker_view_origin_destination, null);
        view.setLayoutParams(new FrameLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT));

        ImageView ivMarkerViewIcon = view.findViewById(R.id.iv_marker_view_icon);

        if (isOrigin) {
            ivMarkerViewIcon.setImageResource(R.drawable.ic_start_trip);
        } else {
            ivMarkerViewIcon.setImageResource(R.drawable.ic_end);
        }

        view.measure(
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        final float offsetX = view.getMeasuredWidth() / 2.0f;
        final float offsetY = view.getMeasuredHeight() / 2.0f;
        markerViewRouteOriginDestination = new CustomizeMarkerView(latlng, view);
        markerViewRouteOriginDestination.setOnPositionUpdateListener(new MarkerView.OnPositionUpdateListener() {
            @Override
            public PointF onUpdate(PointF pointF) {
                pointF.x = pointF.x - offsetX;
                pointF.y = pointF.y - offsetY;

                return pointF;
            }
        });

        markerViewManager.addMarker(markerViewRouteOriginDestination);

        return markerViewRouteOriginDestination;
    }

    private void showDestinationMarker() {
        if (gMapboxMap != null && markerViewManager != null) {
            if (destinationPoint.getAddress() != null && !destinationPoint.getAddress().isEmpty()) {
                destinationMarkerView = addMarkerViewOriginDestination(new LatLng(destinationPoint.getLat(), destinationPoint.getLon()), false);
            }
        }
    }

    private void showOriginMarker() {
        if (gMapboxMap != null && markerViewManager != null) {
            originMarkerView = addMarkerViewOriginDestination(new LatLng(originPoint.getLat(), originPoint.getLon()), true);
        }
    }

    private void moveMapToCurrentLocation() {
        if (gMapboxMap != null) {
            if (MyApplication.getInstance().currentLocation != null) {
                gMapboxMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder()
                        .target(new LatLng(MyApplication.getInstance().currentLocation.getLatitude(),
                                MyApplication.getInstance().currentLocation.getLongitude()))
                        //.zoom(zoomLevel)
                        .build()));
            } else {

                gMapboxMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder()
                        .target(new LatLng(originPoint.getLat(), originPoint.getLon()))
                        //.zoom(zoomLevel)
                        .build()));
            }
        }
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

    public void refreshSaveTripButton() {
        if (ivSaveTrip != null) {

            boolean loggedIn = Utilities.isLoggedIn();

            if (Utilities.itineraryCanBeMonitored(selectedItinerary)) {
                ivSaveTrip.setVisibility(View.VISIBLE);
                ivSaveTrip.setImageResource(R.drawable.ic_save_new);
            } else {
                ivSaveTrip.setVisibility(View.GONE);
                ivSaveTrip.setImageResource(R.drawable.ic_block);
            }
        }
    }

    class ItineraryLegListAdapter extends SimpleAdapter {
        Context mContext;
        private final LayoutInflater l_Inflater;

        public ItineraryLegListAdapter(Context context, List<? extends Map<String, ?>> data,
                                       int resource, String[] from, int[] to) {
            super(context, data, resource, from, to);

            mContext = context;
            l_Inflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return itineraryLegArrayList.size();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ItineraryLeg itineraryLeg = itineraryLegArrayList.get(position);

            if (convertView == null) {
                if (Utilities.checkIfToDisplayAccessibilityUI(mContext)) {
                    convertView = l_Inflater.inflate(
                            R.layout.item_itinerary_leg_accessibility, null);

                } else {
                    convertView = l_Inflater.inflate(
                            R.layout.item_itinerary_leg, null);
                }
            }

            LinearLayout llItineraryLegEnd = convertView.findViewById(R.id.ll_itinerary_leg_end);
            LinearLayout llItineraryLegStopID = convertView.findViewById(R.id.ll_itinerary_leg_stop_id);
            LinearLayout llItineraryLegSteps = convertView.findViewById(R.id.ll_itinerary_leg_steps);
            LinearLayout llItineraryLegStepsDetail = convertView.findViewById(R.id.ll_itinerary_leg_steps_detail);
            LinearLayout llItineraryLegStepsController = convertView.findViewById(R.id.ll_itinerary_leg_steps_controller);

            TextView tvItineraryLegEndTime = convertView.findViewById(R.id.tv_itinerary_leg_end_time);
            TextView tvItineraryLegStartTime = convertView.findViewById(R.id.tv_itinerary_leg_start_time);
            TextView tvItineraryLegStartAddress = convertView.findViewById(R.id.tv_itinerary_leg_start_address);
            TextView tvItineraryLegEndAddress = convertView.findViewById(R.id.tv_itinerary_leg_end_address);
            TextView tvItineraryLegDuration = convertView.findViewById(R.id.tv_itinerary_leg_duration);
            TextView tvItineraryLegDescription = convertView.findViewById(R.id.tv_itinerary_leg_description);
            TextView tvItineraryLegServiceProvider = convertView.findViewById(R.id.tv_itinerary_leg_service_provider);
            TextView tvItineraryLegStopID = convertView.findViewById(R.id.tv_itinerary_leg_stop_id);
            TextView tvItineraryLegStopViewer = convertView.findViewById(R.id.tv_itinerary_leg_stop_viewer);
            TextView tvItineraryLegRouteNumber = convertView.findViewById(R.id.tv_itinerary_leg_route_number);
            TextView tvItineraryLegTripViewerDivider = convertView.findViewById(R.id.tv_itinerary_leg_trip_viewer_divider);

            TextView tvItineraryLegTripViewer = convertView.findViewById(R.id.tv_itinerary_leg_trip_viewer);

            //RelativeLayout rlItineraryLegRouteNumber = convertView.findViewById(R.id.rl_itinerary_leg_route_number);

            //ImageView ivItineraryLegRouteNumber = convertView.findViewById(R.id.iv_itinerary_leg_route_number);
            ImageView ivItineraryLegStart = convertView.findViewById(R.id.iv_itinerary_leg_start);
            ImageView ivItineraryLegPolyline = convertView.findViewById(R.id.iv_itinerary_leg_polyline);
            ImageView ivItineraryIcon = convertView.findViewById(R.id.iv_icon);

            ImageView ivItineraryLegStepController = convertView.findViewById(R.id.iv_itinerary_leg_steps_controller);

            LinearLayout llTimeStatus = convertView.findViewById(R.id.ll_time_status);
            TextView tvTimeStatus = convertView.findViewById(R.id.tv_time_status);
            TextView tvRealTime = convertView.findViewById(R.id.tv_real_time);


            String itineraryMode;
            if (itineraryLeg.getMode() != null) {
                itineraryMode = Utilities.getMappedModeForItinerary(ActiveRouteActivity.this, itineraryLeg.getRouteId(),
                        itineraryLeg.getMode(), itineraryLeg.getRouteShortName(), itineraryLeg.getRouteLongName()).toLowerCase();
                if (itineraryMode.equalsIgnoreCase("bicycle") && itineraryLeg.isRentedBike()) {
                    itineraryMode = "bikeshare";
                }
            } else {
                itineraryMode = "walk";
            }

            ivItineraryIcon.setImageResource(Utilities.getModeImageByMode(
                    itineraryMode, false, ActiveRouteActivity.this, ivItineraryIcon, ivItineraryIcon));

            if (itineraryLeg.getFrom() != null && itineraryLeg.getFrom().getStopId() != null) {
                String stopID = itineraryLeg.getFrom().getStopId();
                String stopCode = itineraryLeg.getFrom().getStopCode();

                if (stopCode != null && !stopCode.isEmpty()) {
                    tvItineraryLegStopID.setText(getString(R.string.stop_id_with_num, stopCode));
                } else {
                    TransitStop transitStop = Utilities.getStopInfo(stopID);
                    if (transitStop != null && transitStop.getCode() != null) {
                        //Logger.i("plantrip", "transitStop code:" + transitStop.getCode());
                        tvItineraryLegStopID.setText(getString(R.string.stop_id_with_num, transitStop.getCode()));
                    } else {
                        if (stopID.lastIndexOf(":") != -1) {
                            stopID = stopID.substring(stopID.lastIndexOf(":") + 1);
                        }
                        tvItineraryLegStopID.setText(getString(R.string.stop_id_with_num, stopID));
                    }
                }
            }

            String startTime = Utilities.formatTimestamp(ActiveRouteActivity.this, itineraryLeg.getStartTime());
            String endTime = Utilities.formatTimestamp(ActiveRouteActivity.this, itineraryLeg.getEndTime());
            String toAddress;
            String distance;

            if (itineraryLeg.isRealTime()) {
                llTimeStatus.setVisibility(View.VISIBLE);
                //less than threshold means on time
                if (Math.abs(itineraryLeg.getArrivalDelay()) < Constants.ON_TIME_THRESHOLD_SECONDS) {
                    tvItineraryLegStartTime.setText(startTime);
                    tvItineraryLegStartTime.setForeground(null);
                    tvRealTime.setText(getString(R.string.on_time));
                    tvTimeStatus.setText("");
                } else {
                    tvItineraryLegStartTime.setForeground(ContextCompat.getDrawable(mContext, R.drawable.red_strikethrough));
                    //> 0 means delay, < 0 means early
                    if (itineraryLeg.getArrivalDelay() > 0) {
                        tvItineraryLegStartTime.setForegroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorItineraryLate, null)));

                        tvTimeStatus.setTextColor(getResources().getColor(R.color.colorItineraryLate, null));
                        tvRealTime.setTextColor(getResources().getColor(R.color.colorItineraryLate, null));

                        tvTimeStatus.setText(getString(R.string.mins_late, (int) (itineraryLeg.getArrivalDelay() / 60)));
                    } else {
                        tvItineraryLegStartTime.setForegroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorItineraryEarly, null)));
                        tvTimeStatus.setTextColor(getResources().getColor(R.color.colorItineraryEarly, null));
                        tvRealTime.setTextColor(getResources().getColor(R.color.colorItineraryEarly, null));

                        tvTimeStatus.setText(getString(R.string.mins_early, (int) (Math.abs(itineraryLeg.getArrivalDelay()) / 60)));
                    }

                    String scheduleTime = Utilities.formatTimestamp(ActiveRouteActivity.this, itineraryLeg.getStartTime() - itineraryLeg.getArrivalDelay() * 1000);
                    tvItineraryLegStartTime.setText(scheduleTime);
                    tvRealTime.setText(Utilities.formatTimestamp(ActiveRouteActivity.this, itineraryLeg.getStartTime()));
                }
            } else {
                llTimeStatus.setVisibility(View.GONE);
                tvItineraryLegStartTime.setForeground(null);
                tvItineraryLegStartTime.setText(startTime);
            }

            llItineraryLegSteps.setTag(position);
            llItineraryLegSteps.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = (Integer) view.getTag();

                    if (position < itineraryLegStepArray.size()) {
                        itineraryLegStepArray.set(position, !itineraryLegStepArray.get(position));
                    }

                    if (itineraryLegListAdapter != null) {
                        itineraryLegListAdapter.notifyDataSetChanged();
                    }
                    Utilities.setTotalHeightToListViewExt(gDm.widthPixels - Convert.dpToPx(30), lvItineraryLegList);
                }
            });
            String routeColorString = itineraryLeg.getRouteColor();
            String textColor = itineraryLeg.getRouteTextColor();
            if (routeColorString == null && itineraryLeg.getOriginalRoute() != null) {
                routeColorString = itineraryLeg.getOriginalRoute().getColor();
                textColor = itineraryLeg.getOriginalRoute().getTextColor();
            }
            int routeColor;
            int textBackgroundColorInt;
            if (routeColorString == null) {
                routeColor = Utilities.getRouteColorByMode(ActiveRouteActivity.this, itineraryMode);
                textBackgroundColorInt = 0xFFD9D9D9;  //default grey value
            } else {
                if (!routeColorString.startsWith("#")) {
                    routeColorString = "#" + routeColorString;
                }
                routeColor = Color.parseColor(routeColorString);

                textBackgroundColorInt = routeColor;
                if (routeColor < 0x1000000) {
                    textBackgroundColorInt = routeColor + 0xAF000000;
                }
            }

            if (textColor != null) {
                if (!textColor.startsWith("#")) {
                    textColor = "#" + textColor;
                }
                tvItineraryLegRouteNumber.setTextColor(Color.parseColor(textColor));
            }

            View viewRouteNumberHeader = convertView.findViewById(R.id.view_route_number_header);
            if (itineraryLeg.getRouteShortName() != null && !itineraryLeg.getRouteShortName().isEmpty()) {
                tvItineraryLegRouteNumber.setVisibility(View.VISIBLE);
                tvItineraryLegRouteNumber.setText(itineraryLeg.getRouteShortName());
                viewRouteNumberHeader.setVisibility(View.VISIBLE);
                tvItineraryLegRouteNumber.setBackgroundTintList(ColorStateList.valueOf(textBackgroundColorInt));
                tvItineraryLegRouteNumber.setBackgroundResource(R.drawable.bottom_rounded_white_with_shadow_bg);
                viewRouteNumberHeader.setBackgroundTintList(ColorStateList.valueOf(routeColor));

            } else if (itineraryLeg.getRouteLongName() != null && !itineraryLeg.getRouteLongName().isEmpty()) {
                tvItineraryLegRouteNumber.setVisibility(View.VISIBLE);
                tvItineraryLegRouteNumber.setText(itineraryLeg.getRouteLongName());
                viewRouteNumberHeader.setVisibility(View.VISIBLE);
                tvItineraryLegRouteNumber.setBackgroundTintList(ColorStateList.valueOf(textBackgroundColorInt));
                tvItineraryLegRouteNumber.setBackgroundResource(R.drawable.bottom_rounded_white_with_shadow_bg);
                viewRouteNumberHeader.setBackgroundTintList(ColorStateList.valueOf(routeColor));
            } else if (itineraryLeg.getRoute() != null && !itineraryLeg.getRoute().isEmpty()) {
                tvItineraryLegRouteNumber.setVisibility(View.VISIBLE);
                tvItineraryLegRouteNumber.setText(itineraryLeg.getRoute());
                viewRouteNumberHeader.setVisibility(View.VISIBLE);
                tvItineraryLegRouteNumber.setBackgroundTintList(ColorStateList.valueOf(textBackgroundColorInt));
                tvItineraryLegRouteNumber.setBackgroundResource(R.drawable.bottom_rounded_white_with_shadow_bg);
                viewRouteNumberHeader.setBackgroundTintList(ColorStateList.valueOf(routeColor));
            } else {
                tvItineraryLegRouteNumber.setVisibility(View.GONE);
                viewRouteNumberHeader.setVisibility(View.GONE);
            }


            LinearLayout llServiceAlerts = convertView.findViewById(R.id.ll_service_alerts);
            TextView tvServiceAlerts = convertView.findViewById(R.id.tv_service_alerts);

            if (itineraryLeg.getAlerts() != null && itineraryLeg.getAlerts().size() > 0) {
                serviceAlerts.clear();
                serviceAlerts.addAll(itineraryLeg.getAlerts());

                if (serviceAlerts.size() > 0) {

                    if (serviceAlerts.size() == 1) {
                        tvServiceAlerts.setText(getString(R.string.one_alert));
                    } else {
                        tvServiceAlerts.setText(String.format("%s %s", serviceAlerts.size() + "", getString(R.string.more_alert)));
                    }

                    llServiceAlerts.setVisibility(View.VISIBLE);

                    llServiceAlerts.setTag(position);

                    llServiceAlerts.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            int position = (int) view.getTag();
                            if (position < itineraryLegArrayList.size()) {
                                ItineraryLeg itineraryLeg1 = itineraryLegArrayList.get(position);

                                if (itineraryLeg1.getAlerts() != null && itineraryLeg1.getAlerts().size() > 0) {
                                    serviceAlerts.clear();
                                    serviceAlerts.addAll(itineraryLeg1.getAlerts());
                                    loadDataInServiceAlerts(itineraryLeg1);

                                    if (serviceAlertAdapter != null) {
                                        serviceAlertAdapter.notifyDataSetChanged();
                                    }

                                    Utilities.setTotalHeightToListView(lvServiceAlerts);


                                }
                            }

                        }
                    });

                } else {
                    llServiceAlerts.setVisibility(View.GONE);
                }


            } else {
                llServiceAlerts.setVisibility(View.GONE);
            }


            LinearLayout llItineraryDetailAccessiblebg = convertView.findViewById(R.id.ll_itinerary_detail_accessible);
            ImageView ivItineraryDetailAccessibleStatus = convertView.findViewById(R.id.iv_itinerary_detail_accessible_status);

            if (Configurations.getBoolean("enabled_search_mode_accessible_routing", false)) {
                int color = getAccessibilityColorForLeg(itineraryLeg);

                switch (color) {
                    case R.color.accessibile:
                        llItineraryDetailAccessiblebg.setBackgroundResource(R.drawable.black_frame_rounded_accessible_bg);
                        llItineraryDetailAccessiblebg.setVisibility(View.VISIBLE);
                        ivItineraryDetailAccessibleStatus.setImageResource(R.drawable.ic_like);
                        break;

                    case R.color.likely_acessibile:
                        llItineraryDetailAccessiblebg.setBackgroundResource(R.drawable.black_frame_rounded_likely_accessible);
                        llItineraryDetailAccessiblebg.setVisibility(View.VISIBLE);
                        ivItineraryDetailAccessibleStatus.setImageResource(R.drawable.ic_help_menu);
                        break;

                    case R.color.unaccessible:
                        llItineraryDetailAccessiblebg.setBackgroundResource(R.drawable.black_frame_rounded_unaccessible);
                        llItineraryDetailAccessiblebg.setVisibility(View.VISIBLE);
                        ivItineraryDetailAccessibleStatus.setImageResource(R.drawable.ic_thumbsdown);
                        break;

                    default:
                        llItineraryDetailAccessiblebg.setVisibility(View.GONE);
                        break;
                }

            } else {
                llItineraryDetailAccessiblebg.setVisibility(View.GONE);
            }


            if (itineraryMode.equals("walk") || itineraryMode.equals("bike") || itineraryMode.equals("bicycle") || itineraryMode.equals("car") || itineraryMode.equals("micromobility")) {
                //dash
                ivItineraryLegPolyline.setBackgroundResource(R.drawable.vertical_dash_line_bg);
                ivItineraryLegPolyline.setBackgroundTintList(ColorStateList.valueOf(routeColor));
            } else {
                //straight vertical line
                ivItineraryLegPolyline.setBackgroundColor(routeColor);
                ivItineraryLegPolyline.setBackgroundTintList(null);
            }

            if ((itineraryLeg.getSteps() != null && itineraryLeg.getSteps().size() > 0)
                    || (itineraryLeg.getIntermediateStops() != null && itineraryLeg.getIntermediateStops().size() > 0)) {
                if (itineraryLeg.getIntermediateStops() != null && itineraryLeg.getIntermediateStops().size() > 0) {
                    tvItineraryLegDuration.setText(getString(R.string.ride_duration_with_stops, (int) (itineraryLeg.getDuration() / 60),
                            itineraryLeg.getIntermediateStops().size() + 1));

                    populateItineraryLegStops(llItineraryLegStepsDetail, itineraryLeg.getIntermediateStops(), routeColorString);
                } else {
                    tvItineraryLegDuration.setText(getString(R.string.min_with_num, (int) (itineraryLeg.getDuration() / 60)));
                    populateItineraryLegSteps(llItineraryLegStepsDetail, itineraryLeg.getSteps());
                }

                if (itineraryLegStepArray.get(position)) {
                    if (itineraryMode.equals("car")) {
                        llItineraryLegStepsDetail.setVisibility(View.GONE);
                        ivItineraryLegStepController.setImageResource(R.drawable.ic_up);
                        ivItineraryLegStepController.setVisibility(View.GONE);
                    } else {
                        llItineraryLegStepsDetail.setVisibility(View.VISIBLE);
                        ivItineraryLegStepController.setImageResource(R.drawable.ic_up);
                        ivItineraryLegStepController.setVisibility(View.VISIBLE);
                    }

                } else {
                    if (itineraryMode.equals("car")) {
                        ivItineraryLegStepController.setImageResource(R.drawable.ic_down);
                        ivItineraryLegStepController.setVisibility(View.GONE);
                    } else {
                        llItineraryLegStepsDetail.setVisibility(View.GONE);
                        ivItineraryLegStepController.setImageResource(R.drawable.ic_down);
                        ivItineraryLegStepController.setVisibility(View.VISIBLE);
                    }
                }


                if (itineraryMode.equals("bus") || itineraryMode.equals("subway") || itineraryMode.equals("rail") || itineraryMode.equals("tram")) {

                    llItineraryLegSteps.setVisibility(View.VISIBLE);
                    tvItineraryLegTripViewer.setVisibility(View.INVISIBLE);
                } else {

                    llItineraryLegSteps.setVisibility(View.VISIBLE);
                    tvItineraryLegTripViewer.setVisibility(View.VISIBLE);
                }
            } else {
                llItineraryLegSteps.setVisibility(View.GONE);
            }

            if (itineraryLeg.getFrom() != null && itineraryLeg.getFrom().getName() != null) {
                if ("micromobility".equalsIgnoreCase(itineraryLeg.getMode()) && itineraryLeg.isRentedVehicle()) {
                    tvItineraryLegStartAddress.setText(getString(R.string.spin_e_scooter));
                } else if ("bicycle".equalsIgnoreCase(itineraryLeg.getMode()) && itineraryLeg.getFrom().getVertexType().equalsIgnoreCase("bikeshare")) {
                    tvItineraryLegStartAddress.setText(getString(R.string.shared_bike));
                } else if ("car".equalsIgnoreCase(itineraryLeg.getMode()) && itineraryLeg.getFrom().getVertexType().equalsIgnoreCase("carshare")) {
                    tvItineraryLegStartAddress.setText(getString(R.string.shared_car));
                } else {
                    String name = itineraryLeg.getFrom().getName();
                    if ("Default vehicle type".equalsIgnoreCase(name) &&
                            "scooter".equalsIgnoreCase(itineraryLeg.getMode())) {
                        name = getString(R.string.link_scooter);
                    } else {
                        if (itineraryLeg.isInterlineWithPreviousLeg()) {
                            name = getString(R.string.stay_on_board_at, name);
                        }
                    }
                    tvItineraryLegStartAddress.setText(name);
                }

            }
            boolean isRentalScooter = false;
            if (position < itineraryLegArrayList.size() - 1) {
                ItineraryLeg itineraryLegNext = itineraryLegArrayList.get(position + 1);
                if (itineraryLegNext != null && "micromobility".equalsIgnoreCase(itineraryLegNext.getMode()) && itineraryLegNext.isRentedVehicle()) {
                    isRentalScooter = true;
                }
            }

            if (isRentalScooter) {
                toAddress = getString(R.string.pick_up_spin_e_scooter);
            } else {
                if (itineraryLeg.getTo() != null && itineraryLeg.getTo().getName() != null) {
                    ItineraryLegPoint toItineraryLeg = itineraryLeg.getTo();
                    toAddress = toItineraryLeg.getName();
                    //special handing for Default vehicle type
                    if ("Default vehicle type".equalsIgnoreCase(toAddress)) {
                        if (toItineraryLeg.getRentalVehicle() != null && toItineraryLeg.getRentalVehicle().getNetwork() != null) {
                            String[] networks = toItineraryLeg.getRentalVehicle().getNetwork().split("_");

                            toAddress = networks[0] + " Vehicle";
                        }
                    }
                } else {
                    toAddress = "";
                }
            }
            if (itineraryMode.equals("bus") || itineraryMode.equals("subway")) {
                if (itineraryLeg.getHeadsign() == null || itineraryLeg.getHeadsign().isEmpty()) {
                    tvItineraryLegDescription.setText(itineraryLeg.getRouteLongName());
                } else {
                    if (itineraryLeg.getRouteLongName() != null) {
                        tvItineraryLegDescription.setText(getString(R.string.itinerary_leg_transit_description,
                                itineraryLeg.getRouteLongName(), itineraryLeg.getHeadsign()));
                    } else {
                        tvItineraryLegDescription.setText(getString(R.string.itinerary_leg_transit_description_without_route_name,
                                itineraryLeg.getHeadsign()));
                    }
                }
            } else {

                if (itineraryLeg.getDistance() < 161) {
                    distance = getString(R.string.feet_with_num, (int) (itineraryLeg.getDistance() * Constants.METER_TO_FOOT));
                } else {
                    distance = getString(R.string.miles_with_num, itineraryLeg.getDistance() / Constants.MILE_TO_METER);
                }
                String mode = itineraryLeg.getMode();

                if ("micromobility".equalsIgnoreCase(mode) || "Scooter".equalsIgnoreCase(mode)) {
                    mode = getString(R.string.ride);
                } else if ("CAR".equalsIgnoreCase(mode)) {
                    mode = getString(R.string.drive);
                } else {
                    if (mode != null && mode.length() > 0) {
                        mode = mode.substring(0, 1).toUpperCase() + mode.substring(1).toLowerCase();
                    }
                }

                //modes that dont show the distance info
                if (!mode.equalsIgnoreCase("Ferry") && !mode.equalsIgnoreCase("water_taxi") && !mode.equalsIgnoreCase("Tram") && !mode.equalsIgnoreCase("Rail")) {
                    tvItineraryLegDescription.setText(getString(R.string.itinerary_leg_description,
                            mode, distance, toAddress));
                    tvItineraryLegDescription.setVisibility(View.VISIBLE);
                } else {
                    tvItineraryLegDescription.setVisibility(View.GONE);
                }

            }

            ivItineraryLegStart.setBackgroundResource(R.drawable.ic_layer_bkg_default);
            ivItineraryLegStart.setColorFilter(getResources().getColor(android.R.color.black));
            tvItineraryLegServiceProvider.setVisibility(View.GONE);
            llItineraryLegStopID.setVisibility(View.GONE);
            if (!Utilities.checkIfToDisplayAccessibilityUI(ActiveRouteActivity.this)) {
                tvItineraryLegTripViewerDivider.setVisibility(View.GONE);
            }

            tvItineraryLegTripViewer.setVisibility(View.GONE);

            int resID = Utilities.getModeImageByMode(itineraryMode, itineraryLeg.isRentedVehicle(), ActiveRouteActivity.this, ivItineraryLegStart, ivItineraryLegStart);
            ivItineraryLegStart.setImageResource(resID);

            switch (itineraryMode) {
                //only bus and subway have trip viewer
                case "subway":
                case "tram":
                case "ferry":
                case "water_taxi":
                case "link":
                case "monorail":
                case "bus":
                    tvItineraryLegServiceProvider.setVisibility(View.VISIBLE);
                    llItineraryLegStopID.setVisibility(View.VISIBLE);

                    if (itineraryLeg.getTripId() != null || itineraryLeg.getTrip().getGtfsId() != null) {
                        if (!Utilities.checkIfToDisplayAccessibilityUI(ActiveRouteActivity.this)) {
                            tvItineraryLegTripViewerDivider.setVisibility(View.VISIBLE);
                        }
                        tvItineraryLegTripViewer.setVisibility(View.GONE);
                    } else {
                        if (!Utilities.checkIfToDisplayAccessibilityUI(ActiveRouteActivity.this)) {
                            tvItineraryLegTripViewerDivider.setVisibility(View.GONE);
                        }
                        tvItineraryLegTripViewer.setVisibility(View.GONE);
                    }
                    break;

                default:
                    tvItineraryLegServiceProvider.setVisibility(View.GONE);
                    if (itineraryLeg.getFrom() != null && itineraryLeg.getFrom().getStopId() != null) {
                        llItineraryLegStopID.setVisibility(View.VISIBLE);
                    } else {
                        llItineraryLegStopID.setVisibility(View.GONE);
                    }
                    if (!Utilities.checkIfToDisplayAccessibilityUI(ActiveRouteActivity.this)) {
                        tvItineraryLegTripViewerDivider.setVisibility(View.GONE);
                    }

                    tvItineraryLegTripViewer.setVisibility(View.GONE);
                    break;
            }

            if (itineraryLeg.getAgencyName() != null || (itineraryLeg.getAgency() != null && itineraryLeg.getAgency().getName() != null)) {

                String agencyNameString = itineraryLeg.getAgencyName();

                if (agencyNameString == null) {
                    agencyNameString = itineraryLeg.getAgency().getName();
                }

                String agencyName = Utilities.getMappedAgencyNameForItinerary(systemContext, agencyNameString);
                String logoUrl = Resource.ROUTE_AGENCY_LOGO_BASE_URL +
                        agencyName.toLowerCase().replaceAll(" ", "_");
                tvItineraryLegServiceProvider.setText(Html.fromHtml(getString(R.string.subway_route_provider, agencyName)));

                MyApplication.getInstance().getImageLoader().get(logoUrl, new ImageLoader.ImageListener() {
                    @Override
                    public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                        if (response != null && response.getBitmap() != null) {
                            Bitmap bitmap = getResizedBitmap(response.getBitmap(), Convert.dpToPx(48));
                            if (bitmap != null) {
                                //VerticalImageSpan imageSpan = new VerticalImageSpan(new BitmapDrawable(getResources(), bitmap));
                                ImageSpan imageSpan = new ImageSpan(bitmap, ImageSpan.ALIGN_BOTTOM) {
                                    public void draw(Canvas canvas, CharSequence text, int start,
                                                     int end, float x, int top, int y, int bottom,
                                                     Paint paint) {
                                        Drawable b = getDrawable();
                                        canvas.save();

                                        int transY = bottom - b.getBounds().bottom;
                                        // this is the key
                                        transY -= paint.getFontMetricsInt().descent / 2;

                                        canvas.translate(x, transY);
                                        b.draw(canvas);
                                        canvas.restore();
                                    }
                                };

                                CharSequence text = Html.fromHtml(getString(R.string.subway_route_provider, agencyName));
                                SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(text);
                                spannableStringBuilder.setSpan(imageSpan, text.length() - 1,
                                        text.length(),
                                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                                //spannableStringBuilder.setSpan(new AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER), 0, text.length()-1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                tvItineraryLegServiceProvider.setText(spannableStringBuilder);

                                if (!isImmediate) {
                                    Utilities.setTotalHeightToListViewExt(gDm.widthPixels - Convert.dpToPx(30), lvItineraryLegList);
                                }
                            }
                        }
                    }

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("agencylogo", "failed to load agency logo");
                    }
                });
            }
            tvItineraryLegTripViewerDivider.setVisibility(View.INVISIBLE);
            if (itineraryLeg.getAgency() != null && itineraryLeg.getAgency().getUrl() != null && !itineraryLeg.getAgency().getUrl().isEmpty()) {
                tvItineraryLegServiceProvider.setTag(itineraryLeg.getAgency().getUrl());
                tvItineraryLegServiceProvider.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String agencyUrl = (String) view.getTag();
                        Utilities.GotoTheUrl(systemContext, agencyUrl);
                    }
                });
            }

            if (position == (itineraryLegArrayList.size() - 1)) {
                llItineraryLegEnd.setVisibility(View.VISIBLE);

                tvItineraryLegEndTime.setText(endTime);
                tvItineraryLegEndAddress.setText(toAddress);
            } else {
                llItineraryLegEnd.setVisibility(View.GONE);
            }

            convertView.setTag(position);
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //move map to show specific itinerary leg
                    int position = (Integer) view.getTag();
                    if (position < itineraryLegArrayList.size()) {
                        ItineraryLeg itineraryLeg = itineraryLegArrayList.get(position);
                        currentLegGeometry = itineraryLeg.getLegGeometry();
                        moveMapToShowItineraryLeg();
                    }
                }
            });

            tvItineraryLegStopViewer.setTag(position);
            tvItineraryLegStopViewer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = (Integer) view.getTag();
                    if (position < itineraryLegArrayList.size()) {
                        ItineraryLeg itineraryLeg = itineraryLegArrayList.get(position);
                        stopViewerItineraryLegPoint = itineraryLeg.getFrom();
                        String mode = Utilities.getMappedModeForItinerary(ActiveRouteActivity.this, itineraryLeg.getRouteId(),
                                itineraryLeg.getMode(), itineraryLeg.getRouteShortName(), itineraryLeg.getRouteLongName()).toLowerCase();
                        enterStopViewerPage(mode);
                    }
                }
            });

            tvItineraryLegTripViewer.setTag(position);
            tvItineraryLegTripViewer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = (Integer) view.getTag();
                    if (position < itineraryLegArrayList.size()) {
                        selectedItineraryLeg = itineraryLegArrayList.get(position);
                        if (selectedItineraryLeg.getTrip() != null) {
                            currentSelectedTripId = selectedItineraryLeg.getTripId();
                        } else if (selectedItineraryLeg.getTrip().getGtfsId() != null) {
                            currentSelectedTripId = selectedItineraryLeg.getTrip().getGtfsId();
                        }
                        gotoTripViewerPage();
                    }
                }
            });

            return convertView;
        }
    }

    private void gotoTripViewerPage() {
        if (this != null) {
            Intent intent = new Intent(this, TripViewerActivity.class);
            intent.putExtra(Constants.CURRENT_ITINERARY, currentSelectedItinerary);
            intent.putExtra(Constants.SELECTED_ITINERARY_LEG, selectedItineraryLeg);

            this.startActivityForResult(intent, Constants.TRIP_VIEWER_REQUEST_CODE);
        }
    }

    private void enterStopViewerPage(String mode) {
        if (ActiveRouteActivity.this != null && stopViewerItineraryLegPoint != null) {
            Intent intent = new Intent(systemContext, StopViewerActivity.class);
            intent.putExtra(Constants.STOP_VIEWER_ITINERARY_LEG_POINT, stopViewerItineraryLegPoint);

            if (selectedItinerary != null) {
                intent.putExtra(Constants.CURRENT_ITINERARY, selectedItinerary);
            }

            if (mode != null) {
                intent.putExtra(Constants.STOP_VIEWER_ITINERARY_MODE, mode);
            }
            intent.putExtra(Constants.CURRENT_ORIGIN, originPoint);
            intent.putExtra(Constants.CURRENT_DESTINATION, destinationPoint);

            this.startActivityForResult(intent, Constants.STOP_VIEWER_REQUEST_CODE);
        }
    }

    private void moveMapToShowItineraryLeg() {
        if (gMapboxMap != null && currentLegGeometry != null && currentLegGeometry.getPoints() != null) {
            LatLngBounds.Builder latLngBoundsBuilder = new LatLngBounds.Builder();

            List<Point> legCoordinates = Utilities.decodeGPSPointsForRoute(currentLegGeometry.getPoints());
            if (legCoordinates != null) {
                for (Point point : legCoordinates) {
                    if (point != null) {
                        latLngBoundsBuilder.include(new LatLng(point.latitude(), point.longitude()));
                    }
                }
            }

            if (latLngBoundsBuilder.build().toLatLngs().length > 0) {
                int paddingTop = Convert.dpToPx(36);
                int paddingBottom = currentHeight + Convert.dpToPx(36);
                int paddingHorizontal = Convert.dpToPx(86);

                gMapboxMap.moveCamera(CameraUpdateFactory.newLatLngBounds(latLngBoundsBuilder.build(),
                        paddingHorizontal, paddingTop, paddingHorizontal, paddingBottom), null);
            }

        }
    }

    private Bitmap getResizedBitmap(Bitmap bm, int maxHeight) {
        if (bm != null && bm.getHeight() != 0 && maxHeight != 0) {
            float scale = (float) bm.getHeight() / (float) maxHeight;
            int dstHeight = (int) ((float) bm.getHeight() / scale);
            int dstWidth = (int) ((float) bm.getWidth() / scale);

            return Bitmap.createScaledBitmap(bm, dstWidth, dstHeight, false);
        }

        return null;
    }

    private void populateItineraryLegSteps(LinearLayout llItineraryLegStepsDetail, ArrayList<ItineraryLegStep> steps) {
        if (llItineraryLegStepsDetail != null && steps != null) {
            llItineraryLegStepsDetail.removeAllViews();
            for (int i = 0; i < steps.size(); i++) {
                ItineraryLegStep itineraryLegStep = steps.get(i);

                if (itineraryLegStep != null) {
                    View itemView = null;
                    if (Utilities.checkIfToDisplayAccessibilityUI(this)) {
                        itemView = LayoutInflater.from(systemContext).inflate(R.layout.item_itinerary_leg_step_accessibility, null);
                    } else {
                        itemView = LayoutInflater.from(systemContext).inflate(R.layout.item_itinerary_leg_step, null);
                    }
                    ImageView ivIndicator = itemView.findViewById(R.id.iv_indicator);
                    ImageView ivStepDirection = itemView.findViewById(R.id.iv_step_direction);
                    TextView tvStepDescription = itemView.findViewById(R.id.tv_step_description);

                    String relativeDirection = itineraryLegStep.getRelativeDirection();
                    String absoluteDirection = itineraryLegStep.getAbsoluteDirection();

                    ivIndicator.setVisibility(View.GONE);
                    ivStepDirection.setVisibility(View.VISIBLE);
                    if (relativeDirection != null) {
                        String relativeDirectionString;
                        if (Utilities.directionStringMapping.containsKey(relativeDirection.toUpperCase())) {
                            relativeDirectionString = getString(Utilities.directionStringMapping.get(relativeDirection.toUpperCase()));
                        } else {
                            relativeDirectionString = relativeDirection;
                        }

                        String distance;
                        if (itineraryLegStep.getDistance() < 161) {
                            distance = getString(R.string.feet_with_num, (int) (itineraryLegStep.getDistance() * Constants.METER_TO_FOOT));
                        } else {
                            distance = getString(R.string.miles_with_num, itineraryLegStep.getDistance() / Constants.MILE_TO_METER);
                        }

                        if (i == 0 && absoluteDirection != null) {
                            tvStepDescription.setText(String.format("%s %s", getString(R.string.step_on,
                                    relativeDirectionString + " " + absoluteDirection.toLowerCase(),
                                    itineraryLegStep.getStreetName()), distance));
                        } else {
                            tvStepDescription.setText(String.format("%s %s", getString(R.string.step_on,
                                    relativeDirectionString, itineraryLegStep.getStreetName()), distance));
                        }

                        if (Utilities.directionIconMapping.containsKey(relativeDirection.toUpperCase())) {
                            ivStepDirection.setImageResource(Utilities.directionIconMapping.get(relativeDirection.toUpperCase()));
                        } else {
                            ivStepDirection.setImageResource(R.drawable.ic_straight);
                        }
                    }
                    llItineraryLegStepsDetail.addView(itemView);
                }
            }
        }
    }

    private void populateItineraryLegStops(LinearLayout llItineraryLegStepsDetail, ArrayList<IntermediateStop> intermediateStops, String routeColorString) {
        if (llItineraryLegStepsDetail != null && intermediateStops != null) {
            llItineraryLegStepsDetail.removeAllViews();
            for (int i = 0; i < intermediateStops.size(); i++) {
                IntermediateStop intermediateStop = intermediateStops.get(i);
                if (intermediateStop != null) {
                    View itemView = null;
                    if (Utilities.checkIfToDisplayAccessibilityUI(this)) {
                        itemView = LayoutInflater.from(systemContext).inflate(R.layout.item_itinerary_leg_step_accessibility, null);
                    } else {
                        itemView = LayoutInflater.from(systemContext).inflate(R.layout.item_itinerary_leg_step, null);
                    }
                    ImageView ivIndicator = itemView.findViewById(R.id.iv_indicator);
                    ImageView ivStepDirection = itemView.findViewById(R.id.iv_step_direction);
                    TextView tvStepDescription = itemView.findViewById(R.id.tv_step_description);

                    ivIndicator.setVisibility(View.VISIBLE);
                    ivStepDirection.setVisibility(View.GONE);
                    tvStepDescription.setText(intermediateStop.getName());

                    llItineraryLegStepsDetail.addView(itemView);

                    if (routeColorString != null && (routeColorString.equalsIgnoreCase("#ffffff")
                            || routeColorString.equalsIgnoreCase("ffffff"))) {
                        ivIndicator.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(android.R.color.black)));
                    } else {
                        ivIndicator.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(android.R.color.white)));
                    }
                }
            }
        }
    }

    private int getAccessibilityColorForLeg(ItineraryLeg leg) {
        if (leg.getAccessibilityScore() != null) {
            double score = leg.getAccessibilityScore();
            if (score < 0.5) {
                return R.color.unaccessible;
            } else if (score >= 0.5 && score < 0.8) {
                return R.color.likely_acessibile;
            } else if (score >= 0.8) {
                return R.color.accessibile;
            }
        }
        return R.color.white;
    }

    private final ArrayList<ServiceAlert> serviceAlerts = new ArrayList<>();


    private ServiceAlertAdapter getServiceAlertAdapter() {
        ArrayList<HashMap<String, Object>> data = new ArrayList<HashMap<String, Object>>();

        for (int i = 0; i < serviceAlerts.size(); i++) {
            ServiceAlert serviceAlert = serviceAlerts.get(i);

            HashMap<String, Object> map = new HashMap<String, Object>();

            map.put("tv_alert_title", serviceAlert.getAlertHeaderText());

            data.add(map);
        }

        ServiceAlertAdapter mServiceAlertAdapter = new ServiceAlertAdapter(
                systemContext, data,
                R.layout.item_service_alerts,
                new String[]{"tv_alert_title"}, new int[]{R.id.tv_alert_title});

        return mServiceAlertAdapter;
    }

    class ServiceAlertAdapter extends SimpleAdapter {
        Context mContext;
        private final LayoutInflater l_Inflater;

        public ServiceAlertAdapter(Context context,
                                   List<? extends Map<String, ?>> data, int resource,
                                   String[] from, int[] to) {
            super(context, data, resource, from, to);

            mContext = context;
            l_Inflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return serviceAlerts.size();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ServiceAlert serviceAlert = serviceAlerts.get(position);

            if (convertView == null) {
                convertView = l_Inflater.inflate(
                        R.layout.item_service_alerts, null);
            }


            ImageView ivExpand = convertView.findViewById(R.id.im_expand);

            TextView tvAlertCollapseTitle = convertView.findViewById(R.id.tv_alert_title);
            TextView tvAlertExpandTitle = convertView.findViewById(R.id.tv_alert_expand_title);
            ImageView ivAlertCollapse = convertView.findViewById(R.id.im_close);

            TextView tvAlertDescription = convertView.findViewById(R.id.tv_alert_description);
            TextView tvAlertDate = convertView.findViewById(R.id.tv_alert_date);

            LinearLayout llAlertTitleCollapse = convertView.findViewById(R.id.ll_alert_title_collapse);
            LinearLayout llAlertTitleExpand = convertView.findViewById(R.id.ll_alert_title_expand);
            LinearLayout llAlertExpand = convertView.findViewById(R.id.ll_alert_expand);

            LinearLayout llMore = convertView.findViewById(R.id.ll_more);


            if (serviceAlert.getAlertHeaderText() != null && !serviceAlert.getAlertHeaderText().isEmpty()) {
                tvAlertExpandTitle.setText(serviceAlert.getAlertHeaderText());
                tvAlertCollapseTitle.setText(serviceAlert.getAlertHeaderText());
            } else {
                tvAlertExpandTitle.setText("");
                tvAlertCollapseTitle.setText("");
            }

            if (serviceAlert.getAlertDescriptionText() != null && !serviceAlert.getAlertDescriptionText().isEmpty()) {
                tvAlertDescription.setText(serviceAlert.getAlertDescriptionText());
            } else {
                tvAlertDescription.setText("");
            }

            if (serviceAlert.getEffectiveStartDate() != null) {

                SimpleDateFormat simpleDateFormatForDate = new SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault());
                Date dt = new Date(serviceAlert.getEffectiveStartDate() * 1000);
                simpleDateFormatForDate.setTimeZone(TimeZone.getTimeZone(Configurations.TARGET_TIMEZONE_ID));
                tvAlertDate.setText(String.format("%s %s", getString(R.string.effective_as_of), simpleDateFormatForDate.format(dt)));

            } else {
                tvAlertDate.setText("");
            }

            if (serviceAlert.getAlertUrl() != null && !serviceAlert.getAlertUrl().isEmpty()) {

                llMore.setVisibility(View.VISIBLE);

                llMore.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Utilities.GotoTheUrl(mContext, Utilities.getUrlWithLanguageFor(serviceAlert.getAlertUrl()));
                    }
                });

            } else {
                llMore.setVisibility(View.GONE);
            }

            llAlertTitleCollapse.setVisibility(View.VISIBLE);
            llAlertExpand.setVisibility(View.GONE);

            llAlertTitleCollapse.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    llAlertTitleCollapse.setVisibility(View.GONE);
                    llAlertExpand.setVisibility(View.VISIBLE);


                }
            });


            llAlertTitleExpand.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    llAlertTitleCollapse.setVisibility(View.VISIBLE);
                    llAlertExpand.setVisibility(View.GONE);


                }
            });


            return convertView;
        }
    }

    private void setupServiceAlerts() {
        ivAlertsClose = findViewById(R.id.iv_alerts_close);
        viewRouteHeaderAlerts = findViewById(R.id.view_route_number_header_alerts);
        tvRouteNumberAlerts = findViewById(R.id.tv_itinerary_leg_route_number_alerts);
        lvServiceAlerts = findViewById(R.id.lv_service_alert);
        rlServiceAlerts = findViewById(R.id.rl_service_alerts);
        tvNumberOfAlerts = findViewById(R.id.tv_numbers_of_alerts);
        tvNumberOfAlertsAccessbility = findViewById(R.id.tv_numbers_of_alerts_accessibility);

        serviceAlertAdapter = getServiceAlertAdapter();
        lvServiceAlerts.setAdapter(serviceAlertAdapter);

        ivAlertsClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rlServiceAlerts.setVisibility(View.GONE);
            }
        });
    }

    private void loadDataInServiceAlerts(ItineraryLeg itineraryLeg) {
        if (viewRouteHeaderAlerts != null && tvRouteNumberAlerts != null && itineraryLeg != null) {

            String itineraryMode;
            if (itineraryLeg.getMode() != null) {
                itineraryMode = Utilities.getMappedModeForItinerary(this, itineraryLeg.getRouteId(),
                        itineraryLeg.getMode(), itineraryLeg.getRouteShortName(), itineraryLeg.getRouteLongName()).toLowerCase();
            } else {
                itineraryMode = "walk";
            }

            String routeColorString = itineraryLeg.getRouteColor();
            String textColor = itineraryLeg.getRouteTextColor();
            if (routeColorString == null && itineraryLeg.getOriginalRoute() != null) {
                routeColorString = itineraryLeg.getOriginalRoute().getColor();
                textColor = itineraryLeg.getOriginalRoute().getTextColor();
            }

            int routeColor;
            int textBackgroundColorInt;
            if (routeColorString == null) {
                routeColor = Utilities.getRouteColorByMode(ActiveRouteActivity.this, itineraryMode);
                textBackgroundColorInt = 0xFFD9D9D9;  //default grey value
            } else {
                if (!routeColorString.startsWith("#")) {
                    routeColorString = "#" + routeColorString;
                }
                routeColor = Color.parseColor(routeColorString);

                textBackgroundColorInt = routeColor;
                if (routeColor < 0x1000000) {
                    textBackgroundColorInt = routeColor + 0xAF000000;
                }
            }

            if (textColor != null) {
                if (!textColor.startsWith("#")) {
                    textColor = "#" + textColor;
                }
                tvRouteNumberAlerts.setTextColor(Color.parseColor(textColor));
            }


            if (itineraryLeg.getRouteShortName() != null && !itineraryLeg.getRouteShortName().isEmpty()) {
                tvRouteNumberAlerts.setVisibility(View.VISIBLE);
                tvRouteNumberAlerts.setText(itineraryLeg.getRouteShortName());

                viewRouteHeaderAlerts.setVisibility(View.VISIBLE);
                tvRouteNumberAlerts.setBackgroundTintList(ColorStateList.valueOf(textBackgroundColorInt));
                tvRouteNumberAlerts.setBackgroundResource(R.drawable.bottom_rounded_white_with_shadow_bg);
                viewRouteHeaderAlerts.setBackgroundTintList(ColorStateList.valueOf(routeColor));

            } else if (itineraryLeg.getRoute() != null && !itineraryLeg.getRoute().isEmpty()) {
                tvRouteNumberAlerts.setVisibility(View.VISIBLE);
                tvRouteNumberAlerts.setText(itineraryLeg.getRoute());

                viewRouteHeaderAlerts.setVisibility(View.VISIBLE);
                tvRouteNumberAlerts.setBackgroundTintList(ColorStateList.valueOf(textBackgroundColorInt));
                tvRouteNumberAlerts.setBackgroundResource(R.drawable.bottom_rounded_white_with_shadow_bg);
                viewRouteHeaderAlerts.setBackgroundTintList(ColorStateList.valueOf(routeColor));
            } else {
                tvRouteNumberAlerts.setVisibility(View.GONE);
                viewRouteHeaderAlerts.setVisibility(View.GONE);
            }
        }

        if (serviceAlerts.size() == 1) {
            tvNumberOfAlerts.setText(getString(R.string.one_alert));
        } else {
            tvNumberOfAlerts.setText(String.format("%s %s", serviceAlerts.size() + "", getString(R.string.more_alert)));
        }

        if (serviceAlerts.size() == 1) {
            tvNumberOfAlertsAccessbility.setText(getString(R.string.one_alert));
        } else {
            tvNumberOfAlertsAccessbility.setText(String.format("%s %s", serviceAlerts.size() + "", getString(R.string.more_alert)));
        }

        if (Utilities.checkIfToDisplayAccessibilityUI(this)) {
            tvNumberOfAlertsAccessbility.setVisibility(View.VISIBLE);
            tvNumberOfAlerts.setVisibility(View.GONE);
        } else {
            tvNumberOfAlertsAccessbility.setVisibility(View.GONE);
            tvNumberOfAlerts.setVisibility(View.VISIBLE);
        }


        if (rlServiceAlerts != null) {
            rlServiceAlerts.setVisibility(View.VISIBLE);
        }
    }

    private ItineraryLegListAdapter getItineraryLegListAdapter() {
        ArrayList<HashMap<String, Object>> data = new ArrayList<HashMap<String, Object>>();

        for (int i = 0; i < itineraryLegArrayList.size(); i++) {
            ItineraryLeg itineraryLeg = itineraryLegArrayList.get(i);

            HashMap<String, Object> map = new HashMap<String, Object>();

            map.put("tv_duration", itineraryLeg.getDuration());

            data.add(map);
        }

        ItineraryLegListAdapter mItineraryLegListAdapter = new ItineraryLegListAdapter(
                systemContext, data,
                R.layout.item_itinerary_leg,
                new String[]{"tv_duration"}, new int[]{R.id.tv_duration});

        return mItineraryLegListAdapter;
    }

    private void onLocationChangeHandler(final Location location) {
        MyApplication.getInstance().currentLocation = location;
        if (locateMeEnabled) {
            moveMapToCurrentLocation();
        }
        refreshActiveRoute();

        if (location != null) {
            TrackingLocation trackingLocation = new TrackingLocation();
            trackingLocation.setBearing(location.getBearing());
            trackingLocation.setLat(location.getLatitude());
            trackingLocation.setLon(location.getLongitude());
            trackingLocation.setSpeed(location.getSpeed());
            trackingLocation.setTimestamp(location.getTime() / 1000);
            trackingLocation.setLocationAccuracy(location.getAccuracy());

            updateTrackingLocations.add(trackingLocation);
        }

        detectJmapLocation();
    }

    /**
     * Receiver for broadcasts sent by {@link LocationUpdatesService}.
     */
    private class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Location location;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                location = intent.getParcelableExtra(NewLocationManager.getInstance(ActiveRouteActivity.this).getExtraLocation());
            } else {
                location = intent.getParcelableExtra(LocationUpdatesService.getExtraLocation());
            }

            if (location != null) {
                //Logger.i("BroadcastReceiver", "lat:" + location.getLatitude() + ",lng:" + location.getLongitude());

                onLocationChangeHandler(location);
            }
        }
    }

    public void startMyReceiver() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            LocalBroadcastManager.getInstance(this).registerReceiver(myReceiver,
                    new IntentFilter(NewLocationManager.getInstance(this).getActionBroadcast()));
        } else {
            mServiceConnection = new ServiceConnection() {

                @Override
                public void onServiceConnected(ComponentName name, IBinder service) {
                    LocationUpdatesService.LocalBinder binder = (LocationUpdatesService.LocalBinder) service;
                    mService = binder.getService();
                    mBound = true;
                }

                @Override
                public void onServiceDisconnected(ComponentName name) {
                    mService = null;
                    mBound = false;
                }
            };
            // Bind to the service. If the service is in foreground mode, this signals to the service
            // that since this activity is in the foreground, the service can exit foreground mode.
            bindService(new Intent(this, LocationUpdatesService.class), mServiceConnection, Context.BIND_AUTO_CREATE);
            LocalBroadcastManager.getInstance(this).registerReceiver(myReceiver,
                    new IntentFilter(LocationUpdatesService.getActionBroadcast()));
        }
    }

    private void startLocationManager() {
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (myReceiver == null) {
                myReceiver = new MyReceiver();
            }
            startMyReceiver();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                startLocationWork();
            }
        }
    }

    private void startLocationWork() {
        OneTimeWorkRequest foregroundWorkRequest = new OneTimeWorkRequest.Builder(LocationWork.class)
                .addTag("LocationWork")
                .setBackoffCriteria(BackoffPolicy.LINEAR, OneTimeWorkRequest.MIN_BACKOFF_MILLIS, TimeUnit.SECONDS)
                .build();
        WorkManager.getInstance(ActiveRouteActivity.this).enqueue(foregroundWorkRequest);
    }

    private void stopAllWork() {
        WorkManager.getInstance(ActiveRouteActivity.this).cancelAllWork();
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

    public void speakOut(AudioAlertElement audioAlertElement) {
        if (audioAlertElement == null || !Configurations.getBoolean("live_tracking_audio_announcement_enabled", true)) {
            return;
        }

        audioAlertElement.setPlayedTimestamp(System.currentTimeMillis() / 1000);
        MyApplication.getInstance().lastAudioAlertElement = audioAlertElement;

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
        File file = this.getFileStreamPath(Constants.TTS_AUDIO_FILE_NAME);

        if (file.exists()) {
            file.delete();
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
//				    am.registerMediaButtonEventReceiver(RemoteControlReceiver);
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
//				    am.registerMediaButtonEventReceiver(RemoteControlReceiver);
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

            int status = tts.synthesizeToFile(text, params, file.getAbsolutePath());
            Logger.d("TTS", "status:" + status);
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
        AudioAlertElement audioAlertElement = new AudioAlertElement();

        audioAlertElement.setDescription(message);
        audioAlertElement.setAlertID(null);
        audioAlertElement.setClustergroup(0);

        speakOut(audioAlertElement);
    }

    private ImageView ivMute, ivAudioNotification;
    private LinearLayout llAudioNotificationPopup;
    private TextView tvAudioNotification;

    private void setupMuteButton() {
        ivMute = findViewById(R.id.iv_mute);
        llAudioNotificationPopup = findViewById(R.id.ll_audio_notification_popup);
        tvAudioNotification = findViewById(R.id.tv_audio_notification);
        ivAudioNotification = findViewById(R.id.iv_audio_notification);
        if (Configurations.getBoolean("live_tracking_audio_announcement_enabled", true)) {
            ivMute.setImageResource(R.drawable.ic_preview_trip_notification);
        } else {
            ivMute.setImageResource(R.drawable.ic_preview_trip_notification_mute);
        }
        ivMute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                llAudioNotificationPopup.setVisibility(View.VISIBLE);

                boolean indoorNavigationAudioAnnouncementEnabled = Configurations.getBoolean("live_tracking_audio_announcement_enabled", true);
                indoorNavigationAudioAnnouncementEnabled = !indoorNavigationAudioAnnouncementEnabled;
                Configurations.setBoolean("live_tracking_audio_announcement_enabled", indoorNavigationAudioAnnouncementEnabled);

                if (indoorNavigationAudioAnnouncementEnabled) {
                    tvAudioNotification.setText(getString(R.string.audio_notification_on));// current state is on
                    ivMute.setImageResource(R.drawable.ic_preview_trip_notification);
                    ivAudioNotification.setImageResource(R.drawable.ic_audio_notification);
                } else {
                    tvAudioNotification.setText(getString(R.string.audio_notification_off));
                    ivMute.setImageResource(R.drawable.ic_preview_trip_notification_mute);
                    ivAudioNotification.setImageResource(R.drawable.ic_mutebell);

                    MyApplication.getInstance().audioAlertElementArray.clear();
                    if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                        mediaPlayer.stop();
                    }
                    MyApplication.getInstance().alertPlaybackStatus = Constants.AUDIO_PLAYBACK_IDLE;
                }

                ivMute.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        llAudioNotificationPopup.setVisibility(View.GONE);
                    }
                }, 3000);
            }
        });
    }
}