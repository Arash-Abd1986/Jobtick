package com.jobtick.android.material.ui.filter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.android.material.radiobutton.MaterialRadioButton
import com.jobtick.android.R
import com.jobtick.android.activities.FiltersActivity
import com.jobtick.android.network.coroutines.ApiHelper
import com.jobtick.android.network.retrofit.ApiClient
import com.jobtick.android.utils.SessionManager
import com.jobtick.android.viewmodel.PostAJobViewModel
import com.jobtick.android.viewmodel.ViewModelFactory
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class JobTypeFragment : Fragment() {
    private lateinit var viewModel: PostAJobViewModel
    private lateinit var sessionManager: SessionManager
    private lateinit var back: AppCompatImageView
    private lateinit var radioInPerson: MaterialRadioButton
    private lateinit var radioRemote: MaterialRadioButton
    private lateinit var radioBoth: MaterialRadioButton
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_job_type, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initVars()
        initVM()
    }

    private fun initVars() {

        sessionManager = SessionManager(requireContext())
        back = requireView().findViewById(R.id.back)
        radioInPerson = requireView().findViewById(R.id.radioInPerson)
        radioRemote = requireView().findViewById(R.id.radioRemote)
        radioBoth = requireView().findViewById(R.id.radioBoth)
        back.setOnClickListener {
            (requireActivity() as FiltersActivity).hideFragment()
        }
        radioInPerson.setOnClickListener {
            radioRemote.isChecked = false
            radioBoth.isChecked = false
            viewModel.setJobType(PostAJobViewModel.JobType.IN_PERSON)
            (requireActivity() as FiltersActivity).hideFragment()
        }
        radioRemote.setOnClickListener {
            radioInPerson.isChecked = false
            radioBoth.isChecked = false
            viewModel.setJobType(PostAJobViewModel.JobType.REMOTE)
            (requireActivity() as FiltersActivity).hideFragment()
        }
        radioBoth.setOnClickListener {
            radioInPerson.isChecked = false
            radioRemote.isChecked = false
            viewModel.setJobType(PostAJobViewModel.JobType.BOTH)
            (requireActivity() as FiltersActivity).hideFragment()
        }
    }


    private fun initVM() {
        viewModel = ViewModelProvider(
                requireActivity(),
                ViewModelFactory(ApiHelper(ApiClient.getClientV2(sessionManager)))
        ).get(PostAJobViewModel::class.java)
        lifecycleScope.launch {
            viewModel.state.collectLatest { state ->
                when (state.jobType) {
                    PostAJobViewModel.JobType.IN_PERSON -> {
                        radioRemote.isChecked = false
                        radioBoth.isChecked = false
                        radioInPerson.isChecked = true
                    }
                    PostAJobViewModel.JobType.REMOTE -> {
                        radioInPerson.isChecked = false
                        radioBoth.isChecked = false
                        radioRemote.isChecked = true
                    }
                    PostAJobViewModel.JobType.BOTH -> {
                        radioInPerson.isChecked = false
                        radioRemote.isChecked = false
                        radioBoth.isChecked = true
                    }
                }
            }
        }
    }
}