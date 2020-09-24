package com.jobtick.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
import com.jobtick.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SettingActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    MaterialToolbar toolbar;
    @BindView(R.id.rlt_btn_edit_account)
    RelativeLayout rltBtnEditAccount;
    @BindView(R.id.rlt_btn_payment_settings)
    RelativeLayout rltBtnPaymentSettings;
    @BindView(R.id.rlt_btn_notification_settings)
    RelativeLayout rltBtnNotificationSettings;
    @BindView(R.id.rlt_btn_change_password)
    RelativeLayout rltBtnChangePassword;
    @BindView(R.id.rlt_btn_mobile_verification)
    RelativeLayout rltBtnMobileVerification;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        ButterKnife.bind(this);

        initToolbar();
    }

    private void initToolbar() {
        toolbar.setNavigationIcon(R.drawable.ic_back);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Settings");
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

    @OnClick({R.id.rlt_btn_edit_account, R.id.rlt_btn_payment_settings, R.id.rlt_btn_notification_settings, R.id.rlt_btn_change_password, R.id.rlt_btn_mobile_verification})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rlt_btn_edit_account:
                startActivity(new Intent(SettingActivity.this, EditProfileActivity.class));

                break;
            case R.id.rlt_btn_payment_settings:
                Intent payment_settings = new Intent(SettingActivity.this, PaymentSettingsActivity.class);
                startActivity(payment_settings);
                break;
            case R.id.rlt_btn_notification_settings:
                Intent notificaiton = new Intent(SettingActivity.this, NotificationSettings.class);
                startActivity(notificaiton);

                break;
            case R.id.rlt_btn_change_password:
                Intent change_password = new Intent(SettingActivity.this, ChangePasswordActivity.class);
                startActivity(change_password);
                break;
            case R.id.rlt_btn_mobile_verification:
                Intent mobile_verification = new Intent(SettingActivity.this, MobileVerificationActivity.class);
                startActivity(mobile_verification);
                break;
        }
    }
}
