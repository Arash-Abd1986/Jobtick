package com.jobtick.fragments;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.jobtick.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ForgotPassword2Fragment extends AbstractVerifyAccountFragment {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //we need 90 seconds in entering password step, the whole time for these 2 steps is 10 min.
        //so first step should be 510 seconds
        wholeTime = 510000;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        lytBtnFinish.setText(getResources().getString(R.string.next));
    }

    @Override
    void whatNext() {
        String otp = edtVerificationCode.getText();

        if (validation())
            authActivity.forgotPasswordSpecialVerification(email, otp);
         else {
            authActivity.showToast("check otp", authActivity);
        }
    }

    @Override
    void onResendOtp() {
        authActivity.resendOtpForResetPassword(email);
    }
}