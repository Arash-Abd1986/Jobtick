package com.jobtick.android.material.ui.landing

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.jobtick.android.R
import com.jobtick.android.activities.DashboardActivity
import com.jobtick.android.models.UserAccountModel
import com.jobtick.android.utils.SessionManager

class LoginStatusFragment : Fragment() {
    private lateinit var sessionManagerA: SessionManager
    private lateinit var activity: OnboardingActivity
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login_status, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sessionManagerA = SessionManager(requireContext())
        activity = (requireActivity() as OnboardingActivity)
        try {
            if(activity.intent.getStringExtra("signin").equals("sss")) {

                activity.navController.navigate(R.id.signInFragment)
                return
            }
        }catch (e: Exception){
            Log.d("errorinex", e.toString())
        }

        when {
            sessionManagerA.userAccount?.account_status?.isBasic_info == true -> {
                dashboard(sessionManagerA.userAccount)
                activity.finish()
            }
            !sessionManagerA.onBoardingStatus -> {
                activity.navController.navigate(R.id.startFragmentSlider)
            }
            sessionManagerA.needSignIN -> {
                sessionManagerA.needSignIN = false
                activity.navController.navigate(R.id.signUpFragment)
            }

            else -> {
                startActivity(Intent(requireActivity(), DashboardActivity::class.java))
                activity.finish()
            }
        }
    }

    private fun dashboard(userAccountModel: UserAccountModel) {
        val intent = Intent(requireActivity(), DashboardActivity::class.java)
        sessionManagerA.userAccount = userAccountModel
        sessionManagerA.latitude = userAccountModel.latitude.toString()
        sessionManagerA.longitude = userAccountModel.longitude.toString()
        sessionManagerA.login = true
        openActivity(intent)

    }

    private fun openActivity(intent: Intent) {
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }
}