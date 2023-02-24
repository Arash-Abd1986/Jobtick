package com.jobtick.android.models;

import android.os.Parcel;
import android.os.Parcelable;

public class PortfolioModel implements Parcelable {

    protected PortfolioModel(Parcel in) {
    }

    public static final Creator<PortfolioModel> CREATOR = new Creator<PortfolioModel>() {
        @Override
        public PortfolioModel createFromParcel(Parcel in) {
            return new PortfolioModel(in);
        }

        @Override
        public PortfolioModel[] newArray(int size) {
            return new PortfolioModel[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
    }
}
