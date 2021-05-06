package com.jobtick.android.models.response.chat

data class Attachment(
    val created_at: String?,
    val file_name: String?,
    val file_size: Double?,
    val id: Int?,
    val mime: String?,
    val modal_url: String?,
    val name: String?,
    val thumb_url: String?,
    val url: String?
)