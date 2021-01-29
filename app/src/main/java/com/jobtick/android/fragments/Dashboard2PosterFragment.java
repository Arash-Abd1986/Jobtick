package com.jobtick.android.fragments;

import android.graphics.Color;

import androidx.core.content.ContextCompat;

import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.jobtick.android.R;

import java.util.ArrayList;

public class Dashboard2PosterFragment extends AbstractDashboard2Fragment {
    public static final int[] CHART_COLORS = {
            Color.rgb(88, 107, 245),Color.rgb(243, 176, 4),Color.rgb(43, 50, 64),
            Color.rgb(255, 86, 48)
    };
    public static Dashboard2PosterFragment newInstance(){
        return new Dashboard2PosterFragment();
    }
    private void setChart() {
        Description desc ;
        Legend L;

        L = chart.getLegend();
        desc = chart.getDescription();
        desc.setText(""); // this is the weirdest way to clear something!!
        L.setEnabled(false);


        YAxis leftAxis = chart.getAxisLeft();
        YAxis rightAxis = chart.getAxisRight();
        XAxis xAxis = chart.getXAxis();

        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextSize(10f);
        xAxis.setDrawAxisLine(true);
        xAxis.setDrawGridLines(false);


        leftAxis.setTextSize(10f);
        leftAxis.setDrawLabels(false);
        leftAxis.setDrawAxisLine(true);
        leftAxis.setDrawGridLines(false);

        rightAxis.setDrawAxisLine(false);
        rightAxis.setDrawGridLines(false);
        rightAxis.setDrawLabels(false);

        BarData data = new BarData(  setDataChart());


        data.setBarWidth(0.9f); // set custom bar width
        chart.setData(data);

        chart.setFitBars(true); // make the x-axis fit exactly all bars
        chart.invalidate(); // refresh
        chart.setScaleEnabled(false);
        chart.setBackground(null);
        chart.setBorderColor(Color.WHITE);
        chart.setGridBackgroundColor(Color.WHITE);
        chart.setBorderColor(Color.WHITE);
        chart.setDoubleTapToZoomEnabled(false);
        chart.setBackgroundColor(Color.TRANSPARENT);
        chart.animateXY(2000, 2000);
        chart.setDrawBorders(false);
        chart.setDescription(desc);
        chart.setDrawValueAboveBar(true);


    }
    public BarDataSet setDataChart()
    {
        ArrayList<BarEntry> entries = new ArrayList<>();

        entries.add(new BarEntry(0f, 30f));
        entries.add(new BarEntry(1f, 80f));
        entries.add(new BarEntry(2f, 60f));
        entries.add(new BarEntry(3f, 50f));
        entries.add(new BarEntry(4f, 70f));
        entries.add(new BarEntry(5f, 60f));


        BarDataSet set = new BarDataSet(entries, "");
        set.setColor(Color.parseColor("#36bb76"));
        set.setValueTextColor(Color.WHITE);
        set.setBarBorderColor(Color.WHITE);
        set.setValueTextColor(Color.parseColor("#36bb76"));
        return set;
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
        txtReleasedMoney.setTitle("Unpaid");
        if (userAccountModel.getPostTaskStatistics() != null && userAccountModel.getPostTaskStatistics().getAssigned() != null) {
            txtAssigend.setValue(userAccountModel.getPostTaskStatistics().getAssigned().toString());
        } else {
            txtAssigend.setValue("0");
        }
        if (userAccountModel.getPostTaskStatistics() != null && userAccountModel.getPostTaskStatistics().getCancelled() != null) {
            extCancelled.setValue(userAccountModel.getPostTaskStatistics().getCancelled().toString());
        } else {
            extCancelled.setValue("0");
        }

        if (userAccountModel.getPosterTier() != null && userAccountModel.getPostTaskStatistics().getUnpaid() != null) {
            txtReleasedMoney.setValue(userAccountModel.getPostTaskStatistics().getUnpaid().toString());
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
            setChart();
        }
    }
}
