
package com.jobtick.android.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.json.JSONException;
import org.json.JSONObject;

public class WorkTaskStatistics implements Parcelable
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
    @SerializedName("overdue")
    @Expose
    private Integer overdue;
    @SerializedName("total_assigned")
    @Expose
    private Integer totalAssigned;
    @SerializedName("total_posted")
    @Expose
    private Integer totalPosted;
    @SerializedName("unpaid")
    @Expose
    private Integer unpaid;
    @SerializedName("active_offers")
    @Expose
    private Integer activeOffers;

    public void setActiveOffers(Integer activeOffers) {
        this.activeOffers = activeOffers;
    }

    public Integer getActiveOffers() {
        return activeOffers;
    }

    public void setUnpaid(Integer unpaid) {
        this.unpaid = unpaid;
    }

    public Integer getUnpaid() {
        return unpaid;
    }

    public final static Creator<WorkTaskStatistics> CREATOR = new Creator<WorkTaskStatistics>() {


        @SuppressWarnings({
            "unchecked"
        })
        public WorkTaskStatistics createFromParcel(Parcel in) {
            return new WorkTaskStatistics(in);
        }

        public WorkTaskStatistics[] newArray(int size) {
            return (new WorkTaskStatistics[size]);
        }

    }
    ;

    protected WorkTaskStatistics(Parcel in) {
        this.assigned = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.cancelled = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.completed = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.completionRate = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.draft = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.overdue = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.totalAssigned = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.totalPosted = ((Integer) in.readValue((Integer.class.getClassLoader())));
    }

    /**
     * No args constructor for use in serialization
     * 
     */
    public WorkTaskStatistics() {
    }

    /**
     * 
     * @param overdue
     * @param totalAssigned
     * @param draft
     * @param totalPosted
     * @param cancelled
     * @param assigned
     * @param completed
     * @param completionRate
     */
    public WorkTaskStatistics(Integer assigned, Integer cancelled, Integer completed, Integer completionRate, Integer draft, Integer overdue, Integer totalAssigned, Integer totalPosted) {
        super();
        this.assigned = assigned;
        this.cancelled = cancelled;
        this.completed = completed;
        this.completionRate = completionRate;
        this.draft = draft;
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
        dest.writeValue(overdue);
        dest.writeValue(totalAssigned);
        dest.writeValue(totalPosted);
    }

    public int describeContents() {
        return  0;
    }

    public WorkTaskStatistics getJsonToModel(JSONObject jsonObject){
        WorkTaskStatistics statisticsModel = new WorkTaskStatistics();
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
