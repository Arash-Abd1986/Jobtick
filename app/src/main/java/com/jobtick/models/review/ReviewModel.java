
package com.jobtick.models.review;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.json.JSONException;
import org.json.JSONObject;

public class ReviewModel implements Parcelable
{

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("rater")
    @Expose
    private Rater rater;
    @SerializedName("ratee_id")
    @Expose
    private Integer rateeId;
    @SerializedName("task")
    @Expose
    private Task task;
    @SerializedName("rating")
    @Expose
    private Integer rating;
    @SerializedName("ratee_type")
    @Expose
    private String rateeType;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("is_published")
    @Expose
    private Integer isPublished;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    public final static Creator<ReviewModel> CREATOR = new Creator<ReviewModel>() {

        @SuppressWarnings({
            "unchecked"
        })
        public ReviewModel createFromParcel(Parcel in) {
            return new ReviewModel(in);
        }

        public ReviewModel[] newArray(int size) {
            return (new ReviewModel[size]);
        }

    };

    protected ReviewModel(Parcel in) {
        this.id = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.rater = ((Rater) in.readValue((Rater.class.getClassLoader())));
        this.rateeId = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.task = ((Task) in.readValue((Task.class.getClassLoader())));
        this.rating = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.rateeType = ((String) in.readValue((String.class.getClassLoader())));
        this.message = ((String) in.readValue((String.class.getClassLoader())));
        this.isPublished = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.createdAt = ((String) in.readValue((String.class.getClassLoader())));
    }

    public ReviewModel() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Rater getRater() {
        return rater;
    }

    public void setRater(Rater rater) {
        this.rater = rater;
    }

    public Integer getRateeId() {
        return rateeId;
    }

    public void setRateeId(Integer rateeId) {
        this.rateeId = rateeId;
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public String getRateeType() {
        return rateeType;
    }

    public void setRateeType(String rateeType) {
        this.rateeType = rateeType;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getIsPublished() {
        return isPublished;
    }

    public void setIsPublished(Integer isPublished) {
        this.isPublished = isPublished;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(id);
        dest.writeValue(rater);
        dest.writeValue(rateeId);
        dest.writeValue(task);
        dest.writeValue(rating);
        dest.writeValue(rateeType);
        dest.writeValue(message);
        dest.writeValue(isPublished);
        dest.writeValue(createdAt);
    }

    public int describeContents() {
        return  0;
    }

    public ReviewModel getJsonToModel(JSONObject jsonObject) {
        ReviewModel reviewModel = new ReviewModel();
        try {
            if (jsonObject.has("ratee_type") && !jsonObject.isNull("ratee_type"))
                reviewModel.setRateeType(jsonObject.getString("ratee_type"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return reviewModel;
    }

}
