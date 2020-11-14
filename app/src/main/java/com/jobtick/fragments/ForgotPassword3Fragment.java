package com.jobtick.fragments;

import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.jobtick.EditText.EditTextRegular;
import com.jobtick.R;
import com.jobtick.activities.AuthActivity;
import com.jobtick.widget.ExtendedEntryText;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 */
public class ForgotPassword3Fragment extends FragmentBase {
    String email,otp;
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
        if(TextUtils.isEmpty(edtNewPassword.getText().toString().trim())){
            edtNewPassword.setError("Please enter the password");
            return false;
        }else if(!edtNewPassword.getText().toString().trim().equals(edtRepeatNewPassword.getText().toString().trim())){
            edtRepeatNewPassword.setError("doesn't match your password");
            return false;
        }
        return true;
    }


}
