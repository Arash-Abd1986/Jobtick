package com.jobtick.fragments;

import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.fragment.app.Fragment;

import com.jobtick.EditText.EditTextRegular;
import com.jobtick.R;
import com.jobtick.activities.AuthActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 */
public class ForgotPassword3Fragment extends Fragment {
    String email,otp;
    @BindView(R.id.edt_new_password)
    EditTextRegular edtNewPassword;
    @BindView(R.id.edt_repeat_new_password)
    EditTextRegular edtRepeatNewPassword;
    @BindView(R.id.lyt_btn_sign_in)
    LinearLayout lytBtnSignIn;
    @BindView(R.id.img_btn_new_password_toggle)
    ImageView imgBtnNewPasswordToggle;
    @BindView(R.id.img_btn_repeat_new_password_toggle)
    ImageView imgBtnRepeatNewPasswordToggle;

    private boolean new_password_hide = true;
    private boolean repeat_new_password_hide = true;
    AuthActivity authActivity;

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
        return view;
    }

    @OnClick({R.id.img_btn_new_password_toggle, R.id.img_btn_repeat_new_password_toggle, R.id.lyt_btn_sign_in})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.img_btn_new_password_toggle:
                new_passowrd_toggle();
                break;
            case R.id.img_btn_repeat_new_password_toggle:
                repeat_new_passowrd_toggle();
                break;
            case R.id.lyt_btn_sign_in:
                if(varification())
                    authActivity.resetPassword(email,otp,edtNewPassword.getText().toString().trim());
                break;
        }
    }

    private boolean varification() {
        if(TextUtils.isEmpty(edtNewPassword.getText().toString().trim())){
            edtNewPassword.setError("Please enter the password");
            return false;
        }else if(!edtNewPassword.getText().toString().trim().equals(edtRepeatNewPassword.getText().toString().trim())){
            edtRepeatNewPassword.setError("doesn't match your password");
            return false;
        }
        return true;
    }

    private void new_passowrd_toggle() {
        if (new_password_hide) {
            new_password_hide = false;
            edtNewPassword.setInputType(
                    InputType.TYPE_CLASS_TEXT |
                            InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            );
            imgBtnNewPasswordToggle.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_eye));
        } else {
            new_password_hide = true;
            edtNewPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
            imgBtnNewPasswordToggle.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_eye_off));
        }
    }


    private void repeat_new_passowrd_toggle() {
        if (repeat_new_password_hide) {
            repeat_new_password_hide = false;
            edtRepeatNewPassword.setInputType(
                    InputType.TYPE_CLASS_TEXT |
                            InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            );
            imgBtnRepeatNewPasswordToggle.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_eye));
        } else {
            repeat_new_password_hide = true;
            edtRepeatNewPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
            imgBtnRepeatNewPasswordToggle.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_eye_off));
        }

    }


}
