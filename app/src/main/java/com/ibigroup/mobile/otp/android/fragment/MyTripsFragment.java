package com.ibigroup.mobile.otp.android.fragment;

import static com.ibigroup.mobile.otp.android.utility.Configurations.TARGET_TIMEZONE_ID;
import static com.ibigroup.mobile.otp.android.utility.Constants.POPUP_TYPE_MULTIPLE_SELECTION;
import static com.ibigroup.mobile.otp.android.utility.Constants.POPUP_TYPE_SINGLE_SELECTION;
import static com.ibigroup.mobile.otp.android.utility.ServerDelegate.volleyAccountStringRequest;
import static com.ibigroup.mobile.otp.android.utility.ServerDelegate.volleyAccountStringRequestExt;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.helper.widget.Flow;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.auth0.android.Auth0;
import com.auth0.android.authentication.AuthenticationAPIClient;
import com.auth0.android.authentication.AuthenticationException;
import com.auth0.android.callback.Callback;
import com.auth0.android.result.UserProfile;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ibigroup.mobile.otp.android.ActiveRouteActivity;
import com.ibigroup.mobile.otp.android.MyApplication;
import com.ibigroup.mobile.otp.android.PreviewTripActivity;
import com.ibigroup.mobile.otp.android.R;
import com.ibigroup.mobile.otp.android.TabActivity;
import com.ibigroup.mobile.otp.android.VerifyEmailActivity;
import com.ibigroup.mobile.otp.android.api.Resource;
import com.ibigroup.mobile.otp.android.customize.CustomizePopupSelectionDialog;
import com.ibigroup.mobile.otp.android.json.DependentMobilityProfile;
import com.ibigroup.mobile.otp.android.json.RelatedUser;
import com.ibigroup.mobile.otp.android.json.SelectionStatus;
import com.ibigroup.mobile.otp.android.json.UserPreferences;
import com.ibigroup.mobile.otp.android.json.itinerary.Itinerary;
import com.ibigroup.mobile.otp.android.json.itinerary.ItineraryForSaveTrip;
import com.ibigroup.mobile.otp.android.json.itinerary.ItineraryLeg;
import com.ibigroup.mobile.otp.android.json.trip.ItineraryExistence;
import com.ibigroup.mobile.otp.android.json.trip.ItineraryExistenceDate;
import com.ibigroup.mobile.otp.android.json.trip.JourneyState;
import com.ibigroup.mobile.otp.android.json.trip.MonitoredTrip;
import com.ibigroup.mobile.otp.android.json.trip.MonitoredTripResponse;
import com.ibigroup.mobile.otp.android.json.trip.MonitoredTripUpdateRequest;
import com.ibigroup.mobile.otp.android.utility.Configurations;
import com.ibigroup.mobile.otp.android.utility.Constants;
import com.ibigroup.mobile.otp.android.utility.Convert;
import com.ibigroup.mobile.otp.android.utility.Logger;
import com.ibigroup.mobile.otp.android.utility.Utilities;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public class MyTripsFragment extends Fragment {


    public MyTripsFragment() {
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
    // TODO: Rename and change types and number of parameters
    public static MyTripsFragment newInstance(String param1, String param2) {
        return new MyTripsFragment();
    }

    private Context systemContext;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        systemContext = MyApplication.getInstance().getApplicationContext();
    }
    private View mFragmentView;
    private TextView tvTitle, tvErrorMessage;
    private ImageView ivBack;

    private RadioButton rbEachWeek, rbSpecificDate;
    private LinearLayout llEachWeek;
    private String previousSelectedStatus = null;
    private ScrollView svEditTrip;
    private LinearLayout llEmptyTrips;
    private ListView lvSavedTrip;
    private SavedTripListAdapter savedTripListAdapter;
    private ProgressBar pbSavedTripList;
    private MonitoredTripResponse monitoredTripResponse = MyApplication.getInstance().gMonitoredTripResponse;
    private MonitoredTrip selectedMonitoredTrip = null;
    private final MonitoredTripUpdateRequest monitoredTripUpdateRequest = new MonitoredTripUpdateRequest();

    private String notificationTripID = null;
    private LinearLayout ll_advanced_settings, ll_monitor_trip, ll_trip_monitoring;
    private RelativeLayout rl_realtime_alert, rl_alternative_route, rl_delay_options, rl_monitor_trip;
    private Spinner sp_realtime_alert, sp_alternative_route, sp_delay_options, sp_monitor_trip;
    private LinearLayout ll_snooze, ll_pause_resume, ll_modes, ll_plan_new_trip;
    private ConstraintLayout constraintLayout;
    private ImageView iv_snooze, iv_pause_resume, iv_advanced_settings;
    private EditText et_trip_name;
    private TextView tv_trip_monitoring_title, tv_last_checked, tv_trip_monitoring_content,
            tv_snooze, tv_pause_resume, tv_itinerary_start_end_time, tv_itinerary_duration;
    private LinearLayout ll_monday, ll_tuesday, ll_wednesday, ll_thursday, ll_friday, ll_saturday, ll_sunday;
    private TextView tv_monday, tv_tuesday, tv_wednesday, tv_thursday, tv_friday, tv_saturday, tv_sunday;
    private ImageView iv_monday, iv_tuesday, iv_wednesday, iv_thursday, iv_friday, iv_saturday, iv_sunday;
    private CheckBox cb_monday, cb_tuesday, cb_wednesday, cb_thursday, cb_friday, cb_saturday, cb_sunday;
    private LinearLayout llCheckingItineraryExistence;
    private TextView tvItineraryExistenceHint;
    private TextView tv_cancel_update_trip, tv_save_trip;
    private final Itinerary itineraryForNewTrip = null;

    private LinearLayout llTravelCompanions, llObserversWatchingThisTrip, llCompanionOnThisTrip;
    private TextView tvObserversWatchingThisTrip, tvCompanionOnThisTrip, tvPrimaryTraveler;
    private ImageView ivObserversWatchingThisTrip, ivCompanionOnThisTrip;
    private String myUserId = null;
    private SpinnerAdapter alertOnJourneySpinnerAdapter, notifyOnItineraryChangeSpinnerAdapter,
            leadTimeSpinnerAdapter, delaySpinnerAdapter;
    private int alertOnJourneyIndex = Constants.DEFAULT_NOTIFY_ON_ALERT_INDEX,
            notifyOnItineraryChangeIndex = Constants.DEFAULT_NOTIFY_ON_ITINERARY_CHANGE_INDEX,
            leadTimeIndex = Constants.DEFAULT_LEAD_TIME_INDEX,
            delayIndex = Constants.DEFAULT_DELAY_INDEX;
    private ItineraryExistence currentItineraryExistence = null;
    private TextView tv_notify_title;
    private LinearLayout ll_notification_layout, ll_notification_warning;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (mFragmentView != null) {
            return mFragmentView;
        }

        if (Utilities.checkIfToDisplayAccessibilityUI(requireActivity())) {
            mFragmentView = inflater.inflate(R.layout.fragment_my_trips_accessibility, container, false);
        } else {
            mFragmentView = inflater.inflate(R.layout.fragment_my_trips, container, false);
        }

        // Inflate the layout for this fragment
        tvTitle = mFragmentView.findViewById(R.id.tv_title);
        ivBack = mFragmentView.findViewById(R.id.iv_back);
        tvErrorMessage = mFragmentView.findViewById(R.id.tv_error_message);
        ivBack.setVisibility(View.GONE);
        tvTitle.setText(R.string.saved_trips);
        setupSavedTripsWidgets();
        setupEditTripWidgets();
        setupTripDaysControllers();
        svEditTrip.setVisibility(View.GONE);

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backKeyHandler();
            }
        });
        return mFragmentView;
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

    public void setNotificationTripID(String tripID) {
        notificationTripID = tripID;
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
                getActivity(), data,
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
            } else {
                if (monitoredTrip.getItinerary() != null) {
                    return Utilities.formatTimestampForSelectedDateInList(monitoredTrip.getItinerary().getStartTime());
                } else {
                    return Utilities.formatTimestampForSelectedDateInList(monitoredTrip.getDateCreated());
                }
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
                if (Utilities.checkIfToDisplayAccessibilityUI(requireActivity())) {
                    convertView = l_Inflater.inflate(
                            R.layout.item_saved_trip_list_accessibility, null);
                } else {
                    convertView = l_Inflater.inflate(
                            R.layout.item_saved_trip_list, null);
                }
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
                TextView tvNotificationsLeadingDescription = convertView.findViewById(R.id.tv_notifications_leading_description);
                ImageView ivPauseResume = convertView.findViewById(R.id.iv_pause_resume);
                LinearLayout llPauseResume = convertView.findViewById(R.id.ll_pause_resume);
                LinearLayout llEdit = convertView.findViewById(R.id.ll_edit);
                LinearLayout llDelete = convertView.findViewById(R.id.ll_delete);

                String happensOnDates = populateHappensOnDates(monitoredTrip);
                tvHappensOnDates.setText(Html.fromHtml(getString(R.string.happens_on, happensOnDates)));

                if (monitoredTrip.isActive()) {
                    tvNotificationsLeading.setText(Html.fromHtml(getString(R.string.notifications_lead_time,
                            monitoredTrip.getLeadTimeInMinutes())));
                    ivPauseResume.setImageResource(R.drawable.ic_saved_trip_notification);
                    tvNotificationsLeadingDescription.setVisibility(View.VISIBLE);
                } else {
                    tvNotificationsLeading.setText(Html.fromHtml(getString(R.string.notifications_disabled)));
                    ivPauseResume.setImageResource(R.drawable.ic_saved_trip_muted);
                    tvNotificationsLeadingDescription.setVisibility(View.GONE);
                }

                LinearLayout llStartTrip = convertView.findViewById(R.id.ll_start_trip);
                TextView tvStartTripDivider = convertView.findViewById(R.id.tv_start_trip_divider);
                if (Configurations.getBoolean("enable_start_route", false)) {
                    llStartTrip.setVisibility(View.VISIBLE);
                    tvStartTripDivider.setVisibility(View.VISIBLE);
                    llStartTrip.setTag(position);
                    llStartTrip.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (v.getTag() != null) {
                                int position = (Integer) v.getTag();

                                if (monitoredTripResponse != null && monitoredTripResponse.getData() != null
                                        && position < monitoredTripResponse.getData().size()) {
                                    MonitoredTrip currentStartedTrip = monitoredTripResponse.getData().get(position);
                                    Intent intent = new Intent(getActivity(), ActiveRouteActivity.class);//IndoorNavigationActivity.class);
                                    intent.putExtra("trip_id", currentStartedTrip.getId());
                                    intent.putExtra("monitored_trip", currentStartedTrip);
                                    startActivity(intent);
                                }

                            }
                        }
                    });
                } else {
                    llStartTrip.setVisibility(View.GONE);
                    tvStartTripDivider.setVisibility(View.GONE);
                }
                LinearLayout llPreviewTrip = convertView.findViewById(R.id.ll_preview_trip);
                llPreviewTrip.setTag(position);
                llPreviewTrip.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (v.getTag() != null) {
                            int position = (Integer) v.getTag();

                            if (monitoredTripResponse != null && monitoredTripResponse.getData() != null
                                    && position < monitoredTripResponse.getData().size()) {
                                MonitoredTrip currentStartedTrip = monitoredTripResponse.getData().get(position);
                                Intent intent = new Intent(getActivity(), PreviewTripActivity.class);
                                intent.putExtra("trip_id", currentStartedTrip.getId());
                                intent.putExtra("monitored_trip", currentStartedTrip);

                                intent.putExtra("show_current_location", currentStartedTrip.getUserId() != null && currentStartedTrip.getUserId().equals(myUserId));
                                startActivity(intent);
                            }

                        }
                    }
                });

                llPauseResume.setTag(position);
                llPauseResume.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int position = (Integer) v.getTag();

                        if (monitoredTripResponse != null && monitoredTripResponse.getData() != null
                                && position < monitoredTripResponse.getData().size()) {
                            MonitoredTrip currentMonitoredTrip = monitoredTripResponse.getData().get(position);
                            currentMonitoredTrip.setActive(!currentMonitoredTrip.isActive());
                            currentMonitoredTrip.setInactive(!currentMonitoredTrip.isActive());

                            updateSavedTrip(currentMonitoredTrip.getId(), currentMonitoredTrip);
                            if (savedTripListAdapter != null) {
                                savedTripListAdapter.notifyDataSetChanged();
                            }
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

                            currentItineraryExistence = null;
                            if (selectedMonitoredTrip.getItineraryExistence() != null) {
                                currentItineraryExistence = selectedMonitoredTrip.getItineraryExistence();
                            }
                            refreshDatesWidgets(currentItineraryExistence);
                            showAndPopulateEditTrip();
                            if (selectedMonitoredTrip.getItinerary() != null) {
                                refreshOnSelectedDate(selectedMonitoredTrip.getItinerary().getStartTime());
                            } else {
                                refreshOnSelectedDate(selectedMonitoredTrip.getDateCreated());
                            }
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

                            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());

                            // set title
                            alertDialogBuilder.setTitle("");

                            // set dialog message
                            alertDialogBuilder
                                    .setMessage(getString(R.string.delete_saved_trip_warning))
                                    .setCancelable(false)
                                    .setNegativeButton(getString(R.string.cancel), null)
                                    .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
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
                        ImageView ivFirstMode = new ImageView(systemContext);
                        llModes.addView(ivFirstMode);
                        loadIconAndBackgroundToMode(ivFirstMode, firstItineraryLeg);

                        if (secondItineraryLeg != null) {
                            ImageView ivDashMode = createNormalImageView(R.drawable.ic_dash);
                            llModes.addView(ivDashMode);

                            ImageView ivSecondMode = new ImageView(systemContext);
                            llModes.addView(ivSecondMode);
                            loadIconAndBackgroundToMode(ivSecondMode, secondItineraryLeg);
                        }

                    }

                    String startTime = Utilities.formatTimestamp(getActivity(), itinerary.getStartTime());
                    String endTime = Utilities.formatTimestamp(getActivity(), itinerary.getEndTime());

                    tvStartEndTime.setText(String.format("%s - %s", startTime, endTime));
                }

                //created by myself, can edit, delete, start trip and change notification
                TextView tvEditTrip = convertView.findViewById(R.id.tv_edit_trip);
                TextView tvNotification = convertView.findViewById(R.id.tv_pause_resume);
                TextView tvStartTrip = convertView.findViewById(R.id.tv_start_trip);
                TextView tvDelete = convertView.findViewById(R.id.tv_delete);

                ImageView ivNotification = convertView.findViewById(R.id.iv_pause_resume);
                ImageView ivStartTrip = convertView.findViewById(R.id.iv_start_trip);
                ImageView ivDelete = convertView.findViewById(R.id.iv_delete);

                if (monitoredTrip.getUserId() != null && !monitoredTrip.getUserId().equals(myUserId)) {
                    tvEditTrip.setText(getString(R.string.view));

                    llPauseResume.setOnClickListener(null);
                    llDelete.setOnClickListener(null);
                    llStartTrip.setOnClickListener(null);

                    tvNotification.setTextColor(getResources().getColor(R.color.colorGrey, null));
                    tvStartTrip.setTextColor(getResources().getColor(R.color.colorGrey, null));
                    tvDelete.setTextColor(getResources().getColor(R.color.colorGrey, null));
                    ivNotification.setColorFilter(getResources().getColor(R.color.colorGrey, null));
                    ivStartTrip.setColorFilter(getResources().getColor(R.color.colorGrey, null));
                    ivDelete.setColorFilter(getResources().getColor(R.color.colorGrey, null));
                } else // view only
                {
                    tvEditTrip.setText(getString(R.string.edit));

                    tvNotification.setTextColor(getResources().getColor(R.color.colorBlack, null));
                    tvStartTrip.setTextColor(getResources().getColor(R.color.colorBlack, null));
                    tvDelete.setTextColor(getResources().getColor(R.color.colorBlack, null));

                    ivNotification.setColorFilter(null);
                    ivStartTrip.setColorFilter(null);
                    ivDelete.setColorFilter(null);
                }

            }
            return convertView;
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
                String mappedMode = Utilities.getMappedModeForItinerary(getActivity(), itineraryLeg.getRouteId(),
                        itineraryLeg.getMode(), itineraryLeg.getRouteShortName(), itineraryLeg.getRouteLongName()).toLowerCase();

                ivMode.setImageResource(Utilities.getModeImageByMode(mappedMode,
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
    }

    private String getCompanionOnThisTrip() {
        if (monitoredTripUpdateRequest.getCompanion() != null && monitoredTripUpdateRequest.getCompanion().getEmail() != null) {
            return monitoredTripUpdateRequest.getCompanion().getEmail();
        } else {
            return "";
        }
    }

    private String getObserversWatchingThisTrip() {
        StringBuilder observers = new StringBuilder();
        if (monitoredTripUpdateRequest.getObservers() != null) {
            for (RelatedUser relatedUser : monitoredTripUpdateRequest.getObservers()) {
                if (relatedUser != null && relatedUser.getEmail() != null) {
                    if (observers.length() > 0) {
                        observers.append(", ");
                    }
                    observers.append(relatedUser.getEmail());
                }
            }

            return observers.toString();
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

    private void refreshPrimaryTravellerIfNeeded() {
        if (svEditTrip.getVisibility() == View.VISIBLE && monitoredTripUpdateRequest != null) {
            if (monitoredTripUpdateRequest.getUserId() != null && monitoredTripUpdateRequest.getUserId().equals(myUserId)
                    && monitoredTripUpdateRequest.getPrimaryTravelerId() != null && monitoredTripUpdateRequest.getPrimaryTravelerId().equals(myUserId)) {
                tvPrimaryTraveler.setText(getString(R.string.myself));
            } else {
                setPrimaryTravellerOthers();
            }
        }
    }

    private void refreshTravelCompanion() {
        getDependantMobilityProfiles();
        //created by myself
        if (monitoredTripUpdateRequest.getUserId() != null && monitoredTripUpdateRequest.getUserId().equals(myUserId)) {
            if (monitoredTripUpdateRequest.getPrimaryTravelerId() == null || monitoredTripUpdateRequest.getPrimaryTravelerId().equals(myUserId)) {
                //created for myself
                tvPrimaryTraveler.setText(getString(R.string.myself));
                enableCompanionAndObservers();

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
                                true, getString(R.string.no_options));

                        observersPopupSelectionDialog.show();
                    }
                });
            } else //created for others who choose me as companion, check primary
            {
                setPrimaryTravellerOthers();

                disableCompanionAndObservers();
            }
        } else //created by one of the dependants/companions, view only
        {
            setPrimaryTravellerOthers();
            disableCompanionAndObservers();
        }

        tvCompanionOnThisTrip.setText(getCompanionOnThisTrip());
        tvObserversWatchingThisTrip.setText(getObserversWatchingThisTrip());

    }

    private void setPrimaryTravellerOthers() {
        if (monitoredTripUpdateRequest.getPrimary() != null) {
            if (monitoredTripUpdateRequest.getPrimary().getEmail() != null) {
                if (MyApplication.getInstance().userPreferences != null
                        && monitoredTripUpdateRequest.getPrimary().getEmail().equals(MyApplication.getInstance().userPreferences.getEmail())) {
                    tvPrimaryTraveler.setText(getString(R.string.myself));
                } else {
                    tvPrimaryTraveler.setText(monitoredTripUpdateRequest.getPrimary().getEmail());
                }
            } else {
                tvPrimaryTraveler.setText("");
            }
        } else {
            if (monitoredTripUpdateRequest.getPrimaryTravelerId() != null) {
                if (monitoredTripUpdateRequest.getPrimaryTravelerId().equals(myUserId)) {
                    tvPrimaryTraveler.setText(getString(R.string.myself));
                } else {
                    if (MyApplication.getInstance().dependentMobilityProfiles != null) {
                        for (DependentMobilityProfile dependentMobilityProfile : MyApplication.getInstance().dependentMobilityProfiles) {
                            if (dependentMobilityProfile != null && dependentMobilityProfile.getUserId().equals(monitoredTripUpdateRequest.getPrimaryTravelerId())) {
                                tvPrimaryTraveler.setText(dependentMobilityProfile.getEmail());
                                break;
                            }
                        }
                    }
                }
            } else {
                tvPrimaryTraveler.setText(getString(R.string.myself));
            }
        }
    }

    private void setupEditTripWidgets() {
        if (mFragmentView == null) {
            return;
        }
        ll_trip_monitoring = mFragmentView.findViewById(R.id.ll_trip_monitoring);
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
        ll_snooze = mFragmentView.findViewById(R.id.ll_snooze);
        ll_pause_resume = mFragmentView.findViewById(R.id.ll_pause_resume);
        ll_plan_new_trip = mFragmentView.findViewById(R.id.ll_plan_new_trip);
        ll_modes = mFragmentView.findViewById(R.id.ll_modes);
        constraintLayout = mFragmentView.findViewById(R.id.cl_modes);
        iv_snooze = mFragmentView.findViewById(R.id.iv_snooze);
        iv_pause_resume = mFragmentView.findViewById(R.id.iv_pause_resume);
        iv_advanced_settings = mFragmentView.findViewById(R.id.iv_advanced_settings);
        et_trip_name = mFragmentView.findViewById(R.id.et_trip_name);
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
        tv_trip_monitoring_title = mFragmentView.findViewById(R.id.tv_trip_monitoring_title);
        tv_last_checked = mFragmentView.findViewById(R.id.tv_last_checked);
        tv_trip_monitoring_content = mFragmentView.findViewById(R.id.tv_trip_monitoring_content);
        tv_snooze = mFragmentView.findViewById(R.id.tv_snooze);
        tv_pause_resume = mFragmentView.findViewById(R.id.tv_pause_resume);
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

        tv_cancel_update_trip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                svEditTrip.setVisibility(View.GONE);
                lvSavedTrip.setVisibility(View.VISIBLE);
                llEmptyTrips.setVisibility(View.GONE);

                ivBack.setVisibility(View.GONE);
                tvTitle.setText(R.string.saved_trips);
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
                        svEditTrip.scrollToDescendant(tvTripNameTitle);
                        tvTripNameTitle.requestFocus();
                    } else {
                        et_trip_name.requestFocus();
                    }

                    return;
                }
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
                updateSavedTrip();
            }
        });

        ll_pause_resume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                monitoredTripUpdateRequest.setActive(!monitoredTripUpdateRequest.isActive());
                refreshTripMonitoring();
            }
        });

        ll_plan_new_trip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getActivity() != null && selectedMonitoredTrip != null) {

                    ((TabActivity) getActivity()).loadPlanTripFromMyTrips(selectedMonitoredTrip.getFrom(),
                            selectedMonitoredTrip.getTo());
                }
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
    }

    private void refreshTripMonitoring() {
        if (monitoredTripUpdateRequest.isActive() && !monitoredTripUpdateRequest.isSnoozed()) {
            ll_pause_resume.setVisibility(View.VISIBLE);
            ll_snooze.setVisibility(View.VISIBLE);
            tv_pause_resume.setText(getString(R.string.pause));
            iv_pause_resume.setImageResource(R.drawable.btn_pause);
            iv_snooze.setImageResource(R.drawable.btn_pause);
            tv_snooze.setText(getString(R.string.snoose_for_rest_today));
            ll_pause_resume.setContentDescription("R.string.pause");

            ItineraryForSaveTrip itinerary = monitoredTripUpdateRequest.getItinerary();

            String startTime = Utilities.formatTimestamp(getActivity(), itinerary.getStartTime());

            String realMonitorTime = Utilities.formatTimestamp(getActivity(),
                    itinerary.getStartTime() - (long) monitoredTripUpdateRequest.getLeadTimeInMinutes() * 60 * 1000);

            if (ll_plan_new_trip != null) {
                ll_plan_new_trip.setVisibility(View.GONE);
            }
            tv_trip_monitoring_content.setText(getString(R.string.trip_is_due_to, startTime, realMonitorTime));
            tv_trip_monitoring_title.setText(getString(R.string.loading));
        } else if (!monitoredTripUpdateRequest.isActive()) {
            ll_pause_resume.setVisibility(View.VISIBLE);
            ll_snooze.setVisibility(View.GONE);
            tv_pause_resume.setText(getString(R.string.resume));
            iv_pause_resume.setImageResource(R.drawable.btn_play);
            tv_trip_monitoring_title.setText(getString(R.string.pause_trip_title));
            tv_trip_monitoring_content.setText(getString(R.string.pause_trip_hint));
            ll_pause_resume.setContentDescription(getString(R.string.resume));
        } else {
            ll_pause_resume.setVisibility(View.GONE);
            ll_snooze.setVisibility(View.VISIBLE);

            iv_snooze.setImageResource(R.drawable.btn_play);
            tv_snooze.setText(getString(R.string.unsnooze_trip_analysis));

            tv_trip_monitoring_title.setText(getString(R.string.unsnooze_trip_analysis_title));
            tv_trip_monitoring_content.setText(getString(R.string.unsnooze_trip_analysis_hint));
        }

        checkItineraryFromItineraryExistence();
        setupTravelCompanion();
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

        notifyOnItineraryChangeSpinnerAdapter = new SpinnerAdapter(getActivity(), R.layout.item_spinner,
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

        leadTimeSpinnerAdapter = new SpinnerAdapter(getActivity(), R.layout.item_spinner,
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

        delaySpinnerAdapter = new SpinnerAdapter(getActivity(), R.layout.item_spinner,
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

        alertOnJourneySpinnerAdapter = new SpinnerAdapter(getActivity(), R.layout.item_spinner,
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
        boolean viewOnly = false;
        svEditTrip.setVisibility(View.VISIBLE);
        ivBack.setVisibility(View.VISIBLE);
        if (monitoredTripUpdateRequest != null && monitoredTripUpdateRequest.getUserId() != null
                && !monitoredTripUpdateRequest.getUserId().equals(myUserId)) {
            viewOnly = true;
        }

        if (viewOnly) {
            tvTitle.setText(R.string.view_saved_trip);
        } else {
            tvTitle.setText(R.string.edit_saved_trip);
        }

        svEditTrip.post(new Runnable() {
            @Override
            public void run() {
                svEditTrip.scrollTo(0, 0);
            }
        });
        lvSavedTrip.setVisibility(View.GONE);
        llEmptyTrips.setVisibility(View.GONE);
        ll_monitor_trip.setVisibility(View.GONE);
        iv_advanced_settings.setImageResource(R.drawable.ic_resume);

        ll_trip_monitoring.setVisibility(View.VISIBLE);
        refreshTripMonitoring();
        refreshLastChecked();

        refreshTripDates();
        refreshDateRows();
        setPreviousSelectedStatus();

        setSpinnerIndex();

//ll_plan_new_trip
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
                String mode = null;
                String previousMode = null;

                for (int i = 0; i < itinerary.getLegs().size(); i++) {
                    ItineraryLeg itineraryLeg = itinerary.getLegs().get(i);
                    if (itineraryLeg.getMode() != null) {
                        mode = Utilities.getMappedModeForItinerary(getActivity(), itineraryLeg.getRouteId(),
                                itineraryLeg.getMode(), itineraryLeg.getRouteShortName(), itineraryLeg.getRouteLongName()).toLowerCase();

                        if (mode.equalsIgnoreCase("walk")) {
                            if (itineraryLeg.getDistance() <= 400) {
                                continue;
                            }
                        }
                        if (previousMode == null || !previousMode.equals(mode)) {
                            previousMode = mode;

                            View modeView = ((LayoutInflater) systemContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.view_group_itinerary_mode, null, false);
                            ImageView ivItineraryMode = modeView.findViewById(R.id.iv_itinerary_mode);
                            RelativeLayout rlItineraryMode = modeView.findViewById(R.id.rl_itinerary_mode);
                            ivItineraryMode.setColorFilter(getResources().getColor(android.R.color.white));

                            ivItineraryMode.setImageResource(Utilities.getModeImageByMode(mode,
                                    itineraryLeg.isRentedVehicle(), requireActivity(), ivItineraryMode, rlItineraryMode));
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

                    if (itineraryLeg.getRouteShortName() != null && !itineraryLeg.getRouteShortName().isEmpty()) {
                        tvRouteNumber.setText(itineraryLeg.getRouteShortName());
                    } else if (itineraryLeg.getOriginalRoute() != null) {
                        if (itineraryLeg.getOriginalRoute().getShortName() != null && !itineraryLeg.getOriginalRoute().getShortName().isEmpty()) {
                            tvRouteNumber.setText(itineraryLeg.getOriginalRoute().getShortName());
                        }
                    }

                    if (!mode.equalsIgnoreCase("walk")
                            && !mode.equalsIgnoreCase("bike")
                            && !mode.equalsIgnoreCase("bicycle")) {
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

            String startTime = Utilities.formatTimestamp(getActivity(), itinerary.getStartTime());
            String endTime = Utilities.formatTimestamp(getActivity(), itinerary.getEndTime());

            tv_itinerary_start_end_time.setText(Html.fromHtml(getString(R.string.itinerary_start_end_time, startTime, endTime)));
        }

        if (viewOnly) {
            ll_pause_resume.setVisibility(View.GONE);
            ll_snooze.setVisibility(View.GONE);
            ll_plan_new_trip.setVisibility(View.GONE);

            et_trip_name.setEnabled(false);
            rbEachWeek.setEnabled(false);
            rbSpecificDate.setEnabled(false);

            sp_realtime_alert.setEnabled(false);
            sp_realtime_alert.setClickable(false);
            sp_realtime_alert.setFocusable(false);
            sp_alternative_route.setEnabled(false);
            sp_alternative_route.setClickable(false);
            sp_alternative_route.setFocusable(false);
            sp_delay_options.setEnabled(false);
            sp_delay_options.setClickable(false);
            sp_delay_options.setFocusable(false);
            sp_monitor_trip.setEnabled(false);
            sp_monitor_trip.setClickable(false);
            sp_monitor_trip.setFocusable(false);

            tv_save_trip.setVisibility(View.GONE);
        } else {
            et_trip_name.setEnabled(true);
            rbEachWeek.setEnabled(true);
            rbSpecificDate.setEnabled(true);

            sp_realtime_alert.setEnabled(true);
            sp_realtime_alert.setClickable(true);
            sp_realtime_alert.setFocusable(true);
            sp_alternative_route.setEnabled(true);
            sp_alternative_route.setClickable(true);
            sp_alternative_route.setFocusable(true);
            sp_delay_options.setEnabled(true);
            sp_delay_options.setClickable(true);
            sp_delay_options.setFocusable(true);
            sp_monitor_trip.setEnabled(true);
            sp_monitor_trip.setClickable(true);
            sp_monitor_trip.setFocusable(true);
            tv_save_trip.setVisibility(View.VISIBLE);
        }
        refreshTravelCompanion();

        refreshDatesWidgets(currentItineraryExistence);
    }

    private void setupSavedTripsWidgets() {
        svEditTrip = mFragmentView.findViewById(R.id.sv_edit_trip);
        llEmptyTrips = mFragmentView.findViewById(R.id.ll_empty_trips);
        lvSavedTrip = mFragmentView.findViewById(R.id.lv_saved_trip);
        pbSavedTripList = mFragmentView.findViewById(R.id.pb_saved_trip_list);

        savedTripListAdapter = getSavedTripListAdapter();
        lvSavedTrip.setAdapter(savedTripListAdapter);
    }

    private void showErrorMessage(String errorMessage) {
        tvErrorMessage.setVisibility(View.VISIBLE);
        tvErrorMessage.setText(errorMessage);
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
                        if (MyApplication.getInstance().userPreferences.isHasConsentedToTerms()) {
                            getSavedTripList();
                        } else {
                            //show error message
                            hideProgressBar();
                            showErrorMessage(getString(R.string.consent_to_terms_to_save_trips));
                        }
                    } catch (Exception e) {
                        hideProgressBar();
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

                //show error message
                if (error != null && error.networkResponse != null) {
                    if (error.networkResponse.statusCode == 401) {
                        showErrorMessage(getString(R.string.retrieve_user_info_failed_unauthorized));
                    } else {
                        showErrorMessage(getString(R.string.retrieve_user_info_failed_other));
                    }
                }
            }
        };

        String url = Resource.API_ACCOUNT_FROM_TOKEN;

        volleyAccountStringRequest(Request.Method.GET, url, "", listener, errorListener);
    }

    private void hideProgressBar() {
        if (getActivity() != null) {
            ((TabActivity) getActivity()).hideProgressBar();
        }
    }

    private void retrieveUserInfo(String accessToken) {
        if (svEditTrip.getVisibility() != View.VISIBLE) {
            if (getActivity() != null) {
                ((TabActivity) getActivity()).showProgressBar();
            }
        }
        Auth0 account = new Auth0(Configurations.getString("auth0_client_id", ""), Configurations.getString("auth0_domain", ""));
        Callback<UserProfile, AuthenticationException> userInfoCallback = new Callback<UserProfile, AuthenticationException>() {
            @Override
            public void onSuccess(UserProfile userProfile) {
                Gson gson = new Gson();
                Configurations.setString("account_user_profile", gson.toJson(userProfile));

                if (userProfile != null) {
                    if (Boolean.TRUE.equals(userProfile.isEmailVerified())) {
                        getFromToken();
                    } else {
                        hideProgressBar();

                        showErrorMessage(getString(R.string.verify_email_to_save_trips));
                    }
                }
            }

            @Override
            public void onFailure(@NonNull AuthenticationException e) {
                hideProgressBar();
                if (e.getStatusCode() == 401) {
                    showErrorMessage(systemContext.getString(R.string.retrieve_user_info_failed_unauthorized));
                } else {
                    showErrorMessage(systemContext.getString(R.string.retrieve_user_info_failed_other));
                }
            }
        };
        AuthenticationAPIClient authenticationAPIClient = new AuthenticationAPIClient(account);
        authenticationAPIClient.userInfo(accessToken).start(userInfoCallback);
    }

    private void getSavedTripList() {
        if (svEditTrip.getVisibility() != View.VISIBLE) {
            if (getActivity() != null) {
                ((TabActivity) getActivity()).showProgressBar();
            }
        }
        Response.Listener listener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response != null) {
                    response = new String(response.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
                }
                hideProgressBar();
                tvErrorMessage.setVisibility(View.GONE);
                Gson gson = new Gson();
                try {
                    MyApplication.getInstance().gMonitoredTripResponse = gson.fromJson(response, MonitoredTripResponse.class);
                    monitoredTripResponse = MyApplication.getInstance().gMonitoredTripResponse;
                    if (svEditTrip.getVisibility() != View.VISIBLE) {
                        if (monitoredTripResponse != null && monitoredTripResponse.getData() != null
                                && monitoredTripResponse.getData().size() > 0) {
                            lvSavedTrip.setVisibility(View.VISIBLE);
                            llEmptyTrips.setVisibility(View.GONE);
                        } else {
                            lvSavedTrip.setVisibility(View.GONE);
                            llEmptyTrips.setVisibility(View.VISIBLE);
                        }
                    }
                    if (savedTripListAdapter != null) {
                        savedTripListAdapter.notifyDataSetChanged();
                    }

                    if (notificationTripID != null) {
                        locateAndOpenTheTrip();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        };
        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                hideProgressBar();
                Logger.e("account", error.toString());

                errorHandler(error, getString(R.string.failed_get_trip_list));
            }
        };

        String url = Resource.API_ACCOUNT_MONITORED_TRIP;

        volleyAccountStringRequest(Request.Method.GET, url, "", listener, errorListener);
    }

    private void locateAndOpenTheTrip() {
        if (monitoredTripResponse != null && monitoredTripResponse.getData() != null) {
            for (int i = 0; i < monitoredTripResponse.getData().size(); i++) {
                MonitoredTrip trip = monitoredTripResponse.getData().get(i);
                if (notificationTripID.equals(trip.getId())) {
                    selectedMonitoredTrip = trip;
                    monitoredTripUpdateRequest.copyFromMonitoredTrip(selectedMonitoredTrip);

                    currentItineraryExistence = null;
                    if (selectedMonitoredTrip.getItineraryExistence() != null) {
                        currentItineraryExistence = selectedMonitoredTrip.getItineraryExistence();
                    }
                    refreshDatesWidgets(currentItineraryExistence);
                    showAndPopulateEditTrip();

                    lvSavedTrip.setSelection(i);
                    if (selectedMonitoredTrip.getItinerary() != null) {
                        refreshOnSelectedDate(selectedMonitoredTrip.getItinerary().getStartTime());
                    } else {
                        refreshOnSelectedDate(selectedMonitoredTrip.getDateCreated());
                    }
                }
            }
        }

        notificationTripID = null;
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

    private void refreshOnSelectedDate(long dateCreated) {
        if (rbSpecificDate != null) {
            rbSpecificDate.setText(getString(R.string.only_on_selected_date,
                    Utilities.formatTimestampForSelectedDate(dateCreated)));
        }
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

    private void checkItineraryFromItineraryExistence() {
        llCheckingItineraryExistence.setVisibility(View.GONE);
        tvItineraryExistenceHint.setVisibility(View.VISIBLE);
        Date dt = new Date();
        dt.setTime(selectedMonitoredTrip.getItinerary().getStartTime());

        boolean showPlanNewTrip = false;
        try {
            currentItineraryExistence = selectedMonitoredTrip.getItineraryExistence();
            refreshDatesWidgets(currentItineraryExistence);
            long minDiff = Long.MAX_VALUE;
            int hour = dt.getHours();
            int minute = dt.getMinutes();
            ArrayList<Date> weekdays = new ArrayList<Date>();
            Date monday = extractAvailableWeekday(currentItineraryExistence.getMonday(), hour, minute);
            if (monday != null) {
                weekdays.add(monday);
            }
            Date tuesday = extractAvailableWeekday(currentItineraryExistence.getTuesday(), hour, minute);
            if (tuesday != null) {
                weekdays.add(tuesday);
            }
            Date wednesday = extractAvailableWeekday(currentItineraryExistence.getWednesday(), hour, minute);
            if (wednesday != null) {
                weekdays.add(wednesday);
            }
            Date thursday = extractAvailableWeekday(currentItineraryExistence.getThursday(), hour, minute);
            if (thursday != null) {
                weekdays.add(thursday);
            }
            Date friday = extractAvailableWeekday(currentItineraryExistence.getFriday(), hour, minute);
            if (friday != null) {
                weekdays.add(friday);
            }
            Date saturday = extractAvailableWeekday(currentItineraryExistence.getSaturday(), hour, minute);
            if (saturday != null) {
                weekdays.add(saturday);
            }
            Date sunday = extractAvailableWeekday(currentItineraryExistence.getSunday(), hour, minute);
            if (sunday != null) {
                weekdays.add(sunday);
            }
            if (weekdays.size() == 0) {
                tv_trip_monitoring_title.setText(getString(R.string.no_available_dates));
                showPlanNewTrip = true;
            } else {
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
                    String startTime = Utilities.formatTimestamp(getActivity(), selectedMonitoredTrip.getItinerary().getStartTime());

                    tv_trip_monitoring_title.setText(getString(R.string.next_trip_starts_on, dateString, startTime));
                } else {
                    tv_trip_monitoring_title.setText(getString(R.string.no_available_dates));
                    showPlanNewTrip = true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (selectedMonitoredTrip != null
                && selectedMonitoredTrip.getJourneyState() != null
                && selectedMonitoredTrip.getJourneyState().getTripStatus() != null) {

            String tripStatus = selectedMonitoredTrip.getJourneyState().getTripStatus();

            if (monitoredTripUpdateRequest.isActive() && !monitoredTripUpdateRequest.isSnoozed()) {
                switch (tripStatus.toUpperCase(Locale.US)) {
                    case "TRIP_UPCOMING":
                        upcomingTripRender(selectedMonitoredTrip);
                        break;

                    case "TRIP_ACTIVE":
                        activeTripRender(selectedMonitoredTrip);
                        break;

                    case "NO_LONGER_POSSIBLE":
                        tv_trip_monitoring_title.setText(R.string.no_longer_possible_heading);
                        tv_trip_monitoring_content.setText(R.string.no_longer_possible_description);
                        break;

                    case "NEXT_TRIP_NOT_POSSIBLE":

                        tv_trip_monitoring_title.setText(R.string.unable_to_monitor_heading);
                        tv_trip_monitoring_content.setText(R.string.unable_to_monitor_description);
                        showPlanNewTrip = true;
                        ll_snooze.setVisibility(View.VISIBLE);
                        tv_snooze.setText(R.string.unsnooze_trip);
                        if (ll_plan_new_trip != null) {
                            ll_plan_new_trip.setVisibility(showPlanNewTrip ? View.VISIBLE : View.GONE);
                        }

                        break;

                    case "PAST_TRIP":
                        tv_trip_monitoring_title.setText(R.string.past_trip_heading);
                        tv_trip_monitoring_content.setText(R.string.past_trip_description);
                        break;
                }
            } else {
                if (tripStatus.equalsIgnoreCase("NEXT_TRIP_NOT_POSSIBLE")) {
                    tv_trip_monitoring_title.setText(R.string.unable_to_monitor_heading);
                    tv_trip_monitoring_content.setText(R.string.unable_to_monitor_description);
                    showPlanNewTrip = true;
                    if (ll_plan_new_trip != null) {
                        ll_plan_new_trip.setVisibility(showPlanNewTrip ? View.VISIBLE : View.GONE);
                    }
                }
            }
        }

        if (ll_plan_new_trip != null) {
            if (showPlanNewTrip) {
                ll_plan_new_trip.setVisibility(View.VISIBLE);
            } else {
                ll_plan_new_trip.setVisibility(View.GONE);
            }
        }

    }

    private void refreshDateRows() {
        if (cb_monday.isChecked() || cb_tuesday.isChecked() || cb_wednesday.isChecked() ||
                cb_thursday.isChecked() || cb_friday.isChecked() || cb_saturday.isChecked() ||
                cb_sunday.isChecked()) {
            llEachWeek.setVisibility(View.VISIBLE);
            rbEachWeek.setChecked(true);
            rbSpecificDate.setChecked(false);
        } else {
            llEachWeek.setVisibility(View.GONE);
            rbEachWeek.setChecked(false);
            rbSpecificDate.setChecked(true);
        }
    }

    private void upcomingTripRender(MonitoredTrip item) {
        JourneyState journeyState = item.getJourneyState();
        Itinerary matchingItinerary = journeyState.getMatchingItinerary();
        if (matchingItinerary != null) {
            long startTime = matchingItinerary.getStartTime();
            long currentTime = System.currentTimeMillis();
            int differenceInMinutes = (int) ((currentTime - startTime) / 60000);

            String startTimeString = Utilities.formatTimestampPDT(getActivity(), startTime);
            String realMonitorTime = Utilities.formatTimestampPDT(getActivity(),
                    startTime - (long) item.getLeadTimeInMinutes() * 60 * 1000);
            tv_trip_monitoring_content.setText(getString(R.string.trip_is_due_to, startTimeString, realMonitorTime));

            if (Math.abs(differenceInMinutes) > item.getLeadTimeInMinutes()) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE MM/dd/yyyy", Locale.getDefault());
                SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm a", Locale.US);
                dateFormat.setTimeZone(TimeZone.getTimeZone(TARGET_TIMEZONE_ID));
                timeFormat.setTimeZone(TimeZone.getTimeZone(TARGET_TIMEZONE_ID));
                Date date = new Date(startTime);

                String dateString = dateFormat.format(date);
                String timeString = timeFormat.format(date);
                tv_trip_monitoring_title.setText(getString(R.string.next_trip_starts_on, dateString, timeString));
            } else {
                if (journeyState.isHasRealtimeData()) {
                    int departureDeviationSeconds =
                            (int) ((startTime - journeyState.getScheduledDepartureTimeEpochMillis()) / 1000);
                    int absDeviation = Math.abs(departureDeviationSeconds);
                    Constants.REALTIME_STATUS tripStatus = getTripStatus(true, departureDeviationSeconds, 60);

                    if (tripStatus == Constants.REALTIME_STATUS.ON_TIME) {
                        tv_trip_monitoring_title.setText(getString(R.string.trip_starting_soon_on_time));
                    } else if (tripStatus == Constants.REALTIME_STATUS.LATE) {
                        String formattedTime = formatDuration(absDeviation);
                        tv_trip_monitoring_title.setText(getString(R.string.trip_start_time_delayed, formattedTime));
                    } else {
                        String formattedTime = formatDuration(absDeviation);
                        tv_trip_monitoring_title.setText(getString(R.string.trip_start_time_earlier, formattedTime));
                    }
                } else {
                    tv_trip_monitoring_title.setText(getString(R.string.trip_starting_soon_no_realtime));
                }
            }
        }
    }

    private String formatDuration(int totalSeconds) {
        int hours = totalSeconds / 3600;
        int minutes = (totalSeconds % 3600) / 60;
        int seconds = totalSeconds % 60;

        StringBuilder sb = new StringBuilder();
        if (hours > 0) {
            sb.append(hours).append(" hour");
            if (hours > 1) sb.append("s");
            sb.append(" ");
        }
        if (minutes > 0) {
            sb.append(minutes).append(" minute");
            if (minutes > 1) sb.append("s");
            sb.append(" ");
        }
        if (seconds > 0 || sb.length() == 0) {
            sb.append(seconds).append(" second");
            if (seconds != 1) sb.append("s");
        }

        return sb.toString().trim();
    }


    private Constants.REALTIME_STATUS getTripStatus(boolean isRealtime, int delaySeconds, int onTimeThresholdSeconds) {
        if (isRealtime) {
            if (delaySeconds > onTimeThresholdSeconds) {
                return Constants.REALTIME_STATUS.LATE;
            } else if (delaySeconds < -onTimeThresholdSeconds) {
                return Constants.REALTIME_STATUS.EARLY;
            } else {
                return Constants.REALTIME_STATUS.ON_TIME;
            }
        } else {
            return Constants.REALTIME_STATUS.SCHEDULED;
        }
    }

    private void activeTripRender(MonitoredTrip item) {
        JourneyState journeyState = item.getJourneyState();
        if (journeyState.isHasRealtimeData()) {
            Itinerary matchingItinerary = journeyState.getMatchingItinerary();
            if (matchingItinerary != null) {
                long endTime = matchingItinerary.getEndTime();
                long scheduledArrivalTimeEpochMillis = journeyState.getScheduledArrivalTimeEpochMillis();
                int arrivalDeviationSeconds = (int) ((endTime - scheduledArrivalTimeEpochMillis) / 1000);
                Constants.REALTIME_STATUS tripStatus = getTripStatus(true, arrivalDeviationSeconds, 60);

                if (tripStatus == Constants.REALTIME_STATUS.ON_TIME) {
                    tv_trip_monitoring_title.setText(getString(R.string.trip_progress_on_time));
                } else if (tripStatus == Constants.REALTIME_STATUS.LATE) {
                    int minutes = Math.abs(arrivalDeviationSeconds) / 60;
                    int seconds = Math.abs(arrivalDeviationSeconds) % 60;
                    tv_trip_monitoring_title.setText(getString(R.string.trip_progress_delayed, minutes, seconds));
                } else {
                    tv_trip_monitoring_title.setText(getString(R.string.trip_progress_earlier, Math.abs(arrivalDeviationSeconds)));
                }

                String timeString = Utilities.formatTimestampPDT(getActivity(), endTime);
                tv_trip_monitoring_content.setText(getString(R.string.trip_is_due_to_arrive_at, timeString));
            }
        } else {
            Itinerary matchingItinerary = journeyState.getMatchingItinerary();
            if (matchingItinerary != null) {
                long endTime = matchingItinerary.getEndTime();
                String timeString = Utilities.formatTimestampPDT(getActivity(), endTime);
                tv_trip_monitoring_title.setText(getString(R.string.trip_progress_no_real_time));
                tv_trip_monitoring_content.setText(getString(R.string.trip_is_due_to_arrive_at, timeString));
            } else {
                tv_trip_monitoring_title.setText(getString(R.string.trip_progress_no_real_time));
                tv_trip_monitoring_content.setText("");
            }
        }
    }

    private void updateSavedTrip(String id, MonitoredTrip monitoredTrip) {
        if (id != null) {
            Response.Listener listener = new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Logger.d("account", response);
                }
            };
            Response.ErrorListener errorListener = new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Logger.e("account", error.toString());
                }
            };

            String url = String.format(Resource.API_ACCOUNT_UPDATE_SAVED_TRIP, id);

            Gson gson = new Gson();
            MonitoredTripUpdateRequest monitoredTripUpdateRequest = new MonitoredTripUpdateRequest();
            monitoredTripUpdateRequest.copyFromMonitoredTrip(monitoredTrip);
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
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
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
                                    ivBack.setVisibility(View.GONE);
                                    tvTitle.setText(R.string.saved_trips);
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

    private void errorHandler(VolleyError error, @NonNull String defaultMessage) {
        if (error != null && error instanceof NoConnectionError) {
            Utilities.showNormalPopupDialog(getActivity(), "", getString(R.string.connection_failed_msg));
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

    @Override
    public void onResume() {
        super.onResume();

        getDependantMobilityProfiles();
        tvErrorMessage.setVisibility(View.GONE);
        retrieveUserInfo(Configurations.getString("account_access_token", ""));

        refreshMyUserId();
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
    }

    private void refreshMyUserId() {
        Gson gson = new Gson();
        if (MyApplication.getInstance().userPreferences == null) {
            MyApplication.getInstance().userPreferences = gson.fromJson(Configurations.getString("account_user_preferences", ""),
                    UserPreferences.class);
        }

        if (MyApplication.getInstance().userPreferences.getId() != null) {
            myUserId = MyApplication.getInstance().userPreferences.getId();
        }
    }

    public boolean backKeyHandler() {
        if (svEditTrip.getVisibility() == View.VISIBLE) {
            svEditTrip.setVisibility(View.GONE);
            lvSavedTrip.setVisibility(View.VISIBLE);
            llEmptyTrips.setVisibility(View.GONE);
            ivBack.setVisibility(View.GONE);
            tvTitle.setText(R.string.saved_trips);
            return true;
        } else {
            return false;
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
                    refreshPrimaryTravellerIfNeeded();
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
}