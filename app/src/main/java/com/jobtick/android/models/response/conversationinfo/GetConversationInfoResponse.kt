package com.jobtick.android.models.response.conversationinfo

import com.google.gson.annotations.SerializedName

data class GetConversationInfoResponse(
    @SerializedName("data") val `data`: Data?,
    @SerializedName("message") val message: String?,
    @SerializedName("success") val success: Boolean?
)