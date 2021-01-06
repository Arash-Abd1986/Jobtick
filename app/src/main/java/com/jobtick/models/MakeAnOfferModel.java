package com.jobtick.models;

import android.os.Parcel;
import android.os.Parcelable;

public class MakeAnOfferModel  implements Parcelable {
    private int task_id;
    private int offer_price;
    private String offer_price_type = "fixed";
    private String message;
    private boolean checkbok = true;
    private AttachmentModel attachment;

    private float allFee;
    private float youWillReceive;

    public MakeAnOfferModel() {
    }

    public MakeAnOfferModel(int task_id, int offer_price, String offer_price_type, String message, boolean checkbok, AttachmentModel attachment) {
        this.task_id = task_id;
        this.offer_price = offer_price;
        this.offer_price_type = offer_price_type;
        this.message = message;
        this.checkbok = checkbok;
        this.attachment = attachment;
    }

    protected MakeAnOfferModel(Parcel in) {
        task_id = in.readInt();
        offer_price = in.readInt();
        offer_price_type = in.readString();
        message = in.readString();
        checkbok = in.readByte() != 0;
        allFee = in.readFloat();
        youWillReceive = in.readFloat();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(task_id);
        dest.writeInt(offer_price);
        dest.writeString(offer_price_type);
        dest.writeString(message);
        dest.writeByte((byte) (checkbok ? 1 : 0));
        dest.writeFloat(allFee);
        dest.writeFloat(youWillReceive);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<MakeAnOfferModel> CREATOR = new Creator<MakeAnOfferModel>() {
        @Override
        public MakeAnOfferModel createFromParcel(Parcel in) {
            return new MakeAnOfferModel(in);
        }

        @Override
        public MakeAnOfferModel[] newArray(int size) {
            return new MakeAnOfferModel[size];
        }
    };

    public boolean isCheckbok() {
        return checkbok;
    }

    public void setCheckbok(boolean checkbok) {
        this.checkbok = checkbok;
    }

    public int getTask_id() {
        return task_id;
    }

    public void setTask_id(int task_id) {
        this.task_id = task_id;
    }

    public int getOffer_price() {
        return offer_price;
    }

    public void setOffer_price(int offer_price) {
        this.offer_price = offer_price;
    }

    public String getOffer_price_type() {
        return offer_price_type;
    }

    public void setOffer_price_type(String offer_price_type) {
        this.offer_price_type = offer_price_type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public AttachmentModel getAttachment() {
        return attachment;
    }

    public void setAttachment(AttachmentModel attachment) {
        this.attachment = attachment;
    }

    public float getAllFee() {
        return allFee;
    }

    public void setAllFee(float allFee) {
        this.allFee = allFee;
    }

    public float getYouWillReceive() {
        return youWillReceive;
    }

    public void setYouWillReceive(float youWillReceive) {
        this.youWillReceive = youWillReceive;
    }
}
