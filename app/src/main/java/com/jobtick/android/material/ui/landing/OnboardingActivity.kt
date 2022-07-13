package com.jobtick.android.material.ui.landing

import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import com.jobtick.android.R
import com.jobtick.android.activities.ActivityBase


class OnboardingActivity : ActivityBase() {
    lateinit var navController: NavController
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding)
        val navHostFragment =
            supportFragmentManager.findFragmentById(com.jobtick.android.R.id.nav_host) as NavHostFragment?
        navController = navHostFragment!!.navController
    }
}