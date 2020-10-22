package com.jobtick.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
import com.jobtick.R;
import com.jobtick.TextView.TextViewBold;
import com.jobtick.TextView.TextViewMedium;
import com.jobtick.TextView.TextViewRegular;
import com.jobtick.models.TaskModel;
import com.jobtick.models.payments.PaymentMethodModel;
import com.jobtick.utils.Constant;
import com.jobtick.utils.ConstantKey;
import com.jobtick.utils.HttpStatus;
import com.stripe.android.ApiResultCallback;
import com.stripe.android.Stripe;
import com.stripe.android.model.Card;
import com.stripe.android.model.PaymentMethod;
import com.stripe.android.model.PaymentMethodCreateParams;
import com.stripe.android.view.CardMultilineWidget;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import timber.log.Timber;

import static com.jobtick.utils.ConstantKey.PUBLISHABLE_KEY;

public class RequestForIncreaseBudgetActivity extends ActivityBase {

    private static final String TAG = RequestForIncreaseBudgetActivity.class.getName();
    @BindView(R.id.toolbar)
    MaterialToolbar toolbar;
    @BindView(R.id.img_icon)
    ImageView imgIcon;
    @BindView(R.id.txt_text)
    TextViewRegular txtText;
    @BindView(R.id.txt_username)
    TextViewRegular txtUsername;
    @BindView(R.id.txt_reason)
    TextViewRegular txtReason;
    @BindView(R.id.txt_pervious_budget)
    TextViewBold txtPerviousBudget;
    @BindView(R.id.txt_increase_budget)
    TextViewBold txtIncreaseBudget;
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
    @BindView(R.id.lyt_btn_decline)
    LinearLayout lytBtnDecline;
    @BindView(R.id.card_button)
    CardView cardButton;
    @BindView(R.id.lyt_btn_accept)
    LinearLayout lytBtnAccept;
    @BindView(R.id.lyt_button)
    LinearLayout lytButton;
    @BindView(R.id.card_accept)
    CardView cardAccept;


    private Card card_xml;
    private TaskModel taskModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_for_increase_budget);
        ButterKnife.bind(this);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        taskModel = new TaskModel();
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            taskModel = bundle.getParcelable(ConstantKey.TASK);
        }

        setUpData();
        getPaymentMethod();

    }


    private void setUpData() {
        txtUsername.setText(taskModel.getWorker().getName());
        txtPerviousBudget.setText("$ " + taskModel.getAmount());
        txtTotalCost.setText("$ " + taskModel.getAmount());
    }

    private void getPaymentMethod() {
        showProgressDialog();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Constant.URL_PAYMENTS_METHOD,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
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
                                            showToast("card not Available", RequestForIncreaseBudgetActivity.this);
                                        }

                                    }
                                } else {
                                    showToast("Something went Wrong", RequestForIncreaseBudgetActivity.this);
                                }
                            }
                        } catch (JSONException e) {
                            Timber.e(String.valueOf(e));
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
                            try {
                                JSONObject jsonObject = new JSONObject(jsonError);
                                JSONObject jsonObject_error = jsonObject.getJSONObject("error");
                                if (jsonObject_error.has("error_text") && !jsonObject_error.isNull("error_text")) {
                                    if (ConstantKey.NO_PAYMENT_METHOD.equalsIgnoreCase(jsonObject_error.getString("error_text"))) {
                                        setUpAddPaymentLayout();
                                    }
                                }
                                if (networkResponse.statusCode == HttpStatus.AUTH_FAILED) {
                                    unauthorizedUser();
                                    hideProgressDialog();
                                    return;
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        Timber.e(error.toString());
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
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, -1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(RequestForIncreaseBudgetActivity.this);
        requestQueue.add(stringRequest);
        Log.e(TAG, stringRequest.getUrl());
    }

    private void setUpAddPaymentLayout() {
        lytBtnAccept.setEnabled(false);
        cardAccept.setAlpha(0.5f);
        rltPaymentMethod.setVisibility(View.GONE);
        lytCardDetails.setVisibility(View.VISIBLE);
    }

    private void setUpLayout(PaymentMethodModel paymentMethodModel) {
        lytBtnAccept.setEnabled(true);
        cardAccept.setAlpha(1.0f);
        rltPaymentMethod.setVisibility(View.VISIBLE);
        lytCardDetails.setVisibility(View.GONE);
        txtAccountNumber.setText("**** **** **** " + paymentMethodModel.getLast4());
        txtExpiresDate.setText("Expires " + paymentMethodModel.getExpMonth() + "/" + paymentMethodModel.getExpYear());
    }


    @OnClick({R.id.txt_btn_update, R.id.lyt_btn_add_payment_method, R.id.lyt_btn_decline, R.id.lyt_btn_accept})
    public void onViewClicked(View view) {
        switch (view.getId()) {
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
            case R.id.lyt_btn_decline:

                break;
            case R.id.lyt_btn_accept:
                
                break;
        }
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
                            Timber.e(jsonObject.toString());
                            if (jsonObject.has("success") && !jsonObject.isNull("success")) {
                                if (jsonObject.getBoolean("success")) {
                                    getPaymentMethod();
                                } else {
                                    hideProgressDialog();
                                    showToast("Something went Wrong", RequestForIncreaseBudgetActivity.this);
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
        RequestQueue requestQueue = Volley.newRequestQueue(RequestForIncreaseBudgetActivity.this);
        requestQueue.add(stringRequest);
        Log.e(TAG, stringRequest.getUrl());
    }

    

}