package com.jobtick.activities;


import android.os.Bundle;
import android.view.View;

/**
 * PP means poster cancels, poster see this activity.
 */
public class PPCancellationSummaryActivity extends AbstractCancellationSummaryActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        cancellationButtons.setVisibility(View.GONE);
    }
}