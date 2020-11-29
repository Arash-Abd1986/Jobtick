
package com.jobtick.models.cancellation.reason;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Worker implements Parcelable
{

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("reason")
    @Expose
    private String reason;
    @SerializedName("responsible_person_type")
    @Expose
    private String responsiblePersonType;
    @SerializedName("parent_id")
    @Expose
    private Object parentId;

    public Worker(Integer id, String reason, String responsiblePersonType, Object parentId) {
        this.id = id;
        this.reason = reason;
        this.responsiblePersonType = responsiblePersonType;
        this.parentId = parentId;
    }

    protected Worker(Parcel in) {
        if (in.readByte() == 0) {
            id = null;
        } else {
            id = in.readInt();
        }
        reason = in.readString();
        responsiblePersonType = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (id == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(id);
        }
        dest.writeString(reason);
        dest.writeString(responsiblePersonType);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Worker> CREATOR = new Creator<Worker>() {
        @Override
        public Worker createFromParcel(Parcel in) {
            return new Worker(in);
        }

        @Override
        public Worker[] newArray(int size) {
            return new Worker[size];
        }
    };

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getResponsiblePersonType() {
        return responsiblePersonType;
    }

    public void setResponsiblePersonType(String responsiblePersonType) {
        this.responsiblePersonType = responsiblePersonType;
    }

    public Object getParentId() {
        return parentId;
    }

    public void setParentId(Object parentId) {
        this.parentId = parentId;
    }
}
