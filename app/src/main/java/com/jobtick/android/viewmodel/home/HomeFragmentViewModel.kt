package com.jobtick.android.viewmodel.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.android.volley.DefaultRetryPolicy
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.google.gson.Gson
import com.jobtick.android.BuildConfig
import com.jobtick.android.models.response.home.HomeResponse
import com.jobtick.android.utils.Constant
import org.json.JSONObject
import timber.log.Timber
import java.lang.Exception
import java.util.*

class HomeFragmentViewModel : ViewModel() {

    private val notifReponse = MutableLiveData<JSONObject>()
    private val error = MutableLiveData<String>()
    private val error2 = MutableLiveData<String>()
    fun getNotifResponse(): LiveData<JSONObject> {
        return this.notifReponse
    }

    private val homeResponse = MutableLiveData<JSONObject>()

    fun getHomeResponse(): LiveData<JSONObject> {
        return this.homeResponse
    }

    fun getError(): LiveData<String> {
        return this.error
    }
    fun getError2(): LiveData<String> {
        return this.error2
    }

    fun notificationList(token: String, requestQueue: RequestQueue) {
        val stringRequest: StringRequest = object : StringRequest(Method.GET, Constant.URL_NOTIFICATION_UNREAD,
                Response.Listener { response: String? ->
                    Timber.e(response)
                    if (response != null) {
                        val jsonObject = JSONObject(response)
                        Timber.e(jsonObject.toString())
                        notifReponse.postValue(jsonObject)
                    } else {
                        error2.postValue("")
                    }
                },
                Response.ErrorListener {
                    this.error2.postValue("error")
                }
        ) {
            override fun getHeaders(): Map<String, String> {
                val map1: MutableMap<String, String> = HashMap()
                map1["Content-Type"] = "application/x-www-form-urlencoded"
                map1["Authorization"] = "Bearer " + token
                map1["Version"] = BuildConfig.VERSION_CODE.toString()
                return map1
            }
        }
        stringRequest.retryPolicy = DefaultRetryPolicy(0, -1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
        requestQueue.add(stringRequest)
        Timber.e(stringRequest.url)
    }

    fun home(token: String, requestQueue: RequestQueue) {
        val stringRequest: StringRequest = object : StringRequest(Method.GET, Constant.HOME,
                Response.Listener { response: String? ->
                    Timber.e(response)
                    try {
                        if (response != null) {
                            val jsonObject = JSONObject(response)
                            Timber.e(jsonObject.toString())
                            val homeResponseTemp = Gson().fromJson(jsonObject.toString(), HomeResponse::class.java)
                            if (homeResponseTemp.success!!)
                                homeResponse.postValue(jsonObject)
                            else {
                                error.postValue("Something went wrong")
                            }
                        } else {
                            error.postValue("Something went wrong")
                        }
                    }catch (e: Exception){
                        error.postValue("Something went wrong")
                    }
                },
                Response.ErrorListener { error: VolleyError? ->
                    this.error.postValue("Something went wrong")
                }
        ) {
            override fun getHeaders(): Map<String, String> {
                val map1: MutableMap<String, String> = HashMap()
                map1["Content-Type"] = "application/x-www-form-urlencoded"
                map1["Authorization"] = "Bearer " + token
                map1["Version"] = BuildConfig.VERSION_CODE.toString()
                return map1
            }
        }
        stringRequest.retryPolicy = DefaultRetryPolicy(0, -1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
        requestQueue.add(stringRequest)
        Timber.e(stringRequest.url)
    }
}