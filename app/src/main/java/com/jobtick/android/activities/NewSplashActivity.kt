package com.jobtick.android.activities

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.TypefaceSpan
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowInsets
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import butterknife.ButterKnife
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import com.jobtick.android.BuildConfig
import com.jobtick.android.R
import com.jobtick.android.material.ui.landing.OnboardingActivity
import com.jobtick.android.models.UserAccountModel
import com.jobtick.android.models.response.checkforupdate.CheckForUpdateResponse
import com.jobtick.android.utils.Constant
import com.jobtick.android.utils.ConstantKey
import com.jobtick.android.utils.CustomTypefaceSpan
import com.jobtick.android.utils.SessionManager
import org.json.JSONException
import org.json.JSONObject
import java.util.*


class NewSplashActivity : AppCompatActivity() {
    var sessionManager: SessionManager? = null
    lateinit var animation: Animation
    lateinit var loveText: RelativeLayout
    lateinit var progressBar: ProgressBar
    lateinit var heart : ImageView

    @SuppressLint("NonConstantResourceId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_splash)
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        //window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        ButterKnife.bind(this)
        animation = AnimationUtils.loadAnimation(
            applicationContext,
            R.anim.fadein
        )
        animation.duration = 2000
        loveText = findViewById(R.id.loveParent)
        progressBar = findViewById(R.id.progressbar)
        heart = findViewById(R.id.love)
        loveText.startAnimation(animation)
        val handler = Handler(Looper.getMainLooper())
        handler.postDelayed({
            sessionManager = SessionManager(this@NewSplashActivity)
//            val intent = Intent(this, DashboardActivity::class.java)
//            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//            startActivity(intent)
            progressBar.visibility = View.VISIBLE
            checkForUpdate()
           // accountDetails
        }, 2000)
    }


    private fun login() {
        if (sessionManager!!.tokenType != null && sessionManager!!.accessToken != null) {
            accountDetails
        } else {
            openSignUpPage()
        }
    }

    private val accountDetails: Unit
        get() {
            val stringRequest: StringRequest = object : StringRequest(Method.GET, Constant.URL_GET_ACCOUNT,
                    Response.Listener { response: String? ->
                        try {
                            val jsonObject = JSONObject(response!!)
                            val jsonObject_data = jsonObject.getJSONObject("data")
                            sessionManager!!.role =  jsonObject.getJSONObject("data").getString("role")
                            val userAccountModel = UserAccountModel().getJsonToModel(jsonObject_data)
                            sessionManager!!.userAccount = userAccountModel
                            if (sessionManager!!.userAccount.account_status.isBasic_info) {
                                sessionManager!!.latitude = java.lang.Double.toString(userAccountModel.latitude)
                                sessionManager!!.longitude = java.lang.Double.toString(userAccountModel.longitude)
                                val main = Intent(this@NewSplashActivity, OnboardingActivity::class.java)
                                main.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                main.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                if (intent?.data?.encodedPath != null){
                                    val bundle = Bundle()
                                    bundle.putString(ConstantKey.DEEP_LINK_BUNDLE, intent?.data?.encodedPath)
                                    main.putExtras(bundle)
                                }

                                startActivity(main)
                            } else {
                                val main = Intent(this@NewSplashActivity, OnboardingActivity::class.java)
                                main.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                main.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                startActivity(main)
                            }
                        } catch (e: JSONException) {
                            e.printStackTrace()
                            openSignUpPage()
                        }
                    },
                    Response.ErrorListener { error: VolleyError? ->

                        openSignUpPage() }) {
                override fun getHeaders(): Map<String, String> {
                    val map1: MutableMap<String, String> = HashMap()
                    map1["authorization"] = sessionManager!!.tokenType + " " + sessionManager!!.accessToken
                    map1["Content-Type"] = "application/x-www-form-urlencoded"
                    map1["X-Requested-With"] = "XMLHttpRequest"
                    return map1
                }
            }
            stringRequest.retryPolicy = DefaultRetryPolicy(0, -1,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
            val requestQueue = Volley.newRequestQueue(this@NewSplashActivity)
            requestQueue.add(stringRequest)
        }

    private fun openSignUpPage() {
        val intent = Intent(this@NewSplashActivity, OnboardingActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun checkForUpdate() {
        Log.d("splashcheckupdate", Constant.BASE_URL + Constant.CHECK_UPDATE)

        val stringRequest: StringRequest = object : StringRequest(Method.GET, Constant.BASE_URL + Constant.CHECK_UPDATE,
                Response.Listener { response: String? ->
                    try {

                        val jsonObject = JSONObject(response!!)
                        val jsonString = jsonObject.toString() //http request
                        val gson = Gson()
                        val (data, _, success) = gson.fromJson(jsonString, CheckForUpdateResponse::class.java)
                        if (success!!) {
                            when {
                                BuildConfig.VERSION_CODE < data!!.latest_major_version!!.toInt() -> {
                                    val intent = Intent(this@NewSplashActivity, UpdateActivity::class.java)
                                    startActivity(intent)
                                    finish()
                                }
                                BuildConfig.VERSION_CODE < data.latest_version!!.toInt() -> {
                                    showNormalUpdate()
                                }
                                else -> {
                                    login()
                                }
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        login()
                    }
                },
                Response.ErrorListener {
                    error: VolleyError ->
                    login()
                } /*errorHandle1(error.networkResponse)*/) {
            override fun getHeaders(): Map<String, String> {
                val map1: MutableMap<String, String> = HashMap()
                map1["authorization"] = sessionManager!!.tokenType + " " + sessionManager!!.accessToken
                map1["Content-Type"] = "application/x-www-form-urlencoded"
                map1["X-Requested-With"] = "XMLHttpRequest"
                return map1
            }
        }
        stringRequest.retryPolicy = DefaultRetryPolicy(0, -1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
        val requestQueue = Volley.newRequestQueue(this)
        requestQueue.add(stringRequest)
    }

    private fun showNormalUpdate() {
        val dialog = Dialog(this, R.style.DialogTheme)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.update_dialog)
        val comment = dialog.findViewById(R.id.comment) as TextView
        val update = dialog.findViewById(R.id.btn_update) as TextView
        val no = dialog.findViewById(R.id.no_tanks) as TextView
        val txt = comment.text.toString()
        val robotoRegular = ResourcesCompat.getFont(this, R.font.roboto_regular)
        val robotoBold = ResourcesCompat.getFont(this, R.font.roboto_bold)


        val robotoRegularSpan: TypefaceSpan = CustomTypefaceSpan("", robotoRegular)
        val robotoBoldSpan: TypefaceSpan = CustomTypefaceSpan("", robotoBold)
        val sb = SpannableStringBuilder(txt)

        sb.setSpan(robotoBoldSpan, 0, 9, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
        sb.setSpan(robotoRegularSpan, 9, txt.length, Spannable.SPAN_INCLUSIVE_INCLUSIVE)

        comment.text = sb
        update.setOnClickListener {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(Constant.URL_google_play))
            startActivity(browserIntent)
        }
        no.setOnClickListener {
            dialog.dismiss()
            login()
        }
        dialog.show()

    }
}