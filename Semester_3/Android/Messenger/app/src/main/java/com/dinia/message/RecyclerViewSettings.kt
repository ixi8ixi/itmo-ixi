package com.dinia.message

import android.view.*
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso

class MsgViewHolder(root: View) : RecyclerView.ViewHolder(root) {
    val message: LinearLayout = root.findViewById(R.id.message)
    private val username: TextView = root.findViewById(R.id.username)
    private val messageText: TextView = root.findViewById(R.id.message_text)
    val messageImage: ImageView = root.findViewById(R.id.message_image)
    private val time: TextView = root.findViewById(R.id.time)

    fun bind(message: MsgData) {
        username.text = message.from
        messageText.text = message.messageText
        if (message.isImage) {
            Picasso
                .get()
                .load("${App.BASE_URL}/thumb/${message.imageLink}")
                .into(messageImage)
        } else {
            Picasso.get().cancelRequest(messageImage)
            messageImage.setImageBitmap(null)
        }
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