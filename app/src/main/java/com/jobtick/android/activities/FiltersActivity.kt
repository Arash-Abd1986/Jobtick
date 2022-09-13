package com.jobtick.android.activities

import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.view.MenuItem
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.viewpager.widget.ViewPager
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.textview.MaterialTextView
import com.jobtick.android.R
import com.jobtick.android.fragments.AbstractFilterFragment
import com.jobtick.android.fragments.FilterAllFragment
import com.jobtick.android.fragments.FilterAllFragment.FragmentCallbackFilterAll
import com.jobtick.android.fragments.FilterInPersonFragment
import com.jobtick.android.fragments.FilterInPersonFragment.FragmentCallbackFilterInPerson
import com.jobtick.android.fragments.FilterRemotelyFragment
import com.jobtick.android.fragments.FilterRemotelyFragment.FragmentCallbackFilterRemote
import com.jobtick.android.material.ui.filter.JobTypeFragment
import com.jobtick.android.material.ui.filter.SortByFragment
import com.jobtick.android.material.ui.postajob.GetLocationFragment
import com.jobtick.android.models.FilterModel
import com.jobtick.android.network.coroutines.ApiHelper
import com.jobtick.android.network.retrofit.ApiClient
import com.jobtick.android.utils.Constant
import com.jobtick.android.utils.SessionManager
import com.jobtick.android.utils.gone
import com.jobtick.android.utils.visible
import com.jobtick.android.viewmodel.PostAJobViewModel
import com.jobtick.android.viewmodel.ViewModelFactory
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


open class FiltersActivity : AppCompatActivity(), FragmentCallbackFilterInPerson, FragmentCallbackFilterAll, FragmentCallbackFilterRemote {

    private var txtSuburb: MaterialTextView? = null
    private lateinit var jobType: MaterialTextView
    private lateinit var sortBy: MaterialTextView
    private lateinit var back: AppCompatImageView
    private var viewPager: ViewPager? = null
    private var filterModel: FilterModel? = null
    private var filters: Map<String, String>? = null
    private var all = true
    private var abstractFilterFragment1: AbstractFilterFragment? = null
    private var abstractFilterFragment2: AbstractFilterFragment? = null
    private var abstractFilterFragment3: AbstractFilterFragment? = null
    private var currentTab = 0
    private lateinit var viewModel: PostAJobViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_filters)
        setIDs()
        initVM()
        filters = HashMap()
        filterModel = FilterModel()
        val bundle = intent.extras
        if (bundle?.getParcelable<Parcelable?>(Constant.FILTER) != null) {
            filterModel = bundle.getParcelable(Constant.FILTER)
        }
        initComponent()
    }

    private fun initVM() {
        viewModel = ViewModelProvider(
                this,
                ViewModelFactory(ApiHelper(ApiClient.getClientV2(SessionManager(applicationContext))))
        ).get(PostAJobViewModel::class.java)
        lifecycleScope.launch {
            viewModel.state.collectLatest { state ->
                sortBy.text = when (state.sortType) {
                    PostAJobViewModel.SortType.DUE_DATE -> "Due date"
                    PostAJobViewModel.SortType.PRICE -> "Price"
                    PostAJobViewModel.SortType.NEARBY_ME -> "Nearby me"
                }
                filterModel?.let { it.sortType = state.sortType.name }
                filterModel?.let { it.ascending = state.isAscending }
                when (state.jobType) {
                    PostAJobViewModel.JobType.IN_PERSON -> {
                        viewPager!!.currentItem = 1
                        jobType.text = "In-Person"
                        all = false
                    }
                    PostAJobViewModel.JobType.REMOTE -> {
                        viewPager!!.currentItem = 0
                        jobType.text = "Remote"
                    }
                    PostAJobViewModel.JobType.BOTH -> {
                        viewPager!!.currentItem = 2
                        jobType.text = "Both"
                        all == true
                    }
                }
            }
        }

    }

    fun showFragment(fragment: Fragment) {
        val view = findViewById<FrameLayout>(R.id.frame_container)
        view.visible()
        val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.frame_container, fragment)
        transaction.commit()
    }

    fun hideFragment() {
        val view = findViewById<FrameLayout>(R.id.frame_container)
        view.gone()
    }

    private fun setIDs() {
        txtSuburb = findViewById(R.id.txt_suburb)
        viewPager = findViewById(R.id.view_pager)
        jobType = findViewById(R.id.job_type)
        sortBy = findViewById(R.id.sortBy)
        back = findViewById(R.id.back)
        sortBy.setOnClickListener {
            showFragment(SortByFragment())
        }
        jobType.setOnClickListener {
            showFragment(JobTypeFragment())
        }
        back.setOnClickListener {
            onBackPressed()
        }
    }

    fun setSuburb(suburb: String?) {
        // txtSuburb!!.text = suburb
    }

    fun setSubError(error: String?) {
        //txtSuburb!!.setError(error)
    }

    private fun initComponent() {
        setupViewPager(viewPager)
        viewPager!!.addOnPageChangeListener(viewPagerPageChangeListener)
        viewPager!!.offscreenPageLimit = 3
        if (filterModel != null) {
            if (filterModel!!.section.equals(Constant.FILTER_REMOTE, ignoreCase = true)) {
                viewPager!!.currentItem = 0
                jobType.text = "Remote"
                viewModel.setJobType(PostAJobViewModel.JobType.REMOTE)
            } else if (filterModel!!.section.equals(Constant.FILTER_IN_PERSON, ignoreCase = true)) {
                viewPager!!.currentItem = 1
                jobType.text = "In-Person"
                viewModel.setJobType(PostAJobViewModel.JobType.IN_PERSON)

                all = false
            } else {
                viewPager!!.currentItem = 2
                jobType.text = "Both"
                all = true
                viewModel.setJobType(PostAJobViewModel.JobType.BOTH)
            }
        } else {
            viewPager!!.currentItem = 2
            jobType.text = "Both"
            viewModel.setJobType(PostAJobViewModel.JobType.BOTH)
            all = true
        }
        /*    rbRemotely!!.setOnCheckedChangeListener { buttonView: CompoundButton?, isChecked: Boolean ->
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
            }*/
        /*txtSuburb!!.setExtendedViewOnClickListener {
            when (currentTab) {
                0 -> abstractFilterFragment1!!.startFindLocation()
                1 -> abstractFilterFragment2!!.startFindLocation()
                2 -> abstractFilterFragment3!!.startFindLocation()
            }
        }*/
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
            /* when (position) {
                 0 -> txtSuburb!!.visibility = View.GONE
                 1 -> txtSuburb!!.visibility = View.VISIBLE
                 2 -> txtSuburb!!.visibility = View.VISIBLE
             }*/
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