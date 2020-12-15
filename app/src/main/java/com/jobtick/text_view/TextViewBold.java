package com.jobtick.text_view;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;


public class TextViewBold extends androidx.appcompat.widget.AppCompatTextView {
    public TextViewBold(Context context) {
        super(context);
        Typeface face=Typeface.createFromAsset(context.getAssets(), "fonts/poppins_Bold.otf");
        this.setTypeface(face);
    }

    public TextViewBold(Context context, AttributeSet attrs) {
        super(context, attrs);
        Typeface face=Typeface.createFromAsset(context.getAssets(), "fonts/poppins_Bold.otf");
        this.setTypeface(face);
    }

    public TextViewBold(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        Typeface face=Typeface.createFromAsset(context.getAssets(), "fonts/poppins_Bold.otf");
        this.setTypeface(face);
    }

}