package com.techverx.impart;

import com.techverx.impart.network.model.Error;

public interface ImpartCallback {

    void messageReceived(String from, String packet);

    void newClientAdded(String id);

    void createClientFailed(Error error);

    void sendMessageFailed(Error error);

}
