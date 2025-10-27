package com.unitech.universe.messages

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.unitech.universe.R

class NewChatActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val usersList = mutableListOf<UserMessage>() // Modelo para mostrar usuarios
    private lateinit var adapter: UsersAdapter // RecyclerView Adapter para mostrar usuarios

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_chat)

        recyclerView = findViewById(R.id.recyclerViewUsers)
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = UsersAdapter(usersList) { userId ->
            // Al hacer click en un usuario, abrir ChatActivity
            val intent = Intent(this, ChatActivity::class.java)
            intent.putExtra("otherUserId", userId)
            startActivity(intent)
            finish()
        }

        recyclerView.adapter = adapter

        loadAllUsers()
    }

    private fun loadAllUsers() {
        val currentUserId = auth.currentUser?.uid ?: return

        db.collection("users")
            .get()
            .addOnSuccessListener { snapshot ->
                usersList.clear()
                for (doc in snapshot.documents) {
                    val userId = doc.id
                    val username = doc.getString("username") ?: "Usuario"
                    if (userId != currentUserId) { // no mostrar al usuario actual
                        usersList.add(UserMessage(userId, username))
                    }
                }
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error al cargar usuarios", Toast.LENGTH_SHORT).show()
            }
    }
}
