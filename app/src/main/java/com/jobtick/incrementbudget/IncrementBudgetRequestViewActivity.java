package com.jobtick.incrementbudget;

import android.app.Dialog;
import android.app.ProgressDialog;
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
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.appbar.MaterialToolbar;
import com.jobtick.EditText.EditTextRegular;
import com.jobtick.R;
import com.jobtick.TextView.TextViewBold;
import com.jobtick.TextView.TextViewMedium;
import com.jobtick.TextView.TextViewRegular;
import com.jobtick.activities.ActivityBase;
import com.jobtick.activities.CompleteMessageActivity;
import com.jobtick.activities.TaskDetailsActivity;
import com.jobtick.models.TaskModel;
import com.jobtick.models.UserAccountModel;
import com.jobtick.models.payments.PaymentMethodModel;
import com.jobtick.utils.Constant;
import com.jobtick.utils.ConstantKey;
import com.jobtick.utils.HttpStatus;
import com.jobtick.utils.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import timber.log.Timber;

import static com.jobtick.utils.Constant.URL_ADDITIONAL_FUND;
import static com.jobtick.utils.ConstantKey.RESULTCODE_INCREASE_BUDGET;

public class IncrementBudgetRequestViewActivity extends ActivityBase {


    @BindView(R.id.toolbar)
    MaterialToolbar toolbar;

    @BindView(R.id.rlt_payment_method)
    RelativeLayout rltPaymentMethod;
    @BindView(R.id.txt_expires_date)
    TextViewMedium txtExpiresDate;
    @BindView(R.id.lyt_card_details)
    LinearLayout lytCardDetails;

    @BindView(R.id.txt_account_number)
    TextViewBold txtAccountNumber;


    @BindView(R.id.txt_total_cost)
    TextViewBold txtTotalCost;


    @BindView(R.id.txt_increase_budget)
    TextViewBold txt_increase_budget;

    @BindView(R.id.txt_previous_budget)
    TextViewBold txt_previous_budget;

    @BindView(R.id.lyt_btn_accept)
    LinearLayout lytBtnAccept;

    @BindView(R.id.lyt_btn_decline)
    LinearLayout lytBtnDecline;
    protected ProgressDialog pDialog;

    @BindView(R.id.txt_req_from)
    TextViewRegular txtReqFrom;

    @BindView(R.id.txt_reason_increase)
    TextViewRegular txtReasonIncrease;


    private TaskModel taskModel;
    private static final String TAG = IncreaseBudgetFromPosterActivity.class.getName();
    private SessionManager sessionManager;
    private UserAccountModel userAccountModel;
    private Boolean isMyTask = false;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_increment_budget_req);
        ButterKnife.bind(this);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
        sessionManager = new SessionManager(this);
        userAccountModel = sessionManager.getUserAccount();

      //  taskModel = new TaskModel();
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            isMyTask = bundle.getBoolean(ConstantKey.IS_MY_TASK);
        }
        taskModel= TaskDetailsActivity.taskModel;

        setData();
        getPaymentMethod();
    }


    private void setData() {

        txt_previous_budget.setText(taskModel.getBudget().toString());
        txt_increase_budget.setText(taskModel.getAdditionalFund().getAmount().toString());
        int totalCost = taskModel.getBudget() + taskModel.getAdditionalFund().getAmount();
        txtTotalCost.setText("$ " + taskModel.getAdditionalFund().getAmount());
        txtReasonIncrease.setText(taskModel.getAdditionalFund().getCreationReason());
        txtReqFrom.setText("New budget request form  " + taskModel.getWorker().getName() + ".");
    }


    @OnClick({R.id.lyt_btn_accept, R.id.lyt_btn_decline})
    public void onViewClicked(View view) {

        switch (view.getId()) {
            case R.id.lyt_btn_decline:
                Intent intent = new Intent(IncrementBudgetRequestViewActivity.this, IncrementBudgetDeclineActivity.class);
                Bundle bundle = new Bundle();
                bundle.putParcelable(ConstantKey.TASK, taskModel);
                bundle.putBoolean(ConstantKey.IS_MY_TASK, isMyTask);
                intent.putExtras(bundle);
                startActivity(intent);
                finish();


                break;
            case R.id.lyt_btn_accept:
                acceptRequest(String.valueOf(taskModel.getAdditionalFund().getId()));
                break;
        }

    }

/*    private void askForReason() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.dialog_ask_for_reason);
        dialog.setCancelable(true);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        EditTextRegular edtReason = dialog.findViewById(R.id.edt_reason);
        dialog.findViewById(R.id.card_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.findViewById(R.id.lyt_btn_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(edtReason.getText().toString())) {
                    rejectRequest(String.valueOf(taskModel.getAdditionalFund().getId()), edtReason.getText().toString());

                } else {
                    showCustomDialog("Please enter reason for decline");
                }
            }
        });

        dialog.show();
        dialog.getWindow().setAttributes(lp);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }


    private void rejectRequest(String id, String rejectReason) {
        showpDialog();
        StringRequest stringRequest = new StringRequest(StringRequest.Method.POST, Constant.BASE_URL + URL_ADDITIONAL_FUND + "/" + id + "/reject",
                response -> {
                    Timber.e(response);
                    hidepDialog();
                    try {

                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.has("success") && !jsonObject.isNull("success")) {
                            if (jsonObject.getBoolean("success")) {
                                Intent intent = new Intent(IncrementBudgetRequestViewActivity.this, CompleteMessageActivity.class);
                                Bundle bundle1 = new Bundle();
                                bundle1.putString(ConstantKey.COMPLETES_MESSAGE_TITLE, "Request Declined");
                                bundle1.putString(ConstantKey.COMPLETES_MESSAGE_SUBTITLE, "Budget Request has been Declined !");
                                intent.putExtras(bundle1);
                                startActivity(intent);
                                finish();
                            } else {
                                showToast("Something went Wrong", IncrementBudgetRequestViewActivity.this);
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
                        Log.e("error", jsonError);
                        if (networkResponse.statusCode == HttpStatus.AUTH_FAILED) {
                            unauthorizedUser();
                            hidepDialog();
                            return;
                        }
                        try {
                            JSONObject jsonObject = new JSONObject(jsonError);


                            JSONObject jsonObject_error = jsonObject.getJSONObject("error");


                            if (jsonObject_error.has("errors")) {
                                JSONObject jsonObject_errors = jsonObject_error.getJSONObject("errors");
                                if (jsonObject_errors.has("amount") && !jsonObject_errors.isNull("amount")) {
                                    JSONArray jsonArray_amount = jsonObject_errors.getJSONArray("amount");
                                    showCustomDialog(jsonArray_amount.getString(0));
                                } else if (jsonObject_errors.has("creation_reason") && !jsonObject_errors.isNull("creation_reason")) {
                                    JSONArray jsonArray_amount = jsonObject_errors.getJSONArray("creation_reason");
                                    showCustomDialog(jsonArray_amount.getString(0));
                                }
                            } else {
                                if (jsonObject_error.has("message")) {
                                    showCustomDialog(jsonObject_error.getString("message"));
                                }
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        showToast("Something Went Wrong", IncrementBudgetRequestViewActivity.this);
                    }
                    Timber.e(error.toString());
                    hidepDialog();
                }) {


            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> map1 = new HashMap<String, String>();

                map1.put("authorization", sessionManager.getTokenType() + " " + sessionManager.getAccessToken());
                map1.put("Content-Type", "application/x-www-form-urlencoded");
                map1.put("X-Requested-With", "XMLHttpRequest");
                return map1;
            }


            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map1 = new HashMap<String, String>();

                map1.put("rejection_reason", rejectReason);
                return map1;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, -1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(IncrementBudgetRequestViewActivity.this);
        requestQueue.add(stringRequest);
        Log.e(TAG, stringRequest.getUrl());

    }*/

    private void acceptRequest(String id) {
        showpDialog();
        StringRequest stringRequest = new StringRequest(StringRequest.Method.GET, Constant.BASE_URL + URL_ADDITIONAL_FUND + "/" + id + "/accept",
                response -> {
                    Timber.e(response);
                    hidepDialog();
                    try {

                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.has("success") && !jsonObject.isNull("success")) {
                            if (jsonObject.getBoolean("success")) {
                                Intent intent = new Intent(IncrementBudgetRequestViewActivity.this, CompleteMessageActivity.class);
                                Bundle bundle1 = new Bundle();
                                bundle1.putInt(ConstantKey.COMPLETES_MESSAGE_FROM, RESULTCODE_INCREASE_BUDGET);
                                bundle1.putString(ConstantKey.COMPLETES_MESSAGE_TITLE, "Request Accepted");
                                bundle1.putString(ConstantKey.COMPLETES_MESSAGE_SUBTITLE, "Budget Request has been accepted ! ");
                                intent.putExtras(bundle1);
                                startActivity(intent);
                                finish();
                            } else {
                                showToast("Something went Wrong", IncrementBudgetRequestViewActivity.this);
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
                        Log.e("error", jsonError);
                        if (networkResponse.statusCode == HttpStatus.AUTH_FAILED) {
                            unauthorizedUser();
                            hidepDialog();
                            return;
                        }
                        try {
                            JSONObject jsonObject = new JSONObject(jsonError);


                            JSONObject jsonObject_error = jsonObject.getJSONObject("error");


                            if (jsonObject_error.has("errors")) {
                                JSONObject jsonObject_errors = jsonObject_error.getJSONObject("errors");
                                if (jsonObject_errors.has("amount") && !jsonObject_errors.isNull("amount")) {
                                    JSONArray jsonArray_amount = jsonObject_errors.getJSONArray("amount");
                                    showCustomDialog(jsonArray_amount.getString(0));
                                } else if (jsonObject_errors.has("creation_reason") && !jsonObject_errors.isNull("creation_reason")) {
                                    JSONArray jsonArray_amount = jsonObject_errors.getJSONArray("creation_reason");
                                    showCustomDialog(jsonArray_amount.getString(0));
                                }
                            } else {
                                if (jsonObject_error.has("message")) {
                                    showCustomDialog(jsonObject_error.getString("message"));
                                }
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        showToast("Something Went Wrong", IncrementBudgetRequestViewActivity.this);
                    }
                    Timber.e(error.toString());
                    hidepDialog();
                }) {


            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> map1 = new HashMap<String, String>();

                map1.put("authorization", sessionManager.getTokenType() + " " + sessionManager.getAccessToken());
                map1.put("Content-Type", "application/x-www-form-urlencoded");
                //   map1.put("X-Requested-With", "XMLHttpRequest");
                return map1;
            }

        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, -1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(IncrementBudgetRequestViewActivity.this);
        requestQueue.add(stringRequest);
        Log.e(TAG, stringRequest.getUrl());

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


    public void initpDialog() {
        pDialog = new ProgressDialog(this);
        pDialog.setTitle(getString(R.string.processing));
        pDialog.setMessage(getString(R.string.please_wait));
        pDialog.setCancelable(false);
    }


    public void showpDialog() {

        if (!pDialog.isShowing())
            pDialog.show();
    }

    public void hidepDialog() {

        if (pDialog.isShowing())
            pDialog.dismiss();
    }

    private void getPaymentMethod() {
        showpDialog();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Constant.URL_PAYMENTS_METHOD,
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
                                    if (jsonObject_data.has("card") && !jsonObject_data.isNull("card")) {
                                        JSONObject jsonObject_card = jsonObject_data.getJSONObject("card");
                                        PaymentMethodModel paymentMethodModel = new PaymentMethodModel().getJsonToModel(jsonObject_card);
                                        setUpLayout(paymentMethodModel);
                                    } else {
                                        showToast("card not Available", IncrementBudgetRequestViewActivity.this);
                                    }

                                }
                            } else {
                                showToast("Something went Wrong", IncrementBudgetRequestViewActivity.this);
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
                        try {
                            JSONObject jsonObject = new JSONObject(jsonError);
                            JSONObject jsonObject_error = jsonObject.getJSONObject("error");
                            if (jsonObject_error.has("error_text") && !jsonObject_error.isNull("error_text")) {
                                if (ConstantKey.NO_PAYMENT_METHOD.equalsIgnoreCase(jsonObject_error.getString("error_text"))) {
                                    setUpAddPaymentLayout();
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    Timber.e(error.toString());
                    errorHandle1(error.networkResponse);
                    hidepDialog();
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> map1 = new HashMap<String, String>();
                map1.put("authorization", sessionManager.getTokenType() + " " + sessionManager.getAccessToken());
                map1.put("Content-Type", "application/x-www-form-urlencoded");
                // map1.put("X-Requested-With", "XMLHttpRequest");
                return map1;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, -1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(IncrementBudgetRequestViewActivity.this);
        requestQueue.add(stringRequest);
        Log.e(TAG, stringRequest.getUrl());
    }


    private void setUpAddPaymentLayout() {
        // cardPay.setAlpha(0.5f);
        rltPaymentMethod.setVisibility(View.GONE);
        lytCardDetails.setVisibility(View.VISIBLE);
    }

    private void setUpLayout(PaymentMethodModel paymentMethodModel) {
        // cardPay.setAlpha(1.0f);
        rltPaymentMethod.setVisibility(View.VISIBLE);
        lytCardDetails.setVisibility(View.GONE);
        txtAccountNumber.setText("**** **** **** " + paymentMethodModel.getLast4());
        txtExpiresDate.setText("Expires " + paymentMethodModel.getExpMonth() + "/" + paymentMethodModel.getExpYear());
    }

}
