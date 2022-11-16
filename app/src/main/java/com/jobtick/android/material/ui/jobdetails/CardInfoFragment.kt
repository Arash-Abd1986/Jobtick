package com.jobtick.android.material.ui.jobdetails

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.google.android.material.textview.MaterialTextView
import com.jobtick.android.R
import com.jobtick.android.network.coroutines.ApiHelper
import com.jobtick.android.network.retrofit.ApiClient
import com.jobtick.android.viewmodel.ViewModelFactory
import com.jobtick.android.viewmodel.home.PaymentOverviewViewModel


class CardInfoFragment : Fragment() {
    val args: CardInfoFragmentArgs by navArgs()
    lateinit var viewModel: PaymentOverviewViewModel
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_card_info, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireView().findViewById<MaterialTextView>(R.id.txt_CVC).text = args.cardInfo.CVC
        requireView().findViewById<MaterialTextView>(R.id.txt_date).text = args.cardInfo.expiryDate
        requireView().findViewById<MaterialTextView>(R.id.txt_CardNumber).text = args.cardInfo.cardNumber
        requireView().findViewById<AppCompatImageView>(R.id.back).setOnClickListener {
            requireActivity().onBackPressed()
        }
        initVm()
        requireView().findViewById<AppCompatImageView>(R.id.btn_delete).setOnClickListener {
            viewModel.setIsCardDeletedByMe(true)
            requireActivity().onBackPressed()
        }
    }

    private fun initVm() {
        viewModel = ViewModelProvider(requireActivity(), ViewModelFactory(ApiHelper(ApiClient.getClient())))[PaymentOverviewViewModel::class.java]
    }
}