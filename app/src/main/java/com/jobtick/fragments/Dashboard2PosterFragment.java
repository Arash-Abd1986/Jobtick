package com.jobtick.fragments;

import androidx.core.content.ContextCompat;

import com.jobtick.R;

public class Dashboard2PosterFragment extends AbstractDashboard2Fragment {

    public static Dashboard2PosterFragment newInstance(){
        return new Dashboard2PosterFragment();
    }

    @Override
    public void setData() {
        txtAccountLevel.setText(sessionManager.getUserAccount().getPosterTier().getName());
        switch (userAccountModel.getPosterTier().getId()){
            case 1:
                ivMedal.setImageResource(R.drawable.ic_boronz_selected);
                break;
            case 2:
                ivMedal.setImageResource(R.drawable.ic_silver_selected);
                break;
            case 3:
                ivMedal.setImageResource(R.drawable.ic_gold_selected);
                break;
            case 4:
                ivMedal.setImageResource(R.drawable.ic_max_selected);
                break;
        }

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



        int percent = 0;
        if (userAccountModel.getAccount_status() != null) {
            if (userAccountModel.getAccount_status().isPortfolio()) {
                iv_green_account.setImageResource(R.drawable.ic_progress_checked);
                percent+=25;
            } else {
                iv_green_account.setColorFilter(ContextCompat.getColor(requireContext(), R.color.grayActive), android.graphics.PorterDuff.Mode.SRC_IN);
            }

            if (userAccountModel.getAccount_status().isCredit_card()) {
                iv_payment.setImageResource(R.drawable.ic_progress_checked);
                percent+=25;
            } else {
                iv_payment.setColorFilter(ContextCompat.getColor(requireContext(), R.color.grayActive), android.graphics.PorterDuff.Mode.SRC_IN);
            }

            if (userAccountModel.getAccount_status().isSkills()) {
                iv_skills.setImageResource(R.drawable.ic_progress_checked);
                percent+=25;
            } else {
                iv_skills.setColorFilter(ContextCompat.getColor(requireContext(), R.color.grayActive), android.graphics.PorterDuff.Mode.SRC_IN);
            }
            if (userAccountModel.getAccount_status().isBadges()) {
                percent+=25;
                iv_badges.setImageResource(R.drawable.ic_progress_checked);

            } else {
                iv_badges.setColorFilter(ContextCompat.getColor(requireContext(), R.color.grayActive), android.graphics.PorterDuff.Mode.SRC_IN);
            }
            percentageChartView.setProgress(percent, false);

        }
    }
}
