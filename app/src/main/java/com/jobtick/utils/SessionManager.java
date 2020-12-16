package com.jobtick.utils;

import android.content.Context;
import android.content.SharedPreferences;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jobtick.R;
import com.jobtick.models.FilterModel;
import com.jobtick.models.PreviewCategorySetModel;
import com.jobtick.models.PreviewTaskSetModel;
import com.jobtick.models.UserAccountModel;

import java.lang.reflect.Type;


public class SessionManager {
    SharedPreferences pref;
    // Editor for Shared preferences
    SharedPreferences.Editor editor;

    // Context
    Context _context;

    // Shared pref mode
    int PRIVATE_MODE = 0;

    // Sharedpref file name
    public static final String KEY = "aa";

    // Constructor
    public SessionManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(context.getString(R.string.app_name), PRIVATE_MODE);
        editor = pref.edit();
    }

    public void setUserAccount(UserAccountModel user) {
        Gson gson = new Gson();
        String json = gson.toJson(user);
        editor.putString("user_account", json);
        editor.commit();
    }

    public UserAccountModel getUserAccount() {
        Gson gson = new Gson();
        String json = pref.getString("user_account", "");
        UserAccountModel user = gson.fromJson(json, UserAccountModel.class);
        return user;
    }

    //login sesstion manager used for the user stilled login not logout
    public void setLogin(Boolean login) {
        editor.putBoolean("login", login);
        editor.commit();
    }


    public void setFilter(FilterModel filterModel) {
        Gson gson = new Gson();
        String json = gson.toJson(filterModel);
        editor.putString("filter", json);
        editor.commit();
    }

    public FilterModel getFilter() {
        Gson gson = new Gson();
        String json = pref.getString("filter", "");
        return gson.fromJson(json, FilterModel.class);
    }


    public Boolean getLogin() {
        return pref.getBoolean("login", false);
    }


    //login sesstion manager used for the user stilled login not logout
    public void setAccessToken(String access_token) {
        editor.putString("access_token", access_token);
        editor.commit();
    }

    public String getAccessToken() {
        return pref.getString("access_token", null);
    }


    //login sesstion manager used for the user stilled login not logout
    public void setTokenType(String token_type) {
        editor.putString("token_type", token_type);
        editor.commit();
    }

    public String getTokenType() {
        return pref.getString("token_type", null);
    }


    public void setLatitude(String latitude) {
        editor.putString("latitude", latitude);
        editor.commit();
    }

    public String getLatitude() {
        return pref.getString("latitude", "0");
    }

    public void setQuickOffer(String offer) {
        editor.putString("quickOfferPref", offer);
        editor.commit();
    }

    public String getQuickOffer() {
        return pref.getString("quickOfferPref", "");
    }

    public void setLongitude(String longitude) {
        editor.putString("longitude", longitude);
        editor.commit();
    }

    public String getLongitude() {
        return pref.getString("longitude", "0");
    }


    public void setBankDetailAccountNumber(String accountNumber) {
        editor.putString("bank_account_number", accountNumber);
        editor.commit();
    }

    public String getBankDetailAccountNumber() {
        return pref.getString("bank_account_number", null);
    }

    public PreviewCategorySetModel getPreviewModel(Class<?> cls){
        String json = pref.getString(cls.getName(), null);
        Type type = new TypeToken<PreviewCategorySetModel>(){}.getType();
        Gson gson = new Gson();
        return gson.fromJson(json, type);
    }

    public void setPreviewModel(PreviewCategorySetModel previewCategorySetModel, Class<?> cls){
        Gson gson = new Gson();
        String previewJson = gson.toJson(previewCategorySetModel);
        editor.putString(cls.getName(), previewJson);
        editor.commit();
    }

    public PreviewTaskSetModel getPreviewTaskModel(Class<?> cls, boolean isMyTasks){
        String previewTaskSavedName = cls.getName() + isMyTasks;
        String json = pref.getString(previewTaskSavedName, null);
        Type type = new TypeToken<PreviewTaskSetModel>(){}.getType();
        Gson gson = new Gson();
        return gson.fromJson(json, type);
    }

    public void setPreviewTaskModel(PreviewTaskSetModel previewTaskSetModel, Class<?> cls, boolean isMyTasks){
        String previewTaskSavedName = cls.getName() + isMyTasks;
        Gson gson = new Gson();
        String previewJson = gson.toJson(previewTaskSetModel);
        editor.putString(previewTaskSavedName, previewJson);
        editor.apply();
    }


  /*  public void setAdmin(AdminObject admin) {

        Gson gson = new Gson();
        String json = gson.toJson(admin);
        editor.putString("admin", json);
        editor.commit();

    }

    public AdminObject getAdmin() {

        Gson gson = new Gson();
        String json = pref.getString("admin", "");

//        System.out.println("get admin---"+json);
        AdminObject admin = gson.fromJson(json, AdminObject.class);

        return admin;
    }


    public void setDemoHighScore(int demo_high_score) {

        editor.putInt("demo_high", demo_high_score);
        editor.commit();

    }

    public int getDemoHighScore() {

       return pref.getInt("demo_high", 0);


    }





    public void setIntro(Boolean intro) {

        editor.putBoolean("intro", intro);
        editor.commit();

    }

    public Boolean getIntro() {

        return pref.getBoolean("intro", false);


    }









    //login sesstion manager used for the user stilled login not logout
    public void setPhoneNumber(Boolean phoneNumber){
        editor.putBoolean("phone", phoneNumber);
        editor.commit();
    }
    public Boolean getPhoneNumber(){
        return pref.getBoolean("phone", false);
    }













    public void setMainActivity(Boolean MainActivity) {

        editor.putBoolean("MainActivity", MainActivity);
        editor.commit();

    }

    public Boolean getMainActivity() {

        return pref.getBoolean("MainActivity", false);


    }






    public void setQuizGameListActivity(Boolean QuizGameListActivity) {

        editor.putBoolean("QuizGameListActivity", QuizGameListActivity);
        editor.commit();

    }

    public Boolean getQuizGameListActivity() {

        return pref.getBoolean("QuizGameListActivity", false);


    }



    public void setDemoQuizDetailsActivity(Boolean DemoQuizDetailsActivity) {

        editor.putBoolean("DemoQuizDetailsActivity", DemoQuizDetailsActivity);
        editor.commit();

    }

    public Boolean getDemoQuizDetailsActivity() {

        return pref.getBoolean("DemoQuizDetailsActivity", false);


    }




    public void setQuizDetailsActivity(Boolean QuizDetailsActivity) {

        editor.putBoolean("QuizDetailsActivity", QuizDetailsActivity);
        editor.commit();

    }

    public Boolean getQuizDetailsActivity() {

        return pref.getBoolean("QuizDetailsActivity", false);


    }




    public void setPlayQuizActivity(Boolean PlayQuizActivity) {

        editor.putBoolean("PlayQuizActivity", PlayQuizActivity);
        editor.commit();

    }

    public Boolean getPlayQuizActivity() {

        return pref.getBoolean("PlayQuizActivity", false);


    }





    public void setUserCreateGameActivity(Boolean UserCreateGameActivity) {

        editor.putBoolean("UserCreateGameActivity", UserCreateGameActivity);
        editor.commit();

    }

    public Boolean getUserCreateGameActivity() {

        return pref.getBoolean("UserCreateGameActivity", false);


    }




    public void setCreditsTransactionActivity(Boolean CreditsTransactionActivity) {

        editor.putBoolean("CreditsTransactionActivity", CreditsTransactionActivity);
        editor.commit();

    }

    public Boolean getCreditsTransactionActivity() {

        return pref.getBoolean("CreditsTransactionActivity", false);


    }



    public void setPlayQuizQuestionNumber(int PlayQuizQuestionNumber) {

        editor.putInt("PlayQuizQuestionNumber", PlayQuizQuestionNumber);
        editor.commit();

    }

    public int getPlayQuizQuestionNumber() {

        return pref.getInt("PlayQuizQuestionNumber", 0);


    }
*/


    public void clearall() {
        editor.clear();
        editor.commit();
    }


}
