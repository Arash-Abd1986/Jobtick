package com.jobtick.android.models.response.conversationinfo

data class Attachment(
    val id: Int?,
    val name: String?,
    val file_name: String?,
    val mime: String?,
    val url: String?,
    val thumb_url: String?,
    val modal_url: String?,
    val created_at: String?
)