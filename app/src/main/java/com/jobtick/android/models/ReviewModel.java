package com.jobtick.android.models;


import java.util.List;

public class ReviewModel {


    /**
     * success : true
     * message : success
     * data : [{"id":3,"rater":{"id":29,"name":"techavtra ticker","avatar":null},"ratee_id":2,"task":{"id":39,"title":"move my sofa","slug":"move-my-sofa-15946448953"},"rating":5,"ratee_type":"poster","message":"this is the first time I have ever seen a good day at work and I will be there at that time I will be there at that time I will be there at that time I will be","is_published":0,"created_at":"2020-07-22T05:41:10+00:00"},{"id":4,"rater":{"id":23,"name":"devyani1411 Chauhan","avatar":null},"ratee_id":2,"task":{"id":38,"title":"need a shower repairing service man","slug":"need-a-shower-repairing-service-man-15946379480"},"rating":4,"ratee_type":"poster","message":"jejejsjaohohxhxgixixxkkhkhkckhxjgxjj🙄👊🙄🙄","is_published":0,"created_at":"2020-07-22T05:55:08+00:00"}]
     * links : {"first":"https://dev.jobtick.com/api/v1/profile/2/reviews/poster?page=1","last":"https://dev.jobtick.com/api/v1/profile/2/reviews/poster?page=1","prev":null,"next":null}
     * meta : {"current_page":1,"from":1,"last_page":1,"path":"https://dev.jobtick.com/api/v1/profile/2/reviews/poster","per_page":20,"to":2,"total":2}
     */

    private boolean success;
    private String message;
    private LinksBean links;
    private MetaBean meta;
    private List<DataBean> data;

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

    public LinksBean getLinks() {
        return links;
    }

    public void setLinks(LinksBean links) {
        this.links = links;
    }

    public MetaBean getMeta() {
        return meta;
    }

    public void setMeta(MetaBean meta) {
        this.meta = meta;
    }

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class LinksBean {
        /**
         * first : https://dev.jobtick.com/api/v1/profile/2/reviews/poster?page=1
         * last : https://dev.jobtick.com/api/v1/profile/2/reviews/poster?page=1
         * prev : null
         * next : null
         */

        private String first;
        private String last;
        private Object prev;
        private Object next;

        public String getFirst() {
            return first;
        }

        public void setFirst(String first) {
            this.first = first;
        }

        public String getLast() {
            return last;
        }

        public void setLast(String last) {
            this.last = last;
        }

        public Object getPrev() {
            return prev;
        }

        public void setPrev(Object prev) {
            this.prev = prev;
        }

        public Object getNext() {
            return next;
        }

        public void setNext(Object next) {
            this.next = next;
        }
    }

    public static class MetaBean {
        /**
         * current_page : 1
         * from : 1
         * last_page : 1
         * path : https://dev.jobtick.com/api/v1/profile/2/reviews/poster
         * per_page : 20
         * to : 2
         * total : 2
         */

        private int current_page;
        private int from;
        private int last_page;
        private String path;
        private int per_page;
        private int to;
        private int total;

        public int getCurrent_page() {
            return current_page;
        }

        public void setCurrent_page(int current_page) {
            this.current_page = current_page;
        }

        public int getFrom() {
            return from;
        }

        public void setFrom(int from) {
            this.from = from;
        }

        public int getLast_page() {
            return last_page;
        }

        public void setLast_page(int last_page) {
            this.last_page = last_page;
        }

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }

        public int getPer_page() {
            return per_page;
        }

        public void setPer_page(int per_page) {
            this.per_page = per_page;
        }

        public int getTo() {
            return to;
        }

        public void setTo(int to) {
            this.to = to;
        }

        public int getTotal() {
            return total;
        }

        public void setTotal(int total) {
            this.total = total;
        }
    }

    public static class DataBean {
        /**
         * id : 3
         * rater : {"id":29,"name":"techavtra ticker","avatar":null}
         * ratee_id : 2
         * task : {"id":39,"title":"move my sofa","slug":"move-my-sofa-15946448953"}
         * rating : 5
         * ratee_type : poster
         * message : this is the first time I have ever seen a good day at work and I will be there at that time I will be there at that time I will be there at that time I will be
         * is_published : 0
         * created_at : 2020-07-22T05:41:10+00:00
         */

        private int id;
        private RaterBean rater;
        private int ratee_id;
        private TaskBean task;
        private int rating;
        private String ratee_type;
        private String message;
        private int is_published;
        private String created_at;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public RaterBean getRater() {
            return rater;
        }

        public void setRater(RaterBean rater) {
            this.rater = rater;
        }

        public int getRatee_id() {
            return ratee_id;
        }

        public void setRatee_id(int ratee_id) {
            this.ratee_id = ratee_id;
        }

        public TaskBean getTask() {
            return task;
        }

        public void setTask(TaskBean task) {
            this.task = task;
        }

        public int getRating() {
            return rating;
        }

        public void setRating(int rating) {
            this.rating = rating;
        }

        public String getRatee_type() {
            return ratee_type;
        }

        public void setRatee_type(String ratee_type) {
            this.ratee_type = ratee_type;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public int getIs_published() {
            return is_published;
        }

        public void setIs_published(int is_published) {
            this.is_published = is_published;
        }

        public String getCreated_at() {
            return created_at;
        }

        public void setCreated_at(String created_at) {
            this.created_at = created_at;
        }

        public static class RaterBean {
            /**
             * id : 29
             * name : techavtra ticker
             * avatar : null
             */

            private int id;
            private String name;
            private AttachmentModel avatar;

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public AttachmentModel getAvatar() {
                return avatar;
            }

            public void setAvatar(AttachmentModel avatar) {
                this.avatar = avatar;
            }
        }

        public static class TaskBean {
            /**
             * id : 39
             * title : move my sofa
             * slug : move-my-sofa-15946448953
             */

            private int id;
            private String title;
            private String slug;

            public int getId() {
                return id;
            }

            public void setId(int id) {
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
        }
    }
}
