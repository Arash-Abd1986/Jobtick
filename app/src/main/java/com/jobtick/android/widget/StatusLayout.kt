package com.jobtick.android.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.RelativeLayout
import com.google.android.material.textview.MaterialTextView
import com.jobtick.android.R

class StatusLayout @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0) : RelativeLayout(context, attrs, defStyle) {
    private var status: String? = null
    private val materialTextView: MaterialTextView
    private val im_dot: ImageView


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
        im_dot = findViewById(R.id.im_dot)
        setStatus(status)
    }

    fun setStatus(status: String?) {
        when(status){
            context.getString(R.string.open) ->{
                im_dot.setImageResource(R.drawable.shape_cirlce_open)
                materialTextView.text = context.getString(R.string.open)
                materialTextView.setTextColor(context.getColor(R.color.newOpen))
            }
            context.getString(R.string.assigned) ->{
                im_dot.setImageResource(R.drawable.shape_cirlce_assigned)
                materialTextView.text = context.getString(R.string.assigned)
                materialTextView.setTextColor(context.getColor(R.color.newAssigned))

            }
            context.getString(R.string.cancelled) ->{
                im_dot.setImageResource(R.drawable.shape_cirlce_canceled)
                materialTextView.text = context.getString(R.string.cancelled)
                materialTextView.setTextColor(context.getColor(R.color.newCanceled))

            }
            context.getString(R.string.completed) ->{
                im_dot.setImageResource(R.drawable.shape_cirlce_complete)
                materialTextView.text = context.getString(R.string.completed)
                materialTextView.setTextColor(context.getColor(R.color.newComplete))

            }
            context.getString(R.string.closed) ->{
                im_dot.setImageResource(R.drawable.shape_cirlce_closed)
                materialTextView.text = context.getString(R.string.closed)
                materialTextView.setTextColor(context.getColor(R.color.newClose))
            }
            context.getString(R.string.overdue) ->{
                im_dot.setImageResource(R.drawable.shape_cirlce_complete)
                materialTextView.text = context.getString(R.string.completed)
                materialTextView.setTextColor(context.getColor(R.color.newComplete))
            }
            context.getString(R.string.posted) ->{
                im_dot.setImageResource(R.drawable.shape_cirlce_open)
                materialTextView.text = context.getString(R.string.posted)
                materialTextView.setTextColor(context.getColor(R.color.newOpen))
            }
            context.getString(R.string.offered) ->{
                im_dot.setImageResource(R.drawable.shape_cirlce_open)
                materialTextView.text = context.getString(R.string.offered)
                materialTextView.setTextColor(context.getColor(R.color.newOpen))
            }
        }
    }

}