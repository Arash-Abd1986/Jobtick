package com.jobtick.android.models.payments;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PostedTaskStatistics {

    @SerializedName("completion_rate")
    @Expose
    private Float completionRate;

    public Float getCompletionRate() {
        return completionRate;
    }

    public void setCompletionRate(Float completionRate) {
        this.completionRate = completionRate;
    }
}
