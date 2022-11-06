package com.dinia.message

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.lang.ref.WeakReference

class MsgServiceCallback {
    var alive = true
    var listener: ChangeListener? = null

    fun refreshRequired() {
        listener?.onRefresh()
    }

    interface ChangeListener {
        fun onRefresh()
    }
}

class MainActivity : AppCompatActivity() {
    lateinit var recyclerView: RecyclerView
    private val viewManager = LinearLayoutManager(this)
    private val callback = MsgServiceCallback()
    lateinit var sendButton: Button

    var myService: SRMessageService? = null
    var isBound = false

    private val boundServiceConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            val binderBridge: SRMessageService.LocalBinder = service as SRMessageService.LocalBinder
            myService = binderBridge.getService()
            isBound = true
            myService?.activity = WeakReference(this@MainActivity)
        }

        override fun onServiceDisconnected(name: ComponentName) {
            isBound = false
            myService = null
        }
    }

    fun refreshRequired() {

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        recyclerView = findViewById(R.id.messageView)
        sendButton = findViewById(R.id.send_button)

        sendButton.setOnClickListener {
            // send message here ...
        }
    }

    override fun onStart() {
        super.onStart()

        val intent = Intent(this, SRMessageService::class.java)
        startService(intent)
        bindService(intent, boundServiceConnection, BIND_AUTO_CREATE)

        recyclerView.apply {
            layoutManager = viewManager
            adapter = MsgAdapter(listOf()) {}
        }
    }

    override fun onStop() {
        super.onStop()
        callback.alive = false
        if (isBound) {
            unbindService(boundServiceConnection)
        }
    }

}