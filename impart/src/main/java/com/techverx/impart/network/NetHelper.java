package com.techverx.impart.network;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

import com.techverx.impart.network.model.NetInfo;

public abstract class NetHelper {

    private final String TAG = "NetState";
    private ConnectivityManager connMgr;

    protected final static String EXTRA_WIFI = "wifiDisabled";
    protected Context ctxt;
    protected SharedPreferences prefs = null;
    public NetInfo net = null;
    protected String info_ip_str = "";
    protected String info_in_str = "";
    protected String info_mo_str = "";

    public void init(Context context) {
        ctxt = context;
        prefs = PreferenceManager.getDefaultSharedPreferences(ctxt);
        connMgr = (ConnectivityManager) ctxt.getSystemService(Context.CONNECTIVITY_SERVICE);
        net = new NetInfo(ctxt);
    }

    public void registerBroadCast() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION);
        ctxt.registerReceiver(receiver, filter);
    }

    public void unregisterBroadCast(){
        ctxt.unregisterReceiver(receiver);
    }

    public void unregisterBroadcast() {
        ctxt.unregisterReceiver(receiver);
    }

    protected abstract void setInfo();

    protected abstract void cancelTasks();

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @SuppressLint("StringFormatMatches")
        public void onReceive(Context ctxt, Intent intent) {
            info_ip_str = "";
            info_mo_str = "";

            // Wifi state
            String action = intent.getAction();
            if (action != null) {
                if (action.equals(WifiManager.WIFI_STATE_CHANGED_ACTION)) {
                    int WifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, -1);
                    //Log.d(TAG, "WifiState=" + WifiState);
                    switch (WifiState) {
                        case WifiManager.WIFI_STATE_ENABLING:
                            break;
                        case WifiManager.WIFI_STATE_ENABLED:
                            break;
                        case WifiManager.WIFI_STATE_DISABLING:
                            break;
                        case WifiManager.WIFI_STATE_DISABLED:
                            break;
                        default:
                    }
                }

                if (action.equals(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION) && net.getWifiInfo()) {
                    SupplicantState sstate = net.getSupplicantState();
                    //Log.d(TAG, "SupplicantState=" + sstate);
                    if (sstate == SupplicantState.SCANNING) {
                    } else if (sstate == SupplicantState.ASSOCIATING) {
                    } else if (sstate == SupplicantState.COMPLETED) {
                    }
                }
            }

            // 3G(connected) -> Wifi(connected)
            // Support Ethernet, with ConnectivityManager.TYPE_ETHER=3
            final NetworkInfo ni = connMgr.getActiveNetworkInfo();
            if (ni != null) {
                //Log.i(TAG, "NetworkState="+ni.getDetailedState());
                if (ni.getDetailedState() == NetworkInfo.DetailedState.CONNECTED) {
                    int type = ni.getType();
                    //Log.i(TAG, "NetworkType="+type);
                    if (type == ConnectivityManager.TYPE_WIFI) { // WIFI
                        net.getWifiInfo();
                        if (net.ssid != null) {
                            net.getIp();

                        }
                    } else if (type == ConnectivityManager.TYPE_MOBILE) { // 3G
                        net.getMobileInfo();
                        if (net.carrier != null) {
                            net.getIp();
                        }
                    } else if (type == 3) { // ETH
                        Log.i(TAG, "Ethernet connectivity detected!");
                    } else {
                        Log.i(TAG, "Connectivity unknown!");
                    }
                } else {
                    cancelTasks();
                }
            } else {
                cancelTasks();
            }
            setInfo();
        }
    };
}
