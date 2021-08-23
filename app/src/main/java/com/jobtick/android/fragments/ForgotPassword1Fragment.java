package com.jobtick.android.fragments;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.jobtick.android.R;
import android.annotation.SuppressLint;

import com.jobtick.android.activities.AuthActivity;
import com.jobtick.android.widget.ExtendedEntryText;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 */
public class ForgotPassword1Fragment extends Fragment implements AuthActivity.EditTextError {


    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.email)
    ExtendedEntryText edtEmailAddress;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.lyt_btn_next)
    MaterialButton lytBtnNext;
    AuthActivity authActivity;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.toolbar)
    MaterialToolbar toolbar;


    public ForgotPassword1Fragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_forgot_password1, container, false);
        ButterKnife.bind(this, view);
        authActivity = (AuthActivity) requireActivity();
        authActivity.setEditTextError(this);

        toolbar.setNavigationOnClickListener(v -> authActivity.onBackPressed());
        return view;

    }

    @OnClick({R.id.lyt_btn_next})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.lyt_btn_next:
                if (TextUtils.isEmpty(edtEmailAddress.getText().trim())) {
                    edtEmailAddress.setError("Please enter the email address");
                    return;
                }
                authActivity.nextStepForgotPassword(edtEmailAddress.getText().trim());
                break;
        }
    }

    @Override
    public void onEmailError(String emailError) {
        edtEmailAddress.setError(emailError);
    }

    @Override
    public void onPasswordError(String passwordError) {

    }
}
