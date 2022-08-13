package com.jobtick.android.material.ui.postajob

import android.graphics.Typeface
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.android.material.button.MaterialButton
import com.jobtick.android.R
import com.jobtick.android.network.coroutines.ApiHelper
import com.jobtick.android.network.retrofit.ApiClient
import com.jobtick.android.utils.CustomTypefaceSpan
import com.jobtick.android.utils.SessionManager
import com.jobtick.android.utils.setSpanStyledTwoLineText
import com.jobtick.android.viewmodel.PostAJobViewModel
import com.jobtick.android.viewmodel.ViewModelFactory
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class PostAJobTimeFragment : Fragment() {


    private lateinit var activity: PostAJobActivity
    private lateinit var next: MaterialButton
    private lateinit var sessionManagerA: SessionManager
    private lateinit var viewModel: PostAJobViewModel
    private lateinit var morning: MaterialButton
    private lateinit var afternoon: MaterialButton
    private lateinit var evening: MaterialButton
    private lateinit var anytime: MaterialButton


    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_post_a_job_time, container, false)
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
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.state.collectLatest {
                if (it.location != null) {

                }
            }
        }

    }

    private fun initVars() {

        activity = (requireActivity() as PostAJobActivity)
        next = requireView().findViewById(R.id.btn_next)
        morning = requireView().findViewById(R.id.morning)
        afternoon = requireView().findViewById(R.id.afternoon)
        evening = requireView().findViewById(R.id.evening)
        anytime = requireView().findViewById(R.id.anytime)
        morning.setSpanStyledTwoLineText("""Morning
            |Before 12:00 PM""".trimMargin(),requireContext(), breakLinePosition = 7)
        afternoon.setSpanStyledTwoLineText("""Afternoon
            |Between 12:00 PM to 06:00 PM""".trimMargin(),requireContext(), breakLinePosition = 9)
        evening.setSpanStyledTwoLineText("""Evening
            |After 06:00 PM""".trimMargin(),requireContext(), breakLinePosition = 7)
        anytime.setSpanStyledTwoLineText("""Anytime
            |You and Ticker agree on""".trimMargin(),requireContext(), breakLinePosition = 7)
        next.setOnClickListener {
            activity.navController.navigate(R.id.postAJobBudgetFragment)

        }

        morning.setOnClickListener {
            viewModel.setTime(PostAJobViewModel.PostAJobTime.MORNING)
            resetAll()
            morning.isChecked = true
        }
        afternoon.setOnClickListener {
            viewModel.setTime(PostAJobViewModel.PostAJobTime.AFTERNOON)
            resetAll()
            afternoon.isChecked = true
        }
        evening.setOnClickListener {
            viewModel.setTime(PostAJobViewModel.PostAJobTime.EVENING)
            resetAll()
            evening.isChecked = true
        }
        anytime.setOnClickListener {
            viewModel.setTime(PostAJobViewModel.PostAJobTime.ANY_TIME)
            resetAll()
            anytime.isChecked = true
        }

    }

    private fun resetAll() {
        morning.isChecked = false
        afternoon.isChecked = false
        evening.isChecked = false
        anytime.isChecked = false
    }


}