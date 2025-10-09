package com.unitech.universe.tool_bars
import android.content.Context
import android.content.Intent
import android.view.MenuItem
import com.unitech.universe.HomePageActivity
import com.unitech.universe.R
import com.unitech.universe.post_feed.CreatePostsActivity
import com.unitech.universe.user_views.SearchViewActivity
import com.unitech.universe.notifications.NotificationActivity


object NavUtils {

    // Mapeo de IDs de elementos del menú a sus respectivas clases de actividad
    private val menuItemToActivityMap = mapOf(
        R.id.nav_home to HomePageActivity::class.java,
        R.id.nav_add to CreatePostsActivity::class.java,
        R.id.nav_search to SearchViewActivity::class.java,
        R.id.nav_notifications to NotificationActivity::class.java
    )

    // Maneja la selección de elementos del menú
    fun handleNavigationItemSelected(context: Context, menuItem: MenuItem) {
        // Obtiene la clase de actividad a partir del ID del elemento del menú
        val targetActivityClass = menuItemToActivityMap[menuItem.itemId]

        targetActivityClass?.let { activityClass ->
            // Crea un Intent para la actividad objetivo
            val intent = Intent(context, activityClass)

            // Añade la bandera para mover la actividad existente al frente si ya está activa
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)

            // Agrega la bandera para cerrar actividades en la parte superior de la pila
            // si ya existe una instancia de la actividad objetivo.
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

            // Transmite el ID del ítem seleccionado a la nueva actividad
            intent.putExtra("selected_tab_id", menuItem.itemId)

            // Inicia la actividad
            context.startActivity(intent)
        }
    }

}