package com.jobtick.android.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.material.textfield.TextInputEditText
import com.google.gson.Gson
import com.jobtick.android.activities.DashboardActivity
import com.jobtick.android.models.UserAccountModel
import com.jobtick.android.models.response.allSkills.AllSkillsResponse
import com.jobtick.android.models.response.allSkills.Skills
import com.jobtick.android.network.retrofit.ApiClient
import com.jobtick.android.utils.Helper
import com.jobtick.android.utils.SessionManager
import okhttp3.MultipartBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber

class   ProfilePortfolioViewModel: ViewModel() {

    var userAccountModelObservable = MutableLiveData<UserAccountModel>()
    var success = MutableLiveData<Boolean>()
    var error = MutableLiveData<String>()
    var sessionManager: SessionManager? = null
    var skillList : MutableLiveData<MutableList<Skills>>? = null
    var jsonobject = MutableLiveData<JSONObject>()

    fun getPortfolioItems(context: Context) {
        sessionManager = SessionManager(context)
        (context as DashboardActivity).showProgressDialog()
        Helper.closeKeyboard(context)
        val call: Call<String?>? = ApiClient.getClientV1WithToken(sessionManager).getPortfolioItems(
            "XMLHttpRequest"
        )
        call!!.enqueue(object : Callback<String?> {
            override fun onResponse(call: Call<String?>, response: Response<String?>) {
                context.hideProgressDialog()
                try {
                    Log.d("portfoliores", response.toString())
                    if(response.isSuccessful) {

                            val jsonObject = JSONObject(response.body().toString())
//                        if(!jsonobject.hasObservers())
//                                jsonobject = MutableLiveData()

                            jsonobject.value = jsonObject
                    }else
                    {
                        val jObjError = JSONObject(
                            response.errorBody()!!.string()
                        )
                        Log.d("errorport", response.toString())
                        context.showToast(jObjError.getJSONObject("error").getString("message"), context)
                    }
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



    fun validate(view: List<TextInputEditText>): Boolean {
        for (t in view)
            if(t.text?.trim()!!.isEmpty())
                return false
        return true
    }



}
