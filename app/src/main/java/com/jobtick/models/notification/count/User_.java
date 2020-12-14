
package com.jobtick.models.notification.count;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class User_ implements Parcelable
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
    private Avatar_ avatar;
    @SerializedName("is_verified_account")
    @Expose
    private Integer isVerifiedAccount;
    @SerializedName("last_online")
    @Expose
    private String lastOnline;
    @SerializedName("position")
    @Expose
    private Position_ position;
    public final static Creator<User_> CREATOR = new Creator<User_>() {


        @SuppressWarnings({
            "unchecked"
        })
        public User_ createFromParcel(Parcel in) {
            return new User_(in);
        }

        public User_[] newArray(int size) {
            return (new User_[size]);
        }

    }
    ;

    protected User_(Parcel in) {
        this.id = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.slug = ((String) in.readValue((String.class.getClassLoader())));
        this.name = ((String) in.readValue((String.class.getClassLoader())));
        this.avatar = ((Avatar_) in.readValue((Avatar_.class.getClassLoader())));
        this.isVerifiedAccount = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.lastOnline = ((String) in.readValue((String.class.getClassLoader())));
        this.position = ((Position_) in.readValue((Position_.class.getClassLoader())));
    }

    /**
     * No args constructor for use in serialization
     * 
     */
    public User_() {
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
    public User_(Integer id, String slug, String name, Avatar_ avatar, Integer isVerifiedAccount, String lastOnline, Position_ position) {
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

    public Avatar_ getAvatar() {
        return avatar;
    }

    public void setAvatar(Avatar_ avatar) {
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

    public Position_ getPosition() {
        return position;
    }

    public void setPosition(Position_ position) {
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
