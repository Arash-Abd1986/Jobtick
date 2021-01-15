package com.jobtick.android.text_view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.util.AttributeSet;

import androidx.core.content.res.ResourcesCompat;

import com.jobtick.android.R;


public class TextViewSemiBold extends androidx.appcompat.widget.AppCompatTextView {
    public TextViewSemiBold(Context context) {
        super(context);
        Typeface face = ResourcesCompat.getFont(context, R.font.roboto_semi_bold);
        this.setTypeface(face);
    }

    public TextViewSemiBold(Context context, AttributeSet attrs) {
        super(context, attrs);
        Typeface face = ResourcesCompat.getFont(context, R.font.roboto_semi_bold);
        this.setTypeface(face);
    }

    public TextViewSemiBold(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        Typeface face = ResourcesCompat.getFont(context, R.font.roboto_semi_bold);
        this.setTypeface(face);
    }

    protected void onDraw (Canvas canvas) {
        super.onDraw(canvas);


    }
}