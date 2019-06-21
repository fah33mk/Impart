package com.techverx.impart;

import com.techverx.impart.network.model.HostBean;

import java.util.ArrayList;

public interface NetworkScanCallback {
    void onScanCompleted(ArrayList<HostBean> ipList);

    void onProgressChanged(int progress, int totalProgress);
}
