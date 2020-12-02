package com.jobtick.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.json.JSONException;
import org.json.JSONObject;

public class BreakdownModel implements Parcelable {
    String TAG = RatingModel.class.getName();

    @SerializedName("1")
    @Expose
    private Integer _1;
    @SerializedName("2")
    @Expose
    private Integer _2;
    @SerializedName("3")
    @Expose
    private Integer _3;
    @SerializedName("4")
    @Expose
    private Integer _4;
    @SerializedName("5")
    @Expose
    private Integer _5;
    public final static Parcelable.Creator<BreakdownModel> CREATOR = new Creator<BreakdownModel>() {


        @SuppressWarnings({
                "unchecked"
        })
        public BreakdownModel createFromParcel(Parcel in) {
            return new BreakdownModel(in);
        }

        public BreakdownModel[] newArray(int size) {
            return (new BreakdownModel[size]);
        }

    }
            ;

    protected BreakdownModel(Parcel in) {
        this._1 = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this._2 = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this._3 = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this._4 = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this._5 = ((Integer) in.readValue((Integer.class.getClassLoader())));
    }

    /**
     * No args constructor for use in serialization
     *
     */
    public BreakdownModel() {
    }

    /**
     *
     * @param _1
     * @param _2
     * @param _3
     * @param _4
     * @param _5
     */
    public BreakdownModel(Integer _1, Integer _2, Integer _3, Integer _4, Integer _5) {
        super();
        this._1 = _1;
        this._2 = _2;
        this._3 = _3;
        this._4 = _4;
        this._5 = _5;
    }

    public Integer get1() {
        return _1;
    }

    public void set1(Integer _1) {
        this._1 = _1;
    }

    public Integer get2() {
        return _2;
    }

    public void set2(Integer _2) {
        this._2 = _2;
    }

    public Integer get3() {
        return _3;
    }

    public void set3(Integer _3) {
        this._3 = _3;
    }

    public Integer get4() {
        return _4;
    }

    public void set4(Integer _4) {
        this._4 = _4;
    }

    public Integer get5() {
        return _5;
    }

    public void set5(Integer _5) {
        this._5 = _5;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(_1);
        dest.writeValue(_2);
        dest.writeValue(_3);
        dest.writeValue(_4);
        dest.writeValue(_5);
    }

    public int describeContents() {
        return  0;
    }

}
