package com.unitech.universe

import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.unitech.universe.post_feed.PostAdapter
import com.unitech.universe.post_feed.Publicacion
import com.unitech.universe.tool_bars.MenuUtils
import com.unitech.universe.tool_bars.NavUtils

class HomePageActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: PostAdapter
    private lateinit var firebaseService: FirebaseService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        firebaseService = FirebaseService()

        showUserNameActive()

        bottomNavigationView = findViewById(R.id.bottom_navigation)
        val selectedTabId = intent.getIntExtra("selected_tab_id", R.id.nav_home)
        bottomNavigationView.selectedItemId = selectedTabId
        bottomNavigationView.setOnItemSelectedListener { menuItem ->
            NavUtils.handleNavigationItemSelected(this, menuItem)
            true
        }

        val menuButton: ImageButton = findViewById(R.id.menuButton)
        menuButton.setOnClickListener {
            MenuUtils.showPopupMenu(this, it)
        }

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // ✅ inicializa adapter vacío UNA sola vez
        adapter = PostAdapter(mutableListOf())
        recyclerView.adapter = adapter

        getPublicacionesFromFirestore()
    }

    private fun getPublicacionesFromFirestore() {
        val db = FirebaseFirestore.getInstance()
        db.collection("publicaciones")
            .addSnapshotListener { snapshots, exception ->
                if (exception != null) {
                    showMessage("Error getting documents: $exception")
                    return@addSnapshotListener
                }

                val publicacionesList = mutableListOf<Publicacion>()
                if (snapshots != null) {
                    for (document in snapshots) {
                        val publicacion = document.toObject(Publicacion::class.java)
                        publicacion.uid = document.id
                        publicacionesList.add(publicacion)
                    }
                }
                // ✅ solo actualizamos la lista, NO recreamos el adapter
                adapter.updateData(publicacionesList)
            }
    }

    private fun showUserNameActive() {
        val usernameTextView: TextView = findViewById(R.id.username)
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val userId = currentUser.uid
            val userRef = db.collection("users").document(userId)
            userRef.get()
                .addOnSuccessListener { document ->
                    if (document?.exists() == true) {
                        val username = document.getString("username")
                        if (!username.isNullOrBlank()) {
                            usernameTextView.text = username
                        } else {
                            showMessage("No se encontró el nombre de usuario.")
                        }
                    } else {
                        showMessage("No se encontraron datos del usuario.")
                    }
                }
                .addOnFailureListener { exception ->
                    showMessage("Error al obtener datos: ${exception.message}")
                }
        } else {
            showMessage("El usuario no está autenticado.")
        }
    }

    private fun showMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
