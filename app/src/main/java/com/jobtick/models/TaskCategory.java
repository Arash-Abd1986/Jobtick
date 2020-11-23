package com.jobtick.models;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import org.json.JSONObject;

import java.util.Objects;

public class TaskCategory implements Parcelable {


    @SerializedName("id")
    private int id;

    @SerializedName("icon")
    private String icon;

    @SerializedName("name")
    private String name;

    public TaskCategory() {
    }

    public TaskCategory(int id, String name) {
        this.id = id;

        this.name = name;
    }

    protected TaskCategory(Parcel in) {
        id = in.readInt();

        name = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<TaskCategory> CREATOR = new Creator<TaskCategory>() {
        @Override
        public TaskCategory createFromParcel(Parcel in) {
            return new TaskCategory(in);
        }

        @Override
        public TaskCategory[] newArray(int size) {
            return new TaskCategory[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public TaskCategory getJsonToModel(JSONObject jsonObject, Context context) {

        TaskCategory taskCategory = new TaskCategory();
        try {
            if (jsonObject.has("id") && !jsonObject.isNull("id"))
                taskCategory.setId(jsonObject.getInt("id"));
            if (jsonObject.has("icon") && !jsonObject.isNull("icon")) {
                taskCategory.setIcon(jsonObject.getString("icon"));
            }
            if (jsonObject.has("name") && !jsonObject.isNull("name")) {
                taskCategory.setName(jsonObject.getString("name"));
            }
        } catch (Exception e) {

        }
        return taskCategory;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TaskCategory that = (TaskCategory) o;
        return name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
