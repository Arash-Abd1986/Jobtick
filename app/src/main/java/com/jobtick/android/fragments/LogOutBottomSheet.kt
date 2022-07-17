package com.jobtick.android.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton
import com.jobtick.android.R
import com.jobtick.android.activities.AuthActivity
import com.jobtick.android.activities.DashboardActivity
import com.jobtick.android.utils.SessionManager

class LogOutBottomSheet : BottomSheetDialogFragment() {
    private var sessionManager: SessionManager? = null
    var logout: MaterialButton? = null
    var cancel: MaterialButton? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.TransparentDialog)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        sessionManager = SessionManager(context)
        val view = inflater.inflate(R.layout.fragment_logout_bottom_sheet, container, false)
        logout = view.findViewById(R.id.logout)
        cancel = view.findViewById(R.id.cancel)
        logout!!.setOnClickListener {
            dismiss()
            sessionManager!!.userAccount = null
            sessionManager!!.login = false
            sessionManager!!.tokenType = null
            sessionManager!!.accessToken = null
            val intent = Intent(context, DashboardActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            requireActivity().finish()
        }
        cancel!!.setOnClickListener { dismiss() }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (activity == null) {
            return
        }
        initUi()
    }

    private fun initUi() {}

    companion object {
        fun newInstance(): LogOutBottomSheet {
            return LogOutBottomSheet()
        }
    }
}