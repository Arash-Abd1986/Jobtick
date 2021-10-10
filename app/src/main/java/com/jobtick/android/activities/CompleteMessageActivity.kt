package com.jobtick.android.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.jobtick.android.R
import com.jobtick.android.activities.DashboardActivity
import com.jobtick.android.activities.TaskDetailsActivity
import com.jobtick.android.fragments.CategoryListBottomSheet
import com.jobtick.android.utils.ConstantKey
import com.jobtick.android.utils.SessionManager

class CompleteMessageActivity : AppCompatActivity() {
    var toolbar: MaterialToolbar? = null
    var txtTitle: TextView? = null
    var txtSubtitle: TextView? = null
    var cardFinish: MaterialButton? = null
    var linearTaskCompleted: LinearLayout? = null
    var lytBtnNewJob: MaterialButton? = null
    var lytBtnViewYourJob: MaterialButton? = null
    var linearOfferSent: LinearLayout? = null
    var viewYourOffer: MaterialButton? = null
    var exploreJobs: MaterialButton? = null
    var from = 0
    var taskSlug: String? = null
    var sessionManager: SessionManager? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_complete_message)
        setIDs()
        onViewClick()
        initToolbar()
        sessionManager = SessionManager(this)
        val bundle = intent.extras
        if (bundle != null) {
            if (bundle.getString(ConstantKey.COMPLETES_MESSAGE_TITLE) != null) {
                txtTitle!!.text = bundle.getString(ConstantKey.COMPLETES_MESSAGE_TITLE)
            }
            if (bundle.getString(ConstantKey.COMPLETES_MESSAGE_SUBTITLE) != null) {
                txtSubtitle!!.text = bundle.getString(ConstantKey.COMPLETES_MESSAGE_SUBTITLE)
            }
            if (bundle.containsKey(ConstantKey.COMPLETES_MESSAGE_FROM)) {
                from = bundle.getInt(ConstantKey.COMPLETES_MESSAGE_FROM, 0)
            }
            if (bundle.containsKey(ConstantKey.SLUG)) {
                taskSlug = bundle.getString(ConstantKey.SLUG, null)
                Log.d("taskSlug", taskSlug + "")
            }
        }
        if (from == ConstantKey.RESULTCODE_MAKEANOFFER) {
            linearTaskCompleted!!.visibility = View.GONE
            cardFinish!!.visibility = View.GONE
            linearOfferSent!!.visibility = View.VISIBLE
        } else if (from == ConstantKey.RESULTCODE_CREATE_TASK) {
            linearTaskCompleted!!.visibility = View.VISIBLE
            cardFinish!!.visibility = View.GONE
            linearOfferSent!!.visibility = View.GONE
        }
    }

    private fun setIDs() {
        toolbar = findViewById(R.id.toolbar)
        txtTitle = findViewById(R.id.txt_title)
        txtSubtitle = findViewById(R.id.txt_subtitle)
        cardFinish = findViewById(R.id.cardFinish)
        linearTaskCompleted = findViewById(R.id.linearTaskCompleted)
        lytBtnNewJob = findViewById(R.id.lyt_btn_new_job)
        lytBtnViewYourJob = findViewById(R.id.lyt_btn_view_your_job)
        linearOfferSent = findViewById(R.id.linearOfferSent)
        viewYourOffer = findViewById(R.id.btn_view_your_offer)
        exploreJobs = findViewById(R.id.btn_explore_jobs)
    }

    private fun initToolbar() {
        toolbar!!.setNavigationIcon(R.drawable.ic_cancel)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.title = "Completed"
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }


    private fun onViewClick() {
        cardFinish!!.setOnClickListener {
            if (from == ConstantKey.RESULTCODE_INCREASE_BUDGET) {
                if (TaskDetailsActivity.requestAcceptListener != null) {
                    TaskDetailsActivity.requestAcceptListener!!.onRequestAccept()
                }
                onBackPressed()
            } else {
                onBackPressed()
            }
        }
        lytBtnViewYourJob!!.setOnClickListener {
            val intent = Intent(this, DashboardActivity::class.java)
            if (taskSlug == null) {
                intent.putExtra(ConstantKey.GO_TO_MY_JOBS, true)
                startActivity(intent)
            } else {
                val taskDetail = Intent(this, TaskDetailsActivity::class.java)
                val bundle1 = Bundle()
                bundle1.putString(ConstantKey.SLUG, taskSlug)
                //    bundle.putInt(ConstantKey.USER_ID, obj.getPoster().getId());
                taskDetail.putExtras(bundle1)
                startActivity(taskDetail)
                finish()
            }
        }
        lytBtnNewJob!!.setOnClickListener {
            val infoBottomSheet = CategoryListBottomSheet(sessionManager)
            infoBottomSheet.show(supportFragmentManager, null)
        }

        exploreJobs!!.setOnClickListener {
            intent = Intent(this, DashboardActivity::class.java)
            intent.putExtra(ConstantKey.GO_TO_EXPLORE, true)
            startActivity(intent)
        }
        viewYourOffer!!.setOnClickListener {
            if (TaskDetailsActivity.requestAcceptListener != null) {
                TaskDetailsActivity.requestAcceptListener!!.onMakeAnOffer()
            }
            onBackPressed()
        }

    }
}