package com.jobtick.android.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.jobtick.android.utils.TimeAgo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

public class QuestionModel implements Parcelable {
    String TAG = QuestionModel.class.getName();
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("question_text")
    @Expose
    private String questionText;
    @SerializedName("user")
    @Expose
    private UserAccountModel user;
    @SerializedName("task_id")
    @Expose
    private Integer taskId;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("attachments")
    @Expose
    private List<AttachmentModel> attachments = new ArrayList<>();
    @SerializedName("comments_count")
    @Expose
    private Integer commentsCount = 3;
    @SerializedName("comments")
    @Expose
    private ArrayList<CommentModel> comments = new ArrayList<>();
    private Boolean reply = false;
    private Boolean isUserPrefrenceToMore = false;
    private String strMore = "More";
    /**
     * No args constructor for use in serialization
     *
     */
    public QuestionModel() {
    }

    public QuestionModel(Integer id, String questionText, UserAccountModel user, Integer taskId, String createdAt, List<AttachmentModel> attachments, Integer commentsCount, ArrayList<CommentModel> comments, Boolean reply, Boolean isUserPrefrenceToMore, String strMore) {
        this.id = id;
        this.questionText = questionText;
        this.user = user;
        this.taskId = taskId;
        this.createdAt = createdAt;
        this.attachments = attachments;
        this.commentsCount = commentsCount;
        this.comments = comments;
        this.reply = reply;
        this.isUserPrefrenceToMore = isUserPrefrenceToMore;
        this.strMore = strMore;
    }

    protected QuestionModel(Parcel in) {
        if (in.readByte() == 0) {
            id = null;
        } else {
            id = in.readInt();
        }
        questionText = in.readString();
        user = in.readParcelable(UserAccountModel.class.getClassLoader());
        if (in.readByte() == 0) {
            taskId = null;
        } else {
            taskId = in.readInt();
        }
        createdAt = in.readString();
        attachments = in.createTypedArrayList(AttachmentModel.CREATOR);
        if (in.readByte() == 0) {
            commentsCount = null;
        } else {
            commentsCount = in.readInt();
        }
        comments = in.createTypedArrayList(CommentModel.CREATOR);
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
        dest.writeString(questionText);
        dest.writeParcelable(user, flags);
        if (taskId == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(taskId);
        }
        dest.writeString(createdAt);
        dest.writeTypedList(attachments);
        if (commentsCount == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(commentsCount);
        }
        dest.writeTypedList(comments);
        dest.writeByte((byte) (reply == null ? 0 : reply ? 1 : 2));
        dest.writeByte((byte) (isUserPrefrenceToMore == null ? 0 : isUserPrefrenceToMore ? 1 : 2));
        dest.writeString(strMore);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<QuestionModel> CREATOR = new Creator<QuestionModel>() {
        @Override
        public QuestionModel createFromParcel(Parcel in) {
            return new QuestionModel(in);
        }

        @Override
        public QuestionModel[] newArray(int size) {
            return new QuestionModel[size];
        }
    };

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public UserAccountModel getUser() {
        return user;
    }

    public void setUser(UserAccountModel user) {
        this.user = user;
    }

    public Integer getTaskId() {
        return taskId;
    }

    public void setTaskId(Integer taskId) {
        this.taskId = taskId;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public List<AttachmentModel> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<AttachmentModel> attachments) {
        this.attachments = attachments;
    }

    public ArrayList<CommentModel> getComments() {
        return comments;
    }

    public void setComments(ArrayList<CommentModel> comments) {
        this.comments = comments;
    }

    public Integer getCommentsCount() {
        return commentsCount;
    }

    public void setCommentsCount(Integer commentsCount) {
        this.commentsCount = commentsCount;
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

    public QuestionModel getJsonToModel(JSONObject jsonObject){
        QuestionModel questionModel = new QuestionModel();
        try{
            if(jsonObject.has("id") && !jsonObject.isNull("id"))
                questionModel.setId(jsonObject.getInt("id"));
            if(jsonObject.has("question_text") && !jsonObject.isNull("question_text"))
                questionModel.setQuestionText(jsonObject.getString("question_text"));
            if(jsonObject.has("user") && !jsonObject.isNull("user"))
                questionModel.setUser(new UserAccountModel().getJsonToModel(jsonObject.getJSONObject("user")));
            if(jsonObject.has("task_id") && !jsonObject.isNull("task_id"))
                questionModel.setTaskId(jsonObject.getInt("task_id"));
            if(jsonObject.has("created_at") && !jsonObject.isNull("created_at"))
                questionModel.setCreatedAt(TimeAgo.getTimeAgo(jsonObject.getString("created_at")));
            if(jsonObject.has("attachments") && !jsonObject.isNull("attachments")) {
                List<AttachmentModel> attachmentArrayList = new ArrayList<>();
                JSONArray jsonArray_attachments = jsonObject.getJSONArray("attachments");
                for(int i=0; jsonArray_attachments.length() > i; i++){
                    JSONObject jsonObject_attachment = jsonArray_attachments.getJSONObject(i);
                    AttachmentModel attachment = new AttachmentModel().getJsonToModel(jsonObject_attachment);
                    attachmentArrayList.add(attachment);
                }
                questionModel.setAttachments(attachmentArrayList);
            }
            if (jsonObject.has("comments_count") && !jsonObject.isNull("comments_count"))
                questionModel.setCommentsCount(jsonObject.getInt("comments_count"));
            if (jsonObject.has("comments") && !jsonObject.isNull("comments")) {
                JSONArray jsonArray = jsonObject.getJSONArray("comments");
                ArrayList<CommentModel> commentModelList = new ArrayList<>();
                if (jsonArray.length() != 0) {
                    for (int i = 0; jsonArray.length() > i; i++) {
                        JSONObject jsonObject_comments = jsonArray.getJSONObject(i);
                        CommentModel commentModel = new CommentModel().getJsonToModel(jsonObject_comments);
                        if (jsonArray.length() - 1 == i) {
                            commentModel.setReply(true);
                        }
                        commentModelList.add(commentModel);
                    }
                } else {
                    questionModel.setReply(true);
                }
                questionModel.setComments(commentModelList);
            }
        }catch (JSONException e){
            Timber.e(e.toString());
            e.printStackTrace();
        }
        return questionModel;
    }
}
