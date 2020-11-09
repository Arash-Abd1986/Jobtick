package com.jobtick.models.payments;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.jobtick.models.AttachmentModel;

public class Worker {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("slug")
    @Expose
    private String slug;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("avatar")
    @Expose
    private AttachmentModel avatar;
    @SerializedName("is_verified_account")
    @Expose
    private Integer isVerifiedAccount;
    @SerializedName("last_online")
    @Expose
    private String lastOnline;
    @SerializedName("position")
    @Expose
    private Object position;
    @SerializedName("poster_ratings")
    @Expose
    private Object posterRatings;
    @SerializedName("worker_ratings")
    @Expose
    private WorkerRatings workerRatings;
    @SerializedName("posted_task_statistics")
    @Expose
    private PostedTaskStatistics postedTaskStatistics;
    @SerializedName("worker_tier")
    @Expose
    private WorkerTier workerTier;
    @SerializedName("poster_tier")
    @Expose
    private PosterTier posterTier;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
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

    public Integer getIsVerifiedAccount() {
        return isVerifiedAccount;
    }

    public void setIsVerifiedAccount(Integer isVerifiedAccount) {
        this.isVerifiedAccount = isVerifiedAccount;
    }

    public String getLastOnline() {
        return lastOnline;
    }

    public void setLastOnline(String lastOnline) {
        this.lastOnline = lastOnline;
    }

    public Object getPosition() {
        return position;
    }

    public void setPosition(Object position) {
        this.position = position;
    }

    public Object getPosterRatings() {
        return posterRatings;
    }

    public void setPosterRatings(Object posterRatings) {
        this.posterRatings = posterRatings;
    }

    public WorkerRatings getWorkerRatings() {
        return workerRatings;
    }

    public void setWorkerRatings(WorkerRatings workerRatings) {
        this.workerRatings = workerRatings;
    }

    public PostedTaskStatistics getPostedTaskStatistics() {
        return postedTaskStatistics;
    }

    public void setPostedTaskStatistics(PostedTaskStatistics postedTaskStatistics) {
        this.postedTaskStatistics = postedTaskStatistics;
    }

    public WorkerTier getWorkerTier() {
        return workerTier;
    }

    public void setWorkerTier(WorkerTier workerTier) {
        this.workerTier = workerTier;
    }

    public PosterTier getPosterTier() {
        return posterTier;
    }

    public void setPosterTier(PosterTier posterTier) {
        this.posterTier = posterTier;
    }
}
