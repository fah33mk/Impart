package com.techverx.impart.network;

import android.app.Activity;
import android.os.AsyncTask;

import com.techverx.impart.NetworkScanCallback;
import com.techverx.impart.network.model.HostBean;

import java.lang.ref.WeakReference;


public abstract class AbstractDiscovery extends AsyncTask<Void, HostBean, Void> {

    protected int hosts_done = 0;
    final protected WeakReference<Activity> mDiscover;

    protected long ip;
    protected long start = 0;
    protected long end = 0;
    protected long size = 0;

    private onNetworkChangedListener listener;
    private NetworkScanCallback networkScanCallback;

    public AbstractDiscovery(Activity discover, NetworkScanCallback networkScanCallback) {
        mDiscover = new WeakReference<Activity>(discover);
        this.networkScanCallback = networkScanCallback;
    }

    public void setOnHostScanListener(onNetworkChangedListener listener) {
        this.listener = listener;
    }

    public void setNetwork(long ip, long start, long end) {
        this.ip = ip;
        this.start = start;
        this.end = end;
    }

    abstract protected Void doInBackground(Void... params);

    @Override
    protected void onPreExecute() {
        size = (int) (end - start + 1);
        if (mDiscover != null) {
            final Activity discover = mDiscover.get();
            if (discover != null) {
                discover.setProgress(0);
            }
        }
    }

    @Override
    protected void onProgressUpdate(HostBean... host) {
        if (mDiscover != null) {
            final Activity discover = mDiscover.get();
            if (discover != null) {
                if (!isCancelled()) {
                    if (host[0] != null) {
                        listener.onHostFound(host[0]);
                    }
                    if (size > 0) {
                        networkScanCallback.onProgressChanged((int) (hosts_done * 100 / size), 100);

                        discover.setProgress((int) (hosts_done * 10000 / size));
                    }
                }

            }
        }
    }

    @Override
    protected void onPostExecute(Void unused) {
        if (mDiscover != null) {
            final Activity discover = mDiscover.get();
            if (discover != null) {
                listener.onDiscoveryFinished();
            }
        }
    }

    @Override
    protected void onCancelled() {
        if (mDiscover != null) {
            final Activity discover = mDiscover.get();
            if (discover != null) {
                listener.onDiscoveryCanceled();
            }
        }
        super.onCancelled();
    }


}
