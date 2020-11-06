package com.jobtick.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.jobtick.R;
import com.jobtick.activities.MakeAnOfferActivity;
import com.jobtick.activities.PaymentOverviewActivity;
import com.jobtick.activities.TaskDetailsActivity;
import com.jobtick.models.CreditCardModel;
import com.jobtick.models.TaskModel;
import com.jobtick.utils.Constant;
import com.jobtick.utils.SessionManager;

import java.util.HashMap;

public class PosterRequirementsBottomSheet extends BottomSheetDialogFragment {

    private HashMap<Requirement, Boolean> state;

    public PosterRequirementsBottomSheet() {
    }

    public static PosterRequirementsBottomSheet newInstance(HashMap<Requirement, Boolean> state) {
        Bundle args = new Bundle();
        args.putSerializable(Constant.STATE_STRIPE_POSTER, state);
        PosterRequirementsBottomSheet fragment = new PosterRequirementsBottomSheet();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.BottomSheetDialogStyle);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bottom_sheet_requirements_poster, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getActivity() == null) {
            return;
        }
        if (getArguments() != null) {
            state = (HashMap<Requirement, Boolean>) getArguments().getSerializable(Constant.STATE_STRIPE_POSTER);
        }



        initUi();
        changeFragment(0);
    }

    private void initUi(){
        //TODO: when some new fragments added
//        if(state.get(Requirement.Profile))
//            profileBtn.setVisibility(View.GONE);
//        else
//            profileBtn.setVisibility(View.VISIBLE);
    }


    private void selectCreditCard() {
        //TODO: when some new fragments added
//        profileBtn.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.bg_white_shape));
//        billingAddressBtn.setBackground(ContextCompat.getDrawable(getContext(), R.color.transparent));
//        bankAccountBtn.setBackground(ContextCompat.getDrawable(getContext(), R.color.transparent));
//        birthDayBtn.setBackground(ContextCompat.getDrawable(getContext(), R.color.transparent));
//        phoneNumberBtn.setBackground(ContextCompat.getDrawable(getContext(), R.color.transparent));
//        profileBtn.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_image_primary));
//        bankAccountBtn.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_credit_card_grey));
//        birthDayBtn.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_calendar_grey));
//        phoneNumberBtn.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_phone_grey));
//        billingAddressBtn.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_map_pin_grey));

        CreditCardReqFragment fragment = CreditCardReqFragment.newInstance();
        getChildFragmentManager().
                beginTransaction().
                replace(R.id.requirements_layout, fragment).
                commitAllowingStateLoss();

    }


    public void changeFragment(int key) {
        switch (key) {
            case 0:
                if(!state.get(Requirement.CreditCard))
                    selectCreditCard();
                else {
                    key++;
                    changeFragment(key);
                }
                break;

            case 1:
                ((PaymentOverviewActivity)requireActivity()).getPaymentMethod();
                dismiss();
                break;
        }
    }

    public enum Requirement{
        CreditCard
    }
}