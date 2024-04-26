package com.unitech.universe

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class HomePageActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        bottomNavigationView = findViewById(R.id.bottom_navigation)

        bottomNavigationView.setOnItemSelectedListener { menuItem ->
            NavUtils.handleNavigationItemSelected(this, menuItem)
            true
        }



        showUserNameActive()

        // Activa el hamburger - Despliega menu lateral
        val menuButton: ImageButton = findViewById(R.id.menuButton)
        menuButton.setOnClickListener {
            MenuUtils.showPopupMenu(this, it)
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
