package com.jobtick.activities;

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
import android.widget.Toast;

import androidx.cardview.widget.CardView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.jobtick.R;
import com.jobtick.interfaces.OnBankAccountAdded;
import com.jobtick.models.BankAccountModel;
import com.jobtick.models.BillingAdreessModel;
import com.jobtick.models.CreditCardModel;
import com.jobtick.utils.Constant;
import com.jobtick.utils.ConstantKey;
import com.jobtick.utils.HttpStatus;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import timber.log.Timber;

import static com.jobtick.utils.Constant.ADD_ACCOUNT_DETAILS;
import static com.jobtick.utils.Constant.ADD_BILLING;
import static com.jobtick.utils.Constant.BASE_URL;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

//TODO: implementing this interface should be changed because page of payment and add bank account is separated.
public class PaymentSettingsActivity extends ActivityBase// implements OnBankAccountAdded {
{

    private CreditCardModel creditCardModel;
    private BankAccountModel bankAccountModel;
    private BillingAdreessModel billingAdreessModel;

    @BindView(R.id.rb_payments)
    RadioButton rbPayments;

    @BindView(R.id.rb_withdrawal)
    RadioButton rbWithdrawal;

    @BindView(R.id.rg_payments_withdrawal)
    RadioGroup rgPaymentsWithdrawal;

    @BindView(R.id.add_credit_card)
    CardView addCreditCard;

    @BindView(R.id.add_bank_account)
    CardView addBankAccount;

    @BindView(R.id.add_billing_address)
    CardView addBillingAddress;

    @BindView(R.id.linear_payment_specs)
    LinearLayout paymentSpecs;

    @BindView(R.id.linear_withdrawal_specs)
    LinearLayout withdrawalSpecs;

    @BindView(R.id.linear_add_credit_card)
    LinearLayout addCreditCardSpecs;

    @BindView(R.id.linear_add_bank_account)
    LinearLayout addBankAccountSpecs;

    @BindView(R.id.linear_add_billing_address)
    LinearLayout addBillingAddressSpecs;

    @BindView(R.id.tv_bsb)
    TextView bsb;

    @BindView(R.id.tv_account_number)
    TextView accountNumber;

    @BindView(R.id.tv_address)
    TextView address;

    @BindView(R.id.tv_state)
    TextView state;

    @BindView(R.id.tv_suburb)
    TextView suburb;

    @BindView(R.id.tv_postcode)
    TextView postCode;

    @BindView(R.id.tv_country)
    TextView country;

    @BindView(R.id.credit_account_name)
    TextView creditAccountName;

    @BindView(R.id.credit_account_number)
    TextView creditAccountNumber;

    @BindView(R.id.ivBack)
    ImageView ivBack;

    public static OnBankAccountAdded onBankaccountadded;

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
        //  toolbar.setNavigationIcon(R.drawable.ic_back);
        // setSupportActionBar(toolbar);
      /*  getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Payment Settings");*/
//        onBankaccountadded = this;
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
    R.id.delete_bank_account, R.id.delete_billing_address, R.id.delete_payment_card,
            R.id.edit_billing_address, R.id.edit_bank_account, R.id.edit_payment_card})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.add_credit_card:
                editPaymentCard();
                break;
            case R.id.add_bank_account:
                editBankAccount();
                break;
            case R.id.add_billing_address:
                editBillingAddress();
                break;
            case R.id.delete_payment_card:
                deleteCreditCard();
                break;
            case R.id.delete_bank_account:
                deleteBankAccountDetails();
                break;
            case R.id.delete_billing_address:
                deleteBillingAddress();
                break;
            case R.id.edit_billing_address:
                editBillingAddress();
                break;
            case R.id.edit_bank_account:
                editBankAccount();
                break;
            case R.id.edit_payment_card:
                editPaymentCard();
                break;
        }
    }

    private void editPaymentCard() {
        Intent add_credit_card = new Intent(PaymentSettingsActivity.this, AddCreditCardActivity.class);
        startActivityForResult(add_credit_card, 111);
    }

    private void editBankAccount() {
        Intent add_bank_account = new Intent(PaymentSettingsActivity.this, AddBankAccountActivity.class);
        startActivityForResult(add_bank_account, 222);
    }

    private void editBillingAddress() {
        Intent add_billing_address = new Intent(PaymentSettingsActivity.this, BillingAddressActivity.class);
        startActivityForResult(add_billing_address, 333);
    }

    private void setupViewBankAccountDetails(boolean success){
        if(success){
            addBankAccount.setVisibility(View.GONE);
            addBankAccountSpecs.setVisibility(View.VISIBLE);
        }
        else{
            addBankAccount.setVisibility(View.VISIBLE);
            addBankAccountSpecs.setVisibility(View.GONE);
        }
    }

    private void setupViewBillingAddress(boolean success){
        if(success){
            addBillingAddress.setVisibility(View.GONE);
            addBillingAddressSpecs.setVisibility(View.VISIBLE);
        }
        else{
            addBillingAddress.setVisibility(View.VISIBLE);
            addBillingAddressSpecs.setVisibility(View.GONE);
        }
    }

    private void setupViewCreditCard(boolean success){
        if(success){
            addCreditCard.setVisibility(View.GONE);
            addCreditCardSpecs.setVisibility(View.VISIBLE);
        }
        else{
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

                                if (bankAccountModel != null) {
                                    if (bankAccountModel.isSuccess()) {

                                        if (bankAccountModel.getData() != null && bankAccountModel.getData().getAccount_number() != null) {

                                            setupViewBankAccountDetails(true);

                                            accountNumber.setText(bankAccountModel.getData().getAccount_number());
                                            bsb.setText(bankAccountModel.getData().getBsb_code());
                                        }
                                    }
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
                                            country.setText(billingAdreessModel.getData().getCountry());
                                            state.setText(billingAdreessModel.getData().getState());
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

                                    if (creditCardModel != null && creditCardModel.getData() != null) {

                                        setupViewCreditCard(true);

                                        creditAccountName.setText(creditCardModel.getData().getCard().getBrand());
                                        creditAccountNumber.setText("xxxx xxxx xxxx " + creditCardModel.getData().getCard().getLast4());
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
        StringRequest stringRequest = new StringRequest(Request.Method.DELETE, Constant.URL_DELETE_BANK_ACCOUNT + "/bankaccount_id=" + bankAccountModel.getData().getId(),
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
        if(resultCode == Activity.RESULT_OK) {
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
