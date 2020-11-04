package com.jobtick.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class BankAccountModel implements Parcelable {

    /**
     * success : true
     * data : {"id":3,"account_name":"ptiyanka","account_number":"2345788654","bsb_code":"6865","created_at":"2020-07-29T05:39:47+00:00","updated_at":"2020-07-29T05:39:47+00:00"}
     */

    private boolean success;
    private DataBean data;

    //having normal object behaviour
    public BankAccountModel(){

    }

    protected BankAccountModel(Parcel in) {
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

    public static final Creator<BankAccountModel> CREATOR = new Creator<BankAccountModel>() {
        @Override
        public BankAccountModel createFromParcel(Parcel in) {
            return new BankAccountModel(in);
        }

        @Override
        public BankAccountModel[] newArray(int size) {
            return new BankAccountModel[size];
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
        /**
         * id : 3
         * account_name : ptiyanka
         * account_number : 2345788654
         * bsb_code : 6865
         * created_at : 2020-07-29T05:39:47+00:00
         * updated_at : 2020-07-29T05:39:47+00:00
         */

        private int id;
        @SerializedName("account_holder_name")
        private String account_name;
        @SerializedName("account_number_last_four")
        private String account_number;
        @SerializedName("routing_number")
        private String bsb_code;
        private String created_at;
        private String updated_at;

        //having normal object behaviour
        public DataBean(){

        }

        protected DataBean(Parcel in) {
            id = in.readInt();
            account_name = in.readString();
            account_number = in.readString();
            bsb_code = in.readString();
            created_at = in.readString();
            updated_at = in.readString();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(id);
            dest.writeString(account_name);
            dest.writeString(account_number);
            dest.writeString(bsb_code);
            dest.writeString(created_at);
            dest.writeString(updated_at);
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

        public String getAccount_name() {
            return account_name;
        }

        public void setAccount_name(String account_name) {
            this.account_name = account_name;
        }

        public String getAccount_number() {
            return account_number;
        }

        public void setAccount_number(String account_number) {
            this.account_number = account_number;
        }

        public String getBsb_code() {
            return bsb_code;
        }

        public void setBsb_code(String bsb_code) {
            this.bsb_code = bsb_code;
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
