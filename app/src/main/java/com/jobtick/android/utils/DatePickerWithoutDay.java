package com.jobtick.android.utils;

import android.app.DatePickerDialog;
import android.content.Context;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class DatePickerWithoutDay extends DatePickerDialog {
    public DatePickerWithoutDay(@NonNull Context context) {
        super(context);
    }

    public DatePickerWithoutDay(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    public DatePickerWithoutDay(@NonNull Context context, @Nullable OnDateSetListener listener, int year, int month, int dayOfMonth) {
        super(context, listener, year, month, dayOfMonth);
    }

    public DatePickerWithoutDay(@NonNull Context context, int themeResId, @Nullable OnDateSetListener listener, int year, int monthOfYear, int dayOfMonth) {
        super(context, themeResId, listener, year, monthOfYear, dayOfMonth);
    }

    @Override
    public ListView getListView() {
        return super.getListView();
    }
}
