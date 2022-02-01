package com.jobtick.android.activities

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.appbar.MaterialToolbar
import com.jobtick.android.BuildConfig
import com.jobtick.android.R
import com.jobtick.android.utils.Constant
import com.jobtick.android.utils.ConstantKey
import com.jobtick.android.utils.KeyboardUtil
import com.jobtick.android.widget.ExtendedCommentText
import com.jobtick.android.widget.ExtendedEntryText
import org.json.JSONException
import org.json.JSONObject
import timber.log.Timber

class ReportActivity : ActivityBase(), ExtendedEntryText.ExtendedViewOnClickListener {
    private lateinit var toolbar: MaterialToolbar
    private lateinit var root: RelativeLayout
    private lateinit var edtSubject: ExtendedEntryText
    private lateinit var edtDescription: ExtendedCommentText
    private lateinit var submit: Button
    private lateinit var spinnerSpam: TextView
    private lateinit var spinnerFraud: TextView
    private lateinit var spinnerOffensive: TextView
    private lateinit var spinnerOthers: TextView
    private lateinit var spinnerContainer: CardView
    private var strSlug: String? = null
    private var strUserid = 0
    private var strOfferid = 0
    private var strCommentid = 0
    private var strQuestionid = 0
    private var strKey: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super
                .onCreate(savedInstanceState)
        setContentView(R.layout.activity_report)
        setIDs()
        initToolbar()
        onViewClick()
        getVars()
        edtSubject.setExtendedViewOnClickListener(this)
        root.setOnClickListener { v: View? -> if (spinnerVisible) hideSpinner() }
    }

    private fun getVars() {
        val bundle = intent.extras
        if (bundle != null) {
            if (bundle.containsKey(ConstantKey.SLUG)) {
                strKey = bundle.getString("key")
                strSlug = bundle.getString(ConstantKey.SLUG)
            }
            if (bundle.containsKey(Constant.userID)) {
                strKey = bundle.getString("key")
                strUserid = bundle.getInt(Constant.userID)
            }
            if (bundle.containsKey(ConstantKey.offerId)) {
                strKey = bundle.getString("key")
                strOfferid = bundle.getInt(ConstantKey.offerId)
            }
            if (bundle.containsKey(ConstantKey.questionId)) {
                strKey = bundle.getString("key")
                strQuestionid = bundle.getInt(ConstantKey.questionId)
            }
            if (bundle.containsKey(ConstantKey.commentId)) {
                strKey = bundle.getString("key")
                strCommentid = bundle.getInt(ConstantKey.commentId)
            }
        }
    }

    private fun setIDs() {
        toolbar = findViewById(R.id.toolbar)
        root = findViewById(R.id.report_root)
        edtSubject = findViewById(R.id.edt_subject)
        edtDescription = findViewById(R.id.edt_description)
        submit = findViewById(R.id.submit)
        spinnerSpam = findViewById(R.id.spinner_spam)
        spinnerFraud = findViewById(R.id.spinner_fraud)
        spinnerOffensive = findViewById(R.id.spinner_offensive)
        spinnerOthers = findViewById(R.id.spinner_others)
        spinnerContainer = findViewById(R.id.spinner_container)
    }

    private fun initToolbar() {
        toolbar.setNavigationIcon(R.drawable.ic_back_black)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.title = "Report Job"
        toolbar.setNavigationOnClickListener { onBackPressed() }
    }

   private fun onViewClick() {
        submit.setOnClickListener {
            when (strKey) {
                ConstantKey.KEY_USER_REPORT -> {
                    reportUserSpam()
                }
                ConstantKey.KEY_TASK_REPORT -> {
                    reportTaskSpam()
                }
                ConstantKey.KEY_COMMENT_REPORT -> {
                    reportCommentSpam()
                }
                ConstantKey.KEY_OFFER_REPORT -> {
                    reportOfferSpam()
                }
                ConstantKey.KEY_QUESTION_REPORT -> {
                    reportQuestionSpam()
                }
            }
        }
        spinnerFraud.setOnClickListener {
            selectSpinnerItem(spinnerFraud.text.toString())
        }
        spinnerSpam.setOnClickListener {
            selectSpinnerItem(spinnerSpam.text.toString())
        }
        spinnerOffensive.setOnClickListener {
            selectSpinnerItem(spinnerOffensive.text.toString())
        }
        spinnerOthers.setOnClickListener {
            selectSpinnerItem(spinnerOthers.text.toString())
        }

    }

    private fun selectSpinnerItem(title: String) {
        edtSubject.text = title
        hideSpinner()
    }

    private fun reportTaskSpam() {
        //  {{baseurl}}/tasks/:slug/report
        showProgressDialog()
        val stringRequest: StringRequest = object : StringRequest(Method.POST, Constant.URL_TASKS + "/" + strSlug + "/report",
                Response.Listener { response: String? ->
                    Timber.e(response)
                    try {
                        val jsonObject = JSONObject(response!!)
                        if (jsonObject.has("success") && !jsonObject.isNull("success")) {
                            if (jsonObject.getBoolean("success")) {
                                showSuccessToast("Reported Task Successfully", this)
                            } else {
                                showToast("Something went wrong !", this@ReportActivity)
                            }
                        } else {
                            showToast(getString(R.string.server_went_wrong), this@ReportActivity)
                        }
                        hideProgressDialog()
                    } catch (e: JSONException) {
                        hideProgressDialog()
                        e.printStackTrace()
                    }
                },
                Response.ErrorListener { error: VolleyError ->
                    hideProgressDialog()

                    //  swipeRefresh.setRefreshing(false);
                    errorHandle1(error.networkResponse)
                }) {
            override fun getParams(): Map<String, String> {
                val map1: MutableMap<String, String> = HashMap()
                map1["subject"] = edtSubject.text
                map1["description"] = edtDescription.text
                return map1
            }

            override fun getHeaders(): Map<String, String> {
                val map1: MutableMap<String, String> = HashMap()
                map1["authorization"] = sessionManager.tokenType + " " + sessionManager.accessToken
                map1["Content-Type"] = "application/x-www-form-urlencoded"
                map1["X-Requested-With"] = "XMLHttpRequest"
                map1["Version"] = BuildConfig.VERSION_CODE.toString()
                return map1
            }
        }
        stringRequest.retryPolicy = DefaultRetryPolicy(0, -1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
        val requestQueue = Volley.newRequestQueue(this@ReportActivity)
        requestQueue.add(stringRequest)
    }

    private fun reportUserSpam() {
        showProgressDialog()
        val stringRequest: StringRequest = object : StringRequest(Method.POST, Constant.URL_TASKS + "/" + strUserid + "/report",
                Response.Listener { response: String? ->
                    Timber.e(response)
                    try {
                        val jsonObject = JSONObject(response!!)
                        if (jsonObject.has("success") && !jsonObject.isNull("success")) {
                            if (jsonObject.getBoolean("success")) {
                                showSuccessToast("Reported User Successfully", this)
                            } else {
                                showToast("Something went wrong ", this@ReportActivity)
                            }
                        } else {
                            showToast(getString(R.string.server_went_wrong), this@ReportActivity)
                        }
                        hideProgressDialog()
                    } catch (e: JSONException) {
                        hideProgressDialog()
                        e.printStackTrace()
                    }
                },
                Response.ErrorListener { error: VolleyError ->
                    hideProgressDialog()

                    //  swipeRefresh.setRefreshing(false);
                    errorHandle1(error.networkResponse)
                }) {
            override fun getParams(): Map<String, String> {
                val map1: MutableMap<String, String> = HashMap()
                map1["subject"] = edtSubject.text
                map1["description"] = edtDescription.text
                return map1
            }

            override fun getHeaders(): Map<String, String> {
                val map1: MutableMap<String, String> = HashMap()
                map1["authorization"] = sessionManager.tokenType + " " + sessionManager.accessToken
                map1["Content-Type"] = "application/x-www-form-urlencoded"
                map1["X-Requested-With"] = "XMLHttpRequest"
                map1["Version"] = BuildConfig.VERSION_CODE.toString()
                return map1
            }
        }
        stringRequest.retryPolicy = DefaultRetryPolicy(0, -1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
        val requestQueue = Volley.newRequestQueue(this@ReportActivity)
        requestQueue.add(stringRequest)
    }

    private fun reportQuestionSpam() {
        showProgressDialog()
        val stringRequest: StringRequest = object : StringRequest(Method.POST, Constant.URL_TASKS + "/" + strQuestionid + "/report",
                Response.Listener { response: String? ->
                    Timber.e(response)
                    try {
                        val jsonObject = JSONObject(response!!)
                        if (jsonObject.has("success") && !jsonObject.isNull("success")) {
                            if (jsonObject.getBoolean("success")) {
                                showSuccessToast("Question Reported Successfully", this)
                            } else {
                                showToast("Something went wrong !", this@ReportActivity)
                            }
                        } else {
                            showToast(getString(R.string.server_went_wrong), this@ReportActivity)
                        }
                        hideProgressDialog()
                    } catch (e: JSONException) {
                        hideProgressDialog()
                        e.printStackTrace()
                    }
                },
                Response.ErrorListener { error: VolleyError ->
                    hideProgressDialog()
                    errorHandle1(error.networkResponse)
                }) {
            override fun getParams(): Map<String, String> {
                val map1: MutableMap<String, String> = HashMap()
                map1["subject"] = edtSubject.text
                map1["description"] = edtDescription.text
                return map1
            }

            override fun getHeaders(): Map<String, String> {
                val map1: MutableMap<String, String> = HashMap()
                map1["authorization"] = sessionManager.tokenType + " " + sessionManager.accessToken
                map1["Content-Type"] = "application/x-www-form-urlencoded"
                map1["X-Requested-With"] = "XMLHttpRequest"
                map1["Version"] = BuildConfig.VERSION_CODE.toString()
                return map1
            }
        }
        stringRequest.retryPolicy = DefaultRetryPolicy(0, -1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
        val requestQueue = Volley.newRequestQueue(this@ReportActivity)
        requestQueue.add(stringRequest)
    }

    private fun reportCommentSpam() {
        showProgressDialog()
        val stringRequest: StringRequest = object : StringRequest(Method.POST, Constant.URL_TASKS + "/" + strCommentid + "/report",
                Response.Listener { response: String? ->
                    Timber.e(response)
                    try {
                        val jsonObject = JSONObject(response!!)
                        if (jsonObject.has("success") && !jsonObject.isNull("success")) {
                            if (jsonObject.getBoolean("success")) {
                                showSuccessToast("Comment Reported Successfully", this)
                            } else {
                                showToast("Something went wrong !", this@ReportActivity)
                            }
                        } else {
                            showToast(getString(R.string.server_went_wrong), this@ReportActivity)
                        }
                        hideProgressDialog()
                    } catch (e: JSONException) {
                        hideProgressDialog()
                        e.printStackTrace()
                    }
                },
                Response.ErrorListener { error: VolleyError ->
                    hideProgressDialog()
                    errorHandle1(error.networkResponse)
                }) {
            override fun getParams(): Map<String, String> {
                val map1: MutableMap<String, String> = HashMap()
                map1["subject"] = edtSubject.text
                map1["description"] = edtDescription.text
                return map1
            }

            override fun getHeaders(): Map<String, String> {
                val map1: MutableMap<String, String> = HashMap()
                map1["authorization"] = sessionManager.tokenType + " " + sessionManager.accessToken
                map1["Content-Type"] = "application/x-www-form-urlencoded"
                map1["X-Requested-With"] = "XMLHttpRequest"
                map1["Version"] = BuildConfig.VERSION_CODE.toString()
                return map1
            }
        }
        stringRequest.retryPolicy = DefaultRetryPolicy(0, -1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
        val requestQueue = Volley.newRequestQueue(this@ReportActivity)
        requestQueue.add(stringRequest)
    }

    private fun reportOfferSpam() {
        //{{baseurl}}/offers/:id/report
        showProgressDialog()
        val stringRequest: StringRequest = object : StringRequest(Method.POST, Constant.URL_OFFERS + "/" + strOfferid + "/report",
                Response.Listener { response: String? ->
                    Timber.e(response)
                    try {
                        val jsonObject = JSONObject(response!!)
                        if (jsonObject.has("success") && !jsonObject.isNull("success")) {
                            if (jsonObject.getBoolean("success")) {
                                showSuccessToast("Offer Reported Successfully", this)
                            } else {
                                showToast("Something went wrong !", this@ReportActivity)
                            }
                        } else {
                            showToast(getString(R.string.server_went_wrong), this@ReportActivity)
                        }
                        hideProgressDialog()
                    } catch (e: JSONException) {
                        hideProgressDialog()
                        e.printStackTrace()
                    }
                },
                Response.ErrorListener { error: VolleyError ->
                    hideProgressDialog()
                    errorHandle1(error.networkResponse)
                }) {
            override fun getParams(): Map<String, String> {
                val map1: MutableMap<String, String> = HashMap()
                map1["subject"] = edtSubject.text
                map1["description"] = edtDescription.text
                return map1
            }
            override fun getHeaders(): Map<String, String> {
                val map1: MutableMap<String, String> = HashMap()
                map1["authorization"] = sessionManager.tokenType + " " + sessionManager.accessToken
                map1["Content-Type"] = "application/x-www-form-urlencoded"
                map1["X-Requested-With"] = "XMLHttpRequest"
                map1["Version"] = BuildConfig.VERSION_CODE.toString()
                return map1
            }
        }
        stringRequest.retryPolicy = DefaultRetryPolicy(0, -1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
        val requestQueue = Volley.newRequestQueue(this@ReportActivity)
        requestQueue.add(stringRequest)
    }

    override fun onClick() {
        showSpinner()
    }

    private var spinnerVisible = false
    private fun showSpinner() {
        if (spinnerVisible) return
        spinnerVisible = true
        spinnerContainer.visibility = View.VISIBLE
        edtDescription.isClickable = false
        edtDescription.isFocusable = false
        KeyboardUtil.hideKeyboard(this)
        spinnerContainer.animate().alpha(1f).setDuration(250).start()
    }

    private fun hideSpinner() {
        if (!spinnerVisible) return
        spinnerVisible = false
        edtDescription.isClickable = true
        edtDescription.isFocusable = true
        spinnerContainer.animate().alpha(0f).setDuration(250).start()
        spinnerContainer.visibility = View.GONE
    }
}