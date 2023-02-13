package com.jobtick.android.models.response.conversationinfo

import com.google.gson.annotations.SerializedName

data class Avatar(
    @SerializedName("created_at") val created_at: String?,
    @SerializedName("file_name") val file_name: String?,
    @SerializedName("id") val id: Int?,
    @SerializedName("mime") val mime: String?,
    @SerializedName("modal_url") val modal_url: String?,
    @SerializedName("name") val name: String?,
    @SerializedName("thumb_url") val thumb_url: String?,
    @SerializedName("url") val url: String?
)