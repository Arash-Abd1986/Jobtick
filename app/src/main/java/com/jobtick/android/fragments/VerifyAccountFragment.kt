package com.jobtick.android.fragments;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.jobtick.android.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class VerifyAccountFragment extends AbstractVerifyAccountFragment {


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    void whatNext() {
        String otp = edtVerificationCode.getText().trim();

        if (validation())
            authActivity.newEmailVerification(email, otp);
    }

    @Override
    void onResendOtp() {
        authActivity.newResendOtp(email);
    }
}
