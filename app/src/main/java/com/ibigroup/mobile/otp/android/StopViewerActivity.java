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


import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static com.ibigroup.mobile.otp.android.utility.Constants.MENU_ACTION_FUNCTION;
import static com.ibigroup.mobile.otp.android.utility.Constants.MENU_ACTION_TRIGGER_EXTERNAL_WEB;
import static com.ibigroup.mobile.otp.android.utility.Constants.USER_NAME_TAG;
import static com.ibigroup.mobile.otp.android.utility.Convert.dpToPx;
import static com.ibigroup.mobile.otp.android.utility.ServerDelegate.volleyAccountStringRequest;
import static com.ibigroup.mobile.otp.android.utility.ServerDelegate.volleyStringRequest;
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

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Html;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityEvent;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.view.AccessibilityDelegateCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.StringRequest;
import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.ApolloClient;
import com.apollographql.apollo.exception.ApolloException;
import com.auth0.android.Auth0;
import com.auth0.android.authentication.AuthenticationAPIClient;
import com.auth0.android.authentication.AuthenticationException;
import com.auth0.android.callback.Callback;
import com.auth0.android.lock.AuthenticationCallback;
import com.auth0.android.lock.InitialScreen;
import com.auth0.android.lock.Lock;
import com.auth0.android.lock.UsernameStyle;
import com.auth0.android.result.Credentials;
import com.auth0.android.result.UserProfile;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import com.google.maps.android.SphericalUtil;
import com.ibigroup.mobile.otp.android.api.Resource;
import com.ibigroup.mobile.otp.android.customize.CustomizeMarkerView;
import com.ibigroup.mobile.otp.android.json.AddedLocation;
import com.ibigroup.mobile.otp.android.json.CreateNewUserRequest;
import com.ibigroup.mobile.otp.android.json.LocationPoint;
import com.ibigroup.mobile.otp.android.json.MenuItem;
import com.ibigroup.mobile.otp.android.json.ParkRideLocation;
import com.ibigroup.mobile.otp.android.json.RentalVehicle;
import com.ibigroup.mobile.otp.android.json.RouteAgency;
import com.ibigroup.mobile.otp.android.json.RouteViewerRoute;
import com.ibigroup.mobile.otp.android.json.StopViewerSchedule;
import com.ibigroup.mobile.otp.android.json.StopViewerTime;
import com.ibigroup.mobile.otp.android.json.TransitStop;
import com.ibigroup.mobile.otp.android.json.UserPreferences;
import com.ibigroup.mobile.otp.android.json.itinerary.Itinerary;
import com.ibigroup.mobile.otp.android.json.itinerary.ItineraryLeg;
import com.ibigroup.mobile.otp.android.json.itinerary.ItineraryLegGeometry;
import com.ibigroup.mobile.otp.android.json.itinerary.ItineraryLegPoint;
import com.ibigroup.mobile.otp.android.manager.ParkRideManager;
import com.ibigroup.mobile.otp.android.manager.StopViewerNextArrivalManager;
import com.ibigroup.mobile.otp.android.manager.TransitStopManager;
import com.ibigroup.mobile.otp.android.utility.Configurations;
import com.ibigroup.mobile.otp.android.utility.Constants;
import com.ibigroup.mobile.otp.android.utility.Logger;
import com.ibigroup.mobile.otp.android.utility.ServerDelegate;
import com.ibigroup.mobile.otp.android.utility.Utilities;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.LineString;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.geometry.LatLngBounds;
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

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;


public class StopViewerActivity extends BaseActivity {
    private DisplayMetrics gDm = new DisplayMetrics();
    private ItineraryLegPoint stopViewerItineraryLegPoint = null;
    private String stopMode = null;
    private int gpsReverseMode = Constants.GPS_REVERSE_MODE_MARKER_VIEW;
    private StringRequest gpsReverseRequest = null;
    private com.mapbox.mapboxsdk.maps.MapView mapView;
    private MapboxMap gMapboxMap;
    private Style gMapStyle;
    private String mapCurrentStyle;
    private RelativeLayout rlMapView;
    private ImageView ivStopViewerSave;
    private boolean isFavoured;
    private Map<String, String> uniqueAgentsUrl = new HashMap<String, String>();
    private LocationPoint originPoint = null;
    private LocationPoint destinationPoint = null;
    private Itinerary selectedItinerary = null;
    private CustomizeMarkerView mvDestination = null, mvOrigin = null, mvStop = null;
    private MarkerViewManager markerViewManager = null;
    private LinearLayout llBottomTab;

    private final ArrayList<MenuItem> menuItems = new ArrayList<>();
    public DrawerLayout mDrawerLayout;
    public ListView mDrawerList;
    private LinearLayout llDrawer;
    private MenuListAdapter menuListAdapter;

    private LatLng markerViewLatlng = null;
    private View customView;
    private MarkerView markerView = null;

    private TransitStop selectedTransitStop;
    private ParkRideLocation selectedParkRideLocation;
    private RentalVehicle selectedRentalVehicle;
    private int currentSelectedLocationMode = Constants.SELECTED_LOCATION_MODE_POINT;

    private TextView tvMapAddress, tvMarkerStopID;
    private ImageView ivMarkerViewClose;
    private NetworkImageView ivAgencyLogo;
    private TextView atSearchOrigin, atSearchDestination, tvOriginUnderline;
    private LinearLayout llMarkerStopViewer, llMarkerBtnStopViewer;
    private TextView tv_itinerary_origin, tv_itinerary_Destination;

    private String address;

    private final int currentMode = Constants.HOME_PAGE_MODE_START_OVER;
    private SymbolLayer unclusteredParkRide, unclusteredTransitStop, unclusteredRentalVehicle;

    private GeoJsonSource parkRideSource, transitStopSource, rentalVehicleSource;

    private TransitStopManager.TransitStopListener transitStopListener;
    private ParkRideManager.ParkRideListener parkRideListener;
    private final ArrayList<LatLng> itineraryLatLngs = new ArrayList<>();
    private final ArrayList<String> sourceIDsForRoutePolyline = new ArrayList<String>();
    private final ArrayList<String> layerIDsForRoutePolyline = new ArrayList<String>();
    private final ArrayList<MarkerView> routeMarkerViews = new ArrayList<>();
    private Lock lock = null;

    private TextView tvStopViewerTitle, tvStopViewerStopID, tvStopViewerRefreshTime,
            tvStopViewerDate, tvStopViewerFromHere, tvStopViewerToHere;
    private ImageView ivStopViewerRefreshTime;

    private LinearLayout llStopViewer, llStopViewerScheduleTitle, llStopViewerDate,
            llStopViewerAutoRefresh, llStopViewerRefreshTime, llHeaderHolder;
    private ListView lvStopViewerArrivalsList, lvStopViewerSchedule;
    private NextArrivalListAdapter nextArrivalListAdapter;
    private ScheduleTimeListAdapter scheduleTimeListAdapter;
    private RelativeLayout rlStopViewerDate;
    private RelativeLayout rlActivityStopViewer;
    private DatePicker dpStopViewerDate;
    private SwitchCompat swStopViewerAutoRefresh;
    private Animation aniRotateClk = null;

    private final ArrayList<StopViewerSchedule> nextArrivals = new ArrayList<>();
    private final ArrayList<Boolean> nextArrivalExpandStatusArray = new ArrayList<>();
    private StopViewerNextArrivalManager.NextArrivalListener nextArrivalListener;
    private final ArrayList<StopViewerTime> scheduleTimes = new ArrayList<>();
    private Comparator<StopViewerTime> stopViewerTimeComparator;
    private int nextScheduleTimeIndex = -1;
    private String currentRouteID = null;
    private final ArrayList<String> routeViewerRoutePoints = new ArrayList<>();
    private final ArrayList<Source> routeViewerRouteSources = new ArrayList<>();
    private final ArrayList<LineLayer> routeViewerRouteLayers = new ArrayList<>();
    private StringRequest getRouteViewerRouteGeometryRequest = null;

    private LinearLayout rlAutoRefresh, llNoDataForArrivals;
    private RelativeLayout rlDepartures, rlSchedule;
    private TextView tvDepartures, tvSchedule;

    private DatePickerDialog datePickerDialog = null;

    private int currentDatePickerYear = 0;
    private int currentDatePickerMonth = 0;
    private int currentDatePickerDayOfMonth = 0;

    private int datePickerYearTmp = 0;
    private int datePickerMonthTmp = 0;
    private int datePickerDayOfMonthTmp = 0;

    private ScrollView svStopViewer;

    private LinearLayout ll_tab_pan_trip, ll_tab_routes, ll_tab_stops, ll_tab_my_trips, ll_tab_sign_in, ll_tab_menu;
    private ImageView iv_tab_pan_trip, iv_tab_routes, iv_tab_stops, iv_tab_my_trips, iv_tab_sign_in;
    private TextView tv_tab_pan_trip, tv_tab_routes, tv_tab_stops, tv_tab_my_trips, tv_tab_sign_in, tv_user_name;
    private int currentSelectedTab = MyApplication.getInstance().currentSelectedTab;
    private RelativeLayout rl_user_name;

    private LinearLayout ll_bottom_tab_menu_item_four, ll_bottom_tab_menu_item_five, ll_bottom_tab_menu_item_three, ll_bottom_tab_menu_item_two, ll_bottom_tab_menu_item_one, ll_bottom_tab_menu_item_zero, ll_bottom_tab_menu_item, ll_bottom_tab_accessibility;
    private ImageView iv_bottom_tab_menu_item_four, iv_bottom_tab_menu_item_three, iv_bottom_tab_menu_item_two, iv_bottom_tab_menu_item_one, iv_bottom_tab_menu_item, iv_down_button, iv_bottom_tab_menu_item_five_expand;
    private TextView tv_bottom_tab_menu_item_four, tv_bottom_tab_menu_item_three, tv_bottom_tab_menu_item_two, tv_bottom_tab_menu_item_one, tv_bottom_tab_menu_item,
            tv_bottom_tab_menu_user_name, tv_bottom_tab_user_name_item_zero, tv_bottom_tab_user_name_item_one, tv_bottom_tab_user_name_item_two, tv_bottom_tab_user_name_item_three, tv_bottom_tab_user_name_item_four;
    private RelativeLayout rl_bottom_tab_menu_accessibility, rl_bottom_tab_menu_user_name, rl_bottom_tab_user_name_item_zero, rl_bottom_tab_user_name_item_one, rl_bottom_tab_user_name_item_two,
            rl_bottom_tab_user_name_item_three, rl_bottom_tab_user_name_item_four;

    static final Map<String, Integer> assetsMapping = new HashMap<String, Integer>() {
        {
            put("ic_leavefeedbacks", R.drawable.ic_leavefeedbacks);
            put("ic_alerts", R.drawable.ic_alerts);
            put("ic_accessibility", R.drawable.ic_accessibility);
            put("ic_safety", R.drawable.ic_safety);
            put("ic_pay", R.drawable.ic_pay);
            put("ic_stations", R.drawable.ic_stations);
            put("ic_help_menu", R.drawable.ic_help_menu);
            put("ic_feedback", R.drawable.ic_feedback);
            put("ic_setting", R.drawable.ic_setting);
            put("ic_info", R.drawable.ic_info);
        }
    };

    private void initMenuItemsData() {
        try {
            String items = Utilities.loadDataFromAsset(this, "json/menu_items.json");
            if (items != null && !items.isEmpty()) {
                JSONObject itemsObject = new JSONObject(items);
                Iterator<String> iterator = itemsObject.keys();
                Gson gson = new GsonBuilder().serializeNulls().create();

                menuItems.clear();
                while (iterator.hasNext()) {
                    String key = iterator.next();
                    Object value = itemsObject.get(key);

                    MenuItem menuItem = gson.fromJson(value.toString(), MenuItem.class);

                    if (menuItem != null) {
                        if (menuItem.isOn()) {
                            menuItems.add(menuItem);
                        }
                    }

                }

                Collections.sort(menuItems, new Comparator<MenuItem>() {
                    @Override
                    public int compare(MenuItem a, MenuItem b) {
                        return (a.getOrder() - b.getOrder());
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private MenuListAdapter getMenuListAdapter() {
        ArrayList<HashMap<String, Object>> data = new ArrayList<HashMap<String, Object>>();

        for (int i = 0; i < menuItems.size(); i++) {
            MenuItem menuItem = menuItems.get(i);

            HashMap<String, Object> map = new HashMap<String, Object>();

            map.put("tv_menu_name", menuItem.getTitle());

            data.add(map);
        }

        MenuListAdapter mMenuListAdapter = new MenuListAdapter(
                this, data,
                R.layout.item_menu,
                new String[]{"tv_menu_name"}, new int[]{R.id.tv_menu_name});

        return mMenuListAdapter;
    }

    class MenuListAdapter extends SimpleAdapter {
        Context mContext;
        private final LayoutInflater l_Inflater;

        public MenuListAdapter(Context context,
                               List<? extends Map<String, ?>> data, int resource,
                               String[] from, int[] to) {
            super(context, data, resource, from, to);

            mContext = context;
            l_Inflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return menuItems.size();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            MenuItem menuItem = menuItems.get(position);

            if (convertView == null) {
                convertView = l_Inflater.inflate(
                        R.layout.item_menu, null);
            }

            TextView tvName = convertView.findViewById(R.id.tv_menu_name);
            ImageView ivMenu = convertView.findViewById(R.id.iv_menu);

            if (Utilities.isCurrentLanguageDefault()) {
                tvName.setText(menuItem.getTitle());
            } else {
                if (menuItem.getTitle() != null && Utilities.stringMapping.containsKey(menuItem.getTitle())) {
                    int strID = Utilities.stringMapping.get(menuItem.getTitle());
                    tvName.setText(strID);
                } else {
                    tvName.setText(menuItem.getTitle());
                }
            }
            if (menuItem.getIcon() != null && assetsMapping.containsKey(menuItem.getIcon())) {
                int resID = assetsMapping.get(menuItem.getIcon());
                ivMenu.setImageResource(resID);
            } else {
                ivMenu.setImageResource(R.drawable.ic_info);
            }

            return convertView;
        }
    }

    private CustomizeMarkerView addMarkerView(LatLng latlng, boolean isStopMarker, boolean isOrigin) {
        CustomizeMarkerView markerViewRouteOriginDestination = null;
        View view;
        ImageView ivMarkerViewIcon;

        if (isStopMarker) {
            view = getLayoutInflater().inflate(R.layout.layout_marker_view_stop_viewer, null);
            view.setLayoutParams(new FrameLayout.LayoutParams(dpToPx(52), dpToPx(52)));
            ivMarkerViewIcon = view.findViewById(R.id.iv_marker_view_icon);
            if (stopMode != null) {
                ivMarkerViewIcon.setImageResource(Utilities.getModeImageByMode(
                        stopMode.toLowerCase(Locale.ROOT), false, getApplicationContext(), ivMarkerViewIcon, ivMarkerViewIcon));
            } else {
                ivMarkerViewIcon.setImageResource(R.drawable.ic_bus);
            }
        } else {
            view = getLayoutInflater().inflate(R.layout.layout_marker_view_origin_destination, null);
            view.setLayoutParams(new FrameLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT));
            ivMarkerViewIcon = view.findViewById(R.id.iv_marker_view_icon);

            if (isOrigin) {
                ivMarkerViewIcon.setImageResource(R.drawable.ic_origin_small);
            } else {
                ivMarkerViewIcon.setImageResource(R.drawable.ic_end);
            }
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

        if (markerViewManager != null) {
            markerViewManager.addMarker(markerViewRouteOriginDestination);
        }

        return markerViewRouteOriginDestination;
    }

    private void showOriginMarker() {
        if (gMapboxMap != null && markerViewManager != null) {
            Double lat = null;
            Double lng = null;
            if (itineraryLatLngs.size() > 0) {
                lat = itineraryLatLngs.get(0).getLatitude();
                lng = itineraryLatLngs.get(0).getLongitude();
            } else if (originPoint != null) {
                lat = originPoint.getLat();
                lng = originPoint.getLon();
            }

            if (lat != null && lng != null) {
                if (mvOrigin == null) {
                    mvOrigin = addMarkerView(new LatLng(lat, lng), false, true);
                } else {
                    mvOrigin.setLatLng(new LatLng(lat, lng));
                    mvOrigin.setMarkerViewLatLng(new LatLng(lat, lng));
                }
            }
        }
    }

    private void showDestinationMarker() {
        if (gMapboxMap != null && markerViewManager != null) {
            Double lat = null;
            Double lng = null;
            if (itineraryLatLngs.size() > 0) {
                lat = itineraryLatLngs.get(itineraryLatLngs.size() - 1).getLatitude();
                lng = itineraryLatLngs.get(itineraryLatLngs.size() - 1).getLongitude();
            } else if (destinationPoint != null) {
                lat = destinationPoint.getLat();
                lng = destinationPoint.getLon();
            }

            if (lat != null && lng != null) {
                if (mvDestination == null) {
                    mvDestination = addMarkerView(new LatLng(lat, lng), false, false);
                } else {
                    mvDestination.setLatLng(new LatLng(lat, lng));
                    mvDestination.setMarkerViewLatLng(new LatLng(lat, lng));
                }
            }
        }
    }

    private void removeOriginMarker() {
        if (mvOrigin != null) {
            markerViewManager.removeMarker(mvOrigin);
            mvOrigin = null;
        }
    }

    private void removeDestinationMarker() {
        if (mvDestination != null) {
            markerViewManager.removeMarker(mvDestination);
            mvDestination = null;
        }
    }

    private void removeRoutePolylineFromMap() {
        if (gMapStyle != null) {
            for (String layerID : layerIDsForRoutePolyline) {
                gMapStyle.removeLayer(layerID);
            }
            for (String sourceID : sourceIDsForRoutePolyline) {
                gMapStyle.removeSource(sourceID);
            }

            layerIDsForRoutePolyline.clear();
            sourceIDsForRoutePolyline.clear();
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

    private void drawRoutePolylineOnMap(Itinerary itinerary) {
        if (gMapStyle != null) {
            removeRoutePolylineFromMap();
            removeRouteMarkerViewsFromMap();
            itineraryLatLngs.clear();
            ArrayList<ItineraryLeg> itineraryLegs = itinerary.getLegs();
            for (int i = 0; i < itineraryLegs.size(); i++) {
                ItineraryLeg itineraryLeg = itineraryLegs.get(i);
                if (itineraryLeg.getLegGeometry() != null && itineraryLeg.getLegGeometry().getPoints() != null) {
                    String routeColorString = itineraryLeg.getRouteColor();
                    String textColor = itineraryLeg.getRouteTextColor();
                    if (routeColorString == null && itineraryLeg.getOriginalRoute() != null) {
                        routeColorString = itineraryLeg.getOriginalRoute().getColor();
                        textColor = itineraryLeg.getOriginalRoute().getTextColor();
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
                    for (Point point : routeCoordinates) {
                        itineraryLatLngs.add(new LatLng(point.latitude(), point.longitude()));
                    }

                    String sourceID = "line-source" + i;
                    String layerID = "linelayer" + i;
                    sourceIDsForRoutePolyline.add(sourceID);
                    layerIDsForRoutePolyline.add(layerID);

                    Feature feature = Feature.fromGeometry(
                            LineString.fromLngLats(routeCoordinates)
                    );
                    feature.addNumberProperty("zIndex", 1.0);
                    gMapStyle.addSource(new GeoJsonSource(sourceID,
                            FeatureCollection.fromFeatures(new Feature[]{feature})));

                    if (mode.equals("walk") || mode.equals("bike") || mode.equals("bicycle") || mode.equals("car") || mode.equals("micromobility")) {
                        if (mode.equals("car")) {
                            gMapStyle.addLayer(new LineLayer(layerID, sourceID).withProperties(
                                    PropertyFactory.lineDasharray(new Float[]{2.0f, 0.5f}),
                                    PropertyFactory.lineCap(Property.LINE_CAP_SQUARE),
                                    PropertyFactory.lineJoin(Property.LINE_JOIN_BEVEL),
                                    PropertyFactory.lineWidth(5.0f),
                                    PropertyFactory.symbolZOrder(SYMBOL_Z_ORDER_AUTO),
                                    PropertyFactory.lineColor(routeColor)
                            ));
                        } else {
                            gMapStyle.addLayer(new LineLayer(layerID, sourceID).withProperties(
                                    PropertyFactory.lineDasharray(new Float[]{0.01f, 2.0f}),
                                    PropertyFactory.lineCap(Property.LINE_CAP_ROUND),
                                    PropertyFactory.lineJoin(Property.LINE_JOIN_ROUND),
                                    PropertyFactory.lineWidth(5.0f),
                                    PropertyFactory.symbolZOrder(SYMBOL_Z_ORDER_AUTO),
                                    PropertyFactory.lineColor(routeColor)
                            ));
                        }

                    } else {
                        gMapStyle.addLayer(new LineLayer(layerID, sourceID).withProperties(
                                PropertyFactory.lineCap(Property.LINE_CAP_ROUND),
                                PropertyFactory.lineJoin(Property.LINE_JOIN_ROUND),
                                PropertyFactory.lineWidth(5.0f),
                                PropertyFactory.symbolZOrder(SYMBOL_Z_ORDER_AUTO),
                                PropertyFactory.lineColor(routeColor)
                        ));

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
                            MarkerView markerViewRoute = addMarkerViewRoute(new LatLng(latitude, longitude),
                                    routeNumber,
                                    routeColorString, textColor);

                            routeMarkerViews.add(markerViewRoute);

                        }
                    }
                }
            }
        }
    }

    private void moveMapToShowRoute() {
        if (gMapboxMap != null && originPoint != null && destinationPoint != null &&
                (originPoint.getLat() != 0.0 || originPoint.getLon() != 0.0) &&
                (destinationPoint.getLat() != 0.0 || destinationPoint.getLon() != 0.0)) {
            LatLngBounds.Builder latLngBoundsBuilder = new LatLngBounds.Builder();

            latLngBoundsBuilder.include(new LatLng(originPoint.getLat(), originPoint.getLon()));
            latLngBoundsBuilder.include(new LatLng(destinationPoint.getLat(), destinationPoint.getLon()));
            int paddingTop = dpToPx(26);
            int paddingBottom = gDm.heightPixels * 6 / 10;

            gMapboxMap.moveCamera(CameraUpdateFactory.newLatLngBounds(latLngBoundsBuilder.build(),
                    dpToPx(26), paddingTop, dpToPx(26), paddingBottom), null);
        }
    }

    private void showOriginDestinationAndItineraryIfExist() {
        if (selectedItinerary != null) {
            drawRoutePolylineOnMap(selectedItinerary);
        }
        showDestinationMarker();
        showOriginMarker();
        moveMapToShowRoute();
    }

    private void getGpsReverseResult(final double lat, final double lon) {
        String url = String.format(Locale.getDefault(), Resource.API_GPS_REVERSE, lat, lon);

        if (gpsReverseRequest != null) {
            gpsReverseRequest.cancel();
            gpsReverseRequest = null;
        }
        gpsReverseRequest = ServerDelegate.volleyStringRequest(Request.Method.GET, url, "", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response != null) {
                    JSONObject jsonObj = null;
                    try {
                        jsonObj = new JSONObject(response);
                        JSONArray predsJsonArray = jsonObj.getJSONArray("features");

                        // Extract the Place descriptions from the results
                        if (predsJsonArray.length() > 0) {
                            JSONObject predsJSONObject = predsJsonArray.getJSONObject(0);
                            JSONObject propertiesJSONObject = predsJSONObject.getJSONObject("properties");
                            String address = propertiesJSONObject.getString("label");
                            finishAndJumpToPlanTripPage(address);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        gpsReverseErrorHandler();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Logger.e("get gps reverse error:", error.toString());
                gpsReverseErrorHandler();
            }
        });
    }

    private void finishAndJumpToPlanTripPage(String address) {
        Intent intent = new Intent();
        if (gpsReverseMode == Constants.GPS_REVERSE_MODE_STOP_VIEWER_FROM) {
            intent.putExtra(Constants.IS_ORIGIN, true);
        } else if (gpsReverseMode == Constants.GPS_REVERSE_MODE_STOP_VIEWER_TO) {
            intent.putExtra(Constants.IS_ORIGIN, false);
        }

        if (address != null) {
            intent.putExtra(Constants.SEARCHED_ADDRESS, address);
        }
        intent.putExtra(Constants.SEARCHED_LATITUDE, stopViewerItineraryLegPoint.getLat());
        intent.putExtra(Constants.SEARCHED_LONGITUDE, stopViewerItineraryLegPoint.getLon());
        setResult(RESULT_OK, intent);
        finish();
    }

    private void gpsReverseErrorHandler() {
        if (stopViewerItineraryLegPoint != null) {
            String address = stopViewerItineraryLegPoint.getName();
            finishAndJumpToPlanTripPage(address);
        }
    }

    private void showStopMarkersOnMap() {
        try {
            if (gMapboxMap != null && markerViewManager != null && stopViewerItineraryLegPoint != null) {
                if (mvStop == null) {
                    mvStop = addMarkerView(new LatLng(stopViewerItineraryLegPoint.getLat(), stopViewerItineraryLegPoint.getLon()), true, false);
                } else {
                    mvStop.setLatLng(new LatLng(stopViewerItineraryLegPoint.getLat(), stopViewerItineraryLegPoint.getLon()));
                    mvStop.setMarkerViewLatLng(new LatLng(stopViewerItineraryLegPoint.getLat(), stopViewerItineraryLegPoint.getLon()));
                }

                gMapboxMap.moveCamera(CameraUpdateFactory.zoomTo(12.0));
                int paddingBottom;
                if (gDm.heightPixels >= dpToPx(700)) {
                    paddingBottom = dpToPx(400);
                } else {
                    paddingBottom = (gDm.heightPixels - dpToPx(30)) / 2;
                }
                gMapboxMap.moveCamera(CameraUpdateFactory.newLatLngPadding(mvStop.getMarkerViewLatLng(),
                        0, dpToPx(16), 0, paddingBottom));
            }
        } catch (Exception e) {
            Log.e("StopViewerActivity", e.toString());
        }

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
                    //only set once
                    loadTransitStopsOnMap();
                    setupAndAddFeatureListeners();

                    showStopMarkersOnMap();

                    showOriginDestinationAndItineraryIfExist();
                }
            });
        }
    }


    private void removeTransitStopsFromMap() {
        if (transitStopSource != null) {
            gMapStyle.removeLayer("unclustered_transit_stop_points");
            gMapStyle.removeSource("transit_stop_list");
            transitStopSource = null;
            unclusteredTransitStop = null;
        }
    }

    private void setupAndAddFeatureListeners() {
        parkRideListener = new ParkRideManager.ParkRideListener() {
            @Override
            public void parkRideChanged() {
                if (Configurations.getBoolean("enable_layer_park_ride_locations", false)) {
//                    loadParkRideLocationsOnMap();
                }
            }
        };

        transitStopListener = new TransitStopManager.TransitStopListener() {
            @Override
            public void transitStopChanged() {
                if (Configurations.getBoolean("enable_layer_transit_stops", true)) {
                    loadTransitStopsOnMap();
                }
            }
        };

        ParkRideManager.getInstance().addListener(parkRideListener);
        TransitStopManager.getInstance().addListener(transitStopListener);
    }

    private void loadTransitStopsOnMap() {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (gMapStyle != null) {
                    gMapStyle.addImage("transit_stop_icon_id", BitmapUtils.getBitmapFromDrawable(
                            StopViewerActivity.this.getResources().getDrawable(R.drawable.ic_place)));

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

    private void setupMapView(Bundle savedInstanceState) {
        rlMapView = findViewById(R.id.rl_map_view);
        mapView = findViewById(R.id.map_view);
        mapView.onCreate(savedInstanceState);

        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(MapboxMap mapboxMap) {
                gMapboxMap = mapboxMap;

                markerViewManager = new MarkerViewManager(mapView, mapboxMap);

                int marginHeight;
                if (gDm.heightPixels >= dpToPx(700)) {
                    marginHeight = dpToPx(380);
                } else {
                    marginHeight = (gDm.heightPixels - dpToPx(88)) / 2;
                }

                //move the compass icon to the bottom left side
                gMapboxMap.getUiSettings().setCompassMargins(0, gDm.heightPixels - marginHeight - dpToPx(126),
                        gDm.widthPixels - dpToPx(60), 0);
                refreshMapStyle();


                gMapboxMap.addOnMapClickListener(new MapboxMap.OnMapClickListener() {
                    @Override
                    public boolean onMapClick(@NonNull LatLng point) {

                        if (customView != null && customView.getVisibility() == View.VISIBLE) {
                            customView.setVisibility(View.GONE);
                            svStopViewer.setVisibility(View.VISIBLE);
                            if (gpsReverseRequest != null) {
                                gpsReverseRequest.cancel();
                                gpsReverseRequest = null;
                            }
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

                            if (unclusteredRentalVehicle != null) {
                                if (gMapboxMap.getCameraPosition().zoom >= Constants.TRANSIT_STOP_MARKER_MIN_ZOOM_LEVEL) {
                                    unclusteredRentalVehicle.setProperties(visibility(VISIBLE));
                                } else {
                                    unclusteredRentalVehicle.setProperties(visibility(NONE));
                                }
                            }
                        }
                    }
                });
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
        if (currentSelectedLocationMode == Constants.SELECTED_LOCATION_MODE_POINT) {
            customView.setVisibility(View.GONE);
            svStopViewer.setVisibility(View.VISIBLE);
        } else {
            customView.setVisibility(View.VISIBLE);
            svStopViewer.setVisibility(View.GONE);
        }
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
            case Constants.SELECTED_LOCATION_MODE_PARK_RIDE:
                if (gpsReverseRequest != null) {
                    gpsReverseRequest.cancel();
                    gpsReverseRequest = null;
                }
                llMarkerStopViewer.setVisibility(View.GONE);
                llMarkerBtnStopViewer.setVisibility(View.GONE);
                ivAgencyLogo.setVisibility(View.GONE);
                if (selectedParkRideLocation != null) {
                    String name = selectedParkRideLocation.getName();
                    if (name != null && name.contains("P+R") && name.length() > (name.indexOf("P+R") + 3)) {
                        name = name.substring(name.indexOf("P+R") + 3).trim();
                    }
                    tvMapAddress.setText(name);

                    tvMapAddress.setTypeface(null, Typeface.BOLD);

                }
                break;
            case Constants.SELECTED_LOCATION_MODE_TRANSIT_STOP:
                if (gpsReverseRequest != null) {
                    gpsReverseRequest.cancel();
                    gpsReverseRequest = null;
                }
                llMarkerStopViewer.setVisibility(View.VISIBLE);
                llMarkerBtnStopViewer.setVisibility(View.VISIBLE);
                ivAgencyLogo.setVisibility(View.GONE);
                if (selectedTransitStop != null) {
                    tvMapAddress.setText(selectedTransitStop.getName());
                    String id = selectedTransitStop.getCode();
                    if (id == null) {
                        id = Utilities.getStopId(selectedTransitStop);
                    }
                    tvMarkerStopID.setText(id);
                    tvMapAddress.setTypeface(null, Typeface.BOLD);
                    getAgencyIDAndLoadLogo(selectedTransitStop.getId());
                }
                break;
            case Constants.SELECTED_LOCATION_MODE_RENTAL_VEHICLE:
                if (gpsReverseRequest != null) {
                    gpsReverseRequest.cancel();
                    gpsReverseRequest = null;
                }
                llMarkerStopViewer.setVisibility(View.GONE);
                llMarkerBtnStopViewer.setVisibility(View.GONE);
                ivAgencyLogo.setVisibility(View.GONE);
                String networkName = "";

                if (selectedRentalVehicle != null) {
                    StringBuilder stringBuilder = new StringBuilder();

                    if (selectedRentalVehicle.getNetwork() != null) {
                        // Split by "_" and "-".
                        String[] network = selectedRentalVehicle.getNetwork().split("[_-]");
                        if (network.length > 0) {
                            // Check for "link" or "bird" and fetch localised names, otherwise default to uppercase.
                            if (network[0].equalsIgnoreCase("link")) {
                                networkName = getString(R.string.link_first_capital); // "Link"
                            } else if (network[0].equalsIgnoreCase("bird")) {
                                networkName = "Bird";
                            } else {
                                // Default to uppercase for other network names.
                                networkName = network[0].toUpperCase(Locale.ROOT);
                            }
                        }
                    }

                    // Check if the vehicle type is a SCOOTER.
                    if (selectedRentalVehicle.getVehicleType() != null &&
                            "SCOOTER".equalsIgnoreCase(selectedRentalVehicle.getVehicleType().getFormFactor())) {
                        // Use the localised scooter string with the network name.
                        stringBuilder.append(getString(R.string.e_scooter_with_service_name, networkName));
                    } else {
                        // If not a scooter, append the free-floating bike string.
                        stringBuilder.append(getString(R.string.free_floating_bike));
                        // Append the network name AFTER the bike string with proper formatting
                        if (!networkName.isEmpty()) {
                            stringBuilder.append(": ").append(networkName);
                        }
                    }

                    tvMapAddress.setText(stringBuilder.toString());
                    tvMapAddress.setTypeface(null, Typeface.BOLD);
                }
                break;
            case Constants.SELECTED_LOCATION_MODE_POINT:
                llMarkerStopViewer.setVisibility(View.GONE);
                llMarkerBtnStopViewer.setVisibility(View.GONE);
                ivAgencyLogo.setVisibility(View.GONE);
                tvMapAddress.setText(String.format(Locale.getDefault(), "%.5f,%.5f", latlng.getLatitude(), latlng.getLongitude()));
                gpsReverseMode = Constants.GPS_REVERSE_MODE_MARKER_VIEW;
                getGpsReverseResult(latlng.getLatitude(), latlng.getLongitude());

                tvMapAddress.setTypeface(null, Typeface.NORMAL);
                break;
            default:
                break;
        }
    }

    private void moveMapToShowMarkerView() {
        if (markerViewLatlng != null) {
            double topPadding;
            if (currentMode == Constants.HOME_PAGE_MODE_ITINERARY_DETAIL) {
                topPadding = 0;
            } else {
                topPadding = dpToPx(200);
            }
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
        ivAgencyLogo = customView.findViewById(R.id.iv_agency_logo);
        LinearLayout llMarkerBtnFromHere = customView.findViewById(R.id.ll_marker_btn_from_here);
        LinearLayout llMarkerBtnToHere = customView.findViewById(R.id.ll_marker_btn_to_here);
        ivMarkerViewClose = customView.findViewById(R.id.iv_marker_view_close);
        llMarkerStopViewer = customView.findViewById(R.id.ll_marker_stop_viewer);
        tvMarkerStopID = customView.findViewById(R.id.tv_marker_stop_id);
        llMarkerBtnStopViewer = customView.findViewById(R.id.ll_marker_btn_stop_viewer);

        ivMarkerViewClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                customView.setVisibility(View.GONE);
                svStopViewer.setVisibility(View.VISIBLE);
            }
        });

        llMarkerBtnFromHere.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (stopViewerItineraryLegPoint != null) {
                    gpsReverseMode = Constants.GPS_REVERSE_MODE_STOP_VIEWER_FROM;
                    getGpsReverseResult(selectedTransitStop.getLat(), selectedTransitStop.getLon());
                }
            }
        });

        llMarkerBtnToHere.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (stopViewerItineraryLegPoint != null) {
                    gpsReverseMode = Constants.GPS_REVERSE_MODE_STOP_VIEWER_TO;
                    getGpsReverseResult(selectedTransitStop.getLat(), selectedTransitStop.getLon());
                }
            }
        });

        llMarkerBtnStopViewer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedTransitStop != null) {
                    tvMapAddress.setText(selectedTransitStop.getName());

                    stopViewerItineraryLegPoint = new ItineraryLegPoint();
                    stopViewerItineraryLegPoint.setStopId(selectedTransitStop.getId());
                    stopViewerItineraryLegPoint.setName(selectedTransitStop.getName());
                    stopViewerItineraryLegPoint.setLat(selectedTransitStop.getLat());
                    stopViewerItineraryLegPoint.setLon(selectedTransitStop.getLon());
                    stopViewerItineraryLegPoint.setStopCode(selectedTransitStop.getCode());
                    customView.setVisibility(View.GONE);

                    enterStopViewerPage(null);
                }
            }
        });
    }

    private void getAgencyIDAndLoadLogo(String stopId) {
        if (stopId != null) {
            String[] ids = stopId.split(":");
            if (ids.length > 0) {
                String agencyName = Utilities.getAgencyName(ids[0].toLowerCase());

                if (agencyName != null) {
                    if (Utilities.agencyLogoIconMapping.containsKey(agencyName)) {
                        ivAgencyLogo.setDefaultImageResId(Utilities.agencyLogoIconMapping.get(agencyName));
                    } else {
                        ivAgencyLogo.setDefaultImageResId(R.drawable.ic_default_agency_logo);
                    }
                    String logoUrl = Resource.ROUTE_AGENCY_LOGO_BASE_URL + agencyName;
                    ivAgencyLogo.setImageUrl(logoUrl, MyApplication.getInstance().getImageLoader());
                    ivAgencyLogo.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    private void enterStopViewerPage(String mode) {
        if (stopViewerItineraryLegPoint != null) {
            Intent intent = new Intent(this, StopViewerActivity.class);
            intent.putExtra(Constants.STOP_VIEWER_ITINERARY_LEG_POINT, stopViewerItineraryLegPoint);

            if (selectedItinerary != null) {
                intent.putExtra(Constants.CURRENT_ITINERARY, selectedItinerary);
            }

            if (mode != null) {
                intent.putExtra(Constants.STOP_VIEWER_ITINERARY_MODE, mode);
            }
            intent.putExtra(Constants.CURRENT_ORIGIN, originPoint);
            intent.putExtra(Constants.CURRENT_DESTINATION, destinationPoint);

            startActivityForResult(intent, Constants.STOP_VIEWER_REQUEST_CODE);
        }
    }

    private void moveMapToShowOriginAndDestination() {
        if (gMapboxMap != null) {
            if (mvOrigin != null && mvDestination != null) {
                LatLngBounds.Builder latLngBoundsBuilder = new LatLngBounds.Builder();
                latLngBoundsBuilder.include(mvOrigin.getMarkerViewLatLng());
                latLngBoundsBuilder.include(mvDestination.getMarkerViewLatLng());

                gMapboxMap.moveCamera(CameraUpdateFactory.newLatLngBounds(latLngBoundsBuilder.build(),
                        dpToPx(76), dpToPx(316), dpToPx(76), dpToPx(76)), null);
            } else if (mvOrigin != null) {
                gMapboxMap.moveCamera(CameraUpdateFactory.newLatLng(mvOrigin.getMarkerViewLatLng()));
            } else if (mvDestination != null) {
                gMapboxMap.moveCamera(CameraUpdateFactory.newLatLng(mvDestination.getMarkerViewLatLng()));
            }
        }
    }

    private String extractRouteId(String patternId) {
        String[] ids = patternId.split(":");
        StringBuilder routeId = new StringBuilder();

        for (int i = 0; i < ids.length - 2; i++) {
            if (routeId.length() > 0) {
                routeId.append(":");
            }
            routeId.append(ids[i]);
        }

        return routeId.toString();
    }

    private ImageView ivStopViewerBack;

    private void getStopRelatedRouteMode() {
        String stopID = null;
        if (stopViewerItineraryLegPoint != null) {
            stopID = stopViewerItineraryLegPoint.getStopId();
        }
        if (!Configurations.getString("graphql_base_url", Configurations.GRAPHQL_SERVER_URL).isEmpty() && stopID != null) {
            ApolloClient apolloClient = ApolloClient.builder()
                    .serverUrl(Configurations.getString("graphql_base_url", Configurations.GRAPHQL_SERVER_URL))
                    .build();
            apolloClient.query(new StopTimesQuery(stopID)).enqueue(new ApolloCall.Callback<StopTimesQuery.Data>() {
                @Override
                public void onResponse(@NonNull com.apollographql.apollo.api.Response<StopTimesQuery.Data> response) {
                    if (response != null && response.getData() != null && response.getData().stop() != null
                            && response.getData().stop().stopTimes() != null) {
                        Gson gson = new Gson();
                        MyApplication.getInstance().stopViewerNextArrivals = gson.fromJson(gson.toJson(response.getData().stop().stopTimes()), new TypeToken<ArrayList<StopViewerSchedule>>() {
                        }.getType());

                        setRouteToPattern(response);


                        Handler mainHandler = new Handler(Looper.getMainLooper());
                        mainHandler.post(() -> {
                            List<StopTimesQuery.Route> routes = response.getData().stop().routes;
                            if (routes != null && routes.size() > 0) {
                                for (StopTimesQuery.Route route : routes) {
                                    if (route != null && route.mode() != null && route.mode().rawValue() != null) {
                                        stopMode = route.mode().rawValue();
                                        if (mvStop == null) {
                                            mvStop = addMarkerView(new LatLng(stopViewerItineraryLegPoint.getLat(), stopViewerItineraryLegPoint.getLon()), true, false);
                                        } else {
                                            View view = mvStop.getView();

                                            ImageView ivMarkerViewIcon = view.findViewById(R.id.iv_marker_view_icon);
                                            if (stopMode != null) {
                                                ivMarkerViewIcon.setImageResource(Utilities.getModeImageByMode(
                                                        stopMode.toLowerCase(Locale.ROOT), false, getBaseContext(), ivMarkerViewIcon, ivMarkerViewIcon));
                                            } else {
                                                ivMarkerViewIcon.setImageResource(R.drawable.ic_bus);
                                            }

                                            //This logic is for showing water taxi logo from stop name check
                                            if (response.getData().stop != null && response.getData().stop.name != null) {

                                                String stopName = response.getData().stop.name.toLowerCase();

                                                if (stopName.contains("taxi") || stopName.contains("fast") ||
                                                        stopName.contains("foot")) {
                                                    ivMarkerViewIcon.setImageResource(Utilities.getModeImageByMode(
                                                            "water taxi", false, getBaseContext(), ivMarkerViewIcon, ivMarkerViewIcon));
                                                }
                                            }
                                        }
                                        break;
                                    }
                                }
                            }
                        });
                    }
                }

                @Override
                public void onFailure(@NonNull ApolloException e) {
                    Logger.e("get next arrivals feed error:", e.toString());
                }
            });
        }
    }

    private void setRouteToPattern(com.apollographql.apollo.api.Response<StopTimesQuery.Data> response) {
        Gson gson = new Gson();
        ArrayList<RouteViewerRoute> routesList = gson.fromJson(gson.toJson(response.getData().stop().routes()), new TypeToken<ArrayList<RouteViewerRoute>>() {
        }.getType());

        if (MyApplication.getInstance().stopViewerNextArrivals != null && routesList != null) {
            for (StopViewerSchedule stopViewerSchedule : MyApplication.getInstance().stopViewerNextArrivals) {
                String routeID = extractRouteId(stopViewerSchedule.getPattern().getId());
                for (RouteViewerRoute route : routesList) {
                    if (routeID.equalsIgnoreCase(route.getId())) {
                        stopViewerSchedule.getPattern().setRouteViewerRoute(route);
                        break;
                    }
                }
            }
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Utilities.checkIfToDisplayAccessibilityUI(this)) {
            setContentView(R.layout.activity_stop_viewer_accessibility);
        } else {
            setContentView(R.layout.activity_stop_viewer);
        }

        gDm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(gDm);
        rlActivityStopViewer = findViewById(R.id.rl_activity_stop_viewer);
        llBottomTab = findViewById(R.id.ll_bottom_tab);
        if (getIntent() != null) {
            Intent intent = getIntent();
            stopViewerItineraryLegPoint = (ItineraryLegPoint) intent.getSerializableExtra(Constants.STOP_VIEWER_ITINERARY_LEG_POINT);
            stopMode = intent.getStringExtra(Constants.STOP_VIEWER_ITINERARY_MODE);
            originPoint = (LocationPoint) intent.getSerializableExtra(Constants.CURRENT_ORIGIN);
            destinationPoint = (LocationPoint) intent.getSerializableExtra(Constants.CURRENT_DESTINATION);
            selectedItinerary = (Itinerary) intent.getSerializableExtra(Constants.CURRENT_ITINERARY);
        }
        if (stopMode == null) {
            getStopRelatedRouteMode();
        }
        ivStopViewerBack = findViewById(R.id.iv_stop_viewer_back);
        ivStopViewerBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyApplication.getInstance().mapTargetOnStopViewerPage = gMapboxMap.getCameraPosition().target;
                MyApplication.getInstance().mapZoomOnStopViewerPage = gMapboxMap.getCameraPosition().zoom;
                finish();
            }
        });

        if (MyApplication.getInstance().userPreferences == null) {
            Gson gson = new Gson();
            try {
                MyApplication.getInstance().userPreferences =
                        gson.fromJson(Configurations.getString("account_user_preferences", "{}"), UserPreferences.class);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        isFavoured = Utilities.isFavouriteStop(stopViewerItineraryLegPoint);

        setupMapView(savedInstanceState);
        setupStopViewer();
        setupDatePickerDialog();
        setupFavouriteStop();


        if (stopViewerItineraryLegPoint != null) {
            MyApplication.getInstance().stopViewerStopID = stopViewerItineraryLegPoint.getStopId();
        }

        refreshStopViewerAutoRefresh(true);
        refreshStopViewer();
        setStopViewerHeight();

        setupBottomTabs();

        mDrawerLayout = findViewById(R.id.drawer_layout);
        llDrawer = findViewById(R.id.ll_drawer);
        mDrawerList = findViewById(R.id.left_drawer);

        initMenuItemsData();
        menuListAdapter = getMenuListAdapter();
        mDrawerList.setAdapter(menuListAdapter);
        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mDrawerLayout.closeDrawer(llDrawer);

                if (position < menuItems.size()) {
                    MenuItem menuItem = menuItems.get(position);

                    if (menuItem != null && menuItem.getAction() != null) {
                        if (menuItem.getAction().equalsIgnoreCase(MENU_ACTION_TRIGGER_EXTERNAL_WEB)) {
                            Utilities.GotoTheUrl(StopViewerActivity.this, Utilities.getUrlWithLanguageFor(menuItem.getValue()));
                        } else if (menuItem.getAction().equalsIgnoreCase(MENU_ACTION_FUNCTION)) {
                            MyApplication.getInstance().gbStartOver = true;
                            MyApplication.getInstance().mapTargetOnStopViewerPage = gMapboxMap.getCameraPosition().target;
                            MyApplication.getInstance().mapZoomOnStopViewerPage = gMapboxMap.getCameraPosition().zoom;
                            finish();
                        }
                    }
                }

            }
        });

        TextView tvVersion = findViewById(R.id.tv_menu_version);
        String version = Utilities.getVersion(this);

        if (tvVersion != null) {
            tvVersion.setText(version);
        }
    }

    private void setupFavouriteStop() {
        ivStopViewerSave = findViewById(R.id.iv_stop_viewer_save);
        ivStopViewerSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String credentialString = Configurations.getString("account_credentials", "");

                if (credentialString != null && !credentialString.isEmpty()) {
                    savePreferences(!isFavoured);
                } else {
                    //login action
                    showNativeAuth();
                }
            }
        });
    }

    private List<String> generateConnections() {
        ArrayList<String> connections = new ArrayList<String>();
        connections.add("Username-Password-Authentication");
        connections.add("email");
        if (connections.isEmpty()) {
            connections.add("no-connection");
        }
        return connections;
    }

    private void gotoVerifyEmailActivity() {
        Intent intent = new Intent(this, VerifyEmailActivity.class);

        startActivity(intent);
    }

    private void gotoCreateNewAccountActivity() {
        Intent intent = new Intent(this, CreateNewAccountActivity.class);

        startActivity(intent);
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
                        if (MyApplication.getInstance().userPreferences.isHasConsentedToTerms()) {
                            isFavoured = Utilities.isFavouriteStop(stopViewerItineraryLegPoint);
                            refreshFavButton();
                        } else {
                            gotoCreateNewAccountActivity();
                        }
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
                if (error != null && error.networkResponse != null && error.networkResponse.statusCode == 404) {
                    //create a new user in otp middleware
                    createNewUserInMiddleware();
                }
            }
        };

        String url = Resource.API_ACCOUNT_FROM_TOKEN;

        volleyAccountStringRequest(Request.Method.GET, url, "", listener, errorListener);
    }

    private void createNewUserInMiddleware() {
        Response.Listener listener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response != null && !response.isEmpty()) {
                    Gson gson = new Gson();
                    Configurations.setString("account_user_preferences", response);

                    try {
                        MyApplication.getInstance().userPreferences = gson.fromJson(response, UserPreferences.class);

                        isFavoured = Utilities.isFavouriteStop(stopViewerItineraryLegPoint);
                        refreshFavButton();
                    } catch (Exception e) {
                        e.printStackTrace();
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

        String url = Resource.API_ACCOUNT_CREATE_USER;
        CreateNewUserRequest createNewUserRequest = new CreateNewUserRequest();
        String profileString = Configurations.getString("account_user_profile", "");
        Gson gson = new Gson();
        if (profileString != null && !profileString.isEmpty()) {
            UserProfile userProfile = gson.fromJson(profileString, UserProfile.class);
            Map<String, Object> maps = userProfile.getExtraInfo();
            if (maps.containsKey("sub")) {
                createNewUserRequest.setAuth0UserId(maps.get("sub").toString());
            } else {
                createNewUserRequest.setAuth0UserId(userProfile.getId());
            }
            createNewUserRequest.setEmail(userProfile.getEmail());
        }
        volleyAccountStringRequest(Request.Method.POST, url, gson.toJson(createNewUserRequest), listener, errorListener);
    }

    private void retrieveUserInfo(String accessToken) {
        Auth0 account = new Auth0(Configurations.getString("auth0_client_id", ""), Configurations.getString("auth0_domain", ""));
        Callback<UserProfile, AuthenticationException> userInfoCallback = new Callback<UserProfile, AuthenticationException>() {
            @Override
            public void onSuccess(UserProfile userProfile) {
                Gson gson = new Gson();
                Configurations.setString("account_user_profile", gson.toJson(userProfile));

                if (!Configurations.getString("device_token", "").isEmpty()) {
                    try {
                        ServerDelegate.postRegistration(Configurations.getString("device_token", ""));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (userProfile != null) {
                    if (Boolean.TRUE.equals(userProfile.isEmailVerified())) {
                        getFromToken();
                    } else {
                        gotoVerifyEmailActivity();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull AuthenticationException e) {
                if (e.getStatusCode() == 401) {
                    Utilities.showNormalPopupDialog(StopViewerActivity.this, "", getString(R.string.retrieve_user_info_failed_unauthorized));
                } else {
                    Utilities.showNormalPopupDialog(StopViewerActivity.this, "", getString(R.string.retrieve_user_info_failed_other));
                }
            }
        };
        AuthenticationAPIClient authenticationAPIClient = new AuthenticationAPIClient(account);
        authenticationAPIClient.userInfo(accessToken).start(userInfoCallback);
    }

    public void showNativeAuth() {
        Auth0 account = new Auth0(Configurations.getString("auth0_client_id", ""), Configurations.getString("auth0_domain", ""));
        AuthenticationCallback callback = new AuthenticationCallback() {

            @Override
            public void onError(@NonNull AuthenticationException error) {

            }

            @Override
            public void onAuthentication(@NonNull Credentials credentials) {
                Gson gson = new Gson();
                Configurations.setString("account_credentials", gson.toJson(Utilities.regenerateCredentials(credentials)));
                Configurations.setString("account_id_token", credentials.getIdToken());
                Configurations.setString("account_access_token", credentials.getAccessToken());
                retrieveUserInfo(credentials.getAccessToken());

            }
        };
        Lock.Builder lockBuilder = Lock.newBuilder(account, callback)
                .withScheme("https")
                .closable(true)
                .useLabeledSubmitButton(true)
                .loginAfterSignUp(true)
                .allowLogIn(true)
                .allowSignUp(true)
                .allowForgotPassword(true)
                .allowedConnections(generateConnections())
                .hideMainScreenTitle(false)
                .withUsernameStyle(UsernameStyle.EMAIL)
                .initialScreen(InitialScreen.LOG_IN)
                .setDefaultDatabaseConnection("Username-Password-Authentication")
                .withAudience(getString(R.string.com_auth0_audience))
                .withScope("openid profile email otp-user");

        if (lock != null) {
            lock.onDestroy(this);
        }
        lock = lockBuilder.build(this);
        if (lock != null) {
            startActivity(lock.newIntent(this));
        }
    }

    private ArrayList<AddedLocation> generateAddedLocations(boolean favoured) {
        ArrayList<AddedLocation> addedLocations = new ArrayList<>();
        UserPreferences userPreferences = MyApplication.getInstance().userPreferences;

        if (userPreferences.getSavedLocations() != null) {
            addedLocations.addAll(userPreferences.getSavedLocations());
        }

        if (stopViewerItineraryLegPoint != null) {
            if (favoured) {

                AddedLocation addedLocation = new AddedLocation();
                addedLocation.setType("stop");
                addedLocation.setIcon("ic_stop");
                addedLocation.setName(stopViewerItineraryLegPoint.getName());
                addedLocation.setAddress(stopViewerItineraryLegPoint.getName());
                addedLocation.setLat(stopViewerItineraryLegPoint.getLat());
                addedLocation.setLon(stopViewerItineraryLegPoint.getLon());

                addedLocations.add(addedLocation);

            } else {
                for (AddedLocation addedLocation : addedLocations) {
                    if ("stop".equalsIgnoreCase(addedLocation.getType())) {
                        if (addedLocation.getName().equals(stopViewerItineraryLegPoint.getName())
                                && 0 == Double.compare(stopViewerItineraryLegPoint.getLat(), addedLocation.getLat())
                                && 0 == Double.compare(stopViewerItineraryLegPoint.getLon(), addedLocation.getLon())) {
                            addedLocations.remove(addedLocation);
                            break;
                        }
                    }
                }
            }
        }
        return addedLocations;
    }

    private void refreshFavButton() {
        if (isFavoured) {
            ivStopViewerSave.setImageResource(R.drawable.ic_saved);
        } else {
            ivStopViewerSave.setImageResource(R.drawable.ic_save_new);
        }
    }

    private void savePreferences(final boolean favoured) {
        Response.Listener listener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                isFavoured = favoured;
                refreshFavButton();

                Gson gson = new Gson();
                try {
                    MyApplication.getInstance().userPreferences = gson.fromJson(response, UserPreferences.class);

                    Configurations.setString("account_user_preferences", response);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Logger.e("stops", error.toString());

            }
        };
        UserPreferences userPreferences = MyApplication.getInstance().userPreferences;
        UserPreferences newPreferences = new UserPreferences();
        if (userPreferences != null) {
            newPreferences.setAuth0UserId(userPreferences.getAuth0UserId());
            newPreferences.setEmail(userPreferences.getEmail());
            newPreferences.setId(userPreferences.getId());
            newPreferences.setPhoneNumber(userPreferences.getPhoneNumber());
            newPreferences.setPhoneNumberVerified(userPreferences.isPhoneNumberVerified());
            newPreferences.setDataToolsUser(userPreferences.isDataToolsUser());
            newPreferences.setHasConsentedToTerms(userPreferences.isHasConsentedToTerms());
            newPreferences.setStoreTripHistory(userPreferences.isStoreTripHistory());
            newPreferences.setSavedLocations(generateAddedLocations(favoured));
            newPreferences.setNotificationChannel(userPreferences.getNotificationChannel());

            Gson gson = new Gson();
            String url = String.format(Resource.API_ACCOUNT_USER_SAVE_PREFERENCES, userPreferences.getId());

            volleyAccountStringRequest(Request.Method.PUT, url, gson.toJson(newPreferences), listener, errorListener);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (lock != null) {
            lock.onDestroy(this);
            lock = null;
        }
        ParkRideManager.getInstance().removeListener(parkRideListener);
        TransitStopManager.getInstance().removeListener(transitStopListener);
    }

    private ScheduleTimeListAdapter getScheduleTimeListAdapter() {
        ArrayList<HashMap<String, Object>> data = new ArrayList<HashMap<String, Object>>();

        for (int i = 0; i < scheduleTimes.size(); i++) {
            StopViewerTime stopViewerTime = scheduleTimes.get(i);
            HashMap<String, Object> map = new HashMap<String, Object>();
            if (stopViewerTime.getRoute() != null) {
                map.put("tv_schedule_route", stopViewerTime.getRoute());
            } else {
                map.put("tv_schedule_route", "");
            }
            data.add(map);
        }

        ScheduleTimeListAdapter mScheduleTimeListAdapter = new ScheduleTimeListAdapter(
                this, data,
                R.layout.item_stop_viewer_schedule,
                new String[]{"tv_schedule_route"}, new int[]{R.id.tv_schedule_route});

        return mScheduleTimeListAdapter;
    }

    class ScheduleTimeListAdapter extends SimpleAdapter {
        Context mContext;
        private final LayoutInflater l_Inflater;

        public ScheduleTimeListAdapter(Context context, List<? extends Map<String, ?>> data,
                                       int resource, String[] from, int[] to) {
            super(context, data, resource, from, to);

            mContext = context;
            l_Inflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return scheduleTimes.size();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            StopViewerTime stopViewerTime = scheduleTimes.get(position);
            if (convertView == null) {
                if (Utilities.checkIfToDisplayAccessibilityUI(mContext)) {
                    convertView = l_Inflater.inflate(
                            R.layout.item_stop_viewer_schedule_accessibility, null);
                } else {
                    convertView = l_Inflater.inflate(
                            R.layout.item_stop_viewer_schedule, null);
                }
            }


            NetworkImageView ivLogo = convertView.findViewById(R.id.iv_schedule_logo);
            TextView tvScheduleRoute = convertView.findViewById(R.id.tv_schedule_route);
            TextView tvScheduleTo = convertView.findViewById(R.id.tv_schedule_to);
            TextView tvScheduleTime = convertView.findViewById(R.id.tv_schedule_time);

            if (stopViewerTime.getRoute() != null) {
                tvScheduleRoute.setText(stopViewerTime.getRoute());
            }

            try {
                String agencyName = stopViewerTime.getRouteAgency().getName().toLowerCase().replaceAll(" ", "_");
                ivLogo.setDefaultImageResId(R.drawable.ic_default_agency_logo);
                String logoUrl = Resource.ROUTE_AGENCY_LOGO_BASE_URL + agencyName;
                ivLogo.setImageUrl(logoUrl, MyApplication.getInstance().getImageLoader());
                ivLogo.setVisibility(View.GONE);
                int iconSize = 35;

                if (!uniqueAgentsUrl.containsKey(agencyName)) {
                    uniqueAgentsUrl.put(agencyName, logoUrl);

                    NetworkImageView networkImageView = new NetworkImageView(getApplicationContext());

                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                            dpToPx(iconSize),
                            dpToPx(iconSize)
                    );
                    networkImageView.setLayoutParams(layoutParams);
                    networkImageView.setImageUrl(logoUrl, MyApplication.getInstance().getImageLoader());
                    llHeaderHolder.addView(networkImageView);
                }
            } catch (Exception e) {
                Log.e("StopViewerActivity", e.getMessage());
            }

            if (stopViewerTime.getTo() != null) {
                String description = stopViewerTime.getTo();

                if (description.contains("(")) {
                    tvScheduleTo.setText(description.substring(0, description.indexOf("(")).trim());
                } else {
                    tvScheduleTo.setText(description);
                }

            }

            long arrivalTime;

            if (stopViewerTime.isRealtime()) {
                arrivalTime = stopViewerTime.getServiceDay() + stopViewerTime.getRealtimeArrival();
            } else {
                arrivalTime = stopViewerTime.getServiceDay() + stopViewerTime.getScheduledArrival();
            }

            tvScheduleTime.setText(Utilities.formatTimestamp(mContext, (1000 * arrivalTime)));

            if (position == nextScheduleTimeIndex) {
                convertView.setBackgroundColor(getResources().getColor(R.color.colorTabUserUnselected, null));
            } else {
                convertView.setBackgroundColor(getResources().getColor(android.R.color.white, null));
            }
            if (position == nextScheduleTimeIndex) {
                convertView.setBackgroundColor(
                        getResources().getColor(R.color.colorTabUserUnselected, null));
            } else {
                convertView.setBackgroundColor(
                        getResources().getColor(android.R.color.white, null));
            }

            convertView.setFocusable(true);
            convertView.setClickable(false);
            ViewCompat.setAccessibilityDelegate(convertView, new AccessibilityDelegateCompat() {
                @Override
                public void onInitializeAccessibilityNodeInfo(View host,
                                                              AccessibilityNodeInfoCompat info) {
                    super.onInitializeAccessibilityNodeInfo(host, info);
                    // remove the click hint
                    info.removeAction(AccessibilityNodeInfoCompat.AccessibilityActionCompat.ACTION_CLICK);
                    info.setClickable(false);

                    // strip away the "collection item" info so TalkBack
                    //      stops saying "in a list" / "out of list"
                    info.setCollectionItemInfo(null);
                }
            });

            return convertView;
        }
    }


    private NextArrivalTimeListAdapter getNextArrivalTimeListAdapter(StopViewerSchedule nextArrival) {
        ArrayList<HashMap<String, Object>> data = new ArrayList<HashMap<String, Object>>();

        if (nextArrival != null && nextArrival.getTimes() != null) {
            for (int i = 0; i < nextArrival.getTimes().size(); i++) {
                HashMap<String, Object> map = new HashMap<String, Object>();
                if (nextArrival.getPattern() != null) {
                    map.put("tv_next_arrival_time", nextArrival.getPattern().getDesc());
                }
                data.add(map);
            }
        }
        NextArrivalTimeListAdapter mNextArrivalTimeListAdapter = new NextArrivalTimeListAdapter(
                this, data,
                R.layout.item_stop_viewer_next_arrival_detail,
                new String[]{"tv_next_arrival_time"}, new int[]{R.id.tv_next_arrival_time}, nextArrival);

        return mNextArrivalTimeListAdapter;
    }

    private String formatNextArrivalTime(long nextArrivalTime) {
        long currentTime = System.currentTimeMillis() / 1000;
        long difference = nextArrivalTime - currentTime;

        if (difference < 60 && difference >= 0) {
            return getString(R.string.due);
        } else if (difference < 3600) {
            return getString(R.string.min_with_num, difference / 60);
        } else {
            long nextArrivalTimeMillis = nextArrivalTime * 1000;
            Date date = new Date(nextArrivalTimeMillis);
            Locale currentLocale = Locale.getDefault();

            DateFormat dateFormat = new SimpleDateFormat("h:mm a", currentLocale);
            dateFormat.setTimeZone(TimeZone.getTimeZone(Configurations.TARGET_TIMEZONE_ID));
            String formattedTime = dateFormat.format(date);

            Calendar currentCalendar = Calendar.getInstance(TimeZone.getTimeZone(Configurations.TARGET_TIMEZONE_ID));
            currentCalendar.setTimeInMillis(currentTime * 1000);

            Calendar arrivalCalendar = Calendar.getInstance(TimeZone.getTimeZone(Configurations.TARGET_TIMEZONE_ID));
            arrivalCalendar.setTime(date);

            if (arrivalCalendar.get(Calendar.DAY_OF_YEAR) > currentCalendar.get(Calendar.DAY_OF_YEAR)) {
                DateFormat dayFormat = new SimpleDateFormat("EEEE", currentLocale);
                dateFormat.setTimeZone(TimeZone.getTimeZone(Configurations.TARGET_TIMEZONE_ID));
                String dayOfWeek = dayFormat.format(date);
                return dayOfWeek + "\n" + formattedTime;
            }

            return formattedTime;
        }
    }

    private String formatNextArrivalTimeStamp(long nextArrivalTime) {
        long currentTime = System.currentTimeMillis() / 1000;
        long difference = nextArrivalTime - currentTime;
        // Convert the nextArrivalTime to milliseconds for formatting
        long nextArrivalTimeMillis = nextArrivalTime * 1000;
        Date date = new Date(nextArrivalTimeMillis);
        Locale currentLocale = Locale.getDefault();

        // Format the timestamp to PDT time
        DateFormat dateFormat = new SimpleDateFormat("h:mm a", currentLocale);
        dateFormat.setTimeZone(TimeZone.getTimeZone(Configurations.TARGET_TIMEZONE_ID));
        return dateFormat.format(date);

    }

    private String formatNextArrivalCountDownTime(long nextArrivalTime) {
        long currentTime = System.currentTimeMillis() / 1000;
        long difference = nextArrivalTime - currentTime;
        long durationMin = difference / 60;
        if (durationMin >= 60) {
            return getString(R.string.hr_min_with_num, durationMin / 60,
                    durationMin % 60);
        } else {
            return getString(R.string.min_with_num, durationMin);
        }
    }

    class NextArrivalTimeListAdapter extends SimpleAdapter {
        Context mContext;
        private final LayoutInflater l_Inflater;
        private final StopViewerSchedule mNextArrival;

        public NextArrivalTimeListAdapter(Context context, List<? extends Map<String, ?>> data,
                                          int resource, String[] from, int[] to, StopViewerSchedule nextArrival) {
            super(context, data, resource, from, to);

            mContext = context;
            l_Inflater = LayoutInflater.from(context);
            mNextArrival = nextArrival;
        }

        @Override
        public int getCount() {
            if (mNextArrival != null && mNextArrival.getTimes() != null) {
                return mNextArrival.getTimes().size();
            } else {
                return 0;
            }
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (mNextArrival == null || mNextArrival.getTimes() == null) {
                return null;
            }

            StopViewerTime stopViewerTime = mNextArrival.getTimes().get(position);

            if (convertView == null) {

                if (Utilities.checkIfToDisplayAccessibilityUI(mContext)) {
                    convertView = l_Inflater.inflate(
                            R.layout.item_stop_viewer_next_arrival_detail_accessibility, null);
                } else {
                    convertView = l_Inflater.inflate(
                            R.layout.item_stop_viewer_next_arrival_detail, null);
                }
            }

            TextView tvNextArrivalDuration = convertView.findViewById(R.id.tv_next_arrival_duration);
            TextView tvNextArrivalTime = convertView.findViewById(R.id.tv_next_arrival_time);
            TextView tvNextArrivalStatus = convertView.findViewById(R.id.tv_next_arrival_status);

            if (stopViewerTime.getRealtimeState().equalsIgnoreCase("SCHEDULED")) {
                tvNextArrivalStatus.setText(getString(R.string.capital_scheduled));
            }

            long nextArrivalTime;

            if (stopViewerTime.isRealtime()) {
                nextArrivalTime = stopViewerTime.getServiceDay() + stopViewerTime.getRealtimeDeparture();
                if (stopViewerTime.getRealtimeState() != null && stopViewerTime.getRealtimeState().equalsIgnoreCase("UPDATED")) {
                    tvNextArrivalStatus.setText(getString(R.string.real_time));
                }
            } else {
                nextArrivalTime = stopViewerTime.getServiceDay() + stopViewerTime.getScheduledDeparture();
            }

            String time = formatNextArrivalTimeStamp(nextArrivalTime);


            tvNextArrivalDuration.setText(formatNextArrivalCountDownTime(nextArrivalTime));
            tvNextArrivalTime.setText(time);

            return convertView;
        }
    }

    private NextArrivalListAdapter getNextArrivalListAdapter() {
        ArrayList<HashMap<String, Object>> data = new ArrayList<HashMap<String, Object>>();

        for (int i = 0; i < nextArrivals.size(); i++) {
            StopViewerSchedule nextArrival = nextArrivals.get(i);

            HashMap<String, Object> map = new HashMap<String, Object>();

            if (nextArrival.getPattern() != null) {
                map.put("tv_pattern", nextArrival.getPattern().getDesc());
            }
            data.add(map);
        }

        NextArrivalListAdapter mNextArrivalListAdapter = new NextArrivalListAdapter(
                this, data,
                R.layout.item_stop_viewer_next_arrival,
                new String[]{"tv_next_arrival_head_sign"}, new int[]{R.id.tv_next_arrival_head_sign});

        return mNextArrivalListAdapter;
    }

    class NextArrivalListAdapter extends SimpleAdapter {
        Context mContext;
        private final LayoutInflater l_Inflater;

        public NextArrivalListAdapter(Context context, List<? extends Map<String, ?>> data,
                                      int resource, String[] from, int[] to) {
            super(context, data, resource, from, to);

            mContext = context;
            l_Inflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return nextArrivals.size();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            StopViewerSchedule nextArrival = nextArrivals.get(position);
            if (convertView == null) {

                if (Utilities.checkIfToDisplayAccessibilityUI(mContext)) {
                    convertView = l_Inflater.inflate(
                            R.layout.item_stop_viewer_next_arrival_accessibility, null);
                } else {
                    convertView = l_Inflater.inflate(
                            R.layout.item_stop_viewer_next_arrival, null);
                }

            }

            ListView lvStopViewerArrivalTimes = convertView.findViewById(R.id.lv_stop_viewer_arrival_times);
            NetworkImageView networkImageView = convertView.findViewById(R.id.iv_arrival_logo);
            TextView tvNextArrivalHeadSign = convertView.findViewById(R.id.tv_next_arrival_head_sign);
            TextView tvNextArrivalTime = convertView.findViewById(R.id.tv_next_arrival_time);
            ImageView ivNextArrivalTimesController = convertView.findViewById(R.id.iv_next_arrival_times_controller);
            LinearLayout llNextArrivalTimesController = convertView.findViewById(R.id.ll_next_arrival_times_controller);
            NextArrivalTimeListAdapter nextArrivalTimeListAdapter = getNextArrivalTimeListAdapter(nextArrival);
            lvStopViewerArrivalTimes.setAdapter(nextArrivalTimeListAdapter);
            Utilities.setTotalHeightToListViewExt(gDm.widthPixels - dpToPx(20), lvStopViewerArrivalTimes);

            if (nextArrival != null) {
                if (nextArrival.getPattern() != null) {
                    String headSign = null;
                    String routeName = null;
                    String agencyName = null;
                    if (nextArrival.getPattern().getRouteViewerRoute() != null) {
                        RouteViewerRoute routeViewerRoute = nextArrival.getPattern().getRouteViewerRoute();
                        if (routeViewerRoute != null) {
                            if (routeViewerRoute.getLongName() != null && !routeViewerRoute.getLongName().isEmpty()) {
                                routeName = routeViewerRoute.getLongName();
                            } else if (routeViewerRoute.getShortName() != null && !routeViewerRoute.getShortName().isEmpty()) {
                                routeName = routeViewerRoute.getShortName();
                            }
                            //update agency logo here
                            agencyName = routeViewerRoute.getAgencyName().toLowerCase().replaceAll(" ", "_");

                        }
                    }
                    if (agencyName == null && nextArrival.getPattern().getId() != null) {
                        String[] ids = nextArrival.getPattern().getId().split(":");
                        if (ids.length > 0) {
                            agencyName = Utilities.getAgencyName(ids[0].toLowerCase());
                        }
                    }
                    if (agencyName != null) {
                        if (Utilities.agencyLogoIconMapping.containsKey(agencyName)) {
                            networkImageView.setDefaultImageResId(Utilities.agencyLogoIconMapping.get(agencyName));
                        } else {
                            networkImageView.setDefaultImageResId(R.drawable.ic_default_agency_logo);
                        }

                        String logoUrl = Resource.ROUTE_AGENCY_LOGO_BASE_URL + agencyName;
                        networkImageView.setImageUrl(logoUrl, MyApplication.getInstance().getImageLoader());
                        networkImageView.setVisibility(View.VISIBLE);

                        if (!uniqueAgentsUrl.containsKey(agencyName)) {
                            uniqueAgentsUrl.put(agencyName, logoUrl);
                            int iconSize = 35;
                            NetworkImageView nv = new NetworkImageView(getApplicationContext());

                            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                                    dpToPx(iconSize),
                                    dpToPx(iconSize)
                            );
                            nv.setLayoutParams(layoutParams);
                            nv.setImageUrl(logoUrl, MyApplication.getInstance().getImageLoader());
                            llHeaderHolder.addView(nv);
                        }
                    }

                    String timeHeadSign = null;
                    if (nextArrival.getTimes() != null && nextArrival.getTimes().size() > 0) {
                        timeHeadSign = nextArrival.getTimes().get(0).getHeadsign();
                    }
                    if (timeHeadSign != null) {
                        if (routeName != null) {
                            headSign = routeName + " " + timeHeadSign.replace(routeName, "").trim();
                        } else {
                            headSign = timeHeadSign;
                        }
                    } else {
                        headSign = Utilities.extractHeadsignFromPattern(nextArrival.getPattern(), null);
                    }

                    if (headSign != null) {
                        String desc = nextArrival.getPattern().getDesc();
                        if (desc != null) {
                            String[] temp = desc.split(" ");
                            if (temp.length > 2 && temp[1].equalsIgnoreCase("to")) { //handle special case like 424 to Snohomish Park & Ride Bay 2 (CommTrans:861) from 4th Ave S & S Jackson St (kcm:620) via Monroe Park & Ride Bay 1 (CommTrans:1928)
                                try {
                                    Integer.parseInt(headSign.split(" ")[0]);
                                    desc = headSign;
                                } catch (Exception e) {
                                    if (nextArrival.getPattern() != null && nextArrival.getPattern().getRouteViewerRoute() != null
                                            && nextArrival.getPattern().getRouteViewerRoute().getLongName() != null) {
                                        desc = temp[0] + " " + nextArrival.getPattern().getRouteViewerRoute().getLongName();
                                    } else {
                                        desc = temp[0] + " " + headSign;
                                    }
                                }
                            }
                        } else {
                            desc = nextArrival.getPattern().getRouteViewerRoute().getShortName() + " " + nextArrival.getPattern().getHeadsign();
                        }

                        tvNextArrivalHeadSign.setText(desc);

                    }
                    if (nextArrival.getPattern().getHeadsign() != null
                            && nextArrival.getPattern().getRouteViewerRoute() != null
                            && nextArrival.getPattern().getRouteViewerRoute().getShortName() != null) {
                        tvNextArrivalHeadSign.setText(nextArrival.getPattern().getRouteViewerRoute().getShortName() + " " + nextArrival.getPattern().getHeadsign());
                    }

                }

                if (nextArrival.getTimes() != null && nextArrival.getTimes().size() > 0) {
                    StopViewerTime stopViewerTime = nextArrival.getTimes().get(0);


                    long nextArrivalTime;

                    if (stopViewerTime.isRealtime()) {
                        nextArrivalTime = stopViewerTime.getServiceDay() + stopViewerTime.getRealtimeDeparture();
                    } else {
                        nextArrivalTime = stopViewerTime.getServiceDay() + stopViewerTime.getScheduledDeparture();
                    }

                    tvNextArrivalTime.setText(formatNextArrivalTime(nextArrivalTime));

                }
            }
            if (position < nextArrivalExpandStatusArray.size()) {
                if (nextArrivalExpandStatusArray.get(position)) {
                    lvStopViewerArrivalTimes.setVisibility(View.VISIBLE);

                    ivNextArrivalTimesController.setImageResource(R.drawable.ic_up);
                } else {
                    lvStopViewerArrivalTimes.setVisibility(View.GONE);
                    ivNextArrivalTimesController.setImageResource(R.drawable.ic_down);
                }
            }
            llNextArrivalTimesController.setTag(position);
            llNextArrivalTimesController.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = (Integer) v.getTag();
                    if (position < nextArrivalExpandStatusArray.size()) {
                        nextArrivalExpandStatusArray.set(position, !nextArrivalExpandStatusArray.get(position));

                        if (nextArrivalListAdapter != null) {
                            nextArrivalListAdapter.notifyDataSetChanged();
                        }

                        Utilities.setTotalHeightToListViewExt(gDm.widthPixels - dpToPx(20),
                                lvStopViewerArrivalsList);

                        if (nextArrivalExpandStatusArray.get(position)) {
                            //show routes on map
                            if (position < nextArrivals.size()) {
                                StopViewerSchedule nextArrival = nextArrivals.get(position);

                                if (nextArrival != null && nextArrival.getPattern() != null) {
                                    //remove routes on map first before drawing
                                    if (getRouteViewerRouteGeometryRequest != null) {
                                        getRouteViewerRouteGeometryRequest.cancel();
                                        getRouteViewerRouteGeometryRequest = null;
                                    }
                                    currentRouteID = null;
                                    removeRouteGeometryFromMapIfNeeded();
                                    getRouteViewerRouteGeometry(nextArrival.getPattern().getId());
                                }
                            }

                        } else {
                            //remove routes on map
                            if (getRouteViewerRouteGeometryRequest != null) {
                                getRouteViewerRouteGeometryRequest.cancel();
                                getRouteViewerRouteGeometryRequest = null;
                            }
                            currentRouteID = null;
                            removeRouteGeometryFromMapIfNeeded();
                        }
                    }
                }
            });
            return convertView;
        }
    }

    private void moveMapToShowRouteOnRouteViewer() {
        if (gMapboxMap != null && routeViewerRoutePoints != null) {
            LatLngBounds.Builder latLngBoundsBuilder = new LatLngBounds.Builder();

            for (String routeViewerRoutePoint : routeViewerRoutePoints) {
                List<Point> routeCoordinates = Utilities.decodeGPSPointsForRoute(routeViewerRoutePoint);
                for (Point point : routeCoordinates) {
                    latLngBoundsBuilder.include(new LatLng(point.latitude(), point.longitude()));
                }
            }

            int paddingBottom = gDm.heightPixels * 6 / 10;

            gMapboxMap.animateCamera(CameraUpdateFactory.newLatLngBounds(latLngBoundsBuilder.build(),
                    dpToPx(16), dpToPx(16), dpToPx(16), paddingBottom), null);
        }
    }

    private void removeRouteGeometryFromMapIfNeeded() {
        for (LineLayer lineLayer : routeViewerRouteLayers) {
            if (lineLayer != null) {
                gMapStyle.removeLayer(lineLayer);
            }
        }
        routeViewerRouteLayers.clear();

        for (Source source : routeViewerRouteSources) {
            if (source != null) {
                gMapStyle.removeSource(source);
            }
        }
        routeViewerRouteSources.clear();

        routeViewerRoutePoints.clear();
    }

    private String getCurrentRouteColor() {
        ArrayList<RouteViewerRoute> routeViewerRoutes = MyApplication.getInstance().routeViewerRoutes;

        if (routeViewerRoutes != null && routeViewerRoutes.size() > 0 && currentRouteID != null) {
            for (RouteViewerRoute routeViewerRoute : routeViewerRoutes) {
                if (routeViewerRoute != null && currentRouteID.contains(routeViewerRoute.getId())) {
                    return routeViewerRoute.getColor();
                }
            }
        }

        return null;
    }

    private void showRouteGeometryOnMap(String points) {
        if (points != null) {
            String routeColorString = getCurrentRouteColor();
            int routeColor;

            if (routeColorString == null) {
                routeColor = getResources().getColor(R.color.colorPrimary);
            } else {
                if (!routeColorString.startsWith("#")) {
                    routeColorString = "#" + routeColorString;
                }

                routeColor = Color.parseColor(routeColorString);
            }

            List<Point> routeCoordinates = Utilities.decodeGPSPointsForRoute(points);

            int index = routeViewerRouteSources.size() + 1;
            String sourceID = "route_viewer_route_source_" + index;
            String layerID = "route_viewer_route_layer_" + index;
            Source source = new GeoJsonSource(sourceID,
                    FeatureCollection.fromFeatures(new Feature[]{Feature.fromGeometry(
                            LineString.fromLngLats(routeCoordinates)
                    )}));
            LineLayer layer = new LineLayer(layerID, sourceID).withProperties(
                    PropertyFactory.lineCap(Property.LINE_CAP_ROUND),
                    PropertyFactory.lineJoin(Property.LINE_JOIN_ROUND),
                    PropertyFactory.lineWidth(5.0f),
                    PropertyFactory.lineColor(routeColor)
            );
            routeViewerRoutePoints.add(points);
            gMapStyle.addSource(source);
            gMapStyle.addLayer(layer);

            routeViewerRouteSources.add(source);
            routeViewerRouteLayers.add(layer);
        }
    }

    private void getRouteViewerRouteGeometry(String routeSubID) {
        currentRouteID = routeSubID;

        Response.Listener<String> listener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response != null && !response.isEmpty()) {
                    Gson gson = new Gson();

                    ItineraryLegGeometry routeGeometry = gson.fromJson(response, ItineraryLegGeometry.class);
                    if (routeGeometry != null) {
                        showRouteGeometryOnMap(routeGeometry.getPoints());
                        moveMapToShowRouteOnRouteViewer();
                    }
                } else {
                    Logger.d("data", "route viewer route detail is not correct:" + response);
                }
            }
        };
        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Logger.e("data", "get route viewer route detail error:" + error.toString());
            }
        };

        try {
            getRouteViewerRouteGeometryRequest = getRouteViewerRouteGeometryResponse(routeSubID, listener, errorListener);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public StringRequest getRouteViewerRouteGeometryResponse(String routeSubID, Response.Listener<String> listener,
                                                             Response.ErrorListener errListener) throws Exception {
        if (routeSubID != null) {
            String url = String.format(Resource.API_ROUTE_VIEWER_GEOMETRY, routeSubID);

            return volleyStringRequest(Request.Method.GET, url, "", listener, errListener);
        }

        return null;
    }

    private void showStopViewerSchedule() {
        rlAutoRefresh.setVisibility(View.GONE);
        lvStopViewerArrivalsList.setVisibility(View.GONE);

        llStopViewerDate.setVisibility(View.VISIBLE);
        llStopViewerScheduleTitle.setVisibility(View.VISIBLE);
        lvStopViewerSchedule.setVisibility(View.VISIBLE);
    }

    private void showStopViewerNextArrivals() {
        rlAutoRefresh.setVisibility(View.VISIBLE);
        lvStopViewerArrivalsList.setVisibility(View.VISIBLE);

        llStopViewerDate.setVisibility(View.GONE);
        llStopViewerScheduleTitle.setVisibility(View.GONE);
        lvStopViewerSchedule.setVisibility(View.GONE);
    }

    private void refreshStopViewer() {
        if (stopViewerItineraryLegPoint != null) {
            String stopID = stopViewerItineraryLegPoint.getStopId();
            String stopCode = stopViewerItineraryLegPoint.getStopCode();

            if (stopCode != null && !stopCode.isEmpty()) {
                tvStopViewerStopID.setText(Html.fromHtml(getString(R.string.bold_stop_id_with_num, stopCode)));
            } else {
                TransitStop transitStop = Utilities.getStopInfo(stopID);
                if (transitStop != null && transitStop.getCode() != null) {
                    tvStopViewerStopID.setText(Html.fromHtml(getString(R.string.bold_stop_id_with_num, transitStop.getCode())));
                } else {
                    if (stopID != null && stopID.contains(":") && stopID.length() > (stopID.lastIndexOf(":") + 1)) {
                        stopID = stopID.substring(stopID.lastIndexOf(":") + 1);
                    }
                    tvStopViewerStopID.setText(Html.fromHtml(getString(R.string.bold_stop_id_with_num, stopID)));
                }
            }

            tvStopViewerTitle.setText(stopViewerItineraryLegPoint.getName());
        }

        showStopViewerNextArrivals();

        resetStopViewerDatePicker();

        refreshFavButton();
    }

    private void refreshStopViewerRefreshTime() {
        if (tvStopViewerRefreshTime != null) {
            tvStopViewerRefreshTime.setText(Utilities.formatTimestamp(this, System.currentTimeMillis()));
        }
    }

    private void refreshStopViewerDate() {

        DatePicker dpSearchDateTmp = datePickerDialog.getDatePicker();
        int year = dpSearchDateTmp.getYear();
        int month = dpSearchDateTmp.getMonth();
        int dayOfMonth = dpSearchDateTmp.getDayOfMonth();
        //refreshSearchDate();

        String dateString = String.format(Locale.getDefault(), "%d-%02d-%02d", year, month + 1, dayOfMonth);
        tvStopViewerDate.setText(dateString);
    }

    private void resetStopViewerDatePicker() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());

        DatePicker dpSearchDateTmp = datePickerDialog.getDatePicker();
        dpSearchDateTmp.updateDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        refreshStopViewerDate();
    }

    private void setupStopViewer() {
        tvStopViewerTitle = findViewById(R.id.tv_stop_viewer_title);
        llHeaderHolder = findViewById(R.id.ll_header_holder);

        tvStopViewerStopID = findViewById(R.id.tv_stop_viewer_stop_id);
        tvStopViewerFromHere = findViewById(R.id.tv_stop_viewer_from_here);
        tvStopViewerToHere = findViewById(R.id.tv_stop_viewer_to_here);
        llStopViewer = findViewById(R.id.ll_stop_viewer);

        lvStopViewerArrivalsList = findViewById(R.id.lv_stop_viewer_arrivals_list);
        tvStopViewerRefreshTime = findViewById(R.id.tv_stop_viewer_refresh_time);

        rlAutoRefresh = findViewById(R.id.rl_auto_refresh);
        llNoDataForArrivals = findViewById(R.id.ll_no_data_available);

        llStopViewerDate = findViewById(R.id.ll_stop_viewer_date);
        llStopViewerScheduleTitle = findViewById(R.id.ll_stop_viewer_schedule_title);
        lvStopViewerSchedule = findViewById(R.id.lv_stop_viewer_schedule);

        tvStopViewerDate = findViewById(R.id.tv_stop_viewer_date);
        dpStopViewerDate = findViewById(R.id.dp_stop_viewer_date);
        rlStopViewerDate = findViewById(R.id.rl_stop_viewer_date);

        llStopViewerAutoRefresh = findViewById(R.id.ll_stop_viewer_auto_refresh);
        llStopViewerRefreshTime = findViewById(R.id.ll_stop_viewer_refresh_time);
        swStopViewerAutoRefresh = findViewById(R.id.sw_stop_viewer_auto_refresh);
        ivStopViewerRefreshTime = findViewById(R.id.iv_stop_viewer_refresh_time);

        rlDepartures = findViewById(R.id.rl_departures);
        rlSchedule = findViewById(R.id.rl_schedule);
        tvDepartures = findViewById(R.id.tv_departures);
        tvSchedule = findViewById(R.id.tv_schedule);
        rlDepartures.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uniqueAgentsUrl = new HashMap<String, String>();
                if (llHeaderHolder != null) {
                    for (int i = llHeaderHolder.getChildCount() - 1; i >= 0; i--) {
                        llHeaderHolder.removeViewAt(i);
                    }
                }
                rlDepartures.setBackgroundResource(R.drawable.rounded_white_with_shadow_bg);
                rlSchedule.setBackground(null);

                tvDepartures.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                tvSchedule.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));

                showStopViewerNextArrivals();
                refreshStopViewerAutoRefresh(true);
            }
        });

        rlSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                uniqueAgentsUrl = new HashMap<String, String>();
                if (llHeaderHolder != null) {
                    for (int i = llHeaderHolder.getChildCount() - 1; i >= 0; i--) {
                        llHeaderHolder.removeViewAt(i);
                    }
                }

                rlDepartures.setBackground(null);
                rlSchedule.setBackgroundResource(R.drawable.rounded_white_with_shadow_bg);

                tvSchedule.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
                tvDepartures.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));

                showStopViewerSchedule();
                StopViewerNextArrivalManager.getInstance().stop();
                getScheduleTimes(false);
                scrollToClosedSchedule();
            }
        });

        rlDepartures.setSelected(true);
        rlSchedule.setSelected(false);

        // 2) shared delegate that only strips off the click hint when host.isSelected()==true:
        final AccessibilityDelegateCompat toggleDelegate = new AccessibilityDelegateCompat() {
            @Override
            public void onInitializeAccessibilityNodeInfo(View host,
                                                          AccessibilityNodeInfoCompat info) {
                super.onInitializeAccessibilityNodeInfo(host, info);

                // always announce exactly the contentDescription
                info.setContentDescription(host.getContentDescription());

                // if the view is selected, remove the "double-tap to activate" hint
                if (host.isSelected()) {
                    info.removeAction(AccessibilityNodeInfoCompat.AccessibilityActionCompat.ACTION_CLICK);
                    info.setClickable(false);
                    info.setCollectionItemInfo(null);
                }
            }
        };


        rlDepartures.setContentDescription(getString(R.string.departures));
        rlDepartures.setFocusable(true);
        rlDepartures.setClickable(true);
        ViewCompat.setAccessibilityDelegate(rlDepartures, toggleDelegate);

        rlSchedule.setContentDescription(getString(R.string.schedule));
        rlSchedule.setFocusable(true);
        rlSchedule.setClickable(true);
        ViewCompat.setAccessibilityDelegate(rlSchedule, toggleDelegate);

        // 4) in your click-listeners, flip that selected flag and then update UI:
        rlDepartures.setOnClickListener(v -> {
            rlDepartures.setSelected(true);
            rlSchedule.setSelected(false);

            // your existing UI logic:
            rlDepartures.setBackgroundResource(R.drawable.rounded_white_with_shadow_bg);
            rlSchedule.setBackground(null);
            tvDepartures.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
            tvSchedule.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
            showStopViewerNextArrivals();
            refreshStopViewerAutoRefresh(true);

            // if you want TalkBack to re‐announce immediately:
            rlDepartures.sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_ACCESSIBILITY_FOCUSED);
        });

        rlSchedule.setOnClickListener(v -> {
            rlSchedule.setSelected(true);
            rlDepartures.setSelected(false);

            rlDepartures.setBackground(null);
            rlSchedule.setBackgroundResource(R.drawable.rounded_white_with_shadow_bg);
            tvSchedule.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
            tvDepartures.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
            showStopViewerSchedule();
            StopViewerNextArrivalManager.getInstance().stop();
            getScheduleTimes(false);
            scrollToClosedSchedule();

            rlSchedule.sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_ACCESSIBILITY_FOCUSED);
        });

        llStopViewerDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker();
                disableAccessbilityForCalender();
            }
        });

        scheduleTimeListAdapter = getScheduleTimeListAdapter();
        lvStopViewerSchedule.setAdapter(scheduleTimeListAdapter);
        Utilities.setTotalHeightToListViewExt(gDm.widthPixels - dpToPx(20),
                lvStopViewerSchedule);
        nextArrivalListAdapter = getNextArrivalListAdapter();
        lvStopViewerArrivalsList.setAdapter(nextArrivalListAdapter);

        nextArrivalListener = new StopViewerNextArrivalManager.NextArrivalListener() {
            @Override
            public void nextArrivalChanged() {
                if (ivStopViewerRefreshTime != null) {
                    ivStopViewerRefreshTime.clearAnimation();
                }

                if (MyApplication.getInstance().stopViewerNextArrivals == null ||
                        MyApplication.getInstance().stopViewerNextArrivals.isEmpty()) {
                    lvStopViewerArrivalsList.setVisibility(View.GONE);
                    llNoDataForArrivals.setVisibility(View.VISIBLE);
                } else {
                    nextArrivals.clear();
                    nextArrivals.addAll(MyApplication.getInstance().stopViewerNextArrivals);

                    //todo remove duplicate arrival matching to web
                    HashSet<String> uniqueIds = new HashSet<>();
                    Iterator<StopViewerSchedule> iterator = nextArrivals.iterator();

                    while (iterator.hasNext()) {
                        try {
                            StopViewerSchedule schedule = iterator.next();
                            String headSign = schedule.getPattern().getHeadsign();
                            String patternId = schedule.getPattern().getRouteViewerRoute().getId();
                            String mode = schedule.getPattern().getRouteViewerRoute().getMode();
                            String name = schedule.getPattern().getRouteViewerRoute().getShortName();
                            String uniquePart = headSign + patternId + mode + name;
                            if (uniqueIds.contains(uniquePart)) {
                                iterator.remove();
                            } else {
                                uniqueIds.add(uniquePart);
                            }
                        } catch (Exception e) {
                            Log.d("StopViewerActivity", e.getMessage());
                        }
                    }

                    if (nextArrivalExpandStatusArray.size() > nextArrivals.size()) {
                        nextArrivalExpandStatusArray.subList(nextArrivals.size(), nextArrivalExpandStatusArray.size()).clear();
                    } else {
                        for (int i = nextArrivalExpandStatusArray.size(); i < nextArrivals.size(); i++) {
                            nextArrivalExpandStatusArray.add(false);
                        }
                    }

                    if (nextArrivalListAdapter != null) {
                        nextArrivalListAdapter.notifyDataSetChanged();
                    }

                    showStopViewerNextArrivals();
                    Utilities.setTotalHeightToListViewExt(gDm.widthPixels - dpToPx(20), lvStopViewerArrivalsList);
                    refreshStopViewerRefreshTime();
                }
            }

            @Override
            public void startRequest() {
                if (aniRotateClk == null) {
                    aniRotateClk = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_animation_clockwise);
                }
                if (ivStopViewerRefreshTime != null) {
                    ivStopViewerRefreshTime.startAnimation(aniRotateClk);
                }
            }

            @Override
            public void stopRequest() {
                if (ivStopViewerRefreshTime != null) {
                    ivStopViewerRefreshTime.clearAnimation();
                }
            }
        };
        StopViewerNextArrivalManager.getInstance().addListener(nextArrivalListener);

        llStopViewerAutoRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Configurations.setBoolean("stop_viewer_auto_refresh_enabled",
                        !Configurations.getBoolean("stop_viewer_auto_refresh_enabled", true));

                refreshStopViewerAutoRefresh(false);
            }
        });

        llStopViewerRefreshTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshStopViewerAutoRefresh(true);
            }
        });

        tvStopViewerFromHere.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (stopViewerItineraryLegPoint != null) {
                    gpsReverseMode = Constants.GPS_REVERSE_MODE_STOP_VIEWER_FROM;
                    getGpsReverseResult(stopViewerItineraryLegPoint.getLat(), stopViewerItineraryLegPoint.getLon());
                }

            }
        });

        tvStopViewerToHere.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (stopViewerItineraryLegPoint != null) {
                    gpsReverseMode = Constants.GPS_REVERSE_MODE_STOP_VIEWER_TO;
                    getGpsReverseResult(stopViewerItineraryLegPoint.getLat(), stopViewerItineraryLegPoint.getLon());
                }
            }
        });
        setStopViewerHeight();
    }


    private void setupDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());

        currentDatePickerYear = calendar.get(Calendar.YEAR);
        currentDatePickerMonth = calendar.get(Calendar.MONTH);
        currentDatePickerDayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

        datePickerYearTmp = currentDatePickerYear;
        datePickerMonthTmp = currentDatePickerMonth;
        datePickerDayOfMonthTmp = currentDatePickerDayOfMonth;

        datePickerDialog = new DatePickerDialog(
                this,
                null, // instead of a listener
                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.setCancelable(false);
        datePickerDialog.setCanceledOnTouchOutside(false);
//        datePickerDialog.getDatePicker().setMinDate(calendar.getTimeInMillis());
        datePickerDialog.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.ok),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DatePicker dpSearchDateTmp = datePickerDialog.getDatePicker();
                        datePickerYearTmp = dpSearchDateTmp.getYear();
                        datePickerMonthTmp = dpSearchDateTmp.getMonth();
                        datePickerDayOfMonthTmp = dpSearchDateTmp.getDayOfMonth();

                        String dateString = String.format(Locale.getDefault(), "%d-%02d-%02d", datePickerYearTmp,
                                datePickerMonthTmp + 1, datePickerDayOfMonthTmp);

                        refreshStopViewerDate();
                        getScheduleTimes(true);
                        enableAccessbilityForCalendar();

                    }
                });
        datePickerDialog.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.cancel),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        datePickerDialog.getDatePicker().updateDate(datePickerYearTmp,
                                datePickerMonthTmp, datePickerDayOfMonthTmp);
                        refreshStopViewerDate();
                        enableAccessbilityForCalendar();
                    }
                });
    }

    private void showDatePicker() {
        if (datePickerDialog == null) {
            setupDatePickerDialog();
        }
        datePickerDialog.show();
    }

    private void disableAccessbilityForCalender() {

        if (mapView != null) {

            mapView.setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_NO_HIDE_DESCENDANTS);

        }
        svStopViewer.setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_NO_HIDE_DESCENDANTS);
        llBottomTab.setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_NO_HIDE_DESCENDANTS);
        ivStopViewerBack.setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_NO_HIDE_DESCENDANTS);
    }

    private void enableAccessbilityForCalendar() {

        if (mapView != null) {

            mapView.setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_AUTO);
        }
        svStopViewer.setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_AUTO);
        llBottomTab.setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_AUTO);
        ivStopViewerBack.setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_AUTO);
    }

    private void scrollToClosedSchedule() {
        if (scheduleTimes != null && scheduleTimes.size() > 0) {
            long timestampCurrent = System.currentTimeMillis() / 1000;

            int i;
            for (i = 0; i < scheduleTimes.size(); i++) {
                StopViewerTime stopViewerTime = scheduleTimes.get(i);
                long arrivalTime;
                if (stopViewerTime != null) {
                    if (stopViewerTime.isRealtime()) {
                        arrivalTime = stopViewerTime.getServiceDay() + stopViewerTime.getRealtimeArrival();
                    } else {
                        arrivalTime = stopViewerTime.getServiceDay() + stopViewerTime.getScheduledArrival();
                    }

                    if (timestampCurrent < arrivalTime) {
                        break;
                    }
                }
            }

            if (i < scheduleTimeListAdapter.getCount()) {
                nextScheduleTimeIndex = i;
            } else {
                nextScheduleTimeIndex = scheduleTimeListAdapter.getCount() - 1;
            }

            scheduleTimeListAdapter.notifyDataSetChanged();
            lvStopViewerSchedule.post(new Runnable() {
                @Override
                public void run() {
                    lvStopViewerSchedule.setSelection(nextScheduleTimeIndex);
                }
            });
            lvStopViewerSchedule.postDelayed(new Runnable() {
                @Override
                public void run() {
                    nextScheduleTimeIndex = -1;
                    scheduleTimeListAdapter.notifyDataSetChanged();
                }
            }, 2000);
        }
    }

    private void refreshStopViewerAutoRefresh(boolean oneTimeRefresh) {
        if (Configurations.getBoolean("stop_viewer_auto_refresh_enabled", true)) {
            swStopViewerAutoRefresh.setChecked(true);

            StopViewerNextArrivalManager.getInstance().start();
        } else {
            swStopViewerAutoRefresh.setChecked(false);
            if (oneTimeRefresh) {
                StopViewerNextArrivalManager.getInstance().startForOneTime();
            } else {
                StopViewerNextArrivalManager.getInstance().stop();
            }
        }
    }

    private void getScheduleTimes(boolean isFromDatePick) {// TODO need to change
        Response.Listener getScheduleTimesListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response != null && !response.isEmpty() && response.startsWith("[")) {
                    Gson gson = new Gson();

                    ArrayList<StopViewerSchedule> stopViewerSchedules = gson.fromJson(response, new TypeToken<ArrayList<StopViewerSchedule>>() {
                    }.getType());

                    scheduleTimes.clear();
                    for (StopViewerSchedule stopViewerSchedule : stopViewerSchedules) {
                        if (stopViewerSchedule.getPattern() != null && stopViewerSchedule.getTimes() != null) {
                            String desc = stopViewerSchedule.getPattern().getDesc();
                            String route = "";
                            String to = null;
                            String routeShortName = null;

                            if (stopViewerSchedule.getPattern().getRouteId() != null) {
                                RouteViewerRoute routeViewerRoute = Utilities.getRouteViewerRouteByID(stopViewerSchedule.getPattern().getRouteId());
                                if (routeViewerRoute != null) {
                                    routeShortName = routeViewerRoute.getShortName();
                                }

                                if (routeViewerRoute.getShortName() != null && !routeViewerRoute.getShortName().isEmpty()) {
                                    route = routeViewerRoute.getShortName();
                                } else if (routeViewerRoute.getLongName() != null && !routeViewerRoute.getLongName().isEmpty()) {
                                    route = routeViewerRoute.getLongName();
                                }
                            }

                            if (stopViewerSchedule.getTimes() != null && stopViewerSchedule.getTimes().size() > 0) {
                                to = stopViewerSchedule.getTimes().get(0).getHeadsign();
                                if (to != null && routeShortName != null) {
                                    to = to.replace(routeShortName, "").trim();
                                }
                            }

                            if (to == null) {
                                to = Utilities.extractHeadsignFromPattern(stopViewerSchedule.getPattern(), routeShortName);
                            }

                            for (StopViewerTime stopViewerTime : stopViewerSchedule.getTimes()) {
                                stopViewerTime.setRoute(route);
                                stopViewerTime.setTo(to);
                            }

                            scheduleTimes.addAll(stopViewerSchedule.getTimes());
                        }
                    }

                    if (stopViewerTimeComparator == null) {
                        stopViewerTimeComparator = new Comparator<StopViewerTime>() {
                            @Override
                            public int compare(StopViewerTime o1, StopViewerTime o2) {
                                long arrivalTime1 = 0;
                                long arrivalTime2 = 0;

                                if (o1 != null) {
                                    if (o1.isRealtime()) {
                                        arrivalTime1 = o1.getServiceDay() + o1.getRealtimeArrival();
                                    } else {
                                        arrivalTime1 = o1.getServiceDay() + o1.getScheduledArrival();
                                    }
                                }

                                if (o2 != null) {
                                    if (o2.isRealtime()) {
                                        arrivalTime2 = o2.getServiceDay() + o2.getRealtimeArrival();
                                    } else {
                                        arrivalTime2 = o2.getServiceDay() + o2.getScheduledArrival();
                                    }
                                }
                                return Long.compare(arrivalTime1, arrivalTime2);
                            }
                        };
                    }
                    Collections.sort(scheduleTimes, stopViewerTimeComparator);
                    if (scheduleTimeListAdapter != null) {
                        scheduleTimeListAdapter.notifyDataSetInvalidated();
                    }
                    Utilities.setTotalHeightToListViewExt(gDm.widthPixels - dpToPx(20),
                            lvStopViewerSchedule);
                } else {
                    Log.d("ScheduleTimes", "data is not correct:" + response);
                }

            }
        };
        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Logger.e("get Schedule Times feed error:", error.toString());
            }
        };

        try {
            getScheduleTimesResponse(getScheduleTimesListener, errorListener, isFromDatePick);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void getScheduleTimesResponse(Response.Listener listener,
                                         Response.ErrorListener errListener, boolean isFromDatePick) throws Exception {
        if (MyApplication.getInstance().stopViewerStopID != null) {
            if (!Configurations.getString("graphql_base_url", Configurations.GRAPHQL_SERVER_URL).isEmpty()) {
                ApolloClient apolloClient = ApolloClient.builder()
                        .serverUrl(Configurations.getString("graphql_base_url", Configurations.GRAPHQL_SERVER_URL))
                        .build();

                Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(Configurations.TARGET_TIMEZONE_ID));

                if (isFromDatePick) {
                    calendar.set(Calendar.YEAR, datePickerYearTmp);
                    calendar.set(Calendar.MONTH, datePickerMonthTmp);
                    calendar.set(Calendar.DAY_OF_MONTH, datePickerDayOfMonthTmp);
                }

                // Set to today at 3:30 AM
                calendar.set(Calendar.HOUR_OF_DAY, 3);
                calendar.set(Calendar.MINUTE, 30);
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);
                long timestampInSeconds = calendar.getTimeInMillis() / 1000;

                String stopId = MyApplication.getInstance().stopViewerStopID;
                apolloClient.query(new StopScheduleQuery(timestampInSeconds, stopId)).enqueue(new ApolloCall.Callback<StopScheduleQuery.Data>() {
                    @Override
                    public void onResponse(@NonNull com.apollographql.apollo.api.Response<StopScheduleQuery.Data> response) {
                        if (response != null && response.getData() != null && response.getData().stop() != null
                                && response.getData().stop().stopTimes() != null) {

                            Handler mainHandler = new Handler(Looper.getMainLooper());
                            mainHandler.post(() -> {

                                Gson gson = new Gson();
                                ArrayList<StopViewerSchedule> stopViewerSchedules = gson.fromJson(gson.toJson(response.getData().stop().stopTimes()), new TypeToken<ArrayList<StopViewerSchedule>>() {
                                }.getType());

                                ArrayList<RouteViewerRoute> stopViewerRoutes = gson.fromJson(gson.toJson(response.getData().stop().routes), new TypeToken<ArrayList<RouteViewerRoute>>() {
                                }.getType());

                                scheduleTimes.clear();
                                for (StopViewerSchedule stopViewerSchedule : stopViewerSchedules) {
                                    if (stopViewerSchedule.getPattern() != null && stopViewerSchedule.getTimes() != null) {
                                        String desc = stopViewerSchedule.getPattern().getDesc();
                                        String route = "";
                                        String to = null;
                                        String routeShortName = null;
                                        String lastStopID = null;
                                        if (stopViewerSchedule.getPattern().getStops() != null) {
                                            lastStopID = stopViewerSchedule.getPattern().getStops().get(stopViewerSchedule.getPattern().getStops().size() - 1).getId();
                                        }


                                        if (stopViewerSchedule.getPattern().getRoute() != null &&
                                                stopViewerSchedule.getPattern().getRoute().getGtfsId() != null) {
                                            String routeID = stopViewerSchedule.getPattern().getRoute().getGtfsId();
                                            RouteViewerRoute routeViewerRoute = Utilities.getRouteViewerRouteByID(routeID);

                                            if (routeViewerRoute != null) {
                                                routeShortName = routeViewerRoute.getShortName();
                                                if (routeViewerRoute.getShortName() != null && !routeViewerRoute.getShortName().isEmpty()) {
                                                    route = routeViewerRoute.getShortName();
                                                } else if (routeViewerRoute.getLongName() != null && !routeViewerRoute.getLongName().isEmpty()) {
                                                    route = routeViewerRoute.getLongName();
                                                }
                                            }
                                        }

                                        if (stopViewerSchedule.getTimes() != null && stopViewerSchedule.getTimes().size() > 0) {
                                            to = stopViewerSchedule.getTimes().get(0).getHeadsign();
                                            if (to != null && routeShortName != null) {
                                                to = to.replace(routeShortName, "").trim();
                                            }
                                        }

                                        if (to == null) {
                                            to = Utilities.extractHeadsignFromPattern(stopViewerSchedule.getPattern(), routeShortName);
                                        }

                                        for (StopViewerTime stopViewerTime : stopViewerSchedule.getTimes()) {
                                            for (RouteViewerRoute routeViewerRoute : stopViewerRoutes) {
                                                RouteAgency agency = stopViewerSchedule.getPattern().getRoute().getAgency();
                                                if (routeViewerRoute.getAgency().getGtfsId().equalsIgnoreCase(agency.getGtfsId())) {
                                                    agency.setName(routeViewerRoute.getAgency().getName());
                                                    stopViewerTime.setRouteAgency(agency);
                                                    break;
                                                }
                                            }
                                        }

                                        for (StopViewerTime stopViewerTime : stopViewerSchedule.getTimes()) {
                                            if (stopViewerTime.getStop() != null && stopViewerTime.getStop().getId() != null) {
                                                stopViewerTime.setRoute(route);
                                                stopViewerTime.setTo(to);
                                                scheduleTimes.add(stopViewerTime);
                                            }
                                        }
                                    }
                                }

                                Comparator<StopViewerTime> stopViewerTimeComparator = null;

                                if (stopViewerTimeComparator == null) {
                                    stopViewerTimeComparator = new Comparator<StopViewerTime>() {
                                        @Override
                                        public int compare(StopViewerTime o1, StopViewerTime o2) {
                                            long arrivalTime1 = 0;
                                            long arrivalTime2 = 0;

                                            if (o1 != null) {
                                                if (o1.isRealtime()) {
                                                    arrivalTime1 = o1.getServiceDay() + o1.getRealtimeArrival();
                                                } else {
                                                    arrivalTime1 = o1.getServiceDay() + o1.getScheduledArrival();
                                                }
                                            }

                                            if (o2 != null) {
                                                if (o2.isRealtime()) {
                                                    arrivalTime2 = o2.getServiceDay() + o2.getRealtimeArrival();
                                                } else {
                                                    arrivalTime2 = o2.getServiceDay() + o2.getScheduledArrival();
                                                }
                                            }
                                            return Long.compare(arrivalTime1, arrivalTime2);
                                        }
                                    };
                                }
                                Collections.sort(scheduleTimes, stopViewerTimeComparator);
                                if (scheduleTimeListAdapter != null) {
                                    scheduleTimeListAdapter.notifyDataSetChanged();
                                    Utilities.setTotalHeightToListViewExt(gDm.widthPixels - dpToPx(20),
                                            lvStopViewerSchedule);
                                }
                            });

                        } else {
                            Log.d("ScheduleTimes", "data is not correct:" + response);
                        }
                    }

                    @Override
                    public void onFailure(@NonNull ApolloException e) {
                        Logger.e("get Schedule Times feed error:", e.toString());
                    }
                });

            } else {
                DatePicker dpSearchDateTmp = datePickerDialog.getDatePicker();
                int year = dpSearchDateTmp.getYear();
                int month = dpSearchDateTmp.getMonth();
                int dayOfMonth = dpSearchDateTmp.getDayOfMonth();

                String dateString = String.format(Locale.getDefault(), "%d%02d%02d", year, month + 1, dayOfMonth);

                String url = String.format(Locale.getDefault(), Resource.API_STOP_VIEWER_SCHEDULE,
                        MyApplication.getInstance().stopViewerStopID, dateString);

                volleyStringRequest(Request.Method.GET, url, "", listener, errListener);
            }
        }
    }

    private void setStopViewerHeight() {
        gDm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(gDm);
        svStopViewer = findViewById(R.id.sv_stop_viewer);
        if (svStopViewer != null) {
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) svStopViewer.getLayoutParams();
            layoutParams.width = gDm.widthPixels;
            layoutParams.height = gDm.heightPixels * 6 / 10 - dpToPx(105);

            svStopViewer.setLayoutParams(layoutParams);
        }
    }

    private void refreshPlanTripTab() {
        if (currentSelectedTab == Constants.BOTTOM_TAB_SELECTED_PLAN_TRIP) {
            iv_tab_pan_trip.setColorFilter(getResources().getColor(R.color.tabColorSelected));
            tv_tab_pan_trip.setTextColor(getResources().getColor(R.color.tabColorSelected));
        } else {
            iv_tab_pan_trip.setColorFilter(getResources().getColor(R.color.tabColorUnselected));
            tv_tab_pan_trip.setTextColor(getResources().getColor(R.color.tabColorUnselected));
        }
    }

    private void refreshRoutesTab() {
        if (currentSelectedTab == Constants.BOTTOM_TAB_SELECTED_ROUTES) {
            iv_tab_routes.setColorFilter(getResources().getColor(R.color.tabColorSelected));
            tv_tab_routes.setTextColor(getResources().getColor(R.color.tabColorSelected));
        } else {
            iv_tab_routes.setColorFilter(getResources().getColor(R.color.tabColorUnselected));
            tv_tab_routes.setTextColor(getResources().getColor(R.color.tabColorUnselected));
        }
    }

    private void refreshStopsTab() {
        if (currentSelectedTab == Constants.BOTTOM_TAB_SELECTED_STOPS) {
            iv_tab_stops.setColorFilter(getResources().getColor(R.color.tabColorSelected));
            tv_tab_stops.setTextColor(getResources().getColor(R.color.tabColorSelected));
        } else {
            iv_tab_stops.setColorFilter(getResources().getColor(R.color.tabColorUnselected));
            tv_tab_stops.setTextColor(getResources().getColor(R.color.tabColorUnselected));
        }
    }

    private void refreshMyTripsTab() {
        if (currentSelectedTab == Constants.BOTTOM_TAB_SELECTED_MY_TRIPS) {
            iv_tab_my_trips.setColorFilter(getResources().getColor(R.color.tabColorSelected));
            tv_tab_my_trips.setTextColor(getResources().getColor(R.color.tabColorSelected));
        } else {
            iv_tab_my_trips.setColorFilter(getResources().getColor(R.color.tabColorUnselected));
            tv_tab_my_trips.setTextColor(getResources().getColor(R.color.tabColorUnselected));
        }
    }

    private void refreshAccountStatusInTab() {
        boolean loggedIn = Utilities.isLoggedIn();
        Gson gson = new Gson();
        try {
            if (loggedIn) {
                String nickName = "";
                String fullName = "";
                String profileString = Configurations.getString("account_user_profile", "");
                if (profileString != null && !profileString.isEmpty()) {
                    UserProfile userProfile = gson.fromJson(profileString, UserProfile.class);

                    if (userProfile.getNickname() != null) {
                        nickName = userProfile.getNickname();
                        if (nickName.length() >= 2) {
                            nickName = nickName.substring(0, 2).toUpperCase();
                        }
                        tv_user_name.setText(nickName);
                        fullName = userProfile.getName();
                    }
                }
                ll_tab_sign_in.setVisibility(View.GONE);
                ll_tab_my_trips.setVisibility(View.VISIBLE);
                rl_user_name.setVisibility(View.VISIBLE);
                if (ll_bottom_tab_accessibility.getVisibility() == View.VISIBLE) {
                    checkSignInTagToReplaceUserName(nickName, fullName);
                    showMyTrips();
                }
            } else {
                ll_tab_sign_in.setVisibility(View.VISIBLE);
                ll_tab_my_trips.setVisibility(View.GONE);
                rl_user_name.setVisibility(View.GONE);
                tv_user_name.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorTabUserUnselected)));
                if (ll_bottom_tab_accessibility.getVisibility() == View.VISIBLE) {
                    checkUserNameTagToReplaceSignIn();
                    disableMyTrips();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void disableMyTrips() {

        ll_bottom_tab_menu_item_one.setVisibility(View.GONE);
        ll_bottom_tab_menu_item_two.setVisibility(View.VISIBLE);

    }


    private void showMyTrips() {
        ll_bottom_tab_menu_item_one.setVisibility(View.VISIBLE);
        ll_bottom_tab_menu_item_two.setVisibility(View.GONE);


    }


    private void checkSignInTagToReplaceUserName(String nickName, String fullName) {

        ll_bottom_tab_menu_item_zero.setVisibility(View.VISIBLE);
        tv_bottom_tab_user_name_item_zero.setText(nickName);

    }


    private void checkUserNameTagToReplaceSignIn() {

        ll_bottom_tab_menu_item_zero.setVisibility(View.GONE);
        ll_bottom_tab_menu_item_one.setVisibility(View.GONE);
        ll_bottom_tab_menu_item_two.setVisibility(View.VISIBLE);
    }


    private void refreshBottomTabs() {
        refreshPlanTripTab();
        refreshRoutesTab();
        refreshStopsTab();
        refreshMyTripsTab();
        refreshAccountStatusInTab();
    }


    private void setupBottomTabs() {
        rl_user_name = findViewById(R.id.rl_user_name);
        tv_user_name = findViewById(R.id.tv_user_name);

        ll_tab_pan_trip = findViewById(R.id.ll_tab_pan_trip);
        ll_tab_routes = findViewById(R.id.ll_tab_routes);
        ll_tab_stops = findViewById(R.id.ll_tab_stops);
        ll_tab_my_trips = findViewById(R.id.ll_tab_my_trips);
        ll_tab_sign_in = findViewById(R.id.ll_tab_sign_in);
        ll_tab_menu = findViewById(R.id.ll_tab_menu);


        ll_bottom_tab_accessibility = findViewById(R.id.ll_bottom_tab_accessibility);
        rl_bottom_tab_menu_accessibility = findViewById(R.id.ll_bottom_tab_menu);
        rl_bottom_tab_menu_user_name = findViewById(R.id.rl_bottom_tab_menu_user_name);

        ll_bottom_tab_menu_item_five = findViewById(R.id.ll_bottom_tab_menu_item_five);
        ll_bottom_tab_menu_item_four = findViewById(R.id.ll_bottom_tab_menu_item_four);
        ll_bottom_tab_menu_item_three = findViewById(R.id.ll_bottom_tab_menu_item_three);
        ll_bottom_tab_menu_item_two = findViewById(R.id.ll_bottom_tab_menu_item_two);
        ll_bottom_tab_menu_item_one = findViewById(R.id.ll_bottom_tab_menu_item_one);
        ll_bottom_tab_menu_item_zero = findViewById(R.id.ll_bottom_tab_menu_item_zero);
        ll_bottom_tab_menu_item = findViewById(R.id.ll_bottom_tab_menu_item);

        rl_bottom_tab_user_name_item_one = findViewById(R.id.rl_bottom_tab_user_name_item_one);
        rl_bottom_tab_user_name_item_two = findViewById(R.id.rl_bottom_tab_user_name_item_two);
        rl_bottom_tab_user_name_item_three = findViewById(R.id.rl_bottom_tab_user_name_item_three);
        rl_bottom_tab_user_name_item_four = findViewById(R.id.rl_bottom_tab_user_name_item_four);
        rl_bottom_tab_user_name_item_zero = findViewById(R.id.rl_bottom_tab_user_name_item_zero);

        tv_bottom_tab_menu_user_name = findViewById(R.id.tv_bottom_tab_menu_user_name);
        tv_bottom_tab_user_name_item_four = findViewById(R.id.tv_bottom_tab_user_name_item_four);
        tv_bottom_tab_user_name_item_three = findViewById(R.id.tv_bottom_tab_user_name_item_three);
        tv_bottom_tab_user_name_item_two = findViewById(R.id.tv_bottom_tab_user_name_item_two);
        tv_bottom_tab_user_name_item_one = findViewById(R.id.tv_bottom_tab_user_name_item_one);
        tv_bottom_tab_user_name_item_zero = findViewById(R.id.tv_bottom_tab_user_name_item_zero);

        iv_bottom_tab_menu_item_four = findViewById(R.id.iv_bottom_tab_menu_item_four);
        iv_bottom_tab_menu_item_three = findViewById(R.id.iv_bottom_tab_menu_item_three);
        iv_bottom_tab_menu_item_two = findViewById(R.id.iv_bottom_tab_menu_item_two);
        iv_bottom_tab_menu_item_one = findViewById(R.id.iv_bottom_tab_menu_item_one);
        iv_bottom_tab_menu_item = findViewById(R.id.iv_bottom_tab_menu_item);

        tv_bottom_tab_menu_item_four = findViewById(R.id.tv_bottom_tab_menu_item_four);
        tv_bottom_tab_menu_item_three = findViewById(R.id.tv_bottom_tab_menu_item_three);
        tv_bottom_tab_menu_item_two = findViewById(R.id.tv_bottom_tab_menu_item_two);
        tv_bottom_tab_menu_item_one = findViewById(R.id.tv_bottom_tab_menu_item_one);
        tv_bottom_tab_menu_item = findViewById(R.id.tv_bottom_tab_menu_item);

        iv_down_button = findViewById(R.id.iv_bottom_tab_menu_item_expand);
        iv_bottom_tab_menu_item_five_expand = findViewById(R.id.iv_bottom_tab_menu_item_five_expand);


        iv_down_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (rl_bottom_tab_menu_accessibility.getVisibility() == View.VISIBLE) {
                    rl_bottom_tab_menu_accessibility.setVisibility(View.GONE);
                } else {

                    if (Integer.parseInt(ll_bottom_tab_menu_item.getTag().toString()) == USER_NAME_TAG) {

                    }

                    refreshMenuItemTabs();

                    rl_bottom_tab_menu_accessibility.setVisibility(View.VISIBLE);
                }
            }
        });


        iv_bottom_tab_menu_item_five_expand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rl_bottom_tab_menu_accessibility.setVisibility(View.GONE);
            }
        });


        View.OnClickListener bottomTabMenuClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.setPressed(true);
                rl_bottom_tab_menu_accessibility.setVisibility(View.GONE);
                switch (Integer.parseInt(view.getTag().toString())) {
                    case Constants.PLAN_TRIP_TAG:
                        currentSelectedTab = Constants.BOTTOM_TAB_SELECTED_PLAN_TRIP;
                        refreshBottomTabs();
                        clickTabAction();
                        break;


                    case Constants.ROUTES_TAG:

                        currentSelectedTab = Constants.BOTTOM_TAB_SELECTED_ROUTES;
                        refreshBottomTabs();
                        clickTabAction();
                        break;

                    case Constants.MENU_TAG:

                        mDrawerLayout.openDrawer(llDrawer);
                        break;

                    case Constants.MY_TRIPS_TAG:

                        currentSelectedTab = Constants.BOTTOM_TAB_SELECTED_MY_TRIPS;
                        refreshBottomTabs();
                        clickTabAction();
                        break;


                    case Constants.SIGN_IN_TAG:

                        currentSelectedTab = Constants.BOTTOM_TAB_SELECTED_SIGN_IN;
                        clickTabAction();

                        break;

                    case Constants.USER_NAME_TAG:
                        currentSelectedTab = Constants.BOTTOM_TAB_SELECTED_AVATAR;
                        clickTabAction();
                        break;


                }
            }
        };


        ll_bottom_tab_menu_item_five.setOnClickListener(bottomTabMenuClickListener);
        ll_bottom_tab_menu_item_four.setOnClickListener(bottomTabMenuClickListener);
        ll_bottom_tab_menu_item_three.setOnClickListener(bottomTabMenuClickListener);
        ll_bottom_tab_menu_item_two.setOnClickListener(bottomTabMenuClickListener);
        ll_bottom_tab_menu_item_one.setOnClickListener(bottomTabMenuClickListener);
        ll_bottom_tab_menu_item_zero.setOnClickListener(bottomTabMenuClickListener);
        rl_bottom_tab_menu_user_name.setOnClickListener(bottomTabMenuClickListener);
        ll_bottom_tab_menu_item.setOnClickListener(bottomTabMenuClickListener);


        ll_tab_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawerLayout.openDrawer(llDrawer);
            }
        });

        ll_tab_pan_trip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentSelectedTab = Constants.BOTTOM_TAB_SELECTED_PLAN_TRIP;
                refreshBottomTabs();
                clickTabAction();
            }
        });

        ll_tab_routes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentSelectedTab = Constants.BOTTOM_TAB_SELECTED_ROUTES;
                refreshBottomTabs();
                clickTabAction();
            }
        });

        ll_tab_stops.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentSelectedTab = Constants.BOTTOM_TAB_SELECTED_STOPS;
                refreshBottomTabs();
                clickTabAction();
            }
        });

        ll_tab_my_trips.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentSelectedTab = Constants.BOTTOM_TAB_SELECTED_MY_TRIPS;
                refreshBottomTabs();
                clickTabAction();
            }
        });

        ll_tab_sign_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentSelectedTab = Constants.BOTTOM_TAB_SELECTED_SIGN_IN;
                clickTabAction();
            }
        });

        rl_user_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentSelectedTab = Constants.BOTTOM_TAB_SELECTED_AVATAR;
                clickTabAction();
            }
        });

        tv_tab_pan_trip = findViewById(R.id.tv_tab_pan_trip);
        tv_tab_routes = findViewById(R.id.tv_tab_routes);
        tv_tab_stops = findViewById(R.id.tv_tab_stops);
        tv_tab_my_trips = findViewById(R.id.tv_tab_my_trips);
        tv_tab_sign_in = findViewById(R.id.tv_tab_sign_in);

        iv_tab_pan_trip = findViewById(R.id.iv_tab_pan_trip);
        iv_tab_routes = findViewById(R.id.iv_tab_routes);
        iv_tab_stops = findViewById(R.id.iv_tab_stops);
        iv_tab_my_trips = findViewById(R.id.iv_tab_my_trips);
        iv_tab_sign_in = findViewById(R.id.iv_tab_sign_in);

    }


    private void refreshMenuItemTabs() {


        if (currentSelectedTab == Constants.BOTTOM_TAB_SELECTED_PLAN_TRIP) {
            iv_bottom_tab_menu_item_four.setColorFilter(getResources().getColor(R.color.tabColorSelected));
            tv_bottom_tab_menu_item_four.setTextColor(getResources().getColor(R.color.tabColorSelected));
        } else {
            iv_bottom_tab_menu_item_four.setColorFilter(getResources().getColor(R.color.tabColorUnselected));
            tv_bottom_tab_menu_item_four.setTextColor(getResources().getColor(R.color.tabColorUnselected));
        }


        if (currentSelectedTab == Constants.BOTTOM_TAB_SELECTED_ROUTES) {
            iv_bottom_tab_menu_item_three.setColorFilter(getResources().getColor(R.color.tabColorSelected));
            tv_bottom_tab_menu_item_three.setTextColor(getResources().getColor(R.color.tabColorSelected));
        } else {
            iv_bottom_tab_menu_item_three.setColorFilter(getResources().getColor(R.color.tabColorUnselected));
            tv_bottom_tab_menu_item_three.setTextColor(getResources().getColor(R.color.tabColorUnselected));
        }


        if (currentSelectedTab == Constants.BOTTOM_TAB_SELECTED_MY_TRIPS) {
            iv_bottom_tab_menu_item_one.setColorFilter(getResources().getColor(R.color.tabColorSelected));
            tv_bottom_tab_menu_item_one.setTextColor(getResources().getColor(R.color.tabColorSelected));
        } else {
            iv_bottom_tab_menu_item_one.setColorFilter(getResources().getColor(R.color.tabColorUnselected));
            tv_bottom_tab_menu_item_one.setTextColor(getResources().getColor(R.color.tabColorUnselected));
        }


    }

    private void clickTabAction() {
        MyApplication.getInstance().currentSelectedTab = currentSelectedTab;
        Intent intent = new Intent(StopViewerActivity.this, TabActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
    }

    private void clickAccessbilityBottomTabAction() {
        ll_bottom_tab_menu_item.setPressed(currentSelectedTab == Integer.parseInt(ll_bottom_tab_menu_item.getTag().toString()));

        ll_bottom_tab_menu_item_one.setPressed(currentSelectedTab == Integer.parseInt(ll_bottom_tab_menu_item_one.getTag().toString()));

        ll_bottom_tab_menu_item_two.setPressed(currentSelectedTab == Integer.parseInt(ll_bottom_tab_menu_item_two.getTag().toString()));

        ll_bottom_tab_menu_item_three.setPressed(currentSelectedTab == Integer.parseInt(ll_bottom_tab_menu_item_three.getTag().toString()));

        ll_bottom_tab_menu_item_four.setPressed(currentSelectedTab == Integer.parseInt(ll_bottom_tab_menu_item_four.getTag().toString()));

        sortMenuItems();

    }


    private void sortMenuItems() {

        switch (currentSelectedTab) {

            case Constants.BOTTOM_TAB_SELECTED_ROUTES:
                ll_bottom_tab_menu_item.setTag(Constants.ROUTES_TAG);
                break;
            case Constants.BOTTOM_TAB_SELECTED_PLAN_TRIP:
                ll_bottom_tab_menu_item.setTag(Constants.PLAN_TRIP_TAG);
                break;
            case Constants.BOTTOM_TAB_SELECTED_MY_TRIPS:
                ll_bottom_tab_menu_item.setTag(Constants.MY_TRIPS_TAG);
                break;
            case Constants.BOTTOM_TAB_SELECTED_AVATAR:
                ll_bottom_tab_menu_item.setTag(Constants.USER_NAME_TAG);
                break;
            case Constants.BOTTOM_TAB_SELECTED_SIGN_IN:
                ll_bottom_tab_menu_item.setTag(Constants.SIGN_IN_TAG);
                break;

        }
        assignMenuItemByTag(ll_bottom_tab_menu_item);

    }


    private void assignMenuItemByTag(LinearLayout ll_menu_item) {

        switch (Integer.parseInt(ll_menu_item.getTag().toString())) {

            case Constants.MENU_TAG:
                RelativeLayout rlUserNameMenu = (RelativeLayout) ll_menu_item.getChildAt(0);
                rlUserNameMenu.setVisibility(View.GONE);
                ImageView imageViewMenuItem = (ImageView) ll_menu_item.getChildAt(1);
                imageViewMenuItem.setVisibility(View.VISIBLE);
                TextView textViewMenuitem = (TextView) ll_menu_item.getChildAt(2);
                imageViewMenuItem.setImageResource(R.drawable.ic_tab_menu);
                textViewMenuitem.setText(R.string.menu);
                break;

            case Constants.PLAN_TRIP_TAG:
                RelativeLayout rlUserNamePlan = (RelativeLayout) ll_menu_item.getChildAt(0);
                rlUserNamePlan.setVisibility(View.GONE);
                ImageView imageViewPlanTrip = (ImageView) ll_menu_item.getChildAt(1);
                imageViewPlanTrip.setVisibility(View.VISIBLE);
                TextView textViewPlanTrip = (TextView) ll_menu_item.getChildAt(2);
                imageViewPlanTrip.setImageResource(R.drawable.ic_plantrip);
                textViewPlanTrip.setText(R.string.plan_trip);
                break;

            case Constants.ROUTES_TAG:
                RelativeLayout rlUserNameRoutes = (RelativeLayout) ll_menu_item.getChildAt(0);
                rlUserNameRoutes.setVisibility(View.GONE);
                ImageView imageViewRoutes = (ImageView) ll_menu_item.getChildAt(1);
                imageViewRoutes.setVisibility(View.VISIBLE);
                TextView textViewRoutes = (TextView) ll_menu_item.getChildAt(2);
                imageViewRoutes.setImageResource(R.drawable.ic_routes);
                textViewRoutes.setText(R.string.routes);
                break;

            case Constants.MY_TRIPS_TAG:
                RelativeLayout rlUserNameMyTrips = (RelativeLayout) ll_menu_item.getChildAt(0);
                rlUserNameMyTrips.setVisibility(View.GONE);
                ImageView imageViewMyTrip = (ImageView) ll_menu_item.getChildAt(1);
                imageViewMyTrip.setVisibility(View.VISIBLE);
                TextView textViewMyTrip = (TextView) ll_menu_item.getChildAt(2);
                imageViewMyTrip.setImageResource(R.drawable.ic_save);
                textViewMyTrip.setText(R.string.my_trips);
                break;

            case Constants.SIGN_IN_TAG:
                RelativeLayout rlUserNameSignIn = (RelativeLayout) ll_menu_item.getChildAt(0);
                rlUserNameSignIn.setVisibility(View.GONE);
                ImageView imageViewSignIn = (ImageView) ll_menu_item.getChildAt(1);
                imageViewSignIn.setVisibility(View.VISIBLE);
                TextView textViewSingIn = (TextView) ll_menu_item.getChildAt(2);
                imageViewSignIn.setImageResource(R.drawable.ic_signin);
                textViewSingIn.setText(R.string.sign_in);
                break;

            case Constants.USER_NAME_TAG:

                RelativeLayout rlUserName = (RelativeLayout) ll_menu_item.getChildAt(0);
                TextView tvUserName = (TextView) rlUserName.getChildAt(0);

                ImageView imageViewUserName = (ImageView) ll_menu_item.getChildAt(1);
                TextView tvFullName = (TextView) ll_menu_item.getChildAt(2);
                boolean loggedIn = Utilities.isLoggedIn();
                Gson gson = new Gson();
                try {
                    if (loggedIn) {
                        String nickName = "";
                        String profileString = Configurations.getString("account_user_profile", "");
                        if (profileString != null && !profileString.isEmpty()) {
                            UserProfile userProfile = gson.fromJson(profileString, UserProfile.class);

                            if (userProfile.getNickname() != null) {
                                nickName = userProfile.getNickname();
                                if (nickName.length() >= 2) {
                                    nickName = nickName.substring(0, 2).toUpperCase();
                                }
                                tvUserName.setText(nickName);
                                tvFullName.setText(userProfile.getName());


                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                rlUserName.setVisibility(View.VISIBLE);
                imageViewUserName.setVisibility(View.GONE);

                break;


        }

    }


    @Override
    protected void onResume() {
        super.onResume();
        refreshBottomTabs();
        if (Utilities.checkIfToDisplayAccessibilityUI(getApplicationContext())) {

            llBottomTab.setVisibility(View.GONE);
            ll_bottom_tab_accessibility.setVisibility(View.VISIBLE);
            clickAccessbilityBottomTabAction();
            refreshAccountStatusInTab();


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

    @SuppressLint("GestureBackNavigation")
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            if (rlStopViewerDate.getVisibility() == View.VISIBLE) {
                rlStopViewerDate.setVisibility(View.GONE);
                enableAccessbilityForCalendar();
                return true;
            } else {
                MyApplication.getInstance().mapTargetOnStopViewerPage = gMapboxMap.getCameraPosition().target;
                MyApplication.getInstance().mapZoomOnStopViewerPage = gMapboxMap.getCameraPosition().zoom;
            }


        }

        return super.onKeyDown(keyCode, event);
    }

    @SuppressLint("GestureBackNavigation")
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        savePreferences(false);
    }
}
