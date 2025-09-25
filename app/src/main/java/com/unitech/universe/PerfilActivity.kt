package com.unitech.universe

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import android.widget.MediaController
import android.widget.TextView
import android.widget.Toast
import android.widget.VideoView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.auth.User
import com.unitech.universe.post_feed.PostAdapter
import com.unitech.universe.tool_bars.MenuUtils
import com.unitech.universe.tool_bars.NavUtils

class PerfilActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var userEmailTextView: TextView
    private lateinit var userFullNameTextView: TextView
    private lateinit var userPhoneTextView: TextView
    private lateinit var usernameTextView:TextView
    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var currentUser: FirebaseUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_perfil)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        // Inicializa las vistas
        userEmailTextView = findViewById(R.id.emailDEditText)
        userFullNameTextView = findViewById(R.id.fullNameDEditText)
        userPhoneTextView = findViewById(R.id.phoneDEditText)
        usernameTextView = findViewById(R.id.userNameDEditText)

        // Obtén el usuario actual
        currentUser = auth.currentUser!!

        // ------------------ Navegadores ----------------------------
        showUserNameActive() // Busqueda de usuario
        showUserPerfil() // Muestra datos del Usuario

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

    }

    private fun showUserPerfil(){
        // Verifica si el usuario está autenticado
        // El usuario está autenticado, puedes obtener su correo electrónico
        val userEmail = currentUser.email
        userEmailTextView.text = "$userEmail"

        // Obtén los datos adicionales del usuario desde Firestore
        val userId = currentUser.uid
        val userRef = db.collection("users").document(userId)
        userRef.get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    // El documento existe, puedes obtener los datos del usuario
                    val firstName = document.getString("firstName")
                    val lastName = document.getString("lastName")
                    val phone = document.getString("phone")
                    val username = document.getString("username")

                    // Muestra los datos en las vistas correspondientes
                    userFullNameTextView.text = "$firstName $lastName"
                    userPhoneTextView.text = "$phone"
                    usernameTextView.text = "$username"
                } else {
                    showMessage("No se encontraron datos del usuario")
                }
            }
            .addOnFailureListener { exception ->
                showMessage("Error al obtener los datos del usuario: $exception")
            }
    }

    private fun showUserNameActive() {
        // Encuentra la TextView para mostrar el nombre del usuario
        val usernameTextView: TextView = findViewById(R.id.username)

        // Verifica si el usuario está autenticado
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
    }


    // ----------------- Mostrar errores ------------------------------
    private fun showMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}