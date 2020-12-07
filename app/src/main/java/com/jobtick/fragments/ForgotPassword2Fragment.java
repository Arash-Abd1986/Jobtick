package com.jobtick.fragments;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.jobtick.EditText.EditTextRegular;
import com.jobtick.R;
import com.jobtick.activities.AuthActivity;
import com.jobtick.utils.Helper;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

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
        String otp = edtVerificationCode.getText().toString();

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