package com.ibigroup.mobile.otp.android;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.ibigroup.mobile.otp.android.api.Resource;
import com.ibigroup.mobile.otp.android.json.RouteViewerRoute;
import com.ibigroup.mobile.otp.android.utility.Configurations;
import com.ibigroup.mobile.otp.android.utility.Constants;
import com.ibigroup.mobile.otp.android.utility.Convert;
import com.ibigroup.mobile.otp.android.utility.Utilities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class RouteViewerFilterActivity extends BaseActivity {
    private ListView lvRouteViewerRoutes;
    private RouteViewerRouteListAdapter routeViewerRouteListAdapter;

    private RouteViewerRoute currentRouteViewerRoute = null;

    private ArrayList<RouteViewerRoute> routeViewerAllRoutes = new ArrayList<>();
    private final ArrayList<RouteViewerRoute> routeViewerSubRoutes = new ArrayList<>();
    private final ArrayList<RouteViewerRoute> routeViewerRoutes = new ArrayList<>();
    private EditText etSearchARoute;
    private TextView tvRouteFilterCount;

    private RelativeLayout rl_layout_route_filter_agency, rl_layout_route_filter_mode;
    private LinearLayout ll_route_filter_agency_selection, ll_route_filter_mode_selection;
    private TextView tv_route_filter_agency_selection, tv_route_filter_mode_selection,
            tv_route_filter_agency_ok, tv_route_filter_mode_ok;
    private ImageView iv_filter_agency_changed, iv_filter_mode_changed;
    private int selectedRouteFilterAgencyIndex = 0, selectedRouteFilterModeIndex = 0;
    private int selectedRouteFilterAgencyIndexTemp = 0, selectedRouteFilterModeIndexTemp = 0;
    private final ArrayList<String> routeFilterAgencies = new ArrayList<>();
    private final ArrayList<String> routeFilterModes = new ArrayList<>();
    private ListView lvRouteFilterAgency, lvRouteFilterMode;
    private RouteFilterAgencyListAdapter routeFilterAgencyListAdapter;
    private RouteFilterModeListAdapter routeFilterModeListAdapter;
    private DisplayMetrics gDm = new DisplayMetrics();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Utilities.checkIfToDisplayAccessibilityUI(this)) {
            setContentView(R.layout.activity_route_viewer_filter_accessibility);
        } else {
            setContentView(R.layout.activity_route_viewer_filter);
        }
        gDm = new DisplayMetrics();
        Convert.mDensity = getResources().getDisplayMetrics().density;
        getWindowManager().getDefaultDisplay().getMetrics(gDm);

        etSearchARoute = findViewById(R.id.et_search_a_route);

        routeViewerAllRoutes = MyApplication.getInstance().routeViewerRoutes;

        lvRouteViewerRoutes = findViewById(R.id.lv_route_viewer_routes);
        routeViewerRouteListAdapter = getRouteViewerRouteListAdapter();
        lvRouteViewerRoutes.setAdapter(routeViewerRouteListAdapter);
        tvRouteFilterCount = findViewById(R.id.tv_route_filter_count);

        etSearchARoute.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                refreshRouteViewerRoutes();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        if (Utilities.checkIfToDisplayAccessibilityUI(this)) {
            LinearLayout ll_itinerary_back_accessibility = findViewById(R.id.ll_itinerary_back_accessibility);
            ll_itinerary_back_accessibility.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                }
            });

        } else {
            ImageView ivBack = findViewById(R.id.iv_back);
            ivBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }


        showKeyboard();

        initFilterAgencies();
        setupRouteFilterPopup();
        setupRouteFilterAgencyPopup();
        setupRouteFilterModePopup();
        initFilterAndRefresh();
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

    private void initFilterAndRefresh() {
        selectedRouteFilterAgencyIndex = Configurations.getInt(Constants.ROUTE_FILTER_AGENCY_KEY, 0);
        selectedRouteFilterModeIndex = Configurations.getInt(Constants.ROUTE_FILTER_MODE_KEY, 0);

        if (selectedRouteFilterAgencyIndex == 0) {
            iv_filter_agency_changed.setVisibility(View.GONE);
        } else {
            iv_filter_agency_changed.setVisibility(View.VISIBLE);
        }

        if (selectedRouteFilterModeIndex == 0) {
            iv_filter_mode_changed.setVisibility(View.GONE);
        } else {
            iv_filter_mode_changed.setVisibility(View.VISIBLE);
        }

        if (tv_route_filter_agency_selection != null) {
            String selectedAgency = null;
            if (selectedRouteFilterAgencyIndex < routeFilterAgencies.size()) {
                selectedAgency = routeFilterAgencies.get(selectedRouteFilterAgencyIndex);
            }
            if (selectedAgency != null) {
                tv_route_filter_agency_selection.setText(Utilities.getMappedAgencyName(RouteViewerFilterActivity.this, selectedAgency));
            } else {
                tv_route_filter_agency_selection.setText(getString(R.string.all_agencies));
            }
        }
        refreshFilterModes();
        refreshRouteViewerRoutes();

        if (routeFilterModeListAdapter != null) {
            routeFilterModeListAdapter.notifyDataSetInvalidated();
        }
    }

    private void showKeyboard() {
        if (etSearchARoute != null) {
            etSearchARoute.requestFocus();
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(etSearchARoute, InputMethodManager.SHOW_FORCED);
        }
    }

    private void hideKeyboard() {
        if (etSearchARoute != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(etSearchARoute.getWindowToken(), 0);
        }
    }

    private void filterRoutes() {
        if (etSearchARoute != null && etSearchARoute.getText().length() > 0) {
            String filterString = etSearchARoute.getText().toString().toLowerCase();
            //ignore route/Route
            filterString = filterString.replaceAll("route", "").trim();
            routeViewerSubRoutes.clear();
            for (RouteViewerRoute routeViewerRoute : routeViewerRoutes) {
                if (routeViewerRoute != null) {
                    if (routeViewerRoute.getShortName() != null && routeViewerRoute.getShortName().toLowerCase().contains(filterString)) {
                        routeViewerSubRoutes.add(routeViewerRoute);
                    } else if (routeViewerRoute.getLongName() != null && routeViewerRoute.getLongName().toLowerCase().contains(filterString)) {
                        routeViewerSubRoutes.add(routeViewerRoute);
                    } else if (routeViewerRoute.getAgencyName() != null) {
                        if (routeViewerRoute.getAgencyName().toLowerCase().contains(filterString) ||
                                Utilities.getMappedAgencyName(this, routeViewerRoute.getAgencyName()).toLowerCase().contains(filterString)) {
                            routeViewerSubRoutes.add(routeViewerRoute);
                        }
                    }
                }
            }
        } else {
            routeViewerSubRoutes.clear();
            routeViewerSubRoutes.addAll(routeViewerRoutes);
        }

        String routesFound;
        if (routeViewerSubRoutes.size() <= 1) {
            routesFound = getString(R.string.routes_found, routeViewerSubRoutes.size());
        } else {
            routesFound = getString(R.string.routes_found, routeViewerSubRoutes.size());
        }
        tvRouteFilterCount.setText(routesFound);
    }

    private RouteViewerRouteListAdapter getRouteViewerRouteListAdapter() {
        ArrayList<HashMap<String, Object>> data = new ArrayList<HashMap<String, Object>>();

        for (int i = 0; i < routeViewerSubRoutes.size(); i++) {
            RouteViewerRoute routeViewerRoute = routeViewerSubRoutes.get(i);

            HashMap<String, Object> map = new HashMap<String, Object>();

            map.put("tv_route", routeViewerRoute.getLongName());
            data.add(map);
        }

        RouteViewerRouteListAdapter mRouteViewerRouteListAdapter = new RouteViewerRouteListAdapter(
                this, data,
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
            return routeViewerSubRoutes.size();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            RouteViewerRoute routeViewerRoute = routeViewerSubRoutes.get(position);
            if (convertView == null) {
                if (Utilities.checkIfToDisplayAccessibilityUI(mContext)) {
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
                LinearLayout llRouteAgency = convertView.findViewById(R.id.ll_route_agency);
                TextView tvRouteAgencyInfo = convertView.findViewById(R.id.tv_route_agency_info);

                tvRouteAgencyInfo.setVisibility(View.GONE);
                llRouteAgency.setVisibility(View.GONE);
                tvRouteDescription.setVisibility(View.GONE);
                convertView.setBackground(null);

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
                    tvRouteName.setText(String.format("%s %s", getString(R.string.route), tvRoute.getText().toString()));
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
                        if (!description.equals(getString(R.string.no_route_url_provided))) {
                            Utilities.GotoTheUrl(v.getContext(), description);
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
                } else {
                    ivLogo.setVisibility(View.GONE);
                }

                if (routeViewerRoute.getMode() != null) {
                    int resID = Utilities.getModeImageByMode(routeViewerRoute.getMode().toLowerCase(Locale.ROOT), false, null, null, null);
                    ivRouteType.setImageResource(resID);
                } else {
                    ivRouteType.setImageResource(R.drawable.ic_bus);
                }
                convertView.setTag(position);
                convertView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int position = (Integer) v.getTag();

                        if (position < routeViewerSubRoutes.size()) {
                            currentRouteViewerRoute = routeViewerSubRoutes.get(position);

                            Intent intent = new Intent();
                            intent.putExtra(Constants.SELECTED_ROUTE, currentRouteViewerRoute);
                            setResult(RESULT_OK, intent);
                            finish();
                        }
                    }
                });
            }
            return convertView;
        }
    }


    private RouteFilterAgencyListAdapter getRouteFilterAgencyListAdapter() {
        ArrayList<HashMap<String, Object>> data = new ArrayList<HashMap<String, Object>>();

        for (int i = 0; i < routeFilterAgencies.size(); i++) {
            String agencyName = routeFilterAgencies.get(i);

            HashMap<String, Object> map = new HashMap<String, Object>();

            map.put("tv_filter_name", agencyName);

            data.add(map);
        }

        RouteFilterAgencyListAdapter mRouteFilterAgencyListAdapter = new RouteFilterAgencyListAdapter(
                this, data,
                R.layout.item_route_filter_list,
                new String[]{"tv_filter_name"}, new int[]{R.id.tv_filter_name});

        return mRouteFilterAgencyListAdapter;
    }

    class RouteFilterAgencyListAdapter extends SimpleAdapter {
        Context mContext;
        private final LayoutInflater l_Inflater;

        public RouteFilterAgencyListAdapter(Context context, List<? extends Map<String, ?>> data,
                                            int resource, String[] from, int[] to) {
            super(context, data, resource, from, to);

            mContext = context;
            l_Inflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return routeFilterAgencies.size();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            String agencyName = routeFilterAgencies.get(position);

            if (convertView == null) {
                convertView = l_Inflater.inflate(
                        R.layout.item_route_filter_list, null);
            }

            TextView tvFilterName = convertView.findViewById(R.id.tv_filter_name);
            ImageView ivFilterSelected = convertView.findViewById(R.id.iv_filter_selected);

            tvFilterName.setText(Utilities.getMappedAgencyName(RouteViewerFilterActivity.this, agencyName));

            if (position == selectedRouteFilterAgencyIndexTemp) {
                ivFilterSelected.setVisibility(View.VISIBLE);
            } else {
                ivFilterSelected.setVisibility(View.INVISIBLE);
            }

            convertView.setTag(position);
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = (Integer) view.getTag();
                    if (position < routeFilterAgencies.size()) {
                        selectedRouteFilterAgencyIndexTemp = position;
                    }

                    if (routeFilterAgencyListAdapter != null) {
                        routeFilterAgencyListAdapter.notifyDataSetChanged();
                    }
                }
            });
            return convertView;
        }
    }

    private RouteFilterModeListAdapter getRouteFilterModeListAdapter() {
        ArrayList<HashMap<String, Object>> data = new ArrayList<HashMap<String, Object>>();

        for (int i = 0; i < routeFilterModes.size(); i++) {
            String mode = routeFilterModes.get(i);

            HashMap<String, Object> map = new HashMap<String, Object>();

            map.put("tv_filter_name", mode);

            data.add(map);
        }

        RouteFilterModeListAdapter mRouteFilterModeListAdapter = new RouteFilterModeListAdapter(
                this, data,
                R.layout.item_route_filter_list,
                new String[]{"tv_filter_name"}, new int[]{R.id.tv_filter_name});

        return mRouteFilterModeListAdapter;
    }

    class RouteFilterModeListAdapter extends SimpleAdapter {
        Context mContext;
        private final LayoutInflater l_Inflater;

        public RouteFilterModeListAdapter(Context context, List<? extends Map<String, ?>> data,
                                          int resource, String[] from, int[] to) {
            super(context, data, resource, from, to);

            mContext = context;
            l_Inflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return routeFilterModes.size();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            String mode = routeFilterModes.get(position);

            if (convertView == null) {
                convertView = l_Inflater.inflate(
                        R.layout.item_route_filter_list, null);
            }

            TextView tvFilterName = convertView.findViewById(R.id.tv_filter_name);
            ImageView ivFilterSelected = convertView.findViewById(R.id.iv_filter_selected);

            if (Utilities.routeFilterModesStringMapping.containsKey(mode.toLowerCase(Locale.ROOT))) {
                mode = getString(Utilities.routeFilterModesStringMapping.get(mode.toLowerCase(Locale.ROOT)));
            }
            tvFilterName.setText(mode);

            if (position == selectedRouteFilterModeIndexTemp) {
                ivFilterSelected.setVisibility(View.VISIBLE);
            } else {
                ivFilterSelected.setVisibility(View.INVISIBLE);
            }

            convertView.setTag(position);
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = (Integer) view.getTag();
                    if (position < routeFilterModes.size()) {
                        selectedRouteFilterModeIndexTemp = position;
                    }

                    if (routeFilterModeListAdapter != null) {
                        routeFilterModeListAdapter.notifyDataSetChanged();
                    }
                }
            });

            return convertView;
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

            if (selectedRouteFilterModeIndex < routeFilterModes.size()) {
                selectedMode = routeFilterModes.get(selectedRouteFilterModeIndex);
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

            //check what's the current mode index if exist
            boolean found = false;
            if (selectedMode != null) {
                for (int i = 0; i < routeFilterModes.size(); i++) {
                    if (routeFilterModes.get(i).equals(selectedMode)) {
                        selectedRouteFilterModeIndex = i;
                        found = true;
                        break;
                    }
                }
            }
            if (found && selectedRouteFilterModeIndex < routeFilterModes.size()) {
                selectedMode = routeFilterModes.get(selectedRouteFilterModeIndex);
                if (Utilities.routeFilterModesStringMapping.containsKey(selectedMode.toLowerCase(Locale.ROOT))) {
                    selectedMode = getString(Utilities.routeFilterModesStringMapping.get(selectedMode.toLowerCase(Locale.ROOT)));
                }
                tv_route_filter_mode_selection.setText(selectedMode);
            } else {
                selectedRouteFilterModeIndex = 0;
                tv_route_filter_mode_selection.setText(getString(R.string.all_modes));
            }
        }
    }

    private void setupRouteFilterPopup() {
        ll_route_filter_agency_selection = findViewById(R.id.ll_route_filter_agency_selection);
        ll_route_filter_mode_selection = findViewById(R.id.ll_route_filter_mode_selection);

        tv_route_filter_agency_selection = findViewById(R.id.tv_route_filter_agency_selection);
        tv_route_filter_mode_selection = findViewById(R.id.tv_route_filter_mode_selection);

        iv_filter_agency_changed = findViewById(R.id.iv_filter_agency_changed);
        iv_filter_mode_changed = findViewById(R.id.iv_filter_mode_changed);

        tv_route_filter_agency_selection.setContentDescription(getString(R.string.agency_filter_button));
        tv_route_filter_mode_selection.setContentDescription(getString(R.string.mode_filter_button));

        ll_route_filter_agency_selection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideKeyboard();
                selectedRouteFilterAgencyIndexTemp = selectedRouteFilterAgencyIndex;
                rl_layout_route_filter_agency.setVisibility(View.VISIBLE);
            }
        });

        ll_route_filter_mode_selection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideKeyboard();
                selectedRouteFilterModeIndexTemp = selectedRouteFilterModeIndex;
                rl_layout_route_filter_mode.setVisibility(View.VISIBLE);
            }
        });
    }

    private void setupRouteFilterAgencyPopup() {
        rl_layout_route_filter_agency = findViewById(R.id.rl_layout_route_filter_agency);
        tv_route_filter_agency_ok = findViewById(R.id.tv_route_filter_agency_ok);
        lvRouteFilterAgency = findViewById(R.id.lv_route_filter_agency);

        tv_route_filter_agency_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String selectedAgency = null;
                selectedRouteFilterAgencyIndex = selectedRouteFilterAgencyIndexTemp;
                if (selectedRouteFilterAgencyIndex < routeFilterAgencies.size()) {
                    selectedAgency = routeFilterAgencies.get(selectedRouteFilterAgencyIndex);
                }
                if (selectedAgency != null) {
                    tv_route_filter_agency_selection.setText(Utilities.getMappedAgencyName(RouteViewerFilterActivity.this, selectedAgency));
                } else {
                    tv_route_filter_agency_selection.setText(getString(R.string.all_agencies));
                }
                refreshFilterModes();

                if (routeFilterModeListAdapter != null) {
                    routeFilterModeListAdapter.notifyDataSetInvalidated();
                }
                rl_layout_route_filter_agency.setVisibility(View.GONE);

                refreshRouteViewerRoutes();

                if (selectedRouteFilterAgencyIndex == 0) {
                    iv_filter_agency_changed.setVisibility(View.GONE);
                } else {
                    iv_filter_agency_changed.setVisibility(View.VISIBLE);
                }

                if (selectedRouteFilterModeIndex == 0) {
                    iv_filter_mode_changed.setVisibility(View.GONE);
                } else {
                    iv_filter_mode_changed.setVisibility(View.VISIBLE);
                }
                Configurations.setInt(Constants.ROUTE_FILTER_AGENCY_KEY, selectedRouteFilterAgencyIndex);
            }
        });

        TextView tv_route_filter_agency_cancel = findViewById(R.id.tv_route_filter_agency_cancel);
        tv_route_filter_agency_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedRouteFilterAgencyIndexTemp = selectedRouteFilterAgencyIndex;
                rl_layout_route_filter_agency.setVisibility(View.GONE);
                if (routeFilterAgencyListAdapter != null) {
                    routeFilterAgencyListAdapter.notifyDataSetInvalidated();
                }
            }
        });

        routeFilterAgencyListAdapter = getRouteFilterAgencyListAdapter();
        lvRouteFilterAgency.setAdapter(routeFilterAgencyListAdapter);

        Utilities.setTotalHeightToListView(lvRouteFilterAgency, gDm.heightPixels - Convert.dpToPx(215));

    }

    private void setupRouteFilterModePopup() {
        rl_layout_route_filter_mode = findViewById(R.id.rl_layout_route_filter_mode);
        tv_route_filter_mode_ok = findViewById(R.id.tv_route_filter_mode_ok);
        lvRouteFilterMode = findViewById(R.id.lv_route_filter_mode);

        tv_route_filter_mode_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String selectedMode = null;
                selectedRouteFilterModeIndex = selectedRouteFilterModeIndexTemp;
                if (selectedRouteFilterModeIndex < routeFilterModes.size()) {
                    selectedMode = routeFilterModes.get(selectedRouteFilterModeIndex);
                }
                if (selectedMode != null) {
                    if (Utilities.routeFilterModesStringMapping.containsKey(selectedMode.toLowerCase(Locale.ROOT))) {
                        selectedMode = getString(Utilities.routeFilterModesStringMapping.get(selectedMode.toLowerCase(Locale.ROOT)));
                    }
                    tv_route_filter_mode_selection.setText(selectedMode);
                } else {
                    tv_route_filter_mode_selection.setText(getString(R.string.all_modes));
                }
                rl_layout_route_filter_mode.setVisibility(View.GONE);

                refreshRouteViewerRoutes();
                if (selectedRouteFilterModeIndex == 0) {
                    iv_filter_mode_changed.setVisibility(View.GONE);
                } else {
                    iv_filter_mode_changed.setVisibility(View.VISIBLE);
                }

                Configurations.setInt(Constants.ROUTE_FILTER_MODE_KEY, selectedRouteFilterModeIndex);
            }
        });

        TextView tv_route_filter_mode_cancel = findViewById(R.id.tv_route_filter_mode_cancel);
        tv_route_filter_mode_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedRouteFilterModeIndexTemp = selectedRouteFilterModeIndex;
                rl_layout_route_filter_mode.setVisibility(View.GONE);
                if (routeFilterModeListAdapter != null) {
                    routeFilterModeListAdapter.notifyDataSetInvalidated();
                }
            }
        });

        routeFilterModeListAdapter = getRouteFilterModeListAdapter();
        lvRouteFilterMode.setAdapter(routeFilterModeListAdapter);

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
                    if (selectedRouteFilterAgencyIndex == 0 || selectedAgency.equals(routeViewerRoute.getAgencyName())) {
                        if (routeViewerRoute.getAgencyName().equalsIgnoreCase("Kitsap Transit")) {
                            if (selectedMode.equals("FERRY") && routeViewerRoute.getMode().equals("WATER_TAXI")) {
                                routeViewerRoutes.add(routeViewerRoute);
                            } else if (selectedRouteFilterModeIndex == 0
                                    || (!selectedMode.equals("WATER_TAXI") && selectedMode.equals(routeViewerRoute.getMode()))) {
                                routeViewerRoutes.add(routeViewerRoute);
                            }
                        } else {
                            if (selectedRouteFilterModeIndex == 0 || selectedMode.equals(routeViewerRoute.getMode())) {
                                routeViewerRoutes.add(routeViewerRoute);
                            }
                        }
                    }
                }
            }
        }

        filterRoutes();
        if (routeViewerRouteListAdapter != null) {
            routeViewerRouteListAdapter.notifyDataSetChanged();
        }
    }
}