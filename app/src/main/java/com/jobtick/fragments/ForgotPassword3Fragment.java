package com.jobtick.fragments;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.jobtick.R;
import android.annotation.SuppressLint;

import com.jobtick.activities.ActivityBase;
import com.jobtick.activities.AuthActivity;
import com.jobtick.utils.TimeHelper;
import com.jobtick.widget.ExtendedEntryText;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import timber.log.Timber;

/**
 * A simple {@link Fragment} subclass.
 */
public class ForgotPassword3Fragment extends Fragment {
    String email,otp;
    CountDownTimer timer;
    private final String zeroTime = "0:00";

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.time_limit)
    TextView timeLimit;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.password)
    ExtendedEntryText edtNewPassword;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.confirm_password)
    ExtendedEntryText edtRepeatNewPassword;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.lyt_btn_verify)
    MaterialButton lytBtnUpdate;

    AuthActivity authActivity;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.toolbar)
    MaterialToolbar toolbar;

    public ForgotPassword3Fragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_forgot_password3, container, false);
        ButterKnife.bind(this, view);
        authActivity = (AuthActivity)requireActivity();
        email = getArguments().getString("email");
        otp = getArguments().getString("otp");

        toolbar.setNavigationOnClickListener(v -> {
            authActivity.onBackPressed();
        });

        timer = new CountDownTimer(90000, 1000) {

            public void onTick(long millisUntilFinished) {
                timeLimit.setText(
                        TimeHelper.convertSecondsToMinAndSeconds((int)(millisUntilFinished / 1000)));

                timeLimit.setAlpha(1F);
            }

            public void onFinish() {
                timeLimit.setText(zeroTime);
                timeLimit.setAlpha(0.4F);
            }
        };

        timer.start();

        return view;
    }

    @OnClick({R.id.lyt_btn_verify})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.lyt_btn_verify:
                Timber.tag("forgotPass3").i("email: " + email + " atp: " + otp);
                if(verification())
                    authActivity.resetPassword(email,otp, edtNewPassword.getText().trim());
                break;
        }
    }

    private boolean verification() {
        if(timeLimit.getText() == zeroTime){
            ((ActivityBase)requireActivity()).showToast("Time limit is ended. Please try again.", requireContext());
            authActivity.unauthorizedUser();
        }
        if(edtNewPassword.getText().length()<8){
            edtNewPassword.setError("Password must be 8 character or more");
            return false;
        }else if(!edtNewPassword.getText().trim().equals(edtRepeatNewPassword.getText().trim())) {
            edtRepeatNewPassword.setError("doesn't match your password");
            return false;
        }


        return true;
    }


}
