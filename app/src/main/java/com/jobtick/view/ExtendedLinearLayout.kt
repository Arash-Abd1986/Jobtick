package com.jobtick.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import com.jobtick.R

class ExtendedLinearLayout(context: Context, attrs: AttributeSet): LinearLayout(context, attrs) {


    override fun getOnFocusChangeListener(): OnFocusChangeListener {
        if(onFocusChangeToChildListener != null) {
            val child = onFocusChangeToChildListener!!.getChildView()

            if(this.indexOfChild(child) != -1)
                child.requestFocus()
        }
        return super.getOnFocusChangeListener()


    }

    private var onFocusChangeToChildListener: OnFocusChangeToChildListener? = null

    interface OnFocusChangeToChildListener {
        fun getChildView(): View
    }


    fun setOnFocusChangeToChildListener(onFocusChangeToChildListener: OnFocusChangeToChildListener){
        this.onFocusChangeToChildListener = onFocusChangeToChildListener
    }
}