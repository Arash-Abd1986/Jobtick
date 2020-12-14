
package com.jobtick.models.notification.count;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UnreadNotificationModel implements Parcelable
{

    @SerializedName("unread_count")
    @Expose
    private Integer unreadCount;
    @SerializedName("last_unread_notification")
    @Expose
    private LastUnreadNotification lastUnreadNotification;
    @SerializedName("last_notification")
    @Expose
    private LastNotification lastNotification;
    public final static Creator<UnreadNotificationModel> CREATOR = new Creator<UnreadNotificationModel>() {


        @SuppressWarnings({
            "unchecked"
        })
        public UnreadNotificationModel createFromParcel(Parcel in) {
            return new UnreadNotificationModel(in);
        }

        public UnreadNotificationModel[] newArray(int size) {
            return (new UnreadNotificationModel[size]);
        }

    }
    ;

    protected UnreadNotificationModel(Parcel in) {
        this.unreadCount = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.lastUnreadNotification = ((LastUnreadNotification) in.readValue((LastUnreadNotification.class.getClassLoader())));
        this.lastNotification = ((LastNotification) in.readValue((LastNotification.class.getClassLoader())));
    }

    /**
     * No args constructor for use in serialization
     * 
     */
    public UnreadNotificationModel() {
    }

    /**
     * 
     * @param lastNotification
     * @param lastUnreadNotification
     * @param unreadCount
     */
    public UnreadNotificationModel(Integer unreadCount, LastUnreadNotification lastUnreadNotification, LastNotification lastNotification) {
        super();
        this.unreadCount = unreadCount;
        this.lastUnreadNotification = lastUnreadNotification;
        this.lastNotification = lastNotification;
    }

    public Integer getUnreadCount() {
        return unreadCount;
    }

    public void setUnreadCount(Integer unreadCount) {
        this.unreadCount = unreadCount;
    }

    public LastUnreadNotification getLastUnreadNotification() {
        return lastUnreadNotification;
    }

    public void setLastUnreadNotification(LastUnreadNotification lastUnreadNotification) {
        this.lastUnreadNotification = lastUnreadNotification;
    }

    public LastNotification getLastNotification() {
        return lastNotification;
    }

    public void setLastNotification(LastNotification lastNotification) {
        this.lastNotification = lastNotification;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(unreadCount);
        dest.writeValue(lastUnreadNotification);
        dest.writeValue(lastNotification);
    }

    public int describeContents() {
        return  0;
    }

}
