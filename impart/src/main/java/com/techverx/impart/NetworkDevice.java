package com.techverx.impart;

import java.net.InetAddress;

public class NetworkDevice {

    private String ip;
    private String name;
    private boolean isReachable;
    private String status;

    public NetworkDevice(InetAddress inetAddress, String status) {
        this.ip = inetAddress.getHostAddress();
        this.name = inetAddress.getHostName();
        this.status=status;
    }

    public NetworkDevice(InetAddress inetAddress, String status, boolean isReachable) {
        this.ip = inetAddress.getHostAddress();
        this.name = inetAddress.getHostName();
        this.status=status;
        this.isReachable=isReachable;
    }

    public NetworkDevice(InetAddress inetAddress) {
        this.ip = inetAddress.getHostAddress();
        this.name = inetAddress.getHostName();
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isReachable() {
        return isReachable;
    }

    public void setReachable(boolean reachable) {
        isReachable = reachable;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
