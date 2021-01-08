
package com.jobtick.models.receipt;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.jobtick.models.UserAccountModel;

public class Receipt implements Parcelable
{

    @SerializedName("receipt_number")
    @Expose
    private String receiptNumber;
    @SerializedName("receipt_amount")
    @Expose
    private Float receiptAmount;
    @SerializedName("task_cost")
    @Expose
    private Float taskCost;
    @SerializedName("fee")
    @Expose
    private String fee;
    @SerializedName("task_assigned_price")
    @Expose
    private Float taskAssignedPrice;
    @SerializedName("additional_fund")
    @Expose
    private String additionalFund;
    @SerializedName("net_amount")
    @Expose
    private Float netAmount;
    @SerializedName("payment_method")
    @Expose
    private PaymentMethod paymentMethod;
    @SerializedName("user")
    @Expose
    private UserAccountModel user;
    public final static Creator<Receipt> CREATOR = new Creator<Receipt>() {


        @SuppressWarnings({
            "unchecked"
        })
        public Receipt createFromParcel(Parcel in) {
            return new Receipt(in);
        }

        public Receipt[] newArray(int size) {
            return (new Receipt[size]);
        }

    }
    ;

    protected Receipt(Parcel in) {
        this.receiptNumber = ((String) in.readValue((String.class.getClassLoader())));
        this.receiptAmount = ((Float) in.readValue((Float.class.getClassLoader())));
        this.taskCost = ((Float) in.readValue((Float.class.getClassLoader())));
        this.fee = ((String) in.readValue((String.class.getClassLoader())));
        this.taskAssignedPrice = ((Float) in.readValue((Float.class.getClassLoader())));
        this.additionalFund = ((String) in.readValue((String.class.getClassLoader())));
        this.netAmount = ((Float) in.readValue((Float.class.getClassLoader())));
        this.paymentMethod = ((PaymentMethod) in.readValue((PaymentMethod.class.getClassLoader())));
        this.user = ((UserAccountModel) in.readValue((UserAccountModel.class.getClassLoader())));
    }

    /**
     * No args constructor for use in serialization
     * 
     */
    public Receipt() {
    }

    /**
     * 
     * @param additionalFund
     * @param receiptAmount
     * @param taskCost
     * @param netAmount
     * @param fee
     * @param taskAssignedPrice
     * @param paymentMethod
     * @param user
     * @param receiptNumber
     */
    public Receipt(String receiptNumber, Float receiptAmount, Float taskCost, String fee, Float taskAssignedPrice, String additionalFund, Float netAmount, PaymentMethod paymentMethod, UserAccountModel user) {
        super();
        this.receiptNumber = receiptNumber;
        this.receiptAmount = receiptAmount;
        this.taskCost = taskCost;
        this.fee = fee;
        this.taskAssignedPrice = taskAssignedPrice;
        this.additionalFund = additionalFund;
        this.netAmount = netAmount;
        this.paymentMethod = paymentMethod;
        this.user = user;
    }

    public String getReceiptNumber() {
        return receiptNumber;
    }

    public void setReceiptNumber(String receiptNumber) {
        this.receiptNumber = receiptNumber;
    }

    public Float getReceiptAmount() {
        return receiptAmount;
    }

    public void setReceiptAmount(Float receiptAmount) {
        this.receiptAmount = receiptAmount;
    }

    public Float getTaskCost() {
        return taskCost;
    }

    public void setTaskCost(Float taskCost) {
        this.taskCost = taskCost;
    }

    public String getFee() {
        return fee;
    }

    public void setFee(String fee) {
        this.fee = fee;
    }

    public Float getTaskAssignedPrice() {
        return taskAssignedPrice;
    }

    public void setTaskAssignedPrice(Float taskAssignedPrice) {
        this.taskAssignedPrice = taskAssignedPrice;
    }

    public String getAdditionalFund() {
        return additionalFund;
    }

    public void setAdditionalFund(String additionalFund) {
        this.additionalFund = additionalFund;
    }

    public Float getNetAmount() {
        return netAmount;
    }

    public void setNetAmount(Float netAmount) {
        this.netAmount = netAmount;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public UserAccountModel getUser() {
        return user;
    }

    public void setUser(UserAccountModel user) {
        this.user = user;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(receiptNumber);
        dest.writeValue(receiptAmount);
        dest.writeValue(taskCost);
        dest.writeValue(fee);
        dest.writeValue(taskAssignedPrice);
        dest.writeValue(additionalFund);
        dest.writeValue(netAmount);
        dest.writeValue(paymentMethod);
        dest.writeValue(user);
    }

    public int describeContents() {
        return  0;
    }

}
