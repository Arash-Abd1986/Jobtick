package com.jobtick.fragments;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.jobtick.R;
import com.jobtick.activities.ActivityBase;
import com.jobtick.activities.OnboardActivity;
import com.jobtick.adapers.ReqAdapter;
import com.jobtick.adapers.SectionsPagerAdapter;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;

import static androidx.fragment.app.FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT;

public class RequirementsBottomSheet extends BottomSheetDialogFragment {

    ReqAdapter adapter;
    int mTab;
    Context context;
    @BindView(R.id.view_pager)
    ViewPager viewPager;
    @BindView(R.id.img_requirement)
    ImageView img;
    @BindView(R.id.calender_requirement)
    ImageView calender;
    @BindView(R.id.map_requirement)
    ImageView map;
    @BindView(R.id.credit_requirement)
    ImageView credit;
    @BindView(R.id.phone_requirement)
    ImageView phone;

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


    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
       initComponent();

    }

    private void initComponent() {
        setupViewPager(viewPager);
        viewPager.addOnPageChangeListener(viewPagerPageChangeListener);
        viewPager.setCurrentItem(0);
        viewPager.setOffscreenPageLimit(5);
        selectImageBtn();
        credit.setEnabled(false);
        map.setEnabled(false);
        calender.setEnabled(false);
        phone.setEnabled(false);
        img.setSelected(true);
    }

    private void setupViewPager(ViewPager viewPager) {
       // adapter = new ReqAdapter(getS);
        //  viewPager.setAdapter(adapter);
    }

    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageSelected(final int position) {
            switch (position) {
                case 0:
                    selectImageBtn();
                    break;
                case 1:
                    selectCreditBtn();
                    break;
                case 2:
                    selectMapBtn();
                    break;
                case 3:
                    selectCalenderBtn();
                    break;
                case 4:
                    selectPhoneBtn();
                    break;
            }

        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }
    };

    @OnClick({R.id.img_requirement, R.id.credit_requirement, R.id.map_requirement, R.id.calender_requirement, R.id.phone_requirement})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.img_requirement:
                viewPager.setCurrentItem(0);
                selectImageBtn();
                break;
            case R.id.credit_requirement:
                viewPager.setCurrentItem(1);
                selectCreditBtn();
                break;
            case R.id.map_requirement:
                viewPager.setCurrentItem(2);
                selectMapBtn();
                break;
            case R.id.calender_requirement:
                viewPager.setCurrentItem(3);
                selectCalenderBtn();
                break;
            case R.id.phone_requirement:
                viewPager.setCurrentItem(4);
                selectPhoneBtn();
                break;
        }
    }

    private void selectImageBtn() {
        ColorStateList csl_primary = AppCompatResources.getColorStateList(context, R.color.colorPrimary);
        img.setImageTintList(csl_primary);
        ColorStateList csl_grey = AppCompatResources.getColorStateList(context, R.color.greyC4C4C4);
        credit.setImageTintList(csl_grey);
        map.setImageTintList(csl_grey);
        calender.setImageTintList(csl_grey);
        phone.setImageTintList(csl_grey);

    }

    private void selectMapBtn() {
        ColorStateList csl_primary = AppCompatResources.getColorStateList(context, R.color.colorPrimary);
        map.setImageTintList(csl_primary);
        ColorStateList csl_grey = AppCompatResources.getColorStateList(context, R.color.greyC4C4C4);
        credit.setImageTintList(csl_grey);
        img.setImageTintList(csl_grey);
        calender.setImageTintList(csl_grey);
        phone.setImageTintList(csl_grey);
    }

    private void selectCreditBtn() {
        ColorStateList csl_primary = AppCompatResources.getColorStateList(context, R.color.colorPrimary);
        credit.setImageTintList(csl_primary);
        ColorStateList csl_grey = AppCompatResources.getColorStateList(context, R.color.greyC4C4C4);
        img.setImageTintList(csl_grey);
        map.setImageTintList(csl_grey);
        calender.setImageTintList(csl_grey);
        phone.setImageTintList(csl_grey);
    }

    private void selectCalenderBtn() {
        ColorStateList csl_primary = AppCompatResources.getColorStateList(context, R.color.colorPrimary);
        calender.setImageTintList(csl_primary);
        ColorStateList csl_grey = AppCompatResources.getColorStateList(context, R.color.greyC4C4C4);
        credit.setImageTintList(csl_grey);
        map.setImageTintList(csl_grey);
        img.setImageTintList(csl_grey);
        phone.setImageTintList(csl_grey);
    }

    private void selectPhoneBtn() {
        ColorStateList csl_primary = AppCompatResources.getColorStateList(context, R.color.colorPrimary);
        phone.setImageTintList(csl_primary);
        ColorStateList csl_grey = AppCompatResources.getColorStateList(context, R.color.greyC4C4C4);
        credit.setImageTintList(csl_grey);
        map.setImageTintList(csl_grey);
        calender.setImageTintList(csl_grey);
        img.setImageTintList(csl_grey);
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