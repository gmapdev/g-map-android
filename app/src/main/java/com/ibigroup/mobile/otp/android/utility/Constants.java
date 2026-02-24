package com.ibigroup.mobile.otp.android.utility;

public class Constants {
    public static final String font_asset_path = "fonts/Helvetica.ttf";

    public final static double DEFAULT_FOCUS_POINT_LATITUDE = 33.749;
    public final static double DEFAULT_FOCUS_POINT_LONGITUDE = -84.388;

    public final static int MAX_TRANSIT_DISTANCE = 1000;

    public final static int MAX_AUTOCOMPLETE_RESULT_NUM = 10;
    public final static int MAX_RECENTLY_SEARCHED_NUM = 3;

    public final static int SELECTED_LOCATION_MODE_POINT = 0;
    public final static int SELECTED_LOCATION_MODE_PARK_RIDE = 1;
    public final static int SELECTED_LOCATION_MODE_TRANSIT_STOP = 2;
    public final static int SELECTED_LOCATION_MODE_VEHICLE_ON_ROUTE = 3;
    public final static int SELECTED_LOCATION_MODE_RENTAL_VEHICLE = 5;

    public final static double TRANSIT_STOP_MARKER_MIN_ZOOM_LEVEL = 13.6;
    public final static double MARKER_POPUP_ZOOM_LEVEL = 13.9;
    public final static String IS_ORIGIN = "is_origin";
    public final static String CURRENT_LOCATION_AVAILABLE = "current_location_available";
    public final static String ADDRESS = "address";
    public final static String SEARCHED_ADDRESS = "searched_address";
    public final static String SEARCHED_LATITUDE = "searched_latitude";
    public final static String SEARCHED_LONGITUDE = "searched_longitude";
    public final static String IS_ADDING_LOCATION = "is_adding_location";

    public final static String AUTH0_SUCCESS = "auth0_success";

    public final static String CURRENT_ITINERARY = "current_itinerary";
    public final static String SELECTED_ITINERARY_LEG = "selected_itinerary_leg";
    public final static String STOP_VIEWER_ITINERARY_LEG_POINT = "stop_viewer_itinerary_leg_point";
    public final static String STOP_VIEWER_ITINERARY_MODE = "stop_mode";
    public final static String CURRENT_ORIGIN = "current_origin";
    public final static String CURRENT_DESTINATION = "current_destination";

    public final static String SELECTED_ROUTE = "selected_route";

    public final static int DEFAULT_MAXIMUM_WALK_INDEX = 3;
    public final static int DEFAULT_WALK_SPEED_INDEX = 2;
    public final static int DEFAULT_OPTIMIZE_OPTION_INDEX = 0;

    public final static int ITINERARY_SORT_DIRECTION_SHORT_TO_LONG = 0;
    public final static int ITINERARY_SORT_DIRECTION_LONG_TO_SHORT = 1;

    public final static String OPTIMIZE_SPEED = "QUICK";
    public final static String OPTIMIZE_FEWEST_TRANSFERS = "TRANSFERS";

    public final static double WALK_SPEED_MPH = 0.447;
    public final static double MAX_WALK_MILE = 1609;//1 Mile is 1609 meter.
    public final static String DEFAULT_ROUTE_SEARCH_STRING = "showIntermediateStops=true&ignoreRealtimeUpdates=false&otherThanPreferredRoutesPenalty=900";


    public final static int ITINERARY_SORT_BY_BEST_OPTION = 0; // remove the best option sorting
    public final static int ITINERARY_SORT_BY_DURATION = 1;
    public final static int ITINERARY_SORT_BY_ARRIVE_TIME = 2;
    public final static int ITINERARY_SORT_BY_DEPARTURE_TIME = 3;
    public final static int ITINERARY_SORT_BY_WALK_TIME = 4;
    public final static int ITINERARY_SORT_BY_COST = 5;


    public final static String CALORIES_GUIDELINES_URL = "https://health.gov/dietaryguidelines/dga2005/document/html/chapter3.htm#table4";

    public final static int HOME_PAGE_MODE_START_OVER = 0;
    public final static int HOME_PAGE_MODE_ROUTE_SEARCH_READY = 1;
    public final static int HOME_PAGE_MODE_ITINERARY_LIST = 2;
    public final static int HOME_PAGE_MODE_ITINERARY_DETAIL = 3;
    public final static int HOME_PAGE_MODE_STOP_VIEWER = 4;
    public final static int HOME_PAGE_MODE_TRIP_VIEWER = 5;
    public final static int HOME_PAGE_MODE_ROUTE_VIEWER = 6;
    public final static int HOME_PAGE_MODE_SAVE_NEW_TRIP = 7;

    public final static String DEFAULT_TRIP_VIEWER_HIGHLIGHT_COLOR = "#13C1C1";

    public final static int GPS_REVERSE_MODE_MARKER_VIEW = 0;
    public final static int GPS_REVERSE_MODE_STOP_VIEWER_FROM = 1;
    public final static int GPS_REVERSE_MODE_STOP_VIEWER_TO = 2;

    public final static double BIKE_SHARE_TRIP_COST_CENTS = 200.0;
    public final static double CAR_PARKING_COST_CENTS = 600.0;
    public final static double DRIVING_CENTS_PER_MILE = 44.5;

    public final static double BEST_OPTION_WALK_RELUCTANCE_WEIGHT = 0.1;
    public final static double BEST_OPTION_DRIVE_RELUCTANCE_WEIGHT = 2;
    public final static double BEST_OPTION_DURATION_FACTOR_WEIGHT = 0.25;
    public final static double BEST_OPTION_FARE_FACTOR_WEIGHT = 0.5;
    public final static double BEST_OPTION_WAIT_RELUCTANCE_WEIGHT = 0.1;
    public final static double BEST_OPTION_TRANSFER_RELUCTANCE_WEIGHT = 0.9;

    // Gets a comparator value for a given route's type (OTP mode).
    public final static int MODE_COMPARATOR_VALUE_TRAM = 1;
    public final static int MODE_COMPARATOR_VALUE_SUBWAY = 0;
    public final static int MODE_COMPARATOR_VALUE_RAIL = 2;
    public final static int MODE_COMPARATOR_VALUE_BUS = 5;
    public final static int MODE_COMPARATOR_VALUE_FERRY = 4;
    public final static int MODE_COMPARATOR_VALUE_CABLE_CAR = 5;
    public final static int MODE_COMPARATOR_VALUE_GONDOLA = 6;
    public final static int MODE_COMPARATOR_VALUE_FUNICULAR = 7;

    public final static int MODE_COMPARATOR_VALUE_STREETCAR = 3;
    public final static int MODE_COMPARATOR_VALUE_WATER_TAXI = 3;
    public final static int MODE_COMPARATOR_VALUE_LINK = 1;

    public final static int SEARCH_ADDRESS_MODE_HOME = 0;
    public final static int SEARCH_ADDRESS_MODE_WORK = 1;
    public final static int SEARCH_ADDRESS_MODE_NEW = 2;

    public final static int ADDRESS_SEARCH_REQUEST_CODE = 19526;
    public final static int LOGIN_SIGNUP_REQUEST_CODE = 19528;
    public final static int TRIP_VIEWER_REQUEST_CODE = 19556;
    public final static int STOP_VIEWER_REQUEST_CODE = 19558;
    public final static int AUTH0_REQUEST_CODE = 19559;
    public final static int ROUTE_VIEWER_FILTER_REQUEST_CODE = 19566;
    public final static int STOPS_FILTER_REQUEST_CODE = 19568;

    public final static int DEFAULT_NOTIFY_ON_ALERT_INDEX = 0;
    public final static int DEFAULT_NOTIFY_ON_ITINERARY_CHANGE_INDEX = 0;
    public final static int DEFAULT_DELAY_INDEX = 0;
    public final static int DEFAULT_LEAD_TIME_INDEX = 1;

    public final static int BOTTOM_TAB_SELECTED_PLAN_TRIP = 0;
    public final static int BOTTOM_TAB_SELECTED_ROUTES = 1;
    public final static int BOTTOM_TAB_SELECTED_STOPS = 2;
    public final static int BOTTOM_TAB_SELECTED_SIGN_IN = 3;
    public final static int BOTTOM_TAB_SELECTED_MY_TRIPS = 4;
    public final static int BOTTOM_TAB_SELECTED_AVATAR = 5;

    public final static int EXPANDED_STATUS_UNEXPANDED = 0;
    public final static int EXPANDED_STATUS_HALF_EXPANDED = 1;
    public final static int EXPANDED_STATUS_FULL_EXPANDED = 2;
    public final static int ON_TIME_THRESHOLD_SECONDS = 60;

    public final static String ROUTE_FILTER_AGENCY_NAME = "agency_name";
    public final static String ROUTE_FILTER_MODE_NAME = "mode_name";
    public static final int MY_PERMISSIONS_REQUEST_FINE_LOCATION = 9000;
    public static final int MY_PERMISSIONS_REQUEST_POST_PUSH = 9006;
    public final static int ITINERARY_DETAIL_MIN_HEIGHT = 146;
    public final static int ITINERARY_DETAIL_MAX_HEIGHT = 236;//gDm.heightPixels - Convert.dpToPx(236)
    public final static int ITINERARY_DETAIL_INITIAL_HEIGHT = 146;

    public final static int ROUTES_MIN_HEIGHT = 116;
    public final static int ROUTES_MAX_HEIGHT = 230;//gDm.heightPixels - Convert.dpToPx(233)

    public static final String MENU_ACTION_TRIGGER_INTERNAL_WEB = "trigger internal web";
    public static final String MENU_ACTION_TRIGGER_EXTERNAL_WEB = "trigger external web";
    public static final String MENU_ACTION_LINK = "link";
    public static final String MENU_ACTION_FUNCTION = "function";

    public final static String ROUTE_FILTER_AGENCY_KEY = "route_filter_agency";
    public final static String ROUTE_FILTER_MODE_KEY = "route_filter_mode";

    public final static int GROUP_ITINERARY_TYPE_WALK_TRANSIT = 0;
    public final static int GROUP_ITINERARY_TYPE_DRIVE_TRANSIT = 1;
    public final static int GROUP_ITINERARY_TYPE_OTHER = 2;
    public final static int GROUP_ITINERARY_TYPE_BIKE_TRANSIT = 3;
    public final static int GROUP_ITINERARY_TYPE_BIKESHARE_TRANSIT = 4;
    public final static int GROUP_ITINERARY_TYPE_SCOOTER_TRANSIT = 5;

    public final static int MENU_TAG = 6;
    public final static int PLAN_TRIP_TAG = 0;
    public final static int ROUTES_TAG = 1;
    public final static int MY_TRIPS_TAG = 4;
    public final static int SIGN_IN_TAG = 3;
    public final static int USER_NAME_TAG = 5;


    //for config
    public static final int MAX_ENCRYPT_STRING_LENGTH = 4096;
    public static final String FEATURE_DATA = "_feature_data";
    public static final String MODE_COMBINATION_FILE_NAME = "mode_combinations.json";

    public static final String NOTIFICATION_KEY_TRIP_ID = "trip_id";

    public static final String NOTIFICATION_KEY_TYPE = "notification_type";
    public static final String NOTIFICATION_KEY_SURVEY_SUBDOMAIN = "survey_subdomain";
    public static final String NOTIFICATION_KEY_SURVEY_ID = "survey_id";
    public static final String NOTIFICATION_KEY_USER_ID = "user_id";
    public static final String NOTIFICATION_KEY_NOTIFICATION_ID = "notification_id";

    public static float METER_TO_FOOT = 3.28084f;
    public static float MILE_TO_METER = 1609.344051499f;

    public final static String TTS_AUDIO_FILE_NAME = "tts_audio.mp3";
    public static final int AUDIO_PLAYBACK_IDLE = 0;
    public static final int AUDIO_PLAYBACK_PLAYING = 1;
    public static final int AUDIO_PLAYBACK_PAUSED = 2;

    public static final int AUDIO_PLAYBACK_TUNNEL_IVR = 0;
    public static final int AUDIO_PLAYBACK_TUNNEL_TTS = 1;

    public static final int POPUP_TYPE_SINGLE_SELECTION = 0;
    public static final int POPUP_TYPE_MULTIPLE_SELECTION = 1;

    public static final long DEFAULT_INTERVAL_IN_MILLISECONDS = 500L;
    public static final long DEFAULT_MAX_WAIT_TIME = DEFAULT_INTERVAL_IN_MILLISECONDS * 5;

    public static final int NOTIFICATION_ID = 9096;

    public static String INDOOR_NAVIGATION_ID = "indoor_navigation";
    public static String LIVE_TRACKING_ID = "live_tracking";
    public static String TRAVEL_COMPANION_ID = "travel_companion";
    public static String MOBILITY_PROFILE_ID = "mobility_profile";
    public static String PREVIEW_TRIP_ID = "preview_trip";
    public static String FARE_INFO_ID = "fare_info";
    public static String TRIP_DETAIL_ID = "trip_detail";

    public static int searchOptionsSpinnerIndex = 0;

    public enum REALTIME_STATUS {
        EARLY,
        LATE,
        ON_TIME,
        SCHEDULED
    }
}
