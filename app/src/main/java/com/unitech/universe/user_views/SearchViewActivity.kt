package com.unitech.universe.user_views

import android.os.Bundle
import android.text.TextUtils
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.unitech.universe.FirebaseService
import com.unitech.universe.R
import com.unitech.universe.post_feed.PostAdapter
import com.unitech.universe.post_feed.Publicacion
import com.unitech.universe.seller_views.Pedido
import com.unitech.universe.tool_bars.MenuUtils
import com.unitech.universe.tool_bars.NavUtils

class SearchViewActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var searchView: SearchView
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: PostAdapter
    private val publicaciones = mutableListOf<Publicacion>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_view)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        searchView = findViewById(R.id.search_view)
        recyclerView = findViewById(R.id.recycler_view_search)

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

        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = PostAdapter(publicaciones)
        recyclerView.adapter = adapter

        setupSearchView()
    }

    private fun setupSearchView() {
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (!TextUtils.isEmpty(query)) {
                    searchPublicaciones(query!!)
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (!TextUtils.isEmpty(newText)) {
                    searchPublicaciones(newText!!)
                }
                return false
            }
        })
    }

    private fun searchPublicaciones(query: String) {
        val db = FirebaseFirestore.getInstance()
        val publicacionesRef = db.collection("publicaciones")

        val queryWords = query.split(" ")

        var firestoreQuery: Query = publicacionesRef

        for (word in queryWords) {
            firestoreQuery = firestoreQuery.whereArrayContains("searchKeywords", word)
        }

        firestoreQuery.get()
            .addOnSuccessListener { documents ->
                publicaciones.clear()
                for (document in documents) {
                    val publicacion = document.toObject(Publicacion::class.java)
                    publicaciones.add(publicacion)
                }
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                // Manejo de errores
                exception.printStackTrace()
            }
    }

    private fun showUserNameActive() {
        // Encuentra la TextView para mostrar el nombre del usuario
        val usernameTextView: TextView = findViewById(R.id.username)

        // Obtén el usuario actual
        val currentUser = auth.currentUser

        // Verifica si el usuario está autenticado
        if (currentUser != null) {
            // Obtén el ID de usuario actual
            val userId = currentUser.uid

            // Referencia al documento del usuario en Firestore
            val userRef = db.collection("users").document(userId)

            // Realiza una solicitud para obtener los datos del documento del usuario
            userRef.get()
                .addOnSuccessListener { document ->
                    // Verifica si el documento existe y contiene datos
                    if (document?.exists() == true) {
                        // Obtén el nombre de usuario
                        val username = document.getString("username")

                        // Verifica si el nombre de usuario es nulo o vacío
                        if (!username.isNullOrBlank()) {
                            // Muestra el nombre de usuario en la TextView
                            usernameTextView.text = username
                        } else {
                            showMessage("No se encontró el nombre de usuario.")
                        }
                    } else {
                        showMessage("No se encontraron datos del usuario.")
                    }
                }
                .addOnFailureListener { exception ->
                    // Maneja el error de manera específica
                    showMessage("Error al obtener los datos del usuario: ${exception.message}")
                }
        } else {
            // Si el usuario no está autenticado
            showMessage("El usuario no está autenticado.")
        }
    }

    // ----------------- Mostrar errores ------------------------------
    private fun showMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
