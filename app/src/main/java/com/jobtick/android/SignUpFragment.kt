package com.jobtick.android

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.ImageSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getColor
import androidx.fragment.app.Fragment
import com.google.android.gms.tasks.Task
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputLayout
import com.google.android.material.textview.MaterialTextView
import com.google.firebase.installations.FirebaseInstallations
import com.google.firebase.installations.InstallationTokenResult
import com.jobtick.android.activities.CompleteRegistrationActivity
import com.jobtick.android.activities.DashboardActivity
import com.jobtick.android.material.ui.landing.OnboardingActivity
import com.jobtick.android.models.UserAccountModel
import com.jobtick.android.utils.FireBaseEvent
import com.jobtick.android.utils.SessionManager


class SignUpFragment : Fragment() {

    private lateinit var sessionManagerA: SessionManager
    private lateinit var activity: OnboardingActivity
    private lateinit var fireBaseEvent: FireBaseEvent
    private lateinit var next: MaterialButton
    private lateinit var edtEmail: TextInputLayout
    private lateinit var edtPassword: TextInputLayout
    private lateinit var txtError: MaterialTextView
    private lateinit var title: MaterialTextView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sign_in_v2, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initVars()
        setTitle()
    }

    private fun setTitle() {
        val sb: Spannable =
            SpannableString(getString(R.string.login_to_jobtick))
        sb.setSpan(
            ForegroundColorSpan(
                getColor(requireContext(), R.color.primary)
            ),
            9,
            sb.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        title.text = sb
    }


    private fun initVars() {
        //fireBaseEvent = FireBaseEvent.getInstance(requireActivity())
        sessionManagerA = SessionManager(requireContext())
        activity = (requireActivity() as OnboardingActivity)
        next = requireView().findViewById(R.id.btn_next)
        edtEmail = requireView().findViewById(R.id.email)
        edtPassword = requireView().findViewById(R.id.password)
        edtPassword.visibility = View.GONE
        txtError = requireView().findViewById(R.id.error)
        title = requireView().findViewById(R.id.title)
        next.setOnClickListener {
            resetError()
            if (checkValidation())
                activity.navController.navigate(R.id.activateAccountFragment)
        }
    }


    private fun checkValidation(): Boolean {
        when {
            edtEmail.editText?.text.isNullOrEmpty() -> {
                setError("Please enter your email address", edtEmail)
                return false
            }
        }
        return true
    }

    private fun resetError() {
        edtPassword.error = ""
        edtEmail.error = ""
        txtError.visibility = View.GONE
    }


    private fun setError(error: String, txtInput: TextInputLayout) {
        val errorDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.ic_error)
        val ss = SpannableString("    $error")
        errorDrawable!!.setBounds(0, 0, errorDrawable.intrinsicWidth, errorDrawable.intrinsicHeight)
        val span = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ImageSpan(errorDrawable, ImageSpan.ALIGN_CENTER)
        } else {
            ImageSpan(errorDrawable, ImageSpan.ALIGN_BOTTOM)
        }
        ss.setSpan(span, 0, 3, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
        txtInput.error = ss
    }
}