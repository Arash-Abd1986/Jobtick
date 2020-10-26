package com.jobtick.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.button.MaterialButton;
import com.jobtick.R;
import com.jobtick.utils.FontManager;

public class ExtendedButton extends MaterialButton {

    public ExtendedButton(@NonNull Context context) {
        super(context);
    }

    public ExtendedButton(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public ExtendedButton(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.ExtendedButton);
        String fontAsset = ta.getString(R.styleable.ExtendedButton_eFont);

        if (!fontAsset.isEmpty()) {
            int type = Integer.parseInt(fontAsset);

            Typeface typeFace = FontManager.getInstance(context).getByType(type);
            ta.recycle();
            super.setTypeface(typeFace);
        }
    }
}


