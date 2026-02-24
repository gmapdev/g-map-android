package com.ibigroup.mobile.otp.android;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.ibigroup.mobile.otp.android.utility.Utilities;

public class AccountSetupCompleteActivity extends BaseActivity {
    private ImageView ivLogo, ivMenu, ivBack, ivClose;
    private TextView tvTitle, tvSignIn, tvAppName;
    private TextView tvBack, tvNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_setup_complete);

        setupHeader();
        setupBottomBar();
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
                Intent intent = new Intent(AccountSetupCompleteActivity.this, TabActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
            }
        });
        initHeaderViews();
    }

    private void setupBottomBar() {
        tvBack = findViewById(R.id.tv_back);
        tvNext = findViewById(R.id.tv_next);
        tvNext.setText(getString(R.string.finish));

        tvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        tvNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(AccountSetupCompleteActivity.this);
                alertDialogBuilder
                        .setMessage(getString(R.string.preferences_saved))
                        .setCancelable(false)
                        .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent intent = new Intent(AccountSetupCompleteActivity.this, TabActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                startActivity(intent);
                            }
                        });

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        });
    }
}