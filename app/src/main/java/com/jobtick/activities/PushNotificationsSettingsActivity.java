package com.jobtick.activities;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.google.android.material.appbar.MaterialToolbar;

import com.jobtick.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PushNotificationsSettingsActivity extends ActivityBase{


    @BindView(R.id.toolbar)
    MaterialToolbar toolbar;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_push_notification_settings);
        ButterKnife.bind(this);
        initToolbar();
    }

    private void initToolbar() {
        toolbar.setNavigationIcon(R.drawable.ic_back);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Push Notifications");
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
    protected void onStop() {
        super.onStop();
        saveSettings();
    }

    private void saveSettings(){
        //TODO: save settings
    }


}
