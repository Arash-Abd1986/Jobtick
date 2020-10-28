package com.jobtick.activities;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.cardview.widget.CardView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.jobtick.EditText.EditTextRegular;
import com.jobtick.R;
import com.jobtick.TextView.TextViewRegular;
import com.jobtick.utils.Constant;
import com.jobtick.utils.Tools;
import com.stripe.android.ApiResultCallback;
import com.stripe.android.Stripe;
import com.stripe.android.model.Card;
import com.stripe.android.model.PaymentMethod;
import com.stripe.android.model.PaymentMethodCreateParams;
import com.stripe.android.view.CardMultilineWidget;
import com.whiteelephant.monthpicker.MonthPickerDialog;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import timber.log.Timber;

import static com.jobtick.activities.PaymentSettingsActivity.onBankaccountadded;
import static com.jobtick.utils.ConstantKey.PUBLISHABLE_KEY;

public class AddCreditCardActivity extends ActivityBase {

//    @BindView(R.id.toolbar)
//    MaterialToolbar toolbar;
    @BindView(R.id.edt_full_name)
    EditTextRegular edtFullName;
    @BindView(R.id.edt_card_number)
    EditTextRegular edtCardNumber;
    @BindView(R.id.txt_expiry_date)
    TextViewRegular txtExpiryDate;
    @BindView(R.id.lyt_btn_add_credit_card)
    MaterialButton lytBtnAddCreditCard;

    int year, month, day;
    String str_expire_date = null;
    DatePickerDialog.OnDateSetListener mDateSetListener;
    @BindView(R.id.edt_security_number)
    EditTextRegular edtSecurityNumber;

    @BindView(R.id.card_multiline_widget)
    CardMultilineWidget cardMultilineWidget;

    @BindView(R.id.ivBack)
    ImageView ivBack;

    private Card card_xml;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_credit_card);
        ButterKnife.bind(this);
        initToolbar();
        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month = month + 1;
                str_expire_date = Tools.getExpireDateFormat(month + "/" + year);
                txtExpiryDate.setText(str_expire_date);
            }
        };
    }


    private void initToolbar() {
        ivBack.setOnClickListener(v -> {
            finish();
        });
//        toolbar.setNavigationIcon(R.drawable.ic_back);
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setTitle("Add Credit Card");
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

    @OnClick({R.id.txt_expiry_date, R.id.lyt_btn_add_credit_card})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.txt_expiry_date:
               /* Calendar calendar = Calendar.getInstance();
                year = calendar.get(Calendar.YEAR);
                month = calendar.get(Calendar.MONTH);
                day = calendar.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog dialog = new DatePickerDialog(
                        this,
                        android.R.style.Theme_Holo_Light_Dialog_NoActionBar,
                        mDateSetListener,
                        year, month, day) {
                    @Override
                    protected void onCreate(Bundle savedInstanceState) {
                        super.onCreate(savedInstanceState);
                        getDatePicker().findViewById(getResources().getIdentifier("day", "id", "android")).setVisibility(View.GONE);
                    }
                };
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();*/

                displayDialog();

                break;
            case R.id.lyt_btn_add_credit_card:
                card_xml = cardMultilineWidget.getCard();
                //  card.toParamMap("CARD",cardMultilineWidget.getCard());
                if (card_xml == null) {
                    showToast("Invalid Card Data", this);
                } else {
                    getToken();
                }
                break;
        }
    }


    public void displayDialog() {
        final Calendar today = Calendar.getInstance();

        MonthPickerDialog.Builder builder = new MonthPickerDialog.Builder(AddCreditCardActivity.this, new MonthPickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(int selectedMonth, int selectedYear) {
                txtExpiryDate.setText(selectedMonth + " /" + selectedYear);
                Log.d("aa", "selectedMonth : " + selectedMonth + " selectedYear : " + selectedYear);
                Toast.makeText(AddCreditCardActivity.this, "Date set with month" + selectedMonth + " year " + selectedYear, Toast.LENGTH_SHORT).show();
            }
        }, today.get(Calendar.YEAR), today.get(Calendar.MONTH));

        builder.setActivatedMonth(Calendar.JULY)
                .setTitle("Select  month and year ")
                // .setMaxMonth(Calendar.OCTOBER)
                // .setYearRange(1890, 1890)
                // .setMonthAndYearRange(Calendar.FEBRUARY, Calendar.OCTOBER, 1890, 1890)
                //.showMonthOnly()
                // .showYearOnly()
                .setOnMonthChangedListener(new MonthPickerDialog.OnMonthChangedListener() {
                    @Override
                    public void onMonthChanged(int selectedMonth) {
                        Log.d("a", "Selected month : " + selectedMonth);
                        // Toast.makeText(MainActivity.this, " Selected month : " + selectedMonth, Toast.LENGTH_SHORT).show();
                    }
                })
                .setOnYearChangedListener(new MonthPickerDialog.OnYearChangedListener() {
                    @Override
                    public void onYearChanged(int selectedYear) {

                        Log.d("a", "Selected year : " + selectedYear);
                        // Toast.makeText(MainActivity.this, " Selected year : " + selectedYear, Toast.LENGTH_SHORT).show();
                    }
                })
                .build()
                .show();

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
                        try {

                            JSONObject jsonObject = new JSONObject(response);

                            if (jsonObject.has("success") && !jsonObject.isNull("success")) {
                                if (jsonObject.getBoolean("success")) {
                                    showToast("Updated successfully ! ", AddCreditCardActivity.this);
                                    if (onBankaccountadded != null) {
                                        onBankaccountadded.creditCard();
                                    }
                                    finish();
                                } else {
                                    hideProgressDialog();
                                    showToast("Something went Wrong", AddCreditCardActivity.this);
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
                map1.put("pm_token", pm_token);
                Timber.e(String.valueOf(map1.size()));
                Timber.e(map1.toString());
                return map1;

            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, -1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(AddCreditCardActivity.this);
        requestQueue.add(stringRequest);
    }
}