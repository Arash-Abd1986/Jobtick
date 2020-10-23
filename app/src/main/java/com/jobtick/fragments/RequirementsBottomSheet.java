package com.jobtick.fragments;

import android.app.ProgressDialog;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.jobtick.R;
import com.jobtick.activities.ActivityBase;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RequirementsBottomSheet extends BottomSheetDialogFragment {

    protected ProgressDialog pDialog;
    private ImageView img,credit,map,calender,phone;
    public RequirementsBottomSheet() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bottom_sheet_requirements, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        selectImageBtn();
        img = view.findViewById(R.id.img_requirement);
         credit=view.findViewById(R.id.credit_requirement);
         map=view.findViewById(R.id.map_requirement);
         calender=view.findViewById(R.id.calender_requirement);
         phone=view.findViewById(R.id.phone_requirement);
        img.setOnClickListener(v -> selectImageBtn());
        credit.setOnClickListener(v -> selectCreditBtn());
        map.setOnClickListener(v -> selectMapBtn());
        calender.setOnClickListener(v -> selectCalenderBtn());
        phone.setOnClickListener(v -> selectPhoneBtn());
    }


    private void selectImageBtn() {
        ImageReqFragment fragment = ImageReqFragment.newInstance();
        getChildFragmentManager().
                beginTransaction().
                replace(R.id.requirements_layout, fragment).
                commitAllowingStateLoss();

    }

    private void selectCreditBtn() {
        CreditReqFragment fragment = CreditReqFragment.newInstance();
        getChildFragmentManager().
                beginTransaction().
                replace(R.id.requirements_layout, fragment).
                commitAllowingStateLoss();
    }

    private void selectMapBtn() {

        MapReqFragment fragment = MapReqFragment.newInstance();
        getChildFragmentManager().
                beginTransaction().
                replace(R.id.requirements_layout, fragment).
                commitAllowingStateLoss();
    }
    private void selectCalenderBtn() {

        CalenderReqFragment fragment = CalenderReqFragment.newInstance();
        getChildFragmentManager().
                beginTransaction().
                replace(R.id.requirements_layout, fragment).
                commitAllowingStateLoss();
    }

    private void selectPhoneBtn() {
        PhoneReqFragment fragment = PhoneReqFragment.newInstance();
        getChildFragmentManager().
                beginTransaction().
                replace(R.id.requirements_layout, fragment).
                commitAllowingStateLoss();
    }
    public void changeFragment(int key) {
        switch (key) {
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

}