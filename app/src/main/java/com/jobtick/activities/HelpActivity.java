package com.jobtick.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
import com.jobtick.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.jobtick.utils.Constant.URL_privacy_policy;

public class HelpActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    MaterialToolbar toolbar;
    @BindView(R.id.rlt_btn_support)
    RelativeLayout rltBtnSupport;
    @BindView(R.id.rlt_btn_terms_condition)
    RelativeLayout rltBtnTermsCondition;
    @BindView(R.id.rlt_privacy_policy)
    RelativeLayout rltPrivacyPolicy;
    @BindView(R.id.rlt_btn_guidelines_for_poster)
    RelativeLayout rltBtnGuidelinesForPoster;
    @BindView(R.id.rlt_btn_guidelines_for_tickers)
    RelativeLayout rltBtnGuidelinesForTickers;

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

    @OnClick({R.id.rlt_btn_support, R.id.rlt_btn_terms_condition, R.id.rlt_privacy_policy, R.id.rlt_btn_guidelines_for_poster, R.id.rlt_btn_guidelines_for_tickers})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rlt_btn_support:

                StartActivity("Support", URL_privacy_policy);
                break;
            case R.id.rlt_btn_terms_condition:
                StartActivity("Terms and Condition", URL_privacy_policy);
                break;
            case R.id.rlt_privacy_policy:
                StartActivity("Privacy Policy", URL_privacy_policy);
                break;
            case R.id.rlt_btn_guidelines_for_poster:
                StartActivity("Guideline for poster", URL_privacy_policy);

                break;
            case R.id.rlt_btn_guidelines_for_tickers:
                StartActivity("Guideline for Ticker", URL_privacy_policy);

                break;
        }
    }


    public void StartActivity(String Title, String URL) {
        Intent i = new Intent(HelpActivity.this, WebViewActivity.class);
        i.putExtra("URL", URL);
        i.putExtra("Title", Title);
        startActivity(i);
    }
}
