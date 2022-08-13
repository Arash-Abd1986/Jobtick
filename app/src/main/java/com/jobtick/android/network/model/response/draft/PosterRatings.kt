package com.jobtick.android.network.model.response.draft

data class PosterRatings(
    val avg_rating: Double,
    val pending_reviews: Int,
    val rating_breakdown: RatingBreakdown,
    val received_reviews: Int,
    val sent_reviews: Int,
    val total_ratings: Int
)