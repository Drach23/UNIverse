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

        startFeed()

        // ------------------ Navegadores ----------------------------
        showUserNameActive() // Busqueda de usuario

        //------------------- Barra de navegacion ---------------------
        bottomNavigationView = findViewById(R.id.bottom_navigation)

        // Recibe el ID del ítem seleccionado
        val selectedTabId = intent.getIntExtra("selected_tab_id", R.id.nav_home)

        // Marca el ítem correspondiente del BottomNavigationView como seleccionado
        bottomNavigationView.selectedItemId = selectedTabId

        // Configura el BottomNavigationView como antes
        bottomNavigationView.setOnItemSelectedListener { menuItem ->
            NavUtils.handleNavigationItemSelected(this, menuItem)
            true
        }

        // Activa el hamburger - Despliega menu lateral
        val menuButton: ImageButton = findViewById(R.id.menuButton)
        menuButton.setOnClickListener {
            MenuUtils.showPopupMenu(this, it)
        }

        // Cargar publicaciones de Firebase
        leerPublicaciones()



    }

    // Inicializacion de Feed e instancias necesarias
    private fun startFeed(){
        // Inicializa firebaseService aquí
        firebaseService = FirebaseService()

        // Inicializar RecyclerView
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Inicializar PublicacionAdapter
        adapter = PostAdapter(emptyList())
        recyclerView.adapter = adapter
    }

    private fun leerPublicaciones() {
        // Leer las publicaciones de Firebase usando FirebaseService
        firebaseService.leerPublicaciones { publicaciones ->
            // Actualiza las publicaciones en el adapter
            adapter.actualizarPublicaciones(publicaciones)
        }
    }

    private fun showUserNameActive(){
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
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

    // ----------------- Mostrar errores ------------------------------
    private fun showMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

}


