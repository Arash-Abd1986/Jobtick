package com.jobtick.android.button

import android.content.Context
import android.graphics.Canvas
import androidx.appcompat.widget.AppCompatButton
import android.graphics.Typeface
import android.util.AttributeSet

class ButtonSemiBold : AppCompatButton {
    constructor(context: Context) : super(context) {
        val face = Typeface.createFromAsset(context.assets, "fonts/poppins_SemiBold.otf")
        this.typeface = face
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        val face = Typeface.createFromAsset(context.assets, "fonts/poppins_SemiBold.otf")
        this.typeface = face
    }

    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    ) {
        val face = Typeface.createFromAsset(context.assets, "fonts/poppins_SemiBold.otf")
        this.typeface = face
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
    }
}