package com.jobtick.android.material.ui.postajob

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.jobtick.android.R
import com.jobtick.android.activities.ActivityBase
import com.jobtick.android.network.coroutines.ApiHelper
import com.jobtick.android.network.retrofit.ApiClient
import com.jobtick.android.utils.SessionManager
import com.jobtick.android.viewmodel.PostAJobViewModel
import com.jobtick.android.viewmodel.ViewModelFactory


class PostAJobActivity : ActivityBase() {
    lateinit var navController: NavController
    private lateinit var viewModel: PostAJobViewModel
    private lateinit var sessionManager: SessionManager


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post_ajob)
        initVM()
        val navHostFragment =
                supportFragmentManager.findFragmentById(R.id.nav_host) as NavHostFragment?
        navController = navHostFragment!!.navController
    }

    private fun initVM() {
        sessionManager = SessionManager(applicationContext)

        viewModel = ViewModelProvider(
                this,
                ViewModelFactory(ApiHelper(ApiClient.getClientV2(sessionManager)))
        ).get(PostAJobViewModel::class.java)
    }
}