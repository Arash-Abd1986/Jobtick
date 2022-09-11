package com.jobtick.android.fragments

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewOutlineProvider
import android.widget.*
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.AppCompatImageView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.textview.MaterialTextView
import com.jobtick.android.R
import com.jobtick.android.activities.TaskCreateActivity
import com.jobtick.android.activities.TaskCreateActivity.ActionDraftDateTime
import com.jobtick.android.models.DueTimeModel
import com.jobtick.android.models.TaskModel
import com.jobtick.android.utils.EventTitles
import com.jobtick.android.utils.SessionManager
import com.jobtick.android.utils.Tools
import com.jobtick.android.utils.pushEvent
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*

class TaskDateTimeFragment : Fragment(), TextWatcher {
    var task: TaskModel? = null

    private lateinit var operationsListener: OperationsListener
    private lateinit var bottomSheet: FrameLayout
    private lateinit var imgDetails: ImageView
    private lateinit var txtDetails: TextView
    private lateinit var imgDateTime: ImageView
    private lateinit var txtDateTime: TextView
    private lateinit var imgBudget: ImageView
    private lateinit var txtBudget: TextView
    private lateinit var lytBtnDetails: LinearLayout
    private lateinit var lytBntDateTime: LinearLayout
    private lateinit var cardDateTime: CardView
    private lateinit var cardDetails: CardView
    private lateinit var lytBtnBudget: LinearLayout
    private lateinit var lyt_btn_back: LinearLayout
    private lateinit var btnNext: MaterialButton
    private lateinit var spinnerItems: LinearLayout
    private lateinit var edtTimeSpinner: MaterialTextView
    private lateinit var spinnerMorning: MaterialTextView
    private lateinit var spinnerAfternoon: MaterialTextView
    private lateinit var spinnerEvening: MaterialTextView
    private lateinit var spinnerAnytime: MaterialTextView
    private lateinit var txtTitleMorning: MaterialTextView
    private lateinit var txtSubtitleMorning: MaterialTextView
    private lateinit var txtTitleAnytime: MaterialTextView
    private lateinit var txtSubtitleAnytime: MaterialTextView
    private lateinit var txtTitleEvening: MaterialTextView
    private lateinit var txtSubtitleEvening: MaterialTextView
    private lateinit var txtTitleAfternoon: MaterialTextView
    private lateinit var rltBtnAfternoon: RelativeLayout
    private lateinit var txtSubtitleAfternoon: MaterialTextView
    private lateinit var spinnerArrow: AppCompatImageView
    private lateinit var calendarView: CalendarView
    private lateinit var rltBtnMorning: RelativeLayout
    private lateinit var rltBtnAnytime: RelativeLayout
    private lateinit var rltBtnEvening: RelativeLayout
    private var sessionManager: SessionManager? = null


    private var isSpinnerOpen = false
    private var taskCreateActivity: TaskCreateActivity? = null
    private var cyear = 0
    private var cmonth = 0
    private var cday = 0
    private var str_due_date: String? = null
    private var txtDate: String? = ""
    private var cbMorning = false
    private var cbAnyTime = false
    private var cbAfternoon = false
    private var cbEvening = false
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_task_date_time, container, false)
        taskCreateActivity = requireActivity() as TaskCreateActivity
        task = TaskModel()
        task!!.dueDate = Tools.getDayMonthDateTimeFormat(requireArguments().getString("DUE_DATE"))
        task!!.dueTime = requireArguments().getParcelable("DUE_TIME")
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setIDS()
        onViewClick()
        sessionManager = SessionManager(taskCreateActivity)
        pushEvent(
            EventTitles.N_PAGE_VIEW_PJ_DATE.key, bundleOf(
                "usr_name" to sessionManager!!.userAccount.name,
                "usr_id" to sessionManager!!.userAccount.id,
                "email" to sessionManager!!.userAccount.email,
                "phone_number" to sessionManager!!.userAccount.mobile,
                "title" to "post a job, time",
                "description" to ""
            )
        )
        cardDateTime.outlineProvider = ViewOutlineProvider.BACKGROUND
        cardDetails.setOnClickListener { v: View? ->
            when (validationCode) {
                0 -> {
                    operationsListener.onBackClickDateTime(
                        Tools.getDayMonthDateTimeFormat(txtDate!!.trim { it <= ' ' }),
                        dueTimeModel
                    )
                    operationsListener.onValidDataFilledDateTimeBack()
                }
                1 -> {
                    operationsListener.onBackClickDateTime(
                        Tools.getDayMonthDateTimeFormat(txtDate!!.trim { it <= ' ' }),
                        dueTimeModel
                    )
                    operationsListener.onValidDataFilledDateTimeBack()
                }
                2 -> {
                    operationsListener.onBackClickDateTime(
                        Tools.getDayMonthDateTimeFormat(txtDate!!.trim { it <= ' ' }),
                        dueTimeModel
                    )
                    operationsListener.onValidDataFilledDateTimeBack()
                }
            }
        }
        val calendar = Calendar.getInstance()
        cyear = calendar[Calendar.YEAR]
        cmonth = calendar[Calendar.MONTH]
        cday = calendar[Calendar.DAY_OF_MONTH]
        edtTimeSpinner.addTextChangedListener(this)
        DatePickerDialog.OnDateSetListener { view1: DatePicker?, year: Int, month: Int, dayOfMonth: Int ->
            val lMonth = month + 1
            str_due_date = Tools.getDayMonthDateTimeFormat("$year-$lMonth-$dayOfMonth")
            txtDate = str_due_date
        }
        txtDate = task!!.dueDate
        if (task!!.dueTime != null) {
            cbMorning = task!!.dueTime.morning
            cbAfternoon = task!!.dueTime.afternoon
            cbEvening = task!!.dueTime.evening
            cbAnyTime = task!!.dueTime.anytime
            setDueTime()
        } else {
            cbMorning = false
            cbAnyTime = false
            cbAfternoon = false
            cbEvening = false
        }

        selectDateTimeBtn()
        taskCreateActivity!!.setActionDraftDateTime(object : ActionDraftDateTime {
            override fun callDraftTaskDateTime(taskModel: TaskModel?) {
                if (taskModel!!.dueDate != null) {
                    operationsListener.draftTaskDateTime(taskModel, true)
                } else {
                    if (!TextUtils.isEmpty(txtDate!!.trim { it <= ' ' }) && !checkDateTodayOrOnwords()) {
                        taskModel.dueDate =
                            Tools.getDayMonthDateTimeFormat(txtDate!!.trim { it <= ' ' })
                    }
                    taskModel.dueTime = dueTimeModel
                    operationsListener.draftTaskDateTime(taskModel, false)
                }
            }
        })
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
        edtTimeSpinner.setOnClickListener { v: View? ->
            if (isSpinnerOpen) {
                hideSpinner()
            } else {
                showSpinner()
            }
        }
        try {
            confDate()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setDueTime() {
        when {
            cbMorning -> edtTimeSpinner.setText(R.string.morning_s)
            cbAfternoon -> edtTimeSpinner.setText(R.string.afternoon_s)
            cbEvening -> edtTimeSpinner.setText(R.string.evening_s)
            cbAnyTime -> edtTimeSpinner.setText(R.string.anytime_s)
        }
    }

    private fun setIDS() {
        bottomSheet = requireView().findViewById(R.id.bottom_sheet)
        imgDetails = requireView().findViewById(R.id.img_details)
        txtDetails = requireView().findViewById(R.id.txt_details)
        imgDateTime = requireView().findViewById(R.id.img_date_time)
        txtDateTime = requireView().findViewById(R.id.txt_date_time)
        imgBudget = requireView().findViewById(R.id.img_budget)
        txtBudget = requireView().findViewById(R.id.txt_budget)
        lytBtnDetails = requireView().findViewById(R.id.lyt_btn_details)
        lytBntDateTime = requireView().findViewById(R.id.lyt_bnt_date_time)
        cardDateTime = requireView().findViewById(R.id.card_date_time)
        cardDetails = requireView().findViewById(R.id.card_details)
        lytBtnBudget = requireView().findViewById(R.id.lyt_btn_budget)
        btnNext = requireView().findViewById(R.id.lyt_btn_next)
        spinnerItems = requireView().findViewById(R.id.spinner_items)
        edtTimeSpinner = requireView().findViewById(R.id.edt_time_spinner)
        spinnerMorning = requireView().findViewById(R.id.spinner_morning)
        spinnerAfternoon = requireView().findViewById(R.id.spinner_afternoon)
        spinnerEvening = requireView().findViewById(R.id.spinner_evening)
        spinnerAnytime = requireView().findViewById(R.id.spinner_anytime)
        spinnerArrow = requireView().findViewById(R.id.spinner_arrow)
        calendarView = requireView().findViewById(R.id.calenderView)
        txtTitleMorning = requireView().findViewById(R.id.txt_title_morning)
        txtSubtitleMorning = requireView().findViewById(R.id.txt_subtitle_morning)
        rltBtnMorning = requireView().findViewById(R.id.rlt_btn_morning)
        txtTitleAnytime = requireView().findViewById(R.id.txt_title_anytime)
        txtSubtitleAnytime = requireView().findViewById(R.id.txt_subtitle_anytime)
        rltBtnAnytime = requireView().findViewById(R.id.rlt_btn_anytime)
        txtTitleAfternoon = requireView().findViewById(R.id.txt_title_afternoon)
        rltBtnAfternoon = requireView().findViewById(R.id.rlt_btn_afternoon)
        txtSubtitleAfternoon = requireView().findViewById(R.id.txt_subtitle_afternoon)
        txtTitleEvening = requireView().findViewById(R.id.txt_title_evening)
        txtSubtitleEvening = requireView().findViewById(R.id.txt_subtitle_evening)
        rltBtnEvening = requireView().findViewById(R.id.rlt_btn_evening)
        lyt_btn_back = requireView().findViewById(R.id.lyt_btn_back)
    }

    private fun onViewClick() {
        txtTitleMorning.setOnClickListener {
            cbMorning = !cbMorning
        }
        txtSubtitleMorning.setOnClickListener {
            cbMorning = !cbMorning
        }
        rltBtnMorning.setOnClickListener {
            cbMorning = !cbMorning
        }
        txtTitleAnytime.setOnClickListener {
            cbAnyTime = !cbAnyTime
        }
        txtSubtitleAnytime.setOnClickListener {
            cbAnyTime = !cbAnyTime
        }
        rltBtnAnytime.setOnClickListener {
            cbAnyTime = !cbAnyTime
        }
        txtTitleAfternoon.setOnClickListener {
            cbAfternoon = !cbAfternoon
        }
        txtSubtitleAfternoon.setOnClickListener {
            cbAfternoon = !cbAfternoon
        }
        rltBtnAfternoon.setOnClickListener {
            cbAfternoon = !cbAfternoon
        }
        txtTitleEvening.setOnClickListener {
            cbEvening = !cbEvening
        }
        rltBtnEvening.setOnClickListener {
            cbEvening = !cbEvening
        }
        txtSubtitleEvening.setOnClickListener {
            cbEvening = !cbEvening
        }
        lyt_btn_back.setOnClickListener {
            when (validationCode) {
                0 -> {
                    operationsListener.onBackClickDateTime(
                        Tools.getDayMonthDateTimeFormat(txtDate!!.trim { it <= ' ' }),
                        dueTimeModel
                    )
                    operationsListener.onValidDataFilledDateTimeBack()
                }
                1 -> {
                    operationsListener.onBackClickDateTime(
                        Tools.getDayMonthDateTimeFormat(txtDate!!.trim { it <= ' ' }),
                        dueTimeModel
                    )
                    operationsListener.onValidDataFilledDateTimeBack()
                }
                2 -> {
                    operationsListener.onBackClickDateTime(
                        Tools.getDayMonthDateTimeFormat(txtDate!!.trim { it <= ' ' }),
                        dueTimeModel
                    )
                    operationsListener.onValidDataFilledDateTimeBack()
                }
            }
        }
        btnNext.setOnClickListener {
            when (validationCode) {
                0 -> {
                    //success
                    pushEvent(EventTitles.N_CLICK_PJ_DATE_NEXT.key, bundleOf(
                        "usr_name" to sessionManager!!.userAccount.name,
                        "usr_id" to sessionManager!!.userAccount.id,
                        "email" to sessionManager!!.userAccount.email,
                        "phone_number" to sessionManager!!.userAccount.mobile,
                        "job_date" to txtDate!!.trim { it <= ' ' },
                        "job_time" to dueTimeModel.toString()
                    ))
                    operationsListener.onNextClickDateTime(
                        txtDate!!.trim { it <= ' ' },
                        dueTimeModel
                    )
                    operationsListener.onValidDataFilledDateTimeNext()
                }
                1 -> {
                }
                2 -> taskCreateActivity!!.showToast("Select Due time", taskCreateActivity)
                3 -> taskCreateActivity!!.showToast("Due Date is old", taskCreateActivity)
            }
        }
    }

    private val dueTimeModel: DueTimeModel
        get() {
            val dueTimeModel = DueTimeModel()
            dueTimeModel.morning = cbMorning
            dueTimeModel.afternoon = cbAfternoon
            dueTimeModel.evening = cbEvening
            dueTimeModel.anytime = cbAnyTime
            if (!cbMorning && !cbEvening && !cbAfternoon && !cbAnyTime){
                dueTimeModel.anytime = true
            }
            return dueTimeModel
        }
    private val validationCode: Int
        get() {
            if (TextUtils.isEmpty(txtDate!!.trim { it <= ' ' })) {
                return 1
            } /*else if (!cbMorning && !cbEvening && !cbAfternoon && !cbAnyTime) {
                return 2
            }*/ else if (checkDateTodayOrOnwords()) {
                return 3
            }
            return 0
        }

    private fun checkDateTodayOrOnwords(): Boolean {
        val selectedDate = txtDate!!.trim { it <= ' ' }
        val rightNow = Calendar.getInstance()
        val rightNowTime = rightNow.time
        val currentHourIn24Format = rightNow[Calendar.HOUR_OF_DAY]
        println("Current time => $rightNowTime")
        val df = SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault())
        val formattedRightNowDate = df.format(rightNowTime)
        if (Tools.getTimeStamp(selectedDate) == Tools.getTimeStamp(formattedRightNowDate)) {
            if (cbMorning) {
                if (currentHourIn24Format >= 12) return true
            }
            if (cbAfternoon) {
                if (currentHourIn24Format >= 18) return true
            }
        }
        return Tools.getTimeStamp(selectedDate) < Tools.getTimeStamp(formattedRightNowDate)
    }

    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
    override fun afterTextChanged(s: Editable) {
        if (validationCode == 0) {
            (activity as TaskCreateActivity?)!!.isDateTimeComplete = true
            btnNext.setBackgroundDrawable(
                ContextCompat.getDrawable(
                    requireActivity(),
                    R.drawable.shape_rounded_back_button_active
                )!!
            )
        } else {
            (activity as TaskCreateActivity?)!!.isDateTimeComplete = false
            btnNext.setBackgroundDrawable(
                ContextCompat.getDrawable(
                    requireActivity(),
                    R.drawable.shape_rounded_back_button_deactive
                )!!
            )
        }
    }

    interface OperationsListener {
        fun onNextClickDateTime(due_date: String?, dueTimeModel: DueTimeModel?)
        fun onBackClickDateTime(due_date: String?, dueTimeModel: DueTimeModel?)
        fun onValidDataFilledDateTimeNext()
        fun onValidDataFilledDateTimeBack()
        fun draftTaskDateTime(taskModel: TaskModel?, moveForword: Boolean)
    }

    override fun onDestroy() {
        Timber.e("destory")
        super.onDestroy()
    }

    override fun onDestroyView() {
        Timber.e("destoryview")
        super.onDestroyView()
    }

    private fun confDate() {
        str_due_date = Tools.getDayMonthDateTimeFormat("$cyear-$cmonth-$cday")
        txtDate = str_due_date
        calendarView.minDate = System.currentTimeMillis()
        val c = Calendar.getInstance()
        c.add(Calendar.MONTH, 2) // subtract 2 years from now
        calendarView.maxDate = c.timeInMillis
        val calendar = Calendar.getInstance()
        cyear = calendar[Calendar.YEAR]
        cmonth = calendar[Calendar.MONTH]
        cmonth += 1
        cday = calendar[Calendar.DAY_OF_MONTH]
        str_due_date = Tools.getDayMonthDateTimeFormat("$cyear-$cmonth-$cday")
        txtDate = str_due_date
        if (validationCode == 0) {
            (activity as TaskCreateActivity?)!!.isDateTimeComplete = true
            btnNext.setBackgroundDrawable(
                ContextCompat.getDrawable(
                    requireActivity(),
                    R.drawable.shape_rounded_back_button_active
                )!!
            )
        } else {
            (activity as TaskCreateActivity?)!!.isDateTimeComplete = false
            btnNext.setBackgroundDrawable(
                ContextCompat.getDrawable(
                    requireActivity(),
                    R.drawable.shape_rounded_back_button_deactive
                )!!
            )
        }
        calendarView.setOnDateChangeListener { arg0: CalendarView?, year: Int, month: Int, date: Int ->
            cmonth = month + 1
            cyear = year
            cday = date
            str_due_date = Tools.getDayMonthDateTimeFormat("$cyear-$cmonth-$cday")
            txtDate = str_due_date
            if (validationCode == 0) {
                (activity as TaskCreateActivity?)!!.isDateTimeComplete = true
                btnNext.setBackgroundDrawable(
                    ContextCompat.getDrawable(
                        requireActivity(),
                        R.drawable.shape_rounded_back_button_active
                    )!!
                )
            } else {
                (activity as TaskCreateActivity?)!!.isDateTimeComplete = false
                btnNext.setBackgroundDrawable(
                    ContextCompat.getDrawable(
                        requireActivity(),
                        R.drawable.shape_rounded_back_button_deactive
                    )!!
                )
            }
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun selectDateTimeBtn() {
        val csl_primary =
            AppCompatResources.getColorStateList(requireContext(), R.color.colorPrimary)
        imgDateTime.imageTintList = csl_primary
        imgDateTime.setImageDrawable(resources.getDrawable(R.drawable.ic_date_time_big))
        txtDateTime.setTextColor(resources.getColor(R.color.colorPrimary))
        val face = ResourcesCompat.getFont(requireActivity(), R.font.roboto_medium)
        txtDateTime.typeface = face
        val csl_grey = AppCompatResources.getColorStateList(requireContext(), R.color.greyC4C4C4)
        val csl_green = AppCompatResources.getColorStateList(requireContext(), R.color.green)
        imgDetails.imageTintList = csl_green
        imgBudget.imageTintList = csl_grey
        txtDetails.setTextColor(resources.getColor(R.color.green))
        txtBudget.setTextColor(resources.getColor(R.color.colorGrayC9C9C9))
        tabClickListener()
    }

    fun checkTabs() {
        if ((activity as TaskCreateActivity?)!!.isBudgetComplete) {
            val csl_green = AppCompatResources.getColorStateList(requireContext(), R.color.green)
            imgBudget.imageTintList = csl_green
            txtBudget.setTextColor(resources.getColor(R.color.green))
        }
    }

    private fun tabClickListener() {
//        if (getActivity() != null && getValidationCode() == 0) {
//            lytBtnDetails.setOnClickListener(v -> ((TaskCreateActivity) requireActivity()).onViewClicked(v));
//            lytBntDateTime.setOnClickListener(v -> ((TaskCreateActivity) requireActivity()).onViewClicked(v));
//            lytBtnBudget.setOnClickListener(v -> ((TaskCreateActivity) requireActivity()).onViewClicked(v));
//        }
    }

    private fun showSpinner() {
        edtTimeSpinner.setTextColor(ContextCompat.getColor(requireActivity(), R.color.P300))
        spinnerArrow.setImageDrawable(
            ContextCompat.getDrawable(
                requireActivity(),
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
                requireActivity(),
                R.drawable.ic_d_arrow_g
            )
        )
        edtTimeSpinner.setTextColor(ContextCompat.getColor(requireActivity(), R.color.N9001))
        spinnerItems.visibility = View.GONE
    }

    companion object {
        fun newInstance(
            due_date: String?,
            due_time: DueTimeModel?,
            operationsListener: OperationsListener?
        ): TaskDateTimeFragment {
            val args = Bundle()
            args.putString("DUE_DATE", due_date)
            args.putParcelable("DUE_TIME", due_time)
            val fragment = TaskDateTimeFragment()
            fragment.operationsListener = operationsListener!!
            fragment.arguments = args
            return fragment
        }
    }
}