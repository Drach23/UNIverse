package com.unitech.universe.seller_views

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.unitech.universe.R
import com.unitech.universe.post_feed.PostAdapter
import com.unitech.universe.post_feed.Publicacion
import com.unitech.universe.tool_bars.MenuUtils
import com.unitech.universe.tool_bars.NavUtils

class ViewsOrdersActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: OrderAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_views_orders)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

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

        // Inicializa el RecyclerView
        recyclerView = findViewById(R.id.recyclerView_Orders)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Llama a la función para obtener las publicaciones
        getPedidosFromFirestore()
    }

    private fun getPedidosFromFirestore() {
        val db = FirebaseFirestore.getInstance()
        val auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser

        if (currentUser != null) {
            val userId = currentUser.uid

            db.collection("pedidos")
                .whereEqualTo("vendedorId", userId)
                .addSnapshotListener { snapshots, exception ->
                    if (exception != null) {
                        showMessage("Error getting documents: $exception")
                        return@addSnapshotListener
                    }

                    val pedidosList = mutableListOf<Pedido>()
                    if (snapshots != null) {
                        for (document in snapshots) {
                            val pedido = document.toObject(Pedido::class.java)
                            pedido.uid = document.id
                            pedidosList.add(pedido)
                        }
                    }
                    // Pasar la lista de pedidos al adaptador
                    adapter = OrderAdapter(pedidosList)
                    recyclerView.adapter = adapter
                }
        } else {
            showMessage("User not authenticated")
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