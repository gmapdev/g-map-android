package com.ibigroup.mobile.otp.android;

import static com.ibigroup.mobile.otp.android.utility.Constants.AUTH0_SUCCESS;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.auth0.android.Auth0;
import com.auth0.android.authentication.AuthenticationAPIClient;
import com.auth0.android.authentication.AuthenticationException;
import com.auth0.android.callback.Callback;
import com.auth0.android.result.Credentials;
import com.google.gson.Gson;
import com.ibigroup.mobile.otp.android.utility.Configurations;
import com.ibigroup.mobile.otp.android.utility.Utilities;

public class LogInSignUpActivity extends BaseActivity {
    private final String connectionName = "Username-Password-Authentication";
    private final String scopeName = "openid profile email otp-user";
    private RelativeLayout rlEmailFrame, rlPasswordFrame;
    private EditText etEmail, etPassword, etEmailForget;
    private LinearLayout llLogInTab, llSignUpTab, llLogIn, llSignUp, llSendEmail, llForgetPassword, llSignUpPasswordRequirements,
            llAuth0Tab, ll_email_error, ll_password_error;
    private TextView tvLogInTab, tvLogInTabDivider, tvSignUpTab, tvSignUpTabDivider, tvForgotPassword,
            tvSignUpTerms, tvEmailError, tvPasswordError;

    private ImageView ivAtLeastEightCharacters, ivTypesCharacters, ivLowerCaseLetters, ivUpperCaseLetters,
            ivNumbers, ivSpecialCharacters, iv_hide_password;
    private TextView tvAtLeastEightCharacters, tvTypesCharacters, tvLowerCaseLetters, tvUpperCaseLetters,
            tvNumbers, tvSpecialCharacters;

    private ScrollView svSignInAndUp;

    private boolean isLoginTabSelected = true;
    private boolean isEmailError = false;
    private boolean isPasswordError = false;

    private boolean isPasswordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in_sign_up);

        tvSignUpTerms = findViewById(R.id.tv_sign_up_terms);
        tvEmailError = findViewById(R.id.tv_email_error);
        ll_email_error = findViewById(R.id.ll_email_error);
        tvPasswordError = findViewById(R.id.tv_password_error);
        ll_password_error = findViewById(R.id.ll_password_error);
        setupFrames();
        setupInputWidgets();
        setupForgotPassword();
        setupTabControl();
        setupSignUpPasswordRequirements();
        refreshWidgetsAccordingly();


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

    private void setupSignUpPasswordRequirements() {
        ivAtLeastEightCharacters = findViewById(R.id.iv_at_least_eight_characters);
        ivTypesCharacters = findViewById(R.id.iv_types_characters);
        ivLowerCaseLetters = findViewById(R.id.iv_lower_case_letters);
        ivUpperCaseLetters = findViewById(R.id.iv_upper_case_letters);
        ivNumbers = findViewById(R.id.iv_numbers);
        ivSpecialCharacters = findViewById(R.id.iv_special_characters);

        tvAtLeastEightCharacters = findViewById(R.id.tv_at_least_eight_characters);
        tvTypesCharacters = findViewById(R.id.tv_types_characters);
        tvLowerCaseLetters = findViewById(R.id.tv_lower_case_letters);
        tvUpperCaseLetters = findViewById(R.id.tv_upper_case_letters);
        tvNumbers = findViewById(R.id.tv_numbers);
        tvSpecialCharacters = findViewById(R.id.tv_special_characters);
    }

    private void refreshSignUpPasswordRequirements() {
        String password = etPassword.getText().toString();
        boolean hasLowerLetters = password.matches(".*[a-z].*");
        boolean hasUpperLetters = password.matches(".*[A-Z].*");
        boolean hasNumbers = password.matches(".*\\d+.*");
        boolean hasSpecialCharacters = password.matches(".*\\W.*");
        int hasItemsCount = 0;

        if (password.length() < 8) {
            ivAtLeastEightCharacters.setImageResource(R.drawable.ic_cancel);
            ivAtLeastEightCharacters.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorSignUpPasswordRed)));
            tvAtLeastEightCharacters.setTextColor(getResources().getColor(R.color.colorSignUpPasswordRed));
        } else {
            ivAtLeastEightCharacters.setImageResource(R.drawable.ic_checkmark);
            ivAtLeastEightCharacters.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorSignUpPasswordGreen)));
            tvAtLeastEightCharacters.setTextColor(getResources().getColor(R.color.colorSignUpPasswordGreen));
        }

        if (hasLowerLetters) {
            hasItemsCount++;
            ivLowerCaseLetters.setImageResource(R.drawable.ic_checkmark);
            ivLowerCaseLetters.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorSignUpPasswordGreen)));
            tvLowerCaseLetters.setTextColor(getResources().getColor(R.color.colorSignUpPasswordGreen));
        } else {
            ivLowerCaseLetters.setImageDrawable(null);
            ivLowerCaseLetters.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorGrey)));
            tvLowerCaseLetters.setTextColor(getResources().getColor(R.color.colorGrey));
        }

        if (hasUpperLetters) {
            hasItemsCount++;
            ivUpperCaseLetters.setImageResource(R.drawable.ic_checkmark);
            ivUpperCaseLetters.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorSignUpPasswordGreen)));
            tvUpperCaseLetters.setTextColor(getResources().getColor(R.color.colorSignUpPasswordGreen));
        } else {
            ivUpperCaseLetters.setImageDrawable(null);
            ivUpperCaseLetters.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorGrey)));
            tvUpperCaseLetters.setTextColor(getResources().getColor(R.color.colorGrey));
        }

        if (hasNumbers) {
            hasItemsCount++;
            ivNumbers.setImageResource(R.drawable.ic_checkmark);
            ivNumbers.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorSignUpPasswordGreen)));
            tvNumbers.setTextColor(getResources().getColor(R.color.colorSignUpPasswordGreen));
        } else {
            ivNumbers.setImageDrawable(null);
            ivNumbers.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorGrey)));
            tvNumbers.setTextColor(getResources().getColor(R.color.colorGrey));
        }

        if (hasSpecialCharacters) {
            hasItemsCount++;
            ivSpecialCharacters.setImageResource(R.drawable.ic_checkmark);
            ivSpecialCharacters.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorSignUpPasswordGreen)));
            tvSpecialCharacters.setTextColor(getResources().getColor(R.color.colorSignUpPasswordGreen));
        } else {
            ivSpecialCharacters.setImageDrawable(null);
            ivSpecialCharacters.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorGrey)));
            tvSpecialCharacters.setTextColor(getResources().getColor(R.color.colorGrey));
        }

        if (hasItemsCount < 3) {
            ivTypesCharacters.setImageResource(R.drawable.ic_cancel);
            ivTypesCharacters.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorSignUpPasswordRed)));
            tvTypesCharacters.setTextColor(getResources().getColor(R.color.colorSignUpPasswordRed));
        } else {
            ivTypesCharacters.setImageResource(R.drawable.ic_checkmark);
            ivTypesCharacters.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorSignUpPasswordGreen)));
            tvTypesCharacters.setTextColor(getResources().getColor(R.color.colorSignUpPasswordGreen));
        }
    }

    private void setupFrames() {
        rlEmailFrame = findViewById(R.id.rl_email_frame);
        rlPasswordFrame = findViewById(R.id.rl_password_frame);
    }

    private void setupInputWidgets() {
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);

        etEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (ll_email_error.getVisibility() == View.VISIBLE) {
                    ll_email_error.setVisibility(View.GONE);
                    isEmailError = false;
                    if (etEmail.hasFocus()) {
                        rlEmailFrame.setBackgroundResource(R.drawable.grey_frame_rounded_transparent_bg);
                    } else {
                        rlEmailFrame.setBackgroundResource(R.drawable.light_grey_frame_rounded_transparent_bg);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        etPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (ll_password_error.getVisibility() == View.VISIBLE) {
                    ll_password_error.setVisibility(View.GONE);
                    isPasswordError = false;
                    if (etPassword.hasFocus()) {
                        rlPasswordFrame.setBackgroundResource(R.drawable.grey_frame_rounded_transparent_bg);
                    } else {
                        rlPasswordFrame.setBackgroundResource(R.drawable.light_grey_frame_rounded_transparent_bg);
                    }
                }

                if (!isLoginTabSelected) {
                    refreshSignUpPasswordRequirements();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        etEmail.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (isEmailError) {
                    rlEmailFrame.setBackgroundResource(R.drawable.red_frame_rounded_transparent_bg);
                } else {
                    if (hasFocus) {
                        rlEmailFrame.setBackgroundResource(R.drawable.grey_frame_rounded_transparent_bg);
                    } else {
                        rlEmailFrame.setBackgroundResource(R.drawable.light_grey_frame_rounded_transparent_bg);
                    }
                }
            }
        });

        etPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (isPasswordError) {
                    rlPasswordFrame.setBackgroundResource(R.drawable.red_frame_rounded_transparent_bg);
                } else {
                    if (hasFocus) {
                        rlPasswordFrame.setBackgroundResource(R.drawable.grey_frame_rounded_transparent_bg);
                    } else {
                        rlPasswordFrame.setBackgroundResource(R.drawable.light_grey_frame_rounded_transparent_bg);
                    }
                }

                if (!isLoginTabSelected && hasFocus) {
                    refreshSignUpPasswordRequirements();
                    llSignUpPasswordRequirements.setVisibility(View.VISIBLE);
                } else {
                    llSignUpPasswordRequirements.setVisibility(View.GONE);
                }
            }
        });


        etPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
        iv_hide_password = findViewById(R.id.iv_hide_password);

        iv_hide_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isPasswordVisible = !isPasswordVisible;
                if (isPasswordVisible) {
                    // Show password
                    etPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
//                    iv_hide_password.setImageResource(R.drawable.ic_password_visible); // Icon for visible password
                } else {
                    // Hide password
                    etPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
//                    iv_hide_password.setImageResource(R.drawable.ic_password_hidden); // Icon for hidden password
                }
                etPassword.setSelection(etPassword.getText().length());
            }
        });
    }

    private void setupForgotPassword() {
        tvForgotPassword = findViewById(R.id.tv_forgot_password);
        llForgetPassword = findViewById(R.id.ll_forget_password);
        llSendEmail = findViewById(R.id.ll_send_email);
        etEmailForget = findViewById(R.id.et_email_forget);
        tvForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showForgetPasswordPage();
            }
        });

        llSendEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Auth0 account = new Auth0(Configurations.getString("auth0_client_id", ""), Configurations.getString("auth0_domain", ""));
                AuthenticationAPIClient client = new AuthenticationAPIClient(account);
                client.resetPassword(etEmailForget.getText().toString(), connectionName)
                        .start(new Callback<Void, AuthenticationException>() {
                            @Override
                            public void onSuccess(Void unused) {
                                showSignInOrUpPage();
                                etEmail.setText(etEmailForget.getText());
                            }

                            @Override
                            public void onFailure(@NonNull AuthenticationException e) {
                                if (!LogInSignUpActivity.this.isFinishing()) {
                                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(LogInSignUpActivity.this);
                                    alertDialogBuilder.setTitle(e.getMessage());
                                    alertDialogBuilder
                                            .setMessage(e.getDescription())
                                            .setCancelable(false)
                                            .setPositiveButton(LogInSignUpActivity.this.getString(R.string.ok), new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {
                                                    dialog.dismiss();
                                                }
                                            });

                                    alertDialogBuilder.create().show();
                                }
                            }
                        });
            }
        });
    }

    private void showForgetPasswordPage() {
        llForgetPassword.setVisibility(View.VISIBLE);
        svSignInAndUp.setVisibility(View.GONE);
        llSendEmail.setVisibility(View.VISIBLE);
        llLogIn.setVisibility(View.GONE);
        llSignUp.setVisibility(View.GONE);
        llAuth0Tab.setVisibility(View.GONE);
    }

    private void showSignInOrUpPage() {
        llForgetPassword.setVisibility(View.GONE);
        llSendEmail.setVisibility(View.GONE);
        svSignInAndUp.setVisibility(View.VISIBLE);
        refreshWidgetsAccordingly();
        llAuth0Tab.setVisibility(View.VISIBLE);
    }

    private void refreshWidgetsAccordingly() {
        if (isLoginTabSelected) {
            tvForgotPassword.setVisibility(View.VISIBLE);
            llLogIn.setVisibility(View.VISIBLE);
            tvSignUpTerms.setVisibility(View.GONE);
            llSignUp.setVisibility(View.GONE);
            llSignUpPasswordRequirements.setVisibility(View.GONE);

            tvLogInTab.setTextColor(getResources().getColor(R.color.colorHighlightText));
            tvLogInTabDivider.setBackgroundColor(getResources().getColor(R.color.colorHighlightText));
            tvSignUpTab.setTextColor(getResources().getColor(R.color.colorUnhighlightText));
            tvSignUpTabDivider.setBackgroundColor(getResources().getColor(R.color.colorUnhighlightDivider));
        } else {
            tvForgotPassword.setVisibility(View.GONE);
            llLogIn.setVisibility(View.GONE);
            tvSignUpTerms.setVisibility(View.GONE);//always gone now
            llSignUp.setVisibility(View.VISIBLE);
            if (etPassword.hasFocus()) {
                llSignUpPasswordRequirements.setVisibility(View.VISIBLE);
            } else {
                llSignUpPasswordRequirements.setVisibility(View.GONE);
            }

            tvLogInTab.setTextColor(getResources().getColor(R.color.colorUnhighlightText));
            tvLogInTabDivider.setBackgroundColor(getResources().getColor(R.color.colorUnhighlightDivider));
            tvSignUpTab.setTextColor(getResources().getColor(R.color.colorHighlightText));
            tvSignUpTabDivider.setBackgroundColor(getResources().getColor(R.color.colorHighlightText));
        }
    }

    private void setupTabControl() {
        llLogInTab = findViewById(R.id.ll_log_in_tab);
        llSignUpTab = findViewById(R.id.ll_sign_up_tab);
        llLogIn = findViewById(R.id.ll_log_in);
        llSignUp = findViewById(R.id.ll_sign_up);
        llAuth0Tab = findViewById(R.id.ll_auth0_tab);
        svSignInAndUp = findViewById(R.id.sv_sign_in_and_up);
        llSignUpPasswordRequirements = findViewById(R.id.ll_sign_up_password_requirements);
        tvLogInTab = findViewById(R.id.tv_log_in_tab);
        tvLogInTabDivider = findViewById(R.id.tv_log_in_tab_divider);
        tvSignUpTab = findViewById(R.id.tv_sign_up_tab);
        tvSignUpTabDivider = findViewById(R.id.tv_sign_up_tab_divider);

        llLogInTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isLoginTabSelected = true;
                refreshWidgetsAccordingly();
            }
        });

        llSignUpTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isLoginTabSelected = false;
                refreshWidgetsAccordingly();
            }
        });

        llLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isValidEmailAndPassword()) {
                    logInAction();
                }
            }
        });

        llSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isValidEmailAndPassword()) {
                    signUpAction();
                }
            }
        });
    }

    public static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

    private boolean isValidEmailAndPassword() {
        boolean ret = true;
        String email = etEmail.getText().toString();
        if (email.isEmpty()) {
            tvEmailError.setText(R.string.email_cant_blank);
            ll_email_error.setVisibility(View.VISIBLE);
            tvEmailError.announceForAccessibility(tvEmailError.getText());
            isEmailError = true;
            rlEmailFrame.setBackgroundResource(R.drawable.red_frame_rounded_transparent_bg);
            ret = false;
        } else if (!isValidEmail(email)) {
            tvEmailError.setText(R.string.email_invalid);
            ll_email_error.setVisibility(View.VISIBLE);
            tvEmailError.announceForAccessibility(tvEmailError.getText());
            isEmailError = true;
            rlEmailFrame.setBackgroundResource(R.drawable.red_frame_rounded_transparent_bg);
            ret = false;
        }

        if (etPassword.getText().toString().isEmpty()) {
            tvPasswordError.setText(R.string.password_cant_blank);
            ll_password_error.setVisibility(View.VISIBLE);
            tvPasswordError.announceForAccessibility(tvPasswordError.getText());
            isPasswordError = true;
            rlPasswordFrame.setBackgroundResource(R.drawable.red_frame_rounded_transparent_bg);
            ret = false;
        }
        return ret;
    }

    private void logInAction() {
        Auth0 account = new Auth0(Configurations.getString("auth0_client_id", ""), Configurations.getString("auth0_domain", ""));
        AuthenticationAPIClient client = new AuthenticationAPIClient(account);
        client.login(etEmail.getText().toString(), etPassword.getText().toString())
                .setConnection(connectionName)
                .setScope(scopeName)
                .setAudience(getString(R.string.com_auth0_audience))
                .start(new Callback<Credentials, AuthenticationException>() {
                    @Override
                    public void onSuccess(Credentials credentials) {
                        Gson gson = new Gson();
                        Configurations.setString("account_credentials", gson.toJson(Utilities.regenerateCredentials(credentials)));
                        Configurations.setString("account_id_token", credentials.getIdToken());
                        Configurations.setString("account_access_token", credentials.getAccessToken());

                        Intent resultIntent = new Intent();
                        resultIntent.putExtra(AUTH0_SUCCESS, credentials.getAccessToken()); // You can put any data you want to send back
                        setResult(RESULT_OK, resultIntent);
                        finish();
                    }

                    @Override
                    public void onFailure(@NonNull AuthenticationException e) {
                        if (!LogInSignUpActivity.this.isFinishing()) {
                            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(LogInSignUpActivity.this);
                            if (e.getDescription().contains("unknown")) {
                                alertDialogBuilder
                                        .setMessage("No Internet Connection")
                                        .setCancelable(false)
                                        .setPositiveButton(LogInSignUpActivity.this.getString(R.string.ok), new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                dialog.dismiss();
                                            }
                                        });

                            } else {
                                alertDialogBuilder.setTitle("An error occurred");
                                alertDialogBuilder
                                        .setMessage(e.getDescription())
                                        .setCancelable(false)
                                        .setPositiveButton(LogInSignUpActivity.this.getString(R.string.ok), new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                dialog.dismiss();
                                            }
                                        });

                            }


                            alertDialogBuilder.create().show();
                        }
                    }
                });


    }

    private void signUpAction() {
        Auth0 account = new Auth0(Configurations.getString("auth0_client_id", ""), Configurations.getString("auth0_domain", ""));

        AuthenticationAPIClient client = new AuthenticationAPIClient(account);
        client.signUp(etEmail.getText().toString(), etPassword.getText().toString(), etEmail.getText().toString().split("@")[0], "Username-Password-Authentication")
                .setConnection(connectionName)
                .setScope(scopeName)
                .setAudience(getString(R.string.com_auth0_audience))
                .start(new Callback<Credentials, AuthenticationException>() {
                    @Override
                    public void onSuccess(Credentials credentials) {
                        Gson gson = new Gson();
                        Configurations.setString("account_credentials", gson.toJson(Utilities.regenerateCredentials(credentials)));
                        Configurations.setString("account_id_token", credentials.getIdToken());
                        Configurations.setString("account_access_token", credentials.getAccessToken());

                        Intent resultIntent = new Intent();
                        resultIntent.putExtra(AUTH0_SUCCESS, credentials.getAccessToken()); // You can put any data you want to send back
                        setResult(RESULT_OK, resultIntent);
                        finish();
                    }

                    @Override
                    public void onFailure(@NonNull AuthenticationException e) {
                        if (!LogInSignUpActivity.this.isFinishing()) {
                            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(LogInSignUpActivity.this);
                            alertDialogBuilder.setTitle(e.getMessage());
                            alertDialogBuilder
                                    .setMessage(e.getDescription())
                                    .setCancelable(false)
                                    .setPositiveButton(LogInSignUpActivity.this.getString(R.string.ok), new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.dismiss();
                                        }
                                    });

                            alertDialogBuilder.create().show();
                        }
                    }
                });

    }

    @Override
    public void onBackPressed() {
        if (llForgetPassword.getVisibility() == View.VISIBLE) {
            showSignInOrUpPage();
        } else {
            super.onBackPressed();
        }
    }
}