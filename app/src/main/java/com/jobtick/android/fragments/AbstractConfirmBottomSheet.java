package com.jobtick.android.fragments;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.jobtick.android.R;

public abstract class AbstractConfirmBottomSheet extends BottomSheetDialogFragment {

    TextView title;
    TextView description;
    Button decline;
    Button accept;

    protected ProgressDialog pDialog;

    private final String cTitle;
    private final String cDescription;
    private final String cPositiveButton;
    private final String cNegativeButton;

    public AbstractConfirmBottomSheet(String title, String description, String blueButtonText, String redButtonText) {
        this.cTitle = title;
        this.cDescription = description;
        this.cPositiveButton = blueButtonText;
        this.cNegativeButton = redButtonText;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.ThemeOverlay_BottomSheetDialog);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_confirm, container, false);


        title = view.findViewById(R.id.title);
        description = view.findViewById(R.id.description);
        decline = view.findViewById(R.id.btn_decline);
        accept = view.findViewById(R.id.btn_accept);

        decline.setOnClickListener(v -> {
            onRedButtonClick();
        });

        accept.setOnClickListener(v -> {
            onBlueButtonClick();
        });

        init();
        initProgressDialog();
        return view;
    }

    private void init() {
        title.setText(cTitle);
        description.setText(cDescription);
        decline.setText(cNegativeButton);
        accept.setText(cPositiveButton);
    }


    public void initProgressDialog() {
        pDialog = new ProgressDialog(requireContext());
        pDialog.setTitle(getString(R.string.processing));
        pDialog.setMessage(getString(R.string.please_wait));
        pDialog.setCancelable(false);
    }


    public void showProgressDialog() {

        if (!pDialog.isShowing())
            pDialog.show();
    }

    public void hideProgressDialog() {

        if (pDialog.isShowing())
            pDialog.dismiss();
    }

    abstract void onBlueButtonClick();
    abstract void onRedButtonClick();
}