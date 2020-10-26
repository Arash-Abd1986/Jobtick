package com.jobtick.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.media.Image;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.jobtick.EditText.EditTextMedium;
import com.jobtick.EditText.EditTextRegular;
import com.jobtick.R;
import com.jobtick.TextView.TextViewSemiBold;


public class ExtendedEntryText extends FrameLayout implements View.OnClickListener {

    private String mTitle;
    private String mContent;
    private boolean mIsPassword;
    private EditTextMedium editText;
    private TextViewSemiBold textView;
    private ImageView imageView;
    private boolean password_hide = true;

    public ExtendedEntryText(@NonNull Context context) {
        super(context);
        LayoutInflater.from(context).inflate(R.layout.view_extended_entry_text, this);
        setListeners();
    }

    public ExtendedEntryText(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initViews(context, attrs);
    }

    public ExtendedEntryText(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initViews(context, attrs);
    }

    public ExtendedEntryText(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initViews(context, attrs);
    }

    public void initViews(Context context, @Nullable AttributeSet attrs) {

        TypedArray sharedAttribute = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.ExtendedEntryText,
                0, 0);

        try {
            mTitle = sharedAttribute.getString(R.styleable.ExtendedEntryText_eTitle);
            mContent = sharedAttribute.getString(R.styleable.ExtendedEntryText_eContent);
            mIsPassword = sharedAttribute.getBoolean(R.styleable.ExtendedEntryText_eIsPassword, false);
        } finally {
             sharedAttribute.recycle();
        }

        LayoutInflater.from(context).inflate(R.layout.view_extended_entry_text, this);

        editText = (EditTextMedium) findViewById(R.id.content);
        textView = (TextViewSemiBold) findViewById(R.id.title);
        imageView = (ImageView) findViewById(R.id.img_btn_password_toggle);

        textView.setText(mTitle);
        editText.setText(mContent);

        if(mIsPassword){
            imageView.setVisibility(VISIBLE);
            editText.setInputType(
                    InputType.TYPE_CLASS_TEXT |
                            InputType.TYPE_TEXT_VARIATION_PASSWORD);
        }
        else
            imageView.setVisibility(GONE);

        setListeners();
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        this.mTitle = title;
        textView.setText(title);
        invalidate();
        requestLayout();
    }

    public String getContent(){
        return mContent;
    }

    public String getText(){
        return mContent;
    }

    public void setContent(String content) {
        this.mContent = content;
        editText.setText(content);
        invalidate();
        requestLayout();
    }



    public void setError(CharSequence error){
        editText.setError(error);
    }

    private void setListeners(){

        Log.i("extendedEntryText", "setting listeners");
        setOnClickListener(this);

        if(mIsPassword) {
            Log.i("extendedEntryText", "setting listeners 2");
            imageView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (password_hide) {
                        Log.i("ExtendedEntry", "hide");
                        password_hide = false;
                        editText.setInputType(
                                InputType.TYPE_CLASS_TEXT |
                                        InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                        );
                        imageView.setImageDrawable(getContext().getResources().getDrawable(R.drawable.ic_eye));
                    } else {
                        Log.i("ExtendedEntry", "show");
                        password_hide = true;
                        editText.setTransformationMethod(PasswordTransformationMethod.getInstance());
                        imageView.setImageDrawable(getContext().getResources().getDrawable(R.drawable.ic_eye_off));
                    }
                }
            });
        }
    }

    @Override
    public void onClick(View v) {
        Log.i("extendedEditText", "clicked");
        //       editText.requestFocusFromTouch();
//        InputMethodManager imm = (InputMethodManager) getContext()
//                .getSystemService(Context.INPUT_METHOD_SERVICE);
//        imm.showSoftInput(editText, 0);
    }

    private void showKeyboard(EditTextRegular editText) {
        editText.post(new Runnable() {
            @Override
            public void run() {
                InputMethodManager imm = (InputMethodManager) getContext()
                        .getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(editText, 0);
            }
        });
    }

//    private float pxFromDp(float dp) {
//        return dp * getContext().getResources().getDisplayMetrics().density;
//    }
//
//    private int dpFromPx(float px){
//        return (int) (px / getResources().getDisplayMetrics().density);
//    }
}
