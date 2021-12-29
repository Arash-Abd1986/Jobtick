package com.jobtick.android.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.method.DigitsKeyListener;
import android.text.method.PasswordTransformationMethod;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.jobtick.android.R;

import static android.text.InputType.TYPE_CLASS_TEXT;


public class ExtendedEntryTextNewDesign extends RelativeLayout implements View.OnClickListener, View.OnFocusChangeListener, TextView.OnEditorActionListener, TextWatcher {

    private String eTitle;
    private final String eContent;
    private final String eHint;
    private final TextView textView;
    private final AutoCompleteTextView autoCompleteTextView;
    private final TextView errorView;
    private final TextView dollar;
    private final EditText editText;
    private final Button verify;
    private EditText secondEditText;
    private final ImageView imageView;
    private int eBoxSize = 0;
    private boolean eIsPassword;
    private final boolean eStartFocus;
    private final boolean eNeedHeader;
    private int eInputType;
    private boolean eVerifyVisible = true;
    private int eImeOptions;
    private boolean password_hide = true;
    private boolean eIsEnable = true;
    private final int tHigh = 0;
    private ExtendedViewOnClickListener extendedViewOnClickListener;
    private TextWatcher textWatcher;

    private int eMaxCharNumber = 0;


    public ExtendedEntryTextNewDesign(Context context) {
        this(context, null);
    }

    public ExtendedEntryTextNewDesign(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ExtendedEntryTextNewDesign(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        TypedArray sharedAttribute = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.ExtendedEntryText,
                0, 0);

        try {
            eTitle = sharedAttribute.getString(R.styleable.ExtendedEntryText_eTitle);
            eContent = sharedAttribute.getString(R.styleable.ExtendedEntryText_eContent);
            eHint = sharedAttribute.getString(R.styleable.ExtendedEntryText_eHint);
            eMaxCharNumber = sharedAttribute.getInt(R.styleable.ExtendedEntryText_eMaxCharNumber, 0);
            eIsEnable = sharedAttribute.getBoolean(R.styleable.ExtendedEntryText_eIsEnable, true);
            eNeedHeader = sharedAttribute.getBoolean(R.styleable.ExtendedEntryText_eNeedHeader, true);
            eVerifyVisible = sharedAttribute.getBoolean(R.styleable.ExtendedEntryText_eVerifyVisible, true);
            eStartFocus = sharedAttribute.getBoolean(R.styleable.ExtendedEntryText_eStartFocus, false);
            String inputType = sharedAttribute.getString(R.styleable.ExtendedEntryText_eInputType);
            if (inputType != null && !inputType.isEmpty())
                eInputType = Integer.parseInt(inputType);

            String boxSize = sharedAttribute.getString(R.styleable.ExtendedEntryText_eBoxSize);
            if (boxSize != null && !boxSize.isEmpty())
                eBoxSize = Integer.parseInt(boxSize);

            String imeOptions = sharedAttribute.getString(R.styleable.ExtendedEntryText_eImeOptions);
            if (imeOptions != null && !imeOptions.isEmpty())
                eImeOptions = Integer.parseInt(imeOptions);

        } finally {
            sharedAttribute.recycle();
        }

        //Inflate and attach the content
        if (eBoxSize == EBoxSize.NORMAL)
            LayoutInflater.from(context).inflate(R.layout.view_extended_entry_text, this);
        else if (eBoxSize == EBoxSize.SMALL)
            LayoutInflater.from(context).inflate(R.layout.view_extended_entry_text_small, this);

        setBackgroundResource(R.drawable.rectangle_card_round_corners_outlined);

        autoCompleteTextView = findViewById(R.id.content_auto_complete);
        if (eInputType == EInputType.AUTOCOMPLETE) {
            editText = autoCompleteTextView;
            secondEditText = findViewById(R.id.content);
        } else
            editText = findViewById(R.id.content);

        textView = findViewById(R.id.title);
        errorView = findViewById(R.id.error);
        imageView = findViewById(R.id.img_btn_password_toggle);
        dollar = findViewById(R.id.dollar);
        verify = findViewById(R.id.verifyButton);
        textView.setText(eTitle);
        editText.setText(eContent);
        editText.setHint(eTitle);
        if (!eNeedHeader){
            textView.setVisibility(GONE);
        }
        if (eHint != null)
            if (eHint.length() > 0)
                editText.setHint(eHint);
        editText.setEnabled(eIsEnable);

        editText.setOnFocusChangeListener(this);
        editText.setOnEditorActionListener(this);
        setInputType();
        setImeOptions();
        setListeners();

        if (eStartFocus) {
            editText.requestFocus();
            showKeyboard(editText);
        }
    }

    @Override
    public void onClick(View v) {
        if (eInputType == EInputType.SUBURB || eInputType == EInputType.CALENDAR || eInputType == EInputType.SPINNER) {
            if (extendedViewOnClickListener == null)
                throw new IllegalStateException(eInputType + " type selected, but ExtendedViewOnClickListener is not implemented.");

            extendedViewOnClickListener.onClick();
            return;
        }


        editText.requestFocus();
        editText.performClick();
        if (editText.getText().toString() != null && !editText.getText().toString().isEmpty())
            editText.setSelection(editText.getText().length());

        showKeyboard(editText);
    }

    private void setInputType() {
        imageView.setVisibility(GONE);
        textView.setTextColor(getResources().getColor(R.color.N100));
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
        else if (eInputType == EInputType.SUBURB || eInputType == EInputType.CALENDAR || eInputType == EInputType.SPINNER) {
            editText.setFocusable(false);
            editText.setInputType(InputType.TYPE_NULL);
            editText.setClickable(true);
            editText.setOnClickListener(v -> {
                extendedViewOnClickListener.onClick();
            });
        } else
            editText.setInputType(TYPE_CLASS_TEXT);


        if (eInputType == EInputType.SUBURB) {
            //TODO: these two lines for multi line for suburb is now working, need to fix it
//            editText.setMaxLines(2);
//            editText.setInputType(InputType.TYPE_TEXT_FLAG_IME_MULTI_LINE);
            editText.setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(getContext(), R.drawable.inset_suburb), null);
        } else {
            editText.setLines(1);
        }
        if (eInputType == EInputType.CALENDAR || eInputType == EInputType.CALENDAR_KEYBOARD) {
            editText.setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(getContext(), R.drawable.inset_calendar), null);
        }
        if (eInputType == EInputType.SPINNER) {
            editText.setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(getContext(), R.drawable.inset_chevron_down), null);
        }
        if (eInputType == EInputType.BUDGET) {
            editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        }
        if (eInputType == EInputType.AUTOCOMPLETE) {
            secondEditText.setVisibility(View.GONE);
            autoCompleteTextView.setVisibility(View.VISIBLE);
            editText.setInputType(TYPE_CLASS_TEXT);
        }
        if (eInputType == EInputType.VERIFY) {
            editText.setInputType(InputType.TYPE_CLASS_PHONE);

            if (eVerifyVisible) {
                verify.setVisibility(View.VISIBLE);
            } else
                verify.setVisibility(View.GONE);

            verify.setOnClickListener(v -> {
                if (extendedViewOnClickListener == null)
                    throw new IllegalStateException(eInputType + " type selected, but ExtendedViewOnClickListener is not implemented.");
                extendedViewOnClickListener.onClick();
            });
        }

        if (eInputType == EInputType.CALENDAR_KEYBOARD) {
            editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(5)});
            editText.setInputType(InputType.TYPE_CLASS_NUMBER);
            editText.setKeyListener(DigitsKeyListener.getInstance("0123456789/"));
        }

    }

    public void setAdapter(String[] items) {
        if (eInputType != EInputType.AUTOCOMPLETE)
            throw new IllegalStateException("for using adapter, you must select autoComplete as input type.");

        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, items);
        autoCompleteTextView.setAdapter(adapter);
    }


    private void setImeOptions() {
        if (eImeOptions == EImeOptions.ACTION_NEXT)
            editText.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        if (eImeOptions == EImeOptions.ACTION_DONE)
            editText.setImeOptions(EditorInfo.IME_ACTION_DONE);
        if (eImeOptions == EImeOptions.NORMAL)
            editText.setImeOptions(EditorInfo.IME_ACTION_UNSPECIFIED);
    }

    public void setFilter(int max) {
        editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(max)});

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

        editText.addTextChangedListener(this);
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
        this.textWatcher = textWatcher;
    }

    public void removeTextChangedListener(TextWatcher textWatcher) {
        this.editText.removeTextChangedListener(textWatcher);
    }

    @Override
    public void onFocusChange(View view, boolean focused) {
        errorView.setVisibility(View.GONE);
        if (focused) {
            textView.setTextColor(getResources().getColor(R.color.P300));
            setBackgroundResource(R.drawable.rectangle_card_round_corners_outlined_primary);
        } else {
            if (editText.getText().length() == 0)
                textView.setTextColor(getResources().getColor(R.color.N100));
            setBackgroundResource(R.drawable.rectangle_card_round_corners_outlined);
        }
    }

    public void setSelection(int i) {
        editText.setSelection(i);
    }

    @Override
    public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            hideKeyboard(editText);
        }
        return false;
    }

    private void showKeyboard(EditText editText) {
        editText.post(() -> {
            InputMethodManager imm = (InputMethodManager) getContext()
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(editText, 0);
        });
    }


    public void hideKeyboard(EditText editText) {
        editText.post(() -> {
            InputMethodManager imm = (InputMethodManager) getContext()
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
            editText.clearFocus();
        });
    }

    public boolean isIsEnable() {
        return eIsEnable;
    }

    public void setIsEnable(boolean eIsEnable) {
        this.eIsEnable = eIsEnable;
    }

    public String geteTitle() {
        return eTitle;
    }

    public void seteTitle(String eTitle) {
        this.eTitle = eTitle;
    }

    public void seteVerifyVisible(Boolean eVerifyVisible) {
        this.eVerifyVisible = eVerifyVisible;
        if (eVerifyVisible)
            this.verify.setVisibility(VISIBLE);
        else
            this.verify.setVisibility(GONE);
    }

    public Boolean geteVerifyVisible() {
        return this.eVerifyVisible;
    }

    public String geteContent() {
        return editText.getText().toString();
    }

    public String getText() {
        return editText.getText().toString();
    }

    public void setText(String content) {
        System.out.println("ExtendedEntryText: onSetText title: " + eTitle + " value: " + content);
        this.editText.setText(content);
    }

    public void seteContent(String eContent) {
        System.out.println("ExtendedEntryText: seteContent title: " + eTitle + " value: " + eContent);
        this.editText.setText(eContent);
    }

    public int geteInputType() {
        return eInputType;
    }

    public void seteInputType(int eInputType) {
        this.eInputType = eInputType;
    }

    @Nullable
    @Override
    protected Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putString("value", editText.getText().toString());
        bundle.putParcelable("state", super.onSaveInstanceState());
        System.out.println("ExtendedEntryText: onSaveInstanceState title: " + eTitle + " value: " + editText.getText());
        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {

        Bundle bundle = (Bundle) state;
        Parcelable newState = bundle.getParcelable("state");
        super.onRestoreInstanceState(newState);

        editText.setText(bundle.getString("value"));
        System.out.println("ExtendedEntryText: onRestoreInstanceState title: " + eTitle + " value: " + editText.getText());
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        if (textWatcher != null)
            textWatcher.beforeTextChanged(charSequence, i, i1, i2);
    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        if (charSequence.length() != 0) {
            if (eInputType == EInputType.BUDGET) {
                dollar.setText("$");
                dollar.setVisibility(View.VISIBLE);
            }
        } else {
            if (eInputType == EInputType.BUDGET) {
                dollar.setText("$");
                dollar.setVisibility(View.INVISIBLE);
            }
        }
        if (eMaxCharNumber != 0 && charSequence.length() > eMaxCharNumber) {
            editText.setText(charSequence.subSequence(0, eMaxCharNumber).toString());
            editText.setSelection(eMaxCharNumber);
        }

        if (textWatcher != null)
            textWatcher.onTextChanged(charSequence, i, i1, i2);

    }

    @Override
    public void afterTextChanged(Editable editable) {
        if (textWatcher != null)
            textWatcher.afterTextChanged(editable);


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
        int SPINNER = 8;
        int AUTOCOMPLETE = 9;
        int VERIFY = 10;
        int CALENDAR_KEYBOARD = 11;
    }

    public interface EImeOptions {

        int ACTION_NEXT = 0;
        int ACTION_DONE = 1;
        int NORMAL = 2;
    }

    public interface EBoxSize {
        int NORMAL = 0;
        int SMALL = 1;
    }

    public interface ExtendedViewOnClickListener {
        void onClick();
    }


}
