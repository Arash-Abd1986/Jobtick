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
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.google.android.material.textview.MaterialTextView
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

class RescheduleTimeRequestActivity : ActivityBase(),
    ExtendedEntryText.ExtendedViewOnClickListener {
    private lateinit var toolbar: MaterialToolbar
    private lateinit var txtDate: ExtendedEntryText
    private lateinit var txtPreviousDate: TextView
    private lateinit var getTxtPreviousTime: TextView
    private lateinit var lytBtnDecline: MaterialButton
    private lateinit var lytBtnVerify: MaterialButton
    private var isSpinnerOpen = false
    private lateinit var spinnerItems: LinearLayout
    private lateinit var spinnerArrow: AppCompatImageView
    private lateinit var edtTimeSpinner: MaterialTextView
    private lateinit var spinnerMorning: MaterialTextView
    private lateinit var spinnerAfternoon: MaterialTextView
    private lateinit var spinnerEvening: MaterialTextView
    private lateinit var spinnerAnytime: MaterialTextView
    private var year = 0
    private var month = 0
    private var day = 0
    private var dueDate: Long = 0
    private var cbMorning = false
    private var cbAnyTime = false
    private var cbAfternoon = false
    private var cbEvening = false
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
        onViewClick()
    }

    private fun setIDs() {
        edtNote = findViewById(R.id.edt_note)
        toolbar = findViewById(R.id.toolbar)
        txtDate = findViewById(R.id.txt_date)
        txtPreviousDate = findViewById(R.id.txt_previous_date)
        getTxtPreviousTime = findViewById(R.id.txt_previous_time)
        lytBtnVerify = findViewById(R.id.lyt_btn_verify)
        lytBtnDecline = findViewById(R.id.lyt_btn_decline)
        edtTimeSpinner = findViewById(R.id.edt_time_spinner)
        spinnerItems = findViewById(R.id.spinner_items)
        spinnerArrow = findViewById(R.id.spinner_arrow)
        spinnerMorning = findViewById(R.id.spinner_morning)
        spinnerAfternoon = findViewById(R.id.spinner_afternoon)
        spinnerEvening = findViewById(R.id.spinner_evening)
        spinnerAnytime = findViewById(R.id.spinner_anytime)
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
        mDateSetListener =
            DatePickerDialog.OnDateSetListener { _: DatePicker?, year: Int, month: Int, dayOfMonth: Int ->
                val monthL = month + 1
                strDueDate = Tools.getDayMonthDateTimeFormat("$year-$monthL-$dayOfMonth")
                txtDate.text = strDueDate
            }
        txtDate.setExtendedViewOnClickListener(this)
    }

    private fun initToolbar() {
        toolbar.setNavigationIcon(R.drawable.ic_back_black)
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


    private fun onViewClick() {
        lytBtnDecline.setOnClickListener {
            finish()
        }
        lytBtnVerify.setOnClickListener {
            if (validation()) createRequest()
        }
        edtTimeSpinner.setOnClickListener { v: View? ->
            if (isSpinnerOpen) {
                hideSpinner()
            } else {
                showSpinner()
            }
        }
        spinnerMorning.setOnClickListener { v: View? ->
            cbMorning = true
            cbAfternoon = false
            cbEvening = false
            cbAnyTime = false
            edtTimeSpinner.setText(R.string.morning_s)
            hideSpinner()
        }
        spinnerAfternoon.setOnClickListener { v: View? ->
            cbAfternoon = true
            cbMorning = false
            cbEvening = false
            cbAnyTime = false
            edtTimeSpinner.setText(R.string.afternoon_s)
            hideSpinner()
        }
        spinnerEvening.setOnClickListener { v: View? ->
            cbEvening = true
            cbAfternoon = false
            cbMorning = false
            cbAnyTime = false
            edtTimeSpinner.setText(R.string.evening_s)
            hideSpinner()
        }
        spinnerAnytime.setOnClickListener { v: View? ->
            cbAnyTime = true
            cbAfternoon = false
            cbEvening = false
            cbMorning = false
            edtTimeSpinner.setText(R.string.anytime_s)
            hideSpinner()
        }
    }

    private fun showSpinner() {
        edtTimeSpinner.setTextColor(ContextCompat.getColor(this, R.color.P300))
        spinnerArrow.setImageDrawable(
            ContextCompat.getDrawable(
                this,
                R.drawable.ic_up_arrow_g
            )
        )
        isSpinnerOpen = true
        spinnerItems.visibility = View.VISIBLE
    }

    private fun hideSpinner() {
        isSpinnerOpen = false
        spinnerArrow.setImageDrawable(
            ContextCompat.getDrawable(
                this,
                R.drawable.ic_d_arrow_g
            )
        )
        edtTimeSpinner.setTextColor(ContextCompat.getColor(this, R.color.N9001))
        spinnerItems.visibility = View.GONE
    }

    private fun validation(): Boolean {

        if (edtNote.text.length < edtNote.geteMinSize()) {
            edtNote.setError("")
            return false
        }
        if (txtDate.text.isEmpty()) {
            txtDate.setError("Please enter new date.")
            return false
        }
        return true
    }

    private fun createRequest() {

        showProgressDialog()
        val stringRequest: StringRequest = object : StringRequest(Method.POST,
            Constant.URL_TASKS + "/" + taskModel!!.slug + "/" + Constant.URL_CREATE_RESCHEDULE,
            Response.Listener { response: String? ->
                Timber.e(response)
                hideProgressDialog()
                try {
                    val jsonObject = JSONObject(response!!)
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
                when {
                    cbMorning -> {
                        map1["new_duetime"] = "morning"
                    }
                    cbAfternoon -> {
                        map1["new_duetime"] = "afternoon"
                    }
                    cbEvening -> {
                        map1["new_duetime"] = "evening"
                    }
                    else -> {
                        map1["new_duetime"] = "anytime"
                    }
                }
                return map1
            }
        }
        stringRequest.retryPolicy = DefaultRetryPolicy(
            0, -1,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )
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
        mBottomSheetDialog!!.setOnDismissListener { dialog: DialogInterface? ->
            mBottomSheetDialog = null
        }
    }
}