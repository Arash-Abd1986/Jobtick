
package com.jobtick.android.models.review;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Rater implements Parcelable
{

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("slug")
    @Expose
    private String slug;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("avatar")
    @Expose
    private Object avatar;
    @SerializedName("is_verified_account")
    @Expose
    private Integer isVerifiedAccount;
    @SerializedName("last_online")
    @Expose
    private String lastOnline;
    @SerializedName("position")
    @Expose
    private Position position;
    public final static Creator<Rater> CREATOR = new Creator<Rater>() {


        @SuppressWarnings({
            "unchecked"
        })
        public Rater createFromParcel(Parcel in) {
            return new Rater(in);
        }

        public Rater[] newArray(int size) {
            return (new Rater[size]);
        }

    }
    ;

    protected Rater(Parcel in) {
        this.id = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.slug = ((String) in.readValue((String.class.getClassLoader())));
        this.name = ((String) in.readValue((String.class.getClassLoader())));
        this.avatar = in.readValue((Object.class.getClassLoader()));
        this.isVerifiedAccount = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.lastOnline = ((String) in.readValue((String.class.getClassLoader())));
        this.position = ((Position) in.readValue((Position.class.getClassLoader())));
    }

    public Rater() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Object getAvatar() {
        return avatar;
    }

    public void setAvatar(Object avatar) {
        this.avatar = avatar;
    }

    public Integer getIsVerifiedAccount() {
        return isVerifiedAccount;
    }

    public void setIsVerifiedAccount(Integer isVerifiedAccount) {
        this.isVerifiedAccount = isVerifiedAccount;
    }

    public String getLastOnline() {
        return lastOnline;
    }

    public void setLastOnline(String lastOnline) {
        this.lastOnline = lastOnline;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(id);
        dest.writeValue(slug);
        dest.writeValue(name);
        dest.writeValue(avatar);
        dest.writeValue(isVerifiedAccount);
        dest.writeValue(lastOnline);
        dest.writeValue(position);
    }

    public int describeContents() {
        return  0;
    }

}
