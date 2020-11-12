
package com.jobtick.models.notification;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class User implements Parcelable
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
    private Avatar avatar;
    @SerializedName("is_verified_account")
    @Expose
    private Integer isVerifiedAccount;
    @SerializedName("last_online")
    @Expose
    private String lastOnline;
    @SerializedName("position")
    @Expose
    private Position position;
    public final static Creator<User> CREATOR = new Creator<User>() {


        @SuppressWarnings({
            "unchecked"
        })
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        public User[] newArray(int size) {
            return (new User[size]);
        }

    }
    ;

    protected User(Parcel in) {
        this.id = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.slug = ((String) in.readValue((String.class.getClassLoader())));
        this.name = ((String) in.readValue((String.class.getClassLoader())));
        this.avatar = ((Avatar) in.readValue((Avatar.class.getClassLoader())));
        this.isVerifiedAccount = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.lastOnline = ((String) in.readValue((String.class.getClassLoader())));
        this.position = ((Position) in.readValue((Position.class.getClassLoader())));
    }

    /**
     * No args constructor for use in serialization
     * 
     */
    public User() {
    }

    /**
     * 
     * @param isVerifiedAccount
     * @param name
     * @param lastOnline
     * @param id
     * @param avatar
     * @param position
     * @param slug
     */
    public User(Integer id, String slug, String name, Avatar avatar, Integer isVerifiedAccount, String lastOnline, Position position) {
        super();
        this.id = id;
        this.slug = slug;
        this.name = name;
        this.avatar = avatar;
        this.isVerifiedAccount = isVerifiedAccount;
        this.lastOnline = lastOnline;
        this.position = position;
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

    public Avatar getAvatar() {
        return avatar;
    }

    public void setAvatar(Avatar avatar) {
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
