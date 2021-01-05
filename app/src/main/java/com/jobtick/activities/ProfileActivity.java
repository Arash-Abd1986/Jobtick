package com.jobtick.activities;

import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.appbar.MaterialToolbar;
import com.jobtick.R;

import com.jobtick.fragments.ProfileViewFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ProfileActivity extends ActivityBase {

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.toolbar)
    MaterialToolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_porfile);
        ButterKnife.bind(this);

        if(getIntent().getIntExtra("id",-1)!=-1){
            Bundle b = new Bundle();
            b.putInt("userId",getIntent().getIntExtra("id",-1));
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ProfileViewFragment profileFragment = new ProfileViewFragment();
            profileFragment.setArguments(b);
            ft.replace(R.id.profile, profileFragment);
            ft.commit();
        }

        initToolbar();
    }


    private void initToolbar() {
        toolbar.setNavigationIcon(R.drawable.ic_back);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Profile");
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }
}