package com.jobtick.android.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.jobtick.android.R;
import com.jobtick.android.models.payments.PaymentHistory;
import com.jobtick.android.utils.TimeHelper;

import java.util.Locale;

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
            try {
                paymentHistory = (PaymentHistory) bundle.getSerializable("paymentHistoryKEY");
            }catch (Exception ignored){

            }
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

        TextView title = view.findViewById(R.id.title);
        TextView typeCardTxt = view.findViewById(R.id.typeCardTxt);
        TextView creditAccountTitle = view.findViewById(R.id.credit_account_title);
        TextView creditAccountNumber = view.findViewById(R.id.credit_account_number);
        TextView bsbTitle = view.findViewById(R.id.bsb_title);
        TextView bsbNumber = view.findViewById(R.id.bsb_number);

        TextView amount = view.findViewById(R.id.tv_amount);
        TextView wallet = view.findViewById(R.id.tv_wallet);
        TextView walletTitle = view.findViewById(R.id.tv_wallet_title);
        TextView fee = view.findViewById(R.id.tv_service_fee);
        TextView taxTxt = view.findViewById(R.id.tv_gst);

        TextView date = view.findViewById(R.id.tv_date);
        TextView time = view.findViewById(R.id.tv_time);
        TextView status = view.findViewById(R.id.tv_status);

        View cardBox = view.findViewById(R.id.card_box);

        if(paymentHistory.getType().equals("debit")){
            //paid or for poster
            title.setText(R.string.paid_details);
            typeCardTxt.setText(paymentHistory.getMethod().get(0).getInformation().getBrand());
            creditAccountTitle.setText(R.string.card_number);
            bsbTitle.setVisibility(View.GONE);
            bsbNumber.setVisibility(View.GONE);
            creditAccountNumber.setText(paymentHistory.getMethod().get(0).getInformation().getCardNumber());
            //TODO: set wallet info, I didn't get this value on documentation
            wallet.setText("$0.0");

        }else if(paymentHistory.getType().equals("credit")){
            //earned or for ticker
            title.setText(R.string.earned_details);
            typeCardTxt.setText(paymentHistory.getMethod().get(0).getInformation().getAccountHolderName());
            creditAccountTitle.setText(R.string.account_number);
            wallet.setVisibility(View.GONE);
            walletTitle.setVisibility(View.GONE);


            creditAccountNumber.setText(String.format("**** **** **** %s",
                    paymentHistory.getMethod().get(0).getInformation().getAccountNumberLastFour()));
            bsbNumber.setText(paymentHistory.getMethod().get(0).getInformation().getRoutingNumber());

        }else{
            throw new IllegalArgumentException("this type of payment history is not debit or credit.");
        }

        amount.setText(String.format(Locale.ENGLISH, "$%s", paymentHistory.getAmount()));
        fee.setText(String.format(Locale.ENGLISH, "$%s", paymentHistory.getPlatformFee()));
        taxTxt.setText(String.format(Locale.ENGLISH, "$%s", paymentHistory.getTax_amount_on_fee()));
        date.setText(TimeHelper.convertToShowJustDateFormat(paymentHistory.getCreatedAt()));
        time.setText(TimeHelper.convertToShowJustTimeFormat(paymentHistory.getCreatedAt()));
        status.setText(paymentHistory.getStatus());

    }
}
