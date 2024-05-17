package com.unitech.universe.seller_views

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import com.unitech.universe.EditPostActivity
import com.unitech.universe.R
import com.unitech.universe.post_feed.Publicacion

class StockAdapter(private val publicaciones: List<Publicacion>) : RecyclerView.Adapter<StockAdapter.PostViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.activity_inventory, parent, false)
        return PostViewHolder(view)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val publicacion = publicaciones[position]

        holder.tituloTextView.text = publicacion.titulo
        holder.categoriaTextView.text = publicacion.categoria
        holder.costoTextView.text = publicacion.costo.toString()
        holder.descripcionTextView.text = publicacion.descripcion
        holder.ubicacionTextView.text = publicacion.ubicacion
        holder.stockTextView.text = "${publicacion.stock.toString()} piezas"

        // Obtener imagen de Firebase
        Picasso.get()
            .load(publicacion.imagenUrl)
            .into(holder.imagenImageView)

        // Botón que lleva a la pestaña de edición de publicación
        holder.editarButton.setOnClickListener {
            val intent = Intent(holder.itemView.context, EditPostActivity::class.java).apply {
                // Agrega los extras con la información de la publicación
                putExtra("ubicacion", publicacion.ubicacion)
                putExtra("titulo", publicacion.titulo)
                putExtra("descripcion", publicacion.descripcion)
                putExtra("categoria", publicacion.categoria)
                putExtra("costo", publicacion.costo)
                putExtra("cant", publicacion.stock)
                putExtra("uid", publicacion.uid)
                putExtra("imagenUrl", publicacion.imagenUrl)
            }
            // Inicia la actividad EditPostActivity
            holder.itemView.context.startActivity(intent)
        }

        // Botón que elimina la publicación junto con todos los pedidos que esta tenga
        holder.deletButton.setOnClickListener {
            val db = FirebaseFirestore.getInstance()
            val auth = FirebaseAuth.getInstance()
            val currentUser = auth.currentUser

            if (currentUser != null) {
                // Elimina la publicación
                db.collection("publicaciones").document(publicacion.uid.toString())
                    .delete()
                    .addOnSuccessListener {
                        // Si la eliminación de la publicación es exitosa, elimina los pedidos asociados
                        db.collection("pedidos")
                            .whereEqualTo("publicacionId", publicacion.uid.toString())
                            .get()
                            .addOnSuccessListener { querySnapshot ->
                                for (document in querySnapshot.documents) {
                                    document.reference.delete()
                                }
                            }
                        // Remover el ítem de la lista y notificar al adaptador
                        (publicaciones as MutableList).removeAt(position)
                        notifyItemRemoved(position)
                        notifyItemRangeChanged(position, publicaciones.size)
                    }
                    .addOnFailureListener { e ->
                        // Manejar el error en la eliminación
                        e.printStackTrace()
                    }
            }
        }
    }

    override fun getItemCount(): Int {
        return publicaciones.size
    }

    inner class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tituloTextView: TextView = itemView.findViewById(R.id.stock_titulo)
        val categoriaTextView: TextView = itemView.findViewById(R.id.stock_category)
        val costoTextView: TextView = itemView.findViewById(R.id.stock_cost)
        val descripcionTextView: TextView = itemView.findViewById(R.id.stock_description)
        val ubicacionTextView: TextView = itemView.findViewById(R.id.stock_locate)
        val imagenImageView: ImageView = itemView.findViewById(R.id.stock_photo)
        val stockTextView: TextView = itemView.findViewById(R.id.stock_cant)
        val editarButton: Button = itemView.findViewById(R.id.stock_editButton)
        val deletButton: Button = itemView.findViewById(R.id.stock_deleteButton)
    }
}
