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

import timber.log.Timber;

public class OfferModel  implements Parcelable{
    String TAG = OfferModel.class.getName();
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("worker")
    @Expose
    private UserAccountModel worker;
    @SerializedName("task_id")
    @Expose
    private Integer taskId;
    @SerializedName("offer_price")
    @Expose
    private Integer offerPrice;
    @SerializedName("payment_type")
    @Expose
    private String paymentType;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("attachments")
    @Expose
    private ArrayList<AttachmentModel> attachments = new ArrayList<>();
    @SerializedName("comments_total")
    @Expose
    private Integer commentsTotal;
    @SerializedName("comments")
    @Expose
    private ArrayList<CommentModel> comments = new ArrayList<>();
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    private Boolean reply = false;
    private Boolean isUserPrefrenceToMore = false;
    private String strMore = "More";

    /**
     * No args constructor for use in serialization
     */
    public OfferModel() {
    }

    /**
     *
     * @param id
     * @param worker
     * @param taskId
     * @param offerPrice
     * @param paymentType
     * @param status
     * @param message
     * @param attachments
     * @param commentsTotal
     * @param comments
     * @param createdAt
     * @param reply
     * @param isUserPrefrenceToMore
     * @param strMore
     */

    public OfferModel(Integer id, UserAccountModel worker, Integer taskId, Integer offerPrice, String paymentType, String status, String message, ArrayList<AttachmentModel> attachments, Integer commentsTotal, ArrayList<CommentModel> comments, String createdAt, Boolean reply, Boolean isUserPrefrenceToMore, String strMore) {
        this.id = id;
        this.worker = worker;
        this.taskId = taskId;
        this.offerPrice = offerPrice;
        this.paymentType = paymentType;
        this.status = status;
        this.message = message;
        this.attachments = attachments;
        this.commentsTotal = commentsTotal;
        this.comments = comments;
        this.createdAt = createdAt;
        this.reply = reply;
        this.isUserPrefrenceToMore = isUserPrefrenceToMore;
        this.strMore = strMore;
    }

    protected OfferModel(Parcel in) {
        if (in.readByte() == 0) {
            id = null;
        } else {
            id = in.readInt();
        }
        worker = in.readParcelable(UserAccountModel.class.getClassLoader());
        if (in.readByte() == 0) {
            taskId = null;
        } else {
            taskId = in.readInt();
        }
        if (in.readByte() == 0) {
            offerPrice = null;
        } else {
            offerPrice = in.readInt();
        }
        paymentType = in.readString();
        status = in.readString();
        message = in.readString();
        attachments = in.createTypedArrayList(AttachmentModel.CREATOR);
        if (in.readByte() == 0) {
            commentsTotal = null;
        } else {
            commentsTotal = in.readInt();
        }
        comments = in.createTypedArrayList(CommentModel.CREATOR);
        createdAt = in.readString();
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
        dest.writeParcelable(worker, flags);
        if (taskId == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(taskId);
        }
        if (offerPrice == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(offerPrice);
        }
        dest.writeString(paymentType);
        dest.writeString(status);
        dest.writeString(message);
        dest.writeTypedList(attachments);
        if (commentsTotal == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(commentsTotal);
        }
        dest.writeTypedList(comments);
        dest.writeString(createdAt);
        dest.writeByte((byte) (reply == null ? 0 : reply ? 1 : 2));
        dest.writeByte((byte) (isUserPrefrenceToMore == null ? 0 : isUserPrefrenceToMore ? 1 : 2));
        dest.writeString(strMore);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<OfferModel> CREATOR = new Creator<OfferModel>() {
        @Override
        public OfferModel createFromParcel(Parcel in) {
            return new OfferModel(in);
        }

        @Override
        public OfferModel[] newArray(int size) {
            return new OfferModel[size];
        }
    };

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public UserAccountModel getWorker() {
        return worker;
    }

    public void setWorker(UserAccountModel worker) {
        this.worker = worker;
    }

    public Integer getTaskId() {
        return taskId;
    }

    public void setTaskId(Integer taskId) {
        this.taskId = taskId;
    }

    public Integer getOfferPrice() {
        return offerPrice;
    }

    public void setOfferPrice(Integer offerPrice) {
        this.offerPrice = offerPrice;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ArrayList<AttachmentModel> getAttachments() {
        return attachments;
    }

    public void setAttachments(ArrayList<AttachmentModel> attachments) {
        this.attachments = attachments;
    }

    public Integer getCommentsTotal() {
        return commentsTotal;
    }

    public void setCommentsTotal(Integer commentsTotal) {
        this.commentsTotal = commentsTotal;
    }

    public ArrayList<CommentModel> getComments() {
        return comments;
    }

    public void setComments(ArrayList<CommentModel> comments) {
        this.comments = comments;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
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

    public OfferModel getJsonToModel(JSONObject jsonObject) {
        OfferModel offerModel = new OfferModel();
        try {
            if (jsonObject.has("id") && !jsonObject.isNull("id"))
                offerModel.setId(jsonObject.getInt("id"));
            if (jsonObject.has("worker") && !jsonObject.isNull("worker"))
                offerModel.setWorker(new UserAccountModel().getJsonToModel(jsonObject.getJSONObject("worker")));
            if (jsonObject.has("task_id") && !jsonObject.isNull("task_id"))
                offerModel.setTaskId(jsonObject.getInt("task_id"));
            if (jsonObject.has("offer_price") && !jsonObject.isNull("offer_price"))
                offerModel.setOfferPrice(jsonObject.getInt("offer_price"));
            if (jsonObject.has("payment_type") && !jsonObject.isNull("payment_type"))
                offerModel.setPaymentType(jsonObject.getString("payment_type"));
            if (jsonObject.has("status") && !jsonObject.isNull("status"))
                offerModel.setStatus(jsonObject.getString("status"));
            if (jsonObject.has("message") && !jsonObject.isNull("message"))
                offerModel.setMessage(jsonObject.getString("message"));
            if (jsonObject.has("attachments") && !jsonObject.isNull("attachments")) {
                JSONArray jsonArray = jsonObject.getJSONArray("attachments");
                ArrayList<AttachmentModel> attachmentList = new ArrayList<>();
                for (int i = 0; jsonArray.length() > i; i++) {
                    JSONObject jsonObject_attachments = jsonArray.getJSONObject(i);
                    attachmentList.add(new AttachmentModel().getJsonToModel(jsonObject_attachments));
                }
                offerModel.setAttachments(attachmentList);
            }
            if (jsonObject.has("comments_total") && !jsonObject.isNull("comments_total"))
                offerModel.setCommentsTotal(jsonObject.getInt("comments_total"));
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
                    offerModel.setReply(true);
                }
                offerModel.setComments(commentModelList);
            }
            if (jsonObject.has("created_at") && !jsonObject.isNull("created_at"))
                offerModel.setCreatedAt(TimeAgo.getTimeAgo(jsonObject.getString("created_at")));
        } catch (JSONException e) {
            Timber.e(e.toString());
            e.printStackTrace();
        }
        return offerModel;
    }
}
