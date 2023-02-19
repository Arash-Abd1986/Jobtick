package com.jobtick.android.activities

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.textfield.TextInputLayout
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

class ReportActivity : ActivityBase() {
    private lateinit var root: RelativeLayout
    private lateinit var edtDescription: TextInputLayout
    private lateinit var submit: Button
    private lateinit var spinnerSpam: TextView
    private lateinit var spinnerFraud: TextView
    private lateinit var spinnerOffensive: TextView
    private lateinit var spinnerOthers: TextView
    private lateinit var back: ImageView
    private var id: String? = null
    private var strKey: String? = null
    private var isFraud = false
    private var isSpam = false
    private var isHarOff = false
    private var isOther = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super
                .onCreate(savedInstanceState)
        setContentView(R.layout.activity_report)
        setIDs()
        onViewClick()
        getVars()
    }

    private fun getVars() {
        val bundle = intent.extras
        if (bundle != null) {
            if (bundle.containsKey(ConstantKey.SLUG)) {
                strKey = bundle.getString("key")
                id = bundle.getString(ConstantKey.SLUG)
            }
            if (bundle.containsKey(Constant.userID)) {
                strKey = bundle.getString("key")
                id = bundle.getInt(Constant.userID).toString()
            }
            if (bundle.containsKey(ConstantKey.offerId)) {
                strKey = bundle.getString("key")
                id = bundle.getInt(ConstantKey.offerId).toString()
            }
            if (bundle.containsKey(ConstantKey.questionId)) {
                strKey = bundle.getString("key")
                id = bundle.getInt(ConstantKey.questionId).toString()
            }
            if (bundle.containsKey(ConstantKey.commentId)) {
                strKey = bundle.getString("key")
                id = bundle.getInt(ConstantKey.commentId).toString()
            }
        }
    }

    private fun setIDs() {
        root = findViewById(R.id.report_root)
        edtDescription = findViewById(R.id.edt_description)
        submit = findViewById(R.id.submit)
        spinnerSpam = findViewById(R.id.spinner_spam)
        spinnerFraud = findViewById(R.id.spinner_fraud)
        spinnerOffensive = findViewById(R.id.spinner_offensive)
        spinnerOthers = findViewById(R.id.spinner_others)
        back = findViewById(R.id.back)
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun onViewClick() {
        submit.setOnClickListener {
            when (strKey) {
                ConstantKey.KEY_USER_REPORT -> {
                    reportTaskSpam()
                }
                ConstantKey.KEY_TASK_REPORT -> {
                    reportTaskSpam()
                }
                ConstantKey.KEY_COMMENT_REPORT -> {
                    reportTaskSpam()
                }
                ConstantKey.KEY_OFFER_REPORT -> {
                    reportOfferSpam()
                }
                ConstantKey.KEY_QUESTION_REPORT -> {
                    reportTaskSpam()
                }
            }
        }

        back.setOnClickListener { finish() }
        spinnerFraud.setOnClickListener {
            // selectSpinnerItem(spinnerFraud.text.toString())
            spinnerFraud.setCompoundDrawablesWithIntrinsicBounds(null, null, getDrawable(R.drawable.ic_check), null)
            spinnerSpam.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
            spinnerOffensive.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
            spinnerOthers.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
            isFraud = true
            isSpam = false
            isHarOff = false
            isOther = false
        }
        spinnerSpam.setOnClickListener {
            spinnerSpam.setCompoundDrawablesWithIntrinsicBounds(null, null, getDrawable(R.drawable.ic_check), null)
            spinnerFraud.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
            spinnerOffensive.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
            spinnerOthers.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
            isFraud = false
            isSpam = true
            isHarOff = false
            isOther = false
        }
        spinnerOffensive.setOnClickListener {
            spinnerOffensive.setCompoundDrawablesWithIntrinsicBounds(null, null, getDrawable(R.drawable.ic_check), null)
            spinnerSpam.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
            spinnerFraud.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
            spinnerOthers.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
            isFraud = false
            isSpam = false
            isHarOff = true
            isOther = false
        }
        spinnerOthers.setOnClickListener {
            spinnerOthers.setCompoundDrawablesWithIntrinsicBounds(null, null, getDrawable(R.drawable.ic_check), null)
            spinnerSpam.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
            spinnerOffensive.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
            spinnerFraud.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
            isFraud = false
            isSpam = false
            isHarOff = false
            isOther = true
        }
    }


    private fun reportTaskSpam() {
        //  {{baseurl}}/tasks/:slug/report
        showProgressDialog()
        val stringRequest: StringRequest =
                object : StringRequest(
                        Method.POST, Constant.URL_TASKS + "/" + id + "/report",
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
                        }
                ) {
                    override fun getParams(): Map<String, String> {
                        val map1: MutableMap<String, String> = HashMap()
                        map1["subject"] = when{
                            isSpam -> "Spam"
                            isFraud -> "Spam or Fraud"
                            isHarOff -> "Harassment or Offensive"
                            else -> "Others"

                        }
                        map1["description"] = edtDescription.editText!!.text.toString()
                        return map1
                    }

                    override fun getHeaders(): Map<String, String> {
                        val map1: MutableMap<String, String> = HashMap()
                        map1["authorization"] =
                                sessionManager.tokenType + " " + sessionManager.accessToken
                        map1["Content-Type"] = "application/x-www-form-urlencoded"
                        map1["X-Requested-With"] = "XMLHttpRequest"
                        map1["Version"] = BuildConfig.VERSION_CODE.toString()
                        return map1
                    }
                }
        stringRequest.retryPolicy = DefaultRetryPolicy(
                0, -1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )
        val requestQueue = Volley.newRequestQueue(this@ReportActivity)
        requestQueue.add(stringRequest)
    }


    private fun reportOfferSpam() {
        // {{baseurl}}/offers/:id/report
        showProgressDialog()
        val stringRequest: StringRequest =
                object : StringRequest(
                        Method.POST, Constant.URL_OFFERS + "/" + id + "/report",
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
                        }
                ) {
                    override fun getParams(): Map<String, String> {
                        val map1: MutableMap<String, String> = HashMap()
                        map1["subject"] = when{
                            isSpam -> "Spam"
                            isFraud -> "Spam or Fraud"
                            isHarOff -> "Harassment or Offensive"
                            else -> "Others"

                        }
                        map1["description"] = edtDescription.editText!!.text.toString()
                        return map1
                    }

                    override fun getHeaders(): Map<String, String> {
                        val map1: MutableMap<String, String> = HashMap()
                        map1["authorization"] =
                                sessionManager.tokenType + " " + sessionManager.accessToken
                        map1["Content-Type"] = "application/x-www-form-urlencoded"
                        map1["X-Requested-With"] = "XMLHttpRequest"
                        map1["Version"] = BuildConfig.VERSION_CODE.toString()
                        return map1
                    }
                }
        stringRequest.retryPolicy = DefaultRetryPolicy(
                0, -1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )
        val requestQueue = Volley.newRequestQueue(this@ReportActivity)
        requestQueue.add(stringRequest)
    }
}
