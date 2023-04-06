package com.dinia.message

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

interface ChatListCallBack {
    fun updateChatList(list: List<ChatData>)
}

class ChatListFragment : Fragment() {
    private lateinit var recycler: RecyclerView
    private lateinit var callback: ChatListCallBack
    private var isBound = false
    private var myService: SRMessageService? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_chat_list, container, false)
    }

    private val boundServiceConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            val binderBridge: SRMessageService.LocalBinder = service as SRMessageService.LocalBinder
            myService = binderBridge.getService()
            isBound = true
            myService?.chatCallBack = callback
//            myService?.getChatsList()
        }

        override fun onServiceDisconnected(name: ComponentName) {
            myService?.callback = null
            myService = null
            isBound = false
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val context = view.context

        recycler = view.findViewById(R.id.recycler)

        recycler.layoutManager = LinearLayoutManager(context)
        callback = object : ChatListCallBack {
            override fun updateChatList(list: List<ChatData>) {
                recycler.apply {
                    adapter = ChatAdapter(list) {
                        (context as ImagePathClickListener).onChatClick(it.title, view)
                    }
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()

        val intent = Intent(activity, SRMessageService::class.java)
        activity?.startService(intent)
        activity?.bindService(intent, boundServiceConnection, AppCompatActivity.BIND_AUTO_CREATE)
    }

    override fun onStop() {
        super.onStop()
        activity?.unbindService(boundServiceConnection)
    }

//    companion object {
//        @JvmStatic
//        fun newInstance(param1: String, param2: String) =
//            ChatListFragment().apply {
//                arguments = Bundle().apply {
//                    // todo to be implemented ...
//                }
//            }
//    }
}