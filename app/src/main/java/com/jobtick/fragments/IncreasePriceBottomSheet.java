package com.jobtick.fragments;


import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.jobtick.R;
import com.jobtick.widget.ExtendedCommentText;
import com.jobtick.widget.ExtendedEntryText;


public class IncreasePriceBottomSheet extends BottomSheetDialogFragment {

    TextView oldPrice;
    TextView serviceFee;
    TextView receiveMoney;
    ExtendedEntryText newPrice;
    ExtendedCommentText reason;
    Button submit;

    private NoticeListener listener;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_increase_budget, container, false);

        oldPrice = view.findViewById(R.id.old_price);
        serviceFee = view.findViewById(R.id.service_fee);
        receiveMoney = view.findViewById(R.id.receive_money);
        newPrice = view.findViewById(R.id.new_price);
        reason = view.findViewById(R.id.reason);
        submit = view.findViewById(R.id.submit);


        submit.setOnClickListener(v -> {
            if(!validation()) return;

        });
        init();
        return view;
    }

    private void init(){

    }

    private boolean validation(){
        if(newPrice.getText().length() == 0){
            newPrice.setError("Please enter new price");
            return false;
        }
        else if(reason.getText().length() < reason.geteMinSize()){
            newPrice.setError("");
            return false;
        }
        return true;
    }

    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            listener = (IncreasePriceBottomSheet.NoticeListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(this.toString()
                    + " must implement NoticeListener");
        }
    }

    public interface NoticeListener {
        void onSubmitIncreasePrice();
    }
}