package com.jobtick.fragments;

import android.content.Context;
import androidx.fragment.app.DialogFragment;

public class RescheduleNoticeDialogFragment extends DialogFragment {


    private NoticeDialogListener listener;

    public interface NoticeDialogListener {
        public void onDialogPositiveClick(DialogFragment dialog);
        public void onDialogNegativeClick(DialogFragment dialog);
    }

    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            listener = (NoticeDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(this.toString()
                    + " must implement NoticeDialogListener");
        }
    }




}
