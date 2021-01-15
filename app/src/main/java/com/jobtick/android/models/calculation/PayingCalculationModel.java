
package com.jobtick.android.models.calculation;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PayingCalculationModel implements Parcelable
{

    @SerializedName("amount")
    @Expose
    private String amount;
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
    @SerializedName("net_paying_amount")
    @Expose
    private Float netPayingAmount;
    @SerializedName("min_service_fee_amount")
    @Expose
    private Integer minServiceFeeAmount;
    @SerializedName("max_service_fee_amount")
    @Expose
    private Integer maxServiceFeeAmount;
    @SerializedName("tax_effect")
    @Expose
    private String taxEffect;
    @SerializedName("level_tier")
    @Expose
    private LevelTier levelTier;
    public final static Parcelable.Creator<PayingCalculationModel> CREATOR = new Creator<PayingCalculationModel>() {


        @SuppressWarnings({
                "unchecked"
        })
        public PayingCalculationModel createFromParcel(Parcel in) {
            return new PayingCalculationModel(in);
        }

        public PayingCalculationModel[] newArray(int size) {
            return (new PayingCalculationModel[size]);
        }

    }
            ;

    protected PayingCalculationModel(Parcel in) {
        this.amount = ((String) in.readValue((String.class.getClassLoader())));
        this.gstRate = ((String) in.readValue((String.class.getClassLoader())));
        this.serviceFeeRate = ((String) in.readValue((String.class.getClassLoader())));
        this.gstAmount = ((Float) in.readValue((Float.class.getClassLoader())));
        this.serviceFee = ((Float) in.readValue((Float.class.getClassLoader())));
        this.netPayingAmount = ((Float) in.readValue((Float.class.getClassLoader())));
        this.minServiceFeeAmount = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.maxServiceFeeAmount = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.taxEffect = ((String) in.readValue((String.class.getClassLoader())));
        this.levelTier = ((LevelTier) in.readValue((LevelTier.class.getClassLoader())));
    }

    /**
     * No args constructor for use in serialization
     *
     */
    public PayingCalculationModel() {
    }

    /**
     *
     * @param gstRate
     * @param serviceFee
     * @param gstAmount
     * @param levelTier
     * @param amount
     * @param netPayingAmount
     * @param minServiceFeeAmount
     * @param taxEffect
     * @param serviceFeeRate
     * @param maxServiceFeeAmount
     */
    public PayingCalculationModel(String amount, String gstRate, String serviceFeeRate, Float gstAmount, Float serviceFee, Float netPayingAmount, Integer minServiceFeeAmount, Integer maxServiceFeeAmount, String taxEffect, LevelTier levelTier) {
        super();
        this.amount = amount;
        this.gstRate = gstRate;
        this.serviceFeeRate = serviceFeeRate;
        this.gstAmount = gstAmount;
        this.serviceFee = serviceFee;
        this.netPayingAmount = netPayingAmount;
        this.minServiceFeeAmount = minServiceFeeAmount;
        this.maxServiceFeeAmount = maxServiceFeeAmount;
        this.taxEffect = taxEffect;
        this.levelTier = levelTier;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
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

    public Float getNetPayingAmount() {
        return netPayingAmount;
    }

    public void setNetPayingAmount(Float netPayingAmount) {
        this.netPayingAmount = netPayingAmount;
    }

    public Integer getMinServiceFeeAmount() {
        return minServiceFeeAmount;
    }

    public void setMinServiceFeeAmount(Integer minServiceFeeAmount) {
        this.minServiceFeeAmount = minServiceFeeAmount;
    }

    public Integer getMaxServiceFeeAmount() {
        return maxServiceFeeAmount;
    }

    public void setMaxServiceFeeAmount(Integer maxServiceFeeAmount) {
        this.maxServiceFeeAmount = maxServiceFeeAmount;
    }

    public String getTaxEffect() {
        return taxEffect;
    }

    public void setTaxEffect(String taxEffect) {
        this.taxEffect = taxEffect;
    }

    public LevelTier getLevelTier() {
        return levelTier;
    }

    public void setLevelTier(LevelTier levelTier) {
        this.levelTier = levelTier;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(amount);
        dest.writeValue(gstRate);
        dest.writeValue(serviceFeeRate);
        dest.writeValue(gstAmount);
        dest.writeValue(serviceFee);
        dest.writeValue(netPayingAmount);
        dest.writeValue(minServiceFeeAmount);
        dest.writeValue(maxServiceFeeAmount);
        dest.writeValue(taxEffect);
        dest.writeValue(levelTier);
    }

    public int describeContents() {
        return  0;
    }

}
