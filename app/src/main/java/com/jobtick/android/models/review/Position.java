
package com.jobtick.android.models.review;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Position implements Parcelable
{

    @SerializedName("latitude")
    @Expose
    private Float latitude;
    @SerializedName("longitude")
    @Expose
    private Float longitude;
    public final static Creator<Position> CREATOR = new Creator<Position>() {


        @SuppressWarnings({
            "unchecked"
        })
        public Position createFromParcel(Parcel in) {
            return new Position(in);
        }

        public Position[] newArray(int size) {
            return (new Position[size]);
        }

    }
    ;

    protected Position(Parcel in) {
        this.latitude = ((Float) in.readValue((Float.class.getClassLoader())));
        this.longitude = ((Float) in.readValue((Float.class.getClassLoader())));
    }

    public Position() {
    }

    public Float getLatitude() {
        return latitude;
    }

    public void setLatitude(Float latitude) {
        this.latitude = latitude;
    }

    public Float getLongitude() {
        return longitude;
    }

    public void setLongitude(Float longitude) {
        this.longitude = longitude;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(latitude);
        dest.writeValue(longitude);
    }

    public int describeContents() {
        return  0;
    }

}
