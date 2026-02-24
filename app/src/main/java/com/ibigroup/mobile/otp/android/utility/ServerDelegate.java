package com.ibigroup.mobile.otp.android.utility;

import android.os.Build;
import android.provider.Settings;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.auth0.android.result.UserProfile;
import com.google.gson.Gson;
import com.ibigroup.mobile.otp.android.MyApplication;
import com.ibigroup.mobile.otp.android.api.Resource;
import com.ibigroup.mobile.otp.android.json.DeviceRegisterRequest;
import com.ibigroup.mobile.otp.android.setup.OTPContext;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ServerDelegate {
    private static void errorHandler(VolleyError error, String APIName) {
        if (error != null && error.networkResponse != null && error.networkResponse.data != null) {
            Logger.d("API call", APIName + ", status code: " + error.networkResponse.statusCode);
        } else {
            Logger.d("API call", APIName + ", network error");
        }
    }

    public static HashMap<String, String> getCommonHeader() {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("x-api-key", Configurations.getString("request_api_key", ""));

        if (Utilities.isLoggedIn() && Utilities.isEmailVerified()) {
            //Logger.d("header", "Is Email Verified!");
            params.put("authorization", "Bearer " + Configurations.
                    getString("account_access_token", ""));
        }
        return params;
    }

    public static StringRequest volleyStringRequest(final int method, final String url, final String params,
                                                    final Response.Listener listener,
                                                    final Response.ErrorListener errorListener) {
        Logger.i("string request", "Url:" + url + ",param:" + params);
        StringRequest req = new StringRequest(method,
                url,
                listener, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                errorHandler(error, url);
                if (errorListener != null) {
                    errorListener.onErrorResponse(error);
                }
            }
        }) {
            @Override
            public byte[] getBody() {

                return params.getBytes();
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> params = getCommonHeader();

                return params;
            }
        };
        req.setRetryPolicy(
                new DefaultRetryPolicy(
                        500000,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                )
        );
        // Adding request to request queue
        MyApplication.getInstance().addToRequestQueue(url, req);
        return req;
    }

    public static HashMap<String, String> getAccountHeader() {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("x-api-key", Configurations.getString("account_api_key", ""));
        params.put("authorization", "Bearer " + Configurations.
                getString("account_access_token", ""));
        return params;
    }

    public static StringRequest volleyAccountStringRequest(final int method, final String url, final String params,
                                                           final Response.Listener listener,
                                                           final Response.ErrorListener errorListener) {
        Logger.i("string request", "Url:" + url + ",param:" + params);
        StringRequest req = new StringRequest(method,
                url,
                listener, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                errorHandler(error, url);
                if (errorListener != null) {
                    errorListener.onErrorResponse(error);
                }
            }
        }) {
            @Override
            public byte[] getBody() {

                return params.getBytes();
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return getAccountHeader();
            }

            @Override
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded";
            }
        };
        req.setRetryPolicy(
                new DefaultRetryPolicy(
                        500000,//DefaultRetryPolicy.DEFAULT_TIMEOUT_MS,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                )
        );
        // Adding request to request queue
        MyApplication.getInstance().addToRequestQueue(url, req);
        return req;
    }

    public static StringRequest volleyAccountStringRequestExt(final int method, final String url, final String params,
                                                              final Response.Listener listener,
                                                              final Response.ErrorListener errorListener) {
        Logger.i("string request", "Url:" + url + ",param:" + params);
        StringRequest req = new StringRequest(method,
                url,
                listener, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                errorHandler(error, url);
                if (errorListener != null) {
                    errorListener.onErrorResponse(error);
                }
            }
        }) {
            @Override
            public byte[] getBody() {

                return params.getBytes();
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return getAccountHeader();
            }

            @Override
            public String getBodyContentType() {
                return "application/json";
            }
        };
        req.setRetryPolicy(
                new DefaultRetryPolicy(
                        500000,//DefaultRetryPolicy.DEFAULT_TIMEOUT_MS,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                )
        );
        // Adding request to request queue
        MyApplication.getInstance().addToRequestQueue(url, req);
        return req;
    }

    public static JsonObjectRequest volleyJsonRequest(final int method, final String url, final String params,
                                                      final Response.Listener listener,
                                                      final Response.ErrorListener errorListener) {
        Logger.i("string request", "Url:" + url + ",param:" + params);
        JsonObjectRequest req = new JsonObjectRequest(method,
                url,
                listener, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                errorHandler(error, url);
                if (errorListener != null) {
                    errorListener.onErrorResponse(error);
                }
            }
        }) {
            @Override
            public byte[] getBody() {

                return params.getBytes();
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/json");
                return params;
            }
        };
        req.setRetryPolicy(
                new DefaultRetryPolicy(
                        500000,//DefaultRetryPolicy.DEFAULT_TIMEOUT_MS,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                )
        );
        // Adding request to request queue
        MyApplication.getInstance().addToRequestQueue(url, req);
        return req;
    }

    public static void postRegistration(String regIdString) throws Exception {
        if (!Configurations.getBoolean("enable_push_notification", false)) {
            return;
        }

        String url = Configurations.getString("push_base_url", "https://ta511.ibigroupmobile.com/ext_api/")
                + Configurations.getString("app_identifier", "")
                + Resource.URL_REGISTER_DEVICE
                + "?" + "api_key" + "=" + Configurations.getString("app_api_key", "");

        Gson gson = new Gson();
        DeviceRegisterRequest request = new DeviceRegisterRequest();

        request.setRegionId("");
        if (MyApplication.getInstance().currentLocation != null) {
            request.setLat(MyApplication.getInstance().currentLocation.getLatitude());
            request.setLng(MyApplication.getInstance().currentLocation.getLongitude());
        } else {
            request.setLat(0.0f);
            request.setLng(0.0f);
        }
        String userEmail = null;
        String profileString = Configurations.getString("account_user_profile", "");
        if (profileString != null && !profileString.isEmpty()) {
            UserProfile userProfile = gson.fromJson(profileString, UserProfile.class);

            if (userProfile != null) {
                if (userProfile.getEmail() != null) {
                    userEmail = userProfile.getEmail();
                } else {
                    userEmail = userProfile.getName();
                }
            }
        }

        if (userEmail != null) {
            request.setUserName(userEmail);
        } else {
            request.setUserName("NA");
        }
        request.setDeviceId(Utilities.getHashedDeviceID(MyApplication.getInstance().getApplicationContext()));
        request.setClientVersion(Utilities.getAppVersion(MyApplication.getInstance().getApplicationContext()));
        request.setPlatform("ANDROID");
        request.setOsVersion(String.valueOf(Build.VERSION.SDK_INT));
        request.setRegistrationInfo1(regIdString);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            request.setDeviceName(Settings.Global.getString(MyApplication.getInstance().getApplicationContext().getContentResolver(), Settings.Global.DEVICE_NAME));
        } else {
            request.setDeviceName(Build.MODEL);
        }
        request.setReinstall(Configurations.getBoolean("re_install", true));
        Configurations.setString("device_token", regIdString);
        final String jsonRequest = gson.toJson(request);
        Logger.d("postRegistration", "url：" + url + "，request=" + jsonRequest);

        volleyJsonRequest(Request.Method.POST, url, jsonRequest, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Logger.d("postRegistration", "response=" + response.toString());
                Configurations.setBoolean("re_install", false);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String log = "Register device error=" + error.toString();
                Logger.d("postRegistration", "error=" + log);
            }
        });
    }

    public static StringRequest simpleVolleyStringRequest(final int method, final String url, final String params,
                                                          final Response.Listener listener,
                                                          final Response.ErrorListener errorListener) {
        Logger.i("string request", "Url:" + url + ",param:" + params);
        StringRequest req = new StringRequest(method,
                url,
                listener, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                errorHandler(error, url);
                if (errorListener != null) {
                    errorListener.onErrorResponse(error);
                }
            }
        }) {
            @Override
            public byte[] getBody() {

                return params.getBytes();
            }
        };
        req.setRetryPolicy(
                new DefaultRetryPolicy(
                        500000,//DefaultRetryPolicy.DEFAULT_TIMEOUT_MS,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                )
        );
        // Adding request to request queue
        MyApplication.getInstance().addToRequestQueue(url, req);
        return req;
    }

    public static StringRequest volleyTrackingStringRequest(final int method, final String url, final String params,
                                                            final Response.Listener listener,
                                                            final Response.ErrorListener errorListener) {
        Logger.i("string request", "Url:" + url + ",param:" + params);
        StringRequest req = new StringRequest(method,
                url,
                listener, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                errorHandler(error, url);
                if (errorListener != null) {
                    errorListener.onErrorResponse(error);
                }
            }
        }) {
            @Override
            public byte[] getBody() {

                return params.getBytes();
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<String, String>();
                params.put("x-api-key", Configurations.getString("account_api_key", ""));
                params.put("authorization", "Bearer " + Configurations.
                        getString("account_access_token", ""));

                params.put("app_platform", "android_" + Build.VERSION.RELEASE + "_" + Build.VERSION.SDK_INT);
                params.put("device_id", OTPContext.getHashedDeviceID());
                params.put("app_version", OTPContext.getActualAppVersion());

                return params;
            }

            @Override
            public String getBodyContentType() {
                return "application/json";
            }
        };
        req.setRetryPolicy(
                new DefaultRetryPolicy(
                        500000,//DefaultRetryPolicy.DEFAULT_TIMEOUT_MS,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                )
        );
        // Adding request to request queue
        MyApplication.getInstance().addToRequestQueue(url, req);
        return req;
    }

    public static JsonObjectRequest volleyJsonObjectRequestForLogSubmit(final int method, final String url, final String params,
                                                                        final Response.Listener listener,
                                                                        final Response.ErrorListener errorListener) {
        Logger.i("string request", "Url:" + url + ",param:" + params);
        JsonObjectRequest req = new JsonObjectRequest(method,
                url,
                listener, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                errorHandler(error, url);
                if (errorListener != null) {
                    errorListener.onErrorResponse(error);
                }
            }
        }) {
            @Override
            public byte[] getBody() {

                return params.getBytes();
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/json");
                params.put("device_id", OTPContext.getHashedDeviceID());
                params.put("device_os", "Android");
                return params;
            }
        };
        req.setRetryPolicy(
                new DefaultRetryPolicy(
                        500000,//DefaultRetryPolicy.DEFAULT_TIMEOUT_MS,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                )
        );
        // Adding request to request queue
        MyApplication.getInstance().addToRequestQueue(url, req);
        return req;
    }
}
