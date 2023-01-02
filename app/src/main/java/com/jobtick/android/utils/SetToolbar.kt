package com.jobtick.android.utils

import android.app.Activity
import android.view.View
import androidx.navigation.findNavController
import com.google.android.material.appbar.MaterialToolbar
import com.jobtick.android.R
import com.jobtick.android.activities.DashboardActivity
import com.jobtick.android.databinding.HeaderNewDesignBinding

class SetToolbar(var activity: Activity, var title: String, var button: String, var backAction: Int?, var binding: HeaderNewDesignBinding?, var view: View) {
    init {
        activity.findViewById<MaterialToolbar>(R.id.toolbar).visibility = View.GONE
        if(title == "")
            binding!!.txtTitle.visibility = View.GONE
        else {
            binding!!.txtTitle.visibility = View.VISIBLE
            binding!!.txtTitle.text = title
        }
        if(button == "")
            binding!!.txtAction.visibility = View.GONE
        else {
            binding!!.txtAction.visibility = View.VISIBLE
            binding!!.txtAction.text = button
        }
        if(backAction == null)
            binding!!.back.visibility = View.GONE
        else
            binding!!.back.visibility = View.VISIBLE
//        binding!!.back.setOnClickListener {
//            if(activity is DashboardActivity) {
//                backAction?.let { it1 ->
//                    binding.root.findNavController().navigate(R.id.action_navigation_profile_account_to_navigation_profile)
////                    (activity as DashboardActivity).navController!!.navigate(
////                        it1
////                    )
//                }
//            }
//
//        }

    }
}