package com.jobtick.button;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.util.AttributeSet;


public class ButtonMedium extends androidx.appcompat.widget.AppCompatButton {
    public ButtonMedium(Context context) {
        super(context);
        Typeface face=Typeface.createFromAsset(context.getAssets(), "fonts/poppins_Medium.otf");
        this.setTypeface(face);
    }

    public ButtonMedium(Context context, AttributeSet attrs) {
        super(context, attrs);
        Typeface face=Typeface.createFromAsset(context.getAssets(), "fonts/poppins_Medium.otf");
        this.setTypeface(face);
    }

    public ButtonMedium(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        Typeface face=Typeface.createFromAsset(context.getAssets(), "fonts/poppins_Medium.otf");
        this.setTypeface(face);
    }

    protected void onDraw (Canvas canvas) {
        super.onDraw(canvas);


    }
}