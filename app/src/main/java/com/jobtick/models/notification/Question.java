
package com.jobtick.models.notification;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Question implements Parcelable
{

    @SerializedName("id")
    @Expose
    private Integer id;
    public final static Creator<Question> CREATOR = new Creator<Question>() {


        @SuppressWarnings({
            "unchecked"
        })
        public Question createFromParcel(Parcel in) {
            return new Question(in);
        }

        public Question[] newArray(int size) {
            return (new Question[size]);
        }

    }
    ;

    protected Question(Parcel in) {
        this.id = ((Integer) in.readValue((Integer.class.getClassLoader())));
    }

    /**
     * No args constructor for use in serialization
     * 
     */
    public Question() {
    }

    /**
     * 
     * @param id
     */
    public Question(Integer id) {
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
