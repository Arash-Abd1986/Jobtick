package com.jobtick.fragments;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;


import com.google.android.material.button.MaterialButton;
import com.jobtick.R;
import android.annotation.SuppressLint;

import com.jobtick.activities.AuthActivity;

import com.jobtick.utils.Helper;
import com.jobtick.widget.ExtendedEntryText;


import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 */
public class SignInFragment extends Fragment implements AuthActivity.EditTextError {

    private AuthActivity authActivity;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.email)
    ExtendedEntryText edtEmailAddress;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.password)
    ExtendedEntryText edtPassword;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.txt_forgot_password)
    TextView txtForgotPassword;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.lyt_btn_sign_in)
    MaterialButton lytBtnSignIn;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.txt_btn_sign_up)
    TextView txtBtnSignUp;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.lyt_btn_google)
    LinearLayout lytBtnGoogle;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.lyt_btn_facebook)
    LinearLayout lytBtnFacebook;


    public SignInFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sign_in, container, false);
        ButterKnife.bind(this, view);
        authActivity = (AuthActivity) requireActivity();
        if (authActivity != null) {
            authActivity.setEditTextError(this);
        }
        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @OnClick({R.id.txt_forgot_password, R.id.lyt_btn_sign_in, R.id.lyt_btn_google, R.id.lyt_btn_facebook, R.id.txt_btn_sign_up,
    R.id.email, R.id.password})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.txt_forgot_password:
                Helper.closeKeyboard(authActivity);
                Fragment fragment = new ForgotPassword1Fragment();
                FragmentTransaction ft = authActivity.getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.auth_layout, fragment);
                ft.addToBackStack(null);
                ft.commit();
                break;
            case R.id.lyt_btn_sign_in:
                if (validation()) {
                    authActivity.login(edtEmailAddress.getText().trim(), edtPassword.getText().trim());
                }
                break;
            case R.id.lyt_btn_google:
                authActivity.signInWithGoogle(false);
                break;
            case R.id.lyt_btn_facebook:
                authActivity.facebookLogin(false);
                break;

            case R.id.txt_btn_sign_up:
                Helper.closeKeyboard(authActivity);
                fragment = new SignUpFragment();
                ft = authActivity.getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.auth_layout, fragment);
                ft.commit();
                // authActivity.switchContent(new SignUpFragment());
                break;

        }
    }

    private boolean validation() {
        if (TextUtils.isEmpty(edtEmailAddress.getText().trim())) {
            edtEmailAddress.setError("Check your email address");
            return false;
        }
        else if (TextUtils.isEmpty(edtPassword.getText().trim())) {
            edtPassword.setError("Enter your password");
            return false;
        }else if(edtPassword.getText().trim().length() < 8){
            edtPassword.setError("Password must be atleast 8 characters.");
            return false;
        }
        return true;
    }

    @Override
    public void onEmailError(String emailError) {
        edtEmailAddress.setError(emailError);
    }

    @Override
    public void onPasswordError(String passwordError) {
        edtPassword.setError(passwordError);
    }
}
