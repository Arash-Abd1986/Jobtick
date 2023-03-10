package com.jobtick.android.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.json.JSONException;
import org.json.JSONObject;

import timber.log.Timber;

public class DueTimeModel implements Parcelable {
    String TAG = DueTimeModel.class.getName();
    @SerializedName("anytime")
    @Expose
    private Boolean anytime;
    @SerializedName("evening")
    @Expose
    private Boolean evening;
    @SerializedName("morning")
    @Expose
    private Boolean morning;
    @SerializedName("afternoon")
    @Expose
    private Boolean afternoon;

    /**
     * No args constructor for use in serialization
     *
     */
    public DueTimeModel() {
    }

    /**
     *
     * @param afternoon
     * @param anytime
     * @param evening
     * @param morning
     */
    public DueTimeModel(Boolean anytime, Boolean evening, Boolean morning, Boolean afternoon) {
        super();
        this.anytime = anytime;
        this.evening = evening;
        this.morning = morning;
        this.afternoon = afternoon;
    }

    protected DueTimeModel(Parcel in) {
        byte tmpAnyTime = in.readByte();
        anytime = tmpAnyTime == 0 ? null : tmpAnyTime == 1;
        byte tmpEvening = in.readByte();
        evening = tmpEvening == 0 ? null : tmpEvening == 1;
        byte tmpMorning = in.readByte();
        morning = tmpMorning == 0 ? null : tmpMorning == 1;
        byte tmpAfternoon = in.readByte();
        afternoon = tmpAfternoon == 0 ? null : tmpAfternoon == 1;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte((byte) (anytime == null ? 0 : anytime ? 1 : 2));
        dest.writeByte((byte) (evening == null ? 0 : evening ? 1 : 2));
        dest.writeByte((byte) (morning == null ? 0 : morning ? 1 : 2));
        dest.writeByte((byte) (afternoon == null ? 0 : afternoon ? 1 : 2));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<DueTimeModel> CREATOR = new Creator<DueTimeModel>() {
        @Override
        public DueTimeModel createFromParcel(Parcel in) {
            return new DueTimeModel(in);
        }

        @Override
        public DueTimeModel[] newArray(int size) {
            return new DueTimeModel[size];
        }
    };

    public Boolean getAnytime() {
        return anytime;
    }

    public void setAnytime(Boolean anytime) {
        this.anytime = anytime;
    }

    public Boolean getEvening() {
        return evening;
    }

    public void setEvening(Boolean evening) {
        this.evening = evening;
    }

    public Boolean getMorning() {
        return morning;
    }

    public void setMorning(Boolean morning) {
        this.morning = morning;
    }

    public Boolean getAfternoon() {
        return afternoon;
    }

    public void setAfternoon(Boolean afternoon) {
        this.afternoon = afternoon;
    }

    public DueTimeModel getJsonToModel(JSONObject jsonObject){
        DueTimeModel dueTimeModel = new DueTimeModel();

        try{
            if(jsonObject.has("anytime") && !jsonObject.isNull("anytime")){
                dueTimeModel.setAnytime(jsonObject.getBoolean("anytime"));
            }
            if(jsonObject.has("evening") && !jsonObject.isNull("evening")){
                dueTimeModel.setEvening(jsonObject.getBoolean("evening"));
            }
            if(jsonObject.has("morning") && !jsonObject.isNull("morning")){
                dueTimeModel.setMorning(jsonObject.getBoolean("morning"));
            }
            if(jsonObject.has("afternoon") && !jsonObject.isNull("afternoon")){
                dueTimeModel.setAfternoon(jsonObject.getBoolean("afternoon"));
            }

        }catch (JSONException e){
            Timber.e(e.toString());
            e.printStackTrace();
        }
        return dueTimeModel;
    }

}
