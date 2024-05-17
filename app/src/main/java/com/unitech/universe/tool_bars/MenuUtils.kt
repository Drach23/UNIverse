package com.unitech.universe.tool_bars

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.unitech.universe.PerfilActivity
import com.unitech.universe.R
import com.unitech.universe.seller_views.StockUserActivity
import com.unitech.universe.seller_views.ViewsOrdersActivity
import com.unitech.universe.start_pages.UniverseActivity
import com.unitech.universe.user_views.OrdersCheckUserActivity

object MenuUtils {
    private var auth: FirebaseAuth? = null // Cambiado a nullable para evitar NullPointerExceptions

    private fun initializeAuth() {
        auth = FirebaseAuth.getInstance()
    }

    fun showPopupMenu(context: Context, anchor: View) {
        // Asegurarse de que auth esté inicializado antes de usarlo
        if (auth == null) {
            initializeAuth()
        }

        val layoutInflater = LayoutInflater.from(context)
        val popupView = layoutInflater.inflate(R.layout.layout_mainpop, null)

        val popupWindow = PopupWindow(popupView,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            true).apply {
            isOutsideTouchable = true
            showAsDropDown(anchor)
        }

        // Configurar eventos de clic en los ítems del menú
        popupView.findViewById<LinearLayout>(R.id.menu_item_1).setOnClickListener {
            Toast.makeText(context, "Ver Perfil", Toast.LENGTH_SHORT).show()
            popupWindow.dismiss()

            val intent = Intent(context, PerfilActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            context.startActivity(intent)
        }

        popupView.findViewById<LinearLayout>(R.id.menu_item_2).setOnClickListener {
            Toast.makeText(context, "Cerrando Sesión", Toast.LENGTH_SHORT).show()
            popupWindow.dismiss()
            // Cerrar sesión y redirigir a la pantalla de inicio de sesión
            auth?.signOut() // Usando el operador de seguridad de llamada para evitar NullPointerExceptions
            val intent = Intent(context, UniverseActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            context.startActivity(intent)
        }

        popupView.findViewById<LinearLayout>(R.id.menu_item_3).setOnClickListener {
            Toast.makeText(context, "Mostrando Ventas", Toast.LENGTH_SHORT).show()
            popupWindow.dismiss()

            val intent = Intent(context, ViewsOrdersActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            context.startActivity(intent)
        }

        popupView.findViewById<LinearLayout>(R.id.menu_item_4).setOnClickListener {
            Toast.makeText(context, "Mostrando Inventario", Toast.LENGTH_SHORT).show()
            popupWindow.dismiss()

            val intent = Intent(context, StockUserActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            context.startActivity(intent)
        }

        popupView.findViewById<LinearLayout>(R.id.menu_item_5).setOnClickListener {
            Toast.makeText(context, "Mostrando Pedidos", Toast.LENGTH_SHORT).show()
            popupWindow.dismiss()

            val intent = Intent(context, OrdersCheckUserActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            context.startActivity(intent)
        }
    }
}
