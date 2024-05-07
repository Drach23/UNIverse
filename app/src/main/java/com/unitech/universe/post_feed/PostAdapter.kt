package com.unitech.universe.post_feed

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import com.unitech.universe.R

class PostAdapter(
    private var publicaciones: List<Publicacion>
) : RecyclerView.Adapter<PostAdapter.PublicacionViewHolder>() {

    // Clase interna que representa el ViewHolder para cada elemento
    class PublicacionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val userTextView: TextView = itemView.findViewById((R.id.item_user_post))
        val tituloTextView: TextView = itemView.findViewById(R.id.item_tittle)
        val categoriaTextView: TextView = itemView.findViewById(R.id.item_category)
        val costoTextView: TextView = itemView.findViewById(R.id.item_price)
        val descripcionTextView: TextView = itemView.findViewById(R.id.item_description)
        val ubicacionTextView: TextView = itemView.findViewById(R.id.item_location)
        val imagenImageView: ImageView = itemView.findViewById(R.id.item_image)
    }

    // Infla el diseño de cada elemento y crea un ViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PublicacionViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_post, parent, false)
        return PublicacionViewHolder(view)
    }

    // Asigna datos a cada ViewHolder
    override fun onBindViewHolder(holder: PublicacionViewHolder, position: Int) {
        val publicacion = publicaciones[position]

        holder.tituloTextView.text = publicacion.titulo
        holder.costoTextView.text = "$ ${publicacion.costo}"
        holder.categoriaTextView.text = publicacion.categoria
        holder.descripcionTextView.text = publicacion.descripcion
        holder.ubicacionTextView.text = publicacion.ubicacion

        // Usamos Picasso para cargar la imagen desde la URL
        Picasso.get()
            .load(publicacion.imagenUrl)
            .into(holder.imagenImageView)

        // Obtener el nombre del usuario que publicó la publicación
        val db = FirebaseFirestore.getInstance()
        val usuarioDocRef = db.collection("users").document(publicacion.usuarioId)
        usuarioDocRef.get()
            .addOnSuccessListener { documentSnapshot ->
                // Si el documento del usuario existe, obtener su nombre
                if (documentSnapshot.exists()) {
                    val nombreUsuario = documentSnapshot.getString("username") ?: "Desconocido"
                    // Asignar el nombre del usuario a la vista
                    holder.userTextView.text = nombreUsuario
                } else {
                    // Si el documento no existe, asignar un valor por defecto
                    holder.userTextView.text = "Desconocido"
                }
            }
            .addOnFailureListener { error ->
                // Manejar error si es necesario, se puede asignar un valor por defecto
                holder.userTextView.text = "Error"
            }
    }

    // Retorna el número de elementos en el conjunto de datos
    override fun getItemCount(): Int = publicaciones.size

    // Actualiza los datos de las publicaciones
    fun actualizarPublicaciones(nuevasPublicaciones: List<Publicacion>) {
        publicaciones = nuevasPublicaciones
        notifyDataSetChanged() // Notifica al RecyclerView que los datos han cambiado
    }
}
