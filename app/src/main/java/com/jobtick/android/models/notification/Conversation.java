
package com.jobtick.android.models.notification;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Conversation implements Parcelable
{

    @SerializedName("id")
    @Expose
    private Integer id;
    public final static Creator<Conversation> CREATOR = new Creator<Conversation>() {


        @SuppressWarnings({
            "unchecked"
        })
        public Conversation createFromParcel(Parcel in) {
            return new Conversation(in);
        }

        public Conversation[] newArray(int size) {
            return (new Conversation[size]);
        }

    }
    ;

    protected Conversation(Parcel in) {
        this.id = ((Integer) in.readValue((Integer.class.getClassLoader())));
    }

    /**
     * No args constructor for use in serialization
     * 
     */
    public Conversation() {
    }

    /**
     * 
     * @param id
     */
    public Conversation(Integer id) {
        super();
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(id);
    }

    public int describeContents() {
        return  0;
    }

}
