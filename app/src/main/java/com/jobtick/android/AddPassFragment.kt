package com.jobtick.android

import android.os.Build
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.ImageSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getColor
import androidx.fragment.app.Fragment
import com.google.android.gms.tasks.Task
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputLayout
import com.google.android.material.textview.MaterialTextView
import com.google.firebase.installations.FirebaseInstallations
import com.google.firebase.installations.InstallationTokenResult
import com.jobtick.android.material.ui.landing.OnboardingActivity
import com.jobtick.android.utils.ExternalIntentHelper


class AddPassFragment : Fragment() {

    private lateinit var activity: OnboardingActivity
    private lateinit var next: MaterialButton
    private lateinit var edtPassword: TextInputLayout
    private lateinit var confirmPassword: TextInputLayout
    private lateinit var title: MaterialTextView
    private lateinit var txtBtnTerms: MaterialTextView
    private lateinit var cbTerms: AppCompatCheckBox
    private lateinit var txtError: MaterialTextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_pass, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initVars()
        setTitle()
    }

    private fun setTitle() {
        val sb: Spannable =
            SpannableString(getString(R.string.welcome_to_jobtick))
        sb.setSpan(
            ForegroundColorSpan(
                getColor(requireContext(), R.color.primary)
            ),
            9,
            sb.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        title.text = sb
        val sb2: Spannable =
            SpannableString(getString(R.string.by_joining_you_agree_to_jobtick_s_terms_of_services))
        sb2.setSpan(
            ForegroundColorSpan(
                getColor(requireContext(), R.color.primary)
            ),
            35,
            sb2.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        txtBtnTerms.text = sb2
    }


    private fun initVars() {
        activity = (requireActivity() as OnboardingActivity)
        next = requireView().findViewById(R.id.btn_next)
        edtPassword = requireView().findViewById(R.id.password)
        confirmPassword = requireView().findViewById(R.id.confirm_password)
        title = requireView().findViewById(R.id.title)
        txtBtnTerms = requireView().findViewById(R.id.txtBtn_terms)
        cbTerms = requireView().findViewById(R.id.cb_terms)
        txtError = requireView().findViewById(R.id.error)

        next.setOnClickListener {
            resetError()
            if (checkValidation())
                activity.navController.navigate(R.id.addNameLastNameFragment)
        }
        txtBtnTerms.setOnClickListener {
            ExternalIntentHelper.openLink(
                requireActivity(),
                "https://www.jobtick.com/terms"
            )
        }
    }

    private fun resetError() {
        edtPassword.error = ""
        confirmPassword.error = ""
        txtError.visibility = View.GONE
    }

    private fun setError(error: String) {
        txtError.visibility = View.VISIBLE
        txtError.text = error
    }

    private fun checkValidation(): Boolean {
        when {
            edtPassword.editText?.text.isNullOrEmpty() -> {
                setError("Please enter your password", edtPassword)
                return false
            }
            confirmPassword.editText?.text.isNullOrEmpty() -> {
                setError("Please confirm your password", confirmPassword)
                return false
            }
            confirmPassword.editText?.text.toString() != edtPassword.editText?.text.toString() -> {
                setError("Those passwords didn’t match, try again", confirmPassword)
                return false
            }
            !cbTerms.isChecked -> {
                setError("Before you can sign up, you must accept the Jobtick Terms of Services.")
                return false
            }
        }
        return true
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

    private val token: String
        get() {
            val token = arrayOf("")
            FirebaseInstallations.getInstance().getToken(true)
                .addOnCompleteListener { task: Task<InstallationTokenResult> ->
                    if (!task.isSuccessful) {
                        return@addOnCompleteListener
                    }

                    // Get new Instance ID token
                    token[0] = task.result.token
                }
            return token[0]
        }


}