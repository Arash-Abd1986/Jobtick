package com.jobtick.android.material.ui.landing

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.ImageSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
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
import com.jobtick.android.R
import com.jobtick.android.activities.CompleteRegistrationActivity
import com.jobtick.android.activities.DashboardActivity
import com.jobtick.android.fragments.ForgotPassword2Fragment
import com.jobtick.android.models.UserAccountModel
import com.jobtick.android.utils.Constant
import com.jobtick.android.utils.FireBaseEvent
import com.jobtick.android.utils.Helper
import com.jobtick.android.utils.SessionManager
import org.json.JSONException
import org.json.JSONObject
import timber.log.Timber

class SignInFragment : Fragment() {

    private lateinit var sessionManagerA: SessionManager
    private lateinit var activity: OnboardingActivity
    private lateinit var fireBaseEvent: FireBaseEvent
    private lateinit var next: MaterialButton
    private lateinit var tvGoogle: MaterialButton
    private lateinit var tvFB: MaterialButton
    private lateinit var signIn: MaterialTextView
    private lateinit var btnForgetPass: MaterialButton
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
                ContextCompat.getColor(requireContext(), R.color.primary)
            ),
            9,
            sb.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        title.text = sb
        val sb2: Spannable =
            SpannableString(getString(R.string.don_t_have_an_account_sign_up))
        sb2.setSpan(
            ForegroundColorSpan(
                ContextCompat.getColor(requireContext(), R.color.primary)
            ),
            23,
            sb2.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        signIn.text = sb2
    }


    private fun initVars() {
        sessionManagerA = SessionManager(requireContext())
        activity = (requireActivity() as OnboardingActivity)
        //fireBaseEvent = FireBaseEvent.getInstance(requireActivity())
        next = requireView().findViewById(R.id.btn_next)
        btnForgetPass = requireView().findViewById(R.id.btn_forget_pass)
        tvFB = requireView().findViewById(R.id.tvFB)
        tvGoogle = requireView().findViewById(R.id.tvGoogle)
        edtEmail = requireView().findViewById(R.id.email)
        edtEmail.editText!!.setText("jalil@jobtick.com")
        signIn = requireView().findViewById(R.id.signIn)
        edtPassword = requireView().findViewById(R.id.password)
        edtPassword.editText!!.setText("Qwertyuiop")
        txtError = requireView().findViewById(R.id.error)
        title = requireView().findViewById(R.id.title)
        next.setOnClickListener {
            resetError()
            if (checkValidation())
                login(edtEmail.editText?.text.toString(), edtPassword.editText?.text.toString())
        }
        btnForgetPass.setOnClickListener {
            activity.navController.navigate(R.id.forgetPassAddMailFragment)

        }
        signIn.setOnClickListener {
            activity.navController.navigate(R.id.signUpFragment)
        }
        tvGoogle.setOnClickListener {
            activity.signInWithGoogle(true)
        }
        tvFB.setOnClickListener {
            activity.facebookLogin(true)
        }
    }
    @SuppressLint("HardwareIds")
    private fun login(email: String?, password: String?) {
        activity.initProgressDialog("Logging in")
        activity.showProgressDialog()
        val strFcmToken = token
        val strDeviceId =
            Settings.Secure.getString(requireActivity().contentResolver, Settings.Secure.ANDROID_ID)
        val strDevice = "Android"
        Helper.closeKeyboard(requireActivity())
        val stringRequest: StringRequest = object : StringRequest(
            Method.POST, Constant.URL_SIGNIN,
            Response.Listener { response: String? ->
                Timber.e(response)
                activity.hideProgressDialog()
                try {

                    val jsonObject = JSONObject(response!!)
                    Log.d("loginresponse", jsonObject.toString())
                    Timber.e(jsonObject.toString())
                    val jsonObjectData = jsonObject.getJSONObject("data")
                    sessionManagerA.accessToken = jsonObjectData.getString("access_token")
                    sessionManagerA.tokenType = jsonObjectData.getString("token_type")
                    val jsonObjectUser = jsonObjectData.getJSONObject("user")
                    val userAccountModel = UserAccountModel().getJsonToModel(jsonObjectUser)
                    sessionManagerA.userAccount = userAccountModel
                    /*fireBaseEvent.sendEvent(
                        FireBaseEvent.Event.LOGIN,
                        FireBaseEvent.EventType.API_RESPOND_SUCCESS,
                        FireBaseEvent.EventValue.LOGIN_NORMAL
                    )*/
                    proceedToCorrectActivity(userAccountModel)
                } catch (e: JSONException) {
                    e.printStackTrace()
                    setError("Something Went Wrong")
                }
            },
            Response.ErrorListener { error: VolleyError ->
                activity.hideProgressDialog()
                val networkResponse = error.networkResponse
                if (networkResponse?.data != null) {
                    val jsonError = String(networkResponse.data)
                    // Print Error!
                    try {
                        val jsonObject = JSONObject(jsonError)
                        Log.d("errorlogin", jsonObject.toString())

                        val jsonObjectError = jsonObject.getJSONObject("error")
                        if (jsonObjectError.has("error_code")) {
                            if (jsonObjectError.getInt("error_code") == 1002) {
                                if (jsonObjectError.has("message"))
                                    setError(
                                        jsonObjectError.getString(
                                            "message"
                                        )
                                    )
                                return@ErrorListener
                            }
                        }
                        if (jsonObjectError.has("errors")) {
                            val jsonObjectErrors = jsonObjectError.getJSONObject("errors")
                            var errorEmail: String? = null
                            var errorPassword: String? = null
                            when {
                                jsonObjectErrors.has("email") -> {
                                    val jsonArrayMobile = jsonObjectErrors.getJSONArray("email")
                                    errorEmail = jsonArrayMobile.getString(0)
                                    setError(errorEmail, edtEmail)
                                }
                                jsonObjectErrors.has("password") -> {
                                    val jsonArrayMobile = jsonObjectErrors.getJSONArray("password")
                                    errorPassword = jsonArrayMobile.getString(0)
                                    setError(errorPassword, edtPassword)
                                }
                                jsonObjectErrors.has("device_token") -> {
                                    val jsonArrayDeviceToken =
                                        jsonObjectErrors.getJSONArray("device_token")
                                    setError(jsonArrayDeviceToken.getString(0))
                                }
                                jsonObjectErrors.has("device_type") -> {
                                    val jsonArrayDeviceType =
                                        jsonObjectErrors.getJSONArray("device_type")
                                    setError(jsonArrayDeviceType.getString(0))
                                }
                                jsonObjectErrors.has("fcm_token") -> {
                                    val jsonArrayFcmToken =
                                        jsonObjectErrors.getJSONArray("fcm_token")
                                    setError(jsonArrayFcmToken.getString(0))
                                }
                                jsonObjectErrors.has("latitude") -> {
                                    val jsonArrayLatitude =
                                        jsonObjectErrors.getJSONArray("latitude")
                                    setError(jsonArrayLatitude.getString(0))
                                }
                                jsonObjectErrors.has("longitude") -> {
                                    val jsonArrayLongitude =
                                        jsonObjectErrors.getJSONArray("longitude")
                                    setError(jsonArrayLongitude.getString(0))
                                }
                            }
                        } else {
                            val message = jsonObjectError.getString("message")
                            setError(message)
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                        setError("Something Went Wrong")
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
                if (email != null) map1["email"] = email
                if (password != null) map1["password"] = password
                if (strDeviceId != null) map1["device_token"] = strDeviceId
                map1["device_type"] = strDevice
                map1["fcm_token"] = strFcmToken
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

    private fun checkValidation(): Boolean {
        when {
            edtEmail.editText?.text.isNullOrEmpty() && edtPassword.editText?.text.isNullOrEmpty() -> {
                setError(
                    "Please enter your email address and password"
                )
                return false
            }
            edtEmail.editText?.text.isNullOrEmpty() -> {
                setError("Please enter your email address", edtEmail)
                return false
            }
            edtPassword.editText?.text.isNullOrEmpty() -> {
                setError("Please enter your password", edtPassword)
            }
        }
        return true
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


    private fun proceedToCorrectActivity(userAccountModel: UserAccountModel) {
        val intent: Intent
        if (userAccountModel.account_status.isBasic_info) {
            intent = Intent(requireActivity(), DashboardActivity::class.java)
            sessionManagerA.userAccount = userAccountModel
            sessionManagerA.latitude = userAccountModel.latitude.toString()
            sessionManagerA.longitude = userAccountModel.longitude.toString()
            sessionManagerA.login = true
            openActivity(intent)
        } else {
            intent = Intent(requireActivity(), CompleteRegistrationActivity::class.java)
            startActivity(intent)
        }
    }

    private fun openActivity(intent: Intent) {
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }
}