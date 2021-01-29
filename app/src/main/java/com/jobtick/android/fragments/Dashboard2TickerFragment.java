package com.jobtick.android.fragments;

import android.graphics.Color;

import androidx.core.content.ContextCompat;


import com.jobtick.android.R;


public class Dashboard2TickerFragment extends AbstractDashboard2Fragment {
    public static final int[] CHART_COLORS = {
            Color.rgb(88, 107, 245),Color.rgb(243, 176, 4),Color.rgb(43, 50, 64),
            Color.rgb(255, 86, 48)
    };
    public static Dashboard2TickerFragment newInstance(){
        return new Dashboard2TickerFragment();
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

        txtAwaitingOffer.setTitle("Offered");
        txtReleasedMoney.setTitle("Payment pending");
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
        if (userAccountModel.getWorkTaskStatistics() != null && userAccountModel.getWorkTaskStatistics().getUnpaid() != null) {
            txtReleasedMoney.setValue(userAccountModel.getWorkTaskStatistics().getUnpaid().toString());
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
        if (userAccountModel.getWorkTaskStatistics() != null && userAccountModel.getWorkTaskStatistics().getActiveOffers() != null) {
            txtAwaitingOffer.setValue(userAccountModel.getWorkTaskStatistics().getActiveOffers().toString());
        } else {
            txtAwaitingOffer.setValue("0");
        }
        if (userAccountModel.getWorkTaskStatistics() != null && userAccountModel.getWorkTaskStatistics().getCompletionRate() != null) {
            if(userAccountModel.getWorkTaskStatistics().getCompletionRate()>=0 && userAccountModel.getWorkTaskStatistics().getCompletionRate()<=100)
               percentageChartView.setProgress(Float.parseFloat(userAccountModel.getWorkTaskStatistics().getCompletionRate().toString()), false);
            else
                percentageChartView.setProgress(0F, false);

        } else {
            percentageChartView.setProgress(0F, false);
        }


        int percent=0;
        if (userAccountModel.getAccount_status() != null) {
            if (userAccountModel.getAccount_status().isPortfolio()) {
                iv_green_account.setImageResource(R.drawable.ic_progress_checked);
                percent+=25;
            } else {
                iv_green_account.setColorFilter(ContextCompat.getColor(requireContext(), R.color.grayActive), android.graphics.PorterDuff.Mode.SRC_IN);
            }


            if (userAccountModel.getAccount_status().isBank_account()) {
                iv_payment.setImageResource(R.drawable.ic_progress_checked);
                percent+=25;

            } else {
                iv_payment.setColorFilter(ContextCompat.getColor(requireContext(), R.color.grayActive), android.graphics.PorterDuff.Mode.SRC_IN);
            }


            if (userAccountModel.getAccount_status().isSkills()) {
                percent+=25;
                iv_skills.setImageResource(R.drawable.ic_progress_checked);
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
