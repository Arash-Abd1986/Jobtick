package com.jobtick.android.models.response.conversationinfo

import com.google.gson.annotations.SerializedName

data class Task(
    @SerializedName("amount") val amount: Int?,
    @SerializedName("closed_at") val closed_at: Any?,
    @SerializedName("completed_at") val completed_at: Any?,
    @SerializedName("created_at") val created_at: String?,
    @SerializedName("due_date") val due_date: String?,
    @SerializedName("id") val id: Int?,
    @SerializedName("slug") val slug: String?,
    @SerializedName("status") val status: String?,
    @SerializedName("task_type") val task_type: String?,
    @SerializedName("title") val title: String?
)