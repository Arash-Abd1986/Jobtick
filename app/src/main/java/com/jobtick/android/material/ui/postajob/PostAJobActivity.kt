package com.jobtick.android.material.ui.postajob

import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.fragment.NavHostFragment
import com.google.android.material.textview.MaterialTextView
import com.jobtick.android.R
import com.jobtick.android.activities.ActivityBase
import com.jobtick.android.network.coroutines.ApiHelper
import com.jobtick.android.network.retrofit.ApiClient
import com.jobtick.android.utils.SessionManager
import com.jobtick.android.viewmodel.PostAJobViewModel
import com.jobtick.android.viewmodel.ViewModelFactory


class PostAJobActivity : ActivityBase() {
    lateinit var navController: NavController
    lateinit var viewModel: PostAJobViewModel
    private lateinit var sessionManager: SessionManager
    private lateinit var title: MaterialTextView
    private lateinit var linTitle: LinearLayout


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post_ajob)
        initVM()
        title = findViewById(R.id.title)
        linTitle = findViewById(R.id.linTitle)
        val navHostFragment =
                supportFragmentManager.findFragmentById(R.id.nav_host) as NavHostFragment?
        navController = navHostFragment!!.navController
        navController.addOnDestinationChangedListener { _, destination, _ ->
            linTitle.visibility = View.VISIBLE
            when (destination.id) {
                R.id.postAJobSetTitleFragment -> {
                    title.text = "Title"
                }
                R.id.postAJobAddLocationFragment -> {
                    title.text = "Location"
                }
                R.id.getLocationFragment -> {
                    linTitle.visibility = View.GONE
                }
                R.id.postAJobDateTimeFragment -> {
                    title.text = "Date & Time"
                }
                R.id.postAJobBudgetFragment -> {
                    title.text = "Budget"
                }
                R.id.postAJobDateFragment -> {
                    title.text = "Date"
                }
                R.id.postAJobTimeFragment -> {
                    title.text = "Time"
                }
                R.id.detailsFragment -> {
                    title.text = "Details"
                }
            }
        }
    }

    private fun initVM() {
        sessionManager = SessionManager(applicationContext)

        viewModel = ViewModelProvider(
                this,
                ViewModelFactory(ApiHelper(ApiClient.getClientV2(sessionManager)))
        ).get(PostAJobViewModel::class.java)
    }
}