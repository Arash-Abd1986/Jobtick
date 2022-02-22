package com.jobtick.android.activities

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.CompoundButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.RadioButton
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatRatingBar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.appbar.MaterialToolbar
import com.google.gson.Gson
import com.jobtick.android.BuildConfig
import com.jobtick.android.R
import com.jobtick.android.adapers.ReviewAdapter
import com.jobtick.android.models.ReviewModel
import com.jobtick.android.models.UserAccountModel
import com.jobtick.android.pagination.PaginationListener
import com.jobtick.android.utils.Constant
import com.jobtick.android.utils.CustomToast
import com.jobtick.android.utils.HttpStatus
import com.jobtick.android.utils.ImageUtil
import com.jobtick.android.utils.SessionManager
import com.mikhaellopez.circularimageview.CircularImageView
import org.json.JSONException
import org.json.JSONObject
import timber.log.Timber
import java.util.Locale

class ReviewsActivity : ActivityBase() {
    private val currentPage = PaginationListener.PAGE_START
    private var totalPage = 10
    private lateinit var reviewAdapter: ReviewAdapter
    private lateinit var reviewModelList: MutableList<ReviewModel.DataBean>
    private lateinit var sessionManagerR: SessionManager
    private var whoIs: String? = null

    private lateinit var txtName: TextView
    private lateinit var txtReviewCounts: TextView
    private lateinit var recyclerReview: RecyclerView
    private lateinit var txtRatingValue: TextView
    private lateinit var ratingBar: AppCompatRatingBar
    private lateinit var progressBar5Star: ProgressBar
    private lateinit var ivMedal: ImageView
    private lateinit var progressBar4Star: ProgressBar
    private lateinit var progressBar3Star: ProgressBar
    private lateinit var progressBar2Star: ProgressBar
    private lateinit var progressBar1Star: ProgressBar
    private lateinit var txtReviewCount5Star: TextView
    private lateinit var txtReviewCount4Star: TextView
    private lateinit var txtReviewCount3Star: TextView
    private lateinit var txtReviewCount2Star: TextView
    private lateinit var txtReviewCount1Star: TextView
    private lateinit var imgVerifiedAccount: ImageView
    private lateinit var toolbar: MaterialToolbar
    private lateinit var imgAvatar: CircularImageView
    private var userId = 0
    private lateinit var poster: RadioButton
    private lateinit var ticker: RadioButton
    private lateinit var noReview: LinearLayout
    private lateinit var lytTicker: LinearLayout
    private lateinit var lytPoster: LinearLayout
    private lateinit var txtReview: TextView
    private lateinit var txtSub: TextView
    private lateinit var txtRatingValueP: TextView
    private lateinit var txtReviewCountsP: TextView
    private lateinit var txtReviewCount1Starp: TextView
    private lateinit var txtReviewCount2Starp: TextView
    private lateinit var txtReviewCount3Starp: TextView
    private lateinit var txtReviewCount4Starp: TextView
    private lateinit var txtReviewCount5Starp: TextView
    private lateinit var progressBar1Starp: ProgressBar
    private lateinit var progressBar2Starp: ProgressBar
    private lateinit var progressBar3Starp: ProgressBar
    private lateinit var progressBar4Starp: ProgressBar
    private lateinit var progressBar5Starp: ProgressBar
    private lateinit var ratingbar: RatingBar
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reviews)
        setIDs()
        val bundle = intent.extras
        sessionManagerR = SessionManager(this@ReviewsActivity)
        if (bundle != null) {
            if (bundle.containsKey(Constant.userID)) {
                userId = bundle.getInt(Constant.userID)
            }
            whoIs = "poster"
        }
        if (userAccountModel == null) {
            userAccountModel = sessionManager.userAccount
        }
        initProgressDialog()
        init()
        reviewList
    }

    private fun setIDs() {
        txtName = findViewById(R.id.txt_full_name)
        txtReviewCounts = findViewById(R.id.txt_reviews_counts)
        recyclerReview = findViewById(R.id.recyclerview_review)
        txtRatingValue = findViewById(R.id.txt_rating_values)
        ratingBar = findViewById(R.id.ratingbar)
        progressBar5Star = findViewById(R.id.progress_bar_5_star)
        ivMedal = findViewById(R.id.ivMedal)
        progressBar4Star = findViewById(R.id.progress_bar_4_star)
        progressBar3Star = findViewById(R.id.progress_bar_3_star)
        progressBar2Star = findViewById(R.id.progress_bar_2_star)
        progressBar1Star = findViewById(R.id.progress_bar_1_star)
        txtReviewCount5Star = findViewById(R.id.txt_review_count_5_star)
        txtReviewCount4Star = findViewById(R.id.txt_review_count_4_star)
        txtReviewCount3Star = findViewById(R.id.txt_review_count_3_star)
        txtReviewCount2Star = findViewById(R.id.txt_review_count_2_star)
        txtReviewCount1Star = findViewById(R.id.txt_review_count_1_star)
        imgVerifiedAccount = findViewById(R.id.img_verified)
        toolbar = findViewById(R.id.toolbar)
        imgAvatar = findViewById(R.id.img_avatar)
        noReview = findViewById(R.id.lyt_no_review)
        txtReview = findViewById(R.id.txt_no_review)
        txtSub = findViewById(R.id.txt_suburb)
        lytTicker = findViewById(R.id.ticker)
        lytPoster = findViewById(R.id.Poster)
        ratingbar = findViewById(R.id.ratingbarP)
        txtRatingValueP = findViewById(R.id.txt_rating_valuesP)
        txtReviewCountsP = findViewById(R.id.txt_reviews_countsP)
        progressBar1Starp = findViewById(R.id.progress_bar_1_starP)
        txtReviewCount1Starp = findViewById(R.id.txt_review_count_1_starP)
        progressBar2Starp = findViewById(R.id.progress_bar_2_starP)
        txtReviewCount2Starp = findViewById(R.id.txt_review_count_2_starP)
        progressBar3Starp = findViewById(R.id.progress_bar_3_starP)
        txtReviewCount3Starp = findViewById(R.id.txt_review_count_3_starP)
        progressBar4Starp = findViewById(R.id.progress_bar_4_starP)
        txtReviewCount4Starp = findViewById(R.id.txt_review_count_4_starP)
        progressBar5Starp = findViewById(R.id.progress_bar_5_starP)
        txtReviewCount5Starp = findViewById(R.id.txt_review_count_5_starP)
        poster = findViewById(R.id.rbPoster)
        ticker = findViewById(R.id.rbTicker)
    }

    fun init() {
        toolbar.setNavigationOnClickListener { view: View? -> onBackPressed() }
        val layoutManager = LinearLayoutManager(this@ReviewsActivity)
        recyclerReview.layoutManager = layoutManager
        reviewAdapter = ReviewAdapter(this@ReviewsActivity, ArrayList())
        recyclerReview.adapter = reviewAdapter
        reviewModelList = ArrayList()
        setData()
        initComponent()
    }

    private fun initComponent() {
        poster.setOnCheckedChangeListener { _: CompoundButton?, _: Boolean -> onChangeTabBiography() }
        ticker.setOnCheckedChangeListener { _: CompoundButton?, _: Boolean -> onChangeTabBiography() }
    }

    private fun onChangeTabBiography() {
        if (poster.isChecked) {
            lytTicker.visibility = View.GONE
            lytPoster.visibility = View.VISIBLE
            poster.setTextColor(resources.getColor(R.color.N100))
            ticker.setTextColor(resources.getColor(R.color.textColor))
            whoIs = "poster"
        } else {
            poster.setTextColor(resources.getColor(R.color.textColor))
            ticker.setTextColor(resources.getColor(R.color.N100))
            whoIs = "worker"
        }
        if (ticker.isChecked) {
            ticker.setTextColor(resources.getColor(R.color.N100))
            poster.setTextColor(resources.getColor(R.color.textColor))
            lytTicker.visibility = View.VISIBLE
            lytPoster.visibility = View.GONE
            whoIs = "worker"
        } else {
            ticker.setTextColor(resources.getColor(R.color.N100))
            poster.setTextColor(resources.getColor(R.color.N600))
            whoIs = "poster"
        }
        reviewList
    }

    @SuppressLint("SetTextI18n")
    fun setData() {
        if (userAccountModel!!.isVerifiedAccount == 1) {
            imgVerifiedAccount.visibility = View.VISIBLE
        } else {
            imgVerifiedAccount.visibility = View.GONE
        }
        when (userAccountModel!!.posterTier.id) {
            1 -> ivMedal.setImageResource(R.drawable.ic_level1_active)
            2 -> ivMedal.setImageResource(R.drawable.ic_level2_active)
            3 -> ivMedal.setImageResource(R.drawable.ic_level3_active)
            4 -> ivMedal.setImageResource(R.drawable.ic_level4_active)
        }
        if (userAccountModel!!.avatar != null) {
            ImageUtil.displayImage(imgAvatar, userAccountModel!!.avatar.thumbUrl, null)
        }
        txtName.text = userAccountModel!!.name
        txtSub.text = userAccountModel!!.location

        //worker
        if (userAccountModel!!.workerRatings != null) {
            ratingBar.rating = userAccountModel!!.workerRatings.avgRating
            txtRatingValue.text = String.format(Locale.US, "%.1f", userAccountModel!!.workerRatings.avgRating)
            txtReviewCounts.text = userAccountModel!!.workerRatings.receivedReviews.toString() + " Review"
            if (userAccountModel!!.workerRatings != null && userAccountModel!!.workerRatings.breakdownModel.get1() != null) {
                progressBar1Star.progress = userAccountModel!!.workerRatings.breakdownModel.get1()
                txtReviewCount1Star.text = "(" + userAccountModel!!.workerRatings.breakdownModel.get1().toString() + ")"
            } else {
                progressBar1Star.progress = 0
                txtReviewCount1Star.text = "(0)"
            }
            if (userAccountModel!!.workerRatings != null && userAccountModel!!.workerRatings.breakdownModel.get2() != null) {
                progressBar2Star.progress = userAccountModel!!.workerRatings.breakdownModel.get2()
                txtReviewCount2Star.text = "(" + userAccountModel!!.workerRatings.breakdownModel.get2().toString() + ")"
            } else {
                progressBar2Star.progress = 0
                txtReviewCount2Star.text = "(0)"
            }
            if (userAccountModel!!.workerRatings != null && userAccountModel!!.workerRatings.breakdownModel.get3() != null) {
                progressBar3Star.progress = userAccountModel!!.workerRatings.breakdownModel.get3()
                txtReviewCount3Star.text = "(" + userAccountModel!!.workerRatings.breakdownModel.get3().toString() + ")"
            } else {
                progressBar3Star.progress = 0
                txtReviewCount3Star.text = "(0)"
            }
            if (userAccountModel!!.workerRatings != null && userAccountModel!!.workerRatings.breakdownModel.get4() != null) {
                progressBar4Star.progress = userAccountModel!!.workerRatings.breakdownModel.get4()
                txtReviewCount4Star.text = "(" + userAccountModel!!.workerRatings.breakdownModel.get4().toString() + ")"
            } else {
                progressBar4Star.progress = 0
                txtReviewCount4Star.text = "(0)"
            }
            if (userAccountModel!!.workerRatings != null && userAccountModel!!.workerRatings.breakdownModel.get5() != null) {
                progressBar5Star.progress = userAccountModel!!.workerRatings.breakdownModel.get5()
                txtReviewCount5Star.text = "(" + userAccountModel!!.workerRatings.breakdownModel.get5().toString() + ")"
            } else {
                progressBar5Star.progress = 0
                txtReviewCount5Star.text = "(0)"
            }
        }

        //poster
        if (userAccountModel!!.posterRatings != null) {
            ratingbar.rating = userAccountModel!!.posterRatings.avgRating
            txtRatingValueP.text = String.format(Locale.US, "%.1f", userAccountModel!!.posterRatings.avgRating)
            txtReviewCountsP.text = userAccountModel!!.posterRatings.receivedReviews.toString() + " Review"
            if (userAccountModel!!.posterRatings != null && userAccountModel!!.posterRatings.breakdownModel.get1() != null) {
                progressBar1Starp.progress = userAccountModel!!.posterRatings.breakdownModel.get1()
                txtReviewCount1Starp.text = "(" + userAccountModel!!.posterRatings.breakdownModel.get1().toString() + ")"
            } else {
                progressBar1Starp.progress = 0
                txtReviewCount1Starp.text = "(0)"
            }
            if (userAccountModel!!.posterRatings != null && userAccountModel!!.posterRatings.breakdownModel.get2() != null) {
                progressBar2Starp.progress = userAccountModel!!.posterRatings.breakdownModel.get2()
                txtReviewCount2Starp.text = "(" + userAccountModel!!.posterRatings.breakdownModel.get2().toString() + ")"
            } else {
                progressBar2Starp.progress = 0
                txtReviewCount2Starp.text = "(0)"
            }
            if (userAccountModel!!.posterRatings != null && userAccountModel!!.posterRatings.breakdownModel.get3() != null) {
                progressBar3Starp.progress = userAccountModel!!.posterRatings.breakdownModel.get3()
                txtReviewCount3Starp.text = "(" + userAccountModel!!.posterRatings.breakdownModel.get3().toString() + ")"
            } else {
                progressBar3Starp.progress = 0
                txtReviewCount3Starp.text = "(0)"
            }
            if (userAccountModel!!.posterRatings != null && userAccountModel!!.posterRatings.breakdownModel.get4() != null) {
                progressBar4Starp.progress = userAccountModel!!.posterRatings.breakdownModel.get4()
                txtReviewCount4Starp.text = "(" + userAccountModel!!.posterRatings.breakdownModel.get4().toString() + ")"
            } else {
                progressBar4Starp.progress = 0
                txtReviewCount4Starp.text = "(0)"
            }
            if (userAccountModel!!.posterRatings != null && userAccountModel!!.posterRatings.breakdownModel != null && userAccountModel!!.posterRatings.breakdownModel.get5() != null) {
                progressBar5Starp.progress = userAccountModel!!.posterRatings.breakdownModel.get5()
                txtReviewCount5Starp.text = "(" + userAccountModel!!.posterRatings.breakdownModel.get5().toString() + ")"
            } else {
                progressBar5Starp.progress = 0
                txtReviewCount5Starp.text = "(0)"
            }
        }
    }

    val reviewList: Unit
        get() {
            showProgressDialog()
            //profile/:user_id/reviews/:ratee_type
            val stringRequest: StringRequest = object : StringRequest(Method.GET, Constant.URL_PROFILE + "/" + userId + "/reviews/" + whoIs,
                    Response.Listener { response: String? ->
                        // categoryArrayList.clear();
                        try {
                            reviewModelList.clear()
                            val jsonObject = JSONObject(response!!)
                            if (jsonObject.has("data") && !jsonObject.isNull("data")) {
                                val jsonString = jsonObject.toString() //http request
                                var data = ReviewModel()
                                val gson = Gson()
                                data = gson.fromJson(jsonString, ReviewModel::class.java)
                                reviewModelList.addAll(data.data)
                            } else {
                                showToast("something went to wrong", this@ReviewsActivity)
                                return@Listener
                            }
                            if (jsonObject.has("meta") && !jsonObject.isNull("meta")) {
                                val jsonObjectMeta = jsonObject.getJSONObject("meta")
                                totalPage = jsonObjectMeta.getInt("last_page")
                                Constant.PAGE_SIZE = jsonObjectMeta.getInt("per_page")
                            }
                            if (reviewModelList.size <= 0) {
                                noReview.visibility = View.VISIBLE
                                txtReview.visibility = View.VISIBLE
                                recyclerReview.visibility = View.GONE
                            } else {
                                noReview.visibility = View.GONE
                                txtReview.visibility = View.GONE
                                recyclerReview.visibility = View.VISIBLE
                            }
                            reviewAdapter.clear()
                            reviewAdapter.addItems(reviewModelList)
                            if (currentPage < totalPage) {
                                reviewAdapter.addLoading()
                            }
                            hideProgressDialog()
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
                                showToast(jsonObjectError.getString("message"), this)
                            } catch (e: JSONException) {
                                e.printStackTrace()
                            }
                        } else {
                            showToast("Something Went Wrong", this@ReviewsActivity)
                        }
                        Timber.e(error.toString())
                        hideProgressDialog()
                    }) {
                override fun getHeaders(): Map<String, String> {
                    val map1: MutableMap<String, String> = HashMap()
                    map1["Content-Type"] = "application/x-www-form-urlencoded"
                    map1["Authorization"] = "Bearer " + sessionManagerR.accessToken
                    map1["Version"] = BuildConfig.VERSION_CODE.toString()
                    return map1
                }
            }
            stringRequest.retryPolicy = DefaultRetryPolicy(0, -1,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
            val requestQueue = Volley.newRequestQueue(this@ReviewsActivity)
            requestQueue.add(stringRequest)
            Timber.e(stringRequest.url)
        }

    override fun showToast(content: String, context: Context) {
        val toast = CustomToast(context)
        toast.setCustomView(content)
        toast.duration = Toast.LENGTH_SHORT
        toast.show()
    }

    companion object {
        var loadAnotherUser = false
        var userAccountModel: UserAccountModel? = null
    }
}