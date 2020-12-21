package com.jobtick.activities;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.jobtick.R;
import android.annotation.SuppressLint;
import com.jobtick.utils.Constant;
import com.jobtick.utils.HttpStatus;
import com.jobtick.widget.ExtendedEntryText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import timber.log.Timber;


public class ChangePasswordActivity extends ActivityBase implements TextWatcher {

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.toolbar)
    MaterialToolbar toolbar;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.old_password)
    ExtendedEntryText oldPassword;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.new_password)
    ExtendedEntryText newPassword;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.btn_change_password)
    MaterialButton btnChangePassword;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        ButterKnife.bind(this);

        initToolbar();
        btnChangePassword.setEnabled(false);
        oldPassword.addTextChangedListener(this);
        newPassword.addTextChangedListener(this);
    }


    private void initToolbar() {
        toolbar.setNavigationIcon(R.drawable.ic_back);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Change Password");
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


    @OnClick({R.id.btn_change_password})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_change_password:
                if (validation())
                    changePassword();
                break;
        }
    }

    private boolean validation() {
        if (TextUtils.isEmpty(oldPassword.getText().trim())) {
            oldPassword.setError("Enter your old password");
            return false;
        } else if (TextUtils.isEmpty(newPassword.getText().trim())) {
            newPassword.setError("Enter new password");
            return false;
        }
        return true;
    }

    private void changePassword() {
        showProgressDialog();
        String str_old_password = oldPassword.getText().trim();
        String str_new_password = newPassword.getText().trim();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.URL_CHANGE_PASSWORD,
                response -> {
                    Timber.e(response);
                    hideProgressDialog();
                    try {

                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.has("success") && !jsonObject.isNull("success")) {
                            if (jsonObject.getBoolean("success")) {
                                showSuccessToast("Password Changed Successfully !", this);
                            }
                        }

                    } catch (JSONException e) {
                        Timber.e(String.valueOf(e));
                        e.printStackTrace();

                    }


                },
                new com.android.volley.Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        NetworkResponse networkResponse = error.networkResponse;
                        if (networkResponse != null && networkResponse.data != null) {
                            String jsonError = new String(networkResponse.data);
                            // Print Error!
                            Timber.e(jsonError);
                            if (networkResponse.statusCode == HttpStatus.AUTH_FAILED) {
                                unauthorizedUser();
                                hideProgressDialog();
                                return;
                            }
                            try {
                                JSONObject jsonObject = new JSONObject(jsonError);
                                JSONObject jsonObject_error = jsonObject.getJSONObject("error");

                                if (jsonObject_error.has("message")) {
                                    showToast(jsonObject_error.getString("message"), ChangePasswordActivity.this);
                                }
                                if (jsonObject_error.has("errors")) {
                                    JSONObject jsonObject_errors = jsonObject_error.getJSONObject("errors");

                                    if (jsonObject_errors.has("old_password")) {
                                        JSONArray jsonArray_mobile = jsonObject_errors.getJSONArray("old_password");
                                        String old_password = jsonArray_mobile.getString(0);
                                        oldPassword.setError(old_password);
                                    }
                                    if (jsonObject_errors.has("new_password")) {
                                        JSONArray jsonArray_mobile = jsonObject_errors.getJSONArray("new_password");
                                        String new_password = jsonArray_mobile.getString(0);
                                        newPassword.setError(new_password);
                                    }


                                }

                                //  ((CredentialActivity)requireActivity()).showToast(message,requireActivity());


                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else {
                            showToast("Something Went Wrong", ChangePasswordActivity.this);
                        }
                        Timber.e(error.toString());
                        hideProgressDialog();
                    }
                }) {


            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> map1 = new HashMap<>();

                map1.put("authorization", sessionManager.getTokenType() + " " + sessionManager.getAccessToken());
                map1.put("Content-Type", "application/x-www-form-urlencoded");
                // map1.put("X-Requested-With", "XMLHttpRequest");
                return map1;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map1 = new HashMap<>();
                map1.put("old_password", str_old_password);
                map1.put("new_password", str_new_password);
                Timber.e(String.valueOf(map1.size()));
                Timber.e(map1.toString());
                return map1;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, -1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(ChangePasswordActivity.this);
        requestQueue.add(stringRequest);
    }


    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable editable) {
        boolean enabled = oldPassword.getText().length() > 0 && newPassword.getText().length() > 0;
        btnChangePassword.setEnabled(enabled);
    }
}
