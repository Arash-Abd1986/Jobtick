package com.jobtick.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.jobtick.AppExecutors;
import com.jobtick.EditText.EditTextMedium;
import com.jobtick.EditText.EditTextRegular;
import com.jobtick.R;
import com.jobtick.utils.HttpStatus;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Token;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import timber.log.Timber;

import static com.jobtick.activities.PaymentSettingsActivity.onBankaccountadded;
import static com.jobtick.utils.Constant.ADD_BILLING;
import static com.jobtick.utils.Constant.BASE_URL;

public class BillingAddressActivity extends ActivityBase {

/*    @BindView(R.id.toolbar)
    MaterialToolbar toolbar;*/

    @BindView(R.id.edt_address_line_1)
    EditText edtAddressLine1;

    @BindView(R.id.edt_address_line_2)
    EditText edtAddressLine2;

    @BindView(R.id.edt_suburs)
    EditText edtSuburs;

    @BindView(R.id.edt_state)
    EditText edtState;

    @BindView(R.id.edt_postcode)
    EditText edtPostcode;

    @BindView(R.id.edt_Country)
    EditText edtCountry;

    @BindView(R.id.lyt_btn_change_billing_address)
    MaterialButton lytBtnChangeBillingAddress;

    @BindView(R.id.ivBack)
    ImageView ivBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_billing_address);
        ButterKnife.bind(this);
        initToolbar();
    }


    private void initToolbar() {

        ivBack.setOnClickListener(v -> {
            finish();
        });
      /*  toolbar.setNavigationIcon(R.drawable.ic_back);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Billing Address");*/
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

    @OnClick(R.id.lyt_btn_change_billing_address)
    public void onViewClicked() {

        if (validation()) {
            AddBillingAddress();
        }

    }

    private boolean validation() {
        if (TextUtils.isEmpty(edtAddressLine1.getText().toString().trim())) {
            edtAddressLine1.setError("Address is mandatory");
            return false;
        }

        if (TextUtils.isEmpty(edtAddressLine2.getText().toString().trim())) {
            edtAddressLine2.setError("Address is mandatory");
            return false;
        }


        if (TextUtils.isEmpty(edtSuburs.getText().toString().trim())) {
            edtSuburs.setError("Please enter Suburb");
            return false;
        }
        if (TextUtils.isEmpty(edtState.getText().toString().trim())) {
            edtState.setError("Please enter state");
            return false;
        }
        if (TextUtils.isEmpty(edtPostcode.getText().toString().trim())) {
            edtPostcode.setError("Please enter Passcode");
            return false;
        }
        if (edtPostcode.getText().toString().length() != 4) {
            edtPostcode.setError("Please enter 4 digit Passcode");
            return false;
        }
        if (TextUtils.isEmpty(edtCountry.getText().toString().trim())) {
            edtCountry.setError("Please Enter Country");
            return false;
        }
        return true;
    }

    private void AddBillingAddress() {
        showProgressDialog();

        StringRequest stringRequest = new StringRequest(StringRequest.Method.POST, BASE_URL + ADD_BILLING,
                response -> {
                    Timber.e(response);
                    hideProgressDialog();
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        Timber.e(jsonObject.toString());
                        if (jsonObject.has("success") && !jsonObject.isNull("success")) {
                            if (jsonObject.getBoolean("success")) {

                                showToast("Updated successfully ! ", BillingAddressActivity.this);

                                if (onBankaccountadded != null) {
                                    onBankaccountadded.billingAddressAdd();
                                }
                                finish();

                            } else {
                                showToast("Something went Wrong", BillingAddressActivity.this);
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
                                Toast.makeText(BillingAddressActivity.this, jsonObject_error.getString("message"), Toast.LENGTH_SHORT).show();
                            }
                            if (jsonObject_error.has("errors")) {
                                JSONObject jsonObject_errors = jsonObject_error.getJSONObject("errors");
                            }
                            //  ((CredentialActivity)getActivity()).showToast(message,getActivity());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        showToast("Something Went Wrong", BillingAddressActivity.this);
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
                Map<String, String> map1 = new HashMap<String, String>();
                map1.put("line1", edtAddressLine1.getText().toString());
                map1.put("line2", edtAddressLine2.getText().toString());
                map1.put("city", edtSuburs.getText().toString());
                map1.put("post_code", edtPostcode.getText().toString());
                map1.put("state", edtState.getText().toString());
                map1.put("country", edtCountry.getText().toString());
                return map1;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, -1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(BillingAddressActivity.this);
        requestQueue.add(stringRequest);
    }


}
