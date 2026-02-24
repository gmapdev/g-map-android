package com.ibigroup.mobile.otp.android;

import static com.ibigroup.mobile.otp.android.utility.ServerDelegate.volleyAccountStringRequest;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.ibigroup.mobile.otp.android.api.Resource;
import com.ibigroup.mobile.otp.android.json.MobilityProfile;
import com.ibigroup.mobile.otp.android.json.UserPreferences;
import com.ibigroup.mobile.otp.android.utility.Configurations;
import com.ibigroup.mobile.otp.android.utility.Logger;
import com.ibigroup.mobile.otp.android.utility.Utilities;

import java.util.ArrayList;

public class AccessibilityProfileActivity extends BaseActivity {
    private ImageView ivLogo, ivMenu, ivBack, ivClose;
    private TextView tvTitle, tvSignIn, tvAppName;
    private TextView tvSavePreferences, tvNext;
    private CheckBox cbNoAssistiveDevice, cb_white_cane, cb_manual_walker, cb_wheeled_walker,
            cb_cane, cb_crutches, cb_stroller, cb_service_animal, cb_mobility_scooter, cb_electric_wheelchair,
            cb_manual_traditional_wheelchair, cb_yes, cb_no, cb_low_vision, cb_legally_blind, cb_no_vision_limitations;

    private UserPreferences userPreferences = null;
    private boolean isFromMySettings = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accessibility_profile);

        if (getIntent() != null) {
            isFromMySettings = getIntent().getBooleanExtra("from_my_settings", false);
        }
        setupHeader();
        setupBottomBar();
        setupCheckBoxes();

        userPreferences = MyApplication.getInstance().userPreferences;
        initAccessibilityProfile();
    }

    private void setupCheckBoxes() {
        cbNoAssistiveDevice = findViewById(R.id.cb_no_assistive_device);
        cbNoAssistiveDevice.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    cb_white_cane.setChecked(false);
                    cb_manual_walker.setChecked(false);
                    cb_wheeled_walker.setChecked(false);
                    cb_cane.setChecked(false);
                    cb_crutches.setChecked(false);
                    cb_stroller.setChecked(false);
                    cb_service_animal.setChecked(false);
                    cb_mobility_scooter.setChecked(false);
                    cb_electric_wheelchair.setChecked(false);
                    cb_manual_traditional_wheelchair.setChecked(false);
                }
            }
        });
        cb_white_cane = findViewById(R.id.cb_white_cane);
        cb_manual_walker = findViewById(R.id.cb_manual_walker);
        cb_wheeled_walker = findViewById(R.id.cb_wheeled_walker);
        cb_cane = findViewById(R.id.cb_cane);
        cb_crutches = findViewById(R.id.cb_crutches);
        cb_stroller = findViewById(R.id.cb_stroller);
        cb_service_animal = findViewById(R.id.cb_service_animal);
        cb_mobility_scooter = findViewById(R.id.cb_mobility_scooter);
        cb_electric_wheelchair = findViewById(R.id.cb_electric_wheelchair);
        cb_manual_traditional_wheelchair = findViewById(R.id.cb_manual_traditional_wheelchair);

        CompoundButton.OnCheckedChangeListener checkedChangeListener = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    cbNoAssistiveDevice.setChecked(false);
                }
            }
        };

        cb_white_cane.setOnCheckedChangeListener(checkedChangeListener);
        cb_manual_walker.setOnCheckedChangeListener(checkedChangeListener);
        cb_wheeled_walker.setOnCheckedChangeListener(checkedChangeListener);
        cb_cane.setOnCheckedChangeListener(checkedChangeListener);
        cb_crutches.setOnCheckedChangeListener(checkedChangeListener);
        cb_stroller.setOnCheckedChangeListener(checkedChangeListener);
        cb_service_animal.setOnCheckedChangeListener(checkedChangeListener);
        cb_mobility_scooter.setOnCheckedChangeListener(checkedChangeListener);
        cb_electric_wheelchair.setOnCheckedChangeListener(checkedChangeListener);
        cb_manual_traditional_wheelchair.setOnCheckedChangeListener(checkedChangeListener);

        cb_yes = findViewById(R.id.cb_yes);
        cb_no = findViewById(R.id.cb_no);

        cb_yes.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    cb_no.setChecked(false);
                }
            }
        });

        cb_no.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    cb_yes.setChecked(false);
                }
            }
        });
        cb_low_vision = findViewById(R.id.cb_low_vision);
        cb_legally_blind = findViewById(R.id.cb_legally_blind);
        cb_no_vision_limitations = findViewById(R.id.cb_no_vision_limitations);

        cb_low_vision.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    cb_legally_blind.setChecked(false);
                    cb_no_vision_limitations.setChecked(false);
                }
            }
        });
        cb_legally_blind.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    cb_low_vision.setChecked(false);
                    cb_no_vision_limitations.setChecked(false);
                }
            }
        });
        cb_no_vision_limitations.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    cb_legally_blind.setChecked(false);
                    cb_low_vision.setChecked(false);
                }
            }
        });
    }

    private void turnoffAll() {
        cbNoAssistiveDevice.setChecked(false);
        cb_white_cane.setChecked(false);
        cb_manual_walker.setChecked(false);
        cb_wheeled_walker.setChecked(false);
        cb_cane.setChecked(false);
        cb_crutches.setChecked(false);
        cb_stroller.setChecked(false);
        cb_service_animal.setChecked(false);
        cb_mobility_scooter.setChecked(false);
        cb_electric_wheelchair.setChecked(false);
        cb_manual_traditional_wheelchair.setChecked(false);

        cb_yes.setChecked(false);
        cb_no.setChecked(false);

        cb_legally_blind.setChecked(false);
        cb_low_vision.setChecked(false);
        cb_no_vision_limitations.setChecked(false);
    }

    private void initAccessibilityProfile() {
        turnoffAll();
        if (userPreferences != null && userPreferences.getMobilityProfile() != null) {
            MobilityProfile mobilityProfile = userPreferences.getMobilityProfile();
            if (mobilityProfile.getMobilityLimited() != null) {
                if (mobilityProfile.getMobilityLimited()) {
                    cb_yes.setChecked(true);
                } else {
                    cb_no.setChecked(true);
                }
            }
            ArrayList<String> mobilityDevices = mobilityProfile.getMobilityDevices();

            if (mobilityDevices.contains("white cane")) {
                cb_white_cane.setChecked(true);
            }
            if (mobilityDevices.contains("manual wheelchair")) {
                cb_manual_traditional_wheelchair.setChecked(true);
            }
            if (mobilityDevices.contains("electric wheelchair")) {
                cb_electric_wheelchair.setChecked(true);
            }
            if (mobilityDevices.contains("mobility scooter")) {
                cb_mobility_scooter.setChecked(true);
            }
            if (mobilityDevices.contains("service animal")) {
                cb_service_animal.setChecked(true);
            }
            if (mobilityDevices.contains("stroller")) {
                cb_stroller.setChecked(true);
            }
            if (mobilityDevices.contains("crutches")) {
                cb_crutches.setChecked(true);
            }
            if (mobilityDevices.contains("cane")) {
                cb_cane.setChecked(true);
            }
            if (mobilityDevices.contains("wheeled walker")) {
                cb_wheeled_walker.setChecked(true);
            }
            if (mobilityDevices.contains("manual walker")) {
                cb_manual_walker.setChecked(true);
            }

            cbNoAssistiveDevice.setChecked(mobilityDevices.size() == 0);
            String visionLimitation = mobilityProfile.getVisionLimitation();
            if (visionLimitation != null) {
                switch (visionLimitation.toLowerCase()) {
                    case "low-vision":
                        cb_low_vision.setChecked(true);
                        break;
                    case "legally-blind":
                        cb_legally_blind.setChecked(true);
                        break;
                    default:
                        cb_no_vision_limitations.setChecked(true);
                        break;

                }
            }
        }
        resetOnCheckChangeListener();
    }

    private void resetOnCheckChangeListener() {
        CompoundButton.OnCheckedChangeListener checkedChangeListener = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    cbNoAssistiveDevice.setChecked(false);
                }
                hasUnsavedChanges = true;
            }
        };

        cb_white_cane.setOnCheckedChangeListener(checkedChangeListener);
        cb_manual_walker.setOnCheckedChangeListener(checkedChangeListener);
        cb_wheeled_walker.setOnCheckedChangeListener(checkedChangeListener);
        cb_cane.setOnCheckedChangeListener(checkedChangeListener);
        cb_crutches.setOnCheckedChangeListener(checkedChangeListener);
        cb_stroller.setOnCheckedChangeListener(checkedChangeListener);
        cb_service_animal.setOnCheckedChangeListener(checkedChangeListener);
        cb_mobility_scooter.setOnCheckedChangeListener(checkedChangeListener);
        cb_electric_wheelchair.setOnCheckedChangeListener(checkedChangeListener);
        cb_manual_traditional_wheelchair.setOnCheckedChangeListener(checkedChangeListener);

        cbNoAssistiveDevice.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    cb_white_cane.setChecked(false);
                    cb_manual_walker.setChecked(false);
                    cb_wheeled_walker.setChecked(false);
                    cb_cane.setChecked(false);
                    cb_crutches.setChecked(false);
                    cb_stroller.setChecked(false);
                    cb_service_animal.setChecked(false);
                    cb_mobility_scooter.setChecked(false);
                    cb_electric_wheelchair.setChecked(false);
                    cb_manual_traditional_wheelchair.setChecked(false);
                } else {
                    if (allOtherOptionsUnselected()) {
                        cbNoAssistiveDevice.setChecked(true);
                    }
                }
                hasUnsavedChanges = true;
            }
        });

        cb_yes.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    cb_no.setChecked(false);
                }
                hasUnsavedChanges = true;
            }
        });

        cb_no.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    cb_yes.setChecked(false);
                }
                hasUnsavedChanges = true;
            }
        });
        cb_low_vision.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    cb_legally_blind.setChecked(false);
                    cb_no_vision_limitations.setChecked(false);
                }
                hasUnsavedChanges = true;
            }
        });
        cb_legally_blind.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    cb_low_vision.setChecked(false);
                    cb_no_vision_limitations.setChecked(false);
                }
                hasUnsavedChanges = true;
            }
        });
        cb_no_vision_limitations.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    cb_legally_blind.setChecked(false);
                    cb_low_vision.setChecked(false);
                }
                hasUnsavedChanges = true;
            }
        });
    }

    private boolean allOtherOptionsUnselected() {
        if (cb_white_cane.isChecked()) {
            return false;
        }
        if (cb_manual_walker.isChecked()) {
            return false;
        }
        if (cb_wheeled_walker.isChecked()) {
            return false;
        }
        if (cb_cane.isChecked()) {
            return false;
        }
        if (cb_crutches.isChecked()) {
            return false;
        }
        if (cb_stroller.isChecked()) {
            return false;
        }
        if (cb_service_animal.isChecked()) {
            return false;
        }
        if (cb_mobility_scooter.isChecked()) {
            return false;
        }
        if (cb_electric_wheelchair.isChecked()) {
            return false;
        }
        return !cb_manual_traditional_wheelchair.isChecked();
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

        if (isFromMySettings) {
            ivLogo.setVisibility(View.GONE);
        } else {
            ivLogo.setVisibility(View.VISIBLE);
        }
        ivMenu.setVisibility(View.GONE);
        ivBack.setVisibility(View.VISIBLE);
        ivClose.setVisibility(View.GONE);
        tvTitle.setVisibility(View.VISIBLE);
        tvAppName.setVisibility(View.GONE);
        tvSignIn.setVisibility(View.GONE);

        tvTitle.setText(R.string.accessibility_profile);
    }

    private void setupHeader() {
        ivLogo = findViewById(R.id.iv_logo);
        ivMenu = findViewById(R.id.iv_menu);
        ivBack = findViewById(R.id.iv_back);
        tvTitle = findViewById(R.id.tv_title);
        tvSignIn = findViewById(R.id.tv_sign_in);
        tvAppName = findViewById(R.id.tv_app_name);
        ivClose = findViewById(R.id.iv_close);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!checkUnsavedChangesAndPopup()) {
                    finish();
                }
            }
        });
        initHeaderViews();
    }

    private void gotoNotificationPreferencesActivity() {
        Intent intent = new Intent(this, NotificationPreferencesActivity.class);

        startActivity(intent);
    }

    private void savePreferences() {
        Response.Listener listener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                hasUnsavedChanges = false;
                Gson gson = new Gson();
                Configurations.setString("account_user_preferences", response);
                try {
                    MyApplication.getInstance().userPreferences = gson.fromJson(response, UserPreferences.class);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Utilities.showNormalPopupDialog(AccessibilityProfileActivity.this, "",
                        getString(R.string.save_preferences_successfully), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (isFromMySettings) {
                                    finish();
                                } else {
                                    gotoNotificationPreferencesActivity();
                                }
                            }
                        });
            }
        };
        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Logger.e("account", error.toString());
                Utilities.showNormalPopupDialog(AccessibilityProfileActivity.this, "", getString(R.string.save_preferences_failed));
            }
        };
        UserPreferences userPreferences = MyApplication.getInstance().userPreferences;
        if (userPreferences != null) {
            MobilityProfile mobilityProfile = userPreferences.getMobilityProfile();
            if (mobilityProfile == null) {
                mobilityProfile = new MobilityProfile();
                userPreferences.setMobilityProfile(mobilityProfile);
            }

            mobilityProfile.setMobilityLimited(cb_yes.isChecked());

            mobilityProfile.setVisionLimitation("none");
            if (cb_low_vision.isChecked()) {
                mobilityProfile.setVisionLimitation("low-vision");
            }
            if (cb_legally_blind.isChecked()) {
                mobilityProfile.setVisionLimitation("legally-blind");
            }
            if (cb_no_vision_limitations.isChecked()) {
                mobilityProfile.setVisionLimitation("none");
            }
            ArrayList<String> mobilityDevices = mobilityProfile.getMobilityDevices();
            mobilityDevices.clear();
            if (!cbNoAssistiveDevice.isChecked()) {
                if (cb_white_cane.isChecked()) {
                    mobilityDevices.add("white cane");
                }
                if (cb_manual_walker.isChecked()) {
                    mobilityDevices.add("manual walker");
                }
                if (cb_wheeled_walker.isChecked()) {
                    mobilityDevices.add("wheeled walker");
                }
                if (cb_cane.isChecked()) {
                    mobilityDevices.add("cane");
                }
                if (cb_crutches.isChecked()) {
                    mobilityDevices.add("crutches");
                }
                if (cb_stroller.isChecked()) {
                    mobilityDevices.add("stroller");
                }
                if (cb_service_animal.isChecked()) {
                    mobilityDevices.add("service animal");
                }
                if (cb_mobility_scooter.isChecked()) {
                    mobilityDevices.add("mobility scooter");
                }
                if (cb_electric_wheelchair.isChecked()) {
                    mobilityDevices.add("electric wheelchair");
                }
                if (cb_manual_traditional_wheelchair.isChecked()) {
                    mobilityDevices.add("manual wheelchair");
                }
            }
            mobilityProfile.setMobilityMode("");

            Gson gson = new Gson();
            String url = String.format(Resource.API_ACCOUNT_USER_SAVE_PREFERENCES, userPreferences.getId());
            volleyAccountStringRequest(Request.Method.PUT, url, gson.toJson(userPreferences), listener, errorListener);
        }

    }

    private void setupBottomBar() {
        tvSavePreferences = findViewById(R.id.tv_save_preferences);
        tvNext = findViewById(R.id.tv_next);
        tvSavePreferences.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                savePreferences();
            }
        });

        tvNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoNotificationPreferencesActivity();
            }
        });

        if (isFromMySettings) {
            tvNext.setVisibility(View.GONE);
        } else {
            tvNext.setVisibility(View.VISIBLE);
        }
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
            if (checkUnsavedChangesAndPopup())
                return true;
        }

        return super.onKeyDown(keyCode, event);
    }
}