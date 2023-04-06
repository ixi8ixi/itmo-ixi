package com.dinia.message

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.graphics.Bitmap
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

interface CallBack {
    fun updateMessages(list: List<MsgData>)
    fun updateImage(pos: Int, image: Bitmap)
    fun chatName() : String
}

class MessagesFragment : Fragment() {
    private lateinit var chatName: String

    private lateinit var constraintLayout: ConstraintLayout
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewManager: LinearLayoutManager
    private lateinit var sendButton: Button
    private lateinit var refreshButton: Button
    private lateinit var editText: EditText
    private lateinit var pictureButton: Button

    private lateinit var callback: CallBack

    private var isBound = false
    private var myService: SRMessageService? = null

    private var messageList: ArrayList<MsgData> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        chatName = arguments?.getString(CHAT_NAME) ?: "1@channel"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_messages, container, false)
    }

    private val boundServiceConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            val binderBridge: SRMessageService.LocalBinder = service as SRMessageService.LocalBinder
            myService = binderBridge.getService()
            isBound = true
            myService?.callback = callback
            myService?.getNewMessages(messageList.size, chatName)
            Log.i("Bounded: ", chatName)
        }

        override fun onServiceDisconnected(name: ComponentName) {
//            myService?.job?.cancel()
            myService?.callback = null
            myService = null
            isBound = false
        }
    }

    private var resultLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()) { result ->
        result?.let { myService?.sendImage(it, chatName) }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val context = view.context

        constraintLayout = view.findViewById(R.id.constraint)
        recyclerView = constraintLayout.findViewById(R.id.messageView)
        viewManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        viewManager.stackFromEnd = true
        sendButton = constraintLayout.findViewById(R.id.send_button)
        refreshButton = constraintLayout.findViewById(R.id.refresh_button)
        editText = constraintLayout.findViewById(R.id.inputMessageView)
        pictureButton = constraintLayout.findViewById(R.id.picture)

        recyclerView.apply {
            layoutManager = viewManager
            adapter = MsgAdapter(messageList) {
                if (it.isImage) {
                    val toHighRes = Intent(context, HighResPicture::class.java)
                    toHighRes.putExtra(LINK, it.imageLink)
                    startActivity(toHighRes)
                }
            }
        }

        sendButton.setOnClickListener {
            if (editText.text.isNotEmpty()) {
                val text = editText.text.toString()
                myService?.sendText(text, chatName)
                editText.setText("")
            }

//            myService?.chatList()
        }

        refreshButton.setOnClickListener {
            myService?.refresh(chatName)

//                        myService?.maxId()
//            Log.i("And size", messageList.size.toString())
//            myService?.clearDB()
        }

        pictureButton.setOnClickListener {
            val type = "image/*"
            resultLauncher.launch(type)
        }

        callback = object : CallBack {
            override fun updateMessages(list: List<MsgData>) {
                addMessages(list)
            }

            override fun updateImage(pos: Int, image: Bitmap) {
                if (pos < messageList.size) {
                    messageList[pos].image = image
                    recyclerView.adapter?.notifyItemChanged(pos)
                }
            }

            override fun chatName() = chatName
        }
    }

    fun addMessages(list: List<MsgData>) {
        val delta = list.size
        messageList.addAll(list)
        recyclerView.apply {
            adapter?.notifyItemRangeInserted(messageList.size - delta, messageList.size)
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

    companion object {
        private const val CHAT_NAME = "CHAT_NAME"
        const val LINK = "LINK"

        fun create(imagePath: String): MessagesFragment {
            return MessagesFragment().apply {
                arguments = Bundle(1).apply {
                    putString(CHAT_NAME, imagePath)
                }
            }
        }
    }
}