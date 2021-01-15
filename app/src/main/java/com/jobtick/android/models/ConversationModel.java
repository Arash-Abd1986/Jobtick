package com.jobtick.android.models;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.jobtick.android.utils.SessionManager;
import com.jobtick.android.utils.TimeAgo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import timber.log.Timber;

public class ConversationModel implements Parcelable {
    String TAG = ConversationModel.class.getName();
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("task_id")
    @Expose
    private Integer taskId;

    @SerializedName("slug")
    @Expose
    private String slug;

    @SerializedName("status")
    @Expose
    private String status;

    @SerializedName("last_message")
    @Expose
    private ChatModel lastMessage;
    @SerializedName("unseen_count")
    @Expose
    private Integer unseenCount;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("chat_closed")
    @Expose
    private Boolean chatClosed;
    private UserAccountModel sender;
    private UserAccountModel receiver;
    private SessionManager sessionManager;


    /**
     * No args constructor for use in serialization
     */
    public ConversationModel(Context context) {
        this.sessionManager = new SessionManager(context);
    }

    /**
     * @param id
     * @param name
     * @param taskId
     * @param lastMessage
     * @param unseenCount
     * @param createdAt
     * @param sender
     * @param receiver
     */
    public ConversationModel(Integer id, String name, Integer taskId, ChatModel lastMessage, Integer unseenCount, String createdAt, UserAccountModel sender, UserAccountModel receiver, String slug, String status, Boolean chatClosed) {
        this.id = id;
        this.name = name;
        this.taskId = taskId;
        this.lastMessage = lastMessage;
        this.unseenCount = unseenCount;
        this.createdAt = createdAt;
        this.sender = sender;
        this.receiver = receiver;
        this.status = status;
        this.slug = slug;
        this.chatClosed = chatClosed;
    }


    protected ConversationModel(Parcel in) {
        if (in.readByte() == 0) {
            id = null;
        } else {
            id = in.readInt();
        }
        name = in.readString();
        if (in.readByte() == 0) {
            taskId = null;
        } else {
            taskId = in.readInt();
        }
        lastMessage = in.readParcelable(ChatModel.class.getClassLoader());
        if (in.readByte() == 0) {
            unseenCount = null;
        } else {
            unseenCount = in.readInt();
        }
        createdAt = in.readString();
        sender = in.readParcelable(UserAccountModel.class.getClassLoader());
        receiver = in.readParcelable(UserAccountModel.class.getClassLoader());
        status = in.readString();
        slug = in.readString();
        this.chatClosed = ((Boolean) in.readValue((Boolean.class.getClassLoader())));
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
        if (taskId == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(taskId);
        }
        dest.writeParcelable(lastMessage, flags);
        if (unseenCount == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(unseenCount);
        }
        dest.writeString(createdAt);
        dest.writeParcelable(sender, flags);
        dest.writeParcelable(receiver, flags);
        dest.writeString(status);
        dest.writeString(slug);
        dest.writeValue(chatClosed);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ConversationModel> CREATOR = new Creator<ConversationModel>() {
        @Override
        public ConversationModel createFromParcel(Parcel in) {
            return new ConversationModel(in);
        }

        @Override
        public ConversationModel[] newArray(int size) {
            return new ConversationModel[size];
        }
    };

    public Integer getUnseenCount() {
        return unseenCount;
    }

    public void setUnseenCount(Integer unseenCount) {
        this.unseenCount = unseenCount;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public ChatModel getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(ChatModel lastMessage) {
        this.lastMessage = lastMessage;
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

    public Integer getTaskId() {
        return taskId;
    }

    public void setTaskId(Integer taskId) {
        this.taskId = taskId;
    }

    public UserAccountModel getSender() {
        return sender;
    }

    public void setSender(UserAccountModel sender) {
        this.sender = sender;
    }

    public UserAccountModel getReceiver() {
        return receiver;
    }

    public void setReceiver(UserAccountModel receiver) {
        this.receiver = receiver;
    }


    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Boolean getChatClosed() {
        return chatClosed;
    }

    public void setChatClosed(Boolean chatClosed) {
        this.chatClosed = chatClosed;
    }

    public ConversationModel getJsonToModel(JSONObject jsonObject, Context context) {
        ConversationModel conversationModel = new ConversationModel(context);
        try {
            if (jsonObject.has("id") && !jsonObject.isNull("id"))
                conversationModel.setId(jsonObject.getInt("id"));
            if (jsonObject.has("name") && !jsonObject.isNull("name"))
                conversationModel.setName(jsonObject.getString("name"));


            if (jsonObject.has("task") && !jsonObject.isNull("task")) {
                JSONObject taskOjbect = jsonObject.getJSONObject("task");
                if (taskOjbect.has("slug") && !taskOjbect.isNull("slug")) {
                    conversationModel.setSlug(taskOjbect.getString("slug"));
                }
                if (taskOjbect.has("status") && !taskOjbect.isNull("status")) {
                    conversationModel.setStatus(taskOjbect.getString("status"));
                }
                if (taskOjbect.has("id") && !taskOjbect.isNull("id")) {
                    conversationModel.setTaskId(taskOjbect.getInt(
                            "id"));
                }

            }
            if (jsonObject.has("users") && !jsonObject.isNull("users")) {
                JSONArray jsonArray_users = jsonObject.getJSONArray("users");
                for (int i = 0; jsonArray_users.length() > i; i++) {
                    JSONObject jsonObject_user = jsonArray_users.getJSONObject(i);
                    UserAccountModel userAccountModel = new UserAccountModel().getJsonToModel(jsonObject_user);
                    if (userAccountModel.getId().equals(sessionManager.getUserAccount().getId())) {
                        conversationModel.setSender(userAccountModel);
                    } else {
                        conversationModel.setReceiver(userAccountModel);
                    }
                }
            }
            if (jsonObject.has("last_message") && !jsonObject.isNull("last_message"))
                conversationModel.setLastMessage(new ChatModel().getJsonToModel(jsonObject.getJSONObject("last_message")));
            if (jsonObject.has("unseen_count") && !jsonObject.isNull("unseen_count"))
                conversationModel.setUnseenCount(jsonObject.getInt("unseen_count"));
            if (jsonObject.has("created_at") && !jsonObject.isNull("created_at"))
                conversationModel.setCreatedAt(TimeAgo.getTimeAgo(jsonObject.getString("created_at")));

            if (jsonObject.has("chat_closed") && !jsonObject.isNull("chat_closed"))
                conversationModel.setChatClosed(jsonObject.getBoolean("chat_closed"));
        } catch (JSONException e) {
            Timber.e(e.toString());
            e.printStackTrace();
        }
        return conversationModel;
    }
}
