package com.jobtick.android.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.text.Html
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.Space
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.jobtick.android.BuildConfig
import com.jobtick.android.R
import com.jobtick.android.activities.ActivityBase
import com.jobtick.android.activities.DashboardActivity
import com.jobtick.android.activities.NotificationActivity
import com.jobtick.android.activities.PaymentHistoryActivity
import com.jobtick.android.activities.TaskCreateActivity
import com.jobtick.android.activities.TaskDetailsActivity
import com.jobtick.android.adapers.OfferedJobsAdapter
import com.jobtick.android.adapers.PostedJobsAdapter
import com.jobtick.android.adapers.RecommendedJobsAdapter
import com.jobtick.android.adapers.TaskCategoryAdapter
import com.jobtick.android.models.TaskCategory
import com.jobtick.android.models.response.home.Banner
import com.jobtick.android.models.response.home.OfferedJob
import com.jobtick.android.models.response.home.Payment
import com.jobtick.android.models.response.home.PostedJob
import com.jobtick.android.utils.AutoResizeTextView
import com.jobtick.android.utils.Constant
import com.jobtick.android.utils.ConstantKey
import com.jobtick.android.utils.Navigator
import com.jobtick.android.utils.SessionManager
import com.jobtick.android.utils.round
import com.jobtick.android.viewmodel.home.HomeFragmentViewModel
import org.json.JSONException
import org.json.JSONObject
import timber.log.Timber

class HomeFragment :
    Fragment(),
    PostedJobsAdapter.OnItemClickListener,
    OfferedJobsAdapter.OnItemClickListener,
    RecommendedJobsAdapter.OnItemClickListener,
    TaskCategoryAdapter.OnItemClickListener {
    private var name: TextView? = null
    private var bannerTXT: TextView? = null
    private var txtIncomeAmount: AutoResizeTextView? = null
    private var txtOutcomeAmount: AutoResizeTextView? = null
    private var txtPaymentsAction: TextView? = null
    private var txtLastTransaction: TextView? = null
    private var txtLastTransactionAmount: AutoResizeTextView? = null
    private var txtLastTransactionDate: TextView? = null
    private var txtTargetName: TextView? = null
    private var txtAction: TextView? = null
    private var imgStartBanner: ImageView? = null
    private var imgEndBanner: ImageView? = null
    private var imgExplore: ImageView? = null
    private var rlBanner: View? = null
    private var rlPayment: View? = null
    private var rlAction: RelativeLayout? = null
    private var rlPostedJobs: RelativeLayout? = null
    private var rlTop: RelativeLayout? = null
    private var linMain: LinearLayout? = null
    private var space: Space? = null
    private var rlRecentJobs: RelativeLayout? = null
    private var llLoading: RelativeLayout? = null
    private var rlOfferedJobs: RelativeLayout? = null
    private var rlRecommendedJobs: RelativeLayout? = null
    private var updateProfile: TextView? = null
    private var seeAllPostedJobs: TextView? = null
    private var seeAllRecommendedJobs: TextView? = null
    private var seeAllOfferedJobs: TextView? = null
    private var seeAllRecentJobs: TextView? = null
    private var myJobs: TextView? = null
    private var scrollView: NestedScrollView? = null
    private var recyclerPostedJobs: RecyclerView? = null
    private var recyclerRecentJobs: RecyclerView? = null
    private var recyclerOfferedJobs: RecyclerView? = null
    private var recyclerRecommendedJobs: RecyclerView? = null
    private var linAction: LinearLayout? = null

    @SuppressLint("NonConstantResourceId")
    private var sessionManager: SessionManager? = null
    private var root: View? = null
    private var toolbar: Toolbar? = null
    private var ivNotification: ImageView? = null
    private var navigator: Navigator? = null
    private lateinit var viewModel: HomeFragmentViewModel
    private lateinit var postedJobsAdapter: OfferedJobsAdapter
    private lateinit var recommendedJobsAdapter: RecommendedJobsAdapter
    private lateinit var offeredJobsAdapter: OfferedJobsAdapter
    private var hasPostedJob = false
    private var hasRecommendedJobs = false
    private var hasOfferedJob = false
    private var hasRecentJobs = false
    private var hasBanner = false
    private var hasPayment = false
    private var recentJobs: ArrayList<PostedJob> = ArrayList()
    private var offeredJobs: ArrayList<OfferedJob> = ArrayList()
    private var postedJobs: ArrayList<PostedJob> = ArrayList()
    private var recommendedJobs: ArrayList<PostedJob> = ArrayList()
    var recyclerViewCategories: RecyclerView? = null
    private var adapter: TaskCategoryAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.fragment_home, container, false)
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setIDs()
        onClick()
        initVars()
        initToolbar()
        initVM()
        initCategories()
    }

    private fun initCategories() {
        val layoutManager = GridLayoutManager(activity, 3)
        recyclerViewCategories!!.layoutManager = layoutManager
        recyclerViewCategories!!.setHasFixedSize(true)
        adapter = TaskCategoryAdapter(activity, java.util.ArrayList())
        recyclerViewCategories!!.adapter = adapter
        adapter!!.setOnItemClickListener(this)
        getTaskCategoryData()
    }

    private fun initVM() {
        val dashboardActivity = requireActivity() as DashboardActivity
        viewModel = ViewModelProvider(this).get(HomeFragmentViewModel::class.java)
        llLoading!!.visibility = View.VISIBLE
        viewModel.getNotifResponse().observe(viewLifecycleOwner) { jsonObject ->
            try {
                if (jsonObject.has("data") && !jsonObject.isNull("data")) {
                    val jsonObjectD = jsonObject.getJSONObject("data")
                    if (jsonObjectD.has("unread_count") && !jsonObjectD.isNull("unread_count")) {
                        val countStr = jsonObjectD.getString("unread_count")
                        if (countStr.toInt() > 0) {
                            ivNotification!!.setImageResource(R.drawable.ic_notification_unread_24_28dp)
                        } else {
                            ivNotification!!.setImageResource(R.drawable.ic_notification_bel_24_28dp)
                        }
                    }
                } else {
                    (requireActivity() as ActivityBase).showToast(
                        "something went wrong.",
                        requireContext()
                    )
                    ivNotification!!.setImageResource(R.drawable.ic_notification_bel_24_28dp)
                }
            } catch (e: JSONException) {
                Timber.e(e.toString())
                e.printStackTrace()
                ivNotification!!.setImageResource(R.drawable.ic_notification_bel_24_28dp)
            }
        }

        viewModel.getHomeResponse().observe(viewLifecycleOwner) {
            Handler().postDelayed({
                llLoading!!.visibility = View.GONE
            }, 1500)

            recentJobs = ArrayList()
            offeredJobs = ArrayList()
            postedJobs = ArrayList()
            recommendedJobs = ArrayList()
            val jsonArray = it.getJSONArray("data")
            for (i in 0 until jsonArray.length()) {
                val item = jsonArray.getJSONObject(i)
                setView(item)
            }

            if (hasRecentJobs) {
                setRecentJobs(recentJobs)
                rlRecentJobs!!.visibility = View.VISIBLE
            } else
                rlRecentJobs!!.visibility = View.GONE

/*            if (hasOfferedJob) {
                setOfferedJobs(offeredJobs)
                rlOfferedJobs!!.visibility = View.VISIBLE
            } else
                rlOfferedJobs!!.visibility = View.GONE*/

/*            if (hasPostedJob) {
                setPostedJobs(postedJobs)
                rlPostedJobs!!.visibility = View.VISIBLE
            } else
                rlPostedJobs!!.visibility = View.GONE*/
/*            if (hasRecommendedJobs) {
                setRecommendedJobs(recommendedJobs)
                rlRecommendedJobs!!.visibility = View.VISIBLE
            } else
                rlRecommendedJobs!!.visibility = View.GONE

            if (hasBanner)
                rlBanner!!.visibility = View.VISIBLE
            else
                rlBanner!!.visibility = View.GONE

            if (hasPayment)
                rlPayment!!.visibility = View.VISIBLE
            else
                rlPayment!!.visibility = View.GONE*/
            try {
                linMain!!.addView(space)
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
                Handler().postDelayed({
                    llLoading!!.visibility = View.GONE
                }, 1500)
            }

            if ((sessionManager!!.role == "worker") && hasRecommendedJobs) {
                rlRecentJobs!!.visibility = View.GONE
            } else {
                rlRecommendedJobs!!.visibility = View.GONE
            }
        }
        viewModel.getError().observe(viewLifecycleOwner) {
            dashboardActivity.showToast("Something went wrong", dashboardActivity)
            Handler().postDelayed({
                llLoading!!.visibility = View.GONE
            }, 1500)
        }
        viewModel.getError2().observe(viewLifecycleOwner) {
            ivNotification!!.setImageResource(R.drawable.ic_notification_bel_24_28dp)
        }

        viewModel.notificationList(
            sessionManager!!.accessToken,
            Volley.newRequestQueue(requireContext())
        )
        viewModel.home(sessionManager!!.accessToken, Volley.newRequestQueue(requireContext()))
    }

    fun getTaskCategoryData(): List<TaskCategory>? {
        val items: MutableList<TaskCategory> = java.util.ArrayList()
        val stringRequest: StringRequest = object : StringRequest(
            Method.GET, Constant.TASK_CATEGORY_V2 + "?query=" + "",
            Response.Listener { response: String? ->
                try {
                    val jsonObject = JSONObject(response)
                    if (jsonObject.has("data") && !jsonObject.isNull("data")) {
                        val jsonArray_data = jsonObject.getJSONArray("data")
                        if (jsonArray_data.length() > 0) {
                            var i = 0
                            while (jsonArray_data.length() > i) {
                                val jsonObject_taskModel_list =
                                    jsonArray_data.getJSONObject(i)
                                val taskModel = TaskCategory().getJsonToModel(
                                    jsonObject_taskModel_list,
                                    activity
                                )
                                items.add(taskModel)
                                i++
                            }
                        }
                    } else {
                        return@Listener
                    }
                    if (jsonObject.has("meta") && !jsonObject.isNull("meta")) {
                        val jsonObject_meta = jsonObject.getJSONObject("meta")
                        Constant.PAGE_SIZE = jsonObject_meta.getInt("per_page")
                    }
                    if (items.size <= 0) {
                        recyclerViewCategories!!.visibility = View.GONE
                    } else {
                        recyclerViewCategories!!.visibility = View.VISIBLE
                        try {
                            linMain!!.addView(recyclerViewCategories)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                    adapter!!.addItems(items)
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            },
            Response.ErrorListener { error: VolleyError? ->
            } /*errorHandle1(error.networkResponse)*/
        ) {
            override fun getHeaders(): Map<String, String> {
                val map1: MutableMap<String, String> = HashMap()
                map1["Content-Type"] = "application/x-www-form-urlencoded"
                map1["Authorization"] = "Bearer " + sessionManager!!.accessToken
                map1["Version"] = BuildConfig.VERSION_CODE.toString()
                return map1
            }
        }
        stringRequest.retryPolicy = DefaultRetryPolicy(
            0, -1,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )
        val requestQueue = Volley.newRequestQueue(activity)
        requestQueue.add(stringRequest)
        return items
    }

    private fun setView(item: JSONObject) {
        when {
            /* item.getString("type") == "banner" -> {
                 setBanner(
                     Gson().fromJson(
                         item.getJSONObject("data").toString(),
                         Banner::class.java
                     )
                 )
                 hasBanner = true
                 try {
                     linMain!!.addView(rlBanner)
                 } catch (e: java.lang.Exception) {
                     e.printStackTrace()
                 }
             }
             item.getString("type") == "payment" -> {
                 setPayment(
                     Gson().fromJson(
                         item.getJSONObject("data").toString(),
                         Payment::class.java
                     )
                 )
                 hasPayment = true
                 try {
                     linMain!!.addView(rlPayment)
                 } catch (e: java.lang.Exception) {
                     e.printStackTrace()
                 }
             }*/
            /*item.getString("type") == "posted-jobs" -> {
                val jsonArrayData = item.getJSONArray("data")
                for (j in 0 until jsonArrayData.length()) {
                    val itemData = jsonArrayData.getJSONObject(j)
                    postedJobs.add(Gson().fromJson(itemData.toString(), PostedJob::class.java))
                }
                if (postedJobs.isNotEmpty()) {
                    hasPostedJob = true
                    try {
                        linMain!!.addView(rlPostedJobs)
                    } catch (e: java.lang.Exception) {
                        e.printStackTrace()
                    }
                }
            }
            item.getString("type") == "recommended-jobs" -> {
                val jsonArrayData = item.getJSONArray("data")
                for (j in 0 until jsonArrayData.length()) {
                    val itemData = jsonArrayData.getJSONObject(j)
                    recommendedJobs.add(Gson().fromJson(itemData.toString(), PostedJob::class.java))
                }
                if (recommendedJobs.isNotEmpty()) {
                    hasRecommendedJobs = true
                    try {
                        linMain!!.addView(rlRecommendedJobs)
                    } catch (e: java.lang.Exception) {
                        e.printStackTrace()
                    }
                }
            }
            item.getString("type") == "offered-jobs" -> {
                val jsonArrayData = item.getJSONArray("data")
                for (j in 0 until jsonArrayData.length()) {
                    val itemData = jsonArrayData.getJSONObject(j)
                    offeredJobs.add(Gson().fromJson(itemData.toString(), OfferedJob::class.java))
                }
                if (offeredJobs.isNotEmpty()) {
                    hasOfferedJob = true
                    try {
                        linMain!!.addView(rlOfferedJobs)
                    } catch (e: java.lang.Exception) {
                        e.printStackTrace()
                    }
                }
            }*/
            item.getString("type") == "recent-jobs" -> {
                val jsonArrayData = item.getJSONArray("data")
                for (j in 0 until jsonArrayData.length()) {
                    val itemData = jsonArrayData.getJSONObject(j)
                    recentJobs.add(Gson().fromJson(itemData.toString(), PostedJob::class.java))
                }
                if (recentJobs.isNotEmpty()) {
                    hasRecentJobs = true
                    try {
                        linMain!!.addView(rlRecentJobs)
                    } catch (e: java.lang.Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        (requireActivity() as DashboardActivity).hideProgressDialog()
    }

    @SuppressLint("SetTextI18n")
    private fun setPayment(payment: Payment?) {
        if (payment!!.income != null)
            txtIncomeAmount!!.text = "$" + payment.income!!.toDouble().round(2).round()
        else
            txtIncomeAmount!!.text = "$0"
        if (payment.outcome != null)
            txtOutcomeAmount!!.text = "$" + payment.outcome.toDouble().round(2).round()
        else
            txtOutcomeAmount!!.text = "$0"

        if (payment.last_trx == null) {
            if (sessionManager!!.role != "worker") {
                txtLastTransaction!!.text = "You haven’t made any transaction yet, post a job now."
                txtPaymentsAction!!.text = "Post a job"
                imgExplore!!.visibility = View.GONE
                txtTargetName!!.visibility = View.GONE
                txtLastTransactionDate!!.visibility = View.GONE
                linAction!!.setOnClickListener {
                    startCategoryList()
                }
            } else {
                txtLastTransaction!!.text =
                    "You haven’t made any transaction yet, explore jobs and make an offer to complete your first job."
                txtPaymentsAction!!.text = "Explore"
                imgExplore!!.visibility = View.VISIBLE
                txtTargetName!!.visibility = View.GONE
                txtLastTransactionDate!!.visibility = View.GONE
                linAction!!.setOnClickListener {
                    (requireActivity() as DashboardActivity).goToFragment(DashboardActivity.Fragment.EXPLORE)
                }
            }
        } else {
            try {
                txtLastTransaction!!.text = payment.last_trx.title
                txtPaymentsAction!!.text = "Payment history"
                imgExplore!!.visibility = View.GONE
                txtTargetName!!.text = payment.last_trx.username
                txtLastTransactionDate!!.text = payment.last_trx.created_at
                txtLastTransactionAmount!!.text =
                    "$" + payment.last_trx.amount!!.toDouble().round(2).round()
                linAction!!.setOnClickListener {
                    startActivity(Intent(requireContext(), PaymentHistoryActivity::class.java))
                }
            } catch (e: java.lang.Exception) {
                txtLastTransaction!!.text = "You haven’t made any transaction yet, post a job now."
                txtPaymentsAction!!.text = "Post a job"
                imgExplore!!.visibility = View.VISIBLE
                txtTargetName!!.visibility = View.GONE
                txtLastTransactionDate!!.visibility = View.GONE
                linAction!!.setOnClickListener {
                    startCategoryList()
                }
            }
        }
    }

    private fun startCategoryList() {
        val infoBottomSheet = CategoryListBottomSheet(sessionManager)
        infoBottomSheet.show(requireActivity().supportFragmentManager, null)
    }

/*    private fun setPostedJobs(postedJobs: List<PostedJob>?) {
        val layoutManager = LinearLayoutManager(context)
        recyclerPostedJobs!!.layoutManager = layoutManager
        postedJobsAdapter = if (postedJobs!!.size > 2)
            if (hasOfferedJob)
                PostedJobsAdapter(postedJobs.subList(0, 2), null)
            else
                PostedJobsAdapter(postedJobs.subList(0, 3), null)
        else
            PostedJobsAdapter(postedJobs, null)
        recyclerPostedJobs!!.adapter = postedJobsAdapter
        postedJobsAdapter.setOnItemClickListener(this)
    }*/

    private fun setRecommendedJobs(recommendedJobs: List<PostedJob>?) {
        val layoutManager = LinearLayoutManager(context)
        recyclerRecommendedJobs!!.layoutManager = layoutManager
        recommendedJobsAdapter = if (recommendedJobs!!.size > 2)
            RecommendedJobsAdapter(recommendedJobs.subList(0, 3), null)
        else
            RecommendedJobsAdapter(recommendedJobs, null)
        recyclerRecommendedJobs!!.adapter = recommendedJobsAdapter
        recommendedJobsAdapter.setOnItemClickListener(this)
    }

    private fun setRecentJobs(recentJobs: List<PostedJob>?) {
        val layoutManager = LinearLayoutManager(context)
        recyclerRecentJobs!!.layoutManager = layoutManager
        postedJobsAdapter = if (recentJobs!!.size > 2)
            OfferedJobsAdapter(recentJobs.subList(0, 3), null)
        else
            OfferedJobsAdapter(recentJobs, null)
        recyclerRecentJobs!!.adapter = postedJobsAdapter
        postedJobsAdapter.setOnItemClickListener(this)
    }

/*    private fun setOfferedJobs(offeredJobs: List<OfferedJob>?) {

        val layoutManager = LinearLayoutManager(context)
        recyclerOfferedJobs!!.layoutManager = layoutManager
        offeredJobsAdapter = if (offeredJobs!!.size > 2)
            if (hasPostedJob)
                OfferedJobsAdapter(offeredJobs.subList(0, 2), null)
            else
                OfferedJobsAdapter(offeredJobs.subList(0, 3), null)
        else
            OfferedJobsAdapter(offeredJobs, null)
        recyclerOfferedJobs!!.adapter = offeredJobsAdapter
        offeredJobsAdapter.setOnItemClickListener(this)
    }*/

    private fun setBanner(banner: Banner?) {
        if (banner!!.leftImage != null) {
            rlAction!!.layoutDirection = View.LAYOUT_DIRECTION_RTL
            Glide.with(imgStartBanner!!).load(banner.leftImage).into(imgStartBanner!!)
        } else {
            val layoutParams: ViewGroup.LayoutParams = imgStartBanner!!.layoutParams
            layoutParams.width = 0
            imgStartBanner!!.layoutParams = layoutParams
            imgStartBanner!!.visibility = View.INVISIBLE
        }

        if (banner.rightImage != null) {
            rlAction!!.layoutDirection = View.LAYOUT_DIRECTION_LTR
            Glide.with(imgEndBanner!!).load(banner.rightImage).into(imgEndBanner!!)
        } else {
            rlAction!!.layoutDirection = View.LAYOUT_DIRECTION_RTL
            val layoutParams: ViewGroup.LayoutParams = imgEndBanner!!.layoutParams
            layoutParams.width = 0
            imgEndBanner!!.layoutParams = layoutParams
            imgEndBanner!!.visibility = View.INVISIBLE
        }

        bannerTXT!!.text = Html.fromHtml(banner.description)
        txtAction!!.text = banner.action!!.label

        rlAction!!.setOnClickListener {
            if (banner.action.type == "invite") {
                (requireActivity() as DashboardActivity).goToFragment(DashboardActivity.Fragment.INVITE)
            } else {
            }
        }
    }

    private fun setIDs() {
        val dashboardActivity = requireActivity() as DashboardActivity
        recyclerViewCategories = requireView().findViewById(R.id.recyclerView_categories)
        ivNotification = dashboardActivity.findViewById(R.id.ivNotification)
        toolbar = dashboardActivity.findViewById(R.id.toolbar)
        llLoading = requireView().findViewById(R.id.loading_view)
        name = requireView().findViewById(R.id.name)
        updateProfile = requireView().findViewById(R.id.update_profile)
        myJobs = requireView().findViewById(R.id.my_jobs)
        scrollView = requireView().findViewById(R.id.scrollView)
        bannerTXT = requireView().findViewById(R.id.txt_banner)
        txtAction = requireView().findViewById(R.id.txt_action)
        imgStartBanner = requireView().findViewById(R.id.img_start)
        imgEndBanner = requireView().findViewById(R.id.img_end)
        rlBanner = requireView().findViewById(R.id.rl_banner)
        rlPayment = requireView().findViewById(R.id.rl_payment)
        rlPostedJobs = requireView().findViewById(R.id.rl_posted_jobs)
        rlAction = requireView().findViewById(R.id.rl_action)
        rlRecentJobs = requireView().findViewById(R.id.rl_recent_jobs)
        rlOfferedJobs = requireView().findViewById(R.id.rl_offered_jobs)
        rlRecommendedJobs = requireView().findViewById(R.id.rl_recommended_jobs)
        recyclerRecentJobs = requireView().findViewById(R.id.recycler_recent_jobs)
        recyclerPostedJobs = requireView().findViewById(R.id.recycler_posted_jobs)
        recyclerOfferedJobs = requireView().findViewById(R.id.recycler_offered_jobs)
        recyclerRecommendedJobs = requireView().findViewById(R.id.recycler_recommended_jobs)
        seeAllPostedJobs = requireView().findViewById(R.id.see_all_posted_jobs)
        seeAllRecommendedJobs = requireView().findViewById(R.id.see_all_recommended_jobs)
        seeAllOfferedJobs = requireView().findViewById(R.id.see_all_offered_jobs)
        seeAllRecentJobs = requireView().findViewById(R.id.see_all_recent_jobs)
        rlTop = requireView().findViewById(R.id.rl_top)
        linMain = requireView().findViewById(R.id.lin_main)
        space = requireView().findViewById(R.id.space)

        txtIncomeAmount = requireView().findViewById(R.id.txt_income_amount)
        txtOutcomeAmount = requireView().findViewById(R.id.txt_outcome_amount)
        txtPaymentsAction = requireView().findViewById(R.id.txt_payments_action)
        txtLastTransaction = requireView().findViewById(R.id.txt_last_transaction)
        txtLastTransactionAmount = requireView().findViewById(R.id.txt_last_transaction_amount)
        txtLastTransactionDate = requireView().findViewById(R.id.txt_last_transaction_date)
        txtTargetName = requireView().findViewById(R.id.txt_target_name)
        imgExplore = requireView().findViewById(R.id.img_explore)
        linAction = requireView().findViewById(R.id.lin_action)
    }

    private fun initToolbar() {
        val dashboardActivity = requireActivity() as DashboardActivity
        toolbar!!.menu.clear()
        toolbar!!.inflateMenu(R.menu.menu_new_task)
        toolbar!!.menu.findItem(R.id.action_search).isVisible = false
        val toolbarTitle = dashboardActivity.findViewById<TextView>(R.id.toolbar_title)
        val filterText = dashboardActivity.findViewById<TextView>(R.id.filter_text)
        dashboardActivity.findViewById<View>(R.id.lin_filter).visibility = View.GONE
        filterText.text = "All jobs"
        toolbarTitle.visibility = View.VISIBLE
        toolbarTitle.setText(R.string.jobTick)
        toolbar!!.setBackgroundColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.backgroundLightGrey
            )
        )
        val params = Toolbar.LayoutParams(
            Toolbar.LayoutParams.WRAP_CONTENT,
            Toolbar.LayoutParams.WRAP_CONTENT
        )
        params.gravity = Gravity.CENTER
        toolbarTitle.layoutParams = params
        ivNotification!!.visibility = View.VISIBLE
        toolbar!!.navigationIcon = null
        toolbar!!.visibility = View.GONE
    }

    private fun initVars() {
        // linMain!!.removeAllViews()
        try {
            linMain!!.addView(rlTop)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        sessionManager = SessionManager(context)
        name!!.text = sessionManager!!.userAccount.name
        if (!sessionManager!!.userAccount.name.isEmpty()) {
            updateProfile!!.visibility = View.GONE
            myJobs!!.visibility = View.VISIBLE
        }
    }

    private fun onClick() {
        myJobs!!.setOnClickListener { v: View? ->
            (requireActivity() as DashboardActivity).goToFragment(
                DashboardActivity.Fragment.MY_JOBS
            )
        }
        seeAllPostedJobs!!.setOnClickListener { v: View? ->
            (requireActivity() as DashboardActivity).goToFragment(
                DashboardActivity.Fragment.MY_JOBS
            )
        }
        seeAllOfferedJobs!!.setOnClickListener { v: View? ->
            (requireActivity() as DashboardActivity).goToFragment(
                DashboardActivity.Fragment.MY_JOBS
            )
        }
        seeAllRecentJobs!!.setOnClickListener { v: View? ->
            (requireActivity() as DashboardActivity).goToFragment(
                DashboardActivity.Fragment.EXPLORE
            )
        }
        toolbar!!.setOnMenuItemClickListener { false }
        updateProfile!!.setOnClickListener { navigator!!.navigate(R.id.navigation_profile) }
        ivNotification!!.setOnClickListener {
            val intent = Intent(requireContext(), NotificationActivity::class.java)
            startActivityForResult(intent, ConstantKey.RESULTCODE_NOTIFICATION_READ)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == ConstantKey.RESULTCODE_NOTIFICATION_READ) {
            viewModel.notificationList(
                sessionManager!!.accessToken,
                Volley.newRequestQueue(requireContext())
            )
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        navigator = context as DashboardActivity
        super.onAttach(context)
    }

    override fun onItemClick(view: View?, obj: PostedJob?, position: Int, action: String?) {
        goToJob(obj!!.slug)
    }

    private fun goToJob(slug: String?) {
        val intent = Intent(requireContext(), TaskDetailsActivity::class.java)
        val bundle = Bundle()
        bundle.putString(ConstantKey.SLUG, slug)
        //   bundle.putInt(ConstantKey.USER_ID, obj.getPoster().getId());
        intent.putExtras(bundle)
        startActivityForResult(intent, ConstantKey.RESULTCODE_MY_JOBS)
        Timber.i("MyTasksFragment Starting Task with slug: %s", slug)
    }

    override fun onItemClick(view: View?, obj: TaskCategory?, position: Int) {
        val creating_task = Intent(activity, TaskCreateActivity::class.java)
        val bundle = Bundle()
        bundle.putInt(ConstantKey.CATEGORY_ID, obj!!.id)
        creating_task.putExtras(bundle)
        requireActivity().startActivityForResult(creating_task, ConstantKey.RESULTCODE_CATEGORY)
    }
}
