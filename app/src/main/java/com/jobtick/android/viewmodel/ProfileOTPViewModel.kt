package com.jobtick.android.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.material.textfield.TextInputEditText
import com.jobtick.android.activities.DashboardActivity
import com.jobtick.android.models.UserAccountModel
import com.jobtick.android.network.retrofit.ApiClient
import com.jobtick.android.payment.VerifyPhoneNumberImpl.SuccessType
import com.jobtick.android.utils.Helper
import com.jobtick.android.utils.SessionManager
import okhttp3.MultipartBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber


class ProfileOTPViewModel: ViewModel() {

    var successEmailSendOtp = MutableLiveData<Boolean>()
    var successEmailVerificationOTP = MutableLiveData<Boolean>()
    var successMobileSendOtp = MutableLiveData<Boolean>()
    var successMobileVerificationOTP = MutableLiveData<Boolean>()
    var errorEmailSendOtp = MutableLiveData<String>()
    var errorEmailVerificationOTP = MutableLiveData<String>()
    var errorMobileVerificationOTP = MutableLiveData<String>()
    var errorMobileSendOtp = MutableLiveData<String>()
    var sessionManager: SessionManager? = null
    var hashCheckToken = MutableLiveData<String>()

    init{
        errorMobileSendOtp.value = "0"
        Log.d("hereiam3", errorMobileSendOtp.value.toString())

    }
    fun emailResendOTP(context: Context, inputs: MutableMap<String, String>) {
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
                    if(response.isSuccessful) {
                        val jsonObject = JSONObject(response.body()!!)
                        context.showSuccessToast(jsonObject.getString("message"), context)
                        successEmailSendOtp.value = true
                    } else {
                        val jObjError = JSONObject(
                            response.errorBody()!!.string()
                        )
                        context.showToast(jObjError.getJSONObject("error").getString("message"), context)
                        successEmailSendOtp.value = false
                    }

                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                    context.showToast("Something Went Wrong", context)
                    successEmailSendOtp.value = false
                }
                context.hideProgressDialog()

            }
            override fun onFailure(call: Call<String?>, t: Throwable) {
                errorEmailSendOtp.value = t.toString()
                context.showToast(t.toString(), context)
                context.hideProgressDialog()
                Timber.e(call.toString())
                successEmailSendOtp.value = false
            }
        })
    }

    fun emailVerificationOTP(context: Context, inputs: MutableMap<String, String>) {
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
                            sessionManager?.accessToken = jsonObjectData.getString("access_token")
                            sessionManager?.tokenType = jsonObjectData.getString("token_type")
                            val jsonObjectUser = jsonObjectData.getJSONObject("user")
                            val userAccountModel = UserAccountModel().getJsonToModel(jsonObjectUser)
                            sessionManager?.userAccount = userAccountModel
                            context.showSuccessToast(jsonObject.getString("message"), context)
                            successEmailVerificationOTP.value = true
                        } catch (e: JSONException) {
                            Timber.e(e.toString())
                            e.printStackTrace()
                        }

                    } else {
                        val jObjError = JSONObject(
                            response.errorBody()!!.string()
                        )
                        context.showToast(jObjError.getJSONObject("error").getString("message"), context)
                        successEmailVerificationOTP.value = false
                    }

                } catch (e: java.lang.Exception) {

                    e.printStackTrace()
                    context.showToast("Something Went Wrong", context)
                    successEmailVerificationOTP.value = false
                }

                context.hideProgressDialog()

            }
            override fun onFailure(call: Call<String?>, t: Throwable) {
                errorEmailVerificationOTP.value = t.toString()
                context.showToast(t.toString(), context)
                context.hideProgressDialog()
                Timber.e(call.toString())
                successEmailVerificationOTP.value = false

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
                            sessionManager?.accessToken = jsonObjectData.getString("access_token")
                            sessionManager?.tokenType = jsonObjectData.getString("token_type")
                            val jsonObjectUser = jsonObjectData.getJSONObject("user")
                            val userAccountModel = UserAccountModel().getJsonToModel(jsonObjectUser)
                            sessionManager?.userAccount = userAccountModel
                            context.showSuccessToast(jsonObject.getString("message"), context)
                            successMobileVerificationOTP.value = true
                        } catch (e: JSONException) {
                            Timber.e(e.toString())
                            e.printStackTrace()
                        }

                    } else {
                        val jObjError = JSONObject(
                            response.errorBody()!!.string()
                        )
                        context.showToast(jObjError.getJSONObject("error").getString("message"), context)
                        successMobileVerificationOTP.value = false
                    }
                    context.hideProgressDialog()


                } catch (e: java.lang.Exception) {
                    Log.d("errorOnOtpSend", e.toString())

                    e.printStackTrace()
                    context.showToast("Something Went Wrong", context)
                    successMobileVerificationOTP.value = false
                }
                context.hideProgressDialog()

            }
            override fun onFailure(call: Call<String?>, t: Throwable) {
                errorMobileVerificationOTP.value = t.toString()
                context.showToast(t.toString(), context)
                context.hideProgressDialog()
                Timber.e(call.toString())
                successMobileVerificationOTP.value = false
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
                            hashCheckToken.value =
                                jsonObject.getJSONObject("data").getString("hash_check_token")
                            successMobileSendOtp.value = true
                            errorMobileSendOtp.postValue("0")
                        }
                    }
                } catch (e: JSONException) {
                    Log.d("hereiam1", errorMobileSendOtp.value.toString())
                    e.printStackTrace()
                    errorMobileSendOtp.postValue("1")
                    context.showToast("Something Went Wrong", context)
                }
                context.hideProgressDialog()

            }
            override fun onFailure(call: Call<String?>, t: Throwable) {
                Log.d("hereiam2", errorMobileSendOtp.value.toString())
                errorMobileSendOtp.value = "1"
                context.showToast(t.toString(), context)
                context.hideProgressDialog()
                Timber.e(call.toString())
            }
        })
    }


    fun validate(view: List<TextInputEditText>): Boolean {
        for (t in view)
            if(t.text?.trim()!!.isEmpty())
                return false
        return true
    }
    
}