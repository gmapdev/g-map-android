package com.ibigroup.mobile.otp.android.setup;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.view.Display;
import android.view.WindowManager;

import com.ibigroup.mobile.otp.android.utility.Configurations;

public class OTPContext {
    private static String appName;
    private static String appVersion;
    private static String deviceName;
    private static String platformVersion;
    private static String carrierName;
    private static String hashedDeviceID;
    private static Context appContext;
    private static Activity currentActivity;
    private static Display display;

    /**
     * Ensure the class is not instantiated.
     */
    private OTPContext() {
    }

    /**
     * Initialize the context at start-up.
     */
    public static void init(Context _applicationContext) throws Exception {
        appContext = _applicationContext;

        PackageManager pm = _applicationContext.getPackageManager();

        try {
            PackageInfo pInfo = pm.getPackageInfo(_applicationContext.getPackageName(), 0);
            appName = pInfo.applicationInfo.loadLabel(pm).toString();
            appVersion = pInfo.versionName;
        } catch (NameNotFoundException e) {
            appName = "NA";
            appVersion = "NA";
        }
        deviceName = Build.MODEL;
        platformVersion = "" + Build.VERSION.SDK_INT;
        TelephonyManager tm = (TelephonyManager) _applicationContext.getSystemService(Context.TELEPHONY_SERVICE);
        carrierName = tm.getNetworkOperatorName();

        //hashedDeviceID = Installation.id(_applicationContext);
        hashedDeviceID = Settings.Secure.getString(appContext.getContentResolver(),
                Settings.Secure.ANDROID_ID);
    }

    public static Display getDisplay() {
        display = ((WindowManager) appContext.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        return display;
    }

    public static String getAppName() {
        return appName;
    }

    public static void setAppName(String appName) {
        OTPContext.appName = appName;
    }

    public static String getActualAppVersion() {
        return appVersion;
    }

    public static String getAppVersion() {
        if (carrierName != null)
            return ("ANDROID-" + deviceName + "|" + platformVersion + "|" + appVersion + "|" + Configurations.getString("app_identifier", "") + "|" + carrierName + "|" + getDisplay().getWidth() + "x" + getDisplay().getHeight());
        else
            return ("ANDROID-" + deviceName + "|" + platformVersion + "|" + appVersion + "|" + Configurations.getString("app_identifier", "") + "|null" + "|" + getDisplay().getWidth() + "x" + getDisplay().getHeight());
    }

    public static void setAppVersion(String appVersion) {
        OTPContext.appVersion = appVersion;
    }

    public static String getDeviceName() {
        return deviceName;
    }

    public static void setDeviceName(String deviceName) {
        OTPContext.deviceName = deviceName;
    }

    public static String getPlatformVersion() {
        return platformVersion;
    }

    public static void setPlatformVersion(String platformVersion) {
        OTPContext.platformVersion = platformVersion;
    }

    public static String getCarrierName() {
        return carrierName;
    }

    public static void setCarrierName(String carrierName) {
        OTPContext.carrierName = carrierName;
    }

    public static String getHashedDeviceID() {
        if (Build.MODEL.equals("google_sdk") || Configurations.getBoolean("enable_simulator", false)) {
            return "simulator-" + hashedDeviceID + Configurations.getString("device_id", "");
        }
        return hashedDeviceID + Configurations.getString("device_id", "");
    }

    public static void setHashedDeviceID(String hashedDeviceID) {
        OTPContext.hashedDeviceID = hashedDeviceID;
    }

    public static Context getAppContext() {
        return appContext;
    }

    public static void setAppContext(Context appContext) {
        OTPContext.appContext = appContext;
    }

    public static Activity getCurrentActivity() {
        return currentActivity;
    }

    public static void setCurrentActivity(
            Activity currentActivity) {
        OTPContext.currentActivity = currentActivity;
    }
}