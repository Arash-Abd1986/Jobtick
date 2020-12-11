
package com.jobtick.models.cancellation.reason;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.json.JSONException;
import org.json.JSONObject;

public class ReasonModel implements Parcelable
{

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("reason")
    @Expose
    private String reason;
    @SerializedName("parent_id")
    @Expose
    private Object parentId;
    @SerializedName("responsible_person_type")
    @Expose
    private String responsiblePersonType;


    public final static Creator<ReasonModel> CREATOR = new Creator<ReasonModel>() {


        @SuppressWarnings({
            "unchecked"
        })
        public ReasonModel createFromParcel(Parcel in) {
            return new ReasonModel(in);
        }

        public ReasonModel[] newArray(int size) {
            return (new ReasonModel[size]);
        }

    }
    ;

    protected ReasonModel(Parcel in) {
        this.id = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.reason = ((String) in.readValue((String.class.getClassLoader())));
        this.parentId = ((Object) in.readValue((Object.class.getClassLoader())));
        this.responsiblePersonType = ((String) in.readValue((String.class.getClassLoader())));
    }

    /**
     * No args constructor for use in serialization
     * 
     */
    public ReasonModel() {
    }

    /**
     * 
     * @param reason
     * @param id
     * @param parentId
     * @param responsiblePersonType
     */
    public ReasonModel(Integer id, String reason, Object parentId, String responsiblePersonType) {
        super();
        this.id = id;
        this.reason = reason;
        this.parentId = parentId;
        this.responsiblePersonType = responsiblePersonType;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Object getParentId() {
        return parentId;
    }

    public void setParentId(Object parentId) {
        this.parentId = parentId;
    }

    public String getResponsiblePersonType() {
        return responsiblePersonType;
    }

    public void setResponsiblePersonType(String responsiblePersonType) {
        this.responsiblePersonType = responsiblePersonType;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(id);
        dest.writeValue(reason);
        dest.writeValue(parentId);
        dest.writeValue(responsiblePersonType);
    }

    public int describeContents() {
        return  0;
    }

    public ReasonModel getJsonToModel(JSONObject jsonObject) {
        ReasonModel reasonModel = new ReasonModel();
        try {
            if (jsonObject.has("id") && !jsonObject.isNull("id"))
                reasonModel.setId(jsonObject.getInt("id"));
            if (jsonObject.has("reason") && !jsonObject.isNull("reason"))
                reasonModel.setReason(jsonObject.getString("reason"));
            if (jsonObject.has("responsible_person_type") && !jsonObject.isNull("responsible_person_type"))
                reasonModel.setReason(jsonObject.getString("responsible_person_type"));
        } catch (
                JSONException e) {
            e.printStackTrace();
        }
        return reasonModel;
    }
}
