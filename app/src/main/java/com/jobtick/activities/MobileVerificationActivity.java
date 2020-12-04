package com.jobtick.activities;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.appbar.MaterialToolbar;
import com.jobtick.R;
import com.jobtick.payment.VerifyPhoneNumber;
import com.jobtick.payment.VerifyPhoneNumberImpl;
import com.jobtick.widget.ExtendedEntryText;


import butterknife.BindView;
import butterknife.ButterKnife;

import static com.jobtick.activities.EditProfileActivity.PHONE_VERIFICATION_REQUEST_CODE;

public class MobileVerificationActivity extends ActivityBase {

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.toolbar)
    MaterialToolbar toolbar;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.phone_verify_message)
    TextView phoneVerifyMessage;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.lyt_btn_verify)
    LinearLayout lytBtnUpdate;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.edt_verification_code)
    ExtendedEntryText etOtp;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.lyt_bottom)
    LinearLayout lytBottom;
    private String phoneNumber;
    private boolean otpSent;

    private VerifyPhoneNumber verifyPhoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mobile_verification);
        ButterKnife.bind(this);
        phoneNumber = getIntent().getStringExtra("phone_number");
        String str = phoneVerifyMessage.getText().toString() +" "+
                phoneNumber;
        phoneVerifyMessage.setText(str);
        initToolbar();

        lytBtnUpdate.setOnClickListener(v -> {
            if(otpSent){
                if(!validationVerify()) return;
                showProgressDialog();
                verifyPhoneNumber.verify(etOtp.getText());
            }
            else{
                MobileVerificationActivity.this.showToast("Request SMS verification first.", MobileVerificationActivity.this);
            }
        });

        verifyPhoneNumber = new VerifyPhoneNumberImpl(this, sessionManager) {
            @Override
            public void onSuccess(VerifyPhoneNumberImpl.SuccessType successType) {
                MobileVerificationActivity.this.hideProgressDialog();
                if(successType == VerifyPhoneNumberImpl.SuccessType.OTP){
                    MobileVerificationActivity.this.showSuccessToast("SMS verification code is sent.", MobileVerificationActivity.this);
                    otpSent = true;
                }
                else if( successType == VerifyPhoneNumberImpl.SuccessType.Verify){
                    setResult(PHONE_VERIFICATION_REQUEST_CODE);
                    finish();
                }
            }

            @Override
            public void onError(Exception e) {
                MobileVerificationActivity.this.hideProgressDialog();
                MobileVerificationActivity.this.showToast(e.getMessage(), MobileVerificationActivity.this);
            }

            @Override
            public void onValidationError(VerifyPhoneNumberImpl.ErrorType errorType, String message) {
                MobileVerificationActivity.this.hideProgressDialog();
                if(errorType == VerifyPhoneNumberImpl.ErrorType.UN_AUTHENTICATED_USER)
                    MobileVerificationActivity.this.unauthorizedUser();
                else
                    MobileVerificationActivity.this.showToast(message, MobileVerificationActivity.this);
            }
        };

        verifyPhoneNumber.sendOTP(phoneNumber);
    }

    private void initToolbar() {
        toolbar.setNavigationIcon(R.drawable.ic_back);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Mobile number verification");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private boolean validationVerify() {
        if (etOtp.getText().length() != 6) {
            etOtp.setError("Enter 6 digits verification code.");
            return false;
        }
        return true;
    }
}
