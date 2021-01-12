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
import com.jobtick.utils.StringUtils;
import com.jobtick.utils.Tools;
import com.jobtick.widget.ExtendedEntryText;
import com.whiteelephant.monthpicker.MonthPickerDialog;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import timber.log.Timber;

public class AddCreditCardActivity extends ActivityBase {

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


    private int expMonth;
    private int expYear;

    private AddCreditCard addCreditCard;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_credit_card);
        ButterKnife.bind(this);
        initToolbar();

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

    private void setExpiryDate(String expiryDate){
        expMonth = Integer.parseInt(expiryDate.substring(0, 2));
        expYear = Integer.parseInt(expiryDate.substring(3));
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
                    setExpiryDate(edtExpiryDate.getText());
                    showProgressDialog();
                    addCreditCard.getToken(edtCardNumber.getText(),
                            expMonth, expYear,
                            edtSecurityNumber.getText(),
                            edtFullName.getText());
                }
                break;
        }
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
        else if(edtExpiryDate.getText().toString() == null || edtExpiryDate.getText().isEmpty()
                || edtExpiryDate.getText().length() != 7){
            edtExpiryDate.setError("The card expiry date must be filled.");
            return false;
        }
        else if(!StringUtils.checkCreditCardExpiryFormat(edtExpiryDate.getText().toString())){
            edtExpiryDate.setError("The card expiry date is not correct.");
            return false;
        }
        else if(Integer.parseInt(edtExpiryDate.getText().substring(0, 2)) > 12){
            edtExpiryDate.setError("The card expiry date is not correct.");
        }
        else if(edtSecurityNumber.getText().isEmpty()){
            edtSecurityNumber.setError("The card CVC must be filled.");
            return false;
        }
        return true;
    }
}