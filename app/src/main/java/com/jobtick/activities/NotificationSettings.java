package com.jobtick.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;

import com.google.android.material.appbar.MaterialToolbar;
import com.jobtick.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class NotificationSettings extends ActivityBase {


/*
    @BindView(R.id.rtl_transactional)
    RelativeLayout rtlTranssactional;


    @BindView(R.id.rtl_task_updates)
    RelativeLayout rtlTaskUpdates;

    @BindView(R.id.rtl_task_reminder)
    RelativeLayout rtlTaskReminder;

    @BindView(R.id.rtl_task_job_alert)
    RelativeLayout rtlTaskJobAlert;

    @BindView(R.id.rtl_task_recommendation)
    RelativeLayout rtlTaskRecommendation;

    @BindView(R.id.rtl_helpful_info)
    RelativeLayout rtlHelpfulInfo;


    @BindView(R.id.rtl_update_newteller)
    RelativeLayout rtlUpdateNewteller;
*/


/*    @BindView(R.id.toolbars)
    MaterialToolbar toolbar;*/

    @BindView(R.id.rtl_btn_email_notification)
    RelativeLayout rtlBtnEmailNotification;

    @BindView(R.id.rtl_btn_push_notifications)
    RelativeLayout rtlBtnPushNotifications;

    @BindView(R.id.rtl_btn_sms_notifications)
    RelativeLayout rtlBtnSMSNotifications;

    @BindView(R.id.ivBack)
    ImageView ivBack;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_notification_settings);
        ButterKnife.bind(this);
        //    initToolbar();
        ivBack.setOnClickListener(v -> {
            finish();
        });
    }

/*
    private void initToolbar() {
        toolbar.setNavigationOnClickListener(view -> onBackPressed());
    }
*/


    @OnClick({R.id.rtl_btn_email_notification, R.id.rtl_btn_push_notifications, R.id.rtl_btn_sms_notifications})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rtl_btn_email_notification:
                startSubSettings("Email Notifications");
                break;
            case R.id.rtl_btn_push_notifications:
                startSubSettings("Push Notification");
                break;
            case R.id.rtl_btn_sms_notifications:
                startSubSettings("SMS Notifications");
                break;

        }
    }

    public void startSubSettings(String title) {
        Intent intent = new Intent(NotificationSettings.this, NotificationSubSettingsActivity.class);
        intent.putExtra("title", title);
        startActivity(intent);
    }

}
