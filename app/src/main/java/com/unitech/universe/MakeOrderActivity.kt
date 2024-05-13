package com.unitech.universe

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import com.unitech.universe.tool_bars.MenuUtils

class MakeOrderActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var orderImage: ImageView
    private lateinit var orderTitle: TextView
    private lateinit var orderDescProduct: TextView
    private lateinit var orderCost: TextView
    private lateinit var orderCantProduct: EditText
    private lateinit var orderTotal: TextView
    private lateinit var orderNameVendedor: TextView
    private lateinit var orderTelVendedor: TextView
    private lateinit var orderNameComprador: TextView
    private lateinit var orderTelComprador: TextView
    private lateinit var orderCancel: Button
    private lateinit var orderPedir: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_make_order)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        // ------------------ Navegadores ----------------------------
        inicializaciones()
        showUserNameActive() // Busqueda de usuario )
        // Activa el hamburger - Despliega menu lateral
        val menuButton: ImageButton = findViewById(R.id.menuButton)
        menuButton.setOnClickListener {
            MenuUtils.showPopupMenu(this, it)
        }

        // Recibe la URL de la imagen del intento y carga la imagen
        val imageUrl = intent.getStringExtra("imagenUrl")
        if (imageUrl != null) {
            Picasso.get().load(imageUrl).into(orderImage)
        }

    }

    private fun inicializaciones(){
        // Inicializar los elementos utilizando findViewById
        orderImage = findViewById(R.id.order_image)
        orderTitle = findViewById(R.id.order_tittle)
        orderDescProduct = findViewById(R.id.order_desc_product)
        orderCost = findViewById(R.id.order_cost)
        orderCantProduct = findViewById(R.id.order_cant_product)
        orderTotal = findViewById(R.id.order_total)
        orderNameVendedor = findViewById(R.id.order_name_vendedor)
        orderTelVendedor = findViewById(R.id.order_tel_vendedor)
        orderNameComprador = findViewById(R.id.order_name_comprador)
        orderTelComprador = findViewById(R.id.order_tel_comprador)
        orderCancel = findViewById(R.id.order_cancel)
        orderPedir = findViewById(R.id.order_pedir)

        // Llamar a showPublicacionDetails() después de inicializar las vistas
        showPublicacionDetails()
        goBack()
    }

    private fun showPublicacionDetails() {
        // Recupera los extras con la información de la publicación
        val titulo = intent.getStringExtra("titulo")
        val descripcion = intent.getStringExtra("descripcion")
        val costo = intent.getFloatExtra("costo", 0.0f)
        val userVendedor = intent.getStringExtra("usuarioId")
        val vendedor = db.collection("users").document(userVendedor!!)
        vendedor.get()
            .addOnSuccessListener { documentSnapshot ->
                val nombreVendedor = documentSnapshot.getString("firstName") ?: "Desconocido"
                val apellidoVendedor = documentSnapshot.getString("lastName") ?: "Desconocido"
                val tel = documentSnapshot.getString("phone") ?: "Desconocido"
                orderNameVendedor.text = nombreVendedor +" " + apellidoVendedor
                orderTelVendedor.text = tel
            }
            .addOnFailureListener {
                orderNameVendedor.text = "Error"
            }

        // Muestra la información en las vistas correspondientes
        orderTitle.text = titulo
        orderDescProduct.text = descripcion
        orderCost.text = "Precio Unitario: $costo" // Formatea como desees
        // Muestra otros datos según sea necesario
    }

    private fun showUserNameActive() {
        //titulo del actionbar
        val usernameTextView: TextView = findViewById(R.id.username)
        // Obtén la referencia a la TextView donde se mostrará el nombre del comprador
        val orderNameComprador: TextView = findViewById(R.id.order_name_comprador)

        // Obtén el usuario actual
        val currentUser = auth.currentUser

        // Verifica si el usuario está autenticado
        if (currentUser != null) {
            // Obtén el ID de usuario actual
            val userId = currentUser.uid

            // Referencia al documento del usuario en Firestore
            val userRef = db.collection("users").document(userId)

            // Realiza una solicitud para obtener los datos del documento del usuario
            userRef.get()
                .addOnSuccessListener { document ->
                    // Verifica si el documento existe y contiene datos
                    if (document?.exists() == true) {
                        // Obtén el nombre de usuario
                        val name = document.getString("firstName")
                        val lastname = document.getString("lastName")
                        val phone = document.getString("phone")
                        val username = document.getString("username")

                        // Verifica si el nombre de usuario es nulo o vacío
                        if (!name.isNullOrBlank()) {
                            // Muestra el nombre de usuario en la TextView correspondiente
                            orderNameComprador.text = name +" "+ lastname
                            orderTelComprador.text = phone
                            usernameTextView.text = username
                        } else {
                            showMessage("No se encontró el nombre de usuario.")
                        }
                    } else {
                        showMessage("No se encontraron datos del usuario.")
                    }
                }
                .addOnFailureListener { exception ->
                    // Maneja el error de manera específica
                    showMessage("Error al obtener los datos del usuario: ${exception.message}")
                }
        } else {
            // Si el usuario no está autenticado
            showMessage("El usuario no está autenticado.")
        }
    }

    private fun goBack(){
        orderCancel.setOnClickListener{
            finish();
        }
    }
    // ----------------- Mostrar errores ------------------------------
    private fun showMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}