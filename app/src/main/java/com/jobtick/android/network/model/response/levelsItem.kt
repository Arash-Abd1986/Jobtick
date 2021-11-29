package com.jobtick.android.network.model.response

data class levelsItem(
    val id: Int,
    val name: String,
    val service_fee: String,
    val slug: String,
    val tax: String,
    val threshold_amount: String
)