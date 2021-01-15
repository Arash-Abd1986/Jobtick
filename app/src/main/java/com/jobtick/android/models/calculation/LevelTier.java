
package com.jobtick.android.models.calculation;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LevelTier implements Parcelable
{

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("level")
    @Expose
    private Integer level;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("service_fee")
    @Expose
    private Integer serviceFee;
    public final static Creator<LevelTier> CREATOR = new Creator<LevelTier>() {


        @SuppressWarnings({
            "unchecked"
        })
        public LevelTier createFromParcel(Parcel in) {
            return new LevelTier(in);
        }

        public LevelTier[] newArray(int size) {
            return (new LevelTier[size]);
        }
    };

    protected LevelTier(Parcel in) {
        this.id = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.level = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.name = ((String) in.readValue((String.class.getClassLoader())));
        this.serviceFee = ((Integer) in.readValue((Integer.class.getClassLoader())));
    }

    /**
     * No args constructor for use in serialization
     * 
     */
    public LevelTier() {
    }

    /**
     * 
     * @param serviceFee
     * @param level
     * @param name
     * @param id
     */
    public LevelTier(Integer id, Integer level, String name, Integer serviceFee) {
        super();
        this.id = id;
        this.level = level;
        this.name = name;
        this.serviceFee = serviceFee;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
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

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(id);
        dest.writeValue(level);
        dest.writeValue(name);
        dest.writeValue(serviceFee);
    }

    public int describeContents() {
        return  0;
    }

}
