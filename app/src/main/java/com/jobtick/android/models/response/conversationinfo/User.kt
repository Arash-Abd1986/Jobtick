package com.jobtick.android.models.response.conversationinfo

data class User(
    val avatar: Avatar?,
    val id: Int?,
    val is_verified_account: Int?,
    val last_online: String?,
    val name: String?,
    val position: Position?,
    val slug: String?
)