package com.dinia.message

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

data class MsgData(val from: String, val to: String, val messageText: String, val time: String)

class MsgViewHolder(root: View) : RecyclerView.ViewHolder(root) {
    val message: LinearLayout = root.findViewById(R.id.message)
    private val username: TextView = root.findViewById(R.id.username)
    private val messageText: TextView = root.findViewById(R.id.message_text)
    private val time: TextView = root.findViewById(R.id.time)

    fun bind(message: MsgData) {
        username.text = message.from
        messageText.text = message.messageText
        time.text = message.time
    }
}

class MsgAdapter(
    private val messages: List<MsgData>,
    private val onClick: (MsgData) -> Unit
) : RecyclerView.Adapter<MsgViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MsgViewHolder {
        val holder = MsgViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.list_item, parent,false
            )
        )

        holder.message.setOnClickListener {
            onClick(messages[holder.adapterPosition])
        }

        return holder
    }

    override fun onBindViewHolder(holder: MsgViewHolder, position: Int) {
        holder.bind(messages[holder.adapterPosition])
    }

    override fun getItemCount(): Int {
        return messages.size
    }
}