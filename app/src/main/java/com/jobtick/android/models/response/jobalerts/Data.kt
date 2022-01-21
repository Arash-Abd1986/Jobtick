package com.jobtick.android.models.response.jobalerts

data class Data(
    val created_at: String?,
    val distance: Int?,
    val id: Int?,
    val text: String?,
    val location: String?,
    val max_price: Any?,
    val min_price: Any?,
    val position: Position?,
    val type: String?
)