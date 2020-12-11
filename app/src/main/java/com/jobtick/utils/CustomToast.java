package com.jobtick.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.jobtick.R;
import android.annotation.SuppressLint;


public class CustomToast extends Toast {

    private final Context context;

    public CustomToast(Context context) {
        super(context);
        this.context = context;
    }

    @Override
    public void setView(View view) {
        super.setView(view);
    }

    public void setCustomView(String message) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        @SuppressLint("InflateParams") View view = inflater != null ? inflater.inflate(R.layout.custom_toast, null) : null;

        TextView toastText = view.findViewById(R.id.toast_txtMessage);
        toastText.setText(message);
        setView(view);
    }
}
