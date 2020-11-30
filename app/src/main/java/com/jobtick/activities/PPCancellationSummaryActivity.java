package com.jobtick.activities;


import android.os.Bundle;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;
import com.jobtick.R;

/**
 * PP means poster cancels, poster see this activity.
 */
public class PPCancellationSummaryActivity extends AbstractCancellationSummaryActivity implements View.OnTouchListener {

    private TextView securedPayment;
    private TextView respondHeader;
    private View cancellationButtons;
    private Button accept;
    private Button decline;
    private LinearLayout withdraw;
    private View extraSpace;
    private View withdrawContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        securedPayment = findViewById(R.id.secured_payment);
        respondHeader = findViewById(R.id.cancellation_respond_header);
        securedPayment = findViewById(R.id.secured_payment);
        respondHeader = findViewById(R.id.cancellation_respond_header);
        cancellationButtons = findViewById(R.id.cancellation_buttons);
        accept = findViewById(R.id.cancellation_accept);
        decline = findViewById(R.id.cancellation_decline);

        withdraw = findViewById(R.id.btn_withdraw);
        withdrawContainer = findViewById(R.id.withdraw_container);
        extraSpace = findViewById(R.id.extra_needed_space);

        init();
    }

    private void init(){
        securedPayment.setVisibility(View.VISIBLE);
        respondHeader.setText(getBaseContext().getString(R.string.cancellation_respond_ticker));

        if(taskModel.getCancellation() != null){
            cancellationButtons.setVisibility(View.GONE);
            withdrawContainer.setVisibility(View.VISIBLE);
            extraSpace.setVisibility(View.VISIBLE);
        }

        decline.setOnClickListener(v -> {
            decline();
        });

        accept.setOnClickListener(v -> {
            accept();
        });

        withdraw.setOnClickListener(v -> {

            withdraw();
        });
    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {

        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) v.getLayoutParams();

        //Code to convert height and width in dp.
        int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 35, getResources().getDisplayMetrics());
        int width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 140, getResources().getDisplayMetrics());

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            layoutParams.width = width + 5;
            layoutParams.height = height + 5;
            withdraw.setLayoutParams(layoutParams);
        }
        if (event.getAction() == MotionEvent.ACTION_UP) {
            layoutParams.width = width;
            layoutParams.height = height;
            withdraw.setLayoutParams(layoutParams);
        }

        return false;
    }
}