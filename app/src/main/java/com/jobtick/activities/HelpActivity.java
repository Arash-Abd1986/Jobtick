package com.jobtick.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
import com.jobtick.R;
import com.jobtick.utils.Constant;
import com.jobtick.utils.ExternalIntentHelper;

import android.annotation.SuppressLint;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.jobtick.utils.Constant.URL_how_it_works;
import static com.jobtick.utils.Constant.URL_privacy_policy;

public class HelpActivity extends AppCompatActivity {

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.toolbar)
    MaterialToolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        ButterKnife.bind(this);
        initToolbar();
    }


    private void initToolbar() {
        toolbar.setNavigationIcon(R.drawable.ic_back);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Help");
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @OnClick({R.id.btn_support, R.id.btn_terms, R.id.btn_privacy, R.id.btn_guidelines_poster, R.id.btn_guidelines_ticker})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_support:
                ExternalIntentHelper.openLink(this, Constant.URL_support);
                break;
            case R.id.btn_terms:
                ExternalIntentHelper.openLink(this, Constant.URL_terms);
                break;
            case R.id.btn_privacy:
                ExternalIntentHelper.openLink(this, URL_privacy_policy);
                break;
            case R.id.btn_guidelines_poster:
                ExternalIntentHelper.openLink(this, URL_how_it_works);
                break;
            case R.id.btn_guidelines_ticker:
                ExternalIntentHelper.openLink(this, URL_how_it_works);
                break;
        }
    }
}
