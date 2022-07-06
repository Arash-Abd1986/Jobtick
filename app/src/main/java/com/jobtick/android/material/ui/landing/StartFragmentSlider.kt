package com.jobtick.android.material.ui.landing

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.button.MaterialButton
import com.jobtick.android.R
import com.jobtick.android.activities.DashboardActivity
import com.jobtick.android.utils.Constant

class StartFragmentSlider : Fragment(), ViewPagerAdapter.ItemClick {
    private lateinit var pager: ViewPager2
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
        pager = view.findViewById(R.id.pager)
        val adapter = ViewPagerAdapter(data)
        pager.adapter = adapter
        pager.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        pager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)

            }
        })
        next.setOnClickListener {
            pager.currentItem = pager.currentItem + 1
            if (pager.currentItem == 2) {
                navigate(role)
            }
        }
        skip.setOnClickListener {
            navigate(role)
        }
    }

    override fun buttonClick(type: ViewPagerAdapter.Role) {

    }
    private fun navigate(role: ViewPagerAdapter.Role){
        val main = Intent(requireContext(), DashboardActivity::class.java)
        main.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        main.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        main.putExtra(Constant.ROLE, role.name)
        startActivity(main)
    }

}