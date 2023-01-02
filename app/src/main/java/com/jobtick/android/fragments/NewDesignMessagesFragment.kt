package com.jobtick.android.fragments;

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.Nullable
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.jobtick.android.R


class NewDesignMessagesFragment : Fragment() {

    private var inboxFragment1: InboxFragmentOpens? = null
    private var inboxFragment2: InboxFragment? = null
    private var viewPager: ViewPager? = null
    private var tabLayout: TabLayout? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_messages_new_design, container, false)
        tabLayout = view.findViewById(R.id.tabLayout)
        viewPager = view.findViewById(R.id.view_pager)
        return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        inboxFragment1 = InboxFragmentOpens()
        inboxFragment2 = InboxFragment()
        tabLayout?.setupWithViewPager(viewPager)
        val viewPagerAdapter = ViewPagerAdapter(getChildFragmentManager(), 0)
        //add fragments and set the adapter
        //add fragments and set the adapter
        viewPagerAdapter.addFragment(inboxFragment1!!, "Open")
        viewPagerAdapter.addFragment(inboxFragment2!!, "Closed")
        viewPager!!.setAdapter(viewPagerAdapter)

        //set the badge
//        val badgeDrawable = tabLayout!!.getTabAt(0)!!.orCreateBadge
//        badgeDrawable.isVisible = true
//        badgeDrawable.number = 5
    }
    private class ViewPagerAdapter(fm: FragmentManager, behavior: Int) :
        FragmentPagerAdapter(fm, behavior) {
        private val fragments: MutableList<Fragment> = ArrayList()
        private val fragmentTitles: MutableList<String> = ArrayList()

        //add fragment to the viewpager
        fun addFragment(fragment: Fragment, title: String) {
            fragments.add(fragment)
            fragmentTitles.add(title)
        }

        override fun getItem(position: Int): Fragment {
            return fragments[position]
        }

        override fun getCount(): Int {
            return fragments.size
        }

        //to setup title of the tab layout
        @Nullable
        override fun getPageTitle(position: Int): CharSequence? {
            return fragmentTitles[position]
        }
    }
}
