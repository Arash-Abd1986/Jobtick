package com.jobtick.android.material.ui.landing

import android.os.Build
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ImageSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
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
import com.jobtick.android.utils.Constant
import com.jobtick.android.utils.Helper
import com.jobtick.android.utils.SessionManager
import com.jobtick.android.utils.gone
import org.json.JSONException
import org.json.JSONObject
import timber.log.Timber

class ForgetPassAddMailFragment : Fragment() {

    private lateinit var sessionManagerA: SessionManager
    private lateinit var activity: OnboardingActivity
    private lateinit var next: MaterialButton
    private lateinit var tvGoogle: MaterialButton
    private lateinit var tvFB: MaterialButton
    private lateinit var edtEmail: TextInputLayout
    private lateinit var edtPassword: TextInputLayout
    private lateinit var txtError: MaterialTextView
    private lateinit var rlSpacer: RelativeLayout
    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_forget_pass_add_mail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initVars()
    }



    private fun initVars() {
        sessionManagerA = SessionManager(requireContext())
        activity = (requireActivity() as OnboardingActivity)
        //fireBaseEvent = FireBaseEvent.getInstance(requireActivity())
        next = requireView().findViewById(R.id.btn_next)
        rlSpacer = requireView().findViewById(R.id.rlSpacer)
        tvFB = requireView().findViewById(R.id.tvFB)
        tvGoogle = requireView().findViewById(R.id.tvGoogle)
        edtEmail = requireView().findViewById(R.id.email)
        edtPassword = requireView().findViewById(R.id.password)
        txtError = requireView().findViewById(R.id.error)
        next.setOnClickListener {
            resetError()
            if (checkValidation2()){
                nextStepForgotPassword(edtEmail.editText?.text.toString())
            }
        }
        tvGoogle.gone()
        tvFB.gone()
        edtPassword.gone()
        rlSpacer.gone()
    }
    fun nextStepForgotPassword(email: String) {
        activity.showProgressDialog()
        Helper.closeKeyboard(requireActivity())
        val stringRequest: StringRequest =
                object : StringRequest(
                        Method.POST, Constant.URL_RESET_PASSWORD,
                        Response.Listener { response: String? ->
                            Timber.e(response)
                            activity.hideProgressDialog()
                            val bundle = Bundle()
                            bundle.putString("email", edtEmail.editText?.text.toString())
                            activity.navController.navigate(R.id.forgetPassFirstPageFragment, bundle)

                        },
                        Response.ErrorListener { error: VolleyError ->
                            val networkResponse = error.networkResponse
                            if (networkResponse?.data != null) {
                                val jsonError = String(networkResponse.data)
                                // Print Error!
                                Timber.e(jsonError)
                                try {
                                    val jsonObject = JSONObject(jsonError)
                                    val jsonObjectError = jsonObject.getJSONObject("error")
                                    if (jsonObjectError.has("errors")) {
                                        val jsonObjectErrors = jsonObjectError.getJSONObject("errors")
                                        var strError: String? = null
                                        when {
                                            jsonObjectErrors.has("email") -> {
                                                val jsonArrayMobile = jsonObjectErrors.getJSONArray("email")
                                                strError = jsonArrayMobile.getString(0)
                                                setError(strError, edtEmail)
                                            }
                                            jsonObjectErrors.has("password") -> {
                                                val jsonArrayMobile =
                                                        jsonObjectErrors.getJSONArray("password")
                                                strError = jsonArrayMobile.getString(0)
                                                setError(strError, edtPassword)
                                            }
                                            else -> {
                                                val message = jsonObjectError.getString("message")
                                                setError(message)
                                            }
                                        }
                                    }
                                } catch (e: JSONException) {
                                    e.printStackTrace()
                                }
                            } else {
                                setError("Something Went Wrong")
                            }
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

    private fun checkValidation2(): Boolean {
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

    private fun setError(error: String) {
        txtError.visibility = View.VISIBLE
        txtError.text = error
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