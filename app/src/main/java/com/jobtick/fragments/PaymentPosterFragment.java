package com.jobtick.fragments;553

import android.os.Bundle;

public class PaymentPosterFragment extends AbstractPaymentFragment {


    private PaymentPosterFragment() {
        // Required empty public constructor
    }

    @Override
    boolean isPoster() {
        return true;
    }

    public static PaymentPosterFragment newInstance() {
        PaymentPosterFragment fragment = new PaymentPosterFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }
}