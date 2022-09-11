package com.jobtick.android.text_view;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

import androidx.core.content.res.ResourcesCompat;

import com.jobtick.android.R;


public class TextViewBold extends androidx.appcompat.widget.AppCompatTextView {
    public TextViewBold(Context context) {
        super(context);
        Typeface face = ResourcesCompat.getFont(context, R.font.roboto_bold);
        this.setTypeface(face);
    }

    public TextViewBold(Context context, AttributeSet attrs) {
        super(context, attrs);
        Typeface face = ResourcesCompat.getFont(context, R.font.roboto_bold);
        this.setTypeface(face);
    }

    public TextViewBold(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        Typeface face = ResourcesCompat.getFont(context, R.font.roboto_bold);
        this.setTypeface(face);
    }

}