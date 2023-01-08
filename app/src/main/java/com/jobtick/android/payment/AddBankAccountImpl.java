package com.jobtick.android.payment;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.jobtick.android.AppExecutors;
import com.jobtick.android.BuildConfig;
import com.jobtick.android.utils.ConstantKey;
import com.jobtick.android.utils.HttpStatus;
import com.jobtick.android.utils.SessionManager;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Token;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import timber.log.Timber;

import static com.jobtick.android.utils.Constant.ADD_ACCOUNT_DETAILS;
import static com.jobtick.android.utils.Constant.BASE_URL;

public abstract class AddBankAccountImpl implements AddBankAccount {

    private final Context context;
    private final SessionManager sessionManager;

    private String btoken;

    public AddBankAccountImpl(Context context, SessionManager sessionManager) {
        this.context = context;
        this.sessionManager = sessionManager;
    }

    @Override
    public void add(String accountName, String bsb, String accountNumber) {

        AppExecutors.getInstance().getNetworkIO().execute(new Runnable() {
            @Override
            public void run() {

                Stripe.apiKey = ConstantKey.PUBLISHABLE_KEY;

                Map<String, Object> bankAccount = new HashMap<>();
                bankAccount.put("country", "AU");
                bankAccount.put("currency", "aud");
                bankAccount.put(
                        "account_holder_name",
                        accountName
                );
                bankAccount.put(
                        "account_holder_type",
                        "individual"
                );
                bankAccount.put("routing_number", bsb);
                bankAccount.put("account_number", accountNumber);
                Map<String, Object> params = new HashMap<>();
                params.put("bank_account", bankAccount);

                try {
                    Token token = Token.create(params);
                    System.out.println("Token success: id:" + token.getId());

                    btoken = token.getId();
                    addBankAccountDetails();

                } catch (StripeException e){
                    e.printStackTrace();

                    FirebaseCrashlytics.getInstance().recordException(e);
                    errorOnMainTread(new Exception("Something went wrong."));
                }
            }
        });
    }

    private void addBankAccountDetails() {

        StringRequest stringRequest = new StringRequest(StringRequest.Method.POST, BASE_URL + ADD_ACCOUNT_DETAILS,
                response -> {
                    Timber.e(response);
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        Timber.e(jsonObject.toString());
                        if (jsonObject.has("success") && !jsonObject.isNull("success")) {
                            if (jsonObject.getBoolean("success")) {
                                successOnMainTread();
                            } else {
                                errorOnMainTread(new Exception("Something went wrong."));
                            }
                        }
                    } catch (JSONException e) {
                        Timber.e(String.valueOf(e));
                        e.printStackTrace();
                    }


                },
                error -> {
                    NetworkResponse networkResponse = error.networkResponse;
                    if (networkResponse != null && networkResponse.data != null) {
                        String jsonError = new String(networkResponse.data);
                        // Print Error!
                        Timber.e(jsonError);
                        if (networkResponse.statusCode == HttpStatus.AUTH_FAILED) {
                            validationErrorOnMainTread(ErrorType.UnAuthenticatedUser, "user is not authenticated.");
                            return;
                        }
                        try {
                            JSONObject jsonObject = new JSONObject(jsonError);
                            JSONObject jsonObject_error = jsonObject.getJSONObject("error");
                            if (jsonObject_error.has("message")) {
                                errorOnMainTread(new Exception(jsonObject_error.getString("message")));
                            }

                        } catch (JSONException e) {
                            errorOnMainTread(e);
                            e.printStackTrace();
                        }
                    } else {
                        errorOnMainTread(new Exception("Something went wrong."));
                    }
                    Timber.e(error.toString());
                }) {


            @Override
            protected Map<String, String> getParams() {

                Map<String, String> map1 = new HashMap<String, String>();
                map1.put("btoken", btoken);

                return map1;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> map1 = new HashMap<String, String>();
                map1.put("authorization", sessionManager.getTokenType() + " " + sessionManager.getAccessToken());
                map1.put("Content-Type", "application/x-www-form-urlencoded");
                map1.put("X-Requested-With", "XMLHttpRequest");
                map1.put("Version", String.valueOf(BuildConfig.VERSION_CODE));
                return map1;
            }

        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, -1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);

    }

    private void successOnMainTread() {
        AppExecutors.getInstance().getMainThread().execute(new Runnable() {
            @Override
            public void run() {
                onSuccess();
            }
        });
    }

    private void errorOnMainTread(Exception e) {
        AppExecutors.getInstance().getMainThread().execute(new Runnable() {
            @Override
            public void run() {
                onError(e);
            }
        });
    }

    private void validationErrorOnMainTread(ErrorType errorType, String message) {
        AppExecutors.getInstance().getMainThread().execute(new Runnable() {
            @Override
            public void run() {
                onValidationError(errorType, message);
            }
        });
    }



    public abstract void onSuccess();
    public abstract void onError(Exception e);
    public abstract void onValidationError(ErrorType errorType, String message);

    public enum ErrorType{
        UnAuthenticatedUser
    }
}
