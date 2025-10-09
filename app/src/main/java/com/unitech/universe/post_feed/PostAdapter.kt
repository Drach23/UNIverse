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
import com.unitech.universe.R
import com.unitech.universe.user_views.MakeOrderActivity

class PostAdapter(private val publicaciones: MutableList<Publicacion>) : RecyclerView.Adapter<PostAdapter.PostViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_post, parent, false)
        return PostViewHolder(view)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val publicacion = publicaciones[position]
        val auth = FirebaseAuth.getInstance()
        val userId = auth.currentUser!!.uid

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
        holder.costoTextView.text = "${publicacion.costo} $"
        holder.descripcionTextView.text = publicacion.descripcion
        holder.ubicacionTextView.text = publicacion.ubicacion
        holder.dateTextView.text = publicacion.fechaPublicacion
        holder.likeCountTextView.text = "${publicacion.likes?.size ?: 0} me gusta"
        holder.stockTextView.text = "${publicacion.stock} piezas"

        val liked = publicacion.likes?.contains(userId) ?: false
        val disliked = publicacion.dislikes?.contains(userId) ?: false
        setColorLike(liked, holder.likeImageView)
        setColorDislike(disliked, holder.dilikeImageView)

        Picasso.get().load(publicacion.imagenUrl).into(holder.imagenImageView)

        holder.likeImageView.setOnClickListener {
            if (liked) {
                removeLike(publicacion.uid!!, userId, position)
            } else {
                addLike(publicacion.uid!!, userId, position)
                if (disliked) removeDislike(publicacion.uid!!, userId, position)
            }
        }

        holder.dilikeImageView.setOnClickListener {
            if (disliked) {
                removeDislike(publicacion.uid!!, userId, position)
            } else {
                addDislike(publicacion.uid!!, userId, position)
                if (liked) removeLike(publicacion.uid!!, userId, position)
            }
        }

        holder.pedidoButton.setOnClickListener {
            val intent = Intent(holder.itemView.context, MakeOrderActivity::class.java).apply {
                putExtra("usuarioId", publicacion.usuarioId)
                putExtra("titulo", publicacion.titulo)
                putExtra("descripcion", publicacion.descripcion)
                putExtra("costo", publicacion.costo)
                putExtra("stock", publicacion.stock)
                putExtra("uid", publicacion.uid)
                putExtra("imagenUrl", publicacion.imagenUrl)
            }
            holder.itemView.context.startActivity(intent)

            // Crear notificación de pedido
            publicacion.usuarioId?.let { ownerId ->
                createNotification(ownerId, userId, "order", "Un usuario quiere pedir tu producto '${publicacion.titulo}'")
            }
        }
    }

    override fun getItemCount(): Int = publicaciones.size

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

    fun setColorLike(liked: Boolean, likeButton: ImageView) {
        likeButton.setImageResource(if (liked) R.drawable.ic_feliz_active else R.drawable.ic_feliz)
    }

    fun setColorDislike(disliked: Boolean, dislikeButton: ImageView) {
        dislikeButton.setImageResource(if (disliked) R.drawable.ic_triste_active else R.drawable.ic_triste)
    }

    private fun removeLike(publicacionId: String, userId: String, position: Int) {
        val db = FirebaseFirestore.getInstance()
        val publicacionRef = db.collection("publicaciones").document(publicacionId)
        publicacionRef.update("likes", FieldValue.arrayRemove(userId))
            .addOnSuccessListener {
                notifyItemChanged(position)
            }
    }

    private fun addLike(publicacionId: String, userId: String, position: Int) {
        val db = FirebaseFirestore.getInstance()
        val publicacionRef = db.collection("publicaciones").document(publicacionId)
        publicacionRef.update("likes", FieldValue.arrayUnion(userId))
            .addOnSuccessListener {
                notifyItemChanged(position)

                publicaciones[position].usuarioId?.let { ownerId ->
                    createNotification(ownerId, userId, "like", "A alguien le gustó tu publicación '${publicaciones[position].titulo}'")
                }
            }
    }

    private fun removeDislike(publicacionId: String, userId: String, position: Int) {
        val db = FirebaseFirestore.getInstance()
        val publicacionRef = db.collection("publicaciones").document(publicacionId)
        publicacionRef.update("dislikes", FieldValue.arrayRemove(userId))
            .addOnSuccessListener {
                notifyItemChanged(position)
            }
    }

    private fun addDislike(publicacionId: String, userId: String, position: Int) {
        val db = FirebaseFirestore.getInstance()
        val publicacionRef = db.collection("publicaciones").document(publicacionId)
        publicacionRef.update("dislikes", FieldValue.arrayUnion(userId))
            .addOnSuccessListener {
                notifyItemChanged(position)

                publicaciones[position].usuarioId?.let { ownerId ->
                    createNotification(ownerId, userId, "dislike", "A alguien no le gustó tu publicación '${publicaciones[position].titulo}'")
                }
            }
    }

    private fun createNotification(ownerId: String, fromUserId: String, type: String, message: String) {
        val db = FirebaseFirestore.getInstance()

        val userRef = db.collection("users").document(fromUserId)
        userRef.get().addOnSuccessListener { document ->
            val fromUsername = document.getString("username") ?: "Alguien"

            val notification = hashMapOf(
                "fromUserId" to fromUserId,
                "fromUsername" to fromUsername,
                "message" to message,
                "seen" to false,
                "timestamp" to System.currentTimeMillis(),
                "type" to type
            )

            db.collection("notifications")
                .document(ownerId)
                .collection("userNotifications")
                .add(notification)
        }
    }

    // Actualizar lista sin perder scroll
    fun updateData(newList: List<Publicacion>) {
        publicaciones.clear()
        publicaciones.addAll(newList)
        notifyDataSetChanged()
    }
}
