
package com.jobtick.models.receipt;

import java.util.List;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

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
        in.readList(this.items, (com.jobtick.models.receipt.Item.class.getClassLoader()));
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

}
