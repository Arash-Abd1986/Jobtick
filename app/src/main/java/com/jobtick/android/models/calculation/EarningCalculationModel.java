
package com.jobtick.android.models.calculation;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class EarningCalculationModel implements Parcelable {

    @SerializedName("offer_amount")
    @Expose
    private String offerAmount;
    @SerializedName("gst_rate")
    @Expose
    private String gstRate;
    @SerializedName("service_fee_rate")
    @Expose
    private String serviceFeeRate;
    @SerializedName("gst_amount")
    @Expose
    private Float gstAmount;
    @SerializedName("service_fee")
    @Expose
    private Float serviceFee;
    @SerializedName("net_earning")
    @Expose
    private Float netEarning;
    @SerializedName("level_tier")
    @Expose
    private LevelTier levelTier;
    public final static Creator<EarningCalculationModel> CREATOR = new Creator<EarningCalculationModel>() {


        @SuppressWarnings({
            "unchecked"
        })
        public EarningCalculationModel createFromParcel(Parcel in) {
            return new EarningCalculationModel(in);
        }

        public EarningCalculationModel[] newArray(int size) {
            return (new EarningCalculationModel[size]);
        }
    };

    protected EarningCalculationModel(Parcel in) {
        this.offerAmount = ((String) in.readValue((String.class.getClassLoader())));
        this.gstRate = ((String) in.readValue((String.class.getClassLoader())));
        this.serviceFeeRate = ((String) in.readValue((String.class.getClassLoader())));
        this.gstAmount = ((Float) in.readValue((Float.class.getClassLoader())));
        this.serviceFee = ((Float) in.readValue((Float.class.getClassLoader())));
        this.netEarning = ((Float) in.readValue((Float.class.getClassLoader())));
        this.levelTier = ((LevelTier) in.readValue((LevelTier.class.getClassLoader())));
    }

    /**
     * No args constructor for use in serialization
     * 
     */
    public EarningCalculationModel() {
    }

    /**
     * 
     * @param gstRate
     * @param serviceFee
     * @param gstAmount
     * @param levelTier
     * @param serviceFeeRate
     * @param offerAmount
     * @param netEarning
     */
    public EarningCalculationModel(String offerAmount, String gstRate, String serviceFeeRate, Float gstAmount, Float serviceFee, Float netEarning, LevelTier levelTier) {
        super();
        this.offerAmount = offerAmount;
        this.gstRate = gstRate;
        this.serviceFeeRate = serviceFeeRate;
        this.gstAmount = gstAmount;
        this.serviceFee = serviceFee;
        this.netEarning = netEarning;
        this.levelTier = levelTier;
    }

    public String getOfferAmount() {
        return offerAmount;
    }

    public void setOfferAmount(String offerAmount) {
        this.offerAmount = offerAmount;
    }

    public String getGstRate() {
        return gstRate;
    }

    public void setGstRate(String gstRate) {
        this.gstRate = gstRate;
    }

    public String getServiceFeeRate() {
        return serviceFeeRate;
    }

    public void setServiceFeeRate(String serviceFeeRate) {
        this.serviceFeeRate = serviceFeeRate;
    }

    public Float getGstAmount() {
        return gstAmount;
    }

    public void setGstAmount(Float gstAmount) {
        this.gstAmount = gstAmount;
    }

    public Float getServiceFee() {
        return serviceFee;
    }

    public void setServiceFee(Float serviceFee) {
        this.serviceFee = serviceFee;
    }

    public Float getNetEarning() {
        return netEarning;
    }

    public void setNetEarning(Float netEarning) {
        this.netEarning = netEarning;
    }

    public LevelTier getLevelTier() {
        return levelTier;
    }

    public void setLevelTier(LevelTier levelTier) {
        this.levelTier = levelTier;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(offerAmount);
        dest.writeValue(gstRate);
        dest.writeValue(serviceFeeRate);
        dest.writeValue(gstAmount);
        dest.writeValue(serviceFee);
        dest.writeValue(netEarning);
        dest.writeValue(levelTier);
    }

    public int describeContents() {
        return  0;
    }

}
