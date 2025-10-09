package com.unitech.universe.notifications

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore

object NotificationUtils {

    fun addNotification(receiverId: String, type: String, senderId: String, message: String) {
        val db = FirebaseFirestore.getInstance()
        val notification = hashMapOf(
            "type" to type,
            "senderId" to senderId,
            "message" to message,
            "timestamp" to System.currentTimeMillis(),
            "seen" to false
        )

        db.collection("notifications")
            .document(receiverId)
            .collection("userNotifications")
            .add(notification)
            .addOnSuccessListener {
                Log.d("Notifications", "Notificación creada para $receiverId")
            }
            .addOnFailureListener { e ->
                Log.w("Notifications", "Error al crear notificación", e)
            }
    }
}
