package com.jobtick.android.models.response

data class AccountStatus(
    val badges: Boolean?,
    val bank_account: Boolean?,
    val basic_info_completed: Boolean?,
    val credit_card: Boolean?,
    val portfolio: Boolean?,
    val skills: Boolean?
)