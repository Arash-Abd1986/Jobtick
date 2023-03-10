package com.jobtick.android.models.payments;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class PaymentHistory implements Serializable {
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("amount_before_fee")
    @Expose
    private String amount_before_fee;
    @SerializedName("amount")
    @Expose
    private String amount;
    @SerializedName("discount")
    @Expose
    private String discount;
    @SerializedName("platform_fee")
    @Expose
    private String platformFee;
    @SerializedName("tax_amount_on_fee")
    @Expose
    private String tax_amount_on_fee;
    @SerializedName("tax_rate")
    @Expose
    private String taxRate;
    @SerializedName("tax_type")
    @Expose
    private String taxType;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("transition")
    @Expose
    private String transition;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("method")
    @Expose
    private List<Method> method = null;
    @SerializedName("task")
    @Expose
    private Task task;

    public String getAmount_before_fee() {
        return amount_before_fee;
    }

    public void setAmount_before_fee(String amount_before_fee) {
        this.amount_before_fee = amount_before_fee;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getAmount() {
        return amount;
    }


    public void setAmount(String amount) {
        this.amount = amount;
    }



    public void setTax_amount_on_fee(String tax_amount_on_fee) {
        this.tax_amount_on_fee = tax_amount_on_fee;
    }

    public String getTax_amount_on_fee() {
        return tax_amount_on_fee;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public String getPlatformFee() {
        return platformFee;
    }

    public void setPlatformFee(String platformFee) {
        this.platformFee = platformFee;
    }

    public String getTaxRate() {
        return taxRate;
    }

    public void setTaxRate(String taxRate) {
        this.taxRate = taxRate;
    }

    public String getTaxType() {
        return taxType;
    }

    public void setTaxType(String taxType) {
        this.taxType = taxType;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTransition() {
        return transition;
    }

    public void setTransition(String transition) {
        this.transition = transition;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public List<Method> getMethod() {
        return method;
    }

    public void setMethod(List<Method> method) {
        this.method = method;
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

}
