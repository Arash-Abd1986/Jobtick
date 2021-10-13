package com.jobtick.android.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.*
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.jobtick.android.BuildConfig
import com.jobtick.android.models.UserAccountModel
import com.jobtick.android.utils.Constant
import com.jobtick.android.utils.SessionManager
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class SplashActivity : AppCompatActivity() {
    var sessionManager: SessionManager? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        val handler = Handler()
        handler.postDelayed({
            sessionManager = SessionManager(this@SplashActivity)
            login()
        }, 2000)
    }

    private fun login() {
        if (sessionManager!!.login) {
            accountDetails
        } else {
            val sign = Intent(this@SplashActivity, SigInSigUpActivity::class.java)
            sign.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            sign.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(sign)
            finish()
        }
    }

    private val accountDetails: Unit
        get() {
            val stringRequest: StringRequest = object : StringRequest(Method.GET, Constant.URL_GET_ACCOUNT,
                    Response.Listener { response: String? ->
                        try {
                            val jsonObject = JSONObject(response!!)
                            val jsonObjectData = jsonObject.getJSONObject("data")
                            val userAccountModel = UserAccountModel().getJsonToModel(jsonObjectData)
                            sessionManager!!.userAccount = userAccountModel
                            val main = Intent(this@SplashActivity, DashboardActivity::class.java)
                            main.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                            main.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(main)
                        } catch (e: JSONException) {
                            e.printStackTrace()
                            val sign = Intent(this@SplashActivity, SigInSigUpActivity::class.java)
                            sign.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                            sign.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(sign)
                            finish()
                        }
                    },
                    Response.ErrorListener { error: VolleyError? ->
                        val sign = Intent(this@SplashActivity, SigInSigUpActivity::class.java)
                        sign.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        sign.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(sign)
                        finish()
                    }) {
                @Throws(AuthFailureError::class)
                override fun getHeaders(): Map<String, String> {
                    val map1: MutableMap<String, String> = HashMap()
                    map1["authorization"] = sessionManager!!.tokenType + " " + sessionManager!!.accessToken
                    map1["Content-Type"] = "application/x-www-form-urlencoded"
                    map1["X-Requested-With"] = "XMLHttpRequest"
                    map1["Version"] = BuildConfig.VERSION_CODE.toString()
                    return map1
                }
            }
            stringRequest.retryPolicy = DefaultRetryPolicy(0, -1,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
            val requestQueue = Volley.newRequestQueue(this@SplashActivity)
            requestQueue.add(stringRequest)
        }
}