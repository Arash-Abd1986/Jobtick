package com.jobtick.android.material.ui.postajob

import android.os.Bundle
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CalendarView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.button.MaterialButton
import com.jobtick.android.R
import com.jobtick.android.network.coroutines.ApiHelper
import com.jobtick.android.network.retrofit.ApiClient
import com.jobtick.android.utils.SessionManager
import com.jobtick.android.utils.Tools
import com.jobtick.android.viewmodel.PostAJobViewModel
import com.jobtick.android.viewmodel.ViewModelFactory
import java.util.*

class PostAJobDateFragment : Fragment() {


    private lateinit var activity: PostAJobActivity
    private lateinit var next: MaterialButton
    private lateinit var sessionManagerA: SessionManager
    private lateinit var viewModel: PostAJobViewModel
    private lateinit var calenderView: CalendarView
    private lateinit var date: Date
    private var year = 0
    private var month = 0
    private var day = 0

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_post_a_job_date, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initVars()
        initVM()
    }

    private fun initVM() {
        sessionManagerA = SessionManager(requireContext())

        viewModel = ViewModelProvider(
                requireActivity(),
                ViewModelFactory(ApiHelper(ApiClient.getClientV2(sessionManagerA)))
        ).get(PostAJobViewModel::class.java)

    }

    private fun initVars() {

        activity = (requireActivity() as PostAJobActivity)
        calenderView = requireView().findViewById(R.id.calenderView)
        calenderView.minDate = System.currentTimeMillis()
        date = Date()
        next = requireView().findViewById(R.id.btn_next)
        next.setOnClickListener {
            activity.navController.navigate(R.id.postAJobTimeFragment)
        }
        calenderView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            this.day = dayOfMonth
            this.month = month + 1
            this.year = year
            next.isEnabled = true
            viewModel.setDate(PostAJobViewModel.PostAJobDate(day, month,
                    year, Tools.getDayMonthDateTimeFormat("${year}-${month + 1}-$dayOfMonth")))
        }
        calenderView.date = date.time
        day = Integer.parseInt(DateFormat.format("dd", date.time) as String)  // 20
        month = Integer.parseInt(DateFormat.format("MM", date.time) as String)  // 06
        year = Integer.parseInt(DateFormat.format("yyyy", date.time) as String)  // 2013

    }

}