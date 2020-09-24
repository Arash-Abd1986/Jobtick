package com.jobtick.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;

import com.google.android.material.appbar.MaterialToolbar;
import com.jobtick.R;

import butterknife.BindView;
import butterknife.OnClick;

public class NotificationSettings extends ActivityBase {


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


    @BindView(R.id.toolbars)
    MaterialToolbar toolbar;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_settings);
          initToolbar();
    }

    private void initToolbar() {
        toolbar.setNavigationOnClickListener(view -> onBackPressed());
    }


    @OnClick({R.id.rtl_transactional, R.id.rtl_task_updates, R.id.rtl_task_reminder,
            R.id.rtl_task_job_alert, R.id.rtl_task_recommendation, R.id.rtl_helpful_info, R.id.rtl_update_newteller})
    public void onViewClicked(View view) {
        switch (view.getId()) {

            case R.id.rtl_transactional:
                startSubSettings("Transactional");
                break;
            case R.id.rtl_task_updates:
                startSubSettings("Task Updates");
                break;
            case R.id.rtl_task_reminder:
                startSubSettings("Task Reminder");
                break;
            case R.id.rtl_task_job_alert:
                startSubSettings("Jobticker Alert");
                break;
            case R.id.rtl_task_recommendation:
                startSubSettings("Task Recommendation");
                break;
            case R.id.rtl_helpful_info:
                startSubSettings("Helpful Information");
                break;
            case R.id.rtl_update_newteller:
                startSubSettings("Update and Newteller");
                break;

        }
    }

    public void startSubSettings(String title) {
        Intent intent = new Intent(NotificationSettings.this, NotificationSubSettingsActivity.class);
        intent.putExtra("title", title);
        startActivity(intent);
    }

}
