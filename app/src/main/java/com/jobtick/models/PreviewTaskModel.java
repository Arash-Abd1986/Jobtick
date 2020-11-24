package com.jobtick.models;


public class PreviewTaskModel {

    private int id;
    private String title;
    private int userId;
    private String slug;

    public PreviewTaskModel(int id, String title, int userId, String slug) {
        this.id = id;
        this.title = title;
        this.userId = userId;
        this.slug = slug;
    }

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

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }
}
