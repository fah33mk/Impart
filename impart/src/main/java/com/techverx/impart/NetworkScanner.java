package com.techverx.impart;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Handler;

import com.techverx.impart.network.AbstractDiscovery;
import com.techverx.impart.network.Constants;
import com.techverx.impart.network.NetworkDiscovery;
import com.techverx.impart.network.NetHelper;
import com.techverx.impart.network.model.HostBean;
import com.techverx.impart.network.model.NetInfo;
import com.techverx.impart.network.onNetworkChangedListener;

import java.util.ArrayList;

public class NetworkScanner {

    private static NetworkScanner networkScanner;
    private NetworkScanCallback networkScanCallback;
    private ArrayList<HostBean> hosts = null;
    private NetHelper netHelper;
    private Activity context;

    private AbstractDiscovery mDiscoveryTask = null;


    private int currentNetwork = 0;
    private long network_ip = 0;
    private long network_start = 0;
    private long network_end = 0;

    private String device_ip = "";

    public NetworkScanner() {
    }

    public synchronized static NetworkScanner getInstance() {
        if (networkScanner == null) {
            networkScanner = new NetworkScanner();
        }
        return networkScanner;
    }

    public void startScan(NetworkScanCallback networkScanCallback, Activity context, String device_ip) {
        this.networkScanCallback = networkScanCallback;
        this.context = context;
        hosts = new ArrayList<>();
        this.device_ip = device_ip;
        run();
    }

    public void stopScan() {
        new Handler().postDelayed(() -> new Thread(() -> {
            if (mDiscoveryTask != null) {
                ((NetworkDiscovery) mDiscoveryTask).shutDownPool();
                mDiscoveryTask = null;
            }
        }).start(), 2000);
    }

    private void run() {
        netHelper = new NetHelper() {
            @Override
            protected void setInfo() {

                if (currentNetwork != net.hashCode()) {
                    currentNetwork = net.hashCode();

                    // Cancel running tasks
                    cancelTasks();
                } else {
                    return;
                }

                network_ip = NetInfo.getUnsignedLongFromIp(net.ip);

                // Detected IP
                int shift = (32 - net.cidr);
                if (net.cidr < 31) {
                    network_start = (network_ip >> shift << shift) + 1;
                    network_end = (network_start | ((1 << shift) - 1)) - 1;
                }

                // Reset ip start-end (is it really convenient ?)
                SharedPreferences.Editor edit = prefs.edit();
                edit.putString(Constants.KEY_IP_START, NetInfo.getIpFromLongUnsigned(network_start));
                edit.putString(Constants.KEY_IP_END, NetInfo.getIpFromLongUnsigned(network_end));
                edit.commit();
            }

            @Override
            protected void cancelTasks() {
                if (mDiscoveryTask != null) {
                    mDiscoveryTask.cancel(true);
                    mDiscoveryTask = null;
                }
            }
        };

        netHelper.init(context);
        netHelper.registerBroadCast();

        new Handler().postDelayed(() -> new Thread(() -> {
            network_ip = NetInfo.getUnsignedLongFromIp(netHelper.net.ip);
            startDiscovering();
        }).start(), 2000);

    }

    /**
     * Discover hosts
     */
    private void startDiscovering() {
        ((NetworkDiscovery) mDiscoveryTask).DEVICE_IP = device_ip;
        mDiscoveryTask = new NetworkDiscovery(context, networkScanCallback);
        mDiscoveryTask.setNetwork(network_ip, network_start, network_end);
        mDiscoveryTask.execute();


        mDiscoveryTask.setOnHostScanListener(new onNetworkChangedListener() {
            @Override
            public void onHostFound(HostBean... host) {
                addHost(host[0]);

            }

            @Override
            public void onDiscoveryFinished() {
                networkScanCallback.onScanCompleted(hosts);
            }

            @Override
            public void onDiscoveryCanceled() {

            }
        });
    }

    public void addHost(HostBean host) {
        host.position = hosts.size();
        hosts.add(host);
    }

    public void unregisterBroadCast() {
        if (netHelper != null)
            netHelper.unregisterBroadCast();
    }
}