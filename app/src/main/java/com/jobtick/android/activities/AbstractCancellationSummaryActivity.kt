package com.jobtick.android.activities

import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.text.HtmlCompat
import com.android.volley.AuthFailureError
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.google.android.material.appbar.MaterialToolbar
import com.google.gson.Gson
import com.jobtick.android.BuildConfig
import com.jobtick.android.R
import com.jobtick.android.cancellations.AbstractCancellationReasonsActivity
import com.jobtick.android.cancellations.CancellationDeclineActivity
import com.jobtick.android.cancellations.CancellationSubmittedActivity
import com.jobtick.android.models.TaskModel
import com.jobtick.android.models.cancellation.notice.CancellationNoticeModel
import com.jobtick.android.utils.*
import com.jobtick.android.utils.ResizeWidthAnimation.OnFinish
import org.json.JSONException
import org.json.JSONObject
import timber.log.Timber
import java.util.*
import kotlin.math.min

abstract class AbstractCancellationSummaryActivity : ActivityBase(), OnTouchListener {
    private var toolbar: MaterialToolbar? = null
    private var title: TextView? = null
    private var securedPayment: TextView? = null
    private var imgAvatar: ImageView? = null
    private var taskTitle: TextView? = null
    private var posterName: TextView? = null
    private var taskFee: TextView? = null
    private var cancellationReason: TextView? = null
    private var commentBox: View? = null
    private var commentContent: TextView? = null
    private var respondHeader: TextView? = null
    private var feeContainer: View? = null
    private var feeAmount: TextView? = null
    private var learnMore: TextView? = null
    private lateinit var submit: Button
    private var animFirstWidth = 0

    @JvmField
    protected var taskModel: TaskModel? = null
    protected var str_SLUG: String? = null
    private var strReason: String? = null
    private var strComment: String? = null
    private var reasonId = 0
    protected var cancellationFeeValue = 0f
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cancellation_summary)
        taskModel = TaskDetailsActivity.taskModel
        toolbar = findViewById(R.id.toolbar)
        title = findViewById(R.id.title)
        securedPayment = findViewById(R.id.secured_payment)
        imgAvatar = findViewById(R.id.ivAvatar)
        taskTitle = findViewById(R.id.task_title)
        posterName = findViewById(R.id.poster_name)
        taskFee = findViewById(R.id.task_fee)
        cancellationReason = findViewById(R.id.cancellation_reason)
        commentBox = findViewById(R.id.comment_box)
        commentContent = findViewById(R.id.comment_content)
        respondHeader = findViewById(R.id.cancellation_respond_header)
        feeContainer = findViewById(R.id.cancellation_fee_container)
        feeAmount = findViewById(R.id.cancellation_fee_amount)
        learnMore = findViewById(R.id.learn_more)
        submit = findViewById(R.id.btn_submit)
        submit.setOnClickListener { cancellationSubmit(strReason, strComment, reasonId) }
        init()
        initToolbar()
    }

    private fun init() {
        learnMore!!.setOnClickListener { ExternalIntentHelper.openLink(this, Constant.URL_privacy_policy) }
        str_SLUG = taskModel!!.slug
        val bundle = intent.extras ?: throw IllegalStateException("there is no bundle.")
        title!!.text = Html.fromHtml(bundle.getString(ConstantKey.CANCELLATION_TITLE), HtmlCompat.FROM_HTML_MODE_LEGACY)
        if (taskModel!!.poster.avatar != null && taskModel!!.poster.avatar.thumbUrl != null) {
            Glide.with(imgAvatar!!).load(taskModel!!.poster.avatar.thumbUrl).into(imgAvatar!!)
        }
        taskTitle!!.text = taskModel!!.title
        taskFee!!.text = String.format(Locale.ENGLISH, "$%d", taskModel!!.amount)
        posterName!!.text = taskModel!!.poster.name
        strComment = bundle.getString(AbstractCancellationReasonsActivity.CANCELLATION_COMMENT, null)
        strReason = bundle.getString(AbstractCancellationReasonsActivity.CANCELLATION_REASON, null)
        reasonId = bundle.getInt(AbstractCancellationReasonsActivity.CANCELLATION_ID, 0)

        //first we check, it the values are inserted in CancellationWorker/PosterActivity
        if (strReason != null) {
            submit.visibility = View.VISIBLE
            cancellationReason!!.text = reasonRectify(strReason!!)
            if (strComment == null || strComment!!.trim { it <= ' ' }.isEmpty()) commentBox!!.visibility = View.GONE else commentContent!!.text = strComment!!.trim { it <= ' ' }
            val feeValue = bundle.getFloat(AbstractCancellationReasonsActivity.CANCELLATION_VALUE, 0f)
            if (feeValue != 0f) {
                feeAmount!!.text = String.format(Locale.ENGLISH, "-$%.1f", feeValue)
                feeContainer!!.visibility = View.VISIBLE
            }

            //here we sure the values is in cancellation model, but as usual, we have to check it
        } else if (taskModel!!.cancellation != null) {
            submit.visibility = View.GONE
            cancellationReason!!.text = reasonRectify(taskModel!!.cancellation.reason)
            if (taskModel!!.cancellation.comment == null || taskModel!!.cancellation.comment.trim { it <= ' ' }
                            .isEmpty()) commentBox!!.visibility = View.GONE else commentContent!!.text = taskModel!!.cancellation.comment.trim { it <= ' ' }
            if (taskModel!!.cancellation.reasonModel != null && taskModel!!.cancellation.reasonModel.reason != null && taskModel!!.cancellation.reasonModel.reason == userType) {
                noticeList
            }
        }
    }

    private fun initToolbar() {
        toolbar!!.setNavigationIcon(R.drawable.ic_back_black)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.title = "Cancellation Request"
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun reasonRectify(reasonIn: String): String {
        var reason = reasonIn
        if (reason.contains("{worker}")) reason = reason.replace("{worker}", taskModel!!.worker.name)
        if (reason.contains("{poster}")) reason = reason.replace("{poster}", taskModel!!.worker.name)
        reason = String.format("\"%s\"", reason)
        return reason
    }

    protected fun decline() {
        val bundle = Bundle()
        bundle.putInt(ConstantKey.KEY_TASK_CANCELLATION_ID, taskModel!!.cancellation.id)
        val intent = Intent(this, CancellationDeclineActivity::class.java)
        intent.putExtras(bundle)
        startActivityForResult(intent, ConstantKey.RESULTCODE_CANCELLATION)
    }

    // Print Error!
    protected val noticeList: Unit
        get() {
            val stringRequest: StringRequest = object : StringRequest(Method.GET, Constant.BASE_URL + "cancellation/notice",
                    Response.Listener { response: String? ->
                        Timber.e(response)
                        hideProgressDialog()
                        try {
                            val jsonObject = JSONObject(response!!)
                            Timber.e(jsonObject.toString())
                            if (jsonObject.has("success") && !jsonObject.isNull("success")) {
                                if (jsonObject.getBoolean("success")) {
                                    if (jsonObject.has("data") && !jsonObject.isNull("data")) {
                                        val data = jsonObject.getString("data")
                                        val gson = Gson()
                                        val notice = gson.fromJson(data, CancellationNoticeModel::class.java)
                                        cancellationFeeValue = calculateCancellationFee(notice)
                                        feeAmount!!.text = String.format(Locale.ENGLISH, "-$%s", cancellationFeeValue)
                                        feeContainer!!.visibility = View.VISIBLE
                                        hideProgressDialog()
                                    }
                                } else {
                                    showToast("Something went Wrong", this)
                                    hideProgressDialog()
                                }
                            }
                        } catch (e: JSONException) {
                            Timber.e(e.toString())
                            e.printStackTrace()
                            hideProgressDialog()
                        }
                    },
                    Response.ErrorListener { error: VolleyError ->
                        val networkResponse = error.networkResponse
                        if (networkResponse?.data != null) {
                            val jsonError = String(networkResponse.data)
                            // Print Error!
                            Timber.e(jsonError)
                            if (networkResponse.statusCode == HttpStatus.AUTH_FAILED) {
                                unauthorizedUser()
                                hideProgressDialog()
                                return@ErrorListener
                            }
                            try {
                                val jsonObject = JSONObject(jsonError)
                                val jsonObjectError = jsonObject.getJSONObject("error")
                                if (jsonObjectError.has("message")) {
                                    showToast(jsonObjectError.getString("message"), this)
                                }
                            } catch (e: JSONException) {
                                e.printStackTrace()
                                showToast("Something Went Wrong", this)
                            }
                        } else {
                            showToast("Something Went Wrong", this)
                        }
                        Timber.e(error.toString())
                        hideProgressDialog()
                    }) {
                @Throws(AuthFailureError::class)
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
            val requestQueue = Volley.newRequestQueue(this)
            requestQueue.add(stringRequest)
        }

    protected fun accept() {
        //no need to check null, we sure we have it
        val cancellationId = taskModel!!.cancellation.id
        showProgressDialog()
        val stringRequest: StringRequest = object : StringRequest(Method.GET, Constant.BASE_URL + "cancellation/" + cancellationId + "/accept",
                Response.Listener { response: String? ->
                    Timber.e(response)
                    try {
                        val jsonObject = JSONObject(response!!)
                        Timber.e(jsonObject.toString())
                        if (jsonObject.has("success") && !jsonObject.isNull("success")) {
                            if (jsonObject.getBoolean("success")) {
                                val bundle = Bundle()
                                bundle.putString(ConstantKey.CANCELLATION_SUBMITTED, "The job is cancelled successfully.")
                                val intent = Intent(this, CancellationSubmittedActivity::class.java)
                                intent.putExtras(bundle)
                                startActivityForResult(intent, ConstantKey.RESULTCODE_CANCELLATION)
                            } else {
                                showToast("Something went Wrong", this)
                            }
                        }
                        hideProgressDialog()
                    } catch (e: JSONException) {
                        Timber.e(e.toString())
                        e.printStackTrace()
                        hideProgressDialog()
                    }
                },
                Response.ErrorListener { error: VolleyError ->
                    hideProgressDialog()
                    val networkResponse = error.networkResponse
                    if (networkResponse?.data != null) {
                        val jsonError = String(networkResponse.data)
                        // Print Error!
                        Timber.e(jsonError)
                        if (networkResponse.statusCode == HttpStatus.AUTH_FAILED) {
                            unauthorizedUser()
                            return@ErrorListener
                        }
                        try {
                            val jsonObject = JSONObject(jsonError)
                            val jsonObjectError = jsonObject.getJSONObject("error")
                            if (jsonObjectError.has("message")) {
                                showToast(jsonObjectError.getString("message"), this)
                            }
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                    } else {
                        showToast("Something Went Wrong", this)
                    }
                    Timber.e(error.toString())
                }) {
            @Throws(AuthFailureError::class)
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
        val requestQueue = Volley.newRequestQueue(this)
        requestQueue.add(stringRequest)
    }

    protected fun withdraw() {
        //no need to check null, we sure we have it
        val cancellationId = taskModel!!.cancellation.id
        showProgressDialog()
        val stringRequest: StringRequest = object : StringRequest(Method.DELETE, Constant.BASE_URL + "cancellation/" + cancellationId,
                Response.Listener { response: String? ->
                    Timber.e(response)
                    try {
                        val jsonObject = JSONObject(response!!)
                        Timber.e(jsonObject.toString())
                        if (jsonObject.has("success") && !jsonObject.isNull("success")) {
                            if (jsonObject.getBoolean("success")) {
                                val intent = Intent()
                                val bundle = Bundle()
                                bundle.putBoolean(ConstantKey.CANCELLATION, true)
                                intent.putExtras(bundle)
                                setResult(RESULT_OK, intent)
                                finish()
                            } else {
                                showToast("Something went Wrong", this)
                            }
                        }
                        hideProgressDialog()
                    } catch (e: JSONException) {
                        Timber.e(e.toString())
                        e.printStackTrace()
                        hideProgressDialog()
                    }
                },
                Response.ErrorListener { error: VolleyError ->
                    hideProgressDialog()
                    val networkResponse = error.networkResponse
                    if (networkResponse?.data != null) {
                        val jsonError = String(networkResponse.data)
                        // Print Error!
                        Timber.e(jsonError)
                        if (networkResponse.statusCode == HttpStatus.AUTH_FAILED) {
                            unauthorizedUser()
                            return@ErrorListener
                        }
                        try {
                            val jsonObject = JSONObject(jsonError)
                            val jsonObjectError = jsonObject.getJSONObject("error")
                            if (jsonObjectError.has("message")) {
                                showToast(jsonObjectError.getString("message"), this)
                            }
                        } catch (e: JSONException) {
                            e.printStackTrace()
                            showToast("Something Went Wrong", this)
                        }
                    } else {
                        showToast("Something Went Wrong", this)
                    }
                    Timber.e(error.toString())
                }) {
            @Throws(AuthFailureError::class)
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
        val requestQueue = Volley.newRequestQueue(this)
        requestQueue.add(stringRequest)
    }

    protected fun cancellationSubmit(str_reason: String?, str_comment: String?, reasonId: Int) {
        showProgressDialog()
        val stringRequest: StringRequest = object : StringRequest(Method.POST, Constant.URL_TASKS + "/" + str_SLUG + "/cancellation",
                Response.Listener { response: String? ->
                    Timber.e(response)
                    hideProgressDialog()
                    try {
                        val jsonObject = JSONObject(response!!)
                        Timber.e(jsonObject.toString())
                        if (jsonObject.has("success") && !jsonObject.isNull("success")) {
                            if (jsonObject.getBoolean("success")) {
                                if (userType == UserType.POSTER) {
                                    FireBaseEvent.getInstance(applicationContext)
                                            .sendEvent(FireBaseEvent.Event.CANCELLATION,
                                                    FireBaseEvent.EventType.API_RESPOND_SUCCESS,
                                                    FireBaseEvent.EventValue.CANCELLATION_POSTER_SUBMIT)
                                } else if (userType == UserType.WORKER) {
                                    FireBaseEvent.getInstance(applicationContext)
                                            .sendEvent(FireBaseEvent.Event.CANCELLATION,
                                                    FireBaseEvent.EventType.API_RESPOND_SUCCESS,
                                                    FireBaseEvent.EventValue.CANCELLATION_WORKER_SUBMIT)
                                }
                                val bundle = Bundle()
                                bundle.putString(ConstantKey.CANCELLATION_SUBMITTED, "Cancellation submitted successfully.")
                                val intent = Intent(this, CancellationSubmittedActivity::class.java)
                                intent.putExtras(bundle)
                                startActivityForResult(intent, ConstantKey.RESULTCODE_CANCELLATION)
                            } else {
                                showToast("Something went Wrong", this)
                            }
                        }
                    } catch (e: JSONException) {
                        Timber.e(e.toString())
                        e.printStackTrace()
                    }
                },
                Response.ErrorListener { error: VolleyError ->
                    val networkResponse = error.networkResponse
                    if (networkResponse?.data != null) {
                        val jsonError = String(networkResponse.data)
                        // Print Error!
                        Timber.e(jsonError)
                        if (networkResponse.statusCode == HttpStatus.AUTH_FAILED) {
                            unauthorizedUser()
                            hideProgressDialog()
                            return@ErrorListener
                        }
                        try {
                            val jsonObject = JSONObject(jsonError)
                            val jsonObjectError = jsonObject.getJSONObject("error")
                            if (jsonObjectError.has("message")) {
                                showToast(jsonObjectError.getString("message"), this)
                            }
                        } catch (e: JSONException) {
                            e.printStackTrace()
                            showToast("Something Went Wrong", this)
                        }
                    } else {
                        showToast("Something Went Wrong", this)
                    }
                    Timber.e(error.toString())
                    hideProgressDialog()
                }) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val map1: MutableMap<String, String> = HashMap()
                map1["authorization"] = sessionManager.tokenType + " " + sessionManager.accessToken
                map1["Content-Type"] = "application/x-www-form-urlencoded"
                map1["X-Requested-With"] = "XMLHttpRequest"
                map1["Version"] = BuildConfig.VERSION_CODE.toString()
                return map1
            }

            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String> {
                val map1: MutableMap<String, String> = HashMap()
                map1["reason"] = str_reason!!
                map1["reason_id"] = reasonId.toString()
                if (str_comment != null) {
                    map1["comment"] = str_comment
                }
                Timber.e(map1.size.toString())
                Timber.e(map1.toString())
                return map1
            }
        }
        stringRequest.retryPolicy = DefaultRetryPolicy(0, -1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
        val requestQueue = Volley.newRequestQueue(this)
        requestQueue.add(stringRequest)
    }

    private fun calculateCancellationFee(notice: CancellationNoticeModel): Float {
        val fee = notice.feePercentage.toInt() / 100.00f * taskModel!!.amount
        var maxFee = notice.maxFeeAmount.toInt().toFloat()
        maxFee = min(maxFee, fee)
        return maxFee
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            if (requestCode == ConstantKey.RESULTCODE_CANCELLATION) {
                setResult(RESULT_OK, data)
                finish()
            }
        }
    }

    private var defaultSize = true
    override fun onTouch(v: View, event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                if (defaultSize) {
                    animFirstWidth = v.width
                    defaultSize = false
                }
                val animGo = ResizeWidthAnimation(v, (animFirstWidth * 1.5).toInt())
                animGo.onFinish = OnFinish { withdraw() }
                animGo.duration = 3000
                v.startAnimation(animGo)
            }
            MotionEvent.ACTION_UP -> {
                val animBack = ResizeWidthAnimation(v, animFirstWidth)
                animBack.duration = 1000
                v.startAnimation(animBack)
            }
        }
        return true
    }

    protected abstract val userType: String

    protected interface UserType {
        companion object {
            const val POSTER = "poster"
            const val WORKER = "worker"
        }
    }
}