package com.jobtick.android.activities

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.jobtick.android.R
import com.jobtick.android.models.response.getbalance.CreditCardModel
import com.jobtick.android.utils.Constant

class CashOutActivity : ActivityBase() {
    private var availableBalance: TextView? = null
    private var cashout: Button? = null
    private var requestedCashout: EditText? = null
    private var creditCardModel: CreditCardModel? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.cashout_activity)
        availableBalance = findViewById(R.id.available_balance)
        cashout = findViewById(R.id.cashout)
        requestedCashout = findViewById(R.id.requested_cashout)
        creditCardModel = intent.extras!![Constant.CASH_OUT] as CreditCardModel?
        cashout!!.setOnClickListener {
            if (validation())
                requestCashout()
        }
        init()
    }

    private fun init() {}
    private fun requestCashout() {
    }

    private fun validation(): Boolean {
        if (requestedCashout!!.text.isEmpty()) {
            showToast("Please enter your requested amount.", this)
            return false
        }
        return true
    }
}