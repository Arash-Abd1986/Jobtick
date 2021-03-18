package com.jobtick.android.models.response

data class WorkTaskStatistics(
    val active_offers: Int?,
    val assigned: Int?,
    val cancelled: Int?,
    val completed: Int?,
    val completion_rate: Int?,
    val overdue: Int?,
    val total_assigned: Int?,
    val unpaid: Int?
)