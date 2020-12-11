package com.jobtick.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.Nullable;

import com.google.android.material.appbar.MaterialToolbar;
import com.jobtick.R;
import android.annotation.SuppressLint;

import com.jobtick.widget.ExtendedSettingItem;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class NotificationSettings extends ActivityBase {


    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.toolbar)
    MaterialToolbar toolbar;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.email_notifications)
    ExtendedSettingItem rtlBtnEmailNotification;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.push_notifications)
    ExtendedSettingItem rtlBtnPushNotifications;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.sms_notifications)
    ExtendedSettingItem rtlBtnSMSNotifications;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_notification_settings);
        ButterKnife.bind(this);
        initToolbar();
    }

    private void initToolbar() {
        toolbar.setNavigationIcon(R.drawable.ic_back);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Notification Settings");
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


    @OnClick({R.id.email_notifications, R.id.push_notifications, R.id.sms_notifications})
    public void onViewClicked(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.email_notifications:
                intent = new Intent(NotificationSettings.this, EmailNotificationsSettingsActivity.class);
                startActivity(intent);
                break;
            case R.id.push_notifications:
                intent = new Intent(NotificationSettings.this, PushNotificationsSettingsActivity.class);
                startActivity(intent);
                break;
            case R.id.sms_notifications:
                intent = new Intent(NotificationSettings.this, SMSNotificationsSettingsActivity.class);
                startActivity(intent);
                break;
        }
    }
}
