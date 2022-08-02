package com.jobtick.android.material.ui.landing

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
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.tasks.Task
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputLayout
import com.google.android.material.textview.MaterialTextView
import com.google.firebase.installations.FirebaseInstallations
import com.google.firebase.installations.InstallationTokenResult
import com.jobtick.android.R
import com.jobtick.android.activities.DashboardActivity
import com.jobtick.android.models.UserAccountModel
import com.jobtick.android.utils.ExternalIntentHelper
import com.jobtick.android.utils.HttpStatus
import com.jobtick.android.utils.SessionManager
import com.jobtick.android.viewmodel.ChangePassViewModel
import org.json.JSONException
import org.json.JSONObject
import timber.log.Timber

class ForgetPassSecFragmentFragment : Fragment() {

    private lateinit var activity: OnboardingActivity
    private lateinit var next: MaterialButton
    private lateinit var edtPassword: TextInputLayout
    private lateinit var confirmPassword: TextInputLayout
    private lateinit var title: MaterialTextView
    private lateinit var txtBtnTerms: MaterialTextView
    private lateinit var cbTerms: AppCompatCheckBox
    private lateinit var txtError: MaterialTextView
    private lateinit var changePassViewModel: ChangePassViewModel
    private lateinit var sessionManagerA: SessionManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_forget_pass_sec_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initVars()
        initVM()
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
        sessionManagerA = SessionManager(requireContext())

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
    private fun initVM() {
        changePassViewModel = ViewModelProvider(this).get(ChangePassViewModel::class.java)
        changePassViewModel.changePassResponse().observe(requireActivity()) {
            activity.hideProgressDialog()
            try {

                if (it.has("success") && !it.isNull("success")) {
                    if (it.getBoolean("success")) {
                        proceedToCorrectActivity(sessionManagerA.userAccount)
                    }
                }
            } catch (e: JSONException) {
                Timber.e(e.toString())
                e.printStackTrace()
            }
        }
        changePassViewModel.getError2().observe(viewLifecycleOwner) {
            setError("Something went wrong")

        }
        changePassViewModel.getError().observe(viewLifecycleOwner, androidx.lifecycle.Observer { networkResponse ->
            if (networkResponse?.data != null) {
                val jsonError = String(networkResponse.data)
                // Print Error!
                Timber.e(jsonError)
                if (networkResponse.statusCode == HttpStatus.AUTH_FAILED) {
                    activity.unauthorizedUser()
                    activity.hideProgressDialog()
                    return@Observer
                }
                try {
                    val jsonObject = JSONObject(jsonError)
                    val jsonObjectError = jsonObject.getJSONObject("error")
                    if (jsonObjectError.has("message")) {
                        setError(jsonObjectError.getString("message"))
                    }
                    if (jsonObjectError.has("errors")) {
                        val jsonObjectErrors = jsonObjectError.getJSONObject("errors")
                        if (jsonObjectErrors.has("old_password")) {
                            val jsonArrayMobile = jsonObjectErrors.getJSONArray("old_password")
                            val oldPassword1 = jsonArrayMobile.getString(0)
                            setError(oldPassword1)
                            //oldPassword!!.setError(oldPassword1)
                        }
                        if (jsonObjectErrors.has("new_password")) {
                            val jsonArrayMobile = jsonObjectErrors.getJSONArray("new_password")
                            val newPassword1 = jsonArrayMobile.getString(0)
                            setError(newPassword1)
                            //newPassword!!.setError(newPassword1)
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    setError("Something went wrong")
                }
            } else {
                setError("Something went wrong")
            }
            activity.hideProgressDialog()
        })
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
        }
        return true
    }

    private fun setError(error: String, txtInput: TextInputLayout) {
        val errorDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.ic_error)
        val ss = SpannableString("    $error\n")
        errorDrawable!!.setBounds(0, 0, errorDrawable.intrinsicWidth, errorDrawable.intrinsicHeight)
        val span = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ImageSpan(errorDrawable, ImageSpan.ALIGN_CENTER)
        } else {
            ImageSpan(errorDrawable, ImageSpan.ALIGN_BOTTOM)
        }
        ss.setSpan(span, 0, 3, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
        txtInput.error = ss
    }

    private fun proceedToCorrectActivity(userAccountModel: UserAccountModel) {
        val intent: Intent
        if (userAccountModel.account_status.isBasic_info) {
            intent = Intent(requireActivity(), DashboardActivity::class.java)
            sessionManagerA.userAccount = userAccountModel
            sessionManagerA.latitude = userAccountModel.latitude.toString()
            sessionManagerA.longitude = userAccountModel.longitude.toString()
            sessionManagerA.login = true
            openActivity(intent)
        }
    }
    private fun openActivity(intent: Intent) {
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }
}