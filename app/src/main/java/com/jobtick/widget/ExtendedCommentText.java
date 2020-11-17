package com.jobtick.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jobtick.R;


public class ExtendedCommentText extends RelativeLayout implements View.OnClickListener, View.OnFocusChangeListener, TextWatcher {

    private String eTitle;
    private String eContent;
    private String eHint;
    private boolean isMandatory;
    private int eMinSize;
    private int eMaxSize;

    private TextView textView;
    private TextView counter;
    private EditText editText;


    public ExtendedCommentText(Context context) {
        this(context, null);
    }

    public ExtendedCommentText(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ExtendedCommentText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        TypedArray sharedAttribute = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.ExtendedCommentText,
                0, 0);

        try {
            eTitle = sharedAttribute.getString(R.styleable.ExtendedCommentText_eTitle);
            eContent = sharedAttribute.getString(R.styleable.ExtendedCommentText_eContent);
            eHint = sharedAttribute.getString(R.styleable.ExtendedCommentText_eHint);
            eMinSize = sharedAttribute.getInt(R.styleable.ExtendedCommentText_eMinCharSize, 10);
            eMaxSize = sharedAttribute.getInt(R.styleable.ExtendedCommentText_eMinCharSize, 100);
            isMandatory = sharedAttribute.getBoolean(R.styleable.ExtendedCommentText_eIsMandatory, false);
        } finally {
            sharedAttribute.recycle();
        }

        //Inflate and attach the content
        LayoutInflater.from(context).inflate(R.layout.view_extended_comment_text, this);

        setBackgroundResource(R.drawable.rectangle_card_round_corners_outlined);

        editText = (EditText) findViewById(R.id.content);
        textView = (TextView) findViewById(R.id.title);
        counter = (TextView) findViewById(R.id.counter);

        textView.setText(eTitle);
        editText.setText(eContent);
        editText.setHint(eHint);

        editText.setOnFocusChangeListener(this);
        editText.addTextChangedListener(this);
        setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        editText.requestFocus();
        editText.performClick();

        showKeyboard(editText);
    }

    private void showKeyboard(EditText editText) {
        editText.post(new Runnable() {
            @Override
            public void run() {
                InputMethodManager imm = (InputMethodManager) getContext()
                        .getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(editText, 0);
            }
        });
    }

    @Override
    public void onFocusChange(View view, boolean focused) {
        if (focused)
            setBackgroundResource(R.drawable.rectangle_card_round_corners_outlined_primary);
        else
            setBackgroundResource(R.drawable.rectangle_card_round_corners_outlined);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    @Override
    public void afterTextChanged(Editable s) {

        if (isMandatory) {
            if (!s.toString().equalsIgnoreCase("")) {
                int length = s.length();
                if (length < eMinSize) {
                    counter.setText(s.length() + "/" + eMinSize + "+");
                    counter.setTextColor(getResources().getColor(R.color.red_600));
                } else if (length <= eMaxSize) {
                    counter.setText(s.length() + "/" + eMaxSize);
                    counter.setTextColor(getResources().getColor(R.color.green));
                } else {
                    editText.setText(s.subSequence(0, eMaxSize));
                    editText.setSelection(eMaxSize);
                }
            } else {
                counter.setText(s.length() + "/" + eMinSize + "+");
                counter.setTextColor(getResources().getColor(R.color.red_600));
            }
        } else {
            if (!s.toString().equalsIgnoreCase("")) {
                int length = s.length();
                if (length <= eMaxSize) {
                    counter.setText(s.length() + "/" + eMaxSize);
                    counter.setTextColor(getResources().getColor(R.color.green));
                } else {
                    editText.setText(s.subSequence(0, eMaxSize));
                    editText.setSelection(eMaxSize);
                }
            } else {
                counter.setText("0/" + eMaxSize);
                counter.setTextColor(getResources().getColor(R.color.colorGrayC9C9C9));
            }
        }
    }

    public void setError(CharSequence error) {
        editText.setError(error);
    }

    public String geteTitle() {
        return eTitle;
    }

    public void seteTitle(String eTitle) {
        this.eTitle = eTitle;
    }

    public String geteContent() {
        return editText.getText().toString();
    }

    public String getText() {
        return editText.getText().toString();
    }

    public void setText(String content) {
        this.editText.setText(content);
    }

    public void seteContent(String eContent) {
        this.editText.setText(eContent);
    }

    public int geteMinSize() {
        return eMinSize;
    }

    public void seteMinSize(int eMinSize) {
        this.eMinSize = eMinSize;
    }

    public int geteMaxSize() {
        return eMaxSize;
    }

    public void seteMaxSize(int eMaxSize) {
        this.eMaxSize = eMaxSize;
    }
}
