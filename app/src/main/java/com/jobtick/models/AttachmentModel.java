package com.jobtick.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.jobtick.adapers.AttachmentAdapter;
import com.jobtick.utils.Tools;

import org.json.JSONException;
import org.json.JSONObject;

import timber.log.Timber;

public class AttachmentModel implements Parcelable{
    String TAG = AttachmentModel.class.getName();
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
    private int drawable = 0;

    private Integer type = AttachmentAdapter.VIEW_TYPE_ADD;
    /**
     * No args constructor for use in serialization
     *
     */
    public AttachmentModel() {
    }

    public AttachmentModel(Integer id, String name, String fileName, String mime, String url,
                           String thumbUrl, String modalUrl, String createdAt, int drawable, Integer type) {
        this.id = id;
        this.name = name;
        this.fileName = fileName;
        this.mime = mime;
        this.url = url;
        this.thumbUrl = thumbUrl;
        this.modalUrl = modalUrl;
        this.createdAt = createdAt;
        this.drawable = drawable;
        this.type = type;
    }

    protected AttachmentModel(Parcel in) {
        if (in.readByte() == 0) {
            id = null;
        } else {
            id = in.readInt();
        }
        name = in.readString();
        fileName = in.readString();
        mime = in.readString();
        url = in.readString();
        thumbUrl = in.readString();
        modalUrl = in.readString();
        createdAt = in.readString();
        drawable = in.readInt();
        if (in.readByte() == 0) {
            type = null;
        } else {
            type = in.readInt();
        }
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (id == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(id);
        }
        dest.writeString(name);
        dest.writeString(fileName);
        dest.writeString(mime);
        dest.writeString(url);
        dest.writeString(thumbUrl);
        dest.writeString(modalUrl);
        dest.writeString(createdAt);
        dest.writeInt(drawable);
        if (type == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(type);
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<AttachmentModel> CREATOR = new Creator<AttachmentModel>() {
        @Override
        public AttachmentModel createFromParcel(Parcel in) {
            return new AttachmentModel(in);
        }

        @Override
        public AttachmentModel[] newArray(int size) {
            return new AttachmentModel[size];
        }
    };

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

    public int getDrawable() {
        return drawable;
    }

    public void setDrawable(int drawable) {
        this.drawable = drawable;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public AttachmentModel getJsonToModel(JSONObject jsonObject){
        AttachmentModel attachment = new AttachmentModel();
        try{
            if(jsonObject.has("id") && !jsonObject.isNull("id"))
                attachment.setId(jsonObject.getInt("id"));
            if(jsonObject.has("name") && !jsonObject.isNull("name"))
                attachment.setName(jsonObject.getString("name"));
            if(jsonObject.has("file_name") && !jsonObject.isNull("file_name"))
                attachment.setFileName(jsonObject.getString("file_name"));
            if(jsonObject.has("mime") && !jsonObject.isNull("mime"))
                attachment.setMime(jsonObject.getString("mime"));
            if(jsonObject.has("url") && !jsonObject.isNull("url"))
                attachment.setUrl(jsonObject.getString("url"));
            if(jsonObject.has("thumb_url") && !jsonObject.isNull("thumb_url"))
                attachment.setThumbUrl(jsonObject.getString("thumb_url"));
            if(jsonObject.has("modal_url") && !jsonObject.isNull("modal_url"))
                attachment.setModalUrl(jsonObject.getString("modal_url"));
            if(jsonObject.has("created_at") && !jsonObject.isNull("created_at"))
                attachment.setCreatedAt(jsonObject.getString("created_at"));
            attachment.setType(AttachmentAdapter.VIEW_TYPE_IMAGE);
        }catch (JSONException e){
            Timber.e(e.toString());
            e.printStackTrace();
        }
        return attachment;
    }
}
