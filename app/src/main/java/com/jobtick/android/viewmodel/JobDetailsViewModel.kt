package com.jobtick.android.viewmodel

import android.content.Context
import android.os.Handler
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.android.volley.DefaultRetryPolicy
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.jobtick.android.activities.TaskDetailsActivity
import com.jobtick.android.models.OfferModel
import com.jobtick.android.models.TaskModel
import com.jobtick.android.models.event.EventRequest
import com.jobtick.android.network.coroutines.MainRepository
import com.jobtick.android.network.coroutines.Resource
import com.jobtick.android.network.coroutines.ServiceType
import com.jobtick.android.network.coroutines.getData
import com.jobtick.android.network.model.Response
import com.jobtick.android.network.model.request.ReviewsRequest
import com.jobtick.android.utils.Constant
import org.json.JSONObject
import timber.log.Timber
import java.util.function.Consumer

class JobDetailsViewModel(private val mainRepository: MainRepository) : ViewModel() {
    private val taskModelResponse = MutableLiveData<TaskModel>()
    private val error = MutableLiveData<String>()
    private val error2 = MutableLiveData<String>()

    enum class UserType {
        POSTER, TICKER, VIEWER
    }

    var userType  = UserType.VIEWER


    fun geTaskModelResponse(): LiveData<TaskModel> {
        return this.taskModelResponse
    }

    fun getError(): LiveData<String> {
        return this.error
    }

    fun getError2(): LiveData<String> {
        return this.error2
    }

    fun getReviews(request: ReviewsRequest): LiveData<Resource<Response>> {
        return getData(ServiceType.REVIEWS, mainRepository, request)
    }

    fun sendEvent(request: EventRequest): LiveData<Resource<Response>> {
        return getData(ServiceType.EVENT, mainRepository, request)
    }

    fun getDetails(request: EventRequest): LiveData<Resource<Response>> {
        return getData(ServiceType.TASK_DETAILS, mainRepository, request)
    }

    fun getTaskModel(context: Context, strSlug: String, tokenType: String, token: String, userId: Int) {
        val stringRequest: StringRequest =
                object : StringRequest(
                        Method.GET, Constant.URL_TASKS + "/" + strSlug,
                        com.android.volley.Response.Listener { response: String? ->
                            try {
                                Log.d("taskModel", response.toString())
                                val jsonObject = JSONObject(response!!)
                                Timber.e(jsonObject.toString())
                                println(jsonObject.toString())
                                if (jsonObject.has("success") &&
                                        !jsonObject.isNull("success") &&
                                        jsonObject.getBoolean("success")
                                ) {
                                    if (jsonObject.has("data") && !jsonObject.isNull("data")) {
                                        val jsonObjectData = jsonObject.getJSONObject("data")
                                        val taskModel = TaskModel().getJsonToModel(
                                                jsonObjectData,
                                                context
                                        )
                                        taskModel!!.offerSent = false
                                        taskModel.offers.forEach(
                                                Consumer { offerModel1: OfferModel ->
                                                    if (offerModel1.worker.id == userId) TaskDetailsActivity.taskModel!!.offerSent =
                                                            true
                                                }
                                        )
                                        taskModelResponse.postValue(taskModel)
                                        //endEvent()
                                        //setOwnerTask()
                                        //dismissStatusAlert()
                                        //initStatusTask(TaskDetailsActivity.taskModel!!.status.toLowerCase())
                                        //initComponent()
                                        //setDataInLayout(TaskDetailsActivity.taskModel!!)
                                        /*setPosterChatButton(
                                                TaskDetailsActivity.taskModel!!.status.toLowerCase(),
                                                jsonObjectData
                                        )*/
                                        //initRestConf(jsonObjectData)
                                    }
                                } else {
                                    /* showToast("Something went wrong", this@JobDetailsActivity)
                                     Handler().postDelayed({
                                         onBackPressed()
                                     }, 2000)*/
                                }
                            } catch (e: Exception) {
                                Log.d("taskModel1", e.toString())

                                /*showToast("Something went wrong", this@JobDetailsActivity)
                                Handler().postDelayed({
                                    onBackPressed()
                                }, 2000)
                                Timber.e(e.toString())
                                e.printStackTrace()*/
                            }
                            //isInitPageLoaded = true
                            //onLoadingFinished()
                        },
                        com.android.volley.Response.ErrorListener { error: VolleyError ->
                            /* //isInitPageLoaded = true
                             // llLoading.visibility = View.GONE
                             //onLoadingFinished()
                             errorHandle1(error.networkResponse)
                             Handler().postDelayed({
                                 onBackPressed()
                             }, 2000)*/
                        }
                ) {
                    override fun getHeaders(): Map<String, String> {
                        val map1: MutableMap<String, String> = HashMap()
                        map1["authorization"] =
                                "$tokenType $token"
                        map1["Content-Type"] = "application/x-www-form-urlencoded"
                        // map1.put("X-Requested-With", "XMLHttpRequest");
                        return map1
                    }
                }
        stringRequest.retryPolicy = DefaultRetryPolicy(
                0, -1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )
        val requestQueue = Volley.newRequestQueue(context)
        requestQueue.add(stringRequest)
    }
}
