package com.jobtick.android.payment;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.jobtick.android.utils.Constant;
import com.jobtick.android.utils.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public abstract class VerifyPhoneNumberImpl implements VerifyPhoneNumber {

    private final Context context;
    private final SessionManager sessionManager;

    private String phoneNumber;
    private String hashCheckToken;

    public VerifyPhoneNumberImpl(Context context, SessionManager sessionManager) {
        this.context = context;
        this.sessionManager = sessionManager;
    }

    @Override
    public void sendOTP(String phoneNumber) {
        this.phoneNumber = phoneNumber;

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.URL_SEND_OTP,
                response -> {
                    try {

                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.has("success") && !jsonObject.isNull("success")) {
                            if (jsonObject.getBoolean("success") && !jsonObject.isNull("data")) {
                                hashCheckToken = jsonObject.getJSONObject("data").getString("hash_check_token");
                                onSuccess(SuccessType.OTP);
                            }
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        onError(e);
                    }

                },
                error -> {
                    NetworkResponse networkResponse = error.networkResponse;
                    if (networkResponse != null && networkResponse.data != null) {
                        String jsonError = new String(networkResponse.data);
                        // Print Error!
                        try {
                            JSONObject jsonObject = new JSONObject(jsonError);
                            JSONObject jsonObject_error = jsonObject.getJSONObject("error");
                            String message = jsonObject_error.getString("message");
                            if (message.equalsIgnoreCase("unauthorized")) {
                                onValidationError(ErrorType.UN_AUTHENTICATED_USER, "user is not authorized.");
                            }
                            if (Objects.equals(jsonObject_error.getString("error_code"), "400")) {
                                onError(new Exception("This mobile number is already verified."));
                            }
                            else
                                onError(new Exception(message));

                        } catch (JSONException e) {
                            e.printStackTrace();
                            onError(e);
                        }
                    } else {
                        onError(new Exception("something went wrong."));
                    }
                }) {


            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> map1 = new HashMap<String, String>();
                map1.put("authorization", sessionManager.getTokenType() + " " + sessionManager.getAccessToken());
                map1.put("Content-Type", "application/x-www-form-urlencoded");
                map1.put("X-Requested-With", "XMLHttpRequest");
                return map1;
            }

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> map1 = new HashMap<String, String>();
                map1.put("mobile", phoneNumber);
                map1.put("dialing_code", "+61");
                return map1;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, -1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }


    @Override
    public void verify(String otp) {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.URL_OTP_VERIFICATION,
                response -> {
                    try {

                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.has("success") && !jsonObject.isNull("success")) {
                            if (jsonObject.getBoolean("success")) {
                                onSuccess(SuccessType.Verify);
                            }
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        onError(e);
                    }

                },
                error -> {
                    NetworkResponse networkResponse = error.networkResponse;
                    if (networkResponse != null && networkResponse.data != null) {
                        String jsonError = new String(networkResponse.data);
                        // Print Error!
                        try {
                            JSONObject jsonObject = new JSONObject(jsonError);
                            JSONObject jsonObject_error = jsonObject.getJSONObject("error");
                            String message = jsonObject_error.getString("message");
                            if (message.equalsIgnoreCase("unauthorized")) {
                                onValidationError(ErrorType.UN_AUTHENTICATED_USER, "user is not authorized.");
                            }
                            onValidationError(ErrorType.GENERAL, message);

                        } catch (JSONException e) {
                            e.printStackTrace();
                            onError(e);
                        }
                    } else {
                        onError(new Exception("something went wrong"));
                    }
                }) {


            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> map1 = new HashMap<String, String>();
                map1.put("authorization", sessionManager.getTokenType() + " " + sessionManager.getAccessToken());
                map1.put("Content-Type", "application/x-www-form-urlencoded");
                map1.put("X-Requested-With", "XMLHttpRequest");
                return map1;
            }

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> map1 = new HashMap<String, String>();
                map1.put("hash_check_token", hashCheckToken);
                map1.put("otp", otp);
                return map1;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, -1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }

    @Override
    public void resendOTP(String phoneNumber){

    }

    public abstract void onSuccess(SuccessType successType);
    public abstract void onError(Exception e);
    public abstract void onValidationError(ErrorType errorType, String message);

    public enum ErrorType{
        UN_AUTHENTICATED_USER,
        GENERAL
    }

    public enum SuccessType {
        OTP, Verify
    }
}
