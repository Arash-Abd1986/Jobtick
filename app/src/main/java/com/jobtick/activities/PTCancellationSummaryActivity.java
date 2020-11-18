package com.jobtick.activities;


import android.os.Bundle;
import android.view.View;

/**
 * PT means poster cancels, ticker see this activity.
 */
public class PTCancellationSummaryActivity extends AbstractCancellationSummaryActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        securedPayment.setVisibility(View.GONE);
    }
}