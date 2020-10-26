package com.jobtick.activities;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.jobtick.R;
import com.jobtick.models.UserAccountModel;
import com.jobtick.utils.Constant;
import com.jobtick.utils.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SplashActivity extends ActivityBase {

    SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        sessionManager = new SessionManager(this);
        permission();
    }

    public void permission() {
        Dexter.withContext(this)
                .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        startLocationUpdates();
                        if (sessionManager.getLogin()) {

                            getAccountDetails();

                        } else {
                            Intent sign = new Intent(SplashActivity.this, SinginSingupAcitivity.class);
                            sign.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            sign.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(sign);
                        }
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        if (response.isPermanentlyDenied()) {
                            // open device settings when the permission is
                            // denied permanently
                            openSettings();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();

    }

    private void getAccountDetails() {
        showProgressDialog();

        StringRequest stringRequest = new StringRequest(Request.Method.GET, Constant.URL_GET_ACCOUNT,
                response -> {
                    Log.e("responce_url", response);

                    hideProgressDialog();
                    try {

                        JSONObject jsonObject = new JSONObject(response);
                        Log.e("json", jsonObject.toString());
                        JSONObject jsonObject_data = jsonObject.getJSONObject("data");

                        UserAccountModel userAccountModel = new UserAccountModel().getJsonToModel(jsonObject_data);
                        sessionManager.setUserAccount(userAccountModel);

                        Intent main = new Intent(SplashActivity.this, DashboardActivity.class);
                        main.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        main.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(main);

                    } catch (JSONException e) {
                        Log.e("EXCEPTION", String.valueOf(e));
                        e.printStackTrace();

                    }


                },
                error -> {
                    errorHandle1(error.networkResponse);
                    hideProgressDialog();
                }) {


            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> map1 = new HashMap<String, String>();

                map1.put("authorization", sessionManager.getTokenType() + " " + sessionManager.getAccessToken());
                map1.put("Content-Type", "application/x-www-form-urlencoded");
                map1.put("X-Requested-With", "XMLHttpRequest");
                return map1;
            }

        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, -1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(SplashActivity.this);
        requestQueue.add(stringRequest);
        Log.e("URL", stringRequest.getUrl());
    }
}
