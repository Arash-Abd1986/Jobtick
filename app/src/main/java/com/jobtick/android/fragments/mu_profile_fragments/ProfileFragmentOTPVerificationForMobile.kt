package com.jobtick.android.fragments.mu_profile_fragments

import `in`.aabhasjindal.otptextview.OTPListener
import android.content.Context
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.jobtick.android.R
import com.jobtick.android.activities.DashboardActivity
import com.jobtick.android.databinding.FragmentProfileOtpVerificationBinding
import com.jobtick.android.models.UserAccountModel
import com.jobtick.android.network.retrofit.ApiClient
import com.jobtick.android.utils.Helper
import com.jobtick.android.utils.SessionManager
import com.jobtick.android.utils.SetToolbar
import com.jobtick.android.viewmodel.ProfileOTPViewModel
import okhttp3.MultipartBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class ProfileFragmentOTPVerificationForMobile : Fragment(), OTPListener {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var activity: DashboardActivity
    private lateinit var sessionManager: SessionManager
    private var _binding: FragmentProfileOtpVerificationBinding? = null
    private val binding get() = _binding!!
    private var time = ""
    private lateinit var timer: CountDownTimer
    private lateinit var mutableMap: MutableMap<String, String>
    private lateinit var viewModel: ProfileOTPViewModel
    private var hasCheckedToken: String? = null
    private var closePage = false;
//    private var observer:Observer<String> = Observer {
//        onError(it!!)
//    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        activity = (requireActivity() as DashboardActivity)
        sessionManager = SessionManager(context)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?)

    {
        super.onViewCreated(view, savedInstanceState)
        SetToolbar(activity, "OTP Verification", "", R.id.navigation_profile, binding.header, view)

        viewModel = ViewModelProvider(activity)[ProfileOTPViewModel::class.java]
       // viewModel.errorMobileSendOtp.removeObserver(observer)
        if(requireArguments().getString("number")?.isNotEmpty() == true) {
            mutableMap = mutableMapOf("mobile" to requireArguments().getString("number").toString(), "dialing_code" to "+61")
            mobileResendOTP(activity, mutableMap)
        }

       // viewModel.errorMobileSendOtp.observeForever(observer)


//        viewModel.errorMobileSendOtp.observeOnce(this, Observer<String> {
//            Log.d("hereiam4", it.toString())
//            if (it != null && it == "1") {
//
//                var bundle: Bundle? = null
//                if (requireArguments().getString("number")?.isNotEmpty() == true)
//                    bundle = bundleOf("number" to requireArguments().getString("number").toString())
//                //viewModel.success.removeObservers(viewLifecycleOwner)
//                view.findNavController().navigate(
//                    R.id.action_navigation_profile_otp_verification_mobile_to_navigation_profile_change_email_second_page,
//                    bundle
//                )
//            }
//        })

//        viewModel.successMobileVerificationOTP.observe(viewLifecycleOwner) {
//            //TODO remove observer not to call always
//            if(it) {
//                view.findNavController().navigate(R.id.action_navigation_profile_otp_verification_to_navigation_profile_account)
//            }
//
//            viewModel.successMobileVerificationOTP.removeObservers(this)
//        }


//        viewModel.successMobileSendOtp.observe(viewLifecycleOwner){
//            //TODO remove observer not to call always
//            if(it) {
//                timer.start()
//                setActive(true)
//            }
//            else
//                setActive(false)
//            viewModel.successMobileSendOtp.removeObservers(this)
//
//        }


//        viewModel.hashCheckToken.observe(viewLifecycleOwner) {
//            if(it != "0")
//                hasCheckedToken = it
//            viewModel.hashCheckToken.removeObservers(this)
//        }

        timer = object : CountDownTimer(3 * 60 * 1000, 1000) {

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
                binding.timer.text = sb
            }



            override fun onFinish() {
                setActive(false)
            }

        }

        binding.otpView.otpListener = this

        binding.btnForgetPass.setOnClickListener {
            if(requireArguments().getString("number")?.isNotEmpty() == true) {
                mutableMap.clear()
                mutableMap = mutableMapOf("number" to requireArguments().getString("number").toString())
                viewModel.mobileResendOTP(activity, mutableMap)
            }

        }

        binding.header.back.setOnClickListener {
            var bundle: Bundle? = null
            if(requireArguments().getString("number")?.isNotEmpty() == true)
                bundle = bundleOf("mobile" to requireArguments().getString("number").toString())
            //viewModel.success.removeObservers(viewLifecycleOwner)
            view.findNavController().navigate(R.id.action_navigation_profile_otp_verification_mobile_to_navigation_profile_change_email_second_page, bundle)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileOtpVerificationBinding.inflate(inflater, container, false)
        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ProfileFragmentAccount().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
    private fun getTime(sec: Long): String {
        val minutes = (sec % 3600) / 60
        val seconds = sec % 60

        return String.format("%02d:%02d", minutes, seconds);
    }

    private fun setActive(status: Boolean) {
        if(status) {
            //binding.timer.text = "The code has been expired!"
            binding.btnForgetPass.setTextColor(activity.getColor(R.color.neutral_light_400))
            binding.btnForgetPass.setBackgroundColor(activity.getColor(R.color.neutral_light_100))
            binding.btnForgetPass.isEnabled = false
        }
        else {
            binding.timer.text = "The code has been expired!"
            binding.btnForgetPass.setTextColor(activity.getColor(R.color.white))
            binding.btnForgetPass.setBackgroundColor(activity.getColor(R.color.primary_500))
            binding.btnForgetPass.isEnabled = true
        }
    }

    override fun onInteractionListener() {
    }

    override fun onOTPComplete(otp: String?) {
        if(requireArguments().getString("number")?.isNotEmpty() == true) {
            mutableMap.clear()
            mutableMap = mutableMapOf("hash_check_token" to hasCheckedToken.toString(),
            "otp" to otp.toString())
            viewModel.mobileVerificationOTP(activity, mutableMap)

        }
        binding.otpView.resetState()
    }

//    fun onError(str: String) {
//        if (str == "1") {
//            viewModel.errorMobileSendOtp.removeObserver(observer)
//            var bundle: Bundle? = null
//            if (requireArguments().getString("number")?.isNotEmpty() == true)
//                bundle = bundleOf("number" to requireArguments().getString("number").toString())
//            //viewModel.success.removeObservers(viewLifecycleOwner)
//            view?.findNavController()?.navigate(
//                R.id.action_navigation_profile_otp_verification_mobile_to_navigation_profile_change_email_second_page,
//                bundle
//            )
//        }
//    }
    fun <T> LiveData<T>.observeOnce(lifecycleOwner: LifecycleOwner, observer: Observer<T>) {
        observe(lifecycleOwner, object : Observer<T> {
            override fun onChanged(t: T?) {
                observer.onChanged(t)
              //  removeObserver(this)
            }
        })
    }

    fun mobileVerificationOTP(context: Context, inputs: MutableMap<String, String>) {
        sessionManager = SessionManager(context)
        (context as DashboardActivity).showProgressDialog()
        val call: Call<String?>?
        Helper.closeKeyboard(context)
        val requestBody = MultipartBody.Builder().setType(MultipartBody.FORM).apply {
            for (input in inputs) {
                addFormDataPart(input.key, input.value)
            }
            //sessionManager?.role?.let { addFormDataPart("role_as", it) }
        }.build()
        call = ApiClient.getClientV1WithToken(sessionManager).emailOtpVerification(
            "XMLHttpRequest",
            requestBody
        )
        call!!.enqueue(object : Callback<String?> {
            override fun onResponse(call: Call<String?>, response: Response<String?>) {
                context.hideProgressDialog()
                try {
                    if(response.isSuccessful) {
                        try {
                            val jsonObject = JSONObject(response.body().toString())
                            Timber.e(jsonObject.toString())
                            val jsonObjectData = jsonObject.getJSONObject("data")
                            sessionManager.accessToken = jsonObjectData.getString("access_token")
                            sessionManager.tokenType = jsonObjectData.getString("token_type")
                            val jsonObjectUser = jsonObjectData.getJSONObject("user")
                            val userAccountModel = UserAccountModel().getJsonToModel(jsonObjectUser)
                            sessionManager.userAccount = userAccountModel
                            context.showSuccessToast(jsonObject.getString("message"), context)
                        } catch (e: JSONException) {
                            Timber.e(e.toString())
                            e.printStackTrace()
                        }

                    } else {
                        val jObjError = JSONObject(
                            response.errorBody()!!.string()
                        )
                        context.showToast(jObjError.getJSONObject("error").getString("message"), context)
                    }

                } catch (e: java.lang.Exception) {
                    Log.d("errorOnOtpSend", e.toString())

                    e.printStackTrace()
                    context.showToast("Something Went Wrong", context)
                }
            }
            override fun onFailure(call: Call<String?>, t: Throwable) {
                context.showToast(t.toString(), context)
                context.hideProgressDialog()
                Timber.e(call.toString())
            }
        })
    }

    fun mobileResendOTP(context: Context, inputs: MutableMap<String, String>) {
        sessionManager = SessionManager(context)
        (context as DashboardActivity).showProgressDialog()
        val call: Call<String?>?
        Helper.closeKeyboard(context)
        val requestBody = MultipartBody.Builder().setType(MultipartBody.FORM).apply {
            for (input in inputs) {
                addFormDataPart(input.key, input.value)
            }
            //sessionManager?.role?.let { addFormDataPart("role_as", it) }
        }.build()
        call = ApiClient.getClientV1WithToken(sessionManager).emailResendOtp(
            "XMLHttpRequest",
            requestBody
        )
        call!!.enqueue(object : Callback<String?> {
            override fun onResponse(call: Call<String?>, response: Response<String?>) {
                context.hideProgressDialog()
                try {
                    val jsonObject = JSONObject(response.toString())
                    if (jsonObject.has("success") && !jsonObject.isNull("success")) {
                        if (jsonObject.getBoolean("success") && !jsonObject.isNull("data")) {
                            context.showSuccessToast(jsonObject.getString("message"), context)
                            timer.start()
                            setActive(true)
                        }
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                    context.showToast("Something Went Wrong", context)
                }
            }
            override fun onFailure(call: Call<String?>, t: Throwable) {
                context.showToast(t.toString(), context)
                context.hideProgressDialog()
                Timber.e(call.toString())
            }
        })
    }

    fun backToSecondPage() {
        var bundle: Bundle? = null
        if(requireArguments().getString("email")?.isNotEmpty() == true) {
            bundle = bundleOf("email" to requireArguments().getString("email").toString())
        }
        else if(requireArguments().getString("number")?.isNotEmpty() == true)
            bundle = bundleOf("number" to requireArguments().getString("number").toString())
        //viewModel.success.removeObservers(viewLifecycleOwner)
        view?.findNavController()?.navigate(R.id.action_navigation_profile_otp_verification_mobile_to_navigation_profile_account, bundle)
    }


}