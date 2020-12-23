package com.jobtick.button;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.util.AttributeSet;

import androidx.core.content.res.ResourcesCompat;

import com.jobtick.R;


public class ButtonRegular extends androidx.appcompat.widget.AppCompatButton {
    public ButtonRegular(Context context) {
        super(context);
        Typeface face= ResourcesCompat.getFont(context, R.font.roboto_regular);
        this.setTypeface(face);
    }

    public ButtonRegular(Context context, AttributeSet attrs) {
        super(context, attrs);
        Typeface face=ResourcesCompat.getFont(context, R.font.roboto_regular);
        this.setTypeface(face);
    }

    public ButtonRegular(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        Typeface face=ResourcesCompat.getFont(context, R.font.roboto_regular);
        this.setTypeface(face);
    }

    protected void onDraw (Canvas canvas) {
        super.onDraw(canvas);


    }
}