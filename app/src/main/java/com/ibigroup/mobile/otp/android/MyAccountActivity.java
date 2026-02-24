package com.ibigroup.mobile.otp.android;

import static com.ibigroup.mobile.otp.android.utility.ServerDelegate.volleyAccountStringRequest;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.LinearLayoutCompat;

import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ibigroup.mobile.otp.android.api.Resource;
import com.ibigroup.mobile.otp.android.customize.CustomizePopupDialog;
import com.ibigroup.mobile.otp.android.json.AddedLocation;
import com.ibigroup.mobile.otp.android.json.RelatedUser;
import com.ibigroup.mobile.otp.android.json.UserPreferences;
import com.ibigroup.mobile.otp.android.json.itinerary.Itinerary;
import com.ibigroup.mobile.otp.android.json.itinerary.ItineraryForSaveTrip;
import com.ibigroup.mobile.otp.android.json.itinerary.ItineraryLeg;
import com.ibigroup.mobile.otp.android.json.trip.CreateNewTripRequest;
import com.ibigroup.mobile.otp.android.json.trip.ItineraryExistence;
import com.ibigroup.mobile.otp.android.json.trip.ItineraryExistenceDate;
import com.ibigroup.mobile.otp.android.json.trip.MonitoredTrip;
import com.ibigroup.mobile.otp.android.json.trip.MonitoredTripResponse;
import com.ibigroup.mobile.otp.android.json.trip.MonitoredTripUpdateRequest;
import com.ibigroup.mobile.otp.android.utility.Configurations;
import com.ibigroup.mobile.otp.android.utility.Constants;
import com.ibigroup.mobile.otp.android.utility.Convert;
import com.ibigroup.mobile.otp.android.utility.Logger;
import com.ibigroup.mobile.otp.android.utility.ServerDelegate;
import com.ibigroup.mobile.otp.android.utility.Utilities;
import com.mapbox.mapboxsdk.geometry.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public class MyAccountActivity extends BaseActivity {
    private ImageView ivLogo, ivMenu, ivBack;
    private TextView tvTitle, tvSignIn, tvAppName;
    private LinearLayout llTabTrips, llTabSettings;
    private TextView tvTabTrips, tvTabTripsDivider, tvTabSettings, tvTabSettingsDivider;
    private RelativeLayout rlSavedTrips;
    private ScrollView svSettings;
    private LinearLayout llSettings;
    private DisplayMetrics gDm = new DisplayMetrics();

    //My locations
    private boolean isEditPlaceMode = false;
    private int currentEditPlaceIndex = 0;
    private ListView lvAddedLocations;
    private AddedLocationListAdapter addedLocationListAdapter;
    private EditText etAddNewLocation;
    private TextView tvHomeAddress, tvWorkAddress, tvNewLocation, tvCancel, tvSave, tvNewLocationTitle;
    private LinearLayout llCurrentLocations, llAddHomeAddress, llAddWorkAddress,
            llAddAnotherPlace, llAddNewLocation;
    private int searchAddressMode = Constants.SEARCH_ADDRESS_MODE_HOME;
    private StringRequest gpsReverseRequest = null;
    private RelativeLayout rlDeleteHomeAddress, rlDeleteWorkAddress;

    //notification preferences
    private TextView tvPreferenceEmail, tvPreferenceSMS, tvPreferenceOptOut, tvNotificationEmail;
    private TextView tvSentVerificationText, tvSentVerificationTextCancel;
    private TextView tvNotificationSMSNumber, tvNotificationSMSNumberStatus, tvChangeNumber,
            tvVerificationCodeVerify, tvRequestNewCode;
    private LinearLayout llNotificationSMS, llNotificationSMSNewNumber,
            llNotificationSMSSentTo, llVerifySMS;
    private EditText etNotificationSMS, etVerificationCode;

    private final ArrayList<AddedLocation> addedLocations = new ArrayList<>();
    private final ArrayList<AddedLocation> favStops = new ArrayList<>();
    private CheckBox cbCreateNewAccountTerms, cbCreateNewAccountOptional;
    private TextView tvCreateNewAccountTerms, tvCreateNewAccountOptional;

    private TextView tvDeleteMyAccount, tvSavePreferences;
    private CheckBox cbEmail, cbSMS, cbPush, cbHapticFeedback;
    private TextView tvRegisteredDevicesCount;
    private boolean showedWarning = false;
    private CustomizePopupDialog smsWarningPopup = null;

    private ScrollView svEditTrip;
    private LinearLayout llEmptyTrips;
    private ListView lvSavedTrip;
    private SavedTripListAdapter savedTripListAdapter;
    private ProgressBar pbSavedTripList;
    private MonitoredTripResponse monitoredTripResponse;
    private MonitoredTrip selectedMonitoredTrip = null;
    private final MonitoredTripUpdateRequest monitoredTripUpdateRequest = new MonitoredTripUpdateRequest();

    private LinearLayout ll_advanced_settings, ll_monitor_trip, ll_trip_monitoring;
    private RelativeLayout rl_realtime_alert, rl_alternative_route, rl_delay_options, rl_monitor_trip;
    private Spinner sp_realtime_alert, sp_alternative_route, sp_delay_options, sp_monitor_trip;
    private LinearLayout ll_snooze, ll_pause_resume, ll_modes;
    private ImageView iv_snooze, iv_pause_resume, iv_advanced_settings;
    private EditText et_trip_name;
    private TextView tv_edit_trip_title, tv_trip_monitoring_title, tv_last_checked, tv_trip_monitoring_content,
            tv_snooze, tv_pause_resume, tv_itinerary_start_end_time, tv_itinerary_duration;
    private LinearLayout ll_monday, ll_tuesday, ll_wednesday, ll_thursday, ll_friday, ll_saturday, ll_sunday;
    private TextView tv_monday, tv_tuesday, tv_wednesday, tv_thursday, tv_friday, tv_saturday, tv_sunday;
    private CheckBox cb_monday, cb_tuesday, cb_wednesday, cb_thursday, cb_friday, cb_saturday, cb_sunday;
    private TextView tv_cancel_update_trip, tv_save_trip;
    private boolean createNewTripMode = false;
    private boolean accountSettingsMode = false;
    private Itinerary itineraryForNewTrip = null;

    private SpinnerAdapter alertOnJourneySpinnerAdapter, notifyOnItineraryChangeSpinnerAdapter,
            leadTimeSpinnerAdapter, delaySpinnerAdapter;
    private int alertOnJourneyIndex = Constants.DEFAULT_NOTIFY_ON_ALERT_INDEX,
            notifyOnItineraryChangeIndex = Constants.DEFAULT_NOTIFY_ON_ITINERARY_CHANGE_INDEX,
            leadTimeIndex = Constants.DEFAULT_LEAD_TIME_INDEX,
            delayIndex = Constants.DEFAULT_DELAY_INDEX;

    private ListView lvTravelCompanion;
    private TravelCompanionListAdapter travelCompanionListAdapter;
    private ArrayList<RelatedUser> relatedUsers = null;
    private TextView tvCurrentTravelCompanionsEmpty, tvSendTravelCompanionInvitation;
    private EditText etTravelCompanionEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_account);
        gDm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(gDm);
        if (getIntent() != null) {
            Intent intent = getIntent();
            createNewTripMode = intent.getBooleanExtra("create_new_trip_mode", false);
            itineraryForNewTrip = (Itinerary) intent.getSerializableExtra("itinerary_new_trip");

            accountSettingsMode = intent.getBooleanExtra("account_settings", false);
        }
        setupHeader();
        setupSavedTripsWidgets();
        setupEditTripWidgets();
        setupSettingsWidgets();
        setupTabControls();

        setupHomeAddressLayout();
        setupWorkAddressLayout();
        setupAddNewLocationLayout();
        setupAddedLocationsLayout();

        setupPreferenceEmailLayout();
        setupPreferenceSMSLayout();
        setupPreferencePushNotificationLayout();
        setupTermsOptional();
        setupPreferenceHapticFeedbackLayout();

        setupSavePreferences();
        setupDeleteMyAccount();
        setupReviewAccessibilityQuestions();

        initWidgets();
        filterNotificationMethods();
        LinearLayout llTabs = findViewById(R.id.ll_tabs);
        String userPreferencesString = Configurations.getString("account_user_preferences", "");
        Gson gson = new Gson();
        if (createNewTripMode) {
            llTabs.setVisibility(View.GONE);
            monitoredTripUpdateRequest.setItinerary(itineraryForNewTrip);
            monitoredTripUpdateRequest.setQueryParams(itineraryForNewTrip.getQueryParams());

            if (userPreferencesString != null && !userPreferencesString.isEmpty()) {
                try {
                    UserPreferences userPreferences = gson.fromJson(userPreferencesString, UserPreferences.class);
                    monitoredTripUpdateRequest.setUserId(userPreferences.getId());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            tv_edit_trip_title.setText(getString(R.string.save_new_trip));
            showAndPopulateEditTrip();
        } else {
            if (userPreferencesString != null && !userPreferencesString.isEmpty()) {
                try {
                    UserPreferences userPreferences = gson.fromJson(userPreferencesString, UserPreferences.class);

                    tvRegisteredDevicesCount.setText(getString(R.string.devices_registered, userPreferences.getPushDevices()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if (accountSettingsMode) {
                llTabs.setVisibility(View.GONE);
                selectedSettingTab();
            } else {
                llTabs.setVisibility(View.VISIBLE);
            }
            tv_edit_trip_title.setText(getString(R.string.edit_saved_trip));
            svEditTrip.setVisibility(View.GONE);
            getSavedTripList();
        }
        setupTravelCompanion();
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

        savedLocations.addAll(favStops);
        return savedLocations;
    }

    private void savePreferences() {
        Response.Listener listener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                hasUnsavedChanges = false;
                Configurations.setString("account_user_preferences", response);
                Gson gson = new Gson();
                try {
                    MyApplication.getInstance().userPreferences = gson.fromJson(response, UserPreferences.class);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                UserPreferences userPreferences = MyApplication.getInstance().userPreferences;
                if (userPreferences != null && userPreferences.getRelatedUsers() != null) {
                    relatedUsers = userPreferences.getRelatedUsers();
                }
                if (travelCompanionListAdapter != null) {
                    travelCompanionListAdapter.notifyDataSetInvalidated();
                }
                refreshTravelCompanionsEmptyStatus();
                Utilities.setTotalHeightToListView(lvTravelCompanion);

                Utilities.showNormalPopupDialog(MyAccountActivity.this, "", getString(R.string.save_preferences_successfully));
            }
        };
        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Logger.e("account", error.toString());
                Utilities.showNormalPopupDialog(MyAccountActivity.this, "", getString(R.string.save_preferences_failed));
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
            newPreferences.setHasConsentedToTerms(cbCreateNewAccountTerms.isChecked());
            newPreferences.setStoreTripHistory(cbCreateNewAccountOptional.isChecked());
            newPreferences.setSavedLocations(generateAddedLocations());
            newPreferences.setMobilityProfile(userPreferences.getMobilityProfile());
            newPreferences.setRelatedUsers(userPreferences.getRelatedUsers());
            newPreferences.setDependents(userPreferences.getDependents());

            StringBuilder stringBuilder = new StringBuilder();

            if (cbEmail.isChecked()) {
                stringBuilder.append("email");
            }

            if (cbSMS.isChecked()) {
                if (stringBuilder.length() > 0) {
                    stringBuilder.append(",");
                }
                stringBuilder.append("sms");
            }

            if (cbPush.isChecked()) {
                if (stringBuilder.length() > 0) {
                    stringBuilder.append(",");
                }
                stringBuilder.append("push");
            }

            if (cbHapticFeedback.isChecked()) {
                if (stringBuilder.length() > 0) {
                    stringBuilder.append(",");
                }
                stringBuilder.append("haptic");
            }

            newPreferences.setNotificationChannel(stringBuilder.toString());
            Gson gson = new Gson();
            String url = String.format(Resource.API_ACCOUNT_USER_SAVE_PREFERENCES, userPreferences.getId());

            volleyAccountStringRequest(Request.Method.PUT, url, gson.toJson(newPreferences), listener, errorListener);
        }

    }

    private boolean needShowWarning() {
        if (showedWarning) {
            return false;
        } else {
            UserPreferences userPreferences = MyApplication.getInstance().userPreferences;

            return cbSMS.isChecked() && userPreferences != null && !userPreferences.isPhoneNumberVerified();
        }
    }

    private void showWarningPopup() {
        showedWarning = true;

        smsWarningPopup = new CustomizePopupDialog(MyAccountActivity.this,
                null, getString(R.string.sms_warning_msg),
                getString(R.string.ok), null, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                smsWarningPopup.dismiss();
                smsWarningPopup = null;
            }
        }, null);
        smsWarningPopup.show();
    }

    private void setupSavePreferences() {
        tvSavePreferences = findViewById(R.id.tv_save_preferences);
        tvSavePreferences.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (needShowWarning()) {
                    showWarningPopup();
                } else {
                    savePreferences();
                }
            }
        });
    }

    private void setupReviewAccessibilityQuestions() {
        TextView tvReviewAccessibilityQuestions = findViewById(R.id.tv_review_accessibility_questions);

        if (Configurations.getBoolean("enable_mobile_questionairs", false)) {
            tvReviewAccessibilityQuestions.setVisibility(View.VISIBLE);
        } else {
            tvReviewAccessibilityQuestions.setVisibility(View.GONE);
        }
        tvReviewAccessibilityQuestions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyAccountActivity.this, AccessibilityProfileActivity.class);

                intent.putExtra("from_my_settings", true);
                startActivity(intent);
            }
        });
    }

    private void deleteMyAccountAction() {
        Response.Listener listener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Configurations.setString("account_credentials", "");
                Configurations.setString("account_user_profile", "");
                Configurations.setString("account_access_token", "");
                Configurations.setString("account_id_token", "");
                Configurations.setString("last_track_trip_id", "");

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MyAccountActivity.this);
                // set title
                alertDialogBuilder.setTitle("");

                // set dialog message
                alertDialogBuilder
                        .setMessage(getString(R.string.delete_account_successfully))
                        .setCancelable(false)
                        .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                                finish();
                            }
                        });

                alertDialogBuilder.create().show();
            }
        };
        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Logger.e("account", error.toString());
                Utilities.showNormalPopupDialog(MyAccountActivity.this, "", getString(R.string.delete_account_failed));
            }
        };
        UserPreferences userPreferences = MyApplication.getInstance().userPreferences;
        if (userPreferences != null) {

            String url = String.format(Resource.API_ACCOUNT_USER_DELETE_MY_ACCOUNT, userPreferences.getId());

            volleyAccountStringRequest(Request.Method.DELETE, url, "", listener, errorListener);
        }

    }

    private void showDeleteConfirmationPopup() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        // set title
        alertDialogBuilder.setTitle("");

        // set dialog message
        alertDialogBuilder
                .setMessage(getString(R.string.delete_account_warning))
                .setCancelable(false)
                .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        deleteMyAccountAction();
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });

        alertDialogBuilder.create().show();
    }

    private void setupDeleteMyAccount() {
        tvDeleteMyAccount = findViewById(R.id.tv_delete_my_account);
        tvDeleteMyAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeleteConfirmationPopup();
            }
        });
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
                    case "dining":
                        addedLocations.add(addedLocation);
                        break;
                    case "stop":
                        favStops.add(addedLocation);
                        break;

                    default:
                        break;
                }
            }

            if (addedLocationListAdapter != null) {
                addedLocationListAdapter.notifyDataSetChanged();
            }
            Utilities.setTotalHeightToListViewExt(gDm.widthPixels - Convert.dpToPx(20), lvAddedLocations);
        }
    }

    private void initNotificationPreferences(@NonNull UserPreferences userPreferences) {
        if (userPreferences.getNotificationChannel() != null) {
            cbEmail.setChecked(userPreferences.getNotificationChannel().toLowerCase(Locale.ROOT).contains("email"));

            cbSMS.setChecked(userPreferences.getNotificationChannel().toLowerCase(Locale.ROOT).contains("sms"));

            cbPush.setChecked(userPreferences.getNotificationChannel().toLowerCase(Locale.ROOT).contains("push"));

            cbHapticFeedback.setChecked(userPreferences.getNotificationChannel().toLowerCase(Locale.ROOT).contains("haptic"));

        }

        if (userPreferences.getEmail() != null) {
            tvNotificationEmail.setText(userPreferences.getEmail());
        }

        if (userPreferences.getPhoneNumber() != null && !userPreferences.getPhoneNumber().isEmpty()) {
            llNotificationSMSSentTo.setVisibility(View.VISIBLE);
            llNotificationSMSNewNumber.setVisibility(View.GONE);
            tvSentVerificationTextCancel.setEnabled(true);
            tvNotificationSMSNumber.setText(userPreferences.getPhoneNumber());

            if (userPreferences.isPhoneNumberVerified()) {
                tvNotificationSMSNumberStatus.setText(getString(R.string.verified));
                tvNotificationSMSNumberStatus.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorPhoneNumberVerified)));
                llVerifySMS.setVisibility(View.GONE);
            } else {
                tvNotificationSMSNumberStatus.setText(getString(R.string.pending));
                tvNotificationSMSNumberStatus.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorPhoneNumberPending)));
                llVerifySMS.setVisibility(View.VISIBLE);
            }
        } else {
            llNotificationSMSSentTo.setVisibility(View.GONE);
            llNotificationSMSNewNumber.setVisibility(View.VISIBLE);
            tvSentVerificationTextCancel.setEnabled(false);
            llVerifySMS.setVisibility(View.VISIBLE);
        }

        CompoundButton.OnCheckedChangeListener onCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                hasUnsavedChanges = true;
            }
        };
        cbEmail.setOnCheckedChangeListener(onCheckedChangeListener);
        cbSMS.setOnCheckedChangeListener(onCheckedChangeListener);
        cbPush.setOnCheckedChangeListener(onCheckedChangeListener);
        cbHapticFeedback.setOnCheckedChangeListener(onCheckedChangeListener);
    }

    private void initTermsAndOptional(@NonNull UserPreferences userPreferences) {
        cbCreateNewAccountTerms.setChecked(userPreferences.isHasConsentedToTerms());
        cbCreateNewAccountOptional.setChecked(userPreferences.isStoreTripHistory());
    }

    private void initWidgets() {
        UserPreferences userPreferences = MyApplication.getInstance().userPreferences;

        if (userPreferences != null) {
            initMyLocations(userPreferences.getSavedLocations());
            initNotificationPreferences(userPreferences);
            initTermsAndOptional(userPreferences);
        }
    }

    private void setupTermsOptional() {
        LinearLayoutCompat llCreateNewAccountOptional = findViewById(R.id.ll_create_new_account_optional);
        if (Configurations.getBoolean("enable_my_setting_optional", true)) {
            llCreateNewAccountOptional.setVisibility(View.VISIBLE);
        } else {
            llCreateNewAccountOptional.setVisibility(View.GONE);
        }
        cbCreateNewAccountTerms = findViewById(R.id.cb_create_new_account_terms);
        cbCreateNewAccountOptional = findViewById(R.id.cb_create_new_account_optional);

        tvCreateNewAccountTerms = findViewById(R.id.tv_create_new_account_terms);
        tvCreateNewAccountOptional = findViewById(R.id.tv_create_new_account_optional);

        Locale currentLocale = getResources().getConfiguration().locale;
        String languageCode = currentLocale.getLanguage();
        String baseUrl = Configurations.getString("url_terms_of_service", "https://otp-sound-transit.s3.amazonaws.com/terms-of-service/TERMS+OF+USE_Sound+Ride+Guide_{locale}.html");

        if (languageCode.contains("en")) {
            languageCode = "en-US";
        }

        String urlWithCurrentLocale;
        String termsOfStorageURL = getString(R.string.create_new_account_optional_url);
        if (Configurations.getBoolean("enable_multiple_language", true)) {
            urlWithCurrentLocale = baseUrl.replace("{locale}", languageCode);
        } else {
            urlWithCurrentLocale = getString(R.string.create_new_account_terms_url);
        }

        String s = "I confirm that I am at least 18 years old, and I have read and consent to the <a href=\"" + urlWithCurrentLocale + "\">Terms of Service</a> for using the Trip Planner.";
        tvCreateNewAccountTerms.setText(Html.fromHtml(s));
        tvCreateNewAccountTerms.setMovementMethod(LinkMovementMethod.getInstance());
        tvCreateNewAccountTerms.setClickable(true);

        String optionS;
        if (termsOfStorageURL.isEmpty()) {
            optionS = getString(R.string.create_new_account_optional);
        } else {
            optionS = "<b>Optional</b>: I consent to the Trip Planner storing my historical planned trips in order to improve transit services in my area. <a href=\"" + termsOfStorageURL + "\">More info</a>";
        }
        tvCreateNewAccountOptional.setText(Html.fromHtml(optionS));

        tvCreateNewAccountTerms.setMovementMethod(LinkMovementMethod.getInstance());
        tvCreateNewAccountOptional.setMovementMethod(LinkMovementMethod.getInstance());
    }


    private void setupPreferenceEmailLayout() {
        tvNotificationEmail = findViewById(R.id.tv_notification_email);
        cbEmail = findViewById(R.id.cb_preference_email);
    }

    private void setupPreferenceNewSMSLayout() {
        llNotificationSMSNewNumber = findViewById(R.id.ll_notification_sms_new_number);
        etNotificationSMS = findViewById(R.id.et_notification_sms);
        tvSentVerificationText = findViewById(R.id.tv_sent_verification_text);
        tvSentVerificationTextCancel = findViewById(R.id.tv_sent_verification_text_cancel);

        etNotificationSMS.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() != 0) {
                    etNotificationSMS.setHint("");
                    enableSendVerificationTextButton();
                }

                if (charSequence.length() == 0) {

                    etNotificationSMS.setHint(getString(R.string.notification_sms_hint));
                    disableSendVerificationTextButton();
                }

                String text = etNotificationSMS.getText().toString();
                int textlength = etNotificationSMS.getText().length();

                if (text.endsWith("(") || text.endsWith(")") || text.endsWith(" ") || text.endsWith("-"))
                    return;

                switch (textlength) {
                    case 1:
                        etNotificationSMS.setText(new StringBuilder(text).insert(text.length() - 1, "(").toString());
                        etNotificationSMS.setSelection(etNotificationSMS.getText().length());
                        break;
                    case 5:
                        etNotificationSMS.setText(new StringBuilder(text).insert(text.length() - 1, ")").toString());
                        etNotificationSMS.setSelection(etNotificationSMS.getText().length());
                        break;
                    case 6:
                        etNotificationSMS.setText(new StringBuilder(text).insert(text.length() - 1, " ").toString());
                        etNotificationSMS.setSelection(etNotificationSMS.getText().length());
                        break;
                    case 10:
                        etNotificationSMS.setText(new StringBuilder(text).insert(text.length() - 1, "-").toString());
                        etNotificationSMS.setSelection(etNotificationSMS.getText().length());
                        break;
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        tvSentVerificationTextCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                llNotificationSMSNewNumber.setVisibility(View.GONE);
                llNotificationSMSSentTo.setVisibility(View.VISIBLE);
            }
        });

        tvSentVerificationText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String unMaskNumber = etNotificationSMS.getText().toString();
                sendVerificationCodeToPhoneNumber(unMaskNumber.replaceAll("[()-]", "").replaceAll("\\s", ""));
                llVerifySMS.setVisibility(View.VISIBLE);
            }
        });

        disableSendVerificationTextButton();
    }

    private void disableSendVerificationTextButton() {
        if (tvSentVerificationText != null) {
            tvSentVerificationText.setEnabled(false);
            tvSentVerificationText.setClickable(false);
            tvSentVerificationText.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorGrey)));
        }
    }

    private void enableSendVerificationTextButton() {
        if (tvSentVerificationText != null) {
            tvSentVerificationText.setEnabled(true);
            tvSentVerificationText.setClickable(true);
            tvSentVerificationText.setBackgroundTintList(null);
        }
    }

    private void setupPreferenceSMSSendToLayout() {
        llNotificationSMSSentTo = findViewById(R.id.ll_notification_sms_sent_to);
        etVerificationCode = findViewById(R.id.et_verification_code);
        tvNotificationSMSNumber = findViewById(R.id.tv_notification_sms_number);
        tvNotificationSMSNumberStatus = findViewById(R.id.tv_notification_sms_number_status);
        tvChangeNumber = findViewById(R.id.tv_change_number);
        tvVerificationCodeVerify = findViewById(R.id.tv_verification_code_verify);
        tvRequestNewCode = findViewById(R.id.tv_request_a_new_code);
        llVerifySMS = findViewById(R.id.ll_verify_sms);


        etVerificationCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() != 0) {
                    etNotificationSMS.setHint("");
                }

                if (charSequence.length() == 0) {
                    etNotificationSMS.setHint(getString(R.string.verification_code_hint));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        tvChangeNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                llNotificationSMSNewNumber.setVisibility(View.VISIBLE);
                llNotificationSMSSentTo.setVisibility(View.GONE);
            }
        });

        tvVerificationCodeVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifyTheVerificationCode(etVerificationCode.getText().toString());
            }
        });

        tvRequestNewCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserPreferences userPreferences = MyApplication.getInstance().userPreferences;
                if (userPreferences != null && userPreferences.getPhoneNumber() != null) {
                    sendVerificationCodeToPhoneNumber(userPreferences.getPhoneNumber());
                }
            }
        });

    }

    private String formatPhoneNumber(@NonNull String phoneNumber) {
        if (phoneNumber.isEmpty() || phoneNumber.startsWith("+")) {
            return phoneNumber;
        } else if (phoneNumber.startsWith("1")) {
            return "+" + phoneNumber;
        } else {
            return "+1" + phoneNumber;
        }
    }

    private void sendVerificationCodeToPhoneNumber(@NonNull final String phoneNumber) {
        Response.Listener listener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Utilities.showNormalPopupDialog(MyAccountActivity.this, "", getString(R.string.verification_code_sent));

                UserPreferences userPreferences = MyApplication.getInstance().userPreferences;
                if (userPreferences != null) {
                    userPreferences.setPhoneNumber(phoneNumber);
                    userPreferences.setPhoneNumberVerified(false);
                }
                tvNotificationSMSNumber.setText(phoneNumber);

                if (llNotificationSMSSentTo.getVisibility() != View.VISIBLE) {
                    llNotificationSMSSentTo.setVisibility(View.VISIBLE);
                    llNotificationSMSNewNumber.setVisibility(View.GONE);
                }
                tvNotificationSMSNumberStatus.setText(getString(R.string.pending));
                tvNotificationSMSNumberStatus.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorPhoneNumberPending)));

                tvSentVerificationTextCancel.setEnabled(true);
            }
        };
        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Logger.e("account", error.toString());
                Utilities.showNormalPopupDialog(MyAccountActivity.this, "", getString(R.string.verification_code_sent_failed));
            }
        };
        UserPreferences userPreferences = MyApplication.getInstance().userPreferences;
        if (userPreferences != null) {
            String url = String.format(Resource.API_ACCOUNT_USER_VERIFY_SMS_SEND_CODE, userPreferences.getId(), Uri.encode(formatPhoneNumber(phoneNumber)));

            volleyAccountStringRequest(Request.Method.GET, url, "", listener, errorListener);
        }
    }

    private void verifyTheVerificationCode(@NonNull String code) {
        Response.Listener listener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Utilities.showNormalPopupDialog(MyAccountActivity.this, "", getString(R.string.verify_verification_code_successfully));

                tvNotificationSMSNumberStatus.setText(getString(R.string.verified));
                tvNotificationSMSNumberStatus.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorPhoneNumberVerified)));
                tvSentVerificationTextCancel.setEnabled(true);
                llVerifySMS.setVisibility(View.GONE);

                UserPreferences userPreferences = MyApplication.getInstance().userPreferences;
                if (userPreferences != null) {
                    userPreferences.setPhoneNumberVerified(true);
                }
            }
        };
        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Logger.e("account", error.toString());
                Utilities.showNormalPopupDialog(MyAccountActivity.this, "", getString(R.string.verify_verification_code_failed));
            }
        };
        UserPreferences userPreferences = MyApplication.getInstance().userPreferences;
        String url = String.format(Resource.API_ACCOUNT_USER_VERIFY_SMS_VERIFY_CODE, userPreferences.getId(), code);

        volleyAccountStringRequest(Request.Method.POST, url, "", listener, errorListener);
    }

    private void setupPreferenceSMSLayout() {
        cbSMS = findViewById(R.id.cb_preference_sms);
        setupPreferenceNewSMSLayout();
        setupPreferenceSMSSendToLayout();
        llNotificationSMS = findViewById(R.id.ll_notification_sms);
    }

    private void setupPreferencePushNotificationLayout() {
        cbPush = findViewById(R.id.cb_preference_push);
        tvRegisteredDevicesCount = findViewById(R.id.tv_registered_devices_count);
    }

    private void setupPreferenceHapticFeedbackLayout() {
        cbHapticFeedback = findViewById(R.id.cb_haptic_feedback);
    }

    private void filterNotificationMethods() {
        String available_notification_methods = Configurations.getString("available_notification_methods",
                "Email,SMS,PushNotification");
        LinearLayout llEmail = findViewById(R.id.ll_preference_email);
        LinearLayout llSMS = findViewById(R.id.ll_preference_sms);
        LinearLayout llPush = findViewById(R.id.ll_preference_push);
        if (available_notification_methods.toLowerCase().contains("email")) {
            llEmail.setVisibility(View.VISIBLE);
        } else {
            llEmail.setVisibility(View.GONE);
        }

        if (available_notification_methods.toLowerCase().contains("sms")) {
            llSMS.setVisibility(View.VISIBLE);
        } else {
            llSMS.setVisibility(View.GONE);
        }

        if (available_notification_methods.toLowerCase().contains("pushnotification")) {
            llPush.setVisibility(View.VISIBLE);
        } else {
            llPush.setVisibility(View.GONE);
        }

        if (available_notification_methods.toLowerCase().contains("hapticfeedback")) {
            cbHapticFeedback.setVisibility(View.VISIBLE);
        } else {
            cbHapticFeedback.setVisibility(View.GONE);
        }
    }

    private void refreshAddress(String searchedAddress, LatLng latLng) {
        if (searchedAddress != null) {
            tvNewLocation.setText(searchedAddress);
            tvNewLocation.setTag(latLng);
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
        Toast.makeText(MyAccountActivity.this, getString(R.string.get_address_failed),
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
        Intent intent = new Intent(MyAccountActivity.this, AddressSearchActivity.class);
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
                //gotoAddressSearchPage(tvHomeAddress.getText().toString());

                isEditPlaceMode = true;
                llCurrentLocations.setVisibility(View.GONE);
                llSettings.setVisibility(View.GONE);
                etAddNewLocation.setVisibility(View.GONE);

                llAddNewLocation.setVisibility(View.VISIBLE);
                tvNewLocationTitle.setText(R.string.edit_home);
                tvNewLocation.setText(tvHomeAddress.getText());
            }
        });

        rlDeleteHomeAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvHomeAddress.setText("");
                tvHomeAddress.setTag(null);
                rlDeleteHomeAddress.setVisibility(View.GONE);
                hasUnsavedChanges = true;
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
                isEditPlaceMode = true;
                llCurrentLocations.setVisibility(View.GONE);
                llSettings.setVisibility(View.GONE);
                etAddNewLocation.setVisibility(View.GONE);

                llAddNewLocation.setVisibility(View.VISIBLE);
                tvNewLocationTitle.setText(R.string.edit_work);
                tvNewLocation.setText(tvWorkAddress.getText());
            }
        });

        rlDeleteWorkAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hasUnsavedChanges = true;
                tvWorkAddress.setText("");
                tvWorkAddress.setTag(null);
                rlDeleteWorkAddress.setVisibility(View.GONE);
            }
        });
    }

    private void resetAddLocationLayouts() {
        ivBack.setVisibility(View.VISIBLE);
        llAddNewLocation.setVisibility(View.GONE);
        llCurrentLocations.setVisibility(View.VISIBLE);
        llSettings.setVisibility(View.VISIBLE);
        etAddNewLocation.setText("");
        tvNewLocation.setText("");
        tvNewLocation.setTag(null);
    }

    private void setupAddNewLocationLayout() {
        etAddNewLocation = findViewById(R.id.et_add_new_location);
        tvNewLocation = findViewById(R.id.tv_new_location);
        tvNewLocationTitle = findViewById(R.id.tv_new_location_title);
        llAddNewLocation = findViewById(R.id.ll_add_new_location);
        llAddAnotherPlace = findViewById(R.id.ll_add_another_place);
        tvCancel = findViewById(R.id.tv_cancel);
        tvSave = findViewById(R.id.tv_save);

        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetAddLocationLayouts();
            }
        });

        llAddAnotherPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchAddressMode = Constants.SEARCH_ADDRESS_MODE_NEW;
                isEditPlaceMode = false;
                llCurrentLocations.setVisibility(View.GONE);
                llSettings.setVisibility(View.GONE);
                etAddNewLocation.setVisibility(View.VISIBLE);

                llAddNewLocation.setVisibility(View.VISIBLE);
                tvNewLocationTitle.setText(R.string.add_new_place);
            }
        });

        tvNewLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoAddressSearchPage(tvNewLocation.getText().toString());
            }
        });

        tvSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hasUnsavedChanges = true;
                if (isEditPlaceMode) {
                    if (searchAddressMode == Constants.SEARCH_ADDRESS_MODE_HOME) {
                        tvHomeAddress.setText(tvNewLocation.getText());
                        tvHomeAddress.setTag(tvNewLocation.getTag());
                        if (tvNewLocation.getText().toString().isEmpty()) {
                            rlDeleteHomeAddress.setVisibility(View.GONE);
                        } else {
                            rlDeleteHomeAddress.setVisibility(View.VISIBLE);
                        }
                    } else if (searchAddressMode == Constants.SEARCH_ADDRESS_MODE_WORK) {
                        tvWorkAddress.setText(tvNewLocation.getText());
                        tvWorkAddress.setTag(tvNewLocation.getTag());
                        if (tvNewLocation.getText().toString().isEmpty()) {
                            rlDeleteWorkAddress.setVisibility(View.GONE);
                        } else {
                            rlDeleteWorkAddress.setVisibility(View.VISIBLE);
                        }
                    } else if (currentEditPlaceIndex < addedLocations.size()) {
                        AddedLocation editLocation = addedLocations.get(currentEditPlaceIndex);
                        editLocation.setName(etAddNewLocation.getText().toString());
                        editLocation.setAddress(tvNewLocation.getText().toString());
                        if (tvNewLocation.getTag() != null) {
                            LatLng latLng = (LatLng) tvNewLocation.getTag();
                            editLocation.setLat(latLng.getLatitude());
                            editLocation.setLon(latLng.getLongitude());
                        }

                    }
                } else {
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
                }

                if (addedLocationListAdapter != null) {
                    addedLocationListAdapter.notifyDataSetChanged();
                }
                Utilities.setTotalHeightToListViewExt(gDm.widthPixels - Convert.dpToPx(20), lvAddedLocations);

                resetAddLocationLayouts();
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
            LinearLayout llLocation = convertView.findViewById(R.id.ll_location);
            ImageView ivAddedLocationType = convertView.findViewById(R.id.iv_added_location_type);


            if (addedLocation.getType().equalsIgnoreCase("dining")) {
                ivAddedLocationType.setImageResource(R.drawable.ic_restaurant);
            } else {
                ivAddedLocationType.setImageResource(R.drawable.ic_account_location);
            }
            tvAddedLocationName.setText(addedLocation.getName());
            tvAddedLocationAddress.setText(addedLocation.getAddress());
            rlDeleteLocation.setTag(position);
            rlDeleteLocation.setContentDescription(getString(R.string.delete) + addedLocation.getName());
            rlDeleteLocation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    hasUnsavedChanges = true;
                    int position = (int) v.getTag();
                    if (position < addedLocations.size()) {
                        addedLocations.remove(position);
                        if (addedLocationListAdapter != null) {
                            addedLocationListAdapter.notifyDataSetChanged();
                        }
                        Utilities.setTotalHeightToListViewExt(gDm.widthPixels - Convert.dpToPx(20), lvAddedLocations);
                    }
                }
            });

            llLocation.setTag(position);
            llLocation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = (int) v.getTag();
                    if (position < addedLocations.size()) {
                        searchAddressMode = Constants.SEARCH_ADDRESS_MODE_NEW;

                        AddedLocation editLocation = addedLocations.get(position);
                        isEditPlaceMode = true;
                        currentEditPlaceIndex = position;

                        llCurrentLocations.setVisibility(View.GONE);
                        llSettings.setVisibility(View.GONE);
                        etAddNewLocation.setVisibility(View.VISIBLE);

                        llAddNewLocation.setVisibility(View.VISIBLE);
                        tvNewLocationTitle.setText(R.string.edit_place);

                        etAddNewLocation.setText(editLocation.getName());
                        tvNewLocation.setText(editLocation.getAddress());
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
        Utilities.setTotalHeightToListViewExt(gDm.widthPixels - Convert.dpToPx(20), lvAddedLocations);
    }

    private SavedTripListAdapter getSavedTripListAdapter() {
        ArrayList<HashMap<String, Object>> data = new ArrayList<HashMap<String, Object>>();

        if (monitoredTripResponse != null && monitoredTripResponse.getData() != null) {
            for (int i = 0; i < monitoredTripResponse.getData().size(); i++) {
                MonitoredTrip monitoredTrip = monitoredTripResponse.getData().get(i);
                HashMap<String, Object> map = new HashMap<String, Object>();
                map.put("tv_trip_name", monitoredTrip.getTripName());
                data.add(map);
            }
        }

        SavedTripListAdapter mSavedTripListAdapter = new SavedTripListAdapter(
                this, data,
                R.layout.item_saved_trip_list,
                new String[]{"tv_trip_name"}, new int[]{R.id.tv_trip_name});

        return mSavedTripListAdapter;
    }

    private String populateHappensOnDates(MonitoredTrip monitoredTrip) {
        StringBuilder ret = new StringBuilder();

        if (monitoredTrip != null) {
            if (monitoredTrip.isMonday()) {
                ret.append(getString(R.string.monday));
                ret.append(", ");
            }
            if (monitoredTrip.isTuesday()) {
                ret.append(getString(R.string.tuesday));
                ret.append(", ");
            }
            if (monitoredTrip.isWednesday()) {
                ret.append(getString(R.string.wednesday));
                ret.append(", ");
            }
            if (monitoredTrip.isThursday()) {
                ret.append(getString(R.string.thursday));
                ret.append(", ");
            }
            if (monitoredTrip.isFriday()) {
                ret.append(getString(R.string.friday));
                ret.append(", ");
            }
            if (monitoredTrip.isSaturday()) {
                ret.append(getString(R.string.saturday));
                ret.append(", ");
            }
            if (monitoredTrip.isSunday()) {
                ret.append(getString(R.string.sunday));
                ret.append(", ");
            }

            if (!ret.toString().isEmpty() && ret.toString().length() > 2) {
                return ret.substring(0, ret.toString().length() - 2);
            }
        }
        return ret.toString();
    }

    class SavedTripListAdapter extends SimpleAdapter {
        Context mContext;
        private final LayoutInflater l_Inflater;

        public SavedTripListAdapter(Context context, List<? extends Map<String, ?>> data,
                                    int resource, String[] from, int[] to) {
            super(context, data, resource, from, to);

            mContext = context;
            l_Inflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            if (monitoredTripResponse != null && monitoredTripResponse.getData() != null) {
                return monitoredTripResponse.getData().size();
            } else {
                return 0;
            }
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = l_Inflater.inflate(
                        R.layout.item_saved_trip_list, null);
            }
            if (monitoredTripResponse != null && monitoredTripResponse.getData() != null) {
                MonitoredTrip monitoredTrip = monitoredTripResponse.getData().get(position);
                TextView tvTripName = convertView.findViewById(R.id.tv_trip_name);
                TextView tvTripFrom = convertView.findViewById(R.id.tv_trip_from);
                TextView tvTripTo = convertView.findViewById(R.id.tv_trip_to);
                TextView tvDuration = convertView.findViewById(R.id.tv_itinerary_duration);
                TextView tvStartEndTime = convertView.findViewById(R.id.tv_itinerary_start_end_time);
                LinearLayout llModes = convertView.findViewById(R.id.ll_modes);
                TextView tvHappensOnDates = convertView.findViewById(R.id.tv_happens_on_dates);
                TextView tvNotificationsLeading = convertView.findViewById(R.id.tv_notifications_leading);
                TextView tvPauseResume = convertView.findViewById(R.id.tv_pause_resume);
                ImageView ivPauseResume = convertView.findViewById(R.id.iv_pause_resume);
                LinearLayout llPauseResume = convertView.findViewById(R.id.ll_pause_resume);
                LinearLayout llEdit = convertView.findViewById(R.id.ll_edit);
                LinearLayout llDelete = convertView.findViewById(R.id.ll_delete);

                String happensOnDates = populateHappensOnDates(monitoredTrip);
                tvHappensOnDates.setText(Html.fromHtml(getString(R.string.happens_on, happensOnDates)));

                if (monitoredTrip.isActive()) {
                    tvNotificationsLeading.setText(Html.fromHtml(getString(R.string.notifications_lead_time,
                            monitoredTrip.getLeadTimeInMinutes())));

                    tvPauseResume.setText(R.string.pause);
                    ivPauseResume.setImageResource(R.drawable.ic_saved_trip_notification);
                    ivPauseResume.setContentDescription(getString(R.string.pause));
                } else {
                    tvNotificationsLeading.setText(Html.fromHtml(getString(R.string.notifications_disabled)));
                    tvPauseResume.setText(R.string.resume);
                    ivPauseResume.setImageResource(R.drawable.ic_saved_trip_muted);
                    ivPauseResume.setContentDescription(getString(R.string.play_button));
                }

                llPauseResume.setTag(position);
                llPauseResume.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int position = (Integer) v.getTag();

                        if (monitoredTripResponse != null && monitoredTripResponse.getData() != null
                                && position < monitoredTripResponse.getData().size()) {
                            selectedMonitoredTrip = monitoredTripResponse.getData().get(position);
                            selectedMonitoredTrip.setActive(!selectedMonitoredTrip.isActive());
                            selectedMonitoredTrip.setInactive(!selectedMonitoredTrip.isActive());

                            updateSavedTrip(selectedMonitoredTrip.getId());
                        }
                    }
                });

                llEdit.setTag(position);
                llEdit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int position = (Integer) v.getTag();
                        if (monitoredTripResponse != null && monitoredTripResponse.getData() != null
                                && position < monitoredTripResponse.getData().size()) {
                            selectedMonitoredTrip = monitoredTripResponse.getData().get(position);
                            monitoredTripUpdateRequest.copyFromMonitoredTrip(selectedMonitoredTrip);
                            showAndPopulateEditTrip();
                        }
                    }
                });
                llDelete.setTag(position);
                llDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int position = (Integer) v.getTag();
                        if (monitoredTripResponse != null && monitoredTripResponse.getData() != null
                                && position < monitoredTripResponse.getData().size()) {
                            selectedMonitoredTrip = monitoredTripResponse.getData().get(position);

                            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MyAccountActivity.this);

                            // set title
                            alertDialogBuilder.setTitle("");

                            // set dialog message
                            alertDialogBuilder
                                    .setMessage(getString(R.string.delete_saved_trip_warning))
                                    .setCancelable(false)
                                    .setNegativeButton(getString(R.string.cancel), null)
                                    .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            hasUnsavedChanges = true;
                                            dialog.dismiss();
                                            deleteSavedTrip(selectedMonitoredTrip.getId());
                                        }
                                    });

                            alertDialogBuilder.create().show();
                        }
                    }
                });

                tvTripName.setText(monitoredTrip.getTripName());

                String from = "";
                String to = "";

                if (monitoredTrip.getFrom() != null) {
                    from = monitoredTrip.getFrom().getName();
                }

                if (monitoredTrip.getTo() != null) {
                    to = monitoredTrip.getTo().getName();
                }
                tvTripFrom.setText(from);
                tvTripTo.setText(to);

                if (monitoredTrip.getItinerary() != null) {
                    Itinerary itinerary = monitoredTrip.getItinerary();
                    int durationMin = monitoredTrip.getItinerary().getDuration() / 60;
                    if (durationMin >= 60) {
                        tvDuration.setText(getString(R.string.hr_min_with_num, durationMin / 60,
                                durationMin % 60));
                    } else {
                        tvDuration.setText(getString(R.string.min_with_num, durationMin));
                    }

                    llModes.removeAllViews();
                    if (itinerary.getLegs() != null && itinerary.getLegs().size() > 0) {
                        ItineraryLeg firstItineraryLeg = itinerary.getLegs().get(0);
                        ItineraryLeg secondItineraryLeg = Utilities.getSecondMode(itinerary.getLegs());

                        ImageView ivFirstMode = new ImageView(mContext);
                        llModes.addView(ivFirstMode);
                        loadIconAndBackgroundToMode(ivFirstMode, firstItineraryLeg);

                        if (secondItineraryLeg != null) {
                            ImageView ivDashMode = createNormalImageView(R.drawable.ic_dash);
                            llModes.addView(ivDashMode);

                            ImageView ivSecondMode = new ImageView(mContext);
                            llModes.addView(ivSecondMode);
                            loadIconAndBackgroundToMode(ivSecondMode, secondItineraryLeg);
                        }

                    }

                    String startTime = Utilities.formatTimestamp(MyAccountActivity.this, itinerary.getStartTime());
                    String endTime = Utilities.formatTimestamp(MyAccountActivity.this, itinerary.getEndTime());

                    tvStartEndTime.setText(Html.fromHtml(getString(R.string.itinerary_start_end_time, startTime, endTime)));
                }
            }
            return convertView;
        }
    }

    private ImageView createNormalImageView(int resID) {
        ImageView imageView = new ImageView(this);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER_VERTICAL;
        imageView.setLayoutParams(params);
        imageView.setImageResource(resID);

        return imageView;
    }

    private void loadIconAndBackgroundToMode(ImageView ivMode, ItineraryLeg itineraryLeg) {
        if (ivMode != null && itineraryLeg != null) {
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) ivMode.getLayoutParams();
            layoutParams.width = Convert.dpToPx(36);//(int)systemContext.getResources().getDimension(R.dimen.layer_icon_size);
            layoutParams.height = Convert.dpToPx(32);//(int)systemContext.getResources().getDimension(R.dimen.layer_icon_size);

            ivMode.setLayoutParams(layoutParams);
            ivMode.setPadding(Convert.dpToPx(5), Convert.dpToPx(3), Convert.dpToPx(5), Convert.dpToPx(3));

            int backgroundColor = Utilities.getModeBackgroundColorByMode(this,
                    itineraryLeg.getMode().toLowerCase());

            GradientDrawable shape = new GradientDrawable();
            shape.setShape(GradientDrawable.RECTANGLE);
            float cornerRadius = Convert.dpToPx(6);
            shape.setCornerRadius(cornerRadius);
            shape.setColor(backgroundColor);
            ivMode.setBackground(shape);

            ivMode.setImageResource(Utilities.getModeImageByMode(itineraryLeg.getMode().toLowerCase(),
                    itineraryLeg.isRentedVehicle(), getApplicationContext(), ivMode, ivMode));
            ivMode.setColorFilter(getResources().getColor(android.R.color.white));
        }
    }

    private void setupEditTripWidgets() {
        ll_trip_monitoring = findViewById(R.id.ll_trip_monitoring);
        ll_advanced_settings = findViewById(R.id.ll_advanced_settings);
        ll_monitor_trip = findViewById(R.id.ll_monitor_trip);
        rl_realtime_alert = findViewById(R.id.rl_realtime_alert);
        rl_alternative_route = findViewById(R.id.rl_alternative_route);
        rl_delay_options = findViewById(R.id.rl_delay_options);
        rl_monitor_trip = findViewById(R.id.rl_monitor_trip);
        sp_realtime_alert = findViewById(R.id.sp_realtime_alert);
        sp_alternative_route = findViewById(R.id.sp_alternative_route);
        sp_delay_options = findViewById(R.id.sp_delay_options);
        sp_monitor_trip = findViewById(R.id.sp_monitor_trip);
        ll_snooze = findViewById(R.id.ll_snooze);
        ll_pause_resume = findViewById(R.id.ll_pause_resume);
        ll_modes = findViewById(R.id.ll_modes);
        iv_snooze = findViewById(R.id.iv_snooze);
        iv_pause_resume = findViewById(R.id.iv_pause_resume);
        iv_advanced_settings = findViewById(R.id.iv_advanced_settings);
        et_trip_name = findViewById(R.id.et_trip_name);
        et_trip_name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String tripName = et_trip_name.getText().toString();
                TextView tvTripNameHint = findViewById(R.id.tv_trip_name_hint);

                if (tvTripNameHint != null) {
                    if (tripName.isEmpty()) {
                        tvTripNameHint.setVisibility(View.VISIBLE);
                    } else {
                        tvTripNameHint.setVisibility(View.GONE);
                    }
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        tv_edit_trip_title = findViewById(R.id.tv_edit_trip_title);
        tv_trip_monitoring_title = findViewById(R.id.tv_trip_monitoring_title);
        tv_last_checked = findViewById(R.id.tv_last_checked);
        tv_trip_monitoring_content = findViewById(R.id.tv_trip_monitoring_content);
        tv_snooze = findViewById(R.id.tv_snooze);
        tv_pause_resume = findViewById(R.id.tv_pause_resume);
        tv_itinerary_start_end_time = findViewById(R.id.tv_itinerary_start_end_time);
        tv_itinerary_duration = findViewById(R.id.tv_itinerary_duration);
        ll_monday = findViewById(R.id.ll_monday);
        ll_tuesday = findViewById(R.id.ll_tuesday);
        ll_wednesday = findViewById(R.id.ll_wednesday);
        ll_thursday = findViewById(R.id.ll_thursday);
        ll_friday = findViewById(R.id.ll_friday);
        ll_saturday = findViewById(R.id.ll_saturday);
        ll_sunday = findViewById(R.id.ll_sunday);

        tv_monday = findViewById(R.id.tv_monday);
        tv_tuesday = findViewById(R.id.tv_tuesday);
        tv_wednesday = findViewById(R.id.tv_wednesday);
        tv_thursday = findViewById(R.id.tv_thursday);
        tv_friday = findViewById(R.id.tv_friday);
        tv_saturday = findViewById(R.id.tv_saturday);
        tv_sunday = findViewById(R.id.tv_sunday);

        cb_monday = findViewById(R.id.cb_monday);
        cb_tuesday = findViewById(R.id.cb_tuesday);
        cb_wednesday = findViewById(R.id.cb_wednesday);
        cb_thursday = findViewById(R.id.cb_thursday);
        cb_friday = findViewById(R.id.cb_friday);
        cb_saturday = findViewById(R.id.cb_saturday);
        cb_sunday = findViewById(R.id.cb_sunday);

        tv_cancel_update_trip = findViewById(R.id.tv_cancel_update_trip);
        tv_save_trip = findViewById(R.id.tv_save_trip);

        tv_cancel_update_trip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (createNewTripMode) {
                    finish();
                } else {
                    svEditTrip.setVisibility(View.GONE);
                    lvSavedTrip.setVisibility(View.VISIBLE);
                    llEmptyTrips.setVisibility(View.GONE);
                }
            }
        });

        tv_save_trip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                monitoredTripUpdateRequest.setTripName(et_trip_name.getText().toString().trim());
                monitoredTripUpdateRequest.setMonday(cb_monday.isChecked());
                monitoredTripUpdateRequest.setTuesday(cb_tuesday.isChecked());
                monitoredTripUpdateRequest.setWednesday(cb_wednesday.isChecked());
                monitoredTripUpdateRequest.setThursday(cb_thursday.isChecked());
                monitoredTripUpdateRequest.setFriday(cb_friday.isChecked());
                monitoredTripUpdateRequest.setSaturday(cb_saturday.isChecked());
                monitoredTripUpdateRequest.setSunday(cb_sunday.isChecked());

                monitoredTripUpdateRequest.setNotifyOnAlert(getAlertOnJourneyValue());
                monitoredTripUpdateRequest.setNotifyOnItineraryChange(getNotifyOnItineraryChangeValue());
                monitoredTripUpdateRequest.setLeadTimeInMinutes(getLeadTimeValue());
                monitoredTripUpdateRequest.setDepartureVarianceMinutesThreshold(getDelayValue());
                monitoredTripUpdateRequest.setArrivalVarianceMinutesThreshold(getDelayValue());

                if (createNewTripMode) {
                    createNewTrip();
                } else {
                    updateSavedTrip();
                }
            }
        });

        ll_pause_resume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                monitoredTripUpdateRequest.setActive(!monitoredTripUpdateRequest.isActive());
                refreshTripMonitoring();
            }
        });

        ll_snooze.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                monitoredTripUpdateRequest.setSnoozed(!monitoredTripUpdateRequest.isSnoozed());

                refreshTripMonitoring();
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
                cb_monday.setChecked(!cb_monday.isChecked());
                monitoredTripUpdateRequest.setMonday(cb_monday.isChecked());
            }
        });
        ll_tuesday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cb_tuesday.setChecked(!cb_tuesday.isChecked());
                monitoredTripUpdateRequest.setTuesday(cb_tuesday.isChecked());
            }
        });
        ll_wednesday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cb_wednesday.setChecked(!cb_wednesday.isChecked());
                monitoredTripUpdateRequest.setWednesday(cb_wednesday.isChecked());
            }
        });
        ll_thursday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cb_thursday.setChecked(!cb_thursday.isChecked());
                monitoredTripUpdateRequest.setThursday(cb_thursday.isChecked());
            }
        });
        ll_friday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cb_friday.setChecked(!cb_friday.isChecked());
                monitoredTripUpdateRequest.setFriday(cb_friday.isChecked());
            }
        });
        ll_saturday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cb_saturday.setChecked(!cb_saturday.isChecked());
                monitoredTripUpdateRequest.setSaturday(cb_saturday.isChecked());
            }
        });
        ll_sunday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cb_sunday.setChecked(!cb_sunday.isChecked());
                monitoredTripUpdateRequest.setSunday(cb_sunday.isChecked());
            }
        });

        initSpinner();
    }

    private void refreshTripMonitoring() {
        if (monitoredTripUpdateRequest.isActive() && !monitoredTripUpdateRequest.isSnoozed()) {
            ll_pause_resume.setVisibility(View.VISIBLE);
            ll_snooze.setVisibility(View.VISIBLE);
            tv_pause_resume.setText(getString(R.string.pause));
            iv_pause_resume.setImageResource(R.drawable.ic_pause);
            iv_snooze.setImageResource(R.drawable.ic_pause);
            tv_snooze.setText(getString(R.string.snoose_for_rest_today));

            ItineraryForSaveTrip itinerary = monitoredTripUpdateRequest.getItinerary();

            String startTime = Utilities.formatTimestamp(this, itinerary.getStartTime());

            String realMonitorTime = Utilities.formatTimestamp(this,
                    itinerary.getStartTime() - (long) monitoredTripUpdateRequest.getLeadTimeInMinutes() * 60 * 1000);
            tv_trip_monitoring_content.setText(getString(R.string.trip_is_due_to, startTime, realMonitorTime));
            tv_trip_monitoring_title.setText(getString(R.string.loading));
            checkItinerary();
        } else if (!monitoredTripUpdateRequest.isActive()) {
            ll_pause_resume.setVisibility(View.VISIBLE);
            ll_snooze.setVisibility(View.GONE);
            tv_pause_resume.setText(getString(R.string.resume));
            iv_pause_resume.setImageResource(R.drawable.ic_resume);
            tv_trip_monitoring_title.setText(getString(R.string.pause_trip_title));
            tv_trip_monitoring_content.setText(getString(R.string.pause_trip_hint));
        } else {
            ll_pause_resume.setVisibility(View.GONE);
            ll_snooze.setVisibility(View.VISIBLE);

            iv_snooze.setImageResource(R.drawable.ic_resume);
            tv_snooze.setText(getString(R.string.unsnooze_trip_analysis));

            tv_trip_monitoring_title.setText(getString(R.string.unsnooze_trip_analysis_title));
            tv_trip_monitoring_content.setText(getString(R.string.unsnooze_trip_analysis_hint));
        }
    }

    private void refreshLastChecked() {
        if (selectedMonitoredTrip != null && selectedMonitoredTrip.getJourneyState() != null) {
            long lastChecked = selectedMonitoredTrip.getJourneyState().getLastCheckedEpochMillis();
            long current = System.currentTimeMillis();

            if (lastChecked > current) {
                tv_last_checked.setText(getString(R.string.last_checked, "0 min"));
            } else {
                long differenceInMin = (current - lastChecked) / 60000L;

                if (differenceInMin >= 60) {
                    tv_last_checked.setText(getString(R.string.last_checked, differenceInMin / 60 + " hr, " + differenceInMin % 60 + " min"));
                } else {
                    tv_last_checked.setText(getString(R.string.last_checked, differenceInMin + " min"));
                }
            }
        }
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
            ImageView ivChecked = convertView.findViewById(R.id.iv_checked);
            TextView tvOption = convertView.findViewById(R.id.tv_option);

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

        notifyOnItineraryChangeSpinnerAdapter = new SpinnerAdapter(this, R.layout.item_spinner,
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

        leadTimeSpinnerAdapter = new SpinnerAdapter(this, R.layout.item_spinner,
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

        delaySpinnerAdapter = new SpinnerAdapter(this, R.layout.item_spinner,
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

        alertOnJourneySpinnerAdapter = new SpinnerAdapter(this, R.layout.item_spinner,
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

    private void showAndPopulateEditTrip() {
        svEditTrip.setVisibility(View.VISIBLE);
        lvSavedTrip.setVisibility(View.GONE);
        llEmptyTrips.setVisibility(View.GONE);
        ll_monitor_trip.setVisibility(View.GONE);
        iv_advanced_settings.setImageResource(R.drawable.ic_resume);
        if (createNewTripMode) {
            ll_trip_monitoring.setVisibility(View.GONE);
        } else {
            ll_trip_monitoring.setVisibility(View.VISIBLE);
            refreshTripMonitoring();
            refreshLastChecked();
        }
        refreshTripDates();
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

            ll_modes.removeAllViews();
            if (itinerary.getLegs() != null && itinerary.getLegs().size() > 0) {
                ItineraryLeg firstItineraryLeg = itinerary.getLegs().get(0);
                ItineraryLeg secondItineraryLeg = Utilities.getSecondMode(itinerary.getLegs());

                ImageView ivFirstMode = new ImageView(this);
                ll_modes.addView(ivFirstMode);
                loadIconAndBackgroundToMode(ivFirstMode, firstItineraryLeg);

                if (secondItineraryLeg != null) {
                    ImageView ivDashMode = createNormalImageView(R.drawable.ic_dash);
                    ll_modes.addView(ivDashMode);

                    ImageView ivSecondMode = new ImageView(this);
                    ll_modes.addView(ivSecondMode);
                    loadIconAndBackgroundToMode(ivSecondMode, secondItineraryLeg);
                }

            }

            String startTime = Utilities.formatTimestamp(MyAccountActivity.this, itinerary.getStartTime());
            String endTime = Utilities.formatTimestamp(MyAccountActivity.this, itinerary.getEndTime());

            tv_itinerary_start_end_time.setText(Html.fromHtml(getString(R.string.itinerary_start_end_time, startTime, endTime)));
        }

    }

    private void setupSavedTripsWidgets() {
        rlSavedTrips = findViewById(R.id.rl_saved_trips);
        svEditTrip = findViewById(R.id.sv_edit_trip);
        llEmptyTrips = findViewById(R.id.ll_empty_trips);
        lvSavedTrip = findViewById(R.id.lv_saved_trip);
        pbSavedTripList = findViewById(R.id.pb_saved_trip_list);

        savedTripListAdapter = getSavedTripListAdapter();
        lvSavedTrip.setAdapter(savedTripListAdapter);
    }

    private void getSavedTripList() {
        pbSavedTripList.setVisibility(View.VISIBLE);
        Response.Listener listener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                pbSavedTripList.setVisibility(View.GONE);
                Gson gson = new Gson();
                try {
                    monitoredTripResponse = gson.fromJson(response, MonitoredTripResponse.class);
                    if (monitoredTripResponse != null && monitoredTripResponse.getData() != null
                            && monitoredTripResponse.getData().size() > 0) {
                        lvSavedTrip.setVisibility(View.VISIBLE);
                        llEmptyTrips.setVisibility(View.GONE);
                    } else {
                        lvSavedTrip.setVisibility(View.GONE);
                        llEmptyTrips.setVisibility(View.VISIBLE);
                    }
                    if (savedTripListAdapter != null) {
                        savedTripListAdapter.notifyDataSetChanged();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        };
        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pbSavedTripList.setVisibility(View.GONE);
                Logger.e("account", error.toString());

                errorHandler(error, getString(R.string.failed_get_trip_list));
            }
        };

        String url = Resource.API_ACCOUNT_MONITORED_TRIP;

        volleyAccountStringRequest(Request.Method.GET, url, "", listener, errorListener);
    }

    private void deleteSavedTrip(String id) {
        if (id != null) {
            pbSavedTripList.setVisibility(View.VISIBLE);
            Response.Listener listener = new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    pbSavedTripList.setVisibility(View.GONE);

                    if (monitoredTripResponse != null && monitoredTripResponse.getData() != null
                            && selectedMonitoredTrip != null) {
                        monitoredTripResponse.getData().remove(selectedMonitoredTrip);
                    }

                    if (monitoredTripResponse != null && monitoredTripResponse.getData() != null
                            && monitoredTripResponse.getData().size() > 0) {
                        lvSavedTrip.setVisibility(View.VISIBLE);
                        llEmptyTrips.setVisibility(View.GONE);
                    } else {
                        lvSavedTrip.setVisibility(View.GONE);
                        llEmptyTrips.setVisibility(View.VISIBLE);
                    }
                    if (savedTripListAdapter != null) {
                        savedTripListAdapter.notifyDataSetChanged();
                    }
                }
            };
            Response.ErrorListener errorListener = new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    pbSavedTripList.setVisibility(View.GONE);
                    Logger.e("account", error.toString());

                    errorHandler(error, getString(R.string.failed_delete_trip));
                }
            };

            String url = String.format(Resource.API_ACCOUNT_DELETE_TRIP, id);

            volleyAccountStringRequest(Request.Method.DELETE, url, "", listener, errorListener);
        }
    }

    private Date extractAvailableWeekday(ItineraryExistenceDate itineraryExistenceDate, int hour, int minute) {
        if (itineraryExistenceDate != null && itineraryExistenceDate.isValid() && itineraryExistenceDate.getValidDates() != null
                && itineraryExistenceDate.getValidDates().size() > 0) {
            String[] validDate = itineraryExistenceDate.getValidDates().get(0).split("-"); //"2021-10-01"
            Date dt = new Date();
            if (validDate.length == 3) {
                dt.setYear(Integer.parseInt(validDate[0]) - 1900);
                dt.setMonth(Integer.parseInt(validDate[1]) - 1);
                dt.setDate(Integer.parseInt(validDate[2]));
                dt.setHours(hour);
                dt.setMinutes(minute);
            }

            return dt;
        }
        return null;
    }

    private void refreshDatesWidgets(ItineraryExistence itineraryExistence) {
        if (itineraryExistence != null) {
            if (itineraryExistence.getMonday() != null && itineraryExistence.getMonday().isValid()) {
                ll_monday.setBackgroundColor(getResources().getColor(R.color.colorPrimary, null));
                tv_monday.setTextColor(getResources().getColor(android.R.color.white, null));
            } else {
                ll_monday.setBackgroundColor(getResources().getColor(R.color.colorInvalidDateBg, null));
                tv_monday.setTextColor(getResources().getColor(R.color.colorInvalidDateText, null));
            }

            if (itineraryExistence.getTuesday() != null && itineraryExistence.getTuesday().isValid()) {
                ll_tuesday.setBackgroundColor(getResources().getColor(android.R.color.white, null));
                tv_tuesday.setTextColor(getResources().getColor(android.R.color.black, null));
            } else {
                ll_tuesday.setBackgroundColor(getResources().getColor(R.color.colorInvalidDateBg, null));
                tv_tuesday.setTextColor(getResources().getColor(R.color.colorInvalidDateText, null));
            }

            if (itineraryExistence.getWednesday() != null && itineraryExistence.getWednesday().isValid()) {
                ll_wednesday.setBackgroundColor(getResources().getColor(android.R.color.white, null));
                tv_wednesday.setTextColor(getResources().getColor(android.R.color.black, null));
            } else {
                ll_wednesday.setBackgroundColor(getResources().getColor(R.color.colorInvalidDateBg, null));
                tv_wednesday.setTextColor(getResources().getColor(R.color.colorInvalidDateText, null));
            }

            if (itineraryExistence.getThursday() != null && itineraryExistence.getThursday().isValid()) {
                ll_thursday.setBackgroundColor(getResources().getColor(android.R.color.white, null));
                tv_thursday.setTextColor(getResources().getColor(android.R.color.black, null));
            } else {
                ll_thursday.setBackgroundColor(getResources().getColor(R.color.colorInvalidDateBg, null));
                tv_thursday.setTextColor(getResources().getColor(R.color.colorInvalidDateText, null));
            }

            if (itineraryExistence.getFriday() != null && itineraryExistence.getFriday().isValid()) {
                ll_friday.setBackgroundColor(getResources().getColor(android.R.color.white, null));
                tv_friday.setTextColor(getResources().getColor(android.R.color.black, null));
            } else {
                ll_friday.setBackgroundColor(getResources().getColor(R.color.colorInvalidDateBg, null));
                tv_friday.setTextColor(getResources().getColor(R.color.colorInvalidDateText, null));
            }

            if (itineraryExistence.getSaturday() != null && itineraryExistence.getSaturday().isValid()) {
                ll_saturday.setBackgroundColor(getResources().getColor(android.R.color.white, null));
                tv_saturday.setTextColor(getResources().getColor(android.R.color.black, null));
            } else {
                ll_saturday.setBackgroundColor(getResources().getColor(R.color.colorInvalidDateBg, null));
                tv_saturday.setTextColor(getResources().getColor(R.color.colorInvalidDateText, null));
            }

            if (itineraryExistence.getSunday() != null && itineraryExistence.getSunday().isValid()) {
                ll_sunday.setBackgroundColor(getResources().getColor(android.R.color.white, null));
                tv_sunday.setTextColor(getResources().getColor(android.R.color.black, null));
            } else {
                ll_sunday.setBackgroundColor(getResources().getColor(R.color.colorInvalidDateBg, null));
                tv_sunday.setTextColor(getResources().getColor(R.color.colorInvalidDateText, null));
            }
        }
    }

    private void checkItinerary() {
        Response.Listener listener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Gson gson = new Gson();

                Date dt = new Date();
                dt.setTime(selectedMonitoredTrip.getItinerary().getStartTime());

                try {
                    ItineraryExistence itineraryExistence = gson.fromJson(response, ItineraryExistence.class);
                    refreshDatesWidgets(itineraryExistence);
                    long minDiff = Long.MAX_VALUE;
                    int hour = dt.getHours();
                    int minute = dt.getMinutes();
                    ArrayList<Date> weekdays = new ArrayList<Date>();
                    Date monday = extractAvailableWeekday(itineraryExistence.getMonday(), hour, minute);
                    if (monday != null) {
                        weekdays.add(monday);
                    }
                    Date tuesday = extractAvailableWeekday(itineraryExistence.getTuesday(), hour, minute);
                    if (tuesday != null) {
                        weekdays.add(tuesday);
                    }
                    Date wednesday = extractAvailableWeekday(itineraryExistence.getWednesday(), hour, minute);
                    if (wednesday != null) {
                        weekdays.add(wednesday);
                    }
                    Date thursday = extractAvailableWeekday(itineraryExistence.getThursday(), hour, minute);
                    if (thursday != null) {
                        weekdays.add(thursday);
                    }
                    Date friday = extractAvailableWeekday(itineraryExistence.getFriday(), hour, minute);
                    if (friday != null) {
                        weekdays.add(friday);
                    }
                    Date saturday = extractAvailableWeekday(itineraryExistence.getSaturday(), hour, minute);
                    if (saturday != null) {
                        weekdays.add(saturday);
                    }
                    Date sunday = extractAvailableWeekday(itineraryExistence.getSunday(), hour, minute);
                    if (sunday != null) {
                        weekdays.add(sunday);
                    }
                    long diff;
                    long currentTime = System.currentTimeMillis();
                    Date selectedDate = null;
                    for (Date date : weekdays) {
                        diff = date.getTime() - currentTime;

                        if (diff > 0 && diff < minDiff) {
                            selectedDate = date;
                            minDiff = diff;
                        }
                    }

                    if (selectedDate != null) {
                        SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE MM/dd/yyyy", Locale.getDefault());
                        dateFormat.setTimeZone(TimeZone.getTimeZone(Configurations.TARGET_TIMEZONE_ID));
                        String dateString = dateFormat.format(selectedDate);
                        String startTime = Utilities.formatTimestamp(MyAccountActivity.this, selectedMonitoredTrip.getItinerary().getStartTime());

                        tv_trip_monitoring_title.setText(getString(R.string.next_trip_starts_on, dateString, startTime));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        };
        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Logger.e("account", error.toString());

                errorHandler(error, getString(R.string.failed_check_trip));
            }
        };

        String url = Resource.API_ACCOUNT_CHECK_TRIP;

        Gson gson = new GsonBuilder().serializeNulls().create();

        String params = gson.toJson(selectedMonitoredTrip);

        volleyAccountStringRequest(Request.Method.POST, url, params, listener, errorListener);

    }


    private void updateSavedTrip(String id) {
        if (id != null) {
            Response.Listener listener = new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    if (savedTripListAdapter != null) {
                        savedTripListAdapter.notifyDataSetChanged();
                    }
                }
            };
            Response.ErrorListener errorListener = new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Logger.e("account", error.toString());

                    errorHandler(error, getString(R.string.failed_update_trip));
                }
            };

            String url = String.format(Resource.API_ACCOUNT_UPDATE_SAVED_TRIP, id);

            Gson gson = new Gson();
            MonitoredTripUpdateRequest monitoredTripUpdateRequest = new MonitoredTripUpdateRequest();
            monitoredTripUpdateRequest.copyFromMonitoredTrip(selectedMonitoredTrip);
            String params = gson.toJson(monitoredTripUpdateRequest);

            volleyAccountStringRequest(Request.Method.PUT, url, params, listener, errorListener);
        }
    }


    private void updateSavedTrip() {
        pbSavedTripList.setVisibility(View.VISIBLE);
        if (monitoredTripUpdateRequest.getId() != null) {
            Response.Listener listener = new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    pbSavedTripList.setVisibility(View.GONE);
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MyAccountActivity.this);
                    // set title
                    alertDialogBuilder.setTitle("");
                    // set dialog message
                    alertDialogBuilder
                            .setMessage(getString(R.string.successfully_update_trip))
                            .setCancelable(false)
                            .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.dismiss();
                                    svEditTrip.setVisibility(View.GONE);
                                    getSavedTripList();
                                }
                            });

                    alertDialogBuilder.create().show();
                }
            };
            Response.ErrorListener errorListener = new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    pbSavedTripList.setVisibility(View.GONE);
                    Logger.e("account", error.toString());

                    errorHandler(error, getString(R.string.failed_update_trip));
                }
            };

            String url = String.format(Resource.API_ACCOUNT_UPDATE_SAVED_TRIP, monitoredTripUpdateRequest.getId());

            Gson gson = new Gson();
            String params = gson.toJson(monitoredTripUpdateRequest);

            volleyAccountStringRequest(Request.Method.PUT, url, params, listener, errorListener);
        }
    }

    private void createNewTrip() {
        pbSavedTripList.setVisibility(View.VISIBLE);

        Response.Listener listener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                pbSavedTripList.setVisibility(View.GONE);
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MyAccountActivity.this);
                // set title
                alertDialogBuilder.setTitle("");
                // set dialog message
                alertDialogBuilder
                        .setMessage(getString(R.string.successfully_create_trip))
                        .setCancelable(false)
                        .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                                finish();
                            }
                        });

                alertDialogBuilder.create().show();
            }
        };
        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pbSavedTripList.setVisibility(View.GONE);
                Logger.e("account", error.toString());
                errorHandler(error, getString(R.string.failed_create_trip));
            }
        };

        String url = Resource.API_ACCOUNT_SAVE_NEW_TRIP;

        Gson gson = new Gson();
        CreateNewTripRequest createNewTripRequest = new CreateNewTripRequest();
        createNewTripRequest.copyFromMonitoredTripUpdateRequest(monitoredTripUpdateRequest);
        String params = gson.toJson(createNewTripRequest);

        volleyAccountStringRequest(Request.Method.POST, url, params, listener, errorListener);

    }

    private void errorHandler(VolleyError error, @NonNull String defaultMessage) {
        if (error != null && error instanceof NoConnectionError) {
            Utilities.showNormalPopupDialog(MyAccountActivity.this, "", getString(R.string.connection_failed_msg));
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
                Utilities.showNormalPopupDialog(MyAccountActivity.this, "", msg);
            }
        } else {
            Utilities.showNormalPopupDialog(MyAccountActivity.this, "", defaultMessage);
        }
    }

    private void setupSettingsWidgets() {
        svSettings = findViewById(R.id.sv_settings);
        llSettings = findViewById(R.id.ll_settings);

    }

    private void setupTabControls() {
        llTabTrips = findViewById(R.id.ll_tab_trips);
        llTabSettings = findViewById(R.id.ll_tab_settings);
        tvTabTrips = findViewById(R.id.tv_tab_trips);
        tvTabTripsDivider = findViewById(R.id.tv_tab_trips_divider);
        tvTabSettings = findViewById(R.id.tv_tab_settings);
        tvTabSettingsDivider = findViewById(R.id.tv_tab_settings_divider);

        llTabTrips.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (rlSavedTrips.getVisibility() != View.VISIBLE) {
                    rlSavedTrips.setVisibility(View.VISIBLE);
                    svSettings.setVisibility(View.GONE);
                    tvTabTrips.setTextColor(getResources().getColor(android.R.color.black));
                    tvTabTripsDivider.setBackgroundColor(getResources().getColor(android.R.color.black));
                    tvTabSettings.setTextColor(getResources().getColor(R.color.colorGrey));
                    tvTabSettingsDivider.setBackgroundColor(getResources().getColor(R.color.colorGrey));
                }
            }
        });

        llTabSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (svSettings.getVisibility() != View.VISIBLE) {
                    selectedSettingTab();
                }
            }
        });
    }

    private void selectedSettingTab() {
        etVerificationCode.clearFocus();
        llAddHomeAddress.requestFocus();
        svSettings.setVisibility(View.VISIBLE);
        rlSavedTrips.setVisibility(View.GONE);
        tvTabSettings.setTextColor(getResources().getColor(android.R.color.black));
        tvTabSettingsDivider.setBackgroundColor(getResources().getColor(android.R.color.black));
        tvTabTrips.setTextColor(getResources().getColor(R.color.colorGrey));
        tvTabTripsDivider.setBackgroundColor(getResources().getColor(R.color.colorGrey));
    }

    private void initHeaderViews() {
        Utilities.setFont(this, tvTitle, false);
        Utilities.setFont(this, tvSignIn, false);

        ivLogo.setVisibility(View.GONE);
        ivMenu.setVisibility(View.GONE);
        ivBack.setVisibility(View.VISIBLE);
        if (accountSettingsMode) {
            tvTitle.setText(R.string.account_settings);
            tvTitle.setVisibility(View.VISIBLE);
        } else {
            tvTitle.setVisibility(View.GONE);
        }

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
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (llAddNewLocation.getVisibility() == View.VISIBLE) {
                    resetAddLocationLayouts();
                } else {
                    if (!checkUnsavedChangesAndPopup()) {
                        finish();
                    }
                }
            }
        });
        initHeaderViews();
    }

    private boolean hasUnsavedChanges = false;

    private boolean checkUnsavedChangesAndPopup() {
        if (hasUnsavedChanges) {
            View popupView = LayoutInflater.from(this).inflate(R.layout.layout_advanced_preference_popup, null);
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
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
                dialog.dismiss(); // Dismiss the dialog or perform any action
                finish();
            });
            // Show the dialog
            dialog.show();
        }

        return hasUnsavedChanges;

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            if (llAddNewLocation.getVisibility() == View.VISIBLE) {
                resetAddLocationLayouts();
                return true;
            } else {
                if (checkUnsavedChangesAndPopup())
                    return true;
            }
        }

        return super.onKeyDown(keyCode, event);
    }

    private void setupTravelCompanion() {
        LinearLayout llTravelCompanions = findViewById(R.id.ll_travel_companions);
        llTravelCompanions.setVisibility(View.VISIBLE);

        tvSendTravelCompanionInvitation = findViewById(R.id.tv_send_travel_companion_invitation);
        tvCurrentTravelCompanionsEmpty = findViewById(R.id.tv_current_travel_companions_empty);
        etTravelCompanionEmail = findViewById(R.id.et_travel_companion_email);
        lvTravelCompanion = findViewById(R.id.lv_travel_companion);
        travelCompanionListAdapter = getTravelCompanionListAdapter();

        lvTravelCompanion.setAdapter(travelCompanionListAdapter);
        Utilities.setTotalHeightToListView(lvTravelCompanion);

        refreshTravelCompanionsEmptyStatus();

        tvSendTravelCompanionInvitation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RelatedUser relatedUser = new RelatedUser();
                relatedUser.setEmail(etTravelCompanionEmail.getText().toString());
                relatedUser.setStatus("PENDING");

                UserPreferences userPreferences = MyApplication.getInstance().userPreferences;
                if (userPreferences != null && userPreferences.getRelatedUsers() != null) {
                    userPreferences.getRelatedUsers().add(relatedUser);
                }
                savePreferences();

                etTravelCompanionEmail.setText("");
            }
        });
    }

    private void refreshTravelCompanionsEmptyStatus() {
        UserPreferences userPreferences = MyApplication.getInstance().userPreferences;
        if (userPreferences != null && userPreferences.getRelatedUsers() != null && userPreferences.getRelatedUsers().size() > 0) {
            tvCurrentTravelCompanionsEmpty.setVisibility(View.GONE);
        } else {
            tvCurrentTravelCompanionsEmpty.setVisibility(View.VISIBLE);
        }
    }

    private TravelCompanionListAdapter getTravelCompanionListAdapter() {
        ArrayList<HashMap<String, Object>> data = new ArrayList<HashMap<String, Object>>();
        UserPreferences userPreferences = MyApplication.getInstance().userPreferences;
        if (userPreferences != null && userPreferences.getRelatedUsers() != null) {
            relatedUsers = userPreferences.getRelatedUsers();
            for (int i = 0; i < relatedUsers.size(); i++) {
                RelatedUser relatedUser = relatedUsers.get(i);

                HashMap<String, Object> map = new HashMap<String, Object>();

                map.put("tv_travel_companion_name", relatedUser.getEmail());

                data.add(map);
            }
        }

        TravelCompanionListAdapter mTravelCompanionListAdapter = new TravelCompanionListAdapter(
                this, data,
                R.layout.item_travel_companion,
                new String[]{"tv_travel_companion_name"}, new int[]{R.id.tv_travel_companion_name});

        return mTravelCompanionListAdapter;
    }

    class TravelCompanionListAdapter extends SimpleAdapter {
        Context mContext;
        private final LayoutInflater l_Inflater;

        public TravelCompanionListAdapter(Context context, List<? extends Map<String, ?>> data,
                                          int resource, String[] from, int[] to) {
            super(context, data, resource, from, to);

            mContext = context;
            l_Inflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            if (relatedUsers == null) {
                return 0;
            }
            return relatedUsers.size();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = l_Inflater.inflate(
                        R.layout.item_travel_companion, null);
            }
            TextView tvTravelCompanionName = convertView.findViewById(R.id.tv_travel_companion_name);
            TextView tvTravelCompanionStatus = convertView.findViewById(R.id.tv_travel_companion_status);
            ImageView ivDeleteTravelCompanion = convertView.findViewById(R.id.iv_delete_travel_companion);

            if (relatedUsers != null && position < relatedUsers.size()) {
                RelatedUser relatedUser = relatedUsers.get(position);

                tvTravelCompanionName.setText(relatedUser.getEmail());

                if (relatedUser.getStatus() != null && relatedUser.getStatus().equalsIgnoreCase("CONFIRMED")) {
                    tvTravelCompanionStatus.setText(getString(R.string.verified));
                    tvTravelCompanionStatus.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorPhoneNumberVerified)));
                } else if (relatedUser.getStatus() != null && relatedUser.getStatus().equalsIgnoreCase("INVALID")) {
                    tvTravelCompanionStatus.setText(getString(R.string.invalid));
                    tvTravelCompanionStatus.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorBgGrayDark)));
                } else {
                    tvTravelCompanionStatus.setText(getString(R.string.pending));
                    tvTravelCompanionStatus.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorPhoneNumberPending)));
                }
            }
            ivDeleteTravelCompanion.setTag(position);

            ivDeleteTravelCompanion.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = (Integer) v.getTag();
                    if (relatedUsers != null && position < relatedUsers.size()) {
                        RelatedUser relatedUser = relatedUsers.get(position);
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MyAccountActivity.this);

                        // set title
                        alertDialogBuilder.setTitle("");

                        // set dialog message
                        alertDialogBuilder
                                .setMessage(getString(R.string.delete_travel_companions, relatedUser.getEmail()))
                                .setCancelable(false)
                                .setNegativeButton(getString(R.string.cancel), null)
                                .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.dismiss();

                                        UserPreferences userPreferences = MyApplication.getInstance().userPreferences;
                                        if (userPreferences != null && userPreferences.getRelatedUsers() != null && position < userPreferences.getRelatedUsers().size()) {
                                            userPreferences.getRelatedUsers().remove(position);
                                        }
                                        savePreferences();
                                    }
                                });

                        alertDialogBuilder.create().show();
                    }
                }
            });

            return convertView;
        }
    }

}