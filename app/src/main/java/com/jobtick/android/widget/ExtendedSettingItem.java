package com.jobtick.android.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.jobtick.android.R;


public class ExtendedSettingItem extends FrameLayout {

    private final String eTitle;
    private final int eColor;
    private final int drawableID = 0;
    private final TextView title;
    private final ImageView icon;

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
            //drawableID = sharedAttribute.getInt(R.styleable.ExtendedSettingItem_drawableID, R.drawable.ic_log_out);
            eColor = sharedAttribute.getColor(R.styleable.ExtendedSettingItem_colorText, this.getContext().getColor(R.color.N600));
        } finally {
            sharedAttribute.recycle();
        }

        //Inflate and attach the content
        LayoutInflater.from(context).inflate(R.layout.view_setting_item, this);

        title = findViewById(R.id.title);
        icon = findViewById(R.id.icon);
        // icon.setImageDrawable(ContextCompat.getDrawable(this.getContext(), drawableID));
        title.setTextColor(eColor);
        title.setText(eTitle);
    }

    public void setTitle(String title) {
        this.title.setText(title);
    }

    public void setDrawableID(int id) {
        if (id != 0)
            this.icon.setImageDrawable(ContextCompat.getDrawable(this.getContext(), id));
        else
            this.icon.setVisibility(GONE);
    }

    public String getTitle() {
        return title.getText().toString();
    }
}
