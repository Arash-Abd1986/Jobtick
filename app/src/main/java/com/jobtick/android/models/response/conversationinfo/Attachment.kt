package com.jobtick.android.models.response.conversationinfo

import com.google.gson.annotations.SerializedName

data class Attachment(
    @SerializedName("id") val id: Int?,
    @SerializedName("name") val name: String?,
    @SerializedName("file_name") val file_name: String?,
    @SerializedName("mime") val mime: String?,
    @SerializedName("url") val url: String?,
    @SerializedName("thumb_url") val thumb_url: String?,
    @SerializedName("modal_url") val modal_url: String?,
    @SerializedName("created_at") val created_at: String?

)