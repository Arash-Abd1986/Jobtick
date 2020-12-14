
package com.jobtick.models.notification.count;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LastNotification implements Parcelable
{

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("data")
    @Expose
    private Data_ data;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("user")
    @Expose
    private User_ user;
    public final static Creator<LastNotification> CREATOR = new Creator<LastNotification>() {


        @SuppressWarnings({
            "unchecked"
        })
        public LastNotification createFromParcel(Parcel in) {
            return new LastNotification(in);
        }

        public LastNotification[] newArray(int size) {
            return (new LastNotification[size]);
        }

    }
    ;

    protected LastNotification(Parcel in) {
        this.id = ((String) in.readValue((String.class.getClassLoader())));
        this.data = ((Data_) in.readValue((Data_.class.getClassLoader())));
        this.createdAt = ((String) in.readValue((String.class.getClassLoader())));
        this.user = ((User_) in.readValue((User_.class.getClassLoader())));
    }

    /**
     * No args constructor for use in serialization
     * 
     */
    public LastNotification() {
    }

    /**
     * 
     * @param createdAt
     * @param data
     * @param id
     * @param user
     */
    public LastNotification(String id, Data_ data, String createdAt, User_ user) {
        super();
        this.id = id;
        this.data = data;
        this.createdAt = createdAt;
        this.user = user;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Data_ getData() {
        return data;
    }

    public void setData(Data_ data) {
        this.data = data;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public User_ getUser() {
        return user;
    }

    public void setUser(User_ user) {
        this.user = user;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(id);
        dest.writeValue(data);
        dest.writeValue(createdAt);
        dest.writeValue(user);
    }

    public int describeContents() {
        return  0;
    }

}
