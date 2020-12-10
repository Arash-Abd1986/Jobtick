package com.jobtick.auto_complete;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.util.AttributeSet;


public class AutoCompleteRegular extends androidx.appcompat.widget.AppCompatAutoCompleteTextView {
    public AutoCompleteRegular(Context context) {
        super(context);
        Typeface face=Typeface.createFromAsset(context.getAssets(), "fonts/Montserrat-Regular.otf");
        this.setTypeface(face);
    }

    public AutoCompleteRegular(Context context, AttributeSet attrs) {
        super(context, attrs);
        Typeface face=Typeface.createFromAsset(context.getAssets(), "fonts/Montserrat-Regular.otf");
        this.setTypeface(face);
    }

    public AutoCompleteRegular(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        Typeface face=Typeface.createFromAsset(context.getAssets(), "fonts/Montserrat-Regular.otf");
        this.setTypeface(face);
    }

    protected void onDraw (Canvas canvas) {
        super.onDraw(canvas);


    }
}