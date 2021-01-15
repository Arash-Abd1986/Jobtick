package com.jobtick.android.models.payments;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PosterRatings {
    @SerializedName("rating_breakdown")
    @Expose
    private RatingBreakdown ratingBreakdown;
    @SerializedName("received_reviews")
    @Expose
    private Integer receivedReviews;
    @SerializedName("sent_reviews")
    @Expose
    private Integer sentReviews;
    @SerializedName("pending_reviews")
    @Expose
    private Integer pendingReviews;
    @SerializedName("total_ratings")
    @Expose
    private Integer totalRatings;
    @SerializedName("avg_rating")
    @Expose
    private Double avgRating;

    public RatingBreakdown getRatingBreakdown() {
        return ratingBreakdown;
    }

    public void setRatingBreakdown(RatingBreakdown ratingBreakdown) {
        this.ratingBreakdown = ratingBreakdown;
    }

    public Integer getReceivedReviews() {
        return receivedReviews;
    }

    public void setReceivedReviews(Integer receivedReviews) {
        this.receivedReviews = receivedReviews;
    }

    public Integer getSentReviews() {
        return sentReviews;
    }

    public void setSentReviews(Integer sentReviews) {
        this.sentReviews = sentReviews;
    }

    public Integer getPendingReviews() {
        return pendingReviews;
    }

    public void setPendingReviews(Integer pendingReviews) {
        this.pendingReviews = pendingReviews;
    }

    public Integer getTotalRatings() {
        return totalRatings;
    }

    public void setTotalRatings(Integer totalRatings) {
        this.totalRatings = totalRatings;
    }

    public Double getAvgRating() {
        return avgRating;
    }

    public void setAvgRating(Double avgRating) {
        this.avgRating = avgRating;
    }
}
