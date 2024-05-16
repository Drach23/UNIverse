package com.unitech.universe.user_views

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.collection.LLRBNode
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import com.unitech.universe.R
import com.unitech.universe.seller_views.OrderAdapter
import com.unitech.universe.seller_views.Pedido

class OrderCheckAdapter(private val pedidos: List<Pedido>) : RecyclerView.Adapter<OrderCheckAdapter.PedidoUserViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PedidoUserViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.order_check, parent, false)
        return PedidoUserViewHolder(view)
    }

    override fun onBindViewHolder(holder: PedidoUserViewHolder, position: Int) {
        val pedido = pedidos[position]

        holder.tituloTextView.text = "${pedido.titulo}"
        holder.cantTextView.text = "Cantidad: ${pedido.cantidad.toString()} pzs"

        // Convertir a tipos no nulos antes de la multiplicación
        val cantidad = pedido.cantidad.toString().toInt()
        val costo = pedido.costo.toString().toFloat()
        val total = (costo * cantidad)

        holder.totalTextView.text = "Total: $total $"

        // Asignar el estado y color del texto según el estado del pedido
        holder.stateTextView.text = "${pedido.state}"
        when (pedido.state) {
            "Cancelado" -> holder.stateTextView.setTextColor(Color.parseColor("#C20E0E"))
            "Aceptado" -> holder.stateTextView.setTextColor(Color.parseColor("#049B90"))
            else -> holder.stateTextView.setTextColor(Color.parseColor("#CCD2DA"))
        }

        // Obtener la URL de la imagen desde Firestore y cargarla en el ImageView
        obtenerUrlImagen(pedido.titulo.toString(), pedido.vendedorId.toString()) { urlImagen ->
            if (urlImagen != null) {
                Picasso.get()
                    .load(urlImagen)
                    .into(holder.photoImageView)
            } else {
                // Opcional: establece una imagen de marcador de posición o de error
                holder.photoImageView.setImageResource(R.drawable.ic_foto_update)
            }
        }

    }

    override fun getItemCount(): Int {
        return pedidos.size
    }

    inner class PedidoUserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val photoImageView: ImageView = itemView.findViewById(R.id.orderCheck_photo)
        val tituloTextView: TextView = itemView.findViewById(R.id.orderCheck_titulo)
        val stateTextView: TextView = itemView.findViewById(R.id.orderCheck_state)
        val cantTextView: TextView = itemView.findViewById(R.id.orderCheck_cant)
        val totalTextView: TextView = itemView.findViewById(R.id.orderCheck_total)
    }

    private fun obtenerUrlImagen(titulo: String, uid: String, callback: (String?) -> Unit) {
        val db = FirebaseFirestore.getInstance()

        // Colección de publicaciones
        val publicacionesRef = db.collection("publicaciones")

        // Consulta por título y UID
        publicacionesRef.whereEqualTo("titulo", titulo)
            .whereEqualTo("usuarioId", uid)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    for (document in documents) {
                        // Suponiendo que la URL de la imagen está en el campo "imagenUrl"
                        val imagenUrl = document.getString("imagenUrl")
                        callback(imagenUrl)
                        return@addOnSuccessListener
                    }
                } else {
                    // No se encontró ninguna publicación
                    callback(null)
                }
            }
            .addOnFailureListener { exception ->
                // Manejo de errores
                callback(null)
            }
    }
}