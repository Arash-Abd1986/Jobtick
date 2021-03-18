package com.jobtick.android.models.response

data class Badge(
    val badge_code: String?,
    val badge_details: BadgeDetails?,
    val id: Int?,
    val is_verified: Int?
)