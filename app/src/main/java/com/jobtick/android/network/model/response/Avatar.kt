package com.jobtick.android.network.model.response

data class Avatar(
    val created_at: String,
    val file_name: String,
    val file_size: Int,
    val id: Int,
    val mime: String,
    val modal_url: String,
    val name: String,
    val thumb_url: String,
    val url: String
)