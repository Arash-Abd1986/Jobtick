package com.jobtick.activities;


import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.google.android.material.radiobutton.MaterialRadioButton;
import com.jobtick.R;
import com.jobtick.models.UserAccountModel;
import com.jobtick.utils.ImageUtil;
import com.jobtick.utils.SessionManager;
import com.jobtick.widget.ExtendedJobInfo;

import butterknife.BindView;
import butterknife.ButterKnife;

public class Dashboard2Activity extends ActivityBase {


    @BindView(R.id.txt_user_name)
    TextView txtUserName;

    @BindView(R.id.txt_account_level)
    TextView txtAccountLevel;

    @BindView(R.id.awaiting_for_offer)
    ExtendedJobInfo txtAwaitingOffer;

    @BindView(R.id.assigned)
    ExtendedJobInfo txtAssigend;

    @BindView(R.id.overdue)
    ExtendedJobInfo txtOverDue;

    @BindView(R.id.released_money)
    ExtendedJobInfo txtReleasedMoney;

    @BindView(R.id.complete)
    ExtendedJobInfo txtCompleted;

    @BindView(R.id.cancelled)
    ExtendedJobInfo extCancelled;

    @BindView(R.id.rb_as_ticker)
    MaterialRadioButton rbAsATicker;

    @BindView(R.id.rb_as_poster)
    MaterialRadioButton rbAsAPoster;

    @BindView(R.id.rg_ticker_poster)
    RadioGroup rgTickerPoster;

    @BindView(R.id.iv_green_account)
    ImageView iv_green_account;

    @BindView(R.id.iv_payment)
    ImageView iv_payment;

    @BindView(R.id.iv_skills)
    ImageView iv_skills;

    @BindView(R.id.iv_badges)
    ImageView iv_badges;

    private String greenColor = "#38C88A";
    private String grayColor = "#EFEFEF";

    private UserAccountModel userAccountModel;
    private SessionManager sessionManager;

    @BindView(R.id.img_user_avatar)
    ImageView imgUserAvatar;
/*
    @BindView(R.id.toolbar)
    MaterialToolbar toolbar;*/

    @BindView(R.id.ivBack)
    ImageView ivBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard2);
        ButterKnife.bind(this);
        sessionManager = new SessionManager(Dashboard2Activity.this);
        userAccountModel = sessionManager.getUserAccount();
        init();
        initToolbar();
        initComponent();
        setData();
        onChangeTabUser();
    }

    private void initToolbar() {
/*        toolbar.setNavigationIcon(R.drawable.ic_back);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);*/
        ivBack.setOnClickListener(v -> {
            finish();
        });
    }


    public void init() {

    }

    public void setData() {


        txtUserName.setText(sessionManager.getUserAccount().getName());
        txtAccountLevel.setText(sessionManager.getUserAccount().getWorkerTier().getName());
        ImageUtil.displayImage(imgUserAvatar, sessionManager.getUserAccount().getAvatar().getThumbUrl(), null);
    }

    public void initComponent() {

        rgTickerPoster.setOnCheckedChangeListener((group, checkedId) -> {
            onChangeTabUser();
        });

    }

    private void onChangeTabUser() {
        if (rbAsAPoster.isChecked()) {
            if (userAccountModel.getPostTaskStatistics() != null && userAccountModel.getPostTaskStatistics().getAssigned() != null) {
                txtAwaitingOffer.setValue(userAccountModel.getPostTaskStatistics().getAssigned().toString());
            } else {
                txtAwaitingOffer.setValue("0");
            }

            if (userAccountModel.getPostTaskStatistics() != null && userAccountModel.getPostTaskStatistics().getOverdue() != null) {
                txtOverDue.setValue(userAccountModel.getPostTaskStatistics().getOverdue().toString());
            } else {
                txtOverDue.setValue("0");
            }
            if (userAccountModel.getPostTaskStatistics() != null && userAccountModel.getPostTaskStatistics().getCompleted() != null) {
                txtCompleted.setValue(userAccountModel.getPostTaskStatistics().getCompleted().toString());
            } else {
                txtCompleted.setValue("0");
            }
            if (userAccountModel.getPostTaskStatistics() != null && userAccountModel.getPostTaskStatistics().getCurrentBids() != null) {
                txtAwaitingOffer.setValue(userAccountModel.getPostTaskStatistics().getCurrentBids().toString());
            } else {
                txtAwaitingOffer.setValue("0");
            }
        } else {

            if (userAccountModel.getWorkTaskStatistics() != null && userAccountModel.getWorkTaskStatistics().getAssigned() != null) {
                txtAwaitingOffer.setValue(userAccountModel.getWorkTaskStatistics().getAssigned().toString());
            } else {
                txtAwaitingOffer.setValue("0");

            }

            if (userAccountModel.getWorkTaskStatistics() != null && userAccountModel.getWorkTaskStatistics().getOverdue() != null) {
                txtOverDue.setValue(userAccountModel.getWorkTaskStatistics().getOverdue().toString());
            } else {
                txtOverDue.setValue("0");
            }
            if (userAccountModel.getWorkTaskStatistics() != null && userAccountModel.getWorkTaskStatistics().getCompleted() != null) {
                txtCompleted.setValue(userAccountModel.getWorkTaskStatistics().getCompleted().toString());
            } else {
                txtCompleted.setValue("0");
            }
            if (userAccountModel.getWorkTaskStatistics() != null && userAccountModel.getWorkTaskStatistics().getCurrentBids() != null) {
                txtAwaitingOffer.setValue(userAccountModel.getWorkTaskStatistics().getCurrentBids().toString());
            } else {
                txtAwaitingOffer.setValue("0");
            }
        }


        if (userAccountModel.getAccount_status() != null) {
            if (userAccountModel.getAccount_status().isPortfolio()) {
                iv_green_account.setImageResource(R.drawable.ic_progress_checked);

            } else {
                iv_green_account.setColorFilter(ContextCompat.getColor(Dashboard2Activity.this, R.color.grayActive), android.graphics.PorterDuff.Mode.SRC_IN);
            }

            if (rbAsAPoster.isChecked()) {
                if (userAccountModel.getAccount_status().isCredit_card()) {
                    iv_payment.setImageResource(R.drawable.ic_progress_checked);
                } else {
                    iv_payment.setColorFilter(ContextCompat.getColor(Dashboard2Activity.this, R.color.grayActive), android.graphics.PorterDuff.Mode.SRC_IN);
                }
            } else {
                if (userAccountModel.getAccount_status().isBank_account()) {
                    iv_payment.setImageResource(R.drawable.ic_progress_checked);

                } else {
                    iv_payment.setColorFilter(ContextCompat.getColor(Dashboard2Activity.this, R.color.grayActive), android.graphics.PorterDuff.Mode.SRC_IN);
                }


            }

            if (userAccountModel.getAccount_status().isSkills()) {
                iv_skills.setImageResource(R.drawable.ic_progress_checked);
            } else {
                iv_skills.setColorFilter(ContextCompat.getColor(Dashboard2Activity.this, R.color.grayActive), android.graphics.PorterDuff.Mode.SRC_IN);
            }
            if (userAccountModel.getAccount_status().isBadges()) {
                iv_badges.setImageResource(R.drawable.ic_progress_checked);

            } else {
                iv_badges.setColorFilter(ContextCompat.getColor(Dashboard2Activity.this, R.color.grayActive), android.graphics.PorterDuff.Mode.SRC_IN);
            }
        }


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