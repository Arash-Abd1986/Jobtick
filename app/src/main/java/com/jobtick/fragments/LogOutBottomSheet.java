package com.jobtick.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;
import com.jobtick.R;
import com.jobtick.activities.AuthActivity;
import com.jobtick.utils.SessionManager;


public class LogOutBottomSheet extends BottomSheetDialogFragment {

    private SessionManager sessionManager;

    MaterialButton logout;
    MaterialButton cancel;


    public LogOutBottomSheet(){

    }

    public static LogOutBottomSheet newInstance() {
        return new LogOutBottomSheet();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.TransparentDialog);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        sessionManager = new SessionManager(getContext());
        View view = inflater.inflate(R.layout.fragment_logout_bottom_sheet, container, false);

        logout = view.findViewById(R.id.logout);
        cancel = view.findViewById(R.id.cancel);


        logout.setOnClickListener(v -> {
            dismiss();
            sessionManager.setUserAccount(null);
            sessionManager.setLogin(false);
            sessionManager.setTokenType(null);
            sessionManager.setAccessToken(null);
            Intent intent = new Intent(getContext(), AuthActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            requireActivity().finish();
        });

        cancel.setOnClickListener(v -> {
            dismiss();
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getActivity() == null) {
            return;
        }


        initUi();
    }

    private void initUi(){


    }
}
