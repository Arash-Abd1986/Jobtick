package com.jobtick.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

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
import com.jobtick.R;
import com.jobtick.fragments.PosterRequirementsBottomSheet;
import com.jobtick.models.OfferModel;
import com.jobtick.models.TaskModel;
import com.jobtick.models.UserAccountModel;
import com.jobtick.models.payments.PaymentMethodModel;
import com.jobtick.utils.Constant;
import com.jobtick.utils.ConstantKey;
import com.jobtick.utils.ImageUtil;
import com.mikhaellopez.circularimageview.CircularImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import timber.log.Timber;

public class PaymentOverviewActivity extends ActivityBase {

    private static final String TAG = PaymentOverviewActivity.class.getSimpleName();
    @BindView(R.id.toolbar)
    MaterialToolbar toolbar;
    @BindView(R.id.img_avatar)
    CircularImageView imgAvatar;
    @BindView(R.id.img_verified)
    ImageView imgVerified;
    @BindView(R.id.txt_user_name)
    MaterialTextView txtUserName;
    @BindView(R.id.txt_post_title)
    MaterialTextView txtPostTitle;
    @BindView(R.id.txt_task_cost)
    MaterialTextView txtTaskCost;
    @BindView(R.id.txt_service_fee)
    MaterialTextView txtServiceFee;
    @BindView(R.id.txt_total_cost)
    MaterialTextView txtTotalCost;
    @BindView(R.id.img_brand)
    ImageView imgBrand;
    @BindView(R.id.txt_account_number)
    MaterialTextView txtAccountNumber;
    @BindView(R.id.txt_expires_date)
    MaterialTextView txtExpiresDate;
    @BindView(R.id.btn_new)
    MaterialButton btnNew;

    @BindView(R.id.rlt_payment_method)
    RelativeLayout rltPaymentMethod;

    @BindView(R.id.btn_pay)
    MaterialButton btnPay;

    @BindView(R.id.lyt_add_credit_card)
    LinearLayout lytAddCreditCard;

    private TaskModel taskModel;
    private OfferModel offerModel;
    private UserAccountModel userAccountModel;

    private final HashMap<PosterRequirementsBottomSheet.Requirement, Boolean> stateRequirement = new HashMap<>();

    float final_task_cost;
    float final_service_fee;
    float final_discount_amount = 0;
    float final_total_cost;

    @BindView(R.id.card_view_user)
    LinearLayout cardViewUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_overview);
        ButterKnife.bind(this);
        userAccountModel = sessionManager.getUserAccount();

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        offerModel = new OfferModel();
        taskModel = TaskDetailsActivity.taskModel;
        offerModel = TaskDetailsActivity.offerModel;

        setUpData();
        getPaymentMethod();
    }

    private void setUpData() {
        txtPostTitle.setText(taskModel.getTitle());
        txtUserName.setText(taskModel.getPoster().getName());
        final_task_cost = offerModel.getOfferPrice();
        final_service_fee = getServiceFee();
        final_total_cost = (final_task_cost + final_service_fee) - final_discount_amount;
        finalCalculation();

        if (taskModel.getPoster().getEmailVerifiedAt() != null) {
            imgVerified.setVisibility(View.VISIBLE);
        } else {
            imgVerified.setVisibility(View.GONE);
        }
        if (taskModel.getPoster().getAvatar() != null && taskModel.getPoster().getAvatar().getThumbUrl() != null) {
            ImageUtil.displayImage(imgAvatar, taskModel.getPoster().getAvatar().getThumbUrl(), null);
        } else {
            //TODO set default image
        }
        stateRequirement.put(PosterRequirementsBottomSheet.Requirement.CreditCard, false);
    }

    private float getServiceFee() {
        return (final_task_cost * taskModel.getPoster().getPosterTier().getServiceFee()) / 100;
    }

    public void getPaymentMethod() {
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
                                        showToast("card not Available", PaymentOverviewActivity.this);
                                    }

                                }
                            } else {
                                showToast("Something went Wrong", PaymentOverviewActivity.this);
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
                            if (jsonObject_error.has("error_code") && !jsonObject_error.isNull("error_code")) {
                                if (Objects.equals(ConstantKey.NO_PAYMENT_METHOD, jsonObject_error.getString("error_code"))) {
                                    setUpAddPaymentLayout();
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    Timber.e(error.toString());
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
        RequestQueue requestQueue = Volley.newRequestQueue(PaymentOverviewActivity.this);
        requestQueue.add(stringRequest);
        Log.e(TAG, stringRequest.getUrl());
    }

    private void setUpAddPaymentLayout() {
        btnPay.setEnabled(false);
        btnPay.setAlpha(0.5f);
        rltPaymentMethod.setVisibility(View.GONE);
        lytAddCreditCard.setVisibility(View.VISIBLE);
    }

    private void setUpLayout(PaymentMethodModel paymentMethodModel) {
        btnPay.setEnabled(true);
        btnPay.setAlpha(1.0f);
        lytAddCreditCard.setVisibility(View.GONE);
        txtAccountNumber.setText("**** **** **** " + paymentMethodModel.getLast4());
        txtExpiresDate.setText("Expires " + paymentMethodModel.getExpMonth() + "/" + paymentMethodModel.getExpYear());
        rltPaymentMethod.setVisibility(View.VISIBLE);
    }

    @OnClick({R.id.lyt_add_credit_card, R.id.btn_new, R.id.btn_pay, R.id.card_view_user})
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
            case R.id.card_view_user:
                Bundle bundleProfile = new Bundle();
                bundleProfile.putInt(Constant.userID, sessionManager.getUserAccount().getId());
                Intent intent = new Intent(PaymentOverviewActivity.this, UserProfileActivity.class);
                intent.putExtras(bundleProfile);
                startActivity(intent);
                break;
        }
    }

    private void showCreditCardRequirementBottomSheet(){
        PosterRequirementsBottomSheet posterRequirementsBottomSheet = PosterRequirementsBottomSheet.newInstance(stateRequirement);
        posterRequirementsBottomSheet.show(getSupportFragmentManager(), "");
    }

    private void payAcceptOffer() {
        showProgressDialog();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.URL_TASKS + "/" + taskModel.getSlug() + "/accept-offer",
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Log.e("response", response);
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
                                                Intent intent = new Intent();
                                                Bundle bundle = new Bundle();
                                                bundle.putBoolean(ConstantKey.PAYMENT_OVERVIEW, true);
                                                intent.putExtras(bundle);
                                                setResult(ConstantKey.RESULTCODE_PAYMENTOVERVIEW, intent);


                                                intent = new Intent(PaymentOverviewActivity.this, CompleteMessageActivity.class);
                                                bundle = new Bundle();
                                                bundle.putString(ConstantKey.COMPLETES_MESSAGE_TITLE, "Offer Accept Successfully");
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
                            }


                        } catch (JSONException e) {
                            Timber.e(String.valueOf(e));
                            e.printStackTrace();
                            hideProgressDialog();
                        }


                    }
                },
                error -> {
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
        Log.e(TAG, stringRequest.getUrl());
    }


//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == ConstantKey.RESULTCODE_COUPON) {
//            if (data != null) {
//                Bundle bundle = data.getExtras();
//                if (bundle != null) {
//                    final_discount_amount = bundle.getFloat(ConstantKey.DISCOUNT_AMOUNT);
//                    discountCoupon = bundle.getString(ConstantKey.DISCOUNT_COUPON);
//
//                    finalCalculation();
//                }
//            }
//        }
//    }

    private void finalCalculation() {
        final_total_cost = final_task_cost + final_service_fee;
        txtTaskCost.setText("$ " + final_task_cost);
        txtServiceFee.setText("$ " + final_service_fee);
        txtTotalCost.setText("$ " + final_total_cost);
    }
}