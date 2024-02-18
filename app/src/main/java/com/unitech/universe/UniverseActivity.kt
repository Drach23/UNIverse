package com.unitech.universe

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
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
}