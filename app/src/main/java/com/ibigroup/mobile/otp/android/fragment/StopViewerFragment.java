package com.ibigroup.mobile.otp.android.fragment;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static com.ibigroup.mobile.otp.android.utility.Configurations.LATITUDE_DEFAULT;
import static com.ibigroup.mobile.otp.android.utility.Configurations.LONGITUDE_DEFAULT;
import static com.ibigroup.mobile.otp.android.utility.Constants.TRANSIT_STOP_MARKER_MIN_ZOOM_LEVEL;
import static com.ibigroup.mobile.otp.android.utility.ServerDelegate.volleyAccountStringRequest;
import static com.mapbox.mapboxsdk.style.expressions.Expression.get;
import static com.mapbox.mapboxsdk.style.expressions.Expression.has;
import static com.mapbox.mapboxsdk.style.expressions.Expression.not;
import static com.mapbox.mapboxsdk.style.layers.Property.NONE;
import static com.mapbox.mapboxsdk.style.layers.Property.VISIBLE;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconAllowOverlap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconColor;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconIgnorePlacement;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconImage;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.symbolZOrder;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.visibility;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.ibigroup.mobile.otp.android.MyApplication;
import com.ibigroup.mobile.otp.android.R;
import com.ibigroup.mobile.otp.android.StopViewerActivity;
import com.ibigroup.mobile.otp.android.StopsFilterActivity;
import com.ibigroup.mobile.otp.android.TabActivity;
import com.ibigroup.mobile.otp.android.api.Resource;
import com.ibigroup.mobile.otp.android.json.AddedLocation;
import com.ibigroup.mobile.otp.android.json.ParkRideLocation;
import com.ibigroup.mobile.otp.android.json.TransitStop;
import com.ibigroup.mobile.otp.android.json.UserPreferences;
import com.ibigroup.mobile.otp.android.json.itinerary.ItineraryLegPoint;
import com.ibigroup.mobile.otp.android.manager.ParkRideManager;
import com.ibigroup.mobile.otp.android.manager.TransitStopManager;
import com.ibigroup.mobile.otp.android.utility.Configurations;
import com.ibigroup.mobile.otp.android.utility.Constants;
import com.ibigroup.mobile.otp.android.utility.Convert;
import com.ibigroup.mobile.otp.android.utility.Logger;
import com.ibigroup.mobile.otp.android.utility.Utilities;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.plugins.markerview.MarkerView;
import com.mapbox.mapboxsdk.plugins.markerview.MarkerViewManager;
import com.mapbox.mapboxsdk.style.expressions.Expression;
import com.mapbox.mapboxsdk.style.layers.Layer;
import com.mapbox.mapboxsdk.style.layers.Property;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonOptions;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.mapbox.mapboxsdk.utils.BitmapUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link StopViewerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StopViewerFragment extends Fragment {
    private DisplayMetrics gDm = new DisplayMetrics();
    private final boolean isExpanded = false;

    private TransitStopManager.TransitStopListener transitStopListener;
    private Context systemContext;
    private View mFragmentView;
    private com.mapbox.mapboxsdk.maps.MapView mapView;
    private MapboxMap gMapboxMap;
    private Style gMapStyle;
    private GeoJsonSource transitStopSource;
    private SymbolLayer unclusteredTransitStop;
    private TransitStop selectedTransitStop;
    private MarkerViewManager markerViewManager = null;
    private MarkerView markerView = null;
    private LatLng markerViewLatlng = null;
    private View customView;
    private LinearLayout llMarkerStopViewer, llMarkerBtnStopViewer;
    private TextView tvMapAddress, tvMarkerStopID;
    private ImageView ivMarkerViewClose;
    private ItineraryLegPoint stopViewerItineraryLegPoint = null;
    private LinearLayout llFavStops;
    private TextView tvEmptyFavStops;
    private ListView lvFavStops;
    private FavStopsListAdapter favStopsListAdapter;
    private ImageView ivFavStopsExpander;
    private int selectedFavStopIndex = -1;
    private final ArrayList<AddedLocation> favStops = new ArrayList<>();
    private RelativeLayout rlCollapsed;
    private ImageView ivExpand, ivCollapsedBack;
    private LinearLayout llSearchBar;
    private int currentFavStopsHeight;
    private int currentFavStopsY;
    private String mapCurrentStyle;
    private ParkRideLocation selectedParkRideLocation;
    private int currentSelectedLocationMode = Constants.SELECTED_LOCATION_MODE_POINT;
    private LinearLayout llLayoutLayers, llStreets, llSatellite, llParkRideLocations, llTransitStops, llSatelliteAccessibility, llParkRideLocationsAccessibility, llTranitStopsAccessibility;
    private ImageView ivLayer, ivLayerBg, ivParkRideLocations, ivTransitStops;
    private TextView tvStreets, tvSatellite, tvStreetsHighlight, tvSatelliteHighlight, tvParkRideLocations, tvTransitStops, tvSatelliteHighlightAccessibility;
    private ParkRideManager.ParkRideListener parkRideListener;

    private GeoJsonSource parkRideSource;
    private SymbolLayer unclusteredParkRide;

    public StopViewerFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RouteViewerFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static StopViewerFragment newInstance(String param1, String param2) {
        return new StopViewerFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        gDm = new DisplayMetrics();
        systemContext = MyApplication.getInstance().getApplicationContext();
        if (getActivity() != null) {
            Convert.mDensity = getActivity().getResources().getDisplayMetrics().density;
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(gDm);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (mFragmentView != null) {
            return mFragmentView;
        }

        mFragmentView = inflater.inflate(R.layout.fragment_stop_viewer, container, false);
        // Inflate the layout for this fragment

        setupMapView(mFragmentView, savedInstanceState);
        setupFavStops();
        setupSearchAStop();
        setupExpandLayouts();
        setupLayers();
        return mFragmentView;
    }

    private void setupMapView(View mView, Bundle savedInstanceState) {
        mapView = mView.findViewById(R.id.map_view);
        mapView.onCreate(savedInstanceState);

        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(MapboxMap mapboxMap) {
                gMapboxMap = mapboxMap;
                markerViewManager = new MarkerViewManager(mapView, mapboxMap);

                int marginHeight;
                if (gDm.heightPixels >= Convert.dpToPx(688)) {
                    marginHeight = Convert.dpToPx(300);
                } else {
                    marginHeight = (gDm.heightPixels - Convert.dpToPx(88)) / 2;
                }

                //move the compass icon to the bottom left side
                gMapboxMap.getUiSettings().setCompassMargins(0, gDm.heightPixels - marginHeight - Convert.dpToPx(139),
                        gDm.widthPixels - Convert.dpToPx(60), 0);

                gMapboxMap.setStyle(Style.LIGHT, new Style.OnStyleLoaded() {
                    @Override
                    public void onStyleLoaded(@NonNull Style style) {
                        gMapStyle = style;
                    }
                });

                moveMapToDefaultLocation();
                loadTransitStopsOnMap();

                setupAndAddFeatureListeners();

                gMapboxMap.addOnCameraIdleListener(new MapboxMap.OnCameraIdleListener() {
                    @Override
                    public void onCameraIdle() {
                        if (gMapStyle != null) {
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

                gMapboxMap.addOnMapClickListener(new MapboxMap.OnMapClickListener() {
                    @Override
                    public boolean onMapClick(@NonNull LatLng point) {
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
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        return true;
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

    private void initMarkerView() {
        Utilities.setFont(systemContext, tvMapAddress, false);
    }

    private void setupMarkerView() {
        tvMapAddress = customView.findViewById(R.id.tv_map_address);
        LinearLayout llMarkerBtnFromHere = customView.findViewById(R.id.ll_marker_btn_from_here);
        LinearLayout llMarkerBtnToHere = customView.findViewById(R.id.ll_marker_btn_to_here);
        ivMarkerViewClose = customView.findViewById(R.id.iv_marker_view_close);
        llMarkerStopViewer = customView.findViewById(R.id.ll_marker_stop_viewer);
        tvMarkerStopID = customView.findViewById(R.id.tv_marker_stop_id);
        llMarkerBtnStopViewer = customView.findViewById(R.id.ll_marker_btn_stop_viewer);

        initMarkerView();

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

    private void refreshMarkerView(LatLng latlng) {
        switch (currentSelectedLocationMode) {
            case Constants.SELECTED_LOCATION_MODE_PARK_RIDE:
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
                llMarkerStopViewer.setVisibility(View.VISIBLE);
                llMarkerBtnStopViewer.setVisibility(View.VISIBLE);
                if (selectedTransitStop != null) {
                    tvMapAddress.setText(selectedTransitStop.getName());
                    String id = Utilities.getStopId(selectedTransitStop);
                    tvMarkerStopID.setText(id);
                    tvMapAddress.setTypeface(null, Typeface.BOLD);
                }
                break;

            default:
                break;
        }
    }

    private void addMarkerViewIfNeeded(LatLng latlng) {
        if (markerView == null) {
            customView = getLayoutInflater().inflate(R.layout.layout_map_trip_plan, null);
            customView.setLayoutParams(new FrameLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT));

            setupMarkerView();

            refreshMarkerView(latlng);
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
        } else {
            customView.setVisibility(View.VISIBLE);
            refreshMarkerView(latlng);
            markerView.setLatLng(latlng);
        }

        markerViewLatlng = latlng;

        CameraPosition position = new CameraPosition.Builder()
                .target(markerViewLatlng)
                .padding(0, Convert.dpToPx(16), 0, 0)
                .build();
        gMapboxMap.moveCamera(CameraUpdateFactory.newCameraPosition(position));
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

    private void removeTransitStopsFromMap() {
        if (transitStopSource != null) {
            gMapStyle.removeLayer("unclustered_transit_stop_points");
            gMapStyle.removeSource("transit_stop_list");
            transitStopSource = null;
            unclusteredTransitStop = null;
        }
    }

    private void loadTransitStopsOnMap() {
        if (gMapStyle != null && getActivity() != null) {
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

                if (gMapboxMap.getCameraPosition().zoom >= TRANSIT_STOP_MARKER_MIN_ZOOM_LEVEL) {
                    unclusteredTransitStop.setProperties(
                            iconImage("transit_stop_icon_id"),
                            iconColor(getResources().getColor(R.color.colorGrey)),
                            symbolZOrder(Property.SYMBOL_Z_ORDER_SOURCE),
                            iconAllowOverlap(true),
                            iconIgnorePlacement(true),
                            visibility(VISIBLE)
                    );
                } else {
                    unclusteredTransitStop.setProperties(
                            iconImage("transit_stop_icon_id"),
                            iconColor(getResources().getColor(R.color.colorGrey)),
                            symbolZOrder(Property.SYMBOL_Z_ORDER_SOURCE),
                            iconAllowOverlap(true),
                            iconIgnorePlacement(true),
                            visibility(NONE)
                    );
                }

                unclusteredTransitStop.setFilter(Expression.all(Expression.eq(get("typeId"), Resource.ID_TRANSIT_STOP), not(has("cluster"))));
                gMapStyle.addLayer(unclusteredTransitStop);
            });
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        TransitStopManager.getInstance().removeListener(transitStopListener);
    }

    private void moveMapToDefaultLocation() {
        gMapboxMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder()
                .target(new LatLng(LATITUDE_DEFAULT, LONGITUDE_DEFAULT))
                .zoom(TRANSIT_STOP_MARKER_MIN_ZOOM_LEVEL)
                .build()));
    }

    private void setFavStopsHeight(int dy) {
        if (mFragmentView != null) {
            llFavStops = mFragmentView.findViewById(R.id.ll_fav_stops);
        }
        if (llFavStops != null && getActivity() != null) {
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) llFavStops.getLayoutParams();
            layoutParams.width = gDm.widthPixels;

            int minHeight = Convert.dpToPx(150);
            int maxHeight;
            if (gDm.heightPixels > Convert.dpToPx(369)) {
                maxHeight = gDm.heightPixels - Convert.dpToPx(219);
            } else {
                maxHeight = minHeight;
            }
            if (currentFavStopsHeight - dy > maxHeight) {
                layoutParams.height = maxHeight;
            } else layoutParams.height = Math.max(currentFavStopsHeight - dy, minHeight);

            llFavStops.setLayoutParams(layoutParams);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshFavStops();

    }

    private void generateFavStops() {
        String userPreferencesString = Configurations.getString("account_user_preferences", "");
        Gson gson = new Gson();
        favStops.clear();
        if (userPreferencesString != null && !userPreferencesString.isEmpty()) {
            try {
                UserPreferences userPreferences = gson.fromJson(userPreferencesString, UserPreferences.class);
                if (userPreferences.getSavedLocations() != null) {
                    for (AddedLocation addedLocation : userPreferences.getSavedLocations()) {
                        if ("stop".equalsIgnoreCase(addedLocation.getType())) {
                            favStops.add(addedLocation);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void refreshFavStops() {
        String credentialString = Configurations.getString("account_credentials", "");

        if (credentialString != null && !credentialString.isEmpty()) {
            if (MyApplication.getInstance().userPreferences == null) {
                Gson gson = new Gson();
                try {
                    MyApplication.getInstance().userPreferences =
                            gson.fromJson(Configurations.getString("account_user_preferences", "{}"), UserPreferences.class);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            lvFavStops.setVisibility(View.VISIBLE);
            tvEmptyFavStops.setText(getString(R.string.empty_fav_stops));
            generateFavStops();

            if (favStops.size() > 0) {
                tvEmptyFavStops.setVisibility(View.GONE);
            } else {
                tvEmptyFavStops.setVisibility(View.VISIBLE);
            }

            if (favStopsListAdapter != null) {
                favStopsListAdapter.notifyDataSetChanged();
            }
        } else {
            tvEmptyFavStops.setVisibility(View.VISIBLE);
            lvFavStops.setVisibility(View.GONE);
            tvEmptyFavStops.setText(getString(R.string.login_to_see_fav_stops));
        }

    }

    private FavStopsListAdapter getFavStopsListAdapter() {
        ArrayList<HashMap<String, Object>> data = new ArrayList<HashMap<String, Object>>();

        for (int i = 0; i < favStops.size(); i++) {
            AddedLocation addedLocation = favStops.get(i);

            HashMap<String, Object> map = new HashMap<String, Object>();

            map.put("tv_stop_name", addedLocation.getName());
            data.add(map);
        }

        FavStopsListAdapter mFavStopsListAdapter = new FavStopsListAdapter(
                systemContext, data,
                R.layout.item_fav_stop,
                new String[]{"tv_stop_name"}, new int[]{R.id.tv_stop_name});

        return mFavStopsListAdapter;
    }

    class FavStopsListAdapter extends SimpleAdapter {
        Context mContext;
        private final LayoutInflater l_Inflater;

        public FavStopsListAdapter(Context context, List<? extends Map<String, ?>> data,
                                   int resource, String[] from, int[] to) {
            super(context, data, resource, from, to);

            mContext = context;
            l_Inflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return favStops.size();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            AddedLocation addedLocation = favStops.get(position);

            if (convertView == null) {
                convertView = l_Inflater.inflate(
                        R.layout.item_fav_stop, null);
            }

            if (addedLocation != null) {
                TextView tvStopID = convertView.findViewById(R.id.tv_stop_id);
                TextView tvStopName = convertView.findViewById(R.id.tv_stop_name);
                ImageView ivStopFav = convertView.findViewById(R.id.iv_stop_fav);
                ImageView ivFavStopIcon = convertView.findViewById(R.id.iv_fav_stop_icon);

                TransitStop transitStop = Utilities.getStopInfo(addedLocation);

                if (transitStop != null) {
                    tvStopID.setText(getString(R.string.bold_stop_id_with_num, Utilities.getStopId(transitStop)));
                }

                ivFavStopIcon.setColorFilter(getResources().getColor(R.color.colorGrey));

                tvStopName.setText(addedLocation.getName());
                ivStopFav.setTag(transitStop);

                ivStopFav.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        TransitStop transitStop = (TransitStop) v.getTag();

                        if (transitStop != null) {
                            if (stopViewerItineraryLegPoint == null) {
                                stopViewerItineraryLegPoint = new ItineraryLegPoint();
                            }
                            stopViewerItineraryLegPoint.setStopId(transitStop.getId());
                            stopViewerItineraryLegPoint.setName(transitStop.getName());
                            stopViewerItineraryLegPoint.setLat(transitStop.getLat());
                            stopViewerItineraryLegPoint.setLon(transitStop.getLon());

                            showRemoveFavStopPopupDialog(getString(R.string.attention),
                                    getString(R.string.remove_fav_stop_warning, transitStop.getName()));
                        }
                    }
                });

                if (position == selectedFavStopIndex) {
                    convertView.setBackgroundResource(R.drawable.fav_stop_selected_bg);
                } else {
                    convertView.setBackground(null);
                }
                convertView.setTag(position);
                convertView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int position = (int) v.getTag();
                        selectedFavStopIndex = position;
                        if (position < favStops.size()) {
                            AddedLocation addedLocation = favStops.get(position);
                            selectedTransitStop = Utilities.getStopInfo(addedLocation);

                            if (selectedTransitStop != null) {
                                if (stopViewerItineraryLegPoint == null) {
                                    stopViewerItineraryLegPoint = new ItineraryLegPoint();
                                }
                                stopViewerItineraryLegPoint.setStopId(selectedTransitStop.getId());
                                stopViewerItineraryLegPoint.setName(selectedTransitStop.getName());
                                stopViewerItineraryLegPoint.setLat(selectedTransitStop.getLat());
                                stopViewerItineraryLegPoint.setLon(selectedTransitStop.getLon());

                                enterStopViewerPage(null);

                                if (favStopsListAdapter != null) {
                                    favStopsListAdapter.notifyDataSetChanged();
                                }

                                addMarkerViewIfNeeded(new LatLng(selectedTransitStop.getLat(), selectedTransitStop.getLon()));
                                moveMapToShowFavStop(selectedTransitStop.getLat(), selectedTransitStop.getLon());

                                rlCollapsed.setVisibility(View.VISIBLE);
                                llSearchBar.setVisibility(View.GONE);
                            }
                        }
                    }
                });
            }
            return convertView;
        }
    }

    private void setupExpandLayouts() {
        if (mFragmentView != null) {
            rlCollapsed = mFragmentView.findViewById(R.id.rl_collapsed);
            ivExpand = mFragmentView.findViewById(R.id.iv_expand);
            ivCollapsedBack = mFragmentView.findViewById(R.id.iv_collapsed_back);
            llSearchBar = mFragmentView.findViewById(R.id.ll_search_bar);

            ivExpand.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    rlCollapsed.setVisibility(View.GONE);
                    llSearchBar.setVisibility(View.VISIBLE);
                }
            });

            ivCollapsedBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    rlCollapsed.setVisibility(View.GONE);
                    llSearchBar.setVisibility(View.VISIBLE);
                }
            });
        }
    }

    private void moveMapToShowFavStop(double lat, double lng) {
        gMapboxMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder()
                .target(new LatLng(lat, lng))
                .zoom(17.0)
                .build()));
    }

    private void showRemoveFavStopPopupDialog(@NonNull String title, @NonNull String message) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());

        // set title
        alertDialogBuilder.setTitle(title);

        // set dialog message
        alertDialogBuilder
                .setMessage(message)
                .setCancelable(false)
                .setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        savePreferences();
                        dialog.dismiss();
                    }
                });

        alertDialogBuilder.create().show();
    }

    private ArrayList<AddedLocation> generateAddedLocations() {
        ArrayList<AddedLocation> addedLocations = new ArrayList<>();
        UserPreferences userPreferences = MyApplication.getInstance().userPreferences;

        if (userPreferences.getSavedLocations() != null) {
            addedLocations.addAll(userPreferences.getSavedLocations());
        }

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

        return addedLocations;
    }

    private void savePreferences() {
        Response.Listener listener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Gson gson = new Gson();
                try {
                    MyApplication.getInstance().userPreferences = gson.fromJson(response, UserPreferences.class);

                    Configurations.setString("account_user_preferences", response);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                refreshFavStops();
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
            newPreferences.setSavedLocations(generateAddedLocations());
            newPreferences.setNotificationChannel(userPreferences.getNotificationChannel());
            newPreferences.setDependents(userPreferences.getDependents());

            Gson gson = new Gson();
            String url = String.format(Resource.API_ACCOUNT_USER_SAVE_PREFERENCES, userPreferences.getId());

            volleyAccountStringRequest(Request.Method.PUT, url, gson.toJson(newPreferences), listener, errorListener);
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setupFavStops() {
        if (mFragmentView != null) {
            tvEmptyFavStops = mFragmentView.findViewById(R.id.tv_empty_fav_stops);
            llFavStops = mFragmentView.findViewById(R.id.ll_fav_stops);
            lvFavStops = mFragmentView.findViewById(R.id.lv_fav_stops);
            favStopsListAdapter = getFavStopsListAdapter();
            lvFavStops.setAdapter(favStopsListAdapter);

            ivFavStopsExpander = mFragmentView.findViewById(R.id.iv_fav_stops_expander);

            ivFavStopsExpander.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            currentFavStopsY = (int) event.getRawY();

                            break;

                        case MotionEvent.ACTION_MOVE:
                            int dy = (int) event.getRawY() - currentFavStopsY;

                            setFavStopsHeight(dy);
                            break;

                        case MotionEvent.ACTION_UP:
                            if (llFavStops != null) {
                                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) llFavStops.getLayoutParams();
                                currentFavStopsHeight = layoutParams.height;
                            }

                            break;
                    }
                    return true;
                }
            });

            initFavStopsHeight();
        }
    }

    private void initFavStopsHeight() {
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) llFavStops.getLayoutParams();
        layoutParams.width = gDm.widthPixels;
        if (gDm.heightPixels >= Convert.dpToPx(688)) {
            layoutParams.height = Convert.dpToPx(300);
        } else {
            layoutParams.height = (gDm.heightPixels - Convert.dpToPx(88)) / 2;
        }
        currentFavStopsHeight = layoutParams.height;
        llFavStops.setLayoutParams(layoutParams);
    }

    private void setupSearchAStop() {
        if (mFragmentView != null) {
            TextView tvSearchAStop = mFragmentView.findViewById(R.id.tv_search_a_stop);

            tvSearchAStop.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    gotoStopsFilterActivity();
                }
            });
        }

    }

    private void gotoStopsFilterActivity() {
        if (getActivity() != null) {
            Intent intent = new Intent(getActivity(), StopsFilterActivity.class);

            getActivity().startActivityForResult(intent, Constants.STOP_VIEWER_REQUEST_CODE);
        }
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
            llTranitStopsAccessibility.setBackgroundResource(R.drawable.grey_frame_rounded_transparent_bg);
            loadTransitStopsOnMap();
        } else {
            llTransitStops.setBackground(null);
            llTranitStopsAccessibility.setBackground(null);
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

    private void setupLayers() {
        RelativeLayout rlLayer = mFragmentView.findViewById(R.id.rl_layer);
        ivLayer = mFragmentView.findViewById(R.id.iv_layer);
        ivLayerBg = mFragmentView.findViewById(R.id.iv_layer_bg);
        tvStreets = mFragmentView.findViewById(R.id.tv_streets);
        tvSatellite = mFragmentView.findViewById(R.id.tv_satellite);
        tvStreetsHighlight = mFragmentView.findViewById(R.id.tv_streets_highlight);
        tvSatelliteHighlight = mFragmentView.findViewById(R.id.tv_satellite_highlight);

        ivParkRideLocations = mFragmentView.findViewById(R.id.iv_park_and_ride_locations);
        ivTransitStops = mFragmentView.findViewById(R.id.iv_transit_stops);
        tvParkRideLocations = mFragmentView.findViewById(R.id.tv_park_and_ride_locations);
        tvTransitStops = mFragmentView.findViewById(R.id.tv_transit_stops);

        llLayoutLayers = mFragmentView.findViewById(R.id.ll_layout_layers);
        llStreets = mFragmentView.findViewById(R.id.ll_streets);
        llSatellite = mFragmentView.findViewById(R.id.ll_satellite);
        llParkRideLocations = mFragmentView.findViewById(R.id.ll_park_and_ride_locations);
        llTransitStops = mFragmentView.findViewById(R.id.ll_transit_stops);
        ImageView ivLayerClose = mFragmentView.findViewById(R.id.iv_layer_close);


        llSatelliteAccessibility = mFragmentView.findViewById(R.id.ll_satellite_accessibility);
        llParkRideLocationsAccessibility = mFragmentView.findViewById(R.id.ll_park_and_ride_locations_accessibility);
        llTranitStopsAccessibility = mFragmentView.findViewById(R.id.ll_transit_stops_accessibility);
        tvSatelliteHighlightAccessibility = mFragmentView.findViewById(R.id.tv_satellite_highlight_accessibility);

        ivLayerClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                llLayoutLayers.setVisibility(View.GONE);
            }
        });

        if (Utilities.checkIfToDisplayAccessibilityUI(requireActivity())) {
            llParkRideLocationsAccessibility.setVisibility(View.VISIBLE);
            llSatelliteAccessibility.setVisibility(View.VISIBLE);
            llTranitStopsAccessibility.setVisibility(View.VISIBLE);

            llParkRideLocations.setVisibility(View.GONE);
            llSatellite.setVisibility(View.GONE);
            llTransitStops.setVisibility(View.GONE);
        } else {

            llParkRideLocationsAccessibility.setVisibility(View.GONE);
            llSatelliteAccessibility.setVisibility(View.GONE);
            llTranitStopsAccessibility.setVisibility(View.GONE);

            llParkRideLocations.setVisibility(View.VISIBLE);
            llSatellite.setVisibility(View.VISIBLE);
            llTransitStops.setVisibility(View.VISIBLE);
        }

        initLayerTextViews();

        refreshMapStyleControllers();
        refreshParkRideLocationControllers();
        refreshTransitStopsControllers();

        rlLayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (llLayoutLayers.getVisibility() == View.VISIBLE) {
                    llLayoutLayers.setVisibility(View.GONE);

                    ivLayer.setColorFilter(null);
                    ivLayerBg.setImageResource(R.drawable.ic_layer_bkg_default);
                    ivLayerBg.setColorFilter(null);
                    llFavStops.setVisibility(View.VISIBLE);
                } else {
                    llLayoutLayers.setVisibility(View.VISIBLE);

                    ivLayer.setColorFilter(getResources().getColor(android.R.color.white));
                    ivLayerBg.setImageResource(R.drawable.ic_layer_bkg_on);
                    ivLayerBg.setColorFilter(getResources().getColor(R.color.colorPrimary));
                    llFavStops.setVisibility(View.GONE);
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

        llTranitStopsAccessibility.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Configurations.setBoolean("enable_layer_transit_stops",
                        !Configurations.getBoolean("enable_layer_transit_stops", true));

                refreshTransitStopsControllers();
            }
        });
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
            gMapStyle.addLayer(unclusteredParkRide);
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
}