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
import com.jobtick.models.UserAccountModel;
import com.jobtick.models.payments.PaymentHistory;
import com.jobtick.utils.SessionManager;

public class PaymentPaidBottomSheet extends BottomSheetDialogFragment {
    private SessionManager sessionManager;
    private Context context;
    private TextView amount,wallet,fee,gst,date,time,status,name;
private PaymentHistory paymentHistory;

    public PaymentPaidBottomSheet(PaymentHistory paymentHistory) {
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
        return inflater.inflate(R.layout.bottom_sheet_paid, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sessionManager = new SessionManager(getContext());
        amount = view.findViewById(R.id.tv_amount);
        wallet = view.findViewById(R.id.tv_wallet);
        fee = view.findViewById(R.id.tv_service_fee);
        gst = view.findViewById(R.id.tv_gst);
        date = view.findViewById(R.id.tv_date);
        time = view.findViewById(R.id.tv_time);
        status = view.findViewById(R.id.tv_status);
        name=view.findViewById(R.id.credit_account_name);
        amount.setText("$ " + paymentHistory.getAmount());
        wallet.setText("$ " + paymentHistory.getDiscount());
        fee.setText("$ " + paymentHistory.getPlatformFee());
        gst.setText("$ " + paymentHistory.getTaxRate());

        String times = paymentHistory.getCreatedAt();
        String[] parts = times.split("T");
        String part1 = parts[0];
        String part2 = parts[1];
        date.setText(part1);
        time.setText(part2.split("\\.")[0]);
        name.setText(paymentHistory.getTask().getWorker().getName());
        status.setText(paymentHistory.getStatus());

    }

}
