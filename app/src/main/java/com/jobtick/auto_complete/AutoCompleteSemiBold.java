package com.jobtick.auto_complete;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.util.AttributeSet;


public class AutoCompleteSemiBold extends androidx.appcompat.widget.AppCompatAutoCompleteTextView {
    public AutoCompleteSemiBold(Context context) {
        super(context);
        Typeface face=Typeface.createFromAsset(context.getAssets(), "fonts/Montserrat-SemiBold.otf");
        this.setTypeface(face);
    }

    public AutoCompleteSemiBold(Context context, AttributeSet attrs) {
        super(context, attrs);
        Typeface face=Typeface.createFromAsset(context.getAssets(), "fonts/Montserrat-SemiBold.otf");
        this.setTypeface(face);
    }

    public AutoCompleteSemiBold(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        Typeface face=Typeface.createFromAsset(context.getAssets(), "fonts/Montserrat-SemiBold.otf");
        this.setTypeface(face);
    }

    protected void onDraw (Canvas canvas) {
        super.onDraw(canvas);


    }
}