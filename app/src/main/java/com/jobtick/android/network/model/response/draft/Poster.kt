package com.jobtick.android.network.model.response.draft

data class Poster(
    val avatar: Avatar,
    val id: Int,
    val is_verified_account: Int,
    val last_online: String,
    val name: String,
    val position: Any,
    val posted_task_statistics: PostedTaskStatistics,
    val poster_ratings: PosterRatings,
    val poster_tier: PosterTier,
    val slug: String,
    val ticker_ratings: TickerRatings,
    val work_task_statistics: WorkTaskStatistics,
    val worker_ratings: WorkerRatings,
    val worker_tier: WorkerTier
)