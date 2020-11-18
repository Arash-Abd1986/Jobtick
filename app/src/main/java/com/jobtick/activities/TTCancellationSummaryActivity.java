package com.jobtick.activities;


import android.os.Bundle;
import android.view.View;

/**
 * TT means ticker cancels, ticker see this activity.
 */
public class TTCancellationSummaryActivity extends AbstractCancellationSummaryActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        securedPayment.setVisibility(View.GONE);
        cancellationButtons.setVisibility(View.GONE);
    }
}