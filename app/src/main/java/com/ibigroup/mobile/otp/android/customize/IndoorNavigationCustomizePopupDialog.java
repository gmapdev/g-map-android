package com.ibigroup.mobile.otp.android.customize;


import static com.mapbox.mapboxsdk.Mapbox.getApplicationContext;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import com.ibigroup.mobile.otp.android.R;
import com.ibigroup.mobile.otp.android.utility.Convert;
import com.ibigroup.mobile.otp.android.utility.Utilities;


public class IndoorNavigationCustomizePopupDialog extends Dialog {
    private Activity context = null;
    private String gTitle = null;

    private String gMessage = null;
    private String gPositiveButton = null;
    private String gNegativeButton = null;

    private int gImageResID = 0;
    private View.OnClickListener gPositiveButtonOnClickListener = null;
    private View.OnClickListener gNegativeButtonOnClickListener = null;
    private boolean gCloseButtonEnabled = false;
    private int popupWidth = 0;
    private int messageGravitiy = Gravity.CENTER_HORIZONTAL;

    public IndoorNavigationCustomizePopupDialog(Activity context) {
        super(context);
        this.context = context;
    }

    public IndoorNavigationCustomizePopupDialog(Activity context, String title, String message, String positiveButton,
                                                String negativeButton,
                                                View.OnClickListener positiveButtonOnClickListener,
                                                View.OnClickListener negativeButtonOnClickListener) {
        super(context);
        this.context = context;
        gTitle = title;
        gMessage = message;
        gPositiveButton = positiveButton;
        gNegativeButton = negativeButton;
        gPositiveButtonOnClickListener = positiveButtonOnClickListener;
        gNegativeButtonOnClickListener = negativeButtonOnClickListener;
    }

    public IndoorNavigationCustomizePopupDialog(Activity context, String title, String message, String positiveButton,
                                                String negativeButton,
                                                View.OnClickListener positiveButtonOnClickListener,
                                                View.OnClickListener negativeButtonOnClickListener, int imageResID) {
        super(context);
        this.context = context;
        gTitle = title;
        gMessage = message;
        gPositiveButton = positiveButton;
        gNegativeButton = negativeButton;
        gPositiveButtonOnClickListener = positiveButtonOnClickListener;
        gNegativeButtonOnClickListener = negativeButtonOnClickListener;
        gImageResID = imageResID;
    }

    public IndoorNavigationCustomizePopupDialog(Activity context, String title, String message, String positiveButton,
                                                String negativeButton,
                                                View.OnClickListener positiveButtonOnClickListener,
                                                View.OnClickListener negativeButtonOnClickListener,
                                                int popupWidth,
                                                int messageGravitiy,
                                                boolean closeButtonEnabled) {
        super(context);
        this.context = context;
        gTitle = title;
        gMessage = message;
        gPositiveButton = positiveButton;
        gNegativeButton = negativeButton;
        gPositiveButtonOnClickListener = positiveButtonOnClickListener;
        gNegativeButtonOnClickListener = negativeButtonOnClickListener;
        gCloseButtonEnabled = closeButtonEnabled;
        this.popupWidth = popupWidth;
        this.messageGravitiy = messageGravitiy;
    }

    public IndoorNavigationCustomizePopupDialog(Activity context, String title, String message, String positiveButton,
                                                String negativeButton,
                                                View.OnClickListener positiveButtonOnClickListener,
                                                View.OnClickListener negativeButtonOnClickListener, boolean closeButtonEnabled) {
        super(context);
        this.context = context;
        gTitle = title;
        gMessage = message;
        gPositiveButton = positiveButton;
        gNegativeButton = negativeButton;
        gPositiveButtonOnClickListener = positiveButtonOnClickListener;
        gNegativeButtonOnClickListener = negativeButtonOnClickListener;
        gCloseButtonEnabled = closeButtonEnabled;
    }

    public IndoorNavigationCustomizePopupDialog(Activity context, String title, String message, String positiveButton,
                                                String negativeButton,
                                                View.OnClickListener positiveButtonOnClickListener,
                                                View.OnClickListener negativeButtonOnClickListener,
                                                boolean closeButtonEnabled, int imageResID) {
        super(context);
        this.context = context;
        gTitle = title;
        gMessage = message;
        gPositiveButton = positiveButton;
        gNegativeButton = negativeButton;
        gPositiveButtonOnClickListener = positiveButtonOnClickListener;
        gNegativeButtonOnClickListener = negativeButtonOnClickListener;
        gCloseButtonEnabled = closeButtonEnabled;
        gImageResID = imageResID;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_custom_popup_indoor_navigation);
        if (this.getWindow() != null) {
            this.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        this.setCancelable(false);
        this.setCanceledOnTouchOutside(false);

        TextView tvTitle = findViewById(R.id.tv_title);
        TextView tvMessage = findViewById(R.id.tv_message);
        ImageView ivImage = findViewById(R.id.iv_image);

        if (gImageResID != 0) {
            ivImage.setImageResource(gImageResID);
            ivImage.setVisibility(View.VISIBLE);
        } else {
            ivImage.setVisibility(View.GONE);
        }
        DisplayMetrics gDm = new DisplayMetrics();

        context.getWindowManager().getDefaultDisplay().getMetrics(gDm);
        Convert.mDensity = context.getResources().getDisplayMetrics().density;

        if (popupWidth == 0) {
            popupWidth = gDm.widthPixels * 2 / 3;
        }

        CardView cdPopup = findViewById(R.id.cd_popup);
        RelativeLayout.LayoutParams cdPopupLayoutParams = (RelativeLayout.LayoutParams) cdPopup.getLayoutParams();
        cdPopupLayoutParams.width = popupWidth;
        cdPopup.setLayoutParams(cdPopupLayoutParams);

        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) tvTitle.getLayoutParams();
        layoutParams.width = popupWidth;

        tvTitle.setLayoutParams(layoutParams);
        tvTitle.setText(gTitle);

        tvMessage.setGravity(messageGravitiy);
        tvMessage.setText(gMessage);

        LinearLayout.LayoutParams tvMessageLayoutParams = (LinearLayout.LayoutParams) tvMessage.getLayoutParams();

        tvMessageLayoutParams.leftMargin = Convert.dpToPx(20);
        tvMessageLayoutParams.rightMargin = Convert.dpToPx(20);
        tvMessageLayoutParams.topMargin = 0;
        tvMessage.setLayoutParams(tvMessageLayoutParams);
        if (Utilities.checkIfToDisplayAccessibilityUI(getApplicationContext())) {
            if (gDm.heightPixels > Convert.dpToPx(360)) {
                tvMessage.setMaxHeight(gDm.heightPixels - Convert.dpToPx(360));
            }

            tvMessage.setMovementMethod(ScrollingMovementMethod.getInstance());
        }

        TextView tvPositive = findViewById(R.id.tv_positive);
        TextView tvNegative = findViewById(R.id.tv_negative);

        if (gPositiveButton != null && !gPositiveButton.isEmpty()) {
            tvPositive.setVisibility(View.VISIBLE);
            tvPositive.setText(gPositiveButton);

            tvPositive.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (gPositiveButtonOnClickListener != null) {
                        gPositiveButtonOnClickListener.onClick(v);
                    }
                    dismiss();
                }
            });
        } else {
            tvPositive.setVisibility(View.GONE);
        }

        if (gNegativeButton != null && !gNegativeButton.isEmpty()) {
            tvNegative.setVisibility(View.VISIBLE);
            tvNegative.setText(gNegativeButton);
            tvNegative.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (gNegativeButtonOnClickListener != null) {
                        gNegativeButtonOnClickListener.onClick(v);
                    }
                    dismiss();
                }
            });
        } else {
            tvNegative.setVisibility(View.GONE);
        }

        ImageView ivClose = findViewById(R.id.iv_close);
        if (gCloseButtonEnabled) {
            ivClose.setVisibility(View.VISIBLE);
        } else {
            ivClose.setVisibility(View.GONE);
        }

        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }
}
