package com.unitech.universe.start_pages

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.unitech.universe.R

class RegisterActivity : AppCompatActivity() {

    private lateinit var registerSaveButton: Button
    private lateinit var firstNameEditText: EditText
    private lateinit var lastNameEditText: EditText
    private lateinit var phoneEditText: EditText
    private lateinit var usernameEditText: EditText
    private lateinit var spinnerGender: Spinner
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var confirmPasswordEditText: EditText

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        spinnerGender = findViewById<Spinner>(R.id.genderSpinner)
        val adapter = ArrayAdapter.createFromResource(this, R.array.optionsGender, android.R.layout.simple_spinner_item)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerGender.adapter = adapter

        // Inicializar las vistas después de inflar el layout
        registerSaveButton = findViewById(R.id.registerSaveButton)
        firstNameEditText = findViewById(R.id.firstNameEditText)
        lastNameEditText = findViewById(R.id.lastNameEditText)
        phoneEditText = findViewById(R.id.phoneEditText)
        usernameEditText = findViewById(R.id.usernameEditText)
        emailEditText = findViewById(R.id.emailEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText)

        passwordEditText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        confirmPasswordEditText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
    }

    fun register(view: View) {
        val email = emailEditText.text.toString()
        val password = passwordEditText.text.toString()
        val confirmPassword = confirmPasswordEditText.text.toString()
        val gender = spinnerGender.selectedItem.toString()

        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
            return
        }

        if (!isValidEmail(email)) {
            Toast.makeText(this, "No es un correo con dominio UDG", Toast.LENGTH_SHORT).show()
            return
        }

        // Verificar que no se haya seleccionado el primer ítem del Spinner
        if (gender == "Selecciona tu género") {
            Toast.makeText(this, "Por favor, selecciona un género válido", Toast.LENGTH_SHORT).show()
            return
        }

        // Crear usuario con email y contraseña en Firebase Authentication
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    if (user != null) {
                        val userId = user.uid
                        // Guardar los datos del usuario en Firestore con el mismo ID de usuario
                        db.collection("users").document(userId).set(
                            hashMapOf(
                                "firstName" to firstNameEditText.text.toString(),
                                "lastName" to lastNameEditText.text.toString(),
                                "gender" to gender,
                                "phone" to phoneEditText.text.toString(),
                                "username" to usernameEditText.text.toString(),
                                "email" to email,
                            )
                        ).addOnSuccessListener {
                            Toast.makeText(this, "Registro exitoso", Toast.LENGTH_SHORT).show()
                            val intent = Intent(this, UniverseActivity::class.java)
                            startActivity(intent)
                            finish()
                        }.addOnFailureListener { e ->
                            Toast.makeText(this, "Error al registrar: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Toast.makeText(this, "Error al registrar: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun cleanScreen() {
        firstNameEditText.setText("")
        lastNameEditText.setText("")
        phoneEditText.setText("")
        emailEditText.setText("")
        usernameEditText.setText("")
        passwordEditText.setText("")
        confirmPasswordEditText.setText("")
    }

    private fun isValidEmail(email: String): Boolean {
        val emailPattern = Regex("[a-zA-Z0-9._%+-]+@alumnos\\.udg\\.mx")
        return emailPattern.matches(email)
    }
}
