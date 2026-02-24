package com.ibigroup.mobile.otp.android;

import static com.ibigroup.mobile.otp.android.utility.ServerDelegate.volleyAccountStringRequest;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.auth0.android.Auth0;
import com.auth0.android.authentication.AuthenticationAPIClient;
import com.auth0.android.authentication.AuthenticationException;
import com.auth0.android.callback.Callback;
import com.auth0.android.result.UserProfile;
import com.google.gson.Gson;
import com.ibigroup.mobile.otp.android.api.Resource;
import com.ibigroup.mobile.otp.android.json.CreateNewUserRequest;
import com.ibigroup.mobile.otp.android.json.UserPreferences;
import com.ibigroup.mobile.otp.android.utility.Configurations;
import com.ibigroup.mobile.otp.android.utility.Logger;
import com.ibigroup.mobile.otp.android.utility.ServerDelegate;
import com.ibigroup.mobile.otp.android.utility.Utilities;

import java.util.Map;

public class VerifyEmailActivity extends BaseActivity {
    private ImageView ivLogo, ivMenu, ivBack, ivClose;
    private TextView tvTitle, tvSignIn, tvAppName;
    private TextView tvEmailIsVerified, tvResendVerificationEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_email);

        setupHeader();

        tvEmailIsVerified = findViewById(R.id.tv_email_is_verified);
        tvResendVerificationEmail = findViewById(R.id.tv_resend_verification_email);

        tvEmailIsVerified.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                retrieveUserInfo(Configurations.getString("account_access_token", ""));
            }
        });

        tvResendVerificationEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resendVerificationEmail();
            }
        });
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

    private void createNewUserInMiddleware() {
        Response.Listener listener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response != null && !response.isEmpty()) {
                    Gson gson = new Gson();
                    Configurations.setString("account_user_preferences", response);

                    try {
                        MyApplication.getInstance().userPreferences = gson.fromJson(response, UserPreferences.class);

                        finish();
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
                            finish();
                        } else {
                            finish();
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
                Logger.e("account", error.toString());
                if (error != null && error.networkResponse != null && error.networkResponse.statusCode == 404) {
                    //create a new user in otp middleware
                    createNewUserInMiddleware();
                }
            }
        };

        String url = Resource.API_ACCOUNT_FROM_TOKEN;

        volleyAccountStringRequest(Request.Method.GET, url, "", listener, errorListener);
    }

    private void retrieveUserInfo(String accessToken) {
        Auth0 account = new Auth0(Configurations.getString("auth0_client_id", ""), Configurations.getString("auth0_domain", ""));
        Callback<UserProfile, AuthenticationException> userInfoCallback = new Callback<UserProfile, AuthenticationException>() {
            @Override
            public void onSuccess(UserProfile userProfile) {
                Gson gson = new Gson();
                Configurations.setString("account_user_profile", gson.toJson(userProfile));

                if (!Configurations.getString("device_token", "").isEmpty()) {
                    try {
                        ServerDelegate.postRegistration(Configurations.getString("device_token", ""));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                if (userProfile != null) {
                    if (Boolean.TRUE.equals(userProfile.isEmailVerified())) {
                        getFromToken();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull AuthenticationException e) {
                Logger.e("account", e.toString());

            }
        };
        AuthenticationAPIClient authenticationAPIClient = new AuthenticationAPIClient(account);
        authenticationAPIClient.userInfo(accessToken).start(userInfoCallback);
    }

    private void resendVerificationEmail() {
        Response.Listener listener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Utilities.showNormalPopupDialog(VerifyEmailActivity.this, "", getString(R.string.resend_verification_email_successfully));
            }
        };
        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Logger.e("account", error.toString());
                Utilities.showNormalPopupDialog(VerifyEmailActivity.this, "", getString(R.string.resend_verification_email_failed));
            }
        };
        String url = Resource.API_ACCOUNT_RESEND_EMAIL;

        volleyAccountStringRequest(Request.Method.GET, url, "", listener, errorListener);
    }

    private void gotoCreateNewAccountActivity() {
        Intent intent = new Intent(this, CreateNewAccountActivity.class);

        startActivity(intent);
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


}