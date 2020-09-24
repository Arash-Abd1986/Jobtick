package com.jobtick.activities;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.appbar.MaterialToolbar;
import com.jobtick.EditText.EditTextRegular;
import com.jobtick.R;
import com.jobtick.TextView.TextViewRegular;
import com.jobtick.utils.Constant;
import com.jobtick.utils.ConstantKey;
import com.jobtick.utils.HttpStatus;
import com.jobtick.utils.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import timber.log.Timber;

public class AddCouponPaymentOverviewActivity extends ActivityBase {

    private static final String TAG = AddCouponPaymentOverviewActivity.class.getName();
    @BindView(R.id.toolbar)
    MaterialToolbar toolbar;
    @BindView(R.id.edt_coupon_code)
    EditTextRegular edtCouponCode;
    @BindView(R.id.lyt_btn_apply)
    LinearLayout lytBtnApply;

    private SessionManager sessionManager;
    private float taskAmount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_coupon_payment_overview);
        ButterKnife.bind(this);
        sessionManager = new SessionManager(AddCouponPaymentOverviewActivity.this);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            taskAmount = bundle.getFloat(ConstantKey.AMOUNT);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @OnClick(R.id.lyt_btn_apply)
    public void onViewClicked() {
        if (validation()) {
            checkCoupon(edtCouponCode.getText().toString().trim());
        }
    }

    private void checkCoupon(String coupon_code) {
        showpDialog();
        StringRequest stringRequest = new StringRequest(StringRequest.Method.POST, Constant.URL_COUPONS + "/check-coupon",
                response -> {
                    Timber.e(response);
                    hidepDialog();
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        Timber.e(jsonObject.toString());
                        if (jsonObject.has("success") && !jsonObject.isNull("success")) {
                            if (jsonObject.getBoolean("success")) {
                                if (jsonObject.has("data") && !jsonObject.isNull("data")) {
                                    JSONObject jsonObject_data = jsonObject.getJSONObject("data");
                                    if (jsonObject_data.has("discount_amount") && !jsonObject_data.isNull("discount_amount")) {
                                        float discount_amount = (float) jsonObject_data.getDouble("discount_amount");
                                        Intent intent = new Intent();
                                        Bundle bundle = new Bundle();
                                        bundle.putFloat(ConstantKey.DISCOUNT_AMOUNT, discount_amount);
                                        bundle.putString(ConstantKey.DISCOUNT_COUPON, coupon_code);
                                        intent.putExtras(bundle);
                                        setResult(ConstantKey.RESULTCODE_COUPON, intent);
                                        onBackPressed();
                                    } else {
                                        showToast("This coupon not applied", AddCouponPaymentOverviewActivity.this);
                                    }
                                }
                            } else {
                                showToast("Something went Wrong", AddCouponPaymentOverviewActivity.this);
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
                            unauthorizedUser();
                            hidepDialog();
                            return;
                        }
                        try {
                            hidepDialog();
                            JSONObject jsonObject = new JSONObject(jsonError);
                            JSONObject jsonObject_error = jsonObject.getJSONObject("error");
                            showCustomDialog(jsonObject_error.getString("message"));
                           /* if (jsonObject_error.has("message")) {
                                Toast.makeText(AddCouponPaymentOverviewActivity.this, jsonObject_error.getString("message"), Toast.LENGTH_SHORT).show();
                            }
                            if (jsonObject_error.has("errors")) {
                                JSONObject jsonObject_errors = jsonObject_error.getJSONObject("errors");
                            }*/
                            //  ((CredentialActivity)getActivity()).showToast(message,getActivity());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        showToast("Something Went Wrong", AddCouponPaymentOverviewActivity.this);
                    }
                    Timber.e(error.toString());
                    hidepDialog();
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
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map1 = new HashMap<String, String>();
                map1.put("coupon", coupon_code);
                map1.put("amount", String.valueOf(taskAmount));
                return map1;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, -1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(AddCouponPaymentOverviewActivity.this);
        requestQueue.add(stringRequest);
        Log.e(TAG, stringRequest.getUrl());
    }

    private boolean validation() {
        if (TextUtils.isEmpty(edtCouponCode.getText().toString().trim())) {
            edtCouponCode.setError("?");
            return false;
        }
        return true;
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

        dialog.findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
        dialog.getWindow().setAttributes(lp);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }


}