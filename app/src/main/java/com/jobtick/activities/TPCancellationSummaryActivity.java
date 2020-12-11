package com.jobtick.activities;


import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.jobtick.R;

/**
 * TP means ticker cancels, poster see this activity.
 */
public class TPCancellationSummaryActivity extends AbstractCancellationSummaryActivity {

    private TextView securedPayment;
    private TextView respondHeader;
    private View cancellationButtons;
    private Button accept;
    private Button decline;
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

        withdrawContainer = findViewById(R.id.withdraw_container);
        extraSpace = findViewById(R.id.extra_needed_space);

        init();
    }

    private void init(){
        securedPayment.setVisibility(View.VISIBLE);
        respondHeader.setText(getBaseContext().getString(R.string.cancellation_respond_you));

        if(taskModel.getCancellation() != null && taskModel.getCancellation().getStatus().equals("pending")){
            cancellationButtons.setVisibility(View.VISIBLE);
            withdrawContainer.setVisibility(View.GONE);
            extraSpace.setVisibility(View.GONE);
        }

        decline.setOnClickListener(v -> {
            decline();
        });

        accept.setOnClickListener(v -> {
            accept();
        });
    }

    @Override
    protected String getUserType() {
        return "poster";
    }
}