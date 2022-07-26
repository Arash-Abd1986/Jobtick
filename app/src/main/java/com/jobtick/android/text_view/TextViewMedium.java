package com.jobtick.android.text_view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.util.AttributeSet;

import androidx.core.content.res.ResourcesCompat;

import com.jobtick.android.R;


public class TextViewMedium extends androidx.appcompat.widget.AppCompatTextView {
    public TextViewMedium(Context context) {
        super(context);
        Typeface face= ResourcesCompat.getFont(context, R.font.dmsans_medium);
        this.setTypeface(face);
    }

    public TextViewMedium(Context context, AttributeSet attrs) {
        super(context, attrs);
        Typeface face=ResourcesCompat.getFont(context, R.font.dmsans_medium);
        this.setTypeface(face);
    }

    public TextViewMedium(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        Typeface face=ResourcesCompat.getFont(context, R.font.dmsans_medium);
        this.setTypeface(face);
    }

    protected void onDraw (Canvas canvas) {
        super.onDraw(canvas);


    }
}