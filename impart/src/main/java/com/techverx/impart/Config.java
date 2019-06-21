package com.techverx.impart;

public class Config {

    private int maxClients;
    private ImpartCallback callback;
    private int port;
    private String path;
    private boolean pingClients;

    private Config() {
    }

    public static Config newConfig() {
        return new Config();
    }

    public int getMaxClients() {
        return maxClients;
    }

    public Config setMaxClients(int maxClients) {
        this.maxClients = maxClients;
        return this;
    }

    public ImpartCallback getCallback() {
        return callback;
    }

    public Config setCallback(ImpartCallback callback) {
        this.callback = callback;
        return this;
    }

    public int getPort() {
        return port;
    }

    public Config setPort(int port) {
        this.port = port;
        return this;
    }

    public Config setPath(String path) {
        this.path = path;
        return this;
    }

    public String getPath() {
        return this.path;
    }

    public boolean isPingClients() {
        return pingClients;
    }

    public Config setPingClients(boolean pingClients) {
        this.pingClients = pingClients;
        return this;
    }
}
