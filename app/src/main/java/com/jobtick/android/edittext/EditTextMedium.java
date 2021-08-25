package com.jobtick.android.edittext;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.util.AttributeSet;

import androidx.core.content.res.ResourcesCompat;

import com.jobtick.android.R;


public class EditTextMedium extends androidx.appcompat.widget.AppCompatEditText {
    public EditTextMedium(Context context) {
        super(context);
        Typeface face = ResourcesCompat.getFont(context, R.font.roboto_medium);
        this.setTypeface(face);
    }

    public EditTextMedium(Context context, AttributeSet attrs) {
        super(context, attrs);
        Typeface face=ResourcesCompat.getFont(context, R.font.roboto_medium);
        this.setTypeface(face);
    }

    public EditTextMedium(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        Typeface face = ResourcesCompat.getFont(context, R.font.roboto_medium);
        this.setTypeface(face);
    }

    protected void onDraw (Canvas canvas) {
        super.onDraw(canvas);


    }
}