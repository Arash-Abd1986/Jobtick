package com.jobtick.android.material.ui.landing

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ImageSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.facebook.BuildConfig
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputLayout
import com.google.android.material.textview.MaterialTextView
import com.jobtick.android.R
import com.jobtick.android.activities.DashboardActivity
import com.jobtick.android.models.UserAccountModel
import com.jobtick.android.utils.Constant
import com.jobtick.android.utils.ExternalIntentHelper
import com.jobtick.android.utils.Helper
import com.jobtick.android.utils.SessionManager
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
            val bundle = arguments
            resetError()
            if (checkValidation())
                resetPassword(
                        bundle!!.getString("email")!!,
                        bundle.getString("otp")!!,
                        edtPassword.editText!!.text.toString())
        }
        txtBtnTerms.setOnClickListener {
            ExternalIntentHelper.openLink(
                    requireActivity(),
                    "https://www.jobtick.com/terms"
            )
        }
    }
    fun resetPassword(email: String, otp: String, new_password: String) {
        activity.showProgressDialog()
        Helper.closeKeyboard(requireActivity())
        val stringRequest: StringRequest =
                object : StringRequest(
                        Method.POST, Constant.URL_FORGOT_PASSWORD,
                        Response.Listener { response: String? ->
                            activity.hideProgressDialog()
                            try {
                                val jsonObject = JSONObject(response!!)
                                val jsonObjectData = jsonObject.getJSONObject("data")
                                sessionManagerA.accessToken = jsonObjectData.getString("access_token")
                                sessionManagerA.tokenType = jsonObjectData.getString("token_type")
                                val jsonObjectUser = jsonObjectData.getJSONObject("user")
                                val userAccountModel = UserAccountModel().getJsonToModel(jsonObjectUser)
                                sessionManagerA.userAccount = userAccountModel
                                activity.navController.navigate(R.id.signInFragment)
                            } catch (e: JSONException) {
                                Timber.e(e.toString())
                                e.printStackTrace()
                                setError("Something went wrong")
                            }
                        },
                        Response.ErrorListener { error: VolleyError ->
                            setError("Something went wrong")
                            activity.hideProgressDialog()
                        }
                ) {
                    override fun getHeaders(): Map<String, String> {
                        val map1: MutableMap<String, String> = HashMap()
                        map1["Content-Type"] = "application/x-www-form-urlencoded"
                        map1["X-Requested-With"] = "XMLHttpRequest"
                        map1["Version"] = BuildConfig.VERSION_CODE.toString()
                        return map1
                    }

                    override fun getParams(): Map<String, String> {
                        val map1: MutableMap<String, String> = HashMap()
                        map1["email"] = email
                        map1["otp"] = otp
                        map1["new_password"] = new_password
                        return map1
                    }
                }
        stringRequest.retryPolicy = DefaultRetryPolicy(
                0, -1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )
        val requestQueue = Volley.newRequestQueue(requireContext())
        requestQueue.add(stringRequest)
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
}