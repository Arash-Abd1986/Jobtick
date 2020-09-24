package com.jobtick.activities;

import android.os.Bundle;
import android.view.MenuItem;
import androidx.annotation.Nullable;
import com.google.android.material.appbar.MaterialToolbar;
import com.jobtick.R;
import butterknife.BindView;

public class NotificationSubSettingsActivity extends ActivityBase {


    @BindView(R.id.toolbar)
    MaterialToolbar toolbar;
    public String title;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_sub_settings);
        title = getIntent().getExtras().getString("title", "");
        initToolbar();
    }

    private void initToolbar() {
        toolbar.setNavigationIcon(R.drawable.ic_back);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(title);
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

}

