package com.jobtick.android.models.response.searchsuburb

import java.io.Serializable

data class Feature(
        val bbox: List<Double>?,
        val center: List<Double>?,
        val context: List<Context>?,
        val geometry: Geometry?,
        val id: String?,
        val language: String?,
        val language_en: String?,
        val place_name: String?,
        var place_name_en: String?,
        val place_type: List<String>?,
        val properties: Properties?,
        val relevance: Int?,
        val text: String?,
        val text_en: String?,
        var state: String?,
        val type: String?
) : Serializable