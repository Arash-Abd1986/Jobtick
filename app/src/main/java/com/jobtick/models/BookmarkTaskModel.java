package com.jobtick.models;

public class BookmarkTaskModel {


    /**
     * success : true
     * message : successfully saved
     * data : {"task_id":78,"user_id":29,"is_important":"1","updated_at":"2020-08-05 11:37:33","created_at":"2020-08-05 11:37:33","id":37}
     */

    private boolean success;
    private String message;
    private DataBean data;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * task_id : 78
         * user_id : 29
         * is_important : 1
         * updated_at : 2020-08-05 11:37:33
         * created_at : 2020-08-05 11:37:33
         * id : 37
         */

        private int task_id;
        private int user_id;
        private String is_important;
        private String updated_at;
        private String created_at;
        private int id;

        public int getTask_id() {
            return task_id;
        }

        public void setTask_id(int task_id) {
            this.task_id = task_id;
        }

        public int getUser_id() {
            return user_id;
        }

        public void setUser_id(int user_id) {
            this.user_id = user_id;
        }

        public String getIs_important() {
            return is_important;
        }

        public void setIs_important(String is_important) {
            this.is_important = is_important;
        }

        public String getUpdated_at() {
            return updated_at;
        }

        public void setUpdated_at(String updated_at) {
            this.updated_at = updated_at;
        }

        public String getCreated_at() {
            return created_at;
        }

        public void setCreated_at(String created_at) {
            this.created_at = created_at;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }
    }
}
