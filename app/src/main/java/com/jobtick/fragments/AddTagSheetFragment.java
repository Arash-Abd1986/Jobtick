package com.jobtick.fragments;

import android.graphics.Rect;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.jobtick.R;

public class AddTagSheetFragment extends BottomSheetDialogFragment {


    public static AddTagSheetFragment newInstance() {
        return new AddTagSheetFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.sheet_add_must_have, container,
                false);


        if (getDialog().getWindow() != null) {
            getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        }
        if (getActivity() != null) {
            View decorView = getActivity().getWindow().getDecorView();
            decorView.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
                Rect displayFrame = new Rect();
                decorView.getWindowVisibleDisplayFrame(displayFrame);
                int height = decorView.getContext().getResources().getDisplayMetrics().heightPixels;
                int heightDifference = height - displayFrame.bottom;
                if (heightDifference != 0) {
                    if (view.getPaddingBottom() != heightDifference) {
                        view.setPadding(0, 0, 0, heightDifference);
                    }
                } else {
                    if (view.getPaddingBottom() != 0) {
                        view.setPadding(0, 0, 0, 0);
                    }
                }
            });
        }
        // get the views and attach the listener

        return view;

    }
}

