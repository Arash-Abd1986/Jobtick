package com.jobtick.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.jobtick.R;
import com.jobtick.fragments.AbstractVerifyAccountFragment;
import com.jobtick.fragments.ForgotPassword1Fragment;
import com.jobtick.fragments.ForgotPassword2Fragment;
import com.jobtick.fragments.ForgotPassword3Fragment;
import com.jobtick.fragments.SignInFragment;
import com.jobtick.fragments.SignUpFragment;
import com.jobtick.fragments.VerifyAccountFragment;
import com.jobtick.models.UserAccountModel;
import com.jobtick.presenter.AuthActivityPresenter;
import com.jobtick.utils.Constant;
import com.jobtick.utils.Helper;
import com.jobtick.utils.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

public class AuthActivity extends ActivityBase {

    public final String DEVICE = "Android";
    private static final int RC_SIGN_IN = 234;
    SignInFragment signInFragment;
    VerifyAccountFragment verifyAccountFragment;
    @BindView(R.id.auth_layout)
    FrameLayout authLayout;
    AuthActivityPresenter authActivityPresenter;
    CallbackManager callbackManager;
    GoogleSignInClient mGoogleSignInClient;
    SessionManager sessionManager;
    ForgotPassword1Fragment forgotPassword1Fragment;
    SignUpFragment signUpFragment;

    EditTextError editTextError;
    OnResendOtp onResendOtp;

    public void setEditTextError(EditTextError editTextError) {
        this.editTextError = editTextError;
    }

    public void forgotPasswordverification(String email, String otp) {
        showProgressDialog();
        String str_email = email;
        String str_otp = otp;
        Helper.closeKeyboard(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.URL_VERIFY_OTP,
                response -> {
                    hideProgressDialog();
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        Bundle bundle = new Bundle();
                        Fragment fragment = new ForgotPassword3Fragment();
                        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                        ft.replace(R.id.auth_layout, fragment);
                        bundle.putString("email", str_email);
                        bundle.putString("otp", otp);
                        fragment.setArguments(bundle);
                        ft.commit();
                    } catch (JSONException e) {
                        e.printStackTrace();
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
                                Fragment fragment = new SignInFragment();
                                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                                ft.replace(R.id.container, fragment);
                                ft.commit();
                            }
                            if (jsonObject_error.has("errors")) {
                                JSONObject jsonObject_errors = jsonObject_error.getJSONObject("errors");
                            }
                            showToast(message, AuthActivity.this);


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        showToast("Something Went Wrong", AuthActivity.this);
                    }
                    hideProgressDialog();
                }) {


            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> map1 = new HashMap<String, String>();
                map1.put("Content-Type", "application/x-www-form-urlencoded");
                map1.put("X-Requested-With", "XMLHttpRequest");
                return map1;
            }

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> map1 = new HashMap<String, String>();
                map1.put("email", str_email);
                map1.put("otp", str_otp);

                return map1;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, -1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }

    public void forgotPasswordSpecialVerification(String email, String otp) {
        showProgressDialog();
        String str_email = email;
        String str_otp = otp;
        Helper.closeKeyboard(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.URL_RESET_PASSWORD_VERIFY_OTP,
                response -> {
                    hideProgressDialog();
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        Bundle bundle = new Bundle();
                        Fragment fragment = new ForgotPassword3Fragment();
                        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                        ft.replace(R.id.auth_layout, fragment);
                        bundle.putString("email", str_email);
                        bundle.putString("otp", otp);
                        fragment.setArguments(bundle);
                        ft.commit();
                    } catch (JSONException e) {
                        e.printStackTrace();
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
                                Fragment fragment = new SignInFragment();
                                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                                ft.replace(R.id.container, fragment);
                                ft.commit();
                            }
                            if (jsonObject_error.has("errors")) {
                                JSONObject jsonObject_errors = jsonObject_error.getJSONObject("errors");
                            }
                            showToast(message, AuthActivity.this);


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        showToast("Something Went Wrong", AuthActivity.this);
                    }
                    hideProgressDialog();
                }) {


            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> map1 = new HashMap<String, String>();
                map1.put("Content-Type", "application/x-www-form-urlencoded");
                map1.put("X-Requested-With", "XMLHttpRequest");
                return map1;
            }

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> map1 = new HashMap<String, String>();
                map1.put("email", str_email);
                map1.put("otp", str_otp);

                return map1;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, -1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }

    public void resetPassword(String email, String otp, String new_password) {
        showProgressDialog();
        String str_email = email;
        String str_otp = otp;
        String str_new_password = new_password;
        Helper.closeKeyboard(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.URL_FORGOT_PASSWORD,
                response -> {

                    hideProgressDialog();
                    try {

                        JSONObject jsonObject = new JSONObject(response);
                        JSONObject jsonObject_data = jsonObject.getJSONObject("data");

                        sessionManager.setAccessToken(jsonObject_data.getString("access_token"));
                        sessionManager.setTokenType(jsonObject_data.getString("token_type"));

                        JSONObject jsonObject_user = jsonObject_data.getJSONObject("user");
                        UserAccountModel userAccountModel = new UserAccountModel().getJsonToModel(jsonObject_user);
                        sessionManager.setUserAccount(userAccountModel);

                        sessionManager.setLogin(true);

                        Intent intent = new Intent(AuthActivity.this, DashboardActivity.class);
                        openActivity(intent);
                    } catch (JSONException e) {
                        Log.e("EXCEPTION", String.valueOf(e));
                        e.printStackTrace();

                    }


                },
                error -> {
                    NetworkResponse networkResponse = error.networkResponse;
                    if (networkResponse != null && networkResponse.data != null) {
                        String jsonError = new String(networkResponse.data);
                        // Print Error!
                        Log.e("intent22", jsonError);
                        try {
                            JSONObject jsonObject = new JSONObject(jsonError);
                            JSONObject jsonObject_error = jsonObject.getJSONObject("error");
                            String message = jsonObject_error.getString("message");
                            if (message.equalsIgnoreCase("unauthorized")) {
                                Fragment fragment = new SignInFragment();
                                switchContent(fragment);
                              /*  FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                                ft.replace(R.id.container, fragment);
                                ft.commit();*/
                            }
                            if (jsonObject_error.has("errors")) {
                                JSONObject jsonObject_errors = jsonObject_error.getJSONObject("errors");
                            }
                            showToast(message, AuthActivity.this);


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        showToast("Something Went Wrong", AuthActivity.this);
                    }
                    hideProgressDialog();
                }) {


            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> map1 = new HashMap<String, String>();
                map1.put("Content-Type", "application/x-www-form-urlencoded");
                map1.put("X-Requested-With", "XMLHttpRequest");
                return map1;
            }

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> map1 = new HashMap<String, String>();
                map1.put("email", str_email);
                map1.put("otp", str_otp);
                map1.put("new_password", str_new_password);
                return map1;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, -1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }

    public interface EditTextError {
        void error(String email, String password);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);




        getWindow().clearFlags(
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                        | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        authActivityPresenter = new AuthActivityPresenter(this);
        ButterKnife.bind(this);
        sessionManager = new SessionManager(this);
        signInFragment = new SignInFragment();

        verifyAccountFragment = new VerifyAccountFragment();
        forgotPassword1Fragment = new ForgotPassword1Fragment();
        signUpFragment = new SignUpFragment();


        if (getIntent() != null && getIntent().getExtras() != null) {
            if (getIntent().hasExtra("type")) {

                if (getIntent().getExtras().getString("type").equals("Signin")) {
                    signInFragment = new SignInFragment();
                    switchContent(signInFragment);
                } else {
                    signUpFragment = new SignUpFragment();
                    switchContent(signUpFragment);

                }
            } else {
                switchContent(signInFragment);

            }

        } else {
            switchContent(signInFragment);

        }
        // switchContent(verifyAccountFragment);

        callbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        // App code
                        boolean loggedIn = AccessToken.getCurrentAccessToken() == null;
                        facebookGetRequiredParameter(loginResult);
                    }

                    @Override
                    public void onCancel() {
                        // App code
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        // App code
                    }
                });

        init();
    }


    protected void facebookGetRequiredParameter(LoginResult loginResult) {
        showProgressDialog();
        GraphRequest request = GraphRequest.newMeRequest(
                loginResult.getAccessToken(),
                (object, response) -> {
                    // Application code
                    try {

                        String email = "", firstName = "", lastName = "";
                        if (object.has("email"))
                            email = object.getString("email");
                        if (object.has("first_name"))
                            firstName = object.getString("first_name");
                        if (object.has("last_name"))
                            lastName = object.getString("last_name");

                        fbSubmitData(loginResult.getAccessToken().getToken(), email, firstName, lastName);

                    } catch (JSONException e) {
                        e.printStackTrace();
                        hideProgressDialog();
                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,email,first_name,last_name,gender");
        request.setParameters(parameters);
        request.executeAsync();


    }

    protected void fbSubmitData(String str_access_token, String str_email, String str_fname, String str_lname) {


        String str_fcm_token = getToken();
        String str_device_id = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        String str_device = "Android";
        Helper.closeKeyboard(this);


        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.URL_SIGNIN_FACEBOOK,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Log.e("responce_url", response);

                        hideProgressDialog();
                        try {

                            JSONObject jsonObject = new JSONObject(response);
                            Log.e("json", jsonObject.toString());
                            JSONObject jsonObject_data = jsonObject.getJSONObject("data");
                            sessionManager.setAccessToken(jsonObject_data.getString("access_token"));
                            sessionManager.setTokenType(jsonObject_data.getString("token_type"));
                            JSONObject jsonObject_user = jsonObject_data.getJSONObject("user");
                            UserAccountModel userAccountModel = new UserAccountModel().getJsonToModel(jsonObject_user);
                            sessionManager.setUserAccount(userAccountModel);
                            sessionManager.setLogin(true);
                            // showToast("Login SuccessFully!!!", AuthActivity.this);
                            Intent intent = new Intent(AuthActivity.this, DashboardActivity.class);
                            openActivity(intent);


                        } catch (JSONException e) {
                            Log.e("EXCEPTION", String.valueOf(e));
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        NetworkResponse networkResponse = error.networkResponse;
                        if (networkResponse != null && networkResponse.data != null) {


                            String jsonError = new String(networkResponse.data);
                            // Print Error!
                            Log.e("intent22", jsonError);

                            try {
                                JSONObject jsonObject = new JSONObject(jsonError);

                                JSONObject jsonObject_error = jsonObject.getJSONObject("error");

                                if (jsonObject_error.has("message")) {
                                    showToast(jsonObject_error.getString("message"), AuthActivity.this);
                                }
                                if (jsonObject_error.has("errors")) {
                                    JSONObject jsonObject_errors = jsonObject_error.getJSONObject("errors");

                                 /*   if (jsonObject_errors.has("email")) {
                                        JSONArray jsonArray_mobile = jsonObject_errors.getJSONArray("email");
                                        String email = jsonArray_mobile.getString(0);
                                        edtEmailAddress.setError(email);
                                    }
                                    if (jsonObject_errors.has("password")) {
                                        JSONArray jsonArray_mobile = jsonObject_errors.getJSONArray("password");
                                        String password = jsonArray_mobile.getString(0);
                                        edtPassword.setError(password);
                                    }*/


                                }


                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else {
                            //  ((CredentialActivity)getActivity()).showToast("Something Went Wrong",getActivity());
                        }
                        Log.e("error", error.toString());
                        hideProgressDialog();
                    }
                }) {


            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> map1 = new HashMap<String, String>();
                map1.put("Content-Type", "application/x-www-form-urlencoded");
                map1.put("X-Requested-With", "XMLHttpRequest");
                return map1;
            }

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> map1 = new HashMap<String, String>();


                map1.put("fname", str_fname);
                map1.put("lname", str_lname);
                map1.put("email", str_email);
                map1.put("device_token", str_device_id);
                map1.put("device_type", str_device);
                map1.put("fcm_token", str_fcm_token);
                map1.put("access_token", str_access_token);
                map1.put("latitude", sessionManager.getLatitude());
                map1.put("longitude", sessionManager.getLongitude());

                return map1;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, -1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }


    public void switchContent(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        while (fragmentManager.popBackStackImmediate()) ;

        if (fragment != null) {
            FragmentTransaction transaction = fragmentManager
                    .beginTransaction().setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out,
                            android.R.anim.fade_in, android.R.anim.fade_out);
            // Replace whatever is in the content_fragment view with this fragment
            transaction.replace(R.id.auth_layout, fragment);

            // Commit the transaction
            transaction.commit();

        }
    }

    protected void init() {
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.clientId))
                .requestEmail()
                .build();
        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // ...
                    }
                });


        // Check for existing Google Sign In account, if the user is already signed in
        // the GoogleSignInAccount will be non-null.
      /*  GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if (account != null)
            signInUpdateUI(account);*/


    }


    public void signInWithGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    public void facebookLogin() {
        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("email"));
    }

    public void login(String email, String password) {
        showProgressDialog();
        String str_email = email;
        String str_password = password;
        String str_fcm_token = getToken();
        String str_device_id = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        String str_device = "Android";
        Helper.closeKeyboard(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.URL_SIGNIN,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Log.e("responce_url", response);

                        hideProgressDialog();
                        try {

                            JSONObject jsonObject = new JSONObject(response);

                            Log.e("json", jsonObject.toString());
                            JSONObject jsonObject_data = jsonObject.getJSONObject("data");
                            sessionManager.setAccessToken(jsonObject_data.getString("access_token"));
                            sessionManager.setTokenType(jsonObject_data.getString("token_type"));
                            JSONObject jsonObject_user = jsonObject_data.getJSONObject("user");
                            UserAccountModel userAccountModel = new UserAccountModel().getJsonToModel(jsonObject_user);
                            sessionManager.setUserAccount(userAccountModel);
                            sessionManager.setLogin(true);
                            Intent intent = new Intent(AuthActivity.this, DashboardActivity.class);
                            openActivity(intent);


                        } catch (JSONException e) {
                            e.printStackTrace();

                        }


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


                            if (jsonObject_error.has("error_code")) {
                                if (jsonObject_error.getInt("error_code") == 1002) {
                                    resendOtp(str_email, str_password);
                                    return;
                                }
                            }


                            if (jsonObject_error.has("errors")) {

                                JSONObject jsonObject_errors = jsonObject_error.getJSONObject("errors");

                                String error_email = null, error_password = null;
                                if (jsonObject_errors.has("email")) {
                                    JSONArray jsonArray_mobile = jsonObject_errors.getJSONArray("email");
                                    error_email = jsonArray_mobile.getString(0);
                                }
                                if (jsonObject_errors.has("password")) {
                                    JSONArray jsonArray_mobile = jsonObject_errors.getJSONArray("password");
                                    error_password = jsonArray_mobile.getString(0);
                                }
                                if (jsonObject_errors.has("device_token")) {
                                    JSONArray jsonArray_device_token = jsonObject_errors.getJSONArray("device_token");
                                    showToast(jsonArray_device_token.getString(0), AuthActivity.this);
                                }
                                if (jsonObject_errors.has("device_type")) {
                                    JSONArray jsonArray_device_type = jsonObject_errors.getJSONArray("device_type");
                                    showToast(jsonArray_device_type.getString(0), AuthActivity.this);
                                }
                                if (jsonObject_errors.has("fcm_token")) {
                                    JSONArray jsonArray_fcm_token = jsonObject_errors.getJSONArray("fcm_token");
                                    showToast(jsonArray_fcm_token.getString(0), AuthActivity.this);
                                }
                                if (jsonObject_errors.has("latitude")) {
                                    JSONArray jsonArray_latitude = jsonObject_errors.getJSONArray("latitude");
                                    showToast(jsonArray_latitude.getString(0), AuthActivity.this);
                                }
                                if (jsonObject_errors.has("longitude")) {
                                    JSONArray jsonArray_longitude = jsonObject_errors.getJSONArray("longitude");
                                    showToast(jsonArray_longitude.getString(0), AuthActivity.this);
                                }


                                editTextError.error(error_email, error_password);
                            } else {
                                String message = jsonObject_error.getString("message");
                                showToast(message, AuthActivity.this);
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        showToast("Something Went Wrong", AuthActivity.this);
                    }
                    hideProgressDialog();
                }) {


            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> map1 = new HashMap<String, String>();
                map1.put("Content-Type", "application/x-www-form-urlencoded");
                map1.put("X-Requested-With", "XMLHttpRequest");
                return map1;
            }

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> map1 = new HashMap<String, String>();
                if (str_email != null)
                    map1.put("email", str_email);
                if (str_password != null)
                    map1.put("password", str_password);
                if (str_device_id != null)
                    map1.put("device_token", str_device_id);
                if (str_device != null)
                    map1.put("device_type", str_device);
                if (str_fcm_token != null)
                    map1.put("fcm_token", str_fcm_token);
//                if (sessionManager.getLatitude() != null)
//                    map1.put("latitude", sessionManager.getLatitude());
//                if (sessionManager.getLongitude() != null)
//                    map1.put("longitude", sessionManager.getLongitude());

                return map1;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, -1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }

    private String getToken() {
        final String[] token = {""};
        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
            @Override
            public void onComplete(@NonNull Task<InstanceIdResult> task) {
                if (!task.isSuccessful()) {
                    return;
                }

                // Get new Instance ID token
                token[0] = task.getResult().getToken();
            }
        });
        return token[0];
    }


    private void resendOtp(String email, String password) {

        String str_email = email;
        String str_password = password;

        Helper.closeKeyboard(this);


        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.URL_RESEND_OTP,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Log.e("responce_url", response);

                        hideProgressDialog();
                        // showToast("Check your inbox",AuthActivity.this);
                        Bundle bundle = new Bundle();
                        Fragment fragment = new VerifyAccountFragment();
                        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                        ft.replace(R.id.auth_layout, fragment);
                        bundle.putString("email", str_email);
                        bundle.putString("password", str_password);
                        fragment.setArguments(bundle);
                        ft.commit();

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        NetworkResponse networkResponse = error.networkResponse;
                        if (networkResponse != null && networkResponse.data != null) {
                            String jsonError = new String(networkResponse.data);
                            // Print Error!
                            Log.e("intent22", jsonError);

                            try {
                                JSONObject jsonObject = new JSONObject(jsonError);

                                JSONObject jsonObject_error = jsonObject.getJSONObject("error");

                                String message = jsonObject_error.getString("message");

                                showToast(message, AuthActivity.this);


                                if (jsonObject_error.has("errors")) {

                                    JSONObject jsonObject_errors = jsonObject_error.getJSONObject("errors");

                                }


                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else {
                            showToast("Something Went Wrong", AuthActivity.this);
                        }
                        Log.e("error", error.toString());
                        hideProgressDialog();
                    }
                }) {


            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> map1 = new HashMap<String, String>();
                map1.put("Content-Type", "application/x-www-form-urlencoded");
                map1.put("X-Requested-With", "XMLHttpRequest");
                return map1;
            }

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> map1 = new HashMap<String, String>();
                map1.put("email", str_email);

                return map1;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, -1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);


    }


    private void openActivity(Intent intent) {
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
            Log.d("LoginGoogle","1:"+task.toString());
        } else {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            signInUpdateUI(account);
            Log.d("LoginGoogle","account:"+account.toString());
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            signInUpdateUI(null);
        }
    }

    String str_id_token = "";
    String str_lname = "";
    String str_fname = "";


    private void signInUpdateUI(GoogleSignInAccount account) {
        try {
            showProgressDialog();

            if (account.getDisplayName() != null) {
                String[] displayName = account.getDisplayName().toString().split(" ");
                int size_of_displayName = displayName.length;
                str_lname = displayName[size_of_displayName - 1];
                int count = 0;

                while (size_of_displayName - 1 > count) {
                    str_fname = str_fname + displayName[count];
                    count++;
                }
            }

            // String str_lname = edtLname.getText().toString().trim();
            String str_email = account.getEmail();
            // String str_password = edtPassword.getText().toString().trim();
            String str_fcm_token = getToken();
            String str_device_id = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
            String str_device = "Android";


            Runnable runnable = () -> {
                try {
                    String scope = "oauth2:" + Scopes.EMAIL + " " + Scopes.PROFILE;
                    str_id_token = GoogleAuthUtil.getToken(getApplicationContext(), account.getAccount(), scope, new Bundle());
                    Helper.closeKeyboard(this);


                    String finalStr_fname = str_fname;
                    String finalStr_lname = str_lname;
                    StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.URL_SIGNIN_GOOGLE,
                            response -> {
                                Log.e("responce_url", response);

                                hideProgressDialog();
                                try {

                                    JSONObject jsonObject = new JSONObject(response);
                                    Log.e("json", jsonObject.toString());
                                    JSONObject jsonObject_data = jsonObject.getJSONObject("data");

                                    sessionManager.setAccessToken(jsonObject_data.getString("access_token"));
                                    sessionManager.setTokenType(jsonObject_data.getString("token_type"));

                                    JSONObject jsonObject_user = jsonObject_data.getJSONObject("user");
                                    UserAccountModel userAccountModel = new UserAccountModel().getJsonToModel(jsonObject_user);
                                    sessionManager.setUserAccount(userAccountModel);

                                    sessionManager.setLogin(true);

                                    //   showToast("Login SuccessFully!!!", AuthActivity.this);

                                    Intent intent = new Intent(AuthActivity.this, DashboardActivity.class);
                                    openActivity(intent);


                                } catch (JSONException e) {
                                    Log.e("EXCEPTION", String.valueOf(e));
                                    e.printStackTrace();

                                }


                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    NetworkResponse networkResponse = error.networkResponse;
                                    if (networkResponse != null && networkResponse.data != null) {
                                        String jsonError = new String(networkResponse.data);
                                        // Print Error!
                                        Log.e("intent22", jsonError);

                                        try {
                                            JSONObject jsonObject = new JSONObject(jsonError);

                                            JSONObject jsonObject_error = jsonObject.getJSONObject("error");

                                            if (jsonObject_error.has("message")) {
                                                showToast(jsonObject_error.getString("message"), AuthActivity.this);
                                            }
                                            if (jsonObject_error.has("errors")) {
                                                JSONObject jsonObject_errors = jsonObject_error.getJSONObject("errors");


                                            }


                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    } else {
                                        showToast("Something Went Wrong", AuthActivity.this);
                                    }
                                    Log.e("error", error.toString());
                                    hideProgressDialog();
                                }
                            }) {


                        @Override
                        public Map<String, String> getHeaders() throws AuthFailureError {
                            Map<String, String> map1 = new HashMap<String, String>();


                            map1.put("Content-Type", "application/x-www-form-urlencoded");
                            map1.put("X-Requested-With", "XMLHttpRequest");
                            return map1;
                        }

                        @Override
                        protected Map<String, String> getParams() {
                            Map<String, String> map1 = new HashMap<String, String>();


                            map1.put("fname", finalStr_fname);
                            map1.put("lname", finalStr_lname);
                            map1.put("email", str_email);
                            map1.put("device_token", str_device_id);
                            map1.put("device_type", str_device);
                            if(str_fcm_token != null && !str_fcm_token.isEmpty())
                                map1.put("fcm_token", str_fcm_token);
                            map1.put("access_token", str_id_token);
                            map1.put("latitude", sessionManager.getLatitude());
                            map1.put("longitude", sessionManager.getLongitude());

                            return map1;
                        }
                    };

                    stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, -1,
                            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                    RequestQueue requestQueue = Volley.newRequestQueue(this);
                    requestQueue.add(stringRequest);

                } catch (IOException e) {
                    e.printStackTrace();
                } catch (GoogleAuthException e) {
                    e.printStackTrace();
                }
            };
            AsyncTask.execute(runnable);


        } catch (Exception e) {
            hideProgressDialog();
        }

    }


    public void Signup(String email, String password) {
        showProgressDialog();
        String str_email = email;
        String str_password = password;
        String str_fcm_token = getToken();
        String str_device_id = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        String str_device = DEVICE;


        Helper.closeKeyboard(this);


        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.URL_SIGNUP,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Timber.tag("responce_url").e(response);

                        hideProgressDialog();
                        //  showToast("Check your inbox", AuthActivity.this);
                        Bundle bundle = new Bundle();
                        Fragment fragment = new VerifyAccountFragment();
                        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                        ft.replace(R.id.auth_layout, fragment);
                        bundle.putString("email", str_email);
                        bundle.putString("password", str_password);
                        fragment.setArguments(bundle);
                        ft.commit();

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        NetworkResponse networkResponse = error.networkResponse;
                        if (networkResponse != null && networkResponse.data != null) {
                            String jsonError = new String(networkResponse.data);
                            // Print Error!
                            Timber.tag("intent22").e(jsonError);

                            try {
                                JSONObject jsonObject = new JSONObject(jsonError);

                                JSONObject jsonObject_error = jsonObject.getJSONObject("error");

                                if (jsonObject_error.has("message")) {
                                    Timber.d("AuthActivity: " + jsonObject_error.getString("message"));
                                    showToast(jsonObject_error.getString("message"), AuthActivity.this);
                                }
                                if (jsonObject_error.has("errors")) {
                                    JSONObject jsonObject_errors = jsonObject_error.getJSONObject("errors");
                                    String error_email = "", error_password = "";
                                    if (jsonObject_errors.has("email")) {
                                        JSONArray jsonArray_mobile = jsonObject_errors.getJSONArray("email");
                                        error_email = jsonArray_mobile.getString(0);

                                    }
                                    if (jsonObject_errors.has("password")) {
                                        JSONArray jsonArray_mobile = jsonObject_errors.getJSONArray("password");
                                        error_password = jsonArray_mobile.getString(0);

                                    }
                                    // signUpFragment.error(error_email,error_password);
                                    editTextError.error("error_email", "error_password");

                                }


                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else {
                            showToast("Something Went Wrong", AuthActivity.this);
                        }
                        Timber.tag("error").e(error.toString());
                        hideProgressDialog();
                    }
                }) {


            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> map1 = new HashMap<String, String>();


                map1.put("Content-Type", "application/x-www-form-urlencoded");
                map1.put("X-Requested-With", "XMLHttpRequest");
                return map1;
            }

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> map1 = new HashMap<String, String>();


                map1.put("email", str_email);
                map1.put("password", str_password);
                map1.put("device_token", str_device_id);
                map1.put("device_type", str_device);
                map1.put("fcm_token", str_fcm_token);
                map1.put("latitude", sessionManager.getLatitude());
                map1.put("longitude", sessionManager.getLongitude());

                return map1;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, -1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }

    public void verification(String email, String password, String otp) {
        showProgressDialog();
        String str_email = email;
        String str_password = password;
        String str_otp = otp;
        Helper.closeKeyboard(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.URL_SIGNIN,
                response -> {
                    Log.e("responce_url", response);

                    hideProgressDialog();
                    try {

                        JSONObject jsonObject = new JSONObject(response);

                        Log.e("json", jsonObject.toString());


                        JSONObject jsonObject_data = jsonObject.getJSONObject("data");

                        sessionManager.setAccessToken(jsonObject_data.getString("access_token"));
                        sessionManager.setTokenType(jsonObject_data.getString("token_type"));

                        JSONObject jsonObject_user = jsonObject_data.getJSONObject("user");
                        UserAccountModel userAccountModel = new UserAccountModel().getJsonToModel(jsonObject_user);
                        sessionManager.setUserAccount(userAccountModel);

                        sessionManager.setLogin(true);

                        //  showToast("Login SuccessFully!!!", AuthActivity.this);

                        Intent intent = new Intent(AuthActivity.this, CompleteRegistrationActivity.class);
                        openActivity(intent);


                    } catch (JSONException e) {
                        Log.e("EXCEPTION", String.valueOf(e));
                        e.printStackTrace();

                    }


                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        NetworkResponse networkResponse = error.networkResponse;
                        if (networkResponse != null && networkResponse.data != null) {
                            String jsonError = new String(networkResponse.data);
                            // Print Error!
                            Log.e("intent22", jsonError);
                            try {
                                JSONObject jsonObject = new JSONObject(jsonError);
                                JSONObject jsonObject_error = jsonObject.getJSONObject("error");
                                String message = jsonObject_error.getString("message");
                                if (message.equalsIgnoreCase("unauthorized")) {
                                    Fragment fragment = new SignInFragment();
                                    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                                    ft.replace(R.id.container, fragment);
                                    ft.commit();
                                }
                                if (jsonObject_error.has("errors")) {
                                    JSONObject jsonObject_errors = jsonObject_error.getJSONObject("errors");
                                }
                                showToast(message, AuthActivity.this);


                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else {
                            showToast("Something Went Wrong", AuthActivity.this);
                        }
                        Log.e("error", error.toString());
                        hideProgressDialog();
                    }
                }) {


            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> map1 = new HashMap<String, String>();
                map1.put("Content-Type", "application/x-www-form-urlencoded");
                map1.put("X-Requested-With", "XMLHttpRequest");
                return map1;
            }

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> map1 = new HashMap<String, String>();
                map1.put("email", str_email);
                map1.put("password", str_password);
                map1.put("otp", str_otp);

                return map1;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, -1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }


    public void nextStepForgotPassowrd(String email) {
        showProgressDialog();
        String str_email = email;

        Helper.closeKeyboard(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.URL_RESET_PASSWORD,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Log.e("responce_url", response);

                        hideProgressDialog();
                        //  showToast("Check your inbox", AuthActivity.this);
                        Bundle bundle = new Bundle();
                        Fragment fragment = new ForgotPassword2Fragment();
                        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                        ft.replace(R.id.auth_layout, fragment);
                        bundle.putString("email", str_email);
                        fragment.setArguments(bundle);
                        ft.commit();

                    }
                },
                error -> {
                    NetworkResponse networkResponse = error.networkResponse;
                    if (networkResponse != null && networkResponse.data != null) {
                        String jsonError = new String(networkResponse.data);
                        // Print Error!
                        Log.e("intent22", jsonError);

                        try {
                            JSONObject jsonObject = new JSONObject(jsonError);

                            JSONObject jsonObject_error = jsonObject.getJSONObject("error");

                            String message = jsonObject_error.getString("message");
                            if (jsonObject_error.has("error_code")) {
                                if (jsonObject_error.getInt("error_code") == 1002) {
                                    // resendOtp(str_email,str_password);
                                }
                            }

                            if (jsonObject_error.has("errors")) {

                                JSONObject jsonObject_errors = jsonObject_error.getJSONObject("errors");

                                String error_email = null;
                                if (jsonObject_errors.has("email")) {
                                    JSONArray jsonArray_mobile = jsonObject_errors.getJSONArray("email");
                                    error_email = jsonArray_mobile.getString(0);
                                }
                                editTextError.error(error_email, "");

                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        showToast("Something Went Wrong", AuthActivity.this);
                    }
                    Log.e("error", error.toString());
                    hideProgressDialog();
                }) {


            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> map1 = new HashMap<String, String>();
                map1.put("Content-Type", "application/x-www-form-urlencoded");
                map1.put("X-Requested-With", "XMLHttpRequest");
                return map1;
            }

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> map1 = new HashMap<String, String>();
                map1.put("email", str_email);
                return map1;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, -1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }


    public void resendOtp(String email) {
        showProgressDialog();
        String str_email = email;

        Helper.closeKeyboard(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.URL_RESEND_OTP,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Log.e("responce_url", response);

                        hideProgressDialog();
                        onResendOtp.success();
                    }
                },
                error -> {
                    onResendOtp.failure();
                    NetworkResponse networkResponse = error.networkResponse;
                    if (networkResponse != null && networkResponse.data != null) {
                        String jsonError = new String(networkResponse.data);
                        // Print Error!
                        Log.e("intent22", jsonError);

                        try {
                            JSONObject jsonObject = new JSONObject(jsonError);

                            JSONObject jsonObject_error = jsonObject.getJSONObject("error");

                            String message = jsonObject_error.getString("message");
                            if (jsonObject_error.has("error_code")) {
                                if (jsonObject_error.getInt("error_code") == 1002) {
                                    // resendOtp(str_email,str_password);
                                }
                            }

                            if (jsonObject_error.has("errors")) {

                                JSONObject jsonObject_errors = jsonObject_error.getJSONObject("errors");

                                String error_email = null;
                                if (jsonObject_errors.has("email")) {
                                    JSONArray jsonArray_mobile = jsonObject_errors.getJSONArray("email");
                                    error_email = jsonArray_mobile.getString(0);
                                }
                                editTextError.error(error_email, "");

                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        showToast("Something Went Wrong", AuthActivity.this);
                    }
                    Log.e("error", error.toString());
                    hideProgressDialog();
                }) {


            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> map1 = new HashMap<String, String>();
                map1.put("Content-Type", "application/x-www-form-urlencoded");
                map1.put("X-Requested-With", "XMLHttpRequest");
                return map1;
            }

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> map1 = new HashMap<String, String>();
                map1.put("email", str_email);
                return map1;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, -1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }

    public OnResendOtp getOnResendOtp() {
        return onResendOtp;
    }

    public void setOnResendOtp(OnResendOtp onResendOtp) {
        this.onResendOtp = onResendOtp;
    }

    public interface OnResendOtp {

        void success();
        void failure();
    }

}
