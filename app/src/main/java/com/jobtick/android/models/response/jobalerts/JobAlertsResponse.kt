package com.jobtick.android.models.response.jobalerts

data class JobAlertsResponse(
    val `data`: List<Data>?,
    val message: String?,
    val success: Boolean?
)