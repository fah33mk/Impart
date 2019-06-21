package com.techverx.impart.network.model;

import com.google.gson.annotations.SerializedName;

public class JsonMessage {

    @SerializedName("appName")
    private String appName;
    @SerializedName("pkgName")
    private String pkgName;
    @SerializedName("versionName")
    private String versionName;
    @SerializedName("versionCode")
    private String versionCode;
    @SerializedName("ip")
    private String ip;
    @SerializedName("mac")
    private String mac;
    @SerializedName("messageType")
    private String messageType;

    public JsonMessage(String appName, String pkgName, String versionName, String versionCode, String ip, String mac, String messageType) {
        this.appName = appName;
        this.pkgName = pkgName;
        this.versionName = versionName;
        this.versionCode = versionCode;
        this.ip = ip;
        this.mac = mac;
        this.messageType = messageType;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getPkgName() {
        return pkgName;
    }

    public void setPkgName(String pkgName) {
        this.pkgName = pkgName;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public String getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(String versionCode) {
        this.versionCode = versionCode;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }
}
