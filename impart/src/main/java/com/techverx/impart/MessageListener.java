package com.techverx.impart;

public interface MessageListener {
    void onMessageSent(String clientID , String packet);
}
