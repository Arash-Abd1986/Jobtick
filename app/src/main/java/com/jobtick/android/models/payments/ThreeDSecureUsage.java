package com.jobtick.android.models.payments;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.json.JSONObject;

import timber.log.Timber;

public class ThreeDSecureUsage {
    String TAG = ThreeDSecureUsage.class.getName();
    @SerializedName("supported")
    @Expose
    private Boolean supported;

    /**
     * No args constructor for use in serialization
     *
     */
    public ThreeDSecureUsage() {
    }

    /**
     *
     * @param supported
     */
    public ThreeDSecureUsage(Boolean supported) {
        super();
        this.supported = supported;
    }

    public Boolean getSupported() {
        return supported;
    }

    public void setSupported(Boolean supported) {
        this.supported = supported;
    }

    public ThreeDSecureUsage getJsonToModel(JSONObject jsonObject){
        ThreeDSecureUsage threeDSecureUsage = new ThreeDSecureUsage();
        try{
            if(jsonObject.has("supported") && !jsonObject.isNull("supported"))
                threeDSecureUsage.setSupported(jsonObject.getBoolean("supported"));
        }catch (Exception e){
            Timber.e(e.toString());
            e.printStackTrace();
        }
        return threeDSecureUsage;
    }

}
