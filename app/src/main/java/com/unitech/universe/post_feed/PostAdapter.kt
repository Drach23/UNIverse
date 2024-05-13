package com.unitech.universe.post_feed

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import com.unitech.universe.MakeOrderActivity
import com.unitech.universe.R

class PostAdapter(private val publicaciones: List<Publicacion>) : RecyclerView.Adapter<PostAdapter.PostViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_post, parent, false)
        return PostViewHolder(view)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val publicacion = publicaciones[position]
        val auth = FirebaseAuth.getInstance()
        val userId = auth.currentUser!!.uid
        println("AQUI ESTA EL USUARIO ${userId}")

        // Asignar los valores de la publicación a las vistas
        // Configura el usuario TextView.
        val db = FirebaseFirestore.getInstance()
        val usuarioDocRef = db.collection("users").document(publicacion.usuarioId!!)
        usuarioDocRef.get()
            .addOnSuccessListener { documentSnapshot ->
                val nombreUsuario = documentSnapshot.getString("username") ?: "Desconocido"
                holder.userTextView.text = nombreUsuario
            }
            .addOnFailureListener {
                holder.userTextView.text = "Error"
            }
        holder.tituloTextView.text = publicacion.titulo
        holder.categoriaTextView.text = publicacion.categoria
        holder.costoTextView.text = publicacion.costo.toString()
        holder.descripcionTextView.text = publicacion.descripcion
        holder.ubicacionTextView.text = publicacion.ubicacion
        holder.dateTextView.text = publicacion.fechaPublicacion
        holder.likeCountTextView.text = "${publicacion.likes?.size.toString()} me gusta"
        holder.stockTextView.text = "${publicacion.stock.toString()} piezas"
        val liked = publicacion.likes?.contains(userId) ?: false
        val disliked = publicacion.dislikes?.contains(userId) ?: false
        setColorLike(liked, holder.likeImageView)
        setColorDislike(disliked, holder.dilikeImageView)
        Picasso.get()
            .load(publicacion.imagenUrl)
            .into(holder.imagenImageView)

        holder.likeImageView.setOnClickListener {
            // Verifica que userId no sea null y que la publicación no sea null
            if (userId != null) {
                if (liked) {
                    setColorLike(liked, holder.likeImageView)
                    removeLike(publicacion.uid!!, userId, position)
                } else {
                    setColorLike(liked, holder.likeImageView)
                    addLike(publicacion.uid!!, userId, position)
                    if(disliked){
                        removeDislike(publicacion.uid!!, userId, position)
                    }
                }
            } else {
                // Maneja caso de valores nulos
                println("Error: datos de publicación o usuario no encontrados.")
            }
        }

        holder.dilikeImageView.setOnClickListener {
            // Verifica que userId no sea null y que la publicación no sea null
            if (userId != null) {
                if (disliked) {
                    setColorDislike(disliked, holder.likeImageView)
                    removeDislike(publicacion.uid!!, userId, position)
                } else {
                    setColorDislike(disliked, holder.likeImageView)
                    addDislike(publicacion.uid!!, userId, position)
                    if(liked){
                        removeLike(publicacion.uid!!, userId, position)
                    }
                }
            } else {
                // Maneja caso de valores nulos
                println("Error: datos de publicación o usuario no encontrados.")
            }
        }

        holder.pedidoButton.setOnClickListener {
            val intent = Intent(holder.itemView.context, MakeOrderActivity::class.java).apply {
                // Agrega los extras con la información de la publicación
                putExtra("usuarioId", publicacion.usuarioId)
                putExtra("titulo", publicacion.titulo)
                putExtra("descripcion", publicacion.descripcion)
                putExtra("costo", publicacion.costo)
                putExtra("stock", publicacion.stock)
                putExtra("uid", publicacion.uid)
                putExtra("imagenUrl", publicacion.imagenUrl)
            }
            // Inicia la actividad MakeOrderActivity
            holder.itemView.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return publicaciones.size
    }

    inner class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val userTextView: TextView = itemView.findViewById(R.id.item_user_post)
        val tituloTextView: TextView = itemView.findViewById(R.id.item_tittle)
        val categoriaTextView: TextView = itemView.findViewById(R.id.item_category)
        val costoTextView: TextView = itemView.findViewById(R.id.item_price)
        val descripcionTextView: TextView = itemView.findViewById(R.id.item_description)
        val ubicacionTextView: TextView = itemView.findViewById(R.id.item_location)
        val imagenImageView: ImageView = itemView.findViewById(R.id.item_image)
        val dateTextView: TextView = itemView.findViewById(R.id.item_date)
        val likeImageView: ImageView = itemView.findViewById(R.id.item_reaction_positive)
        val dilikeImageView: ImageView = itemView.findViewById(R.id.item_reaction_negative)
        val likeCountTextView: TextView = itemView.findViewById(R.id.item_like_count)
        val stockTextView: TextView = itemView.findViewById(R.id.item_product_count)
        val pedidoButton: Button = itemView.findViewById(R.id.item_btn_pedir)
    }

    fun setColorLike(liked: Boolean, likeButton: ImageView){
        // Establecer la imagen en función del estado de 'liked'
        if (liked) {
            // Si 'liked' es true, muestra la imagen que representa un "like" (por ejemplo, ic_liked)
            likeButton.setImageResource(R.drawable.ic_feliz_active)
        } else {
            // Si 'liked' es false, muestra la imagen original (por ejemplo, ic_like)
            likeButton.setImageResource(R.drawable.ic_feliz)
        }
    }

    fun setColorDislike(liked: Boolean, dislikeButton: ImageView){
        // Establecer la imagen en función del estado de 'liked'
        if (liked) {
            // Si 'liked' es true, muestra la imagen que representa un "like" (por ejemplo, ic_liked)
            dislikeButton.setImageResource(R.drawable.ic_triste_active)
        } else {
            // Si 'liked' es false, muestra la imagen original (por ejemplo, ic_like)
            dislikeButton.setImageResource(R.drawable.ic_triste)
        }
    }

    private fun removeLike(publicacionId: String, userId: String, position: Int) {
        val db = FirebaseFirestore.getInstance()
        val publicacionRef = db.collection("publicaciones").document(publicacionId)

        // Eliminar el userId de la lista de likes de la publicación
        publicacionRef.update("likes", FieldValue.arrayRemove(userId))
            .addOnSuccessListener {
                // Actualización exitosa
                println("Like eliminado.")
                // Notificar al adaptador del cambio en los datos en esa posición
                notifyItemChanged(position)
            }
            .addOnFailureListener { e ->
                // Error al eliminar el like
                println("Error al eliminar like: ${e.message}")
            }
    }

    private fun addLike(publicacionId: String, userId: String, position: Int) {
        val db = FirebaseFirestore.getInstance()
        val publicacionRef = db.collection("publicaciones").document(publicacionId)

        // Agregar el userId a la lista de likes de la publicación
        publicacionRef.update("likes", FieldValue.arrayUnion(userId))
            .addOnSuccessListener {
                // Actualización exitosa
                println( "Like agregado.")
                // Notificar al adaptador del cambio en los datos en esa posición
                notifyItemChanged(position)
            }
            .addOnFailureListener { e ->
                // Error al agregar el like
                println("Error al agregar like: ${e.message}")
            }
    }

    private fun removeDislike(publicacionId: String, userId: String, position: Int) {
        val db = FirebaseFirestore.getInstance()
        val publicacionRef = db.collection("publicaciones").document(publicacionId)

        // Eliminar el userId de la lista de likes de la publicación
        publicacionRef.update("dislikes", FieldValue.arrayRemove(userId))
            .addOnSuccessListener {
                // Actualización exitosa
                println("Dislike eliminado.")
                // Notificar al adaptador del cambio en los datos en esa posición
                notifyItemChanged(position)
            }
            .addOnFailureListener { e ->
                // Error al eliminar el like
                println("Error al eliminar like: ${e.message}")
            }
    }

    private fun addDislike(publicacionId: String, userId: String, position: Int) {
        val db = FirebaseFirestore.getInstance()
        val publicacionRef = db.collection("publicaciones").document(publicacionId)

        // Agregar el userId a la lista de likes de la publicación
        publicacionRef.update("dislikes", FieldValue.arrayUnion(userId))
            .addOnSuccessListener {
                // Actualización exitosa
                println( "Disike agregado.")
                // Notificar al adaptador del cambio en los datos en esa posición
                notifyItemChanged(position)
            }
            .addOnFailureListener { e ->
                // Error al agregar el like
                println("Error al agregar like: ${e.message}")
            }
    }

}
