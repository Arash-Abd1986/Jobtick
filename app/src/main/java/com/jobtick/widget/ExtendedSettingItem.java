package com.jobtick.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.jobtick.R;


public class ExtendedSettingItem extends FrameLayout {

    private String eTitle;
    private TextView title;

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
        } finally {
            sharedAttribute.recycle();
        }

        //Inflate and attach the content
        LayoutInflater.from(context).inflate(R.layout.view_setting_item, this);

        title = (TextView) findViewById(R.id.title);

        title.setText(eTitle);
    }

    public void setTitle(String title){
        this.title.setText(title);
    }

    public String getTitle(){
        return title.getText().toString();
    }
}
