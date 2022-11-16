package com.jobtick.android.material.ui.jobdetails

import android.os.Bundle
import android.widget.PopupWindow
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.jobtick.android.R
import com.jobtick.android.activities.ActivityBase
import com.jobtick.android.network.coroutines.ApiHelper
import com.jobtick.android.network.retrofit.ApiClient
import com.jobtick.android.utils.ConstantKey
import com.jobtick.android.utils.SessionManager
import com.jobtick.android.viewmodel.ViewModelFactory
import com.jobtick.android.viewmodel.home.PaymentOverviewViewModel


class PaymentOverviewActivity : ActivityBase() {
    lateinit var navController: NavController
    lateinit var viewModel: PaymentOverviewViewModel
    private lateinit var sessionManager: SessionManager
    var popupWindow: PopupWindow? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment_overview)
        val navHostFragment =
                supportFragmentManager.findFragmentById(R.id.nav_host) as NavHostFragment?
        navController = navHostFragment!!.navController
        initVars()
        initVm()
        setData()
        setGraph()
    }

    private fun setData() {
        val bundle = intent.extras
        viewModel.setPaymentData(bundle!!.getParcelable(ConstantKey.OFFER))
        viewModel.setFound(bundle.getString("found"))
        viewModel.setId(bundle.getString("id"))
    }

    private fun setGraph() {
        val navHostFragment =
                supportFragmentManager.findFragmentById(R.id.nav_host) as NavHostFragment?
        val inflater = navHostFragment?.navController?.navInflater
        val graph = inflater?.inflate(R.navigation.payment_graph)
        graph?.setStartDestination(R.id.paymentOverviewFragment)
        navHostFragment?.navController?.graph = graph!!
    }
    private fun initVars() {
        sessionManager = SessionManager(this)
    }

    private fun initVm() {
        viewModel = ViewModelProvider(
                this,
                ViewModelFactory(ApiHelper(ApiClient.getClient()))
        ).get(PaymentOverviewViewModel::class.java)
    }
}