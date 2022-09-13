package com.jobtick.android.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.jobtick.android.utils.Constant;
import com.jobtick.android.viewmodel.PostAJobViewModel;

public class FilterModel implements Parcelable {
    private String query;
    private String section = Constant.FILTER_ALL_QUERY;
    private String location,distance;
    private String latitude;
    private String logitude;
    private String price;
    private String task_open;
    private Boolean isAscending = true;
    private String sortType;

    public FilterModel() {
    }

    protected FilterModel(Parcel in) {
        query = in.readString();
        section = in.readString();
        location = in.readString();
        distance = in.readString();
        latitude = in.readString();
        logitude = in.readString();
        price = in.readString();
        task_open = in.readString();
        isAscending = in.readByte() != 0;
        sortType = in.readString();
    }

    public Boolean getAscending() {
        return isAscending;
    }

    public void setAscending(Boolean ascending) {
        isAscending = ascending;
    }

    public String getSortType() {
        return sortType;
    }

    public void setSortType(String sortType) {
        this.sortType = sortType;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(query);
        dest.writeString(section);
        dest.writeString(location);
        dest.writeString(distance);
        dest.writeString(latitude);
        dest.writeString(logitude);
        dest.writeString(price);
        dest.writeString(task_open);
        dest.writeByte((byte) (isAscending ? 1 : 0));
        dest.writeString(sortType);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<FilterModel> CREATOR = new Creator<FilterModel>() {
        @Override
        public FilterModel createFromParcel(Parcel in) {
            return new FilterModel(in);
        }

        @Override
        public FilterModel[] newArray(int size) {
            return new FilterModel[size];
        }
    };

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLogitude() {
        return logitude;
    }

    public void setLogitude(String logitude) {
        this.logitude = logitude;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getTask_open() {
        return task_open;
    }

    public void setTask_open(String task_open) {
        this.task_open = task_open;
    }
}
