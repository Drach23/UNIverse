package com.unitech.universe
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
class RegisterActivity : AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?){
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
    }
}