package com.jobtick.android.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BadgesDetails  implements Parcelable {



    @SerializedName("icon")
    @Expose
    private String icon;

    @SerializedName("name")
    @Expose
    private String name;

    public BadgesDetails() {
    }

    protected BadgesDetails(Parcel in) {
        icon = in.readString();
        name = in.readString();
    }

    public static final Creator<BadgesDetails> CREATOR = new Creator<BadgesDetails>() {
        @Override
        public BadgesDetails createFromParcel(Parcel in) {
            return new BadgesDetails(in);
        }

        @Override
        public BadgesDetails[] newArray(int size) {
            return new BadgesDetails[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(icon);
        dest.writeString(name);
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BadgesDetails(String icon, String name) {
        this.icon = icon;
        this.name = name;
    }
}
