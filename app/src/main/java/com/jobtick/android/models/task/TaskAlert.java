package com.jobtick.android.models.task;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import org.jetbrains.annotations.Nullable;
import org.json.JSONException;
import org.json.JSONObject;

public class TaskAlert implements Parcelable {
    private int id;
    private String alert_type;
    private String ketword;
    private String suburb;
    private int distance = -1;
    @Nullable
    private Double lattitude;
    @Nullable
    private Double longitude;


    public TaskAlert() {
    }


    protected TaskAlert(Parcel in) {
        id = in.readInt();
        alert_type = in.readString();
        ketword = in.readString();
        suburb = in.readString();
        distance = in.readInt();
        longitude = in.readDouble();
        lattitude = in.readDouble();

    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(alert_type);
        dest.writeString(ketword);
        dest.writeString(suburb);
        dest.writeInt(distance);
        try {
            dest.writeDouble(lattitude);
            dest.writeDouble(longitude);
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<TaskAlert> CREATOR = new Creator<TaskAlert>() {
        @Override
        public TaskAlert createFromParcel(Parcel in) {
            return new TaskAlert(in);
        }

        @Override
        public TaskAlert[] newArray(int size) {
            return new TaskAlert[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAlert_type() {
        return alert_type;
    }

    public void setAlert_type(String alert_type) {
        this.alert_type = alert_type;
    }

    public String getKetword() {
        return ketword;
    }

    public void setKetword(String ketword) {
        this.ketword = ketword;
    }

    public String getSuburb() {
        return suburb;
    }

    public void setSuburb(String suburb) {
        this.suburb = suburb;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public boolean isValid() {
        return alert_type != null && ketword != null;
    }

    public Double getLattitude() {
        return lattitude;
    }

    public void setLattitude(Double lattitude) {
        this.lattitude = lattitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public TaskAlert getJsonToModel(JSONObject jsonObject, Context context) {

        TaskAlert taskAlert = new TaskAlert();
        try {
            if (jsonObject.has("id")) {
                taskAlert.setId(jsonObject.getInt("id"));
            }
            if (jsonObject.has("text") && jsonObject.getString("text") != null) {
                taskAlert.setKetword(jsonObject.getString("text"));
            }

            if (jsonObject.has("task_type") && jsonObject.getString("task_type") != null) {
                taskAlert.setAlert_type(jsonObject.getString("task_type"));
            }
            if (taskAlert.getAlert_type() != null && taskAlert.getAlert_type().equals("physical")) {
                if (jsonObject.has("location") && jsonObject.getString("location") != null) {
                    taskAlert.setSuburb(jsonObject.getString("location"));

                }

                if (jsonObject.has("distance")) {
                    taskAlert.setDistance(jsonObject.getInt("distance"));
                }
                if (jsonObject.has("position")) {
                    JSONObject jPosition = jsonObject.getJSONObject("position");
                    if (jPosition.has("latitude") && !jsonObject.isNull("latitude")) {
                        taskAlert.setLattitude(jPosition.getDouble("latitude"));
                    }
                    if (jPosition.has("longitude") && !jsonObject.isNull("longitude")) {
                        taskAlert.setLattitude(jPosition.getDouble("longitude"));
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return taskAlert;
    }
}
