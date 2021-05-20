package com.jobtick.android.models.response.home

data class Payment(
    val income: String?,
    val last_trx: TRX?,
    val outcome: String?
)