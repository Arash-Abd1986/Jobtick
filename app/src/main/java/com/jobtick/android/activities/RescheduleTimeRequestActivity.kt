package com.jobtick.android.activities

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
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
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputLayout
import com.google.android.material.textview.MaterialTextView
import com.jobtick.android.BuildConfig
import com.jobtick.android.R
import com.jobtick.android.models.TaskModel
import com.jobtick.android.utils.*
import org.json.JSONException
import org.json.JSONObject
import timber.log.Timber
import java.util.*

class RescheduleTimeRequestActivity : ActivityBase() {
    private lateinit var toolbar: LinearLayout
    private lateinit var txtNewDate: MaterialTextView
    private lateinit var txtPreviousDate: TextView
    private lateinit var getTxtPreviousTime: TextView
    private lateinit var lytBtnVerify: MaterialButton
    private lateinit var txtNewTime: MaterialTextView
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
    private lateinit var edtNote: TextInputLayout
    private var mBottomSheetDialog: BottomSheetDialog? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reschedule_time_request)
        setIDs()
        init()
        onViewClick()
    }

    private fun setIDs() {
        edtNote = findViewById(R.id.edt_note)
        toolbar = findViewById(R.id.linTitle)
        txtNewDate = findViewById(R.id.txt_new_date)
        txtPreviousDate = findViewById(R.id.txt_previous_date)
        getTxtPreviousTime = findViewById(R.id.txt_previous_time)
        lytBtnVerify = findViewById(R.id.lyt_btn_verify)
        txtNewTime = findViewById(R.id.txt_new_time)
    }

    private fun init() {
        taskModel = TaskModel()
        val bundle = intent.extras
        if (bundle != null) {
            taskModel = bundle.getParcelable(ConstantKey.TASK)
        }
        if (taskModel != null) {
            txtPreviousDate.text = taskModel!!.dueDate
            txtNewDate.text = taskModel!!.dueDate
            dueDate = TimeHelper.convertDateToLong(taskModel!!.dueDate)
            if (taskModel!!.dueTime.afternoon) {
                getTxtPreviousTime.setText(R.string.afternoon)
                txtNewTime.setText(R.string.afternoon)
            }
            if (taskModel!!.dueTime.evening) {
                getTxtPreviousTime.setText(R.string.evening)
                txtNewTime.setText(R.string.evening)
            }
            if (taskModel!!.dueTime.morning) {
                getTxtPreviousTime.setText(R.string.morning)
                txtNewTime.setText(R.string.morning)
            }
            if (taskModel!!.dueTime.anytime) {
                getTxtPreviousTime.setText(R.string.anyTime)
                txtNewTime.setText(R.string.anyTime)
            }
        }
        mDateSetListener =
                DatePickerDialog.OnDateSetListener { _: DatePicker?, year: Int, month: Int, dayOfMonth: Int ->
                    val monthL = month + 1
                    strDueDate = Tools.getDayMonthDateTimeFormat("$year-$monthL-$dayOfMonth")
                    txtNewDate.text = strDueDate
                }
    }

    private fun showTimeDialog() {
        val layoutInflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view: View = layoutInflater.inflate(R.layout.select_time_dialog, null)
        val infoDialog = AlertDialog.Builder(this)
                .setView(view)
                .create()
        val window = infoDialog.window;

        val wlp = window!!.attributes;
        window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
        wlp.gravity = Gravity.CENTER
        window.attributes = wlp
        infoDialog.show()

        val morning = window.findViewById<MaterialButton>(R.id.morning)
        val afternoon = window.findViewById<MaterialButton>(R.id.afternoon)
        val evening = window.findViewById<MaterialButton>(R.id.evening)
        val anytime = window.findViewById<MaterialButton>(R.id.anytime)
        val cancel = window.findViewById<MaterialButton>(R.id.cancel)
        val ok = window.findViewById<MaterialButton>(R.id.ok)
        morning.setSpanStyledTwoLineText("""Morning
            |Before 12:00 PM""".trimMargin(), applicationContext, breakLinePosition = 7)
        afternoon.setSpanStyledTwoLineText("""Afternoon
            |Between 12:00 PM to 06:00 PM""".trimMargin(), applicationContext, breakLinePosition = 9)
        evening.setSpanStyledTwoLineText("""Evening
            |After 06:00 PM""".trimMargin(), applicationContext, breakLinePosition = 7)
        anytime.setSpanStyledTwoLineText("""Anytime
            |You and Ticker agree on""".trimMargin(), applicationContext, breakLinePosition = 7)
        ok.setOnClickListener {
            infoDialog.dismiss()
        }
        ok.setOnClickListener {
            when {
                morning.isChecked -> {
                    cbMorning = true
                    cbAfternoon = false
                    cbEvening = false
                    cbAnyTime = false
                    txtNewTime.setText(R.string.morning_s)
                }
                afternoon.isChecked -> {
                    cbAfternoon = true
                    cbMorning = false
                    cbEvening = false
                    cbAnyTime = false
                    txtNewTime.setText(R.string.afternoon_s)
                }
                evening.isChecked -> {
                    cbEvening = true
                    cbAfternoon = false
                    cbMorning = false
                    cbAnyTime = false
                    txtNewTime.setText(R.string.evening_s)
                }
                anytime.isChecked -> {
                    cbAnyTime = true
                    cbAfternoon = false
                    cbEvening = false
                    cbMorning = false
                    txtNewTime.setText(R.string.anytime_s)
                }
            }
            infoDialog.dismiss()
        }
        morning.setOnClickListener {
            //viewModel.setTime(PostAJobViewModel.PostAJobTime.MORNING)
            morning.isChecked = false
            afternoon.isChecked = false
            evening.isChecked = false
            anytime.isChecked = false
            morning.isChecked = true
        }
        afternoon.setOnClickListener {
            //viewModel.setTime(PostAJobViewModel.PostAJobTime.AFTERNOON)
            morning.isChecked = false
            afternoon.isChecked = false
            evening.isChecked = false
            anytime.isChecked = false
            afternoon.isChecked = true
        }
        evening.setOnClickListener {
            //viewModel.setTime(PostAJobViewModel.PostAJobTime.EVENING)
            morning.isChecked = false
            afternoon.isChecked = false
            evening.isChecked = false
            anytime.isChecked = false
            evening.isChecked = true
        }
        anytime.setOnClickListener {
            //viewModel.setTime(PostAJobViewModel.PostAJobTime.ANY_TIME)
            morning.isChecked = false
            afternoon.isChecked = false
            evening.isChecked = false
            anytime.isChecked = false
            anytime.isChecked = true
        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }


    private fun onViewClick() {
        txtNewDate.setOnClickListener {
            showBottomSheetDialogDate()
        }
        lytBtnVerify.setOnClickListener {
            if (validation()) createRequest()
        }
        txtNewTime.setOnClickListener {
            showTimeDialog()
        }
    }


    private fun validation(): Boolean {

        if (edtNote.editText!!.text.length < 25) {
            edtNote.error = "Must be at least 25 characters"
            return false
        }
        if (txtNewDate.text.isEmpty()) {
            txtNewDate.error = "Please enter new date."
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
                map1["reason"] = edtNote.editText!!.text.toString()
                map1["new_duedate"] = Tools.getApplicationFromatToServerFormat(txtNewDate.text.toString())
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
        txtCancel.setOnClickListener { mBottomSheetDialog!!.dismiss() }
        val lytBtnDone = view.findViewById<LinearLayout>(R.id.lyt_btn_done)
        val calendar = Calendar.getInstance()
        year = calendar[Calendar.YEAR]
        month = calendar[Calendar.MONTH]
        month += 1
        day = calendar[Calendar.DAY_OF_MONTH]
        lytBtnDone.setOnClickListener {
            strDueDate = Tools.getDayMonthDateTimeFormat("$year-$month-$day")
            txtNewDate.text = strDueDate
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