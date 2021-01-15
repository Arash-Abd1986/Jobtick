package com.jobtick.android.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.jobtick.android.R;
import com.jobtick.android.activities.MakeAnOfferActivity;
import com.jobtick.android.activities.TaskDetailsActivity;
import com.jobtick.android.models.BankAccountModel;
import com.jobtick.android.models.BillingAdreessModel;
import com.jobtick.android.models.TaskModel;
import com.jobtick.android.models.UserAccountModel;
import com.jobtick.android.utils.Constant;
import com.jobtick.android.utils.SessionManager;

import java.util.HashMap;

public class TickerRequirementsBottomSheet extends AbstractStateExpandedBottomSheet {

    protected ProgressDialog pDialog;
    private ImageView profileBtn, bankAccountBtn, billingAddressBtn, birthDayBtn, phoneNumberBtn;
    private SessionManager sessionManager;
    private UserAccountModel userAccountModel;
    private BankAccountModel bankAccountModel;
    private BillingAdreessModel billingAdreessModel;
    private HashMap<Requirement, Boolean> state;
    private Context context;

    private NoticeListener listener;

    public TickerRequirementsBottomSheet() {
    }

    public static TickerRequirementsBottomSheet newInstance(HashMap<Requirement, Boolean> state) {
        Bundle args = new Bundle();
        args.putSerializable(Constant.STATE_STRIPE_TICKER, state);
        TickerRequirementsBottomSheet fragment = new TickerRequirementsBottomSheet();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.ThemeOverlay_BottomSheetDialog);
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
            state = (HashMap<Requirement, Boolean>) getArguments().getSerializable(Constant.STATE_STRIPE_TICKER);
        }
        userAccountModel = ((TaskDetailsActivity) requireActivity()).userAccountModel;
        bankAccountModel = ((TaskDetailsActivity) requireActivity()).bankAccountModel;
        billingAdreessModel = ((TaskDetailsActivity) requireActivity()).billingAdreessModel;
        profileBtn = view.findViewById(R.id.img_requirement);
        bankAccountBtn = view.findViewById(R.id.credit_requirement);
        billingAddressBtn = view.findViewById(R.id.map_requirement);
        birthDayBtn = view.findViewById(R.id.calender_requirement);
        phoneNumberBtn = view.findViewById(R.id.phone_requirement);


        initUi();
        changeFragment(0);
    }

    private void initUi(){
        if(state.get(Requirement.Profile))
            profileBtn.setVisibility(View.GONE);
        else
            profileBtn.setVisibility(View.VISIBLE);

        if(state.get(Requirement.BankAccount))
            bankAccountBtn.setVisibility(View.GONE);
        else
            bankAccountBtn.setVisibility(View.VISIBLE);

        if(state.get(Requirement.BillingAddress))
            billingAddressBtn.setVisibility(View.GONE);
        else
            billingAddressBtn.setVisibility(View.VISIBLE);

        if(state.get(Requirement.BirthDate))
            birthDayBtn.setVisibility(View.GONE);
        else
            birthDayBtn.setVisibility(View.VISIBLE);

        if(state.get(Requirement.PhoneNumber))
            phoneNumberBtn.setVisibility(View.GONE);
        else
            phoneNumberBtn.setVisibility(View.VISIBLE);
    }


    private void selectImageBtn() {
        profileBtn.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.bg_white_shape));
        billingAddressBtn.setBackground(ContextCompat.getDrawable(getContext(), R.color.transparent));
        bankAccountBtn.setBackground(ContextCompat.getDrawable(getContext(), R.color.transparent));
        birthDayBtn.setBackground(ContextCompat.getDrawable(getContext(), R.color.transparent));
        phoneNumberBtn.setBackground(ContextCompat.getDrawable(getContext(), R.color.transparent));
        profileBtn.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_image_primary));
        bankAccountBtn.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_credit_card_grey));
        birthDayBtn.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_calendar_grey_24dp));
        phoneNumberBtn.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_phone_grey));
        billingAddressBtn.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_map_pin_grey_24dp));

        ProfileReqFragment fragment = ProfileReqFragment.newInstance();
        getChildFragmentManager().
                beginTransaction().
                replace(R.id.requirements_layout, fragment).
                commitAllowingStateLoss();

    }

    private void selectCreditBtn() {
        profileBtn.setBackground(ContextCompat.getDrawable(getContext(), R.color.transparent));
        billingAddressBtn.setBackground(ContextCompat.getDrawable(getContext(), R.color.transparent));
        bankAccountBtn.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.bg_white_shape));
        birthDayBtn.setBackground(ContextCompat.getDrawable(getContext(), R.color.transparent));
        phoneNumberBtn.setBackground(ContextCompat.getDrawable(getContext(), R.color.transparent));
        profileBtn.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_image_cinereous));
        bankAccountBtn.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_credit_card_primary));
        birthDayBtn.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_calendar_grey_24dp));
        phoneNumberBtn.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_phone_grey));
        billingAddressBtn.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_map_pin_grey_24dp));

        AddBankAccountReqFragment fragment = AddBankAccountReqFragment.newInstance();
        getChildFragmentManager().
                beginTransaction().
                replace(R.id.requirements_layout, fragment).
                commitAllowingStateLoss();
    }

    private void selectMapBtn() {
        profileBtn.setBackground(ContextCompat.getDrawable(getContext(), R.color.transparent));
        billingAddressBtn.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.bg_white_shape));
        bankAccountBtn.setBackground(ContextCompat.getDrawable(getContext(), R.color.transparent));
        birthDayBtn.setBackground(ContextCompat.getDrawable(getContext(), R.color.transparent));
        phoneNumberBtn.setBackground(ContextCompat.getDrawable(getContext(), R.color.transparent));
        profileBtn.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_image_cinereous));
        bankAccountBtn.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_credit_card_cinereous));
        birthDayBtn.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_calendar_grey_24dp));
        phoneNumberBtn.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_phone_grey));
        billingAddressBtn.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_map_pin_primary));


        AddBillingReqFragment fragment = AddBillingReqFragment.newInstance();
        getChildFragmentManager().
                beginTransaction().
                replace(R.id.requirements_layout, fragment).
                commitAllowingStateLoss();
    }

    private void selectCalenderBtn() {
        profileBtn.setBackground(ContextCompat.getDrawable(context, R.color.transparent));
        billingAddressBtn.setBackground(ContextCompat.getDrawable(context, R.color.transparent));
        bankAccountBtn.setBackground(ContextCompat.getDrawable(context, R.color.transparent));
        birthDayBtn.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_white_shape));
        phoneNumberBtn.setBackground(ContextCompat.getDrawable(context, R.color.transparent));
        profileBtn.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_image_cinereous));
        bankAccountBtn.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_credit_card_cinereous));
        birthDayBtn.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_calendar_primary));
        phoneNumberBtn.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_phone_grey));
        billingAddressBtn.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_map_pin_cinereous));

        DobReqFragment fragment = DobReqFragment.newInstance();
        getChildFragmentManager().
                beginTransaction().
                replace(R.id.requirements_layout, fragment).
                commitAllowingStateLoss();
    }

    private void selectPhoneBtn() {

        profileBtn.setBackground(ContextCompat.getDrawable(getContext(), R.color.transparent));
        billingAddressBtn.setBackground(ContextCompat.getDrawable(getContext(), R.color.transparent));
        bankAccountBtn.setBackground(ContextCompat.getDrawable(getContext(), R.color.transparent));
        birthDayBtn.setBackground(ContextCompat.getDrawable(getContext(), R.color.transparent));
        phoneNumberBtn.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.bg_white_shape));
        profileBtn.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_image_cinereous));
        bankAccountBtn.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_credit_card_cinereous));
        birthDayBtn.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_calendar_cinereous));
        phoneNumberBtn.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_phone_primary));
        billingAddressBtn.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_map_pin_cinereous));

        PhoneReqFragment fragment = PhoneReqFragment.newInstance();
        getChildFragmentManager().
                beginTransaction().
                replace(R.id.requirements_layout, fragment).
                commitAllowingStateLoss();
    }

    public void changeFragment(int key) {
        switch (key) {
            case 0:
                if(!state.get(Requirement.Profile))
                    selectImageBtn();
                else {
                    key++;
                    changeFragment(key);
                }
                break;
            case 1:
                if(!state.get(Requirement.BankAccount))
                    selectCreditBtn();
                else{
                    key++;
                    changeFragment(key);
                }
                break;
            case 2:
                if(!state.get(Requirement.BillingAddress))
                    selectMapBtn();
                else{
                    key++;
                    changeFragment(key);
                }
                break;
            case 3:
                if(!state.get(Requirement.BirthDate))
                    selectCalenderBtn();
                else{
                    key++;
                    changeFragment(key);
                }
                break;
            case 4:
                if(!state.get(Requirement.PhoneNumber))
                    selectPhoneBtn();
                else{
                    key++;
                    changeFragment(key);
                }
                break;
            case 5:
                listener.onStripeRequirementFilled();
                dismiss();
                TaskModel taskModel = TaskDetailsActivity.taskModel;
                if (taskModel.getMusthave().size() == 0) {
                    Intent intent = new Intent(getContext(), MakeAnOfferActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putInt("id", taskModel.getId());
                    bundle.putInt("budget", taskModel.getBudget());
                    intent.putExtras(bundle);
                    startActivity(intent);

                } else {
                    ((TaskDetailsActivity) requireActivity()).showRequirementDialog();
                }
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        for (Fragment fragment : getChildFragmentManager().getFragments())
            if(fragment instanceof ProfileReqFragment)
                fragment.onActivityResult(requestCode, resultCode, data);
    }

    public enum Requirement{
        Profile,
        BankAccount,
        BillingAddress,
        BirthDate,
        PhoneNumber
    }

    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            listener = (NoticeListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(this.toString()
                    + " must implement NoticeListener");
        }
    }

    public interface NoticeListener{
        public void onStripeRequirementFilled();
    }
}