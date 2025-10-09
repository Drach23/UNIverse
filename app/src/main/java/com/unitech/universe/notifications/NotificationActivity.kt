package com.unitech.universe.notifications

import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.unitech.universe.R
import com.unitech.universe.tool_bars.MenuUtils
import com.unitech.universe.tool_bars.NavUtils

class NotificationActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var notificationsAdapter: NotificationsAdapter
    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var tvNoNotifications: TextView
    private lateinit var ivNoNotifications: ImageView

    private val notificationsList = mutableListOf<NotificationItem>()
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notifications)

        // RecyclerView
        recyclerView = findViewById(R.id.recyclerViewNotifications)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // TextView para mostrar mensaje si no hay notificaciones
        tvNoNotifications = findViewById(R.id.tvNoNotifications)
        tvNoNotifications.visibility = View.GONE

        ivNoNotifications = findViewById(R.id.ivNoNotifications)
        ivNoNotifications.visibility = View.GONE

        // Adaptador
        notificationsAdapter = NotificationsAdapter(notificationsList) { notification ->
            markNotificationAsSeen(notification)
        }
        recyclerView.adapter = notificationsAdapter

        // BottomNavigationView
        bottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNavigationView.selectedItemId = R.id.nav_notifications
        bottomNavigationView.setOnItemSelectedListener { menuItem ->
            NavUtils.handleNavigationItemSelected(this, menuItem)
            true
        }

        // Activa el hamburger - Despliega menu lateral
        val menuButton: ImageButton = findViewById(R.id.menuButton)
        menuButton.setOnClickListener {
            MenuUtils.showPopupMenu(this, it)
        }

        loadNotifications()
        markAllAsSeen()
        showUserNameActive()
    }

    private fun loadNotifications() {
        val userId = auth.currentUser?.uid ?: return

        db.collection("notifications")
            .document(userId)
            .collection("userNotifications")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshots, e ->
                if (e != null) return@addSnapshotListener

                notificationsList.clear()
                for (doc in snapshots!!) {
                    val notification = doc.toObject(NotificationItem::class.java).copy(id = doc.id)
                    notificationsList.add(notification)
                }

                notificationsAdapter.notifyDataSetChanged()

                // Mostrar mensaje si no hay notificaciones
                if (notificationsList.isEmpty()) {
                    tvNoNotifications.visibility = View.VISIBLE
                    ivNoNotifications.visibility = View.VISIBLE
                    recyclerView.visibility = View.GONE
                } else {
                    //mostrar las notificaciones
                    tvNoNotifications.visibility = View.GONE
                    ivNoNotifications.visibility = View.GONE
                    recyclerView.visibility = View.VISIBLE
                }
            }
    }

    private fun markAllAsSeen() {
        val userId = auth.currentUser?.uid ?: return

        db.collection("notifications")
            .document(userId)
            .collection("userNotifications")
            .whereEqualTo("seen", false)
            .get()
            .addOnSuccessListener { docs ->
                for (doc in docs) {
                    doc.reference.update("seen", true)
                }
            }
    }

    private fun markNotificationAsSeen(notification: NotificationItem) {
        val userId = auth.currentUser?.uid ?: return

        db.collection("notifications")
            .document(userId)
            .collection("userNotifications")
            .document(notification.id)
            .update("seen", true)
            .addOnSuccessListener {
                val index = notificationsList.indexOfFirst { it.id == notification.id }
                if (index != -1) {
                    notificationsList[index] = notification.copy(seen = true)
                    notificationsAdapter.notifyItemChanged(index)
                }
            }
    }

    private fun showUserNameActive() {
        // Encuentra la TextView para mostrar el nombre del usuario
        val usernameTextView: TextView = findViewById(R.id.username)

        // Obtén el usuario actual
        val currentUser = auth.currentUser

        // Verifica si el usuario está autenticado
        if (currentUser != null) {
            // Obtén el ID de usuario actual
            val userId = currentUser.uid

            // Referencia al documento del usuario en Firestore
            val userRef = db.collection("users").document(userId)

            // Realiza una solicitud para obtener los datos del documento del usuario
            userRef.get()
                .addOnSuccessListener { document ->
                    // Verifica si el documento existe y contiene datos
                    if (document?.exists() == true) {
                        // Obtén el nombre de usuario
                        val username = document.getString("username")

                        // Verifica si el nombre de usuario es nulo o vacío
                        if (!username.isNullOrBlank()) {
                            // Muestra el nombre de usuario en la TextView
                            usernameTextView.text = username
                        } else {
                            showMessage("No se encontró el nombre de usuario.")
                        }
                    } else {
                        showMessage("No se encontraron datos del usuario.")
                    }
                }
                .addOnFailureListener { exception ->
                    // Maneja el error de manera específica
                    showMessage("Error al obtener los datos del usuario: ${exception.message}")
                }
        } else {
            // Si el usuario no está autenticado
            showMessage("El usuario no está autenticado.")
        }
    }
    private fun showMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
