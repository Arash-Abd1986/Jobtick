
package com.jobtick.android.models.notification;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Links implements Parcelable
{

    @SerializedName("first")
    @Expose
    private String first;
    @SerializedName("last")
    @Expose
    private String last;
    @SerializedName("prev")
    @Expose
    private Object prev;
    @SerializedName("next")
    @Expose
    private String next;
    public final static Creator<Links> CREATOR = new Creator<Links>() {


        @SuppressWarnings({
            "unchecked"
        })
        public Links createFromParcel(Parcel in) {
            return new Links(in);
        }

        public Links[] newArray(int size) {
            return (new Links[size]);
        }

    }
    ;

    protected Links(Parcel in) {
        this.first = ((String) in.readValue((String.class.getClassLoader())));
        this.last = ((String) in.readValue((String.class.getClassLoader())));
        this.prev = in.readValue((Object.class.getClassLoader()));
        this.next = ((String) in.readValue((String.class.getClassLoader())));
    }

    /**
     * No args constructor for use in serialization
     * 
     */
    public Links() {
    }

    /**
     * 
     * @param next
     * @param last
     * @param prev
     * @param first
     */
    public Links(String first, String last, Object prev, String next) {
        super();
        this.first = first;
        this.last = last;
        this.prev = prev;
        this.next = next;
    }

    public String getFirst() {
        return first;
    }

    public void setFirst(String first) {
        this.first = first;
    }

    public String getLast() {
        return last;
    }

    public void setLast(String last) {
        this.last = last;
    }

    public Object getPrev() {
        return prev;
    }

    public void setPrev(Object prev) {
        this.prev = prev;
    }

    public String getNext() {
        return next;
    }

    public void setNext(String next) {
        this.next = next;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(first);
        dest.writeValue(last);
        dest.writeValue(prev);
        dest.writeValue(next);
    }

    public int describeContents() {
        return  0;
    }

}
