package com.jobtick.android.material.ui.landing

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.button.MaterialButton
import com.jobtick.android.R
import com.jobtick.android.activities.DashboardActivity
import com.jobtick.android.utils.Constant
import com.jobtick.android.utils.SessionManager

class StartFragmentSlider : Fragment(), ViewPagerAdapter.ItemClick {
    private lateinit var pager: ViewPager2
    private lateinit var signIn: MaterialButton
    private lateinit var imBack: AppCompatImageView
    private lateinit var activity: OnboardingActivity
    private lateinit var sessionManagerA: SessionManager


    private val role: ViewPagerAdapter.Role = ViewPagerAdapter.Role.POSTER_TICKER
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_start_slider, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val data = arrayOf("0", "1", "2")
        val next: MaterialButton = view.findViewById(R.id.next)
        val skip: MaterialButton = view.findViewById(R.id.skip)
        activity = (requireActivity() as OnboardingActivity)
        sessionManagerA = SessionManager(requireContext())
        sessionManagerA.onBoardingStatus = true
        signIn = view.findViewById(R.id.signIn)
        pager = view.findViewById(R.id.pager)
        imBack = view.findViewById(R.id.imBack)
        val adapter = ViewPagerAdapter(data)
        pager.adapter = adapter
        pager.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        pager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                when (position) {
                    0 -> {
                        imBack.setImageDrawable(
                            ContextCompat.getDrawable(
                                requireContext(),
                                R.drawable.back_start_slide1
                            )
                        )
                        next.text = "Next"
                    }
                    1 -> {
                        imBack.setImageDrawable(
                            ContextCompat.getDrawable(
                                requireContext(),
                                R.drawable.back_start_slide2
                            )
                        )
                        next.text = "Next"
                    }
                    2 -> {
                        imBack.setImageDrawable(
                            ContextCompat.getDrawable(
                                requireContext(),
                                R.drawable.back_start_slide3
                            )
                        )
                        next.text = "Get started"
                    }
                }

                super.onPageSelected(position)
            }
        })
        next.setOnClickListener {
            if (pager.currentItem == 2) {
                navigate(role)
            }
            pager.currentItem = pager.currentItem + 1
        }
        skip.setOnClickListener {
            navigate(role)
        }
        signIn.setOnClickListener {
            activity.navController.navigate(R.id.signInFragment)
        }
    }

    override fun buttonClick(type: ViewPagerAdapter.Role) {
        when (type) {
            ViewPagerAdapter.Role.POSTER -> {
                sessionManagerA.roleLocal = "poster"
            }
            else -> {
                sessionManagerA.roleLocal = "ticker"
            }
        }
    }

    private fun navigate(role: ViewPagerAdapter.Role) {
        val main = Intent(requireContext(), DashboardActivity::class.java)
        main.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        main.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        main.putExtra(Constant.ROLE, role.name)
        startActivity(main)
    }

}