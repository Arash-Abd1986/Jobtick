package com.jobtick.fragments;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.text.InputType;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;


import com.jobtick.EditText.EditTextRegular;
import com.jobtick.R;
import com.jobtick.TextView.TextViewMedium;
import com.jobtick.activities.AuthActivity;

import com.jobtick.utils.Helper;


import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 */
public class SignInFragment extends Fragment implements AuthActivity.EditTextError {

    private AuthActivity authActivity;

    @BindView(R.id.edt_email_address)
    EditTextRegular edtEmailAddress;
    @BindView(R.id.edt_password)
    EditTextRegular edtPassword;
    @BindView(R.id.txt_forgot_password)
    TextViewMedium txtForgotPassword;
    @BindView(R.id.lyt_btn_sign_in)
    LinearLayout lytBtnSignIn;
    @BindView(R.id.txt_btn_sign_up)
    TextView txtBtnSignUp;
    @BindView(R.id.img_btn_password_toggle)
    ImageView imgBtnPasswordToggle;
    @BindView(R.id.lyt_btn_google)
    LinearLayout lytBtnGoogle;
    @BindView(R.id.lyt_btn_facebook)
    LinearLayout lytBtnFacebook;

    private boolean password_hide = true;

    public SignInFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sign_in, container, false);
        ButterKnife.bind(this, view);
        authActivity = (AuthActivity) getActivity();
        authActivity.setEditTextError(this);
        ((InputMethodManager)getContext().getSystemService(Context.INPUT_METHOD_SERVICE))
                .showSoftInput(edtEmailAddress, InputMethodManager.SHOW_FORCED);
        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        imgBtnPasswordToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (password_hide) {
                    password_hide = false;
                    edtPassword.setInputType(
                            InputType.TYPE_CLASS_TEXT |
                                    InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                    );
                    imgBtnPasswordToggle.setImageDrawable(authActivity.getResources().getDrawable(R.drawable.ic_eye));
                } else {
                    password_hide = true;
                    edtPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    imgBtnPasswordToggle.setImageDrawable(authActivity.getResources().getDrawable(R.drawable.ic_eye_off));
                }
            }
        });

    }

    @OnClick({R.id.txt_forgot_password, R.id.lyt_btn_sign_in, R.id.lyt_btn_google, R.id.lyt_btn_facebook, R.id.txt_btn_sign_up})
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
                    authActivity.login(edtEmailAddress.getText().toString().trim(), edtPassword.getText().toString().trim());
                }
                break;
            case R.id.lyt_btn_google:
                authActivity.signInWithGoogle();
                break;
            case R.id.lyt_btn_facebook:
                authActivity.facebookLogin();
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
        if (TextUtils.isEmpty(edtEmailAddress.getText().toString().trim())) {
            edtEmailAddress.setError("Check your email address");
            return false;
        } else if (TextUtils.isEmpty(edtPassword.getText().toString().trim())) {
            edtEmailAddress.setError("Enter your password");
            return false;
        }
        return true;
    }

    @Override
    public void error(String email, String password) {
        edtEmailAddress.setError(email);
        edtPassword.setError(password);
    }


}
