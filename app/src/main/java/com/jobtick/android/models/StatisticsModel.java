package com.jobtick.android.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.json.JSONException;
import org.json.JSONObject;

import timber.log.Timber;

public class StatisticsModel implements Parcelable {
    String TAG = StatisticsModel.class.getName();
    @SerializedName("assigned")
    @Expose
    private Integer assigned;
    @SerializedName("cancelled")
    @Expose
    private Integer cancelled;
    @SerializedName("close_plus_cancelled")
    @Expose
    private Integer closePlusCancelled;
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
    @SerializedName("payment_requested")
    @Expose
    private Integer paymentRequested;
    @SerializedName("total_assigned")
    @Expose
    private Integer totalAssigned;
    @SerializedName("total_posted")
    @Expose
    private Integer totalPosted;
    @SerializedName("current_bids")
    @Expose
    private Integer currentBids;

    /**
     * No args constructor for use in serialization
     *
     */
    public StatisticsModel() {
    }

    /**
     *
     * @param currentBids
     * @param overdue
     * @param openForBids
     * @param totalAssigned
     * @param draft
     * @param totalPosted
     * @param cancelled
     * @param closePlusCancelled
     * @param assigned
     * @param completed
     * @param completionRate
     * @param paymentRequested
     */
    public StatisticsModel(Integer assigned, Integer cancelled, Integer closePlusCancelled, Integer completed, Integer completionRate, Integer draft, Integer openForBids, Integer overdue, Integer paymentRequested, Integer totalAssigned, Integer totalPosted, Integer currentBids) {
        super();
        this.assigned = assigned;
        this.cancelled = cancelled;
        this.closePlusCancelled = closePlusCancelled;
        this.completed = completed;
        this.completionRate = completionRate;
        this.draft = draft;
        this.openForBids = openForBids;
        this.overdue = overdue;
        this.paymentRequested = paymentRequested;
        this.totalAssigned = totalAssigned;
        this.totalPosted = totalPosted;
        this.currentBids = currentBids;
    }

    protected StatisticsModel(Parcel in) {
        if (in.readByte() == 0) {
            assigned = null;
        } else {
            assigned = in.readInt();
        }
        if (in.readByte() == 0) {
            cancelled = null;
        } else {
            cancelled = in.readInt();
        }
        if (in.readByte() == 0) {
            closePlusCancelled = null;
        } else {
            closePlusCancelled = in.readInt();
        }
        if (in.readByte() == 0) {
            completed = null;
        } else {
            completed = in.readInt();
        }
        if (in.readByte() == 0) {
            completionRate = null;
        } else {
            completionRate = in.readInt();
        }
        if (in.readByte() == 0) {
            draft = null;
        } else {
            draft = in.readInt();
        }
        if (in.readByte() == 0) {
            openForBids = null;
        } else {
            openForBids = in.readInt();
        }
        if (in.readByte() == 0) {
            overdue = null;
        } else {
            overdue = in.readInt();
        }
        if (in.readByte() == 0) {
            paymentRequested = null;
        } else {
            paymentRequested = in.readInt();
        }
        if (in.readByte() == 0) {
            totalAssigned = null;
        } else {
            totalAssigned = in.readInt();
        }
        if (in.readByte() == 0) {
            totalPosted = null;
        } else {
            totalPosted = in.readInt();
        }
        if (in.readByte() == 0) {
            currentBids = null;
        } else {
            currentBids = in.readInt();
        }
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (assigned == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(assigned);
        }
        if (cancelled == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(cancelled);
        }
        if (closePlusCancelled == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(closePlusCancelled);
        }
        if (completed == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(completed);
        }
        if (completionRate == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(completionRate);
        }
        if (draft == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(draft);
        }
        if (openForBids == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(openForBids);
        }
        if (overdue == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(overdue);
        }
        if (paymentRequested == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(paymentRequested);
        }
        if (totalAssigned == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(totalAssigned);
        }
        if (totalPosted == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(totalPosted);
        }
        if (currentBids == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(currentBids);
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<StatisticsModel> CREATOR = new Creator<StatisticsModel>() {
        @Override
        public StatisticsModel createFromParcel(Parcel in) {
            return new StatisticsModel(in);
        }

        @Override
        public StatisticsModel[] newArray(int size) {
            return new StatisticsModel[size];
        }
    };

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

    public Integer getClosePlusCancelled() {
        return closePlusCancelled;
    }

    public void setClosePlusCancelled(Integer closePlusCancelled) {
        this.closePlusCancelled = closePlusCancelled;
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

    public Integer getPaymentRequested() {
        return paymentRequested;
    }

    public void setPaymentRequested(Integer paymentRequested) {
        this.paymentRequested = paymentRequested;
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

    public Integer getCurrentBids() {
        return currentBids;
    }

    public void setCurrentBids(Integer currentBids) {
        this.currentBids = currentBids;
    }

    public StatisticsModel getJsonToModel(JSONObject jsonObject){
        StatisticsModel statisticsModel = new StatisticsModel();
        try{
            if(jsonObject.has("assigned") && !jsonObject.isNull("assigned"))
                statisticsModel.setAssigned(jsonObject.getInt("assigned"));
            if(jsonObject.has("cancelled") && !jsonObject.isNull("cancelled"))
                statisticsModel.setCancelled(jsonObject.getInt("cancelled"));
            if(jsonObject.has("close_plus_cancelled") && !jsonObject.isNull("close_plus_cancelled"))
                statisticsModel.setClosePlusCancelled(jsonObject.getInt("close_plus_cancelled"));
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
            if(jsonObject.has("payment_requested") && !jsonObject.isNull("payment_requested"))
                statisticsModel.setPaymentRequested(jsonObject.getInt("payment_requested"));
            if(jsonObject.has("total_assigned") && !jsonObject.isNull("total_assigned"))
                statisticsModel.setTotalAssigned(jsonObject.getInt("total_assigned"));
            if(jsonObject.has("total_posted") && !jsonObject.isNull("total_posted"))
                statisticsModel.setTotalPosted(jsonObject.getInt("total_posted"));
            if(jsonObject.has("current_bids") && !jsonObject.isNull("current_bids"))
                statisticsModel.setCurrentBids(jsonObject.getInt("current_bids"));
        }catch (JSONException e){
            Timber.e(e.toString());
            e.printStackTrace();
        }
        return statisticsModel;
    }
}
