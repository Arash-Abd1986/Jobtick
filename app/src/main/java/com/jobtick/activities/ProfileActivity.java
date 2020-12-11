package com.jobtick.activities;

import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.widget.LinearLayout;

import com.jobtick.R;
import android.annotation.SuppressLint;

import com.jobtick.fragments.ProfileViewFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ProfileActivity extends ActivityBase {
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.btnBack)
    LinearLayout btnBack;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_porfile);

        ButterKnife.bind(this);
        btnBack.setOnClickListener(v -> ProfileActivity.super.onBackPressed());

        if(getIntent().getIntExtra("id",-1)!=-1){
            Bundle b = new Bundle();
            b.putInt("userId",getIntent().getIntExtra("id",-1));
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ProfileViewFragment profileFragment = new ProfileViewFragment();
            profileFragment.setArguments(b);
            ft.replace(R.id.profile, profileFragment);
            ft.commit();
        }
    }
}