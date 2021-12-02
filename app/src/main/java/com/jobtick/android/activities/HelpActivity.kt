package com.jobtick.android.activities

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar
import com.jobtick.android.R
import com.jobtick.android.utils.Constant
import com.jobtick.android.utils.ExternalIntentHelper
import com.jobtick.android.widget.ExtendedSettingItem

class HelpActivity : AppCompatActivity() {
    private var toolbar: MaterialToolbar? = null
    private var btnSupport: ExtendedSettingItem? = null
    private var btnTerms: ExtendedSettingItem? = null
    private var btnPrivacy: ExtendedSettingItem? = null
    private var btnGuidelinesPoster: ExtendedSettingItem? = null
    private var btnGuidelinesTicker: ExtendedSettingItem? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_help)
        toolbar = findViewById(R.id.toolbar)
        btnSupport = findViewById(R.id.btn_support)
        btnTerms = findViewById(R.id.btn_terms)
        btnPrivacy = findViewById(R.id.btn_privacy)
        btnGuidelinesPoster = findViewById(R.id.btn_guidelines_poster)
        btnGuidelinesTicker = findViewById(R.id.btn_guidelines_ticker)
        btnSupport!!.setDrawableID(0)
        btnTerms!!.setDrawableID(0)
        btnPrivacy!!.setDrawableID(0)
        btnGuidelinesPoster!!.setDrawableID(0)
        btnGuidelinesTicker!!.setDrawableID(0)

        initToolbar()
        onClick()
    }

    private fun onClick() {
        btnSupport!!.setOnClickListener {
            ExternalIntentHelper.openLink(this, Constant.URL_support)
        }
        btnTerms!!.setOnClickListener {
            ExternalIntentHelper.openLink(this, Constant.URL_terms)
        }
        btnPrivacy!!.setOnClickListener {
            ExternalIntentHelper.openLink(this, Constant.URL_privacy_policy)
        }
        btnGuidelinesPoster!!.setOnClickListener {
            ExternalIntentHelper.openLink(this, Constant.URL_how_it_works_poster)
        }
        btnGuidelinesTicker!!.setOnClickListener {
            ExternalIntentHelper.openLink(this, Constant.URL_how_it_works_ticker)
        }
    }

    private fun initToolbar() {
        toolbar!!.setNavigationIcon(R.drawable.ic_back)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.title = "Help"
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }



}