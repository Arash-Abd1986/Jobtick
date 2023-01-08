package com.jobtick.android.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.material.textfield.TextInputEditText
import com.google.gson.Gson
import com.jobtick.android.activities.DashboardActivity
import com.jobtick.android.adapers.SkillsSearchAdapter
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
    var skillsSearchAdapter = MutableLiveData<SkillsSearchAdapter?>()
    var skillsSearchAdapter1 = SkillsSearchAdapter()
    var skillList : MutableLiveData<MutableList<Skills>>? = null
    var jsonobject = MutableLiveData<JSONObject>()

    fun getAllSkills(context: Context) {
        sessionManager = SessionManager(context)
        (context as DashboardActivity).showProgressDialog()
        Helper.closeKeyboard(context)
        val call: Call<String?>? = ApiClient.getClientV1WithToken(sessionManager).getAllSkills(
            "XMLHttpRequest"
        )
        call!!.enqueue(object : Callback<String?> {
            override fun onResponse(call: Call<String?>, response: Response<String?>) {
                context.hideProgressDialog()
                try {
                    if(response.isSuccessful) {

                            val jsonObject = JSONObject(response.body())
                            val gson = Gson()
                            val (_, data) = gson.fromJson(
                                jsonObject.toString(),
                                AllSkillsResponse::class.java)

                            skillList?.value = data as MutableList<Skills>
                            skillsSearchAdapter1 = SkillsSearchAdapter()
                            skillsSearchAdapter1.clear()
                            skillsSearchAdapter1.addItems((data as MutableList<Skills>?)!!)
                            skillsSearchAdapter.value = skillsSearchAdapter1
                    }else
                    {
                        val jObjError = JSONObject(
                            response.errorBody()!!.string()
                        )
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

    fun getSkills(context: Context) {
        sessionManager = SessionManager(context)
        (context as DashboardActivity).showProgressDialog()
        Helper.closeKeyboard(context)
        val call: Call<String?>? = ApiClient.getClientV1WithToken(sessionManager).getSkills(
            "XMLHttpRequest"
        )
        call!!.enqueue(object : Callback<String?> {
            override fun onResponse(call: Call<String?>, response: Response<String?>) {
                context.hideProgressDialog()
                try {
                    if(response.isSuccessful) {
                        Log.d("responseUpdateProfile", response.toString())
                        val jsonObject = JSONObject(response.body()!!)
                        Log.d("responseUpdateProfile", jsonObject.toString())
                        jsonobject.value = jsonObject.getJSONObject("data")
//                        for(i in 0 until jsonObjectSkills.length()) {
//                            jsonObjectSkills.getJSONObject(i.toString()).getString("id")
//                            jsonObjectSkills.getJSONObject(i.toString()).getString("title")
//                        }
                    }else
                    {
                        val jObjError = JSONObject(
                            response.errorBody()!!.string()
                        )
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


    fun addSkill(context: Context, inputs: MutableMap<String, String>) {
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
        call = ApiClient.getClientV1WithToken(sessionManager).uploadProfile(
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

    fun validate(view: List<TextInputEditText>): Boolean {
        for (t in view)
            if(t.text?.trim()!!.isEmpty())
                return false
        return true
    }



}