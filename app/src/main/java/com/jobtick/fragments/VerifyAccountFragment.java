package com.jobtick.fragments;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.jobtick.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class VerifyAccountFragment extends AbstractVerifyAccountFragment {


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        lytBtnFinish.setText(getResources().getString(R.string.finish));
    }

    @Override
    void whatNext() {
        String otp = edtVerificationCode.getText().toString().trim();

        if (validation())
            authActivity.newVerification(email, otp);
    }
}
