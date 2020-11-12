package com.jobtick.fragments;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.button.MaterialButton;
import com.jobtick.R;
import com.jobtick.TextView.TextViewRegular;
import com.jobtick.activities.AuthActivity;
import com.jobtick.utils.Helper;
import com.jobtick.widget.ExtendedEntryText;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 */
public class SignUpFragment extends FragmentBase implements AuthActivity.EditTextError {


    @BindView(R.id.email)
    ExtendedEntryText edtEmailAddress;
    @BindView(R.id.password)
    ExtendedEntryText edtPassword;
    @BindView(R.id.confirm_password)
    ExtendedEntryText edtRepeatPassword;

    @BindView(R.id.lyt_btn_sign_up)
    MaterialButton lytBtnSignUp;
    @BindView(R.id.lyt_btn_google)
    LinearLayout lytBtnGoogle;
    @BindView(R.id.lyt_btn_facebook)
    LinearLayout lytBtnFacebook;
    @BindView(R.id.txt_btn_sign_in)
    TextViewRegular txtBtnSignIn;

    @BindView(R.id.txt_btn_terms)
    TextViewRegular txtBtnTerms;

    private AuthActivity authActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sign_up, container, false);
        ButterKnife.bind(this, view);
        authActivity = (AuthActivity) getActivity();
        if (authActivity != null) {
            authActivity.setEditTextError(this);
        }
        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    private boolean validation() {
        if (TextUtils.isEmpty(edtEmailAddress.getText().toString().trim())) {
            edtEmailAddress.setError("Check your email address");
            return false;
        }
        else if (TextUtils.isEmpty(edtPassword.getText().toString().trim())) {
            edtPassword.setError("Enter your password");
            return false;
        }else if(edtPassword.getText().toString().trim().length() < 8){
            edtPassword.setError("Password must be atleast 8 characters.");
            return false;
        } else if (!edtPassword.getText().toString().trim().equals(edtRepeatPassword.getText().toString().trim())) {
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
                    authActivity.Signup(edtEmailAddress.getText().toString().trim(), edtPassword.getText().toString().trim());
                }
                break;
            case R.id.lyt_btn_google:
                authActivity.signInWithGoogle();
                break;
            case R.id.lyt_btn_facebook:
                authActivity.facebookLogin();
                break;
            case R.id.txt_btn_sign_in:
                Helper.closeKeyboard(authActivity);
                Fragment fragment = new SignInFragment();
                FragmentTransaction ft = authActivity.getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.auth_layout, fragment);
                ft.commit();
                break;
            case R.id.txt_btn_terms:
                //TODO: should be implemented
                break;

        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void error(String email, String password) {
        edtEmailAddress.setError(email);
        edtPassword.setError(password);
    }
}
