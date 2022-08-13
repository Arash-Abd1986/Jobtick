package com.jobtick.android.network.model.response.draft

data class DraftResponse(
    val `data`: Data,
    val message: String,
    val success: Boolean
)