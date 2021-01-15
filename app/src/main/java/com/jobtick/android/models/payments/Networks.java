package com.jobtick.android.models.payments;


import com.google.gson.annotations.SerializedName;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.Expose;

import timber.log.Timber;

public class Networks {
    String TAG = Networks.class.getName();
    @SerializedName("available")
    @Expose
    private List<String> available = null;
    @SerializedName("preferred")
    @Expose
    private String preferred;

    /**
     * No args constructor for use in serialization
     */
    public Networks() {
    }

    /**
     * @param available
     * @param preferred
     */
    public Networks(List<String> available, String preferred) {
        super();
        this.available = available;
        this.preferred = preferred;
    }

    public List<String> getAvailable() {
        return available;
    }

    public void setAvailable(List<String> available) {
        this.available = available;
    }

    public String getPreferred() {
        return preferred;
    }

    public void setPreferred(String preferred) {
        this.preferred = preferred;
    }


    public Networks getJsonToModel(JSONObject jsonObject) {
        Networks networks = new Networks();
        try {
            if (jsonObject.has("available") && !jsonObject.isNull("available")) {
                JSONArray jsonArray_available = jsonObject.getJSONArray("available");
                List<String> stringList = new ArrayList<>();
                for (int i = 0; jsonArray_available.length() > i; i++) {
                    stringList.add(jsonArray_available.getString(i));
                }
                networks.setAvailable(stringList);
            }
            if (jsonObject.has("preferred") && !jsonObject.isNull("preferred"))
                networks.setPreferred(jsonObject.getString("preferred"));
        } catch (Exception e) {
            Timber.e(e.toString());
            e.printStackTrace();
        }
        return networks;
    }


}
