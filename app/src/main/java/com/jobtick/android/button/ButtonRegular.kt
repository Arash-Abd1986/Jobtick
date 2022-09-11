package com.jobtick.android.button

import android.content.Context
import android.graphics.Canvas
import androidx.appcompat.widget.AppCompatButton
import android.util.AttributeSet
import androidx.core.content.res.ResourcesCompat
import com.jobtick.android.R

class ButtonRegular : AppCompatButton {
    constructor(context: Context?) : super(context!!) {
        val face = ResourcesCompat.getFont(context, R.font.roboto_regular)
        this.typeface = face
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(
        context!!, attrs
    ) {
        val face = ResourcesCompat.getFont(context, R.font.roboto_regular)
        this.typeface = face
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyle: Int) : super(
        context!!, attrs, defStyle
    ) {
        val face = ResourcesCompat.getFont(context, R.font.roboto_regular)
        this.typeface = face
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
    }
}