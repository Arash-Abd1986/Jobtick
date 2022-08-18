package com.jobtick.android.material.ui.postajob

import android.os.Build
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ImageSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputLayout
import com.jobtick.android.R
import com.jobtick.android.network.coroutines.ApiHelper
import com.jobtick.android.network.retrofit.ApiClient
import com.jobtick.android.utils.SessionManager
import com.jobtick.android.utils.getShortAddress
import com.jobtick.android.viewmodel.PostAJobViewModel
import com.jobtick.android.viewmodel.ViewModelFactory
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.handleCoroutineException
import kotlinx.coroutines.launch

class PostAJobDateTimeFragment : Fragment() {


    private lateinit var activity: PostAJobActivity
    private lateinit var next: MaterialButton
    private lateinit var certain: MaterialButton
    private lateinit var flexible: MaterialButton
    private lateinit var sessionManagerA: SessionManager
    private lateinit var viewModel: PostAJobViewModel


    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_post_a_job_date_time, container, false)
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
                if (it.isFlexible == true) {
                    flexible.isChecked = true
                    next.isEnabled = true
                }
                if (it.isFlexible == false) {
                    certain.isChecked = true
                    next.isEnabled = true
                }
            }
        }
    }

    private fun initVars() {

        activity = (requireActivity() as PostAJobActivity)
        next = requireView().findViewById(R.id.btn_next)
        certain = requireView().findViewById(R.id.btnInperson)
        flexible = requireView().findViewById(R.id.btnRemote)
        next.setOnClickListener {
            viewModel.setIsFlexibleTime(flexible.isChecked)
            if (flexible.isChecked)
                activity.navController.navigate(R.id.postAJobBudgetFragment)
            else
                activity.navController.navigate(R.id.postAJobDateFragment)
        }

        certain.setOnClickListener {
            certain.isChecked = true
            flexible.isChecked = false
            next.isEnabled = true
        }
        flexible.setOnClickListener {
            flexible.isChecked = true
            certain.isChecked = false
            next.isEnabled = true
        }
    }

}