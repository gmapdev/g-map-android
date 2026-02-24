package com.ibigroup.mobile.otp.android;

import static com.ibigroup.mobile.otp.android.utility.Configurations.GRAPHQL_SERVER_URL;
import static com.ibigroup.mobile.otp.android.utility.Constants.AUTH0_REQUEST_CODE;
import static com.ibigroup.mobile.otp.android.utility.Constants.AUTH0_SUCCESS;
import static com.ibigroup.mobile.otp.android.utility.Constants.MENU_ACTION_FUNCTION;
import static com.ibigroup.mobile.otp.android.utility.Constants.MENU_ACTION_LINK;
import static com.ibigroup.mobile.otp.android.utility.Constants.MENU_ACTION_TRIGGER_EXTERNAL_WEB;
import static com.ibigroup.mobile.otp.android.utility.Constants.MENU_ACTION_TRIGGER_INTERNAL_WEB;
import static com.ibigroup.mobile.otp.android.utility.Constants.MY_PERMISSIONS_REQUEST_FINE_LOCATION;
import static com.ibigroup.mobile.otp.android.utility.Constants.USER_NAME_TAG;
import static com.ibigroup.mobile.otp.android.utility.ServerDelegate.volleyAccountStringRequest;
import static com.ibigroup.mobile.otp.android.utility.ServerDelegate.volleyAccountStringRequestExt;
import static com.ibigroup.mobile.otp.android.utility.ServerDelegate.volleyStringRequest;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.drawerlayout.widget.DrawerLayout;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.ApolloClient;
import com.apollographql.apollo.exception.ApolloException;
import com.auth0.android.Auth0;
import com.auth0.android.authentication.AuthenticationAPIClient;
import com.auth0.android.authentication.AuthenticationException;
import com.auth0.android.callback.Callback;
import com.auth0.android.lock.Lock;
import com.auth0.android.result.UserProfile;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.ibigroup.mobile.otp.android.api.Resource;
import com.ibigroup.mobile.otp.android.fragment.MyTripsFragment;
import com.ibigroup.mobile.otp.android.fragment.PlanTripFragment;
import com.ibigroup.mobile.otp.android.fragment.RouteViewerFragment;
import com.ibigroup.mobile.otp.android.fragment.StopViewerFragment;
import com.ibigroup.mobile.otp.android.json.CreateNewUserRequest;
import com.ibigroup.mobile.otp.android.json.DependentMobilityProfile;
import com.ibigroup.mobile.otp.android.json.MenuItem;
import com.ibigroup.mobile.otp.android.json.RentalVehicle;
import com.ibigroup.mobile.otp.android.json.RouteViewerRoute;
import com.ibigroup.mobile.otp.android.json.UserPreferences;
import com.ibigroup.mobile.otp.android.json.itinerary.ItineraryLegPoint;
import com.ibigroup.mobile.otp.android.utility.Configurations;
import com.ibigroup.mobile.otp.android.utility.Constants;
import com.ibigroup.mobile.otp.android.utility.Logger;
import com.ibigroup.mobile.otp.android.utility.ServerDelegate;
import com.ibigroup.mobile.otp.android.utility.Utilities;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class TabActivity extends BaseActivity {

    private RouteViewerFragment routeViewerFragment = null;
    private StopViewerFragment stopViewerFragment = null;
    private MyTripsFragment myTripsFragment = null;
    private PlanTripFragment planTripFragment = null;
    private final Menu tabMenu = null;
    private LinearLayout ll_tab_pan_trip, ll_tab_routes, ll_tab_stops, ll_tab_my_trips, ll_tab_sign_in, ll_tab_menu, ll_bottom_tabs, ll_bottom_tab_accessibility;
    private LinearLayout ll_bottom_tab_menu_item_four, ll_bottom_tab_menu_item_five, ll_bottom_tab_menu_item_three, ll_bottom_tab_menu_item_two, ll_bottom_tab_menu_item_one, ll_bottom_tab_menu_item_zero, ll_bottom_tab_menu_item;
    private ImageView iv_bottom_tab_menu_item_five, iv_bottom_tab_menu_item_four, iv_bottom_tab_menu_item_three, iv_bottom_tab_menu_item_two, iv_bottom_tab_menu_item_one, iv_bottom_tab_menu_item, iv_bottom_tab_menu_item_five_expand;
    private TextView tv_bottom_tab_menu_item_five, tv_bottom_tab_menu_item_four, tv_bottom_tab_menu_item_three, tv_bottom_tab_menu_item_two, tv_bottom_tab_menu_item_one, tv_bottom_tab_menu_item,
            tv_bottom_tab_menu_user_name, tv_bottom_tab_user_name_item_zero, tv_bottom_tab_user_name_item_one, tv_bottom_tab_user_name_item_two, tv_bottom_tab_user_name_item_three, tv_bottom_tab_user_name_item_four;
    private RelativeLayout rl_bottom_tab_menu_accessibility, rl_bottom_tab_menu_user_name, rl_bottom_tab_user_name_item_zero, rl_bottom_tab_user_name_item_one, rl_bottom_tab_user_name_item_two,
            rl_bottom_tab_user_name_item_three, rl_bottom_tab_user_name_item_four;
    private ImageView iv_tab_pan_trip, iv_tab_routes, iv_tab_stops, iv_tab_my_trips, iv_tab_sign_in, iv_down_button;
    private TextView tv_tab_pan_trip, tv_tab_routes, tv_tab_stops, tv_tab_my_trips, tv_tab_sign_in, tv_user_name;
    private int currentSelectedTab = MyApplication.getInstance().currentSelectedTab;
    private int previousSelectedTab = -1;
    private RelativeLayout rl_user_name;
    private Lock lock = null;
    private ArrayList<Integer> backKeyHistory = new ArrayList<Integer>();

    private final ArrayList<MenuItem> menuItems = new ArrayList<>();
    public DrawerLayout mDrawerLayout;
    public ListView mDrawerList;
    private LinearLayout llDrawer;
    private MenuListAdapter menuListAdapter;

    private boolean menuClicked = false;
    private boolean userNameClicked = false;

    private String notificationTripID = null;
    private RelativeLayout llAccountMenu;
    private LinearLayout llAccountMenuAccessibility;
    private TextView tvNickName, tvMyAccount, tvHelp, tvSignOut, tvDividerOne, tvDividertwo;
    private ImageView ivAccountSettings, ivLogout, ivhelp;
    private RelativeLayout rlPB;

    static final Map<String, Integer> assetsMapping = new HashMap<String, Integer>() {
        {
            put("ic_leavefeedbacks", R.drawable.ic_leavefeedbacks);
            put("ic_alerts", R.drawable.ic_alerts);
            put("ic_accessibility", R.drawable.ic_accessibility);
            put("ic_safety", R.drawable.ic_safety);
            put("ic_pay", R.drawable.ic_pay);
            put("ic_stations", R.drawable.ic_stations);
            put("ic_help_menu", R.drawable.ic_help_menu);
            put("ic_help", R.drawable.ic_help);

            put("ic_feedback", R.drawable.ic_feedback);
            put("ic_setting", R.drawable.ic_setting);
            put("ic_info", R.drawable.ic_info);
            put("ic_restart", R.drawable.ic_restart);
            put("ic_external_link", R.drawable.ic_external_link);
        }
    };

    static final Map<String, Integer> stringMapping = new HashMap<String, Integer>() {
        {
            put("Leave feedback", R.string.leave_feedback);
            put("Service Alerts", R.string.service_alerts);
            put("Accessibility", R.string.accessibility);
            put("Safety and Security", R.string.safety_and_security);
            put("How to Pay", R.string.how_to_pay);
            put("Stations", R.string.stations);
            put("Help", R.string.help);
        }
    };

    private void initMenuItemsData() {
        try {
            String items = Configurations.getString("menu_items", "");
            if (items == null || items.isEmpty()) {
                items = Utilities.loadDataFromAsset(this, "json/menu_items.json");
            }

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
                if (Configurations.getBoolean("enable_start_over_in_menu", false)) {

                    MenuItem startOverItem = gson.fromJson("{\n" +
                            "    \"title\": \"Start Over\",\n" +
                            "    \"icon\": \"ic_restart\",\n" +
                            "    \"type\": \"function\",\n" +
                            "    \"url\": \"\",\n" +
                            "    \"isVisible\": true,\n" +
                            "    \"order\": 0\n" +
                            "  }", MenuItem.class);

                    menuItems.add(startOverItem);

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
                if (menuItem.getTitle() != null && stringMapping.containsKey(menuItem.getTitle())) {
                    int strID = stringMapping.get(menuItem.getTitle());
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


    public void logoutHandling() {
        if (lock != null) {
            lock.onDestroy(TabActivity.this);
            lock = null;
        }

        String accountCredentails = Configurations.getString("account_credentials", "");
        if (!accountCredentails.isEmpty() && planTripFragment != null) {
            planTripFragment.resetSearchModeSettings();
            planTripFragment.refreshSearchModesControlsChangedIcon();
        }

        Configurations.setString("account_credentials", "");
        Configurations.setString("account_user_profile", "");
        Configurations.setString("account_access_token", "");
        Configurations.setString("account_id_token", "");
        Configurations.setString("last_track_trip_id", "");
        Configurations.setString("selected_dependant_email", "");
        ll_tab_sign_in.setVisibility(View.VISIBLE);
        ll_tab_my_trips.setVisibility(View.GONE);
        rl_user_name.setVisibility(View.GONE);
        llAccountMenu.setVisibility(View.GONE);
        tv_user_name.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorTabUserUnselected)));
        tv_user_name.setTextColor(getColor(android.R.color.black));


        if (planTripFragment != null) {
            planTripFragment.refreshSaveTripButton();
        }

        if (currentSelectedTab == Constants.BOTTOM_TAB_SELECTED_STOPS && stopViewerFragment != null) {
            stopViewerFragment.refreshFavStops();
        }

        if (currentSelectedTab == Constants.BOTTOM_TAB_SELECTED_MY_TRIPS && myTripsFragment != null) {
            currentSelectedTab = Constants.BOTTOM_TAB_SELECTED_PLAN_TRIP;
            backKeyHistory.add(currentSelectedTab);
            refreshBottomTabs();
            clickTabAction();
        }

        if (ll_bottom_tab_accessibility.getVisibility() == View.VISIBLE) {
            checkUserNameTagToReplaceSignIn();
            disableMyTrips();
        }

        //exit edit trip status without saving
        if (myTripsFragment != null) {
            myTripsFragment.backKeyHandler();
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
                        tvNickName.setText(getString(R.string.Logged_in_as_email, userProfile.getEmail()));
                        fullName = userProfile.getName();

                    }
                }
                ll_tab_sign_in.setVisibility(View.GONE);
                ll_tab_my_trips.setVisibility(View.VISIBLE);
                rl_user_name.setVisibility(View.VISIBLE);
                if (ll_bottom_tab_accessibility.getVisibility() == View.VISIBLE) {
                    checkSignInTagToReplaceUserName(nickName, "Profile");
                    showMyTrips();
                }
            } else {
                logoutHandling();
                ll_tab_sign_in.setVisibility(View.VISIBLE);
                ll_tab_my_trips.setVisibility(View.GONE);
                rl_user_name.setVisibility(View.GONE);
                if (ll_bottom_tab_accessibility.getVisibility() == View.VISIBLE) {
                    checkUserNameTagToReplaceSignIn();
                    disableMyTrips();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void refreshBottomTabs() {
        refreshPlanTripTab();
        refreshRoutesTab();
        refreshStopsTab();
        refreshMyTripsTab();
        refreshAccountStatusInTab();
    }

    public LinearLayout getBottomTab() {

        return this.ll_bottom_tabs;

    }

    private void setupBottomTabs() {
        ll_bottom_tabs = findViewById(R.id.ll_bottom_tab);
        rl_user_name = findViewById(R.id.rl_user_name);
        tv_user_name = findViewById(R.id.tv_user_name);

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

        iv_bottom_tab_menu_item_five = findViewById(R.id.iv_bottom_tab_menu_item_five);
        iv_bottom_tab_menu_item_four = findViewById(R.id.iv_bottom_tab_menu_item_four);
        iv_bottom_tab_menu_item_three = findViewById(R.id.iv_bottom_tab_menu_item_three);
        iv_bottom_tab_menu_item_two = findViewById(R.id.iv_bottom_tab_menu_item_two);
        iv_bottom_tab_menu_item_one = findViewById(R.id.iv_bottom_tab_menu_item_one);
        iv_bottom_tab_menu_item = findViewById(R.id.iv_bottom_tab_menu_item);

        tv_bottom_tab_menu_item_five = findViewById(R.id.tv_bottom_tab_menu_item_five);
        tv_bottom_tab_menu_item_four = findViewById(R.id.tv_bottom_tab_menu_item_four);
        tv_bottom_tab_menu_item_three = findViewById(R.id.tv_bottom_tab_menu_item_three);
        tv_bottom_tab_menu_item_two = findViewById(R.id.tv_bottom_tab_menu_item_two);
        tv_bottom_tab_menu_item_one = findViewById(R.id.tv_bottom_tab_menu_item_one);
        tv_bottom_tab_menu_item = findViewById(R.id.tv_bottom_tab_menu_item);


        iv_down_button = findViewById(R.id.iv_bottom_tab_menu_item_expand);
        iv_bottom_tab_menu_item_five_expand = findViewById(R.id.iv_bottom_tab_menu_item_five_expand);

        tv_user_name.setContentDescription(getString(R.string.user_profile_button));
        ll_tab_pan_trip = findViewById(R.id.ll_tab_pan_trip);
        ll_tab_routes = findViewById(R.id.ll_tab_routes);
        ll_tab_stops = findViewById(R.id.ll_tab_stops);
        ll_tab_my_trips = findViewById(R.id.ll_tab_my_trips);
        ll_tab_sign_in = findViewById(R.id.ll_tab_sign_in);
        ll_tab_menu = findViewById(R.id.ll_tab_menu);

        ll_tab_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawerLayout.openDrawer(llDrawer);
            }
        });

        ll_tab_pan_trip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentSelectedTab != Constants.BOTTOM_TAB_SELECTED_PLAN_TRIP) {
                    currentSelectedTab = Constants.BOTTOM_TAB_SELECTED_PLAN_TRIP;
                    backKeyHistory.add(currentSelectedTab);
                    refreshBottomTabs();
                    clickTabAction();
                }
            }
        });

        ll_tab_routes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentSelectedTab != Constants.BOTTOM_TAB_SELECTED_ROUTES) {
                    currentSelectedTab = Constants.BOTTOM_TAB_SELECTED_ROUTES;
                    backKeyHistory.add(currentSelectedTab);
                    refreshBottomTabs();
                    clickTabAction();

                    if (routeViewerFragment != null) {
                        routeViewerFragment.resetIfNeeded();
                    }
                }
            }
        });

        ll_tab_stops.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentSelectedTab != Constants.BOTTOM_TAB_SELECTED_STOPS) {
                    currentSelectedTab = Constants.BOTTOM_TAB_SELECTED_STOPS;
                    backKeyHistory.add(currentSelectedTab);
                    refreshBottomTabs();
                    clickTabAction();
                }
            }
        });

        ll_tab_my_trips.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentSelectedTab != Constants.BOTTOM_TAB_SELECTED_MY_TRIPS) {
                    currentSelectedTab = Constants.BOTTOM_TAB_SELECTED_MY_TRIPS;
                    backKeyHistory.add(currentSelectedTab);
                    refreshBottomTabs();
                    clickTabAction();
                }
            }
        });

        ll_tab_sign_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                previousSelectedTab = currentSelectedTab;
                currentSelectedTab = Constants.BOTTOM_TAB_SELECTED_SIGN_IN;
                clickTabAction();
            }
        });

        rl_user_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (llAccountMenu.getVisibility() == View.VISIBLE) {
                    closeUserNamePopup();
                } else {
                    previousSelectedTab = currentSelectedTab;
                    currentSelectedTab = -1;
                    refreshBottomTabs();
                    llAccountMenu.setVisibility(View.VISIBLE);
                    tv_user_name.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorPrimary)));
                    tv_user_name.setTextColor(getColor(android.R.color.white));
                }
            }
        });


        iv_down_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (rl_bottom_tab_menu_accessibility.getVisibility() == View.VISIBLE) {
                    rl_bottom_tab_menu_accessibility.setVisibility(View.GONE);
                } else {
                    if (llAccountMenu.getVisibility() == View.VISIBLE) {
                        llAccountMenu.setVisibility(View.GONE);
                        userNameClicked = false;
                        closeUserNamePopup();

                    }

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
                menuClicked = false;
                userNameClicked = false;
                view.setPressed(true);
                rl_bottom_tab_menu_accessibility.setVisibility(View.GONE);
                if (view.getTag() == null || view.getTag().toString() == null) {
                    return;
                }
                switch (Integer.parseInt(view.getTag().toString())) {
                    case Constants.PLAN_TRIP_TAG:
                        if (currentSelectedTab != Constants.BOTTOM_TAB_SELECTED_PLAN_TRIP) {
                            currentSelectedTab = Constants.BOTTOM_TAB_SELECTED_PLAN_TRIP;
                            backKeyHistory.add(currentSelectedTab);
                            refreshBottomTabs();
                            clickTabAction();

                        }
                        sortMenuItems();
                        break;


                    case Constants.ROUTES_TAG:
                        if (currentSelectedTab != Constants.BOTTOM_TAB_SELECTED_ROUTES) {
                            currentSelectedTab = Constants.BOTTOM_TAB_SELECTED_ROUTES;
                            backKeyHistory.add(currentSelectedTab);
                            refreshBottomTabs();
                            clickTabAction();

                        }
                        sortMenuItems();
                        break;

                    case Constants.MENU_TAG:

                        mDrawerLayout.openDrawer(llDrawer);
                        break;

                    case Constants.MY_TRIPS_TAG:

                        if (currentSelectedTab != Constants.BOTTOM_TAB_SELECTED_MY_TRIPS) {
                            currentSelectedTab = Constants.BOTTOM_TAB_SELECTED_MY_TRIPS;
                            backKeyHistory.add(currentSelectedTab);
                            refreshBottomTabs();
                            clickTabAction();

                        }
                        sortMenuItems();
                        break;


                    case Constants.SIGN_IN_TAG:

                        previousSelectedTab = currentSelectedTab;
                        currentSelectedTab = Constants.BOTTOM_TAB_SELECTED_SIGN_IN;
                        clickTabAction();
                        break;

                    case Constants.USER_NAME_TAG:


                        if (llAccountMenu.getVisibility() == View.VISIBLE) {
                            closeUserNamePopup();
                        } else {
                            previousSelectedTab = currentSelectedTab;
                            currentSelectedTab = -1;
                            refreshBottomTabs();
                            llAccountMenu.setVisibility(View.VISIBLE);
                            tv_user_name.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorPrimary)));
                            tv_user_name.setTextColor(getColor(android.R.color.white));
                            userNameClicked = true;
                            sortMenuItems();

                        }

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


    private void sortMenuItems() {
        if (menuClicked) {

            ll_bottom_tab_menu_item.setTag(Constants.MENU_TAG);

        }
        if (userNameClicked) {

            ll_bottom_tab_menu_item.setTag(Constants.USER_NAME_TAG);

        }

        if (!menuClicked && !userNameClicked) {

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
                                tvFullName.setText("Profile");


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

    private void clickTabAction() {
        if (currentSelectedTab != Constants.BOTTOM_TAB_SELECTED_AVATAR
                && llAccountMenu.getVisibility() == View.VISIBLE) {
            closeUserNamePopup();
        }

        switch (currentSelectedTab) {
            case Constants.BOTTOM_TAB_SELECTED_ROUTES:

                if (routeViewerFragment == null) {
                    routeViewerFragment = new RouteViewerFragment();
                }
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fl_container, routeViewerFragment, null)
                        .commit();
                break;
            case Constants.BOTTOM_TAB_SELECTED_STOPS:

                if (stopViewerFragment == null) {
                    stopViewerFragment = new StopViewerFragment();
                }
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fl_container, stopViewerFragment, null)
                        .commit();
                break;

            case Constants.BOTTOM_TAB_SELECTED_MY_TRIPS:

                if (myTripsFragment == null) {
                    myTripsFragment = new MyTripsFragment();
                }
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fl_container, myTripsFragment, null)
                        .commit();
                break;

            case Constants.BOTTOM_TAB_SELECTED_PLAN_TRIP:

                if (planTripFragment == null) {
                    planTripFragment = new PlanTripFragment();
                }
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fl_container, planTripFragment, null)
                        .commit();
                break;

            case Constants.BOTTOM_TAB_SELECTED_SIGN_IN:

                showNativeAuth();
                break;

            case Constants.BOTTOM_TAB_SELECTED_AVATAR:

                llAccountMenu.setVisibility(View.VISIBLE);
                tv_user_name.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorPrimary)));
                tv_user_name.setTextColor(getColor(android.R.color.white));
                break;
        }
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

    public void showNativeAuth() {

        Intent intent = new Intent(this, LogInSignUpActivity.class);
        this.startActivityForResult(intent, AUTH0_REQUEST_CODE);
    }

    private void gotoVerifyEmailActivity() {
        Intent intent = new Intent(this, VerifyEmailActivity.class);

        startActivity(intent);
    }

    private void gotoCreateNewAccountActivity() {
        Intent intent = new Intent(this, CreateNewAccountActivity.class);

        startActivity(intent);
    }

    private void gotoMyAccountActivity() {
        Intent intent = new Intent(this, MyAccountActivity.class);
        intent.putExtra("account_settings", true);
        startActivity(intent);
    }

    public void getFromToken(final boolean isDuringLogin) {
        Response.Listener listener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                hideProgressBar();
                if (response != null && !response.isEmpty()) {
                    Gson gson = new Gson();
                    try {
                        MyApplication.getInstance().userPreferences = gson.fromJson(response, UserPreferences.class);

                        Configurations.setString("account_user_preferences", response);
                        if (MyApplication.getInstance().userPreferences.isHasConsentedToTerms()) {
                            if (!isDuringLogin) {
                                gotoMyAccountActivity();
                            }
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
                hideProgressBar();
                Logger.e("account", error.toString());
                if (error != null && error.networkResponse != null) {
                    if (error.networkResponse.statusCode == 404) {
                        //create a new user in otp middleware
                        createNewUserInMiddleware();
                    } else if (error.networkResponse.statusCode == 401) {
                        showTokenExpiredAndRelogin();
                    }
                }
            }
        };

        String url = Resource.API_ACCOUNT_FROM_TOKEN;

        volleyAccountStringRequest(Request.Method.GET, url, "", listener, errorListener);
    }

    private void showTokenExpiredAndRelogin() {
        logoutHandling();
        closeUserNamePopup();
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        // set title
        alertDialogBuilder.setTitle(getString(R.string.warning));

        // set dialog message
        alertDialogBuilder
                .setMessage(getString(R.string.token_expired_message))
                .setCancelable(false)
                .setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                        showNativeAuth();
                    }
                });


        alertDialogBuilder.create().show();
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

                        gotoCreateNewAccountActivity();
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

    private void retrieveUserInfo(String accessToken, final boolean isDuringLogin) {
        Auth0 account = new Auth0(Configurations.getString("auth0_client_id", ""), Configurations.getString("auth0_domain", ""));
        Callback<UserProfile, AuthenticationException> userInfoCallback = new Callback<UserProfile, AuthenticationException>() {
            @Override
            public void onSuccess(UserProfile userProfile) {
                Gson gson = new Gson();
                Configurations.setString("account_user_profile", gson.toJson(userProfile));

                if (isDuringLogin && !Configurations.getString("device_token", "").isEmpty()) {
                    try {
                        ServerDelegate.postRegistration(Configurations.getString("device_token", ""));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                refreshAccountStatusInTab();
                //showResult("OK > " + userProfile);
                if (userProfile != null) {
                    if (Boolean.TRUE.equals(userProfile.isEmailVerified())) {
                        getFromToken(isDuringLogin);
                    } else {
                        hideProgressBar();
                        gotoVerifyEmailActivity();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull AuthenticationException e) {
                hideProgressBar();
                if (e.getStatusCode() == 401) {
                    Utilities.showNormalPopupDialog(TabActivity.this, "", getString(R.string.retrieve_user_info_failed_unauthorized));
                    logoutHandling();
                    closeUserNamePopup();
                } else {
                    Utilities.showNormalPopupDialog(TabActivity.this, "", getString(R.string.retrieve_user_info_failed_other));
                }
            }
        };
        AuthenticationAPIClient authenticationAPIClient = new AuthenticationAPIClient(account);
        authenticationAPIClient.userInfo(accessToken).start(userInfoCallback);
    }

    private void setupAccountMenu() {
        llAccountMenu = findViewById(R.id.ll_account_menu);
        tvNickName = findViewById(R.id.tv_nickname);
        tvMyAccount = findViewById(R.id.tv_my_account);
        tvHelp = findViewById(R.id.tv_help);
        tvSignOut = findViewById(R.id.tv_sign_out);

        llAccountMenuAccessibility = findViewById(R.id.ll_account_menu_accessibility);
        ivAccountSettings = findViewById(R.id.iv_account_settings);
        ivLogout = findViewById(R.id.iv_account_logout);
        ivhelp = findViewById(R.id.iv_account_help);
        tvDividerOne = findViewById(R.id.tv_divider_one);
        tvDividertwo = findViewById(R.id.tv_divder_two);

        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) llAccountMenuAccessibility.getLayoutParams();
        LinearLayout.LayoutParams accountParams = (LinearLayout.LayoutParams) tvMyAccount.getLayoutParams();
        LinearLayout.LayoutParams helpParams = (LinearLayout.LayoutParams) tvHelp.getLayoutParams();
        LinearLayout.LayoutParams logoutParams = (LinearLayout.LayoutParams) tvSignOut.getLayoutParams();

        if (Utilities.checkIfToDisplayAccessibilityUI(getApplicationContext())) {
            params.height = RelativeLayout.LayoutParams.MATCH_PARENT;
            accountParams.gravity = Gravity.CENTER_HORIZONTAL;
            helpParams.gravity = Gravity.CENTER_HORIZONTAL;
            logoutParams.gravity = Gravity.CENTER_HORIZONTAL;

            llAccountMenuAccessibility.setLayoutParams(params);
            tvMyAccount.setLayoutParams(accountParams);
            tvHelp.setLayoutParams(helpParams);
            tvSignOut.setLayoutParams(logoutParams);

            ivAccountSettings.setVisibility(View.VISIBLE);
            ivLogout.setVisibility(View.VISIBLE);
            ivhelp.setVisibility(View.VISIBLE);
            tvDividertwo.setVisibility(View.GONE);
            tvDividerOne.setVisibility(View.GONE);
        } else {
            params.height = RelativeLayout.LayoutParams.WRAP_CONTENT;
            accountParams.gravity = Gravity.CENTER_HORIZONTAL;
            helpParams.gravity = Gravity.CENTER_HORIZONTAL;
            logoutParams.gravity = Gravity.CENTER_HORIZONTAL;

            llAccountMenuAccessibility.setLayoutParams(params);
            tvMyAccount.setLayoutParams(accountParams);
            tvHelp.setLayoutParams(helpParams);
            tvSignOut.setLayoutParams(logoutParams);

            ivAccountSettings.setVisibility(View.GONE);
            ivLogout.setVisibility(View.GONE);
            ivhelp.setVisibility(View.GONE);
            tvDividertwo.setVisibility(View.VISIBLE);
            tvDividerOne.setVisibility(View.VISIBLE);
        }

        tvSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logoutHandling();
                closeUserNamePopup();
            }
        });

        llAccountMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!Utilities.checkIfToDisplayAccessibilityUI(getApplicationContext())) {
                    closeUserNamePopup();
                }
            }
        });
        tvHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utilities.GotoTheUrl(TabActivity.this, Configurations.ACCOUNT_HELP_URL);
                closeUserNamePopup();
            }
        });

        tvMyAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProgressBar();
                if (!Utilities.checkIfToDisplayAccessibilityUI(getApplicationContext())) {
                    closeUserNamePopup();
                }
                retrieveUserInfo(Configurations.getString("account_access_token", ""), false);
            }
        });
    }

    private void closeUserNamePopup() {
        llAccountMenu.setVisibility(View.GONE);
        tv_user_name.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorTabUserUnselected)));
        tv_user_name.setTextColor(getColor(android.R.color.black));
        if (previousSelectedTab != -1) {
            currentSelectedTab = previousSelectedTab;
            previousSelectedTab = -1;
            refreshBottomTabs();
            userNameClicked = false;
            clickAccessibilityBottomTabAction();
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putIntegerArrayList("back_key_history", backKeyHistory);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_tab);

        if (savedInstanceState != null && savedInstanceState.containsKey("back_key_history")) {
            backKeyHistory = savedInstanceState.getIntegerArrayList("back_key_history");

            if (backKeyHistory == null) {
                backKeyHistory = new ArrayList<Integer>();
            }
        }
        setupBottomTabs();
        setupAccountMenu();
        refreshBottomTabs();

        //initial with the first page Plan Trip
        boolean isFromNotification = false;
        if (getIntent() != null) {
            isFromNotification = getIntent().getBooleanExtra("from_notification", false);
            notificationTripID = getIntent().getStringExtra(Constants.NOTIFICATION_KEY_TRIP_ID);
        }
        if (isFromNotification && Utilities.isLoggedIn()) {
            Logger.i("push", "from_notification");
            currentSelectedTab = Constants.BOTTOM_TAB_SELECTED_MY_TRIPS;
            MyApplication.getInstance().currentSelectedTab = currentSelectedTab;
            refreshBottomTabs();
        } else {
            currentSelectedTab = Constants.BOTTOM_TAB_SELECTED_PLAN_TRIP;
        }
        backKeyHistory.add(currentSelectedTab);
        clickTabAction();

        if (isFromNotification && notificationTripID != null && myTripsFragment != null) {
            myTripsFragment.setNotificationTripID(notificationTripID);
        }
        getRouteViewerRoutes();
        getRentalVehicles();

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
                        if (menuItem.getAction().equalsIgnoreCase(MENU_ACTION_TRIGGER_INTERNAL_WEB)) {
                            String title;
                            if (Utilities.isCurrentLanguageDefault()) {
                                title = menuItem.getTitle();
                            } else {
                                if (menuItem.getTitle() != null && stringMapping.containsKey(menuItem.getTitle())) {
                                    title = getString(stringMapping.get(menuItem.getTitle()));
                                } else {
                                    title = menuItem.getTitle();
                                }
                            }

                        } else if (menuItem.getAction().equalsIgnoreCase(MENU_ACTION_TRIGGER_EXTERNAL_WEB) ||
                                menuItem.getAction().equalsIgnoreCase(MENU_ACTION_LINK)) {
                            Utilities.GotoTheUrl(TabActivity.this, Utilities.getUrlWithLanguageFor(menuItem.getValue()));
                        } else if (menuItem.getAction().equalsIgnoreCase(MENU_ACTION_FUNCTION)) {

                            if (currentSelectedTab != Constants.BOTTOM_TAB_SELECTED_PLAN_TRIP) {
                                currentSelectedTab = Constants.BOTTOM_TAB_SELECTED_PLAN_TRIP;
                                MyApplication.getInstance().currentSelectedTab = currentSelectedTab;
                                if (planTripFragment == null) {
                                    planTripFragment = new PlanTripFragment();
                                }

                                backKeyHistory.add(Constants.BOTTOM_TAB_SELECTED_PLAN_TRIP);
                                getSupportFragmentManager().beginTransaction()
                                        .replace(R.id.fl_container, planTripFragment, null)
                                        .commitNow();
                                refreshBottomTabs();
                            }

                            if (planTripFragment != null) {
                                planTripFragment.resetAction();
                            }

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

        if (Configurations.getBoolean("enable_main_menu", true)) {
            ll_tab_menu.setVisibility(View.VISIBLE);
            mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        } else {
            ll_tab_menu.setVisibility(View.GONE);
            mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        }

        getJmapTriggerableLocations();
        getIndoorEntranceExitList();
        getIndoorMainEntrances();
    }

    public void showProgressBar() {
        if (rlPB == null) {
            rlPB = findViewById(R.id.rl_pb);
        }
        rlPB.setVisibility(View.VISIBLE);
    }

    public void hideProgressBar() {
        if (rlPB == null) {
            rlPB = findViewById(R.id.rl_pb);
        }
        rlPB.setVisibility(View.GONE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == Constants.STOP_VIEWER_REQUEST_CODE || requestCode == Constants.TRIP_VIEWER_REQUEST_CODE) {
                if (data != null) {
                    boolean isOrigin = data.getBooleanExtra(Constants.IS_ORIGIN, true);
                    String searchedAddress = data.getStringExtra(Constants.SEARCHED_ADDRESS);
                    double lat = data.getDoubleExtra(Constants.SEARCHED_LATITUDE, 0.0);
                    double lon = data.getDoubleExtra(Constants.SEARCHED_LONGITUDE, 0.0);

                    if (currentSelectedTab != Constants.BOTTOM_TAB_SELECTED_PLAN_TRIP) {
                        currentSelectedTab = Constants.BOTTOM_TAB_SELECTED_PLAN_TRIP;
                        MyApplication.getInstance().currentSelectedTab = currentSelectedTab;
                        if (planTripFragment == null) {
                            planTripFragment = new PlanTripFragment();
                        }

                        backKeyHistory.add(Constants.BOTTOM_TAB_SELECTED_PLAN_TRIP);
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.fl_container, planTripFragment, null)
                                .commit();
                        refreshBottomTabs();
                    }
                    planTripFragment.loadFromStopViewer(isOrigin, searchedAddress, lat, lon);
                }
            } else if (requestCode == AUTH0_REQUEST_CODE) {

                String token = data.getStringExtra(AUTH0_SUCCESS);
                retrieveUserInfo(token, true);
                if (planTripFragment != null) {
                    planTripFragment.refreshSaveTripButton();
                }

            }
        } else if (resultCode == RESULT_CANCELED && requestCode == Constants.STOP_VIEWER_REQUEST_CODE) {
            if (currentSelectedTab == Constants.BOTTOM_TAB_SELECTED_PLAN_TRIP) {
                if (planTripFragment == null) {
                    planTripFragment = new PlanTripFragment();
                }

                planTripFragment.setMapCenterAndZoomIfNeeded();
            } else if (currentSelectedTab == Constants.BOTTOM_TAB_SELECTED_ROUTES) {
                if (routeViewerFragment == null) {
                    routeViewerFragment = new RouteViewerFragment();
                }
                routeViewerFragment.setMapCenterAndZoomIfNeeded();
            }
        }
    }

    public void loadPlanTripFromStopViewer(boolean isOrigin, String searchedAddress, double lat, double lon) {
        if (currentSelectedTab != Constants.BOTTOM_TAB_SELECTED_PLAN_TRIP) {
            currentSelectedTab = Constants.BOTTOM_TAB_SELECTED_PLAN_TRIP;
            if (planTripFragment == null) {
                planTripFragment = new PlanTripFragment();
            }
            backKeyHistory.add(Constants.BOTTOM_TAB_SELECTED_PLAN_TRIP);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fl_container, planTripFragment, null)
                    .commit();
            refreshBottomTabs();
        }
        planTripFragment.loadFromStopViewer(isOrigin, searchedAddress, lat, lon);
    }

    public void loadPlanTripFromMyTrips(ItineraryLegPoint origin, ItineraryLegPoint destination) {
        if (currentSelectedTab != Constants.BOTTOM_TAB_SELECTED_PLAN_TRIP) {
            currentSelectedTab = Constants.BOTTOM_TAB_SELECTED_PLAN_TRIP;
            if (planTripFragment == null) {
                planTripFragment = new PlanTripFragment();
            }
            backKeyHistory.add(Constants.BOTTOM_TAB_SELECTED_PLAN_TRIP);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fl_container, planTripFragment, null)
                    .commit();
            refreshBottomTabs();
        }
        planTripFragment.loadFromMyTrips(origin, destination);
    }

    private void parseRouteViewerRoutesData() {
        mapModeAsNeeded();
        mapAgencyNameAsNeeded();
        Collections.sort(MyApplication.getInstance().routeViewerRoutes, new Comparator<RouteViewerRoute>() {
            @Override
            public int compare(RouteViewerRoute o1, RouteViewerRoute o2) {
                int mode1 = Utilities.mapMode(o1);
                int mode2 = Utilities.mapMode(o2);

                int agencyOrder1 = Utilities.getAgencyOrder(o1);
                int agencyOrder2 = Utilities.getAgencyOrder(o2);

                if (o1.getSortOrder() != o2.getSortOrder()) {
                    return (o1.getSortOrder() - o2.getSortOrder());
                } else if (agencyOrder1 != agencyOrder2) {
                    return (agencyOrder1 - agencyOrder2);
                } else if (mode1 != mode2) {
                    return (mode1 - mode2);
                } else {
                    if (o1.getShortName() != null && o2.getShortName() == null) {
                        return -1;
                    } else if (o1.getShortName() == null && o2.getShortName() != null) {
                        return 1;
                    } else if (o1.getShortName() != null && o2.getShortName() != null &&
                            !o1.getShortName().equals(o2.getShortName())) {
                        return compareShortName(o1.getShortName(), o2.getShortName());
                    } else if (o1.getLongName() != null && o2.getLongName() != null &&
                            !o1.getLongName().equals(o2.getLongName())) {
                        return o1.getLongName().compareTo(o2.getLongName());
                    }
                }

                return 0;
            }
        });

        Handler mainHandler = new Handler(Looper.getMainLooper());
        mainHandler.post(() -> {
            if (currentSelectedTab == Constants.BOTTOM_TAB_SELECTED_ROUTES
                    && routeViewerFragment != null) {
                routeViewerFragment.notifyDataChanged();
            }
        });

    }

    private int compareShortName(String shortName1, String shortName2) {
        if (shortName1 != null && shortName2 != null) {
            boolean isNumber1 = false;
            boolean isNumber2 = false;

            String firstChar1 = shortName1.substring(0, 1);
            String firstChar2 = shortName2.substring(0, 1);
            if (firstChar1.compareTo("0") >= 0 && firstChar1.compareTo("9") <= 0) {
                isNumber1 = true;
            }

            if (firstChar2.compareTo("0") >= 0 && firstChar2.compareTo("9") <= 0) {
                isNumber2 = true;
            }

            if (isNumber1 && !isNumber2) {
                return 1;
            } else if (!isNumber1 && isNumber2) {
                return -1;
            } else if (!isNumber1 && !isNumber2) {
                return shortName1.compareTo(shortName2);
            }

            int route1 = -1;
            int route2 = -1;
            try {
                route1 = Integer.parseInt(filterNonNumber(shortName1));
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                route2 = Integer.parseInt(filterNonNumber(shortName2));
            } catch (Exception e) {
                e.printStackTrace();
            }


            if (route1 != route2) {
                return Integer.compare(route1, route2);// (route1 - route2);
            } else {
                return shortName1.compareTo(shortName2);//compareNumberString(shortName1, shortName2);
            }
        }
        return 0;
    }

    private String filterNonNumber(String s1) {
        if (s1 != null) {
            StringBuffer stringBuffer = new StringBuffer();
            char char1;
            for (int i = 0; i < s1.length(); i++) {
                char1 = s1.charAt(i);
                if (char1 >= '0' && char1 <= '9') {
                    stringBuffer.append(char1);
                }
            }
            return stringBuffer.toString();
        }
        return null;
    }

    private void getRentalVehicles() {
        if (!GRAPHQL_SERVER_URL.isEmpty()) {
            ApolloClient apolloClient = ApolloClient.builder()
                    .serverUrl(GRAPHQL_SERVER_URL)
                    .build();
            apolloClient.query(new GetRentalVehiclesQuery()).enqueue(new ApolloCall.Callback<GetRentalVehiclesQuery.Data>() {
                @Override
                public void onResponse(@NonNull com.apollographql.apollo.api.Response<GetRentalVehiclesQuery.Data> response) {
                    if (response != null && response.getData() != null && response.getData().rentalVehicles != null) {
                        Gson gson = new Gson();
                        MyApplication.getInstance().rentalVehicles = gson.fromJson(gson.toJson(response.getData().rentalVehicles), new TypeToken<ArrayList<RentalVehicle>>() {
                        }.getType());

                        Handler mainHandler = new Handler(Looper.getMainLooper());
                        mainHandler.post(() -> {
                            if (planTripFragment != null) {
                                planTripFragment.refreshRentalVehiclesOnMap();
                            }

                            if (routeViewerFragment != null) {
                                routeViewerFragment.refreshRentalVehiclesOnMap();
                            }
                        });
                    }
                }

                @Override
                public void onFailure(@NonNull ApolloException e) {
                    Logger.e("data", "get rental vehicles error:" + e);
                }
            });
        }
    }

    private void getRouteViewerRoutes() {
        if (!GRAPHQL_SERVER_URL.isEmpty()) {
            ApolloClient apolloClient = ApolloClient.builder()
                    .serverUrl(GRAPHQL_SERVER_URL)
                    .build();
            apolloClient.query(new RoutesListQuery()).enqueue(new ApolloCall.Callback<RoutesListQuery.Data>() {
                @Override
                public void onResponse(@NonNull com.apollographql.apollo.api.Response<RoutesListQuery.Data> response) {
                    if (response != null && response.getData() != null && response.getData().routes != null) {
                        Gson gson = new Gson();
                        MyApplication.getInstance().routeViewerRoutes = gson.fromJson(gson.toJson(response.getData().routes), new TypeToken<ArrayList<RouteViewerRoute>>() {
                        }.getType());
                        parseRouteViewerRoutesData();
                    }
                }

                @Override
                public void onFailure(@NonNull ApolloException e) {
                    Logger.e("data", "get route viewer info error:" + e);
                }
            });
        } else {
            Response.Listener<String> getTripViewerRouteInfoListener = new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    if (response != null && !response.isEmpty() && response.startsWith("[")) {
                        Gson gson = new Gson();
                        MyApplication.getInstance().routeViewerRoutes = gson.fromJson(response, new TypeToken<ArrayList<RouteViewerRoute>>() {
                        }.getType());

                        parseRouteViewerRoutesData();
                    } else {
                        Logger.d("data", "route viewer info is not correct:" + response);
                    }

                }
            };
            Response.ErrorListener errorListener = new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Logger.e("data", "get route viewer info error:" + error.toString());
                }
            };

            try {
                getRouteViewerRoutesResponse(getTripViewerRouteInfoListener, errorListener);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void mapAgencyNameAsNeeded() {
        try {
            String agencyNameMappingData = Utilities.loadDataFromAsset(TabActivity.this, "json/route_mode_mapping.json");
            if (agencyNameMappingData != null) {
                JSONObject jsonObject = new JSONObject(agencyNameMappingData);
                String routeModeAgencyMappingString = jsonObject.optString("route_mode_agencies_mapping", "");
                String routeModeNameMappingString = jsonObject.optString("route_mode_name_mapping", "");
                if (!routeModeAgencyMappingString.isEmpty() || !routeModeNameMappingString.isEmpty()) {
                    String[] routeModeAgencyMappingStringArray = routeModeAgencyMappingString.split(";");
                    String[] routeModeNameMappingStringArray = routeModeNameMappingString.split(";");
                    for (RouteViewerRoute routeViewerRoute : MyApplication.getInstance().routeViewerRoutes) {
                        if (routeViewerRoute.getAgencyName() != null) {
                            for (String routeModeAgencyMapping : routeModeAgencyMappingStringArray) {
                                String[] routeModeAgencyMappings = routeModeAgencyMapping.split(",");
                                if (routeModeAgencyMappings.length == 2 && routeViewerRoute.getAgencyName().equals(routeModeAgencyMappings[0])) {
                                    routeViewerRoute.setAgencyName(routeModeAgencyMappings[1]);
                                    break;
                                }
                            }
                        }
                        if (routeViewerRoute.getMode() != null) {
                            for (String routeModeNameMapping : routeModeNameMappingStringArray) {
                                String[] routeModeNameMappings = routeModeNameMapping.split(",");
                                if (routeModeNameMappings.length == 2 && routeViewerRoute.getMode().equalsIgnoreCase(routeModeNameMappings[0])) {
                                    routeViewerRoute.setMode(routeModeNameMappings[1]);
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getRouteModeOverridesString() {
        String routeModeOverridesString = Configurations.getString("route_mode_overrides", "");

        if (routeModeOverridesString != null && !routeModeOverridesString.isEmpty()) {
            return routeModeOverridesString;
        } else {
            String routeModeMappingData = Utilities.loadDataFromAsset(TabActivity.this, "json/route_mode_mapping.json");
            if (routeModeMappingData != null) {
                try {
                    JSONObject jsonObject = new JSONObject(routeModeMappingData);
                    return jsonObject.optString("route_mode_overrides", "");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        return null;
    }

    private void mapModeAsNeeded() {
        String routeModeOverridesString = getRouteModeOverridesString();
        if (routeModeOverridesString != null && !routeModeOverridesString.isEmpty()) {
            String[] routeModeOverrideStringArray = routeModeOverridesString.split(";");

            for (RouteViewerRoute routeViewerRoute : MyApplication.getInstance().routeViewerRoutes) {
                if (routeViewerRoute != null && routeViewerRoute.getId() != null) {
                    for (String routeModeOverrideString : routeModeOverrideStringArray) {
                        String[] routeModeMapping = routeModeOverrideString.split(",");
                        if (routeModeMapping.length == 2 && routeViewerRoute.getId().equals(routeModeMapping[0])) {
                            routeViewerRoute.setMode(routeModeMapping[1]);
                            break;
                        }
                    }
                }
            }
        }
    }

    public void getRouteViewerRoutesResponse(Response.Listener<String> listener,
                                             Response.ErrorListener errListener) throws Exception {
        String url = Resource.API_ROUTE_VIEWER_ROUTES;

        volleyStringRequest(Request.Method.GET, url, "", listener, errListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (lock != null) {
            lock.onDestroy(this);
            lock = null;
        }
        MyApplication.getInstance().currentSelectedTab = 0;

    }

    @Override
    protected void onPause() {
        super.onPause();
        MyApplication.getInstance().currentSelectedTab = currentSelectedTab;
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

    @Override
    protected void onResume() {
        super.onResume();
        if (currentSelectedTab != MyApplication.getInstance().currentSelectedTab) {
            currentSelectedTab = MyApplication.getInstance().currentSelectedTab;
            if (currentSelectedTab != Constants.BOTTOM_TAB_SELECTED_AVATAR
                    && currentSelectedTab != Constants.BOTTOM_TAB_SELECTED_SIGN_IN) {
                refreshBottomTabs();

                backKeyHistory.add(currentSelectedTab);
            }
            clickTabAction();
        }

        refreshAccountStatusInTab();

        if (currentSelectedTab == Constants.BOTTOM_TAB_SELECTED_SIGN_IN && previousSelectedTab != -1) {
            currentSelectedTab = previousSelectedTab;
            previousSelectedTab = -1;
            refreshBottomTabs();
        }

        if (Utilities.checkIfToDisplayAccessibilityUI(getApplicationContext())) {
            ll_bottom_tabs.setVisibility(View.GONE);
            ll_bottom_tab_accessibility.setVisibility(View.VISIBLE);
            clickAccessibilityBottomTabAction();
            refreshAccountStatusInTab();
        }

        if (MyApplication.getInstance().needGotoSavedTripPage) {
            MyApplication.getInstance().needGotoSavedTripPage = false;
            if (!Utilities.isLoggedIn()) {
                previousSelectedTab = currentSelectedTab;
                currentSelectedTab = Constants.BOTTOM_TAB_SELECTED_SIGN_IN;
                clickTabAction();
            } else {
                if (currentSelectedTab != Constants.BOTTOM_TAB_SELECTED_MY_TRIPS) {
                    previousSelectedTab = currentSelectedTab;
                    currentSelectedTab = Constants.BOTTOM_TAB_SELECTED_MY_TRIPS;
                    refreshBottomTabs();
                    clickTabAction();
                }
            }
        }

        getDependantMobilityProfilesIfNeeded();

        if (MyApplication.getInstance().gbStartOver) {

            if (currentSelectedTab != Constants.BOTTOM_TAB_SELECTED_PLAN_TRIP) {
                currentSelectedTab = Constants.BOTTOM_TAB_SELECTED_PLAN_TRIP;
                MyApplication.getInstance().currentSelectedTab = currentSelectedTab;
                if (planTripFragment == null) {
                    planTripFragment = new PlanTripFragment();
                }

                if (routeViewerFragment == null) {
                    routeViewerFragment = new RouteViewerFragment();
                }

                backKeyHistory.add(Constants.BOTTOM_TAB_SELECTED_PLAN_TRIP);
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fl_container, planTripFragment, null)
                        .commitNow();
                refreshBottomTabs();
            }


            if (planTripFragment != null) {
                planTripFragment.resetAction();
            }

            if (routeViewerFragment != null) {
                routeViewerFragment.resetAction(currentSelectedTab ==
                        Constants.BOTTOM_TAB_SELECTED_ROUTES);
            }
        }
    }


    private void clickAccessibilityBottomTabAction() {
        ll_bottom_tab_menu_item.setPressed(currentSelectedTab == Integer.parseInt(ll_bottom_tab_menu_item.getTag().toString()));

        ll_bottom_tab_menu_item_one.setPressed(currentSelectedTab == Integer.parseInt(ll_bottom_tab_menu_item_one.getTag().toString()));

        ll_bottom_tab_menu_item_two.setPressed(currentSelectedTab == Integer.parseInt(ll_bottom_tab_menu_item_two.getTag().toString()));

        ll_bottom_tab_menu_item_three.setPressed(currentSelectedTab == Integer.parseInt(ll_bottom_tab_menu_item_three.getTag().toString()));

        ll_bottom_tab_menu_item_four.setPressed(currentSelectedTab == Integer.parseInt(ll_bottom_tab_menu_item_four.getTag().toString()));

        sortMenuItems();

    }


    @SuppressLint("GestureBackNavigation")
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            if (currentSelectedTab == Constants.BOTTOM_TAB_SELECTED_PLAN_TRIP && planTripFragment != null
                    && planTripFragment.backKeyHandler()) {
                return true;
            } else if (currentSelectedTab == Constants.BOTTOM_TAB_SELECTED_MY_TRIPS && myTripsFragment != null
                    && myTripsFragment.backKeyHandler()) {
                return true;
            } else {
                if (backKeyHistory.size() > 1) {
                    backKeyHistory.remove(backKeyHistory.size() - 1);

                    if (Utilities.isLoggedIn()) {
                        currentSelectedTab = backKeyHistory.get(backKeyHistory.size() - 1);
                        refreshBottomTabs();
                        if (Utilities.checkIfToDisplayAccessibilityUI(getApplicationContext())) {
                            clickAccessibilityBottomTabAction();
                        }
                        clickTabAction();
                        return true;
                    } else {
                        for (int i = backKeyHistory.size() - 1; i >= 0; i--) {
                            if (backKeyHistory.get(i) != Constants.BOTTOM_TAB_SELECTED_MY_TRIPS &&
                                    backKeyHistory.get(i) != currentSelectedTab) {
                                currentSelectedTab = backKeyHistory.get(i);
                                refreshBottomTabs();
                                if (Utilities.checkIfToDisplayAccessibilityUI(getApplicationContext())) {
                                    clickAccessibilityBottomTabAction();
                                }
                                clickTabAction();
                                return true;
                            } else {
                                backKeyHistory.remove(i);
                            }
                        }
                    }
                }
            }
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                Log.d("map", "Granted location permission");
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (currentSelectedTab == Constants.BOTTOM_TAB_SELECTED_PLAN_TRIP && planTripFragment != null) {
                        planTripFragment.startLocationListener();
                    }
                }

                if (planTripFragment != null) {
                    planTripFragment.requestPushPermission();
                }
            }
        }
    }

    private void getJmapTriggerableLocations() {
        String url = Configurations.JMAP_LOCATIONS_URL;
        ServerDelegate.simpleVolleyStringRequest(Request.Method.GET, url, "", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response != null) {
                    MyApplication.getInstance().setJmapTriggerableLocations(response);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Logger.e("getJmapTriggerableLocations error:", error.toString());
            }
        });
    }

    private void getIndoorMainEntrances() {
        String url = Configurations.INDOOR_MAIN_ENTRANCE_POPUP_URL;
        if (!url.isEmpty()) {
            ServerDelegate.simpleVolleyStringRequest(Request.Method.GET, url, "", new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    if (response != null) {
                        MyApplication.getInstance().setIndoorMainEntrancesInfo(response);
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Logger.e("getIndoorMainEntrances error:", error.toString());
                }
            });
        }
    }

    private void getIndoorEntranceExitList() {
        String url = Configurations.getString("indoor_entrance_exit_list_url", Configurations.INDOOR_ENTRANCE_EXIT_URL);
        ServerDelegate.simpleVolleyStringRequest(Request.Method.GET, url, "", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response != null) {
                    MyApplication.getInstance().setIndoorEntranceExits(response);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Logger.e("getIndoorEntranceExitList error:", error.toString());
            }
        });
    }

    private void getDependantMobilityProfilesIfNeeded() {
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
        if (Utilities.isLoggedIn() && userPreferences != null && userPreferences.getDependents() != null
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

    public FrameLayout getFragmentHolder() {
        return findViewById(R.id.fl_container);
    }
}