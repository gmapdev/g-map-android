package com.ibigroup.mobile.otp.android.api;

import static com.ibigroup.mobile.otp.android.utility.Configurations.API_VERSION;
import static com.ibigroup.mobile.otp.android.utility.Configurations.APP_DOMAIN;
import static com.ibigroup.mobile.otp.android.utility.Configurations.AUTOCOMPLETE_DOMAIN;

import com.ibigroup.mobile.otp.android.utility.Configurations;

public interface Resource {
    String API_PARK_AND_RIDE = Configurations.getString("app_domain", APP_DOMAIN) + API_VERSION + "/routers/default/park_and_ride";
    String API_TRANSIT_STOP = Configurations.getString("app_domain", APP_DOMAIN) + API_VERSION + "/routers/default/index/stops";
     String API_AUTOCOMPLETE = AUTOCOMPLETE_DOMAIN + "autocomplete?text=%s" +
            "&boundary.rect.min_lat=%f&boundary.rect.min_lon=%f&boundary.rect.max_lat=%f&boundary.rect.max_lon=%f" +
            "&focus.point.lat=%f&focus.point.lon=%f";
    String API_AUTOCOMPLETE_WITHOUT_FOCUS_POINT = AUTOCOMPLETE_DOMAIN + "autocomplete?text=%s" +
            "&boundary.rect.min_lat=%f&boundary.rect.min_lon=%f&boundary.rect.max_lat=%f&boundary.rect.max_lon=%f";

    String API_GPS_REVERSE = AUTOCOMPLETE_DOMAIN + "reverse?point.lat=%f&point.lon=%f";

    String API_TRIP_PLAN = Configurations.getString("app_domain", APP_DOMAIN) + API_VERSION + "/routers/default/plan";

     //input Stop ID and current timestamp
    String API_STOP_VIEWER_NEXT_ARRIVAL = Configurations.getString("app_domain", APP_DOMAIN) + API_VERSION + "/routers/default/index/stops/%s/stoptimes?numberOfDepartures=3&showBlockIds=false&timeRange=345600&startTime=%d";
    //Stop ID and selected Date:YYYYMMDD
    String API_STOP_VIEWER_SCHEDULE = Configurations.getString("app_domain", APP_DOMAIN) + API_VERSION + "/routers/default/index/stops/%s/stoptimes/%s?numberOfDepartures=3&showBlockIds=false&timeRange=345600";

    String API_TRIP_VIEWER_STOPS = Configurations.getString("app_domain", APP_DOMAIN) + API_VERSION + "/routers/default/index/trips/%s/stops";//TRIP ID
    String API_TRIP_VIEWER_STOP_TIMES = Configurations.getString("app_domain", APP_DOMAIN) + API_VERSION + "/routers/default/index/trips/%s/stoptimes";//TRIP ID
    String API_TRIP_VIEWER_INFO = Configurations.getString("app_domain", APP_DOMAIN) + API_VERSION + "/routers/default/index/trips/%s";//TRIP ID
    String API_TRIP_VIEWER_GEOMETRY = Configurations.getString("app_domain", APP_DOMAIN) + API_VERSION + "/routers/default/index/trips/%s/geometry";//TRIP ID

    String API_ROUTE_VIEWER_ROUTES = Configurations.getString("app_domain", APP_DOMAIN) + API_VERSION + "/routers/default/index/routes";
    String API_ROUTE_VIEWER_ROUTE_DETAIL = Configurations.getString("app_domain", APP_DOMAIN) + API_VERSION + "/routers/default/index/routes/%s";//ROUTE ID
    String API_ROUTE_VIEWER_GEOMETRY = Configurations.getString("app_domain", APP_DOMAIN) + API_VERSION + "/routers/default/index/patterns/%s/geometry";//ROUTE SUB ID
    String API_ROUTE_VIEWER_VEHICLES = Configurations.getString("app_domain", APP_DOMAIN) + API_VERSION + "/routers/default/index/routes/%s/vehicles";//ROUTE ID

    String API_ACCOUNT_RESEND_EMAIL = Configurations.getString("app_domain", APP_DOMAIN) + "api/secure/user/verification-email";//GET method
    String API_ACCOUNT_CREATE_USER = Configurations.getString("app_domain", APP_DOMAIN) + "api/secure/user";//POST METHOD with default my preferences

    String API_ACCOUNT_FROM_TOKEN = Configurations.getString("app_domain", APP_DOMAIN) + "api/secure/user/fromtoken";
    String API_ACCOUNT_USER_SAVE_PREFERENCES = Configurations.getString("app_domain", APP_DOMAIN) + "api/secure/user/%s";//FromToken ID //PUT method to save.
    String API_ACCOUNT_USER_DELETE_MY_ACCOUNT = Configurations.getString("app_domain", APP_DOMAIN) + "api/secure/user/%s";//FromToken ID //DELETE method to delete account
    String API_ACCOUNT_USER_VERIFY_SMS_SEND_CODE = Configurations.getString("app_domain", APP_DOMAIN) + "api/secure/user/%s/verify_sms/%s";//FromToken ID + encoded phone number//GET method
    String API_ACCOUNT_USER_VERIFY_SMS_VERIFY_CODE = Configurations.getString("app_domain", APP_DOMAIN) + "api/secure/user/%s/verify_sms/%s";//FromToken ID + CODE//POST method

    String API_ACCOUNT_MONITORED_TRIP = Configurations.getString("app_domain", APP_DOMAIN) + "api/secure/monitoredtrip?offset=0"; //GET method

    String API_ACCOUNT_SAVE_NEW_TRIP = Configurations.getString("app_domain", APP_DOMAIN) + "api/secure/monitoredtrip"; //POST method
    String API_ACCOUNT_UPDATE_SAVED_TRIP = Configurations.getString("app_domain", APP_DOMAIN) + "api/secure/monitoredtrip/%s"; //Saved trip id //PUT method for save
    String API_ACCOUNT_DELETE_TRIP = Configurations.getString("app_domain", APP_DOMAIN) + "api/secure/monitoredtrip/%s"; //Saved trip id //DELETE method to delete trip
    String API_ACCOUNT_CHECK_TRIP = Configurations.getString("app_domain", APP_DOMAIN) + "api/secure/monitoredtrip/checkitinerary"; //POST method
    String API_ACCOUNT_DEPENDANT_MOBILITY_PROFILES = Configurations.getString("app_domain", APP_DOMAIN) + "api/secure/user/getdependentmobilityprofile"; //GET

    String END_TRACKING = Configurations.getString("app_domain", APP_DOMAIN) + "api/secure/monitoredtrip/endtracking"; //POST method

    String TRACK = Configurations.getString("app_domain", APP_DOMAIN) + "api/secure/monitoredtrip/track";
    String REROUTE = Configurations.getString("app_domain", APP_DOMAIN) + "api/secure/monitoredtrip/reroute";
    String API_TRIP_SURVEY_OPEN = Configurations.getString("app_domain", APP_DOMAIN) + "api/trip-survey/open";
    //POST method
    int ID_PARK_RIDE = 1;
    int ID_TRANSIT_STOP = 2;
    int ID_VEHICLE_ON_ROUTE = 3;
    int ID_STOP_ON_ROUTE_DIRECTION = 5;
    int ID_RENTAL_VEHICLE = 6;

    int ID_E_SCOOTER = 1;
    int ID_SHARED_BIKE = 2;

    String ROUTE_AGENCY_LOGO_BASE_URL = "https://ta-511.s3.amazonaws.com/otp/agencies_logos/";

    String URL_REGISTER_DEVICE = "/device/register";
}
