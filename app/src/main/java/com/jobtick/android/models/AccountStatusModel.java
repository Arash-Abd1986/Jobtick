package com.jobtick.android.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.json.JSONException;
import org.json.JSONObject;

import timber.log.Timber;

public class AccountStatusModel implements Parcelable {
    String TAG = TierModel.class.getName();

    @SerializedName("basic_info_completed")
    @Expose
    private boolean basic_info;

    @SerializedName("credit_card")
    @Expose
    private boolean credit_card;

    @SerializedName("bank_account")
    @Expose
    private boolean bank_account;

    @SerializedName("skills")
    @Expose
    private boolean skills;

    @SerializedName("badges")
    @Expose
    private boolean badges;

    @SerializedName("portfolio")
    @Expose
    private boolean portfolio;

    @SerializedName("jobalerts")
    @Expose
    private boolean jobalerts;


    protected AccountStatusModel(Parcel in) {
        TAG = in.readString();
        basic_info = in.readByte() != 0;
        credit_card = in.readByte() != 0;
        bank_account = in.readByte() != 0;
        skills = in.readByte() != 0;
        badges = in.readByte() != 0;
        portfolio = in.readByte() != 0;
        jobalerts = in.readByte() != 0;
    }

    public static final Creator<AccountStatusModel> CREATOR = new Creator<AccountStatusModel>() {
        @Override
        public AccountStatusModel createFromParcel(Parcel in) {
            return new AccountStatusModel(in);
        }

        @Override
        public AccountStatusModel[] newArray(int size) {
            return new AccountStatusModel[size];
        }
    };

    public AccountStatusModel() {

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeByte((byte) (basic_info ? 1 : 0));
        dest.writeByte((byte) (credit_card ? 1 : 0));
        dest.writeByte((byte) (bank_account ? 1 : 0));
        dest.writeByte((byte) (skills ? 1 : 0));
        dest.writeByte((byte) (badges ? 1 : 0));
        dest.writeByte((byte) (portfolio ? 1 : 0));
        dest.writeByte((byte) (jobalerts ? 1 : 0));

    }

    public boolean isBasic_info() {
        return basic_info;
    }

    public void setBasic_info(boolean basic_info) {
        this.basic_info = basic_info;
    }

    public boolean isCredit_card() {
        return credit_card;
    }

    public void setCredit_card(boolean credit_card) {
        this.credit_card = credit_card;
    }

    public boolean isBank_account() {
        return bank_account;
    }

    public void setBank_account(boolean bank_account) {
        this.bank_account = bank_account;
    }

    public boolean isSkills() {
        return skills;
    }

    public void setSkills(boolean skills) {
        this.skills = skills;
    }

    public boolean isBadges() {
        return badges;
    }

    public void setBadges(boolean badges) {
        this.badges = badges;
    }

    public boolean isPortfolio() {
        return portfolio;
    }
    public boolean isJobalerts() {
        return jobalerts;
    }

    public void setPortfolio(boolean portfolio) {
        this.portfolio = portfolio;
    }
    public void setJobalerts(boolean jobalerts) {
        this.jobalerts = jobalerts;
    }

    public AccountStatusModel getJsonToModel(JSONObject jsonObject){
        AccountStatusModel statusModel = new AccountStatusModel();
        try{
            if(jsonObject.has("basic_info_completed") && !jsonObject.isNull("basic_info_completed"))
                statusModel.setBasic_info(jsonObject.getBoolean("basic_info_completed"));

            if(jsonObject.has("bank_account") && !jsonObject.isNull("bank_account"))
                statusModel.setBank_account(jsonObject.getBoolean("bank_account"));

            if(jsonObject.has("credit_card") && !jsonObject.isNull("credit_card"))
                statusModel.setCredit_card(jsonObject.getBoolean("credit_card"));

            if(jsonObject.has("skills") && !jsonObject.isNull("skills"))
                statusModel.setSkills(jsonObject.getBoolean("skills"));

            if(jsonObject.has("badges") && !jsonObject.isNull("badges"))
                statusModel.setBadges(jsonObject.getBoolean("badges"));

            if(jsonObject.has("portfolio") && !jsonObject.isNull("portfolio"))
                statusModel.setPortfolio(jsonObject.getBoolean("portfolio"));

            if(jsonObject.has("jobalerts") && !jsonObject.isNull("jobalerts"))
                statusModel.setJobalerts(jsonObject.getBoolean("jobalerts"));


        }catch (JSONException e){
            Timber.tag(TAG).e(e.toString());
            e.printStackTrace();
        }
        return statusModel;
    }
}
