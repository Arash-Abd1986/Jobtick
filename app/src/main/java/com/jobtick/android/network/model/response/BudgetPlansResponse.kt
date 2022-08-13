package com.jobtick.android.network.model.response

data class BudgetPlansResponse(
    val `data`: List<DataX>,
    val message: String,
    val success: Boolean
)