package com.jobtick.fragments;

import android.os.Bundle;

public class PaymentTickerFragment extends AbstractPaymentFragment {


    private PaymentTickerFragment() {
        // Required empty public constructor
    }

    @Override
    boolean isPoster() {
        return false;
    }

    public static PaymentTickerFragment newInstance() {
        PaymentTickerFragment fragment = new PaymentTickerFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }
}