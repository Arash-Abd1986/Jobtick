package com.jobtick.android.models.response.getbalance

data class GetBalanceResponse(
    val `data`: List<Data>?,
    val message: String?,
    val success: Boolean?
)