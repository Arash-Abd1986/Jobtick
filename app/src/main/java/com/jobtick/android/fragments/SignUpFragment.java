package com.jobtick.android.fragments;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.button.MaterialButton;
import com.jobtick.android.R;
import android.annotation.SuppressLint;
import android.widget.TextView;

import com.jobtick.android.activities.AuthActivity;
import com.jobtick.android.utils.ExternalIntentHelper;
import com.jobtick.android.utils.Helper;
import com.jobtick.android.widget.ExtendedEntryText;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 */
public class SignUpFragment extends Fragment implements AuthActivity.EditTextError {


    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.email)
    ExtendedEntryText edtEmailAddress;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.password)
    ExtendedEntryText edtPassword;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.confirm_password)
    ExtendedEntryText edtRepeatPassword;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.lyt_btn_sign_up)
    MaterialButton lytBtnSignUp;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.lyt_btn_google)
    LinearLayout lytBtnGoogle;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.lyt_btn_facebook)
    LinearLayout lytBtnFacebook;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.txt_btn_sign_in)
    TextView txtBtnSignIn;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.txt_btn_terms)
    TextView txtBtnTerms;

    private AuthActivity authActivity;

    private View fragmentView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if(fragmentView != null){
            return fragmentView;
        }

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sign_up, container, false);
        ButterKnife.bind(this, view);
        authActivity = (AuthActivity) requireActivity();
        if (authActivity != null) {
            authActivity.setEditTextError(this);
        }
        fragmentView = view;
        return view;
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
        } else if (!edtPassword.getText().trim().equals(edtRepeatPassword.getText().trim())) {
            edtRepeatPassword.setError("password doesn't match");
            return false;
        }
        return true;
    }


    @OnClick({R.id.lyt_btn_sign_up, R.id.lyt_btn_google, R.id.lyt_btn_facebook, R.id.txt_btn_sign_in, R.id.txt_btn_terms})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.lyt_btn_sign_up:
                if (validation()) {
                    authActivity.Signup(edtEmailAddress.getText().trim(), edtPassword.getText().trim());
                }
                break;
            case R.id.lyt_btn_google:
                authActivity.signInWithGoogle(true);
                break;
            case R.id.lyt_btn_facebook:
                authActivity.facebookLogin(true);
                break;
            case R.id.txt_btn_sign_in:
                Helper.closeKeyboard(authActivity);
                Fragment fragment = new SignInFragment();
                FragmentTransaction ft = authActivity.getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.auth_layout, fragment).addToBackStack(SignInFragment.class.getName());
                ft.commit();
                break;
            case R.id.txt_btn_terms:
                ExternalIntentHelper.openLink(requireActivity(), "https://www.jobtick.com/terms");
                break;

        }
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
