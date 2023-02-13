package com.jobtick.android.models.response.conversationinfo

import com.google.gson.annotations.SerializedName

data class Data(
    @SerializedName("blocked_by") val blocked_by: Any?,
    @SerializedName("chat_closed") val chat_closed: Boolean?,
    @SerializedName("created_at") val created_at: String?,
    @SerializedName("id") val id: Int?,
    @SerializedName("last_message") val last_message: LastMessage?,
    @SerializedName("name") val name: String?,
    @SerializedName("task") val task: Task?,
    @SerializedName("unseen_count") val unseen_count: Int?,
    @SerializedName("users") val users: List<User>?
)