package com.dinia.message

import android.app.Service
import android.content.Intent
import android.os.*
import android.util.Log
import java.lang.ref.WeakReference

class SRMessageService : Service() {
    var activity: WeakReference<MainActivity>? = null
    private var messageThread: HandlerThread = HandlerThread("SRMessageThread")
    var handler: Handler? = null

    var messageList = ArrayList<MsgData>()
    private var lastId: Long = 0

    companion object {
        const val DELAY: Long = 3000
    }

    private val checkServer: Runnable = object: Runnable {
        override fun run() {
            if (activity != null) {
                Log.wtf("TAG", "Weak reference isn't null")
            } else {
                Log.wtf("TAG", "Weak reference is null")
            }
            handler?.postDelayed(this, DELAY)
        }
    }

    private fun startChecks() {
        handler?.postDelayed(checkServer, DELAY)
    }

    override fun onCreate() {
        super.onCreate()
        messageThread.start()
        handler = Handler(messageThread.looper)
        messageList.add(MsgData("Server", "Dinia", "Hello!", "13:37"))
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        startChecks()
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        messageThread.quit()
        handler = null
    }

    override fun onBind(intent: Intent): IBinder {
        return LocalBinder()
    }

    inner class LocalBinder: Binder() {
        fun getService() = this@SRMessageService
    }
}