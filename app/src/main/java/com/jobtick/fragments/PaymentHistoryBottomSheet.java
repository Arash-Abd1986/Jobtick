package com.jobtick.fragments;

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
import com.jobtick.models.payments.Method;
import com.jobtick.models.payments.PaymentHistory;
import com.jobtick.utils.StringUtils;

public class PaymentHistoryBottomSheet extends BottomSheetDialogFragment {

    private PaymentHistory paymentHistory;

    public static PaymentHistoryBottomSheet newInstance(PaymentHistory paymentHistory) {
        Bundle args = new Bundle();
        args.putSerializable("paymentHistoryKEY", paymentHistory);
        PaymentHistoryBottomSheet fragment = new PaymentHistoryBottomSheet();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.ThemeOverlay_BottomSheetDialog);
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            paymentHistory = (PaymentHistory) bundle.getSerializable("paymentHistoryKEY");
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bottom_sheet_paid, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView amount = view.findViewById(R.id.tv_amount);
        TextView wallet = view.findViewById(R.id.tv_wallet);
        TextView fee = view.findViewById(R.id.tv_service_fee);
        TextView taxTxt = view.findViewById(R.id.tv_gst);
        TextView taxTypeTxt = view.findViewById(R.id.taxTypeTxt);
        TextView date = view.findViewById(R.id.tv_date);
        TextView time = view.findViewById(R.id.tv_time);
        TextView status = view.findViewById(R.id.tv_status);
        TextView name = view.findViewById(R.id.credit_expiry_date);
        TextView creditCardNumber = view.findViewById(R.id.credit_account_number);
        TextView typeCardTxt = view.findViewById(R.id.typeCardTxt);

        amount.setText(StringUtils.getPriceTxt(paymentHistory.getAmount()));

        for (Method tempMethod :
                paymentHistory.getMethod()) {
            if (tempMethod.getType().equals("creditwallet")) {
                wallet.setText(StringUtils.getPriceTxt(tempMethod.getAmount()));
                wallet.setVisibility(View.VISIBLE);
            } else if (tempMethod.getType().equals("card")) {
                creditCardNumber.setVisibility(View.VISIBLE);
                creditCardNumber.setText(tempMethod.getInformation().getCardNumber());
                typeCardTxt.setText(tempMethod.getInformation().getBrand());
            }
        }

        fee.setText(StringUtils.getPriceTxt(paymentHistory.getPlatformFee()));
        taxTxt.setText(StringUtils.getPriceTxt(paymentHistory.getTaxRate()));
        taxTypeTxt.setText(paymentHistory.getTaxType() + "(10%)");

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
