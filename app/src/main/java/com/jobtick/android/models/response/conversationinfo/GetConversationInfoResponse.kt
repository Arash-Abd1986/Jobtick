package com.jobtick.android.models.response.conversationinfo

data class GetConversationInfoResponse(
    val `data`: Data?,
    val message: String?,
    val success: Boolean?
)