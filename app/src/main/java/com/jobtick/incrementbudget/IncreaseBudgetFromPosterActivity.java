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
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatButton;
import androidx.cardview.widget.CardView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.appbar.MaterialToolbar;
import com.jobtick.edit_text.EditTextRegular;
import com.jobtick.edit_text.EditTextSemiBold;
import com.jobtick.R;
import com.jobtick.TextView.TextViewBold;
import com.jobtick.TextView.TextViewMedium;
import com.jobtick.TextView.TextViewRegular;
import com.jobtick.TextView.TextViewSemiBold;
import com.jobtick.activities.ActivityBase;
import com.jobtick.activities.TaskDetailsActivity;
import com.jobtick.activities.UserProfileActivity;
import com.jobtick.models.TaskModel;
import com.jobtick.models.UserAccountModel;
import com.jobtick.models.payments.PaymentMethodModel;
import com.jobtick.utils.Constant;
import com.jobtick.utils.ConstantKey;
import com.jobtick.utils.HttpStatus;
import com.jobtick.utils.SessionManager;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.stripe.android.ApiResultCallback;
import com.stripe.android.Stripe;
import com.stripe.android.model.Card;
import com.stripe.android.model.PaymentMethod;
import com.stripe.android.model.PaymentMethodCreateParams;
import com.stripe.android.view.CardMultilineWidget;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import timber.log.Timber;

import static com.jobtick.utils.Constant.URL_BUDGET_Increment;
import static com.jobtick.utils.ConstantKey.PUBLISHABLE_KEY;

public class IncreaseBudgetFromPosterActivity extends ActivityBase {

    private static final String TAG = IncreaseBudgetFromPosterActivity.class.getName();
    @BindView(R.id.toolbar)
    MaterialToolbar toolbar;
    @BindView(R.id.img_avatar)
    CircularImageView imgAvatar;
    @BindView(R.id.img_verified)
    ImageView imgVerified;
    @BindView(R.id.txt_user_name)
    TextViewBold txtUserName;
    @BindView(R.id.txt_post_title)
    TextViewRegular txtPostTitle;
    @BindView(R.id.txt_budget)
    TextViewSemiBold txtBudget;
    @BindView(R.id.txt_btn_update_amount)
    TextViewRegular txtBtnUpdateAmount;
    @BindView(R.id.edt_increase_budget)
    EditTextSemiBold edtIncreaseBudget;
    @BindView(R.id.card_increase_budget_layout)
    CardView cardIncreaseBudgetLayout;
    @BindView(R.id.txt_total_cost)
    TextViewBold txtTotalCost;
    @BindView(R.id.img_brand)
    ImageView imgBrand;
    @BindView(R.id.txt_account_number)
    TextViewBold txtAccountNumber;
    @BindView(R.id.txt_expires_date)
    TextViewMedium txtExpiresDate;
    @BindView(R.id.txt_btn_update)
    TextView txtBtnUpdate;
    @BindView(R.id.card_update)
    CardView cardUpdate;
    @BindView(R.id.rlt_payment_method)
    RelativeLayout rltPaymentMethod;
    @BindView(R.id.card_multiline_widget)
    CardMultilineWidget cardMultilineWidget;
    @BindView(R.id.lyt_btn_add_payment_method)
    LinearLayout lytBtnAddPaymentMethod;
    @BindView(R.id.card_add_payment_method)
    CardView cardAddPaymentMethod;
    @BindView(R.id.lyt_card_details)
    LinearLayout lytCardDetails;
    @BindView(R.id.lyt_btn_pay)
    LinearLayout lytBtnPay;
    @BindView(R.id.card_pay)
    CardView cardPay;
    @BindView(R.id.edt_note)
    EditTextRegular edtNote;
    /*@BindView(R.id.txt_job_cost)
    TextViewBold txtJobCost;*/


    private Card card_xml;
    private TaskModel taskModel;
    private UserAccountModel userAccountModel;
    private SessionManager sessionManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_increase_budget_from_poster);
        ButterKnife.bind(this);
        sessionManager = new SessionManager(this);
        userAccountModel = sessionManager.getUserAccount();

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

      /*  taskModel = new TaskModel();
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            taskModel = bundle.getParcelable(ConstantKey.TASK);
        }*/
        taskModel= TaskDetailsActivity.taskModel;


        setdata();
        getPaymentMethod();


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
                if (s.length() == 0) {
                    setupBudget(0);
                } else {
                    setupBudget(Integer.parseInt(s.toString()));
                }
            }
        });


    }


    @OnClick({R.id.txt_btn_update, R.id.img_avatar, R.id.txt_btn_update_amount, R.id.lyt_btn_add_payment_method, R.id.lyt_btn_pay, R.id.card_view_user})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.img_avatar:
                break;
            case R.id.txt_btn_update_amount:
                txtBtnUpdateAmount.setVisibility(View.GONE);
                cardIncreaseBudgetLayout.setVisibility(View.VISIBLE);
                break;
            case R.id.txt_btn_update:
                setUpAddPaymentLayout();
                break;
            case R.id.lyt_btn_add_payment_method:
                card_xml = cardMultilineWidget.getCard();
                //  card.toParamMap("CARD",cardMultilineWidget.getCard());
                if (card_xml == null) {
                    showToast("Invalid Card Data", this);
                } else {
                    getToken();
                }
                break;
            case R.id.lyt_btn_pay:
                if (validation()) {
                    submitIncreaseBudget(edtIncreaseBudget.getText().toString().trim(), edtNote.getText().toString().trim());
                }

                break;
            case R.id.card_view_user:
                Bundle bundleProfile = new Bundle();
                bundleProfile.putInt(Constant.userID, taskModel.getWorker().getId());
                Intent intent = new Intent(IncreaseBudgetFromPosterActivity.this, UserProfileActivity.class);
                intent.putExtras(bundleProfile);
                startActivity(intent);
                break;
        }
    }


    private void submitIncreaseBudget(String increase_budget, String increase_budget_reason) {
        showProgressDialog();
        StringRequest stringRequest = new StringRequest(StringRequest.Method.POST, Constant.URL_TASKS + "/" + taskModel.getSlug() + URL_BUDGET_Increment,
                response -> {
                    Timber.e(response);
                    hideProgressDialog();
                    try {

                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.has("success") && !jsonObject.isNull("success")) {
                            if (jsonObject.getBoolean("success")) {

                                Intent intent = new Intent();
                                Bundle bundle = new Bundle();
                                bundle.putBoolean(ConstantKey.INCREASE_BUDGET, true);
                                intent.putExtras(bundle);
                                setResult(ConstantKey.RESULTCODE_INCREASE_BUDGET, intent);
                                onBackPressed();
                            } else {
                                showToast("Something went Wrong", IncreaseBudgetFromPosterActivity.this);
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
                            hideProgressDialog();
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
                        showToast("Something Went Wrong", IncreaseBudgetFromPosterActivity.this);
                    }
                    Timber.e(error.toString());
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
        RequestQueue requestQueue = Volley.newRequestQueue(IncreaseBudgetFromPosterActivity.this);
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

    private boolean validation() {
        if (TextUtils.isEmpty(edtIncreaseBudget.getText().toString().trim())) {
            edtIncreaseBudget.setError("?");
            return false;
        } else if (Integer.parseInt(edtIncreaseBudget.getText().toString().trim()) < 4) {
            edtIncreaseBudget.setError("min. $5");
            return false;
        } else if (Integer.parseInt(edtIncreaseBudget.getText().toString().trim()) > 500) {
            edtIncreaseBudget.setError("max. $500");
            return false;
        } else if (TextUtils.isEmpty(edtNote.getText().toString().trim())) {
            edtNote.setError("?");
            return false;
        }
        return true;
    }

    private void setdata() {
        txtBudget.setText(taskModel.getAmount() + "");
        txtUserName.setText(taskModel.getWorker().getName());
        txtPostTitle.setText(taskModel.getDescription());
    }


    private void setupBudget(int increase_budget) {
        float total_budget = increase_budget + taskModel.getAmount();
        txtTotalCost.setText("$ " + total_budget);
        //  txtJobCost.setText("$ " + increase_budget);
    }

    private void getPaymentMethod() {
        showProgressDialog();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Constant.URL_PAYMENTS_METHOD,
                response -> {
                    Timber.e(response);
                    hideProgressDialog();
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
                                        showToast("card not Available", IncreaseBudgetFromPosterActivity.this);
                                    }

                                }
                            } else {
                                showToast("Something went Wrong", IncreaseBudgetFromPosterActivity.this);
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
                    hideProgressDialog();
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
        RequestQueue requestQueue = Volley.newRequestQueue(IncreaseBudgetFromPosterActivity.this);
        requestQueue.add(stringRequest);
        Log.e(TAG, stringRequest.getUrl());
    }

    private void setUpAddPaymentLayout() {
        lytBtnPay.setEnabled(false);
        cardPay.setAlpha(0.5f);
        rltPaymentMethod.setVisibility(View.GONE);
        lytCardDetails.setVisibility(View.VISIBLE);
    }

    private void setUpLayout(PaymentMethodModel paymentMethodModel) {
        lytBtnPay.setEnabled(true);
        cardPay.setAlpha(1.0f);
        rltPaymentMethod.setVisibility(View.VISIBLE);
        lytCardDetails.setVisibility(View.GONE);
        txtAccountNumber.setText("**** **** **** " + paymentMethodModel.getLast4());
        txtExpiresDate.setText("Expires " + paymentMethodModel.getExpMonth() + "/" + paymentMethodModel.getExpYear());
    }

    private void getToken() {
        boolean validation = card_xml.validateCard();

        if (validation) {
            // startProgress("Validating Credit Card");
            showProgressDialog();
            Stripe stripe = new Stripe(getApplicationContext(),
                    PUBLISHABLE_KEY);
            PaymentMethodCreateParams paymentMethod = PaymentMethodCreateParams.create(card_xml.toPaymentMethodParamsCard());
            stripe.createPaymentMethod(paymentMethod, new ApiResultCallback<PaymentMethod>() {
                @Override
                public void onSuccess(PaymentMethod paymentMethod) {
                    addPaymentTokenTOServer(paymentMethod.id);
                    Log.e("Stripe_token", String.valueOf(paymentMethod.id));
                    //  hidepDialog();
                }

                @Override
                public void onError(@NotNull Exception e) {
                    hideProgressDialog();
                    Log.e("Stripe", e.toString());
                }
            });

        } else if (!card_xml.validateNumber()) {
            Log.e("Stripe", "The card number that you entered is invalid");
        } else if (!card_xml.validateExpiryDate()) {
            Log.e("Stripe", "The expiration date that you entered is invalid");
        } else if (!card_xml.validateCVC()) {
            Log.e("Stripe", "The CVC code that you entered is invalid");
        } else {
            Log.e("Stripe", "The card details that you entered are invalid");
        }

    }

    private void addPaymentTokenTOServer(String pm_token) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.URL_PAYMENTS_METHOD,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Timber.e(response);
                        //   hidepDialog();
                        try {

                            JSONObject jsonObject = new JSONObject(response);
                            Log.e("error", jsonObject.toString());
                            if (jsonObject.has("success") && !jsonObject.isNull("success")) {
                                if (jsonObject.getBoolean("success")) {
                                    getPaymentMethod();
                                } else {
                                    hideProgressDialog();
                                    showToast("Something went Wrong", IncreaseBudgetFromPosterActivity.this);
                                }
                            }


                        } catch (JSONException e) {
                            Timber.e(String.valueOf(e));
                            e.printStackTrace();
                            hideProgressDialog();
                        }


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        errorHandle1(error.networkResponse);
                        hideProgressDialog();
                    }
                }) {


            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> map1 = new HashMap<String, String>();

                map1.put("authorization", sessionManager.getTokenType() + " " + sessionManager.getAccessToken());
                map1.put("Content-Type", "application/x-www-form-urlencoded");
                // map1.put("X-Requested-With", "XMLHttpRequest");
                return map1;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map1 = new HashMap<>();
                map1.put("pm_token", pm_token);
                Timber.e(String.valueOf(map1.size()));
                Timber.e(map1.toString());
                return map1;

            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, -1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(IncreaseBudgetFromPosterActivity.this);
        requestQueue.add(stringRequest);
        Log.e(TAG, stringRequest.getUrl());
    }


}