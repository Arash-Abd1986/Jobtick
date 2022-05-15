package com.jobtick.android.fragments

import android.content.Context
import com.jobtick.android.R

class ConfirmBlockTaskBottomSheet(
    context: Context,
    name: String?,
    title: Int = R.string.confirm_block,
    description: Int = R.string.are_you_sure_block,
    pButton: Int = R.string.block
) : AbstractConfirmBottomSheetv2(
    context.getString(title),
    context.getString(description, name),
    context.getString(pButton),
    context.getString(R.string.cancel)
) {
    var listener: NoticeListener? = null
    public override fun onBlueButtonClick() {
        listener!!.onBlockConfirmClick()
        dismiss()
    }

    public override fun onRedButtonClick() {
        checkNotNull(listener) { "NoticeListener interface must be set." }
        dismiss()
    }

    interface NoticeListener {
        fun onBlockConfirmClick()
    }
}
