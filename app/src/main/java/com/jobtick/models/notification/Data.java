
package com.jobtick.models.notification;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Data implements Parcelable
{

    @SerializedName("trigger")
    @Expose
    private String trigger;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("user_id")
    @Expose
    private Integer userId;
    @SerializedName("task_id")
    @Expose
    private Integer taskId;
    @SerializedName("task_slug")
    @Expose
    private String taskSlug;
    @SerializedName("task_status")
    @Expose
    private String taskStatus;
    @SerializedName("offer")
    @Expose
    private Offer offer;
    public final static Creator<Data> CREATOR = new Creator<Data>() {


        @SuppressWarnings({
            "unchecked"
        })
        public Data createFromParcel(Parcel in) {
            return new Data(in);
        }

        public Data[] newArray(int size) {
            return (new Data[size]);
        }

    }
    ;

    protected Data(Parcel in) {
        this.trigger = ((String) in.readValue((String.class.getClassLoader())));
        this.title = ((String) in.readValue((String.class.getClassLoader())));
        this.userId = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.taskId = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.taskSlug = ((String) in.readValue((String.class.getClassLoader())));
        this.taskStatus = ((String) in.readValue((String.class.getClassLoader())));
        this.offer = ((Offer) in.readValue((Offer.class.getClassLoader())));
    }

    public Data() {
    }

    public String getTrigger() {
        return trigger;
    }

    public void setTrigger(String trigger) {
        this.trigger = trigger;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getTaskId() {
        return taskId;
    }

    public void setTaskId(Integer taskId) {
        this.taskId = taskId;
    }

    public String getTaskSlug() {
        return taskSlug;
    }

    public void setTaskSlug(String taskSlug) {
        this.taskSlug = taskSlug;
    }

    public String getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(String taskStatus) {
        this.taskStatus = taskStatus;
    }

    public Offer getOffer() {
        return offer;
    }

    public void setOffer(Offer offer) {
        this.offer = offer;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(trigger);
        dest.writeValue(title);
        dest.writeValue(userId);
        dest.writeValue(taskId);
        dest.writeValue(taskSlug);
        dest.writeValue(taskStatus);
        dest.writeValue(offer);
    }

    public int describeContents() {
        return  0;
    }

}
