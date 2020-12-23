package com.jobtick.edit_text;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.util.AttributeSet;

import androidx.core.content.res.ResourcesCompat;

import com.jobtick.R;

import java.util.ArrayList;


public class EditTextRegular extends androidx.appcompat.widget.AppCompatEditText {
    ArrayList<GoEditTextListener> listeners;


    public EditTextRegular(Context context) {
        super(context);
        listeners = new ArrayList<>();
        Typeface face = ResourcesCompat.getFont(context, R.font.roboto_regular);
        this.setTypeface(face);
    }

    public EditTextRegular(Context context, AttributeSet attrs) {
        super(context, attrs);
        listeners = new ArrayList<>();
        Typeface face = ResourcesCompat.getFont(context, R.font.roboto_regular);
        this.setTypeface(face);
    }

    public EditTextRegular(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        listeners = new ArrayList<>();
        Typeface face = ResourcesCompat.getFont(context, R.font.roboto_regular);
        this.setTypeface(face);
    }

    protected void onDraw (Canvas canvas) {
        super.onDraw(canvas);


    }


    public void addListener(GoEditTextListener listener) {
        try {
            listeners.add(listener);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }


    public interface GoEditTextListener {
        void onUpdate();
    }


    /**
     * Here you can catch paste, copy and cut events
     */
    @Override
    public boolean onTextContextMenuItem(int id) {
        boolean consumed = super.onTextContextMenuItem(id);
        switch (id){
            case android.R.id.cut:
                onTextCut();
                break;
            case android.R.id.paste:
                onTextPaste();
                break;
            case android.R.id.copy:
                onTextCopy();
        }
        return consumed;
    }

    public void onTextCut(){
    }

    public void onTextCopy(){
    }


    /**
     * adding listener for Paste for example
     */
    public void onTextPaste(){
        for (GoEditTextListener listener : listeners) {
            listener.onUpdate();
        }
    }

}