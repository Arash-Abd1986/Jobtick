package com.jobtick.activities;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.appbar.MaterialToolbar;
import com.jobtick.R;
import com.jobtick.activities.ActivityBase;

public class AbstractCancellationSummaryActivity extends ActivityBase {

    private MaterialToolbar toolbar;
    protected TextView title;
    protected TextView securedPayment;
    protected ImageView avatar;
    protected TextView taskTitle;
    protected TextView posterName;
    protected TextView taskFee;
    protected TextView cancellationReason;
    protected View commentBox;
    protected TextView commentContent;
    protected TextView respondHeader;
    protected View feeContainer;
    protected TextView feeAmount;
    protected TextView learnMore;

    protected View cancellationButtons;
    protected Button accept;
    protected Button decline;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cancellation_summary);

        toolbar = findViewById(R.id.toolbar);
        title = findViewById(R.id.title);
        securedPayment = findViewById(R.id.secured_payment);

        avatar = findViewById(R.id.ivAvatar);
        taskTitle = findViewById(R.id.task_title);
        posterName = findViewById(R.id.poster_name);
        taskFee = findViewById(R.id.task_fee);
        cancellationReason = findViewById(R.id.cancellation_reason);

        commentBox = findViewById(R.id.comment_box);
        commentContent = findViewById(R.id.comment_content);

        respondHeader = findViewById(R.id.cancellation_respond_header);

        feeContainer = findViewById(R.id.cancellation_fee_container);
        feeAmount = findViewById(R.id.cancellation_fee_amount);
        learnMore = findViewById(R.id.learn_more);

        cancellationButtons = findViewById(R.id.cancellation_buttons);
        accept = findViewById(R.id.cancellation_accept);
        decline = findViewById(R.id.cancellation_decline);

        initToolbar();
    }

    private void initToolbar() {
        toolbar.setNavigationIcon(R.drawable.ic_back);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Cancellation Request Summary");
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