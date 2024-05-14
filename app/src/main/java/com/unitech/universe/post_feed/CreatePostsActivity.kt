package com.unitech.universe.post_feed

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.unitech.universe.FirebaseService
import com.unitech.universe.tool_bars.NavUtils
import com.unitech.universe.R
import com.unitech.universe.tool_bars.MenuUtils

class CreatePostsActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var firebaseService: FirebaseService

    private lateinit var usuarioId: String // Asume que tienes el ID del usuario autenticado
    private lateinit var tituloEditText: EditText
    private lateinit var categoriaEditText: EditText
    private lateinit var descripcionEditText: EditText
    private lateinit var ubicacionEditText: EditText
    private lateinit var imagenImageView: ImageView
    private lateinit var crearPublicacionButton: Button
    private lateinit var buttonImage: Button
    private lateinit var priceEditText: EditText
    private lateinit var stockTextView: TextView

    private var imagenUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_posts)

        // Inicializacion de Firebase
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        // Inicializar firebaseService
        firebaseService = FirebaseService()

        // ------------------ Navegadores ----------------------------
        showUserNameActive() // Busqueda de usuario

        //------------------- Barra de navegacion ---------------------
        bottomNavigationView = findViewById(R.id.bottom_navigation)

        // Recibe el ID del ítem seleccionado
        val selectedTabId = intent.getIntExtra("selected_tab_id", R.id.nav_home)

        // Marca el ítem correspondiente del BottomNavigationView como seleccionado
        bottomNavigationView.selectedItemId = selectedTabId

        // Configura el BottomNavigationView como antes
        bottomNavigationView.setOnItemSelectedListener { menuItem ->
            NavUtils.handleNavigationItemSelected(this, menuItem)
            true
        }


        // Activa el hamburger - Despliega menu lateral
        val menuButton: ImageButton = findViewById(R.id.menuButton)
        menuButton.setOnClickListener {
            MenuUtils.showPopupMenu(this, it)
        }
        // ----------------------------------------------------------
        // Inicializar vistas
        tituloEditText = findViewById(R.id.name_product_post)
        categoriaEditText = findViewById(R.id.multispinner_category)
        descripcionEditText = findViewById(R.id.descrip_product_post)
        ubicacionEditText = findViewById(R.id.location_product_post)
        priceEditText = findViewById(R.id.price_product_post)
        imagenImageView = findViewById(R.id.photo_post)
        stockTextView = findViewById(R.id.stock_product_post)
        buttonImage = findViewById(R.id.btn_photo)
        crearPublicacionButton = findViewById(R.id.btn_add)


        // Configurar el botón para seleccionar imagen
        buttonImage.setOnClickListener {
            seleccionarImagen()
        }

        // Configurar el botón para crear una publicación
        crearPublicacionButton.setOnClickListener {
            crearPublicacion()
        }

    }

    private fun seleccionarImagen() {
        // Iniciar una intención para seleccionar una imagen de la galería
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, REQUEST_CODE_SELECCIONAR_IMAGEN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_SELECCIONAR_IMAGEN && resultCode == Activity.RESULT_OK) {
            data?.data?.let {
                imagenUri = it
                imagenImageView.setImageURI(imagenUri)
            }
        }
    }

    private fun crearPublicacion() {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            // Si el usuario no está autenticado, muestra un mensaje de error o redirige a la página de inicio de sesión
            showMessage("El usuario no está autenticado")
            return
        }

        // Obtener la ID del usuario autenticado
        usuarioId = currentUser.uid

        // Obtener los valores de los campos de entrada
        val titulo = tituloEditText.text.toString()
        val categoria = categoriaEditText.text.toString()
        val descripcion = descripcionEditText.text.toString()
        val ubicacion = ubicacionEditText.text.toString()
        val costo = priceEditText.text.toString().toFloat()
        if (costo == null || costo <= 0) {
            showMessage("Costo no válido")
            return
        }
        val stock = stockTextView.text.toString().toInt()
//        val stockCount: Int = stock.toIntOrNull() ?: 0
        if (stock == null || stock <= 0) {
            showMessage("Stock no válido")
            return
        }
        // Verificar si la imagen ha sido seleccionada
        imagenUri?.let { uri ->
            // Subir la imagen y obtener la URL
            firebaseService.subirImagen(uri, { imagenUrl ->
                // Guardar la publicación
                firebaseService.guardarPublicacion(
                    usuarioId,
                    titulo,
                    costo,
                    categoria,
                    descripcion,
                    ubicacion,
                    stock,
                    imagenUrl)
                showMessage("Publicación creada con éxito")
            }, { error ->
                showMessage("Error al subir la imagen: ${error.message}")
            })
        } ?: run {
            showMessage("Por favor, seleccione una imagen")
        }
    }

    companion object {
        const val REQUEST_CODE_SELECCIONAR_IMAGEN = 1
    }


    // -------------- Muestra el nombre del usuario con sesion abierta
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

    // -----------------------------------------------------------------------
    // ----------- Para mostrar mensajes de Error ---------------------
    private fun showMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}