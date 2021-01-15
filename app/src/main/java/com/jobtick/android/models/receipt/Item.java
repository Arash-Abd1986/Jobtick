
package com.jobtick.android.models.receipt;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.json.JSONObject;

public class Item implements Parcelable
{

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("item_name")
    @Expose
    private String itemName;
    @SerializedName("amount")
    @Expose
    private String amount;
    @SerializedName("tax_amount")
    @Expose
    private String taxAmount;
    @SerializedName("final_amount")
    @Expose
    private String finalAmount;
    @SerializedName("tax_effect")
    @Expose
    private String taxEffect;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    public final static Creator<Item> CREATOR = new Creator<Item>() {


        @SuppressWarnings({
            "unchecked"
        })
        public Item createFromParcel(Parcel in) {
            return new Item(in);
        }

        public Item[] newArray(int size) {
            return (new Item[size]);
        }

    }
    ;

    protected Item(Parcel in) {
        this.id = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.itemName = ((String) in.readValue((String.class.getClassLoader())));
        this.amount = ((String) in.readValue((String.class.getClassLoader())));
        this.taxAmount = ((String) in.readValue((String.class.getClassLoader())));
        this.finalAmount = ((String) in.readValue((String.class.getClassLoader())));
        this.taxEffect = ((String) in.readValue((String.class.getClassLoader())));
        this.createdAt = ((String) in.readValue((String.class.getClassLoader())));
    }

    /**
     * No args constructor for use in serialization
     *
     */
    public Item() {
    }

    /**
     *
     * @param createdAt
     * @param itemName
     * @param amount
     * @param finalAmount
     * @param taxEffect
     * @param id
     * @param taxAmount
     */
    public Item(Integer id, String itemName, String amount, String taxAmount, String finalAmount, String taxEffect, String createdAt) {
        super();
        this.id = id;
        this.itemName = itemName;
        this.amount = amount;
        this.taxAmount = taxAmount;
        this.finalAmount = finalAmount;
        this.taxEffect = taxEffect;
        this.createdAt = createdAt;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getTaxAmount() {
        return taxAmount;
    }

    public void setTaxAmount(String taxAmount) {
        this.taxAmount = taxAmount;
    }

    public String getFinalAmount() {
        return finalAmount;
    }

    public void setFinalAmount(String finalAmount) {
        this.finalAmount = finalAmount;
    }

    public String getTaxEffect() {
        return taxEffect;
    }

    public void setTaxEffect(String taxEffect) {
        this.taxEffect = taxEffect;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(id);
        dest.writeValue(itemName);
        dest.writeValue(amount);
        dest.writeValue(taxAmount);
        dest.writeValue(finalAmount);
        dest.writeValue(taxEffect);
        dest.writeValue(createdAt);
    }

    public int describeContents() {
        return  0;
    }

    public Item getJsonToModel(JSONObject jsonObject) {
        Item item = new Item();
        try {
            if (jsonObject.has("id") && !jsonObject.isNull("id"))
                item.setId(jsonObject.getInt("id"));

            if (jsonObject.has("item_name") && !jsonObject.isNull("item_name"))
                item.setItemName(jsonObject.getString("item_name"));
            if (jsonObject.has("amount") && !jsonObject.isNull("amount"))
                item.setAmount(jsonObject.getString("amount"));
            if (jsonObject.has("tax_amount") && !jsonObject.isNull("tax_amount"))
                item.setTaxAmount(jsonObject.getString("tax_amount"));
            if (jsonObject.has("final_amount") && !jsonObject.isNull("final_amount"))
                item.setFinalAmount(jsonObject.getString("final_amount"));
            if (jsonObject.has("tax_effect") && !jsonObject.isNull("tax_effect"))
                item.setTaxEffect(jsonObject.getString("tax_effect"));
            if (jsonObject.has("created_at") && !jsonObject.isNull("created_at"))
                item.setCreatedAt(jsonObject.getString("created_at"));

        }catch(Exception e){
            e.printStackTrace();
        }
        return item;
    }
}
