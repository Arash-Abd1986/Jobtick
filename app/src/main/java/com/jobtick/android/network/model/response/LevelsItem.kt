package com.jobtick.android.network.model.response

data class LevelsItem(
    val id: Int,
    val name: String,
    val service_fee: String,
    val slug: String,
    val tax: String,
    val max_amount: String,
    val min_amount: String
)