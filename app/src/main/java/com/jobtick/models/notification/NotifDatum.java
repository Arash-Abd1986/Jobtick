
package com.jobtick.models.notification;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.jobtick.models.UserAccountModel;

public class NotifDatum implements Parcelable
{

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("data")
    @Expose
    private Data data;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("user")
    @Expose
    private UserAccountModel userAccountModel;
    public final static Creator<NotifDatum> CREATOR = new Creator<NotifDatum>() {


        @SuppressWarnings({
            "unchecked"
        })
        public NotifDatum createFromParcel(Parcel in) {
            return new NotifDatum(in);
        }

        public NotifDatum[] newArray(int size) {
            return (new NotifDatum[size]);
        }

    }
    ;

    protected NotifDatum(Parcel in) {
        this.id = ((String) in.readValue((String.class.getClassLoader())));
        this.data = ((Data) in.readValue((Data.class.getClassLoader())));
        this.createdAt = ((String) in.readValue((String.class.getClassLoader())));
        this.userAccountModel = ((UserAccountModel) in.readValue((UserAccountModel.class.getClassLoader())));
    }

    /**
     * No args constructor for use in serialization
     * 
     */
    public NotifDatum() {
    }

    /**
     * 
     * @param createdAt
     * @param data
     * @param id
     * @param userAccountModel
     */
    public NotifDatum(String id, Data data, String createdAt, UserAccountModel userAccountModel) {
        super();
        this.id = id;
        this.data = data;
        this.createdAt = createdAt;
        this.userAccountModel = userAccountModel;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public UserAccountModel getUserAccountModel() {
        return userAccountModel;
    }

    public void setUserAccountModel(UserAccountModel userAccountModel) {
        this.userAccountModel = userAccountModel;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(id);
        dest.writeValue(data);
        dest.writeValue(createdAt);
        dest.writeValue(userAccountModel);
    }

    public int describeContents() {
        return  0;
    }

}
