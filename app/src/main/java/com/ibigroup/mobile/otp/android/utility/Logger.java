package com.ibigroup.mobile.otp.android.utility;

import android.util.Log;

import com.ibigroup.mobile.otp.android.BuildConfig;


public class Logger {
    public static void d(String tag, String msg) {
        if (BuildConfig.DEBUG || Configurations.getBoolean("enable_logger", false)) {
            if (tag != null && msg != null) {
                Log.d(tag, msg);
            }
        }
    }

    public static void i(String tag, String msg) {
        if (BuildConfig.DEBUG || Configurations.getBoolean("enable_logger", false)) {
            if (tag != null && msg != null) {
                Log.i(tag, msg);
            }
        }
    }

    public static void e(String tag, String msg) {
        if (BuildConfig.DEBUG || Configurations.getBoolean("enable_logger", false)) {
            if (tag != null && msg != null) {
                Log.e(tag, msg);
            }
        }
    }
}
