package com.jobtick.activities;

import android.media.Image;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.google.android.material.appbar.MaterialToolbar;
import com.jobtick.R;
import com.jobtick.TextView.TextViewRegular;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NotificationSubSettingsActivity extends ActivityBase {


    /* @BindView(R.id.toolbar)
     MaterialToolbar toolbar;*/
    public String title;

    @BindView(R.id.toolbar_title)
    TextView toolbarTitle;

    @BindView(R.id.ivBack)
    ImageView ivBack;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_sub_settings);
        ButterKnife.bind(this);
        title = getIntent().getExtras().getString("title", "");
        initToolbar();
    }

    private void initToolbar() {
/*        toolbar.setNavigationIcon(R.drawable.ic_back);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(title);*/

        toolbarTitle.setText(title);
        ivBack.setOnClickListener(v -> finish());
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

