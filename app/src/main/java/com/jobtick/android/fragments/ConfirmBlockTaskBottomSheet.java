package com.jobtick.android.fragments;


import android.content.Context;

import com.jobtick.android.R;


public class ConfirmBlockTaskBottomSheet extends AbstractConfirmBottomSheetv2 {

    private NoticeListener listener;

    public ConfirmBlockTaskBottomSheet(Context context, String name) {

        super(
                context.getString(R.string.confirm_block),
                context.getString(R.string.are_you_sure_block, name),
                context.getString(R.string.block),
                context.getString(R.string.cancel));
    }

    @Override
    void onBlueButtonClick() {
        listener.onBlockConfirmClick();
        dismiss();
    }

    @Override
    void onRedButtonClick() {
        if (listener == null)
            throw new IllegalStateException("NoticeListener interface must be set.");
        dismiss();
    }

    public NoticeListener getListener() {
        return listener;
    }

    public void setListener(NoticeListener listener) {
        this.listener = listener;
    }

    public interface NoticeListener {
        void onBlockConfirmClick();
    }
}