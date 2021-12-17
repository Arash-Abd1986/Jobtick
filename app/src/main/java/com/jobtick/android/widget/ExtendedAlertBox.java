package com.jobtick.android.widget;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.text.Spanned;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.material.button.MaterialButton;
import com.jobtick.android.R;
import com.jobtick.android.activities.TaskDetailsActivity;

public class ExtendedAlertBox extends FrameLayout {

    private final String title;
    private final TextView mTitle;
    private final ImageView alert_image;
    private final ImageView info_image;

    private final String buttonText;
    private final String alertType;
    private final MaterialButton mButton;
    private final ConstraintLayout cnlMain;
    private final boolean hasButton;
    private final boolean hasTopColor;
    private boolean isTicker;

    public boolean getIsTicker() {
        return isTicker;
    }

    public void setIsTicker(Boolean isTicker) {
        this.isTicker = isTicker;
        if (isTicker) {
            cnlMain.setBackgroundResource(R.drawable.shape_rounded_white_outline_s_back);
        }
    }

    private OnExtendedAlertButtonClickListener onExtendedAlertButtonClickListener;

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
            alertType = sharedAttribute.getString(R.styleable.ExtendedAlertBox_eAlertType);
            hasButton = sharedAttribute.getBoolean(R.styleable.ExtendedAlertBox_eAlertHasButton, true);
            hasTopColor = sharedAttribute.getBoolean(R.styleable.ExtendedAlertBox_eAlertHasTopColor, true);
            isTicker = sharedAttribute.getBoolean(R.styleable.ExtendedAlertBox_eAlertIsTicker, false);
        } finally {
            sharedAttribute.recycle();
        }


        //Inflate and attach the content
        LayoutInflater.from(context).inflate(R.layout.view_alert_box, this);

        mTitle = findViewById(R.id.title);
        mButton = findViewById(R.id.button_text);
        cnlMain = findViewById(R.id.cnl_main);
        alert_image = findViewById(R.id.alert_image);
        info_image = findViewById(R.id.info_image);

        mTitle.setText(title);
        mButton.setText(buttonText);
        if (hasButton && isTicker)
            mButton.setVisibility(View.VISIBLE);
        else
            mButton.setVisibility(View.GONE);
        if (hasTopColor)
            setBackgroundResource(R.drawable.rectangle_round_corners_red_8dp);
        else
            setBackgroundResource(R.drawable.shape_transparent);


        mButton.setOnClickListener(v -> {
            onExtendedAlertButtonClickListener.onExtendedAlertButtonClick();
        });

        if (isTicker) {
            cnlMain.setBackgroundResource(R.drawable.shape_rounded_white_outline_s_back);
        }
        if (alertType != null)
            setAlertType(alertType, context);


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
        this.mButton.setText(buttonText);
    }

    public boolean isHasButton() {
        return mButton.getVisibility() == View.VISIBLE;
    }

    public void setHasButton(boolean hasButton) {
        if (hasButton)
            this.mButton.setVisibility(View.VISIBLE);
        else
            this.mButton.setVisibility(View.GONE);
    }


    public void setAlertType(String alertType, Context context) {
        if (alertType.equals(TaskDetailsActivity.AlertType.REVIEW.name())) {
            if (isTicker) {
                cnlMain.setBackgroundResource(R.drawable.shape_rounded_white_5dp);
                mButton.setBackgroundColor(context.getColor(R.color.blue));
                mButton.setStrokeColor(ColorStateList.valueOf(context.getColor(R.color.blue)));
                mButton.setTextColor(context.getColor(R.color.white));
            } else {
                cnlMain.setBackgroundResource(R.drawable.shape_rounded_white_5dp);
                mButton.setBackgroundColor(context.getColor(R.color.blue));
                mButton.setStrokeColor(ColorStateList.valueOf(context.getColor(R.color.blue)));
                mButton.setTextColor(context.getColor(R.color.white));
                info_image.setImageResource(R.drawable.ic_bell_red);
                alert_image.setVisibility(GONE);
                info_image.setVisibility(VISIBLE);
            }
        } else if (alertType.equals(TaskDetailsActivity.AlertType.ASK_TO_RELEASE.name())) {
            if (isTicker) {
                cnlMain.setBackgroundResource(R.drawable.shape_rounded_white_5dp);
                alert_image.setImageResource(R.drawable.ic_alert_orange);
            } else {
                mButton.setBackgroundColor(context.getColor(R.color.white));
                mButton.setStrokeColor(ColorStateList.valueOf(context.getColor(R.color.blue)));
                mButton.setTextColor(context.getColor(R.color.P300));
                info_image.setImageResource(R.drawable.ic_bell_red);
                alert_image.setVisibility(GONE);
                info_image.setVisibility(VISIBLE);
            }
        } else if (alertType.equals(TaskDetailsActivity.AlertType.INCREASE_BUDGET.name())) {
            if (isTicker) {
                cnlMain.setBackgroundResource(R.drawable.shape_rounded_white_5dp);
                alert_image.setImageResource(R.drawable.ic_alert_orange);
            } else {
                mButton.setBackgroundColor(context.getColor(R.color.white));
                mButton.setStrokeColor(ColorStateList.valueOf(context.getColor(R.color.blue)));
                mButton.setTextColor(context.getColor(R.color.P300));
                info_image.setImageResource(R.drawable.ic_bell_red);
                alert_image.setVisibility(GONE);
                info_image.setVisibility(VISIBLE);
            }
        } else if (alertType.equals(TaskDetailsActivity.AlertType.RESCHEDULE.name())) {
            if (isTicker) {
                mButton.setBackgroundColor(context.getColor(R.color.white));
                mButton.setStrokeColor(ColorStateList.valueOf(context.getColor(R.color.blue)));
                mButton.setTextColor(context.getColor(R.color.P300));
            } else {
                mButton.setBackgroundColor(context.getColor(R.color.white));
                mButton.setStrokeColor(ColorStateList.valueOf(context.getColor(R.color.blue)));
                mButton.setTextColor(context.getColor(R.color.P300));
                info_image.setImageResource(R.drawable.ic_bell_red);
                alert_image.setVisibility(GONE);
                info_image.setVisibility(VISIBLE);
            }
        } else if (alertType.equals(TaskDetailsActivity.AlertType.CONFIRM_RELEASE.name())) {
            if (isTicker) {
                mButton.setBackgroundColor(context.getColor(R.color.white));
                mButton.setStrokeColor(ColorStateList.valueOf(context.getColor(R.color.blue)));
                mButton.setTextColor(context.getColor(R.color.P300));
            } else {
                mButton.setBackgroundColor(context.getColor(R.color.white));
                mButton.setStrokeColor(ColorStateList.valueOf(context.getColor(R.color.blue)));
                mButton.setTextColor(context.getColor(R.color.P300));
                info_image.setImageResource(R.drawable.ic_bell_red);
                info_image.setVisibility(VISIBLE);
                alert_image.setVisibility(GONE);
            }
        }else if (alertType.equals(TaskDetailsActivity.AlertType.CANCELLATION.name())) {
            if (isTicker) {
                mButton.setBackgroundColor(context.getColor(R.color.white));
                mButton.setStrokeColor(ColorStateList.valueOf(context.getColor(R.color.blue)));
                mButton.setTextColor(context.getColor(R.color.P300));
            } else {
                mButton.setBackgroundColor(context.getColor(R.color.white));
                mButton.setStrokeColor(ColorStateList.valueOf(context.getColor(R.color.blue)));
                mButton.setTextColor(context.getColor(R.color.P300));
                info_image.setImageResource(R.drawable.ic_bell_red);
                info_image.setVisibility(VISIBLE);
                alert_image.setVisibility(GONE);
            }
        }
    }

    public void setHasTopColor(boolean hasTopColor) {
        if (!hasTopColor)
            setBackgroundResource(R.drawable.shape_transparent);

    }

    public OnExtendedAlertButtonClickListener getOnExtendedAlertButtonClickListener() {
        return onExtendedAlertButtonClickListener;
    }

    public void setOnExtendedAlertButtonClickListener(OnExtendedAlertButtonClickListener onExtendedAlertButtonClickListener) {
        this.onExtendedAlertButtonClickListener = onExtendedAlertButtonClickListener;
    }

    public interface OnExtendedAlertButtonClickListener {

        void onExtendedAlertButtonClick();
    }
}

