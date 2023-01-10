package com.jobtick.android.models.payments

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class WorkerRatings: java.io.Serializable {
    @SerializedName("rating_breakdown")
    @Expose
    var ratingBreakdown: RatingBreakdown? = null

    @SerializedName("received_reviews")
    @Expose
    var receivedReviews: Int? = null

    @SerializedName("sent_reviews")
    @Expose
    var sentReviews: Int? = null

    @SerializedName("pending_reviews")
    @Expose
    var pendingReviews: Int? = null

    @SerializedName("total_ratings")
    @Expose
    var totalRatings: Int? = null

    @SerializedName("avg_rating")
    @Expose
    var avgRating: Double? = null
}