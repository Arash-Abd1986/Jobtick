
package com.jobtick.models.notification.count;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Avatar_ implements Parcelable
{

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("file_name")
    @Expose
    private String fileName;
    @SerializedName("mime")
    @Expose
    private String mime;
    @SerializedName("url")
    @Expose
    private String url;
    @SerializedName("thumb_url")
    @Expose
    private String thumbUrl;
    @SerializedName("modal_url")
    @Expose
    private String modalUrl;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    public final static Creator<Avatar_> CREATOR = new Creator<Avatar_>() {


        @SuppressWarnings({
            "unchecked"
        })
        public Avatar_ createFromParcel(Parcel in) {
            return new Avatar_(in);
        }

        public Avatar_[] newArray(int size) {
            return (new Avatar_[size]);
        }

    }
    ;

    protected Avatar_(Parcel in) {
        this.id = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.name = ((String) in.readValue((String.class.getClassLoader())));
        this.fileName = ((String) in.readValue((String.class.getClassLoader())));
        this.mime = ((String) in.readValue((String.class.getClassLoader())));
        this.url = ((String) in.readValue((String.class.getClassLoader())));
        this.thumbUrl = ((String) in.readValue((String.class.getClassLoader())));
        this.modalUrl = ((String) in.readValue((String.class.getClassLoader())));
        this.createdAt = ((String) in.readValue((String.class.getClassLoader())));
    }

    /**
     * No args constructor for use in serialization
     * 
     */
    public Avatar_() {
    }

    /**
     * 
     * @param createdAt
     * @param fileName
     * @param modalUrl
     * @param mime
     * @param name
     * @param id
     * @param thumbUrl
     * @param url
     */
    public Avatar_(Integer id, String name, String fileName, String mime, String url, String thumbUrl, String modalUrl, String createdAt) {
        super();
        this.id = id;
        this.name = name;
        this.fileName = fileName;
        this.mime = mime;
        this.url = url;
        this.thumbUrl = thumbUrl;
        this.modalUrl = modalUrl;
        this.createdAt = createdAt;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getMime() {
        return mime;
    }

    public void setMime(String mime) {
        this.mime = mime;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getThumbUrl() {
        return thumbUrl;
    }

    public void setThumbUrl(String thumbUrl) {
        this.thumbUrl = thumbUrl;
    }

    public String getModalUrl() {
        return modalUrl;
    }

    public void setModalUrl(String modalUrl) {
        this.modalUrl = modalUrl;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(id);
        dest.writeValue(name);
        dest.writeValue(fileName);
        dest.writeValue(mime);
        dest.writeValue(url);
        dest.writeValue(thumbUrl);
        dest.writeValue(modalUrl);
        dest.writeValue(createdAt);
    }

    public int describeContents() {
        return  0;
    }

}
