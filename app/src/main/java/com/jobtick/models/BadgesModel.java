package com.jobtick.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.nimbusds.jose.jca.JCASupport;

import org.json.JSONException;
import org.json.JSONObject;

public class BadgesModel implements Parcelable {
    String TAG = BadgesModel.class.getName();

    @SerializedName("id")
    @Expose
    private Integer id;

    @SerializedName("badge_code")
    @Expose
    private String badge_code;

    @SerializedName("is_verified")
    @Expose
    private Integer is_verified;

    @SerializedName("badge_details")
    @Expose
    private BadgesDetails badgesDetails;

    public BadgesModel() {
    }

    protected BadgesModel(Parcel in) {
        TAG = in.readString();
        if (in.readByte() == 0) {
            id = null;
        } else {
            id = in.readInt();
        }
        badge_code = in.readString();
        if (in.readByte() == 0) {
            is_verified = null;
        } else {
            is_verified = in.readInt();
        }
        badgesDetails = in.readParcelable(BadgesDetails.class.getClassLoader());
    }

    public static final Creator<BadgesModel> CREATOR = new Creator<BadgesModel>() {
        @Override
        public BadgesModel createFromParcel(Parcel in) {
            return new BadgesModel(in);
        }

        @Override
        public BadgesModel[] newArray(int size) {
            return new BadgesModel[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(TAG);
        if (id == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(id);
        }
        dest.writeString(badge_code);
        if (is_verified == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(is_verified);
        }
        dest.writeParcelable(badgesDetails, flags);
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getBadge_code() {
        return badge_code;
    }

    public void setBadge_code(String badge_code) {
        this.badge_code = badge_code;
    }

    public Integer getIs_verified() {
        return is_verified;
    }

    public void setIs_verified(Integer is_verified) {
        this.is_verified = is_verified;
    }

    public BadgesDetails getBadgesDetails() {
        return badgesDetails;
    }

    public void setBadgesDetails(BadgesDetails badgesDetails) {
        this.badgesDetails = badgesDetails;
    }

    public BadgesModel(Integer id, String badge_code, Integer is_verified, BadgesDetails badgesDetails) {
        this.id = id;
        this.badge_code = badge_code;
        this.is_verified = is_verified;
        this.badgesDetails = badgesDetails;
    }

    public BadgesModel getJsonToModel(JSONObject jsonObject) {
        BadgesModel badgesModel = new BadgesModel();

        try {
            if (jsonObject.has("badge_code") && !jsonObject.isNull("badge_code")) {
                badgesModel.setBadge_code(jsonObject.getString("badge_code").toString());
            }
            if (jsonObject.has("is_verified") && !jsonObject.isNull("is_verified")) {
                badgesModel.setIs_verified(jsonObject.getInt("is_verified"));
            }
            if (jsonObject.has("id") && !jsonObject.isNull("id")) {
                badgesModel.setId(jsonObject.getInt("id"));
            }
            //jsonObject.getJSONObject("badge_details").getString("name")

            JSONObject jsonObjectDetails = jsonObject.getJSONObject("badge_details");
            BadgesDetails badgesDetails = new BadgesDetails();

            if (jsonObjectDetails.has("icon") && !jsonObjectDetails.isNull("icon")) {
                badgesDetails.setIcon(jsonObjectDetails.getString("icon"));
            }

            if (jsonObjectDetails.has("name") && !jsonObjectDetails.isNull("name")) {
                badgesDetails.setName(jsonObjectDetails.getString("name"));
            }
            badgesModel.setBadgesDetails(badgesDetails);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        return badgesModel;

    }

}
