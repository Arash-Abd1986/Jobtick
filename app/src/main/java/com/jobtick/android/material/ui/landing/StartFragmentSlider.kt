package com.jobtick.android.material.ui.landing

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.button.MaterialButton
import com.jobtick.android.R
import com.jobtick.android.activities.DashboardActivity
import com.jobtick.android.utils.Constant
import com.jobtick.android.utils.SessionManager
import com.jobtick.android.utils.dpToPx
import com.jobtick.android.utils.pxToDp

class StartFragmentSlider : Fragment(), ViewPagerAdapter.ItemClick {
    private lateinit var pager: ViewPager2
    private lateinit var signIn: MaterialButton
    private lateinit var signUp: MaterialButton
    private lateinit var radioBtnOne: RadioButton
    private lateinit var radioBtnTwo: RadioButton
    private lateinit var radioBtnThree: RadioButton
    private lateinit var radioGroup: RadioGroup
    private lateinit var imBack: AppCompatImageView
    private lateinit var imBackClick: AppCompatImageView
    private lateinit var activity: OnboardingActivity
    private lateinit var sessionManagerA: SessionManager


    private var role: ViewPagerAdapter.Role = ViewPagerAdapter.Role.POSTER_TICKER
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
        signUp = view.findViewById(R.id.signUp)
        pager = view.findViewById(R.id.pager)
        imBack = view.findViewById(R.id.imBack)
        imBackClick = view.findViewById(R.id.back)
        radioGroup = view.findViewById(R.id.radioGroup)
        radioBtnOne = view.findViewById(R.id.radioBtnOne)
        radioBtnTwo = view.findViewById(R.id.radioBtnTwo)
        radioBtnThree = view.findViewById(R.id.radioBtnThree)
        radioBtnOne.layoutParams = radioBtnOne.layoutParams.also { layoutParams ->
            layoutParams.width = (24).dpToPx()
        }
        radioBtnTwo.layoutParams = radioBtnTwo.layoutParams.also { layoutParams ->
            layoutParams.width = (8).dpToPx()
        }
        radioBtnThree.layoutParams = radioBtnThree.layoutParams.also { layoutParams ->
            layoutParams.width = (8).dpToPx()
        }
        val adapter = ViewPagerAdapter(data)
        adapter.itemClick = this
        pager.adapter = adapter
        pager.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        imBackClick.setOnClickListener {
            pager.currentItem = pager.currentItem - 1
        }
        pager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {

                radioGroup.check(radioGroup.getChildAt(position).id);
                imBackClick.visibility = View.VISIBLE
                when (position) {
                    0 -> {
                        radioBtnOne.layoutParams = radioBtnOne.layoutParams.also { layoutParams ->
                            layoutParams.width = (24).dpToPx()
                        }
                        radioBtnTwo.layoutParams = radioBtnTwo.layoutParams.also { layoutParams ->
                            layoutParams.width = (8).dpToPx()
                        }
                        radioBtnThree.layoutParams = radioBtnThree.layoutParams.also { layoutParams ->
                            layoutParams.width = (8).dpToPx()
                        }
                        imBack.setImageDrawable(
                                ContextCompat.getDrawable(
                                        requireContext(),
                                        R.drawable.back_start_slide1
                                )
                        )
                        next.text = "Next"
                        imBackClick.visibility = View.GONE
                        next.visibility = View.VISIBLE
                        signUp.visibility = View.GONE
                    }
                    1 -> {
                        radioBtnOne.layoutParams = radioBtnOne.layoutParams.also { layoutParams ->
                            layoutParams.width = (8).dpToPx()
                        }
                        radioBtnTwo.layoutParams = radioBtnTwo.layoutParams.also { layoutParams ->
                            layoutParams.width = (24).dpToPx()
                        }
                        radioBtnThree.layoutParams = radioBtnThree.layoutParams.also { layoutParams ->
                            layoutParams.width = (8).dpToPx()
                        }
                        imBack.setImageDrawable(
                                ContextCompat.getDrawable(
                                        requireContext(),
                                        R.drawable.back_start_slide2
                                )
                        )
                        next.text = "Next"
                        next.visibility = View.VISIBLE
                        signUp.visibility = View.GONE
                    }
                    2 -> {
                        radioBtnOne.layoutParams = radioBtnOne.layoutParams.also { layoutParams ->
                            layoutParams.width = (8).dpToPx()
                        }
                        radioBtnTwo.layoutParams = radioBtnTwo.layoutParams.also { layoutParams ->
                            layoutParams.width = (8).dpToPx()
                        }
                        radioBtnThree.layoutParams = radioBtnThree.layoutParams.also { layoutParams ->
                            layoutParams.width = (24).dpToPx()
                        }
                        imBack.setImageDrawable(
                                ContextCompat.getDrawable(
                                        requireContext(),
                                        R.drawable.back_start_slide3
                                )
                        )
                        next.text = "Get started"
                        next.visibility = View.GONE
                        signUp.visibility = View.VISIBLE
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
        signUp.setOnClickListener {
            activity.navController.navigate(R.id.signUpFragment)
        }
    }

    override fun buttonClick(type: ViewPagerAdapter.Role) {
        role = type

        when (type) {
            ViewPagerAdapter.Role.POSTER -> {
                sessionManagerA.roleLocal = "poster"
            }
            else -> {
                sessionManagerA.roleLocal = "ticker"
            }
        }
        navigate(role)
    }

    private fun navigate(role: ViewPagerAdapter.Role) {
        val main = Intent(requireContext(), DashboardActivity::class.java)
        main.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        main.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        main.putExtra(Constant.ROLE, role.name)
        startActivity(main)
    }

}