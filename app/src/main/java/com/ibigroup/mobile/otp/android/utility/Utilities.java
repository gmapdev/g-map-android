package com.ibigroup.mobile.otp.android.utility;

import static java.lang.Math.PI;
import static java.lang.Math.atan;
import static java.lang.Math.exp;
import static java.lang.Math.log;
import static java.lang.Math.sin;
import static java.lang.Math.tan;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityManager;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import com.auth0.android.result.Credentials;
import com.auth0.android.result.UserProfile;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.maps.android.SphericalUtil;
import com.ibigroup.mobile.otp.android.MyApplication;
import com.ibigroup.mobile.otp.android.R;
import com.ibigroup.mobile.otp.android.json.AddedLocation;
import com.ibigroup.mobile.otp.android.json.DependentMobilityProfile;
import com.ibigroup.mobile.otp.android.json.GPSSubmissionStatus;
import com.ibigroup.mobile.otp.android.json.LocationPoint;
import com.ibigroup.mobile.otp.android.json.OTPCredentials;
import com.ibigroup.mobile.otp.android.json.RouteViewerRoute;
import com.ibigroup.mobile.otp.android.json.StopViewerPattern;
import com.ibigroup.mobile.otp.android.json.TransitStop;
import com.ibigroup.mobile.otp.android.json.UserPreferences;
import com.ibigroup.mobile.otp.android.json.itinerary.Itinerary;
import com.ibigroup.mobile.otp.android.json.itinerary.ItineraryLeg;
import com.ibigroup.mobile.otp.android.json.itinerary.ItineraryLegPoint;
import com.ibigroup.mobile.otp.android.setup.OTPContext;
import com.mapbox.geojson.Point;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utilities {
    public static final Map<String, Integer> stringMapping = new HashMap<>() {
        {
            put("Start Over", R.string.start_over);
            put("Leave Feedback", R.string.leave_feedback);
            put("Sound Transit Service Alerts", R.string.service_alerts);
            put("Accessibility in Sound Transit", R.string.accessibility);
            put("Sound Transit Safety and Security", R.string.safety_and_security);
            put("How to Pay", R.string.how_to_pay);
            put("Stations", R.string.stations);
            put("Help", R.string.help);
        }
    };
    public static final Map<String, Integer> directionIconMapping = new HashMap<String, Integer>() {
        {
            put("CONTINUE", R.drawable.ic_straight);
            put("DEPART", R.drawable.ic_straight);
            put("RIGHT", R.drawable.ic_right);
            put("LEFT", R.drawable.ic_left);
            put("HARD_RIGHT", R.drawable.ic_hard_right);
            put("HARD_LEFT", R.drawable.ic_hard_left);
            put("UTURN_RIGHT", R.drawable.ic_uturn_right);
            put("UTURN_LEFT", R.drawable.ic_uturn_left);
            put("SLIGHT_RIGHT", R.drawable.ic_slightright);
            put("SLIGHT_LEFT", R.drawable.ic_slightleft);
            put("SLIGHTLY_RIGHT", R.drawable.ic_slightright);
            put("SLIGHTLY_LEFT", R.drawable.ic_slightleft);
        }
    };

    public static final Map<String, Integer> indoorDirectionIconMapping = new HashMap<String, Integer>() {
        {
            put("continue", R.drawable.ic_indoor_straight);
            put("right", R.drawable.ic_indoor_rightturn);
            put("left", R.drawable.ic_indoor_leftturn);
            put("straight", R.drawable.ic_indoor_straight);
        }
    };

    public static final Map<String, Integer> agencyLogoIconMapping = new HashMap<String, Integer>() {
        {
            put("smart", R.drawable.smart);
            put("c-tran", R.drawable.c_tran);
            put("trimet", R.drawable.trimet);
            put("portland_streetcar", R.drawable.portland_streetcar);
            put("portland_aerial_tram", R.drawable.portland_aerial_tram);
            put("xpress", R.drawable.xpress);
            put("ride_gwinnett", R.drawable.ride_gwinnett);
            put("gwinnett_county_transit", R.drawable.ride_gwinnett);
            put("cobblinc", R.drawable.cobblinc);
            put("connect_douglas", R.drawable.connect_douglas);

        }
    };

    public static final Map<String, Integer> modesStringMapping = new HashMap<String, Integer>() {
        {
            put("bicycle", R.string.bike);
            put("car", R.string.drive);
            put("micromobility", R.string.e_scooter);
            put("tram", R.string.light_rail);
            put("rail", R.string.route_filter_mode_rail);
            put("ferry", R.string.ferry);
            put("gondola", R.string.gondola);
            put("walk", R.string.walk);
            put("bikeshare", R.string.bikeshare);
            put("link", R.string.link);
            put("streetcar", R.string.street_car);
            put("water_taxi", R.string.water_taxi);
            put("scooter", R.string.e_scooter);
        }
    };

    public static final Map<String, Integer> routeFilterModesStringMapping = new HashMap<String, Integer>() {
        {
            put("tram", R.string.street_car);
            put("rail", R.string.route_filter_mode_rail);
            put("ferry", R.string.ferry);
            put("bus", R.string.bus);
            put("monorail", R.string.monorail);
            put("gondola", R.string.gondola);
            put("link", R.string.link);
            put("streetcar", R.string.street_car);
            put("water_taxi", R.string.water_taxi);
        }
    };

    public static final Map<String, Integer> directionStringMapping = new HashMap<String, Integer>() {
        {
            put("CONTINUE", R.string.cap_continue);
            put("DEPART", R.string.head);
            put("RIGHT", R.string.right);
            put("LEFT", R.string.left);
            put("HARD_RIGHT", R.string.hard_right);
            put("HARD_LEFT", R.string.hard_left);
            put("UTURN_RIGHT", R.string.uturn_right);
            put("UTURN_LEFT", R.string.uturn_left);
            put("SLIGHT_RIGHT", R.string.slight_right);
            put("SLIGHT_LEFT", R.string.slight_left);
            put("SLIGHTLY_RIGHT", R.string.slightly_right);
            put("SLIGHTLY_LEFT", R.string.slightly_left);
        }
    };

    public static void setFont(Context context, TextView textView, boolean bold) {
        if (textView != null) {
            if (bold) {
                textView.setTypeface(Typeface.createFromAsset(context.getAssets(), Constants.font_asset_path), Typeface.BOLD);
            } else {
                textView.setTypeface(Typeface.createFromAsset(context.getAssets(), Constants.font_asset_path));
            }
        }
    }

    public static void setTotalHeightToListView(ListView listView) {
        ListAdapter mAdapter = listView.getAdapter();
        int totalHeight = 0;

        for (int i = 0; i < mAdapter.getCount(); i++) {
            View mView = mAdapter.getView(i, null, listView);
            mView.measure(
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
            totalHeight += mView.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        if (mAdapter.getCount() > 0) {
            params.height = totalHeight + (listView.getDividerHeight() * (mAdapter.getCount() - 1));
        } else {
            params.height = 0;
        }
        Logger.i("utility", "params.height:" + params.height);
        listView.setLayoutParams(params);
    }

    public static void setTotalHeightToListView(ListView listView, int maxHeight) {
        ListAdapter mAdapter = listView.getAdapter();
        int totalHeight = 0;

        for (int i = 0; i < mAdapter.getCount(); i++) {
            View mView = mAdapter.getView(i, null, listView);
            mView.measure(
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
            totalHeight += mView.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        if (mAdapter.getCount() > 0) {
            if ((totalHeight + (listView.getDividerHeight() * (mAdapter.getCount() - 1))) > maxHeight) {
                params.height = maxHeight;
            } else {
                params.height = totalHeight + (listView.getDividerHeight() * (mAdapter.getCount() - 1));
            }
        } else {
            params.height = 0;
        }
        Logger.i("utility", "params.height:" + params.height);
        listView.setLayoutParams(params);
    }

    public static void setTotalHeightToListViewExt(int screenWidth, ListView listView) {
        ListAdapter mAdapter = listView.getAdapter();
        int totalHeight = 0;

        for (int i = 0; i < mAdapter.getCount(); i++) {
            View mView = mAdapter.getView(i, null, listView);
            mView.measure(
                    View.MeasureSpec.makeMeasureSpec(screenWidth, View.MeasureSpec.EXACTLY),
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
            totalHeight += mView.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        if (mAdapter.getCount() > 0) {
            params.height = totalHeight + (listView.getDividerHeight() * (mAdapter.getCount() - 1));
        } else {
            params.height = 0;
        }
        Logger.i("utility", "params.height:" + params.height);
        listView.setLayoutParams(params);
    }

    public static List<Point> decodeGPSPointsForRoute(final String encodedPath) {
        int len = encodedPath.length();

        // For speed we preallocate to an upper bound on the final length, then
        // truncate the array before returning.
        final List<Point> path = new ArrayList<Point>();
        int index = 0;
        long lat = 0;
        long lng = 0;

        while (index < len) {
            long result = 1;
            long shift = 0;
            long b;
            do {
                b = encodedPath.charAt(index++) - 63 - 1;
                result += b << shift;
                shift += 5;
            } while (b >= 0x1f);
            lat += (result & 1) != 0 ? ~(result >> 1) : (result >> 1);

            result = 1;
            shift = 0;
            do {
                b = encodedPath.charAt(index++) - 63 - 1;
                result += b << shift;
                shift += 5;
            } while (b >= 0x1f);
            lng += (result & 1) != 0 ? ~(result >> 1) : (result >> 1);

            Point point = Point.fromLngLat(lng * 1e-5, lat * 1e-5);

            path.add(point);
        }

        return path;
    }

    public static String loadDataFromAsset(Context context, String fileName) {
        String json = null;
        if (fileName != null) {
            try {
                InputStream is = context.getAssets().open(fileName);
                int size = is.available();
                byte[] buffer = new byte[size];
                is.read(buffer);
                is.close();
                json = new String(buffer, StandardCharsets.UTF_8);
            } catch (IOException ex) {
                ex.printStackTrace();
                return null;
            }
        }
        return json;
    }

    public static String loadDataFromAssetExt(Context context, String fileName) {
        String json = null;
        if (fileName != null) {
            try {
                InputStream is = context.getAssets().open(fileName);
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                StringBuilder builder = new StringBuilder();
                String line;
                boolean firstLine = true;
                while ((line = reader.readLine()) != null) {
                    if(!firstLine) {
                        builder.append("\n");
                    } else {
                        firstLine = false;
                    }
                    builder.append(line);
                }
                reader.close();
                json = builder.toString();
            } catch (IOException ex) {
                ex.printStackTrace();
                return null;
            }
        }
        return json;
    }

    static public void GotoTheUrl(Context mContext, String mWebSite) {
        if (mWebSite != null) {
            Intent mIntent = null;

            mIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(mWebSite));
            mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            if (mIntent.resolveActivity(mContext.getPackageManager()) != null) {
                mContext.startActivity(mIntent);
            }
        }
    }

    public static long startOfDay(long timestampInMillis) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(timestampInMillis);
        cal.set(Calendar.HOUR_OF_DAY, 0); //set hours to zero
        cal.set(Calendar.MINUTE, 0); // set minutes to zero
        cal.set(Calendar.SECOND, 0); //set seconds to zero
        Log.i("Time", cal.getTime().toString());
        return cal.getTimeInMillis() / 1000;
    }

    public static LatLng extrapolate(List<LatLng> path, LatLng origin, double distance) {
        LatLng extrapolated = null;

        float accDistance = 0f;
        boolean foundStart = false;

        for (int i = 0; i < path.size() - 1; i++) {
            LatLng segmentStart = path.get(i);
            LatLng segmentEnd = path.get(i + 1);

            double currentDistance = 0d;

            if (!foundStart) {
                foundStart = true;

                currentDistance = SphericalUtil.computeDistanceBetween(origin, segmentEnd);

                if (currentDistance > distance) {
                    double heading = SphericalUtil.computeHeading(origin, segmentEnd);
                    extrapolated = SphericalUtil.computeOffset(origin, distance - accDistance, heading);
                    break;
                }
            } else {
                currentDistance = SphericalUtil.computeDistanceBetween(segmentStart, segmentEnd);

                if (currentDistance + accDistance > distance) {
                    double heading = SphericalUtil.computeHeading(segmentStart, segmentEnd);
                    extrapolated = SphericalUtil.computeOffset(segmentStart, distance - accDistance, heading);
                    break;
                }
            }

            accDistance += currentDistance;
        }

        return extrapolated;
    }

    public static String capitalizeString(String name) {
        String capitalizeString = "";
        if (name != null && !name.trim().equals("")) {
            capitalizeString = name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase(Locale.ROOT);
        }
        return capitalizeString;
    }

    public static boolean isContrastYIQBlack(String hexcolor) {
        if (hexcolor == null) {
            return false;
        }
        hexcolor = hexcolor.replace("#", "");
        int r = Integer.parseInt(hexcolor.substring(0, 2), 16);
        int g = Integer.parseInt(hexcolor.substring(2, 4), 16);
        int b = Integer.parseInt(hexcolor.substring(4, 6), 16);
        int yiq = ((r * 299) + (g * 587) + (b * 114)) / 1000;
        return (yiq >= 128);
    }


    public static AlertDialog showNormalPopupDialog(final Activity activity, @NonNull String title, @NonNull String message) {
        if (activity == null || activity.isDestroyed() || activity.isFinishing()) {
            return null;
        }
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);

        // set title
        alertDialogBuilder.setTitle(title);

        // set dialog message
        alertDialogBuilder
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton(activity.getString(R.string.ok), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        if (alertDialog != null) {
            alertDialog.show();
        }
        return alertDialog;
    }

    public static void showNormalPopupDialog(final Activity activity, @NonNull String title, @NonNull String message, DialogInterface.OnClickListener clickListener) {
        if (activity == null || activity.isDestroyed() || activity.isFinishing()) {
            return;
        }
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);

        // set title
        alertDialogBuilder.setTitle(title);

        // set dialog message
        alertDialogBuilder
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton(activity.getString(R.string.ok), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                        if (clickListener != null) {
                            clickListener.onClick(dialog, id);
                        }
                    }
                });

        alertDialogBuilder.create().show();
    }

    public static void showNormalPopupDialog(final Activity activity, @NonNull String title, @NonNull String message,
                                             DialogInterface.OnClickListener clickListener,
                                             DialogInterface.OnClickListener cancelClickListener) {
        if (activity == null || activity.isDestroyed() || activity.isFinishing()) {
            return;
        }
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);

        // set title
        alertDialogBuilder.setTitle(title);

        // set dialog message
        alertDialogBuilder
                .setMessage(message)
                .setCancelable(false)
                .setNegativeButton(activity.getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                        if (cancelClickListener != null) {
                            cancelClickListener.onClick(dialog, id);
                        }
                    }
                })
                .setPositiveButton(activity.getString(R.string.ok), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                        if (clickListener != null) {
                            clickListener.onClick(dialog, id);
                        }
                    }
                });

        alertDialogBuilder.create().show();
    }

    public static int getModeBackgroundColorByMode(Context context, String mode) {
        if (mode != null) {
            int colorId;
            switch (mode) {
                case "car":
                    colorId = R.color.colorCarMode;
                    break;
                case "bike":
                case "bicycle":
                case "bicycleRent":
                    colorId = R.color.colorBikeMode;
                    break;
                case "walk":
                    colorId = R.color.colorWalkMode;
                    break;
                case "bus":
                    colorId = R.color.colorBusMode;
                    break;
                case "subway":
                case "rail":
                case "tram":
                case "marta rail":
                    colorId = R.color.colorSubwayMode;
                    break;
                case "micromobility":
                    colorId = R.color.colorScooterBg;
                    break;
                case "transit":
                    colorId = R.color.colorTransitMode;
                    break;
                default:
                    colorId = R.color.colorPrimary;
                    break;
            }
            return context.getResources().getColor(colorId);
        }
        return Color.parseColor("#FFFFFF"); // default is black
    }

    public static int getModeImageByMode(String mode, boolean isRentalVehicle, Context context, ImageView ivMode, View rlMode) {
        if (mode != null) {
            int resId = R.drawable.ic_car;
            switch (mode) {
                case "car":
                    resId = R.drawable.ic_car;
                    break;
                case "bike":
                case "bikeshare":
                    if (context != null) {
                        if (rlMode != null) {
                            int color = Color.argb(0, 255, 255, 255);
                            rlMode.setBackgroundTintList(ColorStateList.valueOf(color));
                        }
                        if (ivMode != null) {
                            ivMode.setColorFilter(context.getResources().getColor(android.R.color.black));
                        }
                    }
                    resId = R.drawable.ic_bicycle;
                    break;
                case "bicycle":
                    resId = R.drawable.ic_bicycle;
                    break;
                case "walk":
                    resId = R.drawable.ic_walk;
                    break;
                case "bus":
                    resId = R.drawable.ic_bus;
                    break;
                case "subway":
                case "rail":
                case "marta rail":
                    resId = R.drawable.ic_rail;
                    break;
                case "tram":
                case "streetcar":
                    resId = R.drawable.ic_streetcar;
                    break;
                case "micromobility":
                    if (isRentalVehicle) {
                        resId = R.drawable.ic_spin_icon;
                    } else {
                        resId = R.drawable.ic_micromobility;
                    }
                    break;
                case "scooter":
                    resId = R.drawable.ic_scooter;
                    break;
                case "ferry":
                    resId = R.drawable.ic_mode_ferry;
                    break;
                case "gondola":
                    resId = R.drawable.ic_gondola;
                    break;
                case "monorail":
                    resId = R.drawable.ic_mode_monorail;
                    break;
                case "link":
                    resId = R.drawable.ic_mode_link;
                    break;
                case "water taxi":
                case "water_taxi":
                    resId = R.drawable.ic_mode_water_taxi;
                    break;

                default:
                    resId = R.drawable.ic_standardmode;
                    break;
            }
            return resId;
        }
        return R.drawable.ic_walk;
    }

    /**
     * Determines whether the specified Itinerary can be monitored.
     *
     * @returns true if at least one Leg of the specified Itinerary is a transit leg,
     * and none of the legs is a rental or ride hail leg (e.g. CAR_RENT, CAR_HAIL, BICYCLE_RENT, etc.).
     * (We use the corresponding fields returned by OTP to get transit legs and rental/ride hail legs.)
     */
    public static boolean itineraryCanBeMonitored(Itinerary itinerary) {
        if (itinerary == null) {
            return false;
        }

        if (Configurations.getBoolean("enable_save_trip_for_all", false)) {
            return true;
        } else {
            boolean hasTransit = false;
            boolean hasRentalOrRideHail = false;

            if (itinerary != null && itinerary.getLegs() != null) {
                for (ItineraryLeg itineraryLeg : itinerary.getLegs()) {
                    if (itineraryLeg.isTransitLeg()) {
                        hasTransit = true;
                    }
                    if (itineraryLeg.isRentedBike() ||
                            itineraryLeg.isRentedCar() ||
                            itineraryLeg.isRentedVehicle() ||
                            itineraryLeg.isHailedCar()) {
                        hasRentalOrRideHail = true;
                    }
                }
            }

            return hasTransit && !hasRentalOrRideHail;
        }
    }

    public static int getRouteColorByMode(Context context, String mode) {
        if (context != null && mode != null) {
            int colorId;
            switch (mode) {
                case "car":
                    colorId = R.color.colorCarRoute;
                    break;
                case "bike":
                case "bicycle":
                    colorId = R.color.colorBikeRoute;
                    break;
                case "walk":
                    colorId = R.color.colorWalkRoute;
                    break;
                case "micromobility":
                    colorId = R.color.colorMicroMobilityRoute;
                    break;
                default:
                    colorId = R.color.colorBlack;
                    break;
            }
            return context.getResources().getColor(colorId);
        }
        return Color.parseColor("#000000"); // default is black
    }

    public static int mapMode(RouteViewerRoute routeViewerRoute) {
        if (routeViewerRoute != null && routeViewerRoute.getMode() != null) {
            String mode = routeViewerRoute.getMode();
            if (routeViewerRoute.getAgencyName().equalsIgnoreCase("Kitsap Transit") && mode.equals("WATER_TAXI")) {
                mode = "FERRY";
            }
            switch (mode.toUpperCase()) {
                case "TRAM":
                    return Constants.MODE_COMPARATOR_VALUE_TRAM;
                case "SUBWAY":
                case "MARTA RAIL":
                    return Constants.MODE_COMPARATOR_VALUE_SUBWAY;
                case "RAIL":
                    return Constants.MODE_COMPARATOR_VALUE_RAIL;
                case "BUS":
                    return Constants.MODE_COMPARATOR_VALUE_BUS;
                case "FERRY":
                    return Constants.MODE_COMPARATOR_VALUE_FERRY;
                case "CABLE_CAR":
                    return Constants.MODE_COMPARATOR_VALUE_CABLE_CAR;
                case "GONDOLA":
                    return Constants.MODE_COMPARATOR_VALUE_GONDOLA;
                case "FUNICULAR":
                    return Constants.MODE_COMPARATOR_VALUE_FUNICULAR;

                case "LINK":
                    return Constants.MODE_COMPARATOR_VALUE_LINK;
                case "STREETCAR":
                    return Constants.MODE_COMPARATOR_VALUE_STREETCAR;
                case "WATER_TAXI":
                    return Constants.MODE_COMPARATOR_VALUE_WATER_TAXI;

                default:
                    return 0;
            }
        } else {
            return 0;
        }
    }

    public static boolean isFavouriteStop(ItineraryLegPoint stopInfo) {
        if (stopInfo != null && stopInfo.getName() != null) {
            String credentialString = Configurations.getString("account_credentials", "");

            if (credentialString != null && !credentialString.isEmpty()) {
                double stopLat = stopInfo.getLat();
                double stopLong = stopInfo.getLon();

                String userPreferencesString = Configurations.getString("account_user_preferences", "");
                Gson gson = new Gson();
                if (userPreferencesString != null && !userPreferencesString.isEmpty()) {
                    try {
                        UserPreferences userPreferences = gson.fromJson(userPreferencesString, UserPreferences.class);
                        if (userPreferences.getSavedLocations() != null) {
                            for (AddedLocation addedLocation : userPreferences.getSavedLocations()) {
                                if ("stop".equalsIgnoreCase(addedLocation.getType())) {
                                    if (addedLocation.getName().equals(stopInfo.getName())
                                            && 0 == Double.compare(stopLat, addedLocation.getLat())
                                            && 0 == Double.compare(stopLong, addedLocation.getLon())) {
                                        return true;
                                    }
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return false;
    }

    public static String getStopId(TransitStop transitStop) {
        if (transitStop != null) {
            String id = transitStop.getId();
            if (id != null) {
                if (id.contains(":") && id.indexOf(":") < (id.length() - 1)) {
                    id = id.substring(id.indexOf(":") + 1);
                }
            } else {
                id = transitStop.getCode();
            }
            return id;
        }
        return null;
    }

    public static TransitStop getStopInfo(AddedLocation addedLocation) {
        if (MyApplication.getInstance().transitStops == null) {
            String transitStopString = Configurations.getString("transit_stops", "[]");
            if (transitStopString != null && !transitStopString.isEmpty() && transitStopString.startsWith("[")) {
                Gson gson = new Gson();

                MyApplication.getInstance().transitStops = gson.fromJson(transitStopString,
                        new TypeToken<ArrayList<TransitStop>>() {
                        }.getType());
            } else {
                MyApplication.getInstance().transitStops = new ArrayList<>();
            }
        }

        if (addedLocation != null
                && addedLocation.getName() != null) {
            for (TransitStop transitStop : MyApplication.getInstance().transitStops) {
                if (addedLocation.getName().equals(transitStop.getName())
                        && 0 == Double.compare(addedLocation.getLat(), transitStop.getLat())
                        && 0 == Double.compare(addedLocation.getLon(), transitStop.getLon())) {
                    return transitStop;
                }
            }
        }
        return null;
    }

    public static TransitStop getStopInfo(String stopID) {
        if (MyApplication.getInstance().transitStops == null) {
            String transitStopString = Configurations.getString("transit_stops", "[]");
            if (transitStopString != null && !transitStopString.isEmpty() && transitStopString.startsWith("[")) {
                Gson gson = new Gson();

                MyApplication.getInstance().transitStops = gson.fromJson(transitStopString,
                        new TypeToken<ArrayList<TransitStop>>() {
                        }.getType());
            } else {
                MyApplication.getInstance().transitStops = new ArrayList<>();
            }
        }

        if (stopID != null) {
            for (TransitStop transitStop : MyApplication.getInstance().transitStops) {
                if (stopID.equals(transitStop.getId())) {
                    return transitStop;
                }
            }
        }
        return null;
    }

    public static String formatTimestamp(Context context, long timestamp) {
        DateFormat dateFormat = android.text.format.DateFormat.getTimeFormat(context);
        Date dt = new Date();
        dt.setTime(timestamp);
        dateFormat.setTimeZone(TimeZone.getTimeZone(Configurations.TARGET_TIMEZONE_ID));
        return dateFormat.format(dt);
    }

    public static String formatTimestampPDT(Context context, long timestamp) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("h:mm a", Locale.US);
        Date dt = new Date();
        dt.setTime(timestamp);
        dateFormat.setTimeZone(TimeZone.getTimeZone(Configurations.TARGET_TIMEZONE_ID));
        return dateFormat.format(dt);
    }

    public static String formatTimestampForSelectedDate(long timestamp) {
        SimpleDateFormat simpleDateFormatForDate = new SimpleDateFormat("EEEE, MM/dd/yyyy", Locale.getDefault());
        Date dt = new Date();
        dt.setTime(timestamp);
        simpleDateFormatForDate.setTimeZone(TimeZone.getTimeZone(Configurations.TARGET_TIMEZONE_ID));
        return simpleDateFormatForDate.format(dt);
    }

    public static String formatTimestampForSelectedDateInList(long timestamp) {
        SimpleDateFormat simpleDateFormatForDate = new SimpleDateFormat("EEEE, MMMM d, yyyy", Locale.getDefault());
        Date dt = new Date();
        dt.setTime(timestamp);
        simpleDateFormatForDate.setTimeZone(TimeZone.getTimeZone(Configurations.TARGET_TIMEZONE_ID));
        return simpleDateFormatForDate.format(dt);
    }

    public static OTPCredentials regenerateCredentials(Credentials credentials) {
        OTPCredentials otpCredentials = new OTPCredentials();
        if (credentials != null) {
            otpCredentials.setAccessToken(credentials.getAccessToken());
            otpCredentials.setIdToken(credentials.getIdToken());
            otpCredentials.setScope(credentials.getScope());
            otpCredentials.setType(credentials.getType());
            otpCredentials.setRefreshToken(credentials.getRefreshToken());
            otpCredentials.setExpiresAt(credentials.getExpiresAt().getTime());
        }
        return otpCredentials;
    }

    public static boolean isEmailVerified() {
        String profileString = Configurations.getString("account_user_profile", "");
        Gson gson = new Gson();
        if (profileString != null && !profileString.isEmpty()) {
            try {
                UserProfile userProfile = gson.fromJson(profileString, UserProfile.class);
                if (userProfile != null) {
                    return Boolean.TRUE.equals(userProfile.isEmailVerified());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return false;
    }

    public static boolean isLoggedIn() {
        boolean loggedIn = false;
        String credentialString = Configurations.getString("account_credentials", "");
        Gson gson = new Gson();
        if (credentialString != null && !credentialString.isEmpty()) {
            try {
                OTPCredentials otpCredentials = gson.fromJson(credentialString, OTPCredentials.class);

                if (otpCredentials.getExpiresAt() > System.currentTimeMillis()) {
                    loggedIn = true;
                }
            } catch (Exception e) {
                try {
                    Credentials credentials = gson.fromJson(credentialString, Credentials.class);
                    Date date = credentials.getExpiresAt();
                    if (date.getTime() > System.currentTimeMillis()) {
                        loggedIn = true;
                    }
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }
        }
        return loggedIn;
    }

    public static ItineraryLeg getSecondMode(ArrayList<ItineraryLeg> legs) {
        ItineraryLeg secondItineraryLeg = null;
        if (legs != null && legs.size() > 1) {
            boolean found = false;

            for (int i = 1; i < legs.size(); i++) {
                secondItineraryLeg = legs.get(i);

                if (secondItineraryLeg != null) {
                    String secondMode = secondItineraryLeg.getMode().toLowerCase();
                    if (secondMode.equals("car") || secondMode.equals("bus") || secondMode.equals("subway")
                            || secondMode.equals("rail") || secondMode.equals("ferry") || secondMode.equals("tram")) {
                        found = true;
                        break;
                    }
                }
            }

            if (!found) {
                secondItineraryLeg = legs.get(1);
            }
        }
        return secondItineraryLeg;
    }

    public static String getVersion(Context context) {
        if (context != null) {
            PackageInfo pInfo = null;

            try {
//            cfg -> C  cfg-dev ->D
//            production-> P   staging-> S
                StringBuilder sbVersion = new StringBuilder();
                sbVersion.append(context.getString(R.string.app_name));
                sbVersion.append(" ");

                String brandInfo = Configurations.getString("brand_info", "");
                if (!brandInfo.isEmpty()) {
                    JSONObject brandInfoObject = new JSONObject(brandInfo);
                    String environment = brandInfoObject.optString("environment", "");
                    if (!environment.isEmpty() && environment.equalsIgnoreCase("production")) {
                        sbVersion.append("P");
                    } else {
                        sbVersion.append("S");
                    }
                } else {
                    sbVersion.append("S");
                }
                pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);

                sbVersion.append(pInfo.versionName);

                sbVersion.append("(");
                sbVersion.append(pInfo.versionCode);
                sbVersion.append(")");

                return sbVersion.toString();
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return "";
        }
        return "";
    }

    public static void resetItinerarySearchSettings() {
        if (Configurations.getConfigurations() != null) {
            Configurations.setBoolean("home_setting_bus_enabled", true);
            Configurations.setBoolean("home_setting_rail_enabled", true);
            Configurations.setBoolean("home_setting_street_car_enabled", true);
            Configurations.setBoolean("home_setting_ferry_enabled", true);
            Configurations.setBoolean("home_setting_gondola_enabled", true);
            Configurations.setBoolean("home_setting_link_enabled", true);
            Configurations.setBoolean("home_setting_water_taxi_enabled", true);
            Configurations.setBoolean("home_setting_monorail_enabled", true);

            Configurations.setInt("maximum_walk_index", Constants.DEFAULT_MAXIMUM_WALK_INDEX);
            Configurations.setInt("walk_speed_index", Constants.DEFAULT_WALK_SPEED_INDEX);
            Configurations.setInt("optimize_option_index", Constants.DEFAULT_OPTIMIZE_OPTION_INDEX);
        }
    }

    public static void resetItinerarySearchModeSettings() {
        if (Configurations.getConfigurations() != null) {
            Configurations.setBoolean("enabled_search_mode_drive", false);
            Configurations.setBoolean("enabled_search_mode_bike", false);
            Configurations.setBoolean("enabled_search_mode_transit", true);
            Configurations.setBoolean("enabled_search_mode_walk", false);
            Configurations.setBoolean("enabled_search_mode_rental", false);
        }
    }

    public static String getMappedAgencyName(Context context, String agencyName) {
        return agencyName;
    }

    public static String getMappedAgencyNameForItinerary(Context context, String agencyName) {
        try {
            String agencyNameMappingData = loadDataFromAsset(context, "json/route_mode_mapping.json");
            if (agencyNameMappingData != null && agencyName != null) {
                JSONObject jsonObject = new JSONObject(agencyNameMappingData);
                String routeModeAgencyMappingString = jsonObject.optString("route_mode_agencies_mapping", "");
                if (!routeModeAgencyMappingString.isEmpty()) {
                    String[] routeModeAgencyMappingStringArray = routeModeAgencyMappingString.split(";");

                    for (String routeModeAgencyMapping : routeModeAgencyMappingStringArray) {
                        String[] routeModeAgencyMappings = routeModeAgencyMapping.split(",");
                        if (routeModeAgencyMappings.length == 2 && agencyName.equals(routeModeAgencyMappings[0])) {
                            return routeModeAgencyMappings[1];
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return agencyName;
    }

    public static String getMappedModeForItinerary(Context context, String routeID, String mode,
                                                   String shortName, String longName) {
        try {
            String routeModeMappingData = Utilities.loadDataFromAsset(context, "json/route_mode_mapping.json");
            if (routeModeMappingData != null) {
                JSONObject jsonObject = new JSONObject(routeModeMappingData);
                String routeModeOverridesString = jsonObject.optString("route_mode_overrides", "");
                if (!routeModeOverridesString.isEmpty()) {
                    String[] routeModeOverrideStringArray = routeModeOverridesString.split(";");

                    if (routeID != null) {
                        for (String routeModeOverrideString : routeModeOverrideStringArray) {
                            String[] routeModeMapping = routeModeOverrideString.split(",");
                            if (routeModeMapping.length == 2 && routeID.equals(routeModeMapping[0])) {
                                return routeModeMapping[1];
                            }
                        }
                    }
                }
            }

            if (MyApplication.getInstance().routeViewerRoutes != null) {
                if (shortName != null) {
                    for (RouteViewerRoute routeViewerRoute : MyApplication.getInstance().routeViewerRoutes) {
                        if (routeViewerRoute != null && shortName.equals(routeViewerRoute.getShortName())) {
                            return routeViewerRoute.getMode();
                        }
                    }
                }
                if (longName != null) {
                    for (RouteViewerRoute routeViewerRoute : MyApplication.getInstance().routeViewerRoutes) {
                        if (routeViewerRoute != null && longName.equals(routeViewerRoute.getLongName())) {
                            return routeViewerRoute.getMode();
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return mode;
    }

    public static String getHashedDeviceID(Context context) {
        String hashedDeviceID = Settings.Secure.getString(context.getContentResolver(),
                Settings.Secure.ANDROID_ID);
        if (Build.MODEL.equals("google_sdk")) {
            return "simulator-" + hashedDeviceID + Configurations.getString("device_id", "");
        }
        return hashedDeviceID + Configurations.getString("device_id", "");
    }

    public static String getAppVersion(Context applicationContext) {
        PackageManager pm = applicationContext.getPackageManager();
        String appName;
        String appVersion;
        try {
            PackageInfo pInfo = pm.getPackageInfo(applicationContext.getPackageName(), 0);
            appName = pInfo.applicationInfo.loadLabel(pm).toString();
            appVersion = pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            appName = "NA";
            appVersion = "NA";
        }
        return appName + "|" + appVersion;
    }

    public static String getCurrentLanguageCode() {
        if (Configurations.getBoolean("enable_feature_language_setting", false)) {
            String langSettingCode = Configurations.getString("language_setting", "");
            if (!langSettingCode.isEmpty()) {
                return langSettingCode;
            }
        }
        String supportLanguage = Configurations.getString("support_language", "en");
        String defaultLanguage = Configurations.getString("default_language", "en");

        Locale locale = Locale.getDefault();

        if (supportLanguage.contains(locale.getLanguage())) {
            return locale.getLanguage();
        } else {
            return defaultLanguage;
        }
    }

    public static boolean isCurrentLanguageDefault() {
        String defaultLanguage = Configurations.getString("default_language", "en");
        String currentLanguage = Utilities.getCurrentLanguageCode();
        return currentLanguage != null && currentLanguage.equalsIgnoreCase(defaultLanguage);
    }

    public static String getUrlWithLanguageFor(String url) {
        if (Configurations.getBoolean("enable_feature_support_multilanguage", false)) {
            if (url != null && !url.isEmpty() && !url.contains("?lang=") && !url.contains("&lang=")) {
                String currentLanguageCode = getCurrentLanguageCode();

                if (url.contains("?")) {

                    url = url + "&lang=" + currentLanguageCode;
                } else {

                    url = url + "?lang=" + currentLanguageCode;

                }
            }
        }
        return url;
    }

    public static boolean checkIfToDisplayAccessibilityUI(Context context) {
        float fontScale = context.getResources().getConfiguration().fontScale;
        float scaledDensity = context.getResources().getDisplayMetrics().scaledDensity;

        return fontScale >= 1.3 || scaledDensity >= 3.3;

    }

    public static boolean isDarkMode() {
        int nightModeFlags = OTPContext.getAppContext().getResources().getConfiguration().uiMode
                & Configuration.UI_MODE_NIGHT_MASK;

        return nightModeFlags == Configuration.UI_MODE_NIGHT_YES;
    }

    public static RouteViewerRoute getRouteViewerRouteByID(String routeID) {
        if (routeID != null && MyApplication.getInstance().routeViewerRoutes != null) {
            for (RouteViewerRoute routeViewerRoute : MyApplication.getInstance().routeViewerRoutes) {
                if (routeViewerRoute != null && routeID.equalsIgnoreCase(routeViewerRoute.getId())) {
                    return routeViewerRoute;
                }
            }
        }

        return null;
    }

    /**
     * Checking for all possible internet providers
     **/
    public static boolean hasInternetConnectivity() {
        boolean ret = false;
        ConnectivityManager connectivity = (ConnectivityManager) MyApplication.getInstance().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo activeNetworkInfo = connectivity.getActiveNetworkInfo();

            if (activeNetworkInfo != null) { // connected to the internet
                //Toast.makeText(TAContext.getAppContext(), activeNetworkInfo.getTypeName(), Toast.LENGTH_SHORT).show();

                if (activeNetworkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                    // connected to wifi
                    ret = true;
                } else if (activeNetworkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                    // connected to the mobile provider's data plan
                    ret = true;
                }
            }

            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null) {
                for (int i = 0; i < info.length; i++)
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
            }
        }
        return ret;
    }

    public static String extractHeadsignFromPattern(StopViewerPattern pattern, String routeShortName) {
        String headSign = null;

        if (pattern != null) {
            if (pattern.getHeadsign() != null &&
                    !pattern.getHeadsign().isEmpty()) {
                headSign = pattern.getHeadsign();
            } else if (pattern.getDesc() != null &&
                    !pattern.getDesc().isEmpty()) {
                String description = pattern.getDesc();
                headSign = extractHeadsignFromPatternDes(description, routeShortName);
                if (headSign == null || headSign.isEmpty()) {
                    headSign = description;
                }
            }
        }
        return headSign;
    }

    public static String extractHeadsignFromPatternDes(String desc, String routeShortName) {
        String headSign = null;
        if (desc != null) {
            Pattern pattern = Pattern.compile("(?: to )(.*?)(?: \\()");
            Matcher matcher = pattern.matcher(desc);

            if (matcher.find() && matcher.groupCount() >= 1) {
                headSign = matcher.group(1);
            }

            if (headSign == null || headSign.isEmpty()) {
                pattern = Pattern.compile(" to ([^(from)]+) \\(.+\\)");
                matcher = pattern.matcher(desc);
                if (matcher.find() && matcher.groupCount() >= 1) {
                    headSign = matcher.group(1);
                }
            }

            if (headSign == null || headSign.isEmpty()) {
                if (routeShortName != null) {
                    headSign = desc.replace(routeShortName, "").trim();
                } else {
                    if (desc.contains("(")) {
                        headSign = desc.substring(0, desc.indexOf("(")).trim();
                    } else {
                        headSign = desc;
                    }
                }
            }
        }

        return headSign;
    }

    public static double getDistance(LocationPoint locationA,
                                     LocationPoint locationB) {
        if (locationA != null && locationB != null) {
            double earthRadius = 3958.75;
            double latDiff = Math.toRadians(locationB.getLat() - locationA.getLat());
            double lngDiff = Math.toRadians(locationB.getLon() - locationA.getLon());
            double a = Math.sin(latDiff / 2) * Math.sin(latDiff / 2) +
                    Math.cos(Math.toRadians(locationA.getLat())) * Math.cos(Math.toRadians(locationB.getLat())) *
                            Math.sin(lngDiff / 2) * Math.sin(lngDiff / 2);
            double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
            double distance = earthRadius * c;

            int meterConversion = 1609;

            return distance * meterConversion;
        } else {
            return 0;
        }
    }

    public static double getDistance(double locationALat, double locationALon,
                                     LocationPoint locationB) {
        if (locationB != null) {
            double earthRadius = 3958.75;
            double latDiff = Math.toRadians(locationB.getLat() - locationALat);
            double lngDiff = Math.toRadians(locationB.getLon() - locationALon);
            double a = Math.sin(latDiff / 2) * Math.sin(latDiff / 2) +
                    Math.cos(Math.toRadians(locationALat)) * Math.cos(Math.toRadians(locationB.getLat())) *
                            Math.sin(lngDiff / 2) * Math.sin(lngDiff / 2);
            double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
            double distance = earthRadius * c;

            int meterConversion = 1609;

            return distance * meterConversion;
        } else {
            return 0;
        }
    }

    public static double getDistance(double locationALat, double locationALon,
                                     double locationBLat, double locationBLon) {
        double earthRadius = 3958.75;
        double latDiff = Math.toRadians(locationBLat - locationALat);
        double lngDiff = Math.toRadians(locationBLon - locationALon);
        double a = Math.sin(latDiff / 2) * Math.sin(latDiff / 2) +
                Math.cos(Math.toRadians(locationALat)) * Math.cos(Math.toRadians(locationBLat)) *
                        Math.sin(lngDiff / 2) * Math.sin(lngDiff / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = earthRadius * c;

        int meterConversion = 1609;

        return distance * meterConversion;
    }

    public static String getCurrentMobilityProfile() {
        if (isLoggedIn()) {
            String accountUserPreferences = Configurations.getString("account_user_preferences", "");
            String selectedDependantEmail = Configurations.getString("selected_dependant_email", "");
            if (accountUserPreferences != null && !accountUserPreferences.isEmpty()) {
                Gson gson = new Gson();
                try {
                    MyApplication.getInstance().userPreferences = gson.fromJson(accountUserPreferences, UserPreferences.class);
                    if (MyApplication.getInstance().userPreferences != null) {
                        if (selectedDependantEmail != null && !selectedDependantEmail.isEmpty()
                                && MyApplication.getInstance().dependentMobilityProfiles != null
                                && MyApplication.getInstance().dependentMobilityProfiles.size() > 0) {
                            for (DependentMobilityProfile dependentMobilityProfile : MyApplication.getInstance().dependentMobilityProfiles) {
                                if (dependentMobilityProfile != null && selectedDependantEmail.equalsIgnoreCase(dependentMobilityProfile.getEmail())) {
                                    return dependentMobilityProfile.getMobilityMode();
                                }
                            }
                        }

                        if (MyApplication.getInstance().userPreferences.getMobilityProfile() != null &&
                                MyApplication.getInstance().userPreferences.getMobilityProfile().getMobilityMode() != null) {
                            return MyApplication.getInstance().userPreferences.getMobilityProfile().getMobilityMode();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return "";
    }

    public static double toRadians(double degrees) {
        return degrees * PI / 180.0;
    }

    /**
     * Returns the non-negative remainder of x / m.
     *
     * @param x The operand.
     * @param m The modulus.
     */
    static double mod(double x, double m) {
        return ((x % m) + m) % m;
    }

    /**
     * Returns mercator Y corresponding to latitude.
     * See http://en.wikipedia.org/wiki/Mercator_projection .
     */
    static double mercator(double lat) {
        return log(tan(lat * 0.5 + PI / 4));
    }

    /**
     * Returns latitude from mercator Y.
     */
    static double inverseMercator(double y) {
        return 2 * atan(exp(y)) - PI / 2;
    }

    /**
     * Wraps the given value into the inclusive-exclusive interval between min and max.
     *
     * @param n   The value to wrap.
     * @param min The minimum.
     * @param max The maximum.
     */
    static double wrap(double n, double min, double max) {
        return (n >= min && n < max) ? n : (mod(n - min, max - min) + min);
    }

    /**
     * Returns tan(latitude-at-lng3) on the great circle (lat1, lng1) to (lat2, lng2). lng1==0.
     * See http://williams.best.vwh.net/avform.htm .
     */
    private static double tanLatGC(double lat1, double lat2, double lng2, double lng3) {
        return (tan(lat1) * sin(lng2 - lng3) + tan(lat2) * sin(lng3)) / sin(lng2);
    }

    /**
     * Returns mercator(latitude-at-lng3) on the Rhumb line (lat1, lng1) to (lat2, lng2). lng1==0.
     */
    private static double mercatorLatRhumb(double lat1, double lat2, double lng2, double lng3) {
        return (mercator(lat1) * (lng2 - lng3) + mercator(lat2) * lng3) / lng2;
    }

    /**
     * Computes whether the vertical segment (lat3, lng3) to South Pole intersects the segment
     * (lat1, lng1) to (lat2, lng2).
     * Longitudes are offset by -lng1; the implicit lng1 becomes 0.
     */
    private static boolean intersects(double lat1, double lat2, double lng2,
                                      double lat3, double lng3, boolean geodesic) {
        // Both ends on the same side of lng3.
        if ((lng3 >= 0 && lng3 >= lng2) || (lng3 < 0 && lng3 < lng2)) {
            return false;
        }
        // Point is South Pole.
        if (lat3 <= -PI / 2) {
            return false;
        }
        // Any segment end is a pole.
        if (lat1 <= -PI / 2 || lat2 <= -PI / 2 || lat1 >= PI / 2 || lat2 >= PI / 2) {
            return false;
        }
        if (lng2 <= -PI) {
            return false;
        }
        double linearLat = (lat1 * (lng2 - lng3) + lat2 * lng3) / lng2;
        // Northern hemisphere and point under lat-lng line.
        if (lat1 >= 0 && lat2 >= 0 && lat3 < linearLat) {
            return false;
        }
        // Southern hemisphere and point above lat-lng line.
        if (lat1 <= 0 && lat2 <= 0 && lat3 >= linearLat) {
            return true;
        }
        // North Pole.
        if (lat3 >= PI / 2) {
            return true;
        }
        // Compare lat3 with latitude on the GC/Rhumb segment corresponding to lng3.
        // Compare through a strictly-increasing function (tan() or mercator()) as convenient.
        return geodesic ?
                tan(lat3) >= tanLatGC(lat1, lat2, lng2, lng3) :
                mercator(lat3) >= mercatorLatRhumb(lat1, lat2, lng2, lng3);
    }

    public static double toDegrees(double radians) {
        return radians * 180.0 / PI;
    }

    /**
     * Computes whether the given point lies inside the specified polygon.
     * The polygon is always considered closed, regardless of whether the last point equals
     * the first or not.
     * Inside is defined as not containing the South Pole -- the South Pole is always outside.
     * The polygon is formed of great circle segments if geodesic is true, and of rhumb
     * (loxodromic) segments otherwise.
     */
    public static boolean containsLocation(double latitude, double longitude, List<LatLng> polygon, boolean geodesic) {
        final int size = polygon.size();
        if (size == 0) {
            return false;
        }
        double lat3 = toRadians(latitude);
        double lng3 = toRadians(longitude);
        LatLng prev = polygon.get(size - 1);
        double lat1 = toRadians(prev.latitude);
        double lng1 = toRadians(prev.longitude);
        int nIntersect = 0;
        for (LatLng point2 : polygon) {
            double dLng3 = wrap(lng3 - lng1, -PI, PI);
            // Special case: point equal to vertex is inside.
            if (lat3 == lat1 && dLng3 == 0) {
                return true;
            }
            double lat2 = toRadians(point2.latitude);
            double lng2 = toRadians(point2.longitude);
            // Offset longitudes by -lng1.
            if (intersects(lat1, lat2, wrap(lng2 - lng1, -PI, PI), lat3, dLng3, geodesic)) {
                ++nIntersect;
            }
            lat1 = lat2;
            lng1 = lng2;
        }
        return (nIntersect & 1) != 0;
    }

    public static void setListViewTotalHeight(ListView listView, int width) {
        ListAdapter mAdapter = listView.getAdapter();
        if (mAdapter != null) {
            int totalHeight = 0;

            for (int i = 0; i < mAdapter.getCount(); i++) {
                View mView = mAdapter.getView(i, null, listView);
                mView.measure(
                        View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY),
                        View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
                totalHeight += mView.getMeasuredHeight();
            }

            ViewGroup.LayoutParams params = listView.getLayoutParams();
            params.height = totalHeight + (listView.getDividerHeight() * (mAdapter.getCount() - 1));
            Log.e("base", "params.height:" + params.height);
            listView.setLayoutParams(params);
        }
    }

    public static void addGPSSubmissionStatus(boolean submittedSuccessfully, String description) {
        GPSSubmissionStatus gpsSubmissionStatus = new GPSSubmissionStatus();
        gpsSubmissionStatus.setSubmittedSuccessfully(submittedSuccessfully);
        if (description != null) {
            gpsSubmissionStatus.setDescription(description);
        } else {
            gpsSubmissionStatus.setDescription("");
        }
        gpsSubmissionStatus.setIntervalTimestamp(System.currentTimeMillis());

        int maxTrackingEntries = Configurations.getInt("max_tracking_data_history_entries", 50);
        MyApplication.getInstance().gpsSubmissionStatusList.add(0, gpsSubmissionStatus);
        if (MyApplication.getInstance().gpsSubmissionStatusList.size() > maxTrackingEntries
                && maxTrackingEntries > 0) {
            for (int i = MyApplication.getInstance().gpsSubmissionStatusList.size() - 1;
                 i >= maxTrackingEntries; i--) {
                MyApplication.getInstance().gpsSubmissionStatusList.remove(i);
            }
        }

        Gson gson = new Gson();
        Configurations.setString("tracking_data_history_statuses",
                gson.toJson(MyApplication.getInstance().gpsSubmissionStatusList));
    }

    public static String getFormattedTimeForUploadLog(long timestampInMM) {
        Date date = new Date();
        date.setTime(timestampInMM);
        String formattedDate = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss", Locale.US).format(date);
        return formattedDate;
    }

    public static void initGPSSubmissionStatus() {
        Gson gson = new Gson();
        MyApplication.getInstance().gpsSubmissionStatusList = gson.fromJson(
                Configurations.getString("tracking_data_history_statuses", "[]"),
                new TypeToken<List<GPSSubmissionStatus>>() {
                }.getType());
    }

    public static void vibrateOneShot() {
        Vibrator v = (Vibrator) MyApplication.getInstance().getSystemService(Context.VIBRATOR_SERVICE);
        long duration = Configurations.getLong("android_vibration_duration_ms", 250);
        int amplitude = 256 * Configurations.getInt("android_vibration_intensity", 70) / 100;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createOneShot(duration, amplitude));
        } else {
            v.vibrate(duration);
        }
    }

    public static boolean doesCurrentUserNeedHaptic() {
        if (isLoggedIn()) {
            String accountUserPreferences = Configurations.getString("account_user_preferences", "");

            if (accountUserPreferences != null && !accountUserPreferences.isEmpty()) {
                Gson gson = new Gson();
                try {
                    MyApplication.getInstance().userPreferences = gson.fromJson(accountUserPreferences, UserPreferences.class);
                    if (MyApplication.getInstance().userPreferences != null) {
                        if (MyApplication.getInstance().userPreferences.getNotificationChannel() != null &&
                                MyApplication.getInstance().userPreferences.getNotificationChannel().toLowerCase().contains("haptic")) {
                            return true;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    public static boolean isFeatureEnabled(String featureID, boolean defaultValue) {
        if (featureID != null && !featureID.isEmpty()) {
            String key = "enable_feature_" + featureID;

            return Configurations.getBoolean(key, defaultValue);
        }

        return defaultValue;
    }

    private static final List<ParentItem> expandableListItems = new ArrayList<>();

    public static List<ParentItem> getExpandableListItems(FragmentActivity context) {

        if (expandableListItems.size() > 1) {
            return expandableListItems;
        } else {
            resetExpandableListItems(context);
        }

        return expandableListItems;
    }

    public static List<ParentItem> resetExpandableListItems(FragmentActivity context) {

        expandableListItems.clear();
        // Transit modes
        List<ChildItem> transitModes = new ArrayList<>();

        transitModes.add(new ChildItem(context.getString(R.string.bus), Configurations.getBoolean("home_setting_bus_enabled", true), R.drawable.ic_bus_small_round));
        transitModes.add(new ChildItem(context.getString(R.string.marta_rail), Configurations.getBoolean("home_setting_rail_enabled", true), R.drawable.ic_rail_round));
        transitModes.add(new ChildItem(context.getString(R.string.street_car), Configurations.getBoolean("home_setting_street_car_enabled", true), R.drawable.ic_streetcar_small_round));

        ParentItem transit = new ParentItem(
                context.getString(R.string.advance_transit),
                transitModes, R.drawable.ic_transit_group_round
        );
        expandableListItems.add(transit);

        // cycle modes- no children
        List<ChildItem> cycleModes = new ArrayList<>();
        ParentItem cycle = new ParentItem(context.getString(R.string.bike), cycleModes, R.drawable.ic_bike_round);
        expandableListItems.add(cycle);

        // drive modes- no children
        List<ChildItem> driveModes = new ArrayList<>();
        ParentItem drive = new ParentItem(context.getString(R.string.advance_drive), driveModes, R.drawable.ic_car_round);
        expandableListItems.add(drive);

        String selectableModes = Configurations.getString("selectable_modes",
                "TRANSIT:BUS,TRAM,FERRY,RAIL,STREETCAR,WATER_TAXI;BIKE;WALK;CAR;ST-RENT").toLowerCase(Locale.ROOT);
        if (selectableModes.contains("rent")) {
            // Rental modes- no children
            List<ChildItem> rentalModes = new ArrayList<>();
            rentalModes.add(new ChildItem(context.getString(R.string.allow_bike_rental), Configurations.getBoolean("enabled_search_mode_bike_rental", true), R.drawable.ic_bike_small_round));
            rentalModes.add(new ChildItem(context.getString(R.string.allow_scooter_rental), Configurations.getBoolean("enabled_search_mode_scooter_rental", true), R.drawable.ic_scooterrental_small_round));
            ParentItem rental = new ParentItem(context.getString(R.string.rent), rentalModes, R.drawable.ic_rental_round);
            expandableListItems.add(rental);
        }
        if (selectableModes.contains("walk")) {
            // walk modes- no children
            List<ChildItem> walkModes = new ArrayList<>();
            ParentItem walk = new ParentItem(context.getString(R.string.advance_walk), walkModes, R.drawable.ic_walk_round);
            expandableListItems.add(walk);
        }
        return expandableListItems;
    }

    public static String getNotifyTitleString() {
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

        if (userPreferences != null && userPreferences.getNotificationChannel() != null) {
            String channels = userPreferences.getNotificationChannel().toLowerCase(Locale.ROOT);
            boolean hasEmail = channels.contains("email");
            boolean hasSMS = channels.contains("sms");
            boolean hasPush = channels.contains("push");

            if (hasEmail && hasPush && hasSMS) {
                return MyApplication.getInstance().getString(R.string.notify_email_push_sms);
            } else if (hasEmail && hasPush) {
                return MyApplication.getInstance().getString(R.string.notify_email_push);
            } else if (hasPush && hasSMS) {
                return MyApplication.getInstance().getString(R.string.notify_push_sms);
            } else if (hasEmail && hasSMS) {
                return MyApplication.getInstance().getString(R.string.notify_email_sms);
            } else if (hasPush) {
                return MyApplication.getInstance().getString(R.string.notify_push);
            } else if (hasEmail) {
                return MyApplication.getInstance().getString(R.string.notify_email);
            } else if (hasSMS) {
                return MyApplication.getInstance().getString(R.string.notify_sms);
            }
        }

        return MyApplication.getInstance().getString(R.string.notify_via_email);
    }

    public static String getAgencyName(String agencyId) {
        String agencyName = null;
        if (agencyId != null) {
            ArrayList<RouteViewerRoute> routeViewerAllRoutes = MyApplication.getInstance().routeViewerRoutes;
            for (RouteViewerRoute route : routeViewerAllRoutes) {
                if (route.getAgency() != null) {
                    if (route.getAgency().getId() != null && route.getAgency().getId().toLowerCase().contains(agencyId)) {
                        if (route.getAgency().getName() != null) {
                            agencyName = route.getAgency().getName().toLowerCase().replaceAll(" ", "_");
                        }
                        break;
                    }
                }
            }
        }
        return agencyName;
    }

    public static boolean isScreenReaderOn(Context context) {
        AccessibilityManager accessibilityManager = (AccessibilityManager) context.getSystemService(Context.ACCESSIBILITY_SERVICE);
        if (accessibilityManager == null) {
            return false;
        }

        List<AccessibilityServiceInfo> enabledServices = accessibilityManager.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_SPOKEN);

        return !enabledServices.isEmpty();
    }

    public static int getAgencyOrder(RouteViewerRoute routeViewerRoute) {
        int agencyOrder = Integer.MAX_VALUE;
        if (routeViewerRoute != null && routeViewerRoute.getAgency() != null && routeViewerRoute.getAgency().getName() != null) {
            String agencyName = routeViewerRoute.getAgency().getName().toLowerCase(Locale.getDefault());

            if (agencyName.contains("ride")) {
                agencyOrder = 1;
            } else if (agencyName.contains("metropolitan atlanta") || agencyName.contains("marta")) {
                agencyOrder = 2;
            } else if (agencyName.contains("xpress")) {
                agencyOrder = 3;
            }
        }
        return agencyOrder;
    }
}
