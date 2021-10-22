package com.jobtick.android.network.model.response

data class Rater(
    val avatar: Avatar,
    val firstname: String,
    val id: Int,
    val is_verified_account: Int,
    val last_online: String,
    val lastname: String,
    val name: String,
    val slug: String
)