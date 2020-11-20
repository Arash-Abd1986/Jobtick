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


    protected BreakdownModel(Parcel in) {
        if (in.readByte() == 0) {
            _1 = null;
        } else {
            _1 = in.readInt();
        }
        if (in.readByte() == 0) {
            _2 = null;
        } else {
            _2 = in.readInt();
        }
        if (in.readByte() == 0) {
            _3 = null;
        } else {
            _3 = in.readInt();
        }
        if (in.readByte() == 0) {
            _4 = null;
        } else {
            _4 = in.readInt();
        }
        if (in.readByte() == 0) {
            _5 = null;
        } else {
            _5 = in.readInt();
        }

    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (_1 == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(_1);
        }
        if (_2 == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(_2);
        }
        if (_3 == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(_3);
        }
        if (_4 == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(_4);
        }
        if (_5 == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(_5);
        }

    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<RatingModel> CREATOR = new Creator<RatingModel>() {
        @Override
        public RatingModel createFromParcel(Parcel in) {
            return new RatingModel(in);
        }

        @Override
        public RatingModel[] newArray(int size) {
            return new RatingModel[size];
        }
    };

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




    public RatingModel getJsonToModel(JSONObject jsonObject){
        RatingModel ratingModel = new RatingModel();
        try {
            if (jsonObject.has("rating_breakdown") && !jsonObject.isNull("rating_breakdown")) {
                JSONObject jsonObject_rating_breakdown = jsonObject.getJSONObject("rating_breakdown");
                if(jsonObject_rating_breakdown.has("1") && !jsonObject_rating_breakdown.has("1"))
                    ratingModel.set1(jsonObject_rating_breakdown.getInt("1"));
                if(jsonObject_rating_breakdown.has("2") && !jsonObject_rating_breakdown.has("2"))
                    ratingModel.set2(jsonObject_rating_breakdown.getInt("2"));
                if(jsonObject_rating_breakdown.has("3") && !jsonObject_rating_breakdown.has("3"))
                    ratingModel.set3(jsonObject_rating_breakdown.getInt("3"));
                if(jsonObject_rating_breakdown.has("4") && !jsonObject_rating_breakdown.has("4"))
                    ratingModel.set4(jsonObject_rating_breakdown.getInt("4"));
                if(jsonObject_rating_breakdown.has("5") && !jsonObject_rating_breakdown.has("5"))
                    ratingModel.set5(jsonObject_rating_breakdown.getInt("5"));
            }

        }catch (JSONException e){
            Log.e(TAG,e.toString());
            e.printStackTrace();
        }
        return ratingModel;
    }
}
