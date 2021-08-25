package com.jobtick.android.fragments

import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import android.os.Bundle
import com.jobtick.android.R
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

class ServiceFeeInfoBottomSheet : BottomSheetDialogFragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.ThemeOverlay_BottomSheetDialog)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.service_fee_info_bottom_sheet, container, false)
    }
}