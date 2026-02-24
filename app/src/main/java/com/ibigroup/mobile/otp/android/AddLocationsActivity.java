package com.ibigroup.mobile.otp.android;

import static com.ibigroup.mobile.otp.android.utility.ServerDelegate.volleyAccountStringRequest;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.ibigroup.mobile.otp.android.api.Resource;
import com.ibigroup.mobile.otp.android.json.AddedLocation;
import com.ibigroup.mobile.otp.android.json.UserPreferences;
import com.ibigroup.mobile.otp.android.utility.Constants;
import com.ibigroup.mobile.otp.android.utility.Logger;
import com.ibigroup.mobile.otp.android.utility.ServerDelegate;
import com.ibigroup.mobile.otp.android.utility.Utilities;
import com.mapbox.mapboxsdk.geometry.LatLng;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class AddLocationsActivity extends BaseActivity {
    private ImageView ivLogo, ivMenu, ivBack, ivClose;
    private TextView tvTitle, tvSignIn, tvAppName;
    private TextView tvBack, tvNext;
    private ListView lvAddedLocations;
    private AddedLocationListAdapter addedLocationListAdapter;
    private EditText etAddNewLocation;
    private TextView tvHomeAddress, tvWorkAddress, tvNewLocation, tvCancel, tvSave;
    private LinearLayout llCurrentLocations, llAddHomeAddress, llAddWorkAddress,
            llAddAnotherPlace, llAddNewLocation;

    private RelativeLayout rlDeleteHomeAddress, rlDeleteWorkAddress;
    private int searchAddressMode = Constants.SEARCH_ADDRESS_MODE_HOME;
    private StringRequest gpsReverseRequest = null;

    private DisplayMetrics gDm = new DisplayMetrics();
    private final ArrayList<AddedLocation> addedLocations = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_locations);
        gDm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(gDm);
        setupHeader();
        setupBottomBar();
        setupHomeAddressLayout();
        setupWorkAddressLayout();
        setupAddNewLocationLayout();
        setupAddedLocationsLayout();

        UserPreferences userPreferences = MyApplication.getInstance().userPreferences;
        if (userPreferences != null) {
            initMyLocations(userPreferences.getSavedLocations());
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

    private void initMyLocations(ArrayList<AddedLocation> savedLocations) {
        if (savedLocations != null && savedLocations.size() > 0) {
            for (AddedLocation addedLocation : savedLocations) {
                switch (addedLocation.getType()) {
                    case "home":
                        if (addedLocation.getAddress() != null) {
                            tvHomeAddress.setText(addedLocation.getAddress());
                        }
                        tvHomeAddress.setTag(new LatLng(addedLocation.getLat(), addedLocation.getLon()));
                        rlDeleteHomeAddress.setVisibility(View.VISIBLE);
                        break;

                    case "work":
                        if (addedLocation.getAddress() != null) {
                            tvWorkAddress.setText(addedLocation.getAddress());
                        }
                        tvWorkAddress.setTag(new LatLng(addedLocation.getLat(), addedLocation.getLon()));
                        rlDeleteWorkAddress.setVisibility(View.VISIBLE);
                        break;
                    case "custom":
                        addedLocations.add(addedLocation);
                        break;
                    default:
                        break;
                }
            }

            if (addedLocationListAdapter != null) {
                addedLocationListAdapter.notifyDataSetChanged();
            }
            Utilities.setTotalHeightToListView(lvAddedLocations);
        }
    }

    private void refreshAddress(String searchedAddress, LatLng latLng) {
        if (searchedAddress != null) {
            switch (searchAddressMode) {
                case Constants.SEARCH_ADDRESS_MODE_HOME:
                    tvHomeAddress.setText(searchedAddress);
                    tvHomeAddress.setTag(latLng);
                    if (searchedAddress.isEmpty()) {
                        rlDeleteHomeAddress.setVisibility(View.GONE);
                    } else {
                        rlDeleteHomeAddress.setVisibility(View.VISIBLE);
                    }
                    break;
                case Constants.SEARCH_ADDRESS_MODE_WORK:
                    tvWorkAddress.setText(searchedAddress);
                    tvWorkAddress.setTag(latLng);
                    if (searchedAddress.isEmpty()) {
                        rlDeleteWorkAddress.setVisibility(View.GONE);
                    } else {
                        rlDeleteWorkAddress.setVisibility(View.VISIBLE);
                    }

                    break;
                case Constants.SEARCH_ADDRESS_MODE_NEW:
                    tvNewLocation.setText(searchedAddress);
                    tvNewLocation.setTag(latLng);
                    break;
                default:
                    break;
            }
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

                            refreshAddress(address, new LatLng(lat, lon));
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
        Toast.makeText(AddLocationsActivity.this, getString(R.string.get_address_failed),
                Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == Constants.ADDRESS_SEARCH_REQUEST_CODE) {
                if (data != null) {
                    String searchedAddress = data.getStringExtra(Constants.SEARCHED_ADDRESS);
                    double lat = data.getDoubleExtra(Constants.SEARCHED_LATITUDE, 0.0);
                    double lon = data.getDoubleExtra(Constants.SEARCHED_LONGITUDE, 0.0);

                    if (searchedAddress != null && searchedAddress.equals(getString(R.string.current_location_address))) {
                        if (MyApplication.getInstance().currentLocation != null) {
                            lat = MyApplication.getInstance().currentLocation.getLatitude();
                            lon = MyApplication.getInstance().currentLocation.getLongitude();
                            getGpsReverseResult(lat, lon);
                        }
                    } else {
                        refreshAddress(searchedAddress, new LatLng(lat, lon));
                    }
                }
            }
        }
    }

    private void gotoAddressSearchPage(String address) {
        Intent intent = new Intent(AddLocationsActivity.this, AddressSearchActivity.class);
        intent.putExtra(Constants.IS_ADDING_LOCATION, true);
        intent.putExtra(Constants.CURRENT_LOCATION_AVAILABLE, MyApplication.getInstance().currentLocation != null);

        intent.putExtra(Constants.ADDRESS, address);
        startActivityForResult(intent, Constants.ADDRESS_SEARCH_REQUEST_CODE);
    }

    private void setupHomeAddressLayout() {
        llAddHomeAddress = findViewById(R.id.ll_add_home_address);
        tvHomeAddress = findViewById(R.id.tv_home_address);
        rlDeleteHomeAddress = findViewById(R.id.rl_delete_home_address);

        llAddHomeAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchAddressMode = Constants.SEARCH_ADDRESS_MODE_HOME;
                gotoAddressSearchPage(tvHomeAddress.getText().toString());
            }
        });

        rlDeleteHomeAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvHomeAddress.setText("");
                tvHomeAddress.setTag(null);
                rlDeleteHomeAddress.setVisibility(View.GONE);
            }
        });
    }

    private void setupWorkAddressLayout() {
        llAddWorkAddress = findViewById(R.id.ll_add_work_address);
        tvWorkAddress = findViewById(R.id.tv_work_address);
        rlDeleteWorkAddress = findViewById(R.id.rl_delete_work_address);

        llAddWorkAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchAddressMode = Constants.SEARCH_ADDRESS_MODE_WORK;
                gotoAddressSearchPage(tvWorkAddress.getText().toString());
            }
        });

        rlDeleteWorkAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvWorkAddress.setText("");
                tvWorkAddress.setTag(null);
                rlDeleteWorkAddress.setVisibility(View.GONE);
            }
        });
    }

    private void setupAddNewLocationLayout() {
        etAddNewLocation = findViewById(R.id.et_add_new_location);
        tvNewLocation = findViewById(R.id.tv_new_location);
        llAddNewLocation = findViewById(R.id.ll_add_new_location);
        llAddAnotherPlace = findViewById(R.id.ll_add_another_place);
        tvCancel = findViewById(R.id.tv_cancel);
        tvSave = findViewById(R.id.tv_save);

        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                llAddNewLocation.setVisibility(View.GONE);
                llCurrentLocations.setVisibility(View.VISIBLE);
                etAddNewLocation.setText("");
                tvNewLocation.setText("");
            }
        });

        llAddAnotherPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                llCurrentLocations.setVisibility(View.GONE);
                llAddNewLocation.setVisibility(View.VISIBLE);
            }
        });

        tvNewLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchAddressMode = Constants.SEARCH_ADDRESS_MODE_NEW;
                gotoAddressSearchPage(tvNewLocation.getText().toString());
            }
        });

        tvSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddedLocation addedLocation = new AddedLocation();
                addedLocation.setName(etAddNewLocation.getText().toString());
                addedLocation.setAddress(tvNewLocation.getText().toString());
                if (tvNewLocation.getTag() != null) {
                    LatLng latLng = (LatLng) tvNewLocation.getTag();
                    addedLocation.setLat(latLng.getLatitude());
                    addedLocation.setLon(latLng.getLongitude());
                }
                addedLocation.setType("custom");
                addedLocations.add(addedLocation);

                llAddNewLocation.setVisibility(View.GONE);
                llCurrentLocations.setVisibility(View.VISIBLE);

                if (addedLocationListAdapter != null) {
                    addedLocationListAdapter.notifyDataSetChanged();
                }
                Utilities.setTotalHeightToListView(lvAddedLocations);

                etAddNewLocation.setText("");
                tvNewLocation.setText("");
                tvNewLocation.setTag(null);

            }
        });
    }

    private AddedLocationListAdapter getAddedLocationListAdapter() {
        ArrayList<HashMap<String, Object>> data = new ArrayList<HashMap<String, Object>>();

        for (int i = 0; i < addedLocations.size(); i++) {
            AddedLocation addedLocation = addedLocations.get(i);

            HashMap<String, Object> map = new HashMap<String, Object>();

            map.put("tv_added_location_name", addedLocation.getName());

            data.add(map);
        }

        AddedLocationListAdapter mAddedLocationListAdapter = new AddedLocationListAdapter(
                this, data,
                R.layout.item_added_location,
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
            return addedLocations.size();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            AddedLocation addedLocation = addedLocations.get(position);

            if (convertView == null) {
                convertView = l_Inflater.inflate(
                        R.layout.item_added_location, null);
            }
            TextView tvAddedLocationName = convertView.findViewById(R.id.tv_added_location_name);
            TextView tvAddedLocationAddress = convertView.findViewById(R.id.tv_added_location_address);
            RelativeLayout rlDeleteLocation = convertView.findViewById(R.id.rl_delete_location);

            tvAddedLocationName.setText(addedLocation.getName());
            tvAddedLocationAddress.setText(addedLocation.getAddress());
            rlDeleteLocation.setTag(position);
            rlDeleteLocation.setContentDescription("delete" + addedLocation.getName());
            rlDeleteLocation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = (int) v.getTag();
                    if (position < addedLocations.size()) {
                        addedLocations.remove(position);
                        if (addedLocationListAdapter != null) {
                            addedLocationListAdapter.notifyDataSetChanged();
                        }
                        Utilities.setTotalHeightToListView(lvAddedLocations);
                    }
                }
            });
            return convertView;
        }
    }

    private void setupAddedLocationsLayout() {
        llCurrentLocations = findViewById(R.id.ll_current_locations);
        lvAddedLocations = findViewById(R.id.lv_added_locations);

        addedLocationListAdapter = getAddedLocationListAdapter();
        lvAddedLocations.setAdapter(addedLocationListAdapter);
        Utilities.setTotalHeightToListView(lvAddedLocations);
    }

    private void initHeaderViews() {
        Utilities.setFont(this, tvTitle, false);
        Utilities.setFont(this, tvSignIn, false);

        ivLogo.setVisibility(View.VISIBLE);
        ivMenu.setVisibility(View.GONE);
        ivBack.setVisibility(View.GONE);
        ivClose.setVisibility(View.VISIBLE);
        tvTitle.setVisibility(View.GONE);
        tvAppName.setVisibility(View.GONE);
        tvSignIn.setVisibility(View.GONE);
    }

    private void setupHeader() {
        ivLogo = findViewById(R.id.iv_logo);
        ivMenu = findViewById(R.id.iv_menu);
        ivBack = findViewById(R.id.iv_back);
        tvTitle = findViewById(R.id.tv_title);
        tvSignIn = findViewById(R.id.tv_sign_in);
        tvAppName = findViewById(R.id.tv_app_name);
        ivClose = findViewById(R.id.iv_close);
        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AddLocationsActivity.this, TabActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
            }
        });
        initHeaderViews();
    }

    private void gotoAccountSetupCompleteActivity() {
        Intent intent = new Intent(this, AccountSetupCompleteActivity.class);

        startActivity(intent);
    }

    private ArrayList<AddedLocation> generateAddedLocations() {
        ArrayList<AddedLocation> savedLocations = new ArrayList<AddedLocation>();

        if (!tvHomeAddress.getText().toString().isEmpty()) {
            AddedLocation addedLocation = new AddedLocation();
            addedLocation.setName("Home");
            addedLocation.setType("home");
            addedLocation.setIcon("home");
            addedLocation.setAddress(tvHomeAddress.getText().toString());

            if (tvHomeAddress.getTag() != null) {
                LatLng latLng = (LatLng) tvHomeAddress.getTag();
                addedLocation.setLat(latLng.getLatitude());
                addedLocation.setLon(latLng.getLongitude());
            }
            savedLocations.add(addedLocation);
        }

        if (!tvWorkAddress.getText().toString().isEmpty()) {
            AddedLocation addedLocation = new AddedLocation();
            addedLocation.setName("Work");
            addedLocation.setType("work");
            addedLocation.setIcon("work");
            addedLocation.setAddress(tvWorkAddress.getText().toString());

            if (tvWorkAddress.getTag() != null) {
                LatLng latLng = (LatLng) tvWorkAddress.getTag();
                addedLocation.setLat(latLng.getLatitude());
                addedLocation.setLon(latLng.getLongitude());
            }
            savedLocations.add(addedLocation);
        }
        savedLocations.addAll(addedLocations);
        return savedLocations;
    }

    private void savePreferences() {
        Response.Listener listener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                gotoAccountSetupCompleteActivity();
            }
        };
        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Logger.e("account", error.toString());
                Utilities.showNormalPopupDialog(AddLocationsActivity.this, "", getString(R.string.save_preferences_failed));
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

    private void setupBottomBar() {
        tvBack = findViewById(R.id.tv_back);
        tvNext = findViewById(R.id.tv_next);
        tvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        tvNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                savePreferences();
            }
        });
    }
}