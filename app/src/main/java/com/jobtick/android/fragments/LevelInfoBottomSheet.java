package com.jobtick.android.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.jobtick.android.R;
import com.jobtick.android.utils.Constant;

import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LevelInfoBottomSheet#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LevelInfoBottomSheet extends BottomSheetDialogFragment {


    private Context context;
    private TextView txtDescription;
    public LevelInfoBottomSheet() {
    }

    public static TickerRequirementsBottomSheet newInstance(HashMap<TickerRequirementsBottomSheet.Requirement, Boolean> state) {
        Bundle args = new Bundle();
        args.putSerializable(Constant.STATE_STRIPE_TICKER, state);
        TickerRequirementsBottomSheet fragment = new TickerRequirementsBottomSheet();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.ThemeOverlay_BottomSheetDialog);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_level_info_bottom_sheet, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getActivity() == null) {
            return;
        }
        context = getContext();
        txtDescription = view.findViewById(R.id.txt_description);

    }

}