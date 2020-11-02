package com.jobtick.activities;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.button.MaterialButton;
import com.jobtick.EditText.EditTextRegular;
import com.jobtick.R;
import com.jobtick.TextView.TextViewRegular;
import com.jobtick.payment.AddCreditCard;
import com.jobtick.payment.AddCreditCardImpl;
import com.jobtick.utils.Constant;
import com.jobtick.utils.Tools;
import com.stripe.android.ApiResultCallback;
import com.stripe.android.Stripe;
import com.stripe.android.model.Card;
import com.stripe.android.model.CardBrand;
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
    @BindView(R.id.edt_expiry_date)
    TextViewRegular edtExpiryDate;
    @BindView(R.id.edt_security_number)
    EditTextRegular edtSecurityNumber;

    @BindView(R.id.lyt_btn_add_credit_card)
    MaterialButton lytBtnAddCreditCard;

    int year, month, day;
    String str_expire_date = null;
    DatePickerDialog.OnDateSetListener mDateSetListener;

    @BindView(R.id.card_multiline_widget)
    CardMultilineWidget cardMultilineWidget;

    @BindView(R.id.ivBack)
    ImageView ivBack;

    private int expMonth;
    private int expYear;

    private AddCreditCard addCreditCard;


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
                edtExpiryDate.setText(str_expire_date);
            }
        };

        addCreditCard = new AddCreditCardImpl(this, sessionManager) {
            @Override
            public void onSuccess() {
                showToast("Update Successfully.", AddCreditCardActivity.this);
                hideProgressDialog();
            }

            @Override
            public void onError(Exception e) {
                showToast(e.getMessage(), AddCreditCardActivity.this);
                hideProgressDialog();
            }

            @Override
            public void onNetworkResponseError(NetworkResponse networkResponse) {
                errorHandle1(networkResponse);
                hideProgressDialog();
            }

            @Override
            public void onValidationError(ValidationErrorType validationErrorType, String message) {
                showToast(message, AddCreditCardActivity.this);
                hideProgressDialog();
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

    @OnClick({R.id.edt_expiry_date, R.id.lyt_btn_add_credit_card})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.edt_expiry_date:
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
                if(validation()){
                    showProgressDialog();
                    addCreditCard.getToken(edtCardNumber.getText().toString(),
                            expMonth, expYear,
                            edtSecurityNumber.getText().toString(),
                            edtFullName.getText().toString());
                }
                break;
        }
    }


    public void displayDialog() {
        final Calendar today = Calendar.getInstance();

        MonthPickerDialog.Builder builder = new MonthPickerDialog.Builder(AddCreditCardActivity.this, new MonthPickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(int selectedMonth, int selectedYear) {
                expMonth = selectedMonth;
                expYear = selectedYear;
                edtExpiryDate.setText(selectedMonth + " /" + selectedYear);
                Log.d("aa", "selectedMonth : " + selectedMonth + " selectedYear : " + selectedYear);
                Toast.makeText(AddCreditCardActivity.this, "Date set with month " + selectedMonth + " year " + selectedYear, Toast.LENGTH_SHORT).show();
            }
        }, today.get(Calendar.YEAR), today.get(Calendar.MONTH));

        builder.setActivatedMonth(today.get(Calendar.MONTH))
                .setTitle("Select month and year ")
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
                .setMaxYear(2040)
                .build()
                .show();

    }

    private boolean validation(){
        if(edtFullName.getText().toString().isEmpty()){
            edtFullName.setError("The card name must be filled.");
            return false;
        }
        else if(edtCardNumber.getText().toString().isEmpty()){
            edtCardNumber.setError("The card number must be filled.");
            return false;
        }
        else if(edtExpiryDate.getText().toString().isEmpty()){
            edtExpiryDate.setError("The card expiry date must be filled.");
            return false;
        }
        else if(edtSecurityNumber.getText().toString().isEmpty()){
            edtSecurityNumber.setError("The card CVC must be filled.");
            return false;
        }
        return true;
    }
}