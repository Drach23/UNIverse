package com.unitech.universe

import PostAdapter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

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
        // Inicializa firebaseService aquí
        firebaseService = FirebaseService()

        // Inicializar RecyclerView
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Inicializar PublicacionAdapter
        adapter = PostAdapter(emptyList())
        recyclerView.adapter = adapter

        showUserNameActive()
        bottomNavigationView = findViewById(R.id.bottom_navigation)

        bottomNavigationView.setOnItemSelectedListener { menuItem ->
            NavUtils.handleNavigationItemSelected(this, menuItem)
            return@setOnItemSelectedListener true
        }

        // Activa el hamburger - Despliega menu lateral
        val menuButton: ImageButton = findViewById(R.id.menuButton)
        menuButton.setOnClickListener {
            MenuUtils.showPopupMenu(this, it)
        }

        // Cargar publicaciones de Firebase
        leerPublicaciones()



    }

    private fun leerPublicaciones() {
        // Leer las publicaciones de Firebase usando FirebaseService
        firebaseService.leerPublicaciones { publicaciones ->
            // Actualiza las publicaciones en el adapter
            adapter.actualizarPublicaciones(publicaciones)
        }
    }

    private fun showMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }


    // Muestra el nombre del usuario con sesion abierta
    private fun showUserNameActive(){
        val usernameTextView: TextView = findViewById(R.id.username)
        // Obtén el usuario actual
        val currentUser = auth.currentUser
        // Verifica si el usuario está autenticado
        if (currentUser != null) {

            // Obtén los datos adicionales del usuario desde Firestore
            val userId = currentUser.uid
            val userRef = db.collection("users").document(userId)
            userRef.get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        val username = document.getString("username")

                        // Muestra los datos en las vistas correspondientes
                        usernameTextView.text = "$username"
                    } else {
                        showMessage("No se encontraron datos del usuario")
                    }
                }
                .addOnFailureListener { exception ->
                    showMessage("Error al obtener los datos del usuario: $exception")
                }
        } else {
            // El usuario no está autenticado, puedes redirigirlo al inicio de sesión o realizar alguna otra acción
            showMessage("El usuario no está autenticado")
        }
    }

}


