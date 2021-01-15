package com.jobtick.android.cancellations;


public class CancellationDeclinedActivity extends AbstractDoneMessageActivity {

    @Override
    String getToolbarTitle() {
        return "Cancellation Declined";
    }

    @Override
    String getTextTitle() {
        return "Cancellation request declined";
    }
}