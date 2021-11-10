package com.jobtick.android.network.model.response

data class ReviewItem(
    val created_at: String,
    val id: Int,
    val is_published: Int,
    val message: String,
    val ratee_id: Int,
    val ratee_type: String,
    val rater: Rater,
    val rating: Int,
    val task: Task
)