package com.jobtick.models.payments;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PostedTaskStatistics {

    @SerializedName("completion_rate")
    @Expose
    private Integer completionRate;

    public Integer getCompletionRate() {
        return completionRate;
    }

    public void setCompletionRate(Integer completionRate) {
        this.completionRate = completionRate;
    }
}
