
package com.jobtick.android.models.conversation;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.json.JSONException;
import org.json.JSONObject;

public class ConversationModel implements Parcelable
{

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("task")
    @Expose
    private Task task;
    public final static Creator<ConversationModel> CREATOR = new Creator<ConversationModel>() {


        @SuppressWarnings({
            "unchecked"
        })
        public ConversationModel createFromParcel(Parcel in) {
            return new ConversationModel(in);
        }

        public ConversationModel[] newArray(int size) {
            return (new ConversationModel[size]);
        }

    }
    ;

    protected ConversationModel(Parcel in) {
        this.id = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.name = ((String) in.readValue((String.class.getClassLoader())));
        this.task = ((Task) in.readValue((Task.class.getClassLoader())));
    }

    /**
     * No args constructor for use in serialization
     * 
     */
    public ConversationModel() {
    }

    /**
     * 
     * @param task
     * @param name
     * @param id
     */
    public ConversationModel(Integer id, String name, Task task) {
        super();
        this.id = id;
        this.name = name;
        this.task = task;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public ConversationModel withId(Integer id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ConversationModel withName(String name) {
        this.name = name;
        return this;
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    public ConversationModel withTask(Task task) {
        this.task = task;
        return this;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(id);
        dest.writeValue(name);
        dest.writeValue(task);
    }

    public int describeContents() {
        return  0;
    }

    public ConversationModel getJsonToModel(JSONObject jsonObject) {
        ConversationModel model = new ConversationModel();
        try{
        if (jsonObject.has("id") && !jsonObject.isNull("id"))
            model.setId(jsonObject.getInt("id"));
            if (jsonObject.has("name") && !jsonObject.isNull("name"))
                model.setName(jsonObject.getString("name"));
            if (jsonObject.has("task") && !jsonObject.isNull("task"))
                model.setTask(new Task().getJsonToModel(jsonObject.getJSONObject("task")));


        }catch (JSONException e) {
            e.printStackTrace();
        }

        return model;
    }

}
