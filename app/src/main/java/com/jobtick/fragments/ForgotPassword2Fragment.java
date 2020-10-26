package com.jobtick.fragments;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.jobtick.EditText.EditTextRegular;
import com.jobtick.R;
import com.jobtick.activities.AuthActivity;
import com.jobtick.utils.Helper;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 */
public class ForgotPassword2Fragment extends AbstractVerifyAccountFragment {


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        lytBtnFinish.setText(getResources().getString(R.string.next));
    }

    @Override
    void whatNext() {
        String otp = edtVerificationCode.getText().toString();

        if (validation())
            authActivity.forgotPasswordverification(email, otp);
         else {
            authActivity.showToast("check otp", authActivity);
        }
    }
}