package com.jobtick.fragments;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.jobtick.R;
import com.jobtick.activities.ActivityBase;
import com.jobtick.activities.AuthActivity;
import com.jobtick.utils.TimeHelper;
import com.jobtick.widget.ExtendedEntryText;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 */
public class ForgotPassword3Fragment extends Fragment {
    String email,otp;
    CountDownTimer timer;
    private final String zeroTime = "0:00";

    @BindView(R.id.time_limit)
    TextView timeLimit;
    @BindView(R.id.password)
    ExtendedEntryText edtNewPassword;
    @BindView(R.id.confirm_password)
    ExtendedEntryText edtRepeatNewPassword;

    @BindView(R.id.lyt_btn_verify)
    MaterialButton lytBtnUpdate;

    AuthActivity authActivity;
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
        authActivity = (AuthActivity)getActivity();
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
                Log.i("forgotPass3", "email: " + email + " atp: " + otp);
                if(verification())
                    authActivity.resetPassword(email,otp,edtNewPassword.getText().toString().trim());
                break;
        }
    }

    private boolean verification() {
        if(timeLimit.getText() == zeroTime){
            ((ActivityBase)requireActivity()).showToast("Time limit is ended. Please try again.", requireContext());
            authActivity.unauthorizedUser();
        }
        else if(TextUtils.isEmpty(edtNewPassword.getText().toString().trim())){
            edtNewPassword.setError("Please enter the password");
            return false;
        }else if(!edtNewPassword.getText().toString().trim().equals(edtRepeatNewPassword.getText().toString().trim())) {
            edtRepeatNewPassword.setError("doesn't match your password");
            return false;
        }
        return true;
    }


}
