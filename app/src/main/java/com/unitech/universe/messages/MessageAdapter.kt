package com.unitech.universe.messages

import android.graphics.Color
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MessageAdapter(
    private val messageList: List<Message>,
    private val currentUserId: String
) : RecyclerView.Adapter<MessageAdapter.MessageViewHolder>() {

    inner class MessageViewHolder(val textViewMessage: TextView) : RecyclerView.ViewHolder(textViewMessage)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val textView = TextView(parent.context).apply {
            setPadding(24, 16, 24, 16)
            textSize = 16f
        }
        return MessageViewHolder(textView)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val message = messageList[position]
        holder.textViewMessage.text = message.text

        // Estilo según quién envía el mensaje
        if (message.senderId == currentUserId) {
            holder.textViewMessage.gravity = Gravity.END
            holder.textViewMessage.setBackgroundColor(Color.parseColor("#DCF8C6")) // verde claro
        } else {
            holder.textViewMessage.gravity = Gravity.START
            holder.textViewMessage.setBackgroundColor(Color.parseColor("#FFFFFF")) // blanco
        }
    }

    override fun getItemCount() = messageList.size
}
