package com.jobtick.android.network.model.response.skills

data class SkillsResponse(
    val `data`: List<Data>,
    val message: String,
    val success: Boolean
)