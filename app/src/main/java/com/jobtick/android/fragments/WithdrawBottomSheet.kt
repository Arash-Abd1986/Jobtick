package com.jobtick.android.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.DialogFragment
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton
import com.jobtick.android.R
import org.jetbrains.annotations.NotNull

class WithdrawBottomSheet(private val withdrawInterface: @NotNull Withdraw,private val offerId: Int) : BottomSheetDialogFragment() {
    var cancel: RelativeLayout? = null
    var withdraw: RelativeLayout? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.bottomsheet_withdraw, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        cancel = view.findViewById(R.id.rl_cancel)
        withdraw = view.findViewById(R.id.withdraw)
        withdraw!!.setOnClickListener {
            dismiss()
            withdrawInterface.startWithdraw(offerId)
        }
        cancel!!.setOnClickListener { dismiss() }


    }

    interface Withdraw {
        fun startWithdraw(int: Int)
    }

}