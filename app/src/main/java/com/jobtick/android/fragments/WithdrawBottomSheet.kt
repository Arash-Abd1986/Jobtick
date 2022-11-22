package com.jobtick.android.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.fragment.app.DialogFragment
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.textview.MaterialTextView
import com.jobtick.android.R
import com.jobtick.android.models.OfferModel
import com.jobtick.android.utils.TimeAgo
import com.jobtick.android.utils.setSpanColor
import org.jetbrains.annotations.NotNull

class WithdrawBottomSheet(private val withdrawInterface: @NotNull Withdraw, private val offerModel: OfferModel) : BottomSheetDialogFragment() {
    private lateinit var withdraw: MaterialButton
    private lateinit var txtOffer: MaterialTextView
    private lateinit var txtInfo: MaterialTextView
    private var infoStart = "YYou have submitted an offer for this job"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.bottomsheet_withdraw, container, false)
    }


    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        withdraw = view.findViewById(R.id.withdraw)
        txtOffer = view.findViewById(R.id.txtOffer)
        txtInfo = view.findViewById(R.id.txtInfo)
        txtOffer.text = offerModel.message
        txtInfo.text = infoStart + " ${offerModel.createdAt}"
        txtInfo.setSpanColor(infoStart.length, txtInfo.text.length, resources.getColor(R.color.neutral_dark))
        withdraw.setOnClickListener {
            dismiss()
            withdrawInterface.startWithdraw(offerModel.id)
        }
    }

    interface Withdraw {
        fun startWithdraw(int: Int)
    }

}