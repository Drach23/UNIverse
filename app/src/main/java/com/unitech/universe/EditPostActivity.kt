package com.unitech.universe

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import com.unitech.universe.seller_views.StockUserActivity
import com.unitech.universe.tool_bars.MenuUtils
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class EditPostActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var storage: FirebaseStorage
    private lateinit var stockEditImage: ImageView
    private lateinit var stockEditTitle: EditText
    private lateinit var stockEditDescProduct: EditText
    private lateinit var stockEditCost: EditText
    private lateinit var stockEditLocate: EditText
    private lateinit var stockEditCantProduct: EditText
    private lateinit var stockEditCategory: EditText

    private lateinit var stockChangeImage: Button
    private lateinit var stockCancel: Button
    private lateinit var stockActualizar: Button

    private var imageUri: Uri? = null
    private val PICK_IMAGE_REQUEST = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_post)
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        storage = FirebaseStorage.getInstance()

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
            Picasso.get().load(imageUrl).into(stockEditImage)
        }
    }

    private fun inicializaciones() {
        // Inicializar los elementos utilizando findViewById
        stockEditImage = findViewById(R.id.item_edit_image_perfil)
        stockEditTitle = findViewById(R.id.item_edit_tittle)
        stockEditDescProduct = findViewById(R.id.item_edit_description)
        stockEditCost = findViewById(R.id.item_edit_price)
        stockEditLocate = findViewById(R.id.item_edit_location)
        stockEditCantProduct = findViewById(R.id.item_edit_product_count)
        stockEditCategory = findViewById(R.id.item_edit_category)

        stockChangeImage = findViewById(R.id.btn_edit_photo)
        stockCancel = findViewById(R.id.item_edit_btn_delete)
        stockActualizar = findViewById(R.id.item_edit_btn_save)

        // Llamar a showPublicacionDetails() después de inicializar las vistas
        showPublicacionDetails()
        goBack()
        actualizarPublicacion()

        // Agregar listener al botón para cambiar la imagen
        stockChangeImage.setOnClickListener {
            openFileChooser()
        }
    }

    private fun showPublicacionDetails() {
        // Recupera los extras con la información de la publicación
        val titulo = intent.getStringExtra("titulo")
        val descripcion = intent.getStringExtra("descripcion")
        val costo = intent.getFloatExtra("costo", 0.0f)
        val cant = intent.getIntExtra("cant", 0)
        val categoria = intent.getStringExtra("categoria")
        val publicacionId = intent.getStringExtra("uid")
        val ubicacion = intent.getStringExtra("ubicacion")

        // Muestra la información en las vistas correspondientes
        stockEditTitle.setText(titulo)
        stockEditDescProduct.setText(descripcion)
        stockEditCost.setText(costo.toString())
        stockEditCantProduct.setText(cant.toString())
        stockEditLocate.setText(ubicacion)
        stockEditCategory.setText(categoria)
    }

    private fun showUserNameActive() {
        // Encuentra la TextView para mostrar el nombre del usuario
        val usernameTextView: TextView = findViewById(R.id.username)

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
                        val username = document.getString("username")

                        // Verifica si el nombre de usuario es nulo o vacío
                        if (!username.isNullOrBlank()) {
                            // Muestra el nombre de usuario en la TextView
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

    private fun goBack() {
        stockCancel.setOnClickListener {
            finish()
        }
    }

    private fun actualizarPublicacion() {
        stockActualizar.setOnClickListener {
            val titulo = stockEditTitle.text.toString()
            val descripcion = stockEditDescProduct.text.toString()
            val costo = stockEditCost.text.toString().toFloatOrNull() ?: 0.0f
            val ubicacion = stockEditLocate.text.toString()
            val cant = stockEditCantProduct.text.toString().toInt()
            val categoria = stockEditCategory.text.toString()
            val publicacionId = intent.getStringExtra("uid")

            if (publicacionId != null) {
                val publicacionRef = db.collection("publicaciones").document(publicacionId)
                // Obtener la fecha actual
                val calendar = Calendar.getInstance()

                // Formatear la fecha en el formato dd/mm/yy
                val dateFormat = SimpleDateFormat("dd/MM/yy", Locale.getDefault())
                val fechaPublicacion = dateFormat.format(calendar.time)

                val publicacionData = (hashMapOf(
                    "titulo" to titulo,
                    "descripcion" to descripcion,
                    "costo" to costo,
                    "ubicacion" to ubicacion,
                    "stock" to cant,
                    "categoria" to categoria,
                    "fechaPublicacion" to fechaPublicacion
                ) as Map<String, Any>).toMutableMap()

                // Agregar palabras clave de búsqueda al mapa
                val searchKeywords = mutableListOf<String>()
                searchKeywords.addAll(titulo.split(" "))
                searchKeywords.addAll(categoria.split(" "))
                publicacionData["searchKeywords"] = searchKeywords

                // Actualizar la publicación en Firestore
                publicacionRef.update(publicacionData)
                    .addOnSuccessListener {
                        if (imageUri != null) {
                            uploadImage(publicacionId)
                        } else {
                            showMessage("Publicación actualizada correctamente.")
                            redirectToStockPage()
                        }
                    }
                    .addOnFailureListener { e ->
                        showMessage("Error al actualizar la publicación: ${e.message}")
                    }
            } else {
                showMessage("Error: No se encontró el ID de la publicación.")
            }
        }
    }

    private fun openFileChooser() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            imageUri = data.data
            stockEditImage.setImageURI(imageUri)
        }
    }

    private fun uploadImage(publicacionId: String) {
        if (imageUri != null) {
            val storageRef = storage.reference.child("images/${publicacionId}.jpg")
            storageRef.putFile(imageUri!!)
                .addOnSuccessListener {
                    storageRef.downloadUrl.addOnSuccessListener { uri ->
                        val imageUrl = uri.toString()
                        db.collection("publicaciones").document(publicacionId)
                            .update("imagenUrl", imageUrl)
                            .addOnSuccessListener {
                                showMessage("Publicación e imagen actualizadas correctamente.")
                                redirectToStockPage()
                            }
                            .addOnFailureListener { e ->
                                showMessage("Error al actualizar la imagen: ${e.message}")
                            }
                    }
                }
                .addOnFailureListener { e ->
                    showMessage("Error al subir la imagen: ${e.message}")
                }
        }
    }

    private fun redirectToStockPage() {
        val intent = Intent(this, StockUserActivity::class.java)
        startActivity(intent)
        finish() // Finaliza la actividad actual para que el usuario no pueda volver atrás con el botón de retroceso
    }

    // ----------------- Mostrar errores ------------------------------
    private fun showMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
