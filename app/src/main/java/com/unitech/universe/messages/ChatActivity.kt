package com.unitech.universe.messages

import android.os.Bundle
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.unitech.universe.R
import com.unitech.universe.tool_bars.MenuUtils
import com.unitech.universe.tool_bars.NavUtils

class ChatActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var editTextMessage: EditText
    private lateinit var buttonSend: ImageButton
    private lateinit var messageAdapter: MessageAdapter
    private val messageList = mutableListOf<Message>()
    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var chatUserTextView: TextView

    private val db = FirebaseFirestore.getInstance()
    private val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
    private var otherUserId: String = ""
    private var chatId: String = ""
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        // Inicializar vistas
        recyclerView = findViewById(R.id.recyclerViewMessages)
        editTextMessage = findViewById(R.id.editTextMessage)
        buttonSend = findViewById(R.id.buttonSend)
        bottomNavigationView = findViewById(R.id.bottom_navigation)
        chatUserTextView = findViewById(R.id.textViewChatUser)

        // Obtener el ID del otro usuario desde el Intent
        otherUserId = intent.getStringExtra("otherUserId") ?: ""
        if (otherUserId.isEmpty()) {
            finish() // cerrar si no hay usuario
            return
        }

        // Configurar BottomNavigation
        bottomNavigationView.selectedItemId = R.id.nav_messages
        bottomNavigationView.setOnItemSelectedListener { menuItem ->
            NavUtils.handleNavigationItemSelected(this, menuItem)
            true
        }

        // Configurar hamburger
        val menuButton: ImageButton = findViewById(R.id.menuButton)
        menuButton.setOnClickListener {
            MenuUtils.showPopupMenu(this, it)
        }

        // Mostrar el nombre del usuario con el que se está chateando
        showChatUserName()
        showUserNameActive()


        // Generar chatId único
        chatId = if (currentUserId < otherUserId) {
            "${currentUserId}_${otherUserId}"
        } else {
            "${otherUserId}_${currentUserId}"
        }

        // Configurar RecyclerView
        messageAdapter = MessageAdapter(messageList, currentUserId)
        recyclerView.adapter = messageAdapter
        recyclerView.layoutManager = LinearLayoutManager(this).apply { stackFromEnd = true }

        // Escuchar mensajes en tiempo real
        listenMessages()

        // Enviar mensaje
        buttonSend.setOnClickListener { sendMessage() }
    }

    private fun showChatUserName() {
        // Referencia al documento del otro usuario
        val otherUserRef = db.collection("users").document(otherUserId)
        otherUserRef.get()
            .addOnSuccessListener { document ->
                val username = document?.getString("username")
                if (!username.isNullOrBlank()) {
                    chatUserTextView.text = username
                } else {
                    chatUserTextView.text = "Usuario"
                }
            }
            .addOnFailureListener {
                chatUserTextView.text = "Usuario"
            }
    }

    private fun sendMessage() {
        val text = editTextMessage.text.toString().trim()
        if (text.isEmpty()) return

        val message = Message(senderId = currentUserId, text = text)
        val chatRef = db.collection("chats").document(chatId)

        // Guardar mensaje
        chatRef.collection("messages").add(message)

        // Actualizar info general del chat
        chatRef.set(
            mapOf(
                "participants" to listOf(currentUserId, otherUserId),
                "lastMessage" to text,
                "timestamp" to System.currentTimeMillis()
            )
        )

        editTextMessage.text.clear()
    }

    private fun listenMessages() {
        db.collection("chats").document(chatId)
            .collection("messages")
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null) {
                    messageList.clear()
                    for (doc in snapshot.documents) {
                        val message = doc.toObject(Message::class.java)
                        if (message != null) messageList.add(message)
                    }
                    messageAdapter.notifyDataSetChanged()
                    recyclerView.scrollToPosition(messageList.size - 1)
                }
            }
    }

    private fun showUserNameActive() {
        val usernameTextView: TextView = findViewById(R.id.username)
        val currentUser = auth.currentUser

        if (currentUser != null) {
            val userRef = db.collection("users").document(currentUser.uid)
            userRef.get()
                .addOnSuccessListener { document ->
                    val username = document.getString("username")
                    usernameTextView.text = if (!username.isNullOrBlank()) username else "Usuario"
                }
                .addOnFailureListener {
                    showMessage("Error al obtener nombre de usuario.")
                }
        } else {
            showMessage("Usuario no autenticado.")
        }
    }

    private fun showMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
