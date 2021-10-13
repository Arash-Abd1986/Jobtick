package com.jobtick.android.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.RelativeLayout
import com.google.android.material.textview.MaterialTextView
import com.jobtick.android.R

class StatusLayout @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0) : RelativeLayout(context, attrs, defStyle) {
    private var status: String? = null
    private val materialTextView: MaterialTextView


    init {
        val sharedAttribute = context.theme.obtainStyledAttributes(
                attrs,
                R.styleable.ExtendedCommentText,
                0, 0)
        status = try {
            sharedAttribute.getString(R.styleable.StatusLayout_status)
        } finally {
            sharedAttribute.recycle()
        }

        LayoutInflater.from(context).inflate(R.layout.view_status_layout, this)
        materialTextView = findViewById(R.id.txt_status)
        setStatus(status)
    }

    fun setStatus(status: String?) {
        when(status){
            context.getString(R.string.open) ->{
                setBackgroundResource(R.drawable.shape_cirlce_open)
                materialTextView.text = context.getString(R.string.open)
            }
            context.getString(R.string.assigned) ->{
                setBackgroundResource(R.drawable.shape_cirlce_assigned)
                materialTextView.text = context.getString(R.string.assigned)
            }
            context.getString(R.string.cancelled) ->{
                setBackgroundResource(R.drawable.shape_cirlce_canceled)
                materialTextView.text = context.getString(R.string.cancelled)
            }
            context.getString(R.string.completed) ->{
                setBackgroundResource(R.drawable.shape_cirlce_complete)
                materialTextView.text = context.getString(R.string.completed)
            }
            context.getString(R.string.closed) ->{
                setBackgroundResource(R.drawable.shape_cirlce_complete)
                materialTextView.text = context.getString(R.string.closed)
            }
            context.getString(R.string.overdue) ->{
                setBackgroundResource(R.drawable.shape_cirlce_complete)
                materialTextView.text = context.getString(R.string.completed)
            }
            context.getString(R.string.posted) ->{
                setBackgroundResource(R.drawable.shape_cirlce_open)
                materialTextView.text = context.getString(R.string.posted)
            }
            context.getString(R.string.offered) ->{
                setBackgroundResource(R.drawable.shape_cirlce_open)
                materialTextView.text = context.getString(R.string.offered)
            }
        }
    }

}