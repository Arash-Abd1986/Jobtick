package com.jobtick.android.activities;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;

import com.android.volley.NetworkResponse;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.jobtick.android.R;
import android.annotation.SuppressLint;
import android.widget.ImageView;

import com.jobtick.android.payment.AddCreditCard;
import com.jobtick.android.payment.AddCreditCardImpl;
import com.jobtick.android.utils.StringUtils;
import com.jobtick.android.widget.ExtendedEntryText;

import java.util.Calendar;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

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
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.ivCardType)
    ImageView ivCardType;

    private int expMonth;
    private int expYear;

    private AddCreditCard addCreditCard;

    DatePickerDialog.OnDateSetListener mDateSetListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_credit_card);
        ButterKnife.bind(this);
        initToolbar();

        edtExpiryDate.setExtendedViewOnClickListener(() -> {
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_MONTH, -1);
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);

            DatePickerDialog dialog = new DatePickerDialog(this,
                    android.R.style.Theme_Holo_Light_Dialog_NoActionBar,
                    mDateSetListener,
                    year, month,1);
            dialog.getDatePicker().setMinDate(calendar.getTimeInMillis());
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();
        });
        mDateSetListener = (view1, year, month, dayOfMonth) -> {
            month = month + 1;
            String formattedMonth = String.format(Locale.US,"%02d", month);
            String date = formattedMonth + "/" + year;
            edtExpiryDate.setText(date);
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

        setupCardTypes();
    }

    private void setupCardTypes() {
        edtCardNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(edtCardNumber.getText().length()>1)
                {
                    ivCardType.setVisibility(View.GONE);

                    String cardFirstLetters = edtCardNumber.getText().toString().substring(0,2);
                    if(cardFirstLetters.equals("34") || cardFirstLetters.equals("37")){
                        ivCardType.setVisibility(View.VISIBLE);
                        ivCardType.setImageResource(R.drawable.ic_card_american_express);
                    }
                    if(edtCardNumber.getText().substring(0,1).equals("5")){
                        ivCardType.setVisibility(View.VISIBLE);
                        ivCardType.setImageResource(R.drawable.ic_card_master);
                    }
                    if(edtCardNumber.getText().substring(0,1).equals("4")){
                        ivCardType.setVisibility(View.VISIBLE);
                        ivCardType.setImageResource(R.drawable.ic_card_visa);
                    }

                }else{
                    ivCardType.setVisibility(View.GONE);
                }
            }
        });
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