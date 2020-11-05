package com.jobtick.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.jobtick.R;
import com.jobtick.models.payments.PaymentHistory;
import com.jobtick.utils.SessionManager;

public class PaymentEarnedBottomSheet extends BottomSheetDialogFragment {
    private SessionManager sessionManager;
    private Context context;

    private TextView amount,fee,gst,date,time,status;
    private PaymentHistory paymentHistory;

    public PaymentEarnedBottomSheet(PaymentHistory paymentHistory) {
            this.paymentHistory = paymentHistory;

    }



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.BottomSheetDialogStyle);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        sessionManager = new SessionManager(getContext());
        return inflater.inflate(R.layout.bottom_sheet_earned, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sessionManager = new SessionManager(getContext());
        amount = view.findViewById(R.id.tv_amount);
        fee = view.findViewById(R.id.tv_service_fee);
        gst = view.findViewById(R.id.tv_gst);
        date = view.findViewById(R.id.tv_date);
        time = view.findViewById(R.id.tv_time);
        status = view.findViewById(R.id.tv_status);
        amount.setText("$ " + paymentHistory.getAmount());
        fee.setText("$ " + paymentHistory.getPlatformFee());
        gst.setText("$ " + paymentHistory.getTaxRate());
        date.setText(paymentHistory.getCreatedAt());
        time.setText(paymentHistory.getCreatedAt());
        status.setText(paymentHistory.getStatus());
    }

}
