package com.techverx.impart.network;

import com.techverx.impart.network.model.HostBean;

public interface onNetworkChangedListener {

    void onHostFound(HostBean... host);

    void onDiscoveryFinished();

    void onDiscoveryCanceled();

}
