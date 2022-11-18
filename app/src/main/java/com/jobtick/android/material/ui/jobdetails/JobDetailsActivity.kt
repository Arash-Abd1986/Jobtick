package com.jobtick.android.material.ui.jobdetails

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.android.volley.DefaultRetryPolicy
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.textview.MaterialTextView
import com.jobtick.android.R
import com.jobtick.android.activities.ActivityBase
import com.jobtick.android.activities.ReportActivity
import com.jobtick.android.activities.RescheduleTimeRequestActivity
import com.jobtick.android.activities.TaskDetailsActivity
import com.jobtick.android.cancellations.CancellationPosterActivity
import com.jobtick.android.cancellations.CancellationWorkerActivity
import com.jobtick.android.fragments.ConfirmAskToReleaseBottomSheet
import com.jobtick.android.fragments.ConfirmReleaseBottomSheet.NoticeListener
import com.jobtick.android.fragments.IncreaseBudgetNoticeBottomSheet
import com.jobtick.android.models.TaskModel
import com.jobtick.android.network.coroutines.ApiHelper
import com.jobtick.android.network.retrofit.ApiClient
import com.jobtick.android.utils.*
import com.jobtick.android.viewmodel.EventsViewModel
import com.jobtick.android.viewmodel.JobDetailsViewModel
import com.jobtick.android.viewmodel.ViewModelFactory
import org.json.JSONException
import org.json.JSONObject
import timber.log.Timber


class JobDetailsActivity : ActivityBase(), IncreaseBudgetFragment.NoticeListener,
        RescheduleNoticeFragment.NoticeListener, IncreaseBudgetNoticeBottomSheet.NoticeListener, ConfirmAskToReleaseBottomSheet.NoticeListener, NoticeListener {
    lateinit var navController: NavController
    lateinit var viewModel: JobDetailsViewModel
    private lateinit var sessionManager: SessionManager
    private lateinit var title: MaterialTextView
    private lateinit var linTitle: LinearLayout
    private lateinit var close: AppCompatImageView
    private lateinit var options: AppCompatImageView
    private lateinit var back: AppCompatImageView
    private lateinit var eventsViewModel: EventsViewModel
    private var strSlug: String = ""
    private var isFromSearch = false
    private var pushOfferID = 0
    private var pushQuestionID = 0
    private var isUserThePoster = false
    private var isUserTheTicker = false
    private var noActionAvailable = false
    var popupWindow: PopupWindow? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_job_details)
        title = findViewById(R.id.title)
        close = findViewById(R.id.close)
        options = findViewById(R.id.options)
        back = findViewById(R.id.back)
        linTitle = findViewById(R.id.linTitle)
        back.setOnClickListener {
            navController.popBackStack()
        }
        close.setOnClickListener {
            navController.popBackStack()
        }
        val navHostFragment =
                supportFragmentManager.findFragmentById(R.id.nav_host) as NavHostFragment?
        navController = navHostFragment!!.navController
        navController.addOnDestinationChangedListener { _, destination, _ ->
            linTitle.visible()
            close.invisible()
            when (destination.id) {
                R.id.jobDetailsPosterFragment -> {
                    title.text = "Job Details"
                }
                R.id.increaseBudgetBottomSheet -> {
                    linTitle.gone()
                }
                R.id.rescheduleNoticeBottomSheetState -> {
                    linTitle.gone()
                }
                R.id.increaseBudgetNoticeBottomSheet -> {
                    linTitle.gone()
                }
            }
        }
        initVars()
        initVm()
    }

    private fun setToolbarWindow(taskModel: TaskModel) {
        val inflater =
                getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.job_details_menu, null)
        popupWindow = PopupWindow(
                view,
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                true
        )
        popupWindow!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val increasePrice = view.findViewById<View>(R.id.increasePrice) as TextView
        val reschedule = view.findViewById<View>(R.id.reschedule) as TextView
        val cancellation = view.findViewById<View>(R.id.cancellation) as TextView
        val report = view.findViewById<View>(R.id.report) as TextView
        increasePrice.setOnClickListener {
            navController.navigate(R.id.increaseBudgetBottomSheet)
            popupWindow!!.dismiss()
        }
        reschedule.setOnClickListener {
            intent =
                    Intent(applicationContext, RescheduleTimeRequestActivity::class.java)
            val bundle = Bundle()
            bundle.putParcelable(ConstantKey.TASK, taskModel)
            intent.putExtras(bundle)
            startActivityForResult(intent, ConstantKey.RESULTCODE_RESCHEDULE)
            popupWindow!!.dismiss()
        }
        cancellation.setOnClickListener {
            val intent: Intent = if (isUserThePoster) {
                Intent(applicationContext, CancellationPosterActivity::class.java)
            } else {
                Intent(applicationContext, CancellationWorkerActivity::class.java)
            }
            val bundle = Bundle()
            bundle.putString(ConstantKey.SLUG, taskModel.slug)
            intent.putExtras(bundle)
            startActivityForResult(intent, ConstantKey.RESULTCODE_CANCELLATION)
            popupWindow!!.dismiss()
        }
        report.setOnClickListener {
            val bundleReport = Bundle()
            val intentReport = Intent(applicationContext, ReportActivity::class.java)
            bundleReport.putString(ConstantKey.SLUG, taskModel!!.slug)
            bundleReport.putString("key", ConstantKey.KEY_TASK_REPORT)
            intentReport.putExtras(bundleReport)
            startActivity(intentReport)
            popupWindow!!.dismiss()
        }
    }

    private fun initVars() {
        sessionManager = SessionManager(this)
    }

    private fun initVm() {
        viewModel = ViewModelProvider(
                this,
                ViewModelFactory(ApiHelper(ApiClient.getClient()))
        )[JobDetailsViewModel::class.java]
        eventsViewModel = ViewModelProvider(
                this,
                ViewModelFactory(ApiHelper(ApiClient.getClientV2(sessionManager)))
        )[EventsViewModel::class.java]
        viewModel.getTaskModel(applicationContext, strSlug, sessionManager.tokenType, sessionManager.accessToken, sessionManager.userAccount.id)
        showProgressDialog()
        viewModel.geTaskModelResponse().observe(this) { taskModel ->
            hideProgressDialog()
            setOwnerTask(taskModel = taskModel)
            options.setOnClickListener {
                setToolbarWindow(taskModel)
                popupWindow!!.showAsDropDown(options, 0, Tools.px2dip(applicationContext, -20f))
            }
        }
    }

    private fun setOwnerTask(taskModel: TaskModel) {
        if (taskModel.poster.id == sessionManager.userAccount.id) {
            isUserThePoster = true
            isUserTheTicker = false
            viewModel.userType = JobDetailsViewModel.UserType.POSTER
        } else {
            if (taskModel.worker != null) {
                if (taskModel.worker.id == sessionManager.userAccount.id) {
                    isUserThePoster = false
                    isUserTheTicker = true
                    noActionAvailable = false
                    viewModel.userType = JobDetailsViewModel.UserType.TICKER
                } else {
                    noActionAvailable = true
                    isUserTheTicker = false
                }
            } else {
                isUserThePoster = false
                isUserTheTicker = false
                noActionAvailable = false
                viewModel.userType = JobDetailsViewModel.UserType.VIEWER
            }
        }
        setTickerViewerMode()
    }

    private fun setTickerViewerMode() {
        val navHostFragment =
                supportFragmentManager.findFragmentById(R.id.nav_host) as NavHostFragment?
        val inflater = navHostFragment?.navController?.navInflater
        val graph = inflater?.inflate(R.navigation.job_details_graph)
        graph?.setStartDestination(R.id.jobDetailsPosterFragment)

        navHostFragment?.navController?.graph = graph!!
    }

    override fun getExtras() {
        super.getExtras()
        if (intent == null || intent.extras == null) {
            return
        }
        val bundle = intent.extras
        if (bundle!!.getString(ConstantKey.SLUG) != null) {
            strSlug = bundle.getString(ConstantKey.SLUG)!!
        }
        isFromSearch = bundle.getBoolean(ConstantKey.IS_FROM_SEARCH)
        if (bundle.getInt(ConstantKey.PUSH_OFFER_ID) != 0) {
            pushOfferID = bundle.getInt(ConstantKey.PUSH_OFFER_ID)
        }
        if (bundle.getInt(ConstantKey.PUSH_QUESTION_ID) != 0) {
            pushQuestionID = bundle.getInt(ConstantKey.PUSH_QUESTION_ID)
        }
    }

    override fun onSubmitIncreasePrice() {
    }

    override fun onRescheduleTimeAcceptDeclineClick() {
    }

    override fun onRescheduleWithDraw() {
    }

    override fun onIncreaseBudgetAcceptClick() {

    }

    override fun onIncreaseBudgetRejectClick() {
    }

    override fun onIncreaseBudgetWithDrawClick() {
    }

    override fun onReleaseConfirmClick() {
        submitReleaseMoney()
        
    }

    override fun onAskToReleaseConfirmClick() {
        submitAskToReleaseMoney()
    }

    private fun submitAskToReleaseMoney() {
        showProgressDialog()
        val stringRequest: StringRequest = object :
                StringRequest(
                        Method.POST, Constant.URL_TASKS + "/" + viewModel.geTaskModelResponse().value!!.slug + "/complete",
                        com.android.volley.Response.Listener { response: String? ->
                            Timber.e(response)
                            try {
                                val jsonObject = JSONObject(response!!)
                                Timber.e(jsonObject.toString())
                                if (jsonObject.has("success") && !jsonObject.isNull("success")) {
                                    viewModel.getTaskModel(applicationContext, strSlug, sessionManager.tokenType, sessionManager.accessToken, sessionManager.userAccount.id)
                                } else {
                                    hideProgressDialog()
                                    showToast(
                                            getString(R.string.server_went_wrong),
                                            this@JobDetailsActivity
                                    )
                                }
                            } catch (e: JSONException) {
                                hideProgressDialog()
                                e.printStackTrace()
                            }
                        },
                        com.android.volley.Response.ErrorListener { error: VolleyError ->
                            val networkResponse = error.networkResponse
                            if (networkResponse != null && networkResponse.data != null) {
                                val jsonError = String(networkResponse.data)
                                // Print Error!
                                Timber.e(jsonError)
                                if (networkResponse.statusCode == HttpStatus.AUTH_FAILED) {
                                    unauthorizedUser()
                                    hideProgressDialog()
                                    return@ErrorListener
                                }
                                hideProgressDialog()
                                try {
                                    val jsonObject = JSONObject(jsonError)
                                    val jsonObject_error = jsonObject.getJSONObject("error")
                                    if (jsonObject_error.has("message")) {
                                        showToast(jsonObject_error.getString("message"), this)
                                    }
                                } catch (e: JSONException) {
                                    e.printStackTrace()
                                }
                            } else {
                                showToast("Something Went Wrong", this@JobDetailsActivity)
                            }
                            Timber.e(error.toString())
                            hideProgressDialog()
                        }
                ) {
            override fun getHeaders(): Map<String, String> {
                val map1: MutableMap<String, String> = HashMap()
                map1["authorization"] = sessionManager.tokenType + " " + sessionManager.accessToken
                map1["Content-Type"] = "application/x-www-form-urlencoded"
                return map1
            }
        }
        stringRequest.retryPolicy = DefaultRetryPolicy(
                0, -1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )
        val requestQueue = Volley.newRequestQueue(this@JobDetailsActivity)
        requestQueue.add(stringRequest)
    }
    private fun submitReleaseMoney() {
        showProgressDialog()
        val stringRequest: StringRequest = object :
                StringRequest(
                        Method.POST, Constant.URL_TASKS + "/" + viewModel.geTaskModelResponse().value!!.slug + "/close",
                        com.android.volley.Response.Listener { response: String? ->
                            Timber.e(response)
                            try {
                                val jsonObject = JSONObject(response!!)
                                Timber.e(jsonObject.toString())
                                if (jsonObject.has("success") && !jsonObject.isNull("success")) {
                                    viewModel.getTaskModel(applicationContext, strSlug, sessionManager.tokenType, sessionManager.accessToken, sessionManager.userAccount.id)
                                } else {
                                    hideProgressDialog()
                                    showToast(
                                            getString(R.string.server_went_wrong),
                                            this@JobDetailsActivity
                                    )
                                }
                            } catch (e: JSONException) {
                                hideProgressDialog()
                                e.printStackTrace()
                            }
                        },
                        com.android.volley.Response.ErrorListener { error: VolleyError ->
                            val networkResponse = error.networkResponse
                            if (networkResponse != null && networkResponse.data != null) {
                                val jsonError = String(networkResponse.data)
                                // Print Error!
                                Timber.e(jsonError)
                                if (networkResponse.statusCode == HttpStatus.AUTH_FAILED) {
                                    unauthorizedUser()
                                    hideProgressDialog()
                                    return@ErrorListener
                                }
                                hideProgressDialog()
                                try {
                                    val jsonObject = JSONObject(jsonError)
                                    val jsonObject_error = jsonObject.getJSONObject("error")
                                    if (jsonObject_error.has("message")) {
                                        showToast(jsonObject_error.getString("message"), this)
                                    }

                                    //  ((CredentialActivity)requireActivity()).showToast(message,requireActivity());
                                } catch (e: JSONException) {
                                    e.printStackTrace()
                                }
                            } else {
                                showToast("Something Went Wrong", this@JobDetailsActivity)
                            }
                            Timber.e(error.toString())
                            hideProgressDialog()
                        }
                ) {
            override fun getHeaders(): Map<String, String> {
                val map1: MutableMap<String, String> = HashMap()
                map1["authorization"] = sessionManager.tokenType + " " + sessionManager.accessToken
                map1["Content-Type"] = "application/x-www-form-urlencoded"
                return map1
            }
        }
        stringRequest.retryPolicy = DefaultRetryPolicy(
                0, -1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )
        val requestQueue = Volley.newRequestQueue(this@JobDetailsActivity)
        requestQueue.add(stringRequest)
    }

}