package com.jobtick.android.activities

import androidx.appcompat.app.AppCompatActivity
import com.jobtick.android.fragments.FilterInPersonFragment.FragmentCallbackFilterInPerson
import com.jobtick.android.fragments.FilterAllFragment.FragmentCallbackFilterAll
import com.jobtick.android.fragments.FilterRemotelyFragment.FragmentCallbackFilterRemote
import android.annotation.SuppressLint
import butterknife.BindView
import com.jobtick.android.R
import com.google.android.material.appbar.MaterialToolbar
import androidx.appcompat.widget.SwitchCompat
import com.jobtick.android.widget.ExtendedEntryText
import androidx.viewpager.widget.ViewPager
import com.jobtick.android.models.FilterModel
import com.jobtick.android.fragments.AbstractFilterFragment
import android.os.Bundle
import butterknife.ButterKnife
import android.os.Parcelable
import android.widget.CompoundButton
import com.jobtick.android.fragments.FilterRemotelyFragment
import com.jobtick.android.fragments.FilterInPersonFragment
import com.jobtick.android.fragments.FilterAllFragment
import android.content.Intent
import android.view.MenuItem
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import androidx.fragment.app.FragmentPagerAdapter
import com.jobtick.android.utils.Constant
import java.util.ArrayList
import java.util.HashMap

class FiltersActivity : AppCompatActivity(), FragmentCallbackFilterInPerson, FragmentCallbackFilterAll, FragmentCallbackFilterRemote {
    var toolbar: MaterialToolbar? = null
    var rbRemotely: SwitchCompat? = null
    var rbInPerson: SwitchCompat? = null
    var txtSuburb: ExtendedEntryText? = null
    var viewPager: ViewPager? = null
    var filterModel: FilterModel? = null
    var filters: Map<String, String>? = null
    private var all = true
    private var abstractFilterFragment1: AbstractFilterFragment? = null
    private var abstractFilterFragment2: AbstractFilterFragment? = null
    private var abstractFilterFragment3: AbstractFilterFragment? = null
    private var currentTab = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_filters)
        setIDs()
        filters = HashMap()
        filterModel = FilterModel()
        val bundle = intent.extras
        if (bundle?.getParcelable<Parcelable?>(Constant.FILTER) != null) {
            filterModel = bundle.getParcelable(Constant.FILTER)
        }
        initToolbar()
        initComponent()
    }

    private fun setIDs() {
        toolbar= findViewById(R.id.toolbar)
        rbRemotely= findViewById(R.id.rb_remotely)
        rbInPerson= findViewById(R.id.rb_in_person)
        txtSuburb= findViewById(R.id.txt_suburb)
        viewPager= findViewById(R.id.view_pager)
    }

    fun setSuburb(suburb: String?) {
        txtSuburb!!.text = suburb
    }

    fun setSubError(error: String?) {
        txtSuburb!!.setError(error)
    }

    private fun initComponent() {
        setupViewPager(viewPager)
        viewPager!!.addOnPageChangeListener(viewPagerPageChangeListener)
        viewPager!!.offscreenPageLimit = 3
        if (filterModel != null) {
            if (filterModel!!.section.equals(Constant.FILTER_REMOTE, ignoreCase = true)) {
                viewPager!!.currentItem = 0
                rbRemotely!!.isChecked = true
                rbInPerson!!.isChecked = false
                all = false
            } else if (filterModel!!.section.equals(Constant.FILTER_IN_PERSON, ignoreCase = true)) {
                viewPager!!.currentItem = 1
                rbRemotely!!.isChecked = false
                rbInPerson!!.isChecked = true
                all = false
            } else {
                viewPager!!.currentItem = 2
                rbRemotely!!.isChecked = true
                rbInPerson!!.isChecked = true
                all = true
            }
        } else {
            viewPager!!.currentItem = 2
            rbRemotely!!.isChecked = true
            rbInPerson!!.isChecked = true
            all = true
        }
        rbRemotely!!.setOnCheckedChangeListener { buttonView: CompoundButton?, isChecked: Boolean ->
            if (isChecked) {
                if (!rbInPerson!!.isChecked) {
                    viewPager!!.currentItem = 0
                    all = false
                }
                if (rbInPerson!!.isChecked) {
                    all = true
                    viewPager!!.currentItem = 2
                }
            } else {
                viewPager!!.currentItem = 1
                rbInPerson!!.isChecked = true
                all = false
            }
        }
        rbInPerson!!.setOnCheckedChangeListener { buttonView: CompoundButton?, isChecked: Boolean ->
            if (isChecked) {
                if (!rbRemotely!!.isChecked) {
                    viewPager!!.currentItem = 1
                    all = false
                }
                if (rbRemotely!!.isChecked) {
                    all = true
                    viewPager!!.currentItem = 2
                }
            } else {
                rbRemotely!!.isChecked = true
                viewPager!!.currentItem = 0
                all = false
            }
        }
        txtSuburb!!.setExtendedViewOnClickListener {
            when (currentTab) {
                0 -> abstractFilterFragment1!!.startFindLocation()
                1 -> abstractFilterFragment2!!.startFindLocation()
                2 -> abstractFilterFragment3!!.startFindLocation()
            }
        }
    }

    private fun setupViewPager(viewPager: ViewPager?) {
        val adapter = SectionsPagerAdapter(supportFragmentManager)
        abstractFilterFragment1 = FilterRemotelyFragment.newInstance(this, filterModel)
        abstractFilterFragment2 = FilterInPersonFragment.newInstance(this, filterModel)
        abstractFilterFragment3 = FilterAllFragment.newInstance(this, filterModel)
        adapter.addFragment(abstractFilterFragment1, resources.getString(R.string.remotely))
        adapter.addFragment(abstractFilterFragment2, resources.getString(R.string.in_person))
        adapter.addFragment(abstractFilterFragment3, resources.getString(R.string.all))
        viewPager!!.adapter = adapter
    }

    private fun initToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.title = "Filter"
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        val intent = Intent()
        val bundle = Bundle()
        bundle.putParcelable(Constant.FILTER, filterModel)
        intent.putExtras(bundle)
        setResult(101, intent)
        super.onBackPressed()
    }

    //  viewpager change listener
    var viewPagerPageChangeListener: OnPageChangeListener = object : OnPageChangeListener {
        override fun onPageSelected(position: Int) {
            currentTab = position
            when (position) {
                0 -> txtSuburb!!.visibility = View.GONE
                1 -> txtSuburb!!.visibility = View.VISIBLE
                2 -> txtSuburb!!.visibility = View.VISIBLE
            }
        }

        override fun onPageScrolled(arg0: Int, arg1: Float, arg2: Int) {}
        override fun onPageScrollStateChanged(arg0: Int) {}
    }

    override fun getInPersonData(filterModel: FilterModel) {
        this.filterModel = filterModel
        onBackPressed()
    }

    override fun getAllData(filterModel: FilterModel) {
        this.filterModel = filterModel
        onBackPressed()
    }

    override fun getRemoteData(filterModel: FilterModel) {
        this.filterModel = filterModel
        onBackPressed()
    }

    private inner class SectionsPagerAdapter(manager: FragmentManager?) : FragmentPagerAdapter(manager!!) {
        private val mFragmentList: MutableList<Fragment> = ArrayList()
        private val mFragmentTitleList: MutableList<String> = ArrayList()
        override fun getItem(position: Int): Fragment {
            return mFragmentList[position]
        }

        override fun getCount(): Int {
            return mFragmentList.size
        }

        fun addFragment(fragment: Fragment?, title: String) {
            mFragmentList.add(fragment!!)
            mFragmentTitleList.add(title)
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return mFragmentTitleList[position]
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        for (fragment in supportFragmentManager.fragments) {
            fragment.onActivityResult(requestCode, resultCode, data)
        }
    }
}