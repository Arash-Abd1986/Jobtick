package com.jobtick.activities;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatButton;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.appbar.MaterialToolbar;
import com.jobtick.EditText.EditTextRegular;
import com.jobtick.R;
import com.jobtick.TextView.TextViewRegular;
import com.jobtick.utils.Constant;
import com.jobtick.utils.HttpStatus;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import timber.log.Timber;


public class ChangePasswordActivity extends ActivityBase {

    /*    @BindView(R.id.toolbar)
        MaterialToolbar toolbar;*/
    @BindView(R.id.edt_current_password)
    EditTextRegular edtCurrentPassword;
    @BindView(R.id.img_btn_current_password_toggle)
    ImageView imgBtnCurrentPasswordToggle;
    @BindView(R.id.edt_new_password)
    EditTextRegular edtNewPassword;
    @BindView(R.id.img_btn_new_password_toggle)
    ImageView imgBtnNewPasswordToggle;
    @BindView(R.id.edt_confirm_password)
    EditTextRegular edtConfirmPassword;
    @BindView(R.id.img_btn_confirm_password_toggle)
    ImageView imgBtnConfirmPasswordToggle;
    @BindView(R.id.lyt_btn_submit)
    LinearLayout lytBtnSubmit;


    @BindView(R.id.ivBack)
    ImageView ivBack;


    private boolean current_password_hide = true;
    private boolean new_password_hide = true;
    private boolean confirm_password_hide = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        ButterKnife.bind(this);

        initToolbar();
    }


    private void initToolbar() {
        ivBack.setOnClickListener(v -> {
            finish();
        });


    /*    toolbar.setNavigationIcon(R.drawable.ic_back);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Change Password");*/
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


    @OnClick({R.id.img_btn_current_password_toggle, R.id.img_btn_new_password_toggle, R.id.img_btn_confirm_password_toggle, R.id.lyt_btn_submit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.img_btn_current_password_toggle:
                current_passowrd_toggle();
                break;
            case R.id.img_btn_new_password_toggle:
                new_passowrd_toggle();
                break;
            case R.id.img_btn_confirm_password_toggle:
                confirm_passowrd_toggle();
                break;
            case R.id.lyt_btn_submit:
                if (validation())
                    changePassword();
                break;
        }
    }

    private boolean validation() {
        if (TextUtils.isEmpty(edtCurrentPassword.getText().toString().trim())) {
            edtCurrentPassword.setError("Enter your current password");
            return false;
        } else if (TextUtils.isEmpty(edtNewPassword.getText().toString().trim())) {
            edtNewPassword.setError("Enter new password");
            return false;
        } else if (!edtNewPassword.getText().toString().trim().equals(edtConfirmPassword.getText().toString().trim())) {
            edtConfirmPassword.setError("Doesn't match your password");
            return false;
        }
        return true;
    }

    private void changePassword() {
        showpDialog();
        String str_old_password = edtCurrentPassword.getText().toString().trim();
        String str_new_password = edtNewPassword.getText().toString().trim();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.URL_CHANGE_PASSWORD,
                response -> {
                    Timber.e(response);
                    hidepDialog();
                    try {

                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.has("success") && !jsonObject.isNull("success")) {
                            if (jsonObject.getBoolean("success")) {
                                showCustomDialog("Password Changed Successfully !");
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
                                hidepDialog();
                                return;
                            }
                            try {
                                JSONObject jsonObject = new JSONObject(jsonError);
                                JSONObject jsonObject_error = jsonObject.getJSONObject("error");

                                if (jsonObject_error.has("message")) {
                                    Toast.makeText(ChangePasswordActivity.this, jsonObject_error.getString("message"), Toast.LENGTH_SHORT).show();
                                }
                                if (jsonObject_error.has("errors")) {
                                    JSONObject jsonObject_errors = jsonObject_error.getJSONObject("errors");

                                    if (jsonObject_errors.has("old_password")) {
                                        JSONArray jsonArray_mobile = jsonObject_errors.getJSONArray("old_password");
                                        String old_password = jsonArray_mobile.getString(0);
                                        edtCurrentPassword.setError(old_password);
                                    }
                                    if (jsonObject_errors.has("new_password")) {
                                        JSONArray jsonArray_mobile = jsonObject_errors.getJSONArray("new_password");
                                        String new_password = jsonArray_mobile.getString(0);
                                        edtNewPassword.setError(new_password);
                                    }


                                }

                                //  ((CredentialActivity)getActivity()).showToast(message,getActivity());


                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else {
                            showToast("Something Went Wrong", ChangePasswordActivity.this);
                        }
                        Timber.e(error.toString());
                        hidepDialog();
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

    private void new_passowrd_toggle() {
        if (new_password_hide) {
            new_password_hide = false;
            edtNewPassword.setInputType(
                    InputType.TYPE_CLASS_TEXT |
                            InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            );
            imgBtnNewPasswordToggle.setImageDrawable(getResources().getDrawable(R.drawable.ic_eye));
        } else {
            new_password_hide = true;
            edtNewPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
            imgBtnNewPasswordToggle.setImageDrawable(getResources().getDrawable(R.drawable.ic_eye_off));
        }
    }

    private void current_passowrd_toggle() {
        if (current_password_hide) {
            current_password_hide = false;
            edtCurrentPassword.setInputType(
                    InputType.TYPE_CLASS_TEXT |
                            InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            );
            imgBtnCurrentPasswordToggle.setImageDrawable(getResources().getDrawable(R.drawable.ic_eye));
        } else {
            current_password_hide = true;
            edtCurrentPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
            imgBtnCurrentPasswordToggle.setImageDrawable(getResources().getDrawable(R.drawable.ic_eye_off));
        }
    }

    private void confirm_passowrd_toggle() {
        if (confirm_password_hide) {
            confirm_password_hide = false;
            edtConfirmPassword.setInputType(
                    InputType.TYPE_CLASS_TEXT |
                            InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            );
            imgBtnConfirmPasswordToggle.setImageDrawable(getResources().getDrawable(R.drawable.ic_eye));
        } else {
            confirm_password_hide = true;
            edtConfirmPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
            imgBtnConfirmPasswordToggle.setImageDrawable(getResources().getDrawable(R.drawable.ic_eye_off));
        }
    }

    private void showCustomDialog(String message) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.dialog_show_warning);
        dialog.setCancelable(true);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        TextViewRegular txtMessage = dialog.findViewById(R.id.txt_message);
        txtMessage.setText(message);

        ((AppCompatButton) dialog.findViewById(R.id.btn_ok)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                finish();
            }
        });

        dialog.show();
        dialog.getWindow().setAttributes(lp);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

}
