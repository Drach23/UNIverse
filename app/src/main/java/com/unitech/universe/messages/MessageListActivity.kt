package com.unitech.universe.messages

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.unitech.universe.R
import com.unitech.universe.MessagesListAdapter
import com.unitech.universe.tool_bars.MenuUtils
import com.unitech.universe.tool_bars.NavUtils

class MessagesListActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var textNoMessages: TextView
    private lateinit var fabNewMessage: FloatingActionButton
    private lateinit var bottomNavigationView: BottomNavigationView

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val currentUserId = auth.currentUser?.uid ?: ""
    private val chatList = mutableListOf<ChatPreview>()
    private lateinit var adapter: MessagesListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_messages_list)

        // Inicialización de vistas
        recyclerView = findViewById(R.id.recyclerViewChats)
        textNoMessages = findViewById(R.id.textNoMessages)
        fabNewMessage = findViewById(R.id.fabNewMessage)
        bottomNavigationView = findViewById(R.id.bottom_navigation)
        val menuButton: ImageButton = findViewById(R.id.menuButton)

        // Configuración RecyclerView
        adapter = MessagesListAdapter(chatList) { otherUserId ->
            val intent = Intent(this, ChatActivity::class.java)
            intent.putExtra("otherUserId", otherUserId)
            startActivity(intent)
        }
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        // FAB para iniciar nuevo chat
        fabNewMessage.setOnClickListener {
            val intent = Intent(this, NewChatActivity::class.java)
            startActivity(intent)
        }

        // Menu lateral
        menuButton.setOnClickListener {
            MenuUtils.showPopupMenu(this, it)
        }

        // Navegación inferior
        bottomNavigationView.selectedItemId = R.id.nav_messages
        bottomNavigationView.setOnItemSelectedListener { menuItem ->
            NavUtils.handleNavigationItemSelected(this, menuItem)
            true
        }

        // Mostrar nombre de usuario activo
        showUserNameActive()

        // Cargar chats
        listenChats()
    }

    private fun listenChats() {
        db.collection("chats")
            .whereArrayContains("participants", currentUserId)
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null && !snapshot.isEmpty) {
                    chatList.clear()
                    for (doc in snapshot.documents) {
                        val chat = doc.toObject(ChatPreview::class.java)?.copy(chatId = doc.id)
                        if (chat != null) chatList.add(chat)
                    }
                    textNoMessages.visibility = View.GONE
                    recyclerView.visibility = View.VISIBLE
                    adapter.notifyDataSetChanged()
                } else {
                    textNoMessages.visibility = View.VISIBLE
                    recyclerView.visibility = View.GONE
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
