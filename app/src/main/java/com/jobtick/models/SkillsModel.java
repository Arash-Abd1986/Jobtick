package com.jobtick.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;

public class SkillsModel implements Parcelable {
    String TAG = SkillsModel.class.getName();
    @SerializedName("specialities")
    @Expose
    private ArrayList<String> specialities = new ArrayList<>();
    @SerializedName("experience")
    @Expose
    private ArrayList<String> experience = new ArrayList<>();
    @SerializedName("transportation")
    @Expose
    private ArrayList<String> transportation = new ArrayList<>();
    @SerializedName("education")
    @Expose
    private ArrayList<String> education = new ArrayList<>();
    @SerializedName("language")
    @Expose
    private ArrayList<String> language = new ArrayList<>();

    public SkillsModel() {
    }

    public SkillsModel(ArrayList<String> specialities, ArrayList<String> experience, ArrayList<String> transportation, ArrayList<String> education, ArrayList<String> language) {
        this.specialities = specialities;
        this.experience = experience;
        this.transportation = transportation;
        this.education = education;
        this.language = language;
    }

    protected SkillsModel(Parcel in) {
        specialities = in.createStringArrayList();
        experience = in.createStringArrayList();
        transportation = in.createStringArrayList();
        education = in.createStringArrayList();
        language = in.createStringArrayList();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringList(specialities);
        dest.writeStringList(experience);
        dest.writeStringList(transportation);
        dest.writeStringList(education);
        dest.writeStringList(language);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<SkillsModel> CREATOR = new Creator<SkillsModel>() {
        @Override
        public SkillsModel createFromParcel(Parcel in) {
            return new SkillsModel(in);
        }

        @Override
        public SkillsModel[] newArray(int size) {
            return new SkillsModel[size];
        }
    };

    public ArrayList<String> getSpecialities() {
        return specialities;
    }

    public void setSpecialities(ArrayList<String> specialities) {
        this.specialities = specialities;
    }

    public ArrayList<String> getExperience() {
        return experience;
    }

    public void setExperience(ArrayList<String> experience) {
        this.experience = experience;
    }

    public ArrayList<String> getTransportation() {
        return transportation;
    }

    public void setTransportation(ArrayList<String> transportation) {
        this.transportation = transportation;
    }

    public ArrayList<String> getEducation() {
        return education;
    }

    public void setEducation(ArrayList<String> education) {
        this.education = education;
    }

    public ArrayList<String> getLanguage() {
        return language;
    }

    public void setLanguage(ArrayList<String> language) {
        this.language = language;
    }

    public SkillsModel getJsonToModel(JSONObject jsonObject) {
        SkillsModel skillsModel = new SkillsModel();
        try {
            if (jsonObject.has("specialities") && !jsonObject.isNull("specialities")) {
                JSONArray jsonArray_specialities = jsonObject.getJSONArray("specialities");
                ArrayList<String> stringArrayList = new ArrayList<>();
                for (int i = 0; jsonArray_specialities.length() > i; i++) {
                    JSONObject jsonObject_specialities = jsonArray_specialities.getJSONObject(i);
                    stringArrayList.add(jsonObject_specialities.getString("name"));
                }
                skillsModel.setSpecialities(stringArrayList);
            }
            if (jsonObject.has("experience") && !jsonObject.isNull("experience")) {
                JSONArray jsonArray_experience = jsonObject.getJSONArray("experience");
                ArrayList<String> stringArrayList = new ArrayList<>();
                for (int i = 0; jsonArray_experience.length() > i; i++) {
                    JSONObject jsonObject_experience = jsonArray_experience.getJSONObject(i);
                    stringArrayList.add(jsonObject_experience.getString("name"));
                }
                skillsModel.setExperience(stringArrayList);
            }
            if (jsonObject.has("transportation") && !jsonObject.isNull("transportation")) {
                JSONArray jsonArray_transportation = jsonObject.getJSONArray("transportation");
                ArrayList<String> stringArrayList = new ArrayList<>();
                for (int i = 0; jsonArray_transportation.length() > i; i++) {
                    JSONObject jsonObject_transportation = jsonArray_transportation.getJSONObject(i);
                    stringArrayList.add(jsonObject_transportation.getString("name"));
                }
                skillsModel.setTransportation(stringArrayList);
            }
            if (jsonObject.has("language") && !jsonObject.isNull("language")) {
                JSONArray jsonArray_language = jsonObject.getJSONArray("language");
                ArrayList<String> stringArrayList = new ArrayList<>();
                for (int i = 0; jsonArray_language.length() > i; i++) {
                    JSONObject jsonObject_language = jsonArray_language.getJSONObject(i);
                    stringArrayList.add(jsonObject_language.getString("name"));
                }
                skillsModel.setLanguage(stringArrayList);
            }
            if (jsonObject.has("education") && !jsonObject.isNull("education")) {
                JSONArray jsonArray_education = jsonObject.getJSONArray("education");
                ArrayList<String> stringArrayList = new ArrayList<>();
                for (int i = 0; jsonArray_education.length() > i; i++) {
                    JSONObject jsonObject_education = jsonArray_education.getJSONObject(i);
                    stringArrayList.add(jsonObject_education.getString("name"));
                }
                skillsModel.setEducation(stringArrayList);
            }

        } catch (JSONException e) {
            Log.e(TAG,e.toString());
            e.printStackTrace();
        }
        return skillsModel;
    }
}
