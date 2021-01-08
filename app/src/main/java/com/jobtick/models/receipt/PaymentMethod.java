
package com.jobtick.models.receipt;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PaymentMethod implements Parcelable
{

    @SerializedName("brand")
    @Expose
    private String brand;
    @SerializedName("last4")
    @Expose
    private String last4;
    @SerializedName("exp_month")
    @Expose
    private Integer expMonth;
    @SerializedName("exp_year")
    @Expose
    private Integer expYear;
    public final static Creator<PaymentMethod> CREATOR = new Creator<PaymentMethod>() {


        @SuppressWarnings({
            "unchecked"
        })
        public PaymentMethod createFromParcel(Parcel in) {
            return new PaymentMethod(in);
        }

        public PaymentMethod[] newArray(int size) {
            return (new PaymentMethod[size]);
        }

    }
    ;

    protected PaymentMethod(Parcel in) {
        this.brand = ((String) in.readValue((String.class.getClassLoader())));
        this.last4 = ((String) in.readValue((String.class.getClassLoader())));
        this.expMonth = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.expYear = ((Integer) in.readValue((Integer.class.getClassLoader())));
    }

    /**
     * No args constructor for use in serialization
     * 
     */
    public PaymentMethod() {
    }

    /**
     * 
     * @param last4
     * @param expMonth
     * @param expYear
     * @param brand
     */
    public PaymentMethod(String brand, String last4, Integer expMonth, Integer expYear) {
        super();
        this.brand = brand;
        this.last4 = last4;
        this.expMonth = expMonth;
        this.expYear = expYear;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getLast4() {
        return last4;
    }

    public void setLast4(String last4) {
        this.last4 = last4;
    }

    public Integer getExpMonth() {
        return expMonth;
    }

    public void setExpMonth(Integer expMonth) {
        this.expMonth = expMonth;
    }

    public Integer getExpYear() {
        return expYear;
    }

    public void setExpYear(Integer expYear) {
        this.expYear = expYear;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(brand);
        dest.writeValue(last4);
        dest.writeValue(expMonth);
        dest.writeValue(expYear);
    }

    public int describeContents() {
        return  0;
    }

}
