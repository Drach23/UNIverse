package com.unitech.universe

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.unitech.universe.messages.ChatPreview

class MessagesListAdapter(
    private val chatList: List<ChatPreview>,
    private val onChatClick: (String) -> Unit
) : RecyclerView.Adapter<MessagesListAdapter.ChatViewHolder>() {

    inner class ChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textUserName: TextView = itemView.findViewById(R.id.textMessageUserName)
        val textLastMessage: TextView = itemView.findViewById(R.id.textLastMessage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.activity_item_chat_preview, parent, false)
        return ChatViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val chat = chatList[position]
        val otherUserId = chat.participants.firstOrNull { it != FirebaseAuth.getInstance().currentUser?.uid }
        holder.textUserName.text = otherUserId ?: "Usuario"
        holder.textLastMessage.text = chat.lastMessage
        holder.itemView.setOnClickListener { onChatClick(otherUserId ?: "") }
    }

    override fun getItemCount() = chatList.size
}
