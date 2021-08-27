package com.jobtick.android.activities

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.RatingBar.OnRatingBarChangeListener
import android.widget.TextView
import androidx.appcompat.widget.AppCompatRatingBar
import com.android.volley.*
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.appbar.MaterialToolbar
import com.jobtick.android.BuildConfig
import com.jobtick.android.R
import com.jobtick.android.models.TaskModel
import com.jobtick.android.utils.*
import com.jobtick.android.widget.ExtendedCommentText
import com.mikhaellopez.circularimageview.CircularImageView
import org.json.JSONException
import org.json.JSONObject
import timber.log.Timber
import java.util.*

class LeaveReviewActivity : ActivityBase() {
    private lateinit var toolbar: MaterialToolbar
    private lateinit var imgAvatar: CircularImageView
    private lateinit var imgVerifiedAccount: ImageView
    private lateinit var accountRating: TextView
    private lateinit var txtFullName: TextView
    private lateinit var jobSuccessPercentage: TextView
    private lateinit var jobTitle: TextView
    private lateinit var txtCalenderDate: TextView
    private lateinit var txtLocation: TextView
    private lateinit var txtAmount: TextView
    private lateinit var ratingbar: AppCompatRatingBar
    private lateinit var txtProgressRatingbar: TextView
    private lateinit var edtWriteReview: ExtendedCommentText
    private lateinit var submit: Button
    private var sessionManagerL: SessionManager? = null
    private var taskModel: TaskModel? = null
    private var isMyTask = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_leave_review)
        setIDs()
        onViewClick()
        sessionManagerL = SessionManager(this@LeaveReviewActivity)
        val bundle = intent.extras
        if (bundle != null) {
            isMyTask = bundle.getBoolean(ConstantKey.IS_MY_TASK)
            taskModel = TaskDetailsActivity.taskModel
        }
        initToolbar()
        setData()
    }

    private fun setIDs() {
        toolbar = findViewById(R.id.toolbar)
        imgAvatar = findViewById(R.id.img_avatar)
        imgVerifiedAccount = findViewById(R.id.img_verified_account)
        accountRating = findViewById(R.id.account_rating)
        txtFullName = findViewById(R.id.txt_full_name)
        jobSuccessPercentage = findViewById(R.id.job_success_percentage)
        jobTitle = findViewById(R.id.job_title)
        txtCalenderDate = findViewById(R.id.txt_calender_date)
        txtLocation = findViewById(R.id.txt_location)
        txtAmount = findViewById(R.id.txt_amount)
        ratingbar = findViewById(R.id.ratingbar)
        txtProgressRatingbar = findViewById(R.id.txt_progress_ratingbar)
        edtWriteReview = findViewById(R.id.edt_write_review)
        submit = findViewById(R.id.submit)
    }

    private fun initToolbar() {
        toolbar.setNavigationIcon(R.drawable.ic_back)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.title = "Leave a review"
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setData() {
        jobTitle.text = taskModel!!.title
        if (taskModel!!.location != null) {
            txtLocation.text = taskModel!!.location
        } else {
            txtLocation.setText(R.string.remote_task)
        }
        txtAmount.text = String.format(Locale.ENGLISH, "$%d", taskModel!!.amount)
        txtCalenderDate.text = Tools.getDayMonthDateTimeFormat(taskModel!!.dueDate)
        when {
            !isMyTask -> {
                //worker write review
                if (taskModel!!.poster.avatar != null && taskModel!!.poster.avatar.thumbUrl != null) {
                    ImageUtil.displayImage(imgAvatar, taskModel!!.poster.avatar.thumbUrl, null)
                }
                if (taskModel!!.poster.isVerifiedAccount == 1) {
                    imgVerifiedAccount.visibility = View.VISIBLE
                } else {
                    imgVerifiedAccount.visibility = View.GONE
                }
                txtFullName.text = taskModel!!.poster.name
                var jobSuccess = 0
                if (taskModel!!.poster.postTaskStatistics != null &&
                        taskModel!!.poster.postTaskStatistics.completionRate != null) jobSuccess = taskModel!!.poster.postTaskStatistics.completionRate
                jobSuccessPercentage.text = String.format(Locale.ENGLISH, "%d%%", jobSuccess)
                var avgRating = 0f
                var reviews = 0
                if (taskModel!!.poster.posterRatings != null && taskModel!!.poster.posterRatings.avgRating != null) avgRating = taskModel!!.poster.posterRatings.avgRating
                if (taskModel!!.poster.posterRatings != null && taskModel!!.poster.posterRatings.receivedReviews != null) reviews = taskModel!!.poster.posterRatings.receivedReviews
                accountRating.text = String.format(Locale.ENGLISH, "  %.1f (%d)",
                        avgRating, reviews)
            }
            else -> {
                //poster write review
                if (taskModel!!.worker.avatar != null && taskModel!!.worker.avatar.thumbUrl != null) {
                    ImageUtil.displayImage(imgAvatar, taskModel!!.worker.avatar.thumbUrl, null)
                }
                if (taskModel!!.worker.isVerifiedAccount == 1) {
                    imgVerifiedAccount.visibility = View.VISIBLE
                } else {
                    imgVerifiedAccount.visibility = View.GONE
                }
                txtFullName.text = taskModel!!.worker.name
                var jobSuccess = 0
                if (taskModel!!.worker.workTaskStatistics != null &&
                        taskModel!!.worker.workTaskStatistics.completionRate != null) jobSuccess = taskModel!!.worker.workTaskStatistics.completionRate
                jobSuccessPercentage.text = String.format(Locale.ENGLISH, "%d%%", jobSuccess)
                var avgRating = 0f
                var reviews = 0
                if (taskModel!!.worker.workerRatings != null && taskModel!!.worker.workerRatings.avgRating != null) avgRating = taskModel!!.worker.workerRatings.avgRating
                if (taskModel!!.worker.workerRatings != null && taskModel!!.worker.workerRatings.receivedReviews != null) reviews = taskModel!!.worker.workerRatings.receivedReviews
                accountRating.text = String.format(Locale.ENGLISH, "  %.1f (%d)",
                        avgRating, reviews)
            }
        }
        ratingbar.onRatingBarChangeListener = OnRatingBarChangeListener { _, v, _ ->
            if (v > 0.5 && v < 1.5) txtProgressRatingbar.setText(R.string.awful)
            if (v > 1.5 && v < 2.5) txtProgressRatingbar.setText(R.string.bad)
            if (v > 2.5 && v < 3.5) txtProgressRatingbar.setText(R.string.ok)
            if (v > 3.5 && v < 4.5) txtProgressRatingbar.setText(R.string.good)
            if (v > 4.5) txtProgressRatingbar.setText(R.string.excellent)
        }
    }


    private fun onViewClick() {
        submit.setOnClickListener {
            if (!validation()) return@setOnClickListener
            if (isMyTask) {
                submitReview(ratingbar.rating.toString(), edtWriteReview.text.trim { it <= ' ' }, Constant.URL_TASKS + "/" + taskModel!!.slug + "/rating/submit-review")
            } else {
                submitReview(ratingbar.rating.toString(), edtWriteReview.text.trim { it <= ' ' }, Constant.URL_TASKS + "/" + taskModel!!.slug + "/rating/submit-review")
            }
        }
    }

    private fun submitReview(rating: String, written_review: String, url: String) {
        showProgressDialog()
        val stringRequest: StringRequest = object : StringRequest(Method.POST, url,
                Response.Listener { response: String? ->
                    Timber.e(response)
                    hideProgressDialog()
                    try {
                        val jsonObject = JSONObject(response!!)
                        Timber.e(jsonObject.toString())
                        if (jsonObject.has("success") && !jsonObject.isNull("success")) {
                            if (jsonObject.getBoolean("success")) {
                                val intent = Intent()
                                val bundle = Bundle()
                                bundle.putBoolean(ConstantKey.WRITE_REVIEW, true)
                                intent.putExtras(bundle)
                                setResult(ConstantKey.RESULTCODE_WRITE_REVIEW, intent)
                                finish()
                            } else {
                                showToast("Something went Wrong", this@LeaveReviewActivity)
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
                            hideProgressDialog()
                            val jsonObject = JSONObject(jsonError)
                            val jsonObjectError = jsonObject.getJSONObject("error")
                            if (jsonObjectError.has("message")) {
                                showToast(jsonObjectError.getString("message"), this)
                            }
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                    } else {
                        showToast("Something Went Wrong", this@LeaveReviewActivity)
                    }
                    Timber.e(error.toString())
                    hideProgressDialog()
                }) {
            override fun getHeaders(): Map<String, String> {
                val map1: MutableMap<String, String> = HashMap()
                map1["authorization"] = sessionManagerL!!.tokenType + " " + sessionManagerL!!.accessToken
                map1["Content-Type"] = "application/x-www-form-urlencoded"
                map1["X-Requested-With"] = "XMLHttpRequest"
                map1["Version"] = BuildConfig.VERSION_CODE.toString()
                return map1
            }

            override fun getParams(): Map<String, String> {
                val map1: MutableMap<String, String> = HashMap()
                map1["rating"] = rating
                map1["message"] = written_review
                return map1
            }
        }
        stringRequest.retryPolicy = DefaultRetryPolicy(0, -1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
        val requestQueue = Volley.newRequestQueue(this@LeaveReviewActivity)
        requestQueue.add(stringRequest)
        Timber.e(stringRequest.url)
    }

    private fun validation(): Boolean {
        if (ratingbar.rating < 0.5) {
            showToast("Please rate!", this)
            return false
        }
        if (TextUtils.isEmpty(edtWriteReview.text.trim { it <= ' ' })) {
            edtWriteReview.setError("")
            return false
        }
        if (edtWriteReview.text.length < edtWriteReview.geteMinSize()) {
            edtWriteReview.setError("")
            return false
        }
        return true
    }

    companion object {
        private val TAG = LeaveReviewActivity::class.java.name
    }
}