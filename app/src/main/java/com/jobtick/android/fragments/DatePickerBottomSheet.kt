package com.jobtick.android.fragments

import android.os.Bundle
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CalendarView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.jobtick.android.R

class DatePickerBottomSheet(private val time: Long) : BottomSheetDialogFragment() {
    private var calenderView: CalendarView? = null
    private var btnAccept: Button? = null
    private var btnCancel: Button? = null
    private var year = 0
    private var month = 0
    private var day = 0
    var dchange: DateChange? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.bottom_sheet_date_picker, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        calenderView = view.findViewById(R.id.calenderView)
        btnAccept = view.findViewById(R.id.btn_accept)
        btnCancel = view.findViewById(R.id.btn_decline)
        calenderView!!.setOnDateChangeListener { _, year, month, dayOfMonth ->
            this.day = dayOfMonth
            this.month = month
            this.year = year
        }
        calenderView!!.date = time
        day = Integer.parseInt(DateFormat.format("dd", time) as String)  // 20
        month = Integer.parseInt(DateFormat.format("MM", time) as String)  // 06
        year = Integer.parseInt(DateFormat.format("yyyy", time) as String)  // 2013

        btnAccept!!.setOnClickListener {
            dchange!!.onDateChange(year, month, day)
            dismiss()
        }
        btnCancel!!.setOnClickListener {
            dismiss()
        }
    }

    interface DateChange {
        fun onDateChange(year: Int, month: Int, day: Int)
    }
}


