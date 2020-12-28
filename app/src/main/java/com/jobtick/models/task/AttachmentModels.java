package com.jobtick.models.task;

import android.os.Parcel;
import android.os.Parcelable;

import com.jobtick.models.AttachmentModel;

import java.util.ArrayList;
import java.util.List;

public class AttachmentModels implements Parcelable {

    private List<AttachmentModel> attachmentModelList;

    public AttachmentModels(List<AttachmentModel> attachmentModelList) {
        this.attachmentModelList = attachmentModelList;
    }


    protected AttachmentModels(Parcel in) {
        attachmentModelList = in.createTypedArrayList(AttachmentModel.CREATOR);
    }

    public static final Creator<AttachmentModels> CREATOR = new Creator<AttachmentModels>() {
        @Override
        public AttachmentModels createFromParcel(Parcel in) {
            return new AttachmentModels(in);
        }

        @Override
        public AttachmentModels[] newArray(int size) {
            return new AttachmentModels[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeTypedList(attachmentModelList);
    }

    public List<AttachmentModel> getAttachmentModelList() {
        return attachmentModelList;
    }


    public void setAttachmentModelList(List<AttachmentModel> attachmentModelList) {
        this.attachmentModelList = attachmentModelList;
    }
}
