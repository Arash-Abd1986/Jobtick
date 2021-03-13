package com.jobtick.android.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.widget.ImageView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.github.florent37.viewanimator.ViewAnimator;
import com.jobtick.android.R;
import android.annotation.SuppressLint;

import com.jobtick.android.models.UserAccountModel;
import com.jobtick.android.utils.Constant;
import com.jobtick.android.utils.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NewSplashActivity extends AppCompatActivity {
    SessionManager sessionManager;
    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_splash);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        ButterKnife.bind(this);


        Handler handler = new Handler();
        handler.postDelayed(() -> {
            sessionManager = new SessionManager(NewSplashActivity.this);
            login();
        },2000);
    }

    private void login(){
        if (sessionManager.getTokenType() != null && sessionManager.getAccessToken() != null) {
            getAccountDetails();
        } else {
            openSignUpPage();
        }
    }

    private void getAccountDetails() {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Constant.URL_GET_ACCOUNT,
                response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        JSONObject jsonObject_data = jsonObject.getJSONObject("data");

                        UserAccountModel userAccountModel = new UserAccountModel().getJsonToModel(jsonObject_data);
                        sessionManager.setUserAccount(userAccountModel);

                        if(sessionManager.getUserAccount().getAccount_status().isBasic_info()){
                            sessionManager.setLatitude(Double.toString(userAccountModel.getLatitude()));
                            sessionManager.setLongitude(Double.toString(userAccountModel.getLongitude()));
                            Intent main = new Intent(NewSplashActivity.this, DashboardActivity.class);
                            main.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            main.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(main);
                        }
                        else{
                            Intent main = new Intent(NewSplashActivity.this, CompleteRegistrationActivity.class);
                            main.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            main.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(main);
                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                        openSignUpPage();
                    }
                },
                error -> openSignUpPage()) {


            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> map1 = new HashMap<>();

                map1.put("authorization", sessionManager.getTokenType() + " " + sessionManager.getAccessToken());
                map1.put("Content-Type", "application/x-www-form-urlencoded");
                map1.put("X-Requested-With", "XMLHttpRequest");
                return map1;
            }

        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, -1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(NewSplashActivity.this);
        requestQueue.add(stringRequest);
    }

    private void openSignUpPage(){
        Intent intent = new Intent(NewSplashActivity.this, SigInSigUpActivity.class);
        startActivity(intent);
        finish();
    }

}