package com.jobtick.fragments;


import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.jobtick.R;
import android.annotation.SuppressLint;
import timber.log.Timber;
import com.jobtick.activities.AuthActivity;
import com.jobtick.utils.Helper;
import com.jobtick.utils.SessionManager;
import com.jobtick.utils.TimeHelper;
import com.jobtick.widget.ExtendedEntryText;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 */
public abstract class AbstractVerifyAccountFragment extends Fragment implements AuthActivity.OnResendOtp {
    String email, password;
    SessionManager sessionManager;
    AuthActivity authActivity;
    CountDownTimer timer;

    int wholeTime = 120000;

    private final String zeroTime = "0:00";

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.verify)
    ExtendedEntryText edtVerificationCode;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.email_verify_message)
    TextView emailVerifyMessage;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.lyt_btn_finish)
    MaterialButton lytBtnFinish;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.resend_otp)
    TextView resendOtp;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.time_limit)
    TextView timeLimit;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.toolbar)
    MaterialToolbar toolbar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_verify_account, container, false);
        ButterKnife.bind(this, view);
        authActivity = (AuthActivity) requireActivity();
        email = getArguments().getString("email");
        password = getArguments().getString("password");
        sessionManager = new SessionManager(requireActivity());
        otpEnterInEdtText();

        toolbar.setNavigationOnClickListener(v -> {
            authActivity.onBackPressed();
        });

        emailVerifyMessage.setText(email);
        authActivity.setOnResendOtp(this);

        timer = new CountDownTimer(wholeTime, 1000) {

            public void onTick(long millisUntilFinished) {
                timeLimit.setText(
                        TimeHelper.convertSecondsToMinAndSeconds((int)(millisUntilFinished / 1000)));

                lytBtnFinish.setEnabled(true);
                resendOtp.setEnabled(false);
                resendOtp.setAlpha(0.4F);
                timeLimit.setAlpha(1F);
            }

            public void onFinish() {
                lytBtnFinish.setEnabled(false);
                timeLimit.setText(zeroTime);
                resendOtp.setEnabled(true);
                resendOtp.setAlpha(1F);
                timeLimit.setAlpha(0.4F);
            }
        };

        timer.start();
        return view;
    }

    private void otpEnterInEdtText() {
        edtVerificationCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Timber.tag("debug verification: ").i(s.toString());
                if (s.length() > 6) {
                    edtVerificationCode.setText(s.subSequence(0, 6).toString());
                    edtVerificationCode.setSelection(6);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (edtVerificationCode.getText().length() == 6) {
                    Helper.closeKeyboard(authActivity);
                }
            }

        });
    }

    @OnClick({R.id.lyt_btn_finish, R.id.resend_otp})
    public void onViewClicked(View view) {

        switch (view.getId()) {
            case R.id.lyt_btn_finish:
                whatNext();
                break;
            case R.id.resend_otp:
                onResendOtp();
                break;
        }
    }

    protected boolean validation() {
        if (edtVerificationCode.getText().trim().length() != 6) {
            edtVerificationCode.setError("Verification code must be equal to 6 characters.");
            return false;
        }else if(timeLimit.getText() == zeroTime){
            edtVerificationCode.setError("Verification code is expired.");
            return false;
        }
        return true;
    }

    abstract void whatNext();
    abstract void onResendOtp();


    @Override
    public void success() {
        authActivity.showSuccessToast("Code resent successfully.", getContext());
        timer.start();
    }

    @Override
    public void failure() {
        authActivity.showToast("Resend code failed.", getContext());
    }
}
