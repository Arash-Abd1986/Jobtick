package com.jobtick.android.models.payments;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class PostedTaskStatistics implements Serializable {

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
