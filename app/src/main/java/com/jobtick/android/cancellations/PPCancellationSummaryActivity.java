package com.jobtick.android.cancellations;


import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;
import com.jobtick.android.R;
import com.jobtick.android.activities.AbstractCancellationSummaryActivity;

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

        if(taskModel.getCancellation() != null && taskModel.getCancellation().getStatus().equals("pending")){
            cancellationButtons.setVisibility(View.GONE);
            withdrawContainer.setVisibility(View.VISIBLE);
            extraSpace.setVisibility(View.VISIBLE);
        }

        withdraw.setOnTouchListener(this);
    }

    @Override
    protected String getUserType() {
        return UserType.POSTER;
    }
}