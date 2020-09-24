package com.jobtick.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.jobtick.utils.Tools;

import org.json.JSONException;
import org.json.JSONObject;

public class ChatModel implements Parcelable {
    String TAG = ChatModel.class.getName();
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("conversation_id")
    @Expose
    private Integer conversationId;
    @SerializedName("sender_id")
    @Expose
    private Integer senderId;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("is_seen")
    @Expose
    private Integer isSeen;
    @SerializedName("attachment")
    @Expose
    private AttachmentModel attachment;
    @SerializedName("created_at")
    @Expose
    private String createdAt;

    /**
     * No args constructor for use in serialization
     */
    public ChatModel() {
    }

    public ChatModel(Integer id, Integer conversationId, Integer senderId, String message, Integer isSeen, AttachmentModel attachment, String createdAt) {
        this.id = id;
        this.conversationId = conversationId;
        this.senderId = senderId;
        this.message = message;
        this.isSeen = isSeen;
        this.attachment = attachment;
        this.createdAt = createdAt;
    }

    protected ChatModel(Parcel in) {
        if (in.readByte() == 0) {
            id = null;
        } else {
            id = in.readInt();
        }
        if (in.readByte() == 0) {
            conversationId = null;
        } else {
            conversationId = in.readInt();
        }
        if (in.readByte() == 0) {
            senderId = null;
        } else {
            senderId = in.readInt();
        }
        message = in.readString();
        if (in.readByte() == 0) {
            isSeen = null;
        } else {
            isSeen = in.readInt();
        }
        attachment = in.readParcelable(AttachmentModel.class.getClassLoader());
        createdAt = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (id == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(id);
        }
        if (conversationId == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(conversationId);
        }
        if (senderId == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(senderId);
        }
        dest.writeString(message);
        if (isSeen == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(isSeen);
        }
        dest.writeParcelable(attachment, flags);
        dest.writeString(createdAt);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ChatModel> CREATOR = new Creator<ChatModel>() {
        @Override
        public ChatModel createFromParcel(Parcel in) {
            return new ChatModel(in);
        }

        @Override
        public ChatModel[] newArray(int size) {
            return new ChatModel[size];
        }
    };

    public Integer getConversationId() {
        return conversationId;
    }

    public void setConversationId(Integer conversationId) {
        this.conversationId = conversationId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }


    public Integer getSenderId() {
        return senderId;
    }

    public void setSenderId(Integer senderId) {
        this.senderId = senderId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getIsSeen() {
        return isSeen;
    }

    public void setIsSeen(Integer isSeen) {
        this.isSeen = isSeen;
    }

    public AttachmentModel getAttachment() {
        return attachment;
    }

    public void setAttachment(AttachmentModel attachment) {
        this.attachment = attachment;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public ChatModel getJsonToModel(JSONObject jsonObject) {
        ChatModel messageModel = new ChatModel();
        try {
            if (jsonObject.has("id") && !jsonObject.isNull("id"))
                messageModel.setId(jsonObject.getInt("id"));
            if (jsonObject.has("conversation_id") && !jsonObject.isNull("conversation_id"))
                messageModel.setConversationId(jsonObject.getInt("conversation_id"));
            if (jsonObject.has("sender_id") && !jsonObject.isNull("sender_id"))
                messageModel.setSenderId(jsonObject.getInt("sender_id"));
            if (jsonObject.has("message") && !jsonObject.isNull("message"))
                messageModel.setMessage(jsonObject.getString("message"));
            if (jsonObject.has("is_seen") && !jsonObject.isNull("is_seen"))
                messageModel.setIsSeen(jsonObject.getInt("is_seen"));
            if (jsonObject.has("attachment") && !jsonObject.isNull("attachment"))
                messageModel.setAttachment(new AttachmentModel().getJsonToModel(jsonObject.getJSONObject("attachment")));
            if (jsonObject.has("created_at") && !jsonObject.isNull("created_at"))
                messageModel.setCreatedAt(Tools.getGlobalDateTimeFormat(jsonObject.getString("created_at")));

        } catch (JSONException e) {
            Log.e(TAG,e.toString());
            e.printStackTrace();
        }
        return messageModel;
    }

}
