package com.unitech.universe.post_feed

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.snackbar.Snackbar.make
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import com.unitech.universe.FirebaseService
import com.unitech.universe.HomePageActivity
import com.unitech.universe.R

class PostAdapter(private val activity: Activity, private var dataset: List<Publicacion>) : RecyclerView.Adapter<PostAdapter.PublicacionViewHolder>() {

    // Clase interna que representa el ViewHolder para cada elemento
    class PublicacionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val userTextView: TextView = itemView.findViewById((R.id.item_user_post))
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
    }

    // Infla el diseño de cada elemento y crea un ViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PublicacionViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_post, parent, false)

        return PublicacionViewHolder(view)
    }

    // Asigna datos a cada ViewHolder
    override fun onBindViewHolder(holder: PublicacionViewHolder, position: Int) {
        val publicacion = dataset[position]

        // Establece otros campos de la publicación como ya lo tienes.
        holder.tituloTextView.text = publicacion.titulo
        holder.costoTextView.text = "$ ${publicacion.costo}"
        holder.categoriaTextView.text = publicacion.categoria
        holder.descripcionTextView.text = publicacion.descripcion
        holder.ubicacionTextView.text = publicacion.ubicacion
        holder.stockTextView.text = "${publicacion.stock} pzs"
        holder.dateTextView.text = publicacion.date
        holder.likeCountTextView.text = "${publicacion.likesUsuarios!!.size} likes"

        Picasso.get()
            .load(publicacion.imagenUrl)
            .into(holder.imagenImageView)

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

        val auth = FirebaseAuth.getInstance()
        val userId = auth.uid

        // Verifica si el usuario ha dado like
        val liked = publicacion.likesUsuarios!!.contains(userId)
        setColor(liked, holder.likeImageView)

        // Configura el OnClickListener del botón de like
        holder.likeImageView?.setOnClickListener {
//            val auth = FirebaseAuth.getInstance()
//            val userId = auth.uid
//
//            // Verifica que publicacion y userId no sean null
//            if (userId != null && publicacion != null) {
//                val liked = publicacion.likesUsuarios?.contains(userId) ?: false
//                if (liked) {
//                    // Si el usuario ya dio like, quita el like
//                    FirebaseService().removeLike(publicacion.uid!!, userId)
//                } else {
//                    // Si el usuario no dio like, agrega el like
//                    FirebaseService().addLike(publicacion.uid!!, userId)
//                }
//            } else {
//                // Maneja caso de valores nulos
//                Toast.makeText(activity, "Error: datos de publicación o usuario no encontrados.", Toast.LENGTH_SHORT).show()
//            }
        }
    }


    fun setColor(liked: Boolean, likeButton: ImageView){
        // Establecer la imagen en función del estado de 'liked'
        if (liked) {
            // Si 'liked' es true, muestra la imagen que representa un "like" (por ejemplo, ic_liked)
            likeButton.setImageResource(R.drawable.ic_feliz_active)
        } else {
            // Si 'liked' es false, muestra la imagen original (por ejemplo, ic_like)
            likeButton.setImageResource(R.drawable.ic_feliz)
        }
    }

    // Retorna el número de elementos en el conjunto de datos
    override fun getItemCount(): Int = dataset.size


    // Actualiza los datos de las publicaciones
    fun actualizarPublicaciones(nuevasPublicaciones: List<Publicacion>) {
        dataset = nuevasPublicaciones
        notifyDataSetChanged() // Notifica al RecyclerView que los datos han cambiado
    }
}
