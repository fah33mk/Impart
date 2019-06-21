package com.example.darxi

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.techverx.impart.*
import com.techverx.impart.network.model.Error
import com.techverx.impart.network.model.HostBean
import java.util.*

class MainActivity : AppCompatActivity(), ImpartCallback, NetworkScanCallback {
    override fun onScanCompleted(ipList: ArrayList<HostBean>?) {
        // a list of devices will be received here
    }

    override fun onProgressChanged(progress: Int, totalProgress: Int) {
        // progress of the scanning will be updated here
    }

    override fun messageReceived(from: String?, packet: String?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun newClientAdded(id: String?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun createClientFailed(error: Error?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun sendMessageFailed(error: Error?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // To start Impart Client
        Impart.instance()
            .start(Config.newConfig().setCallback(this).setMaxClients(20).setPath("").setPort(3838).setPingClients(false))
        Impart.instance().createClient("hostName", 3421, true)
        Impart.instance().sendMessage("hostname:port", "message")
        NetworkScanner.getInstance().startScan(this, this, ""/*Get Your Device Ip and place here*/);
    }
}
