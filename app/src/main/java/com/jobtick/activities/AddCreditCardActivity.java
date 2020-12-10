package com.jobtick.activities;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import com.android.volley.NetworkResponse;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.jobtick.R;
import com.jobtick.payment.AddCreditCard;
import com.jobtick.payment.AddCreditCardImpl;
import com.jobtick.utils.Tools;
import com.jobtick.widget.ExtendedEntryText;
import com.whiteelephant.monthpicker.MonthPickerDialog;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddCreditCardActivity extends ActivityBase implements ExtendedEntryText.ExtendedViewOnClickListener {

    @BindView(R.id.toolbar)
    MaterialToolbar toolbar;

    @BindView(R.id.edt_full_name)
    ExtendedEntryText edtFullName;
    @BindView(R.id.edt_card_number)
    ExtendedEntryText edtCardNumber;
    @BindView(R.id.edt_expiry_date)
    ExtendedEntryText edtExpiryDate;
    @BindView(R.id.edt_security_number)
    ExtendedEntryText edtSecurityNumber;

    @BindView(R.id.lyt_btn_add_credit_card)
    MaterialButton lytBtnAddCreditCard;

    int year, month, day;
    String str_expire_date = null;
    DatePickerDialog.OnDateSetListener mDateSetListener;


    private int expMonth;
    private int expYear;

    private AddCreditCard addCreditCard;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_credit_card);
        ButterKnife.bind(this);
        initToolbar();
        edtExpiryDate.setExtendedViewOnClickListener(this);

        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                setEdtExpiryDate(year, month);
            }
        };

        addCreditCard = new AddCreditCardImpl(this, sessionManager) {
            @Override
            public void onSuccess() {
                hideProgressDialog();
                Intent returnIntent = new Intent();
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
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
        toolbar.setNavigationIcon(R.drawable.ic_back);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Add Credit Card");
    }

    private void setEdtExpiryDate(int year, int month){
        month = month + 1;
        str_expire_date = Tools.getExpireDateFormat(month + "/" + year);
        edtExpiryDate.setText(str_expire_date);
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

    @OnClick({R.id.lyt_btn_add_credit_card})
    public void onViewClicked(View view) {
        switch (view.getId()) {
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
            }
        }, today.get(Calendar.YEAR), today.get(Calendar.MONTH));

        builder.setActivatedMonth(today.get(Calendar.MONTH))
                .setTitle("Select month and year ").setMaxYear(2040)
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

    @Override
    public void onClick() {
        displayDialog();
    }
}