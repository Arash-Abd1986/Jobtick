package com.jobtick.android

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ImageSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.tasks.Task
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputLayout
import com.google.android.material.textview.MaterialTextView
import com.google.firebase.installations.FirebaseInstallations
import com.google.firebase.installations.InstallationTokenResult
import com.jobtick.android.activities.DashboardActivity
import com.jobtick.android.material.ui.landing.OnboardingActivity
import com.jobtick.android.utils.isLetter
import com.jobtick.android.utils.isNumeric


class AddNameLastNameFragment : Fragment() {

    private lateinit var activity: OnboardingActivity
    private lateinit var next: MaterialButton
    private lateinit var fname: TextInputLayout
    private lateinit var lname: TextInputLayout
    private lateinit var txtError: MaterialTextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_name_last_name, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initVars()
    }



    private fun initVars() {
        activity = (requireActivity() as OnboardingActivity)
        next = requireView().findViewById(R.id.btn_next)
        fname = requireView().findViewById(R.id.name)
        lname = requireView().findViewById(R.id.lname)
        txtError = requireView().findViewById(R.id.error)

        next.setOnClickListener {
            resetError()
            if (checkValidation()) {
                val intent = Intent(requireActivity(), DashboardActivity::class.java)
                startActivity(intent)
            }
        }
    }

    private fun resetError() {
        fname.error = ""
        lname.error = ""
        txtError.visibility = View.GONE
    }

    private fun setError(error: String) {
        txtError.visibility = View.VISIBLE
        txtError.text = error
    }

    private fun checkValidation(): Boolean {
        when {
            fname.editText?.text.isNullOrEmpty() && lname.editText?.text.isNullOrEmpty()  -> {
                setError("Please enter your first name and last name")
                return false
            }
            fname.editText?.text.isNullOrEmpty() -> {
                setError("Please enter your first name", fname)
                return false
            }
            !fname.editText?.text.toString().isLetter() -> {
                setError("Your first name should only contain letters", fname)
                return false
            }
            lname.editText?.text.isNullOrEmpty() -> {
                setError("Please enter your last name", lname)
                return false
            }
            !lname.editText?.text.toString().isLetter() -> {
                setError("Your last name should only contain letters", lname)
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