package com.ibigroup.mobile.otp.android;

import static com.ibigroup.mobile.otp.android.utility.ServerDelegate.volleyAccountStringRequest;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.LinearLayoutCompat;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.ibigroup.mobile.otp.android.api.Resource;
import com.ibigroup.mobile.otp.android.json.UserPreferences;
import com.ibigroup.mobile.otp.android.utility.Configurations;
import com.ibigroup.mobile.otp.android.utility.Logger;
import com.ibigroup.mobile.otp.android.utility.Utilities;

import java.util.Locale;

public class CreateNewAccountActivity extends BaseActivity {
    private ImageView ivLogo, ivMenu, ivBack, ivClose;
    private TextView tvTitle, tvSignIn, tvAppName;
    private TextView tvBack, tvNext;
    private CheckBox cbCreateNewAccountTerms, cbCreateNewAccountOptional;
    private TextView tvCreateNewAccountTerms, tvCreateNewAccountOptional;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_account);

        setupHeader();
        setupBottomBar();
        setupTermsOptional();

        UserPreferences userPreferences = MyApplication.getInstance().userPreferences;
        if (userPreferences != null) {
            initTermsAndOptional(userPreferences);
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

    private void initTermsAndOptional(@NonNull UserPreferences userPreferences) {
        cbCreateNewAccountTerms.setChecked(userPreferences.isHasConsentedToTerms());
        cbCreateNewAccountOptional.setChecked(userPreferences.isStoreTripHistory());
    }

    private void refreshNextButton() {
        if (cbCreateNewAccountTerms.isChecked()) {
            tvNext.setEnabled(true);
            tvNext.setClickable(true);
            tvNext.setAlpha(1);
        } else {
            tvNext.setClickable(false);
            tvNext.setEnabled(false);
            tvNext.setAlpha(0.5f);
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

        cbCreateNewAccountTerms.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                refreshNextButton();
            }
        });
        refreshNextButton();
        tvCreateNewAccountTerms.setMovementMethod(LinkMovementMethod.getInstance());
        tvCreateNewAccountOptional.setMovementMethod(LinkMovementMethod.getInstance());
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
                finish();
            }
        });
        initHeaderViews();
    }

    private void gotoNextActivity() {
        Intent intent;

        if (Configurations.getBoolean("enable_mobile_questionairs", false)) {
            intent = new Intent(this, AccessibilityProfileActivity.class);
        } else {
            intent = new Intent(this, NotificationPreferencesActivity.class);
        }

        startActivity(intent);
    }

    private void savePreferences() {
        Response.Listener listener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                gotoNextActivity();
            }
        };
        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Logger.e("account", error.toString());
                Utilities.showNormalPopupDialog(CreateNewAccountActivity.this, "", getString(R.string.save_preferences_failed));
            }
        };
        UserPreferences userPreferences = MyApplication.getInstance().userPreferences;

        if (userPreferences != null) {
            userPreferences.setHasConsentedToTerms(cbCreateNewAccountTerms.isChecked());
            userPreferences.setStoreTripHistory(cbCreateNewAccountOptional.isChecked());
            Gson gson = new Gson();
            String url = String.format(Resource.API_ACCOUNT_USER_SAVE_PREFERENCES, userPreferences.getId());
            volleyAccountStringRequest(Request.Method.PUT, url, gson.toJson(userPreferences), listener, errorListener);
        }

    }

    private void setupBottomBar() {
        tvBack = findViewById(R.id.tv_back);
        tvNext = findViewById(R.id.tv_next);

        tvBack.setVisibility(View.GONE);
        tvNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                savePreferences();
            }
        });
    }
}