package com.jobtick.android.models.response.conversationinfo

data class Task(
    val amount: Int?,
    val closed_at: Any?,
    val completed_at: Any?,
    val created_at: String?,
    val due_date: String?,
    val id: Int?,
    val slug: String?,
    val status: String?,
    val task_type: String?,
    val title: String?
)