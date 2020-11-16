package com.jobtick.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.Spanned;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;
import com.jobtick.R;

public class ExtendedAlertBox extends FrameLayout {

    private String title;
    private TextView mTitle;

    private String buttonText;
    private MaterialButton mButton;
    private boolean hasButton;

    public ExtendedAlertBox(Context context) {
        this(context, null);
    }

    public ExtendedAlertBox(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ExtendedAlertBox(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        TypedArray sharedAttribute = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.ExtendedAlertBox,
                0, 0);

        try {
            title = sharedAttribute.getString(R.styleable.ExtendedAlertBox_eAlertTitle);
            buttonText = sharedAttribute.getString(R.styleable.ExtendedAlertBox_eAlertButtonText);
            hasButton = sharedAttribute.getBoolean(R.styleable.ExtendedAlertBox_eAlertHasButton, true);
        } finally {
            sharedAttribute.recycle();
        }

        //Inflate and attach the content
        LayoutInflater.from(context).inflate(R.layout.view_alert_box, this);

        setBackgroundResource(R.drawable.rectangle_round_corners_red_8dp);
        mTitle = (TextView) findViewById(R.id.title);
        mButton = (MaterialButton) findViewById(R.id.button_text);

        mTitle.setText(title);
        mButton.setText(buttonText);
        if(hasButton)
            mButton.setVisibility(View.VISIBLE);
        else
            mButton.setVisibility(View.GONE);
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.mTitle.setText(title);
    }

    public void setTitle(Spanned title) {
        this.mTitle.setText(title);
    }

    public String getButtonText() {
        return mButton.getText().toString();
    }

    public void setButtonText(String buttonText) {
        this.setButtonText(buttonText);
    }

    public boolean isHasButton() {
        return mButton.getVisibility() == View.VISIBLE;
    }

    public void setHasButton(boolean hasButton) {
        if(hasButton)
            this.mButton.setVisibility(View.VISIBLE);
        else
            this.mButton.setVisibility(View.GONE);
    }
}

