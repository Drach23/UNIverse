package com.unitech.universe.messages
data class ChatPreview(
    val chatId: String = "",
    val participants: List<String> = emptyList(),
    val lastMessage: String = ""
)
