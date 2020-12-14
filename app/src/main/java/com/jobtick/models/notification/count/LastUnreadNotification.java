
package com.jobtick.models.notification.count;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LastUnreadNotification implements Parcelable
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
    private User user;
    public final static Creator<LastUnreadNotification> CREATOR = new Creator<LastUnreadNotification>() {


        @SuppressWarnings({
            "unchecked"
        })
        public LastUnreadNotification createFromParcel(Parcel in) {
            return new LastUnreadNotification(in);
        }

        public LastUnreadNotification[] newArray(int size) {
            return (new LastUnreadNotification[size]);
        }

    }
    ;

    protected LastUnreadNotification(Parcel in) {
        this.id = ((String) in.readValue((String.class.getClassLoader())));
        this.data = ((Data) in.readValue((Data.class.getClassLoader())));
        this.createdAt = ((String) in.readValue((String.class.getClassLoader())));
        this.user = ((User) in.readValue((User.class.getClassLoader())));
    }

    /**
     * No args constructor for use in serialization
     * 
     */
    public LastUnreadNotification() {
    }

    /**
     * 
     * @param createdAt
     * @param data
     * @param id
     * @param user
     */
    public LastUnreadNotification(String id, Data data, String createdAt, User user) {
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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
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
