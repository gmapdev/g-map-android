package com.ibigroup.mobile.otp.android;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ibigroup.mobile.otp.android.api.Resource;
import com.ibigroup.mobile.otp.android.json.LocationPoint;
import com.ibigroup.mobile.otp.android.utility.Configurations;
import com.ibigroup.mobile.otp.android.utility.Constants;
import com.ibigroup.mobile.otp.android.utility.ServerDelegate;
import com.ibigroup.mobile.otp.android.utility.Utilities;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class AddressSearchActivity extends BaseActivity {
    private ImageView ivLogo, ivMenu, ivBack;
    private TextView tvTitle, tvSignIn;
    private boolean isOrigin = true;
    private boolean currentLocationAvailable = false;
    private String address;
    private ImageView ivFromTo, ivClear;
    private AutoCompleteTextView atSearch;
    private ImageView ivCurrentLocation;
    private TextView tvCurrentLocation;
    private PlacesAutoCompleteAdapter placesAutoCompleteAdapter;
    private ListView lvAutoComplete;
    private AutoCompleteListAdapter autoCompleteListAdapter;
    private ArrayList<LocationPoint> autoCompleteResultList = new ArrayList<LocationPoint>();
    private StringRequest autocompleteStringRequest = null;

    private TextView tvRecentlySearched;
    private ListView lvRecentlySearched;
    private RecentlySearchedListAdapter recentlySearchedListAdapter;
    private ArrayList<LocationPoint> recentlySearchedList = new ArrayList<LocationPoint>();
    private boolean isAddingLocation = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_search);

        Intent intent = getIntent();
        if (intent != null) {
            isOrigin = intent.getBooleanExtra(Constants.IS_ORIGIN, true);
            currentLocationAvailable = intent.getBooleanExtra(Constants.CURRENT_LOCATION_AVAILABLE, false);
            address = intent.getStringExtra(Constants.ADDRESS);

            isAddingLocation = intent.getBooleanExtra(Constants.IS_ADDING_LOCATION, false);
        }
        setupHeader();
        setupAddressSearchWidgets();
        setupCurrentLocationWidgets();
        setupRecentlySearched();
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

    private void initHeaderViews() {
        Utilities.setFont(this, tvTitle, false);
        Utilities.setFont(this, tvSignIn, false);

        ivLogo.setVisibility(View.GONE);
        ivMenu.setVisibility(View.GONE);
        ivBack.setVisibility(View.VISIBLE);

        if (isAddingLocation) {
            tvTitle.setText(getString(R.string.select_from_location));
        } else {
            if (isOrigin) {
                tvTitle.setText(getString(R.string.set_origin));
            } else {
                tvTitle.setText(getString(R.string.set_destination));
            }
        }
    }

    private void setupHeader() {
        ivLogo = findViewById(R.id.iv_logo);
        ivMenu = findViewById(R.id.iv_menu);
        ivBack = findViewById(R.id.iv_back);
        tvTitle = findViewById(R.id.tv_title);
        tvSignIn = findViewById(R.id.tv_sign_in);
        tvSignIn.setVisibility(View.GONE);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });
        initHeaderViews();
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

        if (isAddingLocation || isOrigin) {
            ivFromTo.setImageResource(R.drawable.ic_start_trip);
        } else {
            ivFromTo.setImageResource(R.drawable.ic_end);
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
        setResult(RESULT_OK, intent);
        finish();
    }

    private void setupAddressSearchWidgets() {
        ivFromTo = findViewById(R.id.iv_from_to);
        ivClear = findViewById(R.id.iv_clear);
        atSearch = findViewById(R.id.at_search);
        placesAutoCompleteAdapter = new PlacesAutoCompleteAdapter(this, R.layout.item_empty);

        atSearch.setThreshold(0);
        atSearch.setAdapter(placesAutoCompleteAdapter);
        autoCompleteListAdapter = getAutoCompleteListAdapter();
        lvAutoComplete = findViewById(R.id.lv_auto_complete);
        lvAutoComplete.setAdapter(autoCompleteListAdapter);

        ivClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goBackHomePageWithData("", 0, 0);
            }
        });
        initAddressSearchWidgets();
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
        ivCurrentLocation = findViewById(R.id.iv_current_location);
        tvCurrentLocation = findViewById(R.id.tv_current_location);

        tvCurrentLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentLocationAvailable) {
                    goBackHomePageWithData(getString(R.string.current_location_address), 0, 0);
                }
            }
        });

        initCurrentLocationWidgets();
    }

    private void clearAutoCompleteList() {
        autoCompleteResultList.clear();
        if (autoCompleteListAdapter != null) {
            autoCompleteListAdapter.notifyDataSetChanged();
        }
        Utilities.setTotalHeightToListView(lvAutoComplete);
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
                        getAutocompleteResult(constraint.toString());
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

    private void getAutocompleteResult(String input) {
        String url;

        if (MyApplication.getInstance().currentLocation != null) {
            double focusPointLat = MyApplication.getInstance().currentLocation.getLatitude();
            double focusPointLng = MyApplication.getInstance().currentLocation.getLongitude();
            url = String.format(Locale.getDefault(), Resource.API_AUTOCOMPLETE, input, Configurations.MIN_LATITUDE
                    , Configurations.MIN_LONGITUDE, Configurations.MAX_LATITUDE, Configurations.MAX_LONGITUDE, focusPointLat, focusPointLng);
        } else {
            url = String.format(Locale.getDefault(), Resource.API_AUTOCOMPLETE_WITHOUT_FOCUS_POINT, input, Configurations.MIN_LATITUDE
                    , Configurations.MIN_LONGITUDE, Configurations.MAX_LATITUDE, Configurations.MAX_LONGITUDE);
        }

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
                        for (int i = 0; (i < predsJsonArray.length() && i < Constants.MAX_AUTOCOMPLETE_RESULT_NUM); i++) {
                            JSONObject predsJSONObject = predsJsonArray.getJSONObject(i);
                            LocationPoint address = new LocationPoint();

                            JSONObject geometryJSONObject = predsJSONObject.getJSONObject("geometry");
                            JSONObject propertiesJSONObject = predsJSONObject.getJSONObject("properties");
                            JSONArray coordinatesJSONArray = geometryJSONObject.getJSONArray("coordinates");

                            address.setAddress(propertiesJSONObject.getString("label"));

                            if (coordinatesJSONArray.length() > 0) {
                                address.setLon(coordinatesJSONArray.getDouble(0));
                            }
                            if (coordinatesJSONArray.length() > 1) {
                                address.setLat(coordinatesJSONArray.getDouble(1));
                            }
                            autoCompleteResultList.add(address);
                        }

                        if (autoCompleteListAdapter != null) {
                            autoCompleteListAdapter.notifyDataSetChanged();
                        }
                        Utilities.setTotalHeightToListView(lvAutoComplete);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }, null);
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
                this, data,
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

    private RecentlySearchedListAdapter getRecentlySearchedListAdapter() {
        ArrayList<HashMap<String, Object>> data = new ArrayList<HashMap<String, Object>>();

        for (int i = 0; i < recentlySearchedList.size(); i++) {
            LocationPoint address = recentlySearchedList.get(i);

            HashMap<String, Object> map = new HashMap<String, Object>();

            map.put("tv_address", address.getAddress());

            data.add(map);
        }

        RecentlySearchedListAdapter mRecentlySearchedListAdapter = new RecentlySearchedListAdapter(
                this, data,
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
            tvRecentlySearched.setVisibility(View.VISIBLE);
            lvRecentlySearched.setVisibility(View.VISIBLE);
        }
        recentlySearchedListAdapter = getRecentlySearchedListAdapter();
        lvRecentlySearched.setAdapter(recentlySearchedListAdapter);
    }


    private void setupRecentlySearched() {
        tvRecentlySearched = findViewById(R.id.tv_recently_searched);
        lvRecentlySearched = findViewById(R.id.lv_recently_searched);

        initRecentlySearched();
    }
}