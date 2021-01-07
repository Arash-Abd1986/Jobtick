package com.jobtick.activities;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import com.android.volley.NetworkResponse;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.jobtick.R;
import android.annotation.SuppressLint;

import com.jobtick.payment.AddCreditCard;
import com.jobtick.payment.AddCreditCardImpl;
import com.jobtick.utils.Tools;
import com.jobtick.widget.ExtendedEntryText;
import com.whiteelephant.monthpicker.MonthPickerDialog;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import timber.log.Timber;

public class AddCreditCardActivity extends ActivityBase implements ExtendedEntryText.ExtendedViewOnClickListener {

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.toolbar)
    MaterialToolbar toolbar;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.edt_full_name)
    ExtendedEntryText edtFullName;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.edt_card_number)
    ExtendedEntryText edtCardNumber;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.edt_expiry_date)
    ExtendedEntryText edtExpiryDate;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.edt_security_number)
    ExtendedEntryText edtSecurityNumber;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.lyt_btn_add_credit_card)
    MaterialButton lytBtnAddCreditCard;

    int year, month, day;
    String str_expire_date = null;


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
                showToast(getString(R.string.credit_card_error), AddCreditCardActivity.this);
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
                    addCreditCard.getToken(edtCardNumber.getText(),
                            expMonth, expYear,
                            edtSecurityNumber.getText(),
                            edtFullName.getText());
                }
                break;
        }
    }


    public void displayDialog() {
        final Calendar today = Calendar.getInstance();

        MonthPickerDialog.Builder builder = new MonthPickerDialog.Builder(AddCreditCardActivity.this, new MonthPickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(int selectedMonth, int selectedYear) {
                expMonth = selectedMonth + 1;
                expYear = selectedYear;
                edtExpiryDate.setText((expMonth < 10) ? "0" + expMonth + "/" + expYear : expMonth + "/" + expYear);
                Timber.d("selectedMonth : " + selectedMonth + " selectedYear : " + selectedYear);
            }
        }, today.get(Calendar.YEAR), today.get(Calendar.MONTH));

        builder.setActivatedMonth(today.get(Calendar.MONTH))
                .setActivatedYear(today.get(Calendar.YEAR))
                .setTitle("Select month and year")
                .setOnMonthChangedListener(new MonthPickerDialog.OnMonthChangedListener() {
                    @Override
                    public void onMonthChanged(int selectedMonth) {
                        Timber.d("Selected month : " + selectedMonth);
                    }
                })
                .setOnYearChangedListener(new MonthPickerDialog.OnYearChangedListener() {
                    @Override
                    public void onYearChanged(int selectedYear) {
                        Timber.tag("a").d("Selected year : " + selectedYear);
                    }
                })
                .setMinYear(today.get(Calendar.YEAR))
                .setMinMonth(today.get(Calendar.MONTH))
                .setMaxYear(today.get(Calendar.YEAR) + 20)
                .build()
                .show();

    }

    private boolean validation(){
        if(edtFullName.getText().isEmpty()){
            edtFullName.setError("The card name must be filled.");
            return false;
        }
        else if(edtCardNumber.getText().isEmpty()){
            edtCardNumber.setError("The card number must be filled.");
            return false;
        }
        else if(edtExpiryDate.getText().isEmpty()){
            edtExpiryDate.setError("The card expiry date must be filled.");
            return false;
        }
        else if(edtSecurityNumber.getText().isEmpty()){
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