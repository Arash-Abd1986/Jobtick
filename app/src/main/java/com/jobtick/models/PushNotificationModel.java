package com.jobtick.models;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

public class PushNotificationModel implements Parcelable {


    public String trigger;
    public String model_slug;
    public int model_id;
    public int offer_id;
    public int question_id;
    public int conversation_id;
    public String title;
    public String status;

    public PushNotificationModel(Parcel in) {
        trigger = in.readString();
        model_slug = in.readString();
        model_id = in.readInt();
        offer_id = in.readInt();
        question_id = in.readInt();
        conversation_id = in.readInt();
        title = in.readString();
        status=in.readString();
    }

    public PushNotificationModel() {

    }

    public PushNotificationModel(Context context) {
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(trigger);
        dest.writeString(model_slug);
        dest.writeInt(model_id);
        dest.writeInt(offer_id);
        dest.writeInt(question_id);
        dest.writeInt(conversation_id);
        dest.writeString(title);
        dest.writeString(status);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<PushNotificationModel> CREATOR = new Creator<PushNotificationModel>() {
        @Override
        public PushNotificationModel createFromParcel(Parcel in) {
            return new PushNotificationModel(in);
        }

        @Override
        public PushNotificationModel[] newArray(int size) {
            return new PushNotificationModel[size];
        }
    };

    public String getTrigger() {
        return trigger;
    }

    public void setTrigger(String trigger) {
        this.trigger = trigger;
    }

    public String getModel_slug() {
        return model_slug;
    }

    public void setModel_slug(String model_slug) {
        this.model_slug = model_slug;
    }

    public int getModel_id() {
        return model_id;
    }

    public void setModel_id(int model_id) {
        this.model_id = model_id;
    }

    public int getOffer_id() {
        return offer_id;
    }

    public void setOffer_id(int offer_id) {
        this.offer_id = offer_id;
    }

    public int getQuestion_id() {
        return question_id;
    }

    public void setQuestion_id(int question_id) {
        this.question_id = question_id;
    }

    public int getConversation_id() {
        return conversation_id;
    }

    public void setConversation_id(int conversation_id) {
        this.conversation_id = conversation_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
