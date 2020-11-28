
package com.jobtick.models.conversation;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.json.JSONException;
import org.json.JSONObject;

public class Task implements Parcelable
{

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("slug")
    @Expose
    private String slug;
    @SerializedName("amount")
    @Expose
    private Integer amount;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("task_type")
    @Expose
    private String taskType;
    @SerializedName("due_date")
    @Expose
    private String dueDate;
    @SerializedName("closed_at")
    @Expose
    private String closedAt;
    @SerializedName("completed_at")
    @Expose
    private String completedAt;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    public final static Creator<Task> CREATOR = new Creator<Task>() {


        @SuppressWarnings({
            "unchecked"
        })
        public Task createFromParcel(Parcel in) {
            return new Task(in);
        }

        public Task[] newArray(int size) {
            return (new Task[size]);
        }

    }
    ;

    protected Task(Parcel in) {
        this.id = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.title = ((String) in.readValue((String.class.getClassLoader())));
        this.slug = ((String) in.readValue((String.class.getClassLoader())));
        this.amount = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.status = ((String) in.readValue((String.class.getClassLoader())));
        this.taskType = ((String) in.readValue((String.class.getClassLoader())));
        this.dueDate = ((String) in.readValue((String.class.getClassLoader())));
        this.closedAt = ((String) in.readValue((Object.class.getClassLoader())));
        this.completedAt = ((String) in.readValue((String.class.getClassLoader())));
        this.createdAt = ((String) in.readValue((String.class.getClassLoader())));
    }

    /**
     * No args constructor for use in serialization
     * 
     */
    public Task() {
    }

    /**
     * 
     * @param createdAt
     * @param amount
     * @param taskType
     * @param completedAt
     * @param dueDate
     * @param id
     * @param title
     * @param closedAt
     * @param slug
     * @param status
     */
    public Task(Integer id, String title, String slug, Integer amount, String status, String taskType, String dueDate, String closedAt, String completedAt, String createdAt) {
        super();
        this.id = id;
        this.title = title;
        this.slug = slug;
        this.amount = amount;
        this.status = status;
        this.taskType = taskType;
        this.dueDate = dueDate;
        this.closedAt = closedAt;
        this.completedAt = completedAt;
        this.createdAt = createdAt;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Task withId(Integer id) {
        this.id = id;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Task withTitle(String title) {
        this.title = title;
        return this;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public Task withSlug(String slug) {
        this.slug = slug;
        return this;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public Task withAmount(Integer amount) {
        this.amount = amount;
        return this;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Task withStatus(String status) {
        this.status = status;
        return this;
    }

    public String getTaskType() {
        return taskType;
    }

    public void setTaskType(String taskType) {
        this.taskType = taskType;
    }

    public Task withTaskType(String taskType) {
        this.taskType = taskType;
        return this;
    }

    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public Task withDueDate(String dueDate) {
        this.dueDate = dueDate;
        return this;
    }

    public String getClosedAt() {
        return closedAt;
    }

    public void setClosedAt(String closedAt) {
        this.closedAt = closedAt;
    }

    public Task withClosedAt(String closedAt) {
        this.closedAt = closedAt;
        return this;
    }

    public String getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(String completedAt) {
        this.completedAt = completedAt;
    }

    public Task withCompletedAt(String completedAt) {
        this.completedAt = completedAt;
        return this;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public Task withCreatedAt(String createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(id);
        dest.writeValue(title);
        dest.writeValue(slug);
        dest.writeValue(amount);
        dest.writeValue(status);
        dest.writeValue(taskType);
        dest.writeValue(dueDate);
        dest.writeValue(closedAt);
        dest.writeValue(completedAt);
        dest.writeValue(createdAt);
    }

    public int describeContents() {
        return  0;
    }

    public Task getJsonToModel(JSONObject jsonObject) {
        Task model = new Task();
        try{
            if (jsonObject.has("id") && !jsonObject.isNull("id"))
                model.setId(jsonObject.getInt("id"));
            if (jsonObject.has("title") && !jsonObject.isNull("title"))
                model.setTitle(jsonObject.getString("title"));
            if (jsonObject.has("slug") && !jsonObject.isNull("slug"))
                model.setSlug(jsonObject.getString("slug"));
            if (jsonObject.has("amount") && !jsonObject.isNull("amount"))
                model.setAmount(jsonObject.getInt("amount"));
            if (jsonObject.has("status") && !jsonObject.isNull("status"))
                model.setStatus(jsonObject.getString("status"));
            if (jsonObject.has("task_type") && !jsonObject.isNull("task_type"))
                model.setTaskType(jsonObject.getString("task_type"));
            if (jsonObject.has("due_date") && !jsonObject.isNull("due_date"))
                model.setDueDate(jsonObject.getString("due_date"));
            if (jsonObject.has("completed_at") && !jsonObject.isNull("completed_at"))
                model.setCompletedAt(jsonObject.getString("completed_at"));
            if (jsonObject.has("created_at") && !jsonObject.isNull("created_at"))
                model.setCreatedAt(jsonObject.getString("created_at"));
            if (jsonObject.has("closed_at") && !jsonObject.isNull("closed_at"))
                model.setClosedAt(jsonObject.getString("closed_at"));

        }catch (JSONException e) {
            e.printStackTrace();
        }

        return model;
    }
}
