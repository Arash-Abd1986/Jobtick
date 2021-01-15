
package com.jobtick.android.models.receipt;

import java.util.ArrayList;
import java.util.List;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.json.JSONArray;
import org.json.JSONObject;

public class Invoice implements Parcelable
{

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("invoice_number")
    @Expose
    private String invoiceNumber;
    @SerializedName("abn")
    @Expose
    private String abn;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("items")
    @Expose
    private List<Item> items = null;
    public final static Creator<Invoice> CREATOR = new Creator<Invoice>() {


        @SuppressWarnings({
            "unchecked"
        })
        public Invoice createFromParcel(Parcel in) {
            return new Invoice(in);
        }

        public Invoice[] newArray(int size) {
            return (new Invoice[size]);
        }

    }
    ;

    protected Invoice(Parcel in) {
        this.id = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.invoiceNumber = ((String) in.readValue((String.class.getClassLoader())));
        this.abn = ((String) in.readValue((String.class.getClassLoader())));
        this.status = ((String) in.readValue((String.class.getClassLoader())));
        this.createdAt = ((String) in.readValue((String.class.getClassLoader())));
        in.readList(this.items, (com.jobtick.android.models.receipt.Item.class.getClassLoader()));
    }

    /**
     * No args constructor for use in serialization
     * 
     */
    public Invoice() {
    }

    /**
     * 
     * @param createdAt
     * @param invoiceNumber
     * @param id
     * @param abn
     * @param items
     * @param status
     */
    public Invoice(Integer id, String invoiceNumber, String abn, String status, String createdAt, List<Item> items) {
        super();
        this.id = id;
        this.invoiceNumber = invoiceNumber;
        this.abn = abn;
        this.status = status;
        this.createdAt = createdAt;
        this.items = items;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public String getAbn() {
        return abn;
    }

    public void setAbn(String abn) {
        this.abn = abn;
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

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(id);
        dest.writeValue(invoiceNumber);
        dest.writeValue(abn);
        dest.writeValue(status);
        dest.writeValue(createdAt);
        dest.writeList(items);
    }

    public int describeContents() {
        return  0;
    }

    public Invoice getJsonToModel(JSONObject jsonObject) {
        Invoice invoice = new Invoice();
        try {

            if (jsonObject.has("id") && !jsonObject.isNull("id"))
                invoice.setId(jsonObject.getInt("id"));
            if (jsonObject.has("invoice_number") && !jsonObject.isNull("invoice_number"))
                invoice.setInvoiceNumber(jsonObject.getString("invoice_number"));
            if (jsonObject.has("abn") && !jsonObject.isNull("abn"))
                invoice.setAbn(jsonObject.getString("abn"));
            if (jsonObject.has("status") && !jsonObject.isNull("status"))
                invoice.setStatus(jsonObject.getString("status"));
            if (jsonObject.has("created_at") && !jsonObject.isNull("created_at"))
                invoice.setCreatedAt(jsonObject.getString("created_at"));

            if (jsonObject.has("items") && !jsonObject.isNull("items")) {
                JSONArray jsonArray = jsonObject.getJSONArray("items");
                ArrayList<Item> items = new ArrayList<>();
                for (int i = 0; jsonArray.length() > i; i++) {
                    JSONObject jsonObject_offers = jsonArray.getJSONObject(i);
                    items.add(new Item().getJsonToModel(jsonObject_offers));
                }
                invoice.setItems(items);
            }

        }catch(Exception e){
            e.printStackTrace();
        }
        return invoice;
    }

}
