package com.dinia.message

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ChatViewHolder(root: View) : RecyclerView.ViewHolder(root) {
    val chat: LinearLayout = root.findViewById(R.id.card)
    private val title: TextView = root.findViewById(R.id.title)

    fun bind(chat: ChatData) {
        title.text = chat.title
    }
}

class ChatAdapter(
    private val chatList: List<ChatData>,
    private val onClick: (ChatData) -> Unit
) : RecyclerView.Adapter<ChatViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val holder = ChatViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.chats_list_item, parent,false
            )
        )

        holder.chat.setOnClickListener {
            onClick(chatList[holder.adapterPosition])
        }

        return holder
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        holder.bind(chatList[holder.adapterPosition])
    }

    override fun getItemCount(): Int {
        return chatList.size
    }
}