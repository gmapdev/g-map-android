package com.ibigroup.mobile.otp.android;

import static com.ibigroup.mobile.otp.android.utility.ServerDelegate.volleyAccountStringRequest;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.ibigroup.mobile.otp.android.api.Resource;
import com.ibigroup.mobile.otp.android.customize.CustomizePopupDialog;
import com.ibigroup.mobile.otp.android.json.UserPreferences;
import com.ibigroup.mobile.otp.android.utility.Logger;
import com.ibigroup.mobile.otp.android.utility.Utilities;

import java.util.Locale;

public class NotificationPreferencesActivity extends BaseActivity {
    private ImageView ivLogo, ivMenu, ivBack, ivClose;
    private TextView tvTitle, tvSignIn, tvAppName;
    private TextView tvBack, tvNext;

    private TextView tvPreferenceEmail, tvPreferenceSMS, tvPreferenceOptOut, tvNotificationEmail;
    private TextView tvSentVerificationText, tvSentVerificationTextCancel;
    private TextView tvNotificationSMSNumber, tvNotificationSMSNumberStatus, tvChangeNumber,
            tvVerificationCodeVerify, tvRequestNewCode;
    private LinearLayout llNotificationSMS, llNotificationSMSNewNumber,
            llNotificationSMSSentTo, llVerifySMS;
    private EditText etNotificationSMS, etVerificationCode;

    private TextView tvDeleteMyAccount, tvSavePreferences;
    private CheckBox cbEmail, cbSMS, cbPush;
    private TextView tvRegisteredDevicesCount;

    private boolean showedWarning = false;
    private CustomizePopupDialog smsWarningPopup = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_preferences);

        setupHeader();
        setupBottomBar();
        //setupPreferenceTabs();
        setupPreferenceEmailLayout();
        setupPreferenceSMSLayout();
        setupPreferencePushNotificationLayout();
        UserPreferences userPreferences = MyApplication.getInstance().userPreferences;

        if (userPreferences != null) {
            initNotificationPreferences(userPreferences);
            tvRegisteredDevicesCount.setText(getString(R.string.devices_registered, userPreferences.getPushDevices()));
        }
    }

    private void setupPreferencePushNotificationLayout() {
        cbPush = findViewById(R.id.cb_preference_push);
        tvRegisteredDevicesCount = findViewById(R.id.tv_registered_devices_count);
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

    private void initNotificationPreferences(@NonNull UserPreferences userPreferences) {
        if (userPreferences.getNotificationChannel() != null) {
            cbEmail.setChecked(userPreferences.getNotificationChannel().toLowerCase(Locale.ROOT).contains("email"));

            cbSMS.setChecked(userPreferences.getNotificationChannel().toLowerCase(Locale.ROOT).contains("sms"));

            cbPush.setChecked(userPreferences.getNotificationChannel().toLowerCase(Locale.ROOT).contains("push"));
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

        llVerifySMS = findViewById(R.id.ll_verify_sms);

        tvSentVerificationTextCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                llNotificationSMSNewNumber.setVisibility(View.GONE);
                llNotificationSMSSentTo.setVisibility(View.VISIBLE);
            }
        });

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
                Utilities.showNormalPopupDialog(NotificationPreferencesActivity.this, "", getString(R.string.verification_code_sent));

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
                Utilities.showNormalPopupDialog(NotificationPreferencesActivity.this, "", getString(R.string.verification_code_sent_failed));
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
                Utilities.showNormalPopupDialog(NotificationPreferencesActivity.this, "", getString(R.string.verify_verification_code_successfully));

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
                Utilities.showNormalPopupDialog(NotificationPreferencesActivity.this, "", getString(R.string.verify_verification_code_failed));
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
                Intent intent = new Intent(NotificationPreferencesActivity.this, TabActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
            }
        });
        initHeaderViews();
    }

    private void gotoAddLocationsActivity() {
        Intent intent = new Intent(this, AddLocationsActivity.class);

        startActivity(intent);
    }

    private void savePreferences() {
        Response.Listener listener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                gotoAddLocationsActivity();
            }
        };
        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Logger.e("account", error.toString());
                Utilities.showNormalPopupDialog(NotificationPreferencesActivity.this, "", getString(R.string.save_preferences_failed));
            }
        };
        UserPreferences userPreferences = MyApplication.getInstance().userPreferences;

        if (userPreferences != null) {
            StringBuilder stringBuilder = new StringBuilder();

            if (cbEmail.isChecked()) {
                stringBuilder.append("email");
            }

            if (stringBuilder.length() > 0) {
                stringBuilder.append(",");
            }
            if (cbSMS.isChecked()) {
                stringBuilder.append("sms");
            }

            if (stringBuilder.length() > 0) {
                stringBuilder.append(",");
            }
            if (cbPush.isChecked()) {
                stringBuilder.append("push");
            }

            userPreferences.setNotificationChannel(stringBuilder.toString());

            Gson gson = new Gson();
            String url = String.format(Resource.API_ACCOUNT_USER_SAVE_PREFERENCES, userPreferences.getId());

            volleyAccountStringRequest(Request.Method.PUT, url, gson.toJson(userPreferences), listener, errorListener);
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

        smsWarningPopup = new CustomizePopupDialog(NotificationPreferencesActivity.this,
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
                if (needShowWarning()) {
                    showWarningPopup();
                } else {
                    savePreferences();
                }
            }
        });
    }
}