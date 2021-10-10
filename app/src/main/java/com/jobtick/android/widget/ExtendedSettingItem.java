package com.jobtick.android.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.jobtick.android.R;


public class ExtendedSettingItem extends FrameLayout {

    private final String eTitle;
    private final int eColor;
    private final TextView title;

    public ExtendedSettingItem(Context context) {
        this(context, null);
    }

    public ExtendedSettingItem(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ExtendedSettingItem(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        TypedArray sharedAttribute = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.ExtendedSettingItem,
                0, 0);

        try {
            eTitle = sharedAttribute.getString(R.styleable.ExtendedSettingItem_eSettingTitle);
            eColor = sharedAttribute.getColor(R.styleable.ExtendedSettingItem_colorText, this.getContext().getColor(R.color.P300));
        } finally {
            sharedAttribute.recycle();
        }

        //Inflate and attach the content
        LayoutInflater.from(context).inflate(R.layout.view_setting_item, this);

        title = findViewById(R.id.title);
        title.setTextColor(eColor);
        title.setText(eTitle);
    }

    public void setTitle(String title) {
        this.title.setText(title);
    }

    public String getTitle() {
        return title.getText().toString();
    }
}
