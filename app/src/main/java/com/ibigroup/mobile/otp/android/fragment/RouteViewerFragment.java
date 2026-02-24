package com.ibigroup.mobile.otp.android.fragment;

import static android.app.Activity.RESULT_OK;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static com.ibigroup.mobile.otp.android.utility.Configurations.GRAPHQL_SERVER_URL;
import static com.ibigroup.mobile.otp.android.utility.Configurations.LATITUDE_DEFAULT;
import static com.ibigroup.mobile.otp.android.utility.Configurations.LONGITUDE_DEFAULT;
import static com.ibigroup.mobile.otp.android.utility.Configurations.ZOOM_DEFAULT;
import static com.ibigroup.mobile.otp.android.utility.ServerDelegate.volleyStringRequest;
import static com.mapbox.mapboxsdk.style.expressions.Expression.get;
import static com.mapbox.mapboxsdk.style.expressions.Expression.has;
import static com.mapbox.mapboxsdk.style.expressions.Expression.literal;
import static com.mapbox.mapboxsdk.style.expressions.Expression.not;
import static com.mapbox.mapboxsdk.style.layers.Property.NONE;
import static com.mapbox.mapboxsdk.style.layers.Property.VISIBLE;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconAllowOverlap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconIgnorePlacement;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconImage;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.symbolZOrder;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.visibility;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.StringRequest;
import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.ApolloClient;
import com.apollographql.apollo.ApolloQueryCall;
import com.apollographql.apollo.exception.ApolloException;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import com.ibigroup.mobile.otp.android.GetRouteVehiclesQuery;
import com.ibigroup.mobile.otp.android.MyApplication;
import com.ibigroup.mobile.otp.android.R;
import com.ibigroup.mobile.otp.android.RouteInfoQuery;
import com.ibigroup.mobile.otp.android.RouteViewerFilterActivity;
import com.ibigroup.mobile.otp.android.StopViewerActivity;
import com.ibigroup.mobile.otp.android.TabActivity;
import com.ibigroup.mobile.otp.android.api.Resource;
import com.ibigroup.mobile.otp.android.customize.CustomizeMarkerView;
import com.ibigroup.mobile.otp.android.json.ParkRideLocation;
import com.ibigroup.mobile.otp.android.json.RentalVehicle;
import com.ibigroup.mobile.otp.android.json.RouteViewerPattern;
import com.ibigroup.mobile.otp.android.json.RouteViewerRoute;
import com.ibigroup.mobile.otp.android.json.RouteViewerRouteDetail;
import com.ibigroup.mobile.otp.android.json.TransitStop;
import com.ibigroup.mobile.otp.android.json.VehicleOnRoute;
import com.ibigroup.mobile.otp.android.json.itinerary.ItineraryLegGeometry;
import com.ibigroup.mobile.otp.android.json.itinerary.ItineraryLegPoint;
import com.ibigroup.mobile.otp.android.manager.ParkRideManager;
import com.ibigroup.mobile.otp.android.manager.TransitStopManager;
import com.ibigroup.mobile.otp.android.utility.Configurations;
import com.ibigroup.mobile.otp.android.utility.Constants;
import com.ibigroup.mobile.otp.android.utility.Convert;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RouteViewerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RouteViewerFragment extends Fragment {
    private DisplayMetrics gDm = new DisplayMetrics();
    private View mFragmentView;
    private com.mapbox.mapboxsdk.maps.MapView mapView;
    private MapboxMap gMapboxMap;
    private Style gMapStyle;
    private String mapCurrentStyle;
    private TextView tvSearchARoute;
    private ArrayList<RouteViewerRoute> routeViewerAllRoutes = new ArrayList<>();
    private ImageView iv_filter_changed;
    private int selectedRouteFilterAgencyIndex = 0, selectedRouteFilterModeIndex = 0;
    private final ArrayList<String> routeFilterAgencies = new ArrayList<>();
    private final ArrayList<String> routeFilterModes = new ArrayList<>();
    private StringRequest gpsReverseRequest = null;
    private LinearLayout llRouteViewer;
    private ListView lvRouteViewerRoutes;
    private ImageView ivRoutesViewerExpander;
    private RouteViewerRouteListAdapter routeViewerRouteListAdapter;
    private int selectedRouteViewerRouteIndex = -1;
    private RouteViewerRoute currentRouteViewerRoute = null;
    private RouteViewerRouteDetail currentRouteViewerRouteDetail = null;
    private ArrayList<RouteViewerPattern> routeViewerPatterns = new ArrayList<>();

    private final ArrayList<RouteViewerRoute> routeViewerRoutes = new ArrayList<>();
    private ArrayList<VehicleOnRoute> vehiclesOnRoute = new ArrayList<>();

    private ArrayList<ArrayList<VehicleOnRoute>> vehiclesOnRouteLists = new ArrayList<>();
    private VehicleOnRoute selectedVehicleOnRoute = null;
    private GeoJsonSource vehiclesSource;
    private SymbolLayer unclusteredVehicles;
    private MarkerView markerView = null;
    private View customView;
    private MarkerViewManager markerViewManager = null;
    private LinearLayout llVehicleOnRoute, llStopParkingPopup;
    private TextView tvMarkerVehicleName, tvMarkerVehicleSpeed, tvMarkerVehicleTime;
    private TextView tvMapAddress, tvMarkerStopID;
    private ImageView ivMarkerViewClose;
    private NetworkImageView ivAgencyLogo;
    private LinearLayout llMarkerStopViewer, llMarkerBtnStopViewer;
    private LatLng markerViewLatlng = null;
    private ItineraryLegPoint stopViewerItineraryLegPoint = null;

    private boolean gbShowDirectionStops = true;
    private int gpsReverseMode = Constants.GPS_REVERSE_MODE_MARKER_VIEW;
    private final ArrayList<CustomizeMarkerView> vehicleOnRouteMarkerViews = new ArrayList<CustomizeMarkerView>();
    private ApolloQueryCall routeViewerRouteDetailQueryCall = null;
    private StringRequest routeViewerRouteGeometryStringRequest = null;
    private StringRequest getRouteViewerVehiclesRequest = null;
    private int currentHeight;
    private int currentY;
    private RelativeLayout rlCollapsed;
    private ImageView ivExpand, ivCollapsedBack, ivCollapse;
    private RelativeLayout rlSearchBar;
    private Runnable gRunnable = null;
    private final Handler gHandler = new Handler();
    private TransitStop selectedTransitStop;
    private ParkRideLocation selectedParkRideLocation;
    private int currentSelectedLocationMode = Constants.SELECTED_LOCATION_MODE_POINT;
    private Context systemContext;
    private LinearLayout llLayoutLayers, llStreets, llSatellite, llParkRideLocations, llTransitStops, llSatelliteAccessibility, llParkRideLocationsAccessibility, llTransitStopsAccessibility;
    private ImageView ivLayer, ivLayerBg;
    private TextView tvStreetsHighlight, tvSatelliteHighlight, tvSatelliteHighlightAccessibility;
    private ParkRideManager.ParkRideListener parkRideListener;
    private TransitStopManager.TransitStopListener transitStopListener;
    private GeoJsonSource parkRideSource, transitStopSource, rentalVehicleSource;
    private SymbolLayer unclusteredParkRide, unclusteredTransitStop, unclusteredRentalVehicle;
    private RentalVehicle selectedRentalVehicle;
    private LinearLayout llRentalVehicles, llRentalVehiclesAccessibility;
    private ArrayList<String> titleList = new ArrayList<String>();
    public ArrayList<TransitStop> routeStops = new ArrayList<TransitStop>();
    private TransitStop selectedRouteStop = null;
    private boolean gbNeedRefreshDirectionsAndStops = false;
    static final String ROUTE_DIRECTION_TITLE = "ROUTE_DIRECTION_TITLE";
    private final ArrayList<GridView> gGridViewList = new ArrayList<>();
    private final ArrayList<ViewPager2> vpRouteDirectionList = new ArrayList<>();
    private int currentRouteDirectionIndex = 0xFF;
    private final ArrayList<ListView> routeDirectionStopsListViews = new ArrayList<ListView>();
    private final ArrayList<RouteStopsListAdapter> routeDirectionStopsListAdapters = new ArrayList<RouteStopsListAdapter>();
    private StringRequest getRouteDirectionStopsRequest = null;
    private GeoJsonSource routeDirectionStopSource = null;
    private SymbolLayer unclusteredRouteDirectionStop;

    //If the resetAction() method is called then set this value back to false.
    private boolean resetRouteSelection = false;

    private final ArrayList<String> routeViewerRoutePoints = new ArrayList<>();
    private final ArrayList<Source> routeViewerRouteSources = new ArrayList<>();
    private final ArrayList<LineLayer> routeViewerRouteLayers = new ArrayList<>();

    public RouteViewerFragment() {
        // Required empty public constructor
    }

    public static RouteViewerFragment newInstance(String param1, String param2) {
        return new RouteViewerFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        systemContext = MyApplication.getInstance().getApplicationContext();
        gDm = new DisplayMetrics();

        if (getActivity() != null) {
            Convert.mDensity = getActivity().getResources().getDisplayMetrics().density;
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(gDm);
        }
    }

    private void initFilterAgencies() {
        routeFilterAgencies.clear();
        routeFilterAgencies.add(getString(R.string.all_agencies));
        if (routeViewerAllRoutes != null) {
            for (RouteViewerRoute routeViewerRoute : routeViewerAllRoutes) {
                if (routeViewerRoute != null && routeViewerRoute.getAgencyName() != null) {
                    if (!routeFilterAgencies.contains(routeViewerRoute.getAgencyName())) {
                        routeFilterAgencies.add(routeViewerRoute.getAgencyName());
                    }
                }
            }
        }

        Collections.sort(routeFilterAgencies);
    }

    private void refreshFilterModes() {
        if (routeViewerAllRoutes != null) {
            String selectedAgency = null;
            String selectedMode = null;
            if (selectedRouteFilterAgencyIndex < routeFilterAgencies.size()) {
                selectedAgency = routeFilterAgencies.get(selectedRouteFilterAgencyIndex);
            }

            routeFilterModes.clear();
            routeFilterModes.add(getString(R.string.all_modes));
            if (selectedAgency != null) {
                for (RouteViewerRoute routeViewerRoute : routeViewerAllRoutes) {
                    if (routeViewerRoute != null && (selectedRouteFilterAgencyIndex == 0 ||
                            selectedAgency.equals(routeViewerRoute.getAgencyName()))) {
                        if (!routeFilterModes.contains(routeViewerRoute.getMode())) {
                            routeFilterModes.add(routeViewerRoute.getMode());
                        }
                    }
                }
            }
            Collections.sort(routeFilterModes);

            if (selectedRouteFilterModeIndex < routeFilterModes.size()) {
                selectedMode = routeFilterModes.get(selectedRouteFilterModeIndex);
            }

            if (selectedMode == null) {
                selectedRouteFilterModeIndex = 0;
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Logger.d("VP2", "titleList.size(): " + titleList.size() + ",routeStops.size():" + routeStops.size());
        // Inflate the layout for this fragment
        if (mFragmentView != null && lvRouteViewerRoutes != null) {
            Logger.d("VP2", "mFragmentView != null");
            routeViewerRouteListAdapter = getRouteViewerRouteListAdapter();
            lvRouteViewerRoutes.setAdapter(routeViewerRouteListAdapter);

            return mFragmentView;
        }

        mFragmentView = inflater.inflate(R.layout.fragment_route_viewer, container, false);
        routeViewerAllRoutes = MyApplication.getInstance().routeViewerRoutes;
        if (routeViewerAllRoutes != null) {
            routeViewerRoutes.addAll(routeViewerAllRoutes);
        }
        setupMapView(savedInstanceState);
        setupSearchARoute();
        setupRouteViewer();
        setupExpandLayouts();
        setupLayers();
        setupAndAddFeatureListeners();
        iv_filter_changed = mFragmentView.findViewById(R.id.iv_filter_changed);

        initFilterAgencies();
        refreshFilterModes();
        return mFragmentView;
    }

    private void setupMapView(Bundle savedInstanceState) {
        if (mFragmentView == null) {
            return;
        }
        mapView = mFragmentView.findViewById(R.id.map_view);
        mapView.onCreate(savedInstanceState);

        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(MapboxMap mapboxMap) {
                gMapboxMap = mapboxMap;

                //move the compass icon to the bottom left side
                gMapboxMap.getUiSettings().setCompassMargins(0, gDm.heightPixels - Convert.dpToPx(276),
                        gDm.widthPixels - Convert.dpToPx(60), 0);

                markerViewManager = new MarkerViewManager(mapView, mapboxMap);
                refreshMapStyle();

                moveMapToDefaultLocation();

                gMapboxMap.addOnMapClickListener(new MapboxMap.OnMapClickListener() {
                    @Override
                    public boolean onMapClick(@NonNull LatLng point) {

                        if (customView != null && customView.getVisibility() == View.VISIBLE) {
                            customView.setVisibility(View.GONE);
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
                                            if (Math.abs((double) (feature.getNumberProperty(entry.getKey())) - Resource.ID_VEHICLE_ON_ROUTE) < 0.01) {
                                                String vehicleString = feature.getStringProperty("jsonString");
                                                if (vehicleString != null) {
                                                    Gson gson = new Gson();
                                                    try {
                                                        selectedVehicleOnRoute = gson.fromJson(vehicleString, VehicleOnRoute.class);
                                                        if (selectedVehicleOnRoute != null) {
                                                            currentSelectedLocationMode = Constants.SELECTED_LOCATION_MODE_VEHICLE_ON_ROUTE;
                                                            addMarkerViewIfNeeded(new LatLng(selectedVehicleOnRoute.getLat(), selectedVehicleOnRoute.getLon()));
                                                            return true;
                                                        }
                                                    } catch (Exception e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                            } else if (Math.abs((double) (feature.getNumberProperty(entry.getKey())) - Resource.ID_PARK_RIDE) < 0.01) {
                                                String parkRideString = feature.getStringProperty("jsonString");
                                                if (parkRideString != null) {
                                                    Gson gson = new Gson();
                                                    try {
                                                        selectedParkRideLocation = gson.fromJson(parkRideString, ParkRideLocation.class);
                                                        if (selectedParkRideLocation != null) {
                                                            currentSelectedLocationMode = Constants.SELECTED_LOCATION_MODE_PARK_RIDE;
                                                            addMarkerViewIfNeeded(new LatLng(selectedParkRideLocation.getLat(), selectedParkRideLocation.getLon()));
                                                            return true;
                                                        }
                                                    } catch (Exception e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                            } else if (Math.abs((double) (feature.getNumberProperty(entry.getKey())) - Resource.ID_TRANSIT_STOP) < 0.01
                                                    || Math.abs((double) (feature.getNumberProperty(entry.getKey())) - Resource.ID_STOP_ON_ROUTE_DIRECTION) < 0.01) {
                                                String transitStopString = feature.getStringProperty("jsonString");
                                                if (transitStopString != null) {
                                                    Gson gson = new Gson();
                                                    try {
                                                        selectedTransitStop = gson.fromJson(transitStopString, TransitStop.class);
                                                        if (selectedTransitStop != null) {
                                                            currentSelectedLocationMode = Constants.SELECTED_LOCATION_MODE_TRANSIT_STOP;
                                                            addMarkerViewIfNeeded(new LatLng(selectedTransitStop.getLat(), selectedTransitStop.getLon()));
                                                            return true;
                                                        }
                                                    } catch (Exception e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                            } else if (Math.abs((double) (feature.getNumberProperty(entry.getKey())) - Resource.ID_RENTAL_VEHICLE) < 0.01) {
                                                String rentalVehicleString = feature.getStringProperty("jsonString");
                                                if (rentalVehicleString != null) {
                                                    Gson gson = new Gson();
                                                    try {
                                                        selectedRentalVehicle = gson.fromJson(rentalVehicleString, RentalVehicle.class);
                                                        if (selectedRentalVehicle != null) {
                                                            currentSelectedLocationMode = Constants.SELECTED_LOCATION_MODE_RENTAL_VEHICLE;
                                                            addMarkerViewIfNeeded(new LatLng(selectedRentalVehicle.getLat(), selectedRentalVehicle.getLon()));
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


                        currentSelectedLocationMode = Constants.SELECTED_LOCATION_MODE_POINT;
                        addMarkerViewIfNeeded(point);

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

    private void enterStopViewerPage(String mode) {
        if (getActivity() != null && stopViewerItineraryLegPoint != null) {
            Intent intent = new Intent(systemContext, StopViewerActivity.class);
            intent.putExtra(Constants.STOP_VIEWER_ITINERARY_LEG_POINT, stopViewerItineraryLegPoint);
            if (mode != null) {
                intent.putExtra(Constants.STOP_VIEWER_ITINERARY_MODE, mode);
            }
            getActivity().startActivityForResult(intent, Constants.STOP_VIEWER_REQUEST_CODE);
        }
    }

    private void setupMarkerView() {
        llVehicleOnRoute = customView.findViewById(R.id.ll_vehicle_on_route);
        ivAgencyLogo = customView.findViewById(R.id.iv_agency_logo);
        llStopParkingPopup = customView.findViewById(R.id.ll_stop_parking_marker_popup);

        tvMarkerVehicleName = customView.findViewById(R.id.tv_marker_vehicle_name);
        tvMarkerVehicleSpeed = customView.findViewById(R.id.tv_marker_vehicle_speed);
        tvMarkerVehicleTime = customView.findViewById(R.id.tv_marker_vehicle_time);

        tvMapAddress = customView.findViewById(R.id.tv_map_address);
        LinearLayout llMarkerBtnFromHere = customView.findViewById(R.id.ll_marker_btn_from_here);
        LinearLayout llMarkerBtnToHere = customView.findViewById(R.id.ll_marker_btn_to_here);
        ivMarkerViewClose = customView.findViewById(R.id.iv_marker_view_close);
        llMarkerStopViewer = customView.findViewById(R.id.ll_marker_stop_viewer);
        llMarkerBtnStopViewer = customView.findViewById(R.id.ll_marker_btn_stop_viewer);
        tvMarkerStopID = customView.findViewById(R.id.tv_marker_stop_id);

        ivMarkerViewClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                customView.setVisibility(View.GONE);
            }
        });

        llMarkerBtnFromHere.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                customView.setVisibility(View.GONE);

                if (getActivity() != null) {
                    ((TabActivity) getActivity()).loadPlanTripFromStopViewer(true,
                            tvMapAddress.getText().toString(),
                            markerViewLatlng.getLatitude(), markerViewLatlng.getLongitude());
                }
            }
        });

        llMarkerBtnToHere.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                customView.setVisibility(View.GONE);
                if (getActivity() != null) {
                    ((TabActivity) getActivity()).loadPlanTripFromStopViewer(false,
                            tvMapAddress.getText().toString(),
                            markerViewLatlng.getLatitude(), markerViewLatlng.getLongitude());
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

    private void refreshMarkerView(LatLng latlng) {
        switch (currentSelectedLocationMode) {
            case Constants.SELECTED_LOCATION_MODE_PARK_RIDE:
                if (gpsReverseRequest != null) {
                    gpsReverseRequest.cancel();
                    gpsReverseRequest = null;
                }
                llVehicleOnRoute.setVisibility(View.GONE);
                llStopParkingPopup.setVisibility(View.VISIBLE);
                ivAgencyLogo.setVisibility(View.GONE);
                llMarkerStopViewer.setVisibility(View.GONE);
                llMarkerBtnStopViewer.setVisibility(View.GONE);
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
                llVehicleOnRoute.setVisibility(View.GONE);
                llStopParkingPopup.setVisibility(View.VISIBLE);
                llMarkerStopViewer.setVisibility(View.VISIBLE);
                llMarkerBtnStopViewer.setVisibility(View.VISIBLE);
                ivAgencyLogo.setVisibility(View.GONE);
                if (selectedTransitStop != null) {
                    tvMapAddress.setText(selectedTransitStop.getName());
                    String id = Utilities.getStopId(selectedTransitStop);
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
                llVehicleOnRoute.setVisibility(View.GONE);
                llStopParkingPopup.setVisibility(View.VISIBLE);
                ivAgencyLogo.setVisibility(View.GONE);
                llMarkerStopViewer.setVisibility(View.GONE);
                llMarkerBtnStopViewer.setVisibility(View.GONE);
                if (selectedRentalVehicle != null) {
                    StringBuffer stringBuffer = new StringBuffer();
                    if (selectedRentalVehicle.getVehicleType() != null && "SCOOTER".equalsIgnoreCase(selectedRentalVehicle.getVehicleType().getFormFactor())) {
                        stringBuffer.append(getString(R.string.e_scooter));
                    } else {
                        stringBuffer.append(getString(R.string.free_floating_bike));
                    }
                    stringBuffer.append(": ");
                    if (selectedRentalVehicle.getNetwork() != null) {
                        String[] network = selectedRentalVehicle.getNetwork().split("_");

                        if (network.length > 0) {
                            if (network[0].equalsIgnoreCase("link")) {
                                stringBuffer.append(getString(R.string.link_first_capital));
                            } else {
                                stringBuffer.append(network[0].toUpperCase(Locale.ROOT));
                            }
                        }
                    }
                    tvMapAddress.setText(stringBuffer.toString());
                    tvMapAddress.setTypeface(null, Typeface.BOLD);
                }
                break;
            case Constants.SELECTED_LOCATION_MODE_VEHICLE_ON_ROUTE:
                if (gpsReverseRequest != null) {
                    gpsReverseRequest.cancel();
                    gpsReverseRequest = null;
                }
                llVehicleOnRoute.setVisibility(View.VISIBLE);
                llStopParkingPopup.setVisibility(View.GONE);
                ivAgencyLogo.setVisibility(View.GONE);
                if (selectedVehicleOnRoute != null) {
                    if (selectedVehicleOnRoute.getSpeed() > 0) {
                        tvMarkerVehicleSpeed.setVisibility(View.VISIBLE);
                        tvMarkerVehicleSpeed.setText(systemContext.getString(R.string.vehicle_on_route_speed, (int) selectedVehicleOnRoute.getSpeed()));

                    } else {
                        tvMarkerVehicleSpeed.setVisibility(View.GONE);
                    }

                    if (selectedVehicleOnRoute.getLabel() != null && !selectedVehicleOnRoute.getLabel().isEmpty()) {
                        tvMarkerVehicleName.setText(systemContext.getString(R.string.vehicle_on_route_name, selectedVehicleOnRoute.getLabel()));
                    } else if (selectedVehicleOnRoute.getVehicleId() != null && !selectedVehicleOnRoute.getVehicleId().isEmpty()) {
                        tvMarkerVehicleName.setText(systemContext.getString(R.string.vehicle_on_route_name, selectedVehicleOnRoute.getVehicleId()));
                    } else if (selectedVehicleOnRoute.getPatternId() != null && !selectedVehicleOnRoute.getPatternId().isEmpty()) {
                        tvMarkerVehicleName.setText(systemContext.getString(R.string.vehicle_on_route_name, selectedVehicleOnRoute.getPatternId()));
                    } else {
                        tvMarkerVehicleName.setText(systemContext.getString(R.string.vehicle_on_route_name, ""));
                    }

                    long currentTime = System.currentTimeMillis() / 1000;
                    long diff = currentTime - selectedVehicleOnRoute.getSeconds();
                    tvMarkerVehicleTime.setText(systemContext.getString(R.string.vehicle_on_route_time, (int) diff));
                }
                break;
            case Constants.SELECTED_LOCATION_MODE_POINT:
                llMarkerStopViewer.setVisibility(View.GONE);
                llMarkerBtnStopViewer.setVisibility(View.GONE);
                llVehicleOnRoute.setVisibility(View.GONE);
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
                            if (gpsReverseMode == Constants.GPS_REVERSE_MODE_MARKER_VIEW) {
                                if (tvMapAddress != null) {
                                    tvMapAddress.setText(address);
                                }
                                customView.setVisibility(View.VISIBLE);
                                double zoomLevel = gMapboxMap.getCameraPosition().zoom;
                                if (zoomLevel < Constants.MARKER_POPUP_ZOOM_LEVEL) {
                                    zoomLevel = Constants.MARKER_POPUP_ZOOM_LEVEL;
                                }
                                CameraPosition position = new CameraPosition.Builder()
                                        .padding(0, 0, 0, Convert.dpToPx(30))
                                        .target(markerViewLatlng)
                                        .zoom(zoomLevel)
                                        .build();
                                gMapboxMap.moveCamera(CameraUpdateFactory.newCameraPosition(position));
                            }
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

    private void gpsReverseErrorHandler() {
        if (gpsReverseMode == Constants.GPS_REVERSE_MODE_MARKER_VIEW) {
            customView.setVisibility(View.VISIBLE);
        }
    }

    private void addMarkerViewIfNeeded(LatLng latlng) {
        if (markerView == null) {
            customView = getLayoutInflater().inflate(R.layout.layout_vehicle_on_route, null);
            customView.setLayoutParams(new FrameLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT));

            setupMarkerView();
            refreshMarkerView(latlng);
            if (currentSelectedLocationMode == Constants.SELECTED_LOCATION_MODE_POINT) {
                customView.setVisibility(View.GONE);
            } else {
                customView.setVisibility(View.VISIBLE);
            }
            markerView = new MarkerView(latlng, customView);
            markerView.setOnPositionUpdateListener(new MarkerView.OnPositionUpdateListener() {
                @Override
                public PointF onUpdate(PointF pointF) {

                    customView.measure(
                            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
                    pointF.x = pointF.x - customView.getMeasuredWidth() / 2.0f;
                    pointF.y = pointF.y - customView.getMeasuredHeight() - 56.0f;

                    return pointF;
                }
            });
            markerViewManager.addMarker(markerView);
        } else {
            if (currentSelectedLocationMode == Constants.SELECTED_LOCATION_MODE_POINT) {
                customView.setVisibility(View.GONE);
            } else {
                customView.setVisibility(View.VISIBLE);
            }
            refreshMarkerView(latlng);
            markerView.setLatLng(latlng);
        }
        markerViewLatlng = latlng;
        CameraPosition position = new CameraPosition.Builder()
                .padding(0, 0, 0, Convert.dpToPx(30))
                .target(markerViewLatlng)
                .build();
        gMapboxMap.moveCamera(CameraUpdateFactory.newCameraPosition(position));
    }

    private void moveMapToDefaultLocation() {
        gMapboxMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder()
                .target(new LatLng(LATITUDE_DEFAULT, LONGITUDE_DEFAULT))
                .zoom(ZOOM_DEFAULT)
                .build()));
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
                }
            });
        }
    }

    private void removeVehicleMarkersFromMap() {
        for (CustomizeMarkerView customizeMarkerView : vehicleOnRouteMarkerViews) {
            if (customizeMarkerView != null) {
                markerViewManager.removeMarker(customizeMarkerView);
            }
        }
        vehicleOnRouteMarkerViews.clear();
    }

    private CustomizeMarkerView addVehicleOnRouteMarkerView(VehicleOnRoute vehicleOnRoute) {
        if (getActivity() == null || vehicleOnRoute == null) {
            return null;
        }
        LatLng latlng = new LatLng(vehicleOnRoute.getLat(), vehicleOnRoute.getLon());
        CustomizeMarkerView markerViewRouteOriginDestination = null;
        View view = getLayoutInflater().inflate(R.layout.layout_marker_view_origin_destination, null);
        view.setLayoutParams(new FrameLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT));

        ImageView ivMarkerViewIcon = view.findViewById(R.id.iv_marker_view_icon);

        int resID;
        if (currentRouteViewerRoute != null && currentRouteViewerRoute.getMode() != null) {
            resID = Utilities.getModeImageByMode(currentRouteViewerRoute.getMode().toLowerCase(Locale.ROOT), false, requireActivity(), null, null);
            ivMarkerViewIcon.setBackgroundResource(R.drawable.ic_layer_circle_inactive);
            ivMarkerViewIcon.setColorFilter(getResources().getColor(android.R.color.black));
        } else {
            resID = R.drawable.ic_realtime;
            ivMarkerViewIcon.setColorFilter(null);
            ivMarkerViewIcon.setBackground(null);
        }
        ivMarkerViewIcon.setImageResource(resID);

        ivMarkerViewIcon.setTag(vehicleOnRoute);
        ivMarkerViewIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VehicleOnRoute vehicleOnRoute = (VehicleOnRoute) v.getTag();

                if (vehicleOnRoute != null) {
                    try {
                        selectedVehicleOnRoute = vehicleOnRoute;
                        currentSelectedLocationMode = Constants.SELECTED_LOCATION_MODE_VEHICLE_ON_ROUTE;
                        addMarkerViewIfNeeded(new LatLng(selectedVehicleOnRoute.getLat(), selectedVehicleOnRoute.getLon()));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
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

    private void loadVehiclesOnMap() {
        if (gMapStyle != null && markerViewManager != null) {
            removeVehicleMarkersFromMap();

            if (vehiclesOnRoute != null) {
                for (VehicleOnRoute vehicleOnRoute : vehiclesOnRoute) {
                    vehicleOnRouteMarkerViews.add(addVehicleOnRouteMarkerView(vehicleOnRoute));
                }
            }
        }
    }

    private void digestRouteViewerPatterns() {
        if (currentRouteViewerRouteDetail != null && currentRouteViewerRouteDetail.getPatterns() != null) {
            routeViewerPatterns = currentRouteViewerRouteDetail.getPatterns();
            if (routeViewerPatterns != null) {
                removeRouteGeometryFromMapIfNeeded();
                if (routeViewerPatterns.size() > 0) {
                    RouteViewerPattern routeViewerPattern = routeViewerPatterns.get(0);
                    if (routeViewerPattern != null && routeViewerPattern.getPatternGeometry() != null) {
                        Logger.d("vp", "show route when digestRouteViewerPatterns");
                        showRouteGeometryOnMap(routeViewerPattern.getPatternGeometry().getPoints());
                        moveMapToShowRouteOnRouteViewer();
                    }
                }
            }
            for (int i = 0; i < routeViewerPatterns.size(); i++) {
                RouteViewerPattern routeViewerPattern = routeViewerPatterns.get(i);
                ArrayList<VehicleOnRoute> vehicleOnRoutes = new ArrayList<>();
                if (i < vehiclesOnRouteLists.size()) {
                    vehicleOnRoutes.addAll(vehiclesOnRouteLists.get(i));
                }
                routeViewerPattern.setVehicleOnRoutes(vehicleOnRoutes);
            }
            refreshRouteDirections(currentRouteViewerRouteDetail);
        }
    }

    private void getRouteViewerRouteDetail() {
        if (!GRAPHQL_SERVER_URL.isEmpty()) {
            if (getActivity() != null) {
                ((TabActivity) getActivity()).showProgressBar();
            }
            ApolloClient apolloClient = ApolloClient.builder()
                    .serverUrl(GRAPHQL_SERVER_URL)
                    .build();
            if (routeViewerRouteDetailQueryCall != null) {
                routeViewerRouteDetailQueryCall.cancel();
                routeViewerRouteDetailQueryCall = null;
            }

            routeViewerRouteDetailQueryCall = apolloClient.query(new RouteInfoQuery(currentRouteViewerRoute.getId()));
            routeViewerRouteDetailQueryCall.enqueue(new ApolloCall.Callback<RouteInfoQuery.Data>() {
                @Override
                public void onResponse(@NonNull com.apollographql.apollo.api.Response<RouteInfoQuery.Data> response) {
                    if (response != null && response.getData() != null && response.getData().route() != null) {
                        Gson gson = new Gson();
                        currentRouteViewerRouteDetail = gson.fromJson(gson.toJson(response.getData().route()), RouteViewerRouteDetail.class);

                        Handler mainHandler = new Handler(Looper.getMainLooper());
                        mainHandler.post(() -> {
                            if (routeViewerRouteListAdapter != null) {
                                routeViewerRouteListAdapter.notifyDataSetChanged();
                            }
                            digestRouteViewerPatterns();
                            for (ViewPager2 viewPager2 : vpRouteDirectionList) {
                                if (viewPager2 != null && viewPager2.getAdapter() != null) {
                                    RouteDirectionAdapter routeDirectionAdapter = (RouteDirectionAdapter) viewPager2.getAdapter();
                                    routeDirectionAdapter.notifyItemRangeRemoved(0, routeDirectionAdapter.getItemCount());
                                    routeDirectionAdapter.notifyItemRangeInserted(0, titleList.size());
                                }
                            }
                            if (routeViewerPatterns != null && routeViewerPatterns.size() > 0) {
                                refreshRouteStopsListView(routeViewerPatterns.get(0));
                            }

                            for (GridView gridView : gGridViewList) {
                                setupGridView(gridView);
                                if (gridView != null && gridView.getAdapter() != null) {
                                    GridViewAdapter gridViewAdapter = (GridViewAdapter) gridView.getAdapter();

                                    if (gridViewAdapter != null) {
                                        gridViewAdapter.notifyDataSetChanged();
                                    }
                                }
                            }

                        });
                    }
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            if (getActivity() != null) {
                                ((TabActivity) getActivity()).hideProgressBar();
                            }
                        }
                    });
                }

                @Override
                public void onFailure(@NonNull ApolloException e) {
                    Logger.e("data", "get route viewer route detail error:" + e);
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            if (getActivity() != null) {
                                ((TabActivity) getActivity()).hideProgressBar();
                            }
                        }
                    });
                }
            });

        } else {
            Response.Listener<String> listener = new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    if (response != null && !response.isEmpty()) {
                        Gson gson = new Gson();

                        currentRouteViewerRouteDetail = gson.fromJson(response, RouteViewerRouteDetail.class);
                        new Handler(Looper.getMainLooper()).post(() -> {
                            if (routeViewerRouteListAdapter != null) {
                                routeViewerRouteListAdapter.notifyDataSetChanged();
                            }
                        });
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
                getRouteViewerRouteDetailResponse(listener, errorListener);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void getRouteViewerRouteDetailResponse(Response.Listener<String> listener,
                                                  Response.ErrorListener errListener) throws Exception {
        if (currentRouteViewerRoute != null) {
            String url = String.format(Resource.API_ROUTE_VIEWER_ROUTE_DETAIL, currentRouteViewerRoute.getId());

            volleyStringRequest(Request.Method.GET, url, "", listener, errListener);
        }
    }

    private void moveMapToShowRouteOnRouteViewer() {
        if (gMapboxMap != null && routeViewerRoutePoints != null && routeViewerRoutePoints.size() > 0) {
            LatLngBounds.Builder latLngBoundsBuilder = new LatLngBounds.Builder();
            int locationPointsNumber = 0;
            for (String routeViewerRoutePoint : routeViewerRoutePoints) {
                List<Point> routeCoordinates = Utilities.decodeGPSPointsForRoute(routeViewerRoutePoint);
                for (Point point : routeCoordinates) {
                    latLngBoundsBuilder.include(new LatLng(point.latitude(), point.longitude()));
                    locationPointsNumber++;
                }
            }

            if (locationPointsNumber >= 2) {
                int bottomPadding = currentHeight + Convert.dpToPx(26);
                gMapboxMap.animateCamera(CameraUpdateFactory.newLatLngBounds(latLngBoundsBuilder.build(),
                        Convert.dpToPx(26), Convert.dpToPx(76), Convert.dpToPx(26), bottomPadding), null);
            }
        }
    }

    private void removeRouteGeometryFromMapIfNeeded() {
        Logger.d("vp", "removeRouteGeometryFromMapIfNeeded before show route");
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

    private void showRouteGeometryOnMap(String points) {
        if (points != null) {
            String routeColorString = null;
            if (currentRouteViewerRoute != null) {
                routeColorString = currentRouteViewerRoute.getColor();
            }
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

            if (gMapStyle.getLayer("unclustered_transit_stop_points") != null) {
                Logger.d("vp", "show route unclustered_transit_stop_points is exist");
                gMapStyle.addLayerAbove(layer, "unclustered_transit_stop_points");
            } else {
                Logger.d("vp", "show route unclustered_transit_stop_points is not exist");
                gMapStyle.addLayer(layer);
            }

            routeViewerRouteSources.add(source);
            routeViewerRouteLayers.add(layer);
        }
    }

    private void getRouteViewerRouteGeometry(String routeSubID) {
        Response.Listener<String> listener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response != null && !response.isEmpty()) {
                    Gson gson = new Gson();

                    ItineraryLegGeometry routeGeometry = gson.fromJson(response, ItineraryLegGeometry.class);
                    if (routeGeometry != null) {
                        Logger.d("vp", "show route when getRouteViewerRouteGeometry");
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
            getRouteViewerRouteGeometryResponse(routeSubID, listener, errorListener);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getRouteViewerRouteGeometryResponse(String routeSubID, Response.Listener<String> listener,
                                                    Response.ErrorListener errListener) throws Exception {
        if (routeSubID != null) {
            String url = String.format(Resource.API_ROUTE_VIEWER_GEOMETRY, routeSubID);

            if (routeViewerRouteGeometryStringRequest != null) {
                routeViewerRouteGeometryStringRequest.cancel();
            }
            routeViewerRouteGeometryStringRequest = volleyStringRequest(Request.Method.GET, url, "", listener, errListener);
        }
    }

    private void refreshRouteDirections(RouteViewerRouteDetail routeDetail) {
        titleList.clear();
        String title = null;

        if (routeViewerPatterns != null && routeViewerPatterns.size() > 0) {
            Collections.sort(routeViewerPatterns, new Comparator<RouteViewerPattern>() {
                @Override
                public int compare(RouteViewerPattern routeViewerPattern, RouteViewerPattern t1) {

                    return Integer.compare(t1.getStops().size(), routeViewerPattern.getStops().size());
                }
            });

            //deal with duplicate issue
            ArrayList<RouteViewerPattern> routeViewerPatternsTemp = new ArrayList<>();
            ArrayList<RouteViewerPattern> routeViewerPatternsFinal = new ArrayList<>();
            ArrayList<StopNode> uniqueLines = new ArrayList<>();
            for (int i = 0; i < routeViewerPatterns.size(); i++) {
                RouteViewerPattern p = routeViewerPatterns.get(i);
                if (uniqueLines.isEmpty()) {
                    uniqueLines.add(new StopNode(p.getHeadsign(), Integer.MIN_VALUE));
                    routeViewerPatternsTemp.add(p);
                    routeViewerPatternsFinal.add(p);
                } else {
                    boolean isContainHeadSign = false;
                    for (StopNode n : uniqueLines) {
                        if (n.headSignName.equalsIgnoreCase(p.getHeadsign())) {
                            isContainHeadSign = true;
                            break;
                        }
                    }
                    if (isContainHeadSign && !isSubSequence(routeViewerPatternsTemp, p, true)) {
                        uniqueLines.add(new StopNode(p.getHeadsign(), Integer.MIN_VALUE));
                        routeViewerPatternsTemp.add(p);
                        routeViewerPatternsFinal.add(p);
                    } else if (!isContainHeadSign && !isSubSequence(routeViewerPatternsTemp, p, false)) {
                        uniqueLines.add(new StopNode(p.getHeadsign(), Integer.MIN_VALUE));
                        routeViewerPatternsTemp.add(p);
                        routeViewerPatternsFinal.add(p);
                    }
                }
                if (p.getHeadsign() != null && p.getStops() != null) {
                    for (StopNode s : uniqueLines) {
                        if (p.getHeadsign().equalsIgnoreCase(s.headSignName) && p.getStops().size() > s.maxStops) {
                            s.maxStops = p.getStops().size();
                        }
                    }
                }
            }

            routeViewerPatterns = routeViewerPatternsTemp;

            for (RouteViewerPattern routeViewerPattern : routeViewerPatterns) {
                title = null;
                if (routeViewerPattern.getHeadsign() != null) {
                    if (routeViewerPattern.getName() != null) {
                        for (String t : titleList) {
                            if (t.contains(routeViewerPattern.getHeadsign())) {
                                title = String.format("%s (%s)", routeViewerPattern.getHeadsign(), routeViewerPattern.getStops().get(routeViewerPattern.getStops().size() - 1).getName());
                                routeViewerPattern.setTitle(title);
                                break;
                            }
                        }
                    }
                    if (title == null) {
                        title = routeViewerPattern.getHeadsign();
                    }

                } else {
                    if (routeViewerPattern.getName() != null) {
                        title = routeViewerPattern.getName();
                    }
                }

                if (title != null) {
                    titleList.add(title);
                } else {
                    if (routeDetail != null) {
                        titleList.add(routeDetail.getLongName());
                    } else {
                        titleList.add("");
                    }
                }
            }
        }

        //new feature, sort by active vehicles
        sortByActiveVehicleOnRoute();

        gbNeedRefreshDirectionsAndStops = true;
    }

    private void sortByActiveVehicleOnRoute() {

        TreeMap<String, ArrayList<RouteViewerPattern>> patternMap = new TreeMap<>();
        for (RouteViewerPattern routeViewerPattern : routeViewerPatterns) {
            if (routeViewerPattern.getHeadsign() != null) {
                if (patternMap.containsKey(routeViewerPattern.getHeadsign())) {
                    patternMap.get(routeViewerPattern.getHeadsign()).add(routeViewerPattern);
                } else {
                    ArrayList<RouteViewerPattern> list = new ArrayList<>(List.of(routeViewerPattern));
                    patternMap.put(routeViewerPattern.getHeadsign(), list);
                }
            } else {
                ArrayList<RouteViewerPattern> list = new ArrayList<>(List.of(routeViewerPattern));
                patternMap.put(routeViewerPattern.getId(), list);
            }
        }

        for (String key : patternMap.keySet()) {
            ArrayList<RouteViewerPattern> patterns = patternMap.get(key);
            Collections.sort(patterns, (t1, t2) -> t2.getVehicleOnRoutes().size() - t1.getVehicleOnRoutes().size());
        }

        titleList = new ArrayList<>();
        ArrayList<RouteViewerPattern> routeViewerPatternsTemp = new ArrayList<>();
        for (String key : patternMap.keySet()) {
            ArrayList<RouteViewerPattern> patterns = patternMap.get(key);
            for (RouteViewerPattern pattern : patterns) {
                if (pattern.getTitle() != null) {
                    titleList.add(pattern.getTitle());
                } else {
                    titleList.add(pattern.getHeadsign());
                }
                routeViewerPatternsTemp.add(pattern);
            }
        }

        routeViewerPatterns = routeViewerPatternsTemp;
    }

    private boolean isSubSequence(ArrayList<RouteViewerPattern> routeViewerPatterns, RouteViewerPattern p, boolean isSameHeadSign) {
        boolean isSubSequence = false;

        for (RouteViewerPattern p2 : routeViewerPatterns) {
            if (isSameHeadSign) {
                if (p2.getHeadsign() != null && !p2.getHeadsign().equalsIgnoreCase(p.getHeadsign())) {
                    isSubSequence = true;
                    continue;
                }
            }
            if (p2.getHeadsign() != null && p2.getHeadsign().equalsIgnoreCase(p.getHeadsign()) &&
                    p2.getStops() != null && p.getStops() != null && p2.getStops().size() == p.getStops().size()) {
                isSubSequence = true;
                continue;
            }
            isSubSequence = false;
            int childIndex = 0;
            boolean isHeadFound = false;
            for (TransitStop stopParent : p2.getStops()) {
                if (p.getStops().get(childIndex).getName().equalsIgnoreCase(stopParent.getName())) {
                    isHeadFound = true;
                    if (childIndex + 1 < p.getStops().size()) {
                        childIndex++;
                    } else {
                        isSubSequence = true;
                        return isSubSequence;
                    }
                } else {
                    if (isHeadFound) {
                        break;
                    }
                }
            }
        }
        return isSubSequence;
    }

    private ApolloQueryCall routeViewerVehiclesQueryCall = null;

    private void refreshVehiclesOnRoute(int index) {
        if (gbShowDirectionStops) {
            if (index >= 0 && index < vehiclesOnRouteLists.size()) {
                vehiclesOnRoute.clear();
                vehiclesOnRoute.addAll(vehiclesOnRouteLists.get(index));
            }
        } else {
            vehiclesOnRoute.clear();
            for (int i = 0; i < vehiclesOnRouteLists.size(); i++) {
                vehiclesOnRoute.addAll(vehiclesOnRouteLists.get(i));
            }
        }
    }

    private void refreshVehiclesOnRouteExt(int index) {
        if (gbShowDirectionStops) {
            if (index >= 0 && index < routeViewerPatterns.size()) {
                RouteViewerPattern routeViewerPattern = routeViewerPatterns.get(index);
                vehiclesOnRoute.clear();

                if (routeViewerPattern != null && routeViewerPattern.getVehicleOnRoutes() != null) {
                    vehiclesOnRoute.addAll(routeViewerPattern.getVehicleOnRoutes());
                } else if (index < vehiclesOnRouteLists.size()) {
                    vehiclesOnRoute.addAll(vehiclesOnRouteLists.get(index));
                }
            }
        } else {
            vehiclesOnRoute.clear();
            for (int i = 0; i < vehiclesOnRouteLists.size(); i++) {
                vehiclesOnRoute.addAll(vehiclesOnRouteLists.get(i));
            }
        }
    }


    private void getRouteViewerVehicles() {
        if (!GRAPHQL_SERVER_URL.isEmpty()) {
            ApolloClient apolloClient = ApolloClient.builder()
                    .serverUrl(GRAPHQL_SERVER_URL)
                    .build();
            if (routeViewerVehiclesQueryCall != null) {
                routeViewerVehiclesQueryCall.cancel();
            }
            routeViewerVehiclesQueryCall = apolloClient.query(new GetRouteVehiclesQuery(currentRouteViewerRoute.getId()));
            routeViewerVehiclesQueryCall.enqueue(new ApolloCall.Callback<GetRouteVehiclesQuery.Data>() {
                @Override
                public void onResponse(@NonNull com.apollographql.apollo.api.Response<GetRouteVehiclesQuery.Data> response) {
                    Logger.d("graphql", response.toString());
                    try {
                        if (response.getData() != null && response.getData().route() != null && response.getData().route().patterns() != null) {
                            if (vehiclesOnRouteLists != null) {
                                vehiclesOnRouteLists.clear();
                            } else {
                                vehiclesOnRouteLists = new ArrayList<>();
                            }

                            for (GetRouteVehiclesQuery.Pattern pattern : response.getData().route().patterns()) {
                                ArrayList<VehicleOnRoute> vehicleOnRoutes = new ArrayList<>();

                                if (pattern != null && pattern.vehiclePositions() != null) {
                                    for (GetRouteVehiclesQuery.VehiclePosition vehiclePosition : pattern.vehiclePositions()) {
                                        if (vehiclePosition != null) {
                                            VehicleOnRoute vehicleOnRoute = new VehicleOnRoute();

                                            vehicleOnRoute.setVehicleId(vehiclePosition.vehicleId());
                                            if (vehiclePosition.lat() != null) {
                                                vehicleOnRoute.setLat(vehiclePosition.lat());
                                            }
                                            if (vehiclePosition.lon() != null) {
                                                vehicleOnRoute.setLon(vehiclePosition.lon());
                                            }

                                            if (vehiclePosition.heading() != null) {
                                                vehicleOnRoute.setHeading(vehiclePosition.heading());
                                            }

                                            vehicleOnRoute.setLabel(vehiclePosition.label());
                                            if (vehiclePosition.speed() != null) {
                                                vehicleOnRoute.setSpeed(vehiclePosition.speed());
                                            }

                                            if (vehiclePosition.lastUpdated() != null) {
                                                vehicleOnRoute.setSeconds(Long.parseLong(vehiclePosition.lastUpdated().toString()));
                                            }

                                            vehicleOnRoutes.add(vehicleOnRoute);
                                        }
                                    }
                                }
                                vehiclesOnRouteLists.add(vehicleOnRoutes);
                            }
                        }
                        if (currentRouteDirectionIndex < vehiclesOnRouteLists.size()
                                && currentRouteDirectionIndex < routeViewerPatterns.size()) {
                            refreshVehiclesOnRouteExt(currentRouteDirectionIndex);
                        } else {
                            refreshVehiclesOnRouteExt(0);
                        }

                        if (getActivity() != null) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (vehiclesOnRoute == null || vehiclesOnRoute.size() == 0) {
                                        removeVehicleMarkersFromMap();
                                    } else {
                                        loadVehiclesOnMap();
                                    }
                                }
                            });
                        }
                    } catch (Exception e) {
                        Logger.e("graphql", "Parse data exception:" + e);
                    }
                }

                @Override
                public void onFailure(@NonNull ApolloException e) {
                    Logger.e("graphql", e.toString());
                    Handler mainHandler = new Handler(Looper.getMainLooper());
                    mainHandler.post(() -> {
                        removeVehicleMarkersFromMap();
                    });
                }
            });
        } else {
            Response.Listener<String> listener = new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    if (response != null && !response.isEmpty() && response.startsWith("[")) {
                        Gson gson = new Gson();

                        vehiclesOnRoute = gson.fromJson(response, new TypeToken<ArrayList<VehicleOnRoute>>() {
                        }.getType());
                        if (vehiclesOnRoute != null) {
                            loadVehiclesOnMap();
                        }
                    } else {
                        Logger.d("data", "route viewer route Vehicles is not correct:" + response);
                    }

                    if (vehiclesOnRoute == null || vehiclesOnRoute.size() == 0) {
                        removeVehicleMarkersFromMap();
                    }
                }
            };
            Response.ErrorListener errorListener = new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    removeVehicleMarkersFromMap();
                    Logger.e("data", "get route viewer route Vehicles error:" + error.toString());
                }
            };

            try {
                getRouteViewerVehiclesRequest = getRouteViewerVehiclesResponse(listener, errorListener);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public StringRequest getRouteViewerVehiclesResponse(Response.Listener<String> listener,
                                                        Response.ErrorListener errListener) throws Exception {
        if (currentRouteViewerRoute != null) {
            String url = String.format(Resource.API_ROUTE_VIEWER_VEHICLES, currentRouteViewerRoute.getId());

            return volleyStringRequest(Request.Method.GET, url, "", listener, errListener);
        }

        return null;
    }

    private RouteViewerRouteListAdapter getRouteViewerRouteListAdapter() {
        ArrayList<HashMap<String, Object>> data = new ArrayList<HashMap<String, Object>>();

        for (int i = 0; i < routeViewerRoutes.size(); i++) {
            RouteViewerRoute routeViewerRoute = routeViewerRoutes.get(i);

            HashMap<String, Object> map = new HashMap<String, Object>();

            map.put("tv_route", routeViewerRoute.getLongName());
            data.add(map);
        }

        RouteViewerRouteListAdapter mRouteViewerRouteListAdapter = new RouteViewerRouteListAdapter(
                getActivity(), data,
                R.layout.item_route_viewer_route,
                new String[]{"tv_route"}, new int[]{R.id.tv_route});

        return mRouteViewerRouteListAdapter;
    }

    class RouteViewerRouteListAdapter extends SimpleAdapter {
        Context mContext;
        private final LayoutInflater l_Inflater;

        public RouteViewerRouteListAdapter(Context context, List<? extends Map<String, ?>> data,
                                           int resource, String[] from, int[] to) {
            super(context, data, resource, from, to);

            mContext = context;
            l_Inflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return routeViewerRoutes.size();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            RouteViewerRoute routeViewerRoute = routeViewerRoutes.get(position);

            if (convertView == null) {
                if (Utilities.checkIfToDisplayAccessibilityUI(requireActivity())) {
                    convertView = l_Inflater.inflate(
                            R.layout.item_route_viewer_route_accessibility, null);

                } else {
                    convertView = l_Inflater.inflate(
                            R.layout.item_route_viewer_route, null);
                }

            }

            if (routeViewerRoute != null) {
                TextView tvRoute = convertView.findViewById(R.id.tv_route);
                TextView tvRouteName = convertView.findViewById(R.id.tv_route_name);
                TextView tvRouteDescription = convertView.findViewById(R.id.tv_route_description);
                ImageView ivRouteType = convertView.findViewById(R.id.iv_route_type);
                NetworkImageView ivLogo = convertView.findViewById(R.id.iv_route_logo);
                TextView tvRouteAgencyInfo = convertView.findViewById(R.id.tv_route_agency_info);
                LinearLayout llRouteInfo = convertView.findViewById(R.id.ll_route_info);

                int maxWidth = (gDm.widthPixels - Convert.dpToPx(120)) / 2;
                tvRouteName.setMaxWidth(maxWidth);
                tvRoute.setMaxWidth(maxWidth);

                String routeNumber = null;
                String routeName = null;
                if (routeViewerRoute.getShortName() != null && !routeViewerRoute.getShortName().isEmpty()) {
                    routeNumber = routeViewerRoute.getShortName();
                }

                if (routeViewerRoute.getLongName() != null
                        && !routeViewerRoute.getLongName().equals(routeViewerRoute.getShortName())) {
                    routeName = routeViewerRoute.getLongName();
                }

                if (routeNumber != null && routeName != null) {
                    tvRoute.setText(routeNumber);
                    tvRouteName.setText(routeName);
                } else {
                    if (routeNumber != null) {
                        tvRoute.setText(routeNumber);
                    } else if (routeName != null) {
                        tvRoute.setText(routeName);
                    }
                    if (tvRoute.getText().toString().contains(getString(R.string.route))) {
                        tvRouteName.setText(tvRoute.getText().toString());
                    } else {
                        tvRouteName.setText(String.format("%s %s", getString(R.string.route), tvRoute.getText().toString()));
                    }

                }

                String routeColorString = routeViewerRoute.getColor();
                int routeColor;

                if (routeColorString == null) {
                    routeColor = getResources().getColor(R.color.defaultRouteBackground);
                } else {
                    if (!routeColorString.startsWith("#")) {
                        routeColorString = "#" + routeColorString;
                    }

                    routeColor = Color.parseColor(routeColorString);
                }

                tvRoute.setBackgroundTintList(ColorStateList.valueOf(routeColor));

                boolean isBlackTextColor = Utilities.isContrastYIQBlack(routeColorString);
                if (isBlackTextColor) {
                    tvRoute.setTextColor(getResources().getColor(android.R.color.black));
                } else {
                    tvRoute.setTextColor(getResources().getColor(android.R.color.white));
                }

                tvRouteDescription.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String description = ((TextView) v).getText().toString();
                        String url = (String) v.getTag();
                        if (url != null && !description.equals(getString(R.string.no_route_url_provided))) {
                            Utilities.GotoTheUrl(v.getContext(), url);
                        }
                    }
                });

                if (routeViewerRoute.getAgencyName() != null) {
                    String agencyName = routeViewerRoute.getAgencyName().toLowerCase().replaceAll(" ", "_");
                    if (Utilities.agencyLogoIconMapping.containsKey(agencyName)) {
                        ivLogo.setDefaultImageResId(Utilities.agencyLogoIconMapping.get(agencyName));
                    } else {
                        ivLogo.setDefaultImageResId(R.drawable.ic_default_agency_logo);
                    }
                    String logoUrl = Resource.ROUTE_AGENCY_LOGO_BASE_URL + agencyName;
                    ivLogo.setImageUrl(logoUrl, MyApplication.getInstance().getImageLoader());
                    ivLogo.setVisibility(View.VISIBLE);
                    ivLogo.setContentDescription(routeViewerRoute.getAgencyName().toLowerCase());
                } else {
                    ivLogo.setVisibility(View.GONE);
                }
                if (position == selectedRouteViewerRouteIndex) {
                    tvRouteDescription.setVisibility(View.VISIBLE);
                    convertView.setBackgroundResource(R.drawable.routes_viewer_selected_bg);

                    setupRouteDirectionsStopsView(convertView, position, routeColor);

                    String url = null;
                    String agencyName = null;
                    if (currentRouteViewerRouteDetail != null) {
                        if (currentRouteViewerRouteDetail.getUrl() != null) {
                            url = currentRouteViewerRouteDetail.getUrl();
                        } else if (currentRouteViewerRouteDetail.getAgency() != null
                                && currentRouteViewerRouteDetail.getAgency().getUrl() != null) {
                            url = currentRouteViewerRouteDetail.getAgency().getUrl();
                        }

                        if (currentRouteViewerRouteDetail.getAgency() != null &&
                                currentRouteViewerRouteDetail.getAgency().getName() != null) {
                            agencyName = currentRouteViewerRouteDetail.getAgency().getName();
                        }
                    }

                    if (agencyName != null) {
                        String mappedAgencyName = Utilities.getMappedAgencyNameForItinerary(systemContext, agencyName);

                        tvRouteAgencyInfo.setText(systemContext.getString(R.string.route_agency_info, mappedAgencyName));
                        tvRouteAgencyInfo.setVisibility(View.VISIBLE);
                    } else {
                        tvRouteAgencyInfo.setVisibility(View.GONE);
                    }

                    if (url != null) {
                        tvRouteDescription.setTag(url);
                        tvRouteDescription.setText(systemContext.getString(R.string.view_on_agency_website));
                        tvRouteDescription.setTextColor(getResources().getColor(R.color.colorMoreDetailsLinkBlue));
                    } else {
                        tvRouteDescription.setTextColor(getResources().getColor(android.R.color.black));
                        tvRouteDescription.setText(systemContext.getString(R.string.no_route_url_provided));
                    }
                } else {
                    tvRouteDescription.setVisibility(View.GONE);
                    tvRouteAgencyInfo.setVisibility(View.GONE);
                    convertView.setBackground(null);

                    LinearLayout llRouteDirectionsStops = convertView.findViewById(R.id.ll_route_directions_stops);
                    llRouteDirectionsStops.setVisibility(View.GONE);
                }

                if (routeViewerRoute.getMode() != null) {
                    int resID = Utilities.getModeImageByMode(routeViewerRoute.getMode().toLowerCase(Locale.ROOT), false, requireActivity(), ivRouteType, ivRouteType);

                    ivRouteType.setImageResource(resID);
                } else {
                    ivRouteType.setImageResource(R.drawable.ic_bus);
                }
                convertView.setTag(position);
                convertView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int position = (Integer) v.getTag();
                        if (selectedRouteViewerRouteIndex != position) {
                            setSelection(position);
                            rlCollapsed.setVisibility(View.VISIBLE);
                            rlSearchBar.setVisibility(View.GONE);
                        }
                    }
                });
            }
            return convertView;
        }
    }

    private void setRouteViewerHeight(int dy) {
        if (llRouteViewer != null && getActivity() != null) {
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) llRouteViewer.getLayoutParams();
            layoutParams.width = gDm.widthPixels;

            int minHeight = Convert.dpToPx(Constants.ROUTES_MIN_HEIGHT);
            int maxHeight = gDm.heightPixels - Convert.dpToPx(Constants.ROUTES_MAX_HEIGHT);

            if (currentHeight - dy > maxHeight) {
                layoutParams.height = maxHeight;
            } else layoutParams.height = Math.max(currentHeight - dy, minHeight);

            llRouteViewer.setLayoutParams(layoutParams);
        }
    }

    private void setupRouteViewer() {
        if (mFragmentView != null) {
            llRouteViewer = mFragmentView.findViewById(R.id.ll_route_viewer);
            lvRouteViewerRoutes = mFragmentView.findViewById(R.id.lv_route_viewer_routes);
            routeViewerRouteListAdapter = getRouteViewerRouteListAdapter();
            lvRouteViewerRoutes.setAdapter(routeViewerRouteListAdapter);

            ivRoutesViewerExpander = mFragmentView.findViewById(R.id.iv_routes_viewer_expander);

            ivRoutesViewerExpander.setOnTouchListener((v, event) -> {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        currentY = (int) event.getRawY();
                        break;

                    case MotionEvent.ACTION_MOVE: {
                        int dy = (int) event.getRawY() - currentY;

                        setRouteViewerHeight(dy);
                    }
                    break;

                    case MotionEvent.ACTION_UP:
                        if (llRouteViewer != null) {

                            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) llRouteViewer.getLayoutParams();

                            int dy = (int) event.getRawY() - currentY;
                            int minHeight = Convert.dpToPx(Constants.ROUTES_MIN_HEIGHT);
                            int maxHeight = gDm.heightPixels - Convert.dpToPx(Constants.ROUTES_MAX_HEIGHT);

                            int endHeight = currentHeight - dy;
                            if (endHeight > (maxHeight + minHeight) / 2) {
                                if (endHeight > (maxHeight * 3 / 4 + minHeight / 4)) {
                                    layoutParams.height = maxHeight;
                                    ivRoutesViewerExpander.setImageResource(R.drawable.btn_arrow_down);
                                } else {
                                    layoutParams.height = (maxHeight + minHeight) / 2;
                                    ivRoutesViewerExpander.setImageResource(R.drawable.btn_arrow_up);
                                }
                            } else {
                                if (endHeight > (minHeight * 3 / 4 + maxHeight / 4)) {
                                    layoutParams.height = (maxHeight + minHeight) / 2;
                                    ivRoutesViewerExpander.setImageResource(R.drawable.btn_arrow_up);
                                } else {
                                    layoutParams.height = minHeight;
                                    ivRoutesViewerExpander.setImageResource(R.drawable.btn_arrow_up);
                                }
                            }
                            llRouteViewer.setLayoutParams(layoutParams);
                            currentHeight = layoutParams.height;
                        }
                        moveMapToShowRouteOnRouteViewer();
                        break;
                }
                return true;
            });

            initRouteViewerHeight();
        }
    }

    private void initRouteViewerHeight() {
        gDm = new DisplayMetrics();
        if (getActivity() != null) {
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(gDm);
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) llRouteViewer.getLayoutParams();
            layoutParams.width = gDm.widthPixels;
            layoutParams.height = (gDm.heightPixels - Convert.dpToPx(102)) / 2;
            currentHeight = layoutParams.height;
            llRouteViewer.setLayoutParams(layoutParams);
        }
    }

    private void setupSearchARoute() {
        if (mFragmentView != null) {
            tvSearchARoute = mFragmentView.findViewById(R.id.tv_search_a_route);

            tvSearchARoute.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    gotoRouteViewerFilterPage();
                }
            });

            tvSearchARoute.setContentDescription(getString(R.string.search_for_a_route_text_field));
        }

    }

    private void gotoRouteViewerFilterPage() {
        if (getActivity() != null) {
            Intent intent = new Intent(getActivity(), RouteViewerFilterActivity.class);
            String selectedAgency = null;
            String selectedMode = null;
            if (selectedRouteFilterAgencyIndex < routeFilterAgencies.size()) {
                selectedAgency = routeFilterAgencies.get(selectedRouteFilterAgencyIndex);
            }

            if (selectedRouteFilterModeIndex < routeFilterModes.size()) {
                selectedMode = routeFilterModes.get(selectedRouteFilterModeIndex);
            }

            if (selectedAgency == null) {
                selectedAgency = getString(R.string.all_agencies);
            }
            if (selectedMode == null) {
                selectedMode = getString(R.string.all_modes);
            }
            intent.putExtra(Constants.ROUTE_FILTER_AGENCY_NAME, selectedAgency);
            intent.putExtra(Constants.ROUTE_FILTER_MODE_NAME, selectedMode);
            startActivityForResult(intent, Constants.ROUTE_VIEWER_FILTER_REQUEST_CODE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Constants.ROUTE_VIEWER_FILTER_REQUEST_CODE) {
            selectedRouteFilterAgencyIndex = Configurations.getInt(Constants.ROUTE_FILTER_AGENCY_KEY, 0);
            selectedRouteFilterModeIndex = Configurations.getInt(Constants.ROUTE_FILTER_MODE_KEY, 0);

            if (selectedRouteFilterAgencyIndex == 0 && selectedRouteFilterModeIndex == 0) {
                iv_filter_changed.setVisibility(View.GONE);
            } else {
                iv_filter_changed.setVisibility(View.VISIBLE);
            }

            refreshFilterModes();
            refreshRouteViewerRoutes();
        }

        if (resultCode == RESULT_OK) {
            if (requestCode == Constants.ROUTE_VIEWER_FILTER_REQUEST_CODE || requestCode == Constants.TRIP_VIEWER_REQUEST_CODE) {
                if (data != null) {
                    RouteViewerRoute currentRouteViewerRoute = (RouteViewerRoute) data.getSerializableExtra(Constants.SELECTED_ROUTE);
                    if (currentRouteViewerRoute != null) {
                        for (int i = 0; i < routeViewerRoutes.size(); i++) {
                            RouteViewerRoute routeViewerRoute = routeViewerRoutes.get(i);
                            if (routeViewerRoute != null && routeViewerRoute.getId().equals(currentRouteViewerRoute.getId())) {
                                setSelection(i);

                                rlCollapsed.setVisibility(View.VISIBLE);
                                rlSearchBar.setVisibility(View.GONE);
                                break;
                            }
                        }
                    }
                }
            }
        }
    }

    private void setupExpandLayouts() {
        if (mFragmentView != null) {
            rlCollapsed = mFragmentView.findViewById(R.id.rl_collapsed);
            ivExpand = mFragmentView.findViewById(R.id.iv_expand);
            ivCollapse = mFragmentView.findViewById(R.id.iv_collapse);
            ivCollapsedBack = mFragmentView.findViewById(R.id.iv_collapsed_back);
            rlSearchBar = mFragmentView.findViewById(R.id.rl_search_bar);

            ivExpand.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    rlCollapsed.setVisibility(View.GONE);
                    rlSearchBar.setVisibility(View.VISIBLE);
                    ivCollapse.setVisibility(View.VISIBLE);
                }
            });

            ivCollapsedBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    rlCollapsed.setVisibility(View.GONE);
                    rlSearchBar.setVisibility(View.VISIBLE);
                    ivCollapse.setVisibility(View.VISIBLE);
                }
            });

            ivCollapse.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    rlCollapsed.setVisibility(View.VISIBLE);
                    rlSearchBar.setVisibility(View.GONE);
                    ivCollapse.setVisibility(View.GONE);
                }
            });
        }
    }

    private void setSelection(int position) {
        if (position < routeViewerRoutes.size()) {
            currentRouteViewerRouteDetail = null;
            selectedRouteViewerRouteIndex = position;

            currentRouteViewerRoute = routeViewerRoutes.get(position);
            if (customView != null) {
                customView.setVisibility(View.GONE);
            }

            new Handler(Looper.getMainLooper()).post(() -> {
                for (ViewPager2 viewPager2 : vpRouteDirectionList) {
                    if (viewPager2 != null && viewPager2.getAdapter() != null) {
                        RouteDirectionAdapter routeDirectionAdapter = (RouteDirectionAdapter) viewPager2.getAdapter();
                        routeDirectionAdapter.notifyItemRangeRemoved(0, routeDirectionAdapter.getItemCount());
                    }
                }
                if (routeViewerRouteListAdapter != null) {
                    routeViewerRouteListAdapter.notifyDataSetChanged();
                }
                if (lvRouteViewerRoutes != null && lvRouteViewerRoutes.getCount() >= position) {
                    lvRouteViewerRoutes.setSelection(position);
                }

                routeViewerPatterns.clear();
                routeStops.clear();
                titleList.clear();
                routeDirectionStopsListViews.clear();
                routeDirectionStopsListAdapters.clear();
                gGridViewList.clear();
                vpRouteDirectionList.clear();
                currentRouteDirectionIndex = 0xFF;

                if (gRunnable == null) {
                    gRunnable = new Runnable() {
                        @Override
                        public void run() {
                            getRouteViewerVehicles();
                            gHandler.postDelayed(gRunnable,
                                    Configurations.getInt("realtime_businfo_refresh_interval", 30) * 1000L);
                        }
                    };
                }

                getRouteViewerVehiclesHandler();

                getRouteViewerRouteDetail();
            });
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (gRunnable != null) {
            gHandler.removeCallbacks(gRunnable);
        }
    }

    private void getRouteViewerVehiclesHandler() {
        gHandler.removeCallbacks(gRunnable);
        if (getRouteViewerVehiclesRequest != null) {
            getRouteViewerVehiclesRequest.cancel();
            getRouteViewerVehiclesRequest = null;
        }

        if (routeViewerVehiclesQueryCall != null) {
            routeViewerVehiclesQueryCall.cancel();
            routeViewerVehiclesQueryCall = null;
        }
        getRouteViewerVehicles();
        gHandler.postDelayed(gRunnable,
                Configurations.getInt("realtime_businfo_refresh_interval", 30) * 1000L);
    }

    private void refreshRouteViewerRoutes() {
        routeViewerRoutes.clear();
        if (selectedRouteFilterAgencyIndex == 0 && selectedRouteFilterModeIndex == 0) {
            routeViewerRoutes.addAll(routeViewerAllRoutes);
        } else {
            String selectedAgency = null;
            String selectedMode = null;
            if (selectedRouteFilterAgencyIndex < routeFilterAgencies.size()) {
                selectedAgency = routeFilterAgencies.get(selectedRouteFilterAgencyIndex);
            }

            if (selectedRouteFilterModeIndex < routeFilterModes.size()) {
                selectedMode = routeFilterModes.get(selectedRouteFilterModeIndex);
            }

            if (selectedAgency != null && selectedMode != null) {
                for (RouteViewerRoute routeViewerRoute : routeViewerAllRoutes) {
                    if ((selectedRouteFilterAgencyIndex == 0 || selectedAgency.equals(routeViewerRoute.getAgencyName())) &&
                            (selectedRouteFilterModeIndex == 0 || selectedMode.equals(routeViewerRoute.getMode()))) {
                        routeViewerRoutes.add(routeViewerRoute);
                    }
                }
            }
        }

        new Handler(Looper.getMainLooper()).post(() -> {
            if (routeViewerRouteListAdapter != null) {
                routeViewerRouteListAdapter.notifyDataSetChanged();
            }
        });
    }

    public void notifyDataChanged() {
        routeViewerAllRoutes = MyApplication.getInstance().routeViewerRoutes;
        initFilterAgencies();
        refreshFilterModes();
        refreshRouteViewerRoutes();
    }

    private void refreshMapStyleControllers() {
        if (Configurations.getBoolean("map_style_streets", true)) {
            tvStreetsHighlight.setVisibility(View.VISIBLE);
            tvSatelliteHighlight.setVisibility(View.INVISIBLE);
            tvSatelliteHighlightAccessibility.setVisibility(View.INVISIBLE);
        } else {
            tvStreetsHighlight.setVisibility(View.INVISIBLE);
            tvSatelliteHighlight.setVisibility(View.VISIBLE);
            tvSatelliteHighlightAccessibility.setVisibility(View.VISIBLE);
        }
    }

    private void refreshParkRideLocationControllers() {
        if (Configurations.getBoolean("enable_layer_park_ride_locations", false)) {
            llParkRideLocations.setBackgroundResource(R.drawable.grey_frame_rounded_transparent_bg);
            llParkRideLocationsAccessibility.setBackgroundResource(R.drawable.grey_frame_rounded_transparent_bg);
            loadParkRideLocationsOnMap();
        } else {
            llParkRideLocations.setBackground(null);
            llParkRideLocationsAccessibility.setBackground(null);
            removeParkRideLocationsFromMap();
        }
    }

    private void refreshTransitStopsControllers() {
        if (Configurations.getBoolean("enable_layer_transit_stops", true)) {
            llTransitStops.setBackgroundResource(R.drawable.grey_frame_rounded_transparent_bg);
            llTransitStopsAccessibility.setBackgroundResource(R.drawable.grey_frame_rounded_transparent_bg);
            loadTransitStopsOnMap();
        } else {
            llTransitStops.setBackground(null);
            llTransitStopsAccessibility.setBackground(null);
            removeTransitStopsFromMap();
        }
    }

    private void initLayerTextViews() {
        TextView tvStreets = mFragmentView.findViewById(R.id.tv_streets);
        TextView tvSatellite = mFragmentView.findViewById(R.id.tv_satellite);
        TextView tvParkRideLocations = mFragmentView.findViewById(R.id.tv_park_and_ride_locations);
        TextView tvTransitStops = mFragmentView.findViewById(R.id.tv_transit_stops);

        Utilities.setFont(systemContext, tvStreets, true);
        Utilities.setFont(systemContext, tvSatellite, true);
        Utilities.setFont(systemContext, tvParkRideLocations, true);
        Utilities.setFont(systemContext, tvTransitStops, true);
    }

    private FeatureCollection generateRentalVehicleFeatureData() {
        Gson gson = new Gson();
        ArrayList<Feature> featureList = new ArrayList<Feature>();
        if (MyApplication.getInstance().rentalVehicles != null) {
            ArrayList<RentalVehicle> rentalVehicles = MyApplication.getInstance().rentalVehicles;
            for (RentalVehicle rentalVehicle : rentalVehicles) {

                Feature feature = Feature.fromGeometry(Point.fromLngLat(
                        rentalVehicle.getLon(),
                        rentalVehicle.getLat()
                ));

                feature.addNumberProperty("typeId", Resource.ID_RENTAL_VEHICLE);
                feature.addStringProperty("jsonString", gson.toJson(rentalVehicle));

                if (rentalVehicle.getVehicleType() != null && "SCOOTER".equalsIgnoreCase(rentalVehicle.getVehicleType().getFormFactor())) {
                    feature.addNumberProperty("vehicleTypeId", Resource.ID_E_SCOOTER);
                } else {
                    feature.addNumberProperty("vehicleTypeId", Resource.ID_SHARED_BIKE);
                }

                featureList.add(feature);
            }
        }
        return FeatureCollection.fromFeatures(featureList);
    }

    private void removeRentalVehiclesFromMap() {
        if (rentalVehicleSource != null) {
            gMapStyle.removeLayer("unclustered_rental_vehicle_points");
            gMapStyle.removeSource("rental_vehicle_list");
            rentalVehicleSource = null;
            unclusteredRentalVehicle = null;
        }
    }

    private void loadRentalVehiclesOnMap() {
        if (gMapStyle != null) {
            gMapStyle.addImage("e_scooter_icon_id", BitmapUtils.getBitmapFromDrawable(
                    systemContext.getResources().getDrawable(R.drawable.ic_e_scooter_marker)));
            gMapStyle.addImage("shared_bike_icon_id", BitmapUtils.getBitmapFromDrawable(
                    systemContext.getResources().getDrawable(R.drawable.ic_shared_bike_marker)));

            FeatureCollection features = generateRentalVehicleFeatureData();

            removeRentalVehiclesFromMap();

            rentalVehicleSource = new GeoJsonSource("rental_vehicle_list",
                    features,
                    new GeoJsonOptions()
                            .withCluster(false)
            );

            gMapStyle.addSource(rentalVehicleSource);

            //Creating a marker layer for single data point
            unclusteredRentalVehicle = new SymbolLayer("unclustered_rental_vehicle_points", "rental_vehicle_list");

            if (gMapboxMap.getCameraPosition().zoom >= Constants.TRANSIT_STOP_MARKER_MIN_ZOOM_LEVEL) {
                unclusteredRentalVehicle.setProperties(
                        iconImage(
                                Expression.match(get("vehicleTypeId"),
                                        literal(Resource.ID_E_SCOOTER), literal("e_scooter_icon_id"),
                                        literal("shared_bike_icon_id")
                                )
                        ),
                        symbolZOrder(Property.SYMBOL_Z_ORDER_SOURCE),
                        iconAllowOverlap(true),
                        iconIgnorePlacement(true),
                        visibility(VISIBLE)
                );
            } else {
                unclusteredRentalVehicle.setProperties(
                        iconImage(
                                Expression.match(get("vehicleTypeId"),
                                        literal(Resource.ID_E_SCOOTER), literal("e_scooter_icon_id"),
                                        literal("shared_bike_icon_id")
                                )
                        ),
                        symbolZOrder(Property.SYMBOL_Z_ORDER_SOURCE),
                        iconAllowOverlap(true),
                        iconIgnorePlacement(true),
                        visibility(NONE)
                );
            }

            unclusteredRentalVehicle.setFilter(Expression.all(Expression.eq(get("typeId"), Resource.ID_RENTAL_VEHICLE), not(has("cluster"))));
            gMapStyle.addLayer(unclusteredRentalVehicle);
        }
    }

    public void refreshRentalVehiclesOnMap() {
        if (Configurations.getBoolean("enable_layer_rental_vehicles", false)) {
            loadRentalVehiclesOnMap();
        }
    }

    private void refreshRentalVehiclesControllers() {
        if (Configurations.getBoolean("enable_layer_rental_vehicles", false)) {
            llRentalVehicles.setBackgroundResource(R.drawable.grey_frame_rounded_transparent_bg);
            llRentalVehiclesAccessibility.setBackgroundResource(R.drawable.grey_frame_rounded_transparent_bg);
            loadRentalVehiclesOnMap();
        } else {
            llRentalVehicles.setBackground(null);
            llRentalVehiclesAccessibility.setBackground(null);
            removeRentalVehiclesFromMap();
        }
    }

    private void setupLayers() {
        RelativeLayout rlLayer = mFragmentView.findViewById(R.id.rl_layer);
        ivLayer = mFragmentView.findViewById(R.id.iv_layer);
        ivLayerBg = mFragmentView.findViewById(R.id.iv_layer_bg);
        
        tvStreetsHighlight = mFragmentView.findViewById(R.id.tv_streets_highlight);
        tvSatelliteHighlight = mFragmentView.findViewById(R.id.tv_satellite_highlight);

        llLayoutLayers = mFragmentView.findViewById(R.id.ll_layout_layers);
        llStreets = mFragmentView.findViewById(R.id.ll_streets);
        llSatellite = mFragmentView.findViewById(R.id.ll_satellite);
        llParkRideLocations = mFragmentView.findViewById(R.id.ll_park_and_ride_locations);
        llTransitStops = mFragmentView.findViewById(R.id.ll_transit_stops);

        llRentalVehicles = mFragmentView.findViewById(R.id.ll_rental_vehicles);
        llRentalVehiclesAccessibility = mFragmentView.findViewById(R.id.ll_rental_vehicles_accessibility);

        ImageView ivLayerClose = mFragmentView.findViewById(R.id.iv_layer_close);
        llSatelliteAccessibility = mFragmentView.findViewById(R.id.ll_satellite_accessibility);
        llParkRideLocationsAccessibility = mFragmentView.findViewById(R.id.ll_park_and_ride_locations_accessibility);
        llTransitStopsAccessibility = mFragmentView.findViewById(R.id.ll_transit_stops_accessibility);
        tvSatelliteHighlightAccessibility = mFragmentView.findViewById(R.id.tv_satellite_highlight_accessibility);


        ivLayerClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideLayerPopup();
            }
        });

        if (Utilities.checkIfToDisplayAccessibilityUI(requireActivity())) {
            llParkRideLocationsAccessibility.setVisibility(View.VISIBLE);
            llSatelliteAccessibility.setVisibility(View.VISIBLE);
            llTransitStopsAccessibility.setVisibility(View.VISIBLE);

            llParkRideLocations.setVisibility(View.GONE);
            llSatellite.setVisibility(View.GONE);
            llTransitStops.setVisibility(View.GONE);

            llRentalVehicles.setVisibility(View.GONE);
            llRentalVehiclesAccessibility.setVisibility(View.VISIBLE);
        } else {

            llParkRideLocationsAccessibility.setVisibility(View.GONE);
            llSatelliteAccessibility.setVisibility(View.GONE);
            llTransitStopsAccessibility.setVisibility(View.GONE);

            llParkRideLocations.setVisibility(View.VISIBLE);
            llSatellite.setVisibility(View.VISIBLE);
            llTransitStops.setVisibility(View.VISIBLE);

            llRentalVehicles.setVisibility(View.VISIBLE);
            llRentalVehiclesAccessibility.setVisibility(View.GONE);
        }
        
        initLayerTextViews();

        refreshMapStyleControllers();
        refreshParkRideLocationControllers();
        refreshTransitStopsControllers();
        refreshRentalVehiclesControllers();
        rlLayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (llLayoutLayers.getVisibility() == View.VISIBLE) {
                    hideLayerPopup();
                } else {
                    llLayoutLayers.setVisibility(View.VISIBLE);

                    ivLayer.setColorFilter(getResources().getColor(android.R.color.white));
                    ivLayerBg.setImageResource(R.drawable.ic_layer_bkg_on);
                    ivLayerBg.setColorFilter(getResources().getColor(R.color.colorPrimary));
                    llRouteViewer.setVisibility(View.GONE);
                }
            }
        });

        llStreets.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Configurations.setBoolean("map_style_streets", true);
                refreshMapStyleControllers();
                refreshMapStyle();
            }
        });

        llSatellite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Configurations.setBoolean("map_style_streets", false);
                refreshMapStyleControllers();
                refreshMapStyle();
            }
        });

        llSatelliteAccessibility.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Configurations.setBoolean("map_style_streets", false);
                refreshMapStyleControllers();
                refreshMapStyle();
            }
        });

        llParkRideLocations.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Configurations.setBoolean("enable_layer_park_ride_locations",
                        !Configurations.getBoolean("enable_layer_park_ride_locations", false));

                refreshParkRideLocationControllers();
            }
        });

        llParkRideLocationsAccessibility.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Configurations.setBoolean("enable_layer_park_ride_locations",
                        !Configurations.getBoolean("enable_layer_park_ride_locations", false));

                refreshParkRideLocationControllers();
            }
        });

        llTransitStops.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Configurations.setBoolean("enable_layer_transit_stops",
                        !Configurations.getBoolean("enable_layer_transit_stops", true));

                refreshTransitStopsControllers();
            }
        });

        llTransitStopsAccessibility.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Configurations.setBoolean("enable_layer_transit_stops",
                        !Configurations.getBoolean("enable_layer_transit_stops", true));

                refreshTransitStopsControllers();
            }
        });

        View.OnClickListener rentalVehicleOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Configurations.setBoolean("enable_layer_rental_vehicles",
                        !Configurations.getBoolean("enable_layer_rental_vehicles", false));

                refreshRentalVehiclesControllers();
            }
        };
        llRentalVehicles.setOnClickListener(rentalVehicleOnClickListener);
        llRentalVehiclesAccessibility.setOnClickListener(rentalVehicleOnClickListener);
    }

    private void hideLayerPopup() {
        llLayoutLayers.setVisibility(View.GONE);

        ivLayer.setColorFilter(null);
        ivLayerBg.setImageResource(R.drawable.ic_layer_bkg_default);
        ivLayerBg.setColorFilter(null);
        llRouteViewer.setVisibility(View.VISIBLE);
    }

    @Override
    public void onPause() {
        super.onPause();

        hideLayerPopup();
    }

    private FeatureCollection generateParkRideFeatureData() {
        Gson gson = new Gson();
        ArrayList<Feature> featureList = new ArrayList<Feature>();
        if (MyApplication.getInstance().parkRideLocations != null) {
            ArrayList<ParkRideLocation> parkRideLocations = MyApplication.getInstance().parkRideLocations;
            for (ParkRideLocation parkRideLocation : parkRideLocations) {

                Feature feature = Feature.fromGeometry(Point.fromLngLat(
                        parkRideLocation.getLon(),
                        parkRideLocation.getLat()
                ));

                feature.addNumberProperty("typeId", Resource.ID_PARK_RIDE);
                feature.addStringProperty("jsonString", gson.toJson(parkRideLocation));

                featureList.add(feature);
            }
        }
        return FeatureCollection.fromFeatures(featureList);
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

    private void removeParkRideLocationsFromMap() {
        if (parkRideSource != null) {
            gMapStyle.removeLayer("unclustered_park_ride_points");
            gMapStyle.removeSource("park_ride_list");
            parkRideSource = null;
            unclusteredParkRide = null;
        }
    }

    private void loadParkRideLocationsOnMap() {
        if (gMapStyle != null) {
            gMapStyle.addImage("park_ride_icon_id", BitmapUtils.getBitmapFromDrawable(
                    systemContext.getResources().getDrawable(R.drawable.ic_parking_filled)));

            FeatureCollection features = generateParkRideFeatureData();

            removeParkRideLocationsFromMap();

            parkRideSource = new GeoJsonSource("park_ride_list",
                    features,
                    new GeoJsonOptions()
                            .withCluster(false)
            );

            gMapStyle.addSource(parkRideSource);

            //Creating a marker layer for single data point
            unclusteredParkRide = new SymbolLayer("unclustered_park_ride_points", "park_ride_list");

            unclusteredParkRide.setProperties(
                    iconImage("park_ride_icon_id"),
                    symbolZOrder(Property.SYMBOL_Z_ORDER_SOURCE),
                    iconAllowOverlap(true),
                    iconIgnorePlacement(true)
            );

            unclusteredParkRide.setFilter(Expression.all(Expression.eq(get("typeId"), Resource.ID_PARK_RIDE), not(has("cluster"))));

            int index = routeViewerRouteSources.size();
            String routeViewerRouteLayerID = "route_viewer_route_layer_" + index;
            if (gMapStyle.getLayer(routeViewerRouteLayerID) != null) {
                Logger.d("vp", "show route routeViewerRouteLayerID is exist, index:" + index);
                gMapStyle.addLayerBelow(unclusteredParkRide, routeViewerRouteLayerID);
            } else {
                Logger.d("vp", "show route routeViewerRouteLayerID is not exist");
                gMapStyle.addLayer(unclusteredParkRide);
            }
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

    private void loadTransitStopsOnMap() {
        if (isAdded() && gMapStyle != null && getActivity() != null) {
            getActivity().runOnUiThread(() -> {
                gMapStyle.addImage("transit_stop_icon_id", BitmapUtils.getBitmapFromDrawable(
                        systemContext.getResources().getDrawable(R.drawable.ic_place)));

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

                int index = routeViewerRouteSources.size();
                String routeViewerRouteLayerID = "route_viewer_route_layer_" + index;
                if (gMapStyle.getLayer(routeViewerRouteLayerID) != null) {
                    Logger.d("vp", "show route routeViewerRouteLayerID is exist, index:" + index);
                    gMapStyle.addLayerBelow(unclusteredTransitStop, routeViewerRouteLayerID);
                } else {
                    Logger.d("vp", "show route routeViewerRouteLayerID is not exist");
                    gMapStyle.addLayer(unclusteredTransitStop);
                }
            });
        }
    }

    private void setupAndAddFeatureListeners() {
        parkRideListener = new ParkRideManager.ParkRideListener() {
            @Override
            public void parkRideChanged() {
                if (Configurations.getBoolean("enable_layer_park_ride_locations", false)) {
                    loadParkRideLocationsOnMap();
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

        ParkRideManager.getInstance().start();
        TransitStopManager.getInstance().start();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    public class RouteDirectionAdapter extends FragmentStateAdapter {

        public RouteDirectionAdapter(@NonNull Fragment fragment) {
            super(fragment);
        }


        @NonNull
        @Override
        public Fragment createFragment(int position) {
            RouteDirectionFragment routeDirectionFragment = new RouteDirectionFragment();
            Bundle args = new Bundle();
            String title = titleList.get(position);
            args.putString(ROUTE_DIRECTION_TITLE, title);
            routeDirectionFragment.setArguments(args);
            return routeDirectionFragment;
        }

        @Override
        public int getItemCount() {
            return titleList.size();
        }

        @Override
        public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
            super.onAttachedToRecyclerView(recyclerView);
        }

    }

    // Instances of this class are fragments representing a single
    // object in our collection.
    public static class RouteDirectionFragment extends Fragment {
        View rootView = null;

        @Override
        public View onCreateView(LayoutInflater inflater,
                                 ViewGroup container, Bundle savedInstanceState) {
            // The last two arguments ensure LayoutParams are inflated
            // properly.
            rootView = inflater.inflate(
                    R.layout.item_route_direction, container, false);
            Bundle args = getArguments();

            if (args != null) {
                String title = args.getString(ROUTE_DIRECTION_TITLE, "");

                TextView tvTitle = rootView.findViewById(R.id.tv_title);

                tvTitle.setText(title);
            }

            return rootView;
        }

        @Nullable
        @Override
        public View getView() {
            if (rootView != null) {
                return rootView;
            }
            return super.getView();
        }
    }

    private GridViewAdapter getGridViewAdapter() {
        ArrayList<HashMap<String, Object>> data = new ArrayList<HashMap<String, Object>>();

        for (int i = 0; i < titleList.size(); i++) {
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("iv_dot", "");
            data.add(map);
        }

        GridViewAdapter gridViewAdapter = new GridViewAdapter(
                getActivity(), data,
                R.layout.item_grid_view,
                new String[]{"iv_dot"}, new int[]{R.id.iv_dot});

        return gridViewAdapter;
    }

    class GridViewAdapter extends SimpleAdapter {
        Context mContext;
        private final LayoutInflater l_Inflater;
        private int currentPageIndex = 0;

        public GridViewAdapter(Context context,
                               List<? extends Map<String, ?>> data, int resource,
                               String[] from, int[] to) {
            super(context, data, resource, from, to);

            mContext = context;
            l_Inflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return titleList.size();
        }

        public void setCurrentPageIndex(int index) {
            currentPageIndex = index;
        }

        @Override
        public View getView(int position, View convertView, final ViewGroup parent) {

            if (convertView == null) {
                convertView = l_Inflater.inflate(
                        R.layout.item_grid_view, null);
            }
            ImageView ivDot = convertView.findViewById(R.id.iv_dot);


            if (position == currentPageIndex) {
                ivDot.setAlpha(1.0f);
            } else {
                ivDot.setAlpha(0.3f);
            }

            ivDot.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    for (ViewPager2 viewPager2 : vpRouteDirectionList) {
                        if (viewPager2 != null) {
                            viewPager2.setCurrentItem(position);
                        }
                    }
                    currentPageIndex = position;
                }
            });

            return convertView;
        }
    }

    private void setupGridView(GridView gvSelectedStatus) {
        new Handler(Looper.getMainLooper()).post(() -> {
            GridViewAdapter gridViewAdapter;
            if (gvSelectedStatus != null) {
                gvSelectedStatus.setNumColumns(titleList.size());

                ViewGroup.LayoutParams layoutParams = gvSelectedStatus.getLayoutParams();

                int width = Convert.dpToPx(20) * titleList.size();
                if (width > (gDm.widthPixels - Convert.dpToPx(20))) {
                    width = gDm.widthPixels - Convert.dpToPx(20);
                }
                layoutParams.width = width;
                gvSelectedStatus.setLayoutParams(layoutParams);

                if (gvSelectedStatus.getAdapter() != null) {
                    gridViewAdapter = (GridViewAdapter) gvSelectedStatus.getAdapter();

                    if (gbNeedRefreshDirectionsAndStops) {
                        gridViewAdapter.setCurrentPageIndex(0);
                    }

                    if (gridViewAdapter != null) {
                        gridViewAdapter.notifyDataSetChanged();
                    }
                } else {
                    gridViewAdapter = getGridViewAdapter();
                    gvSelectedStatus.setAdapter(gridViewAdapter);
                    if (gbNeedRefreshDirectionsAndStops) {
                        gridViewAdapter.setCurrentPageIndex(0);
                    }
                }
            }
        });
    }

    private void setupRouteDirectionsStopsView(View convertView, int indexOnRoute, int routeColor) {
        if (convertView == null) {
            return;
        }
        ImageView ivRouteDirectionExpander;
        ViewPager2 vpRouteDirection;
        ListView lvRouteDirectionStops;
        RouteStopsListAdapter routeStopsListAdapter;
        RouteDirectionAdapter routeDirectionAdapter;
        GridView gvSelectedStatus;

        LinearLayout llRouteDirectionsStops = convertView.findViewById(R.id.ll_route_directions_stops);
        llRouteDirectionsStops.setVisibility(View.VISIBLE);

        ivRouteDirectionExpander = convertView.findViewById(R.id.iv_route_direction_expander);
        vpRouteDirection = convertView.findViewById(R.id.vp_route_direction);
        gvSelectedStatus = convertView.findViewById(R.id.gv_selected_status);
        lvRouteDirectionStops = convertView.findViewById(R.id.lv_route_direction_stops);

        if (vpRouteDirection.getAdapter() != null) {
            Logger.d("vp", "vp getAdapter() != null");
            routeDirectionAdapter = (RouteDirectionAdapter) vpRouteDirection.getAdapter();
            routeDirectionAdapter.notifyItemRangeRemoved(0, routeDirectionAdapter.getItemCount());
            routeDirectionAdapter.notifyItemRangeInserted(0, titleList.size());
        } else {
            Logger.d("vp", "vp getAdapter() == null");
            routeDirectionAdapter = new RouteDirectionAdapter(this);
            vpRouteDirection.setAdapter(routeDirectionAdapter);
            //routeDirectionAdapter.notifyDataSetChanged();
        }

        setupGridView(gvSelectedStatus);

        routeStopsListAdapter = getRouteStopsListAdapter(routeColor);
        lvRouteDirectionStops.setAdapter(routeStopsListAdapter);

        Utilities.setTotalHeightToListView(lvRouteDirectionStops);

        vpRouteDirection.setTag(indexOnRoute);
        vpRouteDirection.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                if (position < 0) {
                    return;
                }
                new Handler(Looper.getMainLooper()).post(() -> {
                    gbNeedRefreshDirectionsAndStops = false;
                    int indexOnRoute = (int) (vpRouteDirection.getTag());

                    if (indexOnRoute == selectedRouteViewerRouteIndex && position != currentRouteDirectionIndex
                            || gbNeedRefreshDirectionsAndStops) {
                        for (GridView gridView : gGridViewList) {
                            if (gridView != null && gridView.getAdapter() != null) {
                                GridViewAdapter gridViewAdapter = (GridViewAdapter) gridView.getAdapter();
                                gridViewAdapter.setCurrentPageIndex(position);
                                if (gridViewAdapter != null) {
                                    gridViewAdapter.notifyDataSetChanged();
                                }
                            }
                        }

                        if (position < routeViewerPatterns.size() && routeViewerPatterns.get(position) != null
                                && routeViewerPatterns.get(position).getId() != null) {
                            removeRouteGeometryFromMapIfNeeded();
                            RouteViewerPattern routeViewerPattern = routeViewerPatterns.get(position);
                            if (routeViewerPattern.getPatternGeometry() != null) {
                                Logger.d("vp", "show route when switch page");
                                showRouteGeometryOnMap(routeViewerPattern.getPatternGeometry().getPoints());
                                moveMapToShowRouteOnRouteViewer();
                            } else {
                                getRouteViewerRouteGeometry(routeViewerPattern.getId());
                            }

                            removeRouteDirectionStopsFromMap();

                            refreshRouteStopsListView(routeViewerPattern);
                        }

                        refreshVehiclesOnRouteExt(position);
                        loadVehiclesOnMap();
                    }

                    currentRouteDirectionIndex = position;
                });
            }
        });

        if (gbNeedRefreshDirectionsAndStops) {
            Logger.d("vp", "setCurrentItem(0), currentRouteDirectionIndex:" + currentRouteDirectionIndex);
            vpRouteDirection.setCurrentItem(0);
        }

        if (!vpRouteDirectionList.contains(vpRouteDirection)) {
            vpRouteDirectionList.add(vpRouteDirection);
        }

        if (!gGridViewList.contains(gvSelectedStatus)) {
            gGridViewList.add(gvSelectedStatus);
        }
        if (!routeDirectionStopsListViews.contains(lvRouteDirectionStops)) {
            routeDirectionStopsListViews.add(lvRouteDirectionStops);
        }

        if (!routeDirectionStopsListAdapters.contains(routeStopsListAdapter)) {
            routeDirectionStopsListAdapters.add(routeStopsListAdapter);
        }

        if (gbShowDirectionStops) {
            lvRouteDirectionStops.setVisibility(View.VISIBLE);
            ivRouteDirectionExpander.setImageResource(R.drawable.ic_up);
            ivRouteDirectionExpander.setContentDescription(getString(R.string.collapse));
        } else {
            lvRouteDirectionStops.setVisibility(View.GONE);
            ivRouteDirectionExpander.setImageResource(R.drawable.ic_down);
            ivRouteDirectionExpander.setContentDescription(getString(R.string.expand));
        }
        ivRouteDirectionExpander.setTag(lvRouteDirectionStops);
        ivRouteDirectionExpander.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ListView lvRouteDirectionStops = (ListView) v.getTag();
                gbShowDirectionStops = !gbShowDirectionStops;
                if (gbShowDirectionStops) {
                    lvRouteDirectionStops.setVisibility(View.VISIBLE);
                    ((ImageView) v).setImageResource(R.drawable.ic_up);
                    v.setContentDescription(getString(R.string.collapse));

                } else {
                    lvRouteDirectionStops.setVisibility(View.GONE);
                    ((ImageView) v).setImageResource(R.drawable.ic_down);
                    v.setContentDescription(getString(R.string.expand));
                }
                refreshVehiclesOnRouteExt(currentRouteDirectionIndex);
                loadVehiclesOnMap();
            }
        });
    }

    private void refreshRouteStopsListView(RouteViewerPattern routeViewerPattern) {
        if (routeViewerPattern.getStops() != null) {
            routeStops = routeViewerPattern.getStops();
        } else {
            routeStops = new ArrayList<TransitStop>();
        }

        if (currentRouteViewerRoute != null) {
            String routeColorString = currentRouteViewerRoute.getColor();
            int routeColor;

            if (routeColorString == null) {
                routeColor = getResources().getColor(R.color.defaultRouteBackground);
            } else {
                if (!routeColorString.startsWith("#")) {
                    routeColorString = "#" + routeColorString;
                }

                routeColor = Color.parseColor(routeColorString);
            }

            for (TransitStop transitStop : routeStops) {
                transitStop.setRouteColor(routeColor);
            }
        }

        new Handler(Looper.getMainLooper()).post(() -> {
            for (RouteStopsListAdapter routeStopsListAdapter : routeDirectionStopsListAdapters) {
                if (routeStopsListAdapter != null) {
                    routeStopsListAdapter.notifyDataSetChanged();
                }
            }

            for (ListView lvRouteDirectionStops : routeDirectionStopsListViews) {
                if (lvRouteDirectionStops != null) {
                    Utilities.setTotalHeightToListView(lvRouteDirectionStops);
                }
            }
            loadRouteDirectionStopsOnMap();
        });
    }

    private RouteStopsListAdapter getRouteStopsListAdapter(int routeColor) {
        ArrayList<HashMap<String, Object>> data = new ArrayList<HashMap<String, Object>>();

        for (int i = 0; i < routeStops.size(); i++) {
            TransitStop routeStop = routeStops.get(i);

            HashMap<String, Object> map = new HashMap<String, Object>();

            map.put("tv_stop_name", routeStop.getName());
            data.add(map);
        }

        RouteStopsListAdapter mTripViewerStopsListAdapter = new RouteStopsListAdapter(
                systemContext, data,
                R.layout.item_route_direction_stop,
                new String[]{"tv_stop_name"}, new int[]{R.id.tv_stop_name}, routeColor);

        return mTripViewerStopsListAdapter;
    }

    class RouteStopsListAdapter extends SimpleAdapter {
        Context mContext;
        private final LayoutInflater l_Inflater;
        private final int routeColor;

        public RouteStopsListAdapter(Context context, List<? extends Map<String, ?>> data,
                                     int resource, String[] from, int[] to, int routeColor) {
            super(context, data, resource, from, to);

            mContext = context;
            l_Inflater = LayoutInflater.from(context);
            this.routeColor = routeColor;
        }

        @Override
        public int getCount() {
            return routeStops.size();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TransitStop routeStop = routeStops.get(position);

            if (convertView == null) {
                convertView = l_Inflater.inflate(
                        R.layout.item_route_direction_stop, null);
            }

            if (routeStop != null) {
                TextView tvStopName = convertView.findViewById(R.id.tv_stop_name);
                ImageView ivStopPosition = convertView.findViewById(R.id.iv_stop_position);
                ImageView ivBottomLine = convertView.findViewById(R.id.iv_bottom_line);

                ivStopPosition.setBackgroundResource(R.drawable.ring_bg);

                if (routeStop.getRouteColor() != null) {
                    ivStopPosition.setBackgroundTintList(ColorStateList.valueOf(routeStop.getRouteColor()));
                    ivBottomLine.setBackgroundColor(routeStop.getRouteColor());
                } else {
                    ivStopPosition.setBackgroundTintList(ColorStateList.valueOf(this.routeColor));
                    ivBottomLine.setBackgroundColor(this.routeColor);
                }

                tvStopName.setText(routeStop.getName());

                if (position == (routeStops.size() - 1)) {
                    ivBottomLine.setVisibility(View.GONE);
                } else {
                    ivBottomLine.setVisibility(View.VISIBLE);
                }

                convertView.setTag(position);
                convertView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int position = (Integer) v.getTag();

                        if (position < routeStops.size()) {
                            selectedRouteStop = routeStops.get(position);
                            stopViewerItineraryLegPoint = new ItineraryLegPoint();
                            stopViewerItineraryLegPoint.setStopId(selectedRouteStop.getId());
                            stopViewerItineraryLegPoint.setName(selectedRouteStop.getName());
                            stopViewerItineraryLegPoint.setLat(selectedRouteStop.getLat());
                            stopViewerItineraryLegPoint.setLon(selectedRouteStop.getLon());

                            String mode = null;
                            if (currentRouteViewerRoute != null) {
                                mode = currentRouteViewerRoute.getMode();
                            }
                            enterStopViewerPage(mode);

                        }
                    }
                });
            }
            return convertView;
        }
    }

    private void removeRouteDirectionStopsFromMap() {
        if (routeDirectionStopSource != null) {
            gMapStyle.removeLayer("unclustered_route_direction_stop_points");
            gMapStyle.removeSource("route_direction_stop_list");
            routeDirectionStopSource = null;
            unclusteredRouteDirectionStop = null;
        }
    }

    private void loadRouteDirectionStopsOnMap() {
        if (gMapStyle != null) {
            gMapStyle.addImage("route_direction_stop_icon_id", BitmapUtils.getBitmapFromDrawable(
                    systemContext.getResources().getDrawable(R.drawable.ic_route_direction_stop)));

            FeatureCollection features = generateRouteDirectionStopFeatureData();

            removeRouteDirectionStopsFromMap();

            routeDirectionStopSource = new GeoJsonSource("route_direction_stop_list",
                    features,
                    new GeoJsonOptions()
                            .withCluster(false)
            );

            gMapStyle.addSource(routeDirectionStopSource);

            //Creating a marker layer for single data point
            unclusteredRouteDirectionStop = new SymbolLayer("unclustered_route_direction_stop_points", "route_direction_stop_list");

            unclusteredRouteDirectionStop.setProperties(
                    iconImage("route_direction_stop_icon_id"),
                    symbolZOrder(Property.SYMBOL_Z_ORDER_SOURCE),
                    iconAllowOverlap(true),
                    iconIgnorePlacement(true),
                    visibility(VISIBLE)
            );

            unclusteredRouteDirectionStop.setFilter(Expression.all(Expression.eq(get("typeId"), Resource.ID_STOP_ON_ROUTE_DIRECTION), not(has("cluster"))));
            gMapStyle.addLayer(unclusteredRouteDirectionStop);
        }
    }

    private FeatureCollection generateRouteDirectionStopFeatureData() {
        Gson gson = new Gson();
        ArrayList<Feature> featureList = new ArrayList<Feature>();
        if (routeStops != null) {
            for (TransitStop routeStop : routeStops) {

                Feature feature = Feature.fromGeometry(Point.fromLngLat(
                        routeStop.getLon(),
                        routeStop.getLat()
                ));

                feature.addNumberProperty("typeId", Resource.ID_STOP_ON_ROUTE_DIRECTION);
                feature.addStringProperty("jsonString", gson.toJson(routeStop));

                featureList.add(feature);
            }
        }
        return FeatureCollection.fromFeatures(featureList);
    }

    public void setMapCenterAndZoomIfNeeded() {
        if (selectedRouteViewerRouteIndex == -1 && gMapboxMap != null
                && MyApplication.getInstance().mapTargetOnStopViewerPage != null
                && MyApplication.getInstance().mapZoomOnStopViewerPage > 0.0) {
            gMapboxMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder()
                    .target(MyApplication.getInstance().mapTargetOnStopViewerPage)
                    .zoom(MyApplication.getInstance().mapZoomOnStopViewerPage)
                    .build()));
        }
    }

    private class StopNode {
        public int maxStops;
        public String headSignName;

        public StopNode(String name, int stops) {
            this.maxStops = stops;
            this.headSignName = name;
        }
    }

    public void resetAction(boolean currentSelected) {
        MyApplication.getInstance().gbStartOver = false;
        selectedRouteFilterAgencyIndex = 0;
        selectedRouteFilterModeIndex = 0;
        Configurations.setInt(Constants.ROUTE_FILTER_MODE_KEY, selectedRouteFilterModeIndex);
        Configurations.setInt(Constants.ROUTE_FILTER_AGENCY_KEY, selectedRouteFilterAgencyIndex);
        if (iv_filter_changed != null) {
            iv_filter_changed.setVisibility(View.GONE);
        }

        if (currentSelected) {
            setSelection(0);
        } else {
            resetRouteSelection = true;
        }
    }

    public void resetIfNeeded() {
        if (resetRouteSelection) {
            setSelection(0);

            resetRouteSelection = false;
        }
    }
}