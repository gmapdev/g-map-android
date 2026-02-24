package com.ibigroup.mobile.otp.android.json;

import com.google.gson.annotations.SerializedName;

public class DeviceRegisterRequest {
    @SerializedName("region_id")
    private String regionId;

    private double lat;

    private double lng;

    @SerializedName("user_name")
    private String userName;
    @SerializedName("device_id")
    private String deviceId; //only for action='read'

    @SerializedName(value = "device_name", alternate = {"deviceName"})
    private String deviceName;

    @SerializedName("client_version")
    private String clientVersion;

    private String platform;

    @SerializedName("os_version")
    private String osVersion;

    @SerializedName("device_info")
    private String registrationInfo1;

    @SerializedName("re_install")
    private boolean reinstall;

    public String getClientVersion() {
        return clientVersion;
    }

    public void setClientVersion(String clientVersion) {
        this.clientVersion = clientVersion;
    }

    public String getRegionId() {
        return regionId;
    }

    public void setRegionId(String regionId) {
        this.regionId = regionId;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getRegistrationInfo1() {
        return registrationInfo1;
    }

    public void setRegistrationInfo1(String registrationInfo1) {
        this.registrationInfo1 = registrationInfo1;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getOsVersion() {
        return osVersion;
    }

    public void setOsVersion(String osVersion) {
        this.osVersion = osVersion;
    }

    public boolean isReinstall() {
        return reinstall;
    }

    public void setReinstall(boolean reinstall) {
        this.reinstall = reinstall;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }
}
