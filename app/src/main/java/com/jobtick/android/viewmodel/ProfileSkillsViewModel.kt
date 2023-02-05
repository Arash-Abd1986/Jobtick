package com.jobtick.android.viewmodel

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.material.textfield.TextInputEditText
import com.google.gson.Gson
import com.jobtick.android.R
import com.jobtick.android.activities.DashboardActivity
import com.jobtick.android.adapers.SkillsSearchAdapter
import com.jobtick.android.material.ui.landing.OnboardingActivity
import com.jobtick.android.models.UserAccountModel
import com.jobtick.android.models.response.allSkills.AllSkillsResponse
import com.jobtick.android.models.response.allSkills.Skills
import com.jobtick.android.network.retrofit.ApiClient
import com.jobtick.android.utils.ConstantKey
import com.jobtick.android.utils.Helper
import com.jobtick.android.utils.SessionManager
import okhttp3.MultipartBody
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber

class   ProfileSkillsViewModel: ViewModel() {

    var userAccountModelObservable = MutableLiveData<UserAccountModel>()
    var success = MutableLiveData<Boolean>()
    var error = MutableLiveData<String>()
    var sessionManager: SessionManager? = null
    var skillsSearchAdapter = MutableLiveData<SkillsSearchAdapter?>()
    var skillsSearchAdapter1 = SkillsSearchAdapter()
    var skillList : MutableLiveData<MutableList<Skills>>? = null
    var jsonobject = MutableLiveData<JSONObject>()
    var skillsArray = MutableLiveData<ArrayList<String>>()

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

                            val jsonObject = JSONObject(response.body().toString())
                            val gson = Gson()
                            val (_, data) = gson.fromJson(
                                jsonObject.toString(),
                                AllSkillsResponse::class.java)

//                            skillList?.value = data as MutableList<Skills>
//                            skillsSearchAdapter1 = SkillsSearchAdapter()
                            skillsSearchAdapter1.clear()
                            skillsSearchAdapter1.addItems((data as MutableList<Skills>?)!!)
//                            skillsSearchAdapter = MutableLiveData()
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


        val call = ApiClient.getClientV1WithToken(sessionManager).getSkills(
            "XMLHttpRequest",
        )
        call!!.enqueue(object : Callback<String?> {
            override fun onResponse(call: Call<String?>, response: Response<String?>) {
                context.hideProgressDialog()
                try {
                if(response.isSuccessful) {

                        val jsonObject = JSONObject(response.body().toString())
                        val jsonObject_data = jsonObject.getJSONObject("data")
                        sessionManager!!.role = jsonObject.getJSONObject("data").getString("role")
                        val userAccountModel = UserAccountModel().getJsonToModel(jsonObject_data)
                        sessionManager!!.userAccount = userAccountModel
                        skillsArray.value = sessionManager!!.userAccount.skills.skills
                }else {
                    val jObjError = JSONObject(
                        response.errorBody()!!.string()
                    )
                    context.showToast(jObjError.getJSONObject("error").getString("message"), context)
                }
                } catch (e: Exception) {
                    e.printStackTrace()
                    context.showToast("Something Went Wrong", context)

                }
            }
            override fun onFailure(call: Call<String?>, t: Throwable) {
                context.hideProgressDialog()
                context.showToast(t.toString(), context)

                Timber.e(call.toString())
            }
        })
    }



    fun addSkill(context: Context, inputs: MutableMap<String, MutableList<String>>) {
        sessionManager = SessionManager(context)
        (context as DashboardActivity).showProgressDialog()
        val call: Call<String?>?
        Helper.closeKeyboard(context)
        val requestBody = MultipartBody.Builder().setType(MultipartBody.FORM).apply {
            val quizzes: List<String> = inputs["skills[]"].orEmpty()
            for (input in quizzes) {
                addFormDataPart("skills[]", input)
            }
            //sessionManager?.role?.let { addFormDataPart("role_as", it) }
        }.build()

        call = ApiClient.getClientV1WithToken(sessionManager).addSkills(
            "XMLHttpRequest",
            requestBody
        )
        call!!.enqueue(object : Callback<String?> {
            override fun onResponse(call: Call<String?>, response: Response<String?>) {
                context.showToast(context.getString(R.string.successfully_saved), context)
                context.hideProgressDialog()
            }
            override fun onFailure(call: Call<String?>, t: Throwable) {
                context.hideProgressDialog()
                context.showToast(context.getString(R.string.server_went_wrong), context)

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