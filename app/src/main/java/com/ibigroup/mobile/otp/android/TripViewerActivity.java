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
import static com.ibigroup.mobile.otp.android.utility.ServerDelegate.volleyStringRequest;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.NetworkImageView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.maps.android.SphericalUtil;
import com.ibigroup.mobile.otp.android.api.Resource;
import com.ibigroup.mobile.otp.android.json.TripViewerRouteInfo;
import com.ibigroup.mobile.otp.android.json.TripViewerStop;
import com.ibigroup.mobile.otp.android.json.TripViewerStopTime;
import com.ibigroup.mobile.otp.android.json.itinerary.IntermediateStop;
import com.ibigroup.mobile.otp.android.json.itinerary.Itinerary;
import com.ibigroup.mobile.otp.android.json.itinerary.ItineraryLeg;
import com.ibigroup.mobile.otp.android.json.itinerary.ItineraryLegGeometry;
import com.ibigroup.mobile.otp.android.json.itinerary.ItineraryLegPoint;
import com.ibigroup.mobile.otp.android.utility.Configurations;
import com.ibigroup.mobile.otp.android.utility.Constants;
import com.ibigroup.mobile.otp.android.utility.Convert;
import com.ibigroup.mobile.otp.android.utility.Logger;
import com.ibigroup.mobile.otp.android.utility.Utilities;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.LineString;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.geometry.LatLngBounds;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.plugins.markerview.MarkerView;
import com.mapbox.mapboxsdk.plugins.markerview.MarkerViewManager;
import com.mapbox.mapboxsdk.style.layers.LineLayer;
import com.mapbox.mapboxsdk.style.layers.Property;
import com.mapbox.mapboxsdk.style.layers.PropertyFactory;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.mapbox.mapboxsdk.style.sources.Source;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;


public class TripViewerActivity extends BaseActivity {
    private com.mapbox.mapboxsdk.maps.MapView mapView;
    private MapboxMap gMapboxMap;
    private Style gMapStyle;
    private String mapCurrentStyle;
    private RelativeLayout rlMapView;
    private MarkerViewManager markerViewManager = null;
    private Itinerary currentSelectedItinerary = null;
    private String currentSelectedTripId = null;

    private ItineraryLegPoint stopViewerItineraryLegPoint = null;
    private ItineraryLeg selectedItineraryLeg = null;
    private final ArrayList<MarkerView> routeMarkerViews = new ArrayList<>();
    private final ArrayList<String> sourceIDsForRoutePolyline = new ArrayList<String>();
    private final ArrayList<String> layerIDsForRoutePolyline = new ArrayList<String>();

    private NetworkImageView ivAgencyLogo;
    private Marker originMarker = null, destinationMarker = null;

    private TextView tvTripViewerRoute, tvTripViewerRouteName;
    private LinearLayout llTripViewer, llTripViewerBikesAllowed, llTripViewerTripBikesAllowed,
            llTripViewerAccessibleAllowed;
    private ListView lvTripViewerStops;
    private TripViewerStopsListAdapter tripViewerStopsListAdapter;
    private ImageView ivTripViewerExpander;
    private final ArrayList<ItineraryLegPoint> tripViewerStopsInfoArray = new ArrayList<>();
    private ArrayList<TripViewerStop> tripViewerStops = new ArrayList<>();
    private ArrayList<TripViewerStopTime> tripViewerStopTimes = new ArrayList<>();
    private boolean isTripViewerStopTimesRequested = false;
    private boolean isTripViewerStopsRequested = false;

    private Source tripViewerRouteSource = null;
    private LineLayer tripViewerRouteLayer = null;
    private String tripViewerRouteInfoPoints = null;

    private int currentHeight;
    private int currentY;

    private final int expandedStatus = Constants.EXPANDED_STATUS_HALF_EXPANDED;
    private DisplayMetrics gDm = new DisplayMetrics();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_viewer);

        if (getIntent() != null) {
            Intent intent = getIntent();
            currentSelectedItinerary = (Itinerary) intent.getSerializableExtra(Constants.CURRENT_ITINERARY);
            selectedItineraryLeg = (ItineraryLeg) intent.getSerializableExtra(Constants.SELECTED_ITINERARY_LEG);
        }

        if (selectedItineraryLeg != null) {
            if (selectedItineraryLeg.getTripId() != null) {
                currentSelectedTripId = selectedItineraryLeg.getTripId();
            } else if (selectedItineraryLeg.getTrip().getGtfsId() != null) {
                currentSelectedTripId = selectedItineraryLeg.getTrip().getGtfsId();
            }
        }

        ImageView ivTripViewerBack = findViewById(R.id.iv_trip_viewer_back);
        ivTripViewerBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        setupMapView(savedInstanceState);
        setupTripViewer();

        getTripViewerStops();
        getTripViewerStopTimes();
        getTripViewerRouteInfo();

        refreshTripViewer();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

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

    private void enterStopViewerPage(String mode) {
        if (stopViewerItineraryLegPoint != null) {
            Intent intent = new Intent(this, StopViewerActivity.class);
            intent.putExtra(Constants.STOP_VIEWER_ITINERARY_LEG_POINT, stopViewerItineraryLegPoint);
            if (mode != null) {
                intent.putExtra(Constants.STOP_VIEWER_ITINERARY_MODE, mode);
            }
            startActivityForResult(intent, Constants.STOP_VIEWER_REQUEST_CODE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == Constants.STOP_VIEWER_REQUEST_CODE) {
                if (data != null) {
                    boolean isOrigin = data.getBooleanExtra(Constants.IS_ORIGIN, true);
                    String searchedAddress = data.getStringExtra(Constants.SEARCHED_ADDRESS);
                    double lat = data.getDoubleExtra(Constants.SEARCHED_LATITUDE, 0.0);
                    double lon = data.getDoubleExtra(Constants.SEARCHED_LONGITUDE, 0.0);

                    Intent intent = new Intent();
                    intent.putExtra(Constants.IS_ORIGIN, isOrigin);
                    intent.putExtra(Constants.SEARCHED_ADDRESS, searchedAddress);
                    intent.putExtra(Constants.SEARCHED_LATITUDE, lat);
                    intent.putExtra(Constants.SEARCHED_LONGITUDE, lon);
                    setResult(RESULT_OK, intent);
                    finish();
                }
            }
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

    private void showOriginMarkersOnMap(ItineraryLegPoint itineraryLegPoint) {
        if (itineraryLegPoint != null) {
            if (originMarker == null) {
                MarkerOptions originMarkerOptions = new MarkerOptions();
                originMarkerOptions.setPosition(new LatLng(itineraryLegPoint.getLat(), itineraryLegPoint.getLon()));
                originMarkerOptions.icon(IconFactory.getInstance(this).fromResource(R.drawable.ic_start_trip));
                originMarker = gMapboxMap.addMarker(originMarkerOptions);
            } else {
                originMarker.setPosition(new LatLng(itineraryLegPoint.getLat(), itineraryLegPoint.getLon()));
            }
        }
    }

    private void showDestinationMarkersOnMap(ItineraryLegPoint itineraryLegPoint) {
        if (itineraryLegPoint != null) {
            if (destinationMarker == null) {
                MarkerOptions destinationMarkerOptions = new MarkerOptions();
                destinationMarkerOptions.setPosition(new LatLng(itineraryLegPoint.getLat(), itineraryLegPoint.getLon()));
                destinationMarkerOptions.icon(IconFactory.getInstance(this).fromResource(R.drawable.ic_end));
                destinationMarker = gMapboxMap.addMarker(destinationMarkerOptions);
            } else {
                destinationMarker.setPosition(new LatLng(itineraryLegPoint.getLat(), itineraryLegPoint.getLon()));
            }
        }
    }

    private void drawRoutePolylineOnMap() {
        removeRoutePolylineFromMap();
        removeRouteMarkerViewsFromMap();
        ArrayList<ItineraryLeg> itineraryLegs = currentSelectedItinerary.getLegs();
        Float normalWidth = 5.0f;
        Float highlightWidth = 10.0f;
        Float width = normalWidth;

        for (int i = 0; i < itineraryLegs.size(); i++) {
            ItineraryLeg itineraryLeg = itineraryLegs.get(i);

            if (itineraryLeg.getLegGeometry() != null && itineraryLeg.getLegGeometry().getPoints() != null) {
                String routeColorString = itineraryLeg.getRouteColor();
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

                gMapStyle.addSource(new GeoJsonSource(sourceID,
                        FeatureCollection.fromFeatures(new Feature[]{Feature.fromGeometry(
                                LineString.fromLngLats(routeCoordinates)
                        )})));


                if (mode.equals("walk") || mode.equals("bike") || mode.equals("bicycle") || mode.equals("car") || mode.equals("micromobility")) {
                    if (mode.equals("car")) {
                        gMapStyle.addLayer(new LineLayer(layerID, sourceID).withProperties(
                                PropertyFactory.lineDasharray(new Float[]{2.0f, 0.5f}),
                                PropertyFactory.lineCap(Property.LINE_CAP_SQUARE),
                                PropertyFactory.lineJoin(Property.LINE_JOIN_BEVEL),
                                PropertyFactory.lineWidth(width),
                                PropertyFactory.lineColor(routeColor)
                        ));
                    } else {
                        gMapStyle.addLayer(new LineLayer(layerID, sourceID).withProperties(
                                PropertyFactory.lineDasharray(new Float[]{0.01f, 2.0f}),
                                PropertyFactory.lineCap(Property.LINE_CAP_ROUND),
                                PropertyFactory.lineJoin(Property.LINE_JOIN_ROUND),
                                PropertyFactory.lineWidth(width),
                                PropertyFactory.lineColor(routeColor)
                        ));
                    }

                } else {
                    gMapStyle.addLayer(new LineLayer(layerID, sourceID).withProperties(
                            PropertyFactory.lineCap(Property.LINE_CAP_ROUND),
                            PropertyFactory.lineJoin(Property.LINE_JOIN_ROUND),
                            PropertyFactory.lineWidth(width),
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
                                itineraryLeg.getRouteColor(), itineraryLeg.getRouteTextColor());

                        routeMarkerViews.add(markerViewRoute);

                    }
                }
            }
        }
    }

    private void moveMapToShowRouteAndTrip(List<Point> routeCoordinates) {
        if (gMapboxMap != null) {
            LatLngBounds.Builder latLngBoundsBuilder = new LatLngBounds.Builder();

            if (currentSelectedItinerary != null
                    && currentSelectedItinerary.getLegs() != null && currentSelectedItinerary.getLegs().size() > 0) {
                ItineraryLegPoint from = currentSelectedItinerary.getLegs().get(0).getFrom();
                ItineraryLegPoint to = currentSelectedItinerary.getLegs().get(currentSelectedItinerary.getLegs().size() - 1).getTo();
                if (from != null) {
                    showOriginMarkersOnMap(from);
                    latLngBoundsBuilder.include(new LatLng(from.getLat(), from.getLon()));
                }

                if (to != null) {
                    showDestinationMarkersOnMap(to);
                    latLngBoundsBuilder.include(new LatLng(to.getLat(), to.getLon()));
                }
            }

            if (routeCoordinates != null && routeCoordinates.size() > 0) {
                for (Point point : routeCoordinates) {
                    latLngBoundsBuilder.include(new LatLng(point.latitude(), point.longitude()));
                }
            }

            int paddingBottom;
            if (gDm.heightPixels >= Convert.dpToPx(700)) {
                paddingBottom = Convert.dpToPx(400);
            } else {
                paddingBottom = (gDm.heightPixels - Convert.dpToPx(30)) / 2;
            }
            gMapboxMap.moveCamera(CameraUpdateFactory.newLatLngBounds(latLngBoundsBuilder.build(),
                    0, Convert.dpToPx(16), 0, paddingBottom), null);
        }
    }

    private void moveMapToShowRoute(List<Point> routeCoordinates) {
        if (!moveMapToShowTrip()) {
            if (gMapboxMap != null && routeCoordinates != null && routeCoordinates.size() > 0) {
                LatLngBounds.Builder latLngBoundsBuilder = new LatLngBounds.Builder();

                for (Point point : routeCoordinates) {
                    latLngBoundsBuilder.include(new LatLng(point.latitude(), point.longitude()));
                }

                int paddingBottom;
                if (gDm.heightPixels >= Convert.dpToPx(700)) {
                    paddingBottom = Convert.dpToPx(400);
                } else {
                    paddingBottom = (gDm.heightPixels - Convert.dpToPx(30)) / 2;
                }
                gMapboxMap.moveCamera(CameraUpdateFactory.newLatLngBounds(latLngBoundsBuilder.build(),
                        0, Convert.dpToPx(16), 0, paddingBottom), null);
            }
        }
    }

    private boolean moveMapToShowTrip() {
        if (gMapboxMap != null && currentSelectedItinerary != null
                && currentSelectedItinerary.getLegs() != null && currentSelectedItinerary.getLegs().size() > 0) {
            LatLngBounds.Builder latLngBoundsBuilder = new LatLngBounds.Builder();

            ItineraryLegPoint from = currentSelectedItinerary.getLegs().get(0).getFrom();
            ItineraryLegPoint to = currentSelectedItinerary.getLegs().get(currentSelectedItinerary.getLegs().size() - 1).getTo();
            if (from != null) {
                showOriginMarkersOnMap(from);
            }

            if (to != null) {
                showDestinationMarkersOnMap(to);
            }

            if (from != null && to != null) {
                latLngBoundsBuilder.include(new LatLng(from.getLat(), from.getLon()));
                latLngBoundsBuilder.include(new LatLng(to.getLat(), to.getLon()));

                int paddingBottom;
                if (gDm.heightPixels >= Convert.dpToPx(700)) {
                    paddingBottom = Convert.dpToPx(416);
                } else {
                    paddingBottom = gDm.heightPixels / 2;
                }
                gMapboxMap.moveCamera(CameraUpdateFactory.newLatLngBounds(latLngBoundsBuilder.build(),
                        0, Convert.dpToPx(26), 0, paddingBottom), null);

                return true;
            }
        }
        return false;
    }

    private void refreshAnnotationsIfNeeded() {
        //Add route polyline
        if (currentSelectedItinerary != null) {
            drawRoutePolylineOnMap();
        }

        //Add trip polyline
        if (tripViewerRouteInfoPoints != null) {
            showTripViewerRoutePolylineOnMap(tripViewerRouteInfoPoints);
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
                    getTripViewerGeometry();
                    refreshAnnotationsIfNeeded();
                }
            });
        }
    }

    private void setupMapView(Bundle savedInstanceState) {
        rlMapView = findViewById(R.id.rl_map_view);
        mapView = findViewById(R.id.map_view);
        mapView.onCreate(savedInstanceState);

        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(MapboxMap mapboxMap) {
                gMapboxMap = mapboxMap;

                int marginHeight;
                if (gDm.heightPixels >= Convert.dpToPx(700)) {
                    marginHeight = Convert.dpToPx(380);
                } else {
                    marginHeight = (gDm.heightPixels - Convert.dpToPx(88)) / 2;
                }

                //move the compass icon to the bottom left side
                gMapboxMap.getUiSettings().setCompassMargins(0, gDm.heightPixels - marginHeight - Convert.dpToPx(126),
                        gDm.widthPixels - Convert.dpToPx(60), 0);

                markerViewManager = new MarkerViewManager(mapView, mapboxMap);
                refreshMapStyle();
            }
        });
    }

    private TripViewerStopsListAdapter getTripViewerStopsListAdapter() {
        ArrayList<HashMap<String, Object>> data = new ArrayList<HashMap<String, Object>>();

        for (int i = 0; i < tripViewerStopsInfoArray.size(); i++) {
            ItineraryLegPoint itineraryLegPoint = tripViewerStopsInfoArray.get(i);

            HashMap<String, Object> map = new HashMap<String, Object>();

            map.put("tv_stop_name", itineraryLegPoint.getName());
            data.add(map);
        }

        TripViewerStopsListAdapter mTripViewerStopsListAdapter = new TripViewerStopsListAdapter(
                this, data,
                R.layout.item_trip_viewer_stop_new,
                new String[]{"tv_stop_name"}, new int[]{R.id.tv_stop_name});

        return mTripViewerStopsListAdapter;
    }

    private boolean isInCurrentItineraryLeg(ItineraryLegPoint tripViewerStop) {
        if (selectedItineraryLeg != null && selectedItineraryLeg.getIntermediateStops() != null
                && tripViewerStop != null && tripViewerStop.getStopId() != null) {
            for (IntermediateStop intermediateStop : selectedItineraryLeg.getIntermediateStops()) {
                if (intermediateStop != null && tripViewerStop.getStopId().equals(intermediateStop.getStopId())) {
                    return true;
                }
            }
        }

        return false;
    }

    class TripViewerStopsListAdapter extends SimpleAdapter {
        Context mContext;
        private final LayoutInflater l_Inflater;

        public TripViewerStopsListAdapter(Context context, List<? extends Map<String, ?>> data,
                                          int resource, String[] from, int[] to) {
            super(context, data, resource, from, to);

            mContext = context;
            l_Inflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return tripViewerStopsInfoArray.size();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ItineraryLegPoint tripViewerStop = tripViewerStopsInfoArray.get(position);

            if (convertView == null) {

                if (Utilities.checkIfToDisplayAccessibilityUI(mContext)) {
                    convertView = l_Inflater.inflate(
                            R.layout.item_trip_viewer_stop_new_accessibility, null);
                } else {
                    convertView = l_Inflater.inflate(
                            R.layout.item_trip_viewer_stop_new, null);
                }

            }

            if (tripViewerStop != null) {
                TextView tvArrivalTime = convertView.findViewById(R.id.tv_arrival_time);
                TextView tvStopName = convertView.findViewById(R.id.tv_stop_name);
                TextView tvStopView = convertView.findViewById(R.id.tv_stop_view);
                ImageView ivStopPosition = convertView.findViewById(R.id.iv_stop_position);
                ImageView ivBottomLine = convertView.findViewById(R.id.iv_bottom_line);

                String routeColor = Constants.DEFAULT_TRIP_VIEWER_HIGHLIGHT_COLOR;

                if (position == 0) {
                    ivStopPosition.setImageResource(R.drawable.ic_start);

                    tvStopName.setTypeface(null, Typeface.BOLD);
                } else if (position == tripViewerStopsInfoArray.size() - 1) {
                    ivStopPosition.setImageResource(R.drawable.ic_location);
                    tvStopName.setTypeface(null, Typeface.BOLD);
                } else {
                    ivStopPosition.setImageDrawable(null);
                    tvStopName.setTypeface(null, Typeface.NORMAL);
                }

                if (isInCurrentItineraryLeg(tripViewerStop)) {
                    ivStopPosition.setBackgroundResource(R.drawable.ic_circle);
                } else {
                    ivStopPosition.setBackgroundResource(R.drawable.ring_bg);
                }
                ivStopPosition.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(routeColor)));
                ivBottomLine.setBackgroundColor(Color.parseColor(routeColor));

                if (position == tripViewerStopsInfoArray.size() - 1) {
                    ivBottomLine.setVisibility(View.GONE);
                } else {
                    ivBottomLine.setVisibility(View.VISIBLE);
                }

                tvStopName.setText(tripViewerStop.getName());

                tvArrivalTime.setText(Utilities.formatTimestamp(TripViewerActivity.this, 1000 * tripViewerStop.getArrival()));

                if (position == (tripViewerStops.size() - 1)) {
                    ivBottomLine.setVisibility(View.INVISIBLE);
                } else {
                    ivBottomLine.setVisibility(View.VISIBLE);
                }

                tvStopView.setTag(position);
                tvStopView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int position = (Integer) v.getTag();

                        if (position < tripViewerStopsInfoArray.size()) {
                            stopViewerItineraryLegPoint = tripViewerStopsInfoArray.get(position);

                            String mode = null;
                            if (selectedItineraryLeg != null) {
                                mode = selectedItineraryLeg.getMode();
                            }
                            enterStopViewerPage(mode);
                        }
                    }
                });
            }
            return convertView;
        }
    }

    private void checkAndLoadTripViewerData() {
        if (isTripViewerStopsRequested && isTripViewerStopTimesRequested) {
            if (tripViewerStopTimes != null && tripViewerStops != null) {
                tripViewerStopsInfoArray.clear();
                long startOfToday = Utilities.startOfDay(System.currentTimeMillis());

                for (TripViewerStopTime tripViewerStopTime : tripViewerStopTimes) {
                    ItineraryLegPoint itineraryLegPoint = new ItineraryLegPoint();
                    String stopID = tripViewerStopTime.getStopId();
                    long serviceDay = tripViewerStopTime.getServiceDay();
                    itineraryLegPoint.setStopId(stopID);

                    if (serviceDay <= 0) {
                        serviceDay = startOfToday;
                    }

                    if (tripViewerStopTime.isRealtime()) {
                        itineraryLegPoint.setArrival(serviceDay +
                                tripViewerStopTime.getRealtimeArrival());
                    } else {
                        itineraryLegPoint.setArrival(serviceDay +
                                tripViewerStopTime.getScheduledArrival());
                    }

                    if (stopID != null) {
                        for (TripViewerStop tripViewerStop : tripViewerStops) {
                            if (stopID.equals(tripViewerStop.getId())) {
                                itineraryLegPoint.setName(tripViewerStop.getName());
                                itineraryLegPoint.setLat(tripViewerStop.getLat());
                                itineraryLegPoint.setLon(tripViewerStop.getLon());
                                break;
                            }
                        }
                    }
                    tripViewerStopsInfoArray.add(itineraryLegPoint);
                }

                if (tripViewerStopsListAdapter != null) {
                    tripViewerStopsListAdapter.notifyDataSetChanged();
                }
            }
        }
    }

    private void getTripViewerStops() {
        isTripViewerStopsRequested = false;
        Response.Listener<String> getTripViewerStopsListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                if (response != null && !response.isEmpty() && response.startsWith("[")) {
                    Gson gson = new Gson();

                    tripViewerStops = gson.fromJson(response, new TypeToken<ArrayList<TripViewerStop>>() {
                    }.getType());

                } else {
                    Log.d("data", "trip viewer stops data is not correct:" + response);
                }
                isTripViewerStopsRequested = true;
                checkAndLoadTripViewerData();
            }
        };
        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                isTripViewerStopsRequested = true;
                Logger.e("get trip viewer stops feed error:", error.toString());
            }
        };

        try {
            getTripViewerStopsResponse(getTripViewerStopsListener, errorListener);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void getTripViewerStopsResponse(Response.Listener<String> listener,
                                           Response.ErrorListener errListener) throws Exception {
        if (currentSelectedTripId != null) {
            String url = String.format(Locale.getDefault(), Resource.API_TRIP_VIEWER_STOPS,
                    currentSelectedTripId);

            volleyStringRequest(Request.Method.GET, url, "", listener, errListener);
        }
    }

    private void getTripViewerStopTimes() {
        isTripViewerStopTimesRequested = false;
        Response.Listener<String> getTripViewerStopTimesListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                isTripViewerStopTimesRequested = true;
                if (response != null && !response.isEmpty() && response.startsWith("[")) {
                    Gson gson = new Gson();

                    tripViewerStopTimes = gson.fromJson(response, new TypeToken<ArrayList<TripViewerStopTime>>() {
                    }.getType());

                    Collections.sort(tripViewerStopTimes, new Comparator<TripViewerStopTime>() {
                        @Override
                        public int compare(TripViewerStopTime o1, TripViewerStopTime o2) {
                            if (o1 == null || o2 == null) {
                                return 0;
                            } else {
                                return o1.getStopIndex() - o2.getStopIndex();
                            }
                        }
                    });

                } else {
                    Log.d("data", "trip viewer stops data is not correct:" + response);
                }

                checkAndLoadTripViewerData();
            }
        };
        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                isTripViewerStopTimesRequested = true;
                Logger.e("get trip viewer stop times feed error:", error.toString());
            }
        };

        try {
            getTripViewerStopTimesResponse(getTripViewerStopTimesListener, errorListener);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getTripViewerStopTimesResponse(Response.Listener<String> listener,
                                               Response.ErrorListener errListener) throws Exception {
        if (currentSelectedTripId != null) {
            String url = String.format(Locale.getDefault(), Resource.API_TRIP_VIEWER_STOP_TIMES,
                    currentSelectedTripId);

            volleyStringRequest(Request.Method.GET, url, "", listener, errListener);
        }
    }

    private void getTripViewerRouteInfo() {
        Response.Listener<String> getTripViewerRouteInfoListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response != null && !response.isEmpty()) {
                    Gson gson = new Gson();

                    TripViewerRouteInfo tripViewerRouteInfo = gson.fromJson(response, TripViewerRouteInfo.class);

                    if (tripViewerRouteInfo != null) {
                        if (tripViewerRouteInfo.getBikesAllowed() == 1) {
                            llTripViewerBikesAllowed.setVisibility(View.VISIBLE);
                        } else {
                            llTripViewerBikesAllowed.setVisibility(View.GONE);
                        }

                        if (tripViewerRouteInfo.getTripBikesAllowed() == 1) {
                            llTripViewerTripBikesAllowed.setVisibility(View.VISIBLE);
                        } else {
                            llTripViewerTripBikesAllowed.setVisibility(View.GONE);
                        }

                        if (tripViewerRouteInfo.getWheelchairAccessible() == 1) {
                            llTripViewerAccessibleAllowed.setVisibility(View.VISIBLE);
                        } else {
                            llTripViewerAccessibleAllowed.setVisibility(View.GONE);
                        }
                    }
                } else {
                    Log.d("data", "trip viewer info is not correct:" + response);
                }

                checkAndLoadTripViewerData();
            }
        };
        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Logger.e("get trip viewer info error:", error.toString());
            }
        };

        try {
            getTripViewerRouteInfoResponse(getTripViewerRouteInfoListener, errorListener);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getTripViewerRouteInfoResponse(Response.Listener<String> listener,
                                               Response.ErrorListener errListener) throws Exception {
        if (currentSelectedTripId != null) {
            String url = String.format(Locale.getDefault(), Resource.API_TRIP_VIEWER_INFO,
                    currentSelectedTripId);

            volleyStringRequest(Request.Method.GET, url, "", listener, errListener);
        }
    }

    private void removeTripViewerRoutePolylineFromMapIfNeeded() {
        if (tripViewerRouteLayer != null) {
            gMapStyle.removeLayer("trip_viewer_route_layer");
            tripViewerRouteLayer = null;
        }

        if (tripViewerRouteSource != null) {
            gMapStyle.removeSource("trip_viewer_route_source");
            tripViewerRouteSource = null;
        }

        tripViewerRouteInfoPoints = null;
    }

    private void showTripViewerRoutePolylineOnMap(String points) {
        if (points != null && selectedItineraryLeg != null) {
            ItineraryLeg itineraryLeg = selectedItineraryLeg;

            String routeColorString = itineraryLeg.getRouteColor();
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
//rgba for mapbox color >> 8
            routeColor = 0xAF58D0FF;

            List<Point> routeCoordinates = Utilities.decodeGPSPointsForRoute(points);

            removeTripViewerRoutePolylineFromMapIfNeeded();
            String sourceID = "trip_viewer_route_source";
            String layerID = "trip_viewer_route_layer";
            tripViewerRouteSource = new GeoJsonSource(sourceID,
                    FeatureCollection.fromFeatures(new Feature[]{Feature.fromGeometry(
                            LineString.fromLngLats(routeCoordinates)
                    )}));
            tripViewerRouteLayer = new LineLayer(layerID, sourceID).withProperties(
                    PropertyFactory.lineCap(Property.LINE_CAP_ROUND),
                    PropertyFactory.lineJoin(Property.LINE_JOIN_ROUND),
                    PropertyFactory.lineWidth(8.0f),
                    PropertyFactory.lineColor(routeColor)
            );
            tripViewerRouteInfoPoints = points;
            if (gMapStyle != null) {
                gMapStyle.addSource(tripViewerRouteSource);
                gMapStyle.addLayer(tripViewerRouteLayer);
            }

            moveMapToShowRouteAndTrip(routeCoordinates);
        }
    }

    private void getTripViewerGeometry() {
        Response.Listener<String> getTripViewerRouteInfoListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response != null && !response.isEmpty()) {
                    Gson gson = new Gson();

                    ItineraryLegGeometry tripViewerRouteInfo = gson.fromJson(response, ItineraryLegGeometry.class);
                    showTripViewerRoutePolylineOnMap(tripViewerRouteInfo.getPoints());
                } else {
                    Log.d("data", "trip viewer info is not correct:" + response);
                }

                checkAndLoadTripViewerData();
            }
        };
        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Logger.e("get trip viewer info error:", error.toString());
            }
        };

        try {
            getTripViewerGeometryResponse(getTripViewerRouteInfoListener, errorListener);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getTripViewerGeometryResponse(Response.Listener<String> listener,
                                              Response.ErrorListener errListener) throws Exception {
        if (currentSelectedTripId != null) {
            String url = String.format(Locale.getDefault(), Resource.API_TRIP_VIEWER_GEOMETRY,
                    currentSelectedTripId);

            volleyStringRequest(Request.Method.GET, url, "", listener, errListener);
        }
    }

    private void refreshTripViewer() {
        if (selectedItineraryLeg != null) {
            String route = null;
            if (selectedItineraryLeg.getRouteShortName() != null && !selectedItineraryLeg.getRouteShortName().isEmpty()) {
                route = selectedItineraryLeg.getRouteShortName();
            }

            if (selectedItineraryLeg.getRoute() != null && !selectedItineraryLeg.getRoute().isEmpty()) {
                if (route != null) {
                    if (!route.equals(selectedItineraryLeg.getRoute())) {
                        route += " " + selectedItineraryLeg.getRoute();
                    }
                } else {
                    route = selectedItineraryLeg.getRoute();
                }
            }
            if (route != null) {
                tvTripViewerRoute.setText(route);
            } else {
                tvTripViewerRoute.setText(selectedItineraryLeg.getFrom().getName());
            }

            if (selectedItineraryLeg.getRouteLongName() != null && !selectedItineraryLeg.getRouteLongName().isEmpty()) {
                tvTripViewerRouteName.setText(selectedItineraryLeg.getRouteLongName());
            } else {
                tvTripViewerRouteName.setText(selectedItineraryLeg.getFrom().getName());
            }

            tvTripViewerRoute.measure(
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
            if (tvTripViewerRoute.getMeasuredWidth() < ((gDm.widthPixels - Convert.dpToPx(62)) * 2 / 5)) {
                LinearLayout.LayoutParams layoutParamsTripViewerRoute = (LinearLayout.LayoutParams) tvTripViewerRoute.getLayoutParams();
                layoutParamsTripViewerRoute.width = WRAP_CONTENT;
                tvTripViewerRoute.setLayoutParams(layoutParamsTripViewerRoute);

                LinearLayout.LayoutParams layoutParamsTripViewerName = (LinearLayout.LayoutParams) tvTripViewerRouteName.getLayoutParams();
                layoutParamsTripViewerName.width = WRAP_CONTENT;
                tvTripViewerRouteName.setLayoutParams(layoutParamsTripViewerName);
            }
        }
        llTripViewerBikesAllowed.setVisibility(View.GONE);
        llTripViewerTripBikesAllowed.setVisibility(View.GONE);
        llTripViewerAccessibleAllowed.setVisibility(View.GONE);
    }

    private void setupTripViewer() {
        llTripViewer = findViewById(R.id.ll_trip_viewer);

        lvTripViewerStops = findViewById(R.id.lv_trip_viewer_stops);
        tvTripViewerRoute = findViewById(R.id.tv_trip_viewer_route);
        tvTripViewerRouteName = findViewById(R.id.tv_trip_viewer_route_name);

        llTripViewerBikesAllowed = findViewById(R.id.ll_trip_viewer_bikes_allowed);
        llTripViewerTripBikesAllowed = findViewById(R.id.ll_trip_viewer_trip_bikes_allowed);
        llTripViewerAccessibleAllowed = findViewById(R.id.ll_trip_viewer_accessible_allowed);

        tripViewerStopsListAdapter = getTripViewerStopsListAdapter();
        lvTripViewerStops.setAdapter(tripViewerStopsListAdapter);
        ivTripViewerExpander = findViewById(R.id.iv_trip_viewer_expander);

        ivAgencyLogo = findViewById(R.id.iv_agency_logo);

        if (selectedItineraryLeg != null && (selectedItineraryLeg.getAgencyName() != null
                || (selectedItineraryLeg.getAgency() != null && selectedItineraryLeg.getAgency().getName() != null))) {
            String agencyNameString = selectedItineraryLeg.getAgencyName();

            if (agencyNameString == null) {
                agencyNameString = selectedItineraryLeg.getAgency().getName();
            }
            String logoUrl = Resource.ROUTE_AGENCY_LOGO_BASE_URL +
                    agencyNameString.toLowerCase().replaceAll(" ", "_");
            ivAgencyLogo.setImageUrl(logoUrl, MyApplication.getInstance().getImageLoader());
            ivAgencyLogo.setVisibility(View.VISIBLE);

            getWindowManager().getDefaultDisplay().getMetrics(gDm);
            tvTripViewerRouteName.setMaxWidth(gDm.widthPixels - Convert.dpToPx(136));
        } else {
            ivAgencyLogo.setVisibility(View.GONE);
        }
        ivTripViewerExpander.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        currentY = (int) event.getRawY();

                        break;

                    case MotionEvent.ACTION_MOVE:
                        int dy = (int) event.getRawY() - currentY;

                        setTripViewerHeight(dy);
                        break;

                    case MotionEvent.ACTION_UP:
                        if (llTripViewer != null) {
                            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) llTripViewer.getLayoutParams();
                            currentHeight = layoutParams.height;
                        }

                        break;
                }
                return true;
            }
        });

        initTripViewerHeight();
    }

    private void initTripViewerHeight() {
        gDm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(gDm);
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) llTripViewer.getLayoutParams();
        layoutParams.width = gDm.widthPixels;

        if (Utilities.checkIfToDisplayAccessibilityUI(this)) {
            layoutParams.height = (int) ((gDm.heightPixels - Convert.dpToPx(95)) * 0.8);
        } else {
            layoutParams.height = (int) ((gDm.heightPixels - Convert.dpToPx(95)) * 0.6);
        }

        currentHeight = layoutParams.height;
        llTripViewer.setLayoutParams(layoutParams);
    }

    private void setTripViewerHeight(int dy) {
        if (llTripViewer != null) {
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) llTripViewer.getLayoutParams();
            layoutParams.width = gDm.widthPixels;

            int minHeight = Convert.dpToPx(150);
            int maxHeight = gDm.heightPixels - Convert.dpToPx(100);

            if (currentHeight - dy > maxHeight) {
                layoutParams.height = maxHeight;
            } else layoutParams.height = Math.max(currentHeight - dy, minHeight);

            llTripViewer.setLayoutParams(layoutParams);
        }
    }
}
