package com.jobtick.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.Toast;

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
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.jobtick.R;
import com.jobtick.fragments.ForgotPassword1Fragment;
import com.jobtick.fragments.ForgotPassword2Fragment;
import com.jobtick.fragments.ForgotPassword3Fragment;
import com.jobtick.fragments.SignInFragment;
import com.jobtick.fragments.SignUpFragment;
import com.jobtick.fragments.VerifyAccountFragment;
import com.jobtick.models.UserAccountModel;
import com.jobtick.utils.Constant;
import com.jobtick.utils.FireBaseEvent;
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

import static com.jobtick.utils.FireBaseEvent.EventValue.LOGIN_FACEBOOK;
import static com.jobtick.utils.FireBaseEvent.EventValue.LOGIN_GOOGLE;
import static com.jobtick.utils.FireBaseEvent.EventValue.LOGIN_NORMAL;
import static com.jobtick.utils.FireBaseEvent.EventValue.SIGN_UP_FACEBOOK;
import static com.jobtick.utils.FireBaseEvent.EventValue.SIGN_UP_GOOGLE;
import static com.jobtick.utils.FireBaseEvent.EventValue.SIGN_UP_NORMAL;

public class AuthActivity extends ActivityBase {

    public final String DEVICE = "Android";
    private static final int RC_SIGN_IN = 234;
    SignInFragment signInFragment;
    VerifyAccountFragment verifyAccountFragment;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.auth_layout)
    FrameLayout authLayout;
    CallbackManager callbackManager;
    GoogleSignInClient mGoogleSignInClient;
    SessionManager sessionManager;
    ForgotPassword1Fragment forgotPassword1Fragment;
    SignUpFragment signUpFragment;

    EditTextError editTextError;
    OnResendOtp onResendOtp;

    private FireBaseEvent fireBaseEvent;

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
                        ft.replace(R.id.auth_layout, fragment).addToBackStack(fragment.toString());
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

                        Intent intent = new Intent(AuthActivity.this, AuthActivity.class);
                        intent.putExtra("type", "Signin");
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();

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

    private boolean doubleBackToExitPressedOnce;

    public void onBackPressed() {

        FragmentManager fm = getSupportFragmentManager();
        Timber.tag("back stack entry").d(Integer.toString(fm.getBackStackEntryCount()));

        if (fm.getBackStackEntryCount() > 1) {
            fm.popBackStack();
            return;
        }
        if (doubleBackToExitPressedOnce) {
            finish();
        } else {
            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, "Press back button again to exit",
                    Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {

                    doubleBackToExitPressedOnce = false;
                }
            }, 3000);
        }
    }

    public interface EditTextError {
        void onEmailError(String emailError);

        void onPasswordError(String passwordError);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);


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
                        Log.d("FacebookCheck",exception.getLocalizedMessage().toString());
                    }
                });

        init();

        fireBaseEvent = FireBaseEvent.getInstance(getApplicationContext());
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
                        Timber.e(response);

                        hideProgressDialog();
                        try {

                            JSONObject jsonObject = new JSONObject(response);
                            Timber.e(jsonObject.toString());
                            JSONObject jsonObject_data = jsonObject.getJSONObject("data");
                            sessionManager.setAccessToken(jsonObject_data.getString("access_token"));
                            sessionManager.setTokenType(jsonObject_data.getString("token_type"));
                            JSONObject jsonObject_user = jsonObject_data.getJSONObject("user");
                            UserAccountModel userAccountModel = new UserAccountModel().getJsonToModel(jsonObject_user);
                            sessionManager.setUserAccount(userAccountModel);

                            if (fromSignUp)
                                fireBaseEvent.sendEvent(FireBaseEvent.Event.SIGN_UP,
                                        FireBaseEvent.EventType.API_RESPOND_SUCCESS, SIGN_UP_FACEBOOK);
                            else
                                fireBaseEvent.sendEvent(FireBaseEvent.Event.LOGIN,
                                        FireBaseEvent.EventType.API_RESPOND_SUCCESS, LOGIN_FACEBOOK);

                            proceedToCorrectActivity(userAccountModel);

                        } catch (JSONException e) {
                            Timber.e(String.valueOf(e));
                            FirebaseCrashlytics.getInstance().recordException(e);
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
                            Timber.e(jsonError);
                            FirebaseCrashlytics.getInstance().recordException(error);

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
                            //  ((CredentialActivity)requireActivity()).showToast("Something Went Wrong",requireActivity());
                        }
                        Timber.e(error.toString());
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


                //TODO: due we direct user to complete profile page, we ignore str_fname and str_lname
                map1.put("fname", "no first name");
                map1.put("lname", "no last name");
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
            transaction.replace(R.id.auth_layout, fragment).addToBackStack(fragment.toString());

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

    public boolean fromSignUp = false;

    public void signInWithGoogle(boolean fromSignUp) {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
        this.fromSignUp = fromSignUp;
    }

    public void facebookLogin(boolean fromSignUp) {
        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("email"));
        this.fromSignUp = fromSignUp;
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
                        Timber.e(response);

                        hideProgressDialog();
                        try {

                            JSONObject jsonObject = new JSONObject(response);

                            Timber.e(jsonObject.toString());
                            JSONObject jsonObject_data = jsonObject.getJSONObject("data");
                            sessionManager.setAccessToken(jsonObject_data.getString("access_token"));
                            sessionManager.setTokenType(jsonObject_data.getString("token_type"));
                            JSONObject jsonObject_user = jsonObject_data.getJSONObject("user");
                            UserAccountModel userAccountModel = new UserAccountModel().getJsonToModel(jsonObject_user);
                            sessionManager.setUserAccount(userAccountModel);

                            fireBaseEvent.sendEvent(FireBaseEvent.Event.LOGIN,
                                    FireBaseEvent.EventType.API_RESPOND_SUCCESS, LOGIN_NORMAL);

                            proceedToCorrectActivity(userAccountModel);

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

                                    //TODO: we need to direct user to signup page, if email is not verified.
                                    if(jsonObject_error.has("message"))
                                        showToast(jsonObject_error.getString("message"), this);

                                    return;
                                }
                            }


                            if (jsonObject_error.has("errors")) {

                                JSONObject jsonObject_errors = jsonObject_error.getJSONObject("errors");

                                String error_email = null, error_password = null;
                                if (jsonObject_errors.has("email")) {
                                    JSONArray jsonArray_mobile = jsonObject_errors.getJSONArray("email");
                                    error_email = jsonArray_mobile.getString(0);
                                    showToast(error_email, this);
                                    editTextError.onEmailError(error_email);
                                } else if (jsonObject_errors.has("password")) {
                                    JSONArray jsonArray_mobile = jsonObject_errors.getJSONArray("password");
                                    error_password = jsonArray_mobile.getString(0);
                                    showToast(error_password, this);
                                    editTextError.onPasswordError(error_email);
                                } else if (jsonObject_errors.has("device_token")) {
                                    JSONArray jsonArray_device_token = jsonObject_errors.getJSONArray("device_token");
                                    showToast(jsonArray_device_token.getString(0), AuthActivity.this);
                                } else if (jsonObject_errors.has("device_type")) {
                                    JSONArray jsonArray_device_type = jsonObject_errors.getJSONArray("device_type");
                                    showToast(jsonArray_device_type.getString(0), AuthActivity.this);
                                } else if (jsonObject_errors.has("fcm_token")) {
                                    JSONArray jsonArray_fcm_token = jsonObject_errors.getJSONArray("fcm_token");
                                    showToast(jsonArray_fcm_token.getString(0), AuthActivity.this);
                                } else if (jsonObject_errors.has("latitude")) {
                                    JSONArray jsonArray_latitude = jsonObject_errors.getJSONArray("latitude");
                                    showToast(jsonArray_latitude.getString(0), AuthActivity.this);
                                } else if (jsonObject_errors.has("longitude")) {
                                    JSONArray jsonArray_longitude = jsonObject_errors.getJSONArray("longitude");
                                    showToast(jsonArray_longitude.getString(0), AuthActivity.this);
                                }

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
                        Timber.e(response);

                        hideProgressDialog();
                        // showToast("Check your inbox",AuthActivity.this);
                        Bundle bundle = new Bundle();
                        Fragment fragment = new VerifyAccountFragment();
                        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                        ft.replace(R.id.auth_layout, fragment).addToBackStack(fragment.toString());
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
                            Timber.e(jsonError);

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
                        Timber.e(error.toString());
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


    private void proceedToCorrectActivity(UserAccountModel userAccountModel) {
        Intent intent;
        if (userAccountModel.getAccount_status().isBasic_info()) {
            intent = new Intent(this, DashboardActivity.class);
            sessionManager.setUserAccount(userAccountModel);
            sessionManager.setLatitude(userAccountModel.getLatitude().toString());
            sessionManager.setLongitude(userAccountModel.getLongitude().toString());
            sessionManager.setLogin(true);
            openActivity(intent);
        } else {
            intent = new Intent(this, CompleteRegistrationActivity.class);
            startActivity(intent);
        }
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
            Timber.tag("LoginGoogle").d(task.toString());
        } else {
            Log.d("FacebookCheck",requestCode+":"+resultCode);
            callbackManager.onActivityResult(requestCode, resultCode, data);
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            signInUpdateUI(account);
            Timber.d("LoginGoogle account:%s", account.toString());
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            signInUpdateUI(null);
            FirebaseCrashlytics.getInstance().recordException(e);
        }
    }

    String str_id_token = "";
    String str_lname = "";
    String str_fname = "";


    private void signInUpdateUI(GoogleSignInAccount account) {
        try {
            showProgressDialog();

            //TODO: for now, we can ignore this, because we get fname and lname in complete registration
//            if (account.getDisplayName() != null) {
//                String[] displayName = account.getDisplayName().split(" ");
//                int size_of_displayName = displayName.length;
//                str_lname = displayName[size_of_displayName - 1];
//                int count = 0;
//                StringBuilder stringBuilder = new StringBuilder();
//                while (size_of_displayName - 1 > count) {
//                    stringBuilder.append(displayName[count]);
//                    count++;
//                }
//                str_fname = stringBuilder.toString();
//            }

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


                    //TODO: We ignore this and set it to no name due to getting these data in complete profile
//                    String finalStr_fname = str_fname;
//                    String finalStr_lname = str_lname;

                    StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.URL_SIGNIN_GOOGLE,
                            response -> {
                                Timber.e(response);

                                hideProgressDialog();
                                try {

                                    JSONObject jsonObject = new JSONObject(response);
                                    Timber.e(jsonObject.toString());
                                    JSONObject jsonObject_data = jsonObject.getJSONObject("data");

                                    sessionManager.setAccessToken(jsonObject_data.getString("access_token"));
                                    sessionManager.setTokenType(jsonObject_data.getString("token_type"));

                                    JSONObject jsonObject_user = jsonObject_data.getJSONObject("user");
                                    UserAccountModel userAccountModel = new UserAccountModel().getJsonToModel(jsonObject_user);
                                    sessionManager.setUserAccount(userAccountModel);

                                    if (fromSignUp)
                                        fireBaseEvent.sendEvent(FireBaseEvent.Event.SIGN_UP,
                                                FireBaseEvent.EventType.API_RESPOND_SUCCESS, SIGN_UP_GOOGLE);
                                    else
                                        fireBaseEvent.sendEvent(FireBaseEvent.Event.LOGIN,
                                                FireBaseEvent.EventType.API_RESPOND_SUCCESS, LOGIN_GOOGLE);

                                    proceedToCorrectActivity(userAccountModel);

                                } catch (JSONException e) {
                                    Timber.e(String.valueOf(e));
                                    e.printStackTrace();
                                    FirebaseCrashlytics.getInstance().recordException(e);
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    NetworkResponse networkResponse = error.networkResponse;
                                    if (networkResponse != null && networkResponse.data != null) {
                                        String jsonError = new String(networkResponse.data);
                                        // Print Error!
                                        Timber.e(jsonError);
                                        FirebaseCrashlytics.getInstance().recordException(error);

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
                                    Timber.e(error.toString());
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

                            //TODO: We ignore this and set it to no name due to getting these data in complete profile
                            map1.put("fname", "no first name");
                            map1.put("lname", "no last name");
                            map1.put("email", str_email);
                            map1.put("device_token", str_device_id);
                            map1.put("device_type", str_device);
                            if (str_fcm_token != null && !str_fcm_token.isEmpty())
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

                } catch (IOException | GoogleAuthException e) {
                    e.printStackTrace();
                    FirebaseCrashlytics.getInstance().recordException(e);
                }
            };
            AsyncTask.execute(runnable);


        } catch (Exception e) {
            hideProgressDialog();
            FirebaseCrashlytics.getInstance().recordException(e);
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
                        fireBaseEvent.sendEvent(FireBaseEvent.Event.SIGN_UP,
                                FireBaseEvent.EventType.API_RESPOND_SUCCESS, SIGN_UP_NORMAL);

                        Bundle bundle = new Bundle();
                        Fragment fragment = new VerifyAccountFragment();
                        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                        ft.replace(R.id.auth_layout, fragment).addToBackStack(fragment.toString());
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

                                if (jsonObject_error.has("errors")) {
                                    JSONObject jsonObject_errors = jsonObject_error.getJSONObject("errors");
                                    String error_email = "", error_password = "";
                                    if (jsonObject_errors.has("email")) {
                                        JSONArray jsonArray_mobile = jsonObject_errors.getJSONArray("email");
                                        error_email = jsonArray_mobile.getString(0);
                                        showToast(error_email, AuthActivity.this);
                                        editTextError.onEmailError(error_email);
                                    } else if (jsonObject_errors.has("password")) {
                                        JSONArray jsonArray_mobile = jsonObject_errors.getJSONArray("password");
                                        error_password = jsonArray_mobile.getString(0);
                                        showToast(error_password, AuthActivity.this);
                                        editTextError.onPasswordError(error_password);
                                    }
                                    // signUpFragment.error(error_email,error_password);

                                } else if (jsonObject_error.has("message")) {
                                    showToast(jsonObject_error.getString("message"), AuthActivity.this);
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

                map1.put("fname", "no name");
                map1.put("location", "no location");

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
                    Timber.e(response);

                    hideProgressDialog();
                    try {

                        JSONObject jsonObject = new JSONObject(response);

                        Timber.e(jsonObject.toString());


                        JSONObject jsonObject_data = jsonObject.getJSONObject("data");

                        sessionManager.setAccessToken(jsonObject_data.getString("access_token"));
                        sessionManager.setTokenType(jsonObject_data.getString("token_type"));

                        JSONObject jsonObject_user = jsonObject_data.getJSONObject("user");
                        UserAccountModel userAccountModel = new UserAccountModel().getJsonToModel(jsonObject_user);
                        sessionManager.setUserAccount(userAccountModel);

                        proceedToCorrectActivity(userAccountModel);

                    } catch (JSONException e) {
                        Timber.e(String.valueOf(e));
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
                            Timber.e(jsonError);
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
                        Timber.e(error.toString());
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

    public void newEmailVerification(String email, String otp) {
        showProgressDialog();
        String str_email = email;
        String str_otp = otp;
        Helper.closeKeyboard(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.URL_EMAIL_VERIFICATION,
                response -> {
                    Timber.e(response);
                    hideProgressDialog();
                    try {

                        JSONObject jsonObject = new JSONObject(response);

                        Timber.e(jsonObject.toString());


                        JSONObject jsonObject_data = jsonObject.getJSONObject("data");

                        sessionManager.setAccessToken(jsonObject_data.getString("access_token"));
                        sessionManager.setTokenType(jsonObject_data.getString("token_type"));

                        JSONObject jsonObject_user = jsonObject_data.getJSONObject("user");
                        UserAccountModel userAccountModel = new UserAccountModel().getJsonToModel(jsonObject_user);
                        sessionManager.setUserAccount(userAccountModel);

                        proceedToCorrectActivity(userAccountModel);

                    } catch (JSONException e) {
                        Timber.e(String.valueOf(e));
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
                            Timber.e(jsonError);
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
                        Timber.e(error.toString());
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
                        Timber.e(response);

                        hideProgressDialog();
                        //  showToast("Check your inbox", AuthActivity.this);
                        Bundle bundle = new Bundle();
                        Fragment fragment = new ForgotPassword2Fragment();
                        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                        ft.replace(R.id.auth_layout, fragment).addToBackStack(fragment.toString());
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
                        Timber.e(jsonError);

                        try {
                            JSONObject jsonObject = new JSONObject(jsonError);
                            JSONObject jsonObject_error = jsonObject.getJSONObject("error");

                            if (jsonObject_error.has("errors")) {

                                JSONObject jsonObject_errors = jsonObject_error.getJSONObject("errors");

                                String str_error = null;
                                if (jsonObject_errors.has("email")) {
                                    JSONArray jsonArray_mobile = jsonObject_errors.getJSONArray("email");
                                    str_error = jsonArray_mobile.getString(0);
                                    showToast(str_error, this);
                                    editTextError.onEmailError(str_error);
                                } else if (jsonObject_errors.has("password")) {
                                    JSONArray jsonArray_mobile = jsonObject_errors.getJSONArray("password");
                                    str_error = jsonArray_mobile.getString(0);
                                    showToast(str_error, this);
                                    editTextError.onPasswordError(str_error);
                                } else {
                                    String message = jsonObject_error.getString("message");
                                    showToast(message, this);
                                }
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
                map1.put("email", str_email);
                return map1;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, -1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }


    //There is no such API any more, but we keep it af any changes occurs again
    public void resendOtp(String email) {
        showProgressDialog();
        String str_email = email;

        Helper.closeKeyboard(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.URL_RESEND_OTP,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Timber.e(response);

                        hideProgressDialog();
                        onResendOtp.success();
                    }
                },
                error -> {
                    onResendOtp.failure();
                    NetworkResponse networkResponse = error.networkResponse;
                    if (networkResponse != null && networkResponse.data != null) {
                        String jsonError = new String(networkResponse.data);

                        try {
                            JSONObject jsonObject = new JSONObject(jsonError);

                            JSONObject jsonObject_error = jsonObject.getJSONObject("error");

                            if (jsonObject_error.has("errors")) {

                                JSONObject jsonObject_errors = jsonObject_error.getJSONObject("errors");

                                String error_email = null;
                                if (jsonObject_errors.has("email")) {
                                    JSONArray jsonArray_mobile = jsonObject_errors.getJSONArray("email");
                                    error_email = jsonArray_mobile.getString(0);
                                    showToast(error_email, this);
                                    editTextError.onEmailError(error_email);
                                }
                            } else {
                                String message = jsonObject_error.getString("message");
                                showToast(message, this);

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
                map1.put("email", str_email);
                return map1;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, -1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }

    public void resendOtpForResetPassword(String email) {
        showProgressDialog();
        String str_email = email;

        Helper.closeKeyboard(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.URL_RESET_PASSWORD,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Timber.e(response);

                        hideProgressDialog();
                        onResendOtp.success();
                    }
                },
                error -> {
                    onResendOtp.failure();
                    NetworkResponse networkResponse = error.networkResponse;
                    if (networkResponse != null && networkResponse.data != null) {
                        String jsonError = new String(networkResponse.data);

                        try {
                            JSONObject jsonObject = new JSONObject(jsonError);

                            JSONObject jsonObject_error = jsonObject.getJSONObject("error");

                            if (jsonObject_error.has("errors")) {

                                JSONObject jsonObject_errors = jsonObject_error.getJSONObject("errors");

                                String error_email = null;
                                if (jsonObject_errors.has("email")) {
                                    JSONArray jsonArray_mobile = jsonObject_errors.getJSONArray("email");
                                    error_email = jsonArray_mobile.getString(0);
                                    showToast(error_email, this);
                                    editTextError.onEmailError(error_email);
                                }
                            } else {
                                String message = jsonObject_error.getString("message");
                                showToast(message, this);

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
                map1.put("email", str_email);
                return map1;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, -1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }

    public void newResendOtp(String email) {
        showProgressDialog();
        String str_email = email;

        Helper.closeKeyboard(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.URL_NEW_RESEND_OTP,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Timber.e(response);

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
                        Timber.e(jsonError);

                        try {
                            JSONObject jsonObject = new JSONObject(jsonError);

                            JSONObject jsonObject_error = jsonObject.getJSONObject("error");

                            if (jsonObject_error.has("errors")) {

                                JSONObject jsonObject_errors = jsonObject_error.getJSONObject("errors");

                                String error_email = null;
                                if (jsonObject_errors.has("email")) {
                                    JSONArray jsonArray_mobile = jsonObject_errors.getJSONArray("email");
                                    error_email = jsonArray_mobile.getString(0);
                                    showToast(error_email, this);
                                    editTextError.onEmailError(error_email);
                                }
                            } else {
                                String message = jsonObject_error.getString("message");
                                showToast(message, this);

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        showToast("Something Went Wrong", AuthActivity.this);
                    }
                    Timber.e(error.toString());
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
