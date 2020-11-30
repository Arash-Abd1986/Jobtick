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
import com.jobtick.utils.ResizeWidthAnimation;

/**
 * PP means poster cancels, poster see this activity.
 */
public class PPCancellationSummaryActivity extends AbstractCancellationSummaryActivity implements View.OnTouchListener {

    private TextView securedPayment;
    private TextView respondHeader;
    private View cancellationButtons;
    private MaterialButton withdraw;
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

        withdraw.setOnTouchListener(this);
    }
}