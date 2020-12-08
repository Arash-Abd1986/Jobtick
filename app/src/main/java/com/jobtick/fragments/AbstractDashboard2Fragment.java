package com.jobtick.fragments;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.radiobutton.MaterialRadioButton;
import com.jobtick.R;
import com.jobtick.activities.EditProfileActivity;
import com.jobtick.models.UserAccountModel;
import com.jobtick.utils.ImageUtil;
import com.jobtick.utils.SessionManager;
import com.jobtick.widget.ExtendedJobInfo;
import com.ramijemli.percentagechartview.PercentageChartView;

import butterknife.BindView;
import butterknife.ButterKnife;

public abstract class AbstractDashboard2Fragment extends Fragment {


    @BindView(R.id.txt_user_name)
    TextView txtUserName;

    @BindView(R.id.ivMedal)
    ImageView ivMedal;

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

    @BindView(R.id.rbPortfollio)
    MaterialRadioButton rbProfile;

    @BindView(R.id.rbSkills)
    MaterialRadioButton rbSkills;

    @BindView(R.id.rgTabs)
    RadioGroup rgTabs;

    @BindView(R.id.iv_green_account)
    ImageView iv_green_account;

    @BindView(R.id.iv_payment)
    ImageView iv_payment;

    @BindView(R.id.iv_skills)
    ImageView iv_skills;

    @BindView(R.id.iv_badges)
    ImageView iv_badges;

    @BindView(R.id.img_user_avatar)
    ImageView imgUserAvatar;

    @BindView(R.id.profile_progress)
    PercentageChartView percentageChartView;

    @BindView(R.id.profile_container)
    RelativeLayout profileContainer;

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
