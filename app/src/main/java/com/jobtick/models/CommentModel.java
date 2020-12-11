package com.jobtick.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.jobtick.utils.TimeAgo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

public class CommentModel implements Parcelable{
    String TAG = CommentModel.class.getName();
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("user")
    @Expose
    private UserAccountModel user;
    @SerializedName("comment_text")
    @Expose
    private String commentText;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;
    @SerializedName("attachments")
    @Expose
    private List<AttachmentModel> attachments = new ArrayList<>();
    private Boolean reply = false;
    private Boolean isUserPrefrenceToMore = null;
    private String strMore = "More";

    /**
     * No args constructor for use in serialization
     *
     */
    public CommentModel() {
    }

    /**
     *
     * @param createdAt
     * @param attachments
     * @param isUserPrefrenceToMore
     * @param id
     * @param reply
     * @param user
     * @param commentText
     * @param updatedAt
     * @param strMore
     */
    public CommentModel(Integer id, UserAccountModel user, String commentText, String createdAt, String updatedAt, List<AttachmentModel> attachments, Boolean reply, Boolean isUserPrefrenceToMore, String strMore) {
        super();
        this.id = id;
        this.user = user;
        this.commentText = commentText;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.attachments = attachments;
        this.reply = reply;
        this.isUserPrefrenceToMore = isUserPrefrenceToMore;
        this.strMore = strMore;
    }


    protected CommentModel(Parcel in) {
        if (in.readByte() == 0) {
            id = null;
        } else {
            id = in.readInt();
        }
        user = in.readParcelable(UserAccountModel.class.getClassLoader());
        commentText = in.readString();
        createdAt = in.readString();
        updatedAt = in.readString();
        attachments = in.createTypedArrayList(AttachmentModel.CREATOR);
        byte tmpReply = in.readByte();
        reply = tmpReply == 0 ? null : tmpReply == 1;
        byte tmpIsUserPrefrenceToMore = in.readByte();
        isUserPrefrenceToMore = tmpIsUserPrefrenceToMore == 0 ? null : tmpIsUserPrefrenceToMore == 1;
        strMore = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (id == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(id);
        }
        dest.writeParcelable(user, flags);
        dest.writeString(commentText);
        dest.writeString(createdAt);
        dest.writeString(updatedAt);
        dest.writeTypedList(attachments);
        dest.writeByte((byte) (reply == null ? 0 : reply ? 1 : 2));
        dest.writeByte((byte) (isUserPrefrenceToMore == null ? 0 : isUserPrefrenceToMore ? 1 : 2));
        dest.writeString(strMore);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<CommentModel> CREATOR = new Creator<CommentModel>() {
        @Override
        public CommentModel createFromParcel(Parcel in) {
            return new CommentModel(in);
        }

        @Override
        public CommentModel[] newArray(int size) {
            return new CommentModel[size];
        }
    };

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public UserAccountModel getUser() {
        return user;
    }

    public void setUser(UserAccountModel user) {
        this.user = user;
    }

    public String getCommentText() {
        return commentText;
    }

    public void setCommentText(String commentText) {
        this.commentText = commentText;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public List<AttachmentModel> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<AttachmentModel> attachments) {
        this.attachments = attachments;
    }

    public Boolean getReply() {
        return reply;
    }

    public void setReply(Boolean reply) {
        this.reply = reply;
    }

    public Boolean getIsUserPrefrenceToMore() {
        return isUserPrefrenceToMore;
    }

    public void setIsUserPrefrenceToMore(Boolean isUserPrefrenceToMore) {
        this.isUserPrefrenceToMore = isUserPrefrenceToMore;
    }

    public String getStrMore() {
        return strMore;
    }

    public void setStrMore(String strMore) {
        this.strMore = strMore;
    }


    public CommentModel getJsonToModel(JSONObject jsonObject){
        CommentModel commentModel = new CommentModel();
        try{
            if(jsonObject.has("id") && !jsonObject.isNull("id"))
                commentModel.setId(jsonObject.getInt("id"));
            if(jsonObject.has("user") && !jsonObject.isNull("user"))
                commentModel.setUser(new UserAccountModel().getJsonToModel(jsonObject.getJSONObject("user")));
            if(jsonObject.has("comment_text") && !jsonObject.isNull("comment_text"))
                commentModel.setCommentText(jsonObject.getString("comment_text"));
            if(jsonObject.has("created_at") && !jsonObject.isNull("created_at"))
                commentModel.setCreatedAt(TimeAgo.getTimeAgo(jsonObject.getString("created_at")));
            if(jsonObject.has("updated_at") && !jsonObject.isNull("updated_at"))
                commentModel.setUpdatedAt(TimeAgo.getTimeAgo(jsonObject.getString("updated_at")));
            if(jsonObject.has("attachments") && !jsonObject.isNull("attachments")) {
                JSONArray jsonArray = jsonObject.getJSONArray("attachments");
                List<AttachmentModel> attachmentList = new ArrayList<>();
                for(int i=0; jsonArray.length() > i ; i++) {
                    JSONObject jsonObject_attachments = jsonArray.getJSONObject(i);
                    attachmentList.add(new AttachmentModel().getJsonToModel(jsonObject_attachments));
                }
                commentModel.setAttachments(attachmentList);
            }
        }catch (JSONException e){
            Timber.e(e.toString());
            e.printStackTrace();
        }
        return commentModel;
    }
}
