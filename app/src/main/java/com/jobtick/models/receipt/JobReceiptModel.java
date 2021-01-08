
package com.jobtick.models.receipt;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.jobtick.models.TaskModel;

public class JobReceiptModel implements Parcelable
{

    @SerializedName("invoice")
    @Expose
    private Invoice invoice;
    @SerializedName("receipt")
    @Expose
    private Receipt receipt;
    @SerializedName("task")
    @Expose
    private TaskModel task;
    public final static Creator<JobReceiptModel> CREATOR = new Creator<JobReceiptModel>() {


        @SuppressWarnings({
            "unchecked"
        })
        public JobReceiptModel createFromParcel(Parcel in) {
            return new JobReceiptModel(in);
        }

        public JobReceiptModel[] newArray(int size) {
            return (new JobReceiptModel[size]);
        }

    }
    ;

    protected JobReceiptModel(Parcel in) {
        this.invoice = ((Invoice) in.readValue((Invoice.class.getClassLoader())));
        this.receipt = ((Receipt) in.readValue((Receipt.class.getClassLoader())));
        this.task = ((TaskModel) in.readValue((TaskModel.class.getClassLoader())));
    }

    /**
     * No args constructor for use in serialization
     * 
     */
    public JobReceiptModel() {
    }

    /**
     * 
     * @param task
     * @param receipt
     * @param invoice
     */
    public JobReceiptModel(Invoice invoice, Receipt receipt, TaskModel task) {
        super();
        this.invoice = invoice;
        this.receipt = receipt;
        this.task = task;
    }

    public Invoice getInvoice() {
        return invoice;
    }

    public void setInvoice(Invoice invoice) {
        this.invoice = invoice;
    }

    public Receipt getReceipt() {
        return receipt;
    }

    public void setReceipt(Receipt receipt) {
        this.receipt = receipt;
    }

    public TaskModel getTask() {
        return task;
    }

    public void setTask(TaskModel task) {
        this.task = task;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(invoice);
        dest.writeValue(receipt);
        dest.writeValue(task);
    }

    public int describeContents() {
        return  0;
    }

}
