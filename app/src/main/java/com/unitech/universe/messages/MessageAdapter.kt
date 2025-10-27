package com.unitech.universe.messages

import android.graphics.Color
import android.view.Gravity
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MessageAdapter(
    private val messageList: List<Message>,
    private val currentUserId: String
) : RecyclerView.Adapter<MessageAdapter.MessageViewHolder>() {

    inner class MessageViewHolder(
        val textViewMessage: TextView,
        val container: LinearLayout
    ) : RecyclerView.ViewHolder(container)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        // Contenedor para alinear el mensaje a la izquierda o derecha
        val container = LinearLayout(parent.context).apply {
            layoutParams = RecyclerView.LayoutParams(
                RecyclerView.LayoutParams.MATCH_PARENT,
                RecyclerView.LayoutParams.WRAP_CONTENT
            )
            orientation = LinearLayout.HORIZONTAL
            setPadding(8, 4, 8, 4)
        }

        // TextView que mostrará el mensaje
        val textViewMessage = TextView(parent.context).apply {
            setPadding(24, 16, 24, 16)
            textSize = 16f
        }

        container.addView(textViewMessage)

        return MessageViewHolder(textViewMessage, container)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val message = messageList[position]
        holder.textViewMessage.text = message.text

        if (message.senderId == currentUserId) {
            // Mensaje enviado -> alineado a la derecha
            holder.container.gravity = Gravity.END
            holder.textViewMessage.setBackgroundColor(Color.parseColor("#09437e")) // Azul oscuro
            holder.textViewMessage.setTextColor(Color.WHITE)
        } else {
            // Mensaje recibido -> alineado a la izquierda
            holder.container.gravity = Gravity.START
            holder.textViewMessage.setBackgroundColor(Color.parseColor("#7A7A7A")) // Gris
            holder.textViewMessage.setTextColor(Color.BLACK)
        }
    }

    override fun getItemCount(): Int = messageList.size
}
