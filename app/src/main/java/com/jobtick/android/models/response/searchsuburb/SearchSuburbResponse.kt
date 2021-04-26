package com.jobtick.android.models.response.searchsuburb

data class SearchSuburbResponse(
    val attribution: String?,
    val features: List<Feature>?,
    val query: List<String>?,
    val type: String?
)