
package com.jobtick.models.cancellation.reason;

import java.util.List;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CancellationModel implements Parcelable
{

    @SerializedName("worker")
    @Expose
    private List<Worker> worker = null;
    @SerializedName("poster")
    @Expose
    private List<Poster> poster = null;
    public final static Creator<CancellationModel> CREATOR = new Creator<CancellationModel>() {


        @SuppressWarnings({
            "unchecked"
        })
        public CancellationModel createFromParcel(Parcel in) {
            return new CancellationModel(in);
        }

        public CancellationModel[] newArray(int size) {
            return (new CancellationModel[size]);
        }

    }
    ;

    protected CancellationModel(Parcel in) {
        in.readList(this.worker, (Worker.class.getClassLoader()));
        in.readList(this.poster, (Poster.class.getClassLoader()));
    }

    /**
     * No args constructor for use in serialization
     * 
     */
    public CancellationModel() {
    }

    /**
     * 
     * @param worker
     * @param poster
     */
    public CancellationModel(List<Worker> worker, List<Poster> poster) {
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
