package com.jobtick.android

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
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
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.facebook.BuildConfig
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
import com.jobtick.android.utils.Constant
import com.jobtick.android.utils.FireBaseEvent
import com.jobtick.android.utils.Helper
import com.jobtick.android.utils.SessionManager
import org.json.JSONException
import org.json.JSONObject
import timber.log.Timber


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
                signUp(edtEmail.editText?.text.toString(), "j9012j9012")

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

    @SuppressLint("HardwareIds")
    private fun signUp(email: String?, password: String?) {
        activity.showProgressDialog()
        val strDeviceId =
            Settings.Secure.getString(requireActivity().contentResolver, Settings.Secure.ANDROID_ID)
        val strDevice = "Android"
        Helper.closeKeyboard(requireActivity())
        val stringRequest: StringRequest = object : StringRequest(
            Method.POST, Constant.URL_SIGNUP,
            Response.Listener { response: String? ->
                Timber.e(response)
                activity.hideProgressDialog()
                try {
                    val jsonObject = JSONObject(response!!)
                    Timber.e(jsonObject.toString())
                    activity.navController.navigate(R.id.activateAccountFragment)
                } catch (e: JSONException) {
                    e.printStackTrace()
                    setError("Something Went Wrong", edtEmail)
                }
            },
            Response.ErrorListener { error: VolleyError ->
                val networkResponse = error.networkResponse
                if (networkResponse?.data != null) {
                    val jsonError = String(networkResponse.data)
                    // Print Error!
                    Timber.tag("intent22").e(jsonError)
                    activity.navController.navigate(R.id.activateAccountFragment)
                    try {
                        val jsonObject = JSONObject(jsonError)
                        val jsonObjectError = jsonObject.getJSONObject("error")
                        if (jsonObjectError.has("errors")) {
                            val jsonObjectErrors = jsonObjectError.getJSONObject("errors")
                            var errorEmail = ""
                            if (jsonObjectErrors.has("email")) {
                                val jsonArrayMobile = jsonObjectErrors.getJSONArray("email")
                                errorEmail = jsonArrayMobile.getString(0)
                                setError(errorEmail, edtEmail)
                            } else if (jsonObjectErrors.has("password")) {
                                setError("Something Went Wrong", edtEmail)
                            } else {
                                setError("Something Went Wrong", edtEmail)
                            }
                            // signUpFragment.error(error_email,error_password);
                        } else if (jsonObjectError.has("message")) {
                            setError(jsonObjectError.getString("message"), edtEmail)
                        } else {
                            setError("Something Went Wrong", edtEmail)
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                        setError("Something Went Wrong", edtEmail)
                    }
                } else {
                    setError("Something Went Wrong", edtEmail)
                }
                Timber.tag("error").e(error.toString())
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
                if (email != null) map1["email"] = email
                if (password != null) map1["password"] = password
                 map1["device_token"] = strDeviceId
                map1["fname"] = "Jobtick"
                map1["lname"] = "User"
                map1["latitude"] = "33"
                map1["longitude"] = "151"
                map1["device_type"] = strDevice
                map1["fcm_token"] = token
                map1["location"] = "no location"
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