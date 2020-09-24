package com.jobtick.models.payments;

import android.util.Log;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.json.JSONObject;

public class Checks {
    String TAG = Checks.class.getName();
    @SerializedName("address_line1_check")
    @Expose
    private String addressLine1Check;
    @SerializedName("address_postal_code_check")
    @Expose
    private Integer addressPostalCodeCheck;
    @SerializedName("cvc_check")
    @Expose
    private String cvcCheck;

    /**
     * No args constructor for use in serialization
     *
     */
    public Checks() {
    }

    /**
     *
     * @param addressPostalCodeCheck
     * @param cvcCheck
     * @param addressLine1Check
     */
    public Checks(String addressLine1Check, Integer addressPostalCodeCheck, String cvcCheck) {
        super();
        this.addressLine1Check = addressLine1Check;
        this.addressPostalCodeCheck = addressPostalCodeCheck;
        this.cvcCheck = cvcCheck;
    }

    public String getAddressLine1Check() {
        return addressLine1Check;
    }

    public void setAddressLine1Check(String addressLine1Check) {
        this.addressLine1Check = addressLine1Check;
    }

    public Integer getAddressPostalCodeCheck() {
        return addressPostalCodeCheck;
    }

    public void setAddressPostalCodeCheck(Integer addressPostalCodeCheck) {
        this.addressPostalCodeCheck = addressPostalCodeCheck;
    }

    public String getCvcCheck() {
        return cvcCheck;
    }

    public void setCvcCheck(String cvcCheck) {
        this.cvcCheck = cvcCheck;
    }

    public Checks getJsonToModel(JSONObject jsonObject){
        Checks checks = new Checks();
        try{
            if(jsonObject.has("address_line1_check") && !jsonObject.isNull("address_line1_check"))
                checks.setAddressLine1Check(jsonObject.getString("address_line1_check"));
            if(jsonObject.has("address_postal_code_check") && !jsonObject.isNull("address_postal_code_check"))
                checks.setAddressPostalCodeCheck(jsonObject.getInt("address_postal_code_check"));
            if(jsonObject.has("cvc_check") && !jsonObject.isNull("cvc_check"))
                checks.setCvcCheck(jsonObject.getString("cvc_check"));

        }catch (Exception e){
            Log.e(TAG,e.toString());
            e.printStackTrace();
        }
        return checks;
    }


}
