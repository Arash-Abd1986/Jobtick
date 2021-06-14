package com.jobtick.android.fragments;


import android.content.Context;

import com.jobtick.android.R;


public class ConfirmReleaseBottomSheet extends AbstractConfirmBottomSheetv2{


    private NoticeListener listener;


    public ConfirmReleaseBottomSheet(Context context) {

        super(
                context.getString(R.string.ask_for_payment),
                context.getString(R.string.are_you_satisfied_with_the_job),
                context.getString(R.string.accept),
                context.getString(R.string.decline));
    }

    @Override
    void onBlueButtonClick() {
        listener.onReleaseConfirmClick();
        dismiss();
    }

    @Override
    void onRedButtonClick() {
        dismiss();
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
        void onReleaseConfirmClick();
    }

}