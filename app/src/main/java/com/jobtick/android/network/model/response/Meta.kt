package com.jobtick.android.network.model.response

data class Meta(
    val current_page: Int,
    val from: Any,
    val last_page: Int,
    val links: List<Link>,
    val path: String,
    val per_page: Int,
    val to: Any,
    val total: Int
)