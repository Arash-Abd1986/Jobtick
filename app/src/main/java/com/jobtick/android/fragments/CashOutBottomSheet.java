package com.jobtick.android.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.jobtick.android.R;
import com.jobtick.android.activities.ActivityBase;
import com.jobtick.android.activities.CashOutActivity;
import com.jobtick.android.models.CreditCardModel;
import com.jobtick.android.utils.Constant;
import com.jobtick.android.utils.SessionManager;

import java.util.Locale;


public class CashOutBottomSheet extends AbstractStateExpandedBottomSheet {


    private Button button;
    private TextView myBalance;

    private CreditCardModel creditCardModel;


    public CashOutBottomSheet(){

    }

    public static CashOutBottomSheet newInstance(CreditCardModel creditCardModel) {
        Bundle args = new Bundle();
        args.putParcelable(Constant.CASH_OUT, creditCardModel);
        CashOutBottomSheet fragment = new CashOutBottomSheet();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.ThemeOverlay_BottomSheetDialog);

        if(getArguments() != null)
            creditCardModel = getArguments().getParcelable(Constant.CASH_OUT);

    }
    SessionManager sessionManager;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cashout_bottom_sheet, container, false);
        button = view.findViewById(R.id.btn_cashout);
        myBalance = view.findViewById(R.id.my_balance);
        sessionManager = new SessionManager(getActivity());

        button.setOnClickListener(v -> {
            cashOut();
        });

        return view;
    }

    private void cashOut() {
        String link = "https://www.jobtick.com/contact?email="+sessionManager.getUserAccount().getEmail()+"&type=cashout";
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
        startActivity(browserIntent);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
    }

    private void init(){
        if (creditCardModel != null && creditCardModel.getData() != null && creditCardModel.getData().get(1).getWallet() != null) {
            myBalance.setText(String.format(Locale.ENGLISH, "$ %d", creditCardModel.getData().get(1).getWallet().getBalance()));
        }
    }

    private boolean validation(){
        if(creditCardModel.getData().get(1).getWallet().getBalance() == 0){
            ((ActivityBase)requireActivity()).showToast("Nothing to cashout!", getContext());
            return false;
        }
        return true;
    }
}
