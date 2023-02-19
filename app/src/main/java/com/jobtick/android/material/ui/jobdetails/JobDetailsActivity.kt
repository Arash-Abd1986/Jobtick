package com.jobtick.android.material.ui.jobdetails

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
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
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textview.MaterialTextView
import com.google.gson.Gson
import com.jobtick.android.R
import com.jobtick.android.activities.*
import com.jobtick.android.cancellations.CancellationPosterActivity
import com.jobtick.android.cancellations.CancellationWorkerActivity
import com.jobtick.android.fragments.ConfirmAskToReleaseBottomSheet
import com.jobtick.android.fragments.ConfirmReleaseBottomSheet.NoticeListener
import com.jobtick.android.fragments.IncreaseBudgetDeclineBottomSheet
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
import java.util.*
import kotlin.collections.HashMap


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
            if(!navController.popBackStack())
                finish()
            else
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

        val increasePrice = view.findViewById<View>(R.id.increasePrice) as MaterialButton
        val reschedule = view.findViewById<View>(R.id.reschedule) as MaterialButton
        val cancellation = view.findViewById<View>(R.id.cancellation) as MaterialButton
        val report = view.findViewById<View>(R.id.report) as MaterialButton
        val jobReceipt = view.findViewById<View>(R.id.jobReceipt) as MaterialButton

        if (isUserThePoster)
            when (taskModel.status) {
                "open" -> {
                    increasePrice.gone()
                    reschedule.gone()
                    report.gone()
                    jobReceipt.gone()
                    cancellation.setTextColor(getColor(R.color.primary_error))
                    cancellation.setIconTintResource(R.color.primary_error)
                    cancellation.setMargins()
                }
                "assigned" -> {
                    if (doWeHaveActiveRequest(taskModel)) {
                        increasePrice.visible()
                        increasePrice.isEnabled = false
                        reschedule.visible()
                        reschedule.isEnabled = false
                        jobReceipt.gone()
                        cancellation.visible()
                        cancellation.isEnabled = false
                        report.gone()
                    } else {
                        increasePrice.visible()
                        increasePrice.isEnabled = true
                        reschedule.visible()
                        reschedule.isEnabled = true
                        jobReceipt.gone()
                        cancellation.visible()
                        cancellation.isEnabled = true
                        report.gone()
                    }
                }
                "cancelled" -> {
                    increasePrice.visible()
                    increasePrice.isEnabled = false
                    reschedule.visible()
                    reschedule.isEnabled = false
                    jobReceipt.gone()
                    cancellation.visible()
                    cancellation.isEnabled = false
                    report.gone()
                }
                "completed", "closed" -> {
                    increasePrice.gone()
                    reschedule.gone()
                    cancellation.gone()
                    jobReceipt.visible()
                    report.gone()
                }
            }
        else
            when (taskModel.status) {
                "open" -> {
                    increasePrice.gone()
                    reschedule.gone()
                    jobReceipt.gone()
                    cancellation.gone()
                    report.setMargins()
                }
                "offered" -> {
                    if (isUserTheTicker) {
                        increasePrice.visible()
                        reschedule.visible()
                        cancellation.visible()
                        report.visible()
                        jobReceipt.gone()
                    } else {
                        increasePrice.gone()
                        reschedule.gone()
                        jobReceipt.gone()
                        cancellation.gone()
                        report.visible()
                        report.setMargins()
                        jobReceipt.gone()
                    }
                }
                "assigned" -> {
                    if (doWeHaveActiveRequest(taskModel)) {
                        increasePrice.visible()
                        increasePrice.isEnabled = false
                        reschedule.visible()
                        reschedule.isEnabled = false
                        jobReceipt.gone()
                        cancellation.visible()
                        cancellation.isEnabled = false
                        report.visible()
                    } else {
                        increasePrice.visible()
                        increasePrice.isEnabled = true
                        reschedule.visible()
                        reschedule.isEnabled = true
                        jobReceipt.gone()
                        cancellation.visible()
                        cancellation.isEnabled = true
                        report.visible()
                    }
                }
                "cancelled" -> {
                    increasePrice.visible()
                    increasePrice.isEnabled = false
                    reschedule.visible()
                    reschedule.isEnabled = false
                    jobReceipt.gone()
                    cancellation.visible()
                    cancellation.isEnabled = false
                    report.gone()
                }
                "completed", "closed" -> {
                    increasePrice.gone()
                    reschedule.gone()
                    cancellation.gone()
                    jobReceipt.visible()
                    report.visible()
                }
            }



        jobReceipt.setOnClickListener {
            intent = Intent(this@JobDetailsActivity, JobReceiptActivity::class.java)
            val bundle = Bundle()
            bundle.putBoolean(ConstantKey.IS_MY_TASK, isUserThePoster)
            bundle.putString(ConstantKey.TASK_SLUG, taskModel.slug)
            intent.putExtras(bundle)
            startActivity(intent)
            popupWindow!!.dismiss()
        }
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
            popupWindow!!.dismiss()
            if (taskModel.status != "open") {
                val intent: Intent = if (isUserThePoster) {
                    Intent(applicationContext, CancellationPosterActivity::class.java)
                } else {
                    Intent(applicationContext, CancellationWorkerActivity::class.java)
                }
                val bundle = Bundle()
                val gson = Gson()
                val jsonString = gson.toJson(taskModel)
                bundle.putString(ConstantKey.TASK, jsonString)
                intent.putExtras(bundle)
                startActivityForResult(intent, ConstantKey.RESULTCODE_CANCELLATION)
            } else {
                MaterialAlertDialogBuilder(this@JobDetailsActivity)
                        .setTitle(resources.getString(R.string.title_delete))
                        .setNegativeButton(resources.getString(R.string.no)) { dialog: DialogInterface, which: Int -> dialog.dismiss() }
                        .setPositiveButton(resources.getString(R.string.yes)) { dialog: DialogInterface, which: Int ->
                            dialog.dismiss()
                            deleteTaskPermanent(TaskDetailsActivity.taskModel!!.slug)
                        }.show()
            }
        }
        report.setOnClickListener {
            val bundleReport = Bundle()
            val intentReport = Intent(applicationContext, ReportActivity::class.java)
            bundleReport.putString(ConstantKey.SLUG, taskModel.slug)
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
        Log.d("taskModel", "here1")

        showProgressDialog()
        linTitle.invisible()
        viewModel.geTaskModelResponse().observe(this) { taskModel ->
            hideProgressDialog()
            linTitle.visible()
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
        try {

            val navHostFragment =
                supportFragmentManager.findFragmentById(R.id.nav_host) as NavHostFragment?
            val inflater = navHostFragment?.navController?.navInflater
            val graph = inflater?.inflate(R.navigation.job_details_graph)
            graph?.setStartDestination(R.id.jobDetailsPosterFragment)

            navHostFragment?.navController?.graph = graph!!
            Log.d("herehere", "5")
        }catch (e:Exception) {}
    }

    override fun getExtras() {
        super.getExtras()
        try {

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
        }catch (e: Exception){}
    }

    override fun onSubmitIncreasePrice() {
        recreate()
    }

    override fun onRescheduleTimeAcceptDeclineClick() {
        recreate()
    }

    override fun onRescheduleWithDraw() {
        recreate()
    }

    override fun onIncreaseBudgetAcceptClick() {
        recreate()
    }

    override fun onIncreaseBudgetRejectClick() {
        val fragmentManager = supportFragmentManager
        val dialog = IncreaseBudgetDeclineBottomSheet.newInstance(viewModel.geTaskModelResponse().value)
        dialog.show(fragmentManager, "")
    }

    override fun onIncreaseBudgetWithDrawClick() {
        recreate()
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
                                    recreate()
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
                                    recreate()
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

    private fun deleteTaskPermanent(slug: String) {
        showProgressDialog()
        val stringRequest: StringRequest =
                object : StringRequest(
                        Method.POST, Constant.URL_TASKS + "/" + slug + "/cancellation",
                        com.android.volley.Response.Listener { response: String? ->
                            Timber.e(response)
                            try {
                                val jsonObject = JSONObject(response!!)
                                if (jsonObject.has("success") && !jsonObject.isNull("success")) {
                                    if (jsonObject.getBoolean("success")) {
                                        recreate()
                                        showSuccessToast(
                                                "Job Cancelled Successfully",
                                                this@JobDetailsActivity
                                        )
                                    } else {
                                        showToast("Job not cancelled", this@JobDetailsActivity)
                                    }
                                } else {
                                    showToast(
                                            getString(R.string.server_went_wrong),
                                            this@JobDetailsActivity
                                    )
                                }
                                hideProgressDialog()
                            } catch (e: JSONException) {
                                hideProgressDialog()
                                e.printStackTrace()
                            }
                        },
                        com.android.volley.Response.ErrorListener { error: VolleyError? -> hideProgressDialog() }
                ) {
                    override fun getHeaders(): Map<String, String> {
                        val map1: MutableMap<String, String> = HashMap()
                        map1["authorization"] =
                                sessionManager.tokenType + " " + sessionManager.accessToken
                        map1["Content-Type"] = "application/json"
                        map1["X-Requested-With"] = "XMLHttpRequest"
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


    private fun doWeHaveActiveRequest(taskModel: TaskModel): Boolean {
        if (taskModel.rescheduleReqeust != null && taskModel.rescheduleReqeust.size > 0) {
            for (i in taskModel.rescheduleReqeust.indices) {
                if (taskModel.rescheduleReqeust[i].status == "pending") {
                    return true
                }
            }
        }
        if (taskModel.additionalFund != null && taskModel.additionalFund.status == "pending")
            return true

        return false
    }

}