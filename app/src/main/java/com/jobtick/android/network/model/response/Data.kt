package com.jobtick.android.network.model.response

import com.jobtick.android.models.response.myjobs.AssignedWorker
import com.jobtick.android.models.response.myjobs.OfferedUser

data class Data(
    val amount: Int,
    val assigned_worker: List<AssignedWorker>,
    val budget: Int,
    val category_id: Int,
    val due_date: String,
    val id: Int,
    val latitude: String,
    val location: String,
    val longitude: String,
    val offers: List<OfferedUser>,
    val offers_count: Int,
    val poster_avatar: String,
    val poster_id: Int,
    val slug: String,
    val status: String,
    val title: String
)