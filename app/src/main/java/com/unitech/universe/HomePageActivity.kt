package com.unitech.universe

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class HomePageActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var userEmailTextView: TextView
    private lateinit var userFullNameTextView: TextView
    private lateinit var userPhoneTextView: TextView
    private lateinit var usernameTextView:TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        // Inicializa las vistas
        userEmailTextView = findViewById(R.id.emailDEditText)
        userFullNameTextView = findViewById(R.id.fullNameDEditText)
        userPhoneTextView = findViewById(R.id.phoneDEditText)
        usernameTextView = findViewById(R.id.userNameDEditText)

        // Obtén el usuario actual
        val currentUser = auth.currentUser

        // Verifica si el usuario está autenticado
        if (currentUser != null) {
            // El usuario está autenticado, puedes obtener su correo electrónico
            val userEmail = currentUser.email
            userEmailTextView.text = "Correo electrónico: $userEmail"

            // Obtén los datos adicionales del usuario desde Firestore
            val userId = currentUser.uid
            val userRef = db.collection("users").document(userId)
            userRef.get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        // El documento existe, puedes obtener los datos del usuario
                        val firstName = document.getString("firstName")
                        val lastName = document.getString("lastName")
                        val phone = document.getString("phoneText")
                        val username = document.getString("username")

                        // Muestra los datos en las vistas correspondientes
                        userFullNameTextView.text = "Nombre completo: $firstName $lastName"
                        userPhoneTextView.text = "Teléfono: $phone"
                        usernameTextView.text = "Username: $username"
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

    private fun showMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
