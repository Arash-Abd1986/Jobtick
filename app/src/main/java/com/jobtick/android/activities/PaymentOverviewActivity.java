package com.jobtick.android.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.cardview.widget.CardView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;
import com.google.gson.Gson;
import com.jobtick.android.R;
import android.annotation.SuppressLint;
import android.widget.Toast;

import com.jobtick.android.fragments.PosterRequirementsBottomSheet;
import com.jobtick.android.fragments.others.AddCouponFragment;
import com.jobtick.android.models.CreditCardModel;
import com.jobtick.android.models.OfferModel;
import com.jobtick.android.models.TaskModel;
import com.jobtick.android.models.UserAccountModel;
import com.jobtick.android.models.calculation.PayingCalculationModel;
import com.jobtick.android.utils.Constant;
import com.jobtick.android.utils.ConstantKey;
import com.jobtick.android.utils.FireBaseEvent;
import com.jobtick.android.utils.ImageUtil;
import com.mikhaellopez.circularimageview.CircularImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import timber.log.Timber;

public class PaymentOverviewActivity extends ActivityBase implements PosterRequirementsBottomSheet.NoticeListener,AddCouponFragment.SubmitListener {

    private static final String TAG = PaymentOverviewActivity.class.getSimpleName();
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.toolbar)
    MaterialToolbar toolbar;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.img_avatar)
    CircularImageView imgAvatar;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.txt_user_name)
    MaterialTextView txtUserName;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.txt_post_title)
    MaterialTextView txtPostTitle;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.txt_discount_fee)
    MaterialTextView txtDiscountFee;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.txt_task_cost)
    MaterialTextView txtTaskCost;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.btnDeleteCoupon)
    MaterialButton btnDeleteCoupon;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.txt_service_fee)
    MaterialTextView txtServiceFee;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.txt_wallet_value)
    MaterialTextView txtWallet;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.txt_wallet_title)
    MaterialTextView txtWalletTitle;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.txt_total_cost)
    MaterialTextView txtTotalCost;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.tvCoupon)
    MaterialTextView tvCoupon;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.img_brand)
    CardView imgBrand;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.txt_account_number)
    MaterialTextView txtAccountNumber;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.txt_expires_date)
    MaterialTextView txtExpiresDate;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.btn_new)
    MaterialButton btnNew;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.rlt_payment_method)
    RelativeLayout rltPaymentMethod;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.btn_pay)
    MaterialButton btnPay;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.lyt_add_credit_card)
    LinearLayout lytAddCreditCard;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.llCoupon)
    LinearLayout llCoupon;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.relCouponDetails)
    RelativeLayout relCouponDetails;
    private TaskModel taskModel;
    private OfferModel offerModel;
    private UserAccountModel userAccountModel;

    private String coupon=null;
    private Gson gson;

    private final HashMap<PosterRequirementsBottomSheet.Requirement, Boolean> stateRequirement = new HashMap<>();
    float wallet;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.card_view_user)
    LinearLayout cardViewUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_overview);
        ButterKnife.bind(this);
        initToolbar();
        userAccountModel = sessionManager.getUserAccount();
        offerModel = new OfferModel();
        taskModel = TaskDetailsActivity.taskModel;
        offerModel = TaskDetailsActivity.offerModel;

        gson = new Gson();
        setUpData();
        getPaymentMethod();
    }
    AddCouponFragment addCouponFragment;
    private void setupCoupon(Integer netPayingAmount) {
        llCoupon.setOnClickListener(v -> {
            addCouponFragment = AddCouponFragment.newInstance(netPayingAmount);
            addCouponFragment.show(getSupportFragmentManager(), "");
        });

        if(coupon!=null){
            tvCoupon.setText(coupon);
            relCouponDetails.setVisibility(View.VISIBLE);
        }

        btnDeleteCoupon.setOnClickListener(v -> {
            coupon = null;
            relCouponDetails.setVisibility(View.GONE);
            llCoupon.setVisibility(View.VISIBLE);
            calculate(offerModel.getOfferPrice()+"");
        });
    }

    private void initToolbar() {
        toolbar.setNavigationIcon(R.drawable.ic_back);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Payment Overview");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void setUpData() {
        txtPostTitle.setText(taskModel.getTitle());
        txtUserName.setText(taskModel.getPoster().getName());
        txtTaskCost.setText(String.format(Locale.ENGLISH, "$%d", offerModel.getOfferPrice()));
        if (taskModel.getPoster().getAvatar() != null && taskModel.getPoster().getAvatar().getThumbUrl() != null) {
            ImageUtil.displayImage(imgAvatar, taskModel.getPoster().getAvatar().getThumbUrl(), null);
        } else {
            //TODO set default image
        }
        stateRequirement.put(PosterRequirementsBottomSheet.Requirement.CreditCard, false);
        setupCoupon(offerModel.getOfferPrice());
        calculate(Float.toString(offerModel.getOfferPrice()));

    }

    private void getPaymentMethod() {
        showProgressDialog();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Constant.URL_PAYMENTS_METHOD,
                response -> {
                    Timber.e(response);
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        Timber.e(jsonObject.toString());
                        if (jsonObject.has("success") && !jsonObject.isNull("success")) {
                            if (jsonObject.getBoolean("success")) {
                                if (jsonObject.has("data") && !jsonObject.isNull("data")) {

                                    String jsonString = jsonObject.toString(); //http request
                                    Gson gson = new Gson();
                                    CreditCardModel creditCardModel = gson.fromJson(jsonString, CreditCardModel.class);

                                    if (creditCardModel != null && creditCardModel.getData() != null && creditCardModel.getData().get(0).getCard() != null) {
                                        setUpLayout(creditCardModel);
                                    } else {
                                        setUpAddPaymentLayout();
                                    }
                                }
                            } else {
                                setUpAddPaymentLayout();
                                showToast("Something went Wrong", this);
                            }
                        }
                    } catch (JSONException e) {
                        Timber.e(String.valueOf(e));
                        e.printStackTrace();
                        setUpAddPaymentLayout();
                    }
                },
                error -> {
                    setUpAddPaymentLayout();
                    NetworkResponse networkResponse = error.networkResponse;
                    if (networkResponse != null && networkResponse.data != null) {
                        String jsonError = new String(networkResponse.data);
                        // Print Error!
                        Timber.e(jsonError);
                        try {
                            JSONObject jsonObject = new JSONObject(jsonError);
                            JSONObject jsonObject_error = jsonObject.getJSONObject("error");
                            if (jsonObject_error.has("error_code") && !jsonObject_error.isNull("error_code")) {
                                if (Objects.equals(ConstantKey.NO_PAYMENT_METHOD, jsonObject_error.getString("error_code"))) {
                                    return;
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    Timber.e(error.toString());
                    errorHandle1(error.networkResponse);
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
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void setUpAddPaymentLayout() {
        btnPay.setEnabled(false);
        btnPay.setAlpha(0.5f);
        rltPaymentMethod.setVisibility(View.GONE);
        lytAddCreditCard.setVisibility(View.VISIBLE);
        txtWallet.setVisibility(View.GONE);
        txtWalletTitle.setVisibility(View.GONE);
        hideProgressDialog();
    }

    private void setUpLayout(CreditCardModel creditCardModel) {
        btnPay.setEnabled(true);
        btnPay.setAlpha(1.0f);
        lytAddCreditCard.setVisibility(View.GONE);
        txtAccountNumber.setText(String.format("**** **** **** %s", creditCardModel.getData().get(0).getCard().getLast4()));
        txtExpiresDate.setText(String.format(Locale.ENGLISH, "Expiry Date: %d/%d", creditCardModel.getData().get(0).getCard().getExp_month(), creditCardModel.getData().get(0).getCard().getExp_year()));
        rltPaymentMethod.setVisibility(View.VISIBLE);
        if (creditCardModel.getData() != null && creditCardModel.getData().get(1) != null && creditCardModel.getData().get(1).getWallet() != null) {
            txtWallet.setVisibility(View.VISIBLE);
            txtWalletTitle.setVisibility(View.VISIBLE);
            txtWalletTitle.setText("Wallet Balance");
            wallet = creditCardModel.getData().get(1).getWallet().getBalance();
            txtWallet.setText(String.format(Locale.ENGLISH, "$ %.1f", wallet));
        } else {
            throw new IllegalStateException("There is no wallet value in api using provided format of object.");
        }
        calculate(Float.toString(offerModel.getOfferPrice()));
    }

    @OnClick({R.id.lyt_add_credit_card, R.id.btn_new, R.id.btn_pay})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_pay:
                payAcceptOffer();
                break;
            case R.id.btn_new:
                showCreditCardRequirementBottomSheet();
                setUpAddPaymentLayout();
                break;
            case R.id.lyt_add_credit_card:
                showCreditCardRequirementBottomSheet();
                break;
        }
    }

    private void showCreditCardRequirementBottomSheet() {
        PosterRequirementsBottomSheet posterRequirementsBottomSheet = PosterRequirementsBottomSheet.newInstance(stateRequirement);
        posterRequirementsBottomSheet.show(getSupportFragmentManager(), "");
    }

    private void payAcceptOffer() {
        showProgressDialog();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.URL_TASKS + "/" + taskModel.getSlug() + "/accept-offer",
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
                                    if (jsonObject.has("data") && !jsonObject.isNull("data")) {
                                        JSONObject jsonObject_data = jsonObject.getJSONObject("data");
                                        if (jsonObject_data.has("status") && !jsonObject_data.isNull("status")) {
                                            if (jsonObject_data.getString("status").equalsIgnoreCase("assigned")) {
                                                hideProgressDialog();

                                                FireBaseEvent.getInstance(getApplicationContext())
                                                        .sendEvent(FireBaseEvent.Event.PAYMENT_OVERVIEW,
                                                                FireBaseEvent.EventType.API_RESPOND_SUCCESS,
                                                                FireBaseEvent.EventValue.PAYMENT_OVERVIEW_SUBMIT);

                                                Intent intent = new Intent();
                                                Bundle bundle = new Bundle();
                                                bundle.putBoolean(ConstantKey.PAYMENT_OVERVIEW, true);
                                                intent.putExtras(bundle);
                                                setResult(ConstantKey.RESULTCODE_PAYMENTOVERVIEW, intent);
                                                intent = new Intent(PaymentOverviewActivity.this, CompleteMessageActivity.class);
                                                bundle = new Bundle();
                                                bundle.putString(ConstantKey.COMPLETES_MESSAGE_TITLE, "Your payment is secured, and you will be requested to release it after completion!");
                                                bundle.putString(ConstantKey.COMPLETES_MESSAGE_SUBTITLE, "Wait for an answer or continue looking for more tasks!");
                                                intent.putExtras(bundle);
                                                startActivity(intent);
                                                finish();
                                                return;
                                            }
                                        }
                                    }

                                    hideProgressDialog();

                                } else {
                                    hideProgressDialog();
                                    showToast("Something went Wrong", PaymentOverviewActivity.this);
                                }
                            }else{
                                showToast("Payment failed", PaymentOverviewActivity.this);
                            }


                        } catch (JSONException e) {
                            Timber.e(String.valueOf(e));
                            e.printStackTrace();
                            hideProgressDialog();
                        }


                    }
                },
                error -> {
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

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map1 = new HashMap<>();
                map1.put("offer_id", String.valueOf(offerModel.getId()));
                Timber.e(String.valueOf(map1.size()));
                Timber.e(map1.toString());
                return map1;

            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, -1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(PaymentOverviewActivity.this);
        requestQueue.add(stringRequest);
        Timber.e(stringRequest.getUrl());
    }

    public void calculate(String amount) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.URL_OFFERS_PAYING_CALCULATION,
                response -> {
                    hideProgressDialog();
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        String data = jsonObject.getString("data");

                        PayingCalculationModel model = gson.fromJson(data, PayingCalculationModel.class);
                        txtServiceFee.setText(String.format(Locale.ENGLISH, "$%.1f", model.getServiceFee()));

                        txtDiscountFee.setText(String.format(Locale.ENGLISH, "$%.1f", model.getDiscount()));
                        if(model.getNetPayingAmount()-wallet>=0) {
                            if(wallet>=0)
                                txtWallet.setText(String.format(Locale.ENGLISH, "-$%.1f", wallet));
                            else
                                txtWallet.setText(String.format(Locale.ENGLISH, "$%.1f", Math.abs(wallet)));

                            txtTotalCost.setText(String.format(Locale.ENGLISH, "$%.1f", model.getNetPayingAmount() - wallet));
                        }
                        else {
                            txtTotalCost.setText(String.format(Locale.ENGLISH, "$%.1f", 0f));
                            txtWallet.setText(String.format(Locale.ENGLISH, "-$%.1f", model.getNetPayingAmount()));
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    hideProgressDialog();
                    NetworkResponse networkResponse = error.networkResponse;
                    if (networkResponse != null && networkResponse.data != null) {
                        String jsonError = new String(networkResponse.data);
                        try {
                            JSONObject jsonObject = new JSONObject(jsonError);
                            JSONObject jsonObject_error = jsonObject.getJSONObject("error");
                            String message = jsonObject_error.getString("message");
                            PaymentOverviewActivity.this.showToast(message, PaymentOverviewActivity.this);

                        } catch (JSONException e) {
                            e.printStackTrace();
                            PaymentOverviewActivity.this.showToast("Something went wrong", PaymentOverviewActivity.this);
                        }
                    } else {
                        PaymentOverviewActivity.this.showToast("Something went wrong", PaymentOverviewActivity.this);
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
            protected Map<String, String> getParams() {
                Map<String, String> map1 = new HashMap<String, String>();
                map1.put("amount", amount);
                if(coupon!=null)
                    map1.put("discount_code", coupon);
                return map1;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, -1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(PaymentOverviewActivity.this);
        requestQueue.add(stringRequest);
    }

    @Override
    public void onCreditCardAdded() {
        getPaymentMethod();
    }

    @Override
    public void onVerifySubmit(String coupon) {
        calculate(offerModel.getOfferPrice()+"");
        this.coupon = coupon;
        tvCoupon.setText(coupon);
        llCoupon.setVisibility(View.GONE);
        relCouponDetails.setVisibility(View.VISIBLE);
        if (addCouponFragment!=null)
            addCouponFragment.dismiss();
    }

    @Override
    public void onClose() {
        if (addCouponFragment!=null)
            addCouponFragment.dismiss();
    }
}