package com.jobtick.android.activities

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.viewpager.widget.ViewPager
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.google.android.material.appbar.MaterialToolbar
import com.jobtick.android.R
import com.jobtick.android.adapers.SectionsPagerAdapter
import com.jobtick.android.fragments.PaymentPosterFragment
import com.jobtick.android.fragments.PaymentTickerFragment

class PaymentHistoryActivity : ActivityBase() {
    private lateinit var toolbar: MaterialToolbar
    private lateinit var viewPager: ViewPager
    private lateinit var rbOutgoing: RadioButton
    private lateinit var rbEarned: RadioButton
    private lateinit var rgOutgoingEarned: RadioGroup
    private var viewPagerPageChangeListener: OnPageChangeListener = object : OnPageChangeListener {
        override fun onPageSelected(position: Int) {
            when (position) {
                0 -> {
                    rbEarned.isChecked = false
                    rbOutgoing.isChecked = true
                }
                1 -> {
                    rbEarned.isChecked = true
                    rbOutgoing.isChecked = false
                }
                2 -> {
                }
            }
        }

        override fun onPageScrolled(arg0: Int, arg1: Float, arg2: Int) {}
        override fun onPageScrollStateChanged(arg0: Int) {}
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment_history)
        setIDs()
        initComponent()
        initToolbar()
    }

    private fun setIDs() {
        toolbar = findViewById(R.id.toolbar)
        viewPager = findViewById(R.id.view_pager)
        rbOutgoing = findViewById(R.id.rb_outgoing)
        rbEarned = findViewById(R.id.rb_earned)
        rgOutgoingEarned = findViewById(R.id.rg_outgoing_earned)
    }

    private fun initComponent() {
        setupViewPager(viewPager)
        viewPager.addOnPageChangeListener(viewPagerPageChangeListener)
        viewPager.currentItem = 0
        viewPager.offscreenPageLimit = 2
        clickEvent()
    }

    private fun initToolbar() {
        toolbar.title = "Payment History"
        toolbar.setNavigationIcon(R.drawable.ic_back)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_payment_history, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> onBackPressed()
            R.id.nav_setting -> startActivity(Intent(this, PaymentSettingsActivity::class.java))
        }
        return super.onOptionsItemSelected(item)
    }

    private fun clickEvent() {
        rgOutgoingEarned.setOnCheckedChangeListener { group: RadioGroup?, checkedId: Int ->
            when (checkedId) {
                R.id.rb_outgoing -> viewPager.currentItem = 0
                R.id.rb_earned -> viewPager.currentItem = 1
            }
        }
    }

    private fun setupViewPager(viewPager: ViewPager?) {
        val adapter = SectionsPagerAdapter(supportFragmentManager)
        adapter.addFragment(PaymentPosterFragment.newInstance(), resources.getString(R.string.date_time))
        adapter.addFragment(PaymentTickerFragment.newInstance(), resources.getString(R.string.details))
        viewPager!!.adapter = adapter
    }
}