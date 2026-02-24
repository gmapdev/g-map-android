package com.ibigroup.mobile.otp.android.fragment;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.LOCATION_SERVICE;
import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static com.ibigroup.mobile.otp.android.type.Mode.BICYCLE;
import static com.ibigroup.mobile.otp.android.type.Mode.BUS;
import static com.ibigroup.mobile.otp.android.type.Mode.CAR;
import static com.ibigroup.mobile.otp.android.type.Mode.FERRY;
import static com.ibigroup.mobile.otp.android.type.Mode.MONORAIL;
import static com.ibigroup.mobile.otp.android.type.Mode.RAIL;
import static com.ibigroup.mobile.otp.android.type.Mode.SCOOTER;
import static com.ibigroup.mobile.otp.android.type.Mode.SUBWAY;
import static com.ibigroup.mobile.otp.android.type.Mode.TRAM;
import static com.ibigroup.mobile.otp.android.type.Mode.WALK;
import static com.ibigroup.mobile.otp.android.utility.Configurations.GRAPHQL_SERVER_URL;
import static com.ibigroup.mobile.otp.android.utility.Configurations.LATITUDE_DEFAULT;
import static com.ibigroup.mobile.otp.android.utility.Configurations.LONGITUDE_DEFAULT;
import static com.ibigroup.mobile.otp.android.utility.Configurations.ZOOM_DEFAULT;
import static com.ibigroup.mobile.otp.android.utility.Constants.MODE_COMBINATION_FILE_NAME;
import static com.ibigroup.mobile.otp.android.utility.Constants.MY_PERMISSIONS_REQUEST_FINE_LOCATION;
import static com.ibigroup.mobile.otp.android.utility.Constants.MY_PERMISSIONS_REQUEST_POST_PUSH;
import static com.ibigroup.mobile.otp.android.utility.Constants.POPUP_TYPE_MULTIPLE_SELECTION;
import static com.ibigroup.mobile.otp.android.utility.Constants.POPUP_TYPE_SINGLE_SELECTION;
import static com.ibigroup.mobile.otp.android.utility.Constants.searchOptionsSpinnerIndex;
import static com.ibigroup.mobile.otp.android.utility.ServerDelegate.volleyAccountStringRequest;
import static com.ibigroup.mobile.otp.android.utility.ServerDelegate.volleyAccountStringRequestExt;
import static com.ibigroup.mobile.otp.android.utility.ServerDelegate.volleyStringRequest;
import static com.mapbox.mapboxsdk.Mapbox.getApplicationContext;
import static com.mapbox.mapboxsdk.style.expressions.Expression.get;
import static com.mapbox.mapboxsdk.style.expressions.Expression.has;
import static com.mapbox.mapboxsdk.style.expressions.Expression.literal;
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
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.text.Editable;
import android.text.Html;
import android.text.InputType;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextWatcher;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.text.style.UnderlineSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.constraintlayout.helper.widget.Flow;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.StringRequest;
import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.ApolloClient;
import com.apollographql.apollo.api.Input;
import com.apollographql.apollo.exception.ApolloException;
import com.apollographql.apollo.exception.ApolloNetworkException;
import com.auth0.android.Auth0;
import com.auth0.android.authentication.AuthenticationAPIClient;
import com.auth0.android.authentication.AuthenticationException;
import com.auth0.android.callback.Callback;
import com.auth0.android.result.UserProfile;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.google.maps.android.SphericalUtil;
import com.ibigroup.mobile.otp.android.ActiveRouteActivity;
import com.ibigroup.mobile.otp.android.IndoorNavigationActivity;
import com.ibigroup.mobile.otp.android.MyApplication;
import com.ibigroup.mobile.otp.android.PlanQuery;
import com.ibigroup.mobile.otp.android.R;
import com.ibigroup.mobile.otp.android.StopViewerActivity;
import com.ibigroup.mobile.otp.android.TabActivity;
import com.ibigroup.mobile.otp.android.TripViewerActivity;
import com.ibigroup.mobile.otp.android.VerifyEmailActivity;
import com.ibigroup.mobile.otp.android.api.Resource;
import com.ibigroup.mobile.otp.android.customize.CustomizeMarkerView;
import com.ibigroup.mobile.otp.android.customize.CustomizePopupDialog;
import com.ibigroup.mobile.otp.android.customize.CustomizePopupSelectionDialog;
import com.ibigroup.mobile.otp.android.json.AddedLocation;
import com.ibigroup.mobile.otp.android.json.DependentMobilityProfile;
import com.ibigroup.mobile.otp.android.json.JmapTriggerableLocation;
import com.ibigroup.mobile.otp.android.json.LocationPoint;
import com.ibigroup.mobile.otp.android.json.LogReport;
import com.ibigroup.mobile.otp.android.json.ModeCombination;
import com.ibigroup.mobile.otp.android.json.ModeCombinationConfig;
import com.ibigroup.mobile.otp.android.json.ModeConfig;
import com.ibigroup.mobile.otp.android.json.ParkRideLocation;
import com.ibigroup.mobile.otp.android.json.RelatedUser;
import com.ibigroup.mobile.otp.android.json.RentalVehicle;
import com.ibigroup.mobile.otp.android.json.RouteViewerRoute;
import com.ibigroup.mobile.otp.android.json.RoutingErrors;
import com.ibigroup.mobile.otp.android.json.SelectionStatus;
import com.ibigroup.mobile.otp.android.json.ServiceAlert;
import com.ibigroup.mobile.otp.android.json.TransitStop;
import com.ibigroup.mobile.otp.android.json.TransportModeCombination;
import com.ibigroup.mobile.otp.android.json.UserPreferences;
import com.ibigroup.mobile.otp.android.json.itinerary.FareProducts;
import com.ibigroup.mobile.otp.android.json.itinerary.IntermediateStop;
import com.ibigroup.mobile.otp.android.json.itinerary.Itinerary;
import com.ibigroup.mobile.otp.android.json.itinerary.ItineraryForSaveTrip;
import com.ibigroup.mobile.otp.android.json.itinerary.ItineraryLeg;
import com.ibigroup.mobile.otp.android.json.itinerary.ItineraryLegGeometry;
import com.ibigroup.mobile.otp.android.json.itinerary.ItineraryLegPoint;
import com.ibigroup.mobile.otp.android.json.itinerary.ItineraryLegStep;
import com.ibigroup.mobile.otp.android.json.trip.Banned;
import com.ibigroup.mobile.otp.android.json.trip.CreateNewTripRequest;
import com.ibigroup.mobile.otp.android.json.trip.ItineraryExistence;
import com.ibigroup.mobile.otp.android.json.trip.ItineraryExistenceDate;
import com.ibigroup.mobile.otp.android.json.trip.MonitoredTrip;
import com.ibigroup.mobile.otp.android.json.trip.MonitoredTripResponse;
import com.ibigroup.mobile.otp.android.json.trip.MonitoredTripUpdateRequest;
import com.ibigroup.mobile.otp.android.json.trip.Otp2QueryParams;
import com.ibigroup.mobile.otp.android.json.trip.QueryMode;
import com.ibigroup.mobile.otp.android.json.trip.TripError;
import com.ibigroup.mobile.otp.android.manager.ConfigManager;
import com.ibigroup.mobile.otp.android.manager.ParkRideManager;
import com.ibigroup.mobile.otp.android.manager.TransitStopManager;
import com.ibigroup.mobile.otp.android.setup.OTPContext;
import com.ibigroup.mobile.otp.android.type.InputBanned;
import com.ibigroup.mobile.otp.android.type.Mode;
import com.ibigroup.mobile.otp.android.type.Qualifier;
import com.ibigroup.mobile.otp.android.type.TransportMode;
import com.ibigroup.mobile.otp.android.utility.ChildItem;
import com.ibigroup.mobile.otp.android.utility.Configurations;
import com.ibigroup.mobile.otp.android.utility.Constants;
import com.ibigroup.mobile.otp.android.utility.Convert;
import com.ibigroup.mobile.otp.android.utility.Logger;
import com.ibigroup.mobile.otp.android.utility.ParentItem;
import com.ibigroup.mobile.otp.android.utility.ServerDelegate;
import com.ibigroup.mobile.otp.android.utility.Utilities;
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
import com.mapbox.mapboxsdk.utils.BitmapUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PlanTripFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PlanTripFragment extends Fragment {
    private LinearLayout llBottom_tab;
    private FrameLayout flFragmentHolder, PlanViewMainLayout;
    private com.mapbox.mapboxsdk.maps.MapView mapView;
    private MapboxMap gMapboxMap;
    private Style gMapStyle;
    private String mapCurrentStyle;
    private List<String> planQuerySummaries = new ArrayList<>();
    private MarkerViewManager markerViewManager = null;
    private MarkerView markerView = null;
    private RelativeLayout rlSearchBar, rlLayer, rlLocateMe;
    private SwitchCompat accessibleSwitch, switchAvoidWalking;
    private static boolean isAccessibleTrip = false;
    private static boolean isAvoidWalking = false;
    private RadioButton rbLessWalking, rbFewerTransfer, rbFastest;
    private LatLng markerViewLatlng = null;
    private View customView;
    private DisplayMetrics gDm = new DisplayMetrics();
    private int currentAdvancedSpinnerPosition = 0;
    private ImageView ivLocateMe, ivLocateMeBg;
    private Spinner spSearchOptions, spSearchOptionsAdvanced;
    private boolean explained = false;
    private LocationListener locationListener = null;

    private LinearLayout llPlanTripSearch, ll_lists_holder, llPlanTripSearchPref;
    private RelativeLayout rlMapView, rlSettingSearch, rlHomeSearchModes, rlHomeSearchModesAccessibilty;
    private LinearLayout llLayoutLayers, llStreets, llSatellite, llParkRideLocations, llTransitStops, llSubTransitHomeSearchModeAccessbility,
            llStreetsAccessibility, llSatelliteAccessibility, llParkRideLocationsAccessibility, llTranitStopsAccessibility, llHeaderHolder;

    private ImageView ivLayer, ivLayerBg, ivParkRideLocations, ivTransitStops, ivHomeSearchModeTransitExpand, ivHomeSearchModeTransitCollapse, ivCloseSearchMode, ivCloseSearchModeAccessibility, ivCloseButton;

    private LinearLayout llRentalVehicles, llRentalVehiclesAccessibility;


    private TextView tvStreets, tvSatellite, tvStreetsHighlight, tvSatelliteHighlight, tvParkRideLocations, tvTransitStops, tvSatelliteHighlightAccessibility, tvMoreOptions;
    private ParkRideManager.ParkRideListener parkRideListener;
    private TransitStopManager.TransitStopListener transitStopListener;
    private GeoJsonSource parkRideSource, transitStopSource, rentalVehicleSource;
    private SymbolLayer unclusteredParkRide, unclusteredTransitStop, unclusteredRentalVehicle;

    private final LocationPoint selectedLocationPoint = new LocationPoint();
    private TransitStop selectedTransitStop;
    private ParkRideLocation selectedParkRideLocation;
    private RentalVehicle selectedRentalVehicle;
    private int currentSelectedLocationMode = Constants.SELECTED_LOCATION_MODE_POINT;

    private LinearLayout llSearchBar, llOptionsLayout, llSearchBarHolder, llWalkSpeedHolder;

    private RelativeLayout rlRouteSearchAddressCollapsed;
    private LinearLayout llRouteSearchAddressExpanded, llItineraryBack;
    private ConstraintLayout clTimeOptions, clAdvancedPreferences;
    private ImageView ivRouteSearchAddressCollapsedBack, ivRouteSearchAddressCollapsedExpand, ivRouteSearchAddressCollapse;
    private TextView atSearchOrigin, atSearchDestination, tvOriginUnderline;
    private TextView tv_itinerary_origin, tv_itinerary_Destination, tv_notify_title;
    private ImageView iv_itinerary_back, iv_mapview_destination_icon, ivQuerySummary;
    private final LocationPoint originPoint = new LocationPoint();
    private final LocationPoint destinationPoint = new LocationPoint();
    private ImageView ivAddressSwitch, ivSearchSetting, ivSettingsChanged, ivSearchModesControls, ivSearchModesControlsChanged, ivSearchModeChangedAccessibility;

    private ImageView ivSearchBarReset, ivAccessibleAlert, ivAdditionalModeAlert;
    private LinearLayout llSearchMode, llOrigin, llSwitchLocationAccessibility, llPlanTripAccessbility, llSearchModeIcons, llSearchDestination;
    private RelativeLayout rlSearchModeDrive, rlSearchModeBike, rlSearchModeWalk, rlSearchModeTransit, rlSearchModeRental, rlSearchModeTransitAccessibility, rlSearchModeDriveAccessibility,
            rlSearchModeBikeAccessibility, rlSearchModeRentalAccessibility, rlSearchModeTransitAccessibilityNew, rlSearchModeDriveAccessibilityNew,
            rlSearchModeBikeAccessibilityNew, rlSearchModeRentalAccessibilityNew, rlSearchModeWalkAccessibilityNew, rlSearchModeWalkAccessibility;
    private ImageView ivSearchModeDrive, ivSearchModeBike, ivSearchModeWalk, ivSearchModeTransit, ivSearchModeRental, ivClock, ivSearchModeTransitAccessibility, ivSearchModeDriveAccessibility,
            ivSearchModeBikeAccessibility, ivSearchModeRentalAccessibility, ivSearchModeTransitAccessibilityNew, ivSearchModeDriveAccessibilityNew,
            ivSearchModeBikeAccessibilityNew, ivSearchModeRentalAccessibilityNew, ivSearchModeWalkAccessibilityNew;
    private TextView tvSearchModeDrive, tvSearchModeBike, tvSearchModeWalk, tvSearchModeTransit, tvSearchModeRental;

    private LinearLayout llMarkerStopViewer, llMarkerBtnStopViewer, ll_notification_layout, ll_notification_warning;
    private final Map<String, String> uniqueAgentsUrl = new HashMap();

    private TextView tvMapAddress, tvMarkerStopID;
    private ImageView ivMarkerViewClose;
    private NetworkImageView ivAgencyLogo;
    private final String errorTitleMesage = "";
    private final String errorDescrptionMessage = "";
    private ExpandableListView expandableListView;
    private final ArrayList<ModeConfig> routeSearchModes = new ArrayList<>();
    private final ArrayList<Itinerary> itineraryArrayListWalkTransit = new ArrayList<>();
    private final ArrayList<Itinerary> itineraryArrayListDriveTransit = new ArrayList<>();
    private final ArrayList<Itinerary> itineraryArrayListBikeTransit = new ArrayList<>();

    private GridLayout radioGrid;
    private final ArrayList<RoutingErrors> routingErrorsArrayList = new ArrayList<>();
    private ArrayList<TripError> configRouteErrors;

    private final ArrayList<Itinerary> itineraryArrayListBikeShareTransit = new ArrayList<>();
    private final ArrayList<Itinerary> itineraryArrayListScooterTransit = new ArrayList<>();

    private final ArrayList<Itinerary> itineraryArrayListOther = new ArrayList<>();

    private final ArrayList<ItineraryLeg> itineraryLegArrayList = new ArrayList<>();
    private int currentMode = Constants.HOME_PAGE_MODE_START_OVER;
    private final ArrayList<Integer> modeStack = new ArrayList<>();

    ArrayList<TextView> tvMatchedSchedule = new ArrayList<>();

    private String currentSelectedTripId = null;
    private int gpsReverseMode = Constants.GPS_REVERSE_MODE_MARKER_VIEW;

    private View transitWalkListView, transitBikeListView, transitBikeshareListView, transitDriveListView, transitScooterListView;

    private AdapterView.OnItemSelectedListener itemSelectedListener;
    private CustomSpinnerAdapter adapterSearchState;
    private final HashMap<String, HashMap<String, HashMap<String, Double>>> currentFareTable = new HashMap<>();
    private View mFragmentView;
    private Double onPausedZoom = null;
    private LatLng onPausedMapLatlng = null;
    private ImageView ivIndoorNavigation;
    private RelativeLayout rlIndoorNavigationPopup;
    private TextView tvIndoorNavigationPopupAddress, tvIndoorNavigationPopupCancel, tvIndoorNavigationPopupOpen;
    //Indoor navigation popup only show once
    private boolean isIndoorNavigationPopupShowed = false;
    private CustomizeMarkerView mvDestination = null, mvOrigin = null;
    private final ArrayList<String> sourceIDsForRoutePolyline = new ArrayList<String>();
    private final ArrayList<String> layerIDsForRoutePolyline = new ArrayList<String>();
    private final ArrayList<MarkerView> routeMarkerViews = new ArrayList<>();
    private int planTripQueryCount = 0;
    private Comparator<Itinerary> itineraryComparator = null;
    private RadioButton cb_leave_now, cb_depart_at, cb_arrive_by;

    private RelativeLayout rlHomeSetting, rlHomeDateTime;
    private LinearLayout rlHomeSettingAccessibility, llHomeSettingWalkSpeedAccessibilityPage, llHomeSettingWalkSpeedAccessibilityBack,
            llHomeSettingWalkSpeedAccessibilityOptions;
    private TextView tvHomeSettingWalkSpeedAccessibility;
    private ImageView ivCloseHomeSetting, ivCloseHomeSettingAccessibility;
    private TextView tvHomeSettingOK, tvHomeDateTimeSave, tvHomeDateTimeCancel;
    private RelativeLayout rlLeaveNow, rlDepartAt, rlArriveBy, rlOptionTime, rlOptionDate, rlSearchDateTime;
    private RelativeLayout rlArriveByAtOptionTime, rlArriveByOptionDate;
    private TextView tvLeaveNow, tvDepartAt, tvArriveBy;
    private TextView tvDepartAtOptionTime, tvDepartAtOptionDate, tvSearchTime, tvSearchDate, tvSearchDateTimeMode, tvSearchTimeAccessibility, tvSearchDateAccessibility;
    private TextView tvArriveByOptionTime, tvArriveByOptionDate;
    private LinearLayout llSearchDateTime, llDepartAtDateTime, llArriveByDateTime;
    private Spinner maxWalkSpinner, walkSpeedSpinner, optimizeOptionSpinner;
    private SpinnerAdapter maxWalkSpinnerAdapter, walkSpeedSpinnerAdapter, optimizeOptionSpinnerAdapter;
    private static int walkSpeedSelectIndex = 1;
    private int maxWalkSelectIndex = 3, optimizeOptionSelectIndex = 0;
    private boolean isLeaveNow = true, isDepartAt = false, isArriveBy = false;

    private View invisibleView;


    private RelativeLayout rlHomeSettingBusAccessibility, rlHomeSettingLinkAccessibility, rlHomeSettingFerryAccessibility;
    private LinearLayout llHomeSettingBus, llHomeSettingMartaRail, llHomeSettingStreetCar, llHomeSettingFerry,
            llHomeSettingGondola, llHomeSettingLink, llHomeSettingWaterTaxi, llHomeSettingAllowBikeRental, llHomeSettingAllowScooterRental, llHomeSettingMonorail;
    private LinearLayout llHomeSettingBusAccessibility, llHomeSettingMartaRailAccessibility, llHomeSettingStreetCarAccessibility, llHomeSettingFerryAccessibility,
            llHomeSettingLinkAccessibility, llHomeSettingWaterTaxiAccessibility, llHomeSettingMonorailAccessibility, llHomeSettingAllowBikeRentalAccessibility, llHomeSettingAvoidWalkingAccessibility,
            llHomeSettingAccessibleRoutingAccessibility, llHomeSettingWalkSpeedAccessibility, llHomeSettingAllowScooterRentalAccessibility;
    private TextView tvHomeSettingBus, tvHomeSettingMartaRail, tvHomeSettingStreetCar, tvHomeSettingFerry,
            tvHomeSettingGondola, tvHomeSettingLink, tvHomeSettingWaterTaxi, tvHomeSettingMonorail, tvRestoreMyDefaults;
    private ImageView ivHomeSettingBus, ivHomeSettingMartaRail, ivHomeSettingStreetCar, ivHomeSettingFerry,
            ivHomeSettingGondola, ivHomeSettingLink, ivHomeSettingWaterTaxi, ivHomeSettingMonorail, ivHomeSettingBusAccessibility,
            ivHomeSettingLinkAccessibility, ivHomeSettingStreetCarAccessibility,
            ivHomeSettingFerryAccessibility, ivHomeSettingWaterTaxiAccessibility, ivHomeSettingMonorailAccessibility, ivHomeSettingMartaRailAcessibility, ivHomeSettingAvoidWalking,
            ivHomeSettingAccessibileRouting, ivHomeSettingAllowBikeRental, ivHomeSettingAllowScooterRental;

    private ImageView ivHomeSettingBusAccessibilityNew, ivHomeSettingLinkAccessibilityNew, ivHomeSettingStreetCarAccessibilityNew,
            ivHomeSettingFerryAccessibilityNew, ivHomeSettingWaterTaxiAccessibilityNew, ivHomeSettingMartaRailAcessibilityNew,
            ivHomeSettingMonorailAccessibilityNew;

    private TextView tvHomeSettingBusAccessibility, tvHomeSettingMartaRailAccessibility, tvHomeSettingStreetCarAccessibility, tvHomeSettingFerryAccessibility,
            tvHomeSettingLinkAccessibility, tvHomeSettingWaterTaxiAccessibility, tvHomeSettingAvoidWalkingAccessibility, tvHomeSettingMonorailAccessibility,
            tvHomeSettingAccessibileRoutingAccessibility, tvHomeSettingAllowBikeRentalAccessibility, tvHomeSettingAllowScooterRentalAccessibility;
    private int tempSearchOptionsSpinnerIndex = 0;
    private LinearLayout llMobilityProfile;
    private Spinner spMobilityProfile;
    private SpinnerAdapter spMobilityProfileAdapter;
    private String selectedDependantEmail = null;
    private final ArrayList<String> mobilityProfileStringArrays = new ArrayList<>();
    private DatePickerDialog datePickerDialog = null;
    private TimePickerDialog timePickerDialog = null;
    private int currentTimePickerHour = 0;
    private int currentTimePickerMin = 0;

    private int currentDatePickerYear = 0;
    private int currentDatePickerMonth = 0;
    private int currentDatePickerDayOfMonth = 0;

    private int timePickerHourTmp = 0;
    private int timePickerMinTmp = 0;
    private int datePickerYearTmp = 0;
    private int datePickerMonthTmp = 0;
    private int datePickerDayOfMonthTmp = 0;
    private boolean isLeaveNowTmp = true, isDepartAtTmp = false, isArriveByTmp = false;
    private Spinner itinerarySortSpinner, itinerarySortSpinnerAccessibility;
    private int itinerarySortSpinnerIndex = 0;
    private LinearLayout llItineraryList, llAccessibilitylegend;
    private RelativeLayout rlItinerarySort, rlItinerarySortAccessibility;
    private void initItineraryListLayout() {
        initItinerarySortSpinner();
    }

    private int itineraryListExpandedStatus = Constants.EXPANDED_STATUS_UNEXPANDED;

    private boolean isItineraryDetailExpanded = true;
    private RelativeLayout rlRouteSearchAddress;
    private TextView tvRouteSearchOrigin, tvRouteSearchDestination, tvItinerariesFound;
    private ImageView ivRouteSearchAddressSwitch;
    private ProgressBar pbItineraryList;
    private CustomizePopupDialog noInternetPopupDialog = null;
    private ListView lvItineraryListWalkTransit, lvItineraryListDriveTransit, lvItineraryListOther, lvItineraryListBikeTransit,
            lvItineraryListBikeShareTransit, lvItineraryListScooterTransit;
    private TextView tvDriveTransitTitle, tvWalkTransitTitle, tvBikeTransitTitle, tvOtherTitle, tvBikeShareTransitTitle, tvScooterTransitTitle;
    private ItineraryListAdapter itineraryListAdapterWalkTransit, itineraryListAdapterBikeTransit, itineraryListAdapterDriveTransit,
            itineraryListAdapterBikeShareTransit, itineraryListAdapterScooterTransit;
    private ItineraryListOtherAdapter itineraryListOtherAdapter;
    private LinearLayout llPlanTripErrors;

    private int currentSelectedItineraryIndex = 0;
    private int currentSelectedItineraryGroupType = Constants.GROUP_ITINERARY_TYPE_WALK_TRANSIT;
    private Itinerary currentSelectedItinerary = null;
    private final ArrayList<TextView> tvDepartureTimes = new ArrayList<>();
    private ScrollView svItineraryDetail;
    private LinearLayout llItineraryDetail, llItineraryDetailCaloriesBurned, llPlanTripDetailErrors,
            llCaloriesBurnedDetail, llMoreInfoCaloriesBurned, llItineraryDetailFare, llDetailDateAndTimeAccessibility,
            llMoreInfoCaloriesBurnedAccessibility, llCalorieDetailBurnedAccessibility, llTripDetails;
    private RelativeLayout rlItineraryDetailHeader, rlCaloriesBurnedDetailClose, rlStartRoute;
    private Itinerary selectedItinerary = null;
    private TextView tvCaloriesBurnedDetail, tvItineraryDetailDate, tvItineraryDetailTime, tvItineraryDetailDateAt,
            tvItineraryDetailCaloriesBurned, tvItineraryDetailFare, tvItineraryDetailDateAccessibility, tvItineraryDetailTimeAccessibility,
            tvItineraryDetailFareAccessibility, tvItineraryDetailCaloriesBurnedAccessibility, tvItineraryFareText;
    private ImageView ivItineraryDetailCaloriesBurned, ivCaloriesBurnedDetailClose, ivSaveTrip;
    private ListView lvItineraryLegList;
    private ItineraryLegListAdapter itineraryLegListAdapter;
    private final ArrayList<Boolean> itineraryLegStepArray = new ArrayList<>();
    private ImageView ivItineraryDetailExpander;
    private RelativeLayout rlItineraryDetailExpander;
    private ListView lvServiceAlerts;
    private ImageView ivAlertsClose;
    private View viewRouteHeaderAlerts;
    private TextView tvRouteNumberAlerts, tvNumberOfAlerts, tvNumberOfAlertsAccessbility;
    private ServiceAlertAdapter serviceAlertAdapter;
    private LinearLayout rlServiceAlerts;
    private int currentHeight;
    private int currentY;
    private ItineraryLegGeometry currentLegGeometry = null;
    private ItineraryLegPoint stopViewerItineraryLegPoint = null;
    private ItineraryLeg selectedItineraryLeg = null;
    private final ArrayList<ServiceAlert> serviceAlerts = new ArrayList<>();
    private StringRequest gpsReverseRequest = null;
    private ImageView ivBack;
    private boolean isOrigin = true;
    private boolean currentLocationAvailable = false;
    private String address;
    private ImageView ivClear;
    private AutoCompleteTextView atSearch;
    private ImageView ivCurrentLocation;
    private TextView tvCurrentLocation, tvOther, tvStops;
    private PlacesAutoCompleteAdapter placesAutoCompleteAdapter;
    private ListView lvAutoComplete, lvFavouritePlacesAutoComplete, lvAutoCompleteStops;
    private final ArrayList<AddedLocation> filteredFavoritePlaces = new ArrayList<>();
    private AutoCompleteListAdapter autoCompleteListAdapter;
    private FilteredAddedLocationListAdapter filteredFavoritePlaceAdapter;
    private ArrayList<LocationPoint> autoCompleteResultList = new ArrayList<LocationPoint>();

    private AutoCompleteStopListAdapter autoCompleteStopListAdapter;
    private ArrayList<LocationPoint> autoCompleteStopList = new ArrayList<LocationPoint>();

    private StringRequest autocompleteStringRequest = null;

    private TextView tvRecentlySearched;
    private ListView lvRecentlySearched;
    private RecentlySearchedListAdapter recentlySearchedListAdapter;
    private ArrayList<LocationPoint> recentlySearchedList = new ArrayList<LocationPoint>();
    private boolean isAddingLocation = false;
    private ScrollView svAddressSearch;
    private final ArrayList<AddedLocation> favoritePlaces = new ArrayList<>();
    private ListView lvFavoritePlaces;
    private AddedLocationListAdapter favoritePlaceListAdapter;
    private TextView tvFavoritePlaces;
    private ScrollView sv_edit_trip;
    private LinearLayout ll_advanced_settings, ll_monitor_trip, ll_save_new_trip;
    private RelativeLayout rl_realtime_alert, rl_alternative_route, rl_delay_options, rl_monitor_trip;
    private Spinner sp_realtime_alert, sp_alternative_route, sp_delay_options, sp_monitor_trip;
    private LinearLayout ll_modes;
    private ConstraintLayout constraintLayout;
    private ImageView iv_advanced_settings;
    private EditText et_trip_name;
    private TextView tv_edit_trip_title, tv_itinerary_start_end_time, tv_itinerary_duration;
    private LinearLayout ll_monday, ll_tuesday, ll_wednesday, ll_thursday, ll_friday, ll_saturday, ll_sunday;
    private TextView tv_monday, tv_tuesday, tv_wednesday, tv_thursday, tv_friday, tv_saturday, tv_sunday;
    private ImageView iv_monday, iv_tuesday, iv_wednesday, iv_thursday, iv_friday, iv_saturday, iv_sunday;
    private CheckBox cb_monday, cb_tuesday, cb_wednesday, cb_thursday, cb_friday, cb_saturday, cb_sunday;
    private LinearLayout llCheckingItineraryExistence;
    private TextView tvItineraryExistenceHint;
    private TextView tv_cancel_update_trip, tv_save_trip;
    private final boolean createNewTripMode = false;
    private final boolean accountSettingsMode = false;
    private Itinerary itineraryForNewTrip = null;
    private final MonitoredTripUpdateRequest monitoredTripUpdateRequest = new MonitoredTripUpdateRequest();

    private ImageView ivLogo, ivMenu, ivSaveNewTripBack;
    private TextView tvTitle, tvSignIn, tvAppName;

    private LinearLayout llTravelCompanions, llObserversWatchingThisTrip, llCompanionOnThisTrip;
    private TextView tvObserversWatchingThisTrip, tvCompanionOnThisTrip, tvPrimaryTraveler;
    private ImageView ivObserversWatchingThisTrip, ivCompanionOnThisTrip;
    private String myUserId = null;
    private RadioButton rbEachWeek, rbSpecificDate;
    private LinearLayout llEachWeek;
    private String previousSelectedStatus = null;
    private SaveTripSpinnerAdapter alertOnJourneySpinnerAdapter, notifyOnItineraryChangeSpinnerAdapter,
            leadTimeSpinnerAdapter, delaySpinnerAdapter;
    private int alertOnJourneyIndex = Constants.DEFAULT_NOTIFY_ON_ALERT_INDEX,
            notifyOnItineraryChangeIndex = Constants.DEFAULT_NOTIFY_ON_ITINERARY_CHANGE_INDEX,
            leadTimeIndex = Constants.DEFAULT_LEAD_TIME_INDEX,
            delayIndex = Constants.DEFAULT_DELAY_INDEX;
    private ItineraryExistence currentItineraryExistence = null;
    private LinearLayout ll_Bottom_tab;

    private void setOriginDefaultToCurrentLocation() {
        if (MyApplication.getInstance().currentLocation != null && atSearchOrigin != null) {
            String originText = atSearchOrigin.getText().toString();
            String currentLocation = systemContext.getString(R.string.current_location_address);
            if (originText.isEmpty() || originText.equalsIgnoreCase(currentLocation)) {
                originPoint.setAddress(currentLocation);
                originPoint.setLat(MyApplication.getInstance().currentLocation.getLatitude());
                originPoint.setLon(MyApplication.getInstance().currentLocation.getLongitude());

                setAddressToAutoCompleteTextView(systemContext.getString(R.string.current_location_address), true);
                showOriginMarker();
            }
        }
    }

    public void searchAddressResult(Intent data) {
        if (data != null) {
            boolean isOrigin = data.getBooleanExtra(Constants.IS_ORIGIN, true);
            String searchedAddress = data.getStringExtra(Constants.SEARCHED_ADDRESS);
            double lat = data.getDoubleExtra(Constants.SEARCHED_LATITUDE, 0.0);
            double lon = data.getDoubleExtra(Constants.SEARCHED_LONGITUDE, 0.0);

            if (searchedAddress != null && searchedAddress.equals(systemContext.getString(R.string.current_location_address))) {
                if (MyApplication.getInstance().currentLocation != null) {
                    lat = MyApplication.getInstance().currentLocation.getLatitude();
                    lon = MyApplication.getInstance().currentLocation.getLongitude();
                }
            }

            setAddressToAutoCompleteTextView(searchedAddress, isOrigin);
            if (isOrigin) {
                originPoint.setAddress(searchedAddress);
                originPoint.setLat(lat);
                originPoint.setLon(lon);

                if (searchedAddress != null && !searchedAddress.isEmpty()) {
                    showOriginMarkersOnMap();
                } else {
                    removeOriginMarkersFromMap();
                }
            } else {
                destinationPoint.setAddress(searchedAddress);
                destinationPoint.setLat(lat);
                destinationPoint.setLon(lon);
                if (searchedAddress != null && !searchedAddress.isEmpty()) {
                    showDestinationMarkersOnMap();
                } else {
                    removeDestinationMarkersFromMap();
                }
            }

            if (currentMode != Constants.HOME_PAGE_MODE_ROUTE_SEARCH_READY) {
                enterRouteSearchReadyMode();
            }

            if (!atSearchOrigin.getText().toString().isEmpty() && !atSearchDestination.getText().toString().isEmpty()) {
                hideKeyboard();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == Constants.ADDRESS_SEARCH_REQUEST_CODE) {
                searchAddressResult(data);
            }
        }
    }

    public PlanTripFragment() {
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
    public static PlanTripFragment newInstance(String param1, String param2) {
        return new PlanTripFragment();
    }

    private Context systemContext;
    private int originalBottomMargin;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Configurations.startOverResetStatus();
        FragmentActivity fragmentActivity = requireActivity();
        systemContext = MyApplication.getInstance().getApplicationContext();
        Convert.mDensity = systemContext.getResources().getDisplayMetrics().density;
        gDm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(gDm);

        loadConfigValuesToModes();
    }

    private void loadConfigValuesToModes() {
        List<ParentItem> parentItems = Utilities.getExpandableListItems(requireActivity());
        for (int i = 0; i < parentItems.size(); i++) {
            ParentItem pItem = parentItems.get(i);
            for (ChildItem c : pItem.getChildren()) {
                if (c.getName().equalsIgnoreCase(getString(R.string.link))) {
                    c.setChecked(Configurations.getBoolean("home_setting_link_enabled", false));
                } else if (c.getName().equalsIgnoreCase(getString(R.string.bus))) {
                    c.setChecked(Configurations.getBoolean("home_setting_bus_enabled", false));
                } else if (c.getName().equalsIgnoreCase(getString(R.string.sounder)) ||
                        c.getName().equalsIgnoreCase(getString(R.string.marta_rail))) {
                    c.setChecked(Configurations.getBoolean("home_setting_rail_enabled", false));
                } else if (c.getName().equalsIgnoreCase(getString(R.string.street_car))) {
                    c.setChecked(Configurations.getBoolean("home_setting_street_car_enabled", false));
                } else if (c.getName().equalsIgnoreCase(getString(R.string.monorail))) {
                    c.setChecked(Configurations.getBoolean("home_setting_monorail_enabled", false));
                } else if (c.getName().equalsIgnoreCase(getString(R.string.water_taxi))) {
                    c.setChecked(Configurations.getBoolean("home_setting_water_taxi_enabled", false));
                } else if (c.getName().equalsIgnoreCase(getString(R.string.ferry))) {
                    c.setChecked(Configurations.getBoolean("home_setting_ferry_enabled", false));
                } else if (c.getName().equalsIgnoreCase(getString(R.string.allow_bike_rental))) {
                    c.setChecked(Configurations.getBoolean("enabled_search_mode_bike_rental", false));
                } else if (c.getName().equalsIgnoreCase(getString(R.string.allow_scooter_rental))) {
                    c.setChecked(Configurations.getBoolean("enabled_search_mode_scooter_rental", false));
                }
                c.setCheckedTemp(c.isChecked());
            }
        }
    }

    private void loadFromOtherPageAction(ItineraryLegPoint origin, ItineraryLegPoint destination) {

        if (origin != null) {
            String searchedAddress = origin.getName();
            setAddressToAutoCompleteTextView(origin.getName(), true);
            originPoint.setAddress(searchedAddress);
            originPoint.setLat(origin.getLat());
            originPoint.setLon(origin.getLon());

            if (searchedAddress != null && !searchedAddress.isEmpty()) {
                showOriginMarkersOnMap();
            } else {
                removeOriginMarkersFromMap();
            }
        }

        if (destination != null) {
            String searchedAddress = destination.getName();
            setAddressToAutoCompleteTextView(searchedAddress, false);

            destinationPoint.setAddress(searchedAddress);
            destinationPoint.setLat(destination.getLat());
            destinationPoint.setLon(destination.getLon());
            if (searchedAddress != null && !searchedAddress.isEmpty()) {
                showDestinationMarkersOnMap();
            } else {
                removeDestinationMarkersFromMap();
            }
        }

        llItineraryList.setVisibility(View.GONE);
        rlRouteSearchAddress.setVisibility(View.GONE);

        llItineraryDetail.setVisibility(View.GONE);
        llCaloriesBurnedDetail.setVisibility(View.GONE);
        if (llMoreInfoCaloriesBurned != null) {
            llMoreInfoCaloriesBurned.setContentDescription(getString(R.string.calories_info_show_details));
            llMoreInfoCaloriesBurnedAccessibility.setContentDescription(getString(R.string.calories_info_show_details));
        }
        llSearchBar.setVisibility(View.VISIBLE);
        rlMapView.setVisibility(View.VISIBLE);
        ivSaveTrip.setVisibility(View.GONE);
        removeRoutePolylineFromMap();
        removeRouteMarkerViewsFromMap();

        if (currentMode != Constants.HOME_PAGE_MODE_ROUTE_SEARCH_READY) {
            currentMode = Constants.HOME_PAGE_MODE_START_OVER;
            modeStack.clear();
            modeStack.add(currentMode);

            enterRouteSearchReadyMode();
        }
    }

    private void loadFromOtherPageAction(boolean isOrigin, String searchedAddress, double lat, double lon) {
        setAddressToAutoCompleteTextView(searchedAddress, isOrigin);
        if (isOrigin) {
            originPoint.setAddress(searchedAddress);
            originPoint.setLat(lat);
            originPoint.setLon(lon);

            if (searchedAddress != null && !searchedAddress.isEmpty()) {
                showOriginMarkersOnMap();
            } else {
                removeOriginMarkersFromMap();
            }
        } else {
            destinationPoint.setAddress(searchedAddress);
            destinationPoint.setLat(lat);
            destinationPoint.setLon(lon);
            if (searchedAddress != null && !searchedAddress.isEmpty()) {
                showDestinationMarkersOnMap();
            } else {
                removeDestinationMarkersFromMap();
            }
        }

        if (svAddressSearch != null) {
            svAddressSearch.setVisibility(View.GONE);
        }
        llItineraryList.setVisibility(View.GONE);
        rlRouteSearchAddress.setVisibility(View.GONE);

        llItineraryDetail.setVisibility(View.GONE);
        llCaloriesBurnedDetail.setVisibility(View.GONE);
        if (llMoreInfoCaloriesBurned != null) {
            llMoreInfoCaloriesBurned.setContentDescription(getString(R.string.calories_info_show_details));
            llMoreInfoCaloriesBurnedAccessibility.setContentDescription(getString(R.string.calories_info_show_details));
        }
        llSearchBar.setVisibility(View.VISIBLE);
        rlMapView.setVisibility(View.VISIBLE);
        ivSaveTrip.setVisibility(View.GONE);
        removeRoutePolylineFromMap();
        removeRouteMarkerViewsFromMap();

        if (currentMode != Constants.HOME_PAGE_MODE_ROUTE_SEARCH_READY) {
            currentMode = Constants.HOME_PAGE_MODE_START_OVER;
            modeStack.clear();
            modeStack.add(currentMode);

            enterRouteSearchReadyMode();
        }
    }

    public void loadFromStopViewer(final boolean isOrigin, final String searchedAddress, final double lat, final double lon) {
        Runnable mRunnable = new Runnable() {
            @Override
            public void run() {
                loadFromOtherPageAction(isOrigin, searchedAddress, lat, lon);
            }
        };

        new Handler().postDelayed(mRunnable, 100);
    }

    public void loadFromMyTrips(final ItineraryLegPoint origin, final ItineraryLegPoint destination) {
        Runnable mRunnable = new Runnable() {
            @Override
            public void run() {
                loadFromOtherPageAction(origin, destination);
            }
        };

        new Handler().postDelayed(mRunnable, 100);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (mFragmentView != null) {
            return mFragmentView;
        }

        if (this.requireActivity() instanceof TabActivity) {
            llBottom_tab = ((TabActivity) this.requireActivity()).getBottomTab();
            flFragmentHolder = ((TabActivity) this.requireActivity()).getFragmentHolder();
        }


        if (Utilities.checkIfToDisplayAccessibilityUI(requireActivity())) {
            mFragmentView = inflater.inflate(R.layout.fragment_plan_trip_accessibility, container, false);

        } else {
            mFragmentView = inflater.inflate(R.layout.fragment_plan_trip, container, false);
        }

        // Capture the original bottom margin.
        if (flFragmentHolder != null) {
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) flFragmentHolder.getLayoutParams();
            originalBottomMargin = layoutParams.bottomMargin;
        }

        // Inflate the layout for this fragment
        setupMapView(savedInstanceState);

        setupLocateMeButton();
        setupLayers();
        setupAndAddFeatureListeners();
        setupSearchBar();
        setupSettings();
        setupSearchDateTime();
        setupItineraryListLayout();
        setupRouteSearchAddressLayout();

        setupItineraryDetail();
        setupServiceAlerts();
        setupAdvancedPref();
        setupIndoorNavigation();
        showVersionIfNeeded();

        modeStack.add(currentMode);

        requestLocationPermission();
        return mFragmentView;
    }

    public void resetAction() {
        MyApplication.getInstance().gbStartOver = false;
        refreshMapStyleControllers();
        refreshParkRideLocationControllers();
        refreshTransitStopsControllers();
        refreshRentalVehiclesControllers();

        Utilities.resetItinerarySearchModeSettings();
        Utilities.resetItinerarySearchSettings();

        ivSearchModesControlsChanged.setVisibility(View.GONE);
        ivSettingsChanged.setVisibility(View.GONE);


        initSearchBar();
        initSettings();
        initItineraryListLayout();

        hideHomeSetting();
        hideHomeDateTime();
        hideLayerPopup();
        llItineraryList.setVisibility(View.GONE);
        rlRouteSearchAddress.setVisibility(View.GONE);
        llItineraryDetail.setVisibility(View.GONE);
        llCaloriesBurnedDetail.setVisibility(View.GONE);
        if (llMoreInfoCaloriesBurned != null) {
            llMoreInfoCaloriesBurned.setContentDescription(getString(R.string.calories_info_show_details));
            llMoreInfoCaloriesBurnedAccessibility.setContentDescription(getString(R.string.calories_info_show_details));
        }
        llSearchBar.setVisibility(View.VISIBLE);
        rlMapView.setVisibility(View.VISIBLE);

        atSearchOrigin.setText("");
        atSearchDestination.setText("");
        originPoint.setAddress("");
        destinationPoint.setAddress("");

        tv_itinerary_origin.setText("");
        tv_itinerary_Destination.setText("");

        removeOriginMarkersFromMap();
        removeDestinationMarkersFromMap();
        removeRoutePolylineFromMap();
        removeRouteMarkerViewsFromMap();

        //reset to leave now status
        setStatusToLeaveNow();

        setOriginDefaultToCurrentLocation();
        ivSaveTrip.setVisibility(View.GONE);
        resetDepartureDateTime();

        resetAdvancedPreferences();
    }

    private void resetDepartureDateTime() {
        isLeaveNow = true;
        isDepartAt = false;
        isArriveBy = false;
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());

        refreshSearchTimeWithHourMin(calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE));

        resetTimeAndDatePicker();

        refreshSearchTime();
        refreshSearchDate();
    }

    private void startOverAction() {
        currentMode = Constants.HOME_PAGE_MODE_START_OVER;
        modeStack.clear();
        modeStack.add(currentMode);

        refreshMapStyleControllers();
        refreshParkRideLocationControllers();
        refreshTransitStopsControllers();
        refreshRentalVehiclesControllers();
        initSearchBar();
        initSettings();
        initItineraryListLayout();

        llOrigin.setVisibility(View.GONE);
        ivSearchBarReset.setVisibility(View.GONE);
        tvOriginUnderline.setVisibility(View.GONE);
        clTimeOptions.setVisibility(View.GONE);
        spSearchOptions.setVisibility(View.GONE);
        llSearchModeIcons.setVisibility(View.GONE);

        if (Utilities.checkIfToDisplayAccessibilityUI(requireActivity())) {
            llSwitchLocationAccessibility.setVisibility(View.GONE);
        }

        rlSettingSearch.setVisibility(View.GONE);
        if (Utilities.checkIfToDisplayAccessibilityUI(requireActivity())) {
            llPlanTripAccessbility.setVisibility(View.GONE);
        }
        llSearchMode.setVisibility(View.GONE);

        hideHomeSetting();
        hideHomeDateTime();
        hideLayerPopup();
        llItineraryList.setVisibility(View.GONE);
        rlRouteSearchAddress.setVisibility(View.GONE);
        llItineraryDetail.setVisibility(View.GONE);
        llCaloriesBurnedDetail.setVisibility(View.GONE);
        if (llMoreInfoCaloriesBurned != null) {
            llMoreInfoCaloriesBurned.setContentDescription(getString(R.string.calories_info_show_details));
            llMoreInfoCaloriesBurnedAccessibility.setContentDescription(getString(R.string.calories_info_show_details));
        }
        llSearchBar.setVisibility(View.VISIBLE);
        rlMapView.setVisibility(View.VISIBLE);

        atSearchOrigin.setText("");
        atSearchDestination.setText("");
        originPoint.setAddress("");
        destinationPoint.setAddress("");
        tv_itinerary_origin.setText("");
        tv_itinerary_Destination.setText("");
        removeOriginMarkersFromMap();
        removeDestinationMarkersFromMap();
        removeRoutePolylineFromMap();
        removeRouteMarkerViewsFromMap();

        //reset to leave now status
        setStatusToLeaveNow();

        refreshMapStyle();

        setOriginDefaultToCurrentLocation();
        ivSaveTrip.setVisibility(View.GONE);
    }

    private void enterRouteSearchReadyMode() {
        currentMode = Constants.HOME_PAGE_MODE_ROUTE_SEARCH_READY;
        modeStack.add(currentMode);
        setupAdvancedPref();

        llOrigin.setVisibility(View.VISIBLE);

        if (clTimeOptions != null) {
            clTimeOptions.setVisibility(View.VISIBLE);
        }
        spSearchOptions.setVisibility(View.VISIBLE);
        llSearchModeIcons.setVisibility(View.VISIBLE);
        llSearchDestination.setBackgroundResource(R.drawable.black_frame_rounded_white_bg);

        handleAdvancePreferenceLargeView();
        setupSearchDateTime();

        rlSettingSearch.setVisibility(View.VISIBLE);
        iv_mapview_destination_icon.setVisibility(View.VISIBLE);

        if (Utilities.checkIfToDisplayAccessibilityUI(requireActivity())) {
            llPlanTripAccessbility.setVisibility(View.VISIBLE);
            llPlanTripSearch.setVisibility(View.GONE);
            ivClock.setVisibility(View.GONE);


        } else {
            llPlanTripAccessbility.setVisibility(View.GONE);
            llPlanTripSearch.setVisibility(View.VISIBLE);
            ivClock.setVisibility(View.VISIBLE);
        }
    }

    private void goBackToRouteSearchReady() {
        enableAccessbilityForPlanTrip();
        currentMode = Constants.HOME_PAGE_MODE_ROUTE_SEARCH_READY;

        // Go back to the default option
        itinerarySortSpinnerIndex = 0;
        itinerarySortSpinner.setSelection(itinerarySortSpinnerIndex);
        itinerarySortSpinnerAccessibility.setSelection(itinerarySortSpinnerIndex);
        Configurations.setInt("itinerary_sort_index", itinerarySortSpinnerIndex);

        modeStack.clear();
        modeStack.add(Constants.HOME_PAGE_MODE_START_OVER);
        modeStack.add(Constants.HOME_PAGE_MODE_ROUTE_SEARCH_READY);

        llOrigin.setVisibility(View.VISIBLE);

        llSearchDestination.setBackgroundResource(R.drawable.black_frame_rounded_white_bg);

        llSearchModeIcons.setVisibility(View.VISIBLE);
        spSearchOptions.setVisibility(View.VISIBLE);

        //update searchOptions spinner
        searchOptionsSpinnerIndex = tempSearchOptionsSpinnerIndex;

        handleAdvancePreferenceLargeView();
        setupSearchDateTime();

        rlSettingSearch.setVisibility(View.VISIBLE);

        if (Utilities.checkIfToDisplayAccessibilityUI(requireActivity())) {
            llPlanTripAccessbility.setVisibility(View.VISIBLE);
            llPlanTripSearch.setVisibility(View.GONE);
            ivClock.setVisibility(View.GONE);
        } else {
            llPlanTripAccessbility.setVisibility(View.GONE);
            llPlanTripSearch.setVisibility(View.VISIBLE);
            ivClock.setVisibility(View.VISIBLE);
        }

        llItineraryList.setVisibility(View.GONE);
        rlRouteSearchAddress.setVisibility(View.GONE);
        llItineraryDetail.setVisibility(View.GONE);
        llCaloriesBurnedDetail.setVisibility(View.GONE);
        if (llMoreInfoCaloriesBurned != null) {
            llMoreInfoCaloriesBurned.setContentDescription(getString(R.string.calories_info_show_details));
            llMoreInfoCaloriesBurnedAccessibility.setContentDescription(getString(R.string.calories_info_show_details));
        }
        llSearchBar.setVisibility(View.VISIBLE);
        rlMapView.setVisibility(View.VISIBLE);

        removeRoutePolylineFromMap();
        removeRouteMarkerViewsFromMap();

        ivSaveTrip.setVisibility(View.GONE);

        currentSelectedItinerary = null;
        //move origin/destination markers back
        if (mvOrigin != null) {
            mvOrigin.setLatLng(new LatLng(originPoint.getLat(), originPoint.getLon()));
            mvOrigin.setMarkerViewLatLng(new LatLng(originPoint.getLat(), originPoint.getLon()));
        }
        if (mvDestination != null) {
            mvDestination.setLatLng(new LatLng(destinationPoint.getLat(), destinationPoint.getLon()));
            mvDestination.setMarkerViewLatLng(new LatLng(destinationPoint.getLat(), destinationPoint.getLon()));
        }
        moveMapToShowOriginAndDestination();
    }

    private void handleAdvancePreferenceLargeView() {
        llSearchBarHolder.removeAllViews();
        if (Utilities.checkIfToDisplayAccessibilityUI(requireActivity())) {
            ivAddressSwitch.setVisibility(View.VISIBLE);
            View newLayout = LayoutInflater.from(requireActivity()).inflate(R.layout.layout_options_dates_accessibility, null);
            if (clTimeOptions != null && clTimeOptions.getParent() != null) {
                ((ViewGroup) clTimeOptions.getParent()).removeView(clTimeOptions);
            }
            llSearchBarHolder.addView(newLayout);

            //for advanced pref page
            llWalkSpeedHolder.setOrientation(LinearLayout.VERTICAL);

            LinearLayout llDepartAtDateTime = mFragmentView.findViewById(R.id.ll_depart_after_date_time_advance_pref);
            LinearLayout llArriveByDateTime = mFragmentView.findViewById(R.id.ll_arrive_by_date_time_advance_pref);

            llDepartAtDateTime.setOrientation(LinearLayout.VERTICAL);
            llArriveByDateTime.setOrientation(LinearLayout.VERTICAL);

            RelativeLayout rlOptionTimeDepart = mFragmentView.findViewById(R.id.rl_option_time_advance_pref);
            RelativeLayout rlOptionDateDepart = mFragmentView.findViewById(R.id.rl_option_date_advance_pref);
            RelativeLayout rlOptionTimeArrive = mFragmentView.findViewById(R.id.rl_arrive_by_option_time_advance_pref);
            RelativeLayout rlOptionDateArrive = mFragmentView.findViewById(R.id.rl_arrive_by_option_date_advance_pref);

            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) rlOptionTimeDepart.getLayoutParams();
            layoutParams.width = LinearLayout.LayoutParams.MATCH_PARENT;

            rlOptionTimeDepart.setLayoutParams(layoutParams);
            rlOptionTimeArrive.setLayoutParams(layoutParams);

            rlOptionDateDepart.setLayoutParams(layoutParams);
            rlOptionDateArrive.setLayoutParams(layoutParams);

            radioGrid.setColumnCount(1);

        } else {
            ivAddressSwitch.setVisibility(View.VISIBLE);
            View newLayout = LayoutInflater.from(requireActivity()).inflate(R.layout.layout_options_dates, null);
            if (clTimeOptions != null && clTimeOptions.getParent() != null) {
                ((ViewGroup) clTimeOptions.getParent()).removeView(clTimeOptions);
            }
            llSearchBarHolder.addView(newLayout);

            //for advanced pref page
            llWalkSpeedHolder.setOrientation(LinearLayout.HORIZONTAL);
            LinearLayout llDepartAtDateTime = mFragmentView.findViewById(R.id.ll_depart_after_date_time_advance_pref);
            LinearLayout llArriveByDateTime = mFragmentView.findViewById(R.id.ll_arrive_by_date_time_advance_pref);

            llArriveByDateTime.setOrientation(LinearLayout.HORIZONTAL);
            llDepartAtDateTime.setOrientation(LinearLayout.HORIZONTAL);

            RelativeLayout rlOptionTimeDepart = mFragmentView.findViewById(R.id.rl_option_time_advance_pref);
            RelativeLayout rlOptionDateDepart = mFragmentView.findViewById(R.id.rl_option_date_advance_pref);
            RelativeLayout rlOptionTimeArrive = mFragmentView.findViewById(R.id.rl_arrive_by_option_time_advance_pref);
            RelativeLayout rlOptionDateArrive = mFragmentView.findViewById(R.id.rl_arrive_by_option_date_advance_pref);

            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) rlOptionTimeDepart.getLayoutParams();
            layoutParams.width = 0;

            rlOptionTimeDepart.setLayoutParams(layoutParams);
            rlOptionTimeArrive.setLayoutParams(layoutParams);

            rlOptionDateDepart.setLayoutParams(layoutParams);
            rlOptionDateArrive.setLayoutParams(layoutParams);

            radioGrid.setColumnCount(2);
        }

        for (int i = 0; i < radioGrid.getChildCount(); i++) {
            View child = radioGrid.getChildAt(i);
            if (child instanceof RadioButton) {
                RadioButton radioButton = (RadioButton) child;
                radioButton.setOnClickListener(v -> {
                    String btnName = ((RadioButton) v).getText().toString();

                    if (btnName.equalsIgnoreCase(getString(R.string.fewest_transfers))) {
                        boolean isCheck = Configurations.getBoolean("enabled_search_mode_fewest_transfers", true);
                        if (isCheck) {
                            ((RadioButton) v).setChecked(false);
                            Configurations.setBoolean("enabled_search_mode_fewest_transfers", false);
                        } else {
                            ((RadioButton) v).setChecked(true);
                            Configurations.setBoolean("enabled_search_mode_fewest_transfers", true);
                        }
                    } else if (btnName.equalsIgnoreCase(getString(R.string.fastest))) {
                        boolean isCheck = Configurations.getBoolean("enabled_search_mode_fastest", true);
                        if (isCheck) {
                            ((RadioButton) v).setChecked(false);
                            Configurations.setBoolean("enabled_search_mode_fastest", false);
                        } else {
                            ((RadioButton) v).setChecked(true);
                            Configurations.setBoolean("enabled_search_mode_fastest", true);
                        }
                    } else if (btnName.equalsIgnoreCase(getString(R.string.less_walking))) {
                        boolean isCheck = Configurations.getBoolean("enabled_search_mode_avoid_walking", true);
                        if (isCheck) {
                            ((RadioButton) v).setChecked(false);
                            Configurations.setBoolean("enabled_search_mode_avoid_walking", false);
                        } else {
                            ((RadioButton) v).setChecked(true);
                            Configurations.setBoolean("enabled_search_mode_avoid_walking", true);
                        }

                    }
                });
            }
        }
        setupAdvancedPref();
    }

    private void enterRouteSearchMode() {
        currentMode = Constants.HOME_PAGE_MODE_ITINERARY_LIST;
        modeStack.add(currentMode);

        if (originPoint.getAddress() != null) {
            tvRouteSearchOrigin.setText(originPoint.getAddress());
        }

        if (destinationPoint.getAddress() != null) {
            tvRouteSearchDestination.setText(destinationPoint.getAddress());
        }
        itineraryListExpandedStatus = Constants.EXPANDED_STATUS_UNEXPANDED;

        llItineraryList.setVisibility(View.VISIBLE);
        rlRouteSearchAddress.setVisibility(View.VISIBLE);

        llSearchBar.setVisibility(View.GONE);

        hideHomeSetting();
        hideHomeDateTime();
    }

    private void enterItineraryDetailPage() {
        currentMode = Constants.HOME_PAGE_MODE_ITINERARY_DETAIL;
        modeStack.add(currentMode);

        isItineraryDetailExpanded = true;
        currentLegGeometry = null;
        showMaxItineraryDetailHeight();
        llItineraryDetail.setVisibility(View.VISIBLE);
        rlMapView.setVisibility(View.VISIBLE);
        llItineraryList.setVisibility(View.GONE);

        moveMapToShowRouteInDetail();

        rlRouteSearchAddressCollapsed.setVisibility(View.VISIBLE);
        llRouteSearchAddressExpanded.setVisibility(View.GONE);
        enableAccessbilityForPlanTrip();
    }

    private void enterStopViewerPage(String mode) {
        if (getActivity() != null && stopViewerItineraryLegPoint != null) {
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

            try {
                requireActivity().startActivityForResult(intent, Constants.STOP_VIEWER_REQUEST_CODE);
            } catch (Exception e) {
                Log.e("enterStopViewerPage", e.getMessage());
            }
        }
    }

    private void checkNeedMoveMap(LatLng latitude) {
        customView.measure(
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));

        PointF pointF = gMapboxMap.getProjection().toScreenLocation(latitude);
        float offsetX = 0.0f;
        float offsetY = 0.0f;
        boolean needMoveMap = false;
        if (pointF.x < customView.getMeasuredWidth() / 2.0f) {
            offsetX = pointF.x - customView.getMeasuredWidth() / 2.0f;
            needMoveMap = true;
        } else if ((pointF.x + customView.getMeasuredWidth() / 2.0f) > gDm.widthPixels) {
            offsetX = pointF.x + customView.getMeasuredWidth() / 2.0f - gDm.widthPixels;
            needMoveMap = true;
        }

        if (pointF.y < customView.getMeasuredHeight()) {
            offsetY = pointF.y - customView.getMeasuredHeight();
            needMoveMap = true;
        }

        if (needMoveMap) {
            PointF currentCenterPointF = gMapboxMap.getProjection().toScreenLocation(gMapboxMap.getCameraPosition().target);

            currentCenterPointF.x = currentCenterPointF.x + offsetX;
            currentCenterPointF.y = currentCenterPointF.y + offsetY;

            CameraPosition position = new CameraPosition.Builder()
                    .target(gMapboxMap.getProjection().fromScreenLocation(currentCenterPointF))
                    .build();
            gMapboxMap.moveCamera(CameraUpdateFactory.newCameraPosition(position));
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
            }
        });

        llMarkerBtnFromHere.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                customView.setVisibility(View.GONE);

                atSearchOrigin.setText(tvMapAddress.getText());
                originPoint.setAddress(tvMapAddress.getText().toString());
                tv_itinerary_origin.setText(tvMapAddress.getText());

                if (markerViewLatlng != null) {
                    originPoint.setLat(markerViewLatlng.getLatitude());
                    originPoint.setLon(markerViewLatlng.getLongitude());
                }

                showOriginMarkersOnMap();
                goBackToRouteSearchReady();
            }
        });

        llMarkerBtnToHere.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                customView.setVisibility(View.GONE);
                atSearchDestination.setText(tvMapAddress.getText());
                destinationPoint.setAddress(tvMapAddress.getText().toString());
                tv_itinerary_Destination.setText(address);
                if (markerViewLatlng != null) {
                    destinationPoint.setLat(markerViewLatlng.getLatitude());
                    destinationPoint.setLon(markerViewLatlng.getLongitude());
                }

                showDestinationMarkersOnMap();
                goBackToRouteSearchReady();
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

    private void moveMapToShowMarkerView() {
        if (markerViewLatlng != null) {
            double topPadding;
            if (currentMode == Constants.HOME_PAGE_MODE_ITINERARY_DETAIL) {
                topPadding = 0;
            } else {
                topPadding = Convert.dpToPx(300);
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
            gMapboxMap.moveCamera(CameraUpdateFactory.newCameraPosition(position));
        }
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
            gMapStyle.addLayer(unclusteredParkRide);
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        ParkRideManager.getInstance().removeListener(parkRideListener);
        TransitStopManager.getInstance().removeListener(transitStopListener);
    }

    @Override
    public void onResume() {
        super.onResume();

        startFeatureManagers();

        if (timePickerDialog != null && rlHomeDateTime.getVisibility() == View.VISIBLE) {
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, timePickerHourTmp);
            calendar.set(Calendar.MINUTE, timePickerMinTmp);
            String time = Utilities.formatTimestamp(systemContext, calendar.getTimeInMillis());
            tvDepartAtOptionTime.setText(time);
            tvDepartAtOptionTime.setContentDescription(time);
            tvDepartAtOptionTime.sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_FOCUSED);

            tvArriveByOptionTime.setText(time);
            tvArriveByOptionTime.setContentDescription(time);
            tvArriveByOptionTime.sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_FOCUSED);

        }

        if (onPausedZoom != null && onPausedMapLatlng != null
                && gMapboxMap != null && gMapboxMap.getCameraPosition() != null) {
            if (onPausedZoom != gMapboxMap.getCameraPosition().zoom
                    || onPausedMapLatlng != gMapboxMap.getCameraPosition().target) {
                CameraPosition position = new CameraPosition.Builder()
                        .target(onPausedMapLatlng)
                        .zoom(onPausedZoom)
                        .build();
                gMapboxMap.moveCamera(CameraUpdateFactory.newCameraPosition(position));
            }
        }
        tv_notify_title = mFragmentView.findViewById(R.id.tv_notify_title);
        tv_notify_title.setText(Utilities.getNotifyTitleString());

        ll_notification_layout = mFragmentView.findViewById(R.id.ll_notification_layout);
        ll_notification_warning = mFragmentView.findViewById(R.id.ll_notification_warning);

        if (tv_notify_title.getText().toString().equalsIgnoreCase(getString(R.string.notify_via_email))) {
            ll_notification_layout.setVisibility(View.GONE);
            ll_notification_warning.setVisibility(View.VISIBLE);
        } else {
            ll_notification_layout.setVisibility(View.VISIBLE);
            ll_notification_warning.setVisibility(View.GONE);
        }

        if (rlIndoorNavigationPopup.getVisibility() == View.VISIBLE) {
            hideKeyboard();
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
    public void onPause() {
        super.onPause();
        stopFeatureManagers();
        if (gMapboxMap != null && gMapboxMap.getCameraPosition() != null) {
            onPausedZoom = gMapboxMap.getCameraPosition().zoom;
            onPausedMapLatlng = gMapboxMap.getCameraPosition().target;
        }

        hideLayerPopup();
    }

    private void startFeatureManagers() {
        ParkRideManager.getInstance().start();
        TransitStopManager.getInstance().start();
    }

    private void stopFeatureManagers() {
        ParkRideManager.getInstance().stop();
        TransitStopManager.getInstance().stop();
    }

    private void setupMapView(Bundle savedInstanceState) {
        rlMapView = mFragmentView.findViewById(R.id.rl_map_view);
        mapView = mFragmentView.findViewById(R.id.map_view);
        mapView.onCreate(savedInstanceState);

        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(MapboxMap mapboxMap) {
                gMapboxMap = mapboxMap;

                //move the compass icon to the bottom left side
                gMapboxMap.getUiSettings().setCompassMargins(0, gDm.heightPixels - Convert.dpToPx(168),
                        gDm.widthPixels - Convert.dpToPx(60), 0);

                markerViewManager = new MarkerViewManager(mapView, mapboxMap);

                refreshMapStyle();

                moveMapToDefaultLocation();

                gMapboxMap.addOnMapClickListener(new MapboxMap.OnMapClickListener() {
                    @Override
                    public boolean onMapClick(@NonNull LatLng point) {
                        hideLayerPopup();

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
                                            if (Math.abs((double) (feature.getNumberProperty(entry.getKey())) - Resource.ID_PARK_RIDE) < 0.01) {
                                                String parkRideString = feature.getStringProperty("jsonString");
                                                if (parkRideString != null) {
                                                    Gson gson = new Gson();
                                                    try {
                                                        selectedParkRideLocation = gson.fromJson(parkRideString, ParkRideLocation.class);
                                                        if (selectedParkRideLocation != null) {
                                                            currentSelectedLocationMode = Constants.SELECTED_LOCATION_MODE_PARK_RIDE;
                                                            addMarkerViewIfNeeded(new LatLng(selectedParkRideLocation.getLat(), selectedParkRideLocation.getLon()));
                                                            //addMarkerViewIfNeeded(point);
                                                            return true;
                                                        }
                                                    } catch (Exception e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                            } else if (Math.abs((double) (feature.getNumberProperty(entry.getKey())) - Resource.ID_TRANSIT_STOP) < 0.01) {
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
                                            } else if (Math.abs((double) (feature.getNumberProperty(entry.getKey())) - Resource.ID_RENTAL_VEHICLE) < 0.01) {
                                                String rentalVehicleString = feature.getStringProperty("jsonString");
                                                if (rentalVehicleString != null) {
                                                    Gson gson = new Gson();
                                                    try {
                                                        selectedRentalVehicle = gson.fromJson(rentalVehicleString, RentalVehicle.class);
                                                        if (selectedRentalVehicle != null) {
                                                            currentSelectedLocationMode = Constants.SELECTED_LOCATION_MODE_RENTAL_VEHICLE;
                                                            addMarkerViewIfNeeded(new LatLng(selectedRentalVehicle.getLat(), selectedRentalVehicle.getLon()));
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

                gMapboxMap.addOnMoveListener(new MapboxMap.OnMoveListener() {
                    @Override
                    public void onMoveBegin(@NonNull MoveGestureDetector detector) {

                    }

                    @Override
                    public void onMove(@NonNull MoveGestureDetector detector) {
                        ivLocateMe.setColorFilter(null);
                        ivLocateMeBg.setImageResource(R.drawable.ic_layer_bkg_default);
                        ivLocateMeBg.setColorFilter(null);
                    }

                    @Override
                    public void onMoveEnd(@NonNull MoveGestureDetector detector) {

                    }
                });
            }
        });
    }

    private void moveMapToDefaultLocation() {
        gMapboxMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder()
                .target(new LatLng(LATITUDE_DEFAULT, LONGITUDE_DEFAULT))
                .zoom(ZOOM_DEFAULT)
                .build()));
    }

    private void setupLocateMeButton() {
        RelativeLayout rlLocateMe = mFragmentView.findViewById(R.id.rl_locate_me);
        ivLocateMe = mFragmentView.findViewById(R.id.iv_locate_me);
        ivLocateMeBg = mFragmentView.findViewById(R.id.iv_locate_me_bg);
        rlLocateMe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideLayerPopup();
                if (MyApplication.getInstance().currentLocation != null && gMapboxMap != null) {
                    gMapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder()
                            .target(new LatLng(MyApplication.getInstance().currentLocation.getLatitude(),
                                    MyApplication.getInstance().currentLocation.getLongitude()))
                            .build()));
                    ivLocateMe.setColorFilter(getResources().getColor(android.R.color.white));
                    ivLocateMeBg.setImageResource(R.drawable.ic_layer_bkg_on);
                    ivLocateMeBg.setColorFilter(getResources().getColor(R.color.colorPrimary));
                } else {
                    Toast.makeText(systemContext, systemContext.getString(R.string.current_location_not_available), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                Log.d("map", "Granted location permission");
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    startLocationListener();
                }
                requestPushPermission();
            }
        }
    }

    private void gotoAppPermissionSetting() {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", systemContext.getPackageName(), null);
        intent.setData(uri);
        startActivity(intent);
    }

    private void requestLocationPermission() {
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
// Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION) || ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.ACCESS_COARSE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
                alertDialogBuilder
                        .setMessage(getString(R.string.request_fine_location_explanation))
                        .setCancelable(false)
                        .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                if (Build.VERSION.SDK_INT == Build.VERSION_CODES.Q) {
                                    ActivityCompat.requestPermissions(getActivity(),
                                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_BACKGROUND_LOCATION},
                                            MY_PERMISSIONS_REQUEST_FINE_LOCATION);
                                } else {
                                    ActivityCompat.requestPermissions(getActivity(),
                                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                                            MY_PERMISSIONS_REQUEST_FINE_LOCATION);
                                }
                            }
                        });

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
                explained = true;

            } else {

                // No explanation needed, we can request the permission.
                if (!explained) {
                    if (Build.VERSION.SDK_INT == Build.VERSION_CODES.Q) {
                        ActivityCompat.requestPermissions(getActivity(),
                                new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_BACKGROUND_LOCATION},
                                MY_PERMISSIONS_REQUEST_FINE_LOCATION);
                    } else {
                        ActivityCompat.requestPermissions(getActivity(),
                                new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                                MY_PERMISSIONS_REQUEST_FINE_LOCATION);
                    }
                } else {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
                    alertDialogBuilder
                            .setMessage(getString(R.string.request_fine_location_confirmation))
                            .setCancelable(false)
                            .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    gotoAppPermissionSetting();
                                }
                            }).setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    requestPushPermission();
                                }
                            });

                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();

                }
            }
        } else {
            startLocationListener();
            requestPushPermission();
        }
    }

    private void showVersionIfNeeded() {
        if (getActivity() != null && mFragmentView != null) {
            TextView tvVersion = mFragmentView.findViewById(R.id.tv_version);
            if (tvVersion != null) {
                if (Configurations.getBoolean("enable_main_menu", true)) {
                    tvVersion.setVisibility(View.GONE);
                } else {
                    String version = Utilities.getVersion(systemContext);
                    tvVersion.setText(version);
                    tvVersion.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    public void requestPushPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && getActivity() != null) //TIRAMISU is 33
        {
            if (ContextCompat.checkSelfPermission(getActivity(),
                    Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
// Should we show an explanation?
                if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                        Manifest.permission.POST_NOTIFICATIONS)) {

                    // Show an explanation to the user *asynchronously* -- don't block
                    // this thread waiting for the user's response! After the user
                    // sees the explanation, try again to request the permission.

                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
                    alertDialogBuilder
                            .setMessage(getString(R.string.request_post_push_explanation))
                            .setCancelable(false)
                            .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    ActivityCompat.requestPermissions(getActivity(),
                                            new String[]{Manifest.permission.POST_NOTIFICATIONS},
                                            MY_PERMISSIONS_REQUEST_POST_PUSH);
                                }
                            });

                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                    explained = true;

                } else {

                    // No explanation needed, we can request the permission.
                    if (!explained) {
                        ActivityCompat.requestPermissions(getActivity(),
                                new String[]{Manifest.permission.POST_NOTIFICATIONS},
                                MY_PERMISSIONS_REQUEST_POST_PUSH);
                    } else {
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
                        alertDialogBuilder
                                .setMessage(getString(R.string.request_post_push_confirmation))
                                .setCancelable(false)
                                .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        gotoAppPermissionSetting();
                                    }
                                }).setNegativeButton(getString(R.string.cancel), null);

                        AlertDialog alertDialog = alertDialogBuilder.create();
                        alertDialog.show();

                    }
                }
            }
        }
    }

    private void stopLocationListener() {
        if (locationListener != null) {
            LocationManager locationManager = (LocationManager) systemContext.getSystemService(LOCATION_SERVICE);
            locationManager.removeUpdates(locationListener);
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

    private void resetIndoorNavigationPopupContentHeight() {
        if (mFragmentView != null && Utilities.checkIfToDisplayAccessibilityUI(getApplicationContext())) {
            ScrollView svPopupContent = mFragmentView.findViewById(R.id.sv_popup_content);

            svPopupContent.measure(
                    View.MeasureSpec.makeMeasureSpec(gDm.widthPixels - Convert.dpToPx(40),
                            View.MeasureSpec.EXACTLY),
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
            LinearLayout.LayoutParams svPopupContentLayoutParams = (LinearLayout.LayoutParams) svPopupContent.getLayoutParams();
            //there is 20dp padding top and bottom
            if (gDm.heightPixels > Convert.dpToPx(340) &&
                    svPopupContent.getMeasuredHeight() > (gDm.heightPixels - Convert.dpToPx(340))) {
                svPopupContentLayoutParams.height = gDm.heightPixels - Convert.dpToPx(300);
            } else {
                svPopupContentLayoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT;
            }
            svPopupContent.setLayoutParams(svPopupContentLayoutParams);
        }
    }

    private void setupIndoorNavigation() {
        ivIndoorNavigation = mFragmentView.findViewById(R.id.iv_indoor_navigation);
        rlIndoorNavigationPopup = mFragmentView.findViewById(R.id.rl_indoor_navigation_popup);
        tvIndoorNavigationPopupAddress = mFragmentView.findViewById(R.id.tv_indoor_navigation_address);
        tvIndoorNavigationPopupCancel = mFragmentView.findViewById(R.id.tv_indoor_navigation_popup_cancel);
        tvIndoorNavigationPopupOpen = mFragmentView.findViewById(R.id.tv_indoor_navigation_popup_open);

        ivIndoorNavigation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (MyApplication.getInstance().currentJmapTriggerableLocation != null) {
                    Intent intent = new Intent(getActivity(), IndoorNavigationActivity.class);
                    startActivity(intent);
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
                if (MyApplication.getInstance().currentJmapTriggerableLocation != null) {
                    Intent intent = new Intent(getActivity(), IndoorNavigationActivity.class);
                    startActivity(intent);
                }
            }
        });

        TextView tvBuildingHint = mFragmentView.findViewById(R.id.tv_indoor_navigation_popup_building);

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
        if (MyApplication.getInstance().currentLocation != null) {
            Location currentLocation = MyApplication.getInstance().currentLocation;
            ArrayList<JmapTriggerableLocation> jmapTriggerableLocations = MyApplication.getInstance().
                    getJmapTriggerableLocations();
            LocationPoint locationPoint = new LocationPoint();
            if (MyApplication.getInstance().currentJmapTriggerableLocation != null && currentLocation.hasAccuracy()
                    && currentLocation.getAccuracy() <= 50) {
                JmapTriggerableLocation currentJmapTriggerableLocation = MyApplication.getInstance().currentJmapTriggerableLocation;

                if (currentJmapTriggerableLocation.getType() != null && currentJmapTriggerableLocation.getType().equalsIgnoreCase("polygon") &&
                        currentJmapTriggerableLocation.getPolygon() != null) {
                    if (!Utilities.containsLocation(currentLocation.getLatitude(), currentLocation.getLongitude(),
                            currentJmapTriggerableLocation.getPolygon(), false)) {
                        MyApplication.getInstance().currentJmapTriggerableLocation = null;
                        ivIndoorNavigation.setVisibility(View.GONE);
                    }
                } else {
                    locationPoint.setLat(currentJmapTriggerableLocation.getLatitude());
                    locationPoint.setLon(currentJmapTriggerableLocation.getLongitude());
                    double distance = Utilities.getDistance(currentLocation.getLatitude(), currentLocation.getLongitude(),
                            locationPoint);
                    if (distance > currentJmapTriggerableLocation.getRadiusInMeters()) {
                        MyApplication.getInstance().currentJmapTriggerableLocation = null;
                        ivIndoorNavigation.setVisibility(View.GONE);
                    }
                }
            }

            if (MyApplication.getInstance().currentJmapTriggerableLocation == null) {
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
    }

    private void setCurrentJmapTriggerableLocation(JmapTriggerableLocation jmapTriggerableLocation) {
        if (!isIndoorNavigationPopupShowed) {
            hideKeyboard();
            isIndoorNavigationPopupShowed = true;
            rlIndoorNavigationPopup.setVisibility(View.VISIBLE);
        }

        MyApplication.getInstance().currentJmapTriggerableLocation = jmapTriggerableLocation;
        tvIndoorNavigationPopupAddress.setText(jmapTriggerableLocation.getAddress());
        resetIndoorNavigationPopupContentHeight();
        ivIndoorNavigation.setVisibility(View.VISIBLE);
    }

    public void startLocationListener() {
        if (ActivityCompat.checkSelfPermission(systemContext, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(systemContext, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            LocationManager locationManager = (LocationManager) systemContext.getSystemService(LOCATION_SERVICE);

            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                MyApplication.getInstance().currentLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            } else if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                MyApplication.getInstance().currentLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            } else {
                MyApplication.getInstance().currentLocation = locationManager.getLastKnownLocation(LocationManager.FUSED_PROVIDER);
            }
            detectJmapLocation();
            locationListener = new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    MyApplication.getInstance().currentLocation = location;

                    setOriginDefaultToCurrentLocation();
                    //Log.i("location", "Current location:" + currentLocation.toString());
                    detectJmapLocation();
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {

                }

                @Override
                public void onProviderEnabled(String provider) {

                }

                @Override
                public void onProviderDisabled(String provider) {

                }
            };

            if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 500L, 0, locationListener);
            }

            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 500L, 0, locationListener);
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    locationManager.requestFlush(LocationManager.GPS_PROVIDER, locationListener, 9006);
                } else if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                    locationManager.requestFlush(LocationManager.NETWORK_PROVIDER, locationListener, 9006);
                } else if (locationManager.isProviderEnabled(LocationManager.FUSED_PROVIDER)) {
                    locationManager.requestFlush(LocationManager.FUSED_PROVIDER, locationListener, 9006);
                }
            } else {
                if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, locationListener, null);
                } else if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                    locationManager.requestSingleUpdate(LocationManager.NETWORK_PROVIDER, locationListener, null);
                }
            }

            enableLocationComponentOnMap();
        }
    }

    private void enableLocationComponentOnMap() {
        if (gMapboxMap != null && gMapStyle != null) {
            if (ActivityCompat.checkSelfPermission(systemContext, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(systemContext, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                // Get an instance of the component
                LocationComponent locationComponent = gMapboxMap.getLocationComponent();

// Activate with options
                locationComponent.activateLocationComponent(
                        LocationComponentActivationOptions.builder(systemContext, gMapStyle).build());

// Enable to make component visible
                locationComponent.setLocationComponentEnabled(true);

// Set the component's camera mode
                locationComponent.setCameraMode(CameraMode.NONE);

// Set the component's render mode
                locationComponent.setRenderMode(RenderMode.GPS);

                try {
                    if (locationComponent.getLastKnownLocation() != null) {
                        MyApplication.getInstance().currentLocation = locationComponent.getLastKnownLocation();
                        detectJmapLocation();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void refreshAnnotationsIfNeeded() {
        //Add back route polyline if needed
        if (sourceIDsForRoutePolyline.size() > 0 && layerIDsForRoutePolyline.size() > 0) {
            Itinerary itinerary = null;
            if (currentMode == Constants.GROUP_ITINERARY_TYPE_WALK_TRANSIT &&
                    currentSelectedItineraryIndex < itineraryArrayListWalkTransit.size()) {
                itinerary = itineraryArrayListWalkTransit.get(currentSelectedItineraryIndex);
            } else if (currentMode == Constants.GROUP_ITINERARY_TYPE_BIKE_TRANSIT &&
                    currentSelectedItineraryIndex < itineraryArrayListBikeTransit.size()) {
                itinerary = itineraryArrayListBikeTransit.get(currentSelectedItineraryIndex);
            } else if (currentMode == Constants.GROUP_ITINERARY_TYPE_BIKESHARE_TRANSIT &&
                    currentSelectedItineraryIndex < itineraryArrayListBikeShareTransit.size()) {
                itinerary = itineraryArrayListBikeShareTransit.get(currentSelectedItineraryIndex);
            } else if (currentMode == Constants.GROUP_ITINERARY_TYPE_SCOOTER_TRANSIT &&
                    currentSelectedItineraryIndex < itineraryArrayListScooterTransit.size()) {
                itinerary = itineraryArrayListScooterTransit.get(currentSelectedItineraryIndex);
            } else if (currentMode == Constants.GROUP_ITINERARY_TYPE_DRIVE_TRANSIT &&
                    currentSelectedItineraryIndex < itineraryArrayListDriveTransit.size()) {
                itinerary = itineraryArrayListDriveTransit.get(currentSelectedItineraryIndex);
            } else if (currentSelectedItineraryIndex < itineraryArrayListOther.size()) {
                itinerary = itineraryArrayListOther.get(currentSelectedItineraryIndex);
            }

            if (itinerary != null) {
                drawRoutePolylineOnMap(itinerary);
            }
        }

        //Add back Park&Ride markers if needed
        if (parkRideSource != null && unclusteredParkRide != null) {
            loadParkRideLocationsOnMap();
        }

        //Add back transit stops if needed
        if (transitStopSource != null && unclusteredTransitStop != null) {
            loadTransitStopsOnMap();
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

                    enableLocationComponentOnMap();

                    refreshAnnotationsIfNeeded();
                }
            });
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


        llSatelliteAccessibility = mFragmentView.findViewById(R.id.ll_satellite_accessibility);
        llParkRideLocationsAccessibility = mFragmentView.findViewById(R.id.ll_park_and_ride_locations_accessibility);
        llTranitStopsAccessibility = mFragmentView.findViewById(R.id.ll_transit_stops_accessibility);
        tvSatelliteHighlightAccessibility = mFragmentView.findViewById(R.id.tv_satellite_highlight_accessibility);

        llRentalVehicles = mFragmentView.findViewById(R.id.ll_rental_vehicles);
        llRentalVehiclesAccessibility = mFragmentView.findViewById(R.id.ll_rental_vehicles_accessibility);
        ImageView ivLayerClose = mFragmentView.findViewById(R.id.iv_layer_close);
        ivLayerClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideLayerPopup();
            }
        });

        if (Utilities.checkIfToDisplayAccessibilityUI(requireActivity())) {
            llParkRideLocationsAccessibility.setVisibility(View.VISIBLE);
            llSatelliteAccessibility.setVisibility(View.VISIBLE);
            llTranitStopsAccessibility.setVisibility(View.VISIBLE);

            llParkRideLocations.setVisibility(View.GONE);
            llSatellite.setVisibility(View.GONE);
            llTransitStops.setVisibility(View.GONE);

            llRentalVehicles.setVisibility(View.GONE);
            llRentalVehiclesAccessibility.setVisibility(View.VISIBLE);
        } else {

            llParkRideLocationsAccessibility.setVisibility(View.GONE);
            llSatelliteAccessibility.setVisibility(View.GONE);
            llTranitStopsAccessibility.setVisibility(View.GONE);

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
                    llItineraryList.setAlpha(0.0f);
                    llItineraryDetail.setAlpha(0.0f);
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

    private void setAddressToAutoCompleteTextView(String address, boolean isOrigin) {
        if (address != null) {
            TextView addressTextView;
            if (isOrigin) {
                addressTextView = atSearchOrigin;
                tv_itinerary_origin.setText(address);
            } else {
                addressTextView = atSearchDestination;
                tv_itinerary_Destination.setText(address);
            }
            addressTextView.setText(address);
            if (address.isEmpty()) {
                if (isOrigin) {
                    addressTextView.setHint(systemContext.getString(R.string.from_hint));
                } else {
                    addressTextView.setHint(systemContext.getString(R.string.destination_hint));
                }
            }
        }
    }

    private void gotoAddressSearchPage(boolean mIsOrigin) {
        isOrigin = mIsOrigin;
        currentLocationAvailable = MyApplication.getInstance().currentLocation != null;

        if (isOrigin) {
            address = atSearchOrigin.getText().toString();
        } else {
            address = atSearchDestination.getText().toString();
        }

        isAddingLocation = false;

        setupAddressSearchWidgets();
        setupCurrentLocationWidgets();
        setupRecentlySearched();
        setupFavoritePlacesLayout();

        atSearch.setText(address);
        if (address != null && !address.isEmpty() && address.equalsIgnoreCase("(Current Location)")) {
            atSearch.setText("");
        }
        if (isOrigin) {
            atSearch.setHint(getString(R.string.enter_start_location));
        } else {
            atSearch.setHint(getString(R.string.enter_destination));
        }
        if (address.isEmpty()) {
            ivClear.setVisibility(View.GONE);
        } else {
            ivClear.setVisibility(View.VISIBLE);
        }
        svAddressSearch.setVisibility(View.VISIBLE);
        showKeyboard();
        assignAccessibilitytextToSearchbar(false);
    }

    private void initSearchBar() {
        refreshSearchModeDrive();
        refreshSearchModeBike();
        refreshSearchModeTransit();
        refreshSearchModeWalk();
        refreshSearchModeRental();
    }

    private void refreshSearchModeDrive() {
        boolean enabledSearchModeDrive = Configurations.getBoolean("enabled_search_mode_drive", true);
        if (Configurations.getBoolean("new_search_bar_layout_style", false)) {
            rlSearchModeDrive.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorModesBackground)));
            rlSearchModeDriveAccessibility.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorModesBackground)));
            if (enabledSearchModeDrive) {
                ivSearchModeDrive.setColorFilter(getResources().getColor(android.R.color.black));
                tvSearchModeDrive.setTextColor(getResources().getColor(android.R.color.black));
                rlSearchModeDrive.setBackgroundResource(R.drawable.rounded_primary_bg);
                ivSearchModeDriveAccessibility.setImageResource(R.drawable.ic_circle_active);

                ivSearchModeDriveAccessibilityNew.setColorFilter(getResources().getColor(android.R.color.black));
                rlSearchModeDriveAccessibility.setBackgroundResource(R.drawable.rounded_primary_bg);

            } else {
                ivSearchModeDrive.setColorFilter(getResources().getColor(android.R.color.black));
                tvSearchModeDrive.setTextColor(getResources().getColor(android.R.color.black));
                rlSearchModeDrive.setBackgroundResource(R.drawable.rounded_primary_frame_bg);
                ivSearchModeDriveAccessibility.setImageResource(R.drawable.ic_circle_inactive);

                ivSearchModeDriveAccessibilityNew.setColorFilter(getResources().getColor(android.R.color.black));
                rlSearchModeDriveAccessibility.setBackgroundResource(R.drawable.rounded_primary_frame_bg);
            }
        } else {
            if (enabledSearchModeDrive) {
                ivSearchModeDrive.setColorFilter(getResources().getColor(android.R.color.black));
                ivSearchModeDrive.setBackgroundResource(R.drawable.rounded_primary_bg);
                ivSearchModeDriveAccessibility.setImageResource(R.drawable.ic_circle_active);

                ivSearchModeDriveAccessibilityNew.setColorFilter(getResources().getColor(android.R.color.black));
                rlSearchModeDriveAccessibility.setBackgroundResource(R.drawable.rounded_primary_bg);

            } else {
                ivSearchModeDrive.setColorFilter(getResources().getColor(android.R.color.black));
                ivSearchModeDrive.setBackground(null);
                ivSearchModeDriveAccessibility.setImageResource(R.drawable.ic_circle_inactive);
                ivSearchModeDriveAccessibilityNew.setColorFilter(getResources().getColor(android.R.color.black));
                rlSearchModeDriveAccessibility.setBackgroundResource(R.drawable.rounded_primary_frame_bg);
            }
        }
    }

    private void refreshSearchModeBike() {
        boolean enabledSearchModeBike = Configurations.getBoolean("enabled_search_mode_bike", true);

        if (Configurations.getBoolean("new_search_bar_layout_style", false)) {
            rlSearchModeBike.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorModesBackground)));
            rlSearchModeBikeAccessibility.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorModesBackground)));
            if (enabledSearchModeBike) {
                ivSearchModeBike.setColorFilter(getResources().getColor(android.R.color.black));
                tvSearchModeBike.setTextColor(getResources().getColor(android.R.color.black));
                rlSearchModeBike.setBackgroundResource(R.drawable.rounded_primary_bg);
                ivSearchModeBikeAccessibility.setImageResource(R.drawable.ic_circle_active);

                ivSearchModeBikeAccessibilityNew.setColorFilter(getResources().getColor(android.R.color.black));
                rlSearchModeBikeAccessibility.setBackgroundResource(R.drawable.rounded_primary_bg);

            } else {
                ivSearchModeBike.setColorFilter(getResources().getColor(android.R.color.black));
                tvSearchModeBike.setTextColor(getResources().getColor(android.R.color.black));
                rlSearchModeBike.setBackgroundResource(R.drawable.rounded_primary_frame_bg);
                ivSearchModeBikeAccessibility.setImageResource(R.drawable.ic_circle_inactive);

                ivSearchModeBikeAccessibilityNew.setColorFilter(getResources().getColor(android.R.color.black));
                rlSearchModeBikeAccessibility.setBackgroundResource(R.drawable.rounded_primary_frame_bg);

            }
        } else {
            if (enabledSearchModeBike) {
                ivSearchModeBike.setColorFilter(getResources().getColor(android.R.color.black));
                ivSearchModeBike.setBackgroundResource(R.drawable.rounded_primary_bg);
                ivSearchModeBikeAccessibility.setImageResource(R.drawable.ic_circle_active);

                ivSearchModeBikeAccessibilityNew.setColorFilter(getResources().getColor(android.R.color.black));
                rlSearchModeBikeAccessibility.setBackgroundResource(R.drawable.rounded_primary_bg);

            } else {
                ivSearchModeBike.setColorFilter(getResources().getColor(android.R.color.black));
                ivSearchModeBike.setBackground(null);
                ivSearchModeBikeAccessibility.setImageResource(R.drawable.ic_circle_inactive);

                ivSearchModeBikeAccessibilityNew.setColorFilter(getResources().getColor(android.R.color.black));
                rlSearchModeBikeAccessibility.setBackgroundResource(R.drawable.rounded_primary_frame_bg);
            }
        }
    }

    public void resetSearchModeSettings() {
        Configurations.setBoolean("enabled_search_mode_drive", false);
        Configurations.setBoolean("enabled_search_mode_bike", false);
        Configurations.setBoolean("enabled_search_mode_transit", true);
        Configurations.setBoolean("enabled_search_mode_walk", false);
        Configurations.setBoolean("enabled_search_mode_rental", false);

        if (getContext() != null) {
            refreshSearchModeTransit();
            refreshSearchModeBike();
            refreshSearchModeDrive();
            refreshSearchModeWalk();
            refreshSearchModeRental();
        }
    }

    private void refreshSearchModeTransit() {
        boolean enabledSearchModeTransit = Configurations.getBoolean("enabled_search_mode_transit", true);
        if (Configurations.getBoolean("new_search_bar_layout_style", false)) {
            rlSearchModeTransit.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorModesBackground)));
            rlSearchModeTransitAccessibility.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorModesBackground)));
            if (enabledSearchModeTransit) {
                ivSearchModeTransit.setColorFilter(getResources().getColor(android.R.color.black));
                tvSearchModeTransit.setTextColor(getResources().getColor(android.R.color.black));
                rlSearchModeTransit.setBackgroundResource(R.drawable.rounded_primary_bg);
                ivSearchModeTransitAccessibility.setImageResource(R.drawable.ic_circle_active);

                ivSearchModeTransitAccessibilityNew.setColorFilter(getResources().getColor(android.R.color.black));
                rlSearchModeTransitAccessibility.setBackgroundResource(R.drawable.rounded_primary_bg);

            } else {
                ivSearchModeTransit.setColorFilter(getResources().getColor(android.R.color.black));
                tvSearchModeTransit.setTextColor(getResources().getColor(android.R.color.black));
                rlSearchModeTransit.setBackgroundResource(R.drawable.rounded_primary_frame_bg);
                ivSearchModeTransitAccessibility.setImageResource(R.drawable.ic_circle_inactive);

                ivSearchModeTransitAccessibilityNew.setColorFilter(getResources().getColor(android.R.color.black));
                rlSearchModeTransitAccessibility.setBackgroundResource(R.drawable.rounded_primary_frame_bg);
            }
        } else {
            if (enabledSearchModeTransit) {
                ivSearchModeTransit.setColorFilter(getResources().getColor(android.R.color.black));
                ivSearchModeTransit.setBackgroundResource(R.drawable.rounded_primary_bg);
                ivSearchModeTransitAccessibility.setImageResource(R.drawable.ic_circle_active);

                ivSearchModeTransitAccessibilityNew.setColorFilter(getResources().getColor(android.R.color.black));
                rlSearchModeTransitAccessibility.setBackgroundResource(R.drawable.rounded_primary_bg);

            } else {
                ivSearchModeTransit.setColorFilter(getResources().getColor(android.R.color.black));
                ivSearchModeTransit.setBackground(null);
                ivSearchModeTransitAccessibility.setImageResource(R.drawable.ic_circle_inactive);

                ivSearchModeTransitAccessibilityNew.setColorFilter(getResources().getColor(android.R.color.black));
                rlSearchModeTransitAccessibility.setBackgroundResource(R.drawable.rounded_primary_frame_bg);
            }
        }
    }

    private void refreshSearchModeWalk() {
        boolean enabledSearchModeWalk = Configurations.getBoolean("enabled_search_mode_walk", true);
        if (Configurations.getBoolean("new_search_bar_layout_style", false)) {
            rlSearchModeWalk.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorModesBackground)));
            rlSearchModeWalkAccessibility.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorModesBackground)));

            if (enabledSearchModeWalk) {
                ivSearchModeWalk.setColorFilter(getResources().getColor(android.R.color.black));
                tvSearchModeWalk.setTextColor(getResources().getColor(android.R.color.black));
                rlSearchModeWalk.setBackgroundResource(R.drawable.rounded_primary_bg);

                ivSearchModeWalkAccessibilityNew.setColorFilter(getResources().getColor(android.R.color.black));
                rlSearchModeWalkAccessibility.setBackgroundResource(R.drawable.rounded_primary_bg);

            } else {
                ivSearchModeWalk.setColorFilter(getResources().getColor(android.R.color.black));
                tvSearchModeWalk.setTextColor(getResources().getColor(android.R.color.black));
                rlSearchModeWalk.setBackgroundResource(R.drawable.rounded_primary_frame_bg);

                ivSearchModeWalkAccessibilityNew.setColorFilter(getResources().getColor(android.R.color.black));
                rlSearchModeWalkAccessibility.setBackgroundResource(R.drawable.rounded_primary_frame_bg);
            }
        } else {
            if (enabledSearchModeWalk) {
                ivSearchModeWalk.setColorFilter(getResources().getColor(android.R.color.black));
                ivSearchModeWalk.setBackgroundResource(R.drawable.rounded_primary_bg);

                ivSearchModeWalkAccessibilityNew.setColorFilter(getResources().getColor(android.R.color.black));
                rlSearchModeWalkAccessibility.setBackgroundResource(R.drawable.rounded_primary_bg);

            } else {
                ivSearchModeWalk.setColorFilter(getResources().getColor(android.R.color.black));
                ivSearchModeWalk.setBackground(null);

                ivSearchModeWalkAccessibilityNew.setColorFilter(getResources().getColor(android.R.color.black));
                rlSearchModeWalkAccessibility.setBackgroundResource(R.drawable.rounded_primary_frame_bg);
            }
        }
    }

    private void refreshSearchModeRental() {
        boolean enabledSearchModeRental = Configurations.getBoolean("enabled_search_mode_rental", true);
        if (Configurations.getBoolean("new_search_bar_layout_style", false)) {
            rlSearchModeRental.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorModesBackground)));
            rlSearchModeRentalAccessibility.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorModesBackground)));
            if (enabledSearchModeRental) {
                ivSearchModeRental.setColorFilter(getResources().getColor(android.R.color.black));
                tvSearchModeRental.setTextColor(getResources().getColor(android.R.color.black));
                rlSearchModeRental.setBackgroundResource(R.drawable.rounded_primary_bg);
                ivSearchModeRentalAccessibility.setImageResource(R.drawable.ic_circle_active);
                if (llHomeSettingAllowBikeRental != null) {
                    llHomeSettingAllowBikeRental.setVisibility(View.VISIBLE);
                }

                if (llHomeSettingAllowBikeRentalAccessibility != null) {
                    llHomeSettingAllowBikeRentalAccessibility.setVisibility(View.VISIBLE);
                }

                if (llHomeSettingAllowScooterRental != null) {
                    llHomeSettingAllowScooterRental.setVisibility(View.VISIBLE);
                }

                if (llHomeSettingAllowScooterRentalAccessibility != null) {
                    llHomeSettingAllowScooterRentalAccessibility.setVisibility(View.VISIBLE);
                }

                ivSearchModeRentalAccessibilityNew.setColorFilter(getResources().getColor(android.R.color.black));
                rlSearchModeRentalAccessibility.setBackgroundResource(R.drawable.rounded_primary_bg);

            } else {
                ivSearchModeRental.setColorFilter(getResources().getColor(android.R.color.black));
                tvSearchModeRental.setTextColor(getResources().getColor(android.R.color.black));
                rlSearchModeRental.setBackgroundResource(R.drawable.rounded_primary_frame_bg);
                ivSearchModeRentalAccessibility.setImageResource(R.drawable.ic_circle_inactive);
                if (llHomeSettingAllowBikeRental != null) {
                    llHomeSettingAllowBikeRental.setVisibility(View.GONE);
                }

                if (llHomeSettingAllowBikeRentalAccessibility != null) {
                    llHomeSettingAllowBikeRentalAccessibility.setVisibility(View.GONE);
                }

                if (llHomeSettingAllowScooterRental != null) {
                    llHomeSettingAllowScooterRental.setVisibility(View.GONE);
                }

                if (llHomeSettingAllowScooterRentalAccessibility != null) {
                    llHomeSettingAllowScooterRentalAccessibility.setVisibility(View.GONE);
                }

                ivSearchModeRentalAccessibilityNew.setColorFilter(getResources().getColor(android.R.color.black));
                rlSearchModeRentalAccessibility.setBackgroundResource(R.drawable.rounded_primary_frame_bg);
            }
        } else {
            if (enabledSearchModeRental) {
                ivSearchModeRental.setColorFilter(getResources().getColor(android.R.color.black));
                ivSearchModeRental.setBackgroundResource(R.drawable.rounded_primary_bg);
                ivSearchModeRentalAccessibility.setImageResource(R.drawable.ic_circle_active);
                if (llHomeSettingAllowBikeRental != null) {
                    llHomeSettingAllowBikeRental.setVisibility(View.VISIBLE);
                }

                if (llHomeSettingAllowBikeRentalAccessibility != null) {
                    llHomeSettingAllowBikeRentalAccessibility.setVisibility(View.VISIBLE);
                }

                if (llHomeSettingAllowScooterRental != null) {
                    llHomeSettingAllowScooterRental.setVisibility(View.VISIBLE);
                }

                if (llHomeSettingAllowScooterRentalAccessibility != null) {
                    llHomeSettingAllowScooterRentalAccessibility.setVisibility(View.VISIBLE);
                }

                ivSearchModeRentalAccessibilityNew.setColorFilter(getResources().getColor(android.R.color.black));
                rlSearchModeRentalAccessibility.setBackgroundResource(R.drawable.rounded_primary_bg);

            } else {
                ivSearchModeRental.setColorFilter(getResources().getColor(android.R.color.black));
                ivSearchModeRental.setBackground(null);
                ivSearchModeRentalAccessibility.setImageResource(R.drawable.ic_circle_inactive);
                if (llHomeSettingAllowBikeRental != null) {
                    llHomeSettingAllowBikeRental.setVisibility(View.GONE);
                }

                if (llHomeSettingAllowBikeRentalAccessibility != null) {
                    llHomeSettingAllowBikeRentalAccessibility.setVisibility(View.GONE);
                }

                if (llHomeSettingAllowScooterRental != null) {
                    llHomeSettingAllowScooterRental.setVisibility(View.GONE);
                }

                if (llHomeSettingAllowScooterRentalAccessibility != null) {
                    llHomeSettingAllowScooterRentalAccessibility.setVisibility(View.GONE);
                }

                ivSearchModeRentalAccessibilityNew.setColorFilter(getResources().getColor(android.R.color.black));
                rlSearchModeRentalAccessibility.setBackgroundResource(R.drawable.rounded_primary_frame_bg);
            }
        }

    }

    public void refreshSearchModesControlsChangedIcon() {
        if (ivSearchModesControlsChanged != null) {
            boolean isChanged = false;

            if (rlSearchModeRental.getVisibility() == View.VISIBLE &&
                    Configurations.getBoolean("enabled_search_mode_rental", true)) {
                isChanged = true;
            } else if (rlSearchModeDrive.getVisibility() == View.VISIBLE &&
                    Configurations.getBoolean("enabled_search_mode_drive", true)) {
                isChanged = true;
            } else if (rlSearchModeBike.getVisibility() == View.VISIBLE &&
                    Configurations.getBoolean("enabled_search_mode_bike", true)) {
                isChanged = true;
            } else if (rlSearchModeTransit.getVisibility() == View.VISIBLE &&
                    !Configurations.getBoolean("enabled_search_mode_transit", true)) {
                isChanged = true;
            } else if (rlSearchModeWalk.getVisibility() == View.VISIBLE &&
                    Configurations.getBoolean("enabled_search_mode_walk", true)) {
                isChanged = true;
            }
            if (!isChanged){
                ivSearchModesControlsChanged.setVisibility(View.GONE);
            }
        }

    }

    private void refreshSettingsChangedIcon() {
        if (ivSettingsChanged != null) {
            boolean isChanged = false;

            if (!Configurations.getBoolean("home_setting_bus_enabled", true)) {
                isChanged = true;
            } else if (!Configurations.getBoolean("home_setting_rail_enabled", true)) {
                isChanged = true;
            } else if (!Configurations.getBoolean("home_setting_street_car_enabled", true)) {
                isChanged = true;
            } else if (!Configurations.getBoolean("home_setting_ferry_enabled", true)) {
                isChanged = true;
            } else if (!Configurations.getBoolean("home_setting_gondola_enabled", true)) {
                isChanged = true;
            } else if (!Configurations.getBoolean("home_setting_link_enabled", true)) {
                isChanged = true;
            } else if (!Configurations.getBoolean("home_setting_water_taxi_enabled", true)) {
                isChanged = true;
            } else if (!Configurations.getBoolean("home_setting_monorail_enabled", true)) {
                isChanged = true;
            } else if (Configurations.getBoolean("enabled_search_mode_avoid_walking", false)) {
                isChanged = true;
            } else if (Configurations.getBoolean("enabled_search_mode_accessible_routing", false)) {
                isChanged = true;
            } else if (!Configurations.getBoolean("enabled_search_mode_bike_rental", true)) {
                isChanged = true;
            } else if (!Configurations.getBoolean("enabled_search_mode_scooter_rental", true)) {
                isChanged = true;
            } else if (Configurations.getInt("maximum_walk_index",
                    Constants.DEFAULT_MAXIMUM_WALK_INDEX) != Constants.DEFAULT_MAXIMUM_WALK_INDEX) {
                isChanged = true;
            } else if (Configurations.getInt("walk_speed_index",
                    Constants.DEFAULT_WALK_SPEED_INDEX) != Constants.DEFAULT_WALK_SPEED_INDEX) {
                isChanged = true;
            } else if (Configurations.getInt("optimize_option_index",
                    Constants.DEFAULT_OPTIMIZE_OPTION_INDEX) != Constants.DEFAULT_OPTIMIZE_OPTION_INDEX) {
                isChanged = true;
            }

            if (!isChanged) {
                ivSettingsChanged.setVisibility(View.GONE);
            }
        }


    }

    private void initSearchModesLayout() {
        String selectableModes = Configurations.getString("selectable_modes",
                "TRANSIT:BUS,TRAM,FERRY,RAIL,STREETCAR,WATER_TAXI;BIKE;WALK;CAR;ST-RENT").toLowerCase(Locale.ROOT);

        if (selectableModes.contains("car") || selectableModes.contains("drive")) {
            rlSearchModeDrive.setVisibility(View.VISIBLE);
            rlSearchModeDriveAccessibility.setVisibility(View.VISIBLE);
        } else {
            rlSearchModeDrive.setVisibility(View.GONE);
            rlSearchModeDriveAccessibility.setVisibility(View.GONE);
        }

        if (selectableModes.contains("bike") || selectableModes.contains("bicycle")) {
            rlSearchModeBike.setVisibility(View.VISIBLE);
            rlSearchModeBikeAccessibility.setVisibility(View.VISIBLE);
        } else {
            rlSearchModeBike.setVisibility(View.GONE);
            rlSearchModeBikeAccessibility.setVisibility(View.GONE);
        }

        if (selectableModes.contains("walk")) {
            rlSearchModeWalk.setVisibility(View.VISIBLE);
            rlSearchModeWalkAccessibility.setVisibility(View.VISIBLE);
        } else {
            rlSearchModeWalk.setVisibility(View.GONE);
            rlSearchModeWalkAccessibility.setVisibility(View.GONE);
        }

        if (selectableModes.contains("transit")) {
            rlSearchModeTransitAccessibility.setVisibility(View.VISIBLE);
            rlSearchModeTransit.setVisibility(View.VISIBLE);
        } else {
            rlSearchModeTransitAccessibility.setVisibility(View.GONE);
            rlSearchModeTransit.setVisibility(View.GONE);
        }

        if (selectableModes.contains("rent")) {
            rlSearchModeRental.setVisibility(View.VISIBLE);
            rlSearchModeRentalAccessibility.setVisibility(View.VISIBLE);
        } else {
            rlSearchModeRental.setVisibility(View.GONE);
            rlSearchModeRentalAccessibility.setVisibility(View.GONE);
        }
    }

    private void setupSearchBar() {
        llSearchBar = mFragmentView.findViewById(R.id.ll_search_bar);
        atSearchOrigin = mFragmentView.findViewById(R.id.at_search_origin);
        atSearchDestination = mFragmentView.findViewById(R.id.at_search_destination);
        llSearchDestination = mFragmentView.findViewById(R.id.ll_search_destination);
        iv_mapview_destination_icon = mFragmentView.findViewById(R.id.iv_mapview_destination_icon);
        rlSearchBar = mFragmentView.findViewById(R.id.rl_search_bar);
        rlLayer = mFragmentView.findViewById(R.id.rl_layer);
        rlLocateMe = mFragmentView.findViewById(R.id.rl_locate_me);

        ivAddressSwitch = mFragmentView.findViewById(R.id.iv_address_switch);
        llSearchMode = mFragmentView.findViewById(R.id.ll_search_mode);
        ivClock = mFragmentView.findViewById(R.id.iv_clock);
        rlSearchModeDrive = mFragmentView.findViewById(R.id.rl_search_mode_drive);
        rlSearchModeBike = mFragmentView.findViewById(R.id.rl_search_mode_bike);
        rlSearchModeWalk = mFragmentView.findViewById(R.id.rl_search_mode_walk);
        rlSearchModeTransit = mFragmentView.findViewById(R.id.rl_search_mode_transit);
        rlSearchModeRental = mFragmentView.findViewById(R.id.rl_search_mode_rental);

        ivSearchModeTransitAccessibility = mFragmentView.findViewById(R.id.iv_transit_selected);
        rlSearchModeTransitAccessibility = mFragmentView.findViewById(R.id.rl_search_mode_transit_accessibility);

        ivSearchModeDriveAccessibility = mFragmentView.findViewById(R.id.iv_car_selected);
        rlSearchModeDriveAccessibility = mFragmentView.findViewById(R.id.rl_search_mode_drive_accessibility);

        ivSearchModeBikeAccessibility = mFragmentView.findViewById(R.id.iv_bike_selected);
        rlSearchModeBikeAccessibility = mFragmentView.findViewById(R.id.rl_search_mode_bike_accessibility);

        ivSearchModeRentalAccessibility = mFragmentView.findViewById(R.id.iv_rental_selected);
        rlSearchModeRentalAccessibility = mFragmentView.findViewById(R.id.rl_search_mode_rental_accessibility);

        rlSearchModeWalkAccessibility = mFragmentView.findViewById(R.id.rl_search_mode_walk_accessibility);

        ivSearchModeTransitAccessibilityNew = mFragmentView.findViewById(R.id.iv_search_mode_transit_accessibility);
        rlSearchModeTransitAccessibilityNew = mFragmentView.findViewById(R.id.rl_search_mode_transit_accessibility_new);

        ivSearchModeDriveAccessibilityNew = mFragmentView.findViewById(R.id.iv_search_mode_drive_accessibility);
        rlSearchModeDriveAccessibilityNew = mFragmentView.findViewById(R.id.rl_search_mode_drive_accessibility_new);

        ivSearchModeBikeAccessibilityNew = mFragmentView.findViewById(R.id.iv_search_mode_bike_accessibility);
        rlSearchModeBikeAccessibilityNew = mFragmentView.findViewById(R.id.rl_home_search_mode_bike_accessibility_new);

        ivSearchModeRentalAccessibilityNew = mFragmentView.findViewById(R.id.iv_search_mode_rental_accessibility);
        rlSearchModeRentalAccessibilityNew = mFragmentView.findViewById(R.id.rl_search_mode_rental_accessibility_new);

        ivSearchModeWalkAccessibilityNew = mFragmentView.findViewById(R.id.iv_search_mode_walk_accessibility);
        rlSearchModeWalkAccessibilityNew = mFragmentView.findViewById(R.id.rl_search_mode_walk_accessibility_new);

        initSearchModesLayout();


        llSwitchLocationAccessibility = mFragmentView.findViewById(R.id.ll_switch_locations_accessibility);
        llPlanTripAccessbility = mFragmentView.findViewById(R.id.ll_plan_trip_accessibiliy);

        ivSearchModeDrive = mFragmentView.findViewById(R.id.iv_search_mode_drive);
        ivSearchModeBike = mFragmentView.findViewById(R.id.iv_search_mode_bike);
        ivSearchModeWalk = mFragmentView.findViewById(R.id.iv_search_mode_walk);
        ivSearchModeTransit = mFragmentView.findViewById(R.id.iv_search_mode_transit);
        ivSearchModeRental = mFragmentView.findViewById(R.id.iv_search_mode_rental);

        tvSearchModeDrive = mFragmentView.findViewById(R.id.tv_search_mode_drive);
        tvSearchModeBike = mFragmentView.findViewById(R.id.tv_search_mode_bike);
        tvSearchModeWalk = mFragmentView.findViewById(R.id.tv_search_mode_walk);
        tvSearchModeTransit = mFragmentView.findViewById(R.id.tv_search_mode_transit);
        tvSearchModeRental = mFragmentView.findViewById(R.id.tv_search_mode_rental);
        ivSearchModesControlsChanged = mFragmentView.findViewById(R.id.iv_search_modes_controls_changed);


        ivSettingsChanged = mFragmentView.findViewById(R.id.iv_settings_changed);
        ivSearchSetting = mFragmentView.findViewById(R.id.iv_search_setting);
        llSearchDateTime = mFragmentView.findViewById(R.id.ll_search_date_time);
        tvSearchTime = mFragmentView.findViewById(R.id.tv_search_time);
        tvSearchDate = mFragmentView.findViewById(R.id.tv_search_date);
        tvSearchDateTimeMode = mFragmentView.findViewById(R.id.tv_search_date_time_mode);
        rlSearchDateTime = mFragmentView.findViewById(R.id.rl_search_date_time);
        llPlanTripSearch = mFragmentView.findViewById(R.id.ll_plan_trip_search);
        llPlanTripSearchPref = mFragmentView.findViewById(R.id.ll_plan_trip_search_pref);

        tvSearchTimeAccessibility = mFragmentView.findViewById(R.id.tv_search_time_accessibility);
        tvSearchDateAccessibility = mFragmentView.findViewById(R.id.tv_search_date_accessibility);

        rlSettingSearch = mFragmentView.findViewById(R.id.rl_setting_search);
        tvOriginUnderline = mFragmentView.findViewById(R.id.tv_origin_underline);
        llOrigin = mFragmentView.findViewById(R.id.ll_origin);
        ivSearchBarReset = mFragmentView.findViewById(R.id.iv_search_bar_reset);

        rlHomeSearchModes = mFragmentView.findViewById(R.id.rl_home_search_modes);
        ivSearchModesControls = mFragmentView.findViewById(R.id.iv_search_modes_controls);
        ivCloseSearchMode = mFragmentView.findViewById(R.id.iv_close_search_mode);

        ivCloseSearchModeAccessibility = mFragmentView.findViewById(R.id.iv_close_search_mode_accessibility);

        rlHomeSearchModesAccessibilty = mFragmentView.findViewById(R.id.rl_home_search_modes_accessibility);
        ivHomeSearchModeTransitExpand = mFragmentView.findViewById(R.id.iv_home_setting_transit_expand);

        initSearchBar();

        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) ivSearchBarReset.getLayoutParams();

        if (Utilities.checkIfToDisplayAccessibilityUI(requireActivity())) {
            params.addRule(RelativeLayout.ALIGN_PARENT_END);
            ivSearchBarReset.setLayoutParams(params);
        } else {
            params.addRule(RelativeLayout.START_OF, R.id.ll_plan_trip_search);
            ivSearchBarReset.setLayoutParams(params);
        }

        ivSearchModesControls.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Utilities.checkIfToDisplayAccessibilityUI(requireActivity())) {
                    rlHomeSearchModesAccessibilty.setVisibility(View.VISIBLE);

                } else {
                    refreshSearchModeTransit();
                    refreshSearchModeBike();
                    refreshSearchModeRental();
                    refreshSearchModeDrive();
                    refreshSearchModeWalk();
                    rlHomeSearchModes.setVisibility(View.VISIBLE);
                }

                disableAccessbilityForSaveTrip();
            }
        });
        rlHomeSearchModes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rlHomeSearchModes.setVisibility(View.GONE);
                enableAccessbilityForSaveTrip();
            }
        });

        ivCloseSearchMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rlHomeSearchModes.setVisibility(View.GONE);
                enableAccessbilityForSaveTrip();
            }
        });


        ivCloseSearchModeAccessibility.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rlHomeSearchModesAccessibilty.setVisibility(View.GONE);
                enableAccessbilityForSaveTrip();
            }
        });

        ivSearchBarReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetAction();
            }
        });

        rlSearchModeDrive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean enabledSearchModeDrive = !Configurations.getBoolean("enabled_search_mode_drive", true);
                Configurations.setBoolean("enabled_search_mode_drive", enabledSearchModeDrive);

                refreshSearchModeDrive();
                refreshSearchModesControlsChangedIcon();
            }
        });

        rlSearchModeDriveAccessibilityNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean enabledSearchModeDrive = !Configurations.getBoolean("enabled_search_mode_drive", true);
                Configurations.setBoolean("enabled_search_mode_drive", enabledSearchModeDrive);

                refreshSearchModeDrive();
                refreshSearchModesControlsChangedIcon();
            }
        });

        ivSearchModeDriveAccessibility.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean enabledSearchModeDrive = !Configurations.getBoolean("enabled_search_mode_drive", true);
                Configurations.setBoolean("enabled_search_mode_drive", enabledSearchModeDrive);

                refreshSearchModeDrive();
                refreshSearchModesControlsChangedIcon();
            }
        });

        rlSearchModeBike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean enabledSearchModeBike = !Configurations.getBoolean("enabled_search_mode_bike", true);
                Configurations.setBoolean("enabled_search_mode_bike", enabledSearchModeBike);

                refreshSearchModeBike();
                refreshSearchModesControlsChangedIcon();
            }
        });

        rlSearchModeBikeAccessibilityNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean enabledSearchModeBike = !Configurations.getBoolean("enabled_search_mode_bike", true);
                Configurations.setBoolean("enabled_search_mode_bike", enabledSearchModeBike);

                refreshSearchModeBike();
                refreshSearchModesControlsChangedIcon();
            }
        });

        ivSearchModeBikeAccessibility.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean enabledSearchModeBike = !Configurations.getBoolean("enabled_search_mode_bike", true);
                Configurations.setBoolean("enabled_search_mode_bike", enabledSearchModeBike);

                refreshSearchModeBike();
                refreshSearchModesControlsChangedIcon();
            }
        });

        rlSearchModeTransit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean enabledSearchModeTransit = !Configurations.getBoolean("enabled_search_mode_transit", true);
                Configurations.setBoolean("enabled_search_mode_transit", enabledSearchModeTransit);

                if (enabledSearchModeTransit) {
                    enableAllTransitSubmodes();
                }

                refreshSearchModeTransit();
                refreshSearchModesControlsChangedIcon();
            }
        });

        rlSearchModeTransitAccessibilityNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean enabledSearchModeTransit = !Configurations.getBoolean("enabled_search_mode_transit", true);
                Configurations.setBoolean("enabled_search_mode_transit", enabledSearchModeTransit);

                if (enabledSearchModeTransit) {
                    enableAllTransitSubmodes();
                }
                refreshSearchModeTransit();
                refreshSearchModesControlsChangedIcon();
            }
        });

        ivSearchModeTransitAccessibility.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean enabledSearchModeTransit = !Configurations.getBoolean("enabled_search_mode_transit", true);
                Configurations.setBoolean("enabled_search_mode_transit", enabledSearchModeTransit);

                if (enabledSearchModeTransit) {
                    enableAllTransitSubmodes();
                }
                refreshSearchModeTransit();
                refreshSearchModesControlsChangedIcon();
            }
        });

        rlSearchModeWalk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean enabledSearchModeWalk = !Configurations.getBoolean("enabled_search_mode_walk", true);
                Configurations.setBoolean("enabled_search_mode_walk", enabledSearchModeWalk);

                refreshSearchModeWalk();
                refreshSearchModesControlsChangedIcon();
            }
        });

        rlSearchModeWalkAccessibilityNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean enabledSearchModeWalk = !Configurations.getBoolean("enabled_search_mode_walk", true);
                Configurations.setBoolean("enabled_search_mode_walk", enabledSearchModeWalk);

                refreshSearchModeWalk();
                refreshSearchModesControlsChangedIcon();
            }
        });

        rlSearchModeRental.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean enabledSearchModeRental = !Configurations.getBoolean("enabled_search_mode_rental", true);
                Configurations.setBoolean("enabled_search_mode_rental", enabledSearchModeRental);

                if (enabledSearchModeRental) {
                    enableAllRentSubmodes();
                }
                refreshSearchModeRental();
                refreshSearchModesControlsChangedIcon();
            }
        });

        rlSearchModeRentalAccessibilityNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean enabledSearchModeRental = !Configurations.getBoolean("enabled_search_mode_rental", true);
                Configurations.setBoolean("enabled_search_mode_rental", enabledSearchModeRental);

                if (enabledSearchModeRental) {
                    enableAllRentSubmodes();
                }
                refreshSearchModeRental();
                refreshSearchModesControlsChangedIcon();
            }
        });

        ivSearchModeRentalAccessibility.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean enabledSearchModeRental = !Configurations.getBoolean("enabled_search_mode_rental", true);
                Configurations.setBoolean("enabled_search_mode_rental", enabledSearchModeRental);

                if (enabledSearchModeRental) {
                    enableAllRentSubmodes();
                }
                refreshSearchModeRental();
                refreshSearchModesControlsChangedIcon();
            }
        });

        atSearchOrigin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                gotoAddressSearchPage(true);
            }
        });

        atSearchDestination.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                gotoAddressSearchPage(false);
            }
        });
        llSearchDestination.setOnClickListener(view -> gotoAddressSearchPage(false));

        ivAddressSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchAddress();
            }
        });

        llSwitchLocationAccessibility.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchAddress();
            }
        });

        ivSearchSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideLayerPopup();

                hideHomeDateTime();

                disableAccessbilityForSaveTrip();

                if (rlHomeSetting != null && rlHomeSettingAccessibility != null) {

                    if (rlHomeSetting.getVisibility() == View.VISIBLE || rlHomeSettingAccessibility.getVisibility() == View.VISIBLE) {
                        hideHomeSetting();

                    } else {
                        if (Utilities.checkIfToDisplayAccessibilityUI(requireActivity())) {
                            refreshSearchModeRental();
                            rlHomeSettingAccessibility.setVisibility(View.VISIBLE);
                            ivSearchSetting.setColorFilter(getResources().getColor(android.R.color.black));
                            ivSearchSetting.setBackgroundResource(R.drawable.rounded_primary_with_shadow_bg);
                            refreshWalkSpeedAccessibilitySelectedOption();
                        } else {
                            refreshSearchModeRental();
                            rlHomeSetting.setVisibility(View.VISIBLE);
                            ivSearchSetting.setColorFilter(getResources().getColor(android.R.color.black));
                            ivSearchSetting.setBackgroundResource(R.drawable.rounded_primary_with_shadow_bg);
                        }

                        reloadMobilityProfileSpinner();
                        getDependantMobilityProfiles();
                    }
                }
            }
        });

        rlSearchDateTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideLayerPopup();

                hideHomeSetting();

                if (rlHomeDateTime != null) {
                    timePickerHourTmp = currentTimePickerHour;
                    timePickerMinTmp = currentTimePickerMin;

                    datePickerYearTmp = currentDatePickerYear;
                    datePickerMonthTmp = currentDatePickerMonth;
                    datePickerDayOfMonthTmp = currentDatePickerDayOfMonth;

                    isLeaveNowTmp = isLeaveNow;
                    isDepartAtTmp = isDepartAt;
                    isArriveByTmp = isArriveBy;
                    refreshDepartureOptionsStatus();
                    rlHomeDateTime.setVisibility(View.VISIBLE);
                    disableAccessbilityForSaveTrip();
                }
            }
        });

        llPlanTripSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                planTrip(view);
            }
        });

        llPlanTripAccessbility.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                planTrip(view);
            }
        });
        llPlanTripSearchPref.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //updateSpinner
                searchOptionsSpinnerIndex = tempSearchOptionsSpinnerIndex;
                switchToDefaultTimePicker(false);
                updateSearchOptionsSpinnerStatus(searchOptionsSpinnerIndex, false);

                saveAlterModesToSharedPref(requireActivity());
                if (Configurations.getBoolean("enabled_search_mode_accessible_routing", false)) {
                    llAccessibilitylegend.setVisibility(View.VISIBLE);
                } else {
                    llAccessibilitylegend.setVisibility(View.GONE);
                }

                clAdvancedPreferences.setVisibility(View.GONE);
                PlanViewMainLayout.setVisibility(View.VISIBLE);

                if (llBottom_tab != null && flFragmentHolder != null) {
                    llBottom_tab.setVisibility(View.VISIBLE);
                    RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) flFragmentHolder.getLayoutParams();
                    layoutParams.bottomMargin = originalBottomMargin;
                    flFragmentHolder.setLayoutParams(layoutParams);
                }

                initSearchBar();
            }
        });
        refreshSearchModesControlsChangedIcon();
        refreshSettingsChangedIcon();
    }

    private void enableAllRentSubmodes() {
        List<ParentItem> parentItems = Utilities.getExpandableListItems(requireActivity());
        for (ParentItem p : parentItems) {
            if (p.getName().equalsIgnoreCase(requireActivity().getString(R.string.rent))) {
                for (ChildItem c : p.getChildren()) {
                    c.setCheckedTemp(true);
                    c.setChecked(c.isCheckedTemp());
                    if (c.getName().equalsIgnoreCase(requireActivity().getString(R.string.allow_bike_rental))) {
                        Configurations.setBoolean("enabled_search_mode_rental", true);
                        Configurations.setBoolean("enabled_search_mode_bike_rental", true);
                    } else if (c.getName().equalsIgnoreCase(requireActivity().getString(R.string.allow_scooter_rental))) {
                        Configurations.setBoolean("enabled_search_mode_rental", true);
                        Configurations.setBoolean("enabled_search_mode_scooter_rental", true);
                    }
                }
            }
        }
    }

    private void enableAllTransitSubmodes() {
        List<ParentItem> parentItems = Utilities.getExpandableListItems(requireActivity());
        for (ParentItem p : parentItems) {
            if (p.getName().equalsIgnoreCase(requireActivity().getString(R.string.advance_transit))) {
                for (ChildItem c : p.getChildren()) {
                    c.setCheckedTemp(true);
                    c.setChecked(c.isCheckedTemp());
                    if (c.getName().equalsIgnoreCase(requireActivity().getString(R.string.link))) {
                        Configurations.setBoolean("home_setting_link_enabled", true);
                    } else if (c.getName().equalsIgnoreCase(requireActivity().getString(R.string.bus))) {
                        Configurations.setBoolean("home_setting_bus_enabled", true);
                    } else if (c.getName().equalsIgnoreCase(requireActivity().getString(R.string.sounder))) {
                        Configurations.setBoolean("home_setting_rail_enabled", true);
                    } else if (c.getName().equalsIgnoreCase(requireActivity().getString(R.string.street_car))) {
                        Configurations.setBoolean("home_setting_street_car_enabled", true);
                    } else if (c.getName().equalsIgnoreCase(requireActivity().getString(R.string.monorail))) {
                        Configurations.setBoolean("home_setting_monorail_enabled", true);
                    } else if (c.getName().equalsIgnoreCase(requireActivity().getString(R.string.water_taxi))) {
                        Configurations.setBoolean("home_setting_water_taxi_enabled", true);
                    } else if (c.getName().equalsIgnoreCase(requireActivity().getString(R.string.ferry))) {
                        Configurations.setBoolean("home_setting_ferry_enabled", true);
                    } else if (c.getName().equalsIgnoreCase(requireActivity().getString(R.string.marta_rail))) {
                        Configurations.setBoolean("home_setting_rail_enabled", true);
                    }
                }
            }
        }
    }

    private static void saveAlterModesToSharedPref(FragmentActivity requireActivity) {

        //update status
        Configurations.setBoolean("enabled_search_mode_accessible_routing", isAccessibleTrip);
        Configurations.setBoolean("enabled_search_mode_avoid_walking", isAvoidWalking);
        Configurations.setInt("walk_speed_index", walkSpeedSelectIndex);


        List<ParentItem> parentItems = Utilities.getExpandableListItems(requireActivity);
        //go over all mode checkboxes
        int rentalDeselectCount = 0;
        int transitSelectCount = 0;
        for (ParentItem p : parentItems) {
            for (ChildItem c : p.getChildren()) {
                c.setChecked(c.isCheckedTemp());
                if (c.isChecked()) {
                    if (c.getName().equalsIgnoreCase(requireActivity.getString(R.string.link))) {
                        Configurations.setBoolean("home_setting_link_enabled", true);
                        transitSelectCount++;
                    } else if (c.getName().equalsIgnoreCase(requireActivity.getString(R.string.bus))) {
                        Configurations.setBoolean("home_setting_bus_enabled", true);
                        transitSelectCount++;
                    } else if (c.getName().equalsIgnoreCase(requireActivity.getString(R.string.sounder))) {
                        Configurations.setBoolean("home_setting_rail_enabled", true);
                        transitSelectCount++;
                    } else if (c.getName().equalsIgnoreCase(requireActivity.getString(R.string.street_car))) {
                        Configurations.setBoolean("home_setting_street_car_enabled", true);
                        transitSelectCount++;
                    } else if (c.getName().equalsIgnoreCase(requireActivity.getString(R.string.monorail))) {
                        Configurations.setBoolean("home_setting_monorail_enabled", true);
                        transitSelectCount++;
                    } else if (c.getName().equalsIgnoreCase(requireActivity.getString(R.string.water_taxi))) {
                        Configurations.setBoolean("home_setting_water_taxi_enabled", true);
                        transitSelectCount++;
                    } else if (c.getName().equalsIgnoreCase(requireActivity.getString(R.string.ferry))) {
                        Configurations.setBoolean("home_setting_ferry_enabled", true);
                        transitSelectCount++;
                    } else if (c.getName().equalsIgnoreCase(requireActivity.getString(R.string.marta_rail))) {
                        Configurations.setBoolean("home_setting_rail_enabled", true);
                        transitSelectCount++;
                    } else if (c.getName().equalsIgnoreCase(requireActivity.getString(R.string.allow_bike_rental))) {
                        Configurations.setBoolean("enabled_search_mode_rental", true);
                        Configurations.setBoolean("enabled_search_mode_bike_rental", true);
                    } else if (c.getName().equalsIgnoreCase(requireActivity.getString(R.string.allow_scooter_rental))) {
                        Configurations.setBoolean("enabled_search_mode_rental", true);
                        Configurations.setBoolean("enabled_search_mode_scooter_rental", true);
                    }

                } else {
                    if (c.getName().equalsIgnoreCase(requireActivity.getString(R.string.link))) {
                        Configurations.setBoolean("home_setting_link_enabled", false);

                    } else if (c.getName().equalsIgnoreCase(requireActivity.getString(R.string.bus))) {
                        Configurations.setBoolean("home_setting_bus_enabled", false);

                    } else if (c.getName().equalsIgnoreCase(requireActivity.getString(R.string.sounder)) ||
                            c.getName().equalsIgnoreCase(requireActivity.getString(R.string.marta_rail))) {
                        Configurations.setBoolean("home_setting_rail_enabled", false);

                    } else if (c.getName().equalsIgnoreCase(requireActivity.getString(R.string.street_car))) {
                        Configurations.setBoolean("home_setting_street_car_enabled", false);

                    } else if (c.getName().equalsIgnoreCase(requireActivity.getString(R.string.monorail))) {
                        Configurations.setBoolean("home_setting_monorail_enabled", false);

                    } else if (c.getName().equalsIgnoreCase(requireActivity.getString(R.string.water_taxi))) {
                        Configurations.setBoolean("home_setting_water_taxi_enabled", false);

                    } else if (c.getName().equalsIgnoreCase(requireActivity.getString(R.string.ferry))) {
                        Configurations.setBoolean("home_setting_ferry_enabled", false);

                    } else if (c.getName().equalsIgnoreCase(requireActivity.getString(R.string.allow_bike_rental))) {
                        rentalDeselectCount++;
                        Configurations.setBoolean("enabled_search_mode_bike_rental", false);
                    } else if (c.getName().equalsIgnoreCase(requireActivity.getString(R.string.allow_scooter_rental))) {
                        rentalDeselectCount++;
                        Configurations.setBoolean("enabled_search_mode_scooter_rental", false);
                    }

                    //deselect rental here
                    Configurations.setBoolean("enabled_search_mode_rental", rentalDeselectCount != 2);

                    Configurations.setBoolean("enabled_search_mode_transit", transitSelectCount > 0);
                }

            }
        }

        for (int i = 0; i < parentItems.size(); i++) {
            ParentItem pItem = parentItems.get(i);
            pItem.setCheck(pItem.isCheckedTemp());
            if (pItem.getName().equalsIgnoreCase(requireActivity.getString(R.string.transit))) {
                Configurations.setBoolean("enabled_search_mode_transit", pItem.isCheck());
            } else if (pItem.getName().equalsIgnoreCase(requireActivity.getString(R.string.bike))) {
                Configurations.setBoolean("enabled_search_mode_bike", pItem.isCheck());
            } else if (pItem.getName().equalsIgnoreCase(requireActivity.getString(R.string.drive))) {
                Configurations.setBoolean("enabled_search_mode_drive", pItem.isCheck());
            } else if (pItem.getName().equalsIgnoreCase(requireActivity.getString(R.string.rent))) {
                Configurations.setBoolean("enabled_search_mode_rental", pItem.isCheck());
            } else if (pItem.getName().equalsIgnoreCase(requireActivity.getString(R.string.walk))) {
                Configurations.setBoolean("enabled_search_mode_walk", pItem.isCheck());
            }
            pItem.setCheckedTemp(pItem.isCheck());
        }
    }

    private void planTrip(View view) {

        if (validatedFields()) {
            tv_itinerary_origin.setText(atSearchOrigin.getText());
            tv_itinerary_Destination.setText(atSearchDestination.getText());
            hideKeyboard();
            hideLayerPopup();
            enterRouteSearchMode();

            itineraryArrayListWalkTransit.clear();
            itineraryArrayListBikeTransit.clear();
            itineraryArrayListDriveTransit.clear();
            itineraryArrayListBikeShareTransit.clear();
            itineraryArrayListScooterTransit.clear();
            itineraryArrayListOther.clear();

            routingErrorsArrayList.clear();
            if (llPlanTripErrors != null && llPlanTripErrors.getChildCount() > 0) {
                llPlanTripErrors.removeAllViews();
            }

            currentSelectedItineraryIndex = 0;
            currentMode = Constants.GROUP_ITINERARY_TYPE_WALK_TRANSIT;
            currentSelectedItinerary = null;

            if (itineraryListAdapterWalkTransit != null) {
                itineraryListAdapterWalkTransit.notifyDataSetInvalidated();
            }
            if (itineraryListAdapterBikeTransit != null) {
                itineraryListAdapterBikeTransit.notifyDataSetInvalidated();
            }
            if (itineraryListAdapterBikeShareTransit != null) {
                itineraryListAdapterBikeShareTransit.notifyDataSetChanged();
            }
            if (itineraryListAdapterScooterTransit != null) {
                itineraryListAdapterScooterTransit.notifyDataSetChanged();
            }
            if (itineraryListAdapterDriveTransit != null) {
                itineraryListAdapterDriveTransit.notifyDataSetInvalidated();
            }
            if (itineraryListOtherAdapter != null) {
                itineraryListOtherAdapter.notifyDataSetInvalidated();
            }
            setHeightToItineraryList();
            tvWalkTransitTitle.setVisibility(View.INVISIBLE);
            tvDriveTransitTitle.setVisibility(View.INVISIBLE);
            tvOtherTitle.setVisibility(View.INVISIBLE);
            tvBikeTransitTitle.setVisibility(View.INVISIBLE);
            tvBikeShareTransitTitle.setVisibility(View.INVISIBLE);
            tvScooterTransitTitle.setVisibility(View.INVISIBLE);

            String otherParams = getParamsExceptMode();
            populateRouteSearchModes();
            populateRouteErrors();

            String modeCombinationsString = ConfigManager.loadJsonFromInternalStorage(systemContext, MODE_COMBINATION_FILE_NAME);

            if (modeCombinationsString == null) {
                modeCombinationsString = Utilities.loadDataFromAsset(systemContext, "json/" + MODE_COMBINATION_FILE_NAME);
            }

            ModeCombinationConfig modeCombinationConfig = null;
            if (modeCombinationsString != null && !modeCombinationsString.isEmpty()) {
                ArrayList<String> seletedModes = getSelectedModes();
                ArrayList<String> seletedSubModes = getSelectedSubModes();
                Gson gson = new Gson();

                //enable here for new configuration
                for (int i = seletedSubModes.size() - 1; i >= 0; i--) {
                    String submodes = gson.toJson(seletedSubModes);
                    if (seletedSubModes.get(i).equals("STREETCAR") && !submodes.contains("TRAM")) {
                        seletedSubModes.set(i, "TRAM");
                    }

                    if (seletedSubModes.get(i).equals("WATER_TAXI") && !submodes.contains("FERRY")) {
                        seletedSubModes.set(i, "FERRY");
                    }

                    if (!modeCombinationsString.contains(seletedSubModes.get(i))) {
                        seletedSubModes.remove(i);
                    }
                }

                if (seletedModes.size() > 0 || seletedSubModes.size() > 0) {
                    try {
                        JSONArray jsonArray = new JSONArray(modeCombinationsString);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            JSONArray selectedModesJSONArray = jsonObject.optJSONArray("selectedModes");
                            JSONArray selectedSubModesJSONArray = jsonObject.optJSONArray("selectedSubModes");

                            boolean match = true;
                            if (selectedModesJSONArray != null && selectedSubModesJSONArray != null
                                    && seletedModes.size() == selectedModesJSONArray.length()
                                    && seletedSubModes.size() == selectedSubModesJSONArray.length()) {
                                for (int j = 0; j < selectedModesJSONArray.length(); j++) {
                                    String selectedModeConfig = selectedModesJSONArray.getString(j);
                                    if (!seletedModes.contains(selectedModeConfig)) {
                                        match = false;
                                        break;
                                    }
                                }
                                if (match) {
                                    for (int k = 0; k < selectedSubModesJSONArray.length(); k++) {
                                        String selectedSubModeConfig = selectedSubModesJSONArray.getString(k);
                                        if (!seletedSubModes.contains(selectedSubModeConfig)) {
                                            match = false;
                                            break;
                                        }
                                    }
                                }

                                if (match) {
                                    modeCombinationConfig = gson.fromJson(jsonObject.toString(),
                                            ModeCombinationConfig.class);
                                    break;
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            if ((!GRAPHQL_SERVER_URL.isEmpty() && modeCombinationConfig != null && modeCombinationConfig.getSentModeCombinations() != null
                    && modeCombinationConfig.getSentModeCombinations().size() > 0)
                    || routeSearchModes.size() > 0) {
                tvItinerariesFound.setText(getString(R.string.finding_your_options));
                pbItineraryList.setVisibility(View.VISIBLE);
                if (noInternetPopupDialog != null) {
                    noInternetPopupDialog.dismiss();
                    noInternetPopupDialog = null;
                }

                planQuerySummaries = new ArrayList<>();

                if (!GRAPHQL_SERVER_URL.isEmpty() && modeCombinationConfig != null) {
                    planTripQueryCount = 0;
                    for (ModeCombination modeCombination : modeCombinationConfig.getSentModeCombinations()) {
                        routeSearch(otherParams, modeCombination, null);
                    }
                } else {
                    for (ModeConfig modeConfig : routeSearchModes) {
                        routeSearch(otherParams, null, modeConfig);
                    }
                }
            } else {

                if (modeCombinationConfig == null) {

                    if (llPlanTripErrors != null && llPlanTripErrors.getChildCount() > 0) {
                        llPlanTripErrors.removeAllViews();
                    }

                    View errorView = getLayoutInflater().inflate(R.layout.item_plan_trip_error, null, false);
                    TextView tvPlanTripError = errorView.findViewById(R.id.tv_plan_trip_error);
                    tvPlanTripError.setText(getString(R.string.mode_combination_error));

                    if (llPlanTripErrors != null) {
                        llPlanTripErrors.addView(errorView);
                        llPlanTripErrors.setVisibility(View.VISIBLE);
                    }
                    pbItineraryList.setVisibility(View.GONE);
                } else {
                    tvItinerariesFound.setText(getString(R.string.itinerary_found_with_num, 0));
                    pbItineraryList.setVisibility(View.GONE);
                }
            }

            view.post(new Runnable() {
                @Override
                public void run() {
                    if (getActivity() != null && !getActivity().isFinishing()) {
                        moveMapToShowRoute();
                    }
                }
            });

            disableAccessbilityForPlanTrip();
        }
    }


    private void disableAccessbilityForPlanTrip() {
        rlSearchBar.setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_NO_HIDE_DESCENDANTS);
        rlLayer.setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_NO_HIDE_DESCENDANTS);
        rlLocateMe.setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_NO_HIDE_DESCENDANTS);
    }

    private void enableAccessbilityForPlanTrip() {
        rlSearchBar.setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_AUTO);
        rlLayer.setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_AUTO);
        rlLocateMe.setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_AUTO);
    }

    private void showKeyboard() {
        if (getActivity() != null && atSearch != null) {
            atSearch.requestFocus();
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(atSearch, InputMethodManager.SHOW_FORCED);
        }
    }

    private void hideKeyboard() {
        if (getActivity() != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
            if (imm != null) {
                //Find the currently focused view, so we can grab the correct window token from it.
                View view = getActivity().getCurrentFocus();
                //If no view currently has focus, create a new one, just so we can grab a window token from it
                if (view == null) {
                    view = new View(getActivity());
                }
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                view.clearFocus();

                if (atSearch == null && mFragmentView != null) {
                    atSearch = mFragmentView.findViewById(R.id.at_search);
                }
                if (atSearch != null) {
                    imm.hideSoftInputFromWindow(atSearch.getWindowToken(), 0);
                }

            }
        }
    }

    private void switchAddress() {
        String temp = atSearchOrigin.getText().toString();
        atSearchOrigin.setText(atSearchDestination.getText());
        atSearchDestination.setText(temp);

        LocationPoint tempLocationPoint = new LocationPoint(originPoint);

        originPoint.setAddress(destinationPoint.getAddress());
        originPoint.setLat(destinationPoint.getLat());
        originPoint.setLon(destinationPoint.getLon());

        destinationPoint.setAddress(tempLocationPoint.getAddress());
        destinationPoint.setLat(tempLocationPoint.getLat());
        destinationPoint.setLon(tempLocationPoint.getLon());

        if (originPoint.getAddress() != null && !originPoint.getAddress().isEmpty()) {
            showOriginMarkersOnMap();
        } else {
            removeOriginMarkersFromMap();
        }

        if (destinationPoint.getAddress() != null && !destinationPoint.getAddress().isEmpty()) {
            showDestinationMarkersOnMap();
        } else {
            removeDestinationMarkersFromMap();
        }
    }

    private void hideHomeSetting() {
        if (Utilities.checkIfToDisplayAccessibilityUI(requireActivity())) {

            if (rlHomeSettingAccessibility != null) {
                rlHomeSettingAccessibility.setVisibility(View.GONE);
                ivSearchSetting.setColorFilter(null);
                ivSearchSetting.setBackground(null);
                enableAccessbilityForSaveTrip();
            }

        } else {

            if (rlHomeSetting != null) {
                rlHomeSetting.setVisibility(View.GONE);
                ivSearchSetting.setColorFilter(null);
                ivSearchSetting.setBackground(null);
                enableAccessbilityForSaveTrip();
            }
        }
    }

    private void hideHomeDateTime() {
        if (rlHomeDateTime != null) {
            rlHomeDateTime.setVisibility(View.GONE);
            enableAccessbilityForSaveTrip();
        }
    }

    private void moveMapToShowRoute() {
        if (gMapboxMap != null && originPoint != null && destinationPoint != null) {
            LatLngBounds.Builder latLngBoundsBuilder = new LatLngBounds.Builder();

            latLngBoundsBuilder.include(new LatLng(originPoint.getLat(), originPoint.getLon()));
            latLngBoundsBuilder.include(new LatLng(destinationPoint.getLat(), destinationPoint.getLon()));
            int paddingTop = Convert.dpToPx(120);
            int paddingBottom;

            if (gDm.heightPixels >= Convert.dpToPx(700)) {
                paddingBottom = Convert.dpToPx(400);
            } else {
                paddingBottom = (gDm.heightPixels - Convert.dpToPx(60)) / 2;
            }

            gMapboxMap.moveCamera(CameraUpdateFactory.newLatLngBounds(latLngBoundsBuilder.build(),
                    Convert.dpToPx(26), paddingTop, Convert.dpToPx(26), paddingBottom), null);
        }
    }

    private void removeOriginMarkersFromMap() {
        if (mvOrigin != null) {
            markerViewManager.removeMarker(mvOrigin);
            mvOrigin = null;
        }
    }

    private void removeDestinationMarkersFromMap() {
        if (mvDestination != null) {
            markerViewManager.removeMarker(mvDestination);
            mvDestination = null;
        }
    }

    private void moveMapToShowOriginAndDestination() {
        if (gMapboxMap != null) {
            if (mvOrigin != null && mvDestination != null) {
                LatLngBounds.Builder latLngBoundsBuilder = new LatLngBounds.Builder();
                latLngBoundsBuilder.include(mvOrigin.getMarkerViewLatLng());
                latLngBoundsBuilder.include(mvDestination.getMarkerViewLatLng());

                gMapboxMap.moveCamera(CameraUpdateFactory.newLatLngBounds(latLngBoundsBuilder.build(),
                        Convert.dpToPx(76), Convert.dpToPx(316), Convert.dpToPx(76), Convert.dpToPx(76)), null);
            } else if (mvOrigin != null) {
                gMapboxMap.moveCamera(CameraUpdateFactory.newLatLng(mvOrigin.getMarkerViewLatLng()));
            } else if (mvDestination != null) {
                gMapboxMap.moveCamera(CameraUpdateFactory.newLatLng(mvDestination.getMarkerViewLatLng()));
            }
        }
    }

    private void showOriginMarker() {
        if (gMapboxMap != null && markerViewManager != null) {
            if (mvOrigin == null) {
                mvOrigin = addMarkerViewOriginDestination(new LatLng(originPoint.getLat(), originPoint.getLon()), true);
            } else {
                mvOrigin.setLatLng(new LatLng(originPoint.getLat(), originPoint.getLon()));
                mvOrigin.setMarkerViewLatLng(new LatLng(originPoint.getLat(), originPoint.getLon()));
            }
        }
    }

    private void showOriginMarkersOnMap() {
        if (originPoint.getAddress() != null && !originPoint.getAddress().isEmpty()) {
            showOriginMarker();

            moveMapToShowOriginAndDestination();
        }
    }

    private CustomizeMarkerView addMarkerViewOriginDestination(LatLng latlng, boolean isOrigin) {
        if (getActivity() == null) {
            return null;
        }
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
                if (mvDestination == null) {
                    mvDestination = addMarkerViewOriginDestination(new LatLng(destinationPoint.getLat(), destinationPoint.getLon()), false);
                } else {
                    mvDestination.setLatLng(new LatLng(destinationPoint.getLat(), destinationPoint.getLon()));
                    mvDestination.setMarkerViewLatLng(new LatLng(destinationPoint.getLat(), destinationPoint.getLon()));
                }
            }
        }
    }

    private void showDestinationMarkersOnMap() {
        if (destinationPoint.getAddress() != null && !destinationPoint.getAddress().isEmpty()) {
            showDestinationMarker();

            moveMapToShowOriginAndDestination();
        }
    }

    private boolean isOnlyTransitEnabled() {
        boolean ret = false;

        boolean enabledSearchModeDrive = Configurations.getBoolean("enabled_search_mode_drive", false);
        boolean enabledSearchModeBike = Configurations.getBoolean("enabled_search_mode_bike", true);
        boolean enabledSearchModeTransit = Configurations.getBoolean("enabled_search_mode_transit", true);
        boolean enabledSearchModeWalk = Configurations.getBoolean("enabled_search_mode_walk", true);
        boolean enabledSearchModeRental = Configurations.getBoolean("enabled_search_mode_rental", true);

        if (enabledSearchModeTransit && !enabledSearchModeDrive && !enabledSearchModeBike
                && !enabledSearchModeWalk && !enabledSearchModeRental) {
            ret = true;
        }
        return ret;
    }

    private boolean isModeEnabled(String filter) {
        if (filter != null) {
            if (filter.isEmpty()) {
                return true;
            } else {
                String[] filterSplit = filter.split(",");

                for (int i = 0; i < filterSplit.length; i++) {
                    String searchMode = filterSplit[i].trim();
                    if (!Configurations.getBoolean("enabled_search_mode_" + searchMode, true)) {
                        return false;
                    }
                }
                return true;
            }
        }
        return false;
    }

    private ArrayList<String> getSelectedModes() {
        ArrayList<String> selectedModes = new ArrayList<>();

        if (Configurations.getBoolean("enabled_search_mode_transit", true)) {
            selectedModes.add("TRANSIT");

            //deselect modes when no submodes select. todo
            if (!Configurations.getBoolean("home_setting_bus_enabled", false)
                    && !Configurations.getBoolean("home_setting_rail_enabled", false)
                    && !Configurations.getBoolean("home_setting_ferry_enabled", false)
                    && !Configurations.getBoolean("home_setting_gondola_enabled", false)
                    && !Configurations.getBoolean("home_setting_link_enabled", false)
                    && !Configurations.getBoolean("home_setting_street_car_enabled", false)
                    && !Configurations.getBoolean("home_setting_water_taxi_enabled", false)
                    && !Configurations.getBoolean("home_setting_monorail_enabled", false)) {
                selectedModes = new ArrayList<>();
            }
        }
        if (Configurations.getBoolean("enabled_search_mode_bike", false)) {
            selectedModes.add("BICYCLE");
        }
        if (Configurations.getBoolean("enabled_search_mode_drive", false)) {
            selectedModes.add("CAR");
        }

        if (Configurations.getBoolean("enabled_search_mode_rental", true)) {
            selectedModes.add("ST-RENT");
        }

        String selectableModes = Configurations.getString("selectable_modes",
                "TRANSIT:BUS,TRAM,FERRY,RAIL,STREETCAR,WATER_TAXI;BIKE;WALK;CAR;ST-RENT").toLowerCase(Locale.ROOT);
        if (selectableModes.contains("walk") && Configurations.getBoolean("enabled_search_mode_walk", true)) {
            selectedModes.add("WALK");
        } else {
            if (!selectableModes.contains("walk") && selectedModes.size() > 0) {
                selectedModes.add("WALK");
            }
        }
        return selectedModes;
    }

    private ArrayList<String> getAllSubModes() {
        ArrayList<String> allSubModes = new ArrayList<>();
        allSubModes.add("BICYCLE_RENT");
        allSubModes.add("SCOOTER_RENT");
        allSubModes.add("BUS");
        allSubModes.add("RAIL");
        allSubModes.add("FERRY");
        allSubModes.add("TRAM");
        allSubModes.add("STREETCAR");
        allSubModes.add("WATER_TAXI");
        allSubModes.add("MONORAIL");
        return allSubModes;
    }

    private ArrayList<String> getSelectedSubModes() {
        ArrayList<String> selectedSubModes = new ArrayList<>();
        if (Configurations.getBoolean("enabled_search_mode_rental", true) &&
                Configurations.getBoolean("enabled_search_mode_bike_rental", true)) {
            selectedSubModes.add("BICYCLE_RENT");
        }
        if (Configurations.getBoolean("enabled_search_mode_rental", true) &&
                Configurations.getBoolean("enabled_search_mode_scooter_rental", true)) {
            selectedSubModes.add("SCOOTER_RENT");
        }
        if (Configurations.getBoolean("enabled_search_mode_transit", true)) {
            if (Configurations.getBoolean("home_setting_bus_enabled", true)) {
                selectedSubModes.add("BUS");
            }
            if (Configurations.getBoolean("home_setting_rail_enabled", true)) {
                selectedSubModes.add("RAIL");
                selectedSubModes.add("SUBWAY");
            }
            if (Configurations.getBoolean("home_setting_ferry_enabled", true)) {
                selectedSubModes.add("FERRY");
            }
            if (llHomeSettingGondola != null && llHomeSettingGondola.getVisibility() == View.VISIBLE
                    && Configurations.getBoolean("home_setting_gondola_enabled", false)) {
                selectedSubModes.add("GONDOLA");
            }
            if (Configurations.getBoolean("home_setting_link_enabled", true)) {
                selectedSubModes.add("TRAM");
            }
            if (Configurations.getBoolean("home_setting_street_car_enabled", true)) {
                selectedSubModes.add("STREETCAR");
            }
            if (Configurations.getBoolean("home_setting_water_taxi_enabled", true)) {
                selectedSubModes.add("WATER_TAXI");
            }
            if (Configurations.getBoolean("home_setting_monorail_enabled", true)) {
                selectedSubModes.add("MONORAIL");
            }
        }
        return selectedSubModes;
    }

    private void populateRouteSearchModes() {
        routeSearchModes.clear();

        String configData = Utilities.loadDataFromAsset(systemContext, "json/mode_config.json");
        if (configData != null) {
            try {
                Gson gson = new Gson();

                ArrayList<ModeConfig> modeConfigs = gson.fromJson(configData, new TypeToken<ArrayList<ModeConfig>>() {
                }.getType());

                boolean isOnlyTransitEnabled = isOnlyTransitEnabled();
                for (ModeConfig modeConfig : modeConfigs) {
                    if (isOnlyTransitEnabled && (
                            modeConfig.getMode().equalsIgnoreCase("WALK,TRANSIT") ||
                                    modeConfig.getMode().equalsIgnoreCase("WALK"))) {
                        routeSearchModes.add(modeConfig);
                    } else if (isModeEnabled(modeConfig.getFilter())) {
                        routeSearchModes.add(modeConfig);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    private void populateRouteErrors() {

        if (configRouteErrors != null) {
            configRouteErrors.clear();
        }

        String configData = Utilities.loadDataFromAsset(systemContext, "json/trips_error_mapping.json");
        if (configData != null) {
            try {
                Gson gson = new Gson();

                configRouteErrors = gson.fromJson(configData, new TypeToken<ArrayList<TripError>>() {
                }.getType());

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private String getDateFromSystem() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());

        return String.format(Locale.getDefault(), "%d-%02d-%02d",
                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH));
    }

    private String getTimeFromSystem() {
        Calendar calendar = Calendar.getInstance();

        long currentTime = System.currentTimeMillis();
        int currentTimeZoneOffset = 0;
        int targetOffset = 0;

        if (!Configurations.TARGET_TIMEZONE_ID.isEmpty()) {
            currentTimeZoneOffset = calendar.getTimeZone().getOffset(currentTime);
            targetOffset = TimeZone.getTimeZone(Configurations.TARGET_TIMEZONE_ID).getOffset(currentTime);
        }
        calendar.setTimeInMillis(currentTime - currentTimeZoneOffset + targetOffset);
        return String.format(Locale.getDefault(), "%02d:%02d", calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE));
    }

    private String getDateFromPicker() {
        currentDatePickerYear = datePickerYearTmp;
        currentDatePickerMonth = datePickerMonthTmp;
        currentDatePickerDayOfMonth = datePickerDayOfMonthTmp;

        if (datePickerDialog != null && datePickerDialog.getDatePicker() != null && !isLeaveNow) {
            int year = datePickerDialog.getDatePicker().getYear();
            int month = datePickerDialog.getDatePicker().getMonth();
            int dayOfMonth = datePickerDialog.getDatePicker().getDayOfMonth();

            return String.format(Locale.getDefault(), "%d-%02d-%02d", year, month + 1, dayOfMonth);
        } else {
            return getDateFromSystem();
        }
    }

    private String getTimeFromPicker() {
        currentTimePickerHour = timePickerHourTmp;
        currentTimePickerMin = timePickerMinTmp;
        if (timePickerDialog != null && !isLeaveNow) {
            return String.format(Locale.getDefault(), "%02d:%02d", currentTimePickerHour, currentTimePickerMin);
        } else {
            return getTimeFromSystem();
        }

    }

    private String getWalkSpeed() {
        int index = Configurations.getInt("walk_speed_index", Constants.DEFAULT_WALK_SPEED_INDEX);
        String[] stringArrays = systemContext.getResources().getStringArray(R.array.walk_speed_arrays_value_otp);
        if (index < stringArrays.length) {
            return stringArrays[index];
        } else {
            return "1.34";
        }
    }

    private double getWalkSpeedDouble() {
        int index = Configurations.getInt("walk_speed_index", Constants.DEFAULT_WALK_SPEED_INDEX);
        String[] stringArrays = systemContext.getResources().getStringArray(R.array.walk_speed_arrays_value_otp);
        if (index < stringArrays.length) {
            return Double.valueOf(stringArrays[index]);
        } else {
            return 1.34;
        }
    }

    private String getMaxWalkDistance() {
        int maxWalkDistanceIndex = Configurations.getInt("maximum_walk_index", Constants.DEFAULT_MAXIMUM_WALK_INDEX);
        String[] stringArrays = systemContext.getResources().getStringArray(R.array.maximum_walk_arrays_value);
        if (maxWalkDistanceIndex < stringArrays.length) {
            return stringArrays[maxWalkDistanceIndex];
        } else {
            return "";
        }
    }

    private double getWalkReluctance() {
        //if enabled avoid walking WalkReluctance is 35 otherwise 15
        if (Configurations.getBoolean("enabled_search_mode_avoid_walking", false)) {
            return 35;
        } else {
            return 15;
        }
    }

    private String getOptimizeOption() {
        int index = Configurations.getInt("optimize_option_index", Constants.DEFAULT_OPTIMIZE_OPTION_INDEX);
        String[] stringArrays = systemContext.getResources().getStringArray(R.array.optimize_option_arrays_value);
        if (index < stringArrays.length) {
            return stringArrays[index];
        } else {
            return "";
        }
    }

    private String getParamsExceptMode() {
        StringBuilder params = new StringBuilder();
        params.append("?");
        params.append(Constants.DEFAULT_ROUTE_SEARCH_STRING);
        params.append("&wheelchair=");
        params.append(Configurations.getBoolean("enabled_search_mode_accessible_routing", false));

        if (originPoint != null) {
            params.append("&fromPlace=");
            params.append(Uri.encode(originPoint.getAddress() + "::" + originPoint.getLat() + "," + originPoint.getLon()));
        }

        if (destinationPoint != null) {
            params.append("&toPlace=");
            params.append(Uri.encode(destinationPoint.getAddress() + "::" + destinationPoint.getLat() + "," + destinationPoint.getLon()));
        }

        params.append("&date=");
        if (isArriveBy) {
            params.append(Uri.encode(getDateFromPicker()));
            params.append("&time=");
            params.append(Uri.encode(getTimeFromPicker()));
            params.append("&arriveBy=true");
        } else {
            if (isLeaveNow) {
                params.append(Uri.encode(getDateFromSystem()));
                params.append("&time=");
                params.append(Uri.encode(getTimeFromSystem()));
            } else {
                params.append(Uri.encode(getDateFromPicker()));
                params.append("&time=");
                params.append(Uri.encode(getTimeFromPicker()));
            }
            params.append("&arriveBy=false");
        }

        params.append("&maxWalkDistance=");
        params.append(Uri.encode(getMaxWalkDistance()));
        params.append("&optimize=");
        params.append(Uri.encode(getOptimizeOption()));
        params.append("&walkSpeed=");
        params.append(Uri.encode(getWalkSpeed()));
        return params.toString();
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
        removeRoutePolylineFromMap();
        removeRouteMarkerViewsFromMap();
        currentSelectedItinerary = itinerary;
        ArrayList<ItineraryLeg> itineraryLegs = currentSelectedItinerary.getLegs();
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
                    routeColor = Utilities.getRouteColorByMode(getActivity(), mode);
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
                            if (mode.equalsIgnoreCase("FERRY") || mode.equalsIgnoreCase("WATER_TAXI")) {
                                try {
                                    routeNumber = itineraryLeg.getLongName();
                                } catch (Exception e) {
                                    Log.e("PlanTripFragment", "Fail to load Ferry long name");
                                }
                            }
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
            }
        }
    }

    private void showRoutePolylineOnMap(Itinerary itinerary) {
        if (itinerary != null && !itinerary.equals(currentSelectedItinerary)) {
            drawRoutePolylineOnMap(itinerary);
        }
    }

    private String getTransitModeString() {
        StringBuilder stringBuilder = new StringBuilder();

        //BUS,SUBWAY,TRAM
        if (Configurations.getBoolean("home_setting_bus_enabled", true)) {
            stringBuilder.append("BUS");
        }
        if (Configurations.getBoolean("home_setting_rail_enabled", true)) {
            if (stringBuilder.length() > 0) {
                stringBuilder.append(",");
            }
            stringBuilder.append(getString(R.string.search_rail_mode)); //
        }
        if (Configurations.getBoolean("home_setting_street_car_enabled", true)) {
            if (stringBuilder.length() > 0) {
                stringBuilder.append(",");
            }
            stringBuilder.append("TRAM");
        }
        if (llHomeSettingFerry != null && llHomeSettingFerry.getVisibility() == View.VISIBLE
                && Configurations.getBoolean("home_setting_ferry_enabled", true)) {
            if (stringBuilder.length() > 0) {
                stringBuilder.append(",");
            }
            stringBuilder.append("FERRY");
        }

        return stringBuilder.toString();
    }

    private void routeSearch(String otherParams, ModeCombination modeCombination, ModeConfig modeConfig) {
        isLeaveNow = isLeaveNowTmp;
        isDepartAt = isDepartAtTmp;
        isArriveBy = isArriveByTmp;

        if (otherParams == null || (modeConfig == null && modeCombination == null)) {
            return;
        }

        if (modeCombination != null) {
            ApolloClient apolloClient = ApolloClient.builder()
                    .serverUrl(GRAPHQL_SERVER_URL)
                    .build();

            List<TransportMode> transportModes = new ArrayList<>();
            List<String> modesForDebug = new ArrayList<>();
            StringBuilder stringBuilder = new StringBuilder();

            if (modeCombination != null && modeCombination.getModes() != null) {
                for (TransportModeCombination transportModeCombination : modeCombination.getModes()) {
                    if (!stringBuilder.toString().isEmpty()) {
                        stringBuilder.append(",");
                    }
                    stringBuilder.append(transportModeCombination.getMode());
                    String m = transportModeCombination.getMode();
                    if (m.equalsIgnoreCase("BUS")) {
                        transportModes.add(TransportMode.builder().mode(BUS).build());
                        modesForDebug.add(BUS.rawValue());
                    } else if (m.equalsIgnoreCase("TRAM")) {
                        transportModes.add(TransportMode.builder().mode(TRAM).build());
                        modesForDebug.add(TRAM.rawValue());
                    } else if (m.equalsIgnoreCase("SUBWAY")) {
                        transportModes.add(TransportMode.builder().mode(SUBWAY).build());
                        modesForDebug.add(SUBWAY.rawValue());
                    } else if (m.equalsIgnoreCase("FERRY")) {
                        transportModes.add(TransportMode.builder().mode(FERRY).build());
                        modesForDebug.add(FERRY.rawValue());
                    } else if (m.equalsIgnoreCase("RAIL")) {
                        transportModes.add(TransportMode.builder().mode(RAIL).build());
                        modesForDebug.add(RAIL.rawValue());
                    } else if (m.equalsIgnoreCase("MONORAIL")) {
                        transportModes.add(TransportMode.builder().mode(MONORAIL).build());
                        modesForDebug.add(MONORAIL.rawValue());
                    } else if (m.equalsIgnoreCase("CAR")) {
                        if (transportModeCombination.getQualifier() != null && !transportModeCombination.getQualifier().isEmpty()) {
                            transportModes.add(TransportMode.builder().mode(CAR).qualifier(Qualifier.PARK).build());
                            modesForDebug.add(CAR.rawValue() + ":" + Qualifier.PARK.rawValue());
                        } else {
                            transportModes.add(TransportMode.builder().mode(CAR).build());
                            modesForDebug.add(CAR.rawValue());
                        }
                    } else if (m.equalsIgnoreCase("BICYCLE")) {
                        if (transportModeCombination.getQualifier() != null && !transportModeCombination.getQualifier().isEmpty()) {
                            transportModes.add(TransportMode.builder().mode(BICYCLE).qualifier(Qualifier.RENT).build());
                            modesForDebug.add(BICYCLE.rawValue() + ":" + Qualifier.RENT.rawValue());
                        } else {
                            transportModes.add(TransportMode.builder().mode(BICYCLE).build());
                            modesForDebug.add(BICYCLE.rawValue());
                        }
                    } else if (m.equalsIgnoreCase("SCOOTER")) {
                        if (transportModeCombination.getQualifier() != null && !transportModeCombination.getQualifier().isEmpty()) {
                            transportModes.add(TransportMode.builder().mode(SCOOTER).qualifier(Qualifier.RENT).build());
                            modesForDebug.add(SCOOTER.rawValue() + ":" + Qualifier.RENT.rawValue());
                        } else {
                            transportModes.add(TransportMode.builder().mode(SCOOTER).build());
                            modesForDebug.add(SCOOTER.rawValue());
                        }
                    } else if (m.equalsIgnoreCase("WALK")) {
                        transportModes.add(TransportMode.builder().mode(WALK).build());
                        modesForDebug.add(WALK.rawValue());
                    }
                }
            }

            String mode = stringBuilder.toString();
            Input<List<TransportMode>> modeInput = new Input<>(transportModes, true);

            final String params = otherParams + "&mode=" + Uri.encode(mode) + "&numItineraries=3";

            String from = "";
            if (originPoint != null) {
                from = originPoint.getAddress() + "::" + originPoint.getLat() + "," + originPoint.getLon();
            }

            String to = "";
            if (destinationPoint != null) {
                to = destinationPoint.getAddress() + "::" + destinationPoint.getLat() + "," + destinationPoint.getLon();
            }

            String date = getDateFromPicker();
            String time = getTimeFromPicker();
            boolean currentIsArriveBy = true;
            // 0 = leave, 1 depart, 2 arrive by
            if (spSearchOptions != null && spSearchOptions.getSelectedItemPosition() != 2) {
                if (spSearchOptions.getSelectedItemPosition() == 0) {
                    date = getDateFromSystem();
                    time = getTimeFromSystem();
                }
                currentIsArriveBy = false;
            }

            // deal with banned list here
            String allSubModes = String.join(",", getSelectedSubModes());

            String bannedIDsString = "";//bannedID.toString();
            InputBanned inputBanned = InputBanned.builder().routes(bannedIDsString).build();

            Input<InputBanned> banned = new Input<>(inputBanned, false);//!bannedIDsString.isEmpty());
            String mobilityProfileStr = Utilities.getCurrentMobilityProfile();
            int numItineraries = 3;
            PlanQuery planQuery = new PlanQuery(from,
                    to,
                    modeInput,
                    banned,
                    new Input<>(time, true),
                    new Input<>(date, true),
                    new Input<>(Configurations.getBoolean("enabled_search_mode_accessible_routing", false), false),
                    new Input<>(3.0, false),
                    new Input<>(2.0, false),
                    new Input<>(getWalkReluctance(), false),
                    new Input<>(getWalkSpeedDouble(), false),
                    new Input<>(currentIsArriveBy, true),
                    new Input<>(numItineraries, true),
                    new Input<>(mobilityProfileStr, !mobilityProfileStr.isEmpty())// && !mobilityProfileStr.equalsIgnoreCase("none")
            );


            //load the queries here for debugging purpose.
            String summary = "Arrive By: " + currentIsArriveBy + "\n" +
                    "Banned: " + bannedIDsString + "\n" +
                    "Date: " + date + "\n" +
                    "From: " + from + "\n" +
                    "QueryMode: " + modesForDebug + "\n" +
                    "NumItineraries: " + numItineraries + "\n" +
                    "Time: " + time + "\n" +
                    "To: " + to + "\n" +
                    "WalkReluctance: " + getWalkReluctance() + "\n" +
                    "WalkSpeed: " + getWalkSpeedDouble() + "\n" +
                    "WheelChair: " + Configurations.getBoolean("enabled_search_mode_accessible_routing", false);

            planQuerySummaries.add(summary);

            Otp2QueryParams monitoredOtp2QueryParams = new Otp2QueryParams();
            //update monitoredOtp2QueryParams for saved trip
            monitoredOtp2QueryParams.setArriveBy(currentIsArriveBy);
            Banned bannedString = new Banned();
            if (!bannedIDsString.isEmpty()) {
                bannedString.setRoutes(bannedIDsString);
            } else {
                bannedString.setRoutes(null);
            }

            monitoredOtp2QueryParams.setBanned(bannedString);
            monitoredOtp2QueryParams.setDate(date);
            monitoredOtp2QueryParams.setFromPlace(from);

            ArrayList<QueryMode> modes = new ArrayList<>();
            for (String s : modesForDebug) {
                QueryMode queryMode = new QueryMode();
                String[] temp = s.split(":");
                if (temp.length < 2) {
                    queryMode.setMode(s);
                } else {
                    queryMode.setMode(temp[0]);
                    queryMode.setQualifier(temp[1]);
                }
                modes.add(queryMode);
            }

            monitoredOtp2QueryParams.setModes(modes);
            monitoredOtp2QueryParams.setNumItineraries(numItineraries);
            monitoredOtp2QueryParams.setTime(time);
            monitoredOtp2QueryParams.setToPlace(to);
            monitoredOtp2QueryParams.setWalkReluctance(getWalkReluctance());
            monitoredOtp2QueryParams.setWalkSpeed(getWalkSpeedDouble());
            monitoredOtp2QueryParams.setWheelchair(Configurations.getBoolean("enabled_search_mode_accessible_routing", false));
            monitoredOtp2QueryParams.setMobilityProfile(mobilityProfileStr);

            apolloClient.query(planQuery).enqueue(new ApolloCall.Callback<PlanQuery.Data>() {
                @Override
                public void onResponse(@NonNull com.apollographql.apollo.api.Response<PlanQuery.Data> response) {

                    Handler mainHandler = new Handler(Looper.getMainLooper());
                    mainHandler.post(() -> {
                        parseRouteSearchData(response, params, monitoredOtp2QueryParams);
                        pbItineraryList.setVisibility(View.GONE);
                    });
                }

                @Override
                public void onFailure(@NonNull ApolloException e) {
                    Logger.e("graphql", e.toString());
                    Handler mainHandler = new Handler(Looper.getMainLooper());
                    mainHandler.post(() -> {
                        int itinerariesNum = itineraryArrayListWalkTransit.size() + itineraryArrayListBikeTransit.size() + itineraryArrayListDriveTransit.size() + itineraryArrayListBikeShareTransit.size() + itineraryArrayListScooterTransit.size() +
                                itineraryArrayListOther.size();
                        if (itinerariesNum <= 1) {
                            tvItinerariesFound.setText(systemContext.getString(R.string.itinerary_found_with_num, itinerariesNum));
                        } else {
                            tvItinerariesFound.setText(systemContext.getString(R.string.itineraries_found_with_num, itinerariesNum));
                        }

                        if (noInternetPopupDialog == null && e instanceof ApolloNetworkException &&
                                !Utilities.hasInternetConnectivity()) {
                            noInternetPopupDialog = new CustomizePopupDialog(getActivity(),
                                    getString(R.string.no_internet), getString(R.string.no_internet_message),
                                    getString(R.string.ok), null, new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    noInternetPopupDialog.dismiss();
                                    noInternetPopupDialog = null;
                                }
                            }, null, R.drawable.ic_nointernet);
                            noInternetPopupDialog.show();
                        }
                        pbItineraryList.setVisibility(View.GONE);
                    });
                }
            });

        } else {
            String url = Resource.API_TRIP_PLAN;
            String mode = modeConfig.getMode();
            String filter = modeConfig.getFilter();

            mode = mode.replace("BUS,SUBWAY,TRAM", getTransitModeString());
            final String params = otherParams + "&mode=" + Uri.encode(mode) + "&numItineraries=" + modeConfig.getNumItineraries();

            volleyStringRequest(Request.Method.GET, url + params, "", new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    pbItineraryList.setVisibility(View.GONE);
                    ArrayList<Itinerary> itineraryArrayList = itineraryArrayListOther;
                    int currentItinerarySearchMode = Constants.GROUP_ITINERARY_TYPE_OTHER;
                    ListView listView = lvItineraryListOther;
                    if (filter != null) {
                        if (filter.contains("transit")) {
                            if (filter.contains("drive")) {
                                itineraryArrayList = itineraryArrayListDriveTransit;
                                listView = lvItineraryListDriveTransit;

                                currentItinerarySearchMode = Constants.GROUP_ITINERARY_TYPE_DRIVE_TRANSIT;
                            } else if (filter.contains("walk")) {
                                itineraryArrayList = itineraryArrayListWalkTransit;
                                listView = lvItineraryListWalkTransit;
                                currentItinerarySearchMode = Constants.GROUP_ITINERARY_TYPE_WALK_TRANSIT;
                            } else if (filter.contains("bikeshare")) {
                                itineraryArrayList = itineraryArrayListBikeShareTransit;
                                listView = lvItineraryListBikeShareTransit;
                                currentItinerarySearchMode = Constants.GROUP_ITINERARY_TYPE_BIKESHARE_TRANSIT;
                            } else if (filter.contains("scooter")) {
                                itineraryArrayList = itineraryArrayListScooterTransit;
                                listView = lvItineraryListScooterTransit;
                                currentItinerarySearchMode = Constants.GROUP_ITINERARY_TYPE_SCOOTER_TRANSIT;
                            } else {
                                itineraryArrayList = itineraryArrayListBikeTransit;
                                listView = lvItineraryListBikeTransit;
                                currentItinerarySearchMode = Constants.GROUP_ITINERARY_TYPE_BIKE_TRANSIT;
                            }
                        }
                    }
                    parseRouteSearchData(response, params, itineraryArrayList, currentItinerarySearchMode);

                    Utilities.setTotalHeightToListViewExt(gDm.widthPixels - Convert.dpToPx(20), listView);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    pbItineraryList.setVisibility(View.GONE);
                    Logger.e("Route search error:", error.toString());

                    int itinerariesNum = itineraryArrayListWalkTransit.size() + itineraryArrayListBikeTransit.size() + itineraryArrayListDriveTransit.size() + itineraryArrayListBikeShareTransit.size() + itineraryArrayListScooterTransit.size() +
                            itineraryArrayListOther.size();
                    if (itinerariesNum <= 1) {
                        tvItinerariesFound.setText(systemContext.getString(R.string.itinerary_found_with_num, itinerariesNum));
                    } else {
                        tvItinerariesFound.setText(systemContext.getString(R.string.itineraries_found_with_num, itinerariesNum));
                    }

                    if (noInternetPopupDialog == null && error instanceof NoConnectionError) {
                        noInternetPopupDialog = new CustomizePopupDialog(getActivity(),
                                getString(R.string.no_internet), getString(R.string.no_internet_message),
                                getString(R.string.ok), null, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                noInternetPopupDialog.dismiss();
                                noInternetPopupDialog = null;
                            }
                        }, null, R.drawable.ic_nointernet);
                        noInternetPopupDialog.show();
                    }
                }
            });
        }
    }

    private void parseRouteSearchData
            (com.apollographql.apollo.api.Response<PlanQuery.Data> response, String params, Otp2QueryParams monitoredOtp2QueryParams) {
        monitoredTripUpdateRequest.setOtp2QueryParams(monitoredOtp2QueryParams);

        if (response != null
                && response.getData() != null
                && response.getData().plan() != null
                && response.getData().plan().itineraries() != null) {
            Gson gson = new Gson();
            String json = gson.toJson(response.getData());

            try {
                JSONObject responseObject = new JSONObject(json);
                planTripQueryCount++;
                if (responseObject.has("plan")) {
                    JSONObject planObject = responseObject.getJSONObject("plan");
                    String itineraries = planObject.getString("itineraries");

                    ArrayList<Itinerary> itineraryTempArray = gson.fromJson(itineraries, new TypeToken<ArrayList<Itinerary>>() {
                    }.getType());


                    ArrayList<Itinerary> itineraryArrayList = itineraryArrayListOther;
                    int currentItinerarySearchMode = Constants.GROUP_ITINERARY_TYPE_OTHER;
                    ListView listView = lvItineraryListOther;

                    boolean continueMainForLoop = false;
                    //check route errors returned
                    String routingErrorsString = planObject.getString("routingErrors");

                    ArrayList<RoutingErrors> routingErrors = gson.fromJson(routingErrorsString, new TypeToken<ArrayList<RoutingErrors>>() {
                    }.getType());

                    if (routingErrors != null && routingErrors.size() > 0) {

                        for (int i = 0; i < routingErrors.size(); i++) {

                            String errorDescription = routingErrors.get(i).getDescription();

                            String inputfield = routingErrors.get(i).getInputField();

                            if (errorDescription != null && !errorDescription.isEmpty()) {

                                if (routingErrorsArrayList.size() > 0) {

                                    boolean errorMatched = false;

                                    for (int j = 0; j < routingErrorsArrayList.size(); j++) {

                                        String localErrorDescription = routingErrorsArrayList.get(j).getDescription();


                                        if (localErrorDescription != null && !localErrorDescription.isEmpty() && localErrorDescription.equalsIgnoreCase(errorDescription)) {

                                            String localInputfield = routingErrorsArrayList.get(j).getInputField();

                                            if (localInputfield != null && inputfield != null
                                                    && !localInputfield.isEmpty() && !inputfield.isEmpty()
                                                    && !localInputfield.equalsIgnoreCase(inputfield)) {
                                                errorMatched = true;
                                                routingErrorsArrayList.get(j).setInputField(localInputfield + " " + inputfield);
                                                break;
                                            }

                                            errorMatched = true;
                                            break;
                                        }
                                    }

                                    if (!errorMatched) {
                                        routingErrorsArrayList.add(routingErrors.get(i));
                                    }
                                } else {
                                    routingErrorsArrayList.add(routingErrors.get(i));
                                }
                            }
                        }
                    }

                    //check return modes
                    for (int k = (itineraryTempArray.size() - 1); k >= 0; k--) {
                        if (k >= itineraryTempArray.size()) {
                            break;
                        }
                        continueMainForLoop = false;
                        Itinerary itinerary = itineraryTempArray.get(k);
                        if (itinerary != null) {
                            //add Otp2QueryParams to every itinerary for save and check trip
                            itinerary.setOtp2QueryParams(monitoredOtp2QueryParams);
                            String filter = "";
                            if (itinerary.getLegs() != null && itinerary.getLegs().size() > 0) {
                                for (int j = 0; j < itinerary.getLegs().size(); j++) {
                                    ItineraryLeg itineraryLeg = itinerary.getLegs().get(j);
                                    if (itineraryLeg.getMode() != null) {
                                        String mode = itineraryLeg.getMode();
                                        if (mode.equalsIgnoreCase("bicycle") && itineraryLeg.getFrom().getVertexType().equalsIgnoreCase("bikeshare")) {
                                            filter += itineraryLeg.getFrom().getVertexType() + ",";
                                        } else {
                                            filter += mode + ",";
                                        }

                                    }
                                }
                            }

                            itineraryArrayList = itineraryArrayListOther;
                            currentItinerarySearchMode = Constants.GROUP_ITINERARY_TYPE_OTHER;
                            listView = lvItineraryListOther;
                            filter = filter.toLowerCase(Locale.ROOT);

                            if (filter.contains("bus") || filter.contains("tram") || filter.contains("rail") || filter.contains("subway") || filter.contains("streetcar")
                                    || filter.contains("ferry") || filter.contains("gondola") || filter.contains("monorail") || filter.contains("link")) {
                                if (filter.contains("car")) {
                                    itineraryArrayList = itineraryArrayListDriveTransit;
                                    listView = lvItineraryListDriveTransit;
                                    currentItinerarySearchMode = Constants.GROUP_ITINERARY_TYPE_DRIVE_TRANSIT;
                                } else if (filter.contains("bikeshare")) {
                                    itineraryArrayList = itineraryArrayListBikeShareTransit;
                                    listView = lvItineraryListBikeShareTransit;
                                    currentItinerarySearchMode = Constants.GROUP_ITINERARY_TYPE_BIKESHARE_TRANSIT;
                                } else if (filter.contains("scooter")) {
                                    itineraryArrayList = itineraryArrayListScooterTransit;
                                    listView = lvItineraryListScooterTransit;
                                    currentItinerarySearchMode = Constants.GROUP_ITINERARY_TYPE_SCOOTER_TRANSIT;
                                } else if (filter.contains("bicycle") || filter.contains("bike")) {
                                    itineraryArrayList = itineraryArrayListBikeTransit;
                                    listView = lvItineraryListBikeTransit;
                                    currentItinerarySearchMode = Constants.GROUP_ITINERARY_TYPE_BIKE_TRANSIT;
                                } else {
                                    itineraryArrayList = itineraryArrayListWalkTransit;
                                    listView = lvItineraryListWalkTransit;
                                    currentItinerarySearchMode = Constants.GROUP_ITINERARY_TYPE_WALK_TRANSIT;
                                }
                            }

                            itinerary.setQueryParams(params.substring(1));
                            String itineraryString = gson.toJson(itinerary);

                            if (itineraryString != null && !itineraryString.isEmpty()) {
                                for (Itinerary localItinerary : itineraryArrayList) {
                                    if (itineraryString.equals(gson.toJson(localItinerary))) {
                                        itineraryTempArray.remove(k);
                                        continueMainForLoop = true;
                                        break;
                                    } else {
                                        if (itinerary.getLegs() != null && localItinerary.getLegs() != null
                                                && itinerary.getLegs().size() == localItinerary.getLegs().size()) {

                                            boolean matched = true;
                                            for (int j = 0; j < localItinerary.getLegs().size(); j++) {
                                                ItineraryLeg remoteItineraryLeg = itinerary.getLegs().get(j);
                                                ItineraryLeg localItineraryLeg = localItinerary.getLegs().get(j);

                                                if (remoteItineraryLeg != null && localItineraryLeg != null) {

                                                    if (currentItinerarySearchMode == Constants.GROUP_ITINERARY_TYPE_OTHER && remoteItineraryLeg.getMode() != null && localItineraryLeg.getMode() != null
                                                            && remoteItineraryLeg.getMode().equals(localItineraryLeg.getMode())) {
                                                        matched = true;
                                                        break;
                                                    }

                                                    if (remoteItineraryLeg.getMode() != null && localItineraryLeg.getMode() != null
                                                            && !remoteItineraryLeg.getMode().equals(localItineraryLeg.getMode())) {
                                                        matched = false;
                                                        break;
                                                    }

                                                    if (remoteItineraryLeg.getOriginalRoute() != null && remoteItineraryLeg.getOriginalRoute().getId() != null && localItineraryLeg.getOriginalRoute() != null && localItineraryLeg.getOriginalRoute().getId() != null
                                                            && !remoteItineraryLeg.getOriginalRoute().getId().equalsIgnoreCase(localItineraryLeg.getOriginalRoute().getId())) {
                                                        matched = false;
                                                        break;
                                                    }

                                                    if (!(compareTimeStamps(remoteItineraryLeg.getStartTime(), localItineraryLeg.getStartTime())) || !(compareTimeStamps(localItineraryLeg.getEndTime(), remoteItineraryLeg.getEndTime()))) {
                                                        matched = false;
                                                        break;
                                                    }
                                                }
                                            }

                                            if (matched) {
                                                itineraryTempArray.remove(k);
                                                continueMainForLoop = true;
                                                break;
                                            } else {

                                                if (localItinerary.getMatchedItineraries() != null && localItinerary.getMatchedItineraries().size() > 0) {

                                                    for (Itinerary groupeditinerary : localItinerary.getMatchedItineraries()) {
                                                        if (itineraryString.equals(gson.toJson(groupeditinerary))) {
                                                            itineraryTempArray.remove(k);
                                                            continueMainForLoop = true;
                                                            break;
                                                        } else {

                                                            if (itinerary.getLegs() != null && groupeditinerary.getLegs() != null
                                                                    && itinerary.getLegs().size() == groupeditinerary.getLegs().size()) {

                                                                boolean groupMatched = true;

                                                                for (int m = 0; m < groupeditinerary.getLegs().size(); m++) {
                                                                    ItineraryLeg remoteItineraryLegNext = itinerary.getLegs().get(m);
                                                                    ItineraryLeg groupedItineraryLeg = groupeditinerary.getLegs().get(m);

                                                                    if (remoteItineraryLegNext != null && groupedItineraryLeg != null) {
                                                                        if (remoteItineraryLegNext.getMode() != null && groupedItineraryLeg.getMode() != null
                                                                                && !remoteItineraryLegNext.getMode().equals(groupedItineraryLeg.getMode())) {
                                                                            groupMatched = false;
                                                                            break;
                                                                        }

                                                                        if (remoteItineraryLegNext.getOriginalRoute() != null && remoteItineraryLegNext.getOriginalRoute().getId() != null && groupedItineraryLeg.getOriginalRoute() != null && groupedItineraryLeg.getOriginalRoute().getId() != null
                                                                                && !remoteItineraryLegNext.getOriginalRoute().getId().equalsIgnoreCase(groupedItineraryLeg.getOriginalRoute().getId())) {
                                                                            groupMatched = false;
                                                                            break;
                                                                        }

                                                                        if (!(compareTimeStamps(remoteItineraryLegNext.getStartTime(), groupedItineraryLeg.getStartTime())) || !(compareTimeStamps(groupedItineraryLeg.getEndTime(), remoteItineraryLegNext.getEndTime()))) {
                                                                            groupMatched = false;
                                                                            break;
                                                                        }
                                                                    }
                                                                }

                                                                if (groupMatched) {
                                                                    itineraryTempArray.remove(k);
                                                                    continueMainForLoop = true;
                                                                    break;
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }

                                    }
                                }
                            } else {
                                itineraryTempArray.remove(k);
                                continue;
                            }
                        } else {
                            itineraryTempArray.remove(k);
                            continue;
                        }

                        if (continueMainForLoop) {
                            continue;
                        }

                        // grouping same itineraries
                        if (currentItinerarySearchMode == Constants.GROUP_ITINERARY_TYPE_WALK_TRANSIT
                                || currentItinerarySearchMode == Constants.GROUP_ITINERARY_TYPE_DRIVE_TRANSIT
                                || currentItinerarySearchMode == Constants.GROUP_ITINERARY_TYPE_BIKE_TRANSIT
                                || currentItinerarySearchMode == Constants.GROUP_ITINERARY_TYPE_BIKESHARE_TRANSIT
                                || currentItinerarySearchMode == Constants.GROUP_ITINERARY_TYPE_SCOOTER_TRANSIT) {
                            itineraryArrayList.add(itinerary);
                            Collections.sort(itineraryArrayList, new Comparator<Itinerary>() {
                                @Override
                                public int compare(Itinerary o1, Itinerary o2) {
                                    if (o1 == null || o2 == null) {
                                        return 0;
                                    } else {
                                        return Long.compare(o1.getStartTime(), o2.getStartTime());
                                    }
                                }
                            });

                            for (int i = 0; i < itineraryArrayList.size(); i++) {
                                Itinerary itineraryTemp = itineraryArrayList.get(i);
                                for (int j = i + 1; j < itineraryArrayList.size(); j++) {
                                    Itinerary otherItinerary = itineraryArrayList.get(j);
                                    if (itinerariesAreEqual(itineraryTemp, otherItinerary)) {
                                        if (!itineraryTemp.getMatchedItineraries().contains(otherItinerary)) {
                                            itineraryTemp.getMatchedItineraries().add(otherItinerary);
                                            if (otherItinerary.getMatchedItineraries().size() > 0) {
                                                for (int l = 0; l < otherItinerary.getMatchedItineraries().size(); l++) {

                                                    itineraryTemp.getMatchedItineraries().add(otherItinerary.getMatchedItineraries().get(l));

                                                }
                                            }
                                            itineraryTemp.setMatchedToOtheritinerary(true);
                                            otherItinerary.setMatchedToOtheritinerary(true);
                                            itineraryArrayList.remove(j);
                                            j--;
                                        }
                                    }

                                }
                            }

                        } else {
                            itineraryArrayList.add(itinerary);
                        }

                        Collections.sort(itineraryArrayList, new Comparator<Itinerary>() {
                            @Override
                            public int compare(Itinerary o1, Itinerary o2) {
                                return compareItineraries(o1, o2);
                            }
                        });


                        if (currentItinerarySearchMode == Constants.GROUP_ITINERARY_TYPE_WALK_TRANSIT && itineraryListAdapterWalkTransit != null && itineraryArrayListWalkTransit.size() > 0) {
                            itineraryListAdapterWalkTransit.notifyDataSetChanged();
                        }

                        if (currentItinerarySearchMode == Constants.GROUP_ITINERARY_TYPE_BIKE_TRANSIT && itineraryListAdapterBikeTransit != null && itineraryArrayListBikeTransit.size() > 0) {
                            itineraryListAdapterBikeTransit.notifyDataSetChanged();
                        }

                        if (currentItinerarySearchMode == Constants.GROUP_ITINERARY_TYPE_BIKESHARE_TRANSIT && itineraryListAdapterBikeShareTransit != null && itineraryArrayListBikeShareTransit.size() > 0) {
                            itineraryListAdapterBikeShareTransit.notifyDataSetChanged();
                        }

                        if (currentItinerarySearchMode == Constants.GROUP_ITINERARY_TYPE_SCOOTER_TRANSIT && itineraryListAdapterScooterTransit != null && itineraryArrayListScooterTransit.size() > 0) {
                            itineraryListAdapterScooterTransit.notifyDataSetChanged();
                        }

                        if (currentItinerarySearchMode == Constants.GROUP_ITINERARY_TYPE_DRIVE_TRANSIT && itineraryListAdapterDriveTransit != null && itineraryArrayListDriveTransit.size() > 0) {
                            itineraryListAdapterDriveTransit.notifyDataSetChanged();
                        }

                        if (currentItinerarySearchMode == Constants.GROUP_ITINERARY_TYPE_OTHER && itineraryListOtherAdapter != null && itineraryArrayListOther.size() > 0) {
                            //filter the same mode
                            filterOutSameModeForOther();
                            itineraryListOtherAdapter.notifyDataSetChanged();
                        }

                        sortItineraryListView();

                        Utilities.setTotalHeightToListViewExt(gDm.widthPixels - Convert.dpToPx(20), listView);
                    }


                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Log.d("routeSearch", "data is not correct:" + response);
        }

        int itinerariesNum = itineraryArrayListWalkTransit.size() + itineraryArrayListBikeTransit.size() + itineraryArrayListDriveTransit.size() + itineraryArrayListBikeShareTransit.size()
                + itineraryArrayListScooterTransit.size() + itineraryArrayListOther.size();
        if (itinerariesNum <= 1) {
            tvItinerariesFound.setText(systemContext.getString(R.string.itinerary_found_with_num, itinerariesNum));
        } else {
            tvItinerariesFound.setText(systemContext.getString(R.string.itineraries_found_with_num, itinerariesNum));
        }

        if (llPlanTripErrors != null && llPlanTripErrors.getChildCount() > 0) {
            llPlanTripErrors.removeAllViews();
        }

        if (routingErrorsArrayList != null && configRouteErrors != null) {

            for (int i = 0; i < routingErrorsArrayList.size(); i++) {
                RoutingErrors routingError = routingErrorsArrayList.get(i);
                for (int j = 0; j < configRouteErrors.size(); j++) {
                    TripError cfgTripError = configRouteErrors.get(j);
                    if (routingError.getCode() != null && cfgTripError.getErrorCode() != null && !routingError.getCode().isEmpty() && !cfgTripError.getErrorCode().isEmpty() &&
                            routingError.getCode().equalsIgnoreCase(cfgTripError.getErrorCode())) {

                        String errorTitleMesage = "";
                        String errorDescrptionMessage = "";

                        String fromText = "";
                        String toText = "";
                        String locationtext = "";

                        if (cfgTripError.getDisplaySubText().contains("$0 $1")) {

                            if (routingError.getInputField().contains("FROM")) {
                                fromText = "Origin";
                            }

                            if (routingError.getInputField().contains("TO")) {
                                toText = "Destination";
                            }

                            if (!fromText.isEmpty() && !toText.isEmpty()) {
                                locationtext = "locations are";
                            } else if (!fromText.isEmpty() || !toText.isEmpty()) {
                                locationtext = "location is";
                            }

                            if (!fromText.isEmpty()) {
                                errorDescrptionMessage = cfgTripError.getDisplaySubText().replace("$0 $1", fromText + " " + locationtext);
                            }
                            if (!toText.isEmpty()) {
                                errorDescrptionMessage = cfgTripError.getDisplaySubText().replace("$0 $1", toText + " " + locationtext);
                            }
                            if (!fromText.isEmpty() && !toText.isEmpty()) {
                                errorDescrptionMessage = cfgTripError.getDisplaySubText().replace("$0 $1", fromText + " and " + toText + " " + locationtext);
                            }

                            errorTitleMesage = cfgTripError.getDisplayText();
                        } else {
                            errorDescrptionMessage = cfgTripError.getDisplaySubText();
                            errorTitleMesage = cfgTripError.getDisplayText();
                        }

                        if (errorTitleMesage.equalsIgnoreCase("Transit isn't the fastest way to make this trip")) {
                            errorTitleMesage = "";
                            routingErrorsArrayList.remove(i);
                            i--;
                        }

                        if (!errorDescrptionMessage.isEmpty() && !errorTitleMesage.isEmpty()) {
                            View view = getLayoutInflater().inflate(R.layout.item_plan_trip_error, null, false);
                            TextView tvPlanTripError = view.findViewById(R.id.tv_plan_trip_error);
                            TextView tvTitlePlanTripError = view.findViewById(R.id.tv_title_plan_trip_error);
                            tvPlanTripError.setText(errorDescrptionMessage);
                            tvTitlePlanTripError.setText(errorTitleMesage);
                            if (llPlanTripErrors != null) {
                                llPlanTripErrors.addView(view);
                            }
                        }
                    }
                }
            }

            llPlanTripErrors.setVisibility(View.GONE);

        } else {
            llPlanTripErrors.setVisibility(View.GONE);
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isAdded() && getActivity() != null) {
                    int itinerariesNum = itineraryArrayListWalkTransit.size() + itineraryArrayListBikeTransit.size() + itineraryArrayListDriveTransit.size() + itineraryArrayListBikeShareTransit.size()
                            + itineraryArrayListScooterTransit.size() + itineraryArrayListOther.size();
                    View errorView = getLayoutInflater().inflate(R.layout.item_plan_trip_error, null, false);
                    TextView tvPlanTripError = errorView.findViewById(R.id.tv_plan_trip_error);

                    if (itinerariesNum == 0 && planTripQueryCount > 0 && tvPlanTripError != null
                            && tvPlanTripError.getVisibility() != View.VISIBLE) {
                        if (llPlanTripErrors != null && llPlanTripErrors.getChildCount() > 0) {
                            llPlanTripErrors.removeAllViews();
                        }
                        TextView tvTitlePlanTripError = errorView.findViewById(R.id.tv_title_plan_trip_error);
                        tvPlanTripError.setText(getString(R.string.mode_combination_error));
                        if (llPlanTripErrors != null) {
                            llPlanTripErrors.addView(errorView);
                            llPlanTripErrors.setVisibility(View.VISIBLE);
                        }
                        pbItineraryList.setVisibility(View.GONE);
                    } else if (tvPlanTripError != null
                            && tvPlanTripError.getVisibility() == View.VISIBLE && itinerariesNum == 0) {
                        if (llPlanTripErrors != null && llPlanTripErrors.getChildCount() > 0) {
                            llPlanTripErrors.removeAllViews();
                        }
                        tvPlanTripError.setText(getString(R.string.mode_combination_error));
                        llPlanTripErrors.addView(errorView);
                        llPlanTripErrors.setVisibility(View.VISIBLE);
                    } else {
                        if (llPlanTripErrors != null) {
                            llPlanTripErrors.setVisibility(View.GONE);
                        }
                    }
                }
            }
        }, 1);

        if (itineraryArrayListWalkTransit.size() > 0) {
            tvWalkTransitTitle.setVisibility(View.VISIBLE);
        } else {
            tvWalkTransitTitle.setVisibility(View.GONE);
        }
        if (itineraryArrayListBikeTransit.size() > 0) {
            tvBikeTransitTitle.setVisibility(View.VISIBLE);
        } else {
            tvBikeTransitTitle.setVisibility(View.GONE);
        }
        if (itineraryArrayListBikeShareTransit.size() > 0) {
            tvBikeShareTransitTitle.setVisibility(View.VISIBLE);
        } else {
            tvBikeShareTransitTitle.setVisibility(View.GONE);
        }

        if (itineraryArrayListScooterTransit.size() > 0) {
            tvScooterTransitTitle.setVisibility(View.VISIBLE);
        } else {
            tvScooterTransitTitle.setVisibility(View.GONE);
        }

        if (itineraryArrayListDriveTransit.size() > 0) {
            tvDriveTransitTitle.setVisibility(View.VISIBLE);
        } else {
            tvDriveTransitTitle.setVisibility(View.GONE);
        }

        if (itineraryArrayListOther.size() > 0) {
            tvOtherTitle.setVisibility(View.VISIBLE);
        } else {
            tvOtherTitle.setVisibility(View.GONE);
        }

    }

    private void filterOutSameModeForOther() {
        for (int i = itineraryArrayListOther.size() - 1; i >= 0; i--) {
            Itinerary itinerary = itineraryArrayListOther.get(i);
            if (itinerary != null && itinerary.getLegs() != null && itinerary.getLegs().size() == 1
                    && itinerary.getLegs().get(0) != null && itinerary.getLegs().get(0).getMode() != null) {
                for (int j = 0; j < i; j++) {
                    Itinerary compareItinerary = itineraryArrayListOther.get(j);

                    if (compareItinerary != null && compareItinerary.getLegs() != null && compareItinerary.getLegs().size() == 1
                            && compareItinerary.getLegs().get(0) != null) {
                        if (itinerary.getLegs().get(0).getMode().equals(compareItinerary.getLegs().get(0).getMode())) {
                            itineraryArrayListOther.remove(i);
                            break;
                        }
                    }
                }
            }
        }
    }

    private boolean compareTimeStamps(long t1, long t2) {
        if (t1 == t2) {
            return true;
        }

        return (t1 / (60 * 1000)) == (t2 / (60 * 1000));
    }

    private void parseRouteSearchData(String response, String params, ArrayList<Itinerary> itineraryArrayList,
                                      int currentItinerarySearchMode) {
        if (response != null && !response.isEmpty()) {
            Gson gson = new Gson();

            try {
                JSONObject responseObject = new JSONObject(response);

                if (responseObject.has("plan")) {
                    JSONObject planObject = responseObject.getJSONObject("plan");
                    String itineraries = planObject.getString("itineraries");

                    ArrayList<Itinerary> itineraryTempArray = gson.fromJson(itineraries, new TypeToken<ArrayList<Itinerary>>() {
                    }.getType());


                    for (int i = (itineraryTempArray.size() - 1); i >= 0; i--) {
                        Itinerary itinerary = itineraryTempArray.get(i);

                        if (itinerary != null) {
                            itinerary.setQueryParams(params.substring(1));
                            String itineraryString = gson.toJson(itinerary);
                            if (itineraryString != null && !itineraryString.isEmpty()) {
                                for (Itinerary localItinerary : itineraryArrayList) {
                                    if (itineraryString.equals(gson.toJson(localItinerary))) {
                                        itineraryTempArray.remove(i);
                                        break;
                                    } else {
                                        if (itinerary.getLegs() != null && localItinerary.getLegs() != null
                                                && itinerary.getLegs().size() == localItinerary.getLegs().size()) {
                                            boolean matched = true;
                                            for (int j = 0; j < localItinerary.getLegs().size(); j++) {
                                                ItineraryLeg remoteItineraryLeg = itinerary.getLegs().get(j);
                                                ItineraryLeg localItineraryLeg = localItinerary.getLegs().get(j);

                                                if (remoteItineraryLeg != null && localItineraryLeg != null) {
                                                    if (remoteItineraryLeg.getMode() != null && localItineraryLeg.getMode() != null
                                                            && !remoteItineraryLeg.getMode().equals(localItineraryLeg.getMode())) {
                                                        matched = false;
                                                        break;
                                                    }

                                                    if (remoteItineraryLeg.getRoute() != null && localItineraryLeg.getRoute() != null
                                                            && !remoteItineraryLeg.getRoute().equals(localItineraryLeg.getRoute())) {
                                                        matched = false;
                                                        break;
                                                    }
                                                }
                                            }

                                            if (matched) {
                                                itineraryTempArray.remove(i);
                                                break;
                                            }
                                        }
                                    }
                                }
                            } else {
                                itineraryTempArray.remove(i);
                            }
                        } else {
                            itineraryTempArray.remove(i);
                        }
                    }

                    //remove non-transit itineraries.
                    if (currentItinerarySearchMode == Constants.GROUP_ITINERARY_TYPE_WALK_TRANSIT
                            || currentItinerarySearchMode == Constants.GROUP_ITINERARY_TYPE_DRIVE_TRANSIT
                            || currentItinerarySearchMode == Constants.GROUP_ITINERARY_TYPE_BIKE_TRANSIT) {
                        for (int i = (itineraryTempArray.size() - 1); i >= 0; i--) {
                            Itinerary itinerary = itineraryTempArray.get(i);
                            if (itinerary.getLegs() == null || itinerary.getLegs().size() == 0) {
                                itineraryTempArray.remove(i);
                            } else {
                                boolean isExistTransitLeg = false;
                                for (int j = 0; j < itinerary.getLegs().size(); j++) {
                                    ItineraryLeg itineraryLeg = itinerary.getLegs().get(j);
                                    if (itineraryLeg.getMode() != null) {
                                        String mode = itineraryLeg.getMode().toLowerCase(Locale.ROOT);
                                        if (!mode.equals("walk") && !mode.equals("bike") && !mode.equals("bicycle")
                                                && !mode.equals("car") && !mode.equals("micromobility")) {
                                            isExistTransitLeg = true;
                                            break;
                                        }
                                    }
                                }

                                if (!isExistTransitLeg) {
                                    itineraryTempArray.remove(i);
                                }
                            }
                        }
                    }

                    //remove non-bike itineraries
                    if (currentItinerarySearchMode == Constants.GROUP_ITINERARY_TYPE_BIKE_TRANSIT) {
                        for (int i = (itineraryTempArray.size() - 1); i >= 0; i--) {
                            Itinerary itinerary = itineraryTempArray.get(i);
                            if (itinerary.getLegs() == null || itinerary.getLegs().size() == 0) {
                                itineraryTempArray.remove(i);
                            } else {
                                boolean isExistBikeLeg = false;
                                for (int j = 0; j < itinerary.getLegs().size(); j++) {
                                    ItineraryLeg itineraryLeg = itinerary.getLegs().get(j);
                                    if (itineraryLeg.getMode() != null) {
                                        String mode = itineraryLeg.getMode().toLowerCase(Locale.ROOT);
                                        if (mode.equals("bike") || mode.equals("bicycle")) {
                                            isExistBikeLeg = true;
                                            break;
                                        }
                                    }
                                }

                                if (!isExistBikeLeg) {
                                    itineraryTempArray.remove(i);
                                }
                            }
                        }
                    }

                    // grouping same itineraries
                    if (currentItinerarySearchMode == Constants.GROUP_ITINERARY_TYPE_WALK_TRANSIT
                            || currentItinerarySearchMode == Constants.GROUP_ITINERARY_TYPE_DRIVE_TRANSIT
                            || currentItinerarySearchMode == Constants.GROUP_ITINERARY_TYPE_BIKE_TRANSIT) {


                        Collections.sort(itineraryTempArray, new Comparator<Itinerary>() {
                            @Override
                            public int compare(Itinerary o1, Itinerary o2) {
                                return compareItineraries(o1, o2);
                            }
                        });


                        for (int i = 0; i < itineraryTempArray.size(); i++) {

                            Itinerary itinerary = itineraryTempArray.get(i);


                            for (int j = i + 1; j < itineraryTempArray.size(); j++) {

                                Itinerary otherItinerary = itineraryTempArray.get(j);

                                if (!otherItinerary.isMatchedToOtheritinerary()) {

                                    if (itinerariesAreEqual(itinerary, otherItinerary)) {


                                        if (!itinerary.getMatchedItineraries().contains(otherItinerary)) {

                                            itinerary.getMatchedItineraries().add(otherItinerary);
                                            itineraryTempArray.remove(j);
                                            itinerary.setMatchedToOtheritinerary(true);
                                            otherItinerary.setMatchedToOtheritinerary(true);

                                        }


                                    }

                                }

                            }

                        }


                    }

                    itineraryArrayList.addAll(itineraryTempArray);

                    Collections.sort(itineraryArrayList, new Comparator<Itinerary>() {
                        @Override
                        public int compare(Itinerary o1, Itinerary o2) {
                            return compareItineraries(o1, o2);
                        }
                    });

                    if (currentItinerarySearchMode == Constants.GROUP_ITINERARY_TYPE_WALK_TRANSIT && itineraryListAdapterWalkTransit != null) {
                        itineraryListAdapterWalkTransit.notifyDataSetChanged();
                    }

                    if (currentItinerarySearchMode == Constants.GROUP_ITINERARY_TYPE_BIKE_TRANSIT && itineraryListAdapterBikeTransit != null) {
                        itineraryListAdapterBikeTransit.notifyDataSetChanged();
                    }

                    if (currentItinerarySearchMode == Constants.GROUP_ITINERARY_TYPE_BIKESHARE_TRANSIT && itineraryListAdapterBikeShareTransit != null) {
                        itineraryListAdapterBikeShareTransit.notifyDataSetChanged();
                    }

                    if (currentItinerarySearchMode == Constants.GROUP_ITINERARY_TYPE_SCOOTER_TRANSIT && itineraryListAdapterScooterTransit != null) {
                        itineraryListAdapterScooterTransit.notifyDataSetChanged();
                    }

                    if (currentItinerarySearchMode == Constants.GROUP_ITINERARY_TYPE_DRIVE_TRANSIT && itineraryListAdapterDriveTransit != null) {
                        itineraryListAdapterDriveTransit.notifyDataSetChanged();
                    }

                    if (currentItinerarySearchMode == Constants.GROUP_ITINERARY_TYPE_OTHER && itineraryListOtherAdapter != null) {
                        itineraryListOtherAdapter.notifyDataSetChanged();
                    }

                    sortItineraryListView();
                    //showRoutePolylineOnMap();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Log.d("routeSearch", "data is not correct:" + response);
        }

        int itinerariesNum = itineraryArrayListWalkTransit.size() + itineraryArrayListBikeTransit.size() + itineraryArrayListDriveTransit.size() + itineraryArrayListBikeShareTransit.size() +
                itineraryArrayListScooterTransit.size() + itineraryArrayListOther.size();
        if (itinerariesNum <= 1) {
            tvItinerariesFound.setText(systemContext.getString(R.string.itinerary_found_with_num, itinerariesNum));
        } else {
            tvItinerariesFound.setText(systemContext.getString(R.string.itineraries_found_with_num, itinerariesNum));
        }

        if (itineraryArrayListWalkTransit.size() > 0) {
            tvWalkTransitTitle.setVisibility(View.VISIBLE);
        } else {
            tvWalkTransitTitle.setVisibility(View.GONE);
        }
        if (itineraryArrayListBikeTransit.size() > 0) {
            tvBikeTransitTitle.setVisibility(View.VISIBLE);
        } else {
            tvBikeTransitTitle.setVisibility(View.GONE);
        }
        if (itineraryArrayListBikeShareTransit.size() > 0) {
            tvBikeShareTransitTitle.setVisibility(View.VISIBLE);
        } else {
            tvBikeShareTransitTitle.setVisibility(View.GONE);
        }

        if (itineraryArrayListScooterTransit.size() > 0) {
            tvScooterTransitTitle.setVisibility(View.VISIBLE);
        } else {
            tvScooterTransitTitle.setVisibility(View.GONE);
        }

        if (itineraryArrayListDriveTransit.size() > 0) {
            tvDriveTransitTitle.setVisibility(View.VISIBLE);
        } else {
            tvDriveTransitTitle.setVisibility(View.GONE);
        }

        if (itineraryArrayListOther.size() > 0) {
            tvOtherTitle.setVisibility(View.VISIBLE);
        } else {
            tvOtherTitle.setVisibility(View.GONE);
        }

    }

    private void sortItineraryList() {
        if (itineraryComparator == null) {
            itineraryComparator = new Comparator<Itinerary>() {
                @Override
                public int compare(Itinerary o1, Itinerary o2) {
                    return compareItineraries(o1, o2);
                }
            };
        }
        Collections.sort(itineraryArrayListWalkTransit, itineraryComparator);
        Collections.sort(itineraryArrayListBikeTransit, itineraryComparator);
        Collections.sort(itineraryArrayListBikeShareTransit, itineraryComparator);
        Collections.sort(itineraryArrayListScooterTransit, itineraryComparator);
        Collections.sort(itineraryArrayListDriveTransit, itineraryComparator);
        Collections.sort(itineraryArrayListOther, itineraryComparator);
    }

    private void sortItineraryListView() {
        if (itineraryComparator == null) {
            itineraryComparator = new Comparator<Itinerary>() {
                @Override
                public int compare(Itinerary o1, Itinerary o2) {
                    return compareItineraries(o1, o2);
                }
            };
        }

        ArrayList<Itinerary> tempList = new ArrayList<>();


        if (itineraryArrayListBikeTransit != null && !itineraryArrayListBikeTransit.isEmpty()) {
            tempList.add(itineraryArrayListBikeTransit.get(0).setListViewType("bike"));
        }

        if (itineraryArrayListBikeShareTransit != null && !itineraryArrayListBikeShareTransit.isEmpty()) {
            tempList.add(itineraryArrayListBikeShareTransit.get(0).setListViewType("bikeshare"));
        }

        if (itineraryArrayListScooterTransit != null && !itineraryArrayListScooterTransit.isEmpty()) {
            tempList.add(itineraryArrayListScooterTransit.get(0).setListViewType("scooter"));
        }

        if (itineraryArrayListDriveTransit != null && !itineraryArrayListDriveTransit.isEmpty()) {
            tempList.add(itineraryArrayListDriveTransit.get(0).setListViewType("drive"));
        }

        if (itineraryArrayListWalkTransit != null && !itineraryArrayListWalkTransit.isEmpty()) {
            tempList.add(itineraryArrayListWalkTransit.get(0).setListViewType("walk"));
        }

        //sorting logic goes here
        Collections.sort(tempList, itineraryComparator);

        for (Itinerary it : tempList) {
            if (it.getListViewType().equalsIgnoreCase("walk")) {
                ll_lists_holder.removeView(transitWalkListView);
                ll_lists_holder.addView(transitWalkListView);
                lvItineraryListWalkTransit.setAdapter(itineraryListAdapterWalkTransit);
            } else if (it.getListViewType().equalsIgnoreCase("bike")) {
                ll_lists_holder.removeView(transitBikeListView);
                ll_lists_holder.addView(transitBikeListView);
                lvItineraryListBikeTransit.setAdapter(itineraryListAdapterBikeTransit);
            } else if (it.getListViewType().equalsIgnoreCase("bikeshare")) {
                ll_lists_holder.removeView(transitBikeshareListView);
                ll_lists_holder.addView(transitBikeshareListView);
                lvItineraryListBikeShareTransit.setAdapter(itineraryListAdapterBikeShareTransit);
            } else if (it.getListViewType().equalsIgnoreCase("scooter")) {
                ll_lists_holder.removeView(transitScooterListView);
                ll_lists_holder.addView(transitScooterListView);
                lvItineraryListScooterTransit.setAdapter(itineraryListAdapterScooterTransit);
            } else {
                ll_lists_holder.removeView(transitDriveListView);
                ll_lists_holder.addView(transitDriveListView);
                lvItineraryListDriveTransit.setAdapter(itineraryListAdapterDriveTransit);
            }
        }
    }


    private boolean itinerariesAreEqual(Itinerary itinerary, Itinerary other) {

        if (itinerary.getLegs().size() != other.getLegs().size()) {
            return false;
        }

        for (int i = 0; i < itinerary.getLegs().size(); i++) {

            ItineraryLeg leg = itinerary.getLegs().get(i);
            ItineraryLeg otherLeg = other.getLegs().get(i);

            if (!Objects.equals(leg.getMode(), otherLeg.getMode())) {
                return false;
            }

            if (!legLocationsAreEqual(otherLeg.getTo(), leg.getTo()) || !legLocationsAreEqual(otherLeg.getFrom(), leg.getFrom())) {

                return false;
            }

            if (leg.getRouteShortName() != null && otherLeg.getRouteShortName() != null && !(leg.getRouteShortName().equalsIgnoreCase(otherLeg.getRouteShortName()))) {
                return false;
            }


        }

        return true;


    }


    private boolean legLocationsAreEqual(ItineraryLegPoint legLocation, ItineraryLegPoint other) {
        return (
                legLocation != null &&
                        other != null &&
                        legLocation.getLat() == other.getLat() &&
                        legLocation.getLon() == other.getLon()
        );
    }

    /**
     * Array sort function for itineraries (in batch routing context) that attempts
     * to sort based on the type/direction specified.
     */
    private int compareItineraries(Itinerary itinerary1, Itinerary itinerary2) {
        if (itinerary1 == null || itinerary2 == null) {
            return 0;
        }
        int direction = Configurations.getInt("itinerary_sort_direction",
                Constants.ITINERARY_SORT_DIRECTION_SHORT_TO_LONG);

        int type = Configurations.getInt("itinerary_sort_index", 0);
        switch (type) {
            case Constants.ITINERARY_SORT_BY_DURATION:
                if (direction == Constants.ITINERARY_SORT_DIRECTION_SHORT_TO_LONG) {
                    return itinerary1.getDuration() - itinerary2.getDuration();
                } else {
                    return itinerary2.getDuration() - itinerary1.getDuration();
                }
            case Constants.ITINERARY_SORT_BY_ARRIVE_TIME:
                if (direction == Constants.ITINERARY_SORT_DIRECTION_SHORT_TO_LONG) {
                    return Long.compare(itinerary1.getEndTime(), itinerary2.getEndTime());
                } else {
                    return Long.compare(itinerary2.getEndTime(), itinerary1.getEndTime());
                }
            case Constants.ITINERARY_SORT_BY_DEPARTURE_TIME:
                if (direction == Constants.ITINERARY_SORT_DIRECTION_SHORT_TO_LONG) {
                    return Long.compare(itinerary1.getStartTime(), itinerary2.getStartTime());
                } else {
                    return Long.compare(itinerary2.getStartTime(), itinerary1.getStartTime());
                }
            case Constants.ITINERARY_SORT_BY_WALK_TIME:
                if (direction == Constants.ITINERARY_SORT_DIRECTION_SHORT_TO_LONG) {
                    return Long.compare(itinerary1.getWaitingTime(), itinerary2.getWaitingTime());
                } else {
                    return Long.compare(itinerary2.getWaitingTime(), itinerary1.getWaitingTime());
                }
            case Constants.ITINERARY_SORT_BY_COST:
                if (direction == Constants.ITINERARY_SORT_DIRECTION_SHORT_TO_LONG) {
                    return Double.compare(itinerary1.getCost(), itinerary2.getCost());
                } else {
                    return Double.compare(itinerary2.getCost(), itinerary1.getCost());
                }
                //default is "Best option"
            default:
                if (direction == Constants.ITINERARY_SORT_DIRECTION_SHORT_TO_LONG) {
                    return Double.compare(calculateItineraryWeight(itinerary1), calculateItineraryWeight(itinerary2));
                } else {
                    return Double.compare(calculateItineraryWeight(itinerary2), calculateItineraryWeight(itinerary1));
                }
        }
    }

    /**
     * This calculates the "cost" (not the monetary cost, but the cost according to
     * multiple factors like duration, total fare, and walking distance) for a
     * particular itinerary, for use in sorting itineraries.
     */
    private double calculateItineraryWeight(Itinerary itinerary) {
        if (itinerary != null) {
            return calculateItineraryCost(itinerary) * Constants.BEST_OPTION_FARE_FACTOR_WEIGHT +
                    (double) itinerary.getDuration() * Constants.BEST_OPTION_DURATION_FACTOR_WEIGHT +
                    itinerary.getWalkDistance() * Constants.BEST_OPTION_WALK_RELUCTANCE_WEIGHT +
                    getDriveTime(itinerary) * Constants.BEST_OPTION_DRIVE_RELUCTANCE_WEIGHT +
                    (double) itinerary.getWaitingTime() * Constants.BEST_OPTION_WAIT_RELUCTANCE_WEIGHT +
                    (double) itinerary.getTransfers() * Constants.BEST_OPTION_TRANSFER_RELUCTANCE_WEIGHT;
        } else {
            return 0.0;
        }
    }

    private double getDriveTime(Itinerary itinerary) {
        double driveTime = 0.0;
        if (itinerary != null) {
            if (itinerary.getLegs() != null) {
                for (ItineraryLeg itineraryLeg : itinerary.getLegs()) {
                    if (itineraryLeg != null) {
                        if (itineraryLeg.getMode() != null) {
                            String mode = itineraryLeg.getMode();

                            if (mode.equalsIgnoreCase("CAR")) {
                                driveTime += itineraryLeg.getDuration();
                            }
                        }
                    }
                }
            }
        }
        return driveTime;
    }

    private boolean validatedFields() {
        String origin = atSearchOrigin.getText().toString();
        String destination = atSearchDestination.getText().toString();
        if (origin.isEmpty() || destination.isEmpty()) {
            StringBuilder sbEmptyFields = new StringBuilder();

            if (origin.isEmpty()) {
                sbEmptyFields.append(getString(R.string.origin));
            }

            if (destination.isEmpty()) {
                if (sbEmptyFields.length() > 0) {
                    sbEmptyFields.append(getString(R.string.and));
                }
                sbEmptyFields.append(getString(R.string.destination_lower_case));
            }

            Toast.makeText(systemContext, getString(R.string.search_empty_field_warning,
                    sbEmptyFields.toString()), Toast.LENGTH_LONG).show();
            return false;
        } else {
            if (origin.equalsIgnoreCase(destination)) {
                Toast.makeText(systemContext, getString(R.string.same_origin_destination_error),
                        Toast.LENGTH_LONG).show();
                return false;
            }
            return true;
        }
    }

    public class SaveTripSpinnerAdapter extends ArrayAdapter<String> {
        private final String[] options;
        private final LayoutInflater l_Inflater;
        private int currentSelection;

        public SaveTripSpinnerAdapter(@NonNull Context context, int resource, int textViewResourceId, @NonNull String[] objects) {
            super(context, resource, textViewResourceId, objects);
            this.options = objects;
            l_Inflater = LayoutInflater.from(context);
        }

        public void setCurrentSelection(int position) {
            this.currentSelection = position;
        }

        @Override
        public View getDropDownView(int position, View cnvtView, ViewGroup prnt) {
            return initView(position, cnvtView, prnt);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            return initView(position, convertView, parent);
        }

        private View initView(int position, View convertView,
                              ViewGroup parent) {

            if (convertView == null) {
                convertView = l_Inflater.inflate(
                        R.layout.item_spinner, null);
            }
            LinearLayout llSpinner = convertView.findViewById(R.id.ll_spinner);
            ImageView ivChecked = convertView.findViewById(R.id.iv_checked);
            TextView tvOption = convertView.findViewById(R.id.tv_option);

            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            llSpinner.setLayoutParams(layoutParams);
            llSpinner.setGravity(Gravity.CENTER);

            if (position == currentSelection) {
                ivChecked.setVisibility(View.VISIBLE);
            } else {
                ivChecked.setVisibility(View.INVISIBLE);
            }

            if (options != null && options.length > position) {
                tvOption.setText(options[position]);
            }
            return convertView;
        }
    }

    public class SpinnerAdapter extends ArrayAdapter<String> {
        private final String[] options;
        private final LayoutInflater l_Inflater;
        private int currentSelection;

        public SpinnerAdapter(@NonNull Context context, int resource, int textViewResourceId, @NonNull String[] objects) {
            super(context, resource, textViewResourceId, objects);
            this.options = objects;
            l_Inflater = LayoutInflater.from(context);
        }

        public void setCurrentSelection(int position) {
            this.currentSelection = position;
        }

        @Override
        public View getDropDownView(int position, View cnvtView, ViewGroup prnt) {
            return initView(position, cnvtView, prnt, false);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            return initView(position, convertView, parent, true);
        }

        private View initView(int position, View convertView,
                              ViewGroup parent, boolean hideCheckedImage) {

            if (convertView == null) {
                convertView = l_Inflater.inflate(
                        R.layout.item_spinner, null);
            }
            ImageView ivChecked = convertView.findViewById(R.id.iv_checked);
            TextView tvOption = convertView.findViewById(R.id.tv_option);

            if (position == currentSelection && !hideCheckedImage) {
                ivChecked.setVisibility(View.VISIBLE);
            } else {
                ivChecked.setVisibility(View.INVISIBLE);
            }

            if (options != null && options.length > position) {
                tvOption.setText(options[position]);
            }
            return convertView;
        }
    }

    private void initMaxWalkSpinner() {
        String[] stringArrays = systemContext.getResources().getStringArray(R.array.maximum_walk_arrays);
        maxWalkSelectIndex = Configurations.getInt("maximum_walk_index", Constants.DEFAULT_MAXIMUM_WALK_INDEX);
        if (maxWalkSelectIndex >= stringArrays.length) {
            maxWalkSelectIndex = 0;
        }

        maxWalkSpinnerAdapter = new SpinnerAdapter(systemContext, R.layout.item_spinner,
                R.id.tv_option, stringArrays);
        maxWalkSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_list_item_checked);

        maxWalkSpinner.setAdapter(maxWalkSpinnerAdapter);
        maxWalkSpinner.setSelection(maxWalkSelectIndex);
        maxWalkSpinner.setGravity(Gravity.END | Gravity.CENTER_VERTICAL);
        maxWalkSpinnerAdapter.setCurrentSelection(maxWalkSelectIndex);
        maxWalkSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                maxWalkSelectIndex = position;
                maxWalkSpinnerAdapter.setCurrentSelection(position);
                Configurations.setInt("maximum_walk_index", maxWalkSelectIndex);
                try {
                    //hide the
                    ImageView ivChecked = parent.findViewById(R.id.iv_checked);

                    if (ivChecked != null) {
                        ivChecked.setVisibility(View.GONE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                refreshSettingsChangedIcon();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void initWalkSpeedSpinner() {
        String[] stringArrays = systemContext.getResources().getStringArray(R.array.walk_speed_arrays_otp);
        walkSpeedSelectIndex = Configurations.getInt("walk_speed_index", Constants.DEFAULT_WALK_SPEED_INDEX);
        if (walkSpeedSelectIndex >= stringArrays.length) {
            walkSpeedSelectIndex = 0;
        }

        walkSpeedSpinnerAdapter = new SpinnerAdapter(systemContext, R.layout.item_spinner,
                R.id.tv_option, stringArrays);
        walkSpeedSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_list_item_checked);
        walkSpeedSpinner.setAdapter(walkSpeedSpinnerAdapter);
        walkSpeedSpinner.setSelection(walkSpeedSelectIndex);
        walkSpeedSpinner.setGravity(Gravity.END | Gravity.CENTER_VERTICAL);
        walkSpeedSpinnerAdapter.setCurrentSelection(walkSpeedSelectIndex);
        walkSpeedSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                walkSpeedSelectIndex = position;
                walkSpeedSpinnerAdapter.setCurrentSelection(position);
                Configurations.setInt("walk_speed_index", walkSpeedSelectIndex);
                try {
                    //hide the
                    ImageView ivChecked = parent.findViewById(R.id.iv_checked);

                    if (ivChecked != null) {
                        ivChecked.setVisibility(View.GONE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                refreshSettingsChangedIcon();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void initOptimizeOptionSpinner() {
        String[] stringArrays = systemContext.getResources().getStringArray(R.array.optimize_option_arrays);
        optimizeOptionSelectIndex = Configurations.getInt("optimize_option_index", Constants.DEFAULT_OPTIMIZE_OPTION_INDEX);
        if (optimizeOptionSelectIndex >= stringArrays.length) {
            optimizeOptionSelectIndex = 0;
        }

        optimizeOptionSpinnerAdapter = new SpinnerAdapter(systemContext, R.layout.item_spinner,
                R.id.tv_option, stringArrays);
        optimizeOptionSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_list_item_checked);
        optimizeOptionSpinner.setAdapter(optimizeOptionSpinnerAdapter);
        optimizeOptionSpinner.setGravity(Gravity.END | Gravity.CENTER_VERTICAL);
        optimizeOptionSpinner.setSelection(optimizeOptionSelectIndex);
        optimizeOptionSpinnerAdapter.setCurrentSelection(optimizeOptionSelectIndex);

        optimizeOptionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                optimizeOptionSelectIndex = position;
                optimizeOptionSpinnerAdapter.setCurrentSelection(position);
                Configurations.setInt("optimize_option_index", optimizeOptionSelectIndex);

                try {
                    //hide the
                    ImageView ivChecked = parent.findViewById(R.id.iv_checked);

                    if (ivChecked != null) {
                        ivChecked.setVisibility(View.GONE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                refreshSettingsChangedIcon();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void initSpinners() {
        initMaxWalkSpinner();
        initWalkSpeedSpinner();
        initOptimizeOptionSpinner();
    }

    private void refreshHomeSettingAvoidWalking() {
        boolean isEnabled = Configurations.getBoolean("enabled_search_mode_avoid_walking", false);

        if (Configurations.getBoolean("new_search_bar_layout_style", false)) {
            llHomeSettingAvoidWalkingAccessibility.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorModesBackground)));
            tvHomeSettingAvoidWalkingAccessibility.setTextColor(getResources().getColor(R.color.colorHomeSettingModeForeground));
            if (isEnabled) {
                ivHomeSettingAvoidWalking.setImageResource(R.drawable.ic_layer_toggle_on);
                llHomeSettingAvoidWalkingAccessibility.setBackgroundResource(R.drawable.rounded_primary_bg);

            } else {
                ivHomeSettingAvoidWalking.setImageResource(R.drawable.ic_layer_toggle_off);
                llHomeSettingAvoidWalkingAccessibility.setBackgroundResource(R.drawable.rounded_primary_frame_bg);

            }
        } else {
            if (isEnabled) {
                ivHomeSettingAvoidWalking.setImageResource(R.drawable.ic_layer_toggle_on);

                tvHomeSettingAvoidWalkingAccessibility.setTextColor(getResources().getColor(android.R.color.white));
                llHomeSettingAvoidWalkingAccessibility.setBackgroundResource(R.drawable.rounded_primary_bg);

            } else {
                ivHomeSettingAvoidWalking.setImageResource(R.drawable.ic_layer_toggle_off);

                tvHomeSettingAvoidWalkingAccessibility.setTextColor(getResources().getColor(android.R.color.black));
                llHomeSettingAvoidWalkingAccessibility.setBackgroundResource(R.drawable.rounded_primary_frame_bg);

            }
        }
    }


    private void refreshHomeSettingAccessibleRouting() {
        boolean isEnabled = Configurations.getBoolean("enabled_search_mode_accessible_routing", false);

        if (Configurations.getBoolean("new_search_bar_layout_style", false)) {
            llHomeSettingAccessibleRoutingAccessibility.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorModesBackground)));
            tvHomeSettingAccessibileRoutingAccessibility.setTextColor(getResources().getColor(R.color.colorHomeSettingModeForeground));
            if (isEnabled) {
                ivHomeSettingAccessibileRouting.setImageResource(R.drawable.ic_layer_toggle_on);
                llHomeSettingAccessibleRoutingAccessibility.setBackgroundResource(R.drawable.rounded_primary_bg);

            } else {
                ivHomeSettingAccessibileRouting.setImageResource(R.drawable.ic_layer_toggle_off);
                llHomeSettingAccessibleRoutingAccessibility.setBackgroundResource(R.drawable.rounded_primary_frame_bg);

            }
        } else {
            if (isEnabled) {
                ivHomeSettingAccessibileRouting.setImageResource(R.drawable.ic_layer_toggle_on);

                tvHomeSettingAccessibileRoutingAccessibility.setTextColor(getResources().getColor(android.R.color.white));
                llHomeSettingAccessibleRoutingAccessibility.setBackgroundResource(R.drawable.rounded_primary_bg);

            } else {
                ivHomeSettingAccessibileRouting.setImageResource(R.drawable.ic_layer_toggle_off);

                tvHomeSettingAccessibileRoutingAccessibility.setTextColor(getResources().getColor(android.R.color.black));
                llHomeSettingAccessibleRoutingAccessibility.setBackgroundResource(R.drawable.rounded_primary_frame_bg);

            }
        }
    }


    private void refreshHomeSettingAllowBikeRental() {
        boolean isEnabled = Configurations.getBoolean("enabled_search_mode_bike_rental", true);

        if (Configurations.getBoolean("new_search_bar_layout_style", false)) {
            llHomeSettingAllowBikeRentalAccessibility.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorModesBackground)));
            tvHomeSettingAllowBikeRentalAccessibility.setTextColor(getResources().getColor(R.color.colorHomeSettingModeForeground));
            if (isEnabled) {
                ivHomeSettingAllowBikeRental.setImageResource(R.drawable.ic_layer_toggle_on);
                llHomeSettingAllowBikeRentalAccessibility.setBackgroundResource(R.drawable.rounded_primary_bg);

            } else {
                ivHomeSettingAllowBikeRental.setImageResource(R.drawable.ic_layer_toggle_off);
                llHomeSettingAllowBikeRentalAccessibility.setBackgroundResource(R.drawable.rounded_primary_frame_bg);
            }
        } else {
            if (isEnabled) {
                ivHomeSettingAllowBikeRental.setImageResource(R.drawable.ic_layer_toggle_on);
                tvHomeSettingAllowBikeRentalAccessibility.setTextColor(getResources().getColor(android.R.color.white));
                llHomeSettingAllowBikeRentalAccessibility.setBackgroundResource(R.drawable.rounded_primary_bg);
            } else {
                ivHomeSettingAllowBikeRental.setImageResource(R.drawable.ic_layer_toggle_off);
                tvHomeSettingAllowBikeRentalAccessibility.setTextColor(getResources().getColor(android.R.color.black));
                llHomeSettingAllowBikeRentalAccessibility.setBackgroundResource(R.drawable.rounded_primary_frame_bg);
            }
        }
    }

    private void refreshHomeSettingAllowScooterRental() {
        boolean isEnabled = Configurations.getBoolean("enabled_search_mode_scooter_rental", true);

        if (Configurations.getBoolean("new_search_bar_layout_style", false)) {
            llHomeSettingAllowScooterRentalAccessibility.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorModesBackground)));
            tvHomeSettingAllowScooterRentalAccessibility.setTextColor(getResources().getColor(R.color.colorHomeSettingModeForeground));
            if (isEnabled) {
                ivHomeSettingAllowScooterRental.setImageResource(R.drawable.ic_layer_toggle_on);
                llHomeSettingAllowScooterRentalAccessibility.setBackgroundResource(R.drawable.rounded_primary_bg);
            } else {
                ivHomeSettingAllowScooterRental.setImageResource(R.drawable.ic_layer_toggle_off);
                llHomeSettingAllowScooterRentalAccessibility.setBackgroundResource(R.drawable.rounded_primary_frame_bg);
            }
        } else {
            if (isEnabled) {
                ivHomeSettingAllowScooterRental.setImageResource(R.drawable.ic_layer_toggle_on);
                tvHomeSettingAllowScooterRentalAccessibility.setTextColor(getResources().getColor(android.R.color.white));
                llHomeSettingAllowScooterRentalAccessibility.setBackgroundResource(R.drawable.rounded_primary_bg);
            } else {
                ivHomeSettingAllowScooterRental.setImageResource(R.drawable.ic_layer_toggle_off);
                tvHomeSettingAllowScooterRentalAccessibility.setTextColor(getResources().getColor(android.R.color.black));
                llHomeSettingAllowScooterRentalAccessibility.setBackgroundResource(R.drawable.rounded_primary_frame_bg);
            }
        }
    }

    private void refreshHomeSettingBus() {
        boolean isEnabled = Configurations.getBoolean("home_setting_bus_enabled", true);

        if (Configurations.getBoolean("new_search_bar_layout_style", false)) {
            ivHomeSettingBus.setColorFilter(getResources().getColor(R.color.colorHomeSettingModeForeground));
            tvHomeSettingBus.setTextColor(getResources().getColor(R.color.colorHomeSettingModeForeground));
            ivHomeSettingBusAccessibilityNew.setColorFilter(getResources().getColor(R.color.colorHomeSettingModeForeground));
            tvHomeSettingBusAccessibility.setTextColor(getResources().getColor(R.color.colorHomeSettingModeForeground));
            llHomeSettingBus.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorModesBackground)));
            llHomeSettingBusAccessibility.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorModesBackground)));
            if (isEnabled) {
                llHomeSettingBus.setBackgroundResource(R.drawable.rounded_primary_bg);

                ivHomeSettingBusAccessibility.setImageResource(R.drawable.ic_layer_toggle_on);
                llHomeSettingBusAccessibility.setBackgroundResource(R.drawable.rounded_primary_bg);

            } else {

                llHomeSettingBus.setBackgroundResource(R.drawable.rounded_primary_frame_bg);
                ivHomeSettingBusAccessibility.setImageResource(R.drawable.ic_layer_toggle_off);
                llHomeSettingBusAccessibility.setBackgroundResource(R.drawable.rounded_primary_frame_bg);
            }
        } else {
            if (isEnabled) {
                ivHomeSettingBus.setColorFilter(getResources().getColor(android.R.color.white));
                ivHomeSettingBus.setBackgroundResource(R.drawable.rounded_primary_bg);
                ivHomeSettingBusAccessibility.setImageResource(R.drawable.ic_layer_toggle_on);

                ivHomeSettingBusAccessibilityNew.setColorFilter(getResources().getColor(android.R.color.white));
                tvHomeSettingBusAccessibility.setTextColor(getResources().getColor(android.R.color.white));
                llHomeSettingBusAccessibility.setBackgroundResource(R.drawable.rounded_primary_bg);
            } else {
                ivHomeSettingBus.setColorFilter(getResources().getColor(android.R.color.black));
                ivHomeSettingBus.setBackground(null);
                ivHomeSettingBusAccessibility.setImageResource(R.drawable.ic_layer_toggle_off);

                ivHomeSettingBusAccessibilityNew.setColorFilter(getResources().getColor(android.R.color.black));
                tvHomeSettingBusAccessibility.setTextColor(getResources().getColor(android.R.color.black));
                llHomeSettingBusAccessibility.setBackgroundResource(R.drawable.rounded_primary_frame_bg);
            }
        }
    }

    private void refreshHomeSettingWaterTaxi() {
        boolean isEnabled = Configurations.getBoolean("home_setting_water_taxi_enabled", true);

        if (Configurations.getBoolean("new_search_bar_layout_style", false)) {
            ivHomeSettingWaterTaxi.setColorFilter(getResources().getColor(R.color.colorHomeSettingModeForeground));
            tvHomeSettingWaterTaxi.setTextColor(getResources().getColor(R.color.colorHomeSettingModeForeground));
            ivHomeSettingWaterTaxiAccessibilityNew.setColorFilter(getResources().getColor(R.color.colorHomeSettingModeForeground));
            tvHomeSettingWaterTaxiAccessibility.setTextColor(getResources().getColor(R.color.colorHomeSettingModeForeground));
            llHomeSettingWaterTaxi.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorModesBackground)));
            llHomeSettingWaterTaxiAccessibility.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorModesBackground)));
            if (isEnabled) {
                llHomeSettingWaterTaxi.setBackgroundResource(R.drawable.rounded_primary_bg);
                ivHomeSettingWaterTaxiAccessibility.setImageResource(R.drawable.ic_layer_toggle_on);
                llHomeSettingWaterTaxiAccessibility.setBackgroundResource(R.drawable.rounded_primary_bg);
            } else {
                llHomeSettingWaterTaxi.setBackgroundResource(R.drawable.rounded_primary_frame_bg);
                ivHomeSettingWaterTaxiAccessibility.setImageResource(R.drawable.ic_layer_toggle_off);
                llHomeSettingWaterTaxiAccessibility.setBackgroundResource(R.drawable.rounded_primary_frame_bg);
            }
        } else {
            if (isEnabled) {
                ivHomeSettingWaterTaxi.setColorFilter(getResources().getColor(android.R.color.white));
                ivHomeSettingWaterTaxi.setBackgroundResource(R.drawable.rounded_primary_bg);
                ivHomeSettingWaterTaxiAccessibility.setImageResource(R.drawable.ic_layer_toggle_on);

                ivHomeSettingWaterTaxiAccessibilityNew.setColorFilter(getResources().getColor(android.R.color.white));
                tvHomeSettingWaterTaxiAccessibility.setTextColor(getResources().getColor(android.R.color.white));
                llHomeSettingWaterTaxiAccessibility.setBackgroundResource(R.drawable.rounded_primary_bg);

            } else {
                ivHomeSettingWaterTaxi.setColorFilter(getResources().getColor(android.R.color.black));
                ivHomeSettingWaterTaxi.setBackground(null);
                ivHomeSettingWaterTaxiAccessibility.setImageResource(R.drawable.ic_layer_toggle_off);
                ivHomeSettingWaterTaxiAccessibilityNew.setColorFilter(getResources().getColor(android.R.color.black));
                tvHomeSettingWaterTaxiAccessibility.setTextColor(getResources().getColor(android.R.color.black));
                llHomeSettingWaterTaxiAccessibility.setBackgroundResource(R.drawable.rounded_primary_frame_bg);
            }
        }
    }

    private void refreshHomeSettingMonorail() {
        boolean isEnabled = Configurations.getBoolean("home_setting_monorail_enabled", true);

        if (Configurations.getBoolean("new_search_bar_layout_style", false)) {
            ivHomeSettingMonorail.setColorFilter(getResources().getColor(R.color.colorHomeSettingModeForeground));
            tvHomeSettingMonorail.setTextColor(getResources().getColor(R.color.colorHomeSettingModeForeground));
            ivHomeSettingMonorailAccessibilityNew.setColorFilter(getResources().getColor(R.color.colorHomeSettingModeForeground));
            tvHomeSettingMonorailAccessibility.setTextColor(getResources().getColor(R.color.colorHomeSettingModeForeground));
            llHomeSettingMonorail.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorModesBackground)));
            llHomeSettingMonorailAccessibility.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorModesBackground)));
            if (isEnabled) {
                llHomeSettingMonorail.setBackgroundResource(R.drawable.rounded_primary_bg);
                ivHomeSettingMonorailAccessibility.setImageResource(R.drawable.ic_layer_toggle_on);
                llHomeSettingMonorailAccessibility.setBackgroundResource(R.drawable.rounded_primary_bg);
            } else {
                llHomeSettingMonorail.setBackgroundResource(R.drawable.rounded_primary_frame_bg);
                ivHomeSettingMonorailAccessibility.setImageResource(R.drawable.ic_layer_toggle_off);
                llHomeSettingMonorailAccessibility.setBackgroundResource(R.drawable.rounded_primary_frame_bg);
            }
        } else {
            if (isEnabled) {
                ivHomeSettingMonorail.setColorFilter(getResources().getColor(android.R.color.white));
                ivHomeSettingMonorail.setBackgroundResource(R.drawable.rounded_primary_bg);
                ivHomeSettingMonorailAccessibility.setImageResource(R.drawable.ic_layer_toggle_on);

                ivHomeSettingMonorailAccessibilityNew.setColorFilter(getResources().getColor(android.R.color.white));
                tvHomeSettingMonorailAccessibility.setTextColor(getResources().getColor(android.R.color.white));
                llHomeSettingMonorailAccessibility.setBackgroundResource(R.drawable.rounded_primary_bg);

            } else {
                ivHomeSettingMonorail.setColorFilter(getResources().getColor(android.R.color.black));
                ivHomeSettingMonorail.setBackground(null);
                ivHomeSettingMonorailAccessibility.setImageResource(R.drawable.ic_layer_toggle_off);
                ivHomeSettingMonorailAccessibilityNew.setColorFilter(getResources().getColor(android.R.color.black));
                tvHomeSettingMonorailAccessibility.setTextColor(getResources().getColor(android.R.color.black));
                llHomeSettingMonorailAccessibility.setBackgroundResource(R.drawable.rounded_primary_frame_bg);
            }
        }
    }

    private void refreshHomeSettingLink() {
        boolean isEnabled = Configurations.getBoolean("home_setting_link_enabled", true);

        if (Configurations.getBoolean("new_search_bar_layout_style", false)) {
            ivHomeSettingLink.setColorFilter(getResources().getColor(R.color.colorHomeSettingModeForeground));
            tvHomeSettingLink.setTextColor(getResources().getColor(R.color.colorHomeSettingModeForeground));
            ivHomeSettingLinkAccessibilityNew.setColorFilter(getResources().getColor(R.color.colorHomeSettingModeForeground));
            tvHomeSettingLinkAccessibility.setTextColor(getResources().getColor(R.color.colorHomeSettingModeForeground));
            llHomeSettingLink.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorModesBackground)));
            llHomeSettingLinkAccessibility.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorModesBackground)));
            if (isEnabled) {
                llHomeSettingLink.setBackgroundResource(R.drawable.rounded_primary_bg);
                ivHomeSettingLinkAccessibility.setImageResource(R.drawable.ic_layer_toggle_on);
                llHomeSettingLinkAccessibility.setBackgroundResource(R.drawable.rounded_primary_bg);
            } else {
                llHomeSettingLink.setBackgroundResource(R.drawable.rounded_primary_frame_bg);
                ivHomeSettingLinkAccessibility.setImageResource(R.drawable.ic_layer_toggle_off);
                llHomeSettingLinkAccessibility.setBackgroundResource(R.drawable.rounded_primary_frame_bg);
            }
        } else {
            if (isEnabled) {
                ivHomeSettingLink.setColorFilter(getResources().getColor(android.R.color.white));
                ivHomeSettingLink.setBackgroundResource(R.drawable.rounded_primary_bg);
                ivHomeSettingLinkAccessibility.setImageResource(R.drawable.ic_layer_toggle_on);

                ivHomeSettingLinkAccessibilityNew.setColorFilter(getResources().getColor(android.R.color.white));
                tvHomeSettingLinkAccessibility.setTextColor(getResources().getColor(android.R.color.white));
                llHomeSettingLinkAccessibility.setBackgroundResource(R.drawable.rounded_primary_bg);

            } else {
                ivHomeSettingLink.setColorFilter(getResources().getColor(android.R.color.black));
                ivHomeSettingLink.setBackground(null);
                ivHomeSettingLinkAccessibility.setImageResource(R.drawable.ic_layer_toggle_off);

                ivHomeSettingLinkAccessibilityNew.setColorFilter(getResources().getColor(android.R.color.black));
                tvHomeSettingLinkAccessibility.setTextColor(getResources().getColor(android.R.color.black));
                llHomeSettingLinkAccessibility.setBackgroundResource(R.drawable.rounded_primary_frame_bg);
            }
        }
    }

    private void refreshHomeSettingRail() {
        boolean isEnabled = Configurations.getBoolean("home_setting_rail_enabled", true);

        if (Configurations.getBoolean("new_search_bar_layout_style", false)) {
            ivHomeSettingMartaRail.setColorFilter(getResources().getColor(R.color.colorHomeSettingModeForeground));
            tvHomeSettingMartaRail.setTextColor(getResources().getColor(R.color.colorHomeSettingModeForeground));
            ivHomeSettingMartaRailAcessibilityNew.setColorFilter(getResources().getColor(R.color.colorHomeSettingModeForeground));
            tvHomeSettingMartaRailAccessibility.setTextColor(getResources().getColor(R.color.colorHomeSettingModeForeground));
            llHomeSettingMartaRail.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorModesBackground)));
            llHomeSettingMartaRailAccessibility.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorModesBackground)));
            if (isEnabled) {

                llHomeSettingMartaRail.setBackgroundResource(R.drawable.rounded_primary_bg);
                ivHomeSettingMartaRailAcessibility.setImageResource(R.drawable.ic_layer_toggle_on);
                llHomeSettingMartaRailAccessibility.setBackgroundResource(R.drawable.rounded_primary_bg);

            } else {

                llHomeSettingMartaRail.setBackgroundResource(R.drawable.rounded_primary_frame_bg);
                ivHomeSettingMartaRailAcessibility.setImageResource(R.drawable.ic_layer_toggle_off);
                llHomeSettingMartaRailAccessibility.setBackgroundResource(R.drawable.rounded_primary_frame_bg);
            }
        } else {
            if (isEnabled) {
                ivHomeSettingMartaRail.setColorFilter(getResources().getColor(android.R.color.white));
                ivHomeSettingMartaRail.setBackgroundResource(R.drawable.rounded_primary_bg);
                ivHomeSettingMartaRailAcessibility.setImageResource(R.drawable.ic_layer_toggle_on);

                ivHomeSettingMartaRailAcessibilityNew.setColorFilter(getResources().getColor(android.R.color.white));
                tvHomeSettingMartaRailAccessibility.setTextColor(getResources().getColor(android.R.color.white));
                llHomeSettingMartaRailAccessibility.setBackgroundResource(R.drawable.rounded_primary_bg);

            } else {
                ivHomeSettingMartaRail.setColorFilter(getResources().getColor(android.R.color.black));
                ivHomeSettingMartaRail.setBackground(null);
                ivHomeSettingMartaRailAcessibility.setImageResource(R.drawable.ic_layer_toggle_off);

                ivHomeSettingMartaRailAcessibilityNew.setColorFilter(getResources().getColor(android.R.color.black));
                tvHomeSettingMartaRailAccessibility.setTextColor(getResources().getColor(android.R.color.black));
                llHomeSettingMartaRailAccessibility.setBackgroundResource(R.drawable.rounded_primary_frame_bg);
            }
        }
    }

    private void refreshHomeSettingStreetCar() {
        boolean isEnabled = Configurations.getBoolean("home_setting_street_car_enabled", true);

        if (Configurations.getBoolean("new_search_bar_layout_style", false)) {
            ivHomeSettingStreetCar.setColorFilter(getResources().getColor(R.color.colorHomeSettingModeForeground));
            tvHomeSettingStreetCar.setTextColor(getResources().getColor(R.color.colorHomeSettingModeForeground));
            ivHomeSettingStreetCarAccessibilityNew.setColorFilter(getResources().getColor(R.color.colorHomeSettingModeForeground));
            tvHomeSettingStreetCarAccessibility.setTextColor(getResources().getColor(R.color.colorHomeSettingModeForeground));
            llHomeSettingStreetCar.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorModesBackground)));
            llHomeSettingStreetCarAccessibility.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorModesBackground)));
            if (isEnabled) {
                llHomeSettingStreetCar.setBackgroundResource(R.drawable.rounded_primary_bg);
                ivHomeSettingStreetCarAccessibility.setImageResource(R.drawable.ic_layer_toggle_on);
                llHomeSettingStreetCarAccessibility.setBackgroundResource(R.drawable.rounded_primary_bg);

            } else {

                llHomeSettingStreetCar.setBackgroundResource(R.drawable.rounded_primary_frame_bg);
                ivHomeSettingStreetCarAccessibility.setImageResource(R.drawable.ic_layer_toggle_off);
                llHomeSettingStreetCarAccessibility.setBackgroundResource(R.drawable.rounded_primary_frame_bg);
            }
        } else {
            if (isEnabled) {
                ivHomeSettingStreetCar.setColorFilter(getResources().getColor(android.R.color.white));
                ivHomeSettingStreetCar.setBackgroundResource(R.drawable.rounded_primary_bg);
                ivHomeSettingStreetCarAccessibility.setImageResource(R.drawable.ic_layer_toggle_on);

                ivHomeSettingStreetCarAccessibilityNew.setColorFilter(getResources().getColor(android.R.color.white));
                tvHomeSettingStreetCarAccessibility.setTextColor(getResources().getColor(android.R.color.white));
                llHomeSettingStreetCarAccessibility.setBackgroundResource(R.drawable.rounded_primary_bg);

            } else {
                ivHomeSettingStreetCar.setColorFilter(getResources().getColor(android.R.color.black));
                ivHomeSettingStreetCar.setBackground(null);
                ivHomeSettingStreetCarAccessibility.setImageResource(R.drawable.ic_layer_toggle_off);


                ivHomeSettingStreetCarAccessibilityNew.setColorFilter(getResources().getColor(android.R.color.black));
                tvHomeSettingStreetCarAccessibility.setTextColor(getResources().getColor(android.R.color.black));
                llHomeSettingStreetCarAccessibility.setBackgroundResource(R.drawable.rounded_primary_frame_bg);
            }
        }
    }

    private void refreshHomeSettingFerry() {
        boolean isEnabled = Configurations.getBoolean("home_setting_ferry_enabled", true);

        if (Configurations.getBoolean("new_search_bar_layout_style", false)) {
            ivHomeSettingFerry.setColorFilter(getResources().getColor(R.color.colorHomeSettingModeForeground));
            tvHomeSettingFerry.setTextColor(getResources().getColor(R.color.colorHomeSettingModeForeground));
            ivHomeSettingFerryAccessibilityNew.setColorFilter(getResources().getColor(R.color.colorHomeSettingModeForeground));
            tvHomeSettingFerryAccessibility.setTextColor(getResources().getColor(R.color.colorHomeSettingModeForeground));
            llHomeSettingFerry.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorModesBackground)));
            llHomeSettingFerryAccessibility.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorModesBackground)));
            if (isEnabled) {
                llHomeSettingFerry.setBackgroundResource(R.drawable.rounded_primary_bg);
                ivHomeSettingFerryAccessibility.setImageResource(R.drawable.ic_layer_toggle_on);
                llHomeSettingFerryAccessibility.setBackgroundResource(R.drawable.rounded_primary_bg);

            } else {

                llHomeSettingFerry.setBackgroundResource(R.drawable.rounded_primary_frame_bg);
                ivHomeSettingFerryAccessibility.setImageResource(R.drawable.ic_layer_toggle_off);
                llHomeSettingFerryAccessibility.setBackgroundResource(R.drawable.rounded_primary_frame_bg);
            }
        } else {
            if (isEnabled) {
                ivHomeSettingFerry.setColorFilter(getResources().getColor(android.R.color.white));
                ivHomeSettingFerry.setBackgroundResource(R.drawable.rounded_primary_bg);
                ivHomeSettingFerryAccessibility.setImageResource(R.drawable.ic_layer_toggle_on);

                ivHomeSettingFerryAccessibilityNew.setColorFilter(getResources().getColor(android.R.color.white));
                tvHomeSettingFerryAccessibility.setTextColor(getResources().getColor(android.R.color.white));
                llHomeSettingFerryAccessibility.setBackgroundResource(R.drawable.rounded_primary_bg);

            } else {
                ivHomeSettingFerry.setColorFilter(getResources().getColor(android.R.color.black));
                ivHomeSettingFerry.setBackground(null);
                ivHomeSettingFerryAccessibility.setImageResource(R.drawable.ic_layer_toggle_off);


                ivHomeSettingFerryAccessibilityNew.setColorFilter(getResources().getColor(android.R.color.black));
                tvHomeSettingFerryAccessibility.setTextColor(getResources().getColor(android.R.color.black));
                llHomeSettingFerryAccessibility.setBackgroundResource(R.drawable.rounded_primary_frame_bg);
            }
        }
    }

    private void refreshHomeSettingGondola() {
        boolean isEnabled = Configurations.getBoolean("home_setting_gondola_enabled", true);

        if (Configurations.getBoolean("new_search_bar_layout_style", false)) {
            if (isEnabled) {
                ivHomeSettingGondola.setColorFilter(getResources().getColor(android.R.color.white));
                tvHomeSettingGondola.setTextColor(getResources().getColor(android.R.color.white));
                llHomeSettingGondola.setBackgroundResource(R.drawable.rounded_primary_bg);
            } else {
                ivHomeSettingGondola.setColorFilter(getResources().getColor(android.R.color.black));
                tvHomeSettingGondola.setTextColor(getResources().getColor(android.R.color.black));
                llHomeSettingGondola.setBackground(null);
            }
        } else {
            if (isEnabled) {
                ivHomeSettingGondola.setColorFilter(getResources().getColor(android.R.color.white));
                ivHomeSettingGondola.setBackgroundResource(R.drawable.rounded_primary_bg);
            } else {
                ivHomeSettingGondola.setColorFilter(getResources().getColor(android.R.color.black));
                ivHomeSettingGondola.setBackground(null);
            }
        }
    }

    private void initSettings() {
        initSpinners();

        refreshHomeSettingBus();
        refreshHomeSettingRail();
        refreshHomeSettingStreetCar();
        refreshHomeSettingFerry();
        refreshHomeSettingGondola();
        refreshHomeSettingLink();
        refreshHomeSettingWaterTaxi();
        refreshHomeSettingMonorail();
        refreshHomeSettingAvoidWalking();
        refreshHomeSettingAccessibleRouting();
        refreshHomeSettingAllowBikeRental();
        refreshHomeSettingAllowScooterRental();
    }

    private void refreshWalkSpeedAccessibilitySelectedOption() {
        String[] stringArrays = systemContext.getResources().getStringArray(R.array.walk_speed_arrays_otp);
        int currentSelectIndex = Configurations.getInt("walk_speed_index", Constants.DEFAULT_WALK_SPEED_INDEX);
        if (currentSelectIndex >= stringArrays.length) {
            currentSelectIndex = 0;
        }
        if (tvHomeSettingWalkSpeedAccessibility != null) {
            tvHomeSettingWalkSpeedAccessibility.setText(stringArrays[currentSelectIndex]);
        }
    }

    private void refreshWalkSpeedAccessibilityOptions() {
        if (llHomeSettingWalkSpeedAccessibilityOptions != null && llHomeSettingWalkSpeedAccessibilityOptions.getChildCount() > 0) {
            String[] stringArrays = systemContext.getResources().getStringArray(R.array.walk_speed_arrays_otp);
            int currentSelectIndex = Configurations.getInt("walk_speed_index", Constants.DEFAULT_WALK_SPEED_INDEX);
            if (currentSelectIndex >= stringArrays.length) {
                currentSelectIndex = 0;
            }

            for (int i = 0; i < llHomeSettingWalkSpeedAccessibilityOptions.getChildCount(); i++) {
                View walkSpeedAccessibilityOptionView = llHomeSettingWalkSpeedAccessibilityOptions.getChildAt(i);

                if (walkSpeedAccessibilityOptionView != null) {
                    if (currentSelectIndex == i) {
                        walkSpeedAccessibilityOptionView.setBackgroundResource(R.drawable.rounded_primary_bg);
                    } else {
                        walkSpeedAccessibilityOptionView.setBackgroundResource(R.drawable.rounded_primary_frame_bg);
                    }

                    walkSpeedAccessibilityOptionView.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorModesBackground)));
                }
            }

        }
    }

    private void addWalkSpeedAccessibilityOptionsAsNeeded() {
        if (llHomeSettingWalkSpeedAccessibilityOptions != null) {
            String[] stringArrays = systemContext.getResources().getStringArray(R.array.walk_speed_arrays_otp);
            int currentSelectIndex = Configurations.getInt("walk_speed_index", Constants.DEFAULT_WALK_SPEED_INDEX);
            if (currentSelectIndex >= stringArrays.length) {
                currentSelectIndex = 0;
            }
            llHomeSettingWalkSpeedAccessibilityOptions.removeAllViews();
            for (int i = 0; i < stringArrays.length; i++) {
                View walkSpeedAccessibilityOptionView = ((LayoutInflater) systemContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).
                        inflate(R.layout.item_walk_speed_accessibility_option, null, false);
                TextView tvWalkSpeedOption = walkSpeedAccessibilityOptionView.findViewById(R.id.tv_walk_speed_option);
                walkSpeedAccessibilityOptionView.setTag(i);
                tvWalkSpeedOption.setText(stringArrays[i]);
                if (currentSelectIndex == i) {
                    walkSpeedAccessibilityOptionView.setBackgroundResource(R.drawable.rounded_primary_bg);
                } else {
                    walkSpeedAccessibilityOptionView.setBackgroundResource(R.drawable.rounded_primary_frame_bg);
                }
                walkSpeedAccessibilityOptionView.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorModesBackground)));
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                        MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                layoutParams.bottomMargin = Convert.dpToPx(10);
                walkSpeedAccessibilityOptionView.setLayoutParams(layoutParams);

                walkSpeedAccessibilityOptionView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int index = (int) v.getTag();
                        String[] stringArrays = systemContext.getResources().getStringArray(R.array.walk_speed_arrays_otp);
                        int currentSelectIndex = Configurations.getInt("walk_speed_index", Constants.DEFAULT_WALK_SPEED_INDEX);
                        if (index != currentSelectIndex && index < stringArrays.length) {
                            Configurations.setInt("walk_speed_index", index);
                            refreshWalkSpeedAccessibilityOptions();
                            if (tvHomeSettingWalkSpeedAccessibility != null) {
                                tvHomeSettingWalkSpeedAccessibility.setText(stringArrays[index]);
                            }
                        }
                    }
                });
                llHomeSettingWalkSpeedAccessibilityOptions.addView(walkSpeedAccessibilityOptionView);
            }
        }
    }

    private void showCustomAlertDialog(CompoundButton compoundButton) {
        if (clAdvancedPreferences.getVisibility() != View.VISIBLE) {
            return;
        }
        // Inflate the custom layout/view
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.layout_advanced_warning_popup, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        builder.setView(dialogView);
        AlertDialog alertDialog = builder.create();
        TextView alertTitle = dialogView.findViewById(R.id.tv_popup_title);
        Button btnOk = dialogView.findViewById(R.id.btn_ok);
        Button btnCancel = dialogView.findViewById(R.id.btn_cancel);

        if (compoundButton.getId() == R.id.switch_accessible) {
            alertTitle.setText(R.string.accessible_trip_toggle_msg);
        } else if (compoundButton.getId() == R.id.switch_avoid_walking) {
            alertTitle.setText(R.string.addition_modes_toggle_msg);
        }


        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        alertDialog.show();
    }

    private void setupAdvancedPref() {
        llSearchBarHolder = mFragmentView.findViewById(R.id.ll_search_bar_holder);
        radioGrid = mFragmentView.findViewById(R.id.radio_grid);
        llWalkSpeedHolder = mFragmentView.findViewById(R.id.ll_walk_speed_holder);

        clTimeOptions = mFragmentView.findViewById(R.id.cl_time_options);
        clAdvancedPreferences = mFragmentView.findViewById(R.id.cl_advanced_preferences);
        PlanViewMainLayout = mFragmentView.findViewById(R.id.plan_view_main_layout);

        invisibleView = mFragmentView.findViewById(R.id.invisible_view);
        expandableListView = mFragmentView.findViewById(R.id.expandableListView_pref);
        ExpandableListAdapter expandableListAdapter = new ExpandableListAdapter(
                requireActivity(),
                Utilities.getExpandableListItems(requireActivity())
        );
        expandableListView.setAdapter(expandableListAdapter);

        accessibleSwitch = mFragmentView.findViewById(R.id.switch_accessible);
        accessibleSwitch.getThumbDrawable().setColorFilter(requireActivity().getResources().getColor(R.color.superLightGrey), PorterDuff.Mode.SRC_IN);

        switchAvoidWalking = mFragmentView.findViewById(R.id.switch_avoid_walking);
        switchAvoidWalking.getThumbDrawable().setColorFilter(requireActivity().getResources().getColor(R.color.superLightGrey), PorterDuff.Mode.SRC_IN);

        rbLessWalking = mFragmentView.findViewById(R.id.rb_less_walking);
        rbFewerTransfer = mFragmentView.findViewById(R.id.rb_fewer_transfer);
        rbFastest = mFragmentView.findViewById(R.id.rb_fastest);

        rbLessWalking.setChecked(Configurations.getBoolean("enabled_search_mode_avoid_walking", false));
        rbFewerTransfer.setChecked(Configurations.getBoolean("enabled_search_mode_fewest_transfers", false));
        rbFastest.setChecked(Configurations.getBoolean("enabled_search_mode_fastest", false));

        isAccessibleTrip = Configurations.getBoolean("enabled_search_mode_accessible_routing", false);
        accessibleSwitch.setChecked(isAccessibleTrip);
        accessibleSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isCheck) {
                isAccessibleTrip = isCheck;
            }
        });

        isAvoidWalking = Configurations.getBoolean("enabled_search_mode_avoid_walking", false);
        switchAvoidWalking.setChecked(isAvoidWalking);
        switchAvoidWalking.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isCheck) {
                isAvoidWalking = isCheck;
            }
        });

        ivAccessibleAlert = mFragmentView.findViewById(R.id.iv_accessible_alert);
        ivAdditionalModeAlert = mFragmentView.findViewById(R.id.iv_additional_mode_alert);

        ivAccessibleAlert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCustomAlertDialog(accessibleSwitch);
            }
        });

        ivAdditionalModeAlert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCustomAlertDialog(switchAvoidWalking);
            }
        });


        //add spinner leaveStatusItems
        spSearchOptions = mFragmentView.findViewById(R.id.sp_search_options);
        spSearchOptionsAdvanced = mFragmentView.findViewById(R.id.sp_search_options_advanced);

        if (spSearchOptionsAdvanced.getSelectedItemPosition() == 0) {
            invisibleView.setVisibility(View.GONE);
        } else {
            invisibleView.setVisibility(View.INVISIBLE);
            ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) invisibleView.getLayoutParams();

            if (spSearchOptionsAdvanced.getSelectedItemPosition() == 1) {
                layoutParams.topToBottom = R.id.ll_arrive_by_date_time_advance_pref;
            } else {
                layoutParams.topToBottom = R.id.ll_depart_after_date_time_advance_pref;
            }

            invisibleView.setLayoutParams(layoutParams);
        }

        String[] leaveStatusItems = getResources().getStringArray(R.array.search_options);
        if (leaveStatusItems != null) {
            adapterSearchState = new CustomSpinnerAdapter(requireActivity(), leaveStatusItems);
            spSearchOptions.setAdapter(adapterSearchState);
            spSearchOptionsAdvanced.setAdapter(adapterSearchState);

            if (itemSelectedListener == null) {
                setSearchOptionsItemSelectedListener(leaveStatusItems);
            } else {
                updateSearchOptionsSpinnerStatus(searchOptionsSpinnerIndex, false);
            }

            spSearchOptions.setOnItemSelectedListener(itemSelectedListener);
            spSearchOptionsAdvanced.setOnItemSelectedListener(itemSelectedListener);

            View.OnTouchListener touchListener = (v, event) -> {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    timePickerHourTmp = currentTimePickerHour;
                    timePickerMinTmp = currentTimePickerMin;
                    datePickerYearTmp = currentDatePickerYear;
                    datePickerMonthTmp = currentDatePickerMonth;
                    datePickerDayOfMonthTmp = currentDatePickerDayOfMonth;
                    isLeaveNowTmp = isLeaveNow;
                    isDepartAtTmp = isDepartAt;
                    isArriveByTmp = isArriveBy;
                }
                return false;
            };

            spSearchOptions.setOnTouchListener(touchListener);
            spSearchOptionsAdvanced.setOnTouchListener(touchListener);

        }

        tvMoreOptions = mFragmentView.findViewById(R.id.tv_more_options);
        String text = getString(R.string.more_options);
        SpannableString content = new SpannableString(text);
        content.setSpan(new UnderlineSpan(), 0, text.length(), 0);
        tvMoreOptions.setText(content);


        llSearchModeIcons = mFragmentView.findViewById(R.id.ll_search_mode_icons);
        ivCloseButton = mFragmentView.findViewById(R.id.close_button_pref);

        tvMoreOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clAdvancedPreferences.setVisibility(View.VISIBLE);
                PlanViewMainLayout.setVisibility(View.GONE);
                //update searchOptions spinner
                searchOptionsSpinnerIndex = tempSearchOptionsSpinnerIndex;
                updateSearchOptionsSpinnerStatus(searchOptionsSpinnerIndex, false);

                if (llBottom_tab != null && flFragmentHolder != null) {
                    llBottom_tab.setVisibility(View.GONE);
                    RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) flFragmentHolder.getLayoutParams();
                    layoutParams.bottomMargin = 10;
                    flFragmentHolder.setLayoutParams(layoutParams);
                }

                switchToAdvancedTimePicker();


                //refresh spinner
                currentAdvancedSpinnerPosition = spSearchOptions.getSelectedItemPosition();
                spSearchOptionsAdvanced.setSelection(currentAdvancedSpinnerPosition);
                if (spSearchOptionsAdvanced.getSelectedItemPosition() == 0) {
                    invisibleView.setVisibility(View.GONE);
                } else {
                    invisibleView.setVisibility(View.INVISIBLE);
                    ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) invisibleView.getLayoutParams();

                    if (spSearchOptionsAdvanced.getSelectedItemPosition() == 1) {
                        layoutParams.topToBottom = R.id.ll_arrive_by_date_time_advance_pref;
                    } else {
                        layoutParams.topToBottom = R.id.ll_depart_after_date_time_advance_pref;
                    }

                    invisibleView.setLayoutParams(layoutParams);
                }


                //refresh time
                currentDatePickerYear = datePickerYearTmp;
                currentDatePickerMonth = datePickerMonthTmp;
                currentDatePickerDayOfMonth = datePickerDayOfMonthTmp;

                currentTimePickerHour = timePickerHourTmp;
                currentTimePickerMin = timePickerMinTmp;

                rlOptionTime.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showTimePickerDialog();
                    }
                });

                rlOptionDate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showDatePicker();
                    }
                });

                rlArriveByAtOptionTime.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showTimePickerDialog();
                    }
                });
                rlArriveByOptionDate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showDatePicker();
                    }
                });
                rlHomeDateTime.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        hideLayerPopup();
                    }
                });

                refreshSearchTime();
                refreshSearchDate();

                // make default expand status
                List<ParentItem> parentItems = Utilities.getExpandableListItems(requireActivity());
                for (int i = 0; i < parentItems.size(); i++) {
                    ParentItem pItem = parentItems.get(i);
                    if (pItem.getName().equalsIgnoreCase(getString(R.string.transit))) {
                        pItem.setCheck(Configurations.getBoolean("enabled_search_mode_transit", true));
                    } else if (pItem.getName().equalsIgnoreCase(getString(R.string.bike))) {
                        pItem.setCheck(Configurations.getBoolean("enabled_search_mode_bike", false));
                    } else if (pItem.getName().equalsIgnoreCase(getString(R.string.drive))) {
                        pItem.setCheck(Configurations.getBoolean("enabled_search_mode_drive", false));
                    } else if (pItem.getName().equalsIgnoreCase(getString(R.string.rent))) {
                        pItem.setCheck(Configurations.getBoolean("enabled_search_mode_rental", true));
                    } else if (pItem.getName().equalsIgnoreCase(getString(R.string.walk))) {
                        pItem.setCheck(Configurations.getBoolean("enabled_search_mode_walk", true));
                    }
                    pItem.setCheckedTemp(pItem.isCheck());
                }

                try {
                    if (Configurations.getBoolean("enabled_search_mode_transit", true)) {
                        expandableListView.expandGroup(0, true);
                    } else {
                        expandableListView.collapseGroup(0);
                    }
                    if (Configurations.getBoolean("enabled_search_mode_bike", false)) {
                        expandableListView.expandGroup(1, true);
                    } else {
                        expandableListView.collapseGroup(1);
                    }
                    if (Configurations.getBoolean("enabled_search_mode_drive", false)) {
                        expandableListView.expandGroup(2, true);
                    } else {
                        expandableListView.collapseGroup(2);
                    }
                    String selectableModes = Configurations.getString("selectable_modes",
                            "TRANSIT:BUS,TRAM,FERRY,RAIL,STREETCAR,WATER_TAXI;BIKE;WALK;CAR;ST-RENT").toLowerCase(Locale.ROOT);
                    if (selectableModes.contains("rent")) {
                        if (Configurations.getBoolean("enabled_search_mode_rental", true)) {
                            expandableListView.expandGroup(3, true);
                        } else {
                            expandableListView.collapseGroup(3);
                        }
                    }
                    if (selectableModes.contains("walk")) {
                        if (Configurations.getBoolean("enabled_search_mode_walk", true)) {
                            expandableListView.expandGroup(4, true);
                        } else {
                            expandableListView.collapseGroup(4);
                        }
                    }
                } catch (Exception e) {
                    Log.e("PlanTripFragment", e.getStackTrace().toString());
                }
            }
        });

        // discard change button
        ivCloseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //show popup window
                View popupView = LayoutInflater.from(requireActivity()).inflate(R.layout.layout_advanced_preference_popup, null);
                AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
                builder.setView(popupView);
                AlertDialog dialog = builder.create();

                // Set button click listeners
                Button keepEditingButton = popupView.findViewById(R.id.btn_editing);
                Button discardChangesButton = popupView.findViewById(R.id.btn_discard);

                keepEditingButton.setOnClickListener(v -> {
                    // Handle "Keep Editing" button click
                    dialog.dismiss(); // Dismiss the dialog or perform any action
                });

                discardChangesButton.setOnClickListener(v -> {

                    clAdvancedPreferences.setVisibility(View.GONE);
                    PlanViewMainLayout.setVisibility(View.VISIBLE);
                    if (llBottom_tab != null && flFragmentHolder != null) {
                        llBottom_tab.setVisibility(View.VISIBLE);
                        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) flFragmentHolder.getLayoutParams();
                        layoutParams.bottomMargin = originalBottomMargin;
                        flFragmentHolder.setLayoutParams(layoutParams);
                    }


//                    Utilities.resetExpandableListItems(requireActivity());
                    ExpandableListAdapter expandableListAdapter = new ExpandableListAdapter(
                            requireActivity(),
                            Utilities.getExpandableListItems(requireActivity())
                    );
                    expandableListView.setAdapter(expandableListAdapter);

                    isAccessibleTrip = Configurations.getBoolean("enabled_search_mode_accessible_routing", false);
                    accessibleSwitch.setChecked(isAccessibleTrip);

                    isAvoidWalking = Configurations.getBoolean("enabled_search_mode_avoid_walking", false);
                    switchAvoidWalking.setChecked(isAvoidWalking);

                    initWalkSpeedSpinner();

                    if (itemSelectedListener == null) {
                        setSearchOptionsItemSelectedListener(leaveStatusItems);
                    } else {
                        updateSearchOptionsSpinnerStatus(searchOptionsSpinnerIndex, false);
                        tempSearchOptionsSpinnerIndex = searchOptionsSpinnerIndex;
                    }

                    spSearchOptions.setOnItemSelectedListener(itemSelectedListener);
                    spSearchOptionsAdvanced.setOnItemSelectedListener(itemSelectedListener);

//                    //refresh spinner
                    spSearchOptions.setSelection(currentAdvancedSpinnerPosition);
                    spSearchOptionsAdvanced.setSelection(currentAdvancedSpinnerPosition);

//                    //refresh time
                    switchToDefaultTimePicker(true);
                    dialog.dismiss(); // Dismiss the dialog or perform any action

                    //update check status
                    List<ParentItem> parentItems = Utilities.getExpandableListItems(requireActivity());
                    for (int i = 0; i < parentItems.size(); i++) {
                        ParentItem pItem = parentItems.get(i);
                        pItem.setCheckedTemp(pItem.isCheck());
                        for (ChildItem c : pItem.getChildren()) {
                            c.setCheckedTemp(c.isChecked());
                        }
                    }
                });
                // Show the dialog
                dialog.show();
            }
        });

        initSearchBar();

    }


    public void resetAdvancedPreferences() {
        ExpandableListAdapter expandableListAdapter = new ExpandableListAdapter(
                requireActivity(),
                Utilities.getExpandableListItems(requireActivity())
        );
        expandableListView.setAdapter(expandableListAdapter);


        isAccessibleTrip = Configurations.getBoolean("enabled_search_mode_accessible_routing", false);
        accessibleSwitch.setChecked(isAccessibleTrip);

        isAvoidWalking = Configurations.getBoolean("enabled_search_mode_avoid_walking", false);
        switchAvoidWalking.setChecked(isAvoidWalking);

        initWalkSpeedSpinner();

        updateSearchOptionsSpinnerStatus(searchOptionsSpinnerIndex, false);
        tempSearchOptionsSpinnerIndex = searchOptionsSpinnerIndex;

        spSearchOptions.setOnItemSelectedListener(itemSelectedListener);
        spSearchOptionsAdvanced.setOnItemSelectedListener(itemSelectedListener);

//                    //refresh spinner
        spSearchOptions.setSelection(currentAdvancedSpinnerPosition);
        spSearchOptionsAdvanced.setSelection(currentAdvancedSpinnerPosition);

//                    //refresh time
        switchToDefaultTimePicker(true);

        //update check status
        List<ParentItem> parentItems = Utilities.getExpandableListItems(requireActivity());
        for (int i = 0; i < parentItems.size(); i++) {
            ParentItem pItem = parentItems.get(i);
            pItem.setCheckedTemp(pItem.isCheck());
            for (ChildItem c : pItem.getChildren()) {
                c.setCheckedTemp(c.isChecked());
            }
        }

    }

    private void switchToDefaultTimePicker(boolean isReset) {
        llDepartAtDateTime = mFragmentView.findViewById(R.id.ll_depart_after_date_time_advance);
        llArriveByDateTime = mFragmentView.findViewById(R.id.ll_arrive_by_date_time_advance);

        rlOptionTime = mFragmentView.findViewById(R.id.rl_option_time_advance);
        rlOptionDate = mFragmentView.findViewById(R.id.rl_option_date_advance);

        tvDepartAtOptionTime = mFragmentView.findViewById(R.id.tv_option_time_advance);
        tvDepartAtOptionDate = mFragmentView.findViewById(R.id.tv_option_date_advance);

        rlArriveByAtOptionTime = mFragmentView.findViewById(R.id.rl_arrive_by_option_time_advance);
        rlArriveByOptionDate = mFragmentView.findViewById(R.id.rl_arrive_by_option_date_advance);
        tvArriveByOptionTime = mFragmentView.findViewById(R.id.tv_arrive_by_option_time_advance);
        tvArriveByOptionDate = mFragmentView.findViewById(R.id.tv_arrive_by_option_date_advance);
        if (!isReset) {
            currentTimePickerHour = timePickerHourTmp;
            currentTimePickerMin = timePickerMinTmp;

            currentDatePickerYear = datePickerYearTmp;
            currentDatePickerMonth = datePickerMonthTmp;
            currentDatePickerDayOfMonth = datePickerDayOfMonthTmp;

            isLeaveNow = isLeaveNowTmp;
            isDepartAt = isDepartAtTmp;
            isArriveBy = isArriveByTmp;

        } else {
            if (timePickerDialog != null) {
                timePickerDialog.updateTime(currentTimePickerHour, currentTimePickerMin);
                timePickerHourTmp = currentTimePickerHour;
                timePickerMinTmp = currentTimePickerMin;
            }
            if (datePickerDialog != null && datePickerDialog.getDatePicker() != null) {
                datePickerDialog.getDatePicker().updateDate(currentDatePickerYear, currentDatePickerMonth, currentDatePickerDayOfMonth);
                datePickerYearTmp = currentDatePickerYear;
                datePickerMonthTmp = currentDatePickerMonth;
                datePickerDayOfMonthTmp = currentDatePickerDayOfMonth;
            }
        }

        refreshSearchDate();
        refreshSearchTime();
    }

    private void switchToAdvancedTimePicker() {
        llDepartAtDateTime = mFragmentView.findViewById(R.id.ll_depart_after_date_time_advance_pref);
        llArriveByDateTime = mFragmentView.findViewById(R.id.ll_arrive_by_date_time_advance_pref);

        rlOptionTime = mFragmentView.findViewById(R.id.rl_option_time_advance_pref);
        rlOptionDate = mFragmentView.findViewById(R.id.rl_option_date_advance_pref);

        tvDepartAtOptionTime = mFragmentView.findViewById(R.id.tv_option_time_advance_pref);
        tvDepartAtOptionDate = mFragmentView.findViewById(R.id.tv_option_date_advance_pref);

        rlArriveByAtOptionTime = mFragmentView.findViewById(R.id.rl_arrive_by_option_time_advance_pref);
        rlArriveByOptionDate = mFragmentView.findViewById(R.id.rl_arrive_by_option_date_advance_pref);
        tvArriveByOptionTime = mFragmentView.findViewById(R.id.tv_arrive_by_option_time_advance_pref);
        tvArriveByOptionDate = mFragmentView.findViewById(R.id.tv_arrive_by_option_date_advance_pref);
    }

    private void setSearchOptionsItemSelectedListener(String[] items) {
        itemSelectedListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                updateSearchOptionsSpinnerStatus(position, true);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        };
    }

    private void speakDescription(String text) {
        if (text == null || text.isEmpty()) return;
        AccessibilityManager accessibilityManager = (AccessibilityManager) requireContext().getSystemService(Context.ACCESSIBILITY_SERVICE);
        if (accessibilityManager != null && accessibilityManager.isEnabled()) {
            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    AccessibilityEvent event = AccessibilityEvent.obtain(AccessibilityEvent.TYPE_ANNOUNCEMENT);
                    event.getText().add(text);
                    event.setPackageName(requireContext().getPackageName());
                    event.setClassName(getClass().getName());
                    accessibilityManager.sendAccessibilityEvent(event);
                }
            }, 500);
        }
    }

    private void updateSearchOptionsSpinnerStatus(int position, boolean isUpdateFromClick) {
        String[] items = getResources().getStringArray(R.array.search_options);
        if (items == null) {
            return;
        }

        if (isUpdateFromClick) {
            tempSearchOptionsSpinnerIndex = position;
        } else {
            // Update search options spinner
            spSearchOptions.setSelection(searchOptionsSpinnerIndex);
            spSearchOptionsAdvanced.setSelection(searchOptionsSpinnerIndex);
        }

        adapterSearchState.notifyDataSetChanged();

        String selectedItem = items[position];
        hideLayerPopup();

        if (position == 0) {
            invisibleView.setVisibility(View.GONE);
        } else {

            invisibleView.setVisibility(View.INVISIBLE);
            ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) invisibleView.getLayoutParams();

            if (position == 1) {
                layoutParams.topToBottom = R.id.ll_arrive_by_date_time_advance_pref;
            } else {
                layoutParams.topToBottom = R.id.ll_depart_after_date_time_advance_pref;
            }

            invisibleView.setLayoutParams(layoutParams);

            speakDescription(getResources().getString(R.string.select_time_date));
        }

        // Get the localised string array from resources
        String[] searchOptions = getResources().getStringArray(R.array.search_options);

        // Get the localised strings for comparison
        String leaveNow = searchOptions[0]; // "Leave now"
        String arriveBy = searchOptions[1]; // "Arrive by"
        String departAfter = searchOptions[2]; // "Depart after"

        if (selectedItem.equalsIgnoreCase(leaveNow)) {
            invisibleView.setVisibility(View.GONE);

            llDepartAtDateTime.setVisibility(View.GONE);
            llArriveByDateTime.setVisibility(View.GONE);

            isLeaveNowTmp = true;
            isDepartAtTmp = false;
            isArriveByTmp = false;

            resetTimeAndDatePicker();

        } else if (selectedItem.equalsIgnoreCase(arriveBy)) {

            llDepartAtDateTime.setVisibility(View.GONE);
            llArriveByDateTime.setVisibility(View.VISIBLE);

            isLeaveNowTmp = false;
            isDepartAtTmp = false;
            isArriveByTmp = true;

        } else if (selectedItem.equalsIgnoreCase(departAfter)) {

            llDepartAtDateTime.setVisibility(View.VISIBLE);
            llArriveByDateTime.setVisibility(View.GONE);

            isLeaveNowTmp = false;
            isDepartAtTmp = true;
            isArriveByTmp = false;
        }
    }

    private void getDependantMobilityProfiles() {
        UserPreferences userPreferences = MyApplication.getInstance().userPreferences;
        if (userPreferences != null && userPreferences.getDependents() != null
                && userPreferences.getDependents().size() > 0) {
            Response.Listener listener = new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Gson gson = new Gson();
                    try {
                        MyApplication.getInstance().dependentMobilityProfiles = gson.fromJson(response, new TypeToken<ArrayList<DependentMobilityProfile>>() {
                        }.getType());

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    mobilityProfileStringArrays.clear();
                    if (MyApplication.getInstance().dependentMobilityProfiles != null
                            && MyApplication.getInstance().dependentMobilityProfiles.size() > 0) {
                        mobilityProfileStringArrays.add(getString(R.string.myself));
                        for (DependentMobilityProfile dependentMobilityProfile : MyApplication.getInstance().dependentMobilityProfiles) {
                            if (dependentMobilityProfile != null && dependentMobilityProfile.getEmail() != null) {
                                mobilityProfileStringArrays.add(dependentMobilityProfile.getEmail());
                            }
                        }
                    }
                    reloadMobilityProfileSpinner();
                }
            };
            Response.ErrorListener errorListener = new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Logger.e("getDependantMobilityProfiles", error.toString());
                }
            };

            String url = Resource.API_ACCOUNT_DEPENDANT_MOBILITY_PROFILES + "?dependentuserids=" + String.join(",", userPreferences.getDependents());

            volleyAccountStringRequestExt(Request.Method.GET, url, "", listener, errorListener);
        }
    }

    private void setupMobilityProfileLayouts() {
        llMobilityProfile = mFragmentView.findViewById(R.id.ll_mobility_profile);
        spMobilityProfile = mFragmentView.findViewById(R.id.sp_mobility_profile);
    }

    private void reloadMobilityProfileSpinner() {
        if (mobilityProfileStringArrays != null && mobilityProfileStringArrays.size() > 0) {
            llMobilityProfile.setVisibility(View.VISIBLE);
            int selectionIndex = 0;
            selectedDependantEmail = Configurations.getString("selected_dependant_email", "");

            if (selectedDependantEmail != null && !selectedDependantEmail.isEmpty()) {
                for (int i = 0; i < mobilityProfileStringArrays.size(); i++) {
                    String id = mobilityProfileStringArrays.get(i);
                    if (selectedDependantEmail.equalsIgnoreCase(id)) {
                        selectionIndex = i;
                        break;
                    }
                }
            }
            String[] stringArrays = mobilityProfileStringArrays.toArray(new String[0]);

            spMobilityProfileAdapter = new SpinnerAdapter(systemContext, R.layout.item_spinner,
                    R.id.tv_option, stringArrays);
            spMobilityProfileAdapter.setDropDownViewResource(android.R.layout.simple_list_item_checked);
            spMobilityProfile.setAdapter(spMobilityProfileAdapter);
            spMobilityProfile.setSelection(selectionIndex);
            spMobilityProfile.setGravity(Gravity.END | Gravity.CENTER_VERTICAL);
            spMobilityProfileAdapter.setCurrentSelection(selectionIndex);
            spMobilityProfile.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    spMobilityProfileAdapter.setCurrentSelection(position);
                    if (mobilityProfileStringArrays != null && mobilityProfileStringArrays.size() > 0
                            && position < mobilityProfileStringArrays.size() && position > 0) {
                        Configurations.setString("selected_dependant_email", mobilityProfileStringArrays.get(position));
                    } else {
                        Configurations.setString("selected_dependant_email", "");
                    }

                    try {
                        //hide the
                        ImageView ivChecked = parent.findViewById(R.id.iv_checked);

                        if (ivChecked != null) {
                            ivChecked.setVisibility(View.GONE);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    refreshSettingsChangedIcon();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        } else {
            llMobilityProfile.setVisibility(View.GONE);
        }
    }

    private void setupSettings() {
        setupMobilityProfileLayouts();
        tvHomeSettingOK = mFragmentView.findViewById(R.id.tv_home_setting_ok);
        ivCloseHomeSetting = mFragmentView.findViewById(R.id.iv_close_home_setting);
        rlHomeSetting = mFragmentView.findViewById(R.id.rl_home_setting);
        maxWalkSpinner = mFragmentView.findViewById(R.id.sp_maximum_walk);
        walkSpeedSpinner = mFragmentView.findViewById(R.id.sp_walk_speed_pref);
        optimizeOptionSpinner = mFragmentView.findViewById(R.id.sp_optimize_option);

        ivCloseHomeSettingAccessibility = mFragmentView.findViewById(R.id.iv_close_home_setting_accessibility);

        rlHomeSettingAccessibility = mFragmentView.findViewById(R.id.rl_home_setting_accessibility);
        llHomeSettingWalkSpeedAccessibility = mFragmentView.findViewById(R.id.ll_home_setting_walk_speed_accessibility);
        tvHomeSettingWalkSpeedAccessibility = mFragmentView.findViewById(R.id.tv_home_setting_walk_speed_accessibility);
        llHomeSettingWalkSpeedAccessibilityPage = mFragmentView.findViewById(R.id.ll_home_setting_walk_speed_accessibility_page);
        llHomeSettingWalkSpeedAccessibilityBack = mFragmentView.findViewById(R.id.ll_home_setting_walk_speed_accessibility_back);
        llHomeSettingWalkSpeedAccessibilityOptions = mFragmentView.findViewById(R.id.ll_home_setting_walk_speed_accessibility_options);
        if (llHomeSettingWalkSpeedAccessibility != null) {
            llHomeSettingWalkSpeedAccessibility.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (llHomeSettingWalkSpeedAccessibilityPage != null) {
                        llHomeSettingWalkSpeedAccessibilityPage.setVisibility(View.VISIBLE);
                        refreshWalkSpeedAccessibilityOptions();
                    }
                }
            });
        }
        if (llHomeSettingWalkSpeedAccessibilityBack != null) {
            llHomeSettingWalkSpeedAccessibilityBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (llHomeSettingWalkSpeedAccessibilityPage != null) {
                        llHomeSettingWalkSpeedAccessibilityPage.setVisibility(View.GONE);
                    }
                }
            });
        }
        addWalkSpeedAccessibilityOptionsAsNeeded();

        ivHomeSettingBusAccessibility = mFragmentView.findViewById(R.id.iv_home_setting_bus_active);
        ivHomeSettingLinkAccessibility = mFragmentView.findViewById(R.id.iv_home_setting_link_active);
        rlHomeSettingBusAccessibility = mFragmentView.findViewById(R.id.rl_home_setting_bus_accessibility);
        rlHomeSettingLinkAccessibility = mFragmentView.findViewById(R.id.rl_home_settings_link_accessibility);
        ivHomeSettingStreetCarAccessibility = mFragmentView.findViewById(R.id.iv_home_setting_atl_street_car_active);
        ivHomeSettingMartaRailAcessibility = mFragmentView.findViewById(R.id.iv_home_setting_marta_rail_active);
        ivHomeSettingFerryAccessibility = mFragmentView.findViewById(R.id.iv_home_setting_ferry_active);
        ivHomeSettingWaterTaxiAccessibility = mFragmentView.findViewById(R.id.iv_home_setting_water_taxi_active);
        ivHomeSettingMonorailAccessibility = mFragmentView.findViewById(R.id.iv_home_setting_monorail_active);
        rlHomeSettingFerryAccessibility = mFragmentView.findViewById(R.id.rl_home_setting_ferry_accessibility);


        ivHomeSettingBusAccessibilityNew = mFragmentView.findViewById(R.id.iv_home_setting_bus_accessibility);
        ivHomeSettingLinkAccessibilityNew = mFragmentView.findViewById(R.id.iv_home_setting_link_accessibility);
        ivHomeSettingStreetCarAccessibilityNew = mFragmentView.findViewById(R.id.iv_home_setting_atl_street_car_accessibility);
        ivHomeSettingMartaRailAcessibilityNew = mFragmentView.findViewById(R.id.iv_home_setting_marta_rail_accessibility);
        ivHomeSettingFerryAccessibilityNew = mFragmentView.findViewById(R.id.iv_home_setting_ferry_accessibility);
        ivHomeSettingWaterTaxiAccessibilityNew = mFragmentView.findViewById(R.id.iv_home_setting_water_taxi_accessibility);
        ivHomeSettingMonorailAccessibilityNew = mFragmentView.findViewById(R.id.iv_home_setting_monorail_accessibility);

        tvRestoreMyDefaults = mFragmentView.findViewById(R.id.tv_restore_my_defaults);
        llHomeSettingBus = mFragmentView.findViewById(R.id.ll_home_setting_bus);
        llHomeSettingMartaRail = mFragmentView.findViewById(R.id.ll_home_setting_marta_rail);
        llHomeSettingStreetCar = mFragmentView.findViewById(R.id.ll_home_setting_atl_street_car);
        llHomeSettingFerry = mFragmentView.findViewById(R.id.ll_home_setting_ferry);
        llHomeSettingGondola = mFragmentView.findViewById(R.id.ll_home_setting_gondola);
        llHomeSettingLink = mFragmentView.findViewById(R.id.ll_home_setting_link);
        llHomeSettingMonorail = mFragmentView.findViewById(R.id.ll_home_setting_monorail);
        llHomeSettingWaterTaxi = mFragmentView.findViewById(R.id.ll_home_setting_water_taxi);
        tvHomeSettingBus = mFragmentView.findViewById(R.id.tv_home_setting_bus);
        tvHomeSettingMartaRail = mFragmentView.findViewById(R.id.tv_home_setting_marta_rail);
        tvHomeSettingStreetCar = mFragmentView.findViewById(R.id.tv_home_setting_atl_street_car);
        tvHomeSettingFerry = mFragmentView.findViewById(R.id.tv_home_setting_ferry);
        tvHomeSettingGondola = mFragmentView.findViewById(R.id.tv_home_setting_gondola);
        tvHomeSettingLink = mFragmentView.findViewById(R.id.tv_home_setting_link);
        tvHomeSettingWaterTaxi = mFragmentView.findViewById(R.id.tv_home_setting_water_taxi);
        tvHomeSettingMonorail = mFragmentView.findViewById(R.id.tv_home_setting_monorail);
        ivHomeSettingBus = mFragmentView.findViewById(R.id.iv_home_setting_bus);
        ivHomeSettingMartaRail = mFragmentView.findViewById(R.id.iv_home_setting_marta_rail);
        ivHomeSettingStreetCar = mFragmentView.findViewById(R.id.iv_home_setting_atl_street_car);
        ivHomeSettingFerry = mFragmentView.findViewById(R.id.iv_home_setting_ferry);
        ivHomeSettingGondola = mFragmentView.findViewById(R.id.iv_home_setting_gondola);
        ivHomeSettingLink = mFragmentView.findViewById(R.id.iv_home_setting_link);
        ivHomeSettingWaterTaxi = mFragmentView.findViewById(R.id.iv_home_setting_water_taxi);
        ivHomeSettingMonorail = mFragmentView.findViewById(R.id.iv_home_setting_monorail);
        ivHomeSettingAvoidWalking = mFragmentView.findViewById(R.id.iv_avoid_walking);
        ivHomeSettingAccessibileRouting = mFragmentView.findViewById(R.id.iv_accessible_routing);
        ivHomeSettingAllowBikeRental = mFragmentView.findViewById(R.id.iv_allow_bike_rental);
        llHomeSettingAllowBikeRental = mFragmentView.findViewById(R.id.ll_allow_bike_rental);

        ivHomeSettingAllowScooterRental = mFragmentView.findViewById(R.id.iv_allow_scooter_rental);
        llHomeSettingAllowScooterRental = mFragmentView.findViewById(R.id.ll_allow_scooter_rental);
        tvHomeSettingAllowScooterRentalAccessibility = mFragmentView.findViewById(R.id.tv_allow_scooter_rental_accessibility);
        llHomeSettingAllowScooterRentalAccessibility = mFragmentView.findViewById(R.id.ll_allow_scooter_rental_accessibility);

        llHomeSettingBusAccessibility = mFragmentView.findViewById(R.id.ll_home_setting_bus_accessibility);
        llHomeSettingMartaRailAccessibility = mFragmentView.findViewById(R.id.ll_home_setting_marta_rail_accessibility);
        llHomeSettingStreetCarAccessibility = mFragmentView.findViewById(R.id.ll_home_setting_atl_street_car_accessibility);
        llHomeSettingFerryAccessibility = mFragmentView.findViewById(R.id.ll_home_setting_ferry_accessibility);
        llHomeSettingLinkAccessibility = mFragmentView.findViewById(R.id.ll_home_setting_link_accessibility);
        llHomeSettingWaterTaxiAccessibility = mFragmentView.findViewById(R.id.ll_home_setting_water_taxi_accessibility);
        llHomeSettingMonorailAccessibility = mFragmentView.findViewById(R.id.ll_home_setting_monorail_accessibility);
        llHomeSettingAvoidWalkingAccessibility = mFragmentView.findViewById(R.id.ll_home_setting_Avoid_walking_accessibility);
        llHomeSettingAccessibleRoutingAccessibility = mFragmentView.findViewById(R.id.ll_home_setting_accessible_routing_accessibility);
        llHomeSettingAllowBikeRentalAccessibility = mFragmentView.findViewById(R.id.ll_allow_bike_rental_accessibility);


        tvHomeSettingBusAccessibility = mFragmentView.findViewById(R.id.tv_home_setting_bus_accessibility);
        tvHomeSettingMartaRailAccessibility = mFragmentView.findViewById(R.id.tv_home_setting_marta_rail_accessibility);
        tvHomeSettingStreetCarAccessibility = mFragmentView.findViewById(R.id.tv_home_setting_atl_street_car_accessibility);
        tvHomeSettingFerryAccessibility = mFragmentView.findViewById(R.id.tv_home_setting_ferry_accessibility);
        tvHomeSettingLinkAccessibility = mFragmentView.findViewById(R.id.tv_home_setting_link_accessibility);
        tvHomeSettingWaterTaxiAccessibility = mFragmentView.findViewById(R.id.tv_home_setting_water_taxi_accessibility);
        tvHomeSettingMonorailAccessibility = mFragmentView.findViewById(R.id.tv_home_setting_monorail_accessibility);
        tvHomeSettingAvoidWalkingAccessibility = mFragmentView.findViewById(R.id.tv_avoid_walking_accessibility);
        tvHomeSettingAccessibileRoutingAccessibility = mFragmentView.findViewById(R.id.tv_accessible_routing_accessibility);
        tvHomeSettingAllowBikeRentalAccessibility = mFragmentView.findViewById(R.id.tv_allow_bike_rental_accessibility);

        tvHomeSettingOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideHomeSetting();
            }
        });

        ivCloseHomeSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideHomeSetting();
            }
        });

        ivCloseHomeSettingAccessibility.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideHomeSetting();
            }
        });

        tvRestoreMyDefaults.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utilities.resetItinerarySearchSettings();

                initSettings();
            }
        });

        ivHomeSettingAvoidWalking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isEnabled = Configurations.getBoolean("enabled_search_mode_avoid_walking", true);

                Configurations.setBoolean("enabled_search_mode_avoid_walking", !isEnabled);
                refreshHomeSettingAvoidWalking();
                refreshSettingsChangedIcon();
            }
        });


        llHomeSettingAvoidWalkingAccessibility.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isEnabled = Configurations.getBoolean("enabled_search_mode_avoid_walking", true);

                Configurations.setBoolean("enabled_search_mode_avoid_walking", !isEnabled);
                refreshHomeSettingAvoidWalking();
                refreshSettingsChangedIcon();
            }
        });


        ivHomeSettingAccessibileRouting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isEnabled = Configurations.getBoolean("enabled_search_mode_accessible_routing", true);

                if (isEnabled) {
                    Configurations.setBoolean("enabled_search_mode_accessible_routing", false);
                    if (llAccessibilitylegend != null) {
                        llAccessibilitylegend.setVisibility(View.GONE);
                    }
                } else {
                    Configurations.setBoolean("enabled_search_mode_accessible_routing", true);
                    if (llAccessibilitylegend != null) {
                        llAccessibilitylegend.setVisibility(View.VISIBLE);
                    }

                }
                refreshHomeSettingAccessibleRouting();
                refreshSettingsChangedIcon();
            }
        });


        llHomeSettingAccessibleRoutingAccessibility.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isEnabled = Configurations.getBoolean("enabled_search_mode_accessible_routing", true);

                if (isEnabled) {
                    Configurations.setBoolean("enabled_search_mode_accessible_routing", false);
                    if (llAccessibilitylegend != null) {
                        llAccessibilitylegend.setVisibility(View.GONE);
                    }
                } else {
                    Configurations.setBoolean("enabled_search_mode_accessible_routing", true);
                    if (llAccessibilitylegend != null) {
                        llAccessibilitylegend.setVisibility(View.VISIBLE);
                    }
                }
                refreshHomeSettingAccessibleRouting();
                refreshSettingsChangedIcon();
            }
        });

        ivHomeSettingAllowBikeRental.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isEnabled = Configurations.getBoolean("enabled_search_mode_bike_rental", true);

                Configurations.setBoolean("enabled_search_mode_bike_rental", !isEnabled);
                refreshHomeSettingAllowBikeRental();
                refreshSettingsChangedIcon();
            }
        });


        llHomeSettingAllowBikeRentalAccessibility.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isEnabled = Configurations.getBoolean("enabled_search_mode_bike_rental", true);

                Configurations.setBoolean("enabled_search_mode_bike_rental", !isEnabled);
                refreshHomeSettingAllowBikeRental();
                refreshSettingsChangedIcon();
            }
        });

        ivHomeSettingAllowScooterRental.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isEnabled = Configurations.getBoolean("enabled_search_mode_scooter_rental", true);

                Configurations.setBoolean("enabled_search_mode_scooter_rental", !isEnabled);
                refreshHomeSettingAllowScooterRental();
                refreshSettingsChangedIcon();
            }
        });


        llHomeSettingAllowScooterRentalAccessibility.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isEnabled = Configurations.getBoolean("enabled_search_mode_scooter_rental", true);

                Configurations.setBoolean("enabled_search_mode_scooter_rental", !isEnabled);
                refreshHomeSettingAllowScooterRental();
                refreshSettingsChangedIcon();
            }
        });


        llHomeSettingBus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isEnabled = Configurations.getBoolean("home_setting_bus_enabled", true);

                if (isEnabled) {
                    if (Configurations.getBoolean("home_setting_street_car_enabled", true) ||
                            Configurations.getBoolean("home_setting_rail_enabled", true) ||
                            Configurations.getBoolean("home_setting_link_enabled", true) ||
                            Configurations.getBoolean("home_setting_water_taxi_enabled", true) ||
                            Configurations.getBoolean("home_setting_monorail_enabled", true)
                            || (llHomeSettingFerry.getVisibility() == View.VISIBLE
                            && Configurations.getBoolean("home_setting_ferry_enabled", true))
                            || (rlHomeSettingFerryAccessibility.getVisibility() == View.VISIBLE
                            && Configurations.getBoolean("home_setting_ferry_enabled", true))
                            || (llHomeSettingGondola.getVisibility() == View.VISIBLE
                            && Configurations.getBoolean("home_setting_gondola_enabled", true))) {
                        Configurations.setBoolean("home_setting_bus_enabled", false);
                    }
                } else {
                    Configurations.setBoolean("home_setting_bus_enabled", true);
                }
                refreshHomeSettingBus();

                refreshSettingsChangedIcon();
            }
        });


        llHomeSettingBusAccessibility.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isEnabled = Configurations.getBoolean("home_setting_bus_enabled", true);

                if (isEnabled) {
                    if (Configurations.getBoolean("home_setting_street_car_enabled", true) ||
                            Configurations.getBoolean("home_setting_rail_enabled", true) ||
                            Configurations.getBoolean("home_setting_link_enabled", true) ||
                            Configurations.getBoolean("home_setting_water_taxi_enabled", true) ||
                            Configurations.getBoolean("home_setting_monorail_enabled", true)
                            || (llHomeSettingFerry.getVisibility() == View.VISIBLE
                            && Configurations.getBoolean("home_setting_ferry_enabled", true))
                            || (rlHomeSettingFerryAccessibility.getVisibility() == View.VISIBLE
                            && Configurations.getBoolean("home_setting_ferry_enabled", true))
                            || (llHomeSettingGondola.getVisibility() == View.VISIBLE
                            && Configurations.getBoolean("home_setting_gondola_enabled", true))) {
                        Configurations.setBoolean("home_setting_bus_enabled", false);
                    }
                } else {
                    Configurations.setBoolean("home_setting_bus_enabled", true);
                }
                refreshHomeSettingBus();

                refreshSettingsChangedIcon();
            }
        });


        ivHomeSettingBusAccessibility.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isEnabled = Configurations.getBoolean("home_setting_bus_enabled", true);

                if (isEnabled) {
                    if (Configurations.getBoolean("home_setting_street_car_enabled", true) ||
                            Configurations.getBoolean("home_setting_rail_enabled", true) ||
                            Configurations.getBoolean("home_setting_link_enabled", true) ||
                            Configurations.getBoolean("home_setting_water_taxi_enabled", true) ||
                            Configurations.getBoolean("home_setting_monorail_enabled", true)
                            || (llHomeSettingFerry.getVisibility() == View.VISIBLE
                            && Configurations.getBoolean("home_setting_ferry_enabled", true))
                            || (rlHomeSettingFerryAccessibility.getVisibility() == View.VISIBLE
                            && Configurations.getBoolean("home_setting_ferry_enabled", true))
                            || (llHomeSettingGondola.getVisibility() == View.VISIBLE
                            && Configurations.getBoolean("home_setting_gondola_enabled", true))) {
                        Configurations.setBoolean("home_setting_bus_enabled", false);
                    }
                } else {
                    Configurations.setBoolean("home_setting_bus_enabled", true);
                }
                refreshHomeSettingBus();

                refreshSettingsChangedIcon();
            }
        });


        llHomeSettingMartaRail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isEnabled = Configurations.getBoolean("home_setting_rail_enabled", true);

                if (isEnabled) {
                    if (Configurations.getBoolean("home_setting_street_car_enabled", true) ||
                            Configurations.getBoolean("home_setting_bus_enabled", true) ||
                            Configurations.getBoolean("home_setting_link_enabled", true) ||
                            Configurations.getBoolean("home_setting_water_taxi_enabled", true) ||
                            Configurations.getBoolean("home_setting_monorail_enabled", true)
                            || (llHomeSettingFerry.getVisibility() == View.VISIBLE
                            && Configurations.getBoolean("home_setting_ferry_enabled", true))
                            || (rlHomeSettingFerryAccessibility.getVisibility() == View.VISIBLE
                            && Configurations.getBoolean("home_setting_ferry_enabled", true))
                            || (llHomeSettingGondola.getVisibility() == View.VISIBLE
                            && Configurations.getBoolean("home_setting_gondola_enabled", true))) {
                        Configurations.setBoolean("home_setting_rail_enabled", false);
                    }
                } else {
                    Configurations.setBoolean("home_setting_rail_enabled", true);
                }
                refreshHomeSettingRail();
                refreshSettingsChangedIcon();
            }
        });


        llHomeSettingMartaRailAccessibility.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isEnabled = Configurations.getBoolean("home_setting_rail_enabled", true);

                if (isEnabled) {
                    if (Configurations.getBoolean("home_setting_street_car_enabled", true) ||
                            Configurations.getBoolean("home_setting_bus_enabled", true) ||
                            Configurations.getBoolean("home_setting_link_enabled", true) ||
                            Configurations.getBoolean("home_setting_water_taxi_enabled", true) ||
                            Configurations.getBoolean("home_setting_monorail_enabled", true)
                            || (llHomeSettingFerry.getVisibility() == View.VISIBLE
                            && Configurations.getBoolean("home_setting_ferry_enabled", true))
                            || (rlHomeSettingFerryAccessibility.getVisibility() == View.VISIBLE
                            && Configurations.getBoolean("home_setting_ferry_enabled", true))
                            || (llHomeSettingGondola.getVisibility() == View.VISIBLE
                            && Configurations.getBoolean("home_setting_gondola_enabled", true))) {
                        Configurations.setBoolean("home_setting_rail_enabled", false);
                    }
                } else {
                    Configurations.setBoolean("home_setting_rail_enabled", true);
                }
                refreshHomeSettingRail();
                refreshSettingsChangedIcon();
            }
        });


        ivHomeSettingMartaRailAcessibility.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isEnabled = Configurations.getBoolean("home_setting_rail_enabled", true);

                if (isEnabled) {
                    if (Configurations.getBoolean("home_setting_street_car_enabled", true) ||
                            Configurations.getBoolean("home_setting_bus_enabled", true) ||
                            Configurations.getBoolean("home_setting_link_enabled", true) ||
                            Configurations.getBoolean("home_setting_water_taxi_enabled", true) ||
                            Configurations.getBoolean("home_setting_monorail_enabled", true)
                            || (llHomeSettingFerry.getVisibility() == View.VISIBLE
                            && Configurations.getBoolean("home_setting_ferry_enabled", true))
                            || (rlHomeSettingFerryAccessibility.getVisibility() == View.VISIBLE
                            && Configurations.getBoolean("home_setting_ferry_enabled", true))
                            || (llHomeSettingGondola.getVisibility() == View.VISIBLE
                            && Configurations.getBoolean("home_setting_gondola_enabled", true))) {
                        Configurations.setBoolean("home_setting_rail_enabled", false);
                    }
                } else {
                    Configurations.setBoolean("home_setting_rail_enabled", true);
                }
                refreshHomeSettingRail();
                refreshSettingsChangedIcon();
            }
        });

        llHomeSettingStreetCar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isEnabled = Configurations.getBoolean("home_setting_street_car_enabled", true);

                if (isEnabled) {
                    if (Configurations.getBoolean("home_setting_bus_enabled", true) ||
                            Configurations.getBoolean("home_setting_rail_enabled", true) ||
                            Configurations.getBoolean("home_setting_link_enabled", true) ||
                            Configurations.getBoolean("home_setting_water_taxi_enabled", true) ||
                            Configurations.getBoolean("home_setting_monorail_enabled", true)
                            || (llHomeSettingFerry.getVisibility() == View.VISIBLE
                            && Configurations.getBoolean("home_setting_ferry_enabled", true))
                            || (rlHomeSettingFerryAccessibility.getVisibility() == View.VISIBLE
                            && Configurations.getBoolean("home_setting_ferry_enabled", true))
                            || (llHomeSettingGondola.getVisibility() == View.VISIBLE
                            && Configurations.getBoolean("home_setting_gondola_enabled", true))) {
                        Configurations.setBoolean("home_setting_street_car_enabled", false);
                    }
                } else {
                    Configurations.setBoolean("home_setting_street_car_enabled", true);
                }
                refreshHomeSettingStreetCar();
                refreshSettingsChangedIcon();
            }
        });


        llHomeSettingStreetCarAccessibility.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isEnabled = Configurations.getBoolean("home_setting_street_car_enabled", true);

                if (isEnabled) {
                    if (Configurations.getBoolean("home_setting_bus_enabled", true) ||
                            Configurations.getBoolean("home_setting_rail_enabled", true) ||
                            Configurations.getBoolean("home_setting_link_enabled", true) ||
                            Configurations.getBoolean("home_setting_water_taxi_enabled", true) ||
                            Configurations.getBoolean("home_setting_monorail_enabled", true)
                            || (llHomeSettingFerry.getVisibility() == View.VISIBLE
                            && Configurations.getBoolean("home_setting_ferry_enabled", true))
                            || (rlHomeSettingFerryAccessibility.getVisibility() == View.VISIBLE
                            && Configurations.getBoolean("home_setting_ferry_enabled", true))
                            || (llHomeSettingGondola.getVisibility() == View.VISIBLE
                            && Configurations.getBoolean("home_setting_gondola_enabled", true))) {
                        Configurations.setBoolean("home_setting_street_car_enabled", false);
                    }
                } else {
                    Configurations.setBoolean("home_setting_street_car_enabled", true);
                }
                refreshHomeSettingStreetCar();
                refreshSettingsChangedIcon();
            }
        });


        ivHomeSettingStreetCarAccessibility.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isEnabled = Configurations.getBoolean("home_setting_street_car_enabled", true);

                if (isEnabled) {
                    if (Configurations.getBoolean("home_setting_bus_enabled", true) ||
                            Configurations.getBoolean("home_setting_rail_enabled", true) ||
                            Configurations.getBoolean("home_setting_link_enabled", true) ||
                            Configurations.getBoolean("home_setting_water_taxi_enabled", true) ||
                            Configurations.getBoolean("home_setting_monorail_enabled", true)
                            || (llHomeSettingFerry.getVisibility() == View.VISIBLE
                            && Configurations.getBoolean("home_setting_ferry_enabled", true))
                            || (rlHomeSettingFerryAccessibility.getVisibility() == View.VISIBLE
                            && Configurations.getBoolean("home_setting_ferry_enabled", true))
                            || (llHomeSettingGondola.getVisibility() == View.VISIBLE
                            && Configurations.getBoolean("home_setting_gondola_enabled", true))) {
                        Configurations.setBoolean("home_setting_street_car_enabled", false);
                    }
                } else {
                    Configurations.setBoolean("home_setting_street_car_enabled", true);
                }
                refreshHomeSettingStreetCar();
                refreshSettingsChangedIcon();
            }
        });

        llHomeSettingFerry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isEnabled = Configurations.getBoolean("home_setting_ferry_enabled", true);

                if (isEnabled) {
                    if (Configurations.getBoolean("home_setting_street_car_enabled", true) ||
                            Configurations.getBoolean("home_setting_rail_enabled", true) ||
                            Configurations.getBoolean("home_setting_bus_enabled", true) ||
                            Configurations.getBoolean("home_setting_link_enabled", true) ||
                            Configurations.getBoolean("home_setting_water_taxi_enabled", true) ||
                            Configurations.getBoolean("home_setting_monorail_enabled", true)
                            || (llHomeSettingGondola.getVisibility() == View.VISIBLE
                            && Configurations.getBoolean("home_setting_gondola_enabled", true))) {
                        Configurations.setBoolean("home_setting_ferry_enabled", false);
                    }
                } else {
                    Configurations.setBoolean("home_setting_ferry_enabled", true);
                }
                refreshHomeSettingFerry();

                refreshSettingsChangedIcon();
            }
        });


        llHomeSettingFerryAccessibility.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isEnabled = Configurations.getBoolean("home_setting_ferry_enabled", true);

                if (isEnabled) {
                    if (Configurations.getBoolean("home_setting_street_car_enabled", true) ||
                            Configurations.getBoolean("home_setting_rail_enabled", true) ||
                            Configurations.getBoolean("home_setting_bus_enabled", true) ||
                            Configurations.getBoolean("home_setting_link_enabled", true) ||
                            Configurations.getBoolean("home_setting_water_taxi_enabled", true) ||
                            Configurations.getBoolean("home_setting_monorail_enabled", true)
                            || (llHomeSettingGondola.getVisibility() == View.VISIBLE
                            && Configurations.getBoolean("home_setting_gondola_enabled", true))) {
                        Configurations.setBoolean("home_setting_ferry_enabled", false);
                    }
                } else {
                    Configurations.setBoolean("home_setting_ferry_enabled", true);
                }
                refreshHomeSettingFerry();

                refreshSettingsChangedIcon();
            }
        });


        ivHomeSettingFerryAccessibility.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isEnabled = Configurations.getBoolean("home_setting_ferry_enabled", true);

                if (isEnabled) {
                    if (Configurations.getBoolean("home_setting_street_car_enabled", true) ||
                            Configurations.getBoolean("home_setting_rail_enabled", true) ||
                            Configurations.getBoolean("home_setting_bus_enabled", true) ||
                            Configurations.getBoolean("home_setting_link_enabled", true) ||
                            Configurations.getBoolean("home_setting_water_taxi_enabled", true) ||
                            Configurations.getBoolean("home_setting_monorail_enabled", true)
                            || (llHomeSettingGondola.getVisibility() == View.VISIBLE
                            && Configurations.getBoolean("home_setting_gondola_enabled", true))) {
                        Configurations.setBoolean("home_setting_ferry_enabled", false);
                    }
                } else {
                    Configurations.setBoolean("home_setting_ferry_enabled", true);
                }
                refreshHomeSettingFerry();

                refreshSettingsChangedIcon();
            }
        });


        llHomeSettingGondola.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isEnabled = Configurations.getBoolean("home_setting_gondola_enabled", true);

                if (isEnabled) {
                    if (Configurations.getBoolean("home_setting_street_car_enabled", true) ||
                            Configurations.getBoolean("home_setting_rail_enabled", true) ||
                            Configurations.getBoolean("home_setting_bus_enabled", true) ||
                            Configurations.getBoolean("home_setting_link_enabled", true) ||
                            Configurations.getBoolean("home_setting_water_taxi_enabled", true) ||
                            Configurations.getBoolean("home_setting_monorail_enabled", true)
                            || (llHomeSettingFerry.getVisibility() == View.VISIBLE
                            && Configurations.getBoolean("home_setting_ferry_enabled", true))
                            || (rlHomeSettingFerryAccessibility.getVisibility() == View.VISIBLE
                            && Configurations.getBoolean("home_setting_ferry_enabled", true))) {
                        Configurations.setBoolean("home_setting_gondola_enabled", false);
                    }
                } else {
                    Configurations.setBoolean("home_setting_gondola_enabled", true);
                }
                refreshHomeSettingGondola();

                refreshSettingsChangedIcon();
            }
        });


        llHomeSettingLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isEnabled = Configurations.getBoolean("home_setting_link_enabled", true);

                if (isEnabled) {
                    if (Configurations.getBoolean("home_setting_street_car_enabled", true) ||
                            Configurations.getBoolean("home_setting_rail_enabled", true) ||
                            Configurations.getBoolean("home_setting_bus_enabled", true) ||
                            Configurations.getBoolean("home_setting_water_taxi_enabled", true) ||
                            Configurations.getBoolean("home_setting_monorail_enabled", true)
                            || (llHomeSettingFerry.getVisibility() == View.VISIBLE
                            && Configurations.getBoolean("home_setting_ferry_enabled", true))
                            || (rlHomeSettingFerryAccessibility.getVisibility() == View.VISIBLE
                            && Configurations.getBoolean("home_setting_ferry_enabled", true))
                            || (llHomeSettingGondola.getVisibility() == View.VISIBLE
                            && Configurations.getBoolean("home_setting_gondola_enabled", true))) {
                        Configurations.setBoolean("home_setting_link_enabled", false);
                    }
                } else {
                    Configurations.setBoolean("home_setting_link_enabled", true);
                }
                refreshHomeSettingLink();

                refreshSettingsChangedIcon();
            }
        });

        llHomeSettingLinkAccessibility.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isEnabled = Configurations.getBoolean("home_setting_link_enabled", true);

                if (isEnabled) {
                    if (Configurations.getBoolean("home_setting_street_car_enabled", true) ||
                            Configurations.getBoolean("home_setting_rail_enabled", true) ||
                            Configurations.getBoolean("home_setting_bus_enabled", true) ||
                            Configurations.getBoolean("home_setting_water_taxi_enabled", true) ||
                            Configurations.getBoolean("home_setting_monorail_enabled", true)
                            || (llHomeSettingFerry.getVisibility() == View.VISIBLE
                            && Configurations.getBoolean("home_setting_ferry_enabled", true))
                            || (rlHomeSettingFerryAccessibility.getVisibility() == View.VISIBLE
                            && Configurations.getBoolean("home_setting_ferry_enabled", true))
                            || (llHomeSettingGondola.getVisibility() == View.VISIBLE
                            && Configurations.getBoolean("home_setting_gondola_enabled", true))) {
                        Configurations.setBoolean("home_setting_link_enabled", false);
                    }
                } else {
                    Configurations.setBoolean("home_setting_link_enabled", true);
                }
                refreshHomeSettingLink();

                refreshSettingsChangedIcon();
            }
        });


        ivHomeSettingLinkAccessibility.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isEnabled = Configurations.getBoolean("home_setting_link_enabled", true);

                if (isEnabled) {
                    if (Configurations.getBoolean("home_setting_street_car_enabled", true) ||
                            Configurations.getBoolean("home_setting_rail_enabled", true) ||
                            Configurations.getBoolean("home_setting_bus_enabled", true) ||
                            Configurations.getBoolean("home_setting_water_taxi_enabled", true) ||
                            Configurations.getBoolean("home_setting_monorail_enabled", true)
                            || (llHomeSettingFerry.getVisibility() == View.VISIBLE
                            && Configurations.getBoolean("home_setting_ferry_enabled", true))
                            || (rlHomeSettingFerryAccessibility.getVisibility() == View.VISIBLE
                            && Configurations.getBoolean("home_setting_ferry_enabled", true))
                            || (llHomeSettingGondola.getVisibility() == View.VISIBLE
                            && Configurations.getBoolean("home_setting_gondola_enabled", true))) {
                        Configurations.setBoolean("home_setting_link_enabled", false);
                    }
                } else {
                    Configurations.setBoolean("home_setting_link_enabled", true);
                }
                refreshHomeSettingLink();

                refreshSettingsChangedIcon();
            }
        });


        llHomeSettingWaterTaxi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isEnabled = Configurations.getBoolean("home_setting_water_taxi_enabled", true);

                if (isEnabled) {
                    if (Configurations.getBoolean("home_setting_street_car_enabled", true) ||
                            Configurations.getBoolean("home_setting_rail_enabled", true) ||
                            Configurations.getBoolean("home_setting_bus_enabled", true) ||
                            Configurations.getBoolean("home_setting_link_enabled", true) ||
                            Configurations.getBoolean("home_setting_monorail_enabled", true)
                            || (llHomeSettingFerry.getVisibility() == View.VISIBLE
                            && Configurations.getBoolean("home_setting_ferry_enabled", true))
                            || (rlHomeSettingFerryAccessibility.getVisibility() == View.VISIBLE
                            && Configurations.getBoolean("home_setting_ferry_enabled", true))
                            || (llHomeSettingGondola.getVisibility() == View.VISIBLE
                            && Configurations.getBoolean("home_setting_gondola_enabled", true))) {
                        Configurations.setBoolean("home_setting_water_taxi_enabled", false);
                    }
                } else {
                    Configurations.setBoolean("home_setting_water_taxi_enabled", true);
                }
                refreshHomeSettingWaterTaxi();

                refreshSettingsChangedIcon();
            }
        });

        llHomeSettingWaterTaxiAccessibility.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isEnabled = Configurations.getBoolean("home_setting_water_taxi_enabled", true);

                if (isEnabled) {
                    if (Configurations.getBoolean("home_setting_street_car_enabled", true) ||
                            Configurations.getBoolean("home_setting_rail_enabled", true) ||
                            Configurations.getBoolean("home_setting_bus_enabled", true) ||
                            Configurations.getBoolean("home_setting_link_enabled", true) ||
                            Configurations.getBoolean("home_setting_monorail_enabled", true)
                            || (llHomeSettingFerry.getVisibility() == View.VISIBLE
                            && Configurations.getBoolean("home_setting_ferry_enabled", true))
                            || (rlHomeSettingFerryAccessibility.getVisibility() == View.VISIBLE
                            && Configurations.getBoolean("home_setting_ferry_enabled", true))
                            || (llHomeSettingGondola.getVisibility() == View.VISIBLE
                            && Configurations.getBoolean("home_setting_gondola_enabled", true))) {
                        Configurations.setBoolean("home_setting_water_taxi_enabled", false);
                    }
                } else {
                    Configurations.setBoolean("home_setting_water_taxi_enabled", true);
                }
                refreshHomeSettingWaterTaxi();

                refreshSettingsChangedIcon();
            }
        });


        ivHomeSettingWaterTaxiAccessibility.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isEnabled = Configurations.getBoolean("home_setting_water_taxi_enabled", true);

                if (isEnabled) {
                    if (Configurations.getBoolean("home_setting_street_car_enabled", true) ||
                            Configurations.getBoolean("home_setting_rail_enabled", true) ||
                            Configurations.getBoolean("home_setting_bus_enabled", true) ||
                            Configurations.getBoolean("home_setting_link_enabled", true) ||
                            Configurations.getBoolean("home_setting_monorail_enabled", true)
                            || (llHomeSettingFerry.getVisibility() == View.VISIBLE
                            && Configurations.getBoolean("home_setting_ferry_enabled", true))
                            || (rlHomeSettingFerryAccessibility.getVisibility() == View.VISIBLE
                            && Configurations.getBoolean("home_setting_ferry_enabled", true))
                            || (llHomeSettingGondola.getVisibility() == View.VISIBLE
                            && Configurations.getBoolean("home_setting_gondola_enabled", true))) {
                        Configurations.setBoolean("home_setting_water_taxi_enabled", false);
                    }
                } else {
                    Configurations.setBoolean("home_setting_water_taxi_enabled", true);
                }
                refreshHomeSettingWaterTaxi();

                refreshSettingsChangedIcon();
            }
        });


        llHomeSettingMonorail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isEnabled = Configurations.getBoolean("home_setting_monorail_enabled", true);

                if (isEnabled) {
                    if (Configurations.getBoolean("home_setting_street_car_enabled", true) ||
                            Configurations.getBoolean("home_setting_rail_enabled", true) ||
                            Configurations.getBoolean("home_setting_bus_enabled", true) ||
                            Configurations.getBoolean("home_setting_link_enabled", true) ||
                            Configurations.getBoolean("home_setting_water_taxi_enabled", true)
                            || (llHomeSettingFerry.getVisibility() == View.VISIBLE
                            && Configurations.getBoolean("home_setting_ferry_enabled", true))
                            || (rlHomeSettingFerryAccessibility.getVisibility() == View.VISIBLE
                            && Configurations.getBoolean("home_setting_ferry_enabled", true))
                            || (llHomeSettingGondola.getVisibility() == View.VISIBLE
                            && Configurations.getBoolean("home_setting_gondola_enabled", true))
                    ) {
                        Configurations.setBoolean("home_setting_monorail_enabled", false);
                    }
                } else {
                    Configurations.setBoolean("home_setting_monorail_enabled", true);
                }
                refreshHomeSettingMonorail();

                refreshSettingsChangedIcon();
            }
        });

        llHomeSettingMonorailAccessibility.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isEnabled = Configurations.getBoolean("home_setting_monorail_enabled", true);

                if (isEnabled) {
                    if (Configurations.getBoolean("home_setting_street_car_enabled", true) ||
                            Configurations.getBoolean("home_setting_rail_enabled", true) ||
                            Configurations.getBoolean("home_setting_bus_enabled", true) ||
                            Configurations.getBoolean("home_setting_link_enabled", true) ||
                            Configurations.getBoolean("home_setting_water_taxi_enabled", true)
                            || (llHomeSettingFerry.getVisibility() == View.VISIBLE
                            && Configurations.getBoolean("home_setting_ferry_enabled", true))
                            || (rlHomeSettingFerryAccessibility.getVisibility() == View.VISIBLE
                            && Configurations.getBoolean("home_setting_ferry_enabled", true))
                            || (llHomeSettingGondola.getVisibility() == View.VISIBLE
                            && Configurations.getBoolean("home_setting_gondola_enabled", true))) {
                        Configurations.setBoolean("home_setting_monorail_enabled", false);
                    }
                } else {
                    Configurations.setBoolean("home_setting_water_taxi_enabled", true);
                }
                refreshHomeSettingMonorail();

                refreshSettingsChangedIcon();
            }
        });


        ivHomeSettingMonorailAccessibility.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isEnabled = Configurations.getBoolean("home_setting_monorail_enabled", true);

                if (isEnabled) {
                    if (Configurations.getBoolean("home_setting_street_car_enabled", true) ||
                            Configurations.getBoolean("home_setting_rail_enabled", true) ||
                            Configurations.getBoolean("home_setting_bus_enabled", true) ||
                            Configurations.getBoolean("home_setting_link_enabled", true) ||
                            Configurations.getBoolean("home_setting_water_taxi_enabled", true)
                            || (llHomeSettingFerry.getVisibility() == View.VISIBLE
                            && Configurations.getBoolean("home_setting_ferry_enabled", true))
                            || (rlHomeSettingFerryAccessibility.getVisibility() == View.VISIBLE
                            && Configurations.getBoolean("home_setting_ferry_enabled", true))
                            || (llHomeSettingGondola.getVisibility() == View.VISIBLE
                            && Configurations.getBoolean("home_setting_gondola_enabled", true))) {
                        Configurations.setBoolean("home_setting_monorail_enabled", false);
                    }
                } else {
                    Configurations.setBoolean("home_setting_monorail_enabled", true);
                }
                refreshHomeSettingMonorail();

                refreshSettingsChangedIcon();
            }
        });

        initSettings();
    }

    private void refreshDepartureOptionsStatus() {
        cb_leave_now.setChecked(false);
        cb_depart_at.setChecked(false);
        cb_arrive_by.setChecked(false);
        llDepartAtDateTime.setVisibility(View.GONE);
        llArriveByDateTime.setVisibility(View.GONE);
        tvLeaveNow.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
        tvDepartAt.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
        tvArriveBy.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));

        if (isLeaveNowTmp) {
            tvLeaveNow.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
            cb_leave_now.setChecked(true);
        } else if (isDepartAtTmp) {
            cb_depart_at.setChecked(true);
            llDepartAtDateTime.setVisibility(View.VISIBLE);
            tvDepartAt.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        } else {
            cb_arrive_by.setChecked(true);
            llArriveByDateTime.setVisibility(View.VISIBLE);
            tvArriveBy.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        }
    }

    private void setStatusToLeaveNow() {
        llDepartAtDateTime.setVisibility(View.GONE);
        llArriveByDateTime.setVisibility(View.GONE);
        tvLeaveNow.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        tvDepartAt.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
        tvArriveBy.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));

        isLeaveNowTmp = true;
        isDepartAtTmp = false;
        isArriveByTmp = false;

        resetTimeAndDatePicker();
    }

    private void initSearchDateTime() {
        tvHomeDateTimeCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rlHomeDateTime.setVisibility(View.GONE);
                enableAccessbilityForSaveTrip();

                datePickerDialog.getDatePicker().updateDate(currentDatePickerYear,
                        currentDatePickerMonth, currentDatePickerDayOfMonth);

                updateDateDisplayWithTodayOption();

                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.HOUR_OF_DAY, currentTimePickerHour);
                calendar.set(Calendar.MINUTE, currentTimePickerMin);
                String time = Utilities.formatTimestamp(systemContext, calendar.getTimeInMillis());
                tvDepartAtOptionTime.setText(time);
                tvDepartAtOptionTime.setContentDescription(time);
                tvDepartAtOptionTime.sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_FOCUSED);

                tvArriveByOptionTime.setText(time);
                tvArriveByOptionTime.setContentDescription(time);
                tvArriveByOptionTime.sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_FOCUSED);

                timePickerDialog.updateTime(currentTimePickerHour, currentTimePickerMin);
            }
        });

        tvHomeDateTimeSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rlHomeDateTime.setVisibility(View.GONE);
                enableAccessbilityForSaveTrip();

                currentTimePickerHour = timePickerHourTmp;
                currentTimePickerMin = timePickerMinTmp;

                currentDatePickerYear = datePickerYearTmp;
                currentDatePickerMonth = datePickerMonthTmp;
                currentDatePickerDayOfMonth = datePickerDayOfMonthTmp;

                isLeaveNow = isLeaveNowTmp;
                isDepartAt = isDepartAtTmp;
                isArriveBy = isArriveByTmp;

                refreshSearchDate();
                refreshSearchTime();
            }
        });
        rlLeaveNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideLayerPopup();

                setStatusToLeaveNow();

                cb_leave_now.setChecked(true);
                cb_depart_at.setChecked(false);
                cb_arrive_by.setChecked(false);
            }
        });

        rlDepartAt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                llDepartAtDateTime.setVisibility(View.VISIBLE);
                llArriveByDateTime.setVisibility(View.GONE);
                hideLayerPopup();

                tvLeaveNow.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
                tvDepartAt.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                tvArriveBy.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));

                isLeaveNowTmp = false;
                isDepartAtTmp = true;
                isArriveByTmp = false;

                cb_leave_now.setChecked(false);
                cb_depart_at.setChecked(true);
                cb_arrive_by.setChecked(false);
            }
        });

        rlArriveBy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                llDepartAtDateTime.setVisibility(View.GONE);
                llArriveByDateTime.setVisibility(View.VISIBLE);
                hideLayerPopup();

                tvLeaveNow.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
                tvDepartAt.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
                tvArriveBy.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));

                isLeaveNowTmp = false;
                isDepartAtTmp = false;
                isArriveByTmp = true;

                cb_leave_now.setChecked(false);
                cb_depart_at.setChecked(false);
                cb_arrive_by.setChecked(true);
            }
        });

        rlOptionTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTimePickerDialog();
            }
        });

        rlOptionDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePicker();
            }
        });

        rlArriveByAtOptionTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTimePickerDialog();
            }
        });
        rlArriveByOptionDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePicker();
            }
        });
        rlHomeDateTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideLayerPopup();
            }
        });

        refreshSearchTime();
        refreshSearchDate();
    }

    private void setupTimePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());

        currentTimePickerHour = calendar.get(Calendar.HOUR_OF_DAY);
        currentTimePickerMin = calendar.get(Calendar.MINUTE);

        timePickerHourTmp = currentTimePickerHour;
        timePickerMinTmp = currentTimePickerMin;

        timePickerDialog = new TimePickerDialog(getActivity(),
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        //refreshSearchTimeWithHourMin(hourOfDay, minute);

                        timePickerHourTmp = hourOfDay;
                        timePickerMinTmp = minute;
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(Calendar.HOUR_OF_DAY, timePickerHourTmp);
                        calendar.set(Calendar.MINUTE, timePickerMinTmp);
                        String time = Utilities.formatTimestamp(systemContext, calendar.getTimeInMillis());
                        tvDepartAtOptionTime.setText(time);
                        tvDepartAtOptionTime.setContentDescription(time);
                        tvDepartAtOptionTime.sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_FOCUSED);

                        tvArriveByOptionTime.setText(time);
                        tvArriveByOptionTime.setContentDescription(time);
                        tvArriveByOptionTime.sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_FOCUSED);

                    }
                }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), android.text.format.DateFormat.is24HourFormat(systemContext));
        timePickerDialog.setCancelable(false);
        timePickerDialog.setCanceledOnTouchOutside(false);


        // Add OnShowListener to customise button texts.
        timePickerDialog.setOnShowListener(dialog -> {
            Button btnOk = timePickerDialog.getButton(TimePickerDialog.BUTTON_POSITIVE);
            Button btnCancel = timePickerDialog.getButton(TimePickerDialog.BUTTON_NEGATIVE);

            if (btnOk != null) {
                btnOk.setText(getString(R.string.ok)); // Set OK button text
            }

            if (btnCancel != null) {
                btnCancel.setText(getString(R.string.cancel)); // Set Cancel button text
            }
        });

        timePickerDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                timePickerDialog.updateTime(timePickerHourTmp, timePickerMinTmp);

            }
        });
    }

    private void showTimePickerDialog() {
        if (timePickerDialog == null) {
            setupTimePickerDialog();
        }

        timePickerDialog.show();
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
                getActivity(),
                null, // instead of a listener
                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.setCancelable(false);
        datePickerDialog.setCanceledOnTouchOutside(false);
        datePickerDialog.getDatePicker().setMinDate(calendar.getTimeInMillis());
        datePickerDialog.setButton(DialogInterface.BUTTON_POSITIVE, "OK",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DatePicker dpSearchDateTmp = datePickerDialog.getDatePicker();
                        datePickerYearTmp = dpSearchDateTmp.getYear();
                        datePickerMonthTmp = dpSearchDateTmp.getMonth();
                        datePickerDayOfMonthTmp = dpSearchDateTmp.getDayOfMonth();
                        //refreshSearchDate();
                        updateDateDisplayWithTodayOption();
                    }
                });
        datePickerDialog.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.cancel),
                (dialog, which) -> datePickerDialog.getDatePicker().updateDate(datePickerYearTmp,
                        datePickerMonthTmp, datePickerDayOfMonthTmp));
    }

    private void updateDateDisplayWithTodayOption() {
        String dateString = String.format(Locale.getDefault(), "%d-%02d-%02d", datePickerYearTmp,
                datePickerMonthTmp + 1, datePickerDayOfMonthTmp);

        long currentTimestamp = System.currentTimeMillis();
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(currentTimestamp);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int min = calendar.get(Calendar.MINUTE);
        int sec = calendar.get(Calendar.SECOND);

        calendar.set(datePickerYearTmp, datePickerMonthTmp, datePickerDayOfMonthTmp, hour, min, sec);
        long setTimeStamp = calendar.getTimeInMillis();
        long diffInMillisec = setTimeStamp - currentTimestamp;
        long diffInDays = TimeUnit.MILLISECONDS.toDays(diffInMillisec);
        if (diffInDays == 0) {
            tvDepartAtOptionDate.setText(getString(R.string.today));
            tvArriveByOptionDate.setText(getString(R.string.today));
        } else {
            tvDepartAtOptionDate.setText(dateString);
            tvArriveByOptionDate.setText(dateString);
        }
    }

    private void showDatePicker() {
        if (datePickerDialog == null) {
            setupDatePickerDialog();
        }
        datePickerDialog.show();
    }

    private void setupSearchDateTime() {
        cb_leave_now = mFragmentView.findViewById(R.id.cb_leave_now);
        cb_depart_at = mFragmentView.findViewById(R.id.cb_depart_at);
        cb_arrive_by = mFragmentView.findViewById(R.id.cb_arrive_by);

        tvHomeDateTimeSave = mFragmentView.findViewById(R.id.tv_home_date_time_save);
        tvHomeDateTimeCancel = mFragmentView.findViewById(R.id.tv_home_date_time_cancel);
        rlHomeDateTime = mFragmentView.findViewById(R.id.rl_home_date_time);

        rlLeaveNow = mFragmentView.findViewById(R.id.rl_leave_now);
        rlDepartAt = mFragmentView.findViewById(R.id.rl_depart_at);
        rlArriveBy = mFragmentView.findViewById(R.id.rl_arrive_by);

        tvLeaveNow = mFragmentView.findViewById(R.id.tv_leave_now);
        tvDepartAt = mFragmentView.findViewById(R.id.tv_depart_at);
        tvArriveBy = mFragmentView.findViewById(R.id.tv_arrive_by);

        switchToDefaultTimePicker(false);
        initSearchDateTime();
    }

    private void hideLayerPopup() {
        llLayoutLayers.setVisibility(View.GONE);
        ivLayer.setColorFilter(null);
        ivLayerBg.setImageResource(R.drawable.ic_layer_bkg_default);
        ivLayerBg.setColorFilter(null);
        llItineraryList.setAlpha(1.0f);
        llItineraryDetail.setAlpha(1.0f);
    }

    private void setSearchDateTimeHeight(boolean isFullHeight) {
        if (rlSearchDateTime != null) {
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) rlSearchDateTime.getLayoutParams();

            if (Utilities.checkIfToDisplayAccessibilityUI(requireActivity())) {
                layoutParams.height = WRAP_CONTENT;

            } else {

                if (isFullHeight) {
                    layoutParams.height = Convert.dpToPx(60);
                } else {
                    layoutParams.height = Convert.dpToPx(56);
                }
            }

            rlSearchDateTime.setLayoutParams(layoutParams);
        }
    }

    private void refreshSearchTimeWithHourMin(int hourOfDay, int minute) {
        currentTimePickerHour = hourOfDay;
        currentTimePickerMin = minute;

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        calendar.set(Calendar.MINUTE, minute);

        String time = Utilities.formatTimestamp(systemContext, calendar.getTimeInMillis());

        tvDepartAtOptionTime.setText(time);
        tvDepartAtOptionTime.setContentDescription(time);
        tvDepartAtOptionTime.sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_FOCUSED);

        tvArriveByOptionTime.setText(time);
        tvArriveByOptionTime.setContentDescription(time);
        tvArriveByOptionTime.sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_FOCUSED);

        if (isLeaveNow) {
            tvSearchDateTimeMode.setText(getString(R.string.leave_now_two_lines));
            tvSearchTime.setVisibility(View.GONE);
            tvSearchDate.setVisibility(View.GONE);
            tvSearchTime.setText(time);

            tvSearchTimeAccessibility.setVisibility(View.GONE);
            tvSearchDateAccessibility.setVisibility(View.GONE);
            tvSearchTimeAccessibility.setText(time);

            setSearchDateTimeHeight(false);
        } else if (isDepartAt) {
            if (Configurations.getBoolean("new_search_bar_layout_style", false)) {
                tvSearchDateTimeMode.setText(getString(R.string.departure_colon));
            } else {
                tvSearchDateTimeMode.setText(getString(R.string.depart));
            }

            tvSearchTime.setText(time);
            tvSearchTimeAccessibility.setText(time);

            if (Utilities.checkIfToDisplayAccessibilityUI(requireActivity())) {
                tvSearchTimeAccessibility.setVisibility(View.VISIBLE);
                tvSearchDateAccessibility.setVisibility(View.VISIBLE);
                tvSearchTime.setVisibility(View.GONE);
                tvSearchDate.setVisibility(View.GONE);
            } else {
                tvSearchTime.setVisibility(View.VISIBLE);
                tvSearchDate.setVisibility(View.VISIBLE);
                tvSearchTimeAccessibility.setVisibility(View.GONE);
                tvSearchDateAccessibility.setVisibility(View.GONE);
            }
            setSearchDateTimeHeight(true);
        } else {
            if (Configurations.getBoolean("new_search_bar_layout_style", false)) {
                tvSearchDateTimeMode.setText(getString(R.string.arrive_colon));
            } else {
                tvSearchDateTimeMode.setText(getString(R.string.arrive));
            }

            tvSearchTime.setText(time);
            tvSearchTimeAccessibility.setText(time);
            if (Utilities.checkIfToDisplayAccessibilityUI(requireActivity())) {
                tvSearchTimeAccessibility.setVisibility(View.VISIBLE);
                tvSearchDateAccessibility.setVisibility(View.VISIBLE);
                tvSearchTime.setVisibility(View.GONE);
                tvSearchDate.setVisibility(View.GONE);
            } else {
                tvSearchTime.setVisibility(View.VISIBLE);
                tvSearchDate.setVisibility(View.VISIBLE);
                tvSearchTimeAccessibility.setVisibility(View.GONE);
                tvSearchDateAccessibility.setVisibility(View.GONE);
            }

            setSearchDateTimeHeight(true);
        }
    }

    private void refreshSearchTime() {
        if (timePickerDialog == null) {
            setupTimePickerDialog();
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());

            refreshSearchTimeWithHourMin(calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE));
        } else {
            refreshSearchTimeWithHourMin(currentTimePickerHour, currentTimePickerMin);
        }
    }

    private void refreshSearchDate() {
        if (datePickerDialog == null) {
            setupDatePickerDialog();
        }
        DatePicker dpSearchDateTmp = datePickerDialog.getDatePicker();
        int year = dpSearchDateTmp.getYear();
        int month = dpSearchDateTmp.getMonth();
        int dayOfMonth = dpSearchDateTmp.getDayOfMonth();

        String dateString = String.format(Locale.getDefault(), "%02d/%02d/%d", month + 1, dayOfMonth, year);

        updateDateDisplayWithTodayOption();
        if (isLeaveNow) {
            tvSearchDate.setText(getString(R.string.today));
            tvSearchDateAccessibility.setText(getString(R.string.today));
        } else {
            long currentTimestamp = System.currentTimeMillis();
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(currentTimestamp);
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int min = calendar.get(Calendar.MINUTE);
            int sec = calendar.get(Calendar.SECOND);

            calendar.set(year, month, dayOfMonth, hour, min, sec);
            long setTimeStamp = calendar.getTimeInMillis();
            long diffInMillisec = setTimeStamp - currentTimestamp;
            long diffInDays = TimeUnit.MILLISECONDS.toDays(diffInMillisec);

            if (Math.abs(diffInDays) >= 7) {
                tvSearchDate.setText(dateString);
                tvSearchDateAccessibility.setText(dateString);
            } else if (diffInDays == 0) {
                tvSearchDate.setText(getString(R.string.today));
                tvSearchDateAccessibility.setText(getString(R.string.today));
            } else if (diffInDays == 1) {
                tvSearchDate.setText(getString(R.string.tomorrow));
                tvSearchDateAccessibility.setText(getString(R.string.tomorrow));
            } else if (diffInDays == -1) {
                tvSearchDate.setText(getString(R.string.yesterday));
                tvSearchDateAccessibility.setText(getString(R.string.yesterday));
            } else {
                Date date = new Date();
                date.setTime(setTimeStamp);

                if (diffInDays > 0) {
                    tvSearchDate.setText(new SimpleDateFormat("EEEE", Locale.getDefault()).format(date));
                    tvSearchDateAccessibility.setText(new SimpleDateFormat("EEEE", Locale.getDefault()).format(date));
                } else {
                    tvSearchDate.setText(String.format("%s %s", getString(R.string.last),
                            new SimpleDateFormat("EEEE", Locale.getDefault()).format(date)));
                    tvSearchDateAccessibility.setText(String.format("%s %s", getString(R.string.last),
                            new SimpleDateFormat("EEEE", Locale.getDefault()).format(date)));
                }
            }
        }
    }

    private void resetTimeAndDatePicker() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());

        if (timePickerDialog != null) {
            timePickerDialog.updateTime(calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE));
        }

        if (datePickerDialog != null && datePickerDialog.getDatePicker() != null) {
            datePickerDialog.getDatePicker().updateDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        }
    }

    private void initItinerarySortSpinner() {
        itinerarySortSpinnerIndex = Configurations.getInt("itinerary_sort_index", 0);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(systemContext, R.layout.item_simple_spinner,
                systemContext.getResources().getStringArray(R.array.itinerary_sort_arrays));

        if (Utilities.isFeatureEnabled(Constants.FARE_INFO_ID, false)) {
            arrayAdapter = new ArrayAdapter<>(systemContext, R.layout.item_simple_spinner,
                    systemContext.getResources().getStringArray(R.array.itinerary_sort_arrays));
        } else {
            arrayAdapter = new ArrayAdapter<>(systemContext, R.layout.item_simple_spinner,
                    systemContext.getResources().getStringArray(R.array.itinerary_sort_arrays_without_cost));
        }

        arrayAdapter.setDropDownViewResource(R.layout.item_simple_spinner_dropdown);

        itinerarySortSpinner.setAdapter(arrayAdapter);
        itinerarySortSpinner.setSelection(itinerarySortSpinnerIndex);
        itinerarySortSpinner.setGravity(Gravity.END | Gravity.CENTER_VERTICAL);

        itinerarySortSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                itinerarySortSpinnerIndex = position;

                Configurations.setInt("itinerary_sort_index", itinerarySortSpinnerIndex);

                sortItineraryList();
                sortItineraryListView();
                if (itineraryListAdapterWalkTransit != null) {
                    itineraryListAdapterWalkTransit.notifyDataSetChanged();
                }
                if (itineraryListAdapterBikeTransit != null) {
                    itineraryListAdapterBikeTransit.notifyDataSetChanged();
                }

                if (itineraryListAdapterBikeShareTransit != null) {
                    itineraryListAdapterBikeShareTransit.notifyDataSetChanged();
                }

                if (itineraryListAdapterScooterTransit != null) {
                    itineraryListAdapterScooterTransit.notifyDataSetChanged();
                }

                if (itineraryListAdapterDriveTransit != null) {
                    itineraryListAdapterDriveTransit.notifyDataSetChanged();
                }

                if (itineraryListOtherAdapter != null) {
                    itineraryListOtherAdapter.notifyDataSetChanged();
                }

                setHeightToItineraryList();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        itinerarySortSpinnerAccessibility.setAdapter(arrayAdapter);
        itinerarySortSpinnerAccessibility.setSelection(itinerarySortSpinnerIndex);
        itinerarySortSpinnerAccessibility.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);

        itinerarySortSpinnerAccessibility.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                itinerarySortSpinnerIndex = position;

                Configurations.setInt("itinerary_sort_index", itinerarySortSpinnerIndex);

                sortItineraryList();
                sortItineraryListView();
                if (itineraryListAdapterWalkTransit != null) {
                    itineraryListAdapterWalkTransit.notifyDataSetChanged();
                }
                if (itineraryListAdapterBikeTransit != null) {
                    itineraryListAdapterBikeTransit.notifyDataSetChanged();
                }

                if (itineraryListAdapterBikeShareTransit != null) {
                    itineraryListAdapterBikeShareTransit.notifyDataSetChanged();
                }

                if (itineraryListAdapterScooterTransit != null) {
                    itineraryListAdapterScooterTransit.notifyDataSetChanged();
                }

                if (itineraryListAdapterDriveTransit != null) {
                    itineraryListAdapterDriveTransit.notifyDataSetChanged();
                }

                if (itineraryListOtherAdapter != null) {
                    itineraryListOtherAdapter.notifyDataSetChanged();
                }

                setHeightToItineraryList();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


    }

    private void setHeightToItineraryList() {
        Utilities.setTotalHeightToListViewExt(gDm.widthPixels - Convert.dpToPx(20), lvItineraryListDriveTransit);
        Utilities.setTotalHeightToListViewExt(gDm.widthPixels - Convert.dpToPx(20), lvItineraryListWalkTransit);
        Utilities.setTotalHeightToListViewExt(gDm.widthPixels - Convert.dpToPx(20), lvItineraryListBikeTransit);
        Utilities.setTotalHeightToListViewExt(gDm.widthPixels - Convert.dpToPx(20), lvItineraryListBikeShareTransit);
        Utilities.setTotalHeightToListViewExt(gDm.widthPixels - Convert.dpToPx(20), lvItineraryListScooterTransit);
        Utilities.setTotalHeightToListViewExt(gDm.widthPixels - Convert.dpToPx(20), lvItineraryListOther);
    }

    private void setItineraryDetailHeight(int dy) {
        if (llItineraryDetail != null && systemContext != null) {
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) llItineraryDetail.getLayoutParams();
            layoutParams.width = gDm.widthPixels;

            int minHeight = Convert.dpToPx(Constants.ITINERARY_DETAIL_MIN_HEIGHT);
            int maxHeight = gDm.heightPixels - Convert.dpToPx(Constants.ITINERARY_DETAIL_MAX_HEIGHT);

            if (currentHeight - dy > maxHeight) {
                layoutParams.height = maxHeight;
            } else layoutParams.height = Math.max(currentHeight - dy, minHeight);
            llItineraryDetail.setLayoutParams(layoutParams);
        }
    }

    private void setupItineraryListLayout() {
        llItineraryList = mFragmentView.findViewById(R.id.ll_itinerary_list);
        itinerarySortSpinner = mFragmentView.findViewById(R.id.sp_itinerary_sort);
        tv_itinerary_origin = mFragmentView.findViewById(R.id.tv_itinerary_origin);
        tv_itinerary_Destination = mFragmentView.findViewById(R.id.tv_itinerary_destination);
        iv_itinerary_back = mFragmentView.findViewById(R.id.iv_itinerary_back);
        llItineraryBack = mFragmentView.findViewById(R.id.ll_itinerary_back_accessibility);

        itinerarySortSpinnerAccessibility = mFragmentView.findViewById(R.id.sp_itinerary_sort_accessibility);
        rlItinerarySort = mFragmentView.findViewById(R.id.rl_itinerary_sort);
        rlItinerarySortAccessibility = mFragmentView.findViewById(R.id.rl_itinerary_sort_accessibility);

        llAccessibilitylegend = mFragmentView.findViewById(R.id.ll_accessibility_legend);

        RelativeLayout rlAccessibilitylegendPopUp = mFragmentView.findViewById(R.id.ll_accessibility_legend_pop_up);

        RelativeLayout rlAccessibilitylegendPopUpAccessibility = mFragmentView.findViewById(R.id.ll_accessibility_legend_pop_up_accessibility);

        ImageView ivAccessibiltyLegendClose = mFragmentView.findViewById(R.id.iv_close_accessibility_legend);

        ImageView ivAccessibiltyLegendCloseAccessibility = mFragmentView.findViewById(R.id.iv_close_accessibility_legend_accessibiliy);


        if (Utilities.checkIfToDisplayAccessibilityUI(requireActivity())) {
            llItineraryBack.setVisibility(View.VISIBLE);
            iv_itinerary_back.setVisibility(View.GONE);
        } else {
            llItineraryBack.setVisibility(View.GONE);
            iv_itinerary_back.setVisibility(View.VISIBLE);
        }

        initItineraryListLayout();

        if (Utilities.checkIfToDisplayAccessibilityUI(requireActivity())) {
            rlItinerarySortAccessibility.setVisibility(View.VISIBLE);
            rlItinerarySort.setVisibility(View.GONE);
        } else {
            rlItinerarySortAccessibility.setVisibility(View.GONE);
            rlItinerarySort.setVisibility(View.VISIBLE);
        }

        ImageView ivItineraryListExpander = mFragmentView.findViewById(R.id.iv_itinerary_list_expander);
        ivItineraryListExpander.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (itineraryListExpandedStatus == Constants.EXPANDED_STATUS_FULL_EXPANDED) {
                    itineraryListExpandedStatus = Constants.EXPANDED_STATUS_UNEXPANDED;
                } else {
                    itineraryListExpandedStatus = Constants.EXPANDED_STATUS_FULL_EXPANDED;
                }
            }
        });


        if (Configurations.getBoolean("enabled_search_mode_accessible_routing", false)) {
            llAccessibilitylegend.setVisibility(View.VISIBLE);
        } else {
            llAccessibilitylegend.setVisibility(View.GONE);
        }


        llAccessibilitylegend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (Utilities.checkIfToDisplayAccessibilityUI(requireActivity())) {
                    rlAccessibilitylegendPopUpAccessibility.setVisibility(View.VISIBLE);
                } else {
                    rlAccessibilitylegendPopUp.setVisibility(View.VISIBLE);
                }


            }
        });

        if (Utilities.checkIfToDisplayAccessibilityUI(requireActivity())) {

            ivAccessibiltyLegendCloseAccessibility.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    rlAccessibilitylegendPopUpAccessibility.setVisibility(View.GONE);

                }
            });

        } else {
            ivAccessibiltyLegendClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    rlAccessibilitylegendPopUp.setVisibility(View.GONE);

                }
            });

        }


        iv_itinerary_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goBackToRouteSearchReady();
            }
        });

        llItineraryBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goBackToRouteSearchReady();
            }
        });

    }

    private ItineraryListAdapter getItineraryListAdapter(ArrayList<Itinerary> arrayList, int groupType) {
        ArrayList<HashMap<String, Object>> data = new ArrayList<HashMap<String, Object>>();

        for (int i = 0; i < arrayList.size(); i++) {
            Itinerary itinerary = arrayList.get(i);

            HashMap<String, Object> map = new HashMap<String, Object>();

            map.put("tv_duration", itinerary.getDuration());

            data.add(map);
        }


        ItineraryListAdapter mItineraryListAdapter = new ItineraryListAdapter(
                systemContext, data,
                R.layout.item_group_itinerary_list,
                new String[]{"tv_duration"}, new int[]{R.id.tv_duration}, arrayList, groupType);

        return mItineraryListAdapter;
    }

    class ItineraryListAdapter extends SimpleAdapter {
        Context mContext;
        private final LayoutInflater l_Inflater;
        private ArrayList<Itinerary> itineraryArrayList = new ArrayList<>();
        private final int groupType;

        public ItineraryListAdapter(Context context, List<? extends Map<String, ?>> data,
                                    int resource, String[] from, int[] to,
                                    ArrayList<Itinerary> arrayList, int groupType) {
            super(context, data, resource, from, to);

            mContext = context;
            l_Inflater = LayoutInflater.from(context);
            itineraryArrayList = arrayList;
            this.groupType = groupType;
        }

        @Override
        public int getCount() {
            return itineraryArrayList.size();
        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Itinerary itinerary = itineraryArrayList.get(position);

            if (convertView == null) {

                if (Utilities.checkIfToDisplayAccessibilityUI(mContext)) {
                    convertView = l_Inflater.inflate(
                            R.layout.item_group_itinerary_list_accessibility, null);

                } else {
                    convertView = l_Inflater.inflate(
                            R.layout.item_group_itinerary_list, null);
                }

            }
            boolean isFirstOne = false;
            boolean isLastOne = false;

            if (position == 0) {
                isFirstOne = true;
            }

            if (position == (itineraryArrayList.size() - 1)) {
                isLastOne = true;
            }

            populateDataIntoItineraryItem(convertView, itinerary, currentSelectedItineraryGroupType == groupType && position == currentSelectedItineraryIndex,
                    false, isFirstOne, isLastOne);

            convertView.setTag(position);
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = (Integer) view.getTag();
                    //go into the itinerary detail directly
                    if (position < itineraryArrayList.size()) {
                        currentSelectedItineraryIndex = position;
                        currentSelectedItineraryGroupType = groupType;

                        showRoutePolylineOnMap(itineraryArrayList.get(position));
                        if (currentSelectedItineraryGroupType == Constants.GROUP_ITINERARY_TYPE_WALK_TRANSIT) {
                            lvItineraryListWalkTransit.smoothScrollToPosition(currentSelectedItineraryIndex);
                        } else if (currentSelectedItineraryGroupType == Constants.GROUP_ITINERARY_TYPE_DRIVE_TRANSIT) {
                            lvItineraryListDriveTransit.smoothScrollToPosition(currentSelectedItineraryIndex);
                        } else if (currentSelectedItineraryGroupType == Constants.GROUP_ITINERARY_TYPE_BIKESHARE_TRANSIT) {
                            lvItineraryListBikeShareTransit.smoothScrollToPosition(currentSelectedItineraryIndex);
                        } else if (currentSelectedItineraryGroupType == Constants.GROUP_ITINERARY_TYPE_SCOOTER_TRANSIT) {
                            lvItineraryListScooterTransit.smoothScrollToPosition(currentSelectedItineraryIndex);
                        } else {
                            lvItineraryListBikeTransit.smoothScrollToPosition(currentSelectedItineraryIndex);
                        }
                        if (itineraryListOtherAdapter != null) {
                            itineraryListOtherAdapter.notifyDataSetChanged();
                        }
                        if (itineraryListAdapterWalkTransit != null) {
                            itineraryListAdapterWalkTransit.notifyDataSetChanged();
                        }
                        if (itineraryListAdapterDriveTransit != null) {
                            itineraryListAdapterDriveTransit.notifyDataSetChanged();
                        }
                        if (itineraryListAdapterBikeTransit != null) {
                            itineraryListAdapterBikeTransit.notifyDataSetChanged();
                        }

                        if (itineraryListAdapterBikeShareTransit != null) {
                            itineraryListAdapterBikeShareTransit.notifyDataSetChanged();
                        }

                        if (itineraryListAdapterScooterTransit != null) {
                            itineraryListAdapterScooterTransit.notifyDataSetChanged();
                        }

                        moveMapToShowRoute();

                        selectedItinerary = itineraryArrayList.get(position);

                        enterItineraryDetailPage();

                        itineraryLegArrayList.clear();
                        itineraryLegStepArray.clear();
                        if (selectedItinerary.getLegs() != null) {
                            itineraryLegArrayList.addAll(selectedItinerary.getLegs());
                        }
                        for (ItineraryLeg itineraryLeg : itineraryLegArrayList) {
                            itineraryLegStepArray.add(false);
                        }

                        if (itineraryLegListAdapter != null) {
                            itineraryLegListAdapter.notifyDataSetInvalidated();
                        }
                        Utilities.setTotalHeightToListViewExt(gDm.widthPixels - Convert.dpToPx(30), lvItineraryLegList);
                        loadPlanTripErrorsInTripDetail();
                        loadDataInItineraryDetailHeader();
                        refreshTripDetails();
                        refreshSaveTripButton();

                        llTripDetails.setVisibility(View.VISIBLE);

                        svItineraryDetail.post(new Runnable() {
                            @Override
                            public void run() {
                                svItineraryDetail.scrollTo(0, 0);
                            }
                        });
                    }
                }
            });
            return convertView;
        }
    }

    private ItineraryListOtherAdapter getItineraryListOtherAdapter() {
        ArrayList<HashMap<String, Object>> data = new ArrayList<HashMap<String, Object>>();

        for (int i = 0; i < itineraryArrayListOther.size(); i++) {
            Itinerary itinerary = itineraryArrayListOther.get(i);

            HashMap<String, Object> map = new HashMap<String, Object>();

            map.put("tv_duration", itinerary.getDuration());

            data.add(map);
        }

        ItineraryListOtherAdapter mItineraryListOtherAdapter = new ItineraryListOtherAdapter(
                systemContext, data,
                R.layout.item_group_itinerary_list_other,
                new String[]{"tv_duration"}, new int[]{R.id.tv_duration});

        return mItineraryListOtherAdapter;
    }

    class ItineraryListOtherAdapter extends SimpleAdapter {
        Context mContext;
        private final LayoutInflater l_Inflater;

        public ItineraryListOtherAdapter(Context context, List<? extends Map<String, ?>> data,
                                         int resource, String[] from, int[] to) {
            super(context, data, resource, from, to);

            mContext = context;
            l_Inflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return itineraryArrayListOther.size();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Itinerary itinerary = itineraryArrayListOther.get(position);

            if (convertView == null) {
                convertView = l_Inflater.inflate(
                        R.layout.item_group_itinerary_list_other, null);
            }
            boolean isFirstOne = false;
            boolean isLastOne = false;

            if (position == 0) {
                isFirstOne = true;
            }

            if (position == (itineraryArrayListOther.size() - 1)) {
                isLastOne = true;
            }
            boolean showHighlight;
            showHighlight = currentSelectedItineraryGroupType == Constants.GROUP_ITINERARY_TYPE_OTHER
                    && position == currentSelectedItineraryIndex;

            TextView tvDuration = convertView.findViewById(R.id.tv_duration);
            TextView tvMode = convertView.findViewById(R.id.tv_mode);
            RelativeLayout rlMode = convertView.findViewById(R.id.rl_mode);
            ImageView ivMode = convertView.findViewById(R.id.iv_mode);

            if (itinerary.getLegs() != null && itinerary.getLegs().size() > 0) {
                ItineraryLeg itineraryLeg = itinerary.getLegs().get(0);
                if (itineraryLeg.getMode() != null) {
                    String mode = Utilities.getMappedModeForItinerary(getActivity(), itineraryLeg.getRouteId(),
                            itineraryLeg.getMode(), itineraryLeg.getRouteShortName(), itineraryLeg.getRouteLongName()).toLowerCase();
                    ivMode.setImageResource(Utilities.getModeImageByMode(mode,
                            itineraryLeg.isRentedVehicle(), requireActivity(), ivMode, rlMode));
                    ivMode.setColorFilter(getResources().getColor(android.R.color.white));
                    if (mode.equalsIgnoreCase("BICYCLE")) {
                        String type = itineraryLeg.getFrom().getVertexType().toLowerCase();
                        if (type.equalsIgnoreCase("bikeshare")) {
                            tvMode.setText(systemContext.getString(Utilities.modesStringMapping.get(type)));
                            ivMode.setImageResource(Utilities.getModeImageByMode(mode,
                                    type.equalsIgnoreCase("bikeshare"), requireActivity(), ivMode, rlMode));
                        } else {
                            tvMode.setText(systemContext.getString(Utilities.modesStringMapping.get(mode)));
                        }
                    } else if (mode.equalsIgnoreCase("SCOOTER")) {

                        tvMode.setText(systemContext.getString(Utilities.modesStringMapping.get(mode)));
                        ivMode.setImageResource(Utilities.getModeImageByMode(mode,
                                mode.equalsIgnoreCase("scooter"), requireActivity(), ivMode, rlMode));
                    } else if (Utilities.modesStringMapping.containsKey(mode)) {
                        tvMode.setText(systemContext.getString(Utilities.modesStringMapping.get(mode)));
                    }
                }

                if (itinerary.getLegs().size() > 1) {
                    for (ItineraryLeg leg : itinerary.getLegs()) {
                        if (leg.getMode().toLowerCase().equalsIgnoreCase("BICYCLE")) {
                            itineraryLeg = leg;
                            String type = leg.getFrom().getVertexType().toLowerCase();
                            tvMode.setText(systemContext.getString(Utilities.modesStringMapping.get(type)));
                            ivMode.setImageResource(Utilities.getModeImageByMode(type,
                                    type.equalsIgnoreCase("bikeshare"), requireActivity(), ivMode, rlMode));
                            break;
                        } else if (leg.getMode().toLowerCase().equalsIgnoreCase("SCOOTER")) {
                            itineraryLeg = leg;
                            String mode = Utilities.getMappedModeForItinerary(getActivity(), itineraryLeg.getRouteId(),
                                    itineraryLeg.getMode(), itineraryLeg.getRouteShortName(), itineraryLeg.getRouteLongName()).toLowerCase();
                            tvMode.setText(systemContext.getString(Utilities.modesStringMapping.get(mode)));
                            ivMode.setImageResource(Utilities.getModeImageByMode(mode,
                                    mode.equalsIgnoreCase("scooter"), requireActivity(), ivMode, rlMode));
                            break;
                        }
                    }
                }


            }

            int durationMin = itinerary.getDuration() / 60;
            if (durationMin >= 60) {
                tvDuration.setText(systemContext.getString(R.string.hr_min_with_num, durationMin / 60,
                        durationMin % 60));
            } else {
                tvDuration.setText(systemContext.getString(R.string.min_with_num, durationMin));
            }


            LinearLayout llItineraryAccessiblebg = convertView.findViewById(R.id.ll_itinearary_accessible_bg);
            ImageView ivItineraryAccessibleStatus = convertView.findViewById(R.id.iv_itinerary_accessible_status);

            LinearLayout llItineraryOtherbg = convertView.findViewById(R.id.ll_itinearary_other_bg);

            if (Configurations.getBoolean("enabled_search_mode_accessible_routing", false)) {

                int color = getaccessiblityForItinerary(itinerary);

                int padding = Convert.dpToPx(20);


                switch (color) {

                    case R.color.accessibile:

                        if (isFirstOne && isLastOne) {
                            llItineraryOtherbg.setBackgroundResource(R.drawable.bottom_rounded_white_with_shadow_bg);
                        } else if (isFirstOne) {
                            llItineraryOtherbg.setBackgroundResource(R.drawable.bottom_rounded_white_with_shadow_bg);
                        } else if (isLastOne) {
                            llItineraryOtherbg.setBackgroundResource(R.drawable.bottom_rounded_white_with_shadow_bg);
                        } else {
                            llItineraryOtherbg.setBackgroundColor(getResources().getColor(android.R.color.white));
                        }

                        if (isFirstOne && isLastOne) {
                            llItineraryAccessiblebg.setBackgroundResource(R.drawable.top_rounded_accessible_bg);
                        } else if (isFirstOne) {
                            llItineraryAccessiblebg.setBackgroundResource(R.drawable.top_rounded_accessible_bg);
                        } else if (isLastOne) {
                            llItineraryAccessiblebg.setBackgroundColor(getResources().getColor(R.color.accessibile));
                        } else {
                            llItineraryAccessiblebg.setBackgroundColor(getResources().getColor(R.color.accessibile));
                        }
                        llItineraryAccessiblebg.setVisibility(View.VISIBLE);
                        ivItineraryAccessibleStatus.setImageResource(R.drawable.ic_like);

                        llItineraryOtherbg.setPadding(padding, padding, padding, padding);

                        break;

                    case R.color.likely_acessibile:

                        if (isFirstOne && isLastOne) {
                            llItineraryOtherbg.setBackgroundResource(R.drawable.bottom_rounded_white_with_shadow_bg);
                        } else if (isFirstOne) {
                            llItineraryOtherbg.setBackgroundResource(R.drawable.bottom_rounded_white_with_shadow_bg);
                        } else if (isLastOne) {
                            llItineraryOtherbg.setBackgroundResource(R.drawable.bottom_rounded_white_with_shadow_bg);
                        } else {
                            llItineraryOtherbg.setBackgroundColor(getResources().getColor(android.R.color.white));
                        }


                        if (isFirstOne && isLastOne) {
                            llItineraryAccessiblebg.setBackgroundResource(R.drawable.top_rounded_likely_accessible_bg);
                        } else if (isFirstOne) {
                            llItineraryAccessiblebg.setBackgroundResource(R.drawable.top_rounded_likely_accessible_bg);
                        } else if (isLastOne) {
                            llItineraryAccessiblebg.setBackgroundColor(getResources().getColor(R.color.likely_acessibile));
                        } else {
                            llItineraryAccessiblebg.setBackgroundColor(getResources().getColor(R.color.likely_acessibile));
                        }
                        llItineraryAccessiblebg.setVisibility(View.VISIBLE);
                        ivItineraryAccessibleStatus.setImageResource(R.drawable.ic_help_menu);


                        llItineraryOtherbg.setPadding(padding, padding, padding, padding);

                        break;

                    case R.color.unaccessible:

                        if (isFirstOne && isLastOne) {
                            llItineraryOtherbg.setBackgroundResource(R.drawable.bottom_rounded_white_with_shadow_bg);
                        } else if (isFirstOne) {
                            llItineraryOtherbg.setBackgroundResource(R.drawable.bottom_rounded_white_with_shadow_bg);
                        } else if (isLastOne) {
                            llItineraryOtherbg.setBackgroundResource(R.drawable.bottom_rounded_white_with_shadow_bg);
                        } else {
                            llItineraryOtherbg.setBackgroundColor(getResources().getColor(android.R.color.white));
                        }


                        if (isFirstOne && isLastOne) {
                            llItineraryAccessiblebg.setBackgroundResource(R.drawable.top_rounded_inaccessible_bg);
                        } else if (isFirstOne) {
                            llItineraryAccessiblebg.setBackgroundResource(R.drawable.top_rounded_inaccessible_bg);
                        } else if (isLastOne) {
                            llItineraryAccessiblebg.setBackgroundColor(getResources().getColor(R.color.unaccessible));
                        } else {
                            llItineraryAccessiblebg.setBackgroundColor(getResources().getColor(R.color.unaccessible));
                        }
                        llItineraryAccessiblebg.setVisibility(View.VISIBLE);
                        ivItineraryAccessibleStatus.setImageResource(R.drawable.ic_thumbsdown);

                        llItineraryOtherbg.setPadding(padding, padding, padding, padding);

                        break;

                    default:

                        if (isFirstOne && isLastOne) {
                            convertView.setBackgroundResource(R.drawable.rounded_white_with_shadow_bg);
                        } else if (isFirstOne) {
                            convertView.setBackgroundResource(R.drawable.top_rounded_white_with_shadow_bg);
                        } else if (isLastOne) {
                            convertView.setBackgroundResource(R.drawable.bottom_rounded_white_with_shadow_bg);
                        } else {
                            convertView.setBackgroundColor(getResources().getColor(android.R.color.white));
                        }


                        convertView.setPadding(padding, padding, padding, padding);

                        llItineraryAccessiblebg.setVisibility(View.GONE);
                        break;


                }


            } else {

                llItineraryAccessiblebg.setVisibility(View.GONE);

                //means only one if both are true
                if (isFirstOne && isLastOne) {
                    convertView.setBackgroundResource(R.drawable.rounded_white_with_shadow_bg);
                } else if (isFirstOne) {
                    convertView.setBackgroundResource(R.drawable.top_rounded_white_with_shadow_bg);
                } else if (isLastOne) {
                    convertView.setBackgroundResource(R.drawable.bottom_rounded_white_with_shadow_bg);
                } else {
                    convertView.setBackgroundColor(getResources().getColor(android.R.color.white));
                }

                int padding = Convert.dpToPx(20);
                convertView.setPadding(padding, padding, padding, padding);
            }


            if (showHighlight) {
                convertView.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorItineraryListHighlight)));
            } else {
                convertView.setBackgroundTintList(null);
            }


            convertView.setTag(position);
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = (Integer) view.getTag();
                    //go into the itinerary detail directly
                    if (position < itineraryArrayListOther.size()) {
                        currentSelectedItineraryIndex = position;
                        currentSelectedItineraryGroupType = Constants.GROUP_ITINERARY_TYPE_OTHER;

                        showRoutePolylineOnMap(itineraryArrayListOther.get(position));
                        lvItineraryListOther.smoothScrollToPosition(currentSelectedItineraryIndex);
                        if (itineraryListOtherAdapter != null) {
                            itineraryListOtherAdapter.notifyDataSetChanged();
                        }
                        if (itineraryListAdapterWalkTransit != null) {
                            itineraryListAdapterWalkTransit.notifyDataSetChanged();
                        }
                        if (itineraryListAdapterDriveTransit != null) {
                            itineraryListAdapterDriveTransit.notifyDataSetChanged();
                        }
                        if (itineraryListAdapterBikeTransit != null) {
                            itineraryListAdapterBikeTransit.notifyDataSetChanged();
                        }
                        if (itineraryListAdapterBikeShareTransit != null) {
                            itineraryListAdapterBikeShareTransit.notifyDataSetChanged();
                        }

                        if (itineraryListAdapterScooterTransit != null) {
                            itineraryListAdapterScooterTransit.notifyDataSetChanged();
                        }

                        moveMapToShowRoute();
                        selectedItinerary = itineraryArrayListOther.get(position);

                        enterItineraryDetailPage();

                        itineraryLegArrayList.clear();
                        itineraryLegStepArray.clear();
                        if (selectedItinerary.getLegs() != null) {
                            itineraryLegArrayList.addAll(selectedItinerary.getLegs());
                        }
                        for (ItineraryLeg itineraryLeg : itineraryLegArrayList) {
                            itineraryLegStepArray.add(false);
                        }

                        if (itineraryLegListAdapter != null) {
                            itineraryLegListAdapter.notifyDataSetInvalidated();
                        }
                        Utilities.setTotalHeightToListViewExt(gDm.widthPixels - Convert.dpToPx(30), lvItineraryLegList);
                        loadPlanTripErrorsInTripDetail();
                        loadDataInItineraryDetailHeader();
                        refreshTripDetails();
                        refreshSaveTripButton();

                        svItineraryDetail.post(new Runnable() {
                            @Override
                            public void run() {
                                svItineraryDetail.scrollTo(0, 0);
                            }
                        });
                    }
                }
            });
            return convertView;
        }
    }

    private String getModesString(String firstMode, String secondMode) {
        String modes = firstMode;
        if (modes != null && Utilities.modesStringMapping.containsKey(modes.toLowerCase())) {
            modes = systemContext.getString(Utilities.modesStringMapping.get(modes.toLowerCase()));
        }

        if (secondMode != null && Utilities.modesStringMapping.containsKey(secondMode.toLowerCase())) {
            secondMode = systemContext.getString(Utilities.modesStringMapping.get(secondMode.toLowerCase()));
        }

        if (secondMode != null && modes != null) {
            return systemContext.getString(R.string.mode_to, Utilities.capitalizeString(modes),
                    Utilities.capitalizeString(secondMode));
        } else {
            if (modes != null) {
                return Utilities.capitalizeString(modes);
            } else if (secondMode != null) {
                return Utilities.capitalizeString(secondMode);
            } else {
                return "";
            }
        }
    }

    private void loadIconAndBackgroundToMode(ImageView ivMode, ItineraryLeg itineraryLeg) {
        if (ivMode != null && itineraryLeg != null) {
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) ivMode.getLayoutParams();
            layoutParams.width = Convert.dpToPx(36);//(int)systemContext.getResources().getDimension(R.dimen.layer_icon_size);
            layoutParams.height = Convert.dpToPx(32);//(int)systemContext.getResources().getDimension(R.dimen.layer_icon_size);

            ivMode.setLayoutParams(layoutParams);
            ivMode.setPadding(Convert.dpToPx(5), Convert.dpToPx(3), Convert.dpToPx(5), Convert.dpToPx(3));

            int backgroundColor = Utilities.getModeBackgroundColorByMode(systemContext,
                    itineraryLeg.getMode().toLowerCase());

            GradientDrawable shape = new GradientDrawable();
            shape.setShape(GradientDrawable.RECTANGLE);
            float cornerRadius = Convert.dpToPx(6);
            shape.setCornerRadius(cornerRadius);
            shape.setColor(backgroundColor);
            ivMode.setBackground(shape);
            if (itineraryLeg.getMode() != null) {
                String mode = Utilities.getMappedModeForItinerary(getActivity(), itineraryLeg.getRouteId(),
                        itineraryLeg.getMode(), itineraryLeg.getRouteShortName(), itineraryLeg.getRouteLongName()).toLowerCase();
                ivMode.setImageResource(Utilities.getModeImageByMode(mode,
                        itineraryLeg.isRentedVehicle(), requireActivity(), ivMode, ivMode));
            }
            ivMode.setColorFilter(getResources().getColor(android.R.color.white));
        }
    }

    private ImageView createNormalImageView(int resID) {
        ImageView imageView = new ImageView(systemContext);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER_VERTICAL;
        imageView.setLayoutParams(params);
        imageView.setImageResource(resID);

        return imageView;
    }

    private void populateDataIntoItineraryItem(View convertView, Itinerary itinerary, boolean showHighlight,
                                               boolean isDetail, boolean isFirstOne, boolean isLastOne) {
        if (convertView != null && itinerary != null) {
            TextView tvDuration = convertView.findViewById(R.id.tv_duration);
            ConstraintLayout clDepartureTimes = convertView.findViewById(R.id.cl_departure_time);
            TextView tvWalkTime = convertView.findViewById(R.id.tv_walk_time);
            TextView tvCost = convertView.findViewById(R.id.tv_cost);
            ConstraintLayout constraintLayout = convertView.findViewById(R.id.cl_modes);

            int durationMin = itinerary.getDuration() / 60;
            if (durationMin >= 60) {
                tvDuration.setText(systemContext.getString(R.string.hr_min_with_num, durationMin / 60,
                        durationMin % 60));
            } else {
                tvDuration.setText(systemContext.getString(R.string.min_with_num, durationMin));
            }

            if (Utilities.isFeatureEnabled(Constants.FARE_INFO_ID, false)) {
                tvCost.setVisibility(View.VISIBLE);
            } else {
                tvCost.setVisibility(View.GONE);
            }

            Flow flow = new Flow(systemContext);

            int gap = Convert.dpToPx(8);
            flow.setWrapMode(Flow.WRAP_CHAIN);
            flow.setHorizontalStyle(Flow.CHAIN_PACKED);
            flow.setHorizontalAlign(Flow.HORIZONTAL_ALIGN_START);
            flow.setHorizontalBias(0f);
            flow.setVerticalGap(gap);
            flow.setHorizontalGap(gap);
            flow.setOrientation(Flow.HORIZONTAL);

            constraintLayout.removeAllViews();
            constraintLayout.addView(flow);
            LinkedHashMap<String, LinkedHashMap<String, LinkedHashMap<String, Double>>> fareTableSum = new LinkedHashMap<>();
            LinkedHashMap<String, LinkedHashMap<String, LinkedHashMap<String, LinkedHashMap<String, Double>>>> fareTablePerLeg = new LinkedHashMap<>();
            HashSet<String> productIds = new HashSet<>();

            if (itinerary.getLegs() != null && itinerary.getLegs().size() > 0) {
                ArrayList<Integer> ids = new ArrayList<>();
                String previousMode = null;
                int walkTimeSeconds = 0;
                for (int i = 0; i < itinerary.getLegs().size(); i++) {
                    ItineraryLeg itineraryLeg = itinerary.getLegs().get(i);
                    if (itineraryLeg.getMode() != null) {
                        String mode = Utilities.getMappedModeForItinerary(getActivity(), itineraryLeg.getRouteId(),
                                itineraryLeg.getMode(), itineraryLeg.getRouteShortName(), itineraryLeg.getRouteLongName()).toLowerCase();

                        if (mode.equalsIgnoreCase("walk")) {
                            walkTimeSeconds += Math.abs(itineraryLeg.getEndTime() - itineraryLeg.getStartTime()) / 1000;
                            if (itineraryLeg.getDistance() <= 400) {
                                continue;
                            }
                        }
                        if (mode.equalsIgnoreCase("bicycle") && itineraryLeg.isRentedBike()) {
                            mode = "bikeshare";
                        }
                        if (previousMode == null || !previousMode.equals(mode)) {
                            previousMode = mode;

                            View modeView = ((LayoutInflater) systemContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.view_group_itinerary_mode, null, false);
                            ImageView ivItineraryMode = modeView.findViewById(R.id.iv_itinerary_mode);
                            RelativeLayout rlItineraryMode = modeView.findViewById(R.id.rl_itinerary_mode);
                            ivItineraryMode.setColorFilter(getResources().getColor(android.R.color.white));
                            modeView.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(android.R.color.black)));
                            ivItineraryMode.setImageResource(Utilities.getModeImageByMode(mode,
                                    itineraryLeg.isRentedVehicle(), requireActivity(), ivItineraryMode, modeView));
                            modeView.setMinimumHeight(Convert.dpToPx(50));
                            modeView.setMinimumWidth(Convert.dpToPx(50));
                            int viewID = View.generateViewId();
                            modeView.setId(viewID);

                            ids.add(viewID);
                            constraintLayout.addView(modeView);
                        }

                    }

                    //update route
                    View routeNumberView = ((LayoutInflater) systemContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.view_group_itinerary_transit_number, null, false);
                    View viewRouteNumberHeader = routeNumberView.findViewById(R.id.view_route_number_header);
                    TextView tvRouteNumber = routeNumberView.findViewById(R.id.tv_route_number);

                    if (itineraryLeg.getRouteShortName() != null) {
                        tvRouteNumber.setText(itineraryLeg.getRouteShortName());
                    }

                    if (itineraryLeg.getOriginalRoute() != null) {

                        if (itineraryLeg.getOriginalRoute().getShortName() != null && !itineraryLeg.getOriginalRoute().getShortName().isEmpty()) {
                            tvRouteNumber.setText(itineraryLeg.getOriginalRoute().getShortName());
                        } else {
                            tvRouteNumber.setText(itineraryLeg.getOriginalRoute().getLongName());
                        }

                        String routeColor = itineraryLeg.getRouteColor();
                        String textColor = itineraryLeg.getRouteTextColor();

                        if (routeColor == null && itineraryLeg.getOriginalRoute() != null) {
                            routeColor = itineraryLeg.getOriginalRoute().getColor();
                            textColor = itineraryLeg.getOriginalRoute().getTextColor();
                        }

                        if (routeColor != null) {
                            if (!routeColor.startsWith("#")) {
                                routeColor = "#" + routeColor;
                            }
                            int routeColorInt = Color.parseColor(routeColor);
                            int textBackgroundColorInt = routeColorInt;
                            if (routeColorInt < 0x1000000) {
                                textBackgroundColorInt = routeColorInt + 0xAF000000;
                            }

                            viewRouteNumberHeader.setBackgroundTintList(ColorStateList.valueOf(routeColorInt));
                            tvRouteNumber.setBackgroundTintList(ColorStateList.valueOf(textBackgroundColorInt));
                        }
                        if (textColor != null) {
                            if (!textColor.startsWith("#")) {
                                textColor = "#" + textColor;
                            }
                            tvRouteNumber.setTextColor(Color.parseColor(textColor));
                        }

                        int routeNumberViewID = View.generateViewId();
                        routeNumberView.setId(routeNumberViewID);
                        ids.add(routeNumberViewID);
                        constraintLayout.addView(routeNumberView);
                    }

                    View transfer = ((LayoutInflater) systemContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.view_group_itinerary_transfer, null, false);
                    int transferViewID = View.generateViewId();
                    transfer.setId(transferViewID);
                    ids.add(transferViewID);
                    constraintLayout.addView(transfer);

                    // Update fare cost
                    if (itineraryLeg.getFareProducts() != null && !itineraryLeg.getFareProducts().isEmpty()) {
                        for (FareProducts fare : itineraryLeg.getFareProducts()) {
                            if (productIds.contains(fare.getId())) continue;
                            if (fare.getProduct() != null && fare.getProduct().getName() != null
                                    && fare.getProduct().getPrice() != null) {

                                String productName = fare.getProduct().getName();

                                String defaultID = fare.getProduct().getId();
                                if (fare.getProduct().getId() != null && fare.getProduct().getId().contains("regular")) {
                                    defaultID = "regular";
                                }
                                String riderCategoryId;
                                if (fare.getProduct().getRiderCategory() != null) {
                                    riderCategoryId = fare.getProduct().getRiderCategory().getId();
                                } else {
                                    riderCategoryId = defaultID;
                                }
                                String mediumId;
                                if (fare.getProduct().getMedium() != null) {
                                    mediumId = fare.getProduct().getMedium().getId();
                                } else {
                                    mediumId = defaultID;
                                }
                                double price = fare.getProduct().getPrice().getAmount();

                                if (!fareTableSum.containsKey(productName)) {
                                    fareTableSum.put(productName, new LinkedHashMap<>());
                                }
                                if (riderCategoryId != null && !fareTableSum.get(productName).containsKey(riderCategoryId)) {
                                    fareTableSum.get(productName).put(riderCategoryId, new LinkedHashMap<>());
                                }
                                if (riderCategoryId != null && mediumId != null && !fareTableSum.get(productName).get(riderCategoryId).containsKey(mediumId)) {
                                    fareTableSum.get(productName).get(riderCategoryId).put(mediumId, 0.0);
                                }

                                double currentValue = fareTableSum.get(productName).get(riderCategoryId).get(mediumId);
                                fareTableSum.get(productName).get(riderCategoryId).put(mediumId, currentValue + price);


                                productIds.add(fare.getId());
                            }
                        }

                        LinkedHashMap<String, LinkedHashMap<String, LinkedHashMap<String, Double>>> fareTableLeg = new LinkedHashMap<>();
                        for (FareProducts fare : itineraryLeg.getFareProducts()) {
                            if (fare.getProduct() != null && fare.getProduct().getName() != null
                                    && fare.getProduct().getPrice() != null) {

                                String productName = fare.getProduct().getName();
                                String defaultID = fare.getProduct().getId();
                                if (fare.getProduct().getId() != null && fare.getProduct().getId().contains("regular")) {
                                    defaultID = "regular";
                                }
                                String riderCategoryId;
                                if (fare.getProduct().getRiderCategory() != null) {
                                    riderCategoryId = fare.getProduct().getRiderCategory().getId();
                                } else {
                                    riderCategoryId = defaultID;
                                }
                                String mediumId;
                                if (fare.getProduct().getMedium() != null) {
                                    mediumId = fare.getProduct().getMedium().getId();
                                } else {
                                    mediumId = defaultID;
                                }
                                double price = fare.getProduct().getPrice().getAmount();

                                if (!fareTableLeg.containsKey(productName)) {
                                    fareTableLeg.put(productName, new LinkedHashMap<>());
                                }
                                if (!fareTableLeg.get(productName).containsKey(riderCategoryId)) {
                                    fareTableLeg.get(productName).put(riderCategoryId, new LinkedHashMap<>());
                                }
                                if (!fareTableLeg.get(productName).get(riderCategoryId).containsKey(mediumId)) {
                                    fareTableLeg.get(productName).get(riderCategoryId).put(mediumId, 0.0);
                                }

                                double currentValue = fareTableLeg.get(productName).get(riderCategoryId).get(mediumId);
                                fareTableLeg.get(productName).get(riderCategoryId).put(mediumId, currentValue + price);
                            }
                        }

                        String routeNumber = tvRouteNumber.getText().toString();
                        if (fareTablePerLeg.containsKey(routeNumber)) {
                            fareTablePerLeg.put(routeNumber + " ", fareTableLeg);
                        } else {
                            fareTablePerLeg.put(routeNumber, fareTableLeg);
                        }
                    }
                }

                itinerary.setFareTableSum(fareTableSum);
                // Change to the correct walk time
                itinerary.setWaitingTime(walkTimeSeconds);
                int walkTimeMin = walkTimeSeconds / 60;
                String walkingString = getString(R.string.walking);
                if (walkTimeMin >= 60) {
                    tvWalkTime.setText(String.format("%s %s", systemContext.getString(R.string.hr_min_with_num, walkTimeMin / 60,
                            walkTimeMin % 60), walkingString));
                } else {
                    if (walkTimeMin < 1 && walkTimeSeconds != 0) {
                        walkTimeMin = 1;
                    }
                    tvWalkTime.setText(String.format("%s %s", systemContext.getString(R.string.min_with_num, walkTimeMin), walkingString));
                }

                //update cost
                double cost = 0;
                try {
                    if (fareTableSum.get("rideCost") != null) {

                        cost = fareTableSum.get("rideCost").get(Configurations.FARE_PREFIX + ":regular").get(Configurations.FARE_PREFIX + ":cash");
                    } else if (fareTableSum.get("regular") != null) {
                        cost = fareTableSum.get("regular").get("regular").get("regular");
                    }
                    itinerary.setCost(cost);
                    for (Itinerary it : itinerary.getMatchedItineraries()) {
                        it.setCost(cost);
                    }
                    selectedItinerary = itinerary;

                    tvCost.setText(String.format(Locale.getDefault(), "$%.2f", cost));
                } catch (Exception e) {
                    Logger.e("ParseCost", e.toString());
                    tvCost.setText(getString(R.string.no_fare_information));
                }

                //the last one does not need transfer
                if (ids.size() > 0) {
                    constraintLayout.removeViewAt(constraintLayout.getChildCount() - 1);
                    ids.remove(ids.size() - 1);
                    int[] idsArray = new int[ids.size()];
                    for (int i = 0; i < idsArray.length; i++) {
                        idsArray[i] = ids.get(i);
                    }
                    flow.setReferencedIds(idsArray);
                }

            }

            Flow flowDepartureTimes = new Flow(systemContext);
            ArrayList<Integer> idsDepartureTimes = new ArrayList<>();
            flowDepartureTimes.setWrapMode(Flow.WRAP_CHAIN);
            flowDepartureTimes.setHorizontalStyle(Flow.CHAIN_PACKED);
            flowDepartureTimes.setHorizontalAlign(Flow.HORIZONTAL_ALIGN_START);
            flowDepartureTimes.setHorizontalBias(0f);
            flowDepartureTimes.setVerticalGap(gap);
            flowDepartureTimes.setHorizontalGap(gap);
            flowDepartureTimes.setOrientation(Flow.HORIZONTAL);

            clDepartureTimes.removeAllViews();
            clDepartureTimes.addView(flowDepartureTimes);
            View firstView = ((LayoutInflater) systemContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.view_group_itinerary_departure, null, false);
            int firstViewID = View.generateViewId();
            firstView.setId(firstViewID);
            idsDepartureTimes.add(firstViewID);
            clDepartureTimes.addView(firstView);
            TextView tvDepartureTime = firstView.findViewById(R.id.tv_departure_time);
            TextView tvYouLeave = firstView.findViewById(R.id.tv_you_leave);
            ImageView ivOnTimeIndicator = firstView.findViewById(R.id.iv_on_time_indicator);
            SpannableString firstItinerarySchedule;

            ivOnTimeIndicator.setVisibility(View.GONE);
            if (itinerary.getLegs().size() > 0) {
                for (ItineraryLeg itineraryLeg : itinerary.getLegs()) {
                    if (itineraryLeg != null && itineraryLeg.isRealTime()) {
                        ivOnTimeIndicator.setVisibility(View.VISIBLE);
                        break;
                    }
                }
            }
            if (Configurations.getInt("itinerary_sort_index", 0) == Constants.ITINERARY_SORT_BY_ARRIVE_TIME) {
                tvYouLeave.setText(getString(R.string.you_arrive));
                firstItinerarySchedule = new SpannableString(Utilities.formatTimestamp(getActivity(), itinerary.getEndTime()));
            } else {
                tvYouLeave.setText(getString(R.string.you_leave));
                firstItinerarySchedule = new SpannableString(Utilities.formatTimestamp(getActivity(), itinerary.getStartTime()));
            }

            if (isDetail) {
                tvDepartureTimes.clear();

                tvDepartureTimes.add(tvDepartureTime);
            }
            tvDepartureTime.setText(firstItinerarySchedule);

            if (isDetail) {
                tvDepartureTime.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        refreshDepartureTimes((TextView) view);
                        itineraryLegArrayList.clear();

                        if (itinerary.getLegs() != null) {
                            itineraryLegArrayList.addAll(itinerary.getLegs());
                        }

                        if (itineraryLegListAdapter != null) {
                            itineraryLegListAdapter.notifyDataSetInvalidated();
                        }

                        selectedItinerary = itinerary;
                        currentSelectedItinerary = itinerary;
                        refreshTripDetails();

                        UnderlineSpan underlineSpan = new UnderlineSpan() {

                            @Override
                            public void updateDrawState(@NonNull TextPaint ds) {
                                super.updateDrawState(ds);
                                ds.setUnderlineText(false);
                            }
                        };

                        firstItinerarySchedule.setSpan(underlineSpan, 0, firstItinerarySchedule.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        tvDepartureTime.setText(firstItinerarySchedule);


                    }
                });
            }


            if (itinerary.isMatchedToOtheritinerary() && itinerary.getMatchedItineraries().size() > 0) {
                if (tvMatchedSchedule.size() > 0) {
                    tvMatchedSchedule.clear();
                }

                for (int i = 0; i < itinerary.getMatchedItineraries().size(); i++) {
                    View view = ((LayoutInflater) systemContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.view_group_itinerary_departure, null, false);
                    int viewID = View.generateViewId();
                    view.setId(viewID);
                    idsDepartureTimes.add(viewID);
                    TextView tvMatchedDepartureTime = view.findViewById(R.id.tv_departure_time);
                    TextView tvMatchedYouLeave = view.findViewById(R.id.tv_you_leave);
                    ImageView ivMatchedOnTimeIndicator = view.findViewById(R.id.iv_on_time_indicator);
                    ivMatchedOnTimeIndicator.setVisibility(View.GONE);
                    SpannableString spannableString = null;

                    long time;
                    Itinerary matchedItinerary = itinerary.getMatchedItineraries().get(i);
                    if (matchedItinerary.getLegs().size() > 0) {
                        for (ItineraryLeg matchedItineraryLeg : matchedItinerary.getLegs()) {
                            if (matchedItineraryLeg != null && matchedItineraryLeg.isRealTime()) {
                                ivMatchedOnTimeIndicator.setVisibility(View.VISIBLE);
                                break;
                            }
                        }
                    }
                    if (Configurations.getInt("itinerary_sort_index", 0) == Constants.ITINERARY_SORT_BY_ARRIVE_TIME) {

                        time = matchedItinerary.getEndTime();
                    } else {
                        time = matchedItinerary.getStartTime();
                    }

                    if (i == itinerary.getMatchedItineraries().size() - 1) {
                        tvMatchedYouLeave.setText(getString(R.string.or));
                        spannableString = new SpannableString(Utilities.formatTimestamp(getActivity(), time));
                        if (isDetail) {
                            spannableString.setSpan(new UnderlineSpan(), 0, spannableString.length(), 0);

                        }
                        tvMatchedDepartureTime.setText(spannableString);

                    } else {
                        tvMatchedYouLeave.setText(",");
                        spannableString = new SpannableString(Utilities.formatTimestamp(getActivity(), time));
                        if (isDetail) {
                            spannableString.setSpan(new UnderlineSpan(), 0, spannableString.length(), 0);
                        }
                        tvMatchedDepartureTime.setText(spannableString);
                    }

                    tvMatchedSchedule.add(tvMatchedDepartureTime);

                    if (isDetail) {
                        tvDepartureTimes.add(tvMatchedDepartureTime);
                        int finalI = i;
                        tvMatchedDepartureTime.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                refreshDepartureTimes((TextView) view);
                                itineraryLegArrayList.clear();

                                if (itinerary.getMatchedItineraries().get(finalI).getLegs() != null) {
                                    itineraryLegArrayList.addAll(itinerary.getMatchedItineraries().get(finalI).getLegs());
                                }

                                if (itineraryLegListAdapter != null) {
                                    itineraryLegListAdapter.notifyDataSetInvalidated();
                                }

                                selectedItinerary = itinerary.getMatchedItineraries().get(finalI);
                                currentSelectedItinerary = itinerary.getMatchedItineraries().get(finalI);
                                refreshTripDetails();
                            }
                        });
                    }

                    clDepartureTimes.addView(view);
                }
            }

            if (idsDepartureTimes.size() > 0) {
                int[] idsArray = new int[idsDepartureTimes.size()];
                for (int i = 0; i < idsArray.length; i++) {
                    idsArray[i] = idsDepartureTimes.get(i);
                }
                flowDepartureTimes.setReferencedIds(idsArray);
            }
            if (isDetail) {


                LinearLayout llItineraryAccessiblebg = convertView.findViewById(R.id.ll_itinearary_accessible_bg);
                ImageView ivItineraryAccessibleStatus = convertView.findViewById(R.id.iv_itinerary_accessible_status);

                LinearLayout llItineraryOtherbg = convertView.findViewById(R.id.ll_itinerary_bg);

                int padding = Convert.dpToPx(20);

                if (Configurations.getBoolean("enabled_search_mode_accessible_routing", false)) {

                    int color = getaccessiblityForItinerary(itinerary);


                    switch (color) {

                        case R.color.accessibile:

                            llItineraryAccessiblebg.setBackgroundColor(getResources().getColor(R.color.accessibile));

                            llItineraryAccessiblebg.setVisibility(View.VISIBLE);
                            ivItineraryAccessibleStatus.setImageResource(R.drawable.ic_like);

                            llItineraryOtherbg.setPadding(padding, padding, padding, padding);

                            break;

                        case R.color.likely_acessibile:

                            llItineraryAccessiblebg.setBackgroundColor(getResources().getColor(R.color.likely_acessibile));
                            llItineraryAccessiblebg.setVisibility(View.VISIBLE);
                            ivItineraryAccessibleStatus.setImageResource(R.drawable.ic_help_menu);


                            llItineraryOtherbg.setPadding(padding, padding, padding, padding);

                            break;

                        case R.color.unaccessible:

                            llItineraryAccessiblebg.setBackgroundColor(getResources().getColor(R.color.unaccessible));
                            llItineraryAccessiblebg.setVisibility(View.VISIBLE);
                            ivItineraryAccessibleStatus.setImageResource(R.drawable.ic_thumbsdown);

                            llItineraryOtherbg.setPadding(padding, padding, padding, padding);

                            break;

                        default:

                            convertView.setBackground(null);
                            convertView.setPadding(padding, padding, padding, padding);

                            llItineraryAccessiblebg.setVisibility(View.GONE);
                            break;


                    }


                } else {

                    llItineraryAccessiblebg.setVisibility(View.GONE);

                    convertView.setBackground(null);
                    convertView.setPadding(padding, padding, padding, padding);
                }


            } else {

                LinearLayout llItineraryAccessiblebg = convertView.findViewById(R.id.ll_itinearary_accessible_bg);
                ImageView ivItineraryAccessibleStatus = convertView.findViewById(R.id.iv_itinerary_accessible_status);

                LinearLayout llItineraryOtherbg = convertView.findViewById(R.id.ll_itinerary_bg);

                if (Configurations.getBoolean("enabled_search_mode_accessible_routing", false)) {

                    int color = getaccessiblityForItinerary(itinerary);

                    int padding = Convert.dpToPx(20);


                    switch (color) {

                        case R.color.accessibile:

                            if (isFirstOne && isLastOne) {
                                llItineraryOtherbg.setBackgroundResource(R.drawable.bottom_rounded_white_with_shadow_bg);
                            } else if (isFirstOne) {
                                llItineraryOtherbg.setBackgroundResource(R.drawable.bottom_rounded_white_with_shadow_bg);
                            } else if (isLastOne) {
                                llItineraryOtherbg.setBackgroundResource(R.drawable.bottom_rounded_white_with_shadow_bg);
                            } else {
                                llItineraryOtherbg.setBackgroundColor(getResources().getColor(android.R.color.white));
                            }

                            if (isFirstOne && isLastOne) {
                                llItineraryAccessiblebg.setBackgroundResource(R.drawable.top_rounded_accessible_bg);
                            } else if (isFirstOne) {
                                llItineraryAccessiblebg.setBackgroundResource(R.drawable.top_rounded_accessible_bg);
                            } else if (isLastOne) {
                                llItineraryAccessiblebg.setBackgroundColor(getResources().getColor(R.color.accessibile));
                            } else {
                                llItineraryAccessiblebg.setBackgroundColor(getResources().getColor(R.color.accessibile));
                            }
                            llItineraryAccessiblebg.setVisibility(View.VISIBLE);
                            ivItineraryAccessibleStatus.setImageResource(R.drawable.ic_like);

                            llItineraryOtherbg.setPadding(padding, padding, padding, padding);

                            break;

                        case R.color.likely_acessibile:

                            if (isFirstOne && isLastOne) {
                                llItineraryOtherbg.setBackgroundResource(R.drawable.bottom_rounded_white_with_shadow_bg);
                            } else if (isFirstOne) {
                                llItineraryOtherbg.setBackgroundResource(R.drawable.bottom_rounded_white_with_shadow_bg);
                            } else if (isLastOne) {
                                llItineraryOtherbg.setBackgroundResource(R.drawable.bottom_rounded_white_with_shadow_bg);
                            } else {
                                llItineraryOtherbg.setBackgroundColor(getResources().getColor(android.R.color.white));
                            }


                            if (isFirstOne && isLastOne) {
                                llItineraryAccessiblebg.setBackgroundResource(R.drawable.top_rounded_likely_accessible_bg);
                            } else if (isFirstOne) {
                                llItineraryAccessiblebg.setBackgroundResource(R.drawable.top_rounded_likely_accessible_bg);
                            } else if (isLastOne) {
                                llItineraryAccessiblebg.setBackgroundColor(getResources().getColor(R.color.likely_acessibile));
                            } else {
                                llItineraryAccessiblebg.setBackgroundColor(getResources().getColor(R.color.likely_acessibile));
                            }
                            llItineraryAccessiblebg.setVisibility(View.VISIBLE);
                            ivItineraryAccessibleStatus.setImageResource(R.drawable.ic_help_menu);


                            llItineraryOtherbg.setPadding(padding, padding, padding, padding);

                            break;

                        case R.color.unaccessible:

                            if (isFirstOne && isLastOne) {
                                llItineraryOtherbg.setBackgroundResource(R.drawable.bottom_rounded_white_with_shadow_bg);
                            } else if (isFirstOne) {
                                llItineraryOtherbg.setBackgroundResource(R.drawable.bottom_rounded_white_with_shadow_bg);
                            } else if (isLastOne) {
                                llItineraryOtherbg.setBackgroundResource(R.drawable.bottom_rounded_white_with_shadow_bg);
                            } else {
                                llItineraryOtherbg.setBackgroundColor(getResources().getColor(android.R.color.white));
                            }


                            if (isFirstOne && isLastOne) {
                                llItineraryAccessiblebg.setBackgroundResource(R.drawable.top_rounded_inaccessible_bg);
                            } else if (isFirstOne) {
                                llItineraryAccessiblebg.setBackgroundResource(R.drawable.top_rounded_inaccessible_bg);
                            } else if (isLastOne) {
                                llItineraryAccessiblebg.setBackgroundColor(getResources().getColor(R.color.unaccessible));
                            } else {
                                llItineraryAccessiblebg.setBackgroundColor(getResources().getColor(R.color.unaccessible));
                            }
                            llItineraryAccessiblebg.setVisibility(View.VISIBLE);
                            ivItineraryAccessibleStatus.setImageResource(R.drawable.ic_thumbsdown);

                            llItineraryOtherbg.setPadding(padding, padding, padding, padding);

                            break;

                        default:

                            if (isFirstOne && isLastOne) {
                                convertView.setBackgroundResource(R.drawable.rounded_white_with_shadow_bg);
                            } else if (isFirstOne) {
                                convertView.setBackgroundResource(R.drawable.top_rounded_white_with_shadow_bg);
                            } else if (isLastOne) {
                                convertView.setBackgroundResource(R.drawable.bottom_rounded_white_with_shadow_bg);
                            } else {
                                convertView.setBackgroundColor(getResources().getColor(android.R.color.white));
                            }


                            convertView.setPadding(padding, padding, padding, padding);

                            llItineraryAccessiblebg.setVisibility(View.GONE);
                            break;


                    }


                } else {

                    llItineraryAccessiblebg.setVisibility(View.GONE);

                    //means only one if both are true
                    if (isFirstOne && isLastOne) {
                        convertView.setBackgroundResource(R.drawable.rounded_white_with_shadow_bg);
                    } else if (isFirstOne) {
                        convertView.setBackgroundResource(R.drawable.top_rounded_white_with_shadow_bg);
                    } else if (isLastOne) {
                        convertView.setBackgroundResource(R.drawable.bottom_rounded_white_with_shadow_bg);
                    } else {
                        convertView.setBackgroundColor(getResources().getColor(android.R.color.white));
                    }

                    int padding = Convert.dpToPx(20);
                    convertView.setPadding(padding, padding, padding, padding);
                }


                if (showHighlight) {
                    convertView.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorItineraryListHighlight)));
                } else {
                    convertView.setBackgroundTintList(null);
                }
            }


        }
    }

    private int getaccessiblityForItinerary(Itinerary itinerary) {
        if (itinerary.getAccessibilityScore() != null) {
            if (itinerary.getLegs() != null) {
                double averageScore = 0.0;

                averageScore = itinerary.getAccessibilityScore();

                if (averageScore < 0.5) {
                    return R.color.unaccessible;
                } else if (averageScore >= 0.5 && averageScore < 1.0) {
                    return R.color.likely_acessibile;
                } else if (averageScore >= 1.0) {
                    return R.color.accessibile;
                }
            }
        }
        return R.color.white;
    }


    private void refreshDepartureTimes(TextView view) {
        if (view != null && tvDepartureTimes.size() > 0) {
            String currentHighlightText = view.getText().toString();
            view.setText(currentHighlightText);

            for (int i = 0; i < tvDepartureTimes.size(); i++) {
                TextView other = tvDepartureTimes.get(i);
                if (!other.equals(view)) {
                    String otherText = other.getText().toString();
                    SpannableString spannableString = new SpannableString(otherText);

                    spannableString.setSpan(new UnderlineSpan(), 0, otherText.length(), 0);
                    other.setText(spannableString);
                }
            }
        }
    }

    private void setupRouteSearchAddressLayout() {
        rlRouteSearchAddress = mFragmentView.findViewById(R.id.rl_route_search_address);
        ivRouteSearchAddressSwitch = mFragmentView.findViewById(R.id.iv_route_search_address_switch);
        tvRouteSearchOrigin = mFragmentView.findViewById(R.id.tv_route_search_origin);
        tvRouteSearchDestination = mFragmentView.findViewById(R.id.tv_route_search_destination);
        pbItineraryList = mFragmentView.findViewById(R.id.pb_itinerary_list);
        llPlanTripErrors = mFragmentView.findViewById(R.id.ll_plan_trip_errors);

        ll_lists_holder = mFragmentView.findViewById(R.id.ll_lists_holder);

        transitWalkListView = getLayoutInflater().inflate(R.layout.widget_transit_and_other_list, null, false);
        lvItineraryListWalkTransit = transitWalkListView.findViewById(R.id.lv_itinerary_list_transit);
        tvWalkTransitTitle = transitWalkListView.findViewById(R.id.tv_transit_title);
        tvWalkTransitTitle.setText(getString(R.string.walk_transit));
        ll_lists_holder.addView(transitWalkListView);

        transitBikeListView = getLayoutInflater().inflate(R.layout.widget_transit_and_other_list, null, false);
        lvItineraryListBikeTransit = transitBikeListView.findViewById(R.id.lv_itinerary_list_transit);
        tvBikeTransitTitle = transitBikeListView.findViewById(R.id.tv_transit_title);
        tvBikeTransitTitle.setText(getString(R.string.bike_transit));
        ll_lists_holder.addView(transitBikeListView);

        transitBikeshareListView = getLayoutInflater().inflate(R.layout.widget_transit_and_other_list, null, false);
        lvItineraryListBikeShareTransit = transitBikeshareListView.findViewById(R.id.lv_itinerary_list_transit);
        tvBikeShareTransitTitle = transitBikeshareListView.findViewById(R.id.tv_transit_title);
        tvBikeShareTransitTitle.setText(getString(R.string.bikeshare_transit));
        ll_lists_holder.addView(transitBikeshareListView);

        transitScooterListView = getLayoutInflater().inflate(R.layout.widget_transit_and_other_list, null, false);
        lvItineraryListScooterTransit = transitScooterListView.findViewById(R.id.lv_itinerary_list_transit);
        tvScooterTransitTitle = transitScooterListView.findViewById(R.id.tv_transit_title);
        tvScooterTransitTitle.setText(getString(R.string.scooter_transit));
        ll_lists_holder.addView(transitScooterListView);

        transitDriveListView = getLayoutInflater().inflate(R.layout.widget_transit_and_other_list, null, false);
        lvItineraryListDriveTransit = transitDriveListView.findViewById(R.id.lv_itinerary_list_transit);
        tvDriveTransitTitle = transitDriveListView.findViewById(R.id.tv_transit_title);
        tvDriveTransitTitle.setText(getString(R.string.drive_transit));
        ll_lists_holder.addView(transitDriveListView);

        lvItineraryListOther = mFragmentView.findViewById(R.id.lv_itinerary_list_other);

        tvOtherTitle = mFragmentView.findViewById(R.id.tv_other_title);

        tvItinerariesFound = mFragmentView.findViewById(R.id.tv_itineraries_found);

        rlRouteSearchAddressCollapsed = mFragmentView.findViewById(R.id.rl_route_search_address_collapsed);
        llRouteSearchAddressExpanded = mFragmentView.findViewById(R.id.ll_route_search_address_expanded);
        ivRouteSearchAddressCollapsedBack = mFragmentView.findViewById(R.id.iv_route_search_address_collapsed_back);
        ivRouteSearchAddressCollapsedExpand = mFragmentView.findViewById(R.id.iv_route_search_address_collapsed_expand);
        ivRouteSearchAddressCollapse = mFragmentView.findViewById(R.id.iv_route_search_address_collapse);
        ivRouteSearchAddressCollapsedExpand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rlRouteSearchAddressCollapsed.setVisibility(View.GONE);
                llRouteSearchAddressExpanded.setVisibility(View.VISIBLE);
            }
        });

        ivRouteSearchAddressCollapse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rlRouteSearchAddressCollapsed.setVisibility(View.VISIBLE);
                llRouteSearchAddressExpanded.setVisibility(View.GONE);
            }
        });

        itineraryListAdapterWalkTransit = getItineraryListAdapter(itineraryArrayListWalkTransit,
                Constants.GROUP_ITINERARY_TYPE_WALK_TRANSIT);
        lvItineraryListWalkTransit.setAdapter(itineraryListAdapterWalkTransit);

        itineraryListAdapterBikeTransit = getItineraryListAdapter(itineraryArrayListBikeTransit,
                Constants.GROUP_ITINERARY_TYPE_BIKE_TRANSIT);
        lvItineraryListBikeTransit.setAdapter(itineraryListAdapterBikeTransit);

        itineraryListAdapterBikeShareTransit = getItineraryListAdapter(itineraryArrayListBikeShareTransit,
                Constants.GROUP_ITINERARY_TYPE_BIKESHARE_TRANSIT);
        lvItineraryListBikeShareTransit.setAdapter(itineraryListAdapterBikeShareTransit);

        itineraryListAdapterScooterTransit = getItineraryListAdapter(itineraryArrayListScooterTransit,
                Constants.GROUP_ITINERARY_TYPE_SCOOTER_TRANSIT);
        lvItineraryListScooterTransit.setAdapter(itineraryListAdapterScooterTransit);

        itineraryListAdapterDriveTransit = getItineraryListAdapter(itineraryArrayListDriveTransit,
                Constants.GROUP_ITINERARY_TYPE_DRIVE_TRANSIT);
        lvItineraryListDriveTransit.setAdapter(itineraryListAdapterDriveTransit);

        itineraryListOtherAdapter = getItineraryListOtherAdapter();
        lvItineraryListOther.setAdapter(itineraryListOtherAdapter);

        ImageView ivRouteSearchBarBack = mFragmentView.findViewById(R.id.iv_route_search_bar_back);
        LinearLayout llRouteSearchOrigin = mFragmentView.findViewById(R.id.ll_route_search_origin);
        LinearLayout llRouteSearchDestination = mFragmentView.findViewById(R.id.ll_route_search_destination);

        View.OnClickListener goBackToRouteSearchReadyListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goBackToItineraryListMode();

            }
        };

        ivRouteSearchAddressCollapsedBack.setOnClickListener(goBackToRouteSearchReadyListener);

        ivRouteSearchBarBack.setOnClickListener(goBackToRouteSearchReadyListener);
        ivRouteSearchAddressSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchAddress();
                goBackToRouteSearchReady();
            }
        });

        tvRouteSearchOrigin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                gotoAddressSearchPage(true);
                goBackToRouteSearchReady();
            }
        });

        tvRouteSearchDestination.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                gotoAddressSearchPage(false);
                goBackToRouteSearchReady();
            }
        });

    }

    private void loadPlanTripErrorsInTripDetail() {

        if (llPlanTripDetailErrors != null && llPlanTripDetailErrors.getChildCount() > 0) {
            llPlanTripDetailErrors.removeAllViews();
        }

        if (routingErrorsArrayList != null && configRouteErrors != null && routingErrorsArrayList.size() > 0) {

            for (int i = 0; i < routingErrorsArrayList.size(); i++) {

                RoutingErrors routingError = routingErrorsArrayList.get(i);


                for (int j = 0; j < configRouteErrors.size(); j++) {

                    TripError cfgTripError = configRouteErrors.get(j);


                    if (routingError.getCode() != null && cfgTripError.getErrorCode() != null && !routingError.getCode().isEmpty() && !cfgTripError.getErrorCode().isEmpty() &&
                            routingError.getCode().equalsIgnoreCase(cfgTripError.getErrorCode())) {

                        String errorTitleMesage = "";
                        String errorDescrptionMessage = "";

                        String fromText = "";
                        String toText = "";
                        String locationtext = "";

                        if (cfgTripError.getDisplaySubText().contains("$0 $1")) {

                            if (routingError.getInputField().contains("FROM")) {

                                fromText = "Origin";

                            }

                            if (routingError.getInputField().contains("TO")) {

                                toText = "Destination";

                            }

                            if (!fromText.isEmpty() && !toText.isEmpty()) {
                                locationtext = "locations are";
                            } else if (!fromText.isEmpty() || !toText.isEmpty()) {
                                locationtext = "location is";
                            }


                            if (!fromText.isEmpty()) {
                                errorDescrptionMessage = cfgTripError.getDisplaySubText().replace("$0 $1", fromText + " " + locationtext);
                            }
                            if (!toText.isEmpty()) {
                                errorDescrptionMessage = cfgTripError.getDisplaySubText().replace("$0 $1", toText + " " + locationtext);
                            }
                            if (!fromText.isEmpty() && !toText.isEmpty()) {
                                errorDescrptionMessage = cfgTripError.getDisplaySubText().replace("$0 $1", fromText + " and " + toText + " " + locationtext);
                            }


                            errorTitleMesage = cfgTripError.getDisplayText();


                        } else {


                            errorDescrptionMessage = cfgTripError.getDisplaySubText();
                            errorTitleMesage = cfgTripError.getDisplayText();

                        }

                        if (errorTitleMesage.equalsIgnoreCase("Transit isn't the fastest way to make this trip")) {

                            errorTitleMesage = "";
                            routingErrorsArrayList.remove(i);
                            i--;

                        }


                        if (!errorDescrptionMessage.isEmpty() && !errorTitleMesage.isEmpty()) {


                            View view = getLayoutInflater().inflate(R.layout.item_plan_trip_error, null, false);
                            TextView tvPlanTripError = view.findViewById(R.id.tv_plan_trip_error);
                            TextView tvTitlePlanTripError = view.findViewById(R.id.tv_title_plan_trip_error);
                            ImageView ivPlanTripError = view.findViewById(R.id.iv_item_plan_trip_error);
                            tvPlanTripError.setText(errorDescrptionMessage);
                            tvTitlePlanTripError.setText(errorTitleMesage);
                            tvPlanTripError.setTextColor(getResources().getColor(R.color.colorBlack));
                            tvTitlePlanTripError.setTextColor(getResources().getColor(R.color.colorBlack));
                            ivPlanTripError.setColorFilter(getResources().getColor(R.color.colorBlack));
                            if (llPlanTripDetailErrors != null) {
                                llPlanTripDetailErrors.addView(view);
                            }
                        }


                    }


                }


            }

            //Get rid of the trip errors to match web
            llPlanTripDetailErrors.setVisibility(View.GONE);

        } else {
            llPlanTripDetailErrors.setVisibility(View.GONE);
        }


    }


    private void loadDataInItineraryDetailHeader() {

        int resID = 0;

        if (Utilities.checkIfToDisplayAccessibilityUI(requireActivity())) {
            resID = R.layout.item_group_itinerary_list_accessibility;
        } else {
            resID = R.layout.item_group_itinerary_list;

        }


        rlItineraryDetailHeader.removeAllViews();
        View itineraryItemView = LayoutInflater.from(systemContext).inflate(resID, null);
        populateDataIntoItineraryItem(itineraryItemView, selectedItinerary, false,
                true, false, false);

        rlItineraryDetailHeader.addView(itineraryItemView,
                new RelativeLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT));

    }

    private void refreshTripDetails() {
        double walkDuration = 0.0;
        double bikeDuration = 0.0;
        if (selectedItinerary != null) {
            if (selectedItinerary.getLegs() != null) {
                for (ItineraryLeg itineraryLeg : selectedItinerary.getLegs()) {
                    if (itineraryLeg != null) {
                        String itineraryMode = itineraryLeg.getMode().toLowerCase();

                        if (itineraryMode.equals("bike") || itineraryMode.equals("bicycle")) {
                            bikeDuration = bikeDuration + itineraryLeg.getDuration();
                        } else if (itineraryMode.equals("walk")) {
                            walkDuration = walkDuration + itineraryLeg.getDuration();
                        }
                    }
                }


                if (selectedItinerary.getCost() > 0) {
                    llItineraryDetailFare.setVisibility(View.VISIBLE);
                    tvItineraryDetailFare.setText(String.format(Locale.getDefault(), "$%.2f", selectedItinerary.getCost()));
                    tvItineraryDetailFareAccessibility.setText(String.format(Locale.getDefault(), "$%.2f", selectedItinerary.getCost()));
                } else {
                    llItineraryDetailFare.setVisibility(View.GONE);
                    tvItineraryDetailFareAccessibility.setVisibility(View.GONE);
                }

                if (Configurations.getBoolean("enable_itinerary_calories_burned", true)) {
                    if (walkDuration > 0 || bikeDuration > 0) {
                        llItineraryDetailCaloriesBurned.setVisibility(View.VISIBLE);

                        String walk;
                        String bike;
                        String totalDuration;
                        if (walkDuration >= 120 || walkDuration < 60) {
                            walk = getString(R.string.minutes_with_num, (int) walkDuration / 60);
                        } else {
                            walk = getString(R.string.minute_with_num, (int) walkDuration / 60);
                        }
                        if (bikeDuration >= 120 || bikeDuration < 60) {
                            bike = getString(R.string.minutes_with_num, (int) bikeDuration / 60);
                        } else {
                            bike = getString(R.string.minute_with_num, (int) bikeDuration / 60);
                        }
                        tvCaloriesBurnedDetail.setText(Html.fromHtml(getString(R.string.calories_burned_detail, walk, bike)));

                        if ((walkDuration + bikeDuration) >= 120 || (walkDuration + bikeDuration < 60)) {
                            totalDuration = getString(R.string.minutes_with_num, (int) (walkDuration + bikeDuration) / 60);
                        } else {
                            totalDuration = getString(R.string.minute_with_num, (int) (walkDuration + bikeDuration) / 60);
                        }

                        tvItineraryDetailCaloriesBurned.setText(totalDuration);
                    } else {
                        llItineraryDetailCaloriesBurned.setVisibility(View.GONE);
                        llCaloriesBurnedDetail.setVisibility(View.GONE);
                        if (llMoreInfoCaloriesBurned != null) {
                            llMoreInfoCaloriesBurned.setContentDescription(getString(R.string.calories_info_show_details));
                            llMoreInfoCaloriesBurnedAccessibility.setContentDescription(getString(R.string.calories_info_show_details));
                        }
                    }
                } else {
                    llItineraryDetailCaloriesBurned.setVisibility(View.GONE);
                    llCaloriesBurnedDetail.setVisibility(View.GONE);
                }
            }

            SimpleDateFormat simpleDateFormatForDate = new SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault());
            Date dt = new Date();
            dt.setTime(selectedItinerary.getStartTime());

            tvItineraryDetailDate.setText(simpleDateFormatForDate.format(dt));
            tvItineraryDetailTime.setText(Utilities.formatTimestamp(getActivity(), selectedItinerary.getStartTime()));

            tvItineraryDetailDateAccessibility.setText(simpleDateFormatForDate.format(dt));
            tvItineraryDetailTimeAccessibility.setText(Utilities.formatTimestamp(getActivity(), selectedItinerary.getStartTime()));
        }
    }

    private void setupServiceAlerts() {

        ivAlertsClose = mFragmentView.findViewById(R.id.iv_alerts_close);
        viewRouteHeaderAlerts = mFragmentView.findViewById(R.id.view_route_number_header_alerts);
        tvRouteNumberAlerts = mFragmentView.findViewById(R.id.tv_itinerary_leg_route_number_alerts);
        lvServiceAlerts = mFragmentView.findViewById(R.id.lv_service_alert);
        rlServiceAlerts = mFragmentView.findViewById(R.id.rl_service_alerts);
        tvNumberOfAlerts = mFragmentView.findViewById(R.id.tv_numbers_of_alerts);
        tvNumberOfAlertsAccessbility = mFragmentView.findViewById(R.id.tv_numbers_of_alerts_accessibility);

        serviceAlertAdapter = getServiceAlertAdapter();
        lvServiceAlerts.setAdapter(serviceAlertAdapter);

        ivAlertsClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rlServiceAlerts.setVisibility(View.GONE);
            }
        });


    }


    private void setupItineraryDetail() {
        svItineraryDetail = mFragmentView.findViewById(R.id.sv_itinerary_detail);
        llItineraryDetail = mFragmentView.findViewById(R.id.ll_itinerary_detail);
        ivItineraryDetailExpander = mFragmentView.findViewById(R.id.iv_itinerary_detail_expander);
        ImageView ivItineraryDetailBack = mFragmentView.findViewById(R.id.iv_itinerary_detail_back);

        llPlanTripDetailErrors = mFragmentView.findViewById(R.id.ll_plan_trip_detail_errors);

        ivSaveTrip = mFragmentView.findViewById(R.id.iv_save_trip);
        rlItineraryDetailHeader = mFragmentView.findViewById(R.id.rl_itinerary_detail_header);
        rlStartRoute = mFragmentView.findViewById(R.id.rl_start_route);

        rlStartRoute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ActiveRouteActivity.class);

                intent.putExtra("current_route", selectedItinerary);
                intent.putExtra("origin", originPoint);
                intent.putExtra("destination", destinationPoint);
                if (gMapboxMap != null && gMapboxMap.getCameraPosition() != null) {
                    intent.putExtra("current_map_zoom_level", gMapboxMap.getCameraPosition().zoom);
                }
                startActivity(intent);
            }
        });
        llItineraryDetailCaloriesBurned = mFragmentView.findViewById(R.id.ll_itinerary_detail_calories_burned);
        tvCaloriesBurnedDetail = mFragmentView.findViewById(R.id.tv_calories_burned_detail);
        llCaloriesBurnedDetail = mFragmentView.findViewById(R.id.ll_calories_burned_detail);
        ivItineraryDetailCaloriesBurned = mFragmentView.findViewById(R.id.iv_itinerary_detail_calories_burned);
        llMoreInfoCaloriesBurned = mFragmentView.findViewById(R.id.ll_more_info_on_calories_burned);
        ivCaloriesBurnedDetailClose = mFragmentView.findViewById(R.id.iv_calories_burned_detail_close);
        rlCaloriesBurnedDetailClose = mFragmentView.findViewById(R.id.rl_calories_burned_detail_close);

        llTripDetails = mFragmentView.findViewById(R.id.ll_trip_details);
        tvItineraryDetailDate = mFragmentView.findViewById(R.id.tv_itinerary_detail_date);
        tvItineraryDetailTime = mFragmentView.findViewById(R.id.tv_itinerary_detail_time);
        tvItineraryDetailDateAt = mFragmentView.findViewById(R.id.tv_itinerary_detail_date_at);

        tvItineraryDetailDateAccessibility = mFragmentView.findViewById(R.id.tv_itinerary_detail_date_accessibility);
        tvItineraryDetailTimeAccessibility = mFragmentView.findViewById(R.id.tv_itinerary_detail_time_accessibility);
        llDetailDateAndTimeAccessibility = mFragmentView.findViewById(R.id.ll_itinerary_detail_date_and_time_accessibility);
        tvItineraryDetailFareAccessibility = mFragmentView.findViewById(R.id.tv_itinerary_detail_fare_accessibility);
        tvItineraryDetailCaloriesBurnedAccessibility = mFragmentView.findViewById(R.id.tv_itinerary_detail_calories_burned_accessibility);
        llMoreInfoCaloriesBurnedAccessibility = mFragmentView.findViewById(R.id.ll_more_info_on_calories_burned_accessibility);
        llCalorieDetailBurnedAccessibility = mFragmentView.findViewById(R.id.ll_itinerary_detail_calories_burned_accessibility);


        tvItineraryDetailCaloriesBurned = mFragmentView.findViewById(R.id.tv_itinerary_detail_calories_burned);
        tvItineraryDetailFare = mFragmentView.findViewById(R.id.tv_itinerary_detail_fare);
        tvItineraryFareText = mFragmentView.findViewById(R.id.tv_itinerary_fare_text);

        llItineraryDetailFare = mFragmentView.findViewById(R.id.ll_itinerary_detail_fare);


        lvItineraryLegList = mFragmentView.findViewById(R.id.lv_itinerary_leg_list);
        itineraryLegListAdapter = getItineraryLegListAdapter();
        lvItineraryLegList.setAdapter(itineraryLegListAdapter);

        TextView tvCaloriesBurnedGuidelines = mFragmentView.findViewById(R.id.tv_calories_burned_guidelines);

        if (Utilities.checkIfToDisplayAccessibilityUI(requireActivity())) {
            llDetailDateAndTimeAccessibility.setVisibility(View.VISIBLE);
            tvItineraryDetailFareAccessibility.setVisibility(View.VISIBLE);
            llCalorieDetailBurnedAccessibility.setVisibility(View.VISIBLE);
            tvItineraryDetailDate.setVisibility(View.GONE);
            tvItineraryDetailTime.setVisibility(View.GONE);
            tvItineraryDetailDateAt.setVisibility(View.GONE);
            tvItineraryDetailFare.setVisibility(View.GONE);
            tvItineraryDetailCaloriesBurned.setVisibility(View.GONE);
            llMoreInfoCaloriesBurned.setVisibility(View.GONE);


        } else {

            llDetailDateAndTimeAccessibility.setVisibility(View.GONE);
            tvItineraryDetailFareAccessibility.setVisibility(View.GONE);
            llCalorieDetailBurnedAccessibility.setVisibility(View.GONE);
            tvItineraryDetailDate.setVisibility(View.VISIBLE);
            tvItineraryDetailTime.setVisibility(View.VISIBLE);
            tvItineraryDetailDateAt.setVisibility(View.VISIBLE);
            tvItineraryDetailFare.setVisibility(View.VISIBLE);
            tvItineraryDetailCaloriesBurned.setVisibility(View.VISIBLE);
            llMoreInfoCaloriesBurned.setVisibility(View.VISIBLE);


        }

        tvCaloriesBurnedGuidelines.setText(Html.fromHtml(getString(R.string.calories_burned_guidelines)));
        tvCaloriesBurnedGuidelines.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utilities.GotoTheUrl(systemContext, Constants.CALORIES_GUIDELINES_URL);
            }
        });

        ivItineraryDetailBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goBackToItineraryListMode();
            }
        });

        llMoreInfoCaloriesBurned.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (llCaloriesBurnedDetail.getVisibility() == View.VISIBLE) {
                    llCaloriesBurnedDetail.setVisibility(View.GONE);
                    llMoreInfoCaloriesBurned.setContentDescription(getString(R.string.calories_info_show_details));
                    llMoreInfoCaloriesBurnedAccessibility.setContentDescription(getString(R.string.calories_info_show_details));

                } else {
                    llCaloriesBurnedDetail.setVisibility(View.VISIBLE);
                    llMoreInfoCaloriesBurned.setContentDescription(getString(R.string.calories_info_hide_details));
                    llMoreInfoCaloriesBurnedAccessibility.setContentDescription(getString(R.string.calories_info_hide_details));
                }
            }
        });


        llMoreInfoCaloriesBurnedAccessibility.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (llCaloriesBurnedDetail.getVisibility() == View.VISIBLE) {
                    llCaloriesBurnedDetail.setVisibility(View.GONE);
                    llMoreInfoCaloriesBurned.setContentDescription(getString(R.string.calories_info_show_details));
                    llMoreInfoCaloriesBurnedAccessibility.setContentDescription(getString(R.string.calories_info_show_details));

                } else {
                    llCaloriesBurnedDetail.setVisibility(View.VISIBLE);
                    llMoreInfoCaloriesBurned.setContentDescription(getString(R.string.calories_info_hide_details));
                    llMoreInfoCaloriesBurnedAccessibility.setContentDescription(getString(R.string.calories_info_hide_details));
                }
            }
        });

        rlCaloriesBurnedDetailClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                llCaloriesBurnedDetail.setVisibility(View.GONE);
                if (llMoreInfoCaloriesBurned != null) {
                    llMoreInfoCaloriesBurned.setContentDescription(getString(R.string.calories_info_show_details));
                    llMoreInfoCaloriesBurnedAccessibility.setContentDescription(getString(R.string.calories_info_show_details));
                }
            }
        });

        ivSaveTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (Utilities.itineraryCanBeMonitored(selectedItinerary)) {
                        boolean loggedIn = Utilities.isLoggedIn();

                        if (loggedIn) {
                            //save new trip
                            retrieveUserInfo(Configurations.getString("account_access_token", ""));

                        } else {
                            logoutHandling();
                            showNativeAuth();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        ivItineraryDetailExpander.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        currentY = (int) event.getRawY();

                        break;

                    case MotionEvent.ACTION_MOVE: {
                        int dy = (int) event.getRawY() - currentY;

                        setItineraryDetailHeight(dy);
                    }
                    break;

                    case MotionEvent.ACTION_UP:
                        if (llItineraryDetail != null) {
                            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) llItineraryDetail.getLayoutParams();
                            int dy = (int) event.getRawY() - currentY;
                            int minHeight = Convert.dpToPx(Constants.ITINERARY_DETAIL_MIN_HEIGHT);
                            int maxHeight = gDm.heightPixels - Convert.dpToPx(Constants.ITINERARY_DETAIL_MAX_HEIGHT);

                            int endHeight = currentHeight - dy;
                            if (endHeight > (maxHeight + minHeight) / 2) {
                                if (endHeight > (maxHeight * 3 / 4 + minHeight / 4)) {
                                    layoutParams.height = maxHeight;
                                } else {
                                    layoutParams.height = (maxHeight + minHeight) / 2;
                                }
                            } else {
                                if (endHeight > (minHeight * 3 / 4 + maxHeight / 4)) {
                                    layoutParams.height = (maxHeight + minHeight) / 2;
                                } else {
                                    layoutParams.height = minHeight;
                                }
                            }
                            llItineraryDetail.setLayoutParams(layoutParams);

                            currentHeight = layoutParams.height;

                            if (currentLegGeometry != null) {
                                moveMapToShowItineraryLeg();
                            } else {
                                moveMapToShowRouteInDetail();
                            }
                        }

                        break;
                }
                return true;
            }
        });

        initItineraryDetailHeight();
    }

    private void gotoVerifyEmailActivity() {
        Intent intent = new Intent(getActivity(), VerifyEmailActivity.class);

        startActivity(intent);
    }

    private void retrieveUserInfo(String accessToken) {
        Auth0 account = new Auth0(Configurations.getString("auth0_client_id", ""), Configurations.getString("auth0_domain", ""));
        Callback<UserProfile, AuthenticationException> userInfoCallback = new Callback<UserProfile, AuthenticationException>() {
            @Override
            public void onSuccess(UserProfile userProfile) {
                Gson gson = new Gson();
                Configurations.setString("account_user_profile", gson.toJson(userProfile));

                if (userProfile != null) {
                    if (Boolean.TRUE.equals(userProfile.isEmailVerified())) {
                        enterSaveNewTripMode();
                        disableAccessbilityForSaveTrip();
                    } else {
                        gotoVerifyEmailActivity();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull AuthenticationException e) {
                if (e.getStatusCode() == 401) {
                    Utilities.showNormalPopupDialog(getActivity(), "", getString(R.string.retrieve_user_info_failed_unauthorized));
                    logoutHandling();
                    showNativeAuth();
                } else {
                    Utilities.showNormalPopupDialog(getActivity(), "", getString(R.string.retrieve_user_info_failed_other));
                }
            }
        };
        AuthenticationAPIClient authenticationAPIClient = new AuthenticationAPIClient(account);
        authenticationAPIClient.userInfo(accessToken).start(userInfoCallback);
    }

    private HashMap<String, List<String>> getData() {
        HashMap<String, List<String>> expandableListDetail = new HashMap<>();

        // Level 1
        List<String> adult = new ArrayList<>();
        adult.add("Cash");
        adult.add("Orca");
        adult.add("Orca Lift");

        // Level 2
        List<String> youth = new ArrayList<>();
        youth.add("Cash");
        youth.add("Orca");
        youth.add("Orca Lift");

        expandableListDetail.put("Adult", adult);
        expandableListDetail.put("Youth", youth);

        // Level 3 (Grandchildren)
        for (String option : adult) {
            List<String> grandchildren = new ArrayList<>();
            grandchildren.add("Eline");
            grandchildren.add("Swife");
            grandchildren.add("Blue");
            grandchildren.add("90x");
            grandchildren.add("70x");
            expandableListDetail.put(option, grandchildren);
        }

        for (String option : youth) {
            List<String> grandchildren = new ArrayList<>();
            grandchildren.add("Eline");
            grandchildren.add("Swife");
            grandchildren.add("Blue");
            grandchildren.add("90x");
            grandchildren.add("70x");
            expandableListDetail.put(option, grandchildren);
        }

        return expandableListDetail;
    }

    private void disableAccessbilityForSaveTrip() {

        rlMapView.setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_NO_HIDE_DESCENDANTS);
        if (ll_Bottom_tab != null) {
            ll_Bottom_tab.setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_NO_HIDE_DESCENDANTS);
        }


    }

    private void enableAccessbilityForSaveTrip() {
        rlMapView.setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_AUTO);
        if (ll_Bottom_tab != null) {
            ll_Bottom_tab.setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_AUTO);
        }
    }

    private void showMaxItineraryDetailHeight() {
        if (getActivity() != null && llItineraryDetail != null) {
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) llItineraryDetail.getLayoutParams();
            layoutParams.width = gDm.widthPixels;
            layoutParams.height = Convert.dpToPx(Constants.ITINERARY_DETAIL_INITIAL_HEIGHT);
            currentHeight = layoutParams.height;
            llItineraryDetail.setLayoutParams(layoutParams);
        }
    }

    private void initItineraryDetailHeight() {
        gDm = new DisplayMetrics();
        if (getActivity() != null && llItineraryDetail != null) {
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(gDm);
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) llItineraryDetail.getLayoutParams();
            layoutParams.width = gDm.widthPixels;
            layoutParams.height = Convert.dpToPx(Constants.ITINERARY_DETAIL_MIN_HEIGHT);
            currentHeight = layoutParams.height;
            llItineraryDetail.setLayoutParams(layoutParams);
        }
    }

    private void logoutHandling() {
        Activity activity = getActivity();

        if (activity instanceof TabActivity) {
            ((TabActivity) activity).logoutHandling();
        }

        refreshSaveTripButton();
    }

    private void showNativeAuth() {
        Activity activity = getActivity();

        if (activity instanceof TabActivity) {
            ((TabActivity) activity).showNativeAuth();
        }
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

    private void moveMapToShowRouteInDetail() {
        if (gMapboxMap != null && originPoint != null && destinationPoint != null) {
            LatLngBounds.Builder latLngBoundsBuilder = new LatLngBounds.Builder();

            latLngBoundsBuilder.include(new LatLng(originPoint.getLat(), originPoint.getLon()));
            latLngBoundsBuilder.include(new LatLng(destinationPoint.getLat(), destinationPoint.getLon()));

            ArrayList<LatLng> latLngs = new ArrayList<>();
            if (selectedItinerary != null) {
                for (ItineraryLeg itineraryLeg : selectedItinerary.getLegs()) {
                    if (itineraryLeg != null && itineraryLeg.getLegGeometry() != null
                            && itineraryLeg.getLegGeometry().getPoints() != null) {
                        List<Point> legCoordinates = Utilities.decodeGPSPointsForRoute(itineraryLeg.getLegGeometry().getPoints());
                        if (legCoordinates != null) {
                            for (Point point : legCoordinates) {
                                if (point != null) {
                                    LatLng latLng = new LatLng(point.latitude(), point.longitude());
                                    latLngBoundsBuilder.include(latLng);
                                    latLngs.add(latLng);
                                }
                            }
                        }
                    }
                }
            }

            int paddingTop = Convert.dpToPx(36);
            int paddingBottom = currentHeight + Convert.dpToPx(39);
            int paddingHorizontal = Convert.dpToPx(86);

            if (latLngs.size() > 0) {
                //move origin/destination markers to start/end of the itinerary polyline
                if (mvOrigin != null) {
                    mvOrigin.setLatLng(latLngs.get(0));
                    mvOrigin.setMarkerViewLatLng(latLngs.get(0));
                }
                if (mvDestination != null) {
                    mvDestination.setLatLng(latLngs.get(latLngs.size() - 1));
                    mvDestination.setMarkerViewLatLng(latLngs.get(latLngs.size() - 1));
                }
            }

            gMapboxMap.moveCamera(CameraUpdateFactory.newLatLngBounds(latLngBoundsBuilder.build(),
                    paddingHorizontal, paddingTop, paddingHorizontal, paddingBottom), null);
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
                itineraryMode = Utilities.getMappedModeForItinerary(getActivity(), itineraryLeg.getRouteId(),
                        itineraryLeg.getMode(), itineraryLeg.getRouteShortName(), itineraryLeg.getRouteLongName()).toLowerCase();
                if (itineraryMode.equalsIgnoreCase("bicycle") && itineraryLeg.isRentedBike()) {
                    itineraryMode = "bikeshare";
                }
            } else {
                itineraryMode = "walk";
            }

            ivItineraryIcon.setImageResource(Utilities.getModeImageByMode(
                    itineraryMode, false, requireActivity(), ivItineraryIcon, ivItineraryIcon));

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

            String startTime = Utilities.formatTimestamp(getActivity(), itineraryLeg.getStartTime());
            String endTime = Utilities.formatTimestamp(getActivity(), itineraryLeg.getEndTime());
            String toAddress;
            String distance;

            if (itineraryLeg.isRealTime()) {
                llTimeStatus.setVisibility(View.VISIBLE);
                //less than threshold means on time
                if (Math.abs(itineraryLeg.getArrivalDelay()) < Constants.ON_TIME_THRESHOLD_SECONDS) {
                    tvItineraryLegStartTime.setText(startTime);
                    tvItineraryLegStartTime.setForeground(null);
                    tvTimeStatus.setTextColor(getResources().getColor(R.color.colorItineraryEarly, null));
                    tvRealTime.setTextColor(getResources().getColor(R.color.colorItineraryEarly, null));
                    tvItineraryLegStartTime.setTextColor(getResources().getColor(R.color.colorItineraryEarly, null));

                    //do not show 'on time' for now
                    tvRealTime.setText("");

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

                    String scheduleTime = Utilities.formatTimestamp(getActivity(), itineraryLeg.getStartTime() - itineraryLeg.getArrivalDelay() * 1000);
                    tvItineraryLegStartTime.setText(scheduleTime);
                    tvRealTime.setText(Utilities.formatTimestamp(getActivity(), itineraryLeg.getStartTime()));
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
                routeColor = Utilities.getRouteColorByMode(getActivity(), itineraryMode);
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


                Gson gson = new Gson();

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
            if (!Utilities.checkIfToDisplayAccessibilityUI(requireActivity())) {
                tvItineraryLegTripViewerDivider.setVisibility(View.GONE);
            }

            tvItineraryLegTripViewer.setVisibility(View.GONE);

            int resID = Utilities.getModeImageByMode(itineraryMode, itineraryLeg.isRentedVehicle(), requireActivity(), ivItineraryLegStart, ivItineraryLegStart);
            ivItineraryLegStart.setImageResource(resID);

            switch (itineraryMode) {
                //only bus and subway have trip viewer
                case "subway":
                case "tram":
                case "ferry":
                case "water_taxi":
                case "link":
                case "monorail":
                case "rail":
                case "streetcar":
                case "bus":
                    tvItineraryLegServiceProvider.setVisibility(View.VISIBLE);
                    llItineraryLegStopID.setVisibility(View.VISIBLE);

                    if (itineraryLeg.getTripId() != null || itineraryLeg.getTrip().getGtfsId() != null) {
                        if (!Utilities.checkIfToDisplayAccessibilityUI(requireActivity())) {
                            tvItineraryLegTripViewerDivider.setVisibility(View.VISIBLE);
                        }
                        tvItineraryLegTripViewer.setVisibility(View.GONE);
                    } else {
                        if (!Utilities.checkIfToDisplayAccessibilityUI(requireActivity())) {
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
                    if (!Utilities.checkIfToDisplayAccessibilityUI(requireActivity())) {
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
                        String mode = Utilities.getMappedModeForItinerary(getActivity(), itineraryLeg.getRouteId(),
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

    private void loadDataInServiceAlerts(ItineraryLeg itineraryLeg) {


        if (viewRouteHeaderAlerts != null && tvRouteNumberAlerts != null && itineraryLeg != null) {

            String itineraryMode;
            if (itineraryLeg.getMode() != null) {
                itineraryMode = Utilities.getMappedModeForItinerary(getActivity(), itineraryLeg.getRouteId(),
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
                routeColor = Utilities.getRouteColorByMode(getActivity(), itineraryMode);
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

        if (Utilities.checkIfToDisplayAccessibilityUI(requireActivity())) {
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

    private Bitmap getResizedBitmap(Bitmap bm, int maxHeight) {
        if (bm != null && bm.getHeight() != 0 && maxHeight != 0) {
            float scale = (float) bm.getHeight() / (float) maxHeight;
            int dstHeight = (int) ((float) bm.getHeight() / scale);
            int dstWidth = (int) ((float) bm.getWidth() / scale);

            return Bitmap.createScaledBitmap(bm, dstWidth, dstHeight, false);
        }

        return null;
    }

    private void gotoTripViewerPage() {
        if (getActivity() != null) {
            Intent intent = new Intent(getActivity(), TripViewerActivity.class);
            intent.putExtra(Constants.CURRENT_ITINERARY, currentSelectedItinerary);
            intent.putExtra(Constants.SELECTED_ITINERARY_LEG, selectedItineraryLeg);

            getActivity().startActivityForResult(intent, Constants.TRIP_VIEWER_REQUEST_CODE);
        }
    }

    private void populateItineraryLegSteps(LinearLayout llItineraryLegStepsDetail, ArrayList<ItineraryLegStep> steps) {
        if (llItineraryLegStepsDetail != null && steps != null) {
            llItineraryLegStepsDetail.removeAllViews();
            for (int i = 0; i < steps.size(); i++) {
                ItineraryLegStep itineraryLegStep = steps.get(i);

                if (itineraryLegStep != null) {
                    View itemView = null;
                    if (Utilities.checkIfToDisplayAccessibilityUI(requireActivity())) {
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
                    if (Utilities.checkIfToDisplayAccessibilityUI(requireActivity())) {
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

    private String parseAddressFromGpsReverseResult(String response) {
        if (response != null) {
            if (response.startsWith("{")) {
                try {
                    JSONObject jsonObj = new JSONObject(response);
                    JSONArray predsJsonArray = jsonObj.getJSONArray("features");

                    // Extract the Place descriptions from the results
                    if (predsJsonArray.length() > 0) {
                        JSONObject predsJSONObject = predsJsonArray.getJSONObject(0);
                        JSONObject propertiesJSONObject = predsJSONObject.getJSONObject("properties");
                        return propertiesJSONObject.getString("label");
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    gpsReverseErrorHandler();
                }
            } else {
                try {
                    JSONArray predsJsonArray = new JSONArray(response);

                    // Extract the Place descriptions from the results
                    if (predsJsonArray.length() > 0) {
                        JSONObject predsJSONObject = predsJsonArray.getJSONObject(0);

                        if (predsJSONObject.has("name")) {
                            return predsJSONObject.getString("name");
                        } else {
                            JSONObject rawJSONObject = predsJSONObject.getJSONObject("rawGeocodedFeature");
                            if (rawJSONObject != null) {
                                if (rawJSONObject.has("label")) {
                                    return predsJSONObject.getString("label");
                                } else {
                                    return predsJSONObject.getString("address");
                                }
                            }
                        }

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    gpsReverseErrorHandler();
                }
            }
        }

        return null;
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
                String address = parseAddressFromGpsReverseResult(response);
                if (address == null) {
                    address = "";
                }
                if (gpsReverseMode == Constants.GPS_REVERSE_MODE_MARKER_VIEW) {
                    if (tvMapAddress != null) {
                        tvMapAddress.setText(address);
                    }
                    customView.setVisibility(View.VISIBLE);
                    moveMapToShowMarkerView();
                } else {
                    if (stopViewerItineraryLegPoint != null) {
                        if (gpsReverseMode == Constants.GPS_REVERSE_MODE_STOP_VIEWER_FROM) {
                            atSearchOrigin.setText(address);
                            originPoint.setAddress(address);
                            tv_itinerary_origin.setText(address);

                            originPoint.setLat(stopViewerItineraryLegPoint.getLat());
                            originPoint.setLon(stopViewerItineraryLegPoint.getLon());
                            showOriginMarkersOnMap();
                        } else {
                            atSearchDestination.setText(address);
                            destinationPoint.setAddress(address);
                            tv_itinerary_Destination.setText(address);
                            destinationPoint.setLat(stopViewerItineraryLegPoint.getLat());
                            destinationPoint.setLon(stopViewerItineraryLegPoint.getLon());

                            showDestinationMarkersOnMap();
                        }
                    }
                    goBackToRouteSearchReady();
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
        } else {
            if (stopViewerItineraryLegPoint != null) {
                String address = stopViewerItineraryLegPoint.getName();
                if (gpsReverseMode == Constants.GPS_REVERSE_MODE_STOP_VIEWER_FROM) {
                    atSearchOrigin.setText(address);
                    originPoint.setAddress(address);
                    tv_itinerary_origin.setText(address);
                    originPoint.setLat(stopViewerItineraryLegPoint.getLat());
                    originPoint.setLon(stopViewerItineraryLegPoint.getLon());
                    goBackToRouteSearchReady();
                    showOriginMarkersOnMap();
                } else if (gpsReverseMode == Constants.GPS_REVERSE_MODE_STOP_VIEWER_TO) {
                    atSearchDestination.setText(address);
                    destinationPoint.setAddress(address);
                    tv_itinerary_Destination.setText(address);
                    destinationPoint.setLat(stopViewerItineraryLegPoint.getLat());
                    destinationPoint.setLon(stopViewerItineraryLegPoint.getLon());
                    goBackToRouteSearchReady();
                    showDestinationMarkersOnMap();
                }
            }
        }
    }

    private double calculateItineraryFare(Itinerary itinerary) {
        double bikeShareCost = 0.0;
        double drivingCost = 0.0;
        double transitFare = 0.0;
        double maxTNCFare = 0.0;
        boolean hasBikeShare = false;

        if (itinerary != null) {
            if (itinerary.getFare() != null && itinerary.getFare().getFare() != null
                    && itinerary.getFare().getFare().getRegular() != null) {
                transitFare = (double) (itinerary.getFare().getFare().getRegular().getCents());
            }
            if (itinerary.getLegs() != null) {
                for (ItineraryLeg itineraryLeg : itinerary.getLegs()) {
                    if (itineraryLeg != null) {
                        if (itineraryLeg.getMode() != null) {
                            String mode = itineraryLeg.getMode().toUpperCase();

                            if (mode.equals("CAR")) {
                                drivingCost += itineraryLeg.getDistance() * 0.000621371 * Constants.DRIVING_CENTS_PER_MILE;

                                if (itineraryLeg.isHailedCar() && itineraryLeg.getTncData() != null) {
                                    maxTNCFare += itineraryLeg.getTncData().getMaxCost();
                                }
                            } else if (mode.equals("BICYCLE_RENT") || mode.equals("MICROMOBILITY") || itineraryLeg.isRentedBike()) {
                                hasBikeShare = true;
                            }
                        }
                    }
                }
            }
        }

        if (hasBikeShare) {
            bikeShareCost = Constants.BIKE_SHARE_TRIP_COST_CENTS;
        }

        if (drivingCost > 0) {
            drivingCost += Constants.CAR_PARKING_COST_CENTS;
        }

        return bikeShareCost + drivingCost + transitFare + maxTNCFare * 100;
    }

    private double calculateItineraryCost(Itinerary itinerary) {
        double bikeShareCost = 0.0;
        double drivingCost = 0.0;
        double transitFare = 0.0;
        double maxTNCFare = 0.0;
        boolean hasBikeShare = false;

        if (itinerary != null) {
            if (itinerary.getLegs() != null) {
                for (ItineraryLeg itineraryLeg : itinerary.getLegs()) {
                    if (itineraryLeg != null) {
                        if (itineraryLeg.getMode() != null) {
                            String mode = itineraryLeg.getMode().toUpperCase();

                            if (mode.equals("CAR")) {
                                drivingCost += itineraryLeg.getDistance() * 0.000621371 * Constants.DRIVING_CENTS_PER_MILE;

                                if (itineraryLeg.isHailedCar() && itineraryLeg.getTncData() != null) {
                                    maxTNCFare += itineraryLeg.getTncData().getMaxCost();
                                }
                            } else if (mode.equals("BICYCLE_RENT") || mode.equals("MICROMOBILITY") || itineraryLeg.isRentedBike()) {
                                hasBikeShare = true;
                            }
                        }
                    }
                }
            }
        }

        if (hasBikeShare) {
            bikeShareCost = Constants.BIKE_SHARE_TRIP_COST_CENTS;
        }

        if (drivingCost > 0) {
            drivingCost += Constants.CAR_PARKING_COST_CENTS;
        }

        return bikeShareCost + drivingCost + transitFare + maxTNCFare * 100;
    }

    private void initAddressSearchWidgets() {
        atSearch.setHint(getString(R.string.enter_location));
        if (address != null) {
            if (address.isEmpty()) {
                ivClear.setVisibility(View.GONE);
            } else {
                atSearch.setHint(address);
                ivClear.setVisibility(View.VISIBLE);
            }
        }
    }

    private void goBackHomePageWithData(String address, double latitude, double longitude) {
        Intent intent = new Intent();
        intent.putExtra(Constants.IS_ORIGIN, isOrigin);
        if (address != null) {
            intent.putExtra(Constants.SEARCHED_ADDRESS, address);
        }
        intent.putExtra(Constants.SEARCHED_LATITUDE, latitude);
        intent.putExtra(Constants.SEARCHED_LONGITUDE, longitude);
        searchAddressResult(intent);

        svAddressSearch.setVisibility(View.GONE);
    }

    private void setupAddressSearchWidgets() {
        svAddressSearch = mFragmentView.findViewById(R.id.sv_address_search);
        ImageView ivSearchAddressBack = mFragmentView.findViewById(R.id.iv_search_address_back);
        ivSearchAddressBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                svAddressSearch.setVisibility(View.GONE);
            }
        });

        ivClear = mFragmentView.findViewById(R.id.iv_clear);
        atSearch = mFragmentView.findViewById(R.id.at_search);
        placesAutoCompleteAdapter = new PlacesAutoCompleteAdapter(getActivity(), R.layout.item_empty);

        atSearch.setThreshold(0);
        atSearch.setAdapter(placesAutoCompleteAdapter);
        autoCompleteListAdapter = getAutoCompleteListAdapter();
        lvAutoComplete = mFragmentView.findViewById(R.id.lv_auto_complete);
        lvAutoComplete.setAdapter(autoCompleteListAdapter);

        lvAutoCompleteStops = mFragmentView.findViewById(R.id.lv_stops);
        autoCompleteStopListAdapter = getAutoCompleteStopListAdapter();
        lvAutoCompleteStops.setAdapter(autoCompleteStopListAdapter);

        tvOther = mFragmentView.findViewById(R.id.tv_other);
        tvStops = mFragmentView.findViewById(R.id.tv_stops);

        lvFavouritePlacesAutoComplete = mFragmentView.findViewById(R.id.lv_favorite_places_auto_complete);
        filteredFavoritePlaceAdapter = getFilteredAddedLocationListAdapter();
        lvFavouritePlacesAutoComplete.setAdapter(filteredFavoritePlaceAdapter);

        String credentialString = Configurations.getString("account_credentials", "");

        if (credentialString != null && !credentialString.isEmpty()) {
            lvFavouritePlacesAutoComplete.setVisibility(View.VISIBLE);

        } else {
            lvFavouritePlacesAutoComplete.setVisibility(View.GONE);
        }


        ivClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //goBackHomePageWithData("", 0, 0);
                clearOriginOrDestination();
                atSearch.setText("");
                ivClear.setVisibility(View.GONE);

            }
        });
        initAddressSearchWidgets();

    }

    private void clearOriginOrDestination() {
        setAddressToAutoCompleteTextView("", isOrigin);
        if (isOrigin) {
            originPoint.setAddress("");
            originPoint.setLat(0);
            originPoint.setLon(0);

            removeOriginMarkersFromMap();
        } else {
            destinationPoint.setAddress("");
            destinationPoint.setLat(0);
            destinationPoint.setLon(0);

            removeDestinationMarkersFromMap();
        }
    }

    private void initCurrentLocationWidgets() {
        if (currentLocationAvailable) {
            ivCurrentLocation.setImageResource(R.drawable.ic_currentlocation);
            tvCurrentLocation.setText(getString(R.string.use_current_location));
        } else {
            ivCurrentLocation.setImageResource(R.drawable.ic_notfound);
            tvCurrentLocation.setText(getString(R.string.current_location_not_available));
        }
    }

    private void setupCurrentLocationWidgets() {
        ivCurrentLocation = mFragmentView.findViewById(R.id.iv_current_location);
        tvCurrentLocation = mFragmentView.findViewById(R.id.tv_current_location);

        tvCurrentLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentLocationAvailable) {
                    goBackHomePageWithData(getString(R.string.current_location_address), 0, 0);
                } else {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
                    alertDialogBuilder
                            .setMessage(getString(R.string.location_blocked))
                            .setCancelable(true)
                            .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.dismiss();
                                }
                            });

                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();

                }
            }
        });

        initCurrentLocationWidgets();
    }

    private void clearAutoCompleteList() {
        autoCompleteResultList.clear();
        autoCompleteStopList.clear();
        if (autoCompleteListAdapter != null) {
            autoCompleteListAdapter.notifyDataSetChanged();
        }
        if (autoCompleteStopListAdapter != null) {
            autoCompleteStopListAdapter.notifyDataSetChanged();
        }
        if (tvStops.getVisibility() == View.VISIBLE) {
            tvStops.setVisibility(View.GONE);
        }
        if (tvOther.getVisibility() == View.VISIBLE) {
            tvOther.setVisibility(View.GONE);
        }

        String credentialString = Configurations.getString("account_credentials", "");

        if (credentialString != null && !credentialString.isEmpty() && filteredFavoritePlaceAdapter != null) {
            filteredFavoritePlaces.clear();
            filteredFavoritePlaceAdapter.notifyDataSetChanged();
        }

        Utilities.setTotalHeightToListViewExt(gDm.widthPixels - Convert.dpToPx(80), lvAutoComplete);
        Utilities.setTotalHeightToListViewExt(gDm.widthPixels - Convert.dpToPx(80), lvAutoCompleteStops);
    }

    class PlacesAutoCompleteAdapter extends ArrayAdapter<LocationPoint> implements Filterable {
        private final FragmentActivity context;
        private final ArrayList<LocationPoint> resultList;

        public PlacesAutoCompleteAdapter(FragmentActivity context, int resource) {
            super(context, resource);
            this.context = context;
            resultList = new ArrayList<>();
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {

            if (convertView == null) {
                LayoutInflater inflater = context.getLayoutInflater();
                convertView = inflater.inflate(R.layout.item_empty, null);
            }
            return convertView;
        }


        @Override
        public int getCount() {
            return resultList == null ? 0 : resultList.size();
        }

        @Override
        public LocationPoint getItem(int index) {
            return null;
        }

        @NonNull
        @Override
        public Filter getFilter() {
            Filter filter = new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    FilterResults filterResults = new FilterResults();
                    if (constraint != null) {

                        String credentialString = Configurations.getString("account_credentials", "");

                        if (credentialString != null && !credentialString.isEmpty()) {
                            getFilteredFavoritePlaces(constraint.toString());
                        }

                        getAutocompleteResult(Uri.encode(constraint.toString()));
                    }

                    filterResults.values = resultList;
                    filterResults.count = 0;

                    return filterResults;
                }

                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {
                    if (constraint == null) {
                        Log.e("filter", "constraint is null");
                        if (autocompleteStringRequest != null) {
                            autocompleteStringRequest.cancel();
                            autocompleteStringRequest = null;
                        }
                        clearAutoCompleteList();
                    } else {
                        Log.e("filter", "constraint:" + constraint);
                    }
                }
            };
            return filter;
        }
    }

    private void getFilteredFavoritePlaces(String input) {
        filteredFavoritePlaces.clear();
        if (input != null && !input.isEmpty()) {
            String filterString = input.toLowerCase();
            for (AddedLocation addedLocation : favoritePlaces) {
                if (addedLocation != null) {
                    if (addedLocation.getName() != null && addedLocation.getName().toLowerCase().contains(filterString)) {
                        filteredFavoritePlaces.add(addedLocation);
                    } else if (addedLocation.getAddress() != null && addedLocation.getAddress().toLowerCase().contains(filterString)) {
                        filteredFavoritePlaces.add(addedLocation);
                    }
                }
            }
        }

    }

    private void getAutocompleteResult(String input) {
        String url;
        double focusPointLat;
        double focusPointLng;

        if (MyApplication.getInstance().currentLocation != null) {
            // make default location for the focus point always
            focusPointLat = LATITUDE_DEFAULT;
            focusPointLng = LONGITUDE_DEFAULT;
        } else {
            focusPointLat = LATITUDE_DEFAULT;
            focusPointLng = LONGITUDE_DEFAULT;
        }
        url = String.format(Locale.getDefault(), Resource.API_AUTOCOMPLETE, input, Configurations.MIN_LATITUDE
                , Configurations.MIN_LONGITUDE, Configurations.MAX_LATITUDE, Configurations.MAX_LONGITUDE, focusPointLat, focusPointLng);

        url = url + "&layers=address%2Cvenue%2Cstreet%2Cintersection";

        if (autocompleteStringRequest != null) {
            autocompleteStringRequest.cancel();
            autocompleteStringRequest = null;
        }
        autocompleteStringRequest = ServerDelegate.volleyStringRequest(Request.Method.GET, url, "", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response != null) {
                    JSONObject jsonObj = null;
                    try {
                        jsonObj = new JSONObject(response);
                        JSONArray predsJsonArray = jsonObj.getJSONArray("features");

                        // Extract the Place descriptions from the results
                        autoCompleteResultList = new ArrayList<LocationPoint>(predsJsonArray.length());
                        autoCompleteStopList = new ArrayList<LocationPoint>(predsJsonArray.length());
                        boolean isStop = false;
                        for (int i = 0; i < predsJsonArray.length(); i++) {
                            JSONObject predsJSONObject = predsJsonArray.getJSONObject(i);
                            LocationPoint address = new LocationPoint();
                            isStop = false;

                            JSONObject geometryJSONObject = predsJSONObject.getJSONObject("geometry");
                            JSONObject propertiesJSONObject = predsJSONObject.getJSONObject("properties");
                            JSONArray coordinatesJSONArray = geometryJSONObject.getJSONArray("coordinates");

                            if (propertiesJSONObject != null) {
                                String label = propertiesJSONObject.optString("label", "");
                                String layer = propertiesJSONObject.optString("layer", "");
                                if (layer.equalsIgnoreCase("stops")) {
                                    isStop = true;
                                    address.setAddress(label);
                                } else {
                                    String addressString;

                                    switch (layer) {
                                        case "address":
                                            addressString = constructAddress(propertiesJSONObject);
                                            break;
                                        case "venue":
                                            addressString = constructAddressFromVenue(propertiesJSONObject);
                                            break;
                                        case "neighbourhood":
                                            addressString = constructAddressFromNeighbourhood(propertiesJSONObject);
                                            break;
                                        default:
                                            addressString = null;
                                            break;
                                    }

                                    if (addressString == null || addressString.isEmpty()) {
                                        address.setAddress(label);
                                    } else {
                                        address.setAddress(addressString);
                                    }
                                }
                            }

                            if (coordinatesJSONArray.length() > 0) {
                                address.setLon(coordinatesJSONArray.getDouble(0));
                            }
                            if (coordinatesJSONArray.length() > 1) {
                                address.setLat(coordinatesJSONArray.getDouble(1));
                            }
                            //keep the max number of each type Constants.MAX_AUTOCOMPLETE_RESULT_NUM
                            if (isStop) {
                                if (autoCompleteStopList.size() < Constants.MAX_AUTOCOMPLETE_RESULT_NUM) {
                                    autoCompleteStopList.add(address);
                                }
                            } else {
                                if (autoCompleteResultList.size() < Constants.MAX_AUTOCOMPLETE_RESULT_NUM) {
                                    autoCompleteResultList.add(address);
                                }
                            }
                        }

                        Logger.e("auto complete", "autoCompleteResultList count:" + autoCompleteResultList.size());
                        if (autoCompleteListAdapter != null) {
                            autoCompleteListAdapter.notifyDataSetChanged();

                        }
                        if (autoCompleteStopListAdapter != null) {
                            autoCompleteStopListAdapter.notifyDataSetChanged();
                        }
                        String credentialString = Configurations.getString("account_credentials", "");

                        if (credentialString != null && !credentialString.isEmpty() && filteredFavoritePlaceAdapter != null) {
                            filteredFavoritePlaceAdapter.notifyDataSetChanged();
                        }

                        assignAccessibilitytextToSearchbar(true);
                        Utilities.setTotalHeightToListViewExt(gDm.widthPixels - Convert.dpToPx(80), lvAutoComplete);
                        Utilities.setTotalHeightToListViewExt(gDm.widthPixels - Convert.dpToPx(80), lvAutoCompleteStops);

                    } catch (Exception e) {
                        e.printStackTrace();
                        Logger.e("auto complete", "parsing error:" + e);
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Logger.e("get auto complete error:", error.toString());
                errorHandler(error, getString(R.string.failed_retrieve_auto_complete));
            }
        });


    }

    private String constructAddress(JSONObject propertiesJSONObject) throws JSONException {
        StringBuilder addressBuilder = new StringBuilder();

        if (propertiesJSONObject != null) {
            String housenumber = propertiesJSONObject.optString("housenumber");
            String locality = propertiesJSONObject.optString("locality");
            String name = propertiesJSONObject.optString("name", "");
            String neighbourhood = propertiesJSONObject.optString("neighbourhood");
            String state = propertiesJSONObject.optString("region_a");
            String region = propertiesJSONObject.optString("region");
            String street = propertiesJSONObject.optString("street");

            if (!housenumber.isEmpty()) {
                addressBuilder.append(housenumber);
                addressBuilder.append(" ");
                addressBuilder.append(street);
            } else {
                addressBuilder.append(name);
            }

            if (!locality.isEmpty()) {
                addressBuilder.append(", ");
                addressBuilder.append(locality);
            }

            if (!neighbourhood.isEmpty()) {
                addressBuilder.append(", ");
                addressBuilder.append(neighbourhood);
            }

            if (!state.isEmpty()) {
                addressBuilder.append(", ");
                addressBuilder.append(state);
            } else if (!region.isEmpty()) {
                addressBuilder.append(", ");
                addressBuilder.append(region);
            }
        }
        return addressBuilder.toString();
    }

    private String constructAddressFromVenue(JSONObject propertiesJSONObject) {
        StringBuilder addressBuilder = new StringBuilder();

        if (propertiesJSONObject != null) {
            String name = propertiesJSONObject.optString("name", "");

            String street = propertiesJSONObject.optString("street");
            String neighbourhood = propertiesJSONObject.optString("neighbourhood");
            String locality = propertiesJSONObject.optString("locality");
            String state = propertiesJSONObject.optString("region_a");


            if (!name.isEmpty()) {
                addressBuilder.append(name);
            }

            if (!street.isEmpty()) {
                addressBuilder.append(", ");
                addressBuilder.append(street);
            }

            if (!neighbourhood.isEmpty()) {
                addressBuilder.append(", ");
                addressBuilder.append(neighbourhood);
            }

            if (!locality.isEmpty()) {
                addressBuilder.append(", ");
                addressBuilder.append(locality);
            }

            if (!state.isEmpty()) {
                addressBuilder.append(", ");
                addressBuilder.append(state);
            }

        }
        return addressBuilder.toString();
    }

    private String constructAddressFromNeighbourhood(JSONObject propertiesJSONObject) {
        StringBuilder addressBuilder = new StringBuilder();

        if (propertiesJSONObject != null) {
            String name = propertiesJSONObject.optString("name", "");

            String county = propertiesJSONObject.optString("county");
            String locality = propertiesJSONObject.optString("locality");
            String state = propertiesJSONObject.optString("region_a");

            if (!name.isEmpty()) {
                addressBuilder.append(name);
            }

            if (!county.isEmpty()) {
                addressBuilder.append(", ");
                addressBuilder.append(county);
            }

            if (!locality.isEmpty()) {
                addressBuilder.append(", ");
                addressBuilder.append(locality);
            }

            if (!state.isEmpty()) {
                addressBuilder.append(", ");
                addressBuilder.append(state);
            }

        }
        return addressBuilder.toString();
    }


    private void assignAccessibilitytextToSearchbar(boolean isFromSearchResults) {

        String text = null;
        String credentialString = Configurations.getString("account_credentials", "");

        if (isFromSearchResults) {

            if (atSearch != null && autoCompleteResultList != null) {

                text = autoCompleteResultList.size() + "results for" + atSearch.getText().toString();
                if (autoCompleteResultList.size() > 0) {
                    text = autoCompleteResultList.size() + "results for" + atSearch.getText().toString() + "Swipe right to access the list of results";
                }
            }
        } else {

            if (atSearch != null && recentlySearchedList != null && recentlySearchedList.size() > 0) {
                text = "Enter a location or swipe right to access Recently Searched locations";

                if (favoritePlaces != null && favoritePlaces.size() > 0 && credentialString != null && !credentialString.isEmpty()) {
                    text = "Enter a location or swipe right to access Recent Searched Locations and Favorite Places";
                }
            } else {

                if (atSearch != null && favoritePlaces != null && favoritePlaces.size() > 0 && credentialString != null && !credentialString.isEmpty()) {
                    text = "Enter a location or swipe right to access Favorite Places";
                } else {
                    text = "Enter location";
                }
            }
        }

        if (atSearch != null && text != null) {
            atSearch.announceForAccessibility(text);
            if (!isFromSearchResults) {
                String finalText = text;
                View.AccessibilityDelegate accessibilityDelegate = new View.AccessibilityDelegate() {
                    @Override
                    public void onInitializeAccessibilityNodeInfo(View host, AccessibilityNodeInfo info) {
                        super.onInitializeAccessibilityNodeInfo(host, info);
                        info.setText(finalText);
                    }
                };
                atSearch.requestFocus();
                atSearch.setAccessibilityDelegate(accessibilityDelegate);
            }
        }

    }


    private FilteredAddedLocationListAdapter getFilteredAddedLocationListAdapter() {
        ArrayList<HashMap<String, Object>> data = new ArrayList<HashMap<String, Object>>();

        for (int i = 0; i < filteredFavoritePlaces.size(); i++) {
            AddedLocation favoritePlace = filteredFavoritePlaces.get(i);

            HashMap<String, Object> map = new HashMap<String, Object>();

            map.put("tv_added_location_name", favoritePlace.getName());

            data.add(map);
        }

        FilteredAddedLocationListAdapter mFilteredAddedLocationListAdapter = new FilteredAddedLocationListAdapter(
                systemContext, data,
                R.layout.item_favorite_place,
                new String[]{"tv_added_location_name"}, new int[]{R.id.tv_added_location_name});

        return mFilteredAddedLocationListAdapter;
    }

    class FilteredAddedLocationListAdapter extends SimpleAdapter {
        Context mContext;
        private final LayoutInflater l_Inflater;

        public FilteredAddedLocationListAdapter(Context context, List<? extends Map<String, ?>> data,
                                                int resource, String[] from, int[] to) {
            super(context, data, resource, from, to);

            mContext = context;
            l_Inflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return filteredFavoritePlaces.size();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            AddedLocation favoritePlace = filteredFavoritePlaces.get(position);

            if (convertView == null) {
                convertView = l_Inflater.inflate(
                        R.layout.item_favorite_place, null);
            }
            ImageView ivPlaceType = convertView.findViewById(R.id.iv_place_type);
            TextView tvAddedLocationName = convertView.findViewById(R.id.tv_added_location_name);
            TextView tvAddedLocationAddress = convertView.findViewById(R.id.tv_added_location_address);

            switch (favoritePlace.getType()) {
                case "home":
                    ivPlaceType.setImageResource(R.drawable.ic_home);
                    break;
                case "work":
                    ivPlaceType.setImageResource(R.drawable.ic_work);
                    break;
                case "dining":
                    ivPlaceType.setImageResource(R.drawable.ic_restaurant);
                    break;
                default:
                    ivPlaceType.setImageResource(R.drawable.ic_account_location);
                    break;
            }
            tvAddedLocationName.setText(favoritePlace.getName());
            tvAddedLocationAddress.setText(favoritePlace.getAddress());
            convertView.setTag(position);
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = (int) v.getTag();
                    if (position < filteredFavoritePlaces.size()) {
                        AddedLocation favoritePlace = filteredFavoritePlaces.get(position);
                        goBackHomePageWithData(favoritePlace.getAddress(), favoritePlace.getLat(), favoritePlace.getLon());
                    }
                }
            });
            return convertView;
        }
    }


    private AutoCompleteListAdapter getAutoCompleteListAdapter() {
        ArrayList<HashMap<String, Object>> data = new ArrayList<HashMap<String, Object>>();

        for (int i = 0; i < autoCompleteResultList.size(); i++) {
            LocationPoint address = autoCompleteResultList.get(i);

            HashMap<String, Object> map = new HashMap<String, Object>();

            map.put("tv_address", address.getAddress());

            data.add(map);
        }

        AutoCompleteListAdapter mAutoCompleteListAdapter = new AutoCompleteListAdapter(
                systemContext, data,
                R.layout.item_search_list,
                new String[]{"tv_address"}, new int[]{R.id.tv_address});

        return mAutoCompleteListAdapter;
    }

    class AutoCompleteListAdapter extends SimpleAdapter {
        Context mContext;
        private final LayoutInflater l_Inflater;

        public AutoCompleteListAdapter(Context context, List<? extends Map<String, ?>> data,
                                       int resource, String[] from, int[] to) {
            super(context, data, resource, from, to);

            mContext = context;
            l_Inflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return autoCompleteResultList.size();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LocationPoint locationPoint = autoCompleteResultList.get(position);

            if (convertView == null) {
                convertView = l_Inflater.inflate(
                        R.layout.item_search_list, null);
            }

            LinearLayout llSearchList = convertView.findViewById(R.id.ll_search_list);
            TextView tvAddress = convertView.findViewById(R.id.tv_address);
            ImageView ivType = convertView.findViewById(R.id.iv_type);
            tvAddress.setText(locationPoint.getAddress());
            ivType.setImageResource(R.drawable.ic_location_list);

            llSearchList.setTag(position);
            llSearchList.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = (int) view.getTag();

                    if (position < autoCompleteResultList.size()) {
                        LocationPoint locationPoint = autoCompleteResultList.get(position);
                        addLocationToRecentlySearchedList(locationPoint);
                        goBackHomePageWithData(locationPoint.getAddress(), locationPoint.getLat(), locationPoint.getLon());
                    }
                }
            });
            return convertView;
        }
    }

    private AutoCompleteStopListAdapter getAutoCompleteStopListAdapter() {
        ArrayList<HashMap<String, Object>> data = new ArrayList<HashMap<String, Object>>();

        for (int i = 0; i < autoCompleteStopList.size(); i++) {
            LocationPoint address = autoCompleteStopList.get(i);

            HashMap<String, Object> map = new HashMap<String, Object>();

            map.put("tv_address", address.getAddress());

            data.add(map);
        }

        AutoCompleteStopListAdapter mAutoCompleteStopListAdapter = new AutoCompleteStopListAdapter(
                systemContext, data,
                R.layout.item_search_list,
                new String[]{"tv_address"}, new int[]{R.id.tv_address});

        return mAutoCompleteStopListAdapter;
    }

    class AutoCompleteStopListAdapter extends SimpleAdapter {
        Context mContext;
        private final LayoutInflater l_Inflater;

        public AutoCompleteStopListAdapter(Context context, List<? extends Map<String, ?>> data,
                                           int resource, String[] from, int[] to) {
            super(context, data, resource, from, to);

            mContext = context;
            l_Inflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return autoCompleteStopList.size();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LocationPoint locationPoint = autoCompleteStopList.get(position);

            if (convertView == null) {
                convertView = l_Inflater.inflate(
                        R.layout.item_search_list, null);
            }

            LinearLayout llSearchList = convertView.findViewById(R.id.ll_search_list);
            TextView tvAddress = convertView.findViewById(R.id.tv_address);
            ImageView ivType = convertView.findViewById(R.id.iv_type);
            tvAddress.setText(locationPoint.getAddress());
            ivType.setImageResource(R.drawable.ic_location_list);

            llSearchList.setTag(position);
            llSearchList.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = (int) view.getTag();

                    if (position < autoCompleteStopList.size()) {
                        LocationPoint locationPoint = autoCompleteStopList.get(position);
                        addLocationToRecentlySearchedList(locationPoint);
                        goBackHomePageWithData(locationPoint.getAddress(), locationPoint.getLat(), locationPoint.getLon());
                    }
                }
            });
            return convertView;
        }
    }

    private RecentlySearchedListAdapter getRecentlySearchedListAdapter() {
        ArrayList<HashMap<String, Object>> data = new ArrayList<HashMap<String, Object>>();

        for (int i = 0; i < recentlySearchedList.size(); i++) {
            LocationPoint address = recentlySearchedList.get(i);

            HashMap<String, Object> map = new HashMap<String, Object>();

            map.put("tv_address", address.getAddress());

            data.add(map);
        }

        RecentlySearchedListAdapter mRecentlySearchedListAdapter = new RecentlySearchedListAdapter(
                systemContext, data,
                R.layout.item_search_list,
                new String[]{"tv_address"}, new int[]{R.id.tv_address});

        return mRecentlySearchedListAdapter;
    }

    class RecentlySearchedListAdapter extends SimpleAdapter {
        Context mContext;
        private final LayoutInflater l_Inflater;

        public RecentlySearchedListAdapter(Context context, List<? extends Map<String, ?>> data,
                                           int resource, String[] from, int[] to) {
            super(context, data, resource, from, to);

            mContext = context;
            l_Inflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return recentlySearchedList.size();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LocationPoint locationPoint = recentlySearchedList.get(position);

            if (convertView == null) {
                convertView = l_Inflater.inflate(
                        R.layout.item_search_list, null);
            }

            LinearLayout llSearchList = convertView.findViewById(R.id.ll_search_list);
            TextView tvAddress = convertView.findViewById(R.id.tv_address);
            ImageView ivType = convertView.findViewById(R.id.iv_type);
            tvAddress.setText(locationPoint.getAddress());
            ivType.setImageResource(R.drawable.ic_search);

            llSearchList.setTag(position);
            llSearchList.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = (int) view.getTag();

                    if (position < recentlySearchedList.size()) {
                        LocationPoint locationPoint = recentlySearchedList.get(position);
                        addLocationToRecentlySearchedList(locationPoint);
                        goBackHomePageWithData(locationPoint.getAddress(), locationPoint.getLat(), locationPoint.getLon());
                    }
                }
            });
            return convertView;
        }
    }

    private void addLocationToRecentlySearchedList(LocationPoint locationPoint) {
        if (locationPoint != null) {
            for (LocationPoint recentlySearched : recentlySearchedList) {
                if (locationPoint.equals(recentlySearched)) {
                    recentlySearchedList.remove(recentlySearched);
                    break;
                }
            }
            recentlySearchedList.add(0, locationPoint);

            if (recentlySearchedList.size() > Constants.MAX_RECENTLY_SEARCHED_NUM) {
                for (int i = (recentlySearchedList.size() - 1); i >= Constants.MAX_RECENTLY_SEARCHED_NUM; i--) {
                    recentlySearchedList.remove(i);
                }
            }
            Gson gson = new Gson();
            Configurations.setString("recently_searched_list", gson.toJson(recentlySearchedList));
        }
    }

    private void initRecentlySearched() {
        String recentlySearchedString = Configurations.getString("recently_searched_list", "[]");
        Gson gson = new Gson();

        recentlySearchedList = gson.fromJson(recentlySearchedString, new TypeToken<ArrayList<LocationPoint>>() {
        }.getType());

        if (recentlySearchedList.size() == 0) {
            tvRecentlySearched.setVisibility(View.GONE);
            lvRecentlySearched.setVisibility(View.GONE);
        } else {
            if (recentlySearchedList.size() > Constants.MAX_RECENTLY_SEARCHED_NUM) {
                for (int i = (recentlySearchedList.size() - 1); i >= Constants.MAX_RECENTLY_SEARCHED_NUM; i--) {
                    recentlySearchedList.remove(i);
                }
            }
            tvRecentlySearched.setVisibility(View.VISIBLE);
            lvRecentlySearched.setVisibility(View.VISIBLE);
        }
        recentlySearchedListAdapter = getRecentlySearchedListAdapter();
        lvRecentlySearched.setAdapter(recentlySearchedListAdapter);

        Utilities.setTotalHeightToListViewExt(gDm.widthPixels - Convert.dpToPx(90), lvRecentlySearched);
    }


    private void setupRecentlySearched() {
        tvRecentlySearched = mFragmentView.findViewById(R.id.tv_recently_searched);
        lvRecentlySearched = mFragmentView.findViewById(R.id.lv_recently_searched);

        initRecentlySearched();
    }

    private AddedLocationListAdapter getAddedLocationListAdapter() {
        ArrayList<HashMap<String, Object>> data = new ArrayList<HashMap<String, Object>>();

        for (int i = 0; i < favoritePlaces.size(); i++) {
            AddedLocation favoritePlace = favoritePlaces.get(i);

            HashMap<String, Object> map = new HashMap<String, Object>();

            map.put("tv_added_location_name", favoritePlace.getName());

            data.add(map);
        }

        AddedLocationListAdapter mAddedLocationListAdapter = new AddedLocationListAdapter(
                systemContext, data,
                R.layout.item_favorite_place,
                new String[]{"tv_added_location_name"}, new int[]{R.id.tv_added_location_name});

        return mAddedLocationListAdapter;
    }

    class AddedLocationListAdapter extends SimpleAdapter {
        Context mContext;
        private final LayoutInflater l_Inflater;

        public AddedLocationListAdapter(Context context, List<? extends Map<String, ?>> data,
                                        int resource, String[] from, int[] to) {
            super(context, data, resource, from, to);

            mContext = context;
            l_Inflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return favoritePlaces.size();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            AddedLocation favoritePlace = favoritePlaces.get(position);

            if (convertView == null) {
                convertView = l_Inflater.inflate(
                        R.layout.item_favorite_place, null);
            }
            ImageView ivPlaceType = convertView.findViewById(R.id.iv_place_type);
            TextView tvAddedLocationName = convertView.findViewById(R.id.tv_added_location_name);
            TextView tvAddedLocationAddress = convertView.findViewById(R.id.tv_added_location_address);

            switch (favoritePlace.getType()) {
                case "home":
                    ivPlaceType.setImageResource(R.drawable.ic_home);
                    break;
                case "work":
                    ivPlaceType.setImageResource(R.drawable.ic_work);
                    break;
                case "dining":
                    ivPlaceType.setImageResource(R.drawable.ic_restaurant);
                    break;
                default:
                    ivPlaceType.setImageResource(R.drawable.ic_account_location);
                    break;
            }
            tvAddedLocationName.setText(favoritePlace.getName());
            tvAddedLocationAddress.setText(favoritePlace.getAddress());
            convertView.setTag(position);
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = (int) v.getTag();
                    if (position < favoritePlaces.size()) {
                        AddedLocation favoritePlace = favoritePlaces.get(position);
                        goBackHomePageWithData(favoritePlace.getAddress(), favoritePlace.getLat(), favoritePlace.getLon());
                    }
                }
            });
            return convertView;
        }
    }

    private void setupFavoritePlacesLayout() {
        tvFavoritePlaces = mFragmentView.findViewById(R.id.tv_favorite_places);
        lvFavoritePlaces = mFragmentView.findViewById(R.id.lv_favorite_places);

        String credentialString = Configurations.getString("account_credentials", "");

        if (credentialString != null && !credentialString.isEmpty()) {
            tvFavoritePlaces.setVisibility(View.VISIBLE);
            lvFavoritePlaces.setVisibility(View.VISIBLE);
            generateFavoritePlaces();
            if (favoritePlaceListAdapter == null) {
                favoritePlaceListAdapter = getAddedLocationListAdapter();
                lvFavoritePlaces.setAdapter(favoritePlaceListAdapter);
            } else {
                favoritePlaceListAdapter.notifyDataSetChanged();
            }
            Utilities.setTotalHeightToListView(lvFavoritePlaces);
        } else {
            tvFavoritePlaces.setVisibility(View.GONE);
            lvFavoritePlaces.setVisibility(View.GONE);
        }

    }

    private void generateFavoritePlaces() {
        favoritePlaces.clear();

        if (MyApplication.getInstance().userPreferences == null) {
            Gson gson = new Gson();
            try {
                MyApplication.getInstance().userPreferences =
                        gson.fromJson(Configurations.getString("account_user_preferences", "{}"), UserPreferences.class);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        UserPreferences userPreferences = MyApplication.getInstance().userPreferences;
        if (userPreferences != null) {
            ArrayList<AddedLocation> savedLocations = userPreferences.getSavedLocations();
            if (savedLocations != null && savedLocations.size() > 0) {
                for (AddedLocation favoritePlace : savedLocations) {
                    String localisedName;
                    switch (favoritePlace.getType()) {
                        case "home":
                            // Fetch the localised string for "home"
                            localisedName = MyApplication.getInstance().getResources().getString(R.string.home);
                            favoritePlace.setName(localisedName); // Set the localised name.
                            favoritePlaces.add(favoritePlace);
                            break;
                        case "work":
                            // Fetch the localised string for "work"
                            localisedName = MyApplication.getInstance().getResources().getString(R.string.work);
                            favoritePlace.setName(localisedName); // Set the localised name.
                            favoritePlaces.add(favoritePlace);
                            break;
                        case "custom":
                        case "dining":
                            favoritePlaces.add(favoritePlace);
                            break;
                        case "stop":
                            break;

                        default:
                            break;
                    }
                }
            }
        }

    }

    private void goBackToItineraryListMode() {
        hideLayerPopup();
        if (modeStack.size() > 0) {
            modeStack.remove(modeStack.size() - 1);
        }
        currentMode = Constants.HOME_PAGE_MODE_ITINERARY_LIST;
        llItineraryDetail.setVisibility(View.GONE);
        llCaloriesBurnedDetail.setVisibility(View.GONE);
        if (llMoreInfoCaloriesBurned != null) {
            llMoreInfoCaloriesBurned.setContentDescription(getString(R.string.calories_info_show_details));
            llMoreInfoCaloriesBurnedAccessibility.setContentDescription(getString(R.string.calories_info_show_details));
        }
        rlMapView.setVisibility(View.VISIBLE);
        llItineraryList.setVisibility(View.VISIBLE);
    }

    public boolean backKeyHandler() {
        if (currentMode == Constants.HOME_PAGE_MODE_ITINERARY_DETAIL) {
            goBackToItineraryListMode();
            return true;
        } else if (currentMode == Constants.HOME_PAGE_MODE_ITINERARY_LIST) {
            goBackToRouteSearchReady();
            return true;
        } else if (currentMode == Constants.HOME_PAGE_MODE_SAVE_NEW_TRIP) {
            exitSaveNewTripMode();
            return true;
        } else if (rlHomeSearchModes.getVisibility() == View.VISIBLE) {
            rlHomeSearchModes.setVisibility(View.GONE);
            enableAccessbilityForSaveTrip();
            return true;
        } else if (rlHomeSearchModesAccessibilty.getVisibility() == View.VISIBLE) {
            rlHomeSearchModesAccessibilty.setVisibility(View.GONE);
            enableAccessbilityForSaveTrip();
            return true;
        } else if (rlHomeSetting.getVisibility() == View.VISIBLE) {
            rlHomeSetting.setVisibility(View.GONE);
            ivSearchSetting.setColorFilter(null);
            ivSearchSetting.setBackground(null);
            enableAccessbilityForSaveTrip();
            return true;
        } else if (rlHomeSettingAccessibility.getVisibility() == View.VISIBLE) {
            rlHomeSettingAccessibility.setVisibility(View.GONE);
            ivSearchSetting.setColorFilter(null);
            ivSearchSetting.setBackground(null);
            enableAccessbilityForSaveTrip();
            return true;
        }
        return false;
    }

    private void setupTravelCompanion() {
        if (mFragmentView == null) {
            return;
        }
        llTravelCompanions = mFragmentView.findViewById(R.id.ll_travel_companions);
        llObserversWatchingThisTrip = mFragmentView.findViewById(R.id.ll_observers_watching_this_trip);
        llCompanionOnThisTrip = mFragmentView.findViewById(R.id.ll_companion_on_this_trip);
        tvObserversWatchingThisTrip = mFragmentView.findViewById(R.id.tv_observers_watching_this_trip);
        tvCompanionOnThisTrip = mFragmentView.findViewById(R.id.tv_companion_on_this_trip);
        tvPrimaryTraveler = mFragmentView.findViewById(R.id.tv_primary_traveler);
        ivObserversWatchingThisTrip = mFragmentView.findViewById(R.id.iv_observers_watching_this_trip);
        ivCompanionOnThisTrip = mFragmentView.findViewById(R.id.iv_companion_on_this_trip);
        Gson gson = new Gson();
        if (MyApplication.getInstance().userPreferences == null) {
            MyApplication.getInstance().userPreferences = gson.fromJson(Configurations.getString("account_user_preferences", ""),
                    UserPreferences.class);
        }

        if (MyApplication.getInstance().userPreferences.getId() != null) {
            myUserId = MyApplication.getInstance().userPreferences.getId();
        }
    }

    private String getCompanionOnThisTrip() {
        if (monitoredTripUpdateRequest.getCompanion() != null && monitoredTripUpdateRequest.getCompanion().getEmail() != null) {
            return monitoredTripUpdateRequest.getCompanion().getEmail();
        } else {
            return "";
        }
    }

    private void enableCompanionAndObservers() {
        llObserversWatchingThisTrip.setClickable(true);
        llObserversWatchingThisTrip.setFocusable(true);
        llCompanionOnThisTrip.setClickable(true);
        llCompanionOnThisTrip.setFocusable(true);

        llCompanionOnThisTrip.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(android.R.color.black)));
        llObserversWatchingThisTrip.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(android.R.color.black)));
        tvCompanionOnThisTrip.setTextColor(getResources().getColor(R.color.colorBlack));
        tvObserversWatchingThisTrip.setTextColor(getResources().getColor(R.color.colorBlack));
        ivCompanionOnThisTrip.setColorFilter(getResources().getColor(R.color.colorBlack));
        ivObserversWatchingThisTrip.setColorFilter(getResources().getColor(R.color.colorBlack));
    }

    private void disableCompanionAndObservers() {
        llObserversWatchingThisTrip.setClickable(false);
        llObserversWatchingThisTrip.setFocusable(false);
        llCompanionOnThisTrip.setClickable(false);
        llCompanionOnThisTrip.setFocusable(false);

        llObserversWatchingThisTrip.setOnClickListener(null);
        llCompanionOnThisTrip.setOnClickListener(null);

        llCompanionOnThisTrip.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorGrey)));
        llObserversWatchingThisTrip.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorGrey)));
        tvCompanionOnThisTrip.setTextColor(getResources().getColor(R.color.colorGrey));
        tvObserversWatchingThisTrip.setTextColor(getResources().getColor(R.color.colorGrey));
        ivCompanionOnThisTrip.setColorFilter(getResources().getColor(R.color.colorGrey));
        ivObserversWatchingThisTrip.setColorFilter(getResources().getColor(R.color.colorGrey));
    }

    private void refreshTravelCompanion() {
        if (MyApplication.getInstance().userPreferences.getId() != null) {
            myUserId = MyApplication.getInstance().userPreferences.getId();
        }
        String mSelectedDependantEmail = Configurations.getString("selected_dependant_email", "");
        //created by myself
        if (mSelectedDependantEmail != null && !mSelectedDependantEmail.isEmpty()) {
            tvPrimaryTraveler.setText(mSelectedDependantEmail);

            tvCompanionOnThisTrip.setText(MyApplication.getInstance().userPreferences.getEmail());
            tvObserversWatchingThisTrip.setText("");

            disableCompanionAndObservers();
        } else {
            tvPrimaryTraveler.setText(getString(R.string.myself));
            tvCompanionOnThisTrip.setText("");
            tvObserversWatchingThisTrip.setText("");
            enableCompanionAndObservers();

            //created for myself
            tvPrimaryTraveler.setText(getString(R.string.myself));
            llCompanionOnThisTrip.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ArrayList<SelectionStatus> selectionStatuses = new ArrayList<>();
                    Gson gson = new Gson();

                    UserPreferences userPreferences = MyApplication.getInstance().userPreferences;
                    if (userPreferences != null && userPreferences.getRelatedUsers() != null) {
                        String currentCompanionOnThisTrip = tvCompanionOnThisTrip.getText().toString();
                        String currentObservers = tvObserversWatchingThisTrip.getText().toString();
                        for (int i = 0; i < userPreferences.getRelatedUsers().size(); i++) {
                            RelatedUser relatedUser = userPreferences.getRelatedUsers().get(i);

                            if (relatedUser != null) {
                                if (relatedUser.getStatus() != null &&
                                        (relatedUser.getStatus().equalsIgnoreCase("verified")
                                                || relatedUser.getStatus().equalsIgnoreCase("CONFIRMED"))) {
                                    if (relatedUser.getEmail() != null) {
                                        if (currentObservers.contains(relatedUser.getEmail())) {
                                            continue;
                                        }
                                        SelectionStatus selectionStatus = new SelectionStatus();
                                        selectionStatus.setId((long) i);
                                        selectionStatus.setName(relatedUser.getEmail());

                                        selectionStatus.setSelected(!currentCompanionOnThisTrip.isEmpty() && relatedUser.getEmail().equals(currentCompanionOnThisTrip));
                                        selectionStatuses.add(selectionStatus);
                                    }
                                }
                            }
                        }
                    }

                    CustomizePopupSelectionDialog companionPopupSelectionDialog = new CustomizePopupSelectionDialog(requireActivity(),
                            getString(R.string.select_an_option), gson.toJson(selectionStatuses),
                            getString(R.string.ok), getString(R.string.cancel),
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    String currentCompanion = "";
                                    ArrayList<SelectionStatus> selectionStatuses = (ArrayList<SelectionStatus>) view.getTag();
                                    for (SelectionStatus selectionStatus : selectionStatuses) {
                                        if (selectionStatus != null && selectionStatus.isSelected()) {
                                            currentCompanion = selectionStatus.getName();
                                            break;
                                        }
                                    }
                                    tvCompanionOnThisTrip.setText(currentCompanion);

                                }
                            }, null, POPUP_TYPE_SINGLE_SELECTION,
                            false, getString(R.string.no_options));

                    companionPopupSelectionDialog.show();
                }
            });
            llObserversWatchingThisTrip.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ArrayList<SelectionStatus> selectionStatuses = new ArrayList<>();
                    Gson gson = new Gson();

                    UserPreferences userPreferences = MyApplication.getInstance().userPreferences;
                    if (userPreferences != null && userPreferences.getRelatedUsers() != null) {
                        String currentObserversWatchingThisTrip = tvObserversWatchingThisTrip.getText().toString();
                        String currentCompanionOnThisTrip = tvCompanionOnThisTrip.getText().toString();
                        for (int i = 0; i < userPreferences.getRelatedUsers().size(); i++) {
                            RelatedUser relatedUser = userPreferences.getRelatedUsers().get(i);
                            if (relatedUser != null) {
                                if (relatedUser.getStatus() != null &&
                                        (relatedUser.getStatus().equalsIgnoreCase("verified")
                                                || relatedUser.getStatus().equalsIgnoreCase("CONFIRMED"))) {
                                    if (relatedUser.getEmail() != null) {
                                        if (currentCompanionOnThisTrip.contains(relatedUser.getEmail())) {
                                            continue;
                                        }
                                        SelectionStatus selectionStatus = new SelectionStatus();
                                        selectionStatus.setId((long) i);
                                        selectionStatus.setName(relatedUser.getEmail());

                                        selectionStatus.setSelected(!currentObserversWatchingThisTrip.isEmpty() && currentObserversWatchingThisTrip.contains(relatedUser.getEmail()));
                                        selectionStatuses.add(selectionStatus);
                                    }
                                }
                            }
                        }
                    }

                    CustomizePopupSelectionDialog observersPopupSelectionDialog = new CustomizePopupSelectionDialog(requireActivity(),
                            getString(R.string.select_options), gson.toJson(selectionStatuses),
                            getString(R.string.ok), getString(R.string.cancel),
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    StringBuilder stringBuilder = new StringBuilder();
                                    ArrayList<SelectionStatus> selectionStatuses = (ArrayList<SelectionStatus>) view.getTag();
                                    for (SelectionStatus selectionStatus : selectionStatuses) {
                                        if (selectionStatus != null && selectionStatus.isSelected()) {
                                            if (stringBuilder.length() > 0) {
                                                stringBuilder.append(", ");
                                            }
                                            stringBuilder.append(selectionStatus.getName());
                                        }
                                    }
                                    tvObserversWatchingThisTrip.setText(stringBuilder.toString());
                                }
                            }, null, POPUP_TYPE_MULTIPLE_SELECTION,
                            false, getString(R.string.no_options));

                    observersPopupSelectionDialog.show();
                }
            });

        }

    }

    private void initHeaderViews() {
        Utilities.setFont(systemContext, tvTitle, false);
        Utilities.setFont(systemContext, tvSignIn, false);

        ivLogo.setVisibility(View.GONE);
        ivMenu.setVisibility(View.GONE);
        ivSaveNewTripBack.setVisibility(View.VISIBLE);

        tvTitle.setText(R.string.save_new_trip);
        tvTitle.setVisibility(View.VISIBLE);

        tvAppName.setVisibility(View.GONE);
        tvSignIn.setVisibility(View.GONE);
    }

    private void setupHeader() {
        ivLogo = ll_save_new_trip.findViewById(R.id.iv_logo);
        ivMenu = ll_save_new_trip.findViewById(R.id.iv_menu);
        ivSaveNewTripBack = ll_save_new_trip.findViewById(R.id.iv_back);
        tvTitle = ll_save_new_trip.findViewById(R.id.tv_title);
        tvSignIn = ll_save_new_trip.findViewById(R.id.tv_sign_in);
        tvAppName = ll_save_new_trip.findViewById(R.id.tv_app_name);
        ivSaveNewTripBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                exitSaveNewTripMode();
            }
        });
        initHeaderViews();
    }

    private void refreshOnSelectedDate(long dateCreated) {
        if (rbSpecificDate != null) {
            rbSpecificDate.setText(getString(R.string.only_on_selected_date,
                    Utilities.formatTimestampForSelectedDate(dateCreated)));
        }
    }

    private void setPreviousSelectedStatus() {
        StringBuilder stringBuilder = new StringBuilder();

        if (cb_monday.isChecked()) {
            stringBuilder.append("1");
        }
        if (cb_tuesday.isChecked()) {
            stringBuilder.append("2");
        }
        if (cb_wednesday.isChecked()) {
            stringBuilder.append("3");
        }
        if (cb_thursday.isChecked()) {
            stringBuilder.append("4");
        }
        if (cb_friday.isChecked()) {
            stringBuilder.append("5");
        }
        if (cb_saturday.isChecked()) {
            stringBuilder.append("6");
        }
        if (cb_sunday.isChecked()) {
            stringBuilder.append("7");
        }

        previousSelectedStatus = stringBuilder.toString();
    }

    private void resetPreviousSelectedStatus() {
        if (previousSelectedStatus != null) {
            cb_monday.setChecked(previousSelectedStatus.contains("1"));
            cb_tuesday.setChecked(previousSelectedStatus.contains("2"));
            cb_wednesday.setChecked(previousSelectedStatus.contains("3"));
            cb_thursday.setChecked(previousSelectedStatus.contains("4"));
            cb_friday.setChecked(previousSelectedStatus.contains("5"));
            cb_saturday.setChecked(previousSelectedStatus.contains("6"));
            cb_sunday.setChecked(previousSelectedStatus.contains("7"));

            refreshDatesWidgets(currentItineraryExistence);
        }
    }

    private void automaticallySwitchToSpecificDate() {
        if (!cb_monday.isChecked() && !cb_tuesday.isChecked() && !cb_wednesday.isChecked() && !cb_thursday.isChecked() &&
                !cb_friday.isChecked() && !cb_saturday.isChecked() && !cb_sunday.isChecked()) {
            llEachWeek.setVisibility(View.GONE);
            rbEachWeek.setChecked(false);
            rbSpecificDate.setChecked(true);
        }
    }

    private void setupTripDaysControllers() {
        rbEachWeek = mFragmentView.findViewById(R.id.rb_each_week);
        rbSpecificDate = mFragmentView.findViewById(R.id.rb_specific_date);
        llEachWeek = mFragmentView.findViewById(R.id.ll_each_week);

        rbEachWeek.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetPreviousSelectedStatus();
                llEachWeek.setVisibility(View.VISIBLE);
                rbEachWeek.setChecked(true);
                rbSpecificDate.setChecked(false);
            }
        });

        rbSpecificDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                llEachWeek.setVisibility(View.GONE);
                setPreviousSelectedStatus();
                rbEachWeek.setChecked(false);
                rbSpecificDate.setChecked(true);
            }
        });
    }

    private void setupSaveNewTrip() {
        ll_save_new_trip = mFragmentView.findViewById(R.id.ll_save_new_trip);
        ll_advanced_settings = mFragmentView.findViewById(R.id.ll_advanced_settings);
        ll_monitor_trip = mFragmentView.findViewById(R.id.ll_monitor_trip);
        rl_realtime_alert = mFragmentView.findViewById(R.id.rl_realtime_alert);
        rl_alternative_route = mFragmentView.findViewById(R.id.rl_alternative_route);
        rl_delay_options = mFragmentView.findViewById(R.id.rl_delay_options);
        rl_monitor_trip = mFragmentView.findViewById(R.id.rl_monitor_trip);
        sp_realtime_alert = mFragmentView.findViewById(R.id.sp_realtime_alert);
        sp_alternative_route = mFragmentView.findViewById(R.id.sp_alternative_route);
        sp_delay_options = mFragmentView.findViewById(R.id.sp_delay_options);
        sp_monitor_trip = mFragmentView.findViewById(R.id.sp_monitor_trip);
        ll_modes = ll_save_new_trip.findViewById(R.id.ll_modes);
        constraintLayout = ll_save_new_trip.findViewById(R.id.cl_modes);
        sv_edit_trip = ll_save_new_trip.findViewById(R.id.sv_edit_trip);
        iv_advanced_settings = mFragmentView.findViewById(R.id.iv_advanced_settings);
        et_trip_name = mFragmentView.findViewById(R.id.et_trip_name);
        tv_notify_title = mFragmentView.findViewById(R.id.tv_notify_title);
        tv_notify_title.setText(Utilities.getNotifyTitleString());

        ll_notification_layout = mFragmentView.findViewById(R.id.ll_notification_layout);
        ll_notification_warning = mFragmentView.findViewById(R.id.ll_notification_warning);

        if (tv_notify_title.getText().toString().equalsIgnoreCase(getString(R.string.notify_via_email))) {
            ll_notification_layout.setVisibility(View.GONE);
            ll_notification_warning.setVisibility(View.VISIBLE);
        } else {
            ll_notification_layout.setVisibility(View.VISIBLE);
            ll_notification_warning.setVisibility(View.GONE);
        }

        et_trip_name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (mFragmentView != null) {
                    String tripName = et_trip_name.getText().toString();
                    TextView tvTripNameHint = mFragmentView.findViewById(R.id.tv_trip_name_hint);

                    if (tvTripNameHint != null) {
                        if (tripName.isEmpty()) {
                            tvTripNameHint.setVisibility(View.VISIBLE);
                        } else {
                            tvTripNameHint.setVisibility(View.GONE);
                            tvTripNameHint.setTextColor(getResources().getColor(R.color.colorGrey, null));
                            TextView tvTripNameTitle = mFragmentView.findViewById(R.id.tv_trip_name_title);
                            tvTripNameTitle.setTextColor(getResources().getColor(android.R.color.black, null));
                        }
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        tv_edit_trip_title = mFragmentView.findViewById(R.id.tv_edit_trip_title);
        tv_itinerary_start_end_time = mFragmentView.findViewById(R.id.tv_itinerary_start_end_time);
        tv_itinerary_duration = mFragmentView.findViewById(R.id.tv_itinerary_duration);
        ll_monday = mFragmentView.findViewById(R.id.ll_monday);
        ll_tuesday = mFragmentView.findViewById(R.id.ll_tuesday);
        ll_wednesday = mFragmentView.findViewById(R.id.ll_wednesday);
        ll_thursday = mFragmentView.findViewById(R.id.ll_thursday);
        ll_friday = mFragmentView.findViewById(R.id.ll_friday);
        ll_saturday = mFragmentView.findViewById(R.id.ll_saturday);
        ll_sunday = mFragmentView.findViewById(R.id.ll_sunday);

        llCheckingItineraryExistence = mFragmentView.findViewById(R.id.ll_checking_itinerary_existence);
        tvItineraryExistenceHint = mFragmentView.findViewById(R.id.tv_itinerary_existence_hint);

        tv_monday = mFragmentView.findViewById(R.id.tv_monday);
        tv_tuesday = mFragmentView.findViewById(R.id.tv_tuesday);
        tv_wednesday = mFragmentView.findViewById(R.id.tv_wednesday);
        tv_thursday = mFragmentView.findViewById(R.id.tv_thursday);
        tv_friday = mFragmentView.findViewById(R.id.tv_friday);
        tv_saturday = mFragmentView.findViewById(R.id.tv_saturday);
        tv_sunday = mFragmentView.findViewById(R.id.tv_sunday);

        iv_monday = mFragmentView.findViewById(R.id.iv_monday);
        iv_tuesday = mFragmentView.findViewById(R.id.iv_tuesday);
        iv_wednesday = mFragmentView.findViewById(R.id.iv_wednesday);
        iv_thursday = mFragmentView.findViewById(R.id.iv_thursday);
        iv_friday = mFragmentView.findViewById(R.id.iv_friday);
        iv_saturday = mFragmentView.findViewById(R.id.iv_saturday);
        iv_sunday = mFragmentView.findViewById(R.id.iv_sunday);

        cb_monday = mFragmentView.findViewById(R.id.cb_monday);
        cb_tuesday = mFragmentView.findViewById(R.id.cb_tuesday);
        cb_wednesday = mFragmentView.findViewById(R.id.cb_wednesday);
        cb_thursday = mFragmentView.findViewById(R.id.cb_thursday);
        cb_friday = mFragmentView.findViewById(R.id.cb_friday);
        cb_saturday = mFragmentView.findViewById(R.id.cb_saturday);
        cb_sunday = mFragmentView.findViewById(R.id.cb_sunday);

        tv_cancel_update_trip = mFragmentView.findViewById(R.id.tv_cancel_update_trip);
        tv_save_trip = mFragmentView.findViewById(R.id.tv_save_trip);

        setupHeader();
        tv_cancel_update_trip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exitSaveNewTripMode();
            }
        });

        tv_save_trip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (et_trip_name.getText().toString().trim().isEmpty()) {
                    TextView tvTripNameHint = mFragmentView.findViewById(R.id.tv_trip_name_hint);
                    TextView tvTripNameTitle = mFragmentView.findViewById(R.id.tv_trip_name_title);
                    tvTripNameHint.setTextColor(getResources().getColor(R.color.colorEmptyTripName, null));
                    tvTripNameTitle.setTextColor(getResources().getColor(R.color.colorEmptyTripName, null));
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        sv_edit_trip.scrollToDescendant(tvTripNameTitle);
                        tvTripNameTitle.requestFocus();
                    } else {
                        et_trip_name.requestFocus();
                    }

                    return;
                }
                MonitoredTripResponse monitoredTripResponse = MyApplication.getInstance().gMonitoredTripResponse;
                int maxSavedTripNumber = Configurations.getInt("max_saved_trip_number", 5);
                if (monitoredTripResponse != null && monitoredTripResponse.getData() != null
                        && monitoredTripResponse.getData().size() >= maxSavedTripNumber) {
                    if (getActivity() != null) {
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
                        alertDialogBuilder.setTitle("");
                        alertDialogBuilder
                                .setMessage(systemContext.getString(R.string.reach_max_saved_trip_warning_message, maxSavedTripNumber))
                                .setCancelable(false)
                                .setPositiveButton(systemContext.getString(R.string.ok), new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.dismiss();
                                    }
                                });

                        alertDialogBuilder.create().show();
                    }
                } else {
                    monitoredTripUpdateRequest.setTripName(et_trip_name.getText().toString());
                    if (rbSpecificDate != null && rbSpecificDate.isChecked()) {
                        monitoredTripUpdateRequest.setMonday(false);
                        monitoredTripUpdateRequest.setTuesday(false);
                        monitoredTripUpdateRequest.setWednesday(false);
                        monitoredTripUpdateRequest.setThursday(false);
                        monitoredTripUpdateRequest.setFriday(false);
                        monitoredTripUpdateRequest.setSaturday(false);
                        monitoredTripUpdateRequest.setSunday(false);
                    } else {
                        monitoredTripUpdateRequest.setMonday(cb_monday.isChecked() && (cb_monday.getVisibility() == View.VISIBLE));
                        monitoredTripUpdateRequest.setTuesday(cb_tuesday.isChecked() && (cb_tuesday.getVisibility() == View.VISIBLE));
                        monitoredTripUpdateRequest.setWednesday(cb_wednesday.isChecked() && (cb_wednesday.getVisibility() == View.VISIBLE));
                        monitoredTripUpdateRequest.setThursday(cb_thursday.isChecked() && (cb_thursday.getVisibility() == View.VISIBLE));
                        monitoredTripUpdateRequest.setFriday(cb_friday.isChecked() && (cb_friday.getVisibility() == View.VISIBLE));
                        monitoredTripUpdateRequest.setSaturday(cb_saturday.isChecked() && (cb_saturday.getVisibility() == View.VISIBLE));
                        monitoredTripUpdateRequest.setSunday(cb_sunday.isChecked() && (cb_sunday.getVisibility() == View.VISIBLE));
                    }
                    monitoredTripUpdateRequest.setNotifyOnAlert(getAlertOnJourneyValue());
                    monitoredTripUpdateRequest.setNotifyOnItineraryChange(getNotifyOnItineraryChangeValue());
                    monitoredTripUpdateRequest.setLeadTimeInMinutes(getLeadTimeValue());
                    monitoredTripUpdateRequest.setDepartureVarianceMinutesThreshold(getDelayValue());
                    monitoredTripUpdateRequest.setArrivalVarianceMinutesThreshold(getDelayValue());

                    String mSelectedDependantEmail = Configurations.getString("selected_dependant_email", "");

                    if (mSelectedDependantEmail != null && !mSelectedDependantEmail.isEmpty()) {
                        if (MyApplication.getInstance().dependentMobilityProfiles != null) {
                            for (DependentMobilityProfile dependentMobilityProfile : MyApplication.getInstance().dependentMobilityProfiles) {
                                if (dependentMobilityProfile != null && dependentMobilityProfile.getEmail() != null
                                        && dependentMobilityProfile.getEmail().equals(mSelectedDependantEmail)) {
                                    monitoredTripUpdateRequest.setPrimaryTravelerId(dependentMobilityProfile.getUserId());
                                    monitoredTripUpdateRequest.setPrimary(dependentMobilityProfile);
                                    break;
                                }
                            }
                        }
                        RelatedUser companion = new RelatedUser();
                        companion.setEmail(MyApplication.getInstance().userPreferences.getEmail());
                        companion.setStatus("CONFIRMED");
                        monitoredTripUpdateRequest.setCompanion(companion);
                        monitoredTripUpdateRequest.setObservers(new ArrayList<>());
                    } else //created for myself
                    {
                        monitoredTripUpdateRequest.setPrimaryTravelerId(myUserId);

                        UserPreferences userPreferences = MyApplication.getInstance().userPreferences;
                        if (userPreferences != null && userPreferences.getRelatedUsers() != null) {
                            String currentCompanionOnThisTrip = tvCompanionOnThisTrip.getText().toString();
                            String currentObserversWatchingThisTrip = tvObserversWatchingThisTrip.getText().toString();

                            ArrayList<RelatedUser> observers = new ArrayList<>();
                            RelatedUser companion = null;

                            for (int i = 0; i < userPreferences.getRelatedUsers().size(); i++) {
                                RelatedUser relatedUser = userPreferences.getRelatedUsers().get(i);

                                if (relatedUser != null && relatedUser.getEmail() != null) {
                                    if (!currentCompanionOnThisTrip.isEmpty() && relatedUser.getEmail().equals(currentCompanionOnThisTrip)) {
                                        companion = relatedUser;
                                    }

                                    if (!currentObserversWatchingThisTrip.isEmpty() && currentObserversWatchingThisTrip.contains(relatedUser.getEmail())) {
                                        observers.add(relatedUser);
                                    }
                                }
                            }
                            monitoredTripUpdateRequest.setCompanion(companion);
                            monitoredTripUpdateRequest.setObservers(observers);
                        }
                    }

                    createNewTrip();
                }
            }
        });

        ll_advanced_settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ll_monitor_trip.getVisibility() == View.VISIBLE) {
                    ll_monitor_trip.setVisibility(View.GONE);
                    iv_advanced_settings.setImageResource(R.drawable.ic_resume);
                } else {
                    ll_monitor_trip.setVisibility(View.VISIBLE);
                    iv_advanced_settings.setImageResource(R.drawable.ic_arrow_down);
                }
            }
        });

        ll_monday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cb_monday.getVisibility() == View.VISIBLE) {
                    cb_monday.setChecked(!cb_monday.isChecked());
                    monitoredTripUpdateRequest.setMonday(cb_monday.isChecked());
                    refreshDatesWidgets(currentItineraryExistence);
                    automaticallySwitchToSpecificDate();
                }
            }
        });
        ll_tuesday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cb_tuesday.getVisibility() == View.VISIBLE) {
                    cb_tuesday.setChecked(!cb_tuesday.isChecked());
                    monitoredTripUpdateRequest.setTuesday(cb_tuesday.isChecked());
                    refreshDatesWidgets(currentItineraryExistence);
                    automaticallySwitchToSpecificDate();
                }
            }
        });
        ll_wednesday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cb_wednesday.getVisibility() == View.VISIBLE) {
                    cb_wednesday.setChecked(!cb_wednesday.isChecked());
                    monitoredTripUpdateRequest.setWednesday(cb_wednesday.isChecked());
                    refreshDatesWidgets(currentItineraryExistence);
                    automaticallySwitchToSpecificDate();
                }
            }
        });
        ll_thursday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cb_thursday.getVisibility() == View.VISIBLE) {
                    cb_thursday.setChecked(!cb_thursday.isChecked());
                    monitoredTripUpdateRequest.setThursday(cb_thursday.isChecked());
                    refreshDatesWidgets(currentItineraryExistence);
                    automaticallySwitchToSpecificDate();
                }
            }
        });
        ll_friday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cb_friday.getVisibility() == View.VISIBLE) {
                    cb_friday.setChecked(!cb_friday.isChecked());
                    monitoredTripUpdateRequest.setFriday(cb_friday.isChecked());
                    refreshDatesWidgets(currentItineraryExistence);
                    automaticallySwitchToSpecificDate();
                }
            }
        });
        ll_saturday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cb_saturday.getVisibility() == View.VISIBLE) {
                    cb_saturday.setChecked(!cb_saturday.isChecked());
                    monitoredTripUpdateRequest.setSaturday(cb_saturday.isChecked());
                    refreshDatesWidgets(currentItineraryExistence);
                    automaticallySwitchToSpecificDate();
                }
            }
        });
        ll_sunday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cb_sunday.getVisibility() == View.VISIBLE) {
                    cb_sunday.setChecked(!cb_sunday.isChecked());
                    monitoredTripUpdateRequest.setSunday(cb_sunday.isChecked());
                    refreshDatesWidgets(currentItineraryExistence);
                    automaticallySwitchToSpecificDate();
                }
            }
        });

        initSpinner();

        setupTripDaysControllers();
    }

    public boolean isValidInput() {
        TextView tvTripNameHint = mFragmentView.findViewById(R.id.tv_trip_name_hint);
        TextView tvTripNameText = mFragmentView.findViewById(R.id.tv_trip_name_title);

        if (et_trip_name.getText().toString().isEmpty()) {
            tvTripNameHint.setText(requireActivity().getString(R.string.trip_name_cant_blank));
            tvTripNameHint.setTextColor(getResources().getColor(android.R.color.holo_red_light));
            tvTripNameText.setTextColor(getResources().getColor(android.R.color.holo_red_light));
            tvTripNameHint.setVisibility(View.VISIBLE);
            return false;
        }
        MonitoredTripResponse monitoredTripResponse = MyApplication.getInstance().gMonitoredTripResponse;
        if (monitoredTripResponse != null) {
            for (MonitoredTrip trip : monitoredTripResponse.getData()) {
                if (trip.getTripName().equalsIgnoreCase(et_trip_name.getText().toString())) {
                    tvTripNameHint.setText(requireActivity().getString(R.string.trip_name_duplicate));
                    tvTripNameText.setTextColor(getResources().getColor(android.R.color.holo_red_light));
                    tvTripNameHint.setTextColor(getResources().getColor(android.R.color.holo_red_light));
                    tvTripNameHint.setVisibility(View.VISIBLE);
                    return false;
                }
            }
        }

        return true;
    }

    private void getSavedTripList() {
        Response.Listener listener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Gson gson = new Gson();
                try {
                    MyApplication.getInstance().gMonitoredTripResponse = gson.fromJson(response, MonitoredTripResponse.class);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        };
        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Logger.e("savetrip", error.toString());
            }
        };

        String url = Resource.API_ACCOUNT_MONITORED_TRIP;

        volleyAccountStringRequest(Request.Method.GET, url, "", listener, errorListener);
    }

    private void refreshTripDates() {
        cb_monday.setChecked(monitoredTripUpdateRequest.isMonday());
        cb_tuesday.setChecked(monitoredTripUpdateRequest.isTuesday());
        cb_wednesday.setChecked(monitoredTripUpdateRequest.isWednesday());
        cb_thursday.setChecked(monitoredTripUpdateRequest.isThursday());
        cb_friday.setChecked(monitoredTripUpdateRequest.isFriday());
        cb_saturday.setChecked(monitoredTripUpdateRequest.isSaturday());
        cb_sunday.setChecked(monitoredTripUpdateRequest.isSunday());
    }

    private void initSpinner() {
        initAlertOnJourney();
        initLeadTime();
        initDelay();
        initNotifyOnItineraryChange();
    }

    private void setSpinnerIndex() {
        setDelayIndex();
        setAlertOnJourneyIndex();
        setLeadTimeIndex();
        setNotifyOnItineraryChangeIndex();
    }

    private void initNotifyOnItineraryChange() {
        String[] stringArrays = getResources().getStringArray(R.array.default_control_arrays);

        if (notifyOnItineraryChangeIndex >= stringArrays.length) {
            notifyOnItineraryChangeIndex = 0;
        }

        notifyOnItineraryChangeSpinnerAdapter = new SaveTripSpinnerAdapter(systemContext, R.layout.item_spinner,
                R.id.tv_option, stringArrays);
        notifyOnItineraryChangeSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_list_item_checked);
        sp_alternative_route.setAdapter(notifyOnItineraryChangeSpinnerAdapter);
        sp_alternative_route.setSelection(notifyOnItineraryChangeIndex);
        notifyOnItineraryChangeSpinnerAdapter.setCurrentSelection(notifyOnItineraryChangeIndex);
        sp_alternative_route.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                notifyOnItineraryChangeIndex = position;
                notifyOnItineraryChangeSpinnerAdapter.setCurrentSelection(position);
                try {
                    //hide the
                    ImageView ivChecked = parent.findViewById(R.id.iv_checked);

                    if (ivChecked != null) {
                        ivChecked.setVisibility(View.GONE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void setNotifyOnItineraryChangeIndex() {
        String value = String.valueOf(monitoredTripUpdateRequest.isNotifyOnItineraryChange());
        String[] stringArrays = getResources().getStringArray(R.array.default_control_arrays_value);

        for (int i = 0; i < stringArrays.length; i++) {
            if (stringArrays[i].equalsIgnoreCase(value)) {
                notifyOnItineraryChangeIndex = i;
                sp_alternative_route.setSelection(notifyOnItineraryChangeIndex);
                return;
            }
        }

        notifyOnItineraryChangeIndex = Constants.DEFAULT_NOTIFY_ON_ITINERARY_CHANGE_INDEX;
        sp_alternative_route.setSelection(notifyOnItineraryChangeIndex);
    }

    private boolean getNotifyOnItineraryChangeValue() {
        String[] stringArrays = getResources().getStringArray(R.array.default_control_arrays_value);
        if (notifyOnItineraryChangeIndex < stringArrays.length) {
            String value = stringArrays[notifyOnItineraryChangeIndex];
            return !value.equalsIgnoreCase("false");
        } else {
            return true;
        }
    }

    private void initLeadTime() {
        String[] stringArrays = getResources().getStringArray(R.array.monitor_trip_arrays);

        if (leadTimeIndex >= stringArrays.length) {
            leadTimeIndex = 0;
        }

        leadTimeSpinnerAdapter = new SaveTripSpinnerAdapter(systemContext, R.layout.item_spinner,
                R.id.tv_option, stringArrays);
        leadTimeSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_list_item_checked);
        sp_monitor_trip.setAdapter(leadTimeSpinnerAdapter);
        sp_monitor_trip.setSelection(leadTimeIndex);
        leadTimeSpinnerAdapter.setCurrentSelection(leadTimeIndex);
        sp_monitor_trip.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                leadTimeIndex = position;
                leadTimeSpinnerAdapter.setCurrentSelection(position);
                try {
                    //hide the
                    ImageView ivChecked = parent.findViewById(R.id.iv_checked);

                    if (ivChecked != null) {
                        ivChecked.setVisibility(View.GONE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void setLeadTimeIndex() {
        String value = String.valueOf(monitoredTripUpdateRequest.getLeadTimeInMinutes());
        String[] stringArrays = getResources().getStringArray(R.array.monitor_trip_arrays_value);

        for (int i = 0; i < stringArrays.length; i++) {
            if (stringArrays[i].equalsIgnoreCase(value)) {
                leadTimeIndex = i;
                sp_monitor_trip.setSelection(leadTimeIndex);
                return;
            }
        }

        leadTimeIndex = Constants.DEFAULT_LEAD_TIME_INDEX;
        sp_monitor_trip.setSelection(leadTimeIndex);
    }

    private int getLeadTimeValue() {
        String[] stringArrays = getResources().getStringArray(R.array.monitor_trip_arrays_value);
        if (leadTimeIndex < stringArrays.length) {
            String value = stringArrays[leadTimeIndex];
            return Integer.parseInt(value);
        } else {
            return 30;
        }
    }

    private void initDelay() {
        String[] stringArrays = getResources().getStringArray(R.array.delay_arrays);

        if (delayIndex >= stringArrays.length) {
            delayIndex = 0;
        }

        delaySpinnerAdapter = new SaveTripSpinnerAdapter(systemContext, R.layout.item_spinner,
                R.id.tv_option, stringArrays);
        delaySpinnerAdapter.setDropDownViewResource(android.R.layout.simple_list_item_checked);
        sp_delay_options.setAdapter(delaySpinnerAdapter);
        sp_delay_options.setSelection(delayIndex);
        delaySpinnerAdapter.setCurrentSelection(delayIndex);
        sp_delay_options.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                delayIndex = position;
                delaySpinnerAdapter.setCurrentSelection(position);
                try {
                    //hide the
                    ImageView ivChecked = parent.findViewById(R.id.iv_checked);

                    if (ivChecked != null) {
                        ivChecked.setVisibility(View.GONE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void setDelayIndex() {
        String value = String.valueOf(monitoredTripUpdateRequest.getDepartureVarianceMinutesThreshold());
        String[] stringArrays = getResources().getStringArray(R.array.delay_arrays_value);

        for (int i = 0; i < stringArrays.length; i++) {
            if (stringArrays[i].equalsIgnoreCase(value)) {
                delayIndex = i;
                sp_delay_options.setSelection(delayIndex);
                return;
            }
        }

        delayIndex = Constants.DEFAULT_DELAY_INDEX;
        sp_delay_options.setSelection(delayIndex);
    }

    private int getDelayValue() {
        String[] stringArrays = getResources().getStringArray(R.array.delay_arrays_value);
        if (delayIndex < stringArrays.length) {
            String value = stringArrays[delayIndex];
            return Integer.parseInt(value);
        } else {
            return 5;
        }
    }

    private void initAlertOnJourney() {
        String[] stringArrays = getResources().getStringArray(R.array.default_control_arrays);

        if (alertOnJourneyIndex >= stringArrays.length) {
            alertOnJourneyIndex = 0;
        }

        alertOnJourneySpinnerAdapter = new SaveTripSpinnerAdapter(systemContext, R.layout.item_spinner,
                R.id.tv_option, stringArrays);
        alertOnJourneySpinnerAdapter.setDropDownViewResource(android.R.layout.simple_list_item_checked);
        sp_realtime_alert.setAdapter(alertOnJourneySpinnerAdapter);
        sp_realtime_alert.setSelection(alertOnJourneyIndex);
        alertOnJourneySpinnerAdapter.setCurrentSelection(alertOnJourneyIndex);
        sp_realtime_alert.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                alertOnJourneyIndex = position;
                alertOnJourneySpinnerAdapter.setCurrentSelection(position);
                try {
                    //hide the
                    ImageView ivChecked = parent.findViewById(R.id.iv_checked);

                    if (ivChecked != null) {
                        ivChecked.setVisibility(View.GONE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void setAlertOnJourneyIndex() {
        String value = String.valueOf(monitoredTripUpdateRequest.isNotifyOnAlert());
        String[] stringArrays = getResources().getStringArray(R.array.default_control_arrays_value);

        for (int i = 0; i < stringArrays.length; i++) {
            if (stringArrays[i].equalsIgnoreCase(value)) {
                alertOnJourneyIndex = i;
                sp_realtime_alert.setSelection(alertOnJourneyIndex);
                return;
            }
        }

        alertOnJourneyIndex = Constants.DEFAULT_NOTIFY_ON_ALERT_INDEX;
        sp_realtime_alert.setSelection(alertOnJourneyIndex);
    }

    private boolean getAlertOnJourneyValue() {
        String[] stringArrays = getResources().getStringArray(R.array.default_control_arrays_value);
        if (alertOnJourneyIndex < stringArrays.length) {
            String value = stringArrays[alertOnJourneyIndex];
            return !value.equalsIgnoreCase("false");
        } else {
            return true;
        }
    }

    private void exitSaveNewTripMode() {
        ll_save_new_trip.setVisibility(View.GONE);

        enableAccessbilityForSaveTrip();

        modeStack.remove(modeStack.size() - 1);

        currentMode = modeStack.get(modeStack.size() - 1);
    }

    private void refreshDatesWidgets(ItineraryExistence itineraryExistence) {
        if (itineraryExistence != null && itineraryExistence.getMonday() != null
                && !itineraryExistence.getMonday().isValid()) {
            ll_monday.setBackgroundColor(getResources().getColor(R.color.colorInvalidDateBg, null));
            tv_monday.setTextColor(getResources().getColor(R.color.colorInvalidDateText, null));
            cb_monday.setVisibility(View.GONE);
            iv_monday.setVisibility(View.VISIBLE);
        } else {
            if (cb_monday.isChecked()) {
                ll_monday.setBackgroundColor(getResources().getColor(R.color.colorPrimary, null));
                tv_monday.setTextColor(getResources().getColor(android.R.color.white, null));
                cb_monday.setButtonTintList(ColorStateList.valueOf(getResources().getColor(android.R.color.white, null)));
            } else {
                ll_monday.setBackgroundColor(getResources().getColor(android.R.color.white, null));
                tv_monday.setTextColor(getResources().getColor(android.R.color.black, null));
                cb_monday.setButtonTintList(ColorStateList.valueOf(getResources().getColor(android.R.color.black, null)));
            }

            cb_monday.setVisibility(View.VISIBLE);
            iv_monday.setVisibility(View.GONE);
        }

        if (itineraryExistence != null && itineraryExistence.getTuesday() != null
                && !itineraryExistence.getTuesday().isValid()) {
            ll_tuesday.setBackgroundColor(getResources().getColor(R.color.colorInvalidDateBg, null));
            tv_tuesday.setTextColor(getResources().getColor(R.color.colorInvalidDateText, null));
            cb_tuesday.setVisibility(View.GONE);
            iv_tuesday.setVisibility(View.VISIBLE);
        } else {
            if (cb_tuesday.isChecked()) {
                ll_tuesday.setBackgroundColor(getResources().getColor(R.color.colorPrimary, null));
                tv_tuesday.setTextColor(getResources().getColor(android.R.color.white, null));
                cb_tuesday.setButtonTintList(ColorStateList.valueOf(getResources().getColor(android.R.color.white, null)));
            } else {
                ll_tuesday.setBackgroundColor(getResources().getColor(android.R.color.white, null));
                tv_tuesday.setTextColor(getResources().getColor(android.R.color.black, null));
                cb_tuesday.setButtonTintList(ColorStateList.valueOf(getResources().getColor(android.R.color.black, null)));
            }

            cb_tuesday.setVisibility(View.VISIBLE);
            iv_tuesday.setVisibility(View.GONE);
        }

        if (itineraryExistence != null && itineraryExistence.getWednesday() != null && !itineraryExistence.getWednesday().isValid()) {
            ll_wednesday.setBackgroundColor(getResources().getColor(R.color.colorInvalidDateBg, null));
            tv_wednesday.setTextColor(getResources().getColor(R.color.colorInvalidDateText, null));
            cb_wednesday.setVisibility(View.GONE);
            iv_wednesday.setVisibility(View.VISIBLE);
        } else {
            if (cb_wednesday.isChecked()) {
                ll_wednesday.setBackgroundColor(getResources().getColor(R.color.colorPrimary, null));
                tv_wednesday.setTextColor(getResources().getColor(android.R.color.white, null));
                cb_wednesday.setButtonTintList(ColorStateList.valueOf(getResources().getColor(android.R.color.white, null)));
            } else {
                ll_wednesday.setBackgroundColor(getResources().getColor(android.R.color.white, null));
                tv_wednesday.setTextColor(getResources().getColor(android.R.color.black, null));
                cb_wednesday.setButtonTintList(ColorStateList.valueOf(getResources().getColor(android.R.color.black, null)));
            }

            cb_wednesday.setVisibility(View.VISIBLE);
            iv_wednesday.setVisibility(View.GONE);
        }

        if (itineraryExistence != null && itineraryExistence.getThursday() != null && !itineraryExistence.getThursday().isValid()) {
            ll_thursday.setBackgroundColor(getResources().getColor(R.color.colorInvalidDateBg, null));
            tv_thursday.setTextColor(getResources().getColor(R.color.colorInvalidDateText, null));
            cb_thursday.setVisibility(View.GONE);
            iv_thursday.setVisibility(View.VISIBLE);
        } else {
            if (cb_thursday.isChecked()) {
                ll_thursday.setBackgroundColor(getResources().getColor(R.color.colorPrimary, null));
                tv_thursday.setTextColor(getResources().getColor(android.R.color.white, null));
                cb_thursday.setButtonTintList(ColorStateList.valueOf(getResources().getColor(android.R.color.white, null)));
            } else {
                ll_thursday.setBackgroundColor(getResources().getColor(android.R.color.white, null));
                tv_thursday.setTextColor(getResources().getColor(android.R.color.black, null));
                cb_thursday.setButtonTintList(ColorStateList.valueOf(getResources().getColor(android.R.color.black, null)));
            }

            cb_thursday.setVisibility(View.VISIBLE);
            iv_thursday.setVisibility(View.GONE);
        }

        if (itineraryExistence != null && itineraryExistence.getFriday() != null && !itineraryExistence.getFriday().isValid()) {
            ll_friday.setBackgroundColor(getResources().getColor(R.color.colorInvalidDateBg, null));
            tv_friday.setTextColor(getResources().getColor(R.color.colorInvalidDateText, null));
            cb_friday.setVisibility(View.GONE);
            iv_friday.setVisibility(View.VISIBLE);
        } else {
            if (cb_friday.isChecked()) {
                ll_friday.setBackgroundColor(getResources().getColor(R.color.colorPrimary, null));
                tv_friday.setTextColor(getResources().getColor(android.R.color.white, null));
                cb_friday.setButtonTintList(ColorStateList.valueOf(getResources().getColor(android.R.color.white, null)));
            } else {
                ll_friday.setBackgroundColor(getResources().getColor(android.R.color.white, null));
                tv_friday.setTextColor(getResources().getColor(android.R.color.black, null));
                cb_friday.setButtonTintList(ColorStateList.valueOf(getResources().getColor(android.R.color.black, null)));
            }

            cb_friday.setVisibility(View.VISIBLE);
            iv_friday.setVisibility(View.GONE);
        }

        if (itineraryExistence != null && itineraryExistence.getSaturday() != null && !itineraryExistence.getSaturday().isValid()) {
            ll_saturday.setBackgroundColor(getResources().getColor(R.color.colorInvalidDateBg, null));
            tv_saturday.setTextColor(getResources().getColor(R.color.colorInvalidDateText, null));
            cb_saturday.setVisibility(View.GONE);
            iv_saturday.setVisibility(View.VISIBLE);
        } else {
            if (cb_saturday.isChecked()) {
                ll_saturday.setBackgroundColor(getResources().getColor(R.color.colorPrimary, null));
                tv_saturday.setTextColor(getResources().getColor(android.R.color.white, null));
                cb_saturday.setButtonTintList(ColorStateList.valueOf(getResources().getColor(android.R.color.white, null)));
            } else {
                ll_saturday.setBackgroundColor(getResources().getColor(android.R.color.white, null));
                tv_saturday.setTextColor(getResources().getColor(android.R.color.black, null));
                cb_saturday.setButtonTintList(ColorStateList.valueOf(getResources().getColor(android.R.color.black, null)));
            }

            cb_saturday.setVisibility(View.VISIBLE);
            iv_saturday.setVisibility(View.GONE);
        }

        if (itineraryExistence != null && itineraryExistence.getSunday() != null && !itineraryExistence.getSunday().isValid()) {
            ll_sunday.setBackgroundColor(getResources().getColor(R.color.colorInvalidDateBg, null));
            tv_sunday.setTextColor(getResources().getColor(R.color.colorInvalidDateText, null));
            cb_sunday.setVisibility(View.GONE);
            iv_sunday.setVisibility(View.VISIBLE);
        } else {
            if (cb_sunday.isChecked()) {
                ll_sunday.setBackgroundColor(getResources().getColor(R.color.colorPrimary, null));
                tv_sunday.setTextColor(getResources().getColor(android.R.color.white, null));
                cb_sunday.setButtonTintList(ColorStateList.valueOf(getResources().getColor(android.R.color.white, null)));
            } else {
                ll_sunday.setBackgroundColor(getResources().getColor(android.R.color.white, null));
                tv_sunday.setTextColor(getResources().getColor(android.R.color.black, null));
                cb_sunday.setButtonTintList(ColorStateList.valueOf(getResources().getColor(android.R.color.black, null)));
            }

            cb_sunday.setVisibility(View.VISIBLE);
            iv_sunday.setVisibility(View.GONE);
        }

        //means no available days
        if (iv_monday.getVisibility() == View.VISIBLE &&
                iv_tuesday.getVisibility() == View.VISIBLE &&
                iv_wednesday.getVisibility() == View.VISIBLE &&
                iv_thursday.getVisibility() == View.VISIBLE &&
                iv_friday.getVisibility() == View.VISIBLE &&
                iv_saturday.getVisibility() == View.VISIBLE &&
                iv_sunday.getVisibility() == View.VISIBLE) {
            llEachWeek.setVisibility(View.GONE);
            setPreviousSelectedStatus();
            rbEachWeek.setChecked(false);
            rbSpecificDate.setChecked(true);
        }
    }

    private void checkItinerary() {
        llCheckingItineraryExistence.setVisibility(View.VISIBLE);
        tvItineraryExistenceHint.setVisibility(View.GONE);
        Response.Listener listener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Gson gson = new Gson();
                llCheckingItineraryExistence.setVisibility(View.GONE);
                tvItineraryExistenceHint.setVisibility(View.VISIBLE);
                try {
                    currentItineraryExistence = gson.fromJson(response, ItineraryExistence.class);

                    if (currentItineraryExistence != null) {
                        if (currentItineraryExistence.getMonday() != null &&
                                currentItineraryExistence.getMonday().isValid()) {
                            cb_monday.setChecked(true);
                        }
                        if (currentItineraryExistence.getTuesday() != null &&
                                currentItineraryExistence.getTuesday().isValid()) {
                            cb_tuesday.setChecked(true);
                        }
                        if (currentItineraryExistence.getWednesday() != null &&
                                currentItineraryExistence.getWednesday().isValid()) {
                            cb_wednesday.setChecked(true);
                        }
                        if (currentItineraryExistence.getThursday() != null &&
                                currentItineraryExistence.getThursday().isValid()) {
                            cb_thursday.setChecked(true);
                        }
                        if (currentItineraryExistence.getFriday() != null &&
                                currentItineraryExistence.getFriday().isValid()) {
                            cb_friday.setChecked(true);
                        }
                    }
                    refreshDatesWidgets(currentItineraryExistence);

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        };
        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Logger.e("account", error.toString());
                llCheckingItineraryExistence.setVisibility(View.GONE);
                tvItineraryExistenceHint.setVisibility(View.VISIBLE);
                errorHandler(error, systemContext.getString(R.string.failed_check_trip));
            }
        };

        String url = Resource.API_ACCOUNT_CHECK_TRIP;

        Gson gson = new Gson();

        MonitoredTrip selectedMonitoredTrip = new MonitoredTrip();

        selectedMonitoredTrip.setItinerary(itineraryForNewTrip);
        selectedMonitoredTrip.setQueryParams(itineraryForNewTrip.getQueryParams());
        selectedMonitoredTrip.setOtp2QueryParams(itineraryForNewTrip.getOtp2QueryParams());


        String params = gson.toJson(selectedMonitoredTrip);

        JsonElement jsonElement = gson.fromJson(params, JsonElement.class);


        List<String> keysToRemove = Arrays.asList("__typename", "arriveBy", "dateCreated", "inactive", "lastUpdated",
                "notifyOnAlert", "notifyOnItineraryChange", "snoozed", "cost", "elevationGained", "elevationLost",
                "fareTableSum", "listViewType", "matchedItineraries", "matchedToOtheritinerary");

        removeFieldsRecursively(jsonElement, keysToRemove);

        params = gson.toJson(jsonElement);

        volleyAccountStringRequest(Request.Method.POST, url, params, listener, errorListener);

        refreshOnSelectedDate(itineraryForNewTrip.getStartTime());
    }

    private void initMonitoredTripUpdateRequest() {
        monitoredTripUpdateRequest.setTripName("");
        monitoredTripUpdateRequest.setMonday(false);
        monitoredTripUpdateRequest.setTuesday(false);
        monitoredTripUpdateRequest.setWednesday(false);
        monitoredTripUpdateRequest.setThursday(false);
        monitoredTripUpdateRequest.setFriday(false);
        monitoredTripUpdateRequest.setSaturday(false);
        monitoredTripUpdateRequest.setSunday(false);
    }

    private void enterSaveNewTripMode() {
        currentMode = Constants.HOME_PAGE_MODE_SAVE_NEW_TRIP;
        modeStack.add(currentMode);

        if (ll_save_new_trip == null) {
            setupSaveNewTrip();
            setupTravelCompanion();
        }
        ll_save_new_trip.setVisibility(View.VISIBLE);
        sv_edit_trip.post(new Runnable() {
            @Override
            public void run() {
                sv_edit_trip.scrollTo(0, 0);
            }
        });
        itineraryForNewTrip = currentSelectedItinerary;
        initMonitoredTripUpdateRequest();
        monitoredTripUpdateRequest.setItinerary(itineraryForNewTrip);
        monitoredTripUpdateRequest.setQueryParams(itineraryForNewTrip.getQueryParams());

        refreshTripDates();
        currentItineraryExistence = null;
        refreshDatesWidgets(currentItineraryExistence);
        getSavedTripList();
        checkItinerary();

        String userPreferencesString = Configurations.getString("account_user_preferences", "");
        Gson gson = new Gson();
        if (userPreferencesString != null && !userPreferencesString.isEmpty()) {
            try {
                UserPreferences userPreferences = gson.fromJson(userPreferencesString, UserPreferences.class);
                monitoredTripUpdateRequest.setUserId(userPreferences.getId());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        ll_monitor_trip.setVisibility(View.GONE);
        iv_advanced_settings.setImageResource(R.drawable.ic_resume);

        setSpinnerIndex();

        et_trip_name.setText(monitoredTripUpdateRequest.getTripName());
        if (monitoredTripUpdateRequest.getItinerary() != null) {
            ItineraryForSaveTrip itinerary = monitoredTripUpdateRequest.getItinerary();
            int durationMin = monitoredTripUpdateRequest.getItinerary().getDuration() / 60;
            if (durationMin >= 60) {
                tv_itinerary_duration.setText(getString(R.string.hr_min_with_num, durationMin / 60,
                        durationMin % 60));
            } else {
                tv_itinerary_duration.setText(getString(R.string.min_with_num, durationMin));
            }

            Flow flow = new Flow(systemContext);
            int gap = Convert.dpToPx(8);
            flow.setWrapMode(Flow.WRAP_CHAIN);
            flow.setHorizontalStyle(Flow.CHAIN_PACKED);
            flow.setHorizontalAlign(Flow.HORIZONTAL_ALIGN_START);
            flow.setHorizontalBias(0f);
            flow.setVerticalGap(gap);
            flow.setHorizontalGap(gap);
            flow.setOrientation(Flow.HORIZONTAL);

            constraintLayout.removeAllViews();
            constraintLayout.addView(flow);
            HashMap<String, HashMap<String, HashMap<String, Double>>> fareTable = new HashMap<>();
            HashSet<String> productIds = new HashSet<>();

            if (itinerary.getLegs() != null && itinerary.getLegs().size() > 0) {
                ArrayList<Integer> ids = new ArrayList<>();
                String previousMode = null;
                int walkTimeSeconds = 0;
                for (int i = 0; i < itinerary.getLegs().size(); i++) {
                    ItineraryLeg itineraryLeg = itinerary.getLegs().get(i);
                    if (itineraryLeg.getMode() != null) {
                        String mode = Utilities.getMappedModeForItinerary(getActivity(), itineraryLeg.getRouteId(),
                                itineraryLeg.getMode(), itineraryLeg.getRouteShortName(), itineraryLeg.getRouteLongName()).toLowerCase();

                        if (mode.equalsIgnoreCase("walk")) {
                            walkTimeSeconds += Math.abs(itineraryLeg.getEndTime() - itineraryLeg.getStartTime()) / 1000;
                            if (itineraryLeg.getDistance() <= 400) {
                                continue;
                            }
                        }
                        if (previousMode == null || !previousMode.equals(mode)) {
                            previousMode = mode;

                            View modeView = ((LayoutInflater) systemContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.view_group_itinerary_mode, null, false);
                            ImageView ivItineraryMode = modeView.findViewById(R.id.iv_itinerary_mode);
                            RelativeLayout rlItineraryMode = modeView.findViewById(R.id.rl_itinerary_mode);
                            ivItineraryMode.setImageResource(Utilities.getModeImageByMode(mode,
                                    itineraryLeg.isRentedVehicle(), requireActivity(), ivItineraryMode, rlItineraryMode));
                            ivItineraryMode.setColorFilter(getResources().getColor(android.R.color.white));
                            modeView.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(android.R.color.black)));
                            modeView.setMinimumHeight(Convert.dpToPx(50));
                            modeView.setMinimumWidth(Convert.dpToPx(50));
                            int viewID = View.generateViewId();
                            modeView.setId(viewID);

                            ids.add(viewID);
                            constraintLayout.addView(modeView);
                        }

                    }
                    //update route
                    View routeNumberView = ((LayoutInflater) systemContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.view_group_itinerary_transit_number, null, false);
                    View viewRouteNumberHeader = routeNumberView.findViewById(R.id.view_route_number_header);
                    TextView tvRouteNumber = routeNumberView.findViewById(R.id.tv_route_number);

                    if (itineraryLeg.getRouteShortName() != null) {
                        tvRouteNumber.setText(itineraryLeg.getRouteShortName());
                    }

                    if (itineraryLeg.getOriginalRoute() != null) {

                        if (itineraryLeg.getOriginalRoute().getShortName() != null && !itineraryLeg.getOriginalRoute().getShortName().isEmpty()) {
                            tvRouteNumber.setText(itineraryLeg.getOriginalRoute().getShortName());
                        } else {
                            tvRouteNumber.setText(itineraryLeg.getOriginalRoute().getLongName());
                        }

                        String routeColor = itineraryLeg.getRouteColor();
                        if (routeColor == null && itineraryLeg.getOriginalRoute() != null) {
                            routeColor = itineraryLeg.getOriginalRoute().getColor();
                        }

                        String textColor = itineraryLeg.getRouteTextColor();
                        if (textColor == null && itineraryLeg.getOriginalRoute() != null) {
                            textColor = itineraryLeg.getOriginalRoute().getTextColor();
                        }


                        if (routeColor == null && itineraryLeg.getOriginalRoute() != null) {
                            routeColor = itineraryLeg.getOriginalRoute().getColor();
                            textColor = itineraryLeg.getOriginalRoute().getTextColor();
                        }

                        if (routeColor != null) {
                            if (!routeColor.startsWith("#")) {
                                routeColor = "#" + routeColor;
                            }
                            int routeColorInt = Color.parseColor(routeColor);
                            int textBackgroundColorInt = routeColorInt;
                            if (routeColorInt < 0x1000000) {
                                textBackgroundColorInt = routeColorInt + 0xAF000000;
                            }

                            viewRouteNumberHeader.setBackgroundTintList(ColorStateList.valueOf(routeColorInt));
                            tvRouteNumber.setBackgroundTintList(ColorStateList.valueOf(textBackgroundColorInt));
                        }
                        if (textColor != null) {
                            if (!textColor.startsWith("#")) {
                                textColor = "#" + textColor;
                            }
                            tvRouteNumber.setTextColor(Color.parseColor(textColor));
                        }

                        int routeNumberViewID = View.generateViewId();
                        routeNumberView.setId(routeNumberViewID);
                        ids.add(routeNumberViewID);
                        constraintLayout.addView(routeNumberView);
                    }

                    View transfer = ((LayoutInflater) systemContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.view_group_itinerary_transfer, null, false);
                    int transferViewID = View.generateViewId();
                    transfer.setId(transferViewID);
                    ids.add(transferViewID);
                    constraintLayout.addView(transfer);
                }

                //the last one does not need transfer
                if (ids.size() > 0) {
                    constraintLayout.removeViewAt(constraintLayout.getChildCount() - 1);
                    ids.remove(ids.size() - 1);
                    int[] idsArray = new int[ids.size()];
                    for (int i = 0; i < idsArray.length; i++) {
                        idsArray[i] = ids.get(i);
                    }
                    flow.setReferencedIds(idsArray);
                }
            }


            String startTime = Utilities.formatTimestamp(systemContext, itinerary.getStartTime());
            String endTime = Utilities.formatTimestamp(systemContext, itinerary.getEndTime());

            tv_itinerary_start_end_time.setText(Html.fromHtml(getString(R.string.itinerary_start_end_time, startTime, endTime)));
        }

        refreshTravelCompanion();
    }

    private void createNewTrip() {
        if (getActivity() != null) {
            ((TabActivity) getActivity()).showProgressBar();
        }
        Response.Listener listener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (getActivity() != null) {
                    ((TabActivity) getActivity()).hideProgressBar();
                }
                if (getActivity() != null) {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
                    // set title
                    alertDialogBuilder.setTitle("");
                    // set dialog message
                    alertDialogBuilder
                            .setMessage(systemContext.getString(R.string.successfully_create_trip))
                            .setCancelable(false)
                            .setPositiveButton(systemContext.getString(R.string.ok), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.dismiss();
                                    exitSaveNewTripMode();
                                }
                            });

                    alertDialogBuilder.create().show();
                }
            }
        };
        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (getActivity() != null) {
                    ((TabActivity) getActivity()).hideProgressBar();
                }
                Logger.e("account", error.toString());
                errorHandler(error, systemContext.getString(R.string.failed_create_trip));
            }
        };

        String url = Resource.API_ACCOUNT_SAVE_NEW_TRIP;

        Gson gson = new Gson();
        CreateNewTripRequest createNewTripRequest = new CreateNewTripRequest();
        createNewTripRequest.copyFromMonitoredTripUpdateRequest(monitoredTripUpdateRequest);
        String params = gson.toJson(createNewTripRequest);

        JsonElement jsonElement = gson.fromJson(params, JsonElement.class);

        List<String> keysToRemove = List.of("__typename");

        removeFieldsRecursively(jsonElement, keysToRemove);

        params = gson.toJson(jsonElement);

        volleyAccountStringRequest(Request.Method.POST, url, params, listener, errorListener);

    }

    private static void removeFieldsRecursively(JsonElement jsonElement, List<String> keysToRemove) {
        if (jsonElement.isJsonObject()) {
            JsonObject jsonObject = jsonElement.getAsJsonObject();

            for (String key : keysToRemove) {
                jsonObject.remove(key);
            }

            for (String key : jsonObject.keySet()) {
                JsonElement childElement = jsonObject.get(key);
                removeFieldsRecursively(childElement, keysToRemove);
            }

        } else if (jsonElement.isJsonArray()) {
            JsonArray jsonArray = jsonElement.getAsJsonArray();
            for (JsonElement element : jsonArray) {
                removeFieldsRecursively(element, keysToRemove);
            }
        }
    }

    private void errorHandler(VolleyError error, @NonNull String defaultMessage) {
        if (error != null && error instanceof NoConnectionError) {
            Utilities.showNormalPopupDialog(getActivity(), "", systemContext.getString(R.string.connection_failed_msg));
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
                Utilities.showNormalPopupDialog(getActivity(), "", msg);
            }
        } else {
            Utilities.showNormalPopupDialog(getActivity(), "", defaultMessage);
        }
    }

    public void setMapCenterAndZoomIfNeeded() {
        if (currentMode != Constants.HOME_PAGE_MODE_ITINERARY_LIST
                && currentMode != Constants.HOME_PAGE_MODE_ITINERARY_DETAIL
                && gMapboxMap != null
                && MyApplication.getInstance().mapTargetOnStopViewerPage != null
                && MyApplication.getInstance().mapZoomOnStopViewerPage > 0.0) {
            gMapboxMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder()
                    .target(MyApplication.getInstance().mapTargetOnStopViewerPage)
                    .zoom(MyApplication.getInstance().mapZoomOnStopViewerPage)
                    .build()));
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ll_Bottom_tab = ((TabActivity) this.requireActivity()).getBottomTab();
    }

    class CustomExpListView extends ExpandableListView {
        int expandedHeight = Integer.MAX_VALUE;

        public CustomExpListView(Context context) {
            super(context);
        }

        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            widthMeasureSpec = MeasureSpec.makeMeasureSpec(960, MeasureSpec.AT_MOST);
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(expandedHeight, MeasureSpec.AT_MOST);
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }

    private class FareNode {
        public String name;
        public double price;
        public double discount = 0.0;

        public FareNode(String name, double price) {
            this.name = name;
            this.price = price;
        }

        public FareNode(String name, double price, double discount) {
            this.name = name;
            this.price = price;
            this.discount = discount;
        }
    }

    private class CustomSpinnerAdapter extends ArrayAdapter<String> {

        private final Context context;
        private final String[] leaveStatusItems;

        public CustomSpinnerAdapter(Context context, String[] leaveStatusItems) {
            super(context, R.layout.search_options_spinner, leaveStatusItems);
            this.context = context;
            this.leaveStatusItems = leaveStatusItems;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                LayoutInflater inflater = LayoutInflater.from(context);
                convertView = inflater.inflate(R.layout.search_options_spinner, parent, false);
            }

            TextView textViewItem = convertView.findViewById(R.id.tv_name);

            textViewItem.setText(leaveStatusItems[position]);

            return convertView;
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            View view = convertView;
            if (view == null) {
                view = LayoutInflater.from(requireContext()).inflate(R.layout.search_options_spinner_item, parent, false);
            }

            TextView textView = view.findViewById(R.id.text_view_item);
            textView.setText(leaveStatusItems[position]);

            ImageView checkIcon = view.findViewById(R.id.iv_check_mark);

            if (position == tempSearchOptionsSpinnerIndex) {
                checkIcon.setVisibility(View.VISIBLE);
            } else {
                checkIcon.setVisibility(View.GONE);
            }

            return view;
        }
    }


    private class ExpandableListAdapter extends BaseExpandableListAdapter {
        private final Context context;
        private final List<ParentItem> parentItems;

        public ExpandableListAdapter(Context context, List<ParentItem> parentItems) {
            this.context = context;
            this.parentItems = parentItems;
        }

        // Helper method to check if all submodes are off
        private boolean areAllSubmodesDeselected(ParentItem parentItem) {
            for (ChildItem child : parentItem.getChildren()) {
                if (child.isCheckedTemp()) {
                    return false;
                }
            }
            return true;  // All submodes are off
        }

        @Override
        public int getGroupCount() {
            return parentItems.size();
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return parentItems.get(groupPosition).getChildren().size();
        }

        @Override
        public Object getGroup(int groupPosition) {
            return parentItems.get(groupPosition);
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            return parentItems.get(groupPosition).getChildren().get(childPosition);
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return groupPosition * 1000L + childPosition;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }


        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.parent_item_layout, null);
            }

            if (groupPosition >= parentItems.size()) {
                return convertView;
            }
            TextView parentNameTextView = convertView.findViewById(R.id.parentNameTextView);
            SwitchCompat toggleBtn = convertView.findViewById(R.id.toggle_alter_modes);
            toggleBtn.getThumbDrawable().setColorFilter(requireActivity().getResources().getColor(R.color.superLightGrey), PorterDuff.Mode.SRC_IN);

            ImageView ivLogo = convertView.findViewById(R.id.iv_logo);

            ParentItem currentItem = parentItems.get(groupPosition);
            parentNameTextView.setText(currentItem.getName());
            Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), currentItem.getIconResId());
            Bitmap roundedBitmap = getRoundedBitmap(bitmap);
            ivLogo.setImageBitmap(roundedBitmap);

            currentItem.setExpanded(isExpanded);
            currentItem.setToggleChecked(isExpanded);
            toggleBtn.setChecked(isExpanded);

            boolean isToggleChecked = currentItem.isToggleChecked();
            toggleBtn.setOnClickListener(v -> {
                boolean newToggleState = !isToggleChecked;
                currentItem.setToggleChecked(newToggleState);

                // If turning ON and all submodes are off, enable all submodes
                if (newToggleState) {
                    if (areAllSubmodesDeselected(currentItem)) {
                        for (ChildItem child : currentItem.getChildren()) {
                            child.setCheckedTemp(true);
                        }
                    }
                }

                // Handle group expansion/collapse based on toggle state
                if (expandableListView.isGroupExpanded(groupPosition)) {
                    expandableListView.collapseGroup(groupPosition);
                    currentItem.setCheckedTemp(false);
                } else {
                    expandableListView.expandGroup(groupPosition, true);
                    currentItem.setCheckedTemp(true);
                }

                notifyDataSetChanged();
            });

            return convertView;
        }


        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.child_item_layout, null);
            }

            GridView gridView = convertView.findViewById(R.id.childGridView);
            View dividerView = convertView.findViewById(R.id.view_divider);
            TextView tvAlterMode = convertView.findViewById(R.id.tv_alter_mode);

            // Show/hide divider and alternate mode based on position
            dividerView.setVisibility(childPosition == 0 ? View.VISIBLE : View.GONE);
            tvAlterMode.setVisibility(View.GONE); // always hide for now

            ExpandableListChildAdapter adapter = new ExpandableListChildAdapter(context, parentItems.get(groupPosition).getChildren(), childPosition, groupPosition);
            gridView.setAdapter(adapter);

            return convertView;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }

        private Bitmap getRoundedBitmap(Bitmap bitmap) {
            Bitmap roundedBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(roundedBitmap);
            Paint paint = new Paint();
            paint.setAntiAlias(true);
            paint.setShader(new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP));
            float radius = Math.min(bitmap.getWidth(), bitmap.getHeight()) / 2f;
            canvas.drawCircle(bitmap.getWidth() / 2f, bitmap.getHeight() / 2f, radius, paint);
            return roundedBitmap;
        }

        private void updateCheckStates(boolean isChecked, String itemName, int groupPosition) {
            ParentItem currentGroup = parentItems.get(groupPosition);

            // Apply "All Transit" checkbox logic only to the "Transit" group
            if (currentGroup.getName().equals(context.getString(R.string.transit))) {
                for (ChildItem c : currentGroup.getChildren()) {
                    if (itemName.equals(context.getString(R.string.all_transit))) {
                        c.setCheckedTemp(isChecked);
                    } else if (c.getName().equals(context.getString(R.string.all_transit))) {
                        boolean allChecked = true;
                        for (ChildItem child : currentGroup.getChildren()) {
                            if (!child.getName().equalsIgnoreCase(context.getString(R.string.all_transit)) && !child.isCheckedTemp()) {
                                allChecked = false;
                                break;
                            }
                        }
                        c.setCheckedTemp(allChecked);
                    }
                }
                notifyDataSetChanged();
            }
        }

        private class ExpandableListChildAdapter extends BaseAdapter {
            private final Context context;
            private final List<ChildItem> childItems;
            private final int childPosition;
            private final int groupPosition;

            public ExpandableListChildAdapter(Context context, List<ChildItem> childItems, int childPosition, int groupPosition) {
                this.context = context;
                this.childItems = childItems;
                this.childPosition = childPosition;
                this.groupPosition = groupPosition;
            }

            @Override
            public int getCount() {
                return childItems.size();
            }

            @Override
            public Object getItem(int position) {
                return childItems.get(position);
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if (convertView == null) {
                    LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    convertView = inflater.inflate(R.layout.grid_item_layout, null);
                }
                CheckBox checkBox = convertView.findViewById(R.id.checkBox);


                if (parent instanceof GridView) {
                    GridView gridView = (GridView) parent;
                    int index = position + gridView.getNumColumns() * childPosition;

                    if (index >= 0 && index < childItems.size()) {
                        ChildItem currentItem = childItems.get(index);
                        checkBox.setText(currentItem.getName());
                        checkBox.setVisibility(View.VISIBLE);
                        checkBox.setOnCheckedChangeListener(null);
                        checkBox.setChecked(currentItem.isCheckedTemp());

                        //update checkbox Icon
                        Drawable drawable = ContextCompat.getDrawable(requireActivity(), currentItem.getIconID());
                        Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
                        float density = getResources().getDisplayMetrics().density;
                        int size = 10;
                        int sizeInDP = Math.round((float) size * density);
                        int width = sizeInDP;
                        int height = sizeInDP;
                        Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, width, height, true);

                        Drawable scaledDrawable = new BitmapDrawable(getResources(), scaledBitmap);
                        checkBox.setCompoundDrawablesWithIntrinsicBounds(scaledDrawable, null, null, null);

                        checkBox.setOnCheckedChangeListener((compoundButton, isChecked) -> {
                            ChildItem item = childItems.get(index);
                            item.setCheckedTemp(isChecked);
                            //if this is the last check box, we should turn off the toggle button
                            updateToggleStatus();
                        });
                    } else {
                        checkBox.setVisibility(View.GONE);
                    }
                }

                return convertView;
            }

            private void updateToggleStatus() {
                ParentItem parentItem = parentItems.get(groupPosition);
                if (areAllSubmodesDeselected(parentItem)) {
                    expandableListView.collapseGroup(groupPosition);
                    parentItem.setCheckedTemp(false);
                }
            }
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        requireActivity().getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                goBackToRouteSearchReady();
            }
        });
    }
}