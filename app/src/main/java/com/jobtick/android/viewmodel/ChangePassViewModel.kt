package com.jobtick.android.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.android.volley.*
import com.android.volley.toolbox.StringRequest
import com.jobtick.android.BuildConfig
import com.jobtick.android.utils.Constant
import org.json.JSONObject
import timber.log.Timber
import java.lang.Exception
import java.util.*

class ChangePassViewModel : ViewModel() {

    private val error = MutableLiveData<NetworkResponse>()
    private val error2 = MutableLiveData<String>()
    private val jobsResponse = MutableLiveData<JSONObject>()

    fun getError2(): LiveData<String> {
        return this.error2
    }
    fun changePassResponse(): LiveData<JSONObject> {
        return this.jobsResponse
    }

    fun getError(): LiveData<NetworkResponse> {
        return this.error
    }


    fun changePass(token: String, requestQueue: RequestQueue, strOldPassword: String, strNewPassword: String) {
        val stringRequest: StringRequest = object : StringRequest(Method.POST, Constant.URL_CHANGE_PASSWORD,
                Response.Listener{ response: String? ->
                    Timber.e(response)
                    try {
                        val jsonObject = JSONObject(response)
                        Timber.e(jsonObject.toString())
                        jobsResponse.postValue(jsonObject)
                    } catch (exception: Exception) {
                        this.error2.postValue("Something went wrong")
                    }
                },
                Response.ErrorListener { error: VolleyError -> this.error.postValue(error.networkResponse) }) {
            override fun getHeaders(): Map<String, String> {
                val map1: MutableMap<String, String> = HashMap()
                map1["Content-Type"] = "application/x-www-form-urlencoded"
                map1["Authorization"] = "Bearer $token"
                map1["Version"] = BuildConfig.VERSION_CODE.toString()
                return map1
            }
            override fun getParams(): Map<String, String> {
                val map1: MutableMap<String, String> = HashMap()
                map1["old_password"] = strOldPassword
                map1["new_password"] = strNewPassword
                Timber.e(map1.size.toString())
                Timber.e(map1.toString())
                return map1
            }
        }
        stringRequest.retryPolicy = DefaultRetryPolicy(
                0, -1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )
        requestQueue.add(stringRequest)
        Timber.e(stringRequest.url)
    }
}