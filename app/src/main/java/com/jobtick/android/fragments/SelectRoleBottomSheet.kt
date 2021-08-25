package com.jobtick.android.fragments

import android.content.Context
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import android.widget.TextView
import android.widget.RadioButton
import android.os.Bundle
import com.jobtick.android.R
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import java.lang.ClassCastException
import java.lang.IllegalStateException

class SelectRoleBottomSheet : BottomSheetDialogFragment(), View.OnClickListener {
    var title: TextView? = null
    var getStartedButton: Button? = null
    var cbPoster: RadioButton? = null
    var cbWorker: RadioButton? = null
    private var listener: NoticeListener? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.ThemeOverlay_BottomSheetDialog)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.bottom_sheet_select_role, container, false)
        title = view.findViewById(R.id.title)
        cbPoster = view.findViewById(R.id.cb_poster)
        cbWorker = view.findViewById(R.id.cb_worker)
        getStartedButton = view.findViewById(R.id.btn_get_started)
        cbWorker!!.setOnClickListener(this)
        cbPoster!!.setOnClickListener(this)
        getStartedButton!!.setOnClickListener { v: View? ->
            if (!validation()) return@setOnClickListener
            when {
                cbPoster!!.isChecked -> listener!!.onGetStartedClick("poster")
                cbWorker!!.isChecked -> listener!!.onGetStartedClick("worker")
                else -> {
                    throw IllegalStateException("In spite of validation, there is no role selected.")
                }
            }
            dismiss()
        }
        return view
    }

    private fun validation(): Boolean {
        if (!cbWorker!!.isChecked && !cbPoster!!.isChecked) {
            cbWorker!!.setBackgroundResource(R.drawable.radio_button_background_on_error)
            cbPoster!!.setBackgroundResource(R.drawable.radio_button_background_on_error)
            return false
        }
        return true
    }

    override fun onClick(view: View) {
        view.setBackgroundResource(R.drawable.radio_button_background)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = try {
            context as NoticeListener
        } catch (e: ClassCastException) {
            throw ClassCastException(this.toString()
                    + " must implement NoticeListener")
        }
    }

    interface NoticeListener {
        fun onGetStartedClick(role: String?)
    }
}