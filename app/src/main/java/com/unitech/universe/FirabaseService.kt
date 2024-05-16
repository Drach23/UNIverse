package com.unitech.universe
import android.content.Intent
import com.google.firebase.storage.*
import android.net.Uri
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.unitech.universe.post_feed.Publicacion
import com.unitech.universe.start_pages.UniverseActivity
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

// Esta clase esta diseñada solamente para los servicios de Posteo de Publicaciones

class FirebaseService : AppCompatActivity() {

    fun subirImagen(
        imagenUri: Uri,
        successListener: (String) -> Unit,
        failureListener: (Exception) -> Unit
    ) {
        val storage = FirebaseStorage.getInstance()
        val storageRef = storage.reference
        val imagenRef = storageRef.child("imagenes_publicaciones/${System.currentTimeMillis()}.jpg")

        imagenRef.putFile(imagenUri)
            .addOnSuccessListener {
                imagenRef.downloadUrl.addOnSuccessListener { uri ->
                    successListener(uri.toString())
                }.addOnFailureListener(failureListener)
            }
            .addOnFailureListener(failureListener)
    }

    fun guardarPublicacion(
        usuarioId: String,
        titulo: String,
        costo: Float,
        categoria: String,
        descripcion: String,
        ubicacion: String,
        stock: Int,
        imagenUrl: String,
    ) {
        // Obtener una instancia de FirebaseFirestore
        val db = FirebaseFirestore.getInstance()

        // Obtener la fecha actual
        val calendar = Calendar.getInstance()

        // Formatear la fecha en el formato dd/mm/yy
        val dateFormat = SimpleDateFormat("dd/MM/yy", Locale.getDefault())
        val fechaPublicacion = dateFormat.format(calendar.time)

        // Inicializar los arrays de likes y dislikes vacíos
        val likes: List<String> = listOf()
        val dislikes: List<String> = listOf()

        // Crear un mapa con los datos de la publicación
        val publicacion = hashMapOf(
            "usuarioId" to usuarioId,
            "titulo" to titulo,
            "costo" to costo,
            "categoria" to categoria,
            "descripcion" to descripcion,
            "ubicacion" to ubicacion,
            "stock" to stock,
            "imagenUrl" to imagenUrl,
            "likes" to likes,
            "dislikes" to dislikes,
            "fechaPublicacion" to fechaPublicacion
        )

        // Add search keywords to the map
        val searchKeywords = mutableListOf<String>()
        searchKeywords.addAll(titulo.split(" "))
        searchKeywords.addAll(categoria.split(" "))
        publicacion["searchKeywords"] = searchKeywords

        // Guardar la publicación en la colección "publicaciones"
        db.collection("publicaciones")
            .add(publicacion)
            .addOnSuccessListener {

                println("Publicación guardada con éxito")
                val intent = Intent(this, UniverseActivity::class.java)
                startActivity(intent)
                finish()
            }
            .addOnFailureListener { error ->
                println("Error al guardar la publicación: ${error.message}")
            }
    }

}