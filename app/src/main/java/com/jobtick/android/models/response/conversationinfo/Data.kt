package com.jobtick.android.models.response.conversationinfo

data class Data(
    val blocked_by: Any?,
    val chat_closed: Boolean?,
    val created_at: String?,
    val id: Int?,
    val last_message: LastMessage?,
    val name: String?,
    val task: Task?,
    val unseen_count: Int?,
    val users: List<User>?
)