package com.jobtick.android.network.model.response.draft

data class WorkerTier(
    val id: Int,
    val name: String,
    val service_fee: Int,
    val tax: Int
)