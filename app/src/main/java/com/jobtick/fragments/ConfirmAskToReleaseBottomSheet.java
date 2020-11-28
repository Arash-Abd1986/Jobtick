package com.jobtick.fragments;


import android.content.Context;


import com.jobtick.R;


public class ConfirmAskToReleaseBottomSheet extends AbstractConfirmBottomSheet{


    private NoticeListener listener;


    public ConfirmAskToReleaseBottomSheet(Context context) {

        super(
                context.getString(R.string.ask_to_release),
                context.getString(R.string.have_you_complete_the_job),
                context.getString(R.string.accept),
                context.getString(R.string.decline));
    }

    @Override
    void onPositiveButtonClick() {
        listener.onAskToReleaseConfirmClick();
        dismiss();
    }

    @Override
    void onNegativeButtonClick() {
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
        void onAskToReleaseConfirmClick();
    }

}