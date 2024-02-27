package com.unitech.universe

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

class UniverseActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        Thread.sleep(1000)
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_universe)

    }
    public fun goToRegister(view: View){
        Toast.makeText(this,"ingresando a Registro....",Toast.LENGTH_SHORT).show()
        val intent = Intent(this,RegisterActivity::class.java)
        startActivity(intent)
    }

    public fun login(view: View){
        val emailEditText = findViewById<EditText>(R.id.loginEmailEditText)
        val passwordEdiText = findViewById<EditText>(R.id.loginPasswordEditText)
        val loginButton = findViewById<Button>(R.id.loginIngresarButton)

        loginButton.setOnClickListener{
            val email = emailEditText.text.toString()
            val password = passwordEdiText.text.toString()

            if(email.equals("universe@gmail.com") && password.equals("password123")){
                Toast.makeText(this,"Te has logueado con exito",Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(this,"Email o contraseña invalidos",Toast.LENGTH_SHORT).show()
            }
        }
    }
}