package com.jobtick.models.cancellation.notice;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CancellationNoticeModel implements Parcelable
{

    @SerializedName("fee_percentage")
    @Expose
    private String feePercentage;
    @SerializedName("max_fee_amount")
    @Expose
    private String maxFeeAmount;
    @SerializedName("notice")
    @Expose
    private String notice;
    @SerializedName("link")
    @Expose
    private String link;
    public final static Parcelable.Creator<CancellationNoticeModel> CREATOR = new Creator<CancellationNoticeModel>() {


        @SuppressWarnings({
                "unchecked"
        })
        public CancellationNoticeModel createFromParcel(Parcel in) {
            return new CancellationNoticeModel(in);
        }

        public CancellationNoticeModel[] newArray(int size) {
            return (new CancellationNoticeModel[size]);
        }

    }
            ;

    protected CancellationNoticeModel(Parcel in) {
        this.feePercentage = ((String) in.readValue((String.class.getClassLoader())));
        this.maxFeeAmount = ((String) in.readValue((String.class.getClassLoader())));
        this.notice = ((String) in.readValue((String.class.getClassLoader())));
        this.link = ((String) in.readValue((String.class.getClassLoader())));
    }

    /**
     * No args constructor for use in serialization
     *
     */
    public CancellationNoticeModel() {
    }

    /**
     *
     * @param maxFeeAmount
     * @param feePercentage
     * @param link
     * @param notice
     */
    public CancellationNoticeModel(String feePercentage, String maxFeeAmount, String notice, String link) {
        super();
        this.feePercentage = feePercentage;
        this.maxFeeAmount = maxFeeAmount;
        this.notice = notice;
        this.link = link;
    }

    public String getFeePercentage() {
        return feePercentage;
    }

    public void setFeePercentage(String feePercentage) {
        this.feePercentage = feePercentage;
    }

    public String getMaxFeeAmount() {
        return maxFeeAmount;
    }

    public void setMaxFeeAmount(String maxFeeAmount) {
        this.maxFeeAmount = maxFeeAmount;
    }

    public String getNotice() {
        return notice;
    }

    public void setNotice(String notice) {
        this.notice = notice;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(feePercentage);
        dest.writeValue(maxFeeAmount);
        dest.writeValue(notice);
        dest.writeValue(link);
    }

    public int describeContents() {
        return 0;
    }

}
