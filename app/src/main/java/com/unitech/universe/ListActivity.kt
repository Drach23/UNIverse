package com.unitech.universe

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class ListActivity : AppCompatActivity() {
    private  lateinit var listView: ListView
    private lateinit var  adapter: ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)
        addToList()

        // Agregar el listener de clics directamente a la ListView
        listView = findViewById(R.id.listedItemsView)
        listView.setOnItemClickListener { _, _, position, _ ->
            val selectedItem = adapter.getItem(position)
            showMessage("Has seleccionado: $selectedItem")
        }
    }

    fun addToList(){
        listView = findViewById(R.id.listedItemsView)

        val data = ArrayList<String>()
        data.add("Manzana")
        data.add("Sandia")
        data.add("Coco")
        data.add("Pera")
        data.add("Jicama")
        data.add("Mango")
        data.add("Limon")

        adapter = ArrayAdapter(this,android.R.layout.simple_list_item_1,data)
        listView.adapter = adapter
    }
    private fun showMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}