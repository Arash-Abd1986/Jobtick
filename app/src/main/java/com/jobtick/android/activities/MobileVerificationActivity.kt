package com.jobtick.android.activities

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.google.android.material.appbar.MaterialToolbar
import com.jobtick.android.R
import com.jobtick.android.payment.VerifyPhoneNumber
import com.jobtick.android.payment.VerifyPhoneNumberImpl
import com.jobtick.android.widget.ExtendedEntryText

class MobileVerificationActivity : ActivityBase() {

    private var toolbar: MaterialToolbar? = null
    private var phoneVerifyMessage: TextView? = null
    private var lytBtnUpdate: LinearLayout? = null
    private var etOtp: ExtendedEntryText? = null
    private var lytBottom: LinearLayout? = null
    private var phoneNumber: String? = null
    private var otpSent = false
    private var verifyPhoneNumber: VerifyPhoneNumber? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mobile_verification)
        setIDs()
        phoneNumber = intent.getStringExtra("phone_number")
        val str = phoneVerifyMessage!!.text.toString() + " " +
                phoneNumber
        phoneVerifyMessage!!.text = str
        initToolbar()
        lytBtnUpdate!!.setOnClickListener { v: View? ->
            if (otpSent) {
                if (!validationVerify()) return@setOnClickListener
                showProgressDialog()
                verifyPhoneNumber!!.verify(etOtp!!.text)
            } else {
                showToast("Request SMS verification first.", this@MobileVerificationActivity)
            }
        }
        verifyPhoneNumber = object : VerifyPhoneNumberImpl(this, sessionManager) {
            override fun onSuccess(successType: SuccessType) {
                hideProgressDialog()
                if (successType == SuccessType.OTP) {
                    showSuccessToast("SMS verification code is sent.", this@MobileVerificationActivity)
                    otpSent = true
                } else if (successType == SuccessType.Verify) {
                    setResult(EditProfileActivity.PHONE_VERIFICATION_REQUEST_CODE)
                    finish()
                }
            }

            override fun onError(e: Exception) {
                hideProgressDialog()
                showToast(e.message, this@MobileVerificationActivity)
            }

            override fun onValidationError(errorType: ErrorType, message: String) {
                hideProgressDialog()
                if (errorType == ErrorType.UN_AUTHENTICATED_USER) unauthorizedUser() else showToast(message, this@MobileVerificationActivity)
            }
        }
        verifyPhoneNumber!!.sendOTP(phoneNumber)
    }

    private fun setIDs() {
        toolbar = findViewById(R.id.toolbar)
        phoneVerifyMessage = findViewById(R.id.phone_verify_message)
        lytBtnUpdate = findViewById(R.id.lyt_btn_verify)
        etOtp = findViewById(R.id.edt_verification_code)
        lytBottom = findViewById(R.id.lyt_bottom)
    }

    private fun initToolbar() {
        toolbar!!.setNavigationIcon(R.drawable.ic_back)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.title = "Mobile number verification"
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun validationVerify(): Boolean {
        if (etOtp!!.text.length != 6) {
            etOtp!!.setError("Enter 6 digits verification code.")
            return false
        }
        return true
    }
}