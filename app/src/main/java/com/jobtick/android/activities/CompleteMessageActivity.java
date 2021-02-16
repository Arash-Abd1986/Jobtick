package com.jobtick.android.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.jobtick.android.R;
import com.jobtick.android.utils.ConstantKey;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.jobtick.android.activities.TaskDetailsActivity.requestAcceptListener;
import static com.jobtick.android.utils.ConstantKey.RESULTCODE_CREATE_TASK;
import static com.jobtick.android.utils.ConstantKey.RESULTCODE_INCREASE_BUDGET;
import static com.jobtick.android.utils.ConstantKey.RESULTCODE_MAKEANOFFER;

public class CompleteMessageActivity extends AppCompatActivity {

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.toolbar)
    MaterialToolbar toolbar;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.txt_title)
    TextView txtTitle;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.txt_subtitle)
    TextView txtSubtitle;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.cardFinish)
    MaterialButton cardFinish;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.linearTaskCompleted)
    LinearLayout linearTaskCompleted;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.lyt_btn_new_job)
    MaterialButton lytBtnNewJob;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.lyt_btn_view_your_job)
    MaterialButton lytBtnViewYourJob;


    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.linearOfferSent)
    LinearLayout linearOfferSent;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.btn_view_your_offer)
    MaterialButton viewYourOffer;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.btn_explore_jobs)
    MaterialButton exploreJobs;

    public int from = 0;
    public String taskSlug = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete_message);
        ButterKnife.bind(this);
        initToolbar();
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            if (bundle.getString(ConstantKey.COMPLETES_MESSAGE_TITLE) != null) {
                txtTitle.setText(bundle.getString(ConstantKey.COMPLETES_MESSAGE_TITLE));
            }
            if (bundle.getString(ConstantKey.COMPLETES_MESSAGE_SUBTITLE) != null) {
                txtSubtitle.setText(bundle.getString(ConstantKey.COMPLETES_MESSAGE_SUBTITLE));
            }
            if (bundle.containsKey(ConstantKey.COMPLETES_MESSAGE_FROM)) {
                from = bundle.getInt(ConstantKey.COMPLETES_MESSAGE_FROM, 0);
            }
            if (bundle.containsKey(ConstantKey.SLUG)) {
                taskSlug = bundle.getString(ConstantKey.SLUG, null);
                Log.d("taskSlug",taskSlug+"");
            }
        }

        if (from == RESULTCODE_MAKEANOFFER) {
            linearTaskCompleted.setVisibility(View.GONE);
            cardFinish.setVisibility(View.GONE);
            linearOfferSent.setVisibility(View.VISIBLE);
        }
        else if (from == RESULTCODE_CREATE_TASK) {
            linearTaskCompleted.setVisibility(View.VISIBLE);
            cardFinish.setVisibility(View.GONE);
            linearOfferSent.setVisibility(View.GONE);
        }
    }

    private void initToolbar() {
        toolbar.setNavigationIcon(R.drawable.ic_cancel);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Completed");
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


    @OnClick({R.id.cardFinish, R.id.lyt_btn_view_your_job, R.id.lyt_btn_new_job,
            R.id.btn_explore_jobs, R.id.btn_view_your_offer})
    public void onViewClicked(View view) {

        switch (view.getId()) {
            case R.id.cardFinish:
                if (from == RESULTCODE_INCREASE_BUDGET) {
                    if (requestAcceptListener != null) {
                        requestAcceptListener.onRequestAccept();
                    }
                    onBackPressed();
                } else {
                    onBackPressed();
                }

                break;
            case R.id.lyt_btn_view_your_job:
                Intent intent = new Intent(this, DashboardActivity.class);
                if(taskSlug==null) {
                    intent.putExtra(ConstantKey.GO_TO_MY_JOBS, true);
                    startActivity(intent);
                }else{
                    Intent taskDetail = new Intent(this, TaskDetailsActivity.class);
                    Bundle bundle1 = new Bundle();
                    bundle1.putString(ConstantKey.SLUG, taskSlug);
                    //    bundle.putInt(ConstantKey.USER_ID, obj.getPoster().getId());
                    taskDetail.putExtras(bundle1);
                    startActivity(taskDetail);
                    finish();
                }
                break;
            case R.id.lyt_btn_new_job:

                Intent creating_task = new Intent(CompleteMessageActivity.this, CategoryListActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("category", "");
                creating_task.putExtras(bundle);
                startActivity(creating_task);
                finish();
                break;

            case R.id.btn_explore_jobs:

                intent = new Intent(this, DashboardActivity.class);
                intent.putExtra(ConstantKey.GO_TO_EXPLORE, true);
                startActivity(intent);
                break;

            case R.id.btn_view_your_offer:

                if (requestAcceptListener != null) {
                    requestAcceptListener.onMakeAnOffer();
                }
                onBackPressed();
                break;
        }

    }

}
