package com.jobtick.android.material.ui.postajob

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.jobtick.android.R
import com.jobtick.android.network.coroutines.ApiHelper
import com.jobtick.android.network.retrofit.ApiClient
import com.jobtick.android.utils.SessionManager
import com.jobtick.android.viewmodel.PostAJobViewModel
import com.jobtick.android.viewmodel.ViewModelFactory

class GetLocationFragment : Fragment() {
    private lateinit var viewModel: PostAJobViewModel
    private lateinit var sessionManager: SessionManager
    private lateinit var activity: PostAJobActivity

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_get_location, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initVars()
        initVM()
    }

    private fun initVars() {
        activity = (requireActivity() as PostAJobActivity)

        sessionManager = SessionManager(requireContext())

    }

    private fun initVM() {
        viewModel = ViewModelProvider(
                requireActivity(),
                ViewModelFactory(ApiHelper(ApiClient.getClientV2(sessionManager)))
        ).get(PostAJobViewModel::class.java)
        viewModel.setLocation("bla bla")
        activity.navController.popBackStack()

    }
}