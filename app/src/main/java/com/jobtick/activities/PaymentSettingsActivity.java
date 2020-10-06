package com.jobtick.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.gson.Gson;
import com.jobtick.R;
import com.jobtick.TextView.TextViewBold;
import com.jobtick.TextView.TextViewRegular;
import com.jobtick.TextView.TextViewSemiBold;
import com.jobtick.incrementbudget.IncreaseBudgetFromPosterActivity;
import com.jobtick.interfaces.OnBankAccountAdded;
import com.jobtick.models.BankAccountModel;
import com.jobtick.models.BillingAdreessModel;
import com.jobtick.models.CreditCardModel;
import com.jobtick.models.payments.PaymentMethodModel;
import com.jobtick.utils.Constant;
import com.jobtick.utils.ConstantKey;
import com.jobtick.utils.HttpStatus;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import kotlin.BuilderInference;
import timber.log.Timber;

import static com.jobtick.utils.Constant.ADD_ACCOUNT_DETAILS;
import static com.jobtick.utils.Constant.ADD_BILLING;
import static com.jobtick.utils.Constant.BASE_URL;

public class PaymentSettingsActivity extends ActivityBase implements OnBankAccountAdded {

 /*   @BindView(R.id.toolbar)
    MaterialToolbar toolbar;*/

    @BindView(R.id.rb_payments)
    RadioButton rbPayments;

    @BindView(R.id.rb_withdrawal)
    RadioButton rbWithdrawal;

    @BindView(R.id.rg_payments_withdrawal)
    RadioGroup rgPaymentsWithdrawal;

    @BindView(R.id.img_add)
    ImageView imgAdd;

    @BindView(R.id.txt_add_credit_card)
    TextViewSemiBold txtAddCreditCard;

    @BindView(R.id.rlt_btn_add_credit_card)
    RelativeLayout rltBtnAddCreditCard;


    @BindView(R.id.linear_bank_details)
    LinearLayout linear_bank_details;

    @BindView(R.id.linear_credit_card_view)
    LinearLayout linear_credit_card_view;

    @BindView(R.id.linear_bank)
    LinearLayout linear_bank;


    @BindView(R.id.card_add_billing_address)
    CardView card_add_billing_address;

    @BindView(R.id.card_add_bank_account)
    CardView card_add_bank_account;


    @BindView(R.id.card_view_bank_account)
    CardView card_view_bank_account;


    @BindView(R.id.txt_account_number)
    TextViewSemiBold txtAccountNumber;

    @BindView(R.id.card_view_billing_address)
    CardView card_view_billing_address;

    @BindView(R.id.txt_Billing_Address)
    TextViewSemiBold txtBillingAddress;


    @BindView(R.id.tvCardHolderName)
    TextViewRegular tvCardHolderName;

    @BindView(R.id.txtCardNumber)
    TextViewRegular txtCardNumber;

    @BindView(R.id.txtPrice)
    TextViewBold txtPrice;

    @BindView(R.id.rtl_credit_card_details)
    LinearLayout rtl_credit_card_details;

    @BindView(R.id.card_delete_credit_card)
    CardView card_delete_credit_card;

    @BindView(R.id.liner_no_payment_method)
    LinearLayout liner_no_payment_method;

    @BindView(R.id.ic_delete_bank_account)
    ImageView ic_delete_bank_account;

    @BindView(R.id.ic_delete_billing_account)
    ImageView ic_delete_billing_account;

    @BindView(R.id.ivBack)
    ImageView ivBack;

    public static OnBankAccountAdded onBankaccountadded;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_settings);
        ButterKnife.bind(this);
        initToolbar();
        initalView();
        radioBtnClick();
    }


    private void initToolbar() {
        //  toolbar.setNavigationIcon(R.drawable.ic_back);
        // setSupportActionBar(toolbar);
      /*  getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Payment Settings");*/
        onBankaccountadded = this;
        ivBack.setOnClickListener(v -> finish());
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

    private void initalView() {
        rbPayments.setChecked(true);
        rbPayments.setTextColor(getResources().getColor(R.color.white));
        rbWithdrawal.setTextColor(getResources().getColor(R.color.black));
        linear_credit_card_view.setVisibility(View.VISIBLE);
        linear_bank_details.setVisibility(View.GONE);
        linear_bank.setVisibility(View.GONE);
        liner_no_payment_method.setVisibility(View.VISIBLE);
        rtl_credit_card_details.setVisibility(View.GONE);
        getBillingAddress();
        getPaymentMethod();
        getBankAccountAddress();

    }

    private void radioBtnClick() {
        rgPaymentsWithdrawal.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton rb_btn = (RadioButton) findViewById(checkedId);
                if (rb_btn.getText().equals("Payments")) {
                    rbPayments.setTextColor(getResources().getColor(R.color.white));
                    rbWithdrawal.setTextColor(getResources().getColor(R.color.black));
                    linear_credit_card_view.setVisibility(View.VISIBLE);
                    linear_bank_details.setVisibility(View.GONE);
                } else {
                    rbPayments.setTextColor(getResources().getColor(R.color.black));
                    rbWithdrawal.setTextColor(getResources().getColor(R.color.white));
                    linear_credit_card_view.setVisibility(View.GONE);
                    linear_bank_details.setVisibility(View.VISIBLE);
                }
            }
        });
    }


    public void getBankAccountAddress() {


        showpDialog();


        StringRequest stringRequest = new StringRequest(StringRequest.Method.GET, BASE_URL + ADD_ACCOUNT_DETAILS,
                response -> {
                    Timber.e(response);
                    hidepDialog();
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        Timber.e(jsonObject.toString());
                        if (jsonObject.has("success") && !jsonObject.isNull("success")) {
                            if (jsonObject.getBoolean("success")) {
                                String jsonString = jsonObject.toString(); //http request
                                BankAccountModel data = new BankAccountModel();
                                Gson gson = new Gson();
                                data = gson.fromJson(jsonString, BankAccountModel.class);

                                if (data != null) {
                                    if (data.isSuccess()) {

                                        if (data.getData() != null && data.getData().getAccount_number() != null) {

                                            linear_bank.setVisibility(View.GONE);
                                            card_add_bank_account.setVisibility(View.GONE);
                                            card_view_bank_account.setVisibility(View.VISIBLE);
                                            txtAccountNumber.setText(data.getData().getAccount_number());

                                        }

                                    }
                                }
                            } else {
                                showToast("Something went Wrong", PaymentSettingsActivity.this);
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
                            //  showCustomDialog(jsonObject_error.getString("message"));
                            if (jsonObject_error.has("message")) {
                                Toast.makeText(PaymentSettingsActivity.this, jsonObject_error.getString("message"), Toast.LENGTH_SHORT).show();
                            }
                            if (jsonObject_error.has("errors")) {
                                JSONObject jsonObject_errors = jsonObject_error.getJSONObject("errors");
                            }
                            //  ((CredentialActivity)getActivity()).showToast(message,getActivity());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        showToast("Something Went Wrong", PaymentSettingsActivity.this);
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

        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, -1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(PaymentSettingsActivity.this);
        requestQueue.add(stringRequest);


    }


    public void getBillingAddress() {

        showpDialog();

        StringRequest stringRequest = new StringRequest(StringRequest.Method.GET, BASE_URL + ADD_BILLING,
                response -> {
                    Timber.e(response);
                    hidepDialog();
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        Timber.e(jsonObject.toString());
                        if (jsonObject.has("success") && !jsonObject.isNull("success")) {
                            if (jsonObject.getBoolean("success")) {
                                String jsonString = jsonObject.toString(); //http request
                                BillingAdreessModel data = new BillingAdreessModel();
                                Gson gson = new Gson();
                                data = gson.fromJson(jsonString, BillingAdreessModel.class);

                                if (data != null) {
                                    if (data.isSuccess()) {

                                        if (data.getData() != null && data.getData().getLine1() != null) {
                                            txtBillingAddress.setText(data.getData().getLine1());
                                            card_add_billing_address.setVisibility(View.GONE);
                                            linear_bank.setVisibility(View.GONE);
                                            card_view_billing_address.setVisibility(View.VISIBLE);
                                        }
                                    }
                                }
                            } else {
                                showToast("Something went Wrong", PaymentSettingsActivity.this);
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
                            //  showCustomDialog(jsonObject_error.getString("message"));
                            if (jsonObject_error.has("message")) {
                                Toast.makeText(PaymentSettingsActivity.this, jsonObject_error.getString("message"), Toast.LENGTH_SHORT).show();
                            }
                            if (jsonObject_error.has("errors")) {
                                JSONObject jsonObject_errors = jsonObject_error.getJSONObject("errors");
                            }
                            //  ((CredentialActivity)getActivity()).showToast(message,getActivity());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        showToast("Something Went Wrong", PaymentSettingsActivity.this);
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

        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, -1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(PaymentSettingsActivity.this);
        requestQueue.add(stringRequest);

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
                                  /*  JSONObject jsonObject_data = jsonObject.getJSONObject("data");
                                    if (jsonObject_data.has("card") && !jsonObject_data.isNull("card")) {
                                        JSONObject jsonObject_card = jsonObject_data.getJSONObject("card");
                                        PaymentMethodModel paymentMethodModel = new PaymentMethodModel().getJsonToModel(jsonObject_card);
                                       // setUpLayout(paymentMethodModel);

                                        txtPrice.setText(paymentMethodModel.ba);

                                    } else {
                                        showToast("card not Available", PaymentSettingsActivity.this);
                                    }*/


                                    String jsonString = jsonObject.toString(); //http request
                                    CreditCardModel data = new CreditCardModel();
                                    Gson gson = new Gson();
                                    data = gson.fromJson(jsonString, CreditCardModel.class);

                                    if (data != null && data.getData() != null) {
                                        liner_no_payment_method.setVisibility(View.GONE);
                                        rtl_credit_card_details.setVisibility(View.VISIBLE);
                                        txtPrice.setText("$ " + data.getData().getBalance());
                                        txtCardNumber.setText("xxxx xxxx xxxx " + data.getData().getCard().getLast4());
                                        tvCardHolderName.setText("Expire :" + data.getData().getCard().getExp_month()
                                                + "/" + data.getData().getCard().getExp_year());
                                    } else {
                                        liner_no_payment_method.setVisibility(View.VISIBLE);
                                        rtl_credit_card_details.setVisibility(View.GONE);
                                    }

                                }
                            } else {
                                showToast("Something went Wrong", PaymentSettingsActivity.this);
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
                                    //  setUpAddPaymentLayout();
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
        RequestQueue requestQueue = Volley.newRequestQueue(PaymentSettingsActivity.this);
        requestQueue.add(stringRequest);
    }


    @OnClick({R.id.rlt_btn_add_credit_card, R.id.card_add_bank_account, R.id.card_add_billing_address, R.id.card_delete_credit_card, R.id.ic_delete_bank_account, R.id.ic_delete_billing_account})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rlt_btn_add_credit_card:
                Intent add_credit_card = new Intent(PaymentSettingsActivity.this, AddCreditCardActivity.class);
                startActivity(add_credit_card);
                break;
            case R.id.card_add_bank_account:
                Intent add_bank_account = new Intent(PaymentSettingsActivity.this, AddBankAccountActivity.class);
                startActivity(add_bank_account);
                break;
            case R.id.card_add_billing_address:
                Intent add_billing_address = new Intent(PaymentSettingsActivity.this, BillingAddressActivity.class);
                startActivity(add_billing_address);
                break;
            case R.id.card_delete_credit_card:
                DeleteCard();
                break;
            case R.id.ic_delete_bank_account:
                DeleteBankAccountDetails();
                break;
            case R.id.ic_delete_billing_account:
                DeleteBillingAddress();
                break;
        }
    }

    @Override
    public void bankAccountAdd() {
        getBankAccountAddress();
    }

    @Override
    public void billingAddressAdd() {
        getBillingAddress();
    }

    @Override
    public void creditCard() {
        getPaymentMethod();

    }


    private void DeleteBankAccountDetails() {
        showpDialog();
        StringRequest stringRequest = new StringRequest(Request.Method.DELETE, Constant.URL_DELETE_BANK_ACCOUNT,
                response -> {
                    Timber.e(response);
                    hidepDialog();
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        Timber.e(jsonObject.toString());
                        if (jsonObject.has("success") && !jsonObject.isNull("success")) {
                            if (jsonObject.getBoolean("success")) {
                                linear_bank.setVisibility(View.GONE);
                                card_add_bank_account.setVisibility(View.VISIBLE);
                                card_view_bank_account.setVisibility(View.GONE);

                            } else {
                                showToast("Something went Wrong", PaymentSettingsActivity.this);
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
                                    //  setUpAddPaymentLayout();
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
        RequestQueue requestQueue = Volley.newRequestQueue(PaymentSettingsActivity.this);
        requestQueue.add(stringRequest);

    }

    private void DeleteBillingAddress() {
        showpDialog();
        StringRequest stringRequest = new StringRequest(Request.Method.DELETE, Constant.URL_DELETE_BILLING_ADDRESS,
                response -> {
                    Timber.e(response);
                    hidepDialog();
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        Timber.e(jsonObject.toString());
                        if (jsonObject.has("success") && !jsonObject.isNull("success")) {
                            if (jsonObject.getBoolean("success")) {
                                card_add_billing_address.setVisibility(View.VISIBLE);
                                linear_bank.setVisibility(View.GONE);
                                card_view_billing_address.setVisibility(View.GONE);
                            } else {
                                showToast("Something went Wrong", PaymentSettingsActivity.this);
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
                                    //  setUpAddPaymentLayout();
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
        RequestQueue requestQueue = Volley.newRequestQueue(PaymentSettingsActivity.this);
        requestQueue.add(stringRequest);

    }


    private void DeleteCard() {
        showpDialog();
        StringRequest stringRequest = new StringRequest(Request.Method.DELETE, Constant.URL_PAYMENTS_METHOD,
                response -> {
                    Timber.e(response);
                    hidepDialog();
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        Timber.e(jsonObject.toString());
                        if (jsonObject.has("success") && !jsonObject.isNull("success")) {
                            if (jsonObject.getBoolean("success")) {
                                liner_no_payment_method.setVisibility(View.VISIBLE);
                                rtl_credit_card_details.setVisibility(View.GONE);
                            } else {
                                showToast("Something went Wrong", PaymentSettingsActivity.this);
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
                                    //  setUpAddPaymentLayout();
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
        RequestQueue requestQueue = Volley.newRequestQueue(PaymentSettingsActivity.this);
        requestQueue.add(stringRequest);
    }


}
