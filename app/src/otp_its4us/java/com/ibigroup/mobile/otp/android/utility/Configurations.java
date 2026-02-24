package com.ibigroup.mobile.otp.android.utility;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;

import androidx.annotation.NonNull;

import com.ibigroup.mobile.ibilib.IBIEnvironment;
import com.ibigroup.mobile.ibilib.IBISecurity;
import com.ibigroup.mobile.otp.android.MyApplication;

public class Configurations {
	public final static String AUTOCOMPLETE_DOMAIN = "https://ao9yvlrax5.execute-api.us-east-1.amazonaws.com/atl/";
	public final static String APP_DOMAIN = "https://zcl3o5i5ib.execute-api.us-east-1.amazonaws.com/prod/";

	public final static String API_VERSION = "otp";

	public final static String GRAPHQL_SERVER_URL = Configurations.getString("app_domain", APP_DOMAIN) + "otp/routers/default/index/graphql";
	public final static String ACCOUNT_HELP_URL = Configurations.getString("help_url", "https://georgia-map.com/");
	public static final String TARGET_TIMEZONE_ID = "US/Eastern";

	public static final String PREFS_NAME = "otp_configurations";
	public final static double LATITUDE_DEFAULT = 33.966016;
	public final static double LONGITUDE_DEFAULT = -84.031541;
	public final static double ZOOM_DEFAULT = 11.0;

	public final static String FARE_PREFIX ="atlanta";
	public final static double MIN_LATITUDE = 32.066;
	public final static double MAX_LATITUDE = 35.7251;
	public final static double MIN_LONGITUDE = -86.0856;
	public final static double MAX_LONGITUDE = -81.9499;
	public final static String JMAP_LOCATIONS_URL = "https://ta-511.s3.amazonaws.com/gmap/jmap_triggerable_locations.json";
	public final static String INDOOR_ENTRANCE_EXIT_URL = "https://ta-511.s3.us-east-1.amazonaws.com/otp/indoor_entrance_exit_list.json";
	public final static String INDOOR_MAIN_ENTRANCE_POPUP_URL = "https://ta-511.s3.us-east-1.amazonaws.com/gmap/indoor_main_entrance_popup.json";
	private static Configurations instance = null;

	private static SharedPreferences preferences = null;


	private Configurations(Context _appContext) {
		preferences = _appContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

		try {
			PackageManager pm = _appContext.getPackageManager();
			PackageInfo pInfo = pm.getPackageInfo(_appContext.getPackageName(), 0);

			if (!preferences.getString("storeVersion","").equals(String.valueOf(pInfo.versionCode))) {
				loadLocalConfigurations();
				preferences.edit().putString("storeVersion", String.valueOf(pInfo.versionCode)).commit();
			}
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
	}

	public static SharedPreferences getConfigurations() {
		if (instance == null) {
			synchronized (Configurations.class) {
				// May be another thread has created an instance, check again within the synched code
				if (instance == null) {
					instance = new Configurations(MyApplication.getInstance());
				}
			}
		}
		return preferences;
	}

	public static void loadLocalConfigurations() {
		SharedPreferences.Editor editor = preferences.edit();

		//Features configuration
		editor.putBoolean("map_style_streets", true);
		editor.putBoolean("enable_layer_park_ride_locations", false);
		editor.putBoolean("enable_layer_transit_stops", true);

		editor.putBoolean("new_search_bar_layout_style", true);
		setBoolean("enable_feature_route_filter", true);

		editor.putString("push_base_url","https://st-push-middleware-test.ibi-transit.com/ext_api/otp_push/");
		editor.putString("device_id","-its");
		editor.putBoolean("enable_push_notification", true);
		editor.putInt("max_saved_trip_number", 5);

		editor.putString("revision_v2", "adb0e2043dd5fc128223f5746d39db80");
		editor.putString("app_identifier_v2","65b317dd7cded8b752be282e2e48ca49");
		editor.putBoolean("metric_system_enabled", false);
		editor.putBoolean("enable_terms_of_storage", true);
		editor.putBoolean("enable_multiple_language", false);
		editor.putString("enable_my_setting_optional_v2", "64ec35a1100edf433a8f31dfad8ec900");
		editor.putString("enable_save_trip_for_all_v2", "b33abc7f930aab5eae1378eae5db4db2"); //true
		editor.putString("enable_start_over_in_menu_v2", "b33abc7f930aab5eae1378eae5db4db2");

		editor.putBoolean("enable_indoor_reroute", false);

		editor.putBoolean("indoor_navigation_audio_announcement_enabled", false);
		editor.putBoolean("enable_itinerary_calories_burned", false);
		editor.commit();
	}

	private static void encryptAndSaveToV2Key(@NonNull String key, @NonNull String value)
	{
		String keyV2 = key + "_v2";
		IBISecurity security = new IBISecurity();
		//Encrypt value, save it to V2 key and delete the old key if exist
		SharedPreferences.Editor editor = preferences.edit();
		editor.putString(keyV2, security.encryptData(value, IBIEnvironment.LOCAL));

		if(preferences.contains(key)) {
			editor.remove(key);
		}
		editor.commit();
	}

	public static void setEncryptedString(@NonNull String key, @NonNull String encryptedValue) {
		String keyV2 = key + "_v2";
		SharedPreferences.Editor editor = preferences.edit();
		editor.putString(keyV2, encryptedValue);
		if(preferences.contains(key)) {
			editor.remove(key);
		}
		editor.commit();
	}

	public static void setString(@NonNull String key, @NonNull String value) {
		if(preferences == null)
		{
			synchronized (Configurations.class) {
				// May be another thread has created an instance, check again within the synched code
				if (instance == null) {
					instance = new Configurations(MyApplication.getInstance());
				}
			}
		}
		boolean forceStringEncrypt = preferences.getBoolean("enable_force_string_encrypt", false);
		if(!forceStringEncrypt && value.length() > Constants.MAX_ENCRYPT_STRING_LENGTH)
		{
			SharedPreferences.Editor editor = preferences.edit();
			editor.putString(key, value);
			String keyV2 = key + "_v2";
			if(preferences.contains(keyV2)) {
				editor.remove(keyV2);
			}
			editor.commit();
		}
		else {
			encryptAndSaveToV2Key(key, value);
		}
	}

	public static void setBoolean(@NonNull String key, boolean value) {
		encryptAndSaveToV2Key(key, String.valueOf(value));
	}

	public static void setInt(@NonNull String key, int value) {
		encryptAndSaveToV2Key(key, String.valueOf(value));
	}

	public static void setFloat(@NonNull String key, float value) {
		encryptAndSaveToV2Key(key, String.valueOf(value));
	}

	public static void setLong(@NonNull String key, long value) {
		encryptAndSaveToV2Key(key, String.valueOf(value));
	}

	public static String getString(String key, String defaultValue) {
		if(preferences != null && key != null)
		{
			String keyV2 = key + "_v2";
			IBISecurity security = new IBISecurity();

			if(preferences.contains(keyV2))
			{
				String encryptedValue = preferences.getString(keyV2, "");

				String decryptedValue = security.decryptData(encryptedValue, IBIEnvironment.LOCAL);

				if(decryptedValue != null)
				{
					return decryptedValue;
				}

			}
			else if(preferences.contains(key)) {
				String value = preferences.getString(key, defaultValue);

				//Encrypt value, save it to V2 key and delete the old key
//				SharedPreferences.Editor editor = preferences.edit();
//				editor.putString(keyV2, security.encryptData(String.valueOf(value), IBIEnvironment.LOCAL));
//				editor.remove(key);
//				editor.commit();

				return value;
			}

		}

		return defaultValue;
	}

	public static boolean getBoolean(String key, boolean defaultValue) {
		if(preferences != null && key != null)
		{
			String keyV2 = key + "_v2";
			IBISecurity security = new IBISecurity();

			if(preferences.contains(keyV2))
			{
				String encryptedValue = preferences.getString(keyV2, "");

				String decryptedValue = security.decryptData(encryptedValue, IBIEnvironment.LOCAL);

				if(decryptedValue != null)
				{
					try {
						return Boolean.parseBoolean(decryptedValue);
					}
					catch (Exception e)
					{
						e.printStackTrace();
					}
				}

			}
			else if(preferences.contains(key)) {
				boolean value = preferences.getBoolean(key, defaultValue);

				//Encrypt value, save it to V2 key and delete the old key
				SharedPreferences.Editor editor = preferences.edit();
				editor.putString(keyV2, security.encryptData(String.valueOf(value), IBIEnvironment.LOCAL));
				editor.remove(key);
				editor.commit();

				return value;
			}
		}

		return defaultValue;
	}

	public static int getInt(String key, int defaultValue) {
		if(preferences != null && key != null)
		{
			String keyV2 = key + "_v2";
			IBISecurity security = new IBISecurity();

			if(preferences.contains(keyV2))
			{
				String encryptedValue = preferences.getString(keyV2, "");

				String decryptedValue = security.decryptData(encryptedValue, IBIEnvironment.LOCAL);

				if(decryptedValue != null)
				{
					try {
						return Integer.parseInt(decryptedValue);
					}
					catch (Exception e)
					{
						e.printStackTrace();
					}
				}
			}
			else if(preferences.contains(key)) {
				int value = preferences.getInt(key, defaultValue);

				//Encrypt value, save it to V2 key and delete the old key
				SharedPreferences.Editor editor = preferences.edit();
				editor.putString(keyV2, security.encryptData(String.valueOf(value), IBIEnvironment.LOCAL));
				editor.remove(key);
				editor.commit();

				return value;
			}

		}

		return defaultValue;
	}

	public static float getFloat(String key, float defaultValue) {
		if(preferences != null && key != null)
		{
			String keyV2 = key + "_v2";
			IBISecurity security = new IBISecurity();

			if(preferences.contains(keyV2))
			{
				String encryptedValue = preferences.getString(keyV2, "");

				String decryptedValue = security.decryptData(encryptedValue, IBIEnvironment.LOCAL);

				if(decryptedValue != null)
				{
					try {
						return Float.parseFloat(decryptedValue);
					}
					catch (Exception e)
					{
						e.printStackTrace();
					}
				}

			}
			else if(preferences.contains(key)) {
				float value = preferences.getFloat(key, defaultValue);

				//Encrypt value, save it to V2 key and delete the old key
				SharedPreferences.Editor editor = preferences.edit();
				editor.putString(keyV2, security.encryptData(String.valueOf(value), IBIEnvironment.LOCAL));
				editor.remove(key);
				editor.commit();

				return value;
			}
		}

		return defaultValue;
	}

	public static long getLong(String key, long defaultValue) {
		if(preferences != null && key != null)
		{
			String keyV2 = key + "_v2";
			IBISecurity security = new IBISecurity();

			if(preferences.contains(keyV2))
			{
				String encryptedValue = preferences.getString(keyV2, "");

				String decryptedValue = security.decryptData(encryptedValue, IBIEnvironment.LOCAL);

				if(decryptedValue != null)
				{
					try {
						return Long.parseLong(decryptedValue);
					}
					catch (Exception e)
					{
						e.printStackTrace();
					}
				}
			}
			else if(preferences.contains(key)) {
				long value = preferences.getLong(key, defaultValue);

				//Encrypt value, save it to V2 key and delete the old key
				SharedPreferences.Editor editor = preferences.edit();
				editor.putString(keyV2, security.encryptData(String.valueOf(value), IBIEnvironment.LOCAL));
				editor.remove(key);
				editor.commit();

				return value;
			}

		}

		return defaultValue;
	}

	public static void startOverResetStatus()
	{
		if(Configurations.getConfigurations() != null) {
			setBoolean("map_style_streets", true);
			setBoolean("enable_layer_park_ride_locations", false);
			setBoolean("enable_layer_transit_stops", true);
//			setBoolean("enabled_search_mode_drive", true);
//			setBoolean("enabled_search_mode_bike", true);
//			setBoolean("enabled_search_mode_transit", true);
//			setBoolean("enabled_search_mode_walk", true);
//			setBoolean("enabled_search_mode_rental", true);
			setBoolean("home_setting_bus_enabled", true);
			setBoolean("home_setting_rail_enabled", true);
			setBoolean("home_setting_street_car_enabled", true);

			setInt("maximum_walk_index", Constants.DEFAULT_MAXIMUM_WALK_INDEX);
			setInt("walk_speed_index", Constants.DEFAULT_WALK_SPEED_INDEX);
			setInt("optimize_option_index", Constants.DEFAULT_OPTIMIZE_OPTION_INDEX);
			setBoolean("map_expanded", false);
			setBoolean("stop_viewer_auto_refresh_enabled", true);
			setInt("itinerary_sort_index", 0);
			setInt("itinerary_sort_direction", Constants.ITINERARY_SORT_DIRECTION_SHORT_TO_LONG);
		}
	}

	public static void searchModeSettingsResetStatus()
	{
		if(Configurations.getConfigurations() != null) {

			setBoolean("enabled_search_mode_drive", false);
			setBoolean("enabled_search_mode_bike", false);
			setBoolean("enabled_search_mode_transit", true);
			setBoolean("enabled_search_mode_walk", false);
			setBoolean("enabled_search_mode_rental", false);

		}
	}
}
