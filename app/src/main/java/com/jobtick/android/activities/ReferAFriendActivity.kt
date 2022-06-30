package com.jobtick.android.activities

import com.jobtick.android.activities.ActivityBase
import com.jobtick.android.R
import android.widget.LinearLayout
import android.os.Bundle
import android.view.View
import android.view.WindowManager

class ReferAFriendActivity : ActivityBase() {

    var llBack: LinearLayout? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        val window = this.window
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.statusBarColor = this.resources.getColor(R.color.colorPrimary)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_refer_a_friend_v2)
        llBack = findViewById(R.id.llBack)
        initComponent()
    }

    private fun initComponent() {
        llBack!!.setOnClickListener { v: View? -> super.onBackPressed() }
    }
}