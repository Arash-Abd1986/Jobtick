package com.jobtick.android.models.response.jobalerts

data class Data(
    val created_at: String?,
    val distance: Int?,
    val id: Int?,
    val keyword: String?,
    val location: String?,
    val maxprice: Any?,
    val minprice: Any?,
    val position: Position?,
    val type: String?
)