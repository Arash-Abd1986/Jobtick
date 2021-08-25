package com.jobtick.android.fragments


class VerifyAccountFragment : AbstractVerifyAccountFragment() {

    public override fun whatNext() {
        val otp = edtVerificationCode.text.trim { it <= ' ' }
        if (validation()) authActivity.newEmailVerification(email, otp)
    }

    public override fun onResendOtp() {
        authActivity.newResendOtp(email)
    }
}