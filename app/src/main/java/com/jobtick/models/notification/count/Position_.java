
package com.jobtick.models.notification.count;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Position_ implements Parcelable
{

    @SerializedName("latitude")
    @Expose
    private Float latitude;
    @SerializedName("longitude")
    @Expose
    private Float longitude;
    public final static Creator<Position_> CREATOR = new Creator<Position_>() {


        @SuppressWarnings({
            "unchecked"
        })
        public Position_ createFromParcel(Parcel in) {
            return new Position_(in);
        }

        public Position_[] newArray(int size) {
            return (new Position_[size]);
        }

    }
    ;

    protected Position_(Parcel in) {
        this.latitude = ((Float) in.readValue((Float.class.getClassLoader())));
        this.longitude = ((Float) in.readValue((Float.class.getClassLoader())));
    }

    /**
     * No args constructor for use in serialization
     * 
     */
    public Position_() {
    }

    /**
     * 
     * @param latitude
     * @param longitude
     */
    public Position_(Float latitude, Float longitude) {
        super();
        this.latitude = latitude;
        this.longitude = longitude;
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
