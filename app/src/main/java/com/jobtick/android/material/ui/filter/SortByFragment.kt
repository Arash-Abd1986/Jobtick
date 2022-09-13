package com.jobtick.android.material.ui.filter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.radiobutton.MaterialRadioButton
import com.jobtick.android.R
import com.jobtick.android.activities.FiltersActivity
import com.jobtick.android.network.coroutines.ApiHelper
import com.jobtick.android.network.retrofit.ApiClient
import com.jobtick.android.utils.SessionManager
import com.jobtick.android.viewmodel.PostAJobViewModel
import com.jobtick.android.viewmodel.ViewModelFactory

class SortByFragment : Fragment() {
    private lateinit var viewModel: PostAJobViewModel
    private lateinit var sessionManager: SessionManager
    private lateinit var back: AppCompatImageView
    private lateinit var radioPrice: MaterialRadioButton
    private lateinit var radioDueDate: MaterialRadioButton
    private lateinit var radioNearBy: MaterialRadioButton
    private lateinit var radioAscending: MaterialRadioButton
    private lateinit var radioDescending: MaterialRadioButton
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_sort_by, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initVars()
        initVM()
    }

    private fun initVars() {

        sessionManager = SessionManager(requireContext())
        back = requireView().findViewById(R.id.back)
        radioPrice = requireView().findViewById(R.id.radioPrice)
        radioDueDate = requireView().findViewById(R.id.radioDueDate)
        radioNearBy = requireView().findViewById(R.id.radioNearBy)
        radioAscending = requireView().findViewById(R.id.radioAscending)
        radioDescending = requireView().findViewById(R.id.radioDescending)
        back.setOnClickListener {
            (requireActivity() as FiltersActivity).hideFragment()
        }
        radioAscending.isChecked = true
        radioPrice.isChecked = true
        radioPrice.setOnClickListener {
            radioDueDate.isChecked = false
            radioNearBy.isChecked = false
            viewModel.setSortType(PostAJobViewModel.SortType.PRICE)
            (requireActivity() as FiltersActivity).hideFragment()
        }
        radioDueDate.setOnClickListener {
            radioPrice.isChecked = false
            radioNearBy.isChecked = false
            viewModel.setSortType(PostAJobViewModel.SortType.DUE_DATE)
            (requireActivity() as FiltersActivity).hideFragment()
        }
        radioNearBy.setOnClickListener {
            radioPrice.isChecked = false
            radioDueDate.isChecked = false
            viewModel.setSortType(PostAJobViewModel.SortType.NEARBY_ME)
            (requireActivity() as FiltersActivity).hideFragment()
        }
        radioAscending.setOnClickListener {
            radioDescending.isChecked = false
            viewModel.setIsAscending(true)
            (requireActivity() as FiltersActivity).hideFragment()
        }
        radioDescending.setOnClickListener {
            radioAscending.isChecked = false
            viewModel.setIsAscending(false)
            (requireActivity() as FiltersActivity).hideFragment()
        }
    }


    private fun initVM() {
        viewModel = ViewModelProvider(
                requireActivity(),
                ViewModelFactory(ApiHelper(ApiClient.getClientV2(sessionManager)))
        ).get(PostAJobViewModel::class.java)
    }
}