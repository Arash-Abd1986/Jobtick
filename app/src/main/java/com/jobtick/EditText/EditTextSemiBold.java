package com.jobtick.EditText;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.util.AttributeSet;


public class EditTextSemiBold extends androidx.appcompat.widget.AppCompatEditText {
    public EditTextSemiBold(Context context) {
        super(context);
        Typeface face=Typeface.createFromAsset(context.getAssets(), "fonts/poppins_SemiBold.otf");
        this.setTypeface(face);
    }

    public EditTextSemiBold(Context context, AttributeSet attrs) {
        super(context, attrs);
        Typeface face=Typeface.createFromAsset(context.getAssets(), "fonts/poppins_SemiBold.otf");
        this.setTypeface(face);
    }

    public EditTextSemiBold(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        Typeface face=Typeface.createFromAsset(context.getAssets(), "fonts/poppins_SemiBold.otf");
        this.setTypeface(face);
    }

    protected void onDraw (Canvas canvas) {
        super.onDraw(canvas);


    }
}