package com.jobtick.incrementbudget;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.widget.AppCompatButton;
import androidx.cardview.widget.CardView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.appbar.MaterialToolbar;
import com.jobtick.edit_text.EditTextRegular;
import com.jobtick.edit_text.EditTextSemiBold;
import com.jobtick.R;
import com.jobtick.text_view.TextViewBold;
import com.jobtick.text_view.TextViewRegular;
import com.jobtick.text_view.TextViewSemiBold;
import com.jobtick.activities.ActivityBase;
import com.jobtick.models.TaskModel;
import com.jobtick.models.UserAccountModel;
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

public class IncreaseBudgetActivity extends ActivityBase {

    private static final String TAG = IncreaseBudgetActivity.class.getName();
    @BindView(R.id.toolbar)
    MaterialToolbar toolbar;
    @BindView(R.id.txt_budget)
    TextViewSemiBold txtBudget;
    @BindView(R.id.txt_btn_update_amount)
    TextViewRegular txtBtnUpdateAmount;
    @BindView(R.id.edt_increase_budget)
    EditTextSemiBold edtIncreaseBudget;
    @BindView(R.id.card_increase_budget_layout)
    CardView cardIncreaseBudgetLayout;
    @BindView(R.id.txt_service_fee)
    TextViewBold txtServiceFee;
    @BindView(R.id.txt_final_budget)
    TextViewBold txtFinalBudget;
    @BindView(R.id.edt_increase_budget_reason)
    EditTextRegular edtIncreaseBudgetReason;
    @BindView(R.id.img_level)
    ImageView imgLevel;
    @BindView(R.id.txt_account_level)
    TextViewRegular txtAccountLevel;
    @BindView(R.id.txt_current_service_fee)
    TextViewBold txtCurrentServiceFee;
    @BindView(R.id.txt_learn_how_level_affects_service_fee)
    TextViewRegular txtLearnHowLevelAffectsServiceFee;
    @BindView(R.id.lyt_btn_send_request)
    LinearLayout lytBtnSendRequest;
    @BindView(R.id.card_button)
    CardView cardButton;

    private TaskModel taskModel;
    private UserAccountModel userAccountModel;
    private SessionManager sessionManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_increase_budget);
        ButterKnife.bind(this);
        sessionManager = new SessionManager(this);
        userAccountModel = sessionManager.getUserAccount();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null && bundle.getParcelable(ConstantKey.TASK) != null) {
            taskModel = bundle.getParcelable(ConstantKey.TASK);
        }
        setdata();
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        setupBudget(0);
        edtIncreaseBudget.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.length()==0){
                    setupBudget(0);
                }else{
                    setupBudget(Integer.parseInt(s.toString()));
                }
            }
        });


    }

    private void setdata() {
        txtBudget.setText(taskModel.getAmount()+"");
    }


    private void setupBudget(int increase_budget) {
        float worker_service_fee = userAccountModel.getWorkerTier().getServiceFee();
        txtCurrentServiceFee.setText(worker_service_fee+" %");
        float service_fee =  (((increase_budget+taskModel.getAmount())*worker_service_fee)/100);
        txtServiceFee.setText("$ "+service_fee);
        float total_budget = (increase_budget+taskModel.getAmount()) - (((increase_budget+taskModel.getAmount())*worker_service_fee)/100);
        txtFinalBudget.setText("$ "+total_budget);
    }


    @OnClick({R.id.txt_btn_update_amount, R.id.lyt_btn_send_request})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.txt_btn_update_amount:
                txtBtnUpdateAmount.setVisibility(View.GONE);
                cardIncreaseBudgetLayout.setVisibility(View.VISIBLE);
                break;
            case R.id.lyt_btn_send_request:
                if(validation()){
                    submitIncreaseBudget(edtIncreaseBudget.getText().toString().trim(), edtIncreaseBudgetReason.getText().toString().trim());
                }
                break;
        }
    }

    private void submitIncreaseBudget(String increase_budget, String increase_budget_reason) {
        showProgressDialog();
        StringRequest stringRequest = new StringRequest(StringRequest.Method.POST, Constant.URL_TASKS + "/" + taskModel.getSlug() + "/additionalfund/request",
                new com.android.volley.Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Timber.e(response);
                        hideProgressDialog();
                        try {

                            JSONObject jsonObject = new JSONObject(response);
                            Timber.e(jsonObject.toString());
                            if (jsonObject.has("success") && !jsonObject.isNull("success")) {
                                if (jsonObject.getBoolean("success")) {

                                    Intent intent = new Intent();
                                    Bundle bundle = new Bundle();
                                    bundle.putBoolean(ConstantKey.INCREASE_BUDGET, true);
                                    intent.putExtras(bundle);
                                    setResult(ConstantKey.RESULTCODE_INCREASE_BUDGET, intent);
                                    onBackPressed();
                                } else {
                                    showToast("Something went Wrong", IncreaseBudgetActivity.this);
                                }
                            }


                        } catch (JSONException e) {
                            Timber.e(String.valueOf(e));
                            e.printStackTrace();

                        }


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
                                    showCustomDialog(jsonObject_error.getString("message"));
                                }
                                if (jsonObject_error.has("errors")) {
                                    JSONObject jsonObject_errors = jsonObject_error.getJSONObject("errors");
                                }
                                //  ((CredentialActivity)getActivity()).showToast(message,getActivity());


                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else {
                            showToast("Something Went Wrong", IncreaseBudgetActivity.this);
                        }
                        Timber.e(error.toString());
                        hideProgressDialog();
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
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map1 = new HashMap<>();
                map1.put("amount", increase_budget);
                    map1.put("creation_reason", increase_budget_reason);

                Timber.e(String.valueOf(map1.size()));
                Timber.e(map1.toString());
                return map1;

            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, -1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(IncreaseBudgetActivity.this);
        requestQueue.add(stringRequest);
        Log.e(TAG, stringRequest.getUrl());

    }

    private boolean validation() {
        if(TextUtils.isEmpty(edtIncreaseBudget.getText().toString().trim())){
            edtIncreaseBudget.setError("?");
            return false;
        }else if(Integer.parseInt(edtIncreaseBudget.getText().toString().trim()) < 4){
            edtIncreaseBudget.setError("min. $5");
            return false;
        }else if(TextUtils.isEmpty(edtIncreaseBudgetReason.getText().toString().trim())){
            edtIncreaseBudgetReason.setError("?");
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

        ((AppCompatButton) dialog.findViewById(R.id.btn_ok)).setOnClickListener(new View.OnClickListener() {
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