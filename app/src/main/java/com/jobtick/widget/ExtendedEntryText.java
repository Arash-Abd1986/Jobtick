package com.jobtick.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.core.content.ContextCompat;
import com.jobtick.R;

import static android.text.InputType.TYPE_CLASS_TEXT;


public class ExtendedEntryText extends RelativeLayout implements View.OnClickListener, View.OnFocusChangeListener {

    private String eTitle;
    private String eContent;
    private TextView textView;
    private EditText editText;
    private ImageView imageView;
    private boolean eIsPassword;
    private int eInputType;
    private boolean password_hide = true;

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
            String inputType = sharedAttribute.getString(R.styleable.ExtendedEntryText_eInputType);
            if(inputType != null && !inputType.isEmpty())
                eInputType = Integer.parseInt(inputType);
        } finally {
            sharedAttribute.recycle();
        }

        //Inflate and attach the content
        LayoutInflater.from(context).inflate(R.layout.view_extended_entry_text, this);

        setBackgroundResource(R.drawable.rectangle_card_round_corners_outlined);

        editText = (EditText) findViewById(R.id.content);
        textView = (TextView) findViewById(R.id.title);
        imageView = (ImageView) findViewById(R.id.img_btn_password_toggle);

        textView.setText(eTitle);
        editText.setText(eContent);

        editText.setOnFocusChangeListener(this);
        setInputType();
        setListeners();
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

    private void setInputType() {
        imageView.setVisibility(GONE);

        if (eInputType == EInputType.INTEGER) {
            editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        } else if (eInputType == EInputType.EMAIL)
            editText.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        else if (eInputType ==  EInputType.PASSWORD) {
            eIsPassword = true;
            imageView.setVisibility(VISIBLE);

        } else if (eInputType == EInputType.PHONE)
            editText.setInputType(InputType.TYPE_CLASS_PHONE);
        else
            editText.setInputType(TYPE_CLASS_TEXT);
    }

    private void setListeners() {
        setOnClickListener(this);

        if (eIsPassword) {
            imageView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
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
                }
            });
        }
    }

    private void setError(CharSequence error){
        editText.setError(error);
    }

    public String geteTitle() {
        return eTitle;
    }

    public void seteTitle(String eTitle) {
        this.eTitle = eTitle;
    }

    public String geteContent() {
        return eContent;
    }

    public void seteContent(String eContent) {
        this.eContent = eContent;
    }

    public int geteInputType() {
        return eInputType;
    }

    public void seteInputType(int eInputType) {
        this.eInputType = eInputType;
    }

    @Override
    public void onFocusChange(View view, boolean focused) {
        if(focused)
            setBackgroundResource(R.drawable.rectangle_card_round_corners_outlined_primary);
        else
            setBackgroundResource(R.drawable.rectangle_card_round_corners_outlined);
    }

    public interface EInputType {

        int TEXT = 0;
        int INTEGER = 1;
        int EMAIL = 2;
        int PASSWORD = 3;
        int PHONE = 4;
    }


}
