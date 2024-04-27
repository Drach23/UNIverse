package com.unitech.universe
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import com.unitech.universe.R


object NavUtils {

    // Función para acceder al elemento "Home" del menú de navegación
    fun getHomeMenuItem(menu: Menu): MenuItem? {
        return menu.findItem(R.id.nav_home)
    }

    // Función para acceder al elemento "Search" del menú de navegación
    fun getSearchMenuItem(menu: Menu): MenuItem? {
        return menu.findItem(R.id.nav_search)
    }

    // Función para acceder al elemento "Add" del menú de navegación
    fun getAddMenuItem(menu: Menu): MenuItem? {
        return menu.findItem(R.id.nav_add)
    }

    // Función para acceder al elemento "Notifications" del menú de navegación
    fun getNotificationsMenuItem(menu: Menu): MenuItem? {
        return menu.findItem(R.id.nav_notifications)
    }

    // Función para acceder al elemento "Messages" del menú de navegación
    fun getEmailMenuItem(menu: Menu): MenuItem? {
        return menu.findItem(R.id.nav_email)
    }

    // Función para manejar la selección de elementos del menú de navegación
    fun handleNavigationItemSelected(context: Context, menuItem: MenuItem) {
        val intent = when (menuItem.itemId) {
            R.id.nav_home -> Intent(context, HomePageActivity::class.java)
            R.id.nav_add -> Intent(context, CreatePostsActivity::class.java)
            // Agrega casos para otros elementos del menú aquí si es necesario
            else -> null
        }
        intent?.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
        intent?.let {
            context.startActivity(it)
        }
    }

}