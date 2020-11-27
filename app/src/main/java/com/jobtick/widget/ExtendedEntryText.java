package com.jobtick.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.jobtick.R;

import static android.text.InputType.TYPE_CLASS_TEXT;


public class ExtendedEntryText extends RelativeLayout implements View.OnClickListener, View.OnFocusChangeListener, TextView.OnEditorActionListener {

    private String eTitle;
    private String eContent;
    private String eHint;
    private TextView textView;
    private TextView errorView;
    private TextView dollar;
    private EditText editText;
    private ImageView imageView;
    private boolean eIsPassword;
    private boolean eStartFocus;
    private int eInputType;
    private int eImeOptions;
    private boolean password_hide = true;
    private ExtendedViewOnClickListener extendedViewOnClickListener;

    public ExtendedEntryText(Context context) {
        this(context, null);
    }

    public ExtendedEntryText(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ExtendedEntryText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        TypedArray sharedAttribute = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.ExtendedEntryText,
                0, 0);

        try {
            eTitle = sharedAttribute.getString(R.styleable.ExtendedEntryText_eTitle);
            eContent = sharedAttribute.getString(R.styleable.ExtendedEntryText_eContent);
            eHint = sharedAttribute.getString(R.styleable.ExtendedEntryText_eHint);
            eStartFocus = sharedAttribute.getBoolean(R.styleable.ExtendedEntryText_eStartFocus, false);
            String inputType = sharedAttribute.getString(R.styleable.ExtendedEntryText_eInputType);
            if (inputType != null && !inputType.isEmpty())
                eInputType = Integer.parseInt(inputType);

            String imeOptions = sharedAttribute.getString(R.styleable.ExtendedEntryText_eImeOptions);
            if (imeOptions != null && !imeOptions.isEmpty())
                eImeOptions = Integer.parseInt(imeOptions);

        } finally {
            sharedAttribute.recycle();
        }

        //Inflate and attach the content
        LayoutInflater.from(context).inflate(R.layout.view_extended_entry_text, this);

        setBackgroundResource(R.drawable.rectangle_card_round_corners_outlined);

        editText = (EditText) findViewById(R.id.content);
        textView = (TextView) findViewById(R.id.title);
        errorView = (TextView) findViewById(R.id.error);
        imageView = (ImageView) findViewById(R.id.img_btn_password_toggle);
        dollar = (TextView) findViewById(R.id.dollar);

        textView.setText(eTitle);
        editText.setText(eContent);
        editText.setHint(eHint);

        editText.setOnFocusChangeListener(this);
        editText.setOnEditorActionListener(this);
        setInputType();
        setImeOptions();
        setListeners();

        if(eStartFocus){
            editText.requestFocus();
            showKeyboard(editText);
        }
    }

    @Override
    public void onClick(View v) {
        if (eInputType == EInputType.SUBURB || eInputType == EInputType.CALENDAR) {
            if (extendedViewOnClickListener == null)
                throw new IllegalStateException(eInputType + " type selected, but ExtendedViewOnClickListener is not implemented.");

            extendedViewOnClickListener.onClick();
            return;
        }


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

    private void setInputType() {
        imageView.setVisibility(GONE);

        if (eInputType == EInputType.INTEGER) {
            editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        } else if (eInputType == EInputType.EMAIL)
            editText.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        else if (eInputType == EInputType.PASSWORD) {
            eIsPassword = true;
            imageView.setVisibility(VISIBLE);
            editText.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
            editText.setTransformationMethod(PasswordTransformationMethod.getInstance());

        } else if (eInputType == EInputType.PHONE)
            editText.setInputType(InputType.TYPE_CLASS_PHONE);
        else if (eInputType == EInputType.SUBURB || eInputType == EInputType.CALENDAR) {
            editText.setFocusable(false);
            editText.setOnClickListener(v -> {
                extendedViewOnClickListener.onClick();
            });
            editText.setInputType(InputType.TYPE_NULL);
            editText.setClickable(true);
        } else
            editText.setInputType(TYPE_CLASS_TEXT);


        if (eInputType == EInputType.SUBURB) {
            editText.setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(getContext(), R.drawable.ic_pin_blue), null);
        }
        if (eInputType == EInputType.CALENDAR) {
            editText.setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(getContext(), R.drawable.ic_calendar_blue), null);
        }
        if (eInputType == EInputType.BUDGET) {
            dollar.setText("$ ");
            dollar.setVisibility(View.VISIBLE);
            editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        }

    }



    private void setImeOptions(){
        if(eImeOptions == EImeOptions.ACTION_NEXT)
            editText.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        if(eImeOptions == EImeOptions.ACTION_DONE)
            editText.setImeOptions(EditorInfo.IME_ACTION_DONE);
        if(eImeOptions == EImeOptions.NORMAL)
            editText.setImeOptions(EditorInfo.IME_ACTION_UNSPECIFIED);
    }

    private void setListeners() {
        setOnClickListener(this);

        if (eIsPassword) {
            imageView.setOnClickListener(v -> {

                if (password_hide) {
                    password_hide = false;
                    editText.setInputType(
                            TYPE_CLASS_TEXT |
                                    InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                    );
                    imageView.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_eye));
                } else {
                    password_hide = true;
                    editText.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    imageView.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_eye_off));
                }
                editText.setSelection(editText.getText().length());
            });
        }
    }

    public void setExtendedViewOnClickListener(ExtendedViewOnClickListener extendedViewOnClickListener) {
        this.extendedViewOnClickListener = extendedViewOnClickListener;
    }

    public void setError(CharSequence error) {
        setBackgroundResource(R.drawable.rectangle_card_round_corners_outlined_red);
        //sajad said that remove all error, red background is enough
        //editText.setError(error);
        //Due to new comments, I cancel my suggestion. So I comment delete below lines, we just show
        //red border for errors.
        //errorView.setVisibility(View.VISIBLE);
        //errorView.setText(error);
    }

    public void addTextChangedListener(TextWatcher textWatcher) {
        editText.addTextChangedListener(textWatcher);
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

    public int geteInputType() {
        return eInputType;
    }

    public void seteInputType(int eInputType) {
        this.eInputType = eInputType;
    }

    @Override
    public void onFocusChange(View view, boolean focused) {
        errorView.setVisibility(View.GONE);
        if (focused)
            setBackgroundResource(R.drawable.rectangle_card_round_corners_outlined_primary);
        else
            setBackgroundResource(R.drawable.rectangle_card_round_corners_outlined);
    }

    public void setSelection(int i) {
        editText.setSelection(i);
    }

    @Override
    public boolean onEditorAction(TextView textView,  int actionId, KeyEvent keyEvent) {
        if(actionId == EditorInfo.IME_ACTION_DONE){
            editText.clearFocus();
        }
        return false;
    }


    public interface EInputType {

        int TEXT = 0;
        int INTEGER = 1;
        int EMAIL = 2;
        int PASSWORD = 3;
        int PHONE = 4;
        int SUBURB = 5;
        int CALENDAR = 6;
        int BUDGET = 7;
    }

    public interface EImeOptions {

        int ACTION_NEXT = 0;
        int ACTION_DONE = 1;
        int NORMAL = 2;
    }

    public interface ExtendedViewOnClickListener {
        public void onClick();
    }


}
