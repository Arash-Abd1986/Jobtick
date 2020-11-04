package com.jobtick.activities;

import android.content.DialogInterface;
import android.content.Intent;
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
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.jobtick.R;
import com.jobtick.TextView.TextViewBold;
import com.jobtick.TextView.TextViewMedium;
import com.jobtick.TextView.TextViewRegular;
import com.jobtick.models.OfferModel;
import com.jobtick.models.TaskModel;
import com.jobtick.models.UserAccountModel;
import com.jobtick.models.payments.PaymentMethodModel;
import com.jobtick.utils.Constant;
import com.jobtick.utils.ConstantKey;
import com.jobtick.utils.ImageUtil;
import com.mikhaellopez.circularimageview.CircularImageView;
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

public class PaymentOverviewActivity extends ActivityBase {

    private static final String TAG = PaymentOverviewActivity.class.getSimpleName();
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
    @BindView(R.id.txt_task_cost)
    TextViewBold txtTaskCost;
    @BindView(R.id.txt_service_fee)
    TextViewBold txtServiceFee;
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
    @BindView(R.id.discount_text)
    TextViewMedium discountText;
    @BindView(R.id.txt_btn_remove_coupon)
    TextViewMedium txtBtnRemoveCoupon;
    @BindView(R.id.txt_discount_amount)
    TextViewBold txtDiscountAmount;
    @BindView(R.id.lyt_discount_coupon)
    LinearLayout lytDiscountCoupon;
    @BindView(R.id.lyt_add_coupon)
    LinearLayout lytAddCoupon;

    @BindView(R.id.txt_coupon_code)
    TextViewMedium txtCouponCode;

    @BindView(R.id.lyt_remove_coupon)
    LinearLayout lytRemoveCoupon;

    private Card card_xml;
    private TaskModel taskModel;
    private OfferModel offerModel;
    private UserAccountModel userAccountModel;

    float final_task_cost;
    float final_service_fee;
    float final_discount_amount = 0;
    float final_total_cost;
    String discountCoupon;

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
    }

    private float getServiceFee() {
        return (final_task_cost * taskModel.getPoster().getPosterTier().getServiceFee()) / 100;
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
        RequestQueue requestQueue = Volley.newRequestQueue(PaymentOverviewActivity.this);
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

    @OnClick({R.id.lyt_remove_coupon, R.id.lyt_add_coupon, R.id.txt_btn_update, R.id.lyt_btn_add_payment_method, R.id.lyt_btn_pay, R.id.card_view_user})
    public void onViewClicked(View view) {
        switch (view.getId()) {
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
                payAcceptOffer();
                break;
            case R.id.txt_btn_update:
                setUpAddPaymentLayout();
                break;
            case R.id.lyt_add_coupon:
                Intent intent = new Intent(PaymentOverviewActivity.this, AddCouponPaymentOverviewActivity.class);
                Bundle bundle = new Bundle();
                bundle.putFloat(ConstantKey.AMOUNT, final_task_cost);
                intent.putExtras(bundle);
                startActivityForResult(intent, ConstantKey.RESULTCODE_COUPON);
                break;
            case R.id.lyt_remove_coupon:
                new MaterialAlertDialogBuilder(this)
                        .setTitle("Remove")
                        .setMessage("Remove this Coupon")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                discountCoupon = null;
                                final_discount_amount = 0;
                                finalCalculation();
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();
                break;
            case R.id.card_view_user:
                Bundle bundleProfile = new Bundle();
                bundleProfile.putInt(Constant.userID, sessionManager.getUserAccount().getId());
                intent = new Intent(PaymentOverviewActivity.this, UserProfileActivity.class);
                intent.putExtras(bundleProfile);
                startActivity(intent);
                break;
        }
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
                if (discountCoupon != null) {
                    map1.put("coupon", discountCoupon);
                }
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
        RequestQueue requestQueue = Volley.newRequestQueue(PaymentOverviewActivity.this);
        requestQueue.add(stringRequest);
        Log.e(TAG, stringRequest.getUrl());
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ConstantKey.RESULTCODE_COUPON) {
            if (data != null) {
                Bundle bundle = data.getExtras();
                if (bundle != null) {
                    final_discount_amount = bundle.getFloat(ConstantKey.DISCOUNT_AMOUNT);
                    discountCoupon = bundle.getString(ConstantKey.DISCOUNT_COUPON);

                    finalCalculation();
                }
            }
        }
    }

    private void finalCalculation() {
        final_total_cost = (final_task_cost + final_service_fee) - final_discount_amount;
        txtTaskCost.setText("$ " + final_task_cost);
        txtServiceFee.setText("$ " + final_service_fee);
        if (discountCoupon != null) {
            lytDiscountCoupon.setVisibility(View.VISIBLE);
            txtDiscountAmount.setText("-$ " + final_discount_amount);
            lytAddCoupon.setVisibility(View.GONE);
            txtCouponCode.setText(discountCoupon);
            lytRemoveCoupon.setVisibility(View.VISIBLE);

        } else {
            lytDiscountCoupon.setVisibility(View.GONE);
            lytAddCoupon.setVisibility(View.VISIBLE);
            lytRemoveCoupon.setVisibility(View.GONE);
        }
        txtTotalCost.setText("$ " + final_total_cost);
    }
}