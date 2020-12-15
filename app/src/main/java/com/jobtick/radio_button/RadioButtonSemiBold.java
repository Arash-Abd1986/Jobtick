package com.jobtick.radio_button;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;


public class RadioButtonSemiBold extends androidx.appcompat.widget.AppCompatRadioButton {
    public RadioButtonSemiBold(Context context) {
        super(context);
        Typeface face=Typeface.createFromAsset(context.getAssets(), "fonts/poppins_SemiBold.otf");
        this.setTypeface(face);
    }

    public RadioButtonSemiBold(Context context, AttributeSet attrs) {
        super(context, attrs);
        Typeface face=Typeface.createFromAsset(context.getAssets(), "fonts/poppins_SemiBold.otf");
        this.setTypeface(face);
    }

    public RadioButtonSemiBold(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        Typeface face=Typeface.createFromAsset(context.getAssets(), "fonts/poppins_SemiBold.otf");
        this.setTypeface(face);
    }


}