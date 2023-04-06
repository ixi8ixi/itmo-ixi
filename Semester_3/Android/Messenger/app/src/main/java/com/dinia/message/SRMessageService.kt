package com.dinia.message

import android.app.Service
import android.content.Intent
import android.net.Uri
import android.os.*
import android.util.Log
import android.widget.Toast
import androidx.room.Room
import kotlinx.coroutines.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.net.ConnectException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class SRMessageService : Service() {
    var callback: CallBack? = null
    var chatCallBack: ChatListCallBack? = null

    private var chats = TreeMap<String, ArrayList<MsgData>>()
    private var refreshInProgress = false

    private lateinit var db: AppDatabase
    private lateinit var dao: MessagesDao

    private lateinit var api: Api

    private var topInbox = 0

    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.Default + job)

    private val df = SimpleDateFormat("HH:mm", Locale.getDefault())

    override fun onCreate() {
        super.onCreate()
        db = Room.databaseBuilder(applicationContext, AppDatabase::class.java, "my-db").build()
        dao = db.messageDao()
        api = App.instance.getApi()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(p0: Intent?): IBinder {
        return LocalBinder()
    }

    inner class LocalBinder: Binder() {
        fun getService() = this@SRMessageService
    }

    private fun localStr(id: Int): String {
        return resources.getString(id)
    }

    private fun showToast(text: String) {
        scope.launch(Dispatchers.Main) {
            val duration = Toast.LENGTH_SHORT
            val toast = Toast.makeText(applicationContext, text, duration)
            toast.show()
        }
    }

    private fun showMessageByCode(code: Int) {
        val text = when {
            code / 100 == 5 -> localStr(R.string.server_error_code)
            code == 404 -> localStr(R.string.client_error_not_found)
            code == 409 -> localStr(R.string.client_error_conflict)
            code == 413 -> localStr(R.string.client_error_payload_too_large)
            else -> localStr(R.string.client_error_strange)
        }
        showToast(text)
    }

    fun refresh(chat: String) {
        Log.i("Refresh start: ", chat)
        if (refreshInProgress) {
            return
        }
        refreshInProgress = true
        scope.launch(Dispatchers.IO) {
            if (!chats.containsKey(chat)) {
                chats[chat] = ArrayList()
                val list = dao.getChatMessages(0, chat)
                withContext(Dispatchers.Main) {
                    chats[chat]?.addAll(list)
                    callback?.updateMessages(list)
                }
            }

            try {
                while (true) {
                    if (callback == null || callback?.chatName() != chat) {
                        break
                    }
                    val tid = if (chats[chat]!!.size > 0) chats[chat]!!.last().id else 0
                    val response = api.getChatMessages(chat, tid)
                    if (!response.isSuccessful) {
                        val code = response.code()
                        showMessageByCode(code)
                        break
                    }
                    val list = response.body()
                    if (list == null || list.isEmpty()) {
                        break
                    }
                    val resultList = ArrayList<MsgData>()
                    for (i in list.indices) {
                        val entity = list[i]
                        resultList.add(
                            MsgData(entity.id!!,
                                (chats[chat]?.size ?: 0) + i,
                                entity.data.Image != null,
                                entity.from,
                                entity.to!!,
                                entity.data.Text?.text,
                                entity.data.Image?.link,
                                chat,
                                df.format(Date(entity.time!!)) )
                        )
                    }

                    dao.insertList(resultList)
                    withContext(Dispatchers.Main) {
                        chats[chat]?.addAll(resultList)
                        if (callback?.chatName() == chat) {
                            callback?.updateMessages(resultList)
                        }
                    }
                }
            } catch (e: ConnectException) {
                val text = localStr(R.string.connection_failed)
                showToast(text)
            }

            withContext(Dispatchers.Main) {
                refreshInProgress = false
            }
        }
    }

    fun getNewMessages(size: Int, chat: String) {
        Log.i("Get new messages: ", chats[chat].toString())
        if (chats[chat] != null && size < chats[chat]!!.size) {
            callback?.updateMessages(chats[chat]!!.subList(size, chats[chat]!!.size))
        }
        refresh(chat)
    }

    fun sendText(text: String, chat: String) {
        val call = api.sendMessage(
            MessageEntity(
                null,
                App.NAME,
                chat,
                Data(
                    Content(text, null),
                    null),
                null
            )
        )

        call.enqueue(object : retrofit2.Callback<MessageEntity> {
            override fun onResponse(call: Call<MessageEntity>, response: Response<MessageEntity>) {
                if (response.isSuccessful) {
//                    refresh()
                } else {
                    val code = response.code()
                    showMessageByCode(code)
                }
            }

            override fun onFailure(call: Call<MessageEntity>, t: Throwable) {
//                val toast = localStr(R.string.send_error)
//                showToast(toast)
            }

        })
    }

    fun sendImage(uri: Uri, chat: String) {
        scope.launch(Dispatchers.IO) {
            val name = "${filesDir}/${ System.currentTimeMillis()}.jpg"
            val file = File(name)
            contentResolver.openInputStream(uri)?.use { inputStream ->
                FileOutputStream(file).use { outputStream ->
                    val buffer = ByteArray(1024)
                    while (true) {
                        val length = inputStream.read(buffer)

                        if (length == -1) {
                            break
                        }

                        outputStream.write(buffer, 0, length)
                    }
                }
            }

            val requestFile = RequestBody.create(
                MediaType.parse(contentResolver.getType(uri)!!),
                file
            )
            val body = MultipartBody.Part.createFormData(
                "picture",
                file.name,
                requestFile
            )
            val description = RequestBody.create(
                MediaType.parse("application/json"),
                "{\"from\":\"${App.NAME}\"," +
                        "\"to\":\"$chat\"," +
                        "\"data\":{\"Image\":{\"link\":\"${file.absolutePath}\"}}}"
            )

            Log.i("SIZE: ", file.length().toString())

            val call = api.sendImage(description, body)
            call.enqueue(object : retrofit2.Callback<ResponseBody?> {
                override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>
                ) {
                    Log.i("CODE: ", response.code().toString())
                    showMessageByCode(response.code())
                }

                override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
//                    val toast = localStr(R.string.send_error)
//                    showToast(toast)
                }
            })
        }
    }

    fun refreshChatsList() {

    }

//    fun getChatsList() {
//        scope.launch(Dispatchers.IO) {
//            try {
//                val list = api.getChatList().body()
//                if (list != null) {
//                    val result = list.map { title -> ChatData(title) }
//                    dao.addChats(result)
//                    addInbox()
//                    withContext(Dispatchers.Main) {
//                        chatCallBack?.updateChatList(result)
//                    }
//                }
//            } catch (e: IOException) {
//                val result = dao.getChats()
//                withContext(Dispatchers.Main) {
//                    chatCallBack?.updateChatList(result)
//                }
//            }
//        }
//    }
//
//    suspend fun addInbox() {
//        while (true) {
//            val inbox = api.getInboxMessages(topInbox).body()
//            if (inbox == null || inbox.isEmpty()) {
//                break
//            }
//            for (message in inbox) {
//                topInbox = message.id!!
//                val from = message.from
//                val to = message.to
//                Log.i("From: ", from)
//                Log.i("To: ", to ?: "null")
//                if (from != "DINIA" && !chats.containsKey(from)) {
//                    dao.addChats(listOf(ChatData(from)))
//                    chats[from] = ArrayList()
//                }
//
//                if (to != null && to != "DINIA" && !chats.containsKey(to)) {
//                    dao.addChats(listOf(ChatData(to)))
//                    chats[to] = ArrayList()
//                } // fixme copy/paste
//            }
//        }
//    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }
}