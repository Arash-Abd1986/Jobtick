package com.jobtick.android.models.response.checkforupdate

data class Data(
    val latest_major_version: String?,
    val latest_version: String?,
    val message: String?,
    val platform: String?
)