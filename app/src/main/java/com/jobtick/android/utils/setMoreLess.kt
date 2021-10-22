package com.jobtick.android.utils

import android.graphics.Color
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.widget.TextView

private var isUserPrefToMore = false
private var strMoreOrLess = "show more"

fun setMoreLess(txtDescription: TextView, description: String, lineNumber: Int) {
    txtDescription.text = description
    var spanS: Spannable? = null
    var spanF: Spannable? = null
    txtDescription.post {
        val lineCount = txtDescription.lineCount
        if (lineCount > lineNumber) {
            spanF = SpannableString(txtDescription.text.toString() + "  show less")
            spanF!!.setSpan(ForegroundColorSpan(Color.BLUE), txtDescription.text.toString().length, txtDescription.text.toString().length + 11, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            val end = txtDescription.layout.getLineStart(lineNumber) - 11
            spanS = SpannableString(txtDescription.text.toString().substring(0, end - 11) + "  show more")
            spanS!!.setSpan(ForegroundColorSpan(Color.BLUE), txtDescription.text.toString().substring(0, end - 10).length, txtDescription.text.toString().substring(0, end - 11).length + 11, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            txtDescription.text = spanS
            isUserPrefToMore = true
            if (isUserPrefToMore) {
                txtDescription.maxLines = lineNumber
            }
        }

    }

    txtDescription.setOnClickListener {
        if (strMoreOrLess.equals("show more", ignoreCase = true)) {
            if (spanF != null) {
                txtDescription.maxLines = Int.MAX_VALUE
                txtDescription.text = spanF
                strMoreOrLess = ("show less")
            }
        } else {
            if (spanS != null) {
                txtDescription.text = spanS
                txtDescription.maxLines = lineNumber
                strMoreOrLess = ("show more")
            }
        }
    }
}