package com.jobtick.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import com.jobtick.R;


public class ExtendedJobInfo extends FrameLayout {

    private String eTitle;
    private String eValue;
    private int eBottomColor;

    private TextView title;
    private TextView value;
    private CardView card;
    public ExtendedJobInfo(Context context) {
        this(context, null);
    }

    public ExtendedJobInfo(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ExtendedJobInfo(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        TypedArray sharedAttribute = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.ExtendedJobInfo,
                0, 0);

        try {
            eTitle = sharedAttribute.getString(R.styleable.ExtendedJobInfo_eJobTitle);
            eValue = sharedAttribute.getString(R.styleable.ExtendedJobInfo_eJobValue);
            eBottomColor = sharedAttribute.getColor(R.styleable.ExtendedJobInfo_eJobBottomColor, 0);
        } finally {
            sharedAttribute.recycle();
        }

        //Inflate and attach the content
        LayoutInflater.from(context).inflate(R.layout.view_dashboard_job_cell_info, this);

        title = (TextView) findViewById(R.id.title);
        value = (TextView) findViewById(R.id.value);
        card = (CardView) findViewById(R.id.card_task_background);

        title.setText(eTitle);
        value.setText(eValue);
        card.setCardBackgroundColor(eBottomColor);
    }

    public void setValue(String value){
        this.value.setText(value);
    }

    public void setTitle(String title){
        this.title.setText(title);
    }

    public String getValue(){
        return value.getText().toString();
    }

    public String getTitle(){
        return title.getText().toString();
    }
}
