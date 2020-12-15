package com.jobtick.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
import com.jobtick.R;
import android.annotation.SuppressLint;

import com.jobtick.widget.ExtendedSettingItem;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SettingActivity extends AppCompatActivity {

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.toolbar)
    MaterialToolbar toolbar;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.edit_account)
    ExtendedSettingItem btnEditAccount;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.payment_settings)
    ExtendedSettingItem btnPaymentSettings;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.notification_settings)
    ExtendedSettingItem btnNotificationSettings;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.change_password)
    ExtendedSettingItem btnChangePassword;


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

    @OnClick({R.id.edit_account, R.id.payment_settings, R.id.notification_settings, R.id.change_password})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.edit_account:
                startActivity(new Intent(SettingActivity.this, EditProfileActivity.class));

                break;
            case R.id.payment_settings:
                Intent payment_settings = new Intent(SettingActivity.this, PaymentSettingsActivity.class);
                startActivity(payment_settings);
                break;
            case R.id.notification_settings:
                Intent notificaiton = new Intent(SettingActivity.this, NotificationSettings.class);
                startActivity(notificaiton);

                break;
            case R.id.change_password:
                Intent change_password = new Intent(SettingActivity.this, ChangePasswordActivity.class);
                startActivity(change_password);
                break;
        }
    }
}
