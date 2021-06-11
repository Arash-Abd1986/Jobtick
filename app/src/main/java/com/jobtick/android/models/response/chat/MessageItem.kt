package com.jobtick.android.models.response.chat

data class MessageItem(
    val attachment: Attachment?,
    val conversation_id: Int?,
    val created_at: String?,
    val id: Int?,
    val is_seen: Int?,
    val is_log: Int?,
    val message: String?,
    val sender_id: Int?
)