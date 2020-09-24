package com.jobtick.presenter;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.jobtick.activities.AuthActivity;

import org.json.JSONException;
import org.json.JSONObject;

public class AuthActivityPresenter {


    private AppCompatActivity context;

    public AuthActivityPresenter(AppCompatActivity context) {
        this.context = context;
    }



}
