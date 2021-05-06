package com.jobtick.android.models.response.chat

data class ChatResponse(
    val `data`: List<MessageItem>?,
    val links: Links?,
    val message: String?,
    val meta: Meta?,
    val success: Boolean?
)