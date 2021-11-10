package com.jobtick.android.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.android.volley.DefaultRetryPolicy
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.jobtick.android.BuildConfig
import com.jobtick.android.utils.Constant
import org.json.JSONObject
import timber.log.Timber
import java.util.*

class ChatSearchViewModel : ViewModel() {

    private val error = MutableLiveData<String>()
    private val jobsResponse = MutableLiveData<JSONObject>()

    fun getMessageResponse(): LiveData<JSONObject> {
        return this.jobsResponse
    }

    fun getError(): LiveData<String> {
        return this.error
    }



    fun getMessages(token: String, requestQueue: RequestQueue,query:String) {
        val stringRequest: StringRequest = object : StringRequest(Method.GET, Constant.URL_CHAT +
                "/conversations" + "?page=" + 1 + "&query=" + query,
                Response.Listener{ response: String? ->
                Timber.e(response)
                try {
                    val jsonObject = JSONObject(response!!)
                    Timber.e(jsonObject.toString())
                    jobsResponse.postValue(jsonObject)
                }catch (exception:Exception){
                    this.error.postValue("Something went wrong")
                }
            },
            Response.ErrorListener { this.error.postValue("Something went wrong") }) {
            override fun getHeaders(): Map<String, String> {
                val map1: MutableMap<String, String> = HashMap()
                map1["Content-Type"] = "application/x-www-form-urlencoded"
                map1["Authorization"] = "Bearer " + token
                map1["Version"] = BuildConfig.VERSION_CODE.toString()
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