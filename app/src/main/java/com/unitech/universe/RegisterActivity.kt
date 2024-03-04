package com.unitech.universe

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore

class RegisterActivity : AppCompatActivity() {

    private lateinit var registerSaveButton: Button
    private lateinit var firstNameEditText: EditText
    private lateinit var lastNameEditText: EditText
    private lateinit var phoneEditText: EditText
    private lateinit var usernameEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var confirmPasswordEditText: EditText

    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // Inicializar las vistas después de inflar el layout
        registerSaveButton = findViewById<Button>(R.id.registerSaveButton)
        firstNameEditText = findViewById<EditText>(R.id.firstNameEditText)
        lastNameEditText = findViewById<EditText>(R.id.lastNameEditText)
        phoneEditText = findViewById<EditText>(R.id.phoneEditText)
        usernameEditText = findViewById<EditText>(R.id.usernameEditText)
        emailEditText = findViewById<EditText>(R.id.emailEditText)
        passwordEditText = findViewById<EditText>(R.id.passwordEditText)
        confirmPasswordEditText = findViewById<EditText>(R.id.confirmPasswordEditText)

        passwordEditText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        confirmPasswordEditText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD

    }

    public fun register(view: View) {
        registerSaveButton.setOnClickListener {
            if (validateInputs(
                    firstNameEditText,
                    lastNameEditText,
                    phoneEditText,
                    usernameEditText,
                    emailEditText,
                    passwordEditText,
                    confirmPasswordEditText
                )) {
                val email = emailEditText.text.toString()
                val passwordText = passwordEditText.text.toString()
                val confirmPassword = confirmPasswordEditText.text.toString()

                if (!passwordText.equals(confirmPassword)) {
                    Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
                }else if(!isValidEmail(email)){
                    Toast.makeText(this,"No es un correo con domino UDG",Toast.LENGTH_SHORT).show()
                }else {
                    db.collection("users").document(email).set(
                        hashMapOf(
                            "firstName" to firstNameEditText.text.toString(),
                            "lastName" to lastNameEditText.text.toString(),
                            "phoneText" to phoneEditText.text.toString(),
                            "username" to usernameEditText.text.toString(),
                            "password" to passwordEditText.text.toString()
                        )
                    )
                    Toast.makeText(this, "Registro exitoso", Toast.LENGTH_SHORT).show()
                    cleanScreen()
                }
            } else {
                Toast.makeText(this, "Faltan campos por llenar", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun validateInputs(vararg editTexts: EditText): Boolean {
        for (editText in editTexts) {
            if (editText.text.isNullOrEmpty()) {
                return false
            }
        }
        return true
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
