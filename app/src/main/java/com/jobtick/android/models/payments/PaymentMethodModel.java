package com.jobtick.android.models.payments;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.json.JSONObject;

import timber.log.Timber;

public class PaymentMethodModel {
    String TAG = PaymentMethodModel.class.getName();
    @SerializedName("brand")
    @Expose
    private String brand;
    @SerializedName("checks")
    @Expose
    private Checks checks;
    @SerializedName("country")
    @Expose
    private String country;
    @SerializedName("exp_month")
    @Expose
    private Integer expMonth;
    @SerializedName("exp_year")
    @Expose
    private Integer expYear;
    @SerializedName("fingerprint")
    @Expose
    private String fingerprint;
    @SerializedName("funding")
    @Expose
    private String funding;
    @SerializedName("generated_from")
    @Expose
    private String generatedFrom;
    @SerializedName("last4")
    @Expose
    private String last4;
    @SerializedName("networks")
    @Expose
    private Networks networks;
    @SerializedName("three_d_secure_usage")
    @Expose
    private ThreeDSecureUsage threeDSecureUsage;
    @SerializedName("wallet")
    @Expose
    private Integer wallet;

    /**
     * No args constructor for use in serialization
     */
    public PaymentMethodModel() {
    }

    /**
     * @param country
     * @param expMonth
     * @param last4
     * @param threeDSecureUsage
     * @param funding
     * @param checks
     * @param wallet
     * @param expYear
     * @param fingerprint
     * @param networks
     * @param brand
     * @param generatedFrom
     */
    public PaymentMethodModel(String brand, Checks checks, String country, Integer expMonth, Integer expYear, String fingerprint, String funding, String generatedFrom, String last4, Networks networks, ThreeDSecureUsage threeDSecureUsage, Integer wallet) {
        super();
        this.brand = brand;
        this.checks = checks;
        this.country = country;
        this.expMonth = expMonth;
        this.expYear = expYear;
        this.fingerprint = fingerprint;
        this.funding = funding;
        this.generatedFrom = generatedFrom;
        this.last4 = last4;
        this.networks = networks;
        this.threeDSecureUsage = threeDSecureUsage;
        this.wallet = wallet;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public Checks getChecks() {
        return checks;
    }

    public void setChecks(Checks checks) {
        this.checks = checks;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public Integer getExpMonth() {
        return expMonth;
    }

    public void setExpMonth(Integer expMonth) {
        this.expMonth = expMonth;
    }

    public Integer getExpYear() {
        return expYear;
    }

    public void setExpYear(Integer expYear) {
        this.expYear = expYear;
    }

    public String getFingerprint() {
        return fingerprint;
    }

    public void setFingerprint(String fingerprint) {
        this.fingerprint = fingerprint;
    }

    public String getFunding() {
        return funding;
    }

    public void setFunding(String funding) {
        this.funding = funding;
    }

    public String getGeneratedFrom() {
        return generatedFrom;
    }

    public void setGeneratedFrom(String generatedFrom) {
        this.generatedFrom = generatedFrom;
    }

    public String getLast4() {
        return last4;
    }

    public void setLast4(String last4) {
        this.last4 = last4;
    }

    public Networks getNetworks() {
        return networks;
    }

    public void setNetworks(Networks networks) {
        this.networks = networks;
    }

    public ThreeDSecureUsage getThreeDSecureUsage() {
        return threeDSecureUsage;
    }

    public void setThreeDSecureUsage(ThreeDSecureUsage threeDSecureUsage) {
        this.threeDSecureUsage = threeDSecureUsage;
    }

    public Integer getWallet() {
        return wallet;
    }

    public void setWallet(Integer wallet) {
        this.wallet = wallet;
    }

    public PaymentMethodModel getJsonToModel(JSONObject jsonObject) {
        PaymentMethodModel paymentMethodModel = new PaymentMethodModel();
        try {
            if (jsonObject.has("brand") && !jsonObject.isNull("brand"))
                paymentMethodModel.setBrand(jsonObject.getString("brand"));
            if (jsonObject.has("checks") && !jsonObject.isNull("checks"))
                paymentMethodModel.setChecks(new Checks().getJsonToModel(jsonObject.getJSONObject("checks")));
            if (jsonObject.has("country") && !jsonObject.isNull("country"))
                paymentMethodModel.setCountry(jsonObject.getString("country"));
            if (jsonObject.has("exp_month") && !jsonObject.isNull("exp_month"))
                paymentMethodModel.setExpMonth(jsonObject.getInt("exp_month"));
            if (jsonObject.has("exp_year") && !jsonObject.isNull("exp_year"))
                paymentMethodModel.setExpYear(jsonObject.getInt("exp_year"));
            if (jsonObject.has("fingerprint") && !jsonObject.isNull("fingerprint"))
                paymentMethodModel.setFingerprint(jsonObject.getString("fingerprint"));
            if (jsonObject.has("funding") && !jsonObject.isNull("funding"))
                paymentMethodModel.setFunding(jsonObject.getString("funding"));
            if (jsonObject.has("generated_from") && !jsonObject.isNull("generated_from"))
                paymentMethodModel.setGeneratedFrom(jsonObject.getString("generated_from"));
            if (jsonObject.has("last4") && !jsonObject.isNull("last4"))
                paymentMethodModel.setLast4(jsonObject.getString("last4"));
            if (jsonObject.has("three_d_secure_usage") && !jsonObject.isNull("three_d_secure_usage"))
                paymentMethodModel.setThreeDSecureUsage(new ThreeDSecureUsage().getJsonToModel(jsonObject.getJSONObject("three_d_secure_usage")));
            if (jsonObject.has("networks") && !jsonObject.isNull("networks"))
                paymentMethodModel.setNetworks(new Networks().getJsonToModel(jsonObject.getJSONObject("networks")));
            if (jsonObject.has("wallet") && !jsonObject.isNull("wallet"))
                paymentMethodModel.setWallet(jsonObject.getInt("wallet"));


        } catch (Exception e) {
            Timber.e(e.toString());
            e.printStackTrace();
        }
        return paymentMethodModel;
    }
}
