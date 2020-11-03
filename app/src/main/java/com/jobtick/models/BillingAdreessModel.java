package com.jobtick.models;

import android.os.Parcel;
import android.os.Parcelable;

public class BillingAdreessModel implements Parcelable{


    private boolean success;
    private DataBean data;

    //having normal object behaviour
    public BillingAdreessModel(){

    }

    protected BillingAdreessModel(Parcel in) {
        success = in.readByte() != 0;
        data = in.readParcelable(DataBean.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte((byte) (success ? 1 : 0));
        dest.writeParcelable(data, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<BillingAdreessModel> CREATOR = new Creator<BillingAdreessModel>() {
        @Override
        public BillingAdreessModel createFromParcel(Parcel in) {
            return new BillingAdreessModel(in);
        }

        @Override
        public BillingAdreessModel[] newArray(int size) {
            return new BillingAdreessModel[size];
        }
    };

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean implements Parcelable {

        private int id;
        private String line1;
        private String line2;
        private String location;
        private String post_code;
        private String state;
        private String country;
        private String created_at;
        private String updated_at;

        private String city;

        //having normal object behaviour
        public DataBean(){

        }

        protected DataBean(Parcel in) {
            id = in.readInt();
            line1 = in.readString();
            line2 = in.readString();
            location = in.readString();
            post_code = in.readString();
            state = in.readString();
            country = in.readString();
            created_at = in.readString();
            updated_at = in.readString();
            city = in.readString();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(id);
            dest.writeString(line1);
            dest.writeString(line2);
            dest.writeString(location);
            dest.writeString(post_code);
            dest.writeString(state);
            dest.writeString(country);
            dest.writeString(created_at);
            dest.writeString(updated_at);
            dest.writeString(city);
        }

        @Override
        public int describeContents() {
            return 0;
        }

        public static final Creator<DataBean> CREATOR = new Creator<DataBean>() {
            @Override
            public DataBean createFromParcel(Parcel in) {
                return new DataBean(in);
            }

            @Override
            public DataBean[] newArray(int size) {
                return new DataBean[size];
            }
        };

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getLine1() {
            return line1;
        }

        public void setLine1(String line1) {
            this.line1 = line1;
        }

        public String getLine2() {
            return line2;
        }

        public void setLine2(String line2) {
            this.line2 = line2;
        }

        public String getLocation() {
            return location;
        }

        public void setLocation(String location) {
            this.location = location;
        }

        public String getPost_code() {
            return post_code;
        }

        public void setPost_code(String post_code) {
            this.post_code = post_code;
        }

        public String getState() {
            return state;
        }

        public void setState(String state) {
            this.state = state;
        }

        public String getCountry() {
            return country;
        }

        public void setCountry(String country) {
            this.country = country;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getCreated_at() {
            return created_at;
        }

        public void setCreated_at(String created_at) {
            this.created_at = created_at;
        }

        public String getUpdated_at() {
            return updated_at;
        }

        public void setUpdated_at(String updated_at) {
            this.updated_at = updated_at;
        }
    }
}
