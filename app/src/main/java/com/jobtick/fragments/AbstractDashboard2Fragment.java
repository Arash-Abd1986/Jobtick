package com.jobtick.fragments;

import android.content.Intent;
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
        txtUserName.setText(sessionManager.getUserAccount().getName());
        txtAccountLevel.setText(sessionManager.getUserAccount().getWorkerTier().getName());
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

    private void onChangeTabUser() {
        txtAwaitingOffer.setTitle("Awaiting \nfor offer");
        txtReleasedMoney.setTitle("Released\nMoney");
        if (userAccountModel.getPostTaskStatistics() != null && userAccountModel.getPostTaskStatistics().getAssigned() != null) {
            txtAssigend.setValue(userAccountModel.getPostTaskStatistics().getAssigned().toString());
        } else {
            txtAssigend.setValue("0");
        }
        if (userAccountModel.getPostTaskStatistics() != null && userAccountModel.getPostTaskStatistics().getCancelled() != null) {
            txtAssigend.setValue(userAccountModel.getPostTaskStatistics().getCancelled().toString());
        } else {
            extCancelled.setValue("0");
        }

        if (userAccountModel.getPosterTier() != null && userAccountModel.getPosterTier().getServiceFee() != null) {
            txtReleasedMoney.setValue(userAccountModel.getPosterTier().getServiceFee().toString());
        } else {
            txtReleasedMoney.setValue("0");
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
        if (userAccountModel.getPostTaskStatistics() != null && userAccountModel.getPostTaskStatistics().getOpenForBids() != null) {
            txtAwaitingOffer.setValue(userAccountModel.getPostTaskStatistics().getOpenForBids().toString());
        } else {
            txtAwaitingOffer.setValue("0");
        }
        if (userAccountModel.getPostTaskStatistics() != null && userAccountModel.getPostTaskStatistics().getCompletionRate() != null) {
            percentageChartView.setProgress(Float.parseFloat(userAccountModel.getPostTaskStatistics().getCompletionRate().toString()), false);
        } else {
            percentageChartView.setProgress(0F, false);
        }
//
//
//
//
//            txtAwaitingOffer.setTitle("Offered");
//            txtReleasedMoney.setTitle("Asked to\nrelease");
//            if (userAccountModel.getWorkTaskStatistics() != null && userAccountModel.getWorkTaskStatistics().getAssigned() != null) {
//                txtAssigend.setValue(userAccountModel.getWorkTaskStatistics().getAssigned().toString());
//            } else {
//                txtAssigend.setValue("0");
//            }
//            if (userAccountModel.getWorkTaskStatistics() != null && userAccountModel.getWorkTaskStatistics().getCancelled() != null) {
//                extCancelled.setValue(userAccountModel.getWorkTaskStatistics().getCancelled().toString());
//            } else {
//                extCancelled.setValue("0");
//            }
//            if (userAccountModel.getWorkerTier() != null && userAccountModel.getWorkerTier().getServiceFee() != null) {
//                txtReleasedMoney.setValue(userAccountModel.getWorkerTier().getServiceFee().toString());
//            } else {
//                txtReleasedMoney.setValue("0");
//            }
//
//            if (userAccountModel.getWorkTaskStatistics() != null && userAccountModel.getWorkTaskStatistics().getOverdue() != null) {
//                txtOverDue.setValue(userAccountModel.getWorkTaskStatistics().getOverdue().toString());
//            } else {
//                txtOverDue.setValue("0");
//            }
//            if (userAccountModel.getWorkTaskStatistics() != null && userAccountModel.getWorkTaskStatistics().getCompleted() != null) {
//                txtCompleted.setValue(userAccountModel.getWorkTaskStatistics().getCompleted().toString());
//            } else {
//                txtCompleted.setValue("0");
//            }
//            if (userAccountModel.getWorkTaskStatistics() != null && userAccountModel.getWorkTaskStatistics().getTotalPosted() != null) {
//                txtAwaitingOffer.setValue(userAccountModel.getWorkTaskStatistics().getTotalPosted().toString());
//            } else {
//                txtAwaitingOffer.setValue("0");
//            }
//            if (userAccountModel.getWorkTaskStatistics() != null && userAccountModel.getWorkTaskStatistics().getCompletionRate() != null) {
//                percentageChartView.setProgress(Float.parseFloat(userAccountModel.getWorkTaskStatistics().getCompletionRate().toString()), false);
//            } else {
//                percentageChartView.setProgress(0F, false);
//            }


        if (userAccountModel.getAccount_status() != null) {
            if (userAccountModel.getAccount_status().isPortfolio()) {
                iv_green_account.setImageResource(R.drawable.ic_progress_checked);

            } else {
                iv_green_account.setColorFilter(ContextCompat.getColor(requireContext(), R.color.grayActive), android.graphics.PorterDuff.Mode.SRC_IN);
            }

            if (userAccountModel.getAccount_status().isCredit_card()) {
                iv_payment.setImageResource(R.drawable.ic_progress_checked);
            } else {
                iv_payment.setColorFilter(ContextCompat.getColor(requireContext(), R.color.grayActive), android.graphics.PorterDuff.Mode.SRC_IN);
            }

//                if (userAccountModel.getAccount_status().isBank_account()) {
//                    iv_payment.setImageResource(R.drawable.ic_progress_checked);
//
//                } else {
//                    iv_payment.setColorFilter(ContextCompat.getColor(Dashboard2Activity.this, R.color.grayActive), android.graphics.PorterDuff.Mode.SRC_IN);
//                }


            if (userAccountModel.getAccount_status().isSkills()) {
                iv_skills.setImageResource(R.drawable.ic_progress_checked);
            } else {
                iv_skills.setColorFilter(ContextCompat.getColor(requireContext(), R.color.grayActive), android.graphics.PorterDuff.Mode.SRC_IN);
            }
            if (userAccountModel.getAccount_status().isBadges()) {
                iv_badges.setImageResource(R.drawable.ic_progress_checked);

            } else {
                iv_badges.setColorFilter(ContextCompat.getColor(requireContext(), R.color.grayActive), android.graphics.PorterDuff.Mode.SRC_IN);
            }
        }
    }
}
