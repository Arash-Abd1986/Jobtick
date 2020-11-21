package com.jobtick.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.json.JSONException;
import org.json.JSONObject;

public class RatingModel implements Parcelable {
    String TAG = RatingModel.class.getName();

    @Expose
    @SerializedName("rating_breakdown")
    private BreakdownModel breakdownModel;
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
    private Integer avgRating;

    public BreakdownModel getBreakdownModel() {
        return breakdownModel;
    }

    public void setBreakdownModel(BreakdownModel breakdownModel) {
        this.breakdownModel = breakdownModel;
    }

    /**
     * No args constructor for use in serialization
     */
    public RatingModel() {
    }

    /**
     * @param receivedReviews
     * @param totalRatings
     * @param pendingReviews
     * @param avgRating
     * @param sentReviews
     * @param breakdownModel
     */
    public RatingModel(Integer receivedReviews, Integer sentReviews, BreakdownModel breakdownModel, Integer pendingReviews, Integer totalRatings, Integer avgRating) {
        super();
        this.breakdownModel = breakdownModel;
        this.receivedReviews = receivedReviews;
        this.sentReviews = sentReviews;
        this.pendingReviews = pendingReviews;
        this.totalRatings = totalRatings;
        this.avgRating = avgRating;
    }


    protected RatingModel(Parcel in) {
        if (in.readByte() == 0) {
            receivedReviews = null;
        } else {
            receivedReviews = in.readInt();
        }
        if (in.readByte() == 0) {
            sentReviews = null;
        } else {
            sentReviews = in.readInt();
        }
        if (in.readByte() == 0) {
            pendingReviews = null;
        } else {
            pendingReviews = in.readInt();
        }
        if (in.readByte() == 0) {
            totalRatings = null;
        } else {
            totalRatings = in.readInt();
        }
        if (in.readByte() == 0) {
            avgRating = null;
        } else {
            avgRating = in.readInt();
        }
        breakdownModel = in.readParcelable(BreakdownModel.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (receivedReviews == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(receivedReviews);
        }
        if (sentReviews == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(sentReviews);
        }
        if (pendingReviews == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(pendingReviews);
        }
        if (totalRatings == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(totalRatings);
        }
        if (avgRating == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(avgRating);
        }
        dest.writeParcelable(breakdownModel, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<RatingModel> CREATOR = new Creator<RatingModel>() {
        @Override
        public RatingModel createFromParcel(Parcel in) {
            return new RatingModel(in);
        }

        @Override
        public RatingModel[] newArray(int size) {
            return new RatingModel[size];
        }
    };

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

    public Integer getAvgRating() {
        return avgRating;
    }

    public void setAvgRating(Integer avgRating) {
        this.avgRating = avgRating;
    }


    public RatingModel getJsonToModel(JSONObject jsonObject) {
        RatingModel ratingModel = new RatingModel();
        try {
            if (jsonObject.has("received_reviews") && !jsonObject.isNull("received_reviews"))
                ratingModel.setReceivedReviews(jsonObject.getInt("received_reviews"));
            if (jsonObject.has("sent_reviews") && !jsonObject.isNull("sent_reviews"))
                ratingModel.setSentReviews(jsonObject.getInt("sent_reviews"));
            if (jsonObject.has("pending_reviews") && !jsonObject.isNull("pending_reviews"))
                ratingModel.setPendingReviews(jsonObject.getInt("pending_reviews"));
            if (jsonObject.has("total_ratings") && !jsonObject.isNull("total_ratings"))
                ratingModel.setTotalRatings(jsonObject.getInt("total_ratings"));
            if (jsonObject.has("avg_rating") && !jsonObject.isNull("avg_rating"))
                ratingModel.setAvgRating(jsonObject.getInt("avg_rating"));
            if (jsonObject.has("rating_breakdown") && !jsonObject.isNull("rating_breakdown")) {
                Gson gson = new Gson();
                BreakdownModel breakdownModel = gson.fromJson(jsonObject.getJSONObject("rating_breakdown").toString(), BreakdownModel.class);
                ratingModel.setBreakdownModel(breakdownModel);
            }
        } catch (JSONException e) {
            Log.e(TAG, e.toString());
            e.printStackTrace();
        }
        return ratingModel;
    }
}
