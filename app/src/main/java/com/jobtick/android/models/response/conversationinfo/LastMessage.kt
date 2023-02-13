package com.jobtick.android.models.response.conversationinfo

import com.google.gson.annotations.SerializedName

data class LastMessage(
    @SerializedName("attachment") val attachment: Attachment?,
    @SerializedName("conversation_id") val conversation_id: Int?,
    @SerializedName("created_at") val created_at: String?,
    @SerializedName("id") val id: Int?,
    @SerializedName("users") val is_seen: Int?,
    @SerializedName("is_seen") val message: String?,
    @SerializedName("sender_id") val sender_id: Int?
)