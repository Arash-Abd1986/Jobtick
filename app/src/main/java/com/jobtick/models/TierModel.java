package com.jobtick.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.json.JSONException;
import org.json.JSONObject;

public class TierModel implements Parcelable {
    String TAG = TierModel.class.getName();
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("service_fee")
    @Expose
    private Integer serviceFee;
    @SerializedName("tax")
    @Expose
    private Integer tax;

    /**
     * No args constructor for use in serialization
     *
     */
    public TierModel() {
    }

    public TierModel(Integer id, String name, Integer serviceFee, Integer tax) {
        this.id = id;
        this.name = name;
        this.serviceFee = serviceFee;
        this.tax = tax;
    }

    protected TierModel(Parcel in) {
        if (in.readByte() == 0) {
            id = null;
        } else {
            id = in.readInt();
        }
        name = in.readString();
        if (in.readByte() == 0) {
            serviceFee = null;
        } else {
            serviceFee = in.readInt();
        }
        if (in.readByte() == 0) {
            tax = null;
        } else {
            tax = in.readInt();
        }
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (id == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(id);
        }
        dest.writeString(name);
        if (serviceFee == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(serviceFee);
        }
        if (tax == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(tax);
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<TierModel> CREATOR = new Creator<TierModel>() {
        @Override
        public TierModel createFromParcel(Parcel in) {
            return new TierModel(in);
        }

        @Override
        public TierModel[] newArray(int size) {
            return new TierModel[size];
        }
    };

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getServiceFee() {
        return serviceFee;
    }

    public void setServiceFee(Integer serviceFee) {
        this.serviceFee = serviceFee;
    }

    public Integer getTax() {
        return tax;
    }

    public void setTax(Integer tax) {
        this.tax = tax;
    }

    public TierModel getJsonToModel(JSONObject jsonObject){
        TierModel tierModel = new TierModel();
        try{
            if(jsonObject.has("id") && !jsonObject.isNull("id"))
                tierModel.setId(jsonObject.getInt("id"));
            if(jsonObject.has("name") && !jsonObject.isNull("name"))
                tierModel.setName(jsonObject.getString("name"));
             if(jsonObject.has("service_fee") && !jsonObject.isNull("service_fee"))
                tierModel.setServiceFee(jsonObject.getInt("service_fee"));
             if(jsonObject.has("tax") && !jsonObject.isNull("tax"))
                tierModel.setTax(jsonObject.getInt("tax"));
        }catch (JSONException e){
            Log.e(TAG,e.toString());
            e.printStackTrace();
        }
        return tierModel;
    }
}




