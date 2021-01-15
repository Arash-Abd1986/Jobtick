package com.jobtick.android.models;

import android.content.Context;

public class NotificationModel {

    public String trigger;
    public String title;
    public String status;


    public NotificationModel(Context context) {
    }

    public NotificationModel() {

    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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
}
