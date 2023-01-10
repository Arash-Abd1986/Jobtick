package com.jobtick.android.models.response;

import org.json.JSONArray;

public class PortfolioDataModel {
    String id;
    String title;
    String share_in;
    String description;
    String img_count;
    String status;
    JSONArray img;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getShare_in() {
        return share_in;
    }

    public void setShare_in(String share_in) {
        this.share_in = share_in;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImg_count() {
        return img_count;
    }

    public void setImg_count(String img_count) {
        this.img_count = img_count;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public JSONArray getImg() {
        return img;
    }

    public void setImg(JSONArray img) {
        this.img = img;
    }
}
