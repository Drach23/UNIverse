package com.unitech.universe
import android.content.Intent
import com.google.firebase.storage.*
import android.net.Uri
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.unitech.universe.post_feed.Publicacion
import com.unitech.universe.start_pages.UniverseActivity

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
        costo: String,
        categoria: String,
        descripcion: String,
        ubicacion: String,
        imagenUrl: String
    ) {
        // Obtén una instancia de FirebaseFirestore
        val db = FirebaseFirestore.getInstance()

        // Crear un mapa con los datos de la publicación
        val publicacion = hashMapOf(
            "usuarioId" to usuarioId,
            "titulo" to titulo,
            "costo" to costo,
            "categoria" to categoria,
            "descripcion" to descripcion,
            "ubicacion" to ubicacion,
            "imagenUrl" to imagenUrl
        )

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

    fun leerPublicaciones(dataChangeListener: (List<Publicacion>) -> Unit) {
        // Obtén una instancia de Firebase Firestore
        val db = FirebaseFirestore.getInstance()

        // Consulta la colección "publicaciones" en Firestore
        db.collection("publicaciones")
            .get()
            .addOnSuccessListener { querySnapshot: QuerySnapshot ->
                // Lista para almacenar las publicaciones recibidas
                val publicaciones = mutableListOf<Publicacion>()

                // Recorre cada documento en la consulta
                for (document in querySnapshot.documents) {
                    // Convierte cada documento a una instancia de Publicacion
                    val publicacion = document.toObject(Publicacion::class.java)

                    // Si la conversión fue exitosa, agrega la publicación a la lista
                    publicacion?.let {
                        publicaciones.add(it)
                    }
                }

                // Llama al dataChangeListener con la lista de publicaciones
                dataChangeListener(publicaciones)
            }
            .addOnFailureListener { error ->
                // Manejar error
                println("Ocurrio un erro: ${error.message}")
            }
    }

}
