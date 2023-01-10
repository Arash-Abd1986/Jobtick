package com.jobtick.android.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.material.textfield.TextInputEditText
import com.jobtick.android.activities.DashboardActivity
import com.jobtick.android.models.UserAccountModel
import com.jobtick.android.network.retrofit.ApiClient
import com.jobtick.android.utils.Helper
import com.jobtick.android.utils.SessionManager
import okhttp3.MultipartBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber

class   ProfileNewViewModel: ViewModel() {

    var userAccountModelObservable = MutableLiveData<UserAccountModel>()
    var success = MutableLiveData<Boolean>()
    var error = MutableLiveData<String>()
    var sessionManager: SessionManager? = null


     fun updateProfile(context: Context, inputs: MutableMap<String, String>) {
         sessionManager = SessionManager(context)
        (context as DashboardActivity).showProgressDialog()
        val call: Call<String?>?
        Helper.closeKeyboard(context)
        val requestBody = MultipartBody.Builder().setType(MultipartBody.FORM).apply {
            for (input in inputs) {
                addFormDataPart(input.key, input.value)
                Log.d("responseUpdateProfile", input.key + ":" + input.value)
            }
            sessionManager?.role?.let { addFormDataPart("role_as", it) }
        }.build()
        call = ApiClient.getClientV1WithToken(sessionManager).editProfile(
            "XMLHttpRequest",
            requestBody
        )
        call!!.enqueue(object : Callback<String?> {
            override fun onResponse(call: Call<String?>, response: Response<String?>) {
                context.hideProgressDialog()
                try {
                    Log.d("responseUpdateProfile", response.toString())
                    val jsonObject = JSONObject(response.body()!!)
                    Log.d("responseUpdateProfile", jsonObject.toString())
                    val jsonObjectUser = jsonObject.getJSONObject("data")
                    val userAccountModel = UserAccountModel().getJsonToModel(jsonObjectUser)
                    sessionManager?.userAccount = userAccountModel
                    userAccountModelObservable.value = UserAccountModel().getJsonToModel(jsonObjectUser)
                    context.showSuccessToast(jsonObject.getString("message"), context)
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                    context.showToast("Something Went Wrong", context)
                }
            }
            override fun onFailure(call: Call<String?>, t: Throwable) {
                context.hideProgressDialog()
                Timber.e(call.toString())
            }
        })
    }

    fun emailResendOTP(context: Context, inputs: MutableMap<String, String>) {
        sessionManager = SessionManager(context)
        (context as DashboardActivity).showProgressDialog()
        val call: Call<String?>?
        Helper.closeKeyboard(context)
        val requestBody = MultipartBody.Builder().setType(MultipartBody.FORM).apply {
            for (input in inputs) {
                addFormDataPart(input.key, input.value)
                Log.d("responseUpdateProfile", input.key + ":" + input.value)
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
                        Log.d("responseUpdateProfilesec", jsonObject.toString())
                        context.showSuccessToast(jsonObject.getString("message"), context)
                        success.value = true
                    } else {
                        context.showToast("Something Went Wrong", context)

                        Log.d("responseUpdateProfilecode", response.code().toString())
                        success.value = false
                    }

                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                    context.showToast("Something Went Wrong", context)
                    success.value = false
                }
            }
            override fun onFailure(call: Call<String?>, t: Throwable) {
                error.value = t.toString()
                Log.d("responseUpdateProfileerr", t.toString())
                context.showToast(t.toString(), context)
                context.hideProgressDialog()
                Timber.e(call.toString())
                success.value = false
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
                Log.d("responseUpdateProfile", input.key + ":" + input.value)
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
                        val jsonObject = JSONObject(response.body()!!)
                        Log.d("responseUpdateProfilesec", jsonObject.toString())
                        context.showSuccessToast(jsonObject.getString("message"), context)
                        success.value = true
                    } else {
                        context.showToast("Something Went Wrong", context)

                        Log.d("responseUpdateProfilecode", response.code().toString())
                        success.value = false
                    }

                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                    context.showToast("Something Went Wrong", context)
                    success.value = false
                }
            }
            override fun onFailure(call: Call<String?>, t: Throwable) {
                error.value = t.toString()
                Log.d("responseUpdateProfileerr", t.toString())
                context.showToast(t.toString(), context)
                context.hideProgressDialog()
                Timber.e(call.toString())
                success.value = false
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