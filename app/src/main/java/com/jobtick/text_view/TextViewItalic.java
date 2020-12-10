package com.jobtick.text_view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.util.AttributeSet;


public class TextViewItalic extends androidx.appcompat.widget.AppCompatTextView {
    public TextViewItalic(Context context) {
        super(context);
        Typeface face=Typeface.createFromAsset(context.getAssets(), "fonts/Poppins-BoldItalic.ttf");
        this.setTypeface(face);
    }

    public TextViewItalic(Context context, AttributeSet attrs) {
        super(context, attrs);
        Typeface face=Typeface.createFromAsset(context.getAssets(), "fonts/Poppins-BoldItalic.ttf");
        this.setTypeface(face);
    }

    public TextViewItalic(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        Typeface face=Typeface.createFromAsset(context.getAssets(), "fonts/Poppins-BoldItalic.ttf");
        this.setTypeface(face);
    }

    protected void onDraw (Canvas canvas) {
        super.onDraw(canvas);


    }
}