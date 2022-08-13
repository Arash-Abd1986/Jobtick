package com.jobtick.android.network.model.response.draft

data class DueTime(
    val afternoon: Boolean,
    val anytime: Boolean,
    val evening: Boolean,
    val morning: Boolean
)