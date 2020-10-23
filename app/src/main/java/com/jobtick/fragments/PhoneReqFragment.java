package com.jobtick.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jobtick.R;

import butterknife.ButterKnife;


public class PhoneReqFragment extends Fragment {

    public PhoneReqFragment() {
    }

    public static PhoneReqFragment newInstance() {
        return new PhoneReqFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.fragment_phone_req, container, false);
        ButterKnife.bind(this,view);
        return view;
    }
}