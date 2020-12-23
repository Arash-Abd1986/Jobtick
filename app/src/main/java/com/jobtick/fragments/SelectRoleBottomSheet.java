package com.jobtick.fragments;


import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.jobtick.R;

public class SelectRoleBottomSheet extends BottomSheetDialogFragment implements View.OnClickListener {

    TextView title;
    Button getStartedButton;
    RadioButton cbPoster;
    RadioButton cbWorker;

    protected NoticeListener listener;

    public SelectRoleBottomSheet(){

    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.ThemeOverlay_BottomSheetDialog);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_select_role, container, false);


        title = view.findViewById(R.id.title);
        cbPoster = view.findViewById(R.id.cb_poster);
        cbWorker = view.findViewById(R.id.cb_worker);
        getStartedButton = view.findViewById(R.id.btn_get_started);

        cbWorker.setOnClickListener(this);
        cbPoster.setOnClickListener(this);

        getStartedButton.setOnClickListener(v -> {
            if(!validation()) return;
            if(cbPoster.isChecked())
                listener.onGetStartedClick("poster");
            else if(cbWorker.isChecked())
                listener.onGetStartedClick("worker");
            else{
                throw new IllegalStateException("In spite of validation, there is no role selected.");
            }
            dismiss();
        });


        return view;
    }


    private boolean validation() {
        if(!cbWorker.isChecked() && !cbPoster.isChecked()){
            cbWorker.setBackgroundResource(R.drawable.radio_button_background_on_error);
            cbPoster.setBackgroundResource(R.drawable.radio_button_background_on_error);
            return false;
        }
        return true;
    }

    @Override
    public void onClick(View view) {
        view.setBackgroundResource(R.drawable.radio_button_background);
    }


    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            listener = (NoticeListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(this.toString()
                    + " must implement NoticeListener");
        }
    }

    public interface NoticeListener {

        void onGetStartedClick(String role);
    }

}