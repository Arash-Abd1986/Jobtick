package com.jobtick.models;

public class BankAccountModel {

    /**
     * success : true
     * data : {"id":3,"account_name":"ptiyanka","account_number":"2345788654","bsb_code":"6865","created_at":"2020-07-29T05:39:47+00:00","updated_at":"2020-07-29T05:39:47+00:00"}
     */

    private boolean success;
    private DataBean data;

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

    public static class DataBean {
        /**
         * id : 3
         * account_name : ptiyanka
         * account_number : 2345788654
         * bsb_code : 6865
         * created_at : 2020-07-29T05:39:47+00:00
         * updated_at : 2020-07-29T05:39:47+00:00
         */

        private int id;
        private String account_name;
        private String account_number;
        private String bsb_code;
        private String created_at;
        private String updated_at;

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
