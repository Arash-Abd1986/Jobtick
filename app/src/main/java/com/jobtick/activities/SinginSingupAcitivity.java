package com.jobtick.activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;

import com.google.android.material.button.MaterialButton;
import com.jobtick.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SinginSingupAcitivity extends ActivityBase {


    @BindView(R.id.lyt_btn_sign_up)
    MaterialButton lytBtnSignup;

    @BindView(R.id.lyt_btn_sign_in)
    MaterialButton lytBtnSingin;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin_signup);
        ButterKnife.bind(this);

        lytBtnSignup.setOnClickListener(v -> {
            Intent intent = new Intent(SinginSingupAcitivity.this, AuthActivity.class);
            intent.putExtra("type", "Signup");
            startActivity(intent);
        });

        lytBtnSingin.setOnClickListener(v -> {
            Intent intent = new Intent(SinginSingupAcitivity.this, AuthActivity.class);
            intent.putExtra("type", "Signin");
            startActivity(intent);
        });


    }

}
