package com.jobtick.activities;

import android.net.IpSecManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.button.MaterialButton;
import com.jobtick.AppExecutors;
import com.jobtick.EditText.EditTextRegular;
import com.jobtick.R;
import com.jobtick.utils.Constant;
import com.jobtick.utils.ConstantKey;
import com.jobtick.utils.HttpStatus;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import com.stripe.model.Token;
import com.stripe.net.RequestOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import timber.log.Timber;

import static com.jobtick.activities.PaymentSettingsActivity.onBankaccountadded;
import static com.jobtick.utils.Constant.ADD_ACCOUNT_DETAILS;
import static com.jobtick.utils.Constant.BASE_URL;

public class AddBankAccountActivity extends ActivityBase {

/*    @BindView(R.id.toolbar)
    MaterialToolbar toolbar;*/

    @BindView(R.id.edt_account_name)
    EditTextRegular edtAccountName;

    @BindView(R.id.edt_bsb)
    EditTextRegular edtBsb;

    @BindView(R.id.edt_account_number)
    EditTextRegular edtAccountNumber;

    @BindView(R.id.lyt_btn_add_bank_account)
    MaterialButton lytBtnAddBankAccount;

    @BindView(R.id.ivBack)
    ImageView ivBack;

    private String btoken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_bank_account);
        ButterKnife.bind(this);
        initToolbar();
    }

    private void initToolbar() {
        ivBack.setOnClickListener(v -> {
            finish();
        });
       /* toolbar.setNavigationIcon(R.drawable.ic_back);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Add Bank Account");*/
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

    @OnClick(R.id.lyt_btn_add_bank_account)
    public void onViewClicked() {

        if (validate()) {
            addBankAccount();
            //addBankAccountDetails();
        }
    }

    private void addBankAccount() {
        showProgressDialog();
        AppExecutors.getInstance().getNetworkIO().execute(new Runnable() {
            @Override
            public void run() {

                Stripe.apiKey = ConstantKey.PUBLISHABLE_KEY;

                Map<String, Object> bankAccount = new HashMap<>();
                bankAccount.put("country", "AU");
                bankAccount.put("currency", "aud");
                bankAccount.put(
                        "account_holder_name",
                        edtAccountName.getText().toString()
                );
                bankAccount.put(
                        "account_holder_type",
                        "individual"
                );
                bankAccount.put("routing_number", edtBsb.getText().toString());
                bankAccount.put("account_number", edtAccountNumber.getText().toString());
                Map<String, Object> params = new HashMap<>();
                params.put("bank_account", bankAccount);

                try {
                    Token token = Token.create(params);
                    System.out.println("Token success: id:" + token.getId());

                    btoken = token.getId();
                    addBankAccountDetails();

                } catch (StripeException e){
                    e.printStackTrace();

                    AppExecutors.getInstance().getMainThread().execute(new Runnable() {
                        @Override
                        public void run() {
                            showToast("Your bank account information is not valid.", AddBankAccountActivity.this);
                        }
                    });
                }

                AppExecutors.getInstance().getMainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        hideProgressDialog();
                    }
                });
            }
        });


    }

    public void addBankAccountDetails() {

        StringRequest stringRequest = new StringRequest(StringRequest.Method.POST, BASE_URL + ADD_ACCOUNT_DETAILS,
                response -> {
                    Timber.e(response);
                    hideProgressDialog();
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        Timber.e(jsonObject.toString());
                        if (jsonObject.has("success") && !jsonObject.isNull("success")) {
                            if (jsonObject.getBoolean("success")) {

                                showToast("Updated successfully ! ", AddBankAccountActivity.this);

                                if (onBankaccountadded != null) {
                                    onBankaccountadded.bankAccountAdd();
                                }
                                finish();
                            } else {
                                showToast("Something went Wrong", AddBankAccountActivity.this);
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
                            hideProgressDialog();
                            return;
                        }
                        try {
                            hideProgressDialog();
                            JSONObject jsonObject = new JSONObject(jsonError);
                            JSONObject jsonObject_error = jsonObject.getJSONObject("error");
                            //  showCustomDialog(jsonObject_error.getString("message"));
                            if (jsonObject_error.has("message")) {
                                Toast.makeText(AddBankAccountActivity.this, jsonObject_error.getString("message"), Toast.LENGTH_SHORT).show();
                            }
                            if (jsonObject_error.has("errors")) {
                                JSONObject jsonObject_errors = jsonObject_error.getJSONObject("errors");
                            }
                            //  ((CredentialActivity)getActivity()).showToast(message,getActivity());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        showToast("Something Went Wrong", AddBankAccountActivity.this);
                    }
                    Timber.e(error.toString());
                }) {


            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> map1 = new HashMap<String, String>();
                map1.put("btoken", btoken);

                return map1;
            }

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
        RequestQueue requestQueue = Volley.newRequestQueue(AddBankAccountActivity.this);
        requestQueue.add(stringRequest);

    }


    public boolean validate() {
        if (TextUtils.isEmpty(edtAccountName.getText().toString())) {
            edtAccountName.setError("Please Enter Account Name");
            return false;
        }

        if (TextUtils.isEmpty(edtBsb.getText().toString())) {
            edtBsb.setError("Please enter BSB");
            return false;
        }

        if (TextUtils.isEmpty(edtAccountNumber.getText().toString())) {
            edtAccountNumber.setError("Please account Number");
            return false;
        }
        return true;

    }

}
