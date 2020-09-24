package com.jobtick.AutoComplete;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.util.AttributeSet;


public class AutoCompleteLight extends androidx.appcompat.widget.AppCompatAutoCompleteTextView {
    public AutoCompleteLight(Context context) {
        super(context);
        Typeface face=Typeface.createFromAsset(context.getAssets(), "fonts/Montserrat-Light.otf");
        this.setTypeface(face);
    }

    public AutoCompleteLight(Context context, AttributeSet attrs) {
        super(context, attrs);
        Typeface face=Typeface.createFromAsset(context.getAssets(), "fonts/Montserrat-Light.otf");
        this.setTypeface(face);
    }

    public AutoCompleteLight(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        Typeface face=Typeface.createFromAsset(context.getAssets(), "fonts/Montserrat-Light.otf");
        this.setTypeface(face);
    }

    protected void onDraw (Canvas canvas) {
        super.onDraw(canvas);


    }
}