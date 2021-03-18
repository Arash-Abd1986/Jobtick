package com.jobtick.android.models.response

data class PostedTaskStatistics(
    val assigned: Int?,
    val cancelled: Int?,
    val completed: Int?,
    val completion_rate: Int?,
    val draft: Int?,
    val open_for_bids: Int?,
    val overdue: Int?,
    val total_assigned: Int?,
    val total_posted: Int?,
    val unpaid: Int?
)