package com.jobtick.android.network.model.response

data class DataX(
    val created_at: Any,
    val id: Int,
    val max_budget: Int,
    val min_budget: Int,
    val title: String,
    val updated_at: Any,
    var isChecked:Boolean = false
)