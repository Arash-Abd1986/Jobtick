package com.jobtick.android.fragments.others;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.jobtick.android.R;
import com.jobtick.android.fragments.AbstractStateExpandedBottomSheet;
import com.jobtick.android.fragments.CashOutBottomSheet;
import com.jobtick.android.models.CreditCardModel;
import com.jobtick.android.utils.Constant;
import com.jobtick.android.utils.SessionManager;

import java.util.Timer;
import java.util.TimerTask;


public class AddCouponFragment extends AbstractStateExpandedBottomSheet {
    SessionManager sessionManager;

    public static AddCouponFragment newInstance() {
        Bundle args = new Bundle();
        AddCouponFragment fragment = new AddCouponFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.ThemeOverlay_BottomSheetDialog);
    }

    Button btnVerify;
    TextView tvError;
    EditText etPromoCode;
    ImageView ivState;
    ProgressBar pbLoading;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_coupon, container, false);
        sessionManager = new SessionManager(getActivity());
        setupPromotionCodeChecker(view);
        btnVerify.setOnClickListener(v -> {
            verify();
        });

        return view;
    }

    private void setupPromotionCodeChecker(View view) {
        btnVerify = view.findViewById(R.id.btnVerify);
        tvError = view.findViewById(R.id.tvError);
        etPromoCode = view.findViewById(R.id.etPromoCode);
        ivState = view.findViewById(R.id.ivState);
        pbLoading = view.findViewById(R.id.pbLoading);

        etPromoCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(etPromoCode.getText().length()>2){
                    ivState.setVisibility(View.GONE);
                    pbLoading.setVisibility(View.VISIBLE);
                }else{
                    ivState.setVisibility(View.VISIBLE);
                    pbLoading.setVisibility(View.GONE);
                }
            }
            private Timer timer =new Timer();

            @Override
            public void afterTextChanged(Editable s) {
                timer.cancel();
                timer =new Timer();
                long delay = 3000L;
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        if(etPromoCode.getText().length()>2){
                            ivState.setVisibility(View.GONE);
                            pbLoading.setVisibility(View.VISIBLE);
                            checkPromoCode();
                        }
                        else{
                            ivState.setVisibility(View.VISIBLE);
                            pbLoading.setVisibility(View.GONE);
                        }
                    }
                }, delay);
            }
        });

    }

    private void checkPromoCode() {

    }

    private void verify() {

    }
}