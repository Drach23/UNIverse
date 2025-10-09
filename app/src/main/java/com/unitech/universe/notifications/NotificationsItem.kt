package com.unitech.universe.notifications

data class NotificationItem(
    val id: String = "",
    val type: String = "",      // "like", "comment", "follow"
    val senderId: String = "",  // quién generó la notificación
    val message: String = "",   // texto que se mostrará
    val timestamp: Long = 0,    // fecha/hora
    val seen: Boolean = false   // si ya fue vista
)
