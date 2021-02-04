package com.jobtick.android.fragments;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.google.android.material.radiobutton.MaterialRadioButton;
import com.jobtick.android.R;
import android.annotation.SuppressLint;

import com.jobtick.android.activities.EditProfileActivity;
import com.jobtick.android.models.UserAccountModel;
import com.jobtick.android.utils.ImageUtil;
import com.jobtick.android.utils.SessionManager;
import com.jobtick.android.widget.ExtendedJobInfo;
import com.ramijemli.percentagechartview.PercentageChartView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public abstract class AbstractDashboard2Fragment extends Fragment {


    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.txt_user_name)
    TextView txtUserName;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.ivMedal)
    ImageView ivMedal;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.txt_account_level)
    TextView txtAccountLevel;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.awaiting_for_offer)
    ExtendedJobInfo txtAwaitingOffer;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.assigned)
    ExtendedJobInfo txtAssigend;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.overdue)
    ExtendedJobInfo txtOverDue;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.released_money)
    ExtendedJobInfo txtReleasedMoney;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.complete)
    ExtendedJobInfo txtCompleted;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.cancelled)
    ExtendedJobInfo extCancelled;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.rbPortfollio)
    MaterialRadioButton rbProfile;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.rbSkills)
    MaterialRadioButton rbSkills;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.rgTabs)
    RadioGroup rgTabs;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.iv_green_account)
    ImageView iv_green_account;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.iv_payment)
    ImageView iv_payment;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.iv_skills)
    ImageView iv_skills;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.iv_badges)
    ImageView iv_badges;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.img_user_avatar)
    ImageView imgUserAvatar;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.profile_progress)
    PercentageChartView percentageChartView;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.profile_container)
    RelativeLayout profileContainer;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.complete_profile)
    TextView completeProfile;

    protected UserAccountModel userAccountModel;
    protected SessionManager sessionManager;

    public AbstractDashboard2Fragment() {

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sessionManager = new SessionManager(requireContext());
        userAccountModel = sessionManager.getUserAccount();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_dashboard2, container, false);
        ButterKnife.bind(this, view);
        initComponent();
        return view;
    }

    private void initComponent() {
        if(sessionManager.getUserAccount() == null) return;
        if(sessionManager.getUserAccount().getName() != null)
            txtUserName.setText(sessionManager.getUserAccount().getName());
        if(sessionManager.getUserAccount().getWorkerTier() != null && sessionManager.getUserAccount().getWorkerTier().getName() != null)
            txtAccountLevel.setText(sessionManager.getUserAccount().getWorkerTier().getName());
        if(sessionManager.getUserAccount().getAvatar() != null && sessionManager.getUserAccount().getAvatar().getThumbUrl() != null)
            ImageUtil.displayImage(imgUserAvatar, sessionManager.getUserAccount().getAvatar().getThumbUrl(), null);

        rgTabs.setOnCheckedChangeListener((group, checkedId) -> {
            onChangeTabProfile();
        });
        completeProfile.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), EditProfileActivity.class);
            startActivity(intent);
        });

        setData();
    }

    private void onChangeTabProfile() {
        if (rbProfile.isChecked()) {
            //TODO: should be changed after API support chart
            profileContainer.setVisibility(View.VISIBLE);
        } else {
            profileContainer.setVisibility(View.GONE);
        }
    }

    public abstract void setData();

}
