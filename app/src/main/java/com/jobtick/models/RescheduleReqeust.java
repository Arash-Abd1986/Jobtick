package com.jobtick.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.json.JSONException;
import org.json.JSONObject;

public class RescheduleReqeust implements Parcelable {
    String TAG = AttachmentModel.class.getName();

    @SerializedName("id")
    @Expose
    private Integer id;

    @SerializedName("task_id")
    @Expose
    private Integer task_id;

    @SerializedName("requester_id")
    @Expose
    private Integer requester_id;

    @SerializedName("reason")
    @Expose
    private String reason;


    @SerializedName("status")
    @Expose
    private String status;

    @SerializedName("old_duedate")
    @Expose
    private String old_duedate;

    @SerializedName("new_duedate")
    @Expose
    private String new_duedate;

    @SerializedName("accepted_at")
    @Expose
    private String accepted_at;


    @SerializedName("auto_accepted_at")
    @Expose
    private String auto_accepted_at;

    @SerializedName("declined_at")
    @Expose
    private String declined_at;

    @SerializedName("declined_reason")
    @Expose
    private String declined_reason;

    @SerializedName("created_at")
    @Expose
    private String created_at;

    protected RescheduleReqeust(Parcel in) {

        id = in.readInt();
        task_id = in.readInt();
        requester_id = in.readInt();
        reason = in.readString();
        status = in.readString();
        old_duedate = in.readString();
        new_duedate = in.readString();
        accepted_at = in.readString();
        auto_accepted_at = in.readString();
        declined_at = in.readString();
        declined_reason = in.readString();
        created_at = in.readString();

    }


    public RescheduleReqeust(Integer id, Integer task_id, Integer requester_id, String reason, String status,
                             String old_duedate, String new_duedate, String accepted_at, String auto_accepted_at,
                             String declined_at, String declined_reason, String created_at) {
        this.id = id;
        this.task_id = task_id;
        this.requester_id = requester_id;
        this.reason = reason;
        this.status = status;
        this.old_duedate = old_duedate;
        this.new_duedate = new_duedate;
        this.accepted_at = accepted_at;
        this.auto_accepted_at = auto_accepted_at;
        this.declined_at = declined_at;
        this.declined_reason = declined_reason;
        this.created_at = created_at;
    }


    public RescheduleReqeust() {

    }


    public static final Creator<RescheduleReqeust> CREATOR = new Creator<RescheduleReqeust>() {
        @Override
        public RescheduleReqeust createFromParcel(Parcel in) {
            return new RescheduleReqeust(in);
        }

        @Override
        public RescheduleReqeust[] newArray(int size) {
            return new RescheduleReqeust[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }


    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (id == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(id);
        }

        if (task_id == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(task_id);
        }


        if (requester_id == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(requester_id);
        }


        dest.writeString(reason);
        dest.writeString(status);
        dest.writeString(old_duedate);
        dest.writeString(new_duedate);
        dest.writeString(accepted_at);
        dest.writeString(auto_accepted_at);
        dest.writeString(declined_at);
        dest.writeString(declined_reason);
        dest.writeString(created_at);
    }


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getTask_id() {
        return task_id;
    }

    public void setTask_id(Integer task_id) {
        this.task_id = task_id;
    }

    public Integer getRequester_id() {
        return requester_id;
    }

    public void setRequester_id(Integer requester_id) {
        this.requester_id = requester_id;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getOld_duedate() {
        return old_duedate;
    }

    public void setOld_duedate(String old_duedate) {
        this.old_duedate = old_duedate;
    }

    public String getNew_duedate() {
        return new_duedate;
    }

    public void setNew_duedate(String new_duedate) {
        this.new_duedate = new_duedate;
    }

    public String getAccepted_at() {
        return accepted_at;
    }

    public void setAccepted_at(String accepted_at) {
        this.accepted_at = accepted_at;
    }

    public String getAuto_accepted_at() {
        return auto_accepted_at;
    }

    public void setAuto_accepted_at(String auto_accepted_at) {
        this.auto_accepted_at = auto_accepted_at;
    }

    public String getDeclined_at() {
        return declined_at;
    }

    public void setDeclined_at(String declined_at) {
        this.declined_at = declined_at;
    }

    public String getDeclined_reason() {
        return declined_reason;
    }

    public void setDeclined_reason(String declined_reason) {
        this.declined_reason = declined_reason;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public RescheduleReqeust getJsonToModel(JSONObject jsonObject) {
        RescheduleReqeust reqeust = new RescheduleReqeust();
        try {
            if (jsonObject.has("id") && !jsonObject.isNull("id"))
                reqeust.setId(jsonObject.getInt("id"));
            if (jsonObject.has("task_id") && !jsonObject.isNull("task_id"))
                reqeust.setTask_id(jsonObject.getInt("task_id"));
            if (jsonObject.has("requester_id"))
                reqeust.setRequester_id(jsonObject.getInt("requester_id"));
            if (jsonObject.has("reason") && !jsonObject.isNull("reason"))
                reqeust.setReason(jsonObject.getString("reason"));
            if (jsonObject.has("status") && !jsonObject.isNull("status"))
                reqeust.setStatus(jsonObject.getString("status"));
            if (jsonObject.has("old_duedate") && !jsonObject.isNull("old_duedate"))
                reqeust.setOld_duedate(jsonObject.getString("old_duedate"));
            if (jsonObject.has("new_duedate") && !jsonObject.isNull("new_duedate"))
                reqeust.setNew_duedate(jsonObject.getString("new_duedate"));
            if (jsonObject.has("accepted_at") && !jsonObject.isNull("accepted_at"))
                reqeust.setAccepted_at(jsonObject.getString("accepted_at"));
            if (jsonObject.has("auto_accepted_at") && !jsonObject.isNull("auto_accepted_at"))
                reqeust.setAuto_accepted_at(jsonObject.getString("auto_accepted_at"));
            if (jsonObject.has("declined_at") && !jsonObject.isNull("declined_at"))
                reqeust.setDeclined_at(jsonObject.getString("declined_at"));
            if (jsonObject.has("declined_reason") && !jsonObject.isNull("declined_reason"))
                reqeust.setDeclined_reason(jsonObject.getString("declined_reason"));
            if (jsonObject.has("created_at") && !jsonObject.isNull("created_at"))
                reqeust.setCreated_at(jsonObject.getString("created_at"));


        } catch (JSONException e) {
            Log.e(TAG, e.toString());
            e.printStackTrace();
        }
        return reqeust;
    }
}
