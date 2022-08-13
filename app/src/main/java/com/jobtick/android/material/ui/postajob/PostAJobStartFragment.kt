package com.jobtick.android.material.ui.postajob

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.button.MaterialButton
import com.jobtick.android.R
import com.jobtick.android.network.coroutines.ApiHelper
import com.jobtick.android.network.retrofit.ApiClient
import com.jobtick.android.utils.SessionManager
import com.jobtick.android.viewmodel.PostAJobViewModel
import com.jobtick.android.viewmodel.ViewModelFactory

class PostAJobStartFragment : Fragment() {

    private lateinit var activity: PostAJobActivity
    private lateinit var newJob: MaterialButton
    private lateinit var resume: MaterialButton
    private lateinit var sessionManagerA: SessionManager
    private lateinit var viewModel: PostAJobViewModel


    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_post_a_job_start, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initVars()
    }


    private fun initVars() {
        sessionManagerA = SessionManager(requireContext())
        viewModel = ViewModelProvider(
                requireActivity(),
                ViewModelFactory(ApiHelper(ApiClient.getClientV2(sessionManagerA)))
        ).get(PostAJobViewModel::class.java)
        activity = (requireActivity() as PostAJobActivity)
        newJob = requireView().findViewById(R.id.btn_post_a_job)
        resume = requireView().findViewById(R.id.btn_resume)

        newJob.setOnClickListener {
           activity.navController.navigate(R.id.postAJobSetTitleFragment)
        }
        resume.setOnClickListener {
            activity.setData()
            activity.navController.navigate(R.id.postAJobSetTitleFragment)
        }

    }

}