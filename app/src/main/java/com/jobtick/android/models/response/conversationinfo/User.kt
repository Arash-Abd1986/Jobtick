package com.jobtick.android.models.response.conversationinfo

import com.google.gson.annotations.SerializedName

data class User(
    @SerializedName("avatar") val avatar: Avatar?,
    @SerializedName("id") val id: Int?,
    @SerializedName("is_verified_account") val is_verified_account: Int?,
    @SerializedName("last_online") val last_online: String?,
    @SerializedName("name") val name: String?,
    @SerializedName("position") val position: Position?,
    @SerializedName("slug") val slug: String?
)