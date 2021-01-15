
package com.jobtick.android.models.cancellation.reason;

import java.util.List;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CancellationReasonModel implements Parcelable
{

    @SerializedName("worker")
    @Expose
    private List<Worker> worker = null;
    @SerializedName("poster")
    @Expose
    private List<Poster> poster = null;
    public final static Creator<CancellationReasonModel> CREATOR = new Creator<CancellationReasonModel>() {


        @SuppressWarnings({
            "unchecked"
        })
        public CancellationReasonModel createFromParcel(Parcel in) {
            return new CancellationReasonModel(in);
        }

        public CancellationReasonModel[] newArray(int size) {
            return (new CancellationReasonModel[size]);
        }

    }
    ;

    protected CancellationReasonModel(Parcel in) {
        in.readList(this.worker, (Worker.class.getClassLoader()));
        in.readList(this.poster, (Poster.class.getClassLoader()));
    }

    /**
     * No args constructor for use in serialization
     * 
     */
    public CancellationReasonModel() {
    }

    /**
     * 
     * @param worker
     * @param poster
     */
    public CancellationReasonModel(List<Worker> worker, List<Poster> poster) {
        super();
        this.worker = worker;
        this.poster = poster;
    }

    public List<Worker> getWorker() {
        return worker;
    }

    public void setWorker(List<Worker> worker) {
        this.worker = worker;
    }

    public List<Poster> getPoster() {
        return poster;
    }

    public void setPoster(List<Poster> poster) {
        this.poster = poster;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeList(worker);
        dest.writeList(poster);
    }

    public int describeContents() {
        return  0;
    }

}
