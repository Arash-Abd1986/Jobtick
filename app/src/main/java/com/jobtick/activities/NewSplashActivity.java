package com.jobtick.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
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
import com.jobtick.R;
import com.jobtick.models.UserAccountModel;
import com.jobtick.utils.Constant;
import com.jobtick.utils.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NewSplashActivity extends AppCompatActivity {
    SessionManager sessionManager;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.logo)
    ImageView logo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_splash);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        ButterKnife.bind(this);
        ViewAnimator
                .animate(logo)
                .duration(2000)
                .alpha(0,1)
                .scale(0.9f,1)
                .start();

        Handler handler = new Handler();
        handler.postDelayed(() -> {
            sessionManager = new SessionManager(NewSplashActivity.this);
            login();
        },2000);
    }

    private void login(){
        if (sessionManager.getLogin()) {
            getAccountDetails();
        } else {
            Intent sign = new Intent(NewSplashActivity.this, SinginSingupAcitivity.class);
            sign.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            sign.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(sign);
            finish();
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

                        Intent main = new Intent(NewSplashActivity.this, DashboardActivity.class);
                        main.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        main.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(main);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {

                }) {


            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> map1 = new HashMap<String, String>();

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
}