package com.jobtick.android.models.response.conversationinfo

data class LastMessage(
    val attachment: Attachment?,
    val conversation_id: Int?,
    val created_at: String?,
    val id: Int?,
    val is_seen: Int?,
    val message: String?,
    val sender_id: Int?
)