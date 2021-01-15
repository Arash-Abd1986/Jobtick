package com.jobtick.android.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.json.JSONException;
import org.json.JSONObject;

import timber.log.Timber;


public class AdditionalFundModel implements Parcelable {
    String TAG = AdditionalFundModel.class.getName();
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("amount")
    @Expose
    private Integer amount;
    @SerializedName("requester_id")
    @Expose
    private Integer requesterId;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("rejection_reason")
    @Expose
    private String rejectionReason;
    @SerializedName("creation_reason")
    @Expose
    private String creationReason;

    /**
     * No args constructor for use in serialization
     *
     */
    public AdditionalFundModel() {
    }

    /**
     *
     * @param createdAt
     * @param amount
     * @param requesterId
     * @param id
     * @param rejectionReason
     * @param creationReason
     * @param status
     */
    public AdditionalFundModel(Integer id, Integer amount, Integer requesterId, String status, String createdAt, String rejectionReason, String creationReason) {
        super();
        this.id = id;
        this.amount = amount;
        this.requesterId = requesterId;
        this.status = status;
        this.createdAt = createdAt;
        this.rejectionReason = rejectionReason;
        this.creationReason = creationReason;
    }

    protected AdditionalFundModel(Parcel in) {
        if (in.readByte() == 0) {
            id = null;
        } else {
            id = in.readInt();
        }
        if (in.readByte() == 0) {
            amount = null;
        } else {
            amount = in.readInt();
        }
        if (in.readByte() == 0) {
            requesterId = null;
        } else {
            requesterId = in.readInt();
        }
        status = in.readString();
        createdAt = in.readString();
        rejectionReason = in.readString();
        creationReason = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (id == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(id);
        }
        if (amount == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(amount);
        }
        if (requesterId == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(requesterId);
        }
        dest.writeString(status);
        dest.writeString(createdAt);
        dest.writeString(rejectionReason);
        dest.writeString(creationReason);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<AdditionalFundModel> CREATOR = new Creator<AdditionalFundModel>() {
        @Override
        public AdditionalFundModel createFromParcel(Parcel in) {
            return new AdditionalFundModel(in);
        }

        @Override
        public AdditionalFundModel[] newArray(int size) {
            return new AdditionalFundModel[size];
        }
    };

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public Integer getRequesterId() {
        return requesterId;
    }

    public void setRequesterId(Integer requesterId) {
        this.requesterId = requesterId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getRejectionReason() {
        return rejectionReason;
    }

    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }

    public String getCreationReason() {
        return creationReason;
    }

    public void setCreationReason(String creationReason) {
        this.creationReason = creationReason;
    }

    public AdditionalFundModel getJsonToModel(JSONObject jsonObject){
        AdditionalFundModel additionalFundModel = new AdditionalFundModel();
        try{
            if(jsonObject.has("id") && !jsonObject.isNull("id"))
                additionalFundModel.setId(jsonObject.getInt("id"));
            if(jsonObject.has("amount") && !jsonObject.isNull("amount"))
                additionalFundModel.setAmount(jsonObject.getInt("amount"));
            if(jsonObject.has("requester_id") && !jsonObject.isNull("requester_id"))
                additionalFundModel.setRequesterId(jsonObject.getInt("requester_id"));
            if(jsonObject.has("status") && !jsonObject.isNull("status"))
                additionalFundModel.setStatus(jsonObject.getString("status"));
            if(jsonObject.has("created_at") && !jsonObject.isNull("created_at"))
                additionalFundModel.setCreatedAt(jsonObject.getString("created_at"));
            if(jsonObject.has("rejection_reason") && !jsonObject.isNull("rejection_reason"))
                additionalFundModel.setRejectionReason(jsonObject.getString("rejection_reason"));
            if(jsonObject.has("creation_reason") && !jsonObject.isNull("creation_reason"))
                additionalFundModel.setCreationReason(jsonObject.getString("creation_reason"));

        }catch (JSONException e){
            Timber.e(e.toString());
            e.printStackTrace();
        }
        return additionalFundModel;
    }
}
