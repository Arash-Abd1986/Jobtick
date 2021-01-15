package com.jobtick.android.models;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.jobtick.android.models.review.ReviewModel;
import com.jobtick.android.utils.TimeAgo;
import com.jobtick.android.models.conversation.ConversationModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

public class TaskModel implements Parcelable {
    String TAG = TaskModel.class.getName();
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("category_id")
    @Expose
    private int category_id;

    @SerializedName("slug")
    @Expose
    private String slug;
    @SerializedName("position")
    @Expose
    private PositionModel position;
    @SerializedName("location")
    @Expose
    private String location;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("cancellation")
    @Expose
    private CancellationModel cancellation;
    @SerializedName("review")
    @Expose
    private List<ReviewModel> reviewModels = new ArrayList<>();
    @SerializedName("additional_fund")
    @Expose
    private AdditionalFundModel additionalFund;

    @SerializedName("reschedule_requests")
    @Expose
    private ArrayList<RescheduleReqeust> rescheduleReqeust = new ArrayList<>();

    @SerializedName("amount")
    @Expose
    private Integer amount = 0;


    @SerializedName("budget")
    @Expose
    private Integer budget = 0;
    @SerializedName("hourly_rate")
    @Expose
    private Integer hourlyRate = 0;
    @SerializedName("total_hours")
    @Expose
    private Integer totalHours = 0;
    @SerializedName("payment_type")
    @Expose
    private String paymentType;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("task_type")
    @Expose
    private String taskType;
    @SerializedName("due_date")
    @Expose
    private String dueDate;
    @SerializedName("due_time")
    @Expose
    private DueTimeModel dueTime;
    @SerializedName("musthave")
    @Expose
    private ArrayList<String> musthave = new ArrayList<>();
    @SerializedName("offer_count")
    @Expose
    private Integer offerCount;
    @SerializedName("poster")
    @Expose
    private UserAccountModel poster;
    @SerializedName("worker")
    @Expose
    private UserAccountModel worker;
    @SerializedName("conversation")
    @Expose
    private ConversationModel conversation;
    @SerializedName("attachments")
    @Expose
    private ArrayList<AttachmentModel> attachments = new ArrayList<>();
    @SerializedName("offers")
    @Expose
    private ArrayList<OfferModel> offers = new ArrayList<>();
    @SerializedName("offer_sent")
    @Expose
    private Boolean offerSent;
    @SerializedName("question_count")
    @Expose
    private Integer questionCount = 3;
    @SerializedName("questions")
    @Expose
    private ArrayList<QuestionModel> questions = new ArrayList<>();
    @SerializedName("created_at")
    @Expose
    private String createdAt;

    @SerializedName("bookmark_id")
    @Expose
    private Integer bookmarkID;

    /**
     * No args constructor for use in serialization
     */
    public TaskModel() {
    }

    public TaskModel(Integer id, String title, String slug, PositionModel position, String location,
                     String description, CancellationModel cancellation, AdditionalFundModel additionalFund,
                     Integer amount, Integer budget, Integer hourlyRate, Integer totalHours, String paymentType,
                     String status, String taskType, String dueDate, DueTimeModel dueTime, ArrayList<String> musthave,
                     Integer offerCount, UserAccountModel poster, UserAccountModel worker, ConversationModel conversation,
                     ArrayList<AttachmentModel> attachments, ArrayList<OfferModel> offers, Boolean offerSent, Integer questionCount,
                     ArrayList<QuestionModel> questions,ArrayList<ReviewModel> reviewModels, String createdAt, ArrayList<RescheduleReqeust> rescheduleReqeust, Integer bookmarkID, int category_id) {
        this.id = id;
        this.title = title;
        this.slug = slug;
        this.position = position;
        this.location = location;
        this.description = description;
        this.cancellation = cancellation;
        this.additionalFund = additionalFund;
        this.amount = amount;
        this.budget = budget;
        this.hourlyRate = hourlyRate;
        this.totalHours = totalHours;
        this.paymentType = paymentType;
        this.status = status;
        this.taskType = taskType;
        this.dueDate = dueDate;
        this.dueTime = dueTime;
        this.musthave = musthave;
        this.offerCount = offerCount;
        this.poster = poster;
        this.worker = worker;
        this.conversation = conversation;
        this.attachments = attachments;
        this.offers = offers;
        this.offerSent = offerSent;
        this.questionCount = questionCount;
        this.questions = questions;
        this.createdAt = createdAt;
        this.rescheduleReqeust = rescheduleReqeust;
        this.bookmarkID = bookmarkID;
        this.category_id = category_id;
        this.reviewModels = reviewModels;

    }

    protected TaskModel(Parcel in) {
        if (in.readByte() == 0) {
            id = null;
        } else {
            id = in.readInt();
        }
        title = in.readString();
        category_id = in.readInt();
        slug = in.readString();
        position = in.readParcelable(PositionModel.class.getClassLoader());
        location = in.readString();
        description = in.readString();
        cancellation = in.readParcelable(CancellationModel.class.getClassLoader());
        additionalFund = in.readParcelable(AdditionalFundModel.class.getClassLoader());
        if (in.readByte() == 0) {
            amount = null;
        } else {
            amount = in.readInt();
        }
        if (in.readByte() == 0) {
            budget = null;
        } else {
            budget = in.readInt();
        }
        if (in.readByte() == 0) {
            hourlyRate = null;
        } else {
            hourlyRate = in.readInt();
        }
        if (in.readByte() == 0) {
            totalHours = null;
        } else {
            totalHours = in.readInt();
        }
        paymentType = in.readString();
        status = in.readString();
        taskType = in.readString();
        dueDate = in.readString();
        dueTime = in.readParcelable(DueTimeModel.class.getClassLoader());
        musthave = in.createStringArrayList();
        if (in.readByte() == 0) {
            offerCount = null;
        } else {
            offerCount = in.readInt();
        }
        poster = in.readParcelable(UserAccountModel.class.getClassLoader());
        worker = in.readParcelable(UserAccountModel.class.getClassLoader());
        conversation = in.readParcelable(ConversationModel.class.getClassLoader());
        attachments = in.createTypedArrayList(AttachmentModel.CREATOR);
        reviewModels = in.createTypedArrayList(ReviewModel.CREATOR);
        offers = in.createTypedArrayList(OfferModel.CREATOR);
        //rescheduleReqeust=in.createTypedArrayList(RescheduleReqeust.CREATOR);


        byte tmpOfferSent = in.readByte();
        offerSent = tmpOfferSent == 0 ? null : tmpOfferSent == 1;
        if (in.readByte() == 0) {
            questionCount = null;
        } else {
            questionCount = in.readInt();
        }
        questions = in.createTypedArrayList(QuestionModel.CREATOR);
        createdAt = in.readString();

        if (in.readByte() == 0) {
            bookmarkID = null;
        } else {
            bookmarkID = in.readInt();
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
        dest.writeString(title);
        dest.writeInt(category_id);
        dest.writeString(slug);
        dest.writeParcelable(position, flags);
        dest.writeString(location);
        dest.writeString(description);
        dest.writeParcelable(cancellation, flags);
        dest.writeParcelable(additionalFund, flags);
//        dest.writeTypedList(rescheduleReqeust);

        if (amount == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(amount);
        }
        if (budget == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(budget);
        }
        if (hourlyRate == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(hourlyRate);
        }
        if (totalHours == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(totalHours);
        }
        dest.writeString(paymentType);
        dest.writeString(status);
        dest.writeString(taskType);
        dest.writeString(dueDate);
        dest.writeParcelable(dueTime, flags);
        dest.writeStringList(musthave);
        if (offerCount == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(offerCount);
        }
        dest.writeParcelable(poster, flags);
        dest.writeParcelable(worker, flags);
        dest.writeParcelable(conversation, flags);
        dest.writeTypedList(attachments);
        dest.writeTypedList(reviewModels);
        dest.writeTypedList(offers);
        dest.writeByte((byte) (offerSent == null ? 0 : offerSent ? 1 : 2));
        if (questionCount == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(questionCount);
        }
        dest.writeTypedList(questions);
        dest.writeString(createdAt);
        if (bookmarkID == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(bookmarkID);
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<TaskModel> CREATOR = new Creator<TaskModel>() {
        @Override
        public TaskModel createFromParcel(Parcel in) {
            return new TaskModel(in);
        }

        @Override
        public TaskModel[] newArray(int size) {
            return new TaskModel[size];
        }
    };

    public AdditionalFundModel getAdditionalFund() {
        return additionalFund;
    }

    public void setAdditionalFund(AdditionalFundModel additionalFund) {
        this.additionalFund = additionalFund;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public PositionModel getPosition() {
        return position;
    }

    public void setPosition(PositionModel position) {
        this.position = position;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public CancellationModel getCancellation() {
        return cancellation;
    }

    public void setCancellation(CancellationModel cancellation) {
        this.cancellation = cancellation;
    }

    public List<ReviewModel> getReviewModels() {
        return reviewModels;
    }

    public void setReviewModels(List<ReviewModel> reviewModels) {
        this.reviewModels = reviewModels;
    }

    public static Creator<TaskModel> getCREATOR() {
        return CREATOR;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public Integer getBudget() {
        return budget;
    }

    public void setBudget(Integer budget) {
        this.budget = budget;
    }

    public Integer getHourlyRate() {
        return hourlyRate;
    }

    public void setHourlyRate(Integer hourlyRate) {
        this.hourlyRate = hourlyRate;
    }

    public Integer getTotalHours() {
        return totalHours;
    }

    public void setTotalHours(Integer totalHours) {
        this.totalHours = totalHours;
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

    public String getTaskType() {
        return taskType;
    }

    public void setTaskType(String taskType) {
        this.taskType = taskType;
    }

    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public DueTimeModel getDueTime() {
        return dueTime;
    }

    public void setDueTime(DueTimeModel dueTime) {
        this.dueTime = dueTime;
    }

    public ArrayList<String> getMusthave() {
        return musthave;
    }

    public void setMusthave(ArrayList<String> musthave) {
        this.musthave = musthave;
    }

    public Integer getOfferCount() {
        return offerCount;
    }

    public void setOfferCount(Integer offerCount) {
        this.offerCount = offerCount;
    }

    public UserAccountModel getPoster() {
        return poster;
    }

    public void setPoster(UserAccountModel poster) {
        this.poster = poster;
    }

    public UserAccountModel getWorker() {
        return worker;
    }

    public void setWorker(UserAccountModel worker) {
        this.worker = worker;
    }

    public ConversationModel getConversation() {
        return conversation;
    }

    public void setConversation(ConversationModel conversation) {
        this.conversation = conversation;
    }

    public ArrayList<AttachmentModel> getAttachments() {
        return attachments;
    }

    public void setAttachments(ArrayList<AttachmentModel> attachments) {
        this.attachments = attachments;
    }

    public ArrayList<OfferModel> getOffers() {
        return offers;
    }

    public void setOffers(ArrayList<OfferModel> offers) {
        this.offers = offers;
    }

    public Boolean getOfferSent() {
        return offerSent;
    }

    public void setOfferSent(Boolean offerSent) {
        this.offerSent = offerSent;
    }

    public Integer getQuestionCount() {
        return questionCount;
    }

    public void setQuestionCount(Integer questionCount) {
        this.questionCount = questionCount;
    }

    public ArrayList<QuestionModel> getQuestions() {
        return questions;
    }

    public void setQuestions(ArrayList<QuestionModel> questions) {
        this.questions = questions;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public ArrayList<RescheduleReqeust> getRescheduleReqeust() {
        return rescheduleReqeust;
    }

    public void setRescheduleReqeust(ArrayList<RescheduleReqeust> rescheduleReqeust) {
        this.rescheduleReqeust = rescheduleReqeust;
    }

    public Integer getBookmarkID() {
        return bookmarkID;
    }

    public void setBookmarkID(Integer bookmarkID) {
        this.bookmarkID = bookmarkID;
    }

    public int getCategory_id() {
        return category_id;
    }

    public void setCategory_id(int category_id) {
        this.category_id = category_id;
    }

    public TaskModel getJsonToModel(JSONObject jsonObject, Context context) {
        TaskModel taskModel = new TaskModel();
        try {
            if (jsonObject.has("id") && !jsonObject.isNull("id"))
                taskModel.setId(jsonObject.getInt("id"));
            if (jsonObject.has("category_id") && !jsonObject.isNull("category_id"))
                taskModel.setCategory_id(jsonObject.getInt("category_id"));
            if (jsonObject.has("poster") && !jsonObject.isNull("poster"))
                taskModel.setPoster(new UserAccountModel().getJsonToModel(jsonObject.getJSONObject("poster")));
            if (jsonObject.has("worker") && !jsonObject.isNull("worker"))
                taskModel.setWorker(new UserAccountModel().getJsonToModel(jsonObject.getJSONObject("worker")));
            if (jsonObject.has("conversation") && !jsonObject.isNull("conversation"))
                taskModel.setConversation(new ConversationModel().getJsonToModel(jsonObject.getJSONObject("conversation")));
            if (jsonObject.has("title") && !jsonObject.isNull("title"))
                taskModel.setTitle(jsonObject.getString("title"));
            if (jsonObject.has("slug") && !jsonObject.isNull("slug"))
                taskModel.setSlug(jsonObject.getString("slug"));
            if (jsonObject.has("position") && !jsonObject.isNull("position"))
                taskModel.setPosition(new PositionModel().getJsonToModel(jsonObject.getJSONObject("position")));
            if (jsonObject.has("location") && !jsonObject.isNull("location"))
                taskModel.setLocation(jsonObject.getString("location"));
            if (jsonObject.has("description") && !jsonObject.isNull("description"))
                taskModel.setDescription(jsonObject.getString("description"));
            if (jsonObject.has("cancellation") && !jsonObject.isNull("cancellation"))
                taskModel.setCancellation(new CancellationModel().getJsonToModel(jsonObject.getJSONObject("cancellation")));
            if (jsonObject.has("additional_fund") && !jsonObject.isNull("additional_fund"))
                taskModel.setAdditionalFund(new AdditionalFundModel().getJsonToModel(jsonObject.getJSONObject("additional_fund")));
            if (jsonObject.has("reschedule_requests") && !jsonObject.isNull("reschedule_requests")) {
                JSONArray jsonArray_request = jsonObject.getJSONArray("reschedule_requests");
                ArrayList<RescheduleReqeust> rescheduleReqeustList = new ArrayList<>();
                for (int i = 0; jsonArray_request.length() > i; i++) {
                    JSONObject jsonObject_request = jsonArray_request.getJSONObject(i);

                    rescheduleReqeustList.add(new RescheduleReqeust().getJsonToModel(jsonObject_request));
                }
                taskModel.setRescheduleReqeust(rescheduleReqeustList);

            }

            if (jsonObject.has("budget") && !jsonObject.isNull("budget"))
                taskModel.setBudget(jsonObject.getInt("budget"));
            if (jsonObject.has("amount") && !jsonObject.isNull("amount"))
                taskModel.setAmount(jsonObject.getInt("amount"));
            if (jsonObject.has("hourly_rate") && !jsonObject.isNull("hourly_rate"))
                taskModel.setHourlyRate(jsonObject.getInt("hourly_rate"));
            if (jsonObject.has("total_hours") && !jsonObject.isNull("total_hours"))
                taskModel.setTotalHours(jsonObject.getInt("total_hours"));
            if (jsonObject.has("payment_type") && !jsonObject.isNull("payment_type"))
                taskModel.setPaymentType(jsonObject.getString("payment_type"));
            if (jsonObject.has("status") && !jsonObject.isNull("status"))
                taskModel.setStatus(jsonObject.getString("status"));
            if (jsonObject.has("task_type") && !jsonObject.isNull("task_type"))
                taskModel.setTaskType(jsonObject.getString("task_type"));
            if (jsonObject.has("due_date") && !jsonObject.isNull("due_date"))
                taskModel.setDueDate(jsonObject.getString("due_date").substring(0, 10));
            if (jsonObject.has("due_time") && !jsonObject.isNull("due_time"))
                taskModel.setDueTime(new DueTimeModel().getJsonToModel(jsonObject.getJSONObject("due_time")));
            if (jsonObject.has("musthave") && !jsonObject.isNull("musthave")) {
                JSONArray jsonArray_musthave = jsonObject.getJSONArray("musthave");
                ArrayList<String> mustHaveList = new ArrayList<>();
                for (int i = 0; jsonArray_musthave.length() > i; i++) {
                    mustHaveList.add(jsonArray_musthave.getString(i));
                }
                taskModel.setMusthave(mustHaveList);
            }
            if (jsonObject.has("attachments") && !jsonObject.isNull("attachments")) {
                JSONArray jsonArray = jsonObject.getJSONArray("attachments");
                ArrayList<AttachmentModel> attachmentList = new ArrayList<>();
                for (int i = 0; jsonArray.length() > i; i++) {
                    JSONObject jsonObject_attachments = jsonArray.getJSONObject(i);
                    attachmentList.add(new AttachmentModel().getJsonToModel(jsonObject_attachments));
                }
                taskModel.setAttachments(attachmentList);
            }
            if (jsonObject.has("review") && !jsonObject.isNull("review")) {
                JSONArray jsonArray = jsonObject.getJSONArray("review");
                for (int i = 0; jsonArray.length() > i; i++) {
                    JSONObject jsonObject_reviewModel = jsonArray.getJSONObject(i);
                    taskModel.getReviewModels().add(new ReviewModel().getJsonToModel(jsonObject_reviewModel));
                }
            }
            if (jsonObject.has("offers") && !jsonObject.isNull("offers")) {
                JSONArray jsonArray = jsonObject.getJSONArray("offers");
                ArrayList<OfferModel> offerModelList = new ArrayList<>();
                for (int i = 0; jsonArray.length() > i; i++) {
                    JSONObject jsonObject_offers = jsonArray.getJSONObject(i);
                    offerModelList.add(new OfferModel().getJsonToModel(jsonObject_offers));
                }
                taskModel.setOffers(offerModelList);
            }
            if (jsonObject.has("offer_count") && !jsonObject.isNull("offer_count"))
                taskModel.setOfferCount(jsonObject.getInt("offer_count"));
            if (jsonObject.has("question_count") && !jsonObject.isNull("question_count"))
                taskModel.setQuestionCount(jsonObject.getInt("question_count"));
            if (jsonObject.has("offer_sent") && !jsonObject.isNull("offer_sent"))
                taskModel.setOfferSent(jsonObject.getBoolean("offer_sent"));
            if (jsonObject.has("questions") && !jsonObject.isNull("questions")) {
                JSONArray jsonArray = jsonObject.getJSONArray("questions");
                ArrayList<QuestionModel> questionModelArrayList = new ArrayList<>();
                for (int i = 0; jsonArray.length() > i; i++) {
                    JSONObject jsonObject_questions = jsonArray.getJSONObject(i);
                    questionModelArrayList.add(new QuestionModel().getJsonToModel(jsonObject_questions));
                }
                taskModel.setQuestions(questionModelArrayList);
            }
            if (jsonObject.has("created_at") && !jsonObject.isNull("created_at"))
                taskModel.setCreatedAt(TimeAgo.getTimeAgo(jsonObject.getString("created_at")));

            if (jsonObject.has("bookmark_id") && !jsonObject.isNull("bookmark_id"))
                taskModel.setBookmarkID(jsonObject.getInt("bookmark_id"));

        } catch (JSONException e) {
            Timber.e(e.toString());
            e.printStackTrace();
        }
        return taskModel;
    }

    public PreviewTaskModel getPreviewTaskModel() {
        return new PreviewTaskModel(getId(), getTitle(), getPoster().getId(),getSlug() );
    }
}
