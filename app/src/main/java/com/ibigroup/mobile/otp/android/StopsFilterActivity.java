package com.ibigroup.mobile.otp.android;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ibigroup.mobile.otp.android.json.AddedLocation;
import com.ibigroup.mobile.otp.android.json.TransitStop;
import com.ibigroup.mobile.otp.android.json.UserPreferences;
import com.ibigroup.mobile.otp.android.json.itinerary.ItineraryLegPoint;
import com.ibigroup.mobile.otp.android.utility.Configurations;
import com.ibigroup.mobile.otp.android.utility.Constants;
import com.ibigroup.mobile.otp.android.utility.Utilities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StopsFilterActivity extends BaseActivity {
    private ListView lvFavStops, lvFilteredStops;
    private FavStopsListAdapter favStopsListAdapter;
    private TransitStopsListAdapter transitStopsListAdapter;
    private TextView tvStopsFound, tvMyFavoriteStops;
    private final ArrayList<AddedLocation> favStops = new ArrayList<>();
    private final ArrayList<TransitStop> transitStops = new ArrayList<>();
    private EditText etSearchAStop;
    private ItineraryLegPoint stopViewerItineraryLegPoint = null;

    @Override
    protected void onResume() {
        super.onResume();

        String credentialString = Configurations.getString("account_credentials", "");

        if (credentialString != null && !credentialString.isEmpty()) {
            tvMyFavoriteStops.setVisibility(View.VISIBLE);
            lvFavStops.setVisibility(View.VISIBLE);

            generateFavStops();
            if (favStopsListAdapter != null) {
                favStopsListAdapter.notifyDataSetChanged();
            }
            Utilities.setTotalHeightToListView(lvFavStops);
        } else {
            tvMyFavoriteStops.setVisibility(View.GONE);
            lvFavStops.setVisibility(View.GONE);
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

    private void showKeyboard() {
        if (etSearchAStop != null) {
            etSearchAStop.requestFocus();
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(etSearchAStop, InputMethodManager.SHOW_FORCED);
        }
    }

    private void initTransitStops() {
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

    private void setupTransitStops() {
        lvFilteredStops = findViewById(R.id.lv_filtered_stops);
        transitStopsListAdapter = getTransitStopsListAdapter();
        lvFilteredStops.setAdapter(transitStopsListAdapter);

        View filteredStopsHeaderView = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE)).
                inflate(R.layout.filter_stops_header, lvFilteredStops, false);
        tvStopsFound = filteredStopsHeaderView.findViewById(R.id.tv_stops_found);
        lvFilteredStops.addHeaderView(filteredStopsHeaderView, null, false);

        View footerView = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE)).
                inflate(R.layout.filter_stops_footer, null, false);
        lvFavStops = footerView.findViewById(R.id.lv_fav_stops);
        tvMyFavoriteStops = footerView.findViewById(R.id.tv_my_favorite_stops);
        favStopsListAdapter = getFavStopsListAdapter();
        lvFavStops.setAdapter(favStopsListAdapter);
        lvFilteredStops.addFooterView(footerView);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stops_filter);

        etSearchAStop = findViewById(R.id.et_search_a_stop);
        initTransitStops();

        setupTransitStops();
        filterStops();

        etSearchAStop.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterStops();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        ImageView ivBack = findViewById(R.id.iv_back);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        showKeyboard();
    }

    private void updateUi(ArrayList<TransitStop> newTransitStops) {
        transitStops.clear();
        transitStops.addAll(newTransitStops);

        if (transitStops.size() == 0) {
            tvStopsFound.setText(getString(R.string.no_stops_found));
        } else {
            tvStopsFound.setText(getString(R.string.stops_found, transitStops.size()));
        }

        if (transitStopsListAdapter != null) {
            transitStopsListAdapter.notifyDataSetChanged();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void filterStops() {
        if (etSearchAStop != null && etSearchAStop.getText().length() >= 2) {
            String filterString = etSearchAStop.getText().toString();
            if (MyApplication.getInstance().transitStops == null) {
                initTransitStops();
            }

            ArrayList<TransitStop> newTransitStops = new ArrayList<>();
            for (TransitStop transitStop : MyApplication.getInstance().transitStops) {
                if (transitStop != null) {
                    if (transitStop.getName() != null && transitStop.getName().contains(filterString)) {
                        newTransitStops.add(transitStop);
                    } else {
                        String stopID = Utilities.getStopId(transitStop);
                        if (stopID != null && stopID.contains(filterString)) {
                            newTransitStops.add(transitStop);
                        }
                    }
                }
            }
            updateUi(newTransitStops);
            tvStopsFound.setVisibility(View.VISIBLE);
        } else {
            tvStopsFound.setVisibility(View.GONE);

            transitStops.clear();
            if (transitStopsListAdapter != null) {
                transitStopsListAdapter.notifyDataSetChanged();
            }
        }
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

    private FavStopsListAdapter getFavStopsListAdapter() {
        ArrayList<HashMap<String, Object>> data = new ArrayList<HashMap<String, Object>>();

        for (int i = 0; i < favStops.size(); i++) {
            AddedLocation addedLocation = favStops.get(i);

            HashMap<String, Object> map = new HashMap<String, Object>();

            map.put("tv_stop_name", addedLocation.getName());
            data.add(map);
        }

        FavStopsListAdapter mFavStopsListAdapter = new FavStopsListAdapter(
                this, data,
                R.layout.item_filter_fav_stop,
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
                        R.layout.item_filter_fav_stop, null);
            }

            if (addedLocation != null) {
                TextView tvStopID = convertView.findViewById(R.id.tv_stop_id);
                TextView tvStopName = convertView.findViewById(R.id.tv_stop_name);
                ImageView ivFavStopIcon = convertView.findViewById(R.id.iv_fav_stop_icon);

                TransitStop transitStop = Utilities.getStopInfo(addedLocation);

                if (transitStop != null) {
                    tvStopID.setText(getString(R.string.bold_stop_id_with_num, Utilities.getStopId(transitStop)));
                }

                ivFavStopIcon.setColorFilter(getResources().getColor(android.R.color.black));

                tvStopName.setText(addedLocation.getName());

                convertView.setTag(transitStop);
                convertView.setOnClickListener(new View.OnClickListener() {
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

                            enterStopViewerPage(null);
                        }
                    }
                });
            }
            return convertView;
        }
    }

    private TransitStopsListAdapter getTransitStopsListAdapter() {
        ArrayList<HashMap<String, Object>> data = new ArrayList<HashMap<String, Object>>();

        for (int i = 0; i < transitStops.size(); i++) {
            TransitStop transitStop = transitStops.get(i);

            HashMap<String, Object> map = new HashMap<String, Object>();

            map.put("tv_stop_name", transitStop.getName());
            data.add(map);
        }

        TransitStopsListAdapter mTransitStopsListAdapter = new TransitStopsListAdapter(
                this, data,
                R.layout.item_filter_stop,
                new String[]{"tv_stop_name"}, new int[]{R.id.tv_stop_name});

        return mTransitStopsListAdapter;
    }

    class TransitStopsListAdapter extends SimpleAdapter {
        Context mContext;
        private final LayoutInflater l_Inflater;

        public TransitStopsListAdapter(Context context, List<? extends Map<String, ?>> data,
                                       int resource, String[] from, int[] to) {
            super(context, data, resource, from, to);

            mContext = context;
            l_Inflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return transitStops.size();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TransitStop transitStop = transitStops.get(position);

            if (convertView == null) {
                convertView = l_Inflater.inflate(
                        R.layout.item_filter_stop, null);
            }

            if (transitStop != null) {
                TextView tvStopID = convertView.findViewById(R.id.tv_stop_id);
                TextView tvStopName = convertView.findViewById(R.id.tv_stop_name);
                ImageView ivFavStopIcon = convertView.findViewById(R.id.iv_fav_stop_icon);

                tvStopID.setText(getString(R.string.bold_stop_id_with_num, Utilities.getStopId(transitStop)));

                ivFavStopIcon.setColorFilter(getResources().getColor(android.R.color.black));

                tvStopName.setText(transitStop.getName());

                convertView.setTag(transitStop);
                convertView.setOnClickListener(new View.OnClickListener() {
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

                            enterStopViewerPage(null);
                        }
                    }
                });
            }
            return convertView;
        }
    }
}