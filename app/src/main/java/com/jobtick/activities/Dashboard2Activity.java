package com.jobtick.activities;


import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.core.content.ContextCompat;

import com.google.android.material.appbar.MaterialToolbar;
import com.jobtick.R;
import com.jobtick.TextView.TextViewMedium;
import com.jobtick.TextView.TextViewRegular;
import com.jobtick.TextView.TextViewSemiBold;
import com.jobtick.models.UserAccountModel;
import com.jobtick.utils.SessionManager;

import butterknife.BindView;
import butterknife.ButterKnife;

public class Dashboard2Activity extends ActivityBase {


    @BindView(R.id.txt_user_name)
    TextViewSemiBold txtUserName;

    @BindView(R.id.txt_account_level)
    TextViewRegular txtAccountLevel;

    @BindView(R.id.txt_awaiting_for_offer)
    TextViewMedium txtAwaitingOffer;

    @BindView(R.id.txt_assigned)
    TextViewMedium txtAssigend;

    @BindView(R.id.txt_overdue)
    TextViewMedium txtOverDue;

    @BindView(R.id.txt_released_money)
    TextViewMedium txtReleasedMoney;

    @BindView(R.id.txt_completed)
    TextViewMedium txtCompleted;

    @BindView(R.id.rb_as_ticker)
    RadioButton rbAsATicker;

    @BindView(R.id.rb_as_poster)
    RadioButton rbAsAPoster;

    @BindView(R.id.rg_ticker_poster)
    RadioGroup rgTickerPoster;

    @BindView(R.id.view_account1)
    View view_account1;

    @BindView(R.id.view_account2)
    View view_account2;

    @BindView(R.id.iv_green_account)
    ImageView iv_green_account;

    @BindView(R.id.txt_account)
    TextViewRegular txt_account;

    @BindView(R.id.txt_payment)
    TextViewRegular txt_payment;

    @BindView(R.id.txt_skills)
    TextViewRegular txt_skills;

    @BindView(R.id.txt_badges)
    TextViewRegular txt_badges;

    @BindView(R.id.view_payment1)
    View view_payment1;

    @BindView(R.id.view_payment2)
    View view_payment2;

    @BindView(R.id.iv_payment)
    ImageView iv_payment;


    @BindView(R.id.view_skills_1)
    View view_skills_1;

    @BindView(R.id.view_skills_2)
    View view_skills_2;

    @BindView(R.id.iv_skills)
    ImageView iv_skills;


    @BindView(R.id.iv_badges)
    ImageView iv_badges;


    @BindView(R.id.view_badges2)
    View view_badges2;

    @BindView(R.id.view_badges1)
    View view_badges1;

    private String greenColor = "#38C88A";
    private String grayColor = "#EFEFEF";

    private UserAccountModel userAccountModel;
    private SessionManager sessionManager;

    @BindView(R.id.toolbar)
    MaterialToolbar toolbar;

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
        toolbar.setNavigationIcon(R.drawable.ic_back);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }


    public void init() {

    }

    public void setData() {
        txtUserName.setText(sessionManager.getUserAccount().getName());
        txtAccountLevel.setText(sessionManager.getUserAccount().getWorkerTier().getName());
    }

    public void initComponent() {

        rgTickerPoster.setOnCheckedChangeListener((group, checkedId) -> {
            onChangeTabUser();
        });

    }

    private void onChangeTabUser() {
        if (rbAsAPoster.isChecked()) {
            if (userAccountModel.getPostTaskStatistics() != null && userAccountModel.getPostTaskStatistics().getAssigned() != null) {
                txtAwaitingOffer.setText(userAccountModel.getPostTaskStatistics().getAssigned().toString());
            } else {
                txtAwaitingOffer.setText("0");
            }

            if (userAccountModel.getPostTaskStatistics() != null && userAccountModel.getPostTaskStatistics().getOverdue() != null) {
                txtOverDue.setText(userAccountModel.getPostTaskStatistics().getOverdue().toString());
            } else {
                txtOverDue.setText("0");
            }
            if (userAccountModel.getPostTaskStatistics() != null && userAccountModel.getPostTaskStatistics().getCompleted() != null) {
                txtCompleted.setText(userAccountModel.getPostTaskStatistics().getCompleted().toString());
            } else {
                txtCompleted.setText("0");
            }
            if (userAccountModel.getPostTaskStatistics() != null && userAccountModel.getPostTaskStatistics().getCurrentBids() != null) {
                txtAwaitingOffer.setText(userAccountModel.getPostTaskStatistics().getCurrentBids().toString());
            } else {
                txtAwaitingOffer.setText("0");
            }
        } else {

            if (userAccountModel.getWorkTaskStatistics() != null && userAccountModel.getWorkTaskStatistics().getAssigned() != null) {
                txtAwaitingOffer.setText(userAccountModel.getWorkTaskStatistics().getAssigned().toString());
            } else {
                txtAwaitingOffer.setText("0");

            }

            if (userAccountModel.getWorkTaskStatistics() != null && userAccountModel.getWorkTaskStatistics().getOverdue() != null) {
                txtOverDue.setText(userAccountModel.getWorkTaskStatistics().getOverdue().toString());
            } else {
                txtOverDue.setText("0");
            }
            if (userAccountModel.getWorkTaskStatistics() != null && userAccountModel.getWorkTaskStatistics().getCompleted() != null) {
                txtCompleted.setText(userAccountModel.getWorkTaskStatistics().getCompleted().toString());
            } else {
                txtCompleted.setText("0");
            }
            if (userAccountModel.getWorkTaskStatistics() != null && userAccountModel.getWorkTaskStatistics().getCurrentBids() != null) {
                txtAwaitingOffer.setText(userAccountModel.getWorkTaskStatistics().getCurrentBids().toString());
            } else {
                txtAwaitingOffer.setText("0");
            }
        }


        if (userAccountModel.getAccount_status() != null) {
            if (userAccountModel.getAccount_status().isPortfolio()) {
                view_account1.setBackgroundColor(Color.parseColor(greenColor));
                view_account2.setBackgroundColor(Color.parseColor(greenColor));
                iv_green_account.setColorFilter(ContextCompat.getColor(Dashboard2Activity.this, R.color.greenActive), android.graphics.PorterDuff.Mode.SRC_IN);
                txt_account.setTextColor(Color.parseColor(greenColor));

            } else {
                view_account1.setBackgroundColor(Color.parseColor(grayColor));
                view_account2.setBackgroundColor(Color.parseColor(grayColor));
                txt_account.setTextColor(Color.parseColor(grayColor));
                iv_green_account.setColorFilter(ContextCompat.getColor(Dashboard2Activity.this, R.color.grayActive), android.graphics.PorterDuff.Mode.SRC_IN);
            }

            if (rbAsAPoster.isChecked()) {
                if (userAccountModel.getAccount_status().isCredit_card()) {
                    view_payment1.setBackgroundColor(Color.parseColor(greenColor));
                    view_payment2.setBackgroundColor(Color.parseColor(greenColor));
                    iv_payment.setColorFilter(ContextCompat.getColor(Dashboard2Activity.this, R.color.greenActive), android.graphics.PorterDuff.Mode.SRC_IN);
                    txt_payment.setTextColor(Color.parseColor(greenColor));
                } else {
                    view_payment1.setBackgroundColor(Color.parseColor(grayColor));
                    view_payment2.setBackgroundColor(Color.parseColor(grayColor));
                    iv_payment.setColorFilter(ContextCompat.getColor(Dashboard2Activity.this, R.color.grayActive), android.graphics.PorterDuff.Mode.SRC_IN);
                    txt_payment.setTextColor(Color.parseColor(grayColor));
                }
            } else {
                if (userAccountModel.getAccount_status().isBank_account()) {

                    view_payment1.setBackgroundColor(Color.parseColor(greenColor));
                    view_payment2.setBackgroundColor(Color.parseColor(greenColor));
                    iv_payment.setColorFilter(ContextCompat.getColor(Dashboard2Activity.this, R.color.greenActive), android.graphics.PorterDuff.Mode.SRC_IN);
                    txt_payment.setTextColor(Color.parseColor(greenColor));

                } else {

                    view_payment1.setBackgroundColor(Color.parseColor(grayColor));
                    view_payment2.setBackgroundColor(Color.parseColor(grayColor));
                    iv_payment.setColorFilter(ContextCompat.getColor(Dashboard2Activity.this, R.color.grayActive), android.graphics.PorterDuff.Mode.SRC_IN);
                    txt_payment.setTextColor(Color.parseColor(grayColor));

                }


            }

            if (userAccountModel.getAccount_status().isSkills()) {

                view_skills_1.setBackgroundColor(Color.parseColor(greenColor));
                view_skills_2.setBackgroundColor(Color.parseColor(greenColor));
                iv_skills.setColorFilter(ContextCompat.getColor(Dashboard2Activity.this, R.color.greenActive), android.graphics.PorterDuff.Mode.SRC_IN);
                txt_skills.setTextColor(Color.parseColor(greenColor));

            } else {
                view_skills_1.setBackgroundColor(Color.parseColor(grayColor));
                view_skills_2.setBackgroundColor(Color.parseColor(grayColor));
                iv_skills.setColorFilter(ContextCompat.getColor(Dashboard2Activity.this, R.color.grayActive), android.graphics.PorterDuff.Mode.SRC_IN);
                txt_skills.setTextColor(Color.parseColor(grayColor));
            }
            if (userAccountModel.getAccount_status().isBadges()) {
                view_badges1.setBackgroundColor(Color.parseColor(greenColor));
                view_badges2.setBackgroundColor(Color.parseColor(greenColor));
                iv_badges.setColorFilter(ContextCompat.getColor(Dashboard2Activity.this, R.color.greenActive), android.graphics.PorterDuff.Mode.SRC_IN);
                txt_badges.setTextColor(Color.parseColor(greenColor));

            } else {
                view_badges1.setBackgroundColor(Color.parseColor(grayColor));
                view_badges2.setBackgroundColor(Color.parseColor(grayColor));
                iv_badges.setColorFilter(ContextCompat.getColor(Dashboard2Activity.this, R.color.grayActive), android.graphics.PorterDuff.Mode.SRC_IN);
                txt_badges.setTextColor(Color.parseColor(grayColor));
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