package com.jobtick.android.radio_button;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.util.AttributeSet;

import androidx.core.content.res.ResourcesCompat;

import com.jobtick.android.R;


public class RadioButtonMedium extends androidx.appcompat.widget.AppCompatRadioButton {
    public RadioButtonMedium(Context context) {
        super(context);
        Typeface face= ResourcesCompat.getFont(context, R.font.dmsans_medium);
        this.setTypeface(face);
    }

    public RadioButtonMedium(Context context, AttributeSet attrs) {
        super(context, attrs);
        Typeface face=ResourcesCompat.getFont(context, R.font.dmsans_medium);
        this.setTypeface(face);
    }

    public RadioButtonMedium(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        Typeface face=ResourcesCompat.getFont(context, R.font.dmsans_medium);
        this.setTypeface(face);
    }

    protected void onDraw (Canvas canvas) {
        super.onDraw(canvas);


    }
}