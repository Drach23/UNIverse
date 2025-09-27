package com.unitech.universe.start_pages

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.unitech.universe.HomePageActivity
import com.unitech.universe.R

class UniverseActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        Thread.sleep(1000)
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_universe)

        auth = FirebaseAuth.getInstance()

        emailEditText = findViewById(R.id.loginEmailEditText)
        passwordEditText = findViewById(R.id.loginPasswordEditText)

        passwordEditText = findViewById(R.id.loginPasswordEditText)
        setupPasswordToggle(passwordEditText, R.drawable.ic_eye, R.drawable.ic_eye_off)


        if (auth.currentUser != null) {
            goToHome()
        }
    }

    fun goToRegister(view: View) {
        Toast.makeText(this, "Ingresando a Registro....", Toast.LENGTH_SHORT).show()
        val intent = Intent(this, RegisterActivity::class.java)
        startActivity(intent)
    }

    fun login(view: View) {
        val email = emailEditText.text.toString()
        val password = passwordEditText.text.toString()

        if (email.isEmpty() || password.isEmpty()) {
            showMessage("Por favor ingrese el correo y la contraseña")
            return
        }

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // La autenticación fue exitosa
                    showMessage("Inicio de sesión exitoso")
                    goToHome()
                } else {
                    // La autenticación falló
                    showMessage("Error al iniciar sesión: Usuario y/o contraseña no validos")
                }
            }
    }


    fun goToHome() {
        showMessage("Ingresando a la pantalla principal...")
        val intent = Intent(this, HomePageActivity::class.java)
        startActivity(intent)
    }

    private fun setupPasswordToggle(editText: EditText, drawableVisible: Int, drawableHidden: Int) {
        var isPasswordVisible = false
        editText.setOnTouchListener { v, event ->
            val DRAWABLE_END = 2
            if (event.action == android.view.MotionEvent.ACTION_UP) {
                if (event.rawX >= (editText.right - editText.compoundDrawables[DRAWABLE_END].bounds.width())) {
                    isPasswordVisible = !isPasswordVisible
                    if (isPasswordVisible) {
                        editText.inputType = android.text.InputType.TYPE_CLASS_TEXT or
                                android.text.InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                        editText.setCompoundDrawablesWithIntrinsicBounds(0, 0, drawableHidden, 0)
                    } else {
                        editText.inputType = android.text.InputType.TYPE_CLASS_TEXT or
                                android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD
                        editText.setCompoundDrawablesWithIntrinsicBounds(0, 0, drawableVisible, 0)
                    }
                    editText.setSelection(editText.text.length)
                    true
                } else false
            } else false
        }
    }


    private fun showMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
