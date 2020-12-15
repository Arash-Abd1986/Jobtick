package com.jobtick.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.json.JSONException;
import org.json.JSONObject;

import timber.log.Timber;

public class PositionModel implements Parcelable {
    String TAG = PositionModel.class.getName();
    @SerializedName("latitude")
    @Expose
    private Double latitude = 0.0;
    @SerializedName("longitude")
    @Expose
    private Double longitude = 0.0;

    /**
     * No args constructor for use in serialization
     *
     */
    public PositionModel() {
    }

    /**
     *
     * @param latitude
     * @param longitude
     */
    public PositionModel(Double latitude, Double longitude) {
        super();
        this.latitude = latitude;
        this.longitude = longitude;
    }


    protected PositionModel(Parcel in) {
        if (in.readByte() == 0) {
            latitude = null;
        } else {
            latitude = in.readDouble();
        }
        if (in.readByte() == 0) {
            longitude = null;
        } else {
            longitude = in.readDouble();
        }
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (latitude == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeDouble(latitude);
        }
        if (longitude == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeDouble(longitude);
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<PositionModel> CREATOR = new Creator<PositionModel>() {
        @Override
        public PositionModel createFromParcel(Parcel in) {
            return new PositionModel(in);
        }

        @Override
        public PositionModel[] newArray(int size) {
            return new PositionModel[size];
        }
    };

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public PositionModel getJsonToModel(JSONObject jsonObject){
        PositionModel positionModel = new PositionModel();
        try{
            if(jsonObject.has("latitude") && !jsonObject.isNull("latitude"))
                positionModel.setLatitude(jsonObject.getDouble("latitude"));
            if(jsonObject.has("longitude") && !jsonObject.isNull("longitude"))
                positionModel.setLongitude(jsonObject.getDouble("longitude"));

        }catch (JSONException e){
            Timber.e(e.toString());
            e.printStackTrace();
        }
        return positionModel;
    }

}
