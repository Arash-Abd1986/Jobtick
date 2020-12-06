package com.jobtick.fragments;


import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.jobtick.AppExecutors;
import com.jobtick.R;
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

    private final String zeroTime = "0:00";

    @BindView(R.id.verify)
    ExtendedEntryText edtVerificationCode;

    @BindView(R.id.email_verify_message)
    TextView emailVerifyMessage;

    @BindView(R.id.lyt_btn_finish)
    MaterialButton lytBtnFinish;

    @BindView(R.id.resend_otp)
    TextView resendOtp;

    @BindView(R.id.time_limit)
    TextView timeLimit;

    @BindView(R.id.toolbar)
    MaterialToolbar toolbar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_verify_account, container, false);
        ButterKnife.bind(this, view);
        authActivity = (AuthActivity) getActivity();
        email = getArguments().getString("email");
        password = getArguments().getString("password");
        sessionManager = new SessionManager(getActivity());
        otpEnterInEdtText();

        toolbar.setNavigationOnClickListener(v -> {
            authActivity.onBackPressed();
        });

        emailVerifyMessage.setText(email);
        authActivity.setOnResendOtp(this);

        timer = new CountDownTimer(600000, 1000) {

            public void onTick(long millisUntilFinished) {
                timeLimit.setText(
                        TimeHelper.convertSecondsToMinAndSeconds((int)(millisUntilFinished / 1000)));

                resendOtp.setEnabled(false);
                resendOtp.setAlpha(0.4F);
                timeLimit.setAlpha(1F);
            }

            public void onFinish() {
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
                Log.i("debug verification: ", s.toString());
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
                authActivity.resendOtp(email);
                break;
        }
    }

    protected boolean validation() {
        if (edtVerificationCode.getText().toString().trim().length() != 6) {
            edtVerificationCode.setError("Verification code must be equal to 6 characters.");
            return false;
        }else if(timeLimit.getText() == zeroTime){
            edtVerificationCode.setError("Verification code is expired.");
            return false;
        }
        return true;
    }

    abstract void whatNext();


    @Override
    public void success() {
        authActivity.showToast("Code resent successfully.", getContext());
        timer.start();
    }

    @Override
    public void failure() {
        authActivity.showToast("Resend code failed.", getContext());
    }
}
