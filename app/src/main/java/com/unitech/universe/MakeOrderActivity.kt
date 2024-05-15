package com.unitech.universe

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
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
    private lateinit var cantidadEditText: EditText


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

        orderPedir.setOnClickListener{
            setOrder();
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
        cantidadEditText = findViewById(R.id.order_cant_product)

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
        val cantidadText = cantidadEditText.text.toString()
        val cantidad = if (cantidadText.isNotEmpty()) cantidadText.toInt() else 0

        // Calcular el total
        val total = cantidad * costo

        // Actualizar el TextView con el total
        orderTotal.text = "Total: %.2f".format(total)
        // Configura el TextWatcher para el EditText
        cantidadEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                updateTotal(costo)
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // No necesitamos hacer nada aquí
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // No necesitamos hacer nada aquí
            }
        })
    }

    private fun updateTotal(costo: Float) {
        // Obtener la cantidad ingresada
        val cantidadText = cantidadEditText.text.toString()
        val cantidad = if (cantidadText.isNotEmpty()) cantidadText.toInt() else 0

        // Calcular el total
        val total = cantidad * costo

        // Actualizar el TextView con el total
        orderTotal.text = "Total: %.2f".format(total)
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

    private fun setOrder(){
        // Obtener los datos del pedido
        val userId = auth.currentUser?.uid
        val userVendedor = intent.getStringExtra("usuarioId")
        val titulo = orderTitle.text.toString()
        val costo = orderCost.text.toString().substringAfter(":").trim().toFloat()
        val cantidad = orderCantProduct.text.toString().toInt()
        val comprador = orderNameComprador.text.toString() // Obtener el nombre del comprador
        val telComprador = orderTelComprador.text.toString() // Obtener el teléfono del comprador
        val productUid = intent.getStringExtra("uid")
        val state = "Pendiente de revisar"

        // Crear un mapa con los datos del pedido
        val pedido = hashMapOf(
            "compradorId" to userId,
            "vendedorId" to userVendedor,
            "titulo" to titulo,
            "costo" to costo,
            "cantidad" to cantidad,
            "comprador" to comprador,
            "telComprador" to telComprador,
            "state" to state
        )

        // Agregar el pedido a la colección "pedidos" en Firestore
        db.collection("pedidos")
            .add(pedido)
            .addOnSuccessListener { documentReference ->
                showMessage("Pedido enviado con éxito: ${documentReference.id}")

                // Actualizar la cantidad del producto en la base de datos del vendedor
                if (productUid != null) {
                    updateSellerProductQuantity(productUid, cantidad)
                } else {
                    showMessage("Error: el UID del producto es nulo.")
                }
            }
            .addOnFailureListener { e ->
                showMessage("Error al enviar el pedido: $e")
            }
    }

    private fun updateSellerProductQuantity(productId: String, quantityToDeduct: Int) {
        // Obtener la referencia al documento del producto del vendedor
        val productRef = db.collection("publicaciones").document(productId)

        // Obtener la cantidad actual del producto del vendedor
        productRef.get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    val currentQuantity = documentSnapshot.getLong("stock") ?: 0

                    // Restar la cantidad del pedido de la cantidad actual del producto del vendedor
                    val newQuantity = currentQuantity - quantityToDeduct

                    // Verificar si la nueva cantidad es positiva antes de actualizarla
                    if (newQuantity >= 0) {
                        // Actualizar la cantidad del producto en la base de datos del vendedor
                        updateProductQuantity(productId, newQuantity)
                    } else {
                        showMessage("La cantidad del producto no puede ser negativa.")
                    }
                } else {
                    showMessage("El producto del vendedor no existe.")
                }
            }
            .addOnFailureListener { e ->
                showMessage("Error al obtener el producto del vendedor: $e")
            }
    }

    private fun updateProductQuantity(productId: String, newQuantity: Long) {
        // Obtener la referencia al documento del producto en la base de datos
        val productRef = db.collection("publicaciones").document(productId)

        // Actualizar la cantidad del producto con el nuevo valor
        productRef.update("stock", newQuantity)
            .addOnSuccessListener {
                showMessage("Cantidad del producto actualizada exitosamente.")
                redirectToHomePage()
            }
            .addOnFailureListener { e ->
                showMessage("Error al actualizar la cantidad del producto: $e")
            }
    }

    private fun redirectToHomePage() {
        val intent = Intent(this, HomePageActivity::class.java)
        startActivity(intent)
        finish() // Finaliza la actividad actual para que el usuario no pueda volver atrás con el botón de retroceso
    }

    // ----------------- Mostrar errores ------------------------------
    private fun showMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}