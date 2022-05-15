package com.jobtick.android.network.model.request

data class BlockUserRequest(
    val block: Boolean,
    val user_id: String
)
