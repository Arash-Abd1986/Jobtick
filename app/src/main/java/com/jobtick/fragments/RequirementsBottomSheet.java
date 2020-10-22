package com.jobtick.fragments;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.jobtick.R;
import com.jobtick.activities.ActivityBase;
import com.jobtick.activities.OnboardActivity;
import com.jobtick.adapers.ReqAdapter;

import java.util.ArrayList;

import butterknife.BindView;

import static androidx.fragment.app.FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT;

class RequirementsBottomSheet extends BottomSheetDialogFragment   {

    ReqAdapter adapter;
    int mTab;
    Context context;
    @BindView(R.id.view_pager)
    ViewPager viewPager;


    public RequirementsBottomSheet() {

    }

    @SuppressLint("RestrictedApi")
    @Override
    public void setupDialog(@NonNull Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        View contentView = View.inflate(getContext(), R.layout.bottom_sheet_requirements, null);
        dialog.setContentView(contentView);
        ((View) contentView.getParent()).setBackgroundColor(Color.TRANSPARENT);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bottom_sheet_requirements, container, false);
        adapter = new ReqAdapter(getChildFragmentManager(),mTab);
        viewPager.setAdapter(adapter);

    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


    }



    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onStop() {
        super.onStop();

    }

}