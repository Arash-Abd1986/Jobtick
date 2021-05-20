package com.jobtick.android.models.response.home

data class Job(
    val amount: Int?,
    val assigned_worker: List<Any>?,
    val budget: Int?,
    val category_id: Int?,
    val due_date: String?,
    val id: Int?,
    val latitude: Any?,
    val location: Any?,
    val longitude: Any?,
    val offers: List<Any>?,
    val offers_count: Int?,
    val poster_avatar: Any?,
    val poster_id: Int?,
    val slug: String?,
    val status: String?,
    val title: String?
)