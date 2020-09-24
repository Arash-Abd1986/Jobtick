package com.jobtick.fragments;

import android.os.Bundle;
import android.provider.Settings;
import android.text.InputType;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.login.LoginManager;
import com.google.firebase.iid.FirebaseInstanceId;
import com.jobtick.EditText.EditTextRegular;
import com.jobtick.R;
import com.jobtick.TextView.TextViewRegular;
import com.jobtick.activities.AuthActivity;
import com.jobtick.utils.Constant;
import com.jobtick.utils.Helper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 */
public class SignUpFragment extends Fragment implements AuthActivity.EditTextError {


    @BindView(R.id.edt_email_address)
    EditTextRegular edtEmailAddress;
    @BindView(R.id.edt_password)
    EditTextRegular edtPassword;
    @BindView(R.id.img_btn_password_toggle)
    ImageView imgBtnPasswordToggle;
    @BindView(R.id.edt_repeat_password)
    EditTextRegular edtRepeatPassword;
    @BindView(R.id.lyt_btn_sign_up)
    LinearLayout lytBtnSignUp;
    @BindView(R.id.lyt_btn_google)
    LinearLayout lytBtnGoogle;
    @BindView(R.id.lyt_btn_facebook)
    LinearLayout lytBtnFacebook;
    @BindView(R.id.txt_btn_sign_in)
    TextViewRegular txtBtnSignIn;
    @BindView(R.id.img_btn_repeat_password_toggle)
    ImageView imgBtnRepeatPasswordToggle;

    private boolean password_hide = true;
    private boolean repeat_password_hide = true;

    private AuthActivity authActivity;

    public SignUpFragment() {
        // Required empty public constructor
    }


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
        } else if (TextUtils.isEmpty(edtPassword.getText().toString().trim())) {
            edtEmailAddress.setError("Enter your password");
            return false;
        } else if (!edtPassword.getText().toString().trim().equals(edtRepeatPassword.getText().toString().trim())) {
            edtRepeatPassword.setError("password doesn't match");
            return false;
        }
        return true;
    }


    @OnClick({R.id.img_btn_password_toggle, R.id.img_btn_repeat_password_toggle, R.id.lyt_btn_sign_up, R.id.lyt_btn_google, R.id.lyt_btn_facebook, R.id.txt_btn_sign_in})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.img_btn_password_toggle:
                passowrd_toggle();
                break;
            case R.id.img_btn_repeat_password_toggle:
                repeat_passowrd_toggle();
                break;
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
        }
    }

    private void passowrd_toggle() {
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


    private void repeat_passowrd_toggle() {
        if (repeat_password_hide) {
            repeat_password_hide = false;
            edtRepeatPassword.setInputType(
                    InputType.TYPE_CLASS_TEXT |
                            InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            );
            imgBtnRepeatPasswordToggle.setImageDrawable(authActivity.getResources().getDrawable(R.drawable.ic_eye));
        } else {
            repeat_password_hide = true;
            edtRepeatPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
            imgBtnRepeatPasswordToggle.setImageDrawable(authActivity.getResources().getDrawable(R.drawable.ic_eye_off));
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
