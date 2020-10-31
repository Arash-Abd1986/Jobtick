package com.jobtick.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.jobtick.R;
import com.jobtick.activities.MakeAnOfferActivity;
import com.jobtick.activities.TaskDetailsActivity;
import com.jobtick.models.BankAccountModel;
import com.jobtick.models.BillingAdreessModel;
import com.jobtick.models.TaskModel;
import com.jobtick.models.UserAccountModel;
import com.jobtick.utils.Constant;
import com.jobtick.utils.SessionManager;

public class RequirementsBottomSheet extends BottomSheetDialogFragment {

    protected ProgressDialog pDialog;
    private ImageView img, credit, map, calender, phone;
    private SessionManager sessionManager;
    private UserAccountModel userAccountModel;
    private BankAccountModel bankAccountModel;
    private BillingAdreessModel billingAdreessModel;
    private int state = 0;
    private Context context;

    public RequirementsBottomSheet() {
    }

    public static RequirementsBottomSheet newInstance(int i) {
        Bundle args = new Bundle();
        args.putInt(Constant.STATE_STRIP, i);
        RequirementsBottomSheet fragment = new RequirementsBottomSheet();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        sessionManager = new SessionManager(getContext());
        return inflater.inflate(R.layout.bottom_sheet_requirements, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getActivity() == null) {
            return;
        }
        context = getContext();
        if (getArguments() != null) {
            state = getArguments().getInt(Constant.STATE_STRIP);
        }
        userAccountModel = ((TaskDetailsActivity) getActivity()).userAccountModel;
        bankAccountModel = ((TaskDetailsActivity) getActivity()).bankAccountModel;
        billingAdreessModel = ((TaskDetailsActivity) getActivity()).billingAdreessModel;
        img = view.findViewById(R.id.img_requirement);
        credit = view.findViewById(R.id.credit_requirement);
        map = view.findViewById(R.id.map_requirement);
        calender = view.findViewById(R.id.calender_requirement);
        phone = view.findViewById(R.id.phone_requirement);
        img.setOnClickListener(v -> {
            selectImageBtn();
        });
        credit.setOnClickListener(v -> {
            if (handleState() > 0) {
                selectCreditBtn();
            }
        });
        map.setOnClickListener(v -> {
            if (handleState() > 1) {
                selectMapBtn();
            }
        });
        calender.setOnClickListener(v -> {
            if (handleState() > 2) {
                selectCalenderBtn();
            }
        });
        phone.setOnClickListener(v -> {
            if (handleState() > 3) {
                selectPhoneBtn();
            }
        });


        changeFragment(state);
    }

    private int handleState() {
        if (userAccountModel != null) {
            if (userAccountModel.getAvatar() == null || userAccountModel.getAvatar().getUrl().equals("")) {
                return 0;
            } else if (bankAccountModel == null || bankAccountModel.getData() == null || bankAccountModel.getData().getAccount_name().equals("") || bankAccountModel.getData().getAccount_number().equals("")) {
                return 1;
            } else if (billingAdreessModel == null || billingAdreessModel.getData() == null || billingAdreessModel.getData().getLocation().equals("") || billingAdreessModel.getData().getPost_code().equals("")) {
                return 2;
            } else if (userAccountModel.getDob() == null || userAccountModel.getDob().equals("")) {
                return 3;
            } else if (userAccountModel.getMobile().equals("") || userAccountModel.getMobileVerifiedAt().equals("")) {
                return 4;
            } else {
                return 5;
            }
        } else {
            return 0;
        }
    }

    private void selectImageBtn() {
        img.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.bg_white_shape));
        map.setBackground(ContextCompat.getDrawable(getContext(), R.color.transparent));
        credit.setBackground(ContextCompat.getDrawable(getContext(), R.color.transparent));
        calender.setBackground(ContextCompat.getDrawable(getContext(), R.color.transparent));
        phone.setBackground(ContextCompat.getDrawable(getContext(), R.color.transparent));
        img.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_image_primary));
        credit.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_credit_card));
        calender.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_calendar));
        phone.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_phone));
        map.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_map_pin));

        ImageReqFragment fragment = ImageReqFragment.newInstance();
        getChildFragmentManager().
                beginTransaction().
                replace(R.id.requirements_layout, fragment).
                commitAllowingStateLoss();

    }

    private void selectCreditBtn() {
        img.setBackground(ContextCompat.getDrawable(getContext(), R.color.transparent));
        map.setBackground(ContextCompat.getDrawable(getContext(), R.color.transparent));
        credit.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.bg_white_shape));
        calender.setBackground(ContextCompat.getDrawable(getContext(), R.color.transparent));
        phone.setBackground(ContextCompat.getDrawable(getContext(), R.color.transparent));
        img.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_image));
        credit.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_credit_card_primary));
        calender.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_calendar));
        phone.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_phone));
        map.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_map_pin));

        CreditReqFragment fragment = CreditReqFragment.newInstance();
        getChildFragmentManager().
                beginTransaction().
                replace(R.id.requirements_layout, fragment).
                commitAllowingStateLoss();
    }

    private void selectMapBtn() {
        img.setBackground(ContextCompat.getDrawable(getContext(), R.color.transparent));
        map.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.bg_white_shape));
        credit.setBackground(ContextCompat.getDrawable(getContext(), R.color.transparent));
        calender.setBackground(ContextCompat.getDrawable(getContext(), R.color.transparent));
        phone.setBackground(ContextCompat.getDrawable(getContext(), R.color.transparent));
        img.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_image));
        credit.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_credit_card));
        calender.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_calendar));
        phone.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_phone));
        map.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_map_pin_primary));


        MapReqFragment fragment = MapReqFragment.newInstance();
        getChildFragmentManager().
                beginTransaction().
                replace(R.id.requirements_layout, fragment).
                commitAllowingStateLoss();
    }

    private void selectCalenderBtn() {
        img.setBackground(ContextCompat.getDrawable(context, R.color.transparent));
        map.setBackground(ContextCompat.getDrawable(context, R.color.transparent));
        credit.setBackground(ContextCompat.getDrawable(context, R.color.transparent));
        calender.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_white_shape));
        phone.setBackground(ContextCompat.getDrawable(context, R.color.transparent));
        img.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_image));
        credit.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_credit_card));
        calender.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_calendar_primary));
        phone.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_phone));
        map.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_map_pin));

        CalenderReqFragment fragment = CalenderReqFragment.newInstance();
        getChildFragmentManager().
                beginTransaction().
                replace(R.id.requirements_layout, fragment).
                commitAllowingStateLoss();
    }

    private void selectPhoneBtn() {

        img.setBackground(ContextCompat.getDrawable(getContext(), R.color.transparent));
        map.setBackground(ContextCompat.getDrawable(getContext(), R.color.transparent));
        credit.setBackground(ContextCompat.getDrawable(getContext(), R.color.transparent));
        calender.setBackground(ContextCompat.getDrawable(getContext(), R.color.transparent));
        phone.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.bg_white_shape));
        img.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_image));
        credit.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_credit_card));
        calender.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_calendar));
        phone.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_phone_primary));
        map.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_map_pin));

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
            case 5:
                TaskModel taskModel = ((TaskDetailsActivity) getActivity()).taskModel;
                if (taskModel.getMusthave().size() == 0) {
                    Intent intent = new Intent(getContext(), MakeAnOfferActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putInt("id", taskModel.getId());
                    bundle.putInt("budget", taskModel.getBudget());
                    intent.putExtras(bundle);
                    startActivity(intent);

                } else {
                    ((TaskDetailsActivity) getActivity()).showRequirementDialog();
                }
                break;
        }
    }
}