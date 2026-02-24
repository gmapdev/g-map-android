package com.ibigroup.mobile.otp.android.json;

import com.google.gson.annotations.SerializedName;

public class OTPCredentials {
    @SerializedName("id_token")
    private String idToken;

    @SerializedName("access_token")
    private String accessToken;

    @SerializedName("token_type")
    private String type;

    @SerializedName("refresh_token")
    private String refreshToken;

    @SerializedName("expires_at")
    private long expiresAt;

    @SerializedName("scope")
    private String scope;

    public String getIdToken() {
        return idToken;
    }

    public void setIdToken(String idToken) {
        this.idToken = idToken;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public long getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(long expiresAt) {
        this.expiresAt = expiresAt;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }
}
