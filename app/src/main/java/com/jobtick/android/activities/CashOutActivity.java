package com.jobtick.android.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.jobtick.android.R;
import com.jobtick.android.models.CreditCardModel;
import com.jobtick.android.utils.Constant;

import java.util.Locale;

public class CashOutActivity extends ActivityBase {

    private TextView availableBalance;
    private Button cashout;
    private EditText requestedCashout;

    private CreditCardModel creditCardModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cashout_activity);

        availableBalance = findViewById(R.id.available_balance);
        cashout = findViewById(R.id.cashout);
        requestedCashout = findViewById(R.id.requested_cashout);

        creditCardModel = (CreditCardModel) getIntent().getExtras().get(Constant.CASH_OUT);


        cashout.setOnClickListener(v ->{
            if(!validation()) return;

            requestCashout();
        });

        init();
    }

    private void init(){
    }

    private void requestCashout() {

        //TODO: after api created, do request cashout here


    }

    private boolean validation(){
        if(requestedCashout.getText().length() == 0){
            showToast("Please enter your requested amount.", this);
            return false;
        }
            return true;
    }


}
