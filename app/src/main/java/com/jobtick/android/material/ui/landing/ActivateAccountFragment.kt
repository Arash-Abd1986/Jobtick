package com.jobtick.android.material.ui.landing

import `in`.aabhasjindal.otptextview.OTPListener
import `in`.aabhasjindal.otptextview.OtpTextView
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
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
import com.google.android.material.button.MaterialButton
import com.google.android.material.textview.MaterialTextView
import com.jobtick.android.R
import com.jobtick.android.models.UserAccountModel
import com.jobtick.android.utils.Constant
import com.jobtick.android.utils.Helper
import com.jobtick.android.utils.SessionManager
import org.json.JSONException
import org.json.JSONObject
import timber.log.Timber

class ActivateAccountFragment : Fragment(), OTPListener {
    private lateinit var txtTimer: MaterialTextView
    private lateinit var otpView: OtpTextView
    private lateinit var btnForgetPass: MaterialButton
    private lateinit var activity: OnboardingActivity
    private lateinit var sessionManagerA: SessionManager


    private var time = ""
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_activate_account, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sessionManagerA = SessionManager(requireContext())
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
                        ContextCompat.getColor(requireContext(), R.color.neutral_dark)
                    ),
                    24,
                    sb.length,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                txtTimer.text = sb
            }

            override fun onFinish() {
                btnForgetPass.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.primary
                    )
                )
            }
        }.start()
        timer.start()
        btnForgetPass.setOnClickListener {
            btnForgetPass.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.neutral_light_400
                )
            )
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

    fun newEmailVerification(email: String, otp: String) {
        activity.showProgressDialog()
        Helper.closeKeyboard(requireActivity())
        val stringRequest: StringRequest =
            object : StringRequest(
                Method.POST, Constant.URL_EMAIL_VERIFICATION,
                Response.Listener { response: String? ->
                    Timber.e(response)
                    activity.hideProgressDialog()
                    try {
                        val jsonObject = JSONObject(response!!)
                        Timber.e(jsonObject.toString())
                        val jsonObjectData = jsonObject.getJSONObject("data")
                        sessionManagerA.accessToken = jsonObjectData.getString("access_token")
                        sessionManagerA.tokenType = jsonObjectData.getString("token_type")
                        val jsonObjectUser = jsonObjectData.getJSONObject("user")
                        val userAccountModel = UserAccountModel().getJsonToModel(jsonObjectUser)
                        sessionManagerA.userAccount = userAccountModel
                        activity.navController.navigate(R.id.addPassFragment)
                    } catch (e: JSONException) {
                        Timber.e(e.toString())
                        e.printStackTrace()
                    }
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
                            val message = jsonObjectError.getString("message")
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                    } else {
                        //showToast("Something Went Wrong", this@AuthActivity)
                    }
                    Timber.e(error.toString())
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

    override fun onOTPComplete(otp: String?) {
        if (otp != null)
            arguments?.getString("email")?.let { newEmailVerification(it, otp) }
    }
}