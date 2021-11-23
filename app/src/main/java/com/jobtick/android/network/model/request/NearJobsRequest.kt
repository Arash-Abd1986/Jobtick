package com.jobtick.android.network.model.request

data class NearJobsRequest(
    val latitude: Float, val longitude: Float, val radius: Int, val limit: Int
)
