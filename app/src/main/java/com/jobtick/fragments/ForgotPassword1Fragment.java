package com.jobtick.fragments;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.fragment.app.Fragment;

import com.google.android.material.appbar.MaterialToolbar;
import com.jobtick.EditText.EditTextRegular;
import com.jobtick.R;
import com.jobtick.activities.AuthActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 */
public class ForgotPassword1Fragment extends Fragment implements AuthActivity.EditTextError {


    @BindView(R.id.edt_email_address)
    EditTextRegular edtEmailAddress;
    @BindView(R.id.lyt_btn_next)
    LinearLayout lytBtnNext;
    AuthActivity authActivity;

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
        authActivity = (AuthActivity) getActivity();
        authActivity.setEditTextError(this);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                authActivity.onBackPressed();
            }
        });
        return view;

    }

    @OnClick({R.id.lyt_btn_next})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.lyt_btn_next:
                if (TextUtils.isEmpty(edtEmailAddress.getText().toString().trim())) {
                    edtEmailAddress.setError("Please enter the email address");
                    return;
                }
                authActivity.nextStepForgotPassowrd(edtEmailAddress.getText().toString().trim());
                break;
        }


    }


    @Override
    public void error(String email, String password) {
        edtEmailAddress.setError(email);
    }
}
