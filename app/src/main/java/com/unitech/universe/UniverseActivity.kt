package com.unitech.universe

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class UniverseActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        Thread.sleep(1000)
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_universe)
    }
}