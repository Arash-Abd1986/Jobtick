package com.jobtick.android

import `in`.aabhasjindal.otptextview.OTPListener
import `in`.aabhasjindal.otptextview.OtpTextView
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat.getColor
import com.google.android.material.button.MaterialButton
import com.google.android.material.textview.MaterialTextView
import com.jobtick.android.material.ui.landing.OnboardingActivity


class ActivateAccountFragment : Fragment(), OTPListener {
    private lateinit var txtTimer: MaterialTextView
    private lateinit var otpView: OtpTextView
    private lateinit var btnForgetPass: MaterialButton
    private lateinit var activity: OnboardingActivity

    private var time = ""
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_activate_account, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        txtTimer = requireView().findViewById(R.id.timer)
        otpView = requireView().findViewById(R.id.otp_view)
        btnForgetPass = requireView().findViewById(R.id.btn_forget_pass)
        activity = (requireActivity() as OnboardingActivity)

        val timer = object : CountDownTimer(3 * 60 * 1000, 1000) {

            override fun onTick(millisUntilFinished: Long) {
                time = getTime(
                    millisUntilFinished / 1000
                )
                val sb: Spannable =
                    SpannableString(
                        getString(R.string.the_code_will_expire_in) + " " + time
                    )
                sb.setSpan(
                    ForegroundColorSpan(
                        getColor(requireContext(), R.color.neutral_dark)
                    ),
                    24,
                    sb.length,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                txtTimer.text = sb
            }

            override fun onFinish() {
                btnForgetPass.setTextColor(getColor(requireContext(), R.color.primary))
            }
        }.start()
        timer.start()
        btnForgetPass.setOnClickListener {
            btnForgetPass.setTextColor(getColor(requireContext(), R.color.neutral_light_400))
            if (time == "00:00")
                timer.start()
        }
        otpView.otpListener = this
    }

    private fun getTime(sec: Long): String {
        val minutes = (sec % 3600) / 60;
        val seconds = sec % 60;

        return String.format("%02d:%02d", minutes, seconds);
    }

    override fun onInteractionListener() {

    }

    override fun onOTPComplete(otp: String?) {
        activity.navController.navigate(R.id.addPassFragment)
    }
}