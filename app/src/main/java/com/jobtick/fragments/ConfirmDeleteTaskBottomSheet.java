package com.jobtick.fragments;


import android.content.Context;

import com.jobtick.R;


public class ConfirmDeleteTaskBottomSheet extends AbstractConfirmBottomSheet{

    private NoticeListener listener;

    public ConfirmDeleteTaskBottomSheet(Context context) {

        super(
                context.getString(R.string.confirm_delete),
                context.getString(R.string.are_you_sure_delete_draft),
                context.getString(R.string.no),
                context.getString(R.string.yes));
    }

    @Override
    void onBlueButtonClick() {
        dismiss();
    }

    @Override
    void onRedButtonClick() {
        if(listener == null) throw new IllegalStateException("NoticeListener interface must be set.");
        listener.onDeleteConfirmClick();
        dismiss();
    }

    public NoticeListener getListener() {
        return listener;
    }

    public void setListener(NoticeListener listener) {
        this.listener = listener;
    }

    public interface NoticeListener {
        void onDeleteConfirmClick();
    }
}