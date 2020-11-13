package com.jobtick.fragments;

import androidx.core.content.ContextCompat;

import com.jobtick.R;

public class Dashboard2TickerFragment extends AbstractDashboard2Fragment {

    public static Dashboard2TickerFragment newInstance(){
        return new Dashboard2TickerFragment();
    }

    @Override
    public void setData() {
        txtAwaitingOffer.setTitle("Offered");
        txtReleasedMoney.setTitle("Asked to\nrelease");
        if (userAccountModel.getWorkTaskStatistics() != null && userAccountModel.getWorkTaskStatistics().getAssigned() != null) {
            txtAssigend.setValue(userAccountModel.getWorkTaskStatistics().getAssigned().toString());
        } else {
            txtAssigend.setValue("0");
        }
        if (userAccountModel.getWorkTaskStatistics() != null && userAccountModel.getWorkTaskStatistics().getCancelled() != null) {
            extCancelled.setValue(userAccountModel.getWorkTaskStatistics().getCancelled().toString());
        } else {
            extCancelled.setValue("0");
        }
        if (userAccountModel.getWorkerTier() != null && userAccountModel.getWorkerTier().getServiceFee() != null) {
            txtReleasedMoney.setValue(userAccountModel.getWorkerTier().getServiceFee().toString());
        } else {
            txtReleasedMoney.setValue("0");
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
        if (userAccountModel.getWorkTaskStatistics() != null && userAccountModel.getWorkTaskStatistics().getTotalPosted() != null) {
            txtAwaitingOffer.setValue(userAccountModel.getWorkTaskStatistics().getTotalPosted().toString());
        } else {
            txtAwaitingOffer.setValue("0");
        }
        if (userAccountModel.getWorkTaskStatistics() != null && userAccountModel.getWorkTaskStatistics().getCompletionRate() != null) {
            percentageChartView.setProgress(Float.parseFloat(userAccountModel.getWorkTaskStatistics().getCompletionRate().toString()), false);
        } else {
            percentageChartView.setProgress(0F, false);
        }


        if (userAccountModel.getAccount_status() != null) {
            if (userAccountModel.getAccount_status().isPortfolio()) {
                iv_green_account.setImageResource(R.drawable.ic_progress_checked);

            } else {
                iv_green_account.setColorFilter(ContextCompat.getColor(requireContext(), R.color.grayActive), android.graphics.PorterDuff.Mode.SRC_IN);
            }


            if (userAccountModel.getAccount_status().isBank_account()) {
                iv_payment.setImageResource(R.drawable.ic_progress_checked);

            } else {
                iv_payment.setColorFilter(ContextCompat.getColor(requireContext(), R.color.grayActive), android.graphics.PorterDuff.Mode.SRC_IN);
            }


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
