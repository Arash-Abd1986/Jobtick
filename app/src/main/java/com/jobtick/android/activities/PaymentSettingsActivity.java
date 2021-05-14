package com.jobtick.android.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.gson.Gson;
import com.jobtick.android.BuildConfig;
import com.jobtick.android.R;

import android.annotation.SuppressLint;

import com.jobtick.android.models.BankAccountModel;
import com.jobtick.android.models.BillingAdreessModel;
import com.jobtick.android.models.response.getbalance.CreditCardModel;
import com.jobtick.android.utils.CardTypes;
import com.jobtick.android.utils.Constant;
import com.jobtick.android.utils.ConstantKey;
import com.jobtick.android.utils.HttpStatus;
import com.jobtick.android.utils.StateHelper;

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

import static com.jobtick.android.utils.Constant.ADD_ACCOUNT_DETAILS;
import static com.jobtick.android.utils.Constant.ADD_BILLING;
import static com.jobtick.android.utils.Constant.BASE_URL;

public class PaymentSettingsActivity extends ActivityBase {

    private CreditCardModel creditCardModel;
    private BankAccountModel bankAccountModel;
    private BillingAdreessModel billingAdreessModel;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.toolbar)
    MaterialToolbar toolbar;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.rb_payments)
    RadioButton rbPayments;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.rb_withdrawal)
    RadioButton rbWithdrawal;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.rg_payments_withdrawal)
    RadioGroup rgPaymentsWithdrawal;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.add_credit_card)
    CardView addCreditCard;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.add_bank_account)
    CardView addBankAccount;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.add_billing_address)
    CardView addBillingAddress;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.linear_payment_specs)
    LinearLayout paymentSpecs;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.linear_withdrawal_specs)
    LinearLayout withdrawalSpecs;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.linear_add_credit_card)
    LinearLayout addCreditCardSpecs;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.linear_add_bank_account)
    LinearLayout addBankAccountSpecs;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.linear_add_billing_address)
    LinearLayout addBillingAddressSpecs;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.tv_bsb)
    TextView bsb;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.tv_account_number)
    TextView accountNumber;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.tv_address)
    TextView address;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.tv_state)
    TextView state;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.tv_suburb)
    TextView suburb;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.tv_postcode)
    TextView postCode;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.tv_country)
    TextView country;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.credit_expiry_date)
    TextView edtExpiryDate;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.credit_account_number)
    TextView creditAccountNumber;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.card_type)
    TextView cardType;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.card_icon)
    ImageView cardIcon;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_settings);
        ButterKnife.bind(this);
        initToolbar();
        initView();
        radioBtnClick();
    }

    private void initToolbar() {
        toolbar.setTitle("Payment Settings");
        toolbar.setNavigationIcon(R.drawable.ic_back);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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

    private void initView() {
        rbPayments.setChecked(true);
        rbPayments.setTextColor(getResources().getColor(R.color.white));
        rbWithdrawal.setTextColor(getResources().getColor(R.color.black));
        paymentSpecs.setVisibility(View.VISIBLE);
        withdrawalSpecs.setVisibility(View.GONE);


        getBillingAddress();
        getPaymentMethod();
        getBankAccountDetails();

    }

    private void radioBtnClick() {
        rgPaymentsWithdrawal.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton rb_btn = (RadioButton) findViewById(checkedId);
                if (rb_btn.getId() == R.id.rb_payments) {
                    rbPayments.setTextColor(getResources().getColor(R.color.white));
                    rbWithdrawal.setTextColor(getResources().getColor(R.color.black));
                    paymentSpecs.setVisibility(View.VISIBLE);
                    withdrawalSpecs.setVisibility(View.GONE);
                } else {
                    rbPayments.setTextColor(getResources().getColor(R.color.black));
                    rbWithdrawal.setTextColor(getResources().getColor(R.color.white));
                    paymentSpecs.setVisibility(View.GONE);
                    withdrawalSpecs.setVisibility(View.VISIBLE);
                }
            }
        });
    }


    @OnClick({R.id.add_credit_card, R.id.add_billing_address, R.id.add_bank_account,
            R.id.delete_bank_account, R.id.delete_billing_address, R.id.delete_card,
            R.id.edit_billing_address, R.id.edit_bank_account})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.add_credit_card:
                addPaymentCard();
                break;
            case R.id.add_bank_account:
                editBankAccount(false);
                break;
            case R.id.add_billing_address:
                editBillingAddress(false);
                break;
            case R.id.delete_card:
                deleteCreditCard();
                break;
            case R.id.delete_bank_account:
                deleteBankAccountDetails();
                break;
            case R.id.delete_billing_address:
                deleteBillingAddress();
                break;
            case R.id.edit_billing_address:
                editBillingAddress(true);
                break;
            case R.id.edit_bank_account:
                editBankAccount(true);
                break;
        }
    }

    private void addPaymentCard() {

        Intent add_credit_card = new Intent(PaymentSettingsActivity.this, AddCreditCardActivity.class);
        startActivityForResult(add_credit_card, 111);
    }

    private void editBankAccount(boolean editMode) {
        Intent add_bank_account = new Intent(PaymentSettingsActivity.this, AddBankAccountActivity.class);
        Bundle bundle = new Bundle();
        add_bank_account.putExtra(Constant.EDIT_MODE, editMode);

        if (editMode) {
            bundle.putParcelable(BankAccountModel.class.getName(), bankAccountModel);
        }

        add_bank_account.putExtras(bundle);
        startActivityForResult(add_bank_account, 222);

    }

    private void editBillingAddress(boolean editMode) {
        Intent add_billing_address = new Intent(PaymentSettingsActivity.this, BillingAddressActivity.class);
        Bundle bundle = new Bundle();
        add_billing_address.putExtra(Constant.EDIT_MODE, editMode);

        if (editMode) {
            bundle.putParcelable(BillingAdreessModel.class.getName(), billingAdreessModel);
        }

        add_billing_address.putExtras(bundle);
        startActivityForResult(add_billing_address, 333);
    }

    private void setupViewBankAccountDetails(boolean success) {
        if (success) {
            addBankAccount.setVisibility(View.GONE);
            addBankAccountSpecs.setVisibility(View.VISIBLE);
        } else {
            addBankAccount.setVisibility(View.VISIBLE);
            addBankAccountSpecs.setVisibility(View.GONE);
        }
    }

    private void setupViewBillingAddress(boolean success) {
        if (success) {
            addBillingAddress.setVisibility(View.GONE);
            addBillingAddressSpecs.setVisibility(View.VISIBLE);
        } else {
            addBillingAddress.setVisibility(View.VISIBLE);
            addBillingAddressSpecs.setVisibility(View.GONE);
        }
    }

    private void setupViewCreditCard(boolean success) {
        if (success) {
            addCreditCard.setVisibility(View.GONE);
            addCreditCardSpecs.setVisibility(View.VISIBLE);
        } else {
            addCreditCard.setVisibility(View.VISIBLE);
            addCreditCardSpecs.setVisibility(View.GONE);
        }
    }


    public void getBankAccountDetails() {
        showProgressDialog();
        StringRequest stringRequest = new StringRequest(StringRequest.Method.GET, BASE_URL + ADD_ACCOUNT_DETAILS,
                response -> {
                    Timber.e(response);
                    hideProgressDialog();
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        Timber.e(jsonObject.toString());
                        if (jsonObject.has("success") && !jsonObject.isNull("success")) {
                            if (jsonObject.getBoolean("success")) {
                                String jsonString = jsonObject.toString(); //http request
                                Gson gson = new Gson();
                                bankAccountModel = gson.fromJson(jsonString, BankAccountModel.class);


                                if (bankAccountModel != null && bankAccountModel.isSuccess() && bankAccountModel.getData() != null && bankAccountModel.getData().getAccount_number() != null) {

                                    setupViewBankAccountDetails(true);

                                    accountNumber.setText(String.format("xxxxx%s", bankAccountModel.getData().getAccount_number()));
                                    bsb.setText(bankAccountModel.getData().getBsb_code());
                                } else {
                                    setupViewBankAccountDetails(false);
                                }
                            } else {
                                showToast("Something went Wrong", PaymentSettingsActivity.this);
                                setupViewBankAccountDetails(false);
                            }
                        }
                    } catch (JSONException e) {
                        Timber.e(String.valueOf(e));
                        e.printStackTrace();
                        showToast(e.getMessage(), PaymentSettingsActivity.this);
                        setupViewBankAccountDetails(false);
                    }


                },
                error -> {

                    setupViewBankAccountDetails(false);

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
                            hideProgressDialog();
                            JSONObject jsonObject = new JSONObject(jsonError);
                            JSONObject jsonObject_error = jsonObject.getJSONObject("error");
                            if (jsonObject_error.has("message")) {
                                showToast(jsonObject_error.getString("message"), this);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        showToast("Something Went Wrong", PaymentSettingsActivity.this);
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
                map1.put("Version", String.valueOf(BuildConfig.VERSION_CODE));
                return map1;
            }

        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, -1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(PaymentSettingsActivity.this);
        requestQueue.add(stringRequest);
    }

    public void getBillingAddress() {

        showProgressDialog();

        StringRequest stringRequest = new StringRequest(StringRequest.Method.GET, BASE_URL + ADD_BILLING,
                response -> {
                    Timber.e(response);
                    hideProgressDialog();
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        Timber.e(jsonObject.toString());
                        if (jsonObject.has("success") && !jsonObject.isNull("success")) {
                            if (jsonObject.getBoolean("success")) {
                                String jsonString = jsonObject.toString(); //http request
                                Gson gson = new Gson();
                                billingAdreessModel = gson.fromJson(jsonString, BillingAdreessModel.class);

                                if (billingAdreessModel != null) {
                                    if (billingAdreessModel.isSuccess()) {

                                        if (billingAdreessModel.getData() != null && billingAdreessModel.getData().getLine1() != null) {

                                            setupViewBillingAddress(true);

                                            address.setText(billingAdreessModel.getData().getLine1());
                                            suburb.setText(billingAdreessModel.getData().getCity());
                                            postCode.setText(billingAdreessModel.getData().getPost_code());
                                            country.setText(R.string.australia);
                                            StateHelper helper = new StateHelper(getApplicationContext());
                                            state.setText(helper.getStateName(billingAdreessModel.getData().getState()));
                                        }
                                    }
                                }
                            } else {
                                setupViewBillingAddress(false);
                                showToast("Something went Wrong", PaymentSettingsActivity.this);
                            }
                        }
                    } catch (JSONException e) {
                        Timber.e(String.valueOf(e));
                        e.printStackTrace();
                        setupViewBillingAddress(false);
                    }


                },
                error -> {
                    setupViewBillingAddress(false);
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
                            hideProgressDialog();
                            JSONObject jsonObject = new JSONObject(jsonError);
                            JSONObject jsonObject_error = jsonObject.getJSONObject("error");
                            if (jsonObject_error.has("message")) {
                                showToast(jsonObject_error.getString("message"), this);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        showToast("Something Went Wrong", PaymentSettingsActivity.this);
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
                map1.put("Version", String.valueOf(BuildConfig.VERSION_CODE));
                return map1;
            }

        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, -1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(PaymentSettingsActivity.this);
        requestQueue.add(stringRequest);

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
                                    creditCardModel = gson.fromJson(jsonString, CreditCardModel.class);

                                    if (creditCardModel != null && creditCardModel.getData() != null && creditCardModel.getData().get(0).getCard() != null) {

                                        setupViewCreditCard(true);
                                        String brand = creditCardModel.getData().get(0).getCard().getBrand();
                                        cardType.setText(brand);
                                        if (brand.equalsIgnoreCase(CardTypes.MASTER.getType()))
                                            cardIcon.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_card_master));
                                        if (brand.equalsIgnoreCase(CardTypes.VISA.getType()))
                                            cardIcon.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_card_visa));
                                        if (brand.contains(CardTypes.AMERICAN.getType()))
                                            cardIcon.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_card_american_express));

                                        edtExpiryDate.setText(String.format(Locale.ENGLISH, "Expiry Date: %s/%d",
                                                (creditCardModel.getData().get(0).getCard().getExp_month() < 10) ? "0" + creditCardModel.getData().get(0).getCard().getExp_month() : creditCardModel.getData().get(0).getCard().getExp_month(),
                                                creditCardModel.getData().get(0).getCard().getExp_year()));
                                        creditAccountNumber.setText(String.format("xxxx xxxx xxxx %s", creditCardModel.getData().get(0).getCard().getLast4()));

                                    } else {
                                        setupViewCreditCard(false);
                                    }
                                    hideProgressDialog();
                                }
                            } else {
                                setupViewCreditCard(false);
                                showToast("Something went Wrong", PaymentSettingsActivity.this);
                            }
                        }
                    } catch (JSONException e) {
                        Timber.e(String.valueOf(e));
                        e.printStackTrace();
                        setupViewCreditCard(false);
                    }
                },
                error -> {
                    setupViewCreditCard(false);
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
                                    hideProgressDialog();
                                    return;
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
                map1.put("Version", String.valueOf(BuildConfig.VERSION_CODE));
                // map1.put("X-Requested-With", "XMLHttpRequest");
                return map1;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, -1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(PaymentSettingsActivity.this);
        requestQueue.add(stringRequest);
    }

    private void deleteBankAccountDetails() {
        showProgressDialog();
        StringRequest stringRequest = new StringRequest(Request.Method.DELETE, Constant.URL_DELETE_BANK_ACCOUNT + "/" + bankAccountModel.getData().getId(),
                response -> {
                    Timber.e(response);
                    hideProgressDialog();
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        Timber.e(jsonObject.toString());
                        if (jsonObject.has("success") && !jsonObject.isNull("success")) {
                            if (jsonObject.getBoolean("success")) {
                                setupViewBankAccountDetails(false);

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
                    hideProgressDialog();
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> map1 = new HashMap<String, String>();
                map1.put("authorization", sessionManager.getTokenType() + " " + sessionManager.getAccessToken());
                map1.put("Content-Type", "application/x-www-form-urlencoded");
                map1.put("Version", String.valueOf(BuildConfig.VERSION_CODE));
                // map1.put("X-Requested-With", "XMLHttpRequest");
                return map1;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, -1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(PaymentSettingsActivity.this);
        requestQueue.add(stringRequest);

    }

    private void deleteBillingAddress() {
        showProgressDialog();
        StringRequest stringRequest = new StringRequest(Request.Method.DELETE, Constant.URL_DELETE_BILLING_ADDRESS,
                response -> {
                    Timber.e(response);
                    hideProgressDialog();
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        Timber.e(jsonObject.toString());
                        if (jsonObject.has("success") && !jsonObject.isNull("success")) {
                            if (jsonObject.getBoolean("success")) {
                                setupViewBillingAddress(false);
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
                    hideProgressDialog();
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> map1 = new HashMap<String, String>();
                map1.put("authorization", sessionManager.getTokenType() + " " + sessionManager.getAccessToken());
                map1.put("Content-Type", "application/x-www-form-urlencoded");
                map1.put("Version", String.valueOf(BuildConfig.VERSION_CODE));
                // map1.put("X-Requested-With", "XMLHttpRequest");
                return map1;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, -1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(PaymentSettingsActivity.this);
        requestQueue.add(stringRequest);

    }

    private void deleteCreditCard() {
        showProgressDialog();
        StringRequest stringRequest = new StringRequest(Request.Method.DELETE, Constant.URL_PAYMENTS_METHOD,
                response -> {
                    Timber.e(response);
                    hideProgressDialog();
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        Timber.e(jsonObject.toString());
                        if (jsonObject.has("success") && !jsonObject.isNull("success")) {
                            if (jsonObject.getBoolean("success")) {
                                setupViewCreditCard(false);
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
                    hideProgressDialog();
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> map1 = new HashMap<String, String>();
                map1.put("authorization", sessionManager.getTokenType() + " " + sessionManager.getAccessToken());
                map1.put("Content-Type", "application/x-www-form-urlencoded");
                map1.put("Version", String.valueOf(BuildConfig.VERSION_CODE));
                // map1.put("X-Requested-With", "XMLHttpRequest");
                return map1;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, -1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(PaymentSettingsActivity.this);
        requestQueue.add(stringRequest);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 111) {
                getPaymentMethod();
            } else if (requestCode == 222) {
                getBankAccountDetails();
            } else if (requestCode == 333) {
                getBillingAddress();
            }
        }
    }
}
