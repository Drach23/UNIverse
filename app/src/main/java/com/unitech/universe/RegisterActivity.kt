package com.unitech.universe
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

class RegisterActivity : AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?){
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val registerSaveButton = findViewById<Button>(R.id.registerSaveButton)
        val firstNameEditText = findViewById<EditText>(R.id.firstNameEditText)

        registerSaveButton.setOnClickListener{
            val firstNameText = firstNameEditText.text.toString()
            Toast.makeText(this,"Gracias $firstNameText por pulsar el boton",Toast.LENGTH_SHORT).show()
            firstNameEditText.setText("Gracias: $firstNameText por haber pulsado el boton")
        }
    }
}