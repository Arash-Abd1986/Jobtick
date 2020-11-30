package com.jobtick.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.jobtick.models.cancellation.reason.ReasonModel;

import org.json.JSONException;
import org.json.JSONObject;

public class CancellationModel implements Parcelable {
    String TAG = CancellationModel.class.getName();
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("requester_id")
    @Expose
    private Integer requesterId;
    @SerializedName("task_id")
    @Expose
    private Integer taskId;
    @SerializedName("reason")
    @Expose
    private String reason;
    @SerializedName("comment")
    @Expose
    private String comment;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("accepted_at")
    @Expose
    private String acceptedAt;
    @SerializedName("auto_accepted_at")
    @Expose
    private String autoAcceptedAt;
    @SerializedName("declined_at")
    @Expose
    private String declinedAt;
    @SerializedName("declined_reason")
    @Expose
    private String declinedReason;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;
    @SerializedName("reason_model")
    @Expose
    private ReasonModel reasonModel;

    /**
     * No args constructor for use in serialization
     */
    public CancellationModel() {
    }

    /**
     * @param reason
     * @param createdAt
     * @param autoAcceptedAt
     * @param requesterId
     * @param declinedReason
     * @param comment
     * @param id
     * @param taskId
     * @param declinedAt
     * @param status
     * @param acceptedAt
     * @param updatedAt
     */
    public CancellationModel(Integer id, Integer requesterId, Integer taskId, String reason, String comment, String status, String acceptedAt, String autoAcceptedAt, String declinedAt, String declinedReason, String createdAt, String updatedAt, ReasonModel reasonModel) {
        super();
        this.id = id;
        this.requesterId = requesterId;
        this.taskId = taskId;
        this.reason = reason;
        this.comment = comment;
        this.status = status;
        this.acceptedAt = acceptedAt;
        this.autoAcceptedAt = autoAcceptedAt;
        this.declinedAt = declinedAt;
        this.declinedReason = declinedReason;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.reasonModel = reasonModel;
    }

    protected CancellationModel(Parcel in) {
        if (in.readByte() == 0) {
            id = null;
        } else {
            id = in.readInt();
        }
        if (in.readByte() == 0) {
            requesterId = null;
        } else {
            requesterId = in.readInt();
        }
        if (in.readByte() == 0) {
            taskId = null;
        } else {
            taskId = in.readInt();
        }
        reason = in.readString();
        comment = in.readString();
        status = in.readString();
        acceptedAt = in.readString();
        autoAcceptedAt = in.readString();
        declinedAt = in.readString();
        declinedReason = in.readString();
        createdAt = in.readString();
        updatedAt = in.readString();
        reasonModel = in.readParcelable(ReasonModel.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (id == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(id);
        }
        if (requesterId == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(requesterId);
        }
        if (taskId == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(taskId);
        }
        dest.writeString(reason);
        dest.writeString(comment);
        dest.writeString(status);
        dest.writeString(acceptedAt);
        dest.writeString(autoAcceptedAt);
        dest.writeString(declinedAt);
        dest.writeString(declinedReason);
        dest.writeString(createdAt);
        dest.writeString(updatedAt);
        dest.writeParcelable(reasonModel, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<CancellationModel> CREATOR = new Creator<CancellationModel>() {
        @Override
        public CancellationModel createFromParcel(Parcel in) {
            return new CancellationModel(in);
        }

        @Override
        public CancellationModel[] newArray(int size) {
            return new CancellationModel[size];
        }
    };

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getRequesterId() {
        return requesterId;
    }

    public void setRequesterId(Integer requesterId) {
        this.requesterId = requesterId;
    }

    public Integer getTaskId() {
        return taskId;
    }

    public void setTaskId(Integer taskId) {
        this.taskId = taskId;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAcceptedAt() {
        return acceptedAt;
    }

    public void setAcceptedAt(String acceptedAt) {
        this.acceptedAt = acceptedAt;
    }

    public String getAutoAcceptedAt() {
        return autoAcceptedAt;
    }

    public void setAutoAcceptedAt(String autoAcceptedAt) {
        this.autoAcceptedAt = autoAcceptedAt;
    }

    public String getDeclinedAt() {
        return declinedAt;
    }

    public void setDeclinedAt(String declinedAt) {
        this.declinedAt = declinedAt;
    }

    public String getDeclinedReason() {
        return declinedReason;
    }

    public void setDeclinedReason(String declinedReason) {
        this.declinedReason = declinedReason;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }


    public ReasonModel getReasonModel() {
        return reasonModel;
    }

    public void setReasonModel(ReasonModel reasonModel) {
        this.reasonModel = reasonModel;
    }


    public CancellationModel getJsonToModel(JSONObject jsonObject) {
        CancellationModel cancellationModel = new CancellationModel();
        try {
            if (jsonObject.has("id") && !jsonObject.isNull("id"))
                cancellationModel.setId(jsonObject.getInt("id"));
            if (jsonObject.has("requester_id") && !jsonObject.isNull("requester_id"))
                cancellationModel.setRequesterId(jsonObject.getInt("requester_id"));
            if (jsonObject.has("task_id") && !jsonObject.isNull("task_id"))
                cancellationModel.setTaskId(jsonObject.getInt("task_id"));
            if (jsonObject.has("reason") && !jsonObject.isNull("reason"))
                cancellationModel.setReason(jsonObject.getString("reason"));
            if (jsonObject.has("comment") && !jsonObject.isNull("comment"))
                cancellationModel.setComment(jsonObject.getString("comment"));
            if (jsonObject.has("status") && !jsonObject.isNull("status"))
                cancellationModel.setStatus(jsonObject.getString("status"));
            if (jsonObject.has("accepted_at") && !jsonObject.isNull("accepted_at"))
                cancellationModel.setAcceptedAt(jsonObject.getString("accepted_at"));
            if (jsonObject.has("auto_accepted_at") && !jsonObject.isNull("auto_accepted_at"))
                cancellationModel.setAutoAcceptedAt(jsonObject.getString("auto_accepted_at"));
            if (jsonObject.has("declined_at") && !jsonObject.isNull("declined_at"))
                cancellationModel.setDeclinedAt(jsonObject.getString("declined_at"));
            if (jsonObject.has("declined_reason") && !jsonObject.isNull("declined_reason"))
                cancellationModel.setDeclinedReason(jsonObject.getString("declined_reason"));
            if (jsonObject.has("created_at") && !jsonObject.isNull("created_at"))
                cancellationModel.setCreatedAt(jsonObject.getString("created_at"));
            if (jsonObject.has("updated_at") && !jsonObject.isNull("updated_at"))
                cancellationModel.setUpdatedAt(jsonObject.getString("updated_at"));
            if (jsonObject.has("reason_model") && !jsonObject.isNull("reason_model"))
                cancellationModel.setReasonModel(new ReasonModel().getJsonToModel(jsonObject.getJSONObject("reason_model")));

        } catch (JSONException e) {
            Log.e(TAG, e.toString());
            e.printStackTrace();
        }
        return cancellationModel;
    }
}
