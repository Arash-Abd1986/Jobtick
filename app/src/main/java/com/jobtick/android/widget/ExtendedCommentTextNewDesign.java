package com.jobtick.android.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatImageView;

import com.jobtick.android.R;
import com.jobtick.android.utils.MyExtensions;

import java.util.Locale;


public class ExtendedCommentTextNewDesign extends RelativeLayout implements View.OnClickListener, View.OnFocusChangeListener, TextWatcher {

    private String eTitle;
    private final String eContent;
    private final String eHint;
    private final boolean isMandatory;
    private int eMinSize;
    private int eMaxSize;
    private final boolean eStartFocus;
    private final boolean eSingleLine;
    private final boolean isRequired;
    private final boolean isSuburb;
    private final boolean showCounter;
    private int eImeOptions;

    private final TextView textView;
    private final TextView counter;
    private final EditText editText;
    private final AppCompatImageView suburbIcon;

    private TextWatcher textWatcher;


    public ExtendedCommentTextNewDesign(Context context) {
        this(context, null);
    }

    public ExtendedCommentTextNewDesign(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ExtendedCommentTextNewDesign(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        TypedArray sharedAttribute = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.ExtendedCommentText,
                0, 0);

        try {
            eTitle = sharedAttribute.getString(R.styleable.ExtendedCommentText_eTitle);
            eContent = sharedAttribute.getString(R.styleable.ExtendedCommentText_eContent);
            eHint = sharedAttribute.getString(R.styleable.ExtendedCommentText_eHint);
            eStartFocus = sharedAttribute.getBoolean(R.styleable.ExtendedCommentText_eStartFocusComment, false);
            eSingleLine = sharedAttribute.getBoolean(R.styleable.ExtendedCommentText_eSingleLine, false);
            eMinSize = sharedAttribute.getInt(R.styleable.ExtendedCommentText_eMinCharSize, 10);
            eMaxSize = sharedAttribute.getInt(R.styleable.ExtendedCommentText_eMaxCharSize, 100);
            isMandatory = sharedAttribute.getBoolean(R.styleable.ExtendedCommentText_eIsMandatory, false);
            isRequired = sharedAttribute.getBoolean(R.styleable.ExtendedCommentText_isRequired, false);
            showCounter = sharedAttribute.getBoolean(R.styleable.ExtendedCommentText_showCounter, true);
            isSuburb = sharedAttribute.getBoolean(R.styleable.ExtendedCommentText_isSuburb, false);

            String imeOptions = sharedAttribute.getString(R.styleable.ExtendedCommentText_eImeOptionsComment);
            if (imeOptions != null && !imeOptions.isEmpty())
                eImeOptions = Integer.parseInt(imeOptions);
        } finally {
            sharedAttribute.recycle();
        }

        //Inflate and attach the content
        LayoutInflater.from(context).inflate(R.layout.view_extended_comment_text_new_design, this);

        setBackgroundResource(R.drawable.rectangle_card_round_corners_outlined);

        editText = findViewById(R.id.content);
        textView = findViewById(R.id.title);
        counter = findViewById(R.id.counter);
        suburbIcon = findViewById(R.id.suburb_icon);
        if (!showCounter)
            counter.setVisibility(INVISIBLE);
        textView.setText(eTitle);
        editText.setText(eContent);
        editText.setHint(eHint);
        if (eSingleLine)
            editText.setMaxLines(1);
        if (isSuburb) {
            editText.setEnabled(false);
            editText.setFocusable(false);
            suburbIcon.setVisibility(VISIBLE);
        }

        editText.setSingleLine(eSingleLine);

        editText.setOnFocusChangeListener(this);
        editText.addTextChangedListener(this);
        setOnClickListener(this);

        setImeOptions();
        init();
    }

    private void setImeOptions() {
        if (eImeOptions == ExtendedEntryText.EImeOptions.ACTION_NEXT)
            editText.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        if (eImeOptions == ExtendedEntryText.EImeOptions.ACTION_DONE)
            editText.setImeOptions(EditorInfo.IME_ACTION_DONE);
        if (eImeOptions == ExtendedEntryText.EImeOptions.NORMAL)
            editText.setImeOptions(EditorInfo.IME_ACTION_UNSPECIFIED);
    }

    public void addTextChangedListener(TextWatcher textWatcher) {
        this.textWatcher = textWatcher;
    }

    @SuppressLint("SetTextI18n")
    private void init() {
        if (isMandatory) {
            counter.setText(String.format(Locale.ENGLISH, "0/%d+", eMinSize));
            counter.setTextColor(getResources().getColor(R.color.strokeRed));
        } else {
            counter.setText(String.format(Locale.ENGLISH, "0/%d", eMaxSize));
            counter.setTextColor(getResources().getColor(R.color.N050));
        }

        if (eStartFocus) {
            editText.requestFocus();
            showKeyboard(editText);
        }
        if (isRequired)
            textView.setText(textView.getText() + " * ");
    }

    public void setSelection(int i) {
        editText.setSelection(i);
    }

    @Override
    public void onClick(View v) {

        editText.requestFocus();
        editText.performClick();

        showKeyboard(editText);
    }

    private void showKeyboard(EditText editText) {
        editText.post(() -> {
            InputMethodManager imm = (InputMethodManager) getContext()
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(editText, 0);
        });
    }

    @Override
    public void onFocusChange(View view, boolean focused) {
        if (focused) {
            textView.setTextColor(getResources().getColor(R.color.P300));
            setBackgroundResource(R.drawable.rectangle_card_round_corners_outlined_primary);
        } else {
            if (textView.getText().length() == 0)
                textView.setTextColor(getResources().getColor(R.color.N100));
            setBackgroundResource(R.drawable.rectangle_card_round_corners_outlined);
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        if (textWatcher != null)
            textWatcher.onTextChanged(s, start, count, after);
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

        if (textWatcher != null)
            textWatcher.onTextChanged(s, start, before, count);
    }

    @Override
    public void afterTextChanged(Editable s) {
        if (s.length() > 0) {
            try {
                LayoutParams lp = (LayoutParams) editText.getLayoutParams();
                lp.setMargins(MyExtensions.dpToPx(16), MyExtensions.dpToPx(4), MyExtensions.dpToPx(12), MyExtensions.dpToPx(12));
                editText.setLayoutParams(lp);
            }catch (Exception e){
                e.printStackTrace();
            }
            textView.setTextColor(getResources().getColor(R.color.P300));
        } else {
            try {
                LayoutParams lp = (LayoutParams) editText.getLayoutParams();
                lp.setMargins(MyExtensions.dpToPx(16), MyExtensions.dpToPx(4), MyExtensions.dpToPx(12), MyExtensions.dpToPx(0));
                editText.setLayoutParams(lp);
            }catch (Exception e){
                e.printStackTrace();
            }
            textView.setTextColor(getResources().getColor(R.color.N100));
        }
        if (isMandatory) {
            if (!s.toString().equalsIgnoreCase("")) {
                int length = s.toString().trim().length();
                if (length < eMinSize) {
                    counter.setText(String.format(Locale.ENGLISH, "%d/%d+", s.toString().trim().length(), eMinSize));
                    counter.setTextColor(getResources().getColor(R.color.strokeRed));
                } else if (length <= eMaxSize) {
                    counter.setText(String.format(Locale.ENGLISH, "%d/%d", s.toString().trim().length(), eMaxSize));
                    counter.setTextColor(getResources().getColor(R.color.green));
                } else {
                    editText.setText(s.subSequence(0, eMaxSize));
                    editText.setSelection(eMaxSize);
                }
            } else {
                counter.setText(String.format(Locale.ENGLISH, "%d/%d+", s.toString().trim().length(), eMinSize));
                counter.setTextColor(getResources().getColor(R.color.strokeRed));
            }
        } else {
            if (!s.toString().equalsIgnoreCase("")) {
                int length = s.toString().trim().length();
                if (length <= eMaxSize) {
                    counter.setText(String.format(Locale.ENGLISH, "%d/%d", s.toString().trim().length(), eMaxSize));
                    counter.setTextColor(getResources().getColor(R.color.green));
                } else {
                    editText.setText(s.subSequence(0, eMaxSize));
                    editText.setSelection(eMaxSize);
                }
            } else {
                counter.setText(String.format(Locale.ENGLISH, "0/%d", eMaxSize));
                counter.setTextColor(getResources().getColor(R.color.N050));
            }
        }

        if (textWatcher != null)
            textWatcher.afterTextChanged(s);
    }

    public void setError(CharSequence error) {
        setBackgroundResource(R.drawable.rectangle_card_round_corners_outlined_red);
        //editText.setError(error);
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

    public TextWatcher getTextWatcher() {
        return textWatcher;
    }

    public void setTextWatcher(TextWatcher textWatcher) {
        this.textWatcher = textWatcher;
    }
}
