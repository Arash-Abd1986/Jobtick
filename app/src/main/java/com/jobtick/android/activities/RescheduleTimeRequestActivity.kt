package com.jobtick.android.activities

import android.app.DatePickerDialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.CalendarView
import android.widget.DatePicker
import android.widget.LinearLayout
import android.widget.TextView
import butterknife.OnClick
import com.android.volley.*
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.jobtick.android.BuildConfig
import com.jobtick.android.R
import com.jobtick.android.models.TaskModel
import com.jobtick.android.utils.Constant
import com.jobtick.android.utils.HttpStatus
import com.jobtick.android.utils.TimeHelper
import com.jobtick.android.utils.Tools
import com.jobtick.android.widget.ExtendedCommentText
import com.jobtick.android.widget.ExtendedEntryText
import org.json.JSONException
import org.json.JSONObject
import timber.log.Timber
import java.util.*

class RescheduleTimeRequestActivity : ActivityBase(), ExtendedEntryText.ExtendedViewOnClickListener {
    private lateinit var toolbar: MaterialToolbar
    private lateinit var txtDate: ExtendedEntryText
    private lateinit var txtPreviousDate: TextView
    private lateinit var getTxtPreviousTime: TextView
    private  var year = 0
    private  var month = 0
    private  var day = 0
    private  var dueDate: Long = 0
    private lateinit var mDateSetListener: DatePickerDialog.OnDateSetListener
    private lateinit var strDueDate: String
    private var taskModel: TaskModel? = null
    private lateinit var edtNote: ExtendedCommentText
    private var mBottomSheetDialog: BottomSheetDialog? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reschedule_time_request)
        setIDs()
        initToolbar()
        init()
    }

    private fun setIDs() {
        edtNote = findViewById(R.id.edt_note)
        toolbar = findViewById(R.id.toolbar)
        txtDate = findViewById(R.id.txt_date)
        txtPreviousDate = findViewById(R.id.txt_previous_date)
        getTxtPreviousTime = findViewById(R.id.txt_previous_time)
    }

    private fun init() {
        taskModel = TaskModel()
        val bundle = intent.extras
        if (bundle != null) {
            taskModel = TaskDetailsActivity.taskModel
        }
        if (taskModel != null) {
            txtPreviousDate.text = taskModel!!.dueDate
            dueDate = TimeHelper.convertDateToLong(taskModel!!.dueDate)
            if (taskModel!!.dueTime.afternoon) getTxtPreviousTime.setText(R.string.afternoon)
            if (taskModel!!.dueTime.evening) getTxtPreviousTime.setText(R.string.evening)
            if (taskModel!!.dueTime.morning) getTxtPreviousTime.setText(R.string.morning)
            if (taskModel!!.dueTime.anytime) getTxtPreviousTime.setText(R.string.anyTime)
        }
        mDateSetListener = DatePickerDialog.OnDateSetListener { _: DatePicker?, year: Int, month: Int, dayOfMonth: Int ->
            val monthL = month + 1
            strDueDate = Tools.getDayMonthDateTimeFormat("$year-$monthL-$dayOfMonth")
            txtDate.text = strDueDate
        }
        txtDate.setExtendedViewOnClickListener(this)
    }

    private fun initToolbar() {
        toolbar.setNavigationIcon(R.drawable.ic_cancel)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.title = "Reschedule time"
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }


    @OnClick(R.id.lyt_btn_verify, R.id.lyt_btn_decline)
    fun onViewClicked(view: View) {
        when (view.id) {
            R.id.lyt_btn_verify -> if (validation()) createRequest()
            R.id.lyt_btn_decline -> finish()
        }
    }

    private fun validation(): Boolean {
        if (txtDate.text.isEmpty()) {
            txtDate.setError("Please enter new date.")
            return false
        }
        if (edtNote.text.length < edtNote.geteMinSize()) {
            edtNote.setError("")
            return false
        }
        return true
    }

    private fun createRequest() {

        showProgressDialog()
        val stringRequest: StringRequest = object : StringRequest(Method.POST, Constant.URL_TASKS + "/" + taskModel!!.slug + "/" + Constant.URL_CREATE_RESCHEDULE,
                Response.Listener { response: String? ->
                    Timber.e(response)
                    hideProgressDialog()
                    try {
                        val jsonObject = JSONObject(response)
                        Timber.e(jsonObject.toString())
                        if (jsonObject.has("success") && !jsonObject.isNull("success")) {
                            if (jsonObject.getBoolean("success")) {
                                finish()
                            } else {
                                showToast("Something went Wrong", this@RescheduleTimeRequestActivity)
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
                showToast("Something Went Wrong", this@RescheduleTimeRequestActivity)
            }
            Timber.e(error.toString())
            hideProgressDialog()
        }) {
            override fun getHeaders(): Map<String, String> {
                val map1: MutableMap<String, String> = HashMap()
                map1["authorization"] = sessionManager.tokenType + " " + sessionManager.accessToken
                map1["Content-Type"] = "application/x-www-form-urlencoded"
                map1["X-Requested-With"] = "XMLHttpRequest"
                map1["Version"] = BuildConfig.VERSION_CODE.toString()
                return map1
            }

            override fun getParams(): Map<String, String> {
                val map1: MutableMap<String, String> = HashMap()
                map1["reason"] = edtNote.text
                map1["new_duedate"] = Tools.getApplicationFromatToServerFormat(txtDate.text)
                return map1
            }
        }
        stringRequest.retryPolicy = DefaultRetryPolicy(0, -1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
        val requestQueue = Volley.newRequestQueue(this@RescheduleTimeRequestActivity)
        requestQueue.add(stringRequest)
    }

    override fun onClick() {
        showBottomSheetDialogDate()
    }

    private fun showBottomSheetDialogDate() {
        val view = layoutInflater.inflate(R.layout.sheet_date, null)
        mBottomSheetDialog = BottomSheetDialog(this)
        mBottomSheetDialog!!.setContentView(view)
        mBottomSheetDialog!!.window!!.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        val oneDay: Long = 86400000
        val calendarView = view.findViewById<CalendarView>(R.id.calenderView)
        //set min date to tomorrow
        calendarView.minDate = System.currentTimeMillis() - 1000 + oneDay
        //set max date to two weeks later
        calendarView.maxDate = dueDate + oneDay * Constant.MAX_RESCHEDULE_DAY + 1000
        val txtCancel = view.findViewById<TextView>(R.id.txt_cancel)
        txtCancel.setOnClickListener { v: View? -> mBottomSheetDialog!!.dismiss() }
        val lytBtnDone = view.findViewById<LinearLayout>(R.id.lyt_btn_done)
        val calendar = Calendar.getInstance()
        year = calendar[Calendar.YEAR]
        month = calendar[Calendar.MONTH]
        month += 1
        day = calendar[Calendar.DAY_OF_MONTH]
        lytBtnDone.setOnClickListener { v: View? ->
            strDueDate = Tools.getDayMonthDateTimeFormat("$year-$month-$day")
            txtDate.text = strDueDate
            mBottomSheetDialog!!.dismiss()
        }
        calendarView.setOnDateChangeListener { arg0: CalendarView?, year: Int, month: Int, date: Int ->
            this.month = month + 1
            this.year = year
            day = date
        }


        // set background transparent
        (view.parent as View).setBackgroundColor(resources.getColor(android.R.color.transparent))
        mBottomSheetDialog!!.show()
        mBottomSheetDialog!!.setOnDismissListener { dialog: DialogInterface? -> mBottomSheetDialog = null }
    }
}