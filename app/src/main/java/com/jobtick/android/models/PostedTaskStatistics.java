
package com.jobtick.android.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.json.JSONException;
import org.json.JSONObject;

public class PostedTaskStatistics implements Parcelable
{

    @SerializedName("assigned")
    @Expose
    private Integer assigned;
    @SerializedName("cancelled")
    @Expose
    private Integer cancelled;
    @SerializedName("completed")
    @Expose
    private Integer completed;
    @SerializedName("completion_rate")
    @Expose
    private Integer completionRate;
    @SerializedName("draft")
    @Expose
    private Integer draft;
    @SerializedName("open_for_bids")
    @Expose
    private Integer openForBids;
    @SerializedName("overdue")
    @Expose
    private Integer overdue;
    @SerializedName("total_assigned")
    @Expose
    private Integer totalAssigned;
    @SerializedName("total_posted")
    @Expose
    private Integer totalPosted;
    public final static Creator<PostedTaskStatistics> CREATOR = new Creator<PostedTaskStatistics>() {


        @SuppressWarnings({
            "unchecked"
        })
        public PostedTaskStatistics createFromParcel(Parcel in) {
            return new PostedTaskStatistics(in);
        }

        public PostedTaskStatistics[] newArray(int size) {
            return (new PostedTaskStatistics[size]);
        }

    }
    ;

    protected PostedTaskStatistics(Parcel in) {
        this.assigned = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.cancelled = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.completed = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.completionRate = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.draft = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.openForBids = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.overdue = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.totalAssigned = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.totalPosted = ((Integer) in.readValue((Integer.class.getClassLoader())));
    }

    /**
     * No args constructor for use in serialization
     * 
     */
    public PostedTaskStatistics() {
    }

    /**
     * 
     * @param overdue
     * @param openForBids
     * @param totalAssigned
     * @param draft
     * @param totalPosted
     * @param cancelled
     * @param assigned
     * @param completed
     * @param completionRate
     */
    public PostedTaskStatistics(Integer assigned, Integer cancelled, Integer completed, Integer completionRate, Integer draft, Integer openForBids, Integer overdue, Integer totalAssigned, Integer totalPosted) {
        super();
        this.assigned = assigned;
        this.cancelled = cancelled;
        this.completed = completed;
        this.completionRate = completionRate;
        this.draft = draft;
        this.openForBids = openForBids;
        this.overdue = overdue;
        this.totalAssigned = totalAssigned;
        this.totalPosted = totalPosted;
    }

    public Integer getAssigned() {
        return assigned;
    }

    public void setAssigned(Integer assigned) {
        this.assigned = assigned;
    }

    public Integer getCancelled() {
        return cancelled;
    }

    public void setCancelled(Integer cancelled) {
        this.cancelled = cancelled;
    }

    public Integer getCompleted() {
        return completed;
    }

    public void setCompleted(Integer completed) {
        this.completed = completed;
    }

    public Integer getCompletionRate() {
        return completionRate;
    }

    public void setCompletionRate(Integer completionRate) {
        this.completionRate = completionRate;
    }

    public Integer getDraft() {
        return draft;
    }

    public void setDraft(Integer draft) {
        this.draft = draft;
    }

    public Integer getOpenForBids() {
        return openForBids;
    }

    public void setOpenForBids(Integer openForBids) {
        this.openForBids = openForBids;
    }

    public Integer getOverdue() {
        return overdue;
    }

    public void setOverdue(Integer overdue) {
        this.overdue = overdue;
    }

    public Integer getTotalAssigned() {
        return totalAssigned;
    }

    public void setTotalAssigned(Integer totalAssigned) {
        this.totalAssigned = totalAssigned;
    }

    public Integer getTotalPosted() {
        return totalPosted;
    }

    public void setTotalPosted(Integer totalPosted) {
        this.totalPosted = totalPosted;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(assigned);
        dest.writeValue(cancelled);
        dest.writeValue(completed);
        dest.writeValue(completionRate);
        dest.writeValue(draft);
        dest.writeValue(openForBids);
        dest.writeValue(overdue);
        dest.writeValue(totalAssigned);
        dest.writeValue(totalPosted);
    }

    public int describeContents() {
        return  0;
    }

    public PostedTaskStatistics getJsonToModel(JSONObject jsonObject){
        PostedTaskStatistics statisticsModel = new PostedTaskStatistics();
        try{
            if(jsonObject.has("assigned") && !jsonObject.isNull("assigned"))
                statisticsModel.setAssigned(jsonObject.getInt("assigned"));
            if(jsonObject.has("cancelled") && !jsonObject.isNull("cancelled"))
                statisticsModel.setCancelled(jsonObject.getInt("cancelled"));
            if(jsonObject.has("completed") && !jsonObject.isNull("completed"))
                statisticsModel.setCompleted(jsonObject.getInt("completed"));
            if(jsonObject.has("completion_rate") && !jsonObject.isNull("completion_rate"))
                statisticsModel.setCompletionRate(jsonObject.getInt("completion_rate"));
            if(jsonObject.has("draft") && !jsonObject.isNull("draft"))
                statisticsModel.setDraft(jsonObject.getInt("draft"));
            if(jsonObject.has("open_for_bids") && !jsonObject.isNull("open_for_bids"))
                statisticsModel.setOpenForBids(jsonObject.getInt("open_for_bids"));
            if(jsonObject.has("overdue") && !jsonObject.isNull("overdue"))
                statisticsModel.setOverdue(jsonObject.getInt("overdue"));
            if(jsonObject.has("total_assigned") && !jsonObject.isNull("total_assigned"))
                statisticsModel.setTotalAssigned(jsonObject.getInt("total_assigned"));
            if(jsonObject.has("total_posted") && !jsonObject.isNull("total_posted"))
                statisticsModel.setTotalPosted(jsonObject.getInt("total_posted"));
        }catch (JSONException e){
            e.printStackTrace();
        }
        return statisticsModel;
    }

}
