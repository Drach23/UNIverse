package com.unitech.universe.seller_views

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.unitech.universe.R

class OrderAdapter(private val pedidos: List<Pedido>) : RecyclerView.Adapter<OrderAdapter.PedidoViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PedidoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.order_table, parent, false)
        return PedidoViewHolder(view)
    }

    override fun onBindViewHolder(holder: PedidoViewHolder, position: Int) {
        val pedido = pedidos[position]
        val compradorUID = pedido.compradorId.toString()

        // Asignar los valores de la compra a las vistas
        holder.cantidadTextView.text = "${pedido.cantidad.toString()} pza"
        holder.compradorTextView.text = pedido.comprador
        holder.costoTextView.text = "${pedido.costo.toString()} $"
        holder.telCompradorTextView.text = pedido.telComprador
        holder.tituloTextView.text = pedido.titulo
        holder.stateTextView.text = pedido.state

        // Convertir a tipos no nulos antes de la multiplicación
        val cantidad = pedido.cantidad.toString().toInt()
        val costo = pedido.costo.toString().toFloat()
        val total = (costo * cantidad)

        holder.totalTextView.text = "$total $"


        // Obtener username
        // Obtener el nombre de usuario del comprador desde Firebase Firestore
        val db = FirebaseFirestore.getInstance()
        val userRef = db.collection("users").document(compradorUID!!)
        userRef.get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    val nombreUsuario = documentSnapshot.getString("username") ?: "Desconocido"
                    holder.userComprador.text = nombreUsuario
                } else {
                    holder.userComprador.text = "Desconocido"
                }
            }
            .addOnFailureListener {
                holder.userComprador.text = "Error al obtener usuario"
            }

        // Establecer la visibilidad de los botones
        if (pedido.state == "Aceptado" || pedido.state == "Cancelado") {
            holder.btnAceptarOrden.visibility = View.INVISIBLE
            holder.btnCancelararOrden.visibility = View.INVISIBLE
            holder.btnChangeState.visibility = View.VISIBLE
            holder.stateTextView.visibility = View.VISIBLE
        } else {
            holder.btnAceptarOrden.visibility = View.VISIBLE
            holder.btnCancelararOrden.visibility = View.VISIBLE
            holder.stateTextView.visibility = View.INVISIBLE
            holder.btnChangeState.visibility = View.INVISIBLE
        }

        // Listeners para los botones de aceptar y cancelar orden
        holder.btnAceptarOrden.setOnClickListener {
            actualizarEstadoOrden(pedido.uid, "Aceptado", position)
        }

        holder.btnCancelararOrden.setOnClickListener {
            actualizarEstadoOrden(pedido.uid, "Cancelado", position)
        }

        holder.btnChangeState.setOnClickListener{
            holder.btnAceptarOrden.visibility = View.VISIBLE
            holder.btnCancelararOrden.visibility = View.VISIBLE
            holder.stateTextView.visibility = View.INVISIBLE
            holder.btnChangeState.visibility = View.INVISIBLE
        }

    }

    override fun getItemCount(): Int {
        return pedidos.size
    }

    inner class PedidoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cantidadTextView: TextView = itemView.findViewById(R.id.order_cant)
        val compradorTextView: TextView = itemView.findViewById(R.id.order_user_name)
        val userComprador: TextView = itemView.findViewById(R.id.order_user_username)
        val costoTextView: TextView = itemView.findViewById(R.id.order_cost)
        val telCompradorTextView: TextView = itemView.findViewById(R.id.order_user_tel)
        val tituloTextView: TextView = itemView.findViewById(R.id.order_view_product_name)
        val totalTextView: TextView = itemView.findViewById(R.id.order_total)
        val btnAceptarOrden: Button = itemView.findViewById(R.id.order_btn_aceptar)
        val btnCancelararOrden: Button = itemView.findViewById(R.id.order_btn_cancelar)
        val stateTextView: TextView = itemView.findViewById(R.id.order_state_text)
        val btnChangeState: Button = itemView.findViewById(R.id.order_change_state)
    }

    private fun actualizarEstadoOrden(pedidoId: String?, nuevoEstado: String, position: Int) {
        if (pedidoId == null) return

        val db = FirebaseFirestore.getInstance()
        val pedidoRef = db.collection("pedidos").document(pedidoId)

        val datosActualizar: MutableMap<String, Any> = hashMapOf(
            "state" to nuevoEstado
        )


        pedidoRef.update(datosActualizar)
            .addOnSuccessListener {
                // Actualización exitosa
                pedidos[position].state = nuevoEstado
                notifyItemChanged(position)
            }
            .addOnFailureListener { e ->
                // Error al actualizar el estado
                println("Error al actualizar el estado: ${e.message}")
            }
    }

}